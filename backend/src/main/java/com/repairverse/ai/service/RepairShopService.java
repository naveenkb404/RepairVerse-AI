package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.ShopDto.*;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairShopService {

    private final RepairShopRepository repairShopRepository;
    private final ObjectMapper objectMapper;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Transactional(readOnly = true)
    public ShopListResponse getShops(
            Double latitude,
            Double longitude,
            Double radiusKm,
            String serviceCategory,
            String search,
            Double minRating,
            Boolean ecoCertifiedOnly,
            String sortBy
    ) {
        // Validate coordinates if provided
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
        }

        List<RepairShop> shopsFromDb = repairShopRepository.findAll();
        List<RepairShopResponse> resultList;

        if (shopsFromDb.isEmpty()) {
            log.info("No shops found in database. Returning sample/demo reference repair shops.");
            resultList = getSampleShops(latitude, longitude);
        } else {
            resultList = shopsFromDb.stream()
                    .map(shop -> mapToResponse(shop, latitude, longitude))
                    .collect(Collectors.toList());
        }

        // Apply filtering
        double effectiveRadius = radiusKm != null && radiusKm > 0 ? radiusKm : 50.0;
        double effectiveMinRating = minRating != null ? minRating : 0.0;

        List<RepairShopResponse> filtered = resultList.stream()
                .filter(s -> {
                    if (latitude != null && longitude != null && s.distanceKm() != null) {
                        if (s.distanceKm() > effectiveRadius) return false;
                    }
                    if (s.rating() != null && s.rating() < effectiveMinRating) return false;
                    if (Boolean.TRUE.equals(ecoCertifiedOnly) && !Boolean.TRUE.equals(s.ecoCertified())) return false;

                    if (serviceCategory != null && !serviceCategory.isBlank() && !"All Services".equalsIgnoreCase(serviceCategory)) {
                        boolean categoryMatch = s.serviceCategories() != null && s.serviceCategories().stream()
                                .anyMatch(c -> c.toLowerCase().contains(serviceCategory.toLowerCase()));
                        if (!categoryMatch) return false;
                    }

                    if (search != null && !search.isBlank()) {
                        String q = search.toLowerCase();
                        boolean nameMatch = s.shopName() != null && s.shopName().toLowerCase().contains(q);
                        boolean addressMatch = s.address() != null && s.address().toLowerCase().contains(q);
                        if (!nameMatch && !addressMatch) return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        // Apply sorting
        if ("rating".equalsIgnoreCase(sortBy)) {
            filtered.sort(Comparator.comparing(RepairShopResponse::rating, Comparator.nullsLast(Comparator.reverseOrder())));
        } else {
            // Default "nearest" or by distance
            filtered.sort(Comparator.comparing(RepairShopResponse::distanceKm, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return new ShopListResponse(true, "Repair shops retrieved successfully", filtered);
    }

    @Transactional(readOnly = true)
    public ShopDetailResponse getShopById(String shopId) {
        Optional<RepairShop> shopOpt = repairShopRepository.findById(shopId);
        if (shopOpt.isPresent()) {
            return new ShopDetailResponse(true, "Repair shop detail retrieved", mapToResponse(shopOpt.get(), null, null));
        }

        // Check sample list
        Optional<RepairShopResponse> sampleOpt = getSampleShops(null, null).stream()
                .filter(s -> s.id().equalsIgnoreCase(shopId))
                .findFirst();

        if (sampleOpt.isPresent()) {
            return new ShopDetailResponse(true, "Sample repair shop detail retrieved (Demo Mode)", sampleOpt.get());
        }

        throw new ResourceNotFoundException("Repair shop not found with ID: " + shopId);
    }

    /**
     * Calculates Haversine distance in kilometers between two coordinates.
     */
    public static double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS_KM * c * 10.0) / 10.0;
    }

    private RepairShopResponse mapToResponse(RepairShop shop, Double userLat, Double userLng) {
        Double distKm = null;
        if (userLat != null && userLng != null && shop.getLatitude() != null && shop.getLongitude() != null) {
            distKm = calculateHaversineDistance(userLat, userLng, shop.getLatitude(), shop.getLongitude());
        }

        List<String> services = parseJsonList(shop.getServicesJson());
        List<String> serviceCategories = parseJsonList(shop.getServiceCategoriesJson());
        List<String> certifiedBrands = parseJsonList(shop.getCertifiedBrandsJson());

        return new RepairShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getShopName(),
                shop.getOwnerName(),
                shop.getAddress(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getRating() != null ? shop.getRating() : 4.5,
                shop.getReviewCount() != null ? shop.getReviewCount() : 0,
                shop.getPhone(),
                shop.getEmail(),
                shop.getHours(),
                services,
                serviceCategories,
                certifiedBrands,
                shop.getEstimatedTurnaround(),
                shop.getAvgPrice(),
                shop.getVerified(),
                shop.getVerified(),
                shop.getIsOpen(),
                distKm,
                shop.getEcoCertified(),
                shop.getIsDemo() != null ? shop.getIsDemo() : false
        );
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.debug("Failed to parse JSON string to list: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<RepairShopResponse> getSampleShops(Double userLat, Double userLng) {
        List<RepairShopResponse> samples = List.of(
                new RepairShopResponse(
                        "shop-001",
                        "TechCare Express Repair (SAMPLE)",
                        "TechCare Express Repair (SAMPLE)",
                        "Demo Technician A",
                        "742 Market Street, Suite 300, San Francisco, CA 94103",
                        37.7865, -122.4045,
                        4.9, 328,
                        "+1 (415) 555-0192", "sf-downtown@techcare.io", "Mon–Sat: 9:00 AM – 7:00 PM",
                        List.of("Smartphone", "Tablet", "Laptop", "Micro-soldering"),
                        List.of("Smartphone Repair", "Laptop Repair", "Tablet Repair"),
                        List.of("Apple", "Samsung", "Google Pixel"),
                        "Same-Day (2-4 hrs)", "$45 – $120",
                        true, true, true,
                        userLat != null && userLng != null ? calculateHaversineDistance(userLat, userLng, 37.7865, -122.4045) : 0.8,
                        true, true
                ),
                new RepairShopResponse(
                        "shop-002",
                        "GreenCircuit Electronics Lab (SAMPLE)",
                        "GreenCircuit Electronics Lab (SAMPLE)",
                        "Demo Technician B",
                        "1280 Folsom St, San Francisco, CA 94103",
                        37.7765, -122.4102,
                        4.8, 194,
                        "+1 (415) 555-0348", "contact@greencircuitlab.com", "Tue–Sun: 10:00 AM – 6:00 PM",
                        List.of("Laptop", "Gaming Console", "Audio Equipment", "Logic Board Repair"),
                        List.of("Laptop Repair", "Gaming Console Repair", "Audio/Headphone Repair"),
                        List.of("Apple", "Sony PlayStation", "Dell", "Lenovo"),
                        "1-2 Business Days", "$60 – $180",
                        true, true, true,
                        userLat != null && userLng != null ? calculateHaversineDistance(userLat, userLng, 37.7765, -122.4102) : 1.4,
                        true, true
                ),
                new RepairShopResponse(
                        "shop-003",
                        "QuickFix Mobile Hub (SAMPLE)",
                        "QuickFix Mobile Hub (SAMPLE)",
                        "Demo Technician C",
                        "550 Mission St, San Francisco, CA 94105",
                        37.7892, -122.3998,
                        4.7, 412,
                        "+1 (415) 555-0781", "support@quickfixhub.net", "Mon–Fri: 8:30 AM – 8:00 PM",
                        List.of("Smartphone", "Smartwatch", "Battery Replacement", "Screen Repair"),
                        List.of("Smartphone Repair", "Smartwatch Repair"),
                        List.of("Apple", "Samsung", "Xiaomi", "OnePlus"),
                        "30-60 Mins", "$35 – $95",
                        true, true, true,
                        userLat != null && userLng != null ? calculateHaversineDistance(userLat, userLng, 37.7892, -122.3998) : 2.1,
                        false, true
                ),
                new RepairShopResponse(
                        "shop-004",
                        "Silicon Valley Tech Clinic (SAMPLE)",
                        "Silicon Valley Tech Clinic (SAMPLE)",
                        "Demo Technician D",
                        "2100 University Ave, Palo Alto, CA 94301",
                        37.4445, -122.1612,
                        4.9, 520,
                        "+1 (650) 555-0914", "service@svtechclinic.com", "Mon–Sat: 9:00 AM – 6:00 PM",
                        List.of("Laptop", "Smartphone", "Tablet", "Data Recovery", "Component Soldering"),
                        List.of("Laptop Repair", "PC Repair", "Tablet Repair"),
                        List.of("Apple", "Microsoft Surface", "HP", "Asus"),
                        "Same-Day", "$70 – $220",
                        true, true, false,
                        userLat != null && userLng != null ? calculateHaversineDistance(userLat, userLng, 37.4445, -122.1612) : 5.3,
                        true, true
                )
        );

        return samples;
    }
}
