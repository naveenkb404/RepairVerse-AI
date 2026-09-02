package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartRepairRecommendationService {

    private final RepairMatchingService matchingService;
    private final DeviceRepository deviceRepository;

    /**
     * Generate smart categorized recommendations for a device.
     */
    public SmartRecommendationResponse getRecommendationsForDevice(
            String deviceId,
            String userId,
            Double userLat,
            Double userLng,
            String diagnosisId,
            String repairType) {

        Device device = deviceRepository.findById(deviceId).orElse(null);
        String deviceName = (device != null && device.getDeviceName() != null) ? device.getDeviceName() : "Universal Device";
        boolean isDemo = (device == null || device.getId().startsWith("dev-demo") || device.getId().startsWith("demo-"));

        List<RepairShopMatchResponse> matches = matchingService.findMatchesForDevice(
                deviceId, userId, userLat, userLng, diagnosisId, repairType);

        if (matches.isEmpty()) {
            return new SmartRecommendationResponse(
                    deviceId,
                    deviceName,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    0,
                    LocalDateTime.now(),
                    isDemo
            );
        }

        List<CategoryRecommendation> recommendations = new ArrayList<>();

        // 1. BEST_OVERALL (Rank 1 match)
        RepairShopMatchResponse bestOverall = matches.get(0);
        recommendations.add(new CategoryRecommendation(
                "BEST_OVERALL",
                "Best Overall Match",
                bestOverall,
                String.format("Top holistic score (%d/100) combining specialization, verified reputation, and turnaround speed.", bestOverall.overallScore())
        ));

        // 2. BEST_VALUE (Lowest estimated cost with overallScore >= 60)
        RepairShopMatchResponse bestValue = matches.stream()
                .filter(m -> m.overallScore() >= 60)
                .min(Comparator.comparingDouble(RepairShopMatchResponse::estimatedCost))
                .orElse(bestOverall);
        recommendations.add(new CategoryRecommendation(
                "BEST_VALUE",
                "Best Value Choice",
                bestValue,
                String.format("Most competitive estimated diagnostic and repair fee (~$%.0f) with quality guarantee.", bestValue.estimatedCost())
        ));

        // 3. FASTEST_REPAIR (Lowest turnaround hours)
        RepairShopMatchResponse fastest = matches.stream()
                .min(Comparator.comparingDouble(RepairShopMatchResponse::turnaroundHours))
                .orElse(bestOverall);
        recommendations.add(new CategoryRecommendation(
                "FASTEST_REPAIR",
                "Fastest Turnaround",
                fastest,
                String.format("Express service completed in ~%.0f hours.", fastest.turnaroundHours())
        ));

        // 4. MOST_TRUSTED (Highest trust score)
        RepairShopMatchResponse mostTrusted = matches.stream()
                .max(Comparator.comparingInt(RepairShopMatchResponse::trustScore))
                .orElse(bestOverall);
        recommendations.add(new CategoryRecommendation(
                "MOST_TRUSTED",
                "Highest Trust Rating",
                mostTrusted,
                String.format("Industry-leading trust score (%d/100) with %d verified reviews.", mostTrusted.trustScore(), mostTrusted.reviewCount())
        ));

        // 5. MOST_SUSTAINABLE (Eco certified with best match)
        RepairShopMatchResponse mostSustainable = matches.stream()
                .filter(RepairShopMatchResponse::isEcoCertified)
                .max(Comparator.comparingInt(RepairShopMatchResponse::overallScore))
                .orElse(bestOverall);
        recommendations.add(new CategoryRecommendation(
                "MOST_SUSTAINABLE",
                "Eco & Circularity Leader",
                mostSustainable,
                "Certified green partner minimizing e-waste and employing circular component renewal."
        ));

        // 6. NEAREST (Closest distance, if available)
        Optional<RepairShopMatchResponse> nearestOpt = matches.stream()
                .filter(m -> m.distanceKm() != null)
                .min(Comparator.comparingDouble(RepairShopMatchResponse::distanceKm));
        if (nearestOpt.isPresent()) {
            RepairShopMatchResponse nearest = nearestOpt.get();
            recommendations.add(new CategoryRecommendation(
                    "NEAREST",
                    "Nearest Location",
                    nearest,
                    String.format("Closest proximity (%.1f km) for quick walk-in or local drop-off.", nearest.distanceKm())
            ));
        }

        return new SmartRecommendationResponse(
                deviceId,
                deviceName,
                recommendations,
                matches,
                matches.size(),
                LocalDateTime.now(),
                isDemo
        );
    }

    /**
     * Compare selected repair shops side-by-side.
     */
    public RepairMarketplaceComparison compareShops(
            List<String> shopIds,
            String deviceId,
            String userId,
            Double userLat,
            Double userLng) {

        List<RepairShopMatchResponse> allMatches = matchingService.findMatchesForDevice(
                deviceId != null ? deviceId : "dev-demo",
                userId != null ? userId : "usr-1",
                userLat,
                userLng,
                null,
                null
        );

        List<RepairShopMatchResponse> selectedShops;
        if (shopIds == null || shopIds.isEmpty()) {
            selectedShops = allMatches.stream().limit(3).toList();
        } else {
            selectedShops = allMatches.stream()
                    .filter(m -> shopIds.contains(m.shopId()))
                    .toList();
            if (selectedShops.isEmpty()) {
                selectedShops = allMatches.stream().limit(3).toList();
            }
        }

        List<ShopComparisonMetric> metrics = new ArrayList<>();

        // 1. Compatibility Score
        Map<String, String> scoreMap = new HashMap<>();
        String bestScoreShopId = selectedShops.get(0).shopId();
        int maxScore = -1;
        for (RepairShopMatchResponse s : selectedShops) {
            scoreMap.put(s.shopId(), s.overallScore() + "/100 (" + s.matchLevel().replace("_", " ") + ")");
            if (s.overallScore() > maxScore) {
                maxScore = s.overallScore();
                bestScoreShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("COMPATIBILITY", "Compatibility Match", "Deterministic 0-100 overall fit score", scoreMap, bestScoreShopId));

        // 2. Trust Score
        Map<String, String> trustMap = new HashMap<>();
        String bestTrustShopId = selectedShops.get(0).shopId();
        int maxTrust = -1;
        for (RepairShopMatchResponse s : selectedShops) {
            trustMap.put(s.shopId(), s.trustScore() + "/100 (" + s.verificationLevel() + ")");
            if (s.trustScore() > maxTrust) {
                maxTrust = s.trustScore();
                bestTrustShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("TRUST", "Trust & Verification", "Platform verified trust score and audit level", trustMap, bestTrustShopId));

        // 3. Customer Rating
        Map<String, String> ratingMap = new HashMap<>();
        String bestRatingShopId = selectedShops.get(0).shopId();
        double maxRating = -1;
        for (RepairShopMatchResponse s : selectedShops) {
            ratingMap.put(s.shopId(), s.rating() + " ★ (" + s.reviewCount() + " reviews)");
            if (s.rating() > maxRating) {
                maxRating = s.rating();
                bestRatingShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("RATING", "Customer Reviews", "Verified customer review average and feedback count", ratingMap, bestRatingShopId));

        // 4. Estimated Price
        Map<String, String> priceMap = new HashMap<>();
        String lowestCostShopId = selectedShops.get(0).shopId();
        double minPrice = Double.MAX_VALUE;
        for (RepairShopMatchResponse s : selectedShops) {
            priceMap.put(s.shopId(), String.format("$%.0f (Est.)", s.estimatedCost()));
            if (s.estimatedCost() < minPrice) {
                minPrice = s.estimatedCost();
                lowestCostShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("PRICE", "Estimated Service Cost", "Estimated baseline repair & diagnostics pricing", priceMap, lowestCostShopId));

        // 5. Distance / Location
        Map<String, String> distMap = new HashMap<>();
        String nearestShopId = selectedShops.get(0).shopId();
        double minDist = Double.MAX_VALUE;
        for (RepairShopMatchResponse s : selectedShops) {
            String val = (s.distanceKm() != null) ? String.format("%.1f km", s.distanceKm()) : "Mail-in / Regional";
            distMap.put(s.shopId(), val);
            if (s.distanceKm() != null && s.distanceKm() < minDist) {
                minDist = s.distanceKm();
                nearestShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("DISTANCE", "Proximity & Distance", "Distance from user coordinates or regional dispatch", distMap, nearestShopId));

        // 6. Turnaround Time
        Map<String, String> speedMap = new HashMap<>();
        String fastestShopId = selectedShops.get(0).shopId();
        double minHours = Double.MAX_VALUE;
        for (RepairShopMatchResponse s : selectedShops) {
            speedMap.put(s.shopId(), String.format("%.0f Hours", s.turnaroundHours()));
            if (s.turnaroundHours() < minHours) {
                minHours = s.turnaroundHours();
                fastestShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("TURNAROUND", "Turnaround Speed", "Estimated completion and testing window", speedMap, fastestShopId));

        // 7. Warranty Coverage
        Map<String, String> warrantyMap = new HashMap<>();
        String longestWarrantyShopId = selectedShops.get(0).shopId();
        int maxWarranty = -1;
        for (RepairShopMatchResponse s : selectedShops) {
            warrantyMap.put(s.shopId(), s.warrantyDays() + " Days Guarantee");
            if (s.warrantyDays() > maxWarranty) {
                maxWarranty = s.warrantyDays();
                longestWarrantyShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("WARRANTY", "Warranty Guarantee", "Post-repair parts and labor warranty protection", warrantyMap, longestWarrantyShopId));

        // 8. Sustainability
        Map<String, String> ecoMap = new HashMap<>();
        String mostSustainableShopId = selectedShops.get(0).shopId();
        for (RepairShopMatchResponse s : selectedShops) {
            ecoMap.put(s.shopId(), s.isEcoCertified() ? "Certified Eco-Partner 🌱" : "Standard Compliance");
            if (s.isEcoCertified()) {
                mostSustainableShopId = s.shopId();
            }
        }
        metrics.add(new ShopComparisonMetric("SUSTAINABILITY", "Circularity & Eco Care", "Component level reuse and e-waste prevention", ecoMap, mostSustainableShopId));

        final String finalBestScoreShopId = bestScoreShopId;
        final String finalLowestCostShopId = lowestCostShopId;

        String summary = String.format("Comparing %d shops: '%s' leads in overall compatibility (%d/100), while '%s' provides the lowest estimated service cost.",
                selectedShops.size(),
                selectedShops.stream().filter(s -> s.shopId().equals(finalBestScoreShopId)).findFirst().map(RepairShopMatchResponse::shopName).orElse("Leading Shop"),
                maxScore,
                selectedShops.stream().filter(s -> s.shopId().equals(finalLowestCostShopId)).findFirst().map(RepairShopMatchResponse::shopName).orElse("Value Shop")
        );

        boolean isDemo = selectedShops.stream().anyMatch(RepairShopMatchResponse::isDemo);

        return new RepairMarketplaceComparison(
                selectedShops,
                metrics,
                bestScoreShopId,
                lowestCostShopId,
                fastestShopId,
                bestTrustShopId,
                mostSustainableShopId,
                nearestShopId,
                summary,
                isDemo
        );
    }
}
