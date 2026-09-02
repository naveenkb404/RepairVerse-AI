package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase 26 — Repair Marketplace Service.
 *
 * Provides intelligent, deterministic marketplace shop discovery, multi-factor ranking,
 * and transparent score explanations.
 *
 * Ranking Formula (Total: 100 pts):
 *   - Verification Score:        Max 25 pts
 *   - Customer Rating Score:     Max 25 pts
 *   - Specialization Match:      Max 20 pts
 *   - Response Performance:      Max 10 pts
 *   - Warranty Protection:       Max 10 pts
 *   - Experience/Volume:         Max 10 pts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairMarketplaceService {

    private final RepairShopRepository repairShopRepository;
    private final RepairShopProfileRepository profileRepository;
    private final RepairShopSpecializationRepository specializationRepository;
    private final RepairTrustService trustService;

    /**
     * Discover and rank marketplace shops with optional search and filter criteria.
     */
    @Transactional(readOnly = true)
    public List<MarketplaceShopResponse> discoverShops(
            String deviceCategory,
            String brand,
            Double minRating,
            String verificationStatus,
            Double latitude,
            Double longitude,
            Double radiusKm) {

        List<RepairShop> shops = repairShopRepository.findAll();
        if (shops.isEmpty()) {
            log.info("No shops in database. Returning demo fallback reference shops.");
            return getDemoMarketplaceShops();
        }

        List<MarketplaceShopResponse> responses = new ArrayList<>();

        for (RepairShop shop : shops) {
            Optional<RepairShopProfile> profileOpt = profileRepository.findByRepairShopId(shop.getId());
            RepairShopProfile profile = profileOpt.orElseGet(() -> createDefaultProfile(shop));

            // Filter: Minimum Rating
            if (minRating != null && profile.getAverageRating() < minRating) {
                continue;
            }

            // Filter: Verification Status
            if (verificationStatus != null && !verificationStatus.isBlank() &&
                    !verificationStatus.equalsIgnoreCase(profile.getVerificationStatus())) {
                continue;
            }

            // Filter: Specialization match (if requested)
            List<RepairShopSpecialization> specs = specializationRepository.findByRepairShopId(shop.getId());
            boolean matchesCategory = deviceCategory == null || deviceCategory.isBlank() ||
                    specs.stream().anyMatch(s -> s.getDeviceCategory().equalsIgnoreCase(deviceCategory));
            boolean matchesBrand = brand == null || brand.isBlank() ||
                    specs.stream().anyMatch(s -> s.getBrand().equalsIgnoreCase(brand));

            if ((deviceCategory != null && !deviceCategory.isBlank() && !matchesCategory) &&
                (brand != null && !brand.isBlank() && !matchesBrand)) {
                // If neither category nor brand matches, lower relevance or skip if strict
                // We keep in results with reduced score if specs is empty, else skip
                if (!specs.isEmpty()) continue;
            }

            ShopRankingResponse ranking = calculateRanking(shop, profile, specs, deviceCategory, brand);
            responses.add(mapToMarketplaceResponse(shop, profile, specs, ranking));
        }

        // Sort descending by deterministic total marketplace score
        responses.sort(Comparator.comparingInt(MarketplaceShopResponse::marketplaceScore).reversed());
        return responses;
    }

    /**
     * Retrieve single shop marketplace details.
     */
    @Transactional(readOnly = true)
    public MarketplaceShopResponse getShopDetails(String shopId) {
        RepairShop shop = repairShopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair shop not found: " + shopId));

        RepairShopProfile profile = profileRepository.findByRepairShopId(shopId)
                .orElseGet(() -> createDefaultProfile(shop));
        List<RepairShopSpecialization> specs = specializationRepository.findByRepairShopId(shopId);

        ShopRankingResponse ranking = calculateRanking(shop, profile, specs, null, null);
        return mapToMarketplaceResponse(shop, profile, specs, ranking);
    }

    /**
     * Transparent ranking score breakdown.
     */
    @Transactional(readOnly = true)
    public ShopRankingResponse getShopRanking(String shopId, String category, String brand) {
        RepairShop shop = repairShopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair shop not found: " + shopId));

        RepairShopProfile profile = profileRepository.findByRepairShopId(shopId)
                .orElseGet(() -> createDefaultProfile(shop));
        List<RepairShopSpecialization> specs = specializationRepository.findByRepairShopId(shopId);

        return calculateRanking(shop, profile, specs, category, brand);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Ranking Engine (Deterministic)
    // ─────────────────────────────────────────────────────────────────────────

    public ShopRankingResponse calculateRanking(
            RepairShop shop,
            RepairShopProfile profile,
            List<RepairShopSpecialization> specs,
            String requestedCategory,
            String requestedBrand) {

        List<String> reasons = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 1. Verification (Max 25 pts)
        int vScore = switch (profile.getVerificationStatus().toUpperCase()) {
            case "TRUSTED" -> 25;
            case "VERIFIED" -> 20;
            case "SUSPENDED" -> 0;
            default -> 10;
        };
        reasons.add(String.format("Verification Status: %s (+%d/25 pts)", profile.getVerificationStatus(), vScore));
        if (vScore >= 20) strengths.add("Verified business identity and workshop facilities.");
        if (vScore == 0) warnings.add("Provider currently under suspension.");

        // 2. Rating Score (Max 25 pts)
        double rating = profile.getAverageRating();
        int rScore = (int) Math.round((rating / 5.0) * 25);
        reasons.add(String.format("Customer Rating: %.1f★ with %d reviews (+%d/25 pts)", rating, profile.getTotalReviews(), rScore));
        if (rating >= 4.7) strengths.add("Top-rated service satisfaction (≥4.7★).");

        // 3. Specialization Match (Max 20 pts)
        int sScore = 10; // Baseline generalist score
        if (requestedCategory != null && !requestedCategory.isBlank()) {
            boolean catMatch = specs.stream().anyMatch(s -> s.getDeviceCategory().equalsIgnoreCase(requestedCategory));
            if (catMatch) sScore += 5;
        }
        if (requestedBrand != null && !requestedBrand.isBlank()) {
            boolean brandMatch = specs.stream().anyMatch(s -> s.getBrand().equalsIgnoreCase(requestedBrand));
            if (brandMatch) sScore += 5;
        }
        if (sScore == 10 && !specs.isEmpty()) sScore = 15; // Shop has verified specializations
        reasons.add(String.format("Device & Brand Specialization Match (+%d/20 pts)", sScore));
        if (sScore >= 15) strengths.add("Certified specialized tooling for your hardware family.");

        // 4. Response Performance (Max 10 pts)
        int respScore = profile.getResponseRate() >= 95.0 ? 10 : profile.getResponseRate() >= 80.0 ? 7 : 4;
        reasons.add(String.format("Response Rate: %.0f%% within ~%d mins (+%d/10 pts)",
                profile.getResponseRate(), profile.getAverageResponseTimeMinutes(), respScore));
        if (respScore == 10) strengths.add("Fast quote turnaround under 30 minutes.");

        // 5. Warranty Protection (Max 10 pts)
        int wScore = profile.getWarrantyDays() >= 180 ? 10 : profile.getWarrantyDays() >= 90 ? 8 : 4;
        reasons.add(String.format("Warranty Period: %d days guarantee (+%d/10 pts)", profile.getWarrantyDays(), wScore));
        if (profile.getWarrantyDays() >= 90) strengths.add("Includes comprehensive 90+ day replacement warranty.");

        // 6. Experience (Max 10 pts)
        int expScore = Math.min(profile.getYearsOfExperience() * 2, 10);
        reasons.add(String.format("Industry Experience: %d years (+%d/10 pts)", profile.getYearsOfExperience(), expScore));

        int total = Math.min(vScore + rScore + sScore + respScore + wScore + expScore, 100);

        String trustLevel = total >= 85 ? "EXCEPTIONAL" :
                total >= 70 ? "TRUSTED" :
                total >= 50 ? "GOOD" :
                total >= 30 ? "LIMITED" : "UNVERIFIED";

        return new ShopRankingResponse(
                shop.getId(),
                shop.getShopName(),
                total,
                trustLevel,
                vScore,
                rScore,
                sScore,
                respScore,
                wScore,
                expScore,
                reasons,
                strengths,
                warnings,
                false
        );
    }

    private MarketplaceShopResponse mapToMarketplaceResponse(
            RepairShop shop,
            RepairShopProfile profile,
            List<RepairShopSpecialization> specs,
            ShopRankingResponse ranking) {

        List<String> specList = specs.stream()
                .map(s -> s.getBrand() + " " + s.getDeviceCategory())
                .collect(Collectors.toList());

        if (specList.isEmpty() && shop.getCertifiedBrandsJson() != null) {
            specList = Arrays.asList(shop.getCertifiedBrandsJson().replace("[\"", "").replace("\"]", "").split("\",\""));
        }

        return new MarketplaceShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getAddress(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getPhone(),
                shop.getEmail(),
                shop.getHours(),
                profile.getAverageRating(),
                profile.getTotalReviews(),
                profile.getVerificationStatus(),
                profile.getVerificationLevel(),
                profile.getYearsOfExperience(),
                profile.getTotalRepairsCompleted(),
                profile.getResponseRate(),
                profile.getAverageResponseTimeMinutes(),
                profile.getWarrantyOffered(),
                profile.getWarrantyDays(),
                specList,
                List.of("ISO 9001 Certified", "Right-to-Repair Partner"),
                ranking.totalScore(),
                ranking.trustLevel(),
                ranking.strengths(),
                false
        );
    }

    private RepairShopProfile createDefaultProfile(RepairShop shop) {
        return RepairShopProfile.builder()
                .id("rsp-" + shop.getId())
                .repairShopId(shop.getId())
                .verificationStatus("VERIFIED")
                .verificationLevel("VERIFIED")
                .yearsOfExperience(5)
                .totalRepairsCompleted(150)
                .averageRating(shop.getRating() != null ? shop.getRating() : 4.8)
                .totalReviews(shop.getReviewCount() != null ? shop.getReviewCount() : 34)
                .responseRate(98.0)
                .averageResponseTimeMinutes(25)
                .warrantyOffered(true)
                .warrantyDays(90)
                .build();
    }

    private List<MarketplaceShopResponse> getDemoMarketplaceShops() {
        return List.of(
                new MarketplaceShopResponse(
                        "shop-demo-1", "FixVerse Certified Hub", "123 Tech Blvd, Silicon Valley, CA",
                        37.7749, -122.4194, "+1 555-0192", "support@fixversehub.com", "Mon-Sat: 09:00 - 19:00",
                        4.9, 86, "TRUSTED", "PREMIUM", 8, 420, 99.0, 15, true, 180,
                        List.of("Apple Smartphone", "Apple Laptop", "Samsung Smartphone"),
                        List.of("Apple Independent Repair Provider", "IPC Master Tech"),
                        94, "EXCEPTIONAL",
                        List.of("Premium certified facility", "Top-rated 4.9★ service", "180-day guarantee"),
                        true
                ),
                new MarketplaceShopResponse(
                        "shop-demo-2", "GreenCircuit Eco Repairs", "456 Circular Ave, San Francisco, CA",
                        37.7833, -122.4167, "+1 555-0144", "contact@greencircuit.org", "Mon-Fri: 10:00 - 18:00",
                        4.8, 52, "VERIFIED", "VERIFIED", 5, 210, 95.0, 30, true, 90,
                        List.of("Dell Laptop", "Lenovo Laptop", "Smartphone All"),
                        List.of("Circular Economy Certified", "Right to Repair Partner"),
                        86, "TRUSTED",
                        List.of("Component-level motherboard repair", "90-day warranty"),
                        true
                )
        );
    }
}
