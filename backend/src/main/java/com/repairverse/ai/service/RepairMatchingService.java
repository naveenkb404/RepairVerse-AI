package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.MarketplaceDto.TrustScoreResponse;
import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairMatchingService {

    private final DeviceRepository deviceRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final RepairShopRepository repairShopRepository;
    private final RepairShopProfileRepository profileRepository;
    private final RepairShopSpecializationRepository specializationRepository;
    private final RepairMatchHistoryRepository matchHistoryRepository;
    private final RepairTrustService trustService;
    private final ObjectMapper objectMapper;

    /**
     * Compute deterministic smart matches for a device.
     */
    @Transactional
    public List<RepairShopMatchResponse> findMatchesForDevice(
            String deviceId,
            String userId,
            Double userLat,
            Double userLng,
            String diagnosisId,
            String repairType) {

        Device device = deviceRepository.findById(deviceId).orElse(null);
        String category = (device != null && device.getCategory() != null) ? device.getCategory() : "Smartphone";
        String brand = (device != null && device.getBrand() != null) ? device.getBrand() : "Universal";
        boolean isDemoDevice = (device == null || device.getId().startsWith("dev-demo") || device.getId().startsWith("demo-"));

        // Check if diagnosis provides specific fault / repair type context
        if (diagnosisId != null && (repairType == null || repairType.isBlank())) {
            DiagnosisReport diag = diagnosisReportRepository.findById(diagnosisId).orElse(null);
            if (diag != null && diag.getProbableIssue() != null) {
                repairType = diag.getProbableIssue();
            }
        }
        if (repairType == null || repairType.isBlank()) {
            repairType = "General Hardware Diagnostics & Repair";
        }

        List<RepairShop> shops = repairShopRepository.findAll();
        boolean isDemo = false;

        if (shops.isEmpty()) {
            isDemo = true;
            shops = getDemoRepairShops();
        }

        List<RepairShopMatchResponse> matchResults = new ArrayList<>();

        for (RepairShop shop : shops) {
            RepairShopProfile profile = profileRepository.findByRepairShopId(shop.getId()).orElse(null);
            List<RepairShopSpecialization> specs = specializationRepository.findByRepairShopId(shop.getId());

            RepairShopMatchResponse match = evaluateShopMatch(
                    shop, profile, specs, category, brand, repairType, userLat, userLng, isDemoDevice || isDemo);
            matchResults.add(match);
        }

        // Deterministic Sort: Overall Score desc -> Trust Score desc -> Rating desc -> Distance asc
        matchResults.sort(Comparator
                .comparingInt(RepairShopMatchResponse::overallScore).reversed()
                .thenComparing(Comparator.comparingInt(RepairShopMatchResponse::trustScore).reversed())
                .thenComparing(Comparator.comparingDouble(RepairShopMatchResponse::rating).reversed())
                .thenComparing(Comparator.comparingDouble(m -> m.distanceKm() != null ? m.distanceKm() : 999.0)));

        // Re-assign ranks 1..N
        List<RepairShopMatchResponse> rankedMatches = new ArrayList<>();
        for (int i = 0; i < matchResults.size(); i++) {
            RepairShopMatchResponse original = matchResults.get(i);
            rankedMatches.add(new RepairShopMatchResponse(
                    original.shopId(),
                    original.shopName(),
                    original.address(),
                    original.latitude(),
                    original.longitude(),
                    original.phone(),
                    original.email(),
                    original.hours(),
                    original.rating(),
                    original.reviewCount(),
                    original.verificationStatus(),
                    original.verificationLevel(),
                    original.distanceKm(),
                    original.overallScore(),
                    original.matchLevel(),
                    i + 1,
                    original.factors(),
                    original.explanation(),
                    original.estimatedCost(),
                    original.turnaroundHours(),
                    original.warrantyDays(),
                    original.trustScore(),
                    original.isEcoCertified(),
                    original.isDemo()
            ));
        }

        // Persist match history
        saveMatchHistory(userId, deviceId, rankedMatches);

        log.info("Smart matching completed for device '{}': found {} ranked shops", deviceId, rankedMatches.size());
        return rankedMatches;
    }

    private RepairShopMatchResponse evaluateShopMatch(
            RepairShop shop,
            RepairShopProfile profile,
            List<RepairShopSpecialization> specializations,
            String category,
            String brand,
            String repairType,
            Double userLat,
            Double userLng,
            boolean isDemo) {

        List<MatchingFactor> factors = new ArrayList<>();

        // 1. Specialization Match (Max 25 pts)
        int specScore = 0;
        List<String> services = parseJsonList(shop.getServicesJson());
        List<String> categories = parseJsonList(shop.getServiceCategoriesJson());
        List<String> certifiedBrands = parseJsonList(shop.getCertifiedBrandsJson());

        boolean categoryMatch = categories.stream().anyMatch(c -> c.equalsIgnoreCase(category))
                || services.stream().anyMatch(s -> s.toLowerCase().contains(category.toLowerCase()));
        boolean brandMatch = certifiedBrands.stream().anyMatch(b -> b.equalsIgnoreCase(brand))
                || specializations.stream().anyMatch(s -> s.getBrand() != null && s.getBrand().equalsIgnoreCase(brand));
        boolean repairTypeMatch = services.stream().anyMatch(s -> s.toLowerCase().contains(repairType.toLowerCase()));

        if (categoryMatch) specScore += 12;
        if (brandMatch) specScore += 8;
        if (repairTypeMatch) specScore += 5;

        // Check explicit specialization table
        for (RepairShopSpecialization spec : specializations) {
            if (spec.getDeviceCategory().equalsIgnoreCase(category)) {
                if ("EXPERT".equalsIgnoreCase(spec.getSpecializationLevel()) || "CERTIFIED".equalsIgnoreCase(spec.getSpecializationLevel())) {
                    specScore = Math.min(25, specScore + 5);
                }
            }
        }
        if (specScore == 0) specScore = 8; // Baseline capability
        specScore = Math.min(25, specScore);

        factors.add(new MatchingFactor(
                "Specialization & Hardware Fit",
                specScore,
                25,
                25,
                categoryMatch && brandMatch
                        ? "Certified specialist in " + brand + " " + category + " architecture."
                        : (categoryMatch ? "Handles " + category + " hardware maintenance." : "General hardware repair capability."),
                specScore >= 18
        ));

        // 2. Trust & Reputation (Max 20 pts)
        int trustVal = 85;
        try {
            TrustScoreResponse tr = trustService.evaluateTrust(shop.getId());
            trustVal = tr.trustScore();
        } catch (Exception e) {
            if (profile != null && "TRUSTED".equalsIgnoreCase(profile.getVerificationStatus())) {
                trustVal = 92;
            } else if (Boolean.TRUE.equals(shop.getVerified())) {
                trustVal = 85;
            }
        }
        int trustScore = (int) Math.round((trustVal / 100.0) * 20.0);
        trustScore = Math.max(5, Math.min(20, trustScore));

        factors.add(new MatchingFactor(
                "Trust & Verified Reputation",
                trustScore,
                20,
                20,
                trustVal >= 90
                        ? "Exceptional verified track record with authentic customer reviews (" + shop.getRating() + "★)."
                        : "Verified repair provider meeting platform quality criteria.",
                trustScore >= 15
        ));

        // 3. Quote & Cost Competitiveness (Max 15 pts)
        double estimatedCost = 75.0;
        if (shop.getAvgPrice() != null) {
            try {
                String clean = shop.getAvgPrice().replaceAll("[^0-9.]", "");
                if (!clean.isEmpty()) estimatedCost = Double.parseDouble(clean);
            } catch (Exception ignored) {}
        }
        // Deterministic price evaluation: benchmark $80
        int costScore = 12;
        if (estimatedCost <= 65.0) {
            costScore = 15;
        } else if (estimatedCost <= 95.0) {
            costScore = 13;
        } else if (estimatedCost <= 130.0) {
            costScore = 10;
        } else {
            costScore = 7;
        }

        factors.add(new MatchingFactor(
                "Quote & Pricing Value",
                costScore,
                15,
                15,
                "Estimated diagnostic & service cost ~$" + String.format("%.0f", estimatedCost) + " with transparent labor guarantee.",
                costScore >= 12
        ));

        // 4. Distance / Location (Max 15 pts)
        Double distanceKm = null;
        int distanceScore = 10; // Default when location is unavailable
        String distanceExplanation = "Location data approximate; nationwide delivery & mail-in available.";

        if (userLat != null && userLng != null && shop.getLatitude() != null && shop.getLongitude() != null) {
            distanceKm = calculateDistanceKm(userLat, userLng, shop.getLatitude(), shop.getLongitude());
            if (distanceKm <= 3.0) {
                distanceScore = 15;
                distanceExplanation = String.format("Extremely close (%.1f km) — walk-in eligible.", distanceKm);
            } else if (distanceKm <= 8.0) {
                distanceScore = 13;
                distanceExplanation = String.format("Nearby location (%.1f km) — convenient local drop-off.", distanceKm);
            } else if (distanceKm <= 20.0) {
                distanceScore = 10;
                distanceExplanation = String.format("Within metropolitan radius (%.1f km).", distanceKm);
            } else if (distanceKm <= 40.0) {
                distanceScore = 7;
                distanceExplanation = String.format("Regional service center (%.1f km).", distanceKm);
            } else {
                distanceScore = 4;
                distanceExplanation = String.format("Longer distance (%.1f km) — courier dispatch suggested.", distanceKm);
            }
        }

        factors.add(new MatchingFactor(
                "Proximity & Distance",
                distanceScore,
                15,
                15,
                distanceExplanation,
                distanceScore >= 10
        ));

        // 5. Availability & Response Time (Max 10 pts)
        int availScore = 0;
        if (Boolean.TRUE.equals(shop.getIsOpen())) availScore += 3;
        double turnaroundHours = 24.0;
        if (shop.getEstimatedTurnaround() != null) {
            String lower = shop.getEstimatedTurnaround().toLowerCase();
            if (lower.contains("same") || lower.contains("1-2 hours") || lower.contains("2 hours")) turnaroundHours = 4.0;
            else if (lower.contains("1-2 days") || lower.contains("24h")) turnaroundHours = 24.0;
            else if (lower.contains("3-5 days")) turnaroundHours = 72.0;
        }
        if (turnaroundHours <= 6.0) availScore += 4;
        else if (turnaroundHours <= 24.0) availScore += 3;
        else availScore += 2;

        int respTime = (profile != null) ? profile.getAverageResponseTimeMinutes() : 30;
        if (respTime <= 30) availScore += 3;
        else if (respTime <= 60) availScore += 2;
        else availScore += 1;
        availScore = Math.min(10, availScore);

        factors.add(new MatchingFactor(
                "Speed & Turnaround",
                availScore,
                10,
                10,
                "Estimated turnaround ~" + (turnaroundHours <= 6 ? "Same-day service" : (int) turnaroundHours + "h") + " with responsive support.",
                availScore >= 7
        ));

        // 6. Experience & Track Record (Max 10 pts)
        int expYears = (profile != null) ? profile.getYearsOfExperience() : 5;
        int completedRepairs = (profile != null) ? profile.getTotalRepairsCompleted() : 250;
        int expScore = 0;
        if (expYears >= 7) expScore += 5;
        else if (expYears >= 3) expScore += 4;
        else expScore += 2;

        if (completedRepairs >= 500) expScore += 5;
        else if (completedRepairs >= 100) expScore += 4;
        else expScore += 2;
        expScore = Math.min(10, expScore);

        factors.add(new MatchingFactor(
                "Mastery & Experience",
                expScore,
                10,
                10,
                expYears + "+ years active servicing with over " + completedRepairs + " verified successful repairs.",
                expScore >= 7
        ));

        // 7. Sustainability & Circularity (Max 5 pts)
        boolean isEco = Boolean.TRUE.equals(shop.getEcoCertified());
        int ecoScore = isEco ? 5 : 3;

        factors.add(new MatchingFactor(
                "Sustainability & Circularity",
                ecoScore,
                5,
                5,
                isEco ? "Certified Eco-Partner prioritizing component-level restoration & e-waste reduction." : "Standard electronic parts recycling compliant.",
                isEco
        ));

        // Calculate Overall Compatibility Score
        int totalScore = specScore + trustScore + costScore + distanceScore + availScore + expScore + ecoScore;
        totalScore = Math.max(0, Math.min(100, totalScore));

        String matchLevel;
        if (totalScore >= 88) matchLevel = "EXCELLENT_MATCH";
        else if (totalScore >= 75) matchLevel = "GREAT_MATCH";
        else if (totalScore >= 60) matchLevel = "GOOD_MATCH";
        else if (totalScore >= 45) matchLevel = "FAIR_MATCH";
        else matchLevel = "LOW_MATCH";

        // Build Explanation
        List<String> keyReasons = new ArrayList<>();
        if (categoryMatch && brandMatch) keyReasons.add("Expertly specialized in " + brand + " " + category + " devices.");
        if (trustVal >= 90) keyReasons.add("Top-tier trust rating (" + trustVal + "/100) with strong warranty backing.");
        if (turnaroundHours <= 12.0) keyReasons.add("Fast turnaround service available.");
        if (isEco) keyReasons.add("Eco-certified green repair shop.");
        if (distanceScore >= 13) keyReasons.add("Conveniently located near your registered location.");

        if (keyReasons.isEmpty()) {
            keyReasons.add("Reliable general electronics repair facility.");
            keyReasons.add("Standard warranty coverage provided.");
        }

        String summary = String.format("%s match for your %s %s. %s",
                matchLevel.replace("_", " ").toLowerCase(),
                brand,
                category,
                keyReasons.get(0));

        List<String> recommendations = List.of(
                "Request formal quote for exact hardware part costs",
                "Backup device data prior to drop-off or courier pickup",
                "Ensure warranty terms (typically " + (profile != null ? profile.getWarrantyDays() : 90) + " days) are confirmed"
        );

        RepairMatchExplanation explanation = new RepairMatchExplanation(
                summary,
                keyReasons,
                matchLevel,
                recommendations
        );

        String verStatus = (profile != null) ? profile.getVerificationStatus() : (Boolean.TRUE.equals(shop.getVerified()) ? "VERIFIED" : "PENDING");
        String verLevel = (profile != null) ? profile.getVerificationLevel() : "VERIFIED";
        int warrantyDays = (profile != null) ? profile.getWarrantyDays() : 90;

        return new RepairShopMatchResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getAddress(),
                shop.getLatitude(),
                shop.getLongitude(),
                shop.getPhone() != null ? shop.getPhone() : "+1 (555) 234-5678",
                shop.getEmail() != null ? shop.getEmail() : "support@" + shop.getShopName().toLowerCase().replaceAll("[^a-z0-9]", "") + ".com",
                shop.getHours() != null ? shop.getHours() : "Mon-Sat 9AM-7PM",
                shop.getRating() != null ? shop.getRating() : 4.8,
                shop.getReviewCount() != null ? shop.getReviewCount() : 64,
                verStatus,
                verLevel,
                distanceKm != null ? Math.round(distanceKm * 10.0) / 10.0 : null,
                totalScore,
                matchLevel,
                1, // updated in caller
                factors,
                explanation,
                estimatedCost,
                turnaroundHours,
                warrantyDays,
                trustVal,
                isEco,
                isDemo
        );
    }

    private void saveMatchHistory(String userId, String deviceId, List<RepairShopMatchResponse> rankedMatches) {
        if (userId == null || deviceId == null || rankedMatches.isEmpty()) return;
        try {
            for (RepairShopMatchResponse match : rankedMatches.stream().limit(5).toList()) {
                String factorsJson = objectMapper.writeValueAsString(match.factors());
                RepairMatchHistory history = RepairMatchHistory.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .repairShopId(match.shopId())
                        .matchScore(match.overallScore())
                        .matchLevel(match.matchLevel())
                        .rankPosition(match.rank())
                        .factorsJson(factorsJson)
                        .explanation(match.explanation() != null ? match.explanation().summary() : "")
                        .build();
                matchHistoryRepository.save(history);
            }
        } catch (Exception e) {
            log.warn("Failed to persist match history for device '{}': {}", deviceId, e.getMessage());
        }
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in KM
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Arrays.stream(json.replace("[", "").replace("]", "").replace("\"", "").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    private List<RepairShop> getDemoRepairShops() {
        return List.of(
                RepairShop.builder()
                        .id("shop-1")
                        .shopName("Apex Micro-Electronics Care")
                        .address("452 Tech Plaza, Innovation District")
                        .latitude(37.7749)
                        .longitude(-122.4194)
                        .rating(4.9)
                        .reviewCount(142)
                        .phone("+1 (555) 432-1098")
                        .email("contact@apexmicro.io")
                        .hours("Mon-Sat 8:30AM - 6:30PM")
                        .servicesJson("[\"Smartphone Repair\",\"Logic Board Repair\",\"Battery Replacement\",\"Screen Replacement\"]")
                        .serviceCategoriesJson("[\"Smartphone\",\"Laptop\",\"Tablet\"]")
                        .certifiedBrandsJson("[\"Apple\",\"Samsung\",\"Google\",\"Dell\"]")
                        .estimatedTurnaround("Same Day (2-4 hrs)")
                        .avgPrice("$65")
                        .verified(true)
                        .isOpen(true)
                        .ecoCertified(true)
                        .isDemo(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                RepairShop.builder()
                        .id("shop-2")
                        .shopName("GreenCircuit Refurb & Repair")
                        .address("88 Eco Boulevard, Westside")
                        .latitude(37.7833)
                        .longitude(-122.4167)
                        .rating(4.8)
                        .reviewCount(98)
                        .phone("+1 (555) 876-5432")
                        .email("support@greencircuit.org")
                        .hours("Mon-Fri 9:00AM - 7:00PM")
                        .servicesJson("[\"Soldering & Micro-repairs\",\"Component Refurbishment\",\"E-waste Recycling\",\"Diagnostics\"]")
                        .serviceCategoriesJson("[\"Smartphone\",\"Laptop\",\"Audio\",\"Wearable\"]")
                        .certifiedBrandsJson("[\"Apple\",\"Sony\",\"Lenovo\",\"Samsung\"]")
                        .estimatedTurnaround("24-48 Hours")
                        .avgPrice("$50")
                        .verified(true)
                        .isOpen(true)
                        .ecoCertified(true)
                        .isDemo(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                RepairShop.builder()
                        .id("shop-3")
                        .shopName("RapidFix Silicon Valley")
                        .address("101 Fast Track Way, Downtown")
                        .latitude(37.7650)
                        .longitude(-122.4300)
                        .rating(4.7)
                        .reviewCount(210)
                        .phone("+1 (555) 321-7654")
                        .email("help@rapidfix.com")
                        .hours("Mon-Sun 8:00AM - 8:00PM")
                        .servicesJson("[\"Express Screen Fix\",\"Port Replacement\",\"Battery Swap\",\"Liquid Damage Cleaning\"]")
                        .serviceCategoriesJson("[\"Smartphone\",\"Tablet\",\"Wearable\"]")
                        .certifiedBrandsJson("[\"Apple\",\"Samsung\",\"Xiaomi\",\"OnePlus\"]")
                        .estimatedTurnaround("Express (1-2 hrs)")
                        .avgPrice("$75")
                        .verified(true)
                        .isOpen(true)
                        .ecoCertified(false)
                        .isDemo(true)
                        .createdAt(LocalDateTime.now())
                        .build(),
                RepairShop.builder()
                        .id("shop-4")
                        .shopName("Precision Motherboard Labs")
                        .address("72 Foundry Center, Industrial Park")
                        .latitude(37.7900)
                        .longitude(-122.4000)
                        .rating(4.9)
                        .reviewCount(76)
                        .phone("+1 (555) 999-1122")
                        .email("lab@precisionmotherboard.net")
                        .hours("Mon-Fri 10:00AM - 6:00PM")
                        .servicesJson("[\"BGA Rework\",\"Micro-soldering\",\"GPU/CPU Reballing\",\"Complex Board Level Repair\"]")
                        .serviceCategoriesJson("[\"Laptop\",\"Desktop\",\"Gaming Console\"]")
                        .certifiedBrandsJson("[\"Apple\",\"Dell\",\"HP\",\"Asus\",\"Lenovo\"]")
                        .estimatedTurnaround("3-5 Days")
                        .avgPrice("$120")
                        .verified(true)
                        .isOpen(true)
                        .ecoCertified(true)
                        .isDemo(true)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
