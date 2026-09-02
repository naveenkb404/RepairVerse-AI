package com.repairverse.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Phase 27 — Intelligent Repair Marketplace Experience & Smart Matching DTOs.
 * All records are immutable value objects.
 */
public final class RepairMatchingDto {

    private RepairMatchingDto() {}

    /**
     * Request to match repair shops for a specific device and repair context.
     */
    public record RepairMatchRequest(
            String deviceId,
            String diagnosisId,
            String repairType,
            Double latitude,
            Double longitude,
            Double maxBudget,
            Double maxDistanceKm
    ) {}

    /**
     * Individual scoring breakdown factor for deterministic evaluation.
     */
    public record MatchingFactor(
            String factorName,
            int score,
            int maxScore,
            int weightPercent,
            String explanation,
            boolean positiveImpact
    ) {}

    /**
     * Explainable match rationale.
     */
    public record RepairMatchExplanation(
            String summary,
            List<String> keyReasons,
            String compatibilityLevel, // EXCELLENT_MATCH | GREAT_MATCH | GOOD_MATCH | FAIR_MATCH | LOW_MATCH
            List<String> recommendations
    ) {}

    /**
     * Single shop smart match result with 0-100 compatibility score and breakdown.
     */
    public record RepairShopMatchResponse(
            String shopId,
            String shopName,
            String address,
            Double latitude,
            Double longitude,
            String phone,
            String email,
            String hours,
            Double rating,
            Integer reviewCount,
            String verificationStatus,
            String verificationLevel,
            Double distanceKm,
            int overallScore,          // 0–100 deterministic compatibility score
            String matchLevel,         // EXCELLENT_MATCH | GREAT_MATCH | GOOD_MATCH | FAIR_MATCH | LOW_MATCH
            int rank,
            List<MatchingFactor> factors,
            RepairMatchExplanation explanation,
            Double estimatedCost,
            Double turnaroundHours,
            Integer warrantyDays,
            int trustScore,
            boolean isEcoCertified,
            boolean isDemo
    ) {}

    /**
     * Recommendation card for a specific recommendation badge/category.
     */
    public record CategoryRecommendation(
            String category,           // BEST_OVERALL | BEST_VALUE | FASTEST_REPAIR | MOST_TRUSTED | MOST_SUSTAINABLE | NEAREST
            String categoryLabel,
            RepairShopMatchResponse shop,
            String highlightReason
    ) {}

    /**
     * Overall smart recommendations response for a device.
     */
    public record SmartRecommendationResponse(
            String deviceId,
            String deviceName,
            List<CategoryRecommendation> recommendations,
            List<RepairShopMatchResponse> topMatches,
            int totalEvaluated,
            LocalDateTime generatedAt,
            boolean isDemo
    ) {}

    /**
     * Request to compare multiple selected repair shops.
     */
    public record CompareShopsRequest(
            List<String> shopIds,
            String deviceId
    ) {}

    /**
     * Metric comparison row across shops.
     */
    public record ShopComparisonMetric(
            String metricKey,
            String metricName,
            String description,
            Map<String, String> shopValues, // shopId -> display value
            String winnerShopId
    ) {}

    /**
     * Side-by-side shop comparison matrix response.
     */
    public record RepairMarketplaceComparison(
            List<RepairShopMatchResponse> shops,
            List<ShopComparisonMetric> metrics,
            String bestOverallShopId,
            String bestValueShopId,
            String fastestShopId,
            String mostTrustedShopId,
            String mostSustainableShopId,
            String nearestShopId,
            String comparisonSummary,
            boolean isDemo
    ) {}

    /**
     * Deterministic price intelligence and valuation for a quotation.
     */
    public record QuoteIntelligenceResponse(
            String quoteId,
            String repairShopId,
            String shopName,
            Double estimatedCost,
            Double partsCost,
            Double laborCost,
            Double marketAverageCost,
            Double costDifference,
            Double costDifferencePercent,
            String classification,      // EXCELLENT_VALUE | GOOD_VALUE | FAIR_PRICE | ABOVE_MARKET | OVERPRICED | SUSPICIOUSLY_LOW
            String classificationLabel,
            int priceFairnessScore,     // 0–100 score
            List<String> insights,
            List<String> warnings,
            boolean isDemo
    ) {}

    /**
     * User-level marketplace savings, comparisons, and quote analytics.
     */
    public record UserMarketplaceInsights(
            int totalShopsCompared,
            int totalQuotesRequested,
            int totalQuotesAccepted,
            Double averageRepairCost,
            Double totalPotentialSavings,
            List<String> bestValueOpportunities,
            List<RepairShopMatchResponse> recentMatches,
            boolean isDemo
    ) {}

    /**
     * High-performing platform shop item for analytics.
     */
    public record HighPerformingShop(
            String shopId,
            String shopName,
            int trustScore,
            Double averageRating,
            int totalQuotesAccepted,
            Double acceptanceRate
    ) {}

    /**
     * Platform-wide marketplace intelligence analytics for administrators.
     */
    public record PlatformMarketplaceAnalytics(
            int totalShops,
            int verifiedShops,
            int totalQuotes,
            Double quoteAcceptanceRate,
            Double averageMarketplaceRepairCost,
            Map<String, Long> popularDeviceCategories,
            Map<String, Long> topRequestedRepairs,
            List<HighPerformingShop> highPerformingShops,
            Map<String, Long> interactionTrends,
            boolean isDemo
    ) {}

    /**
     * Track user interaction event.
     */
    public record TrackInteractionRequest(
            String interactionType,
            String entityId,
            String entityType,
            String metadata
    ) {}
}
