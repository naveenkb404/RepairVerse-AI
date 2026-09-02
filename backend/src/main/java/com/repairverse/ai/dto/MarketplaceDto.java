package com.repairverse.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Phase 26 — Marketplace, Quotations & Reputation DTOs.
 * All records are immutable value objects.
 */
public final class MarketplaceDto {

    private MarketplaceDto() {}

    /**
     * Marketplace shop discovery and directory response.
     */
    public record MarketplaceShopResponse(
            String id,
            String shopName,
            String address,
            Double latitude,
            Double longitude,
            String phone,
            String email,
            String hours,
            Double rating,
            Integer reviewCount,
            String verificationStatus,      // PENDING | VERIFIED | TRUSTED | SUSPENDED
            String verificationLevel,       // BASIC | VERIFIED | PREMIUM
            Integer yearsOfExperience,
            Integer totalRepairsCompleted,
            Double responseRate,
            Integer averageResponseTimeMinutes,
            Boolean warrantyOffered,
            Integer warrantyDays,
            List<String> specializations,
            List<String> certifications,
            int marketplaceScore,           // 0–100 deterministic score
            String trustLevel,              // EXCEPTIONAL | TRUSTED | GOOD | LIMITED | UNVERIFIED
            List<String> keyStrengths,
            boolean isDemo
    ) {}

    /**
     * Transparent marketplace ranking score breakdown.
     */
    public record ShopRankingResponse(
            String shopId,
            String shopName,
            int totalScore,                 // 0–100
            String trustLevel,
            int verificationScore,          // max 25
            int customerRatingScore,        // max 25
            int specializationScore,        // max 20
            int responsePerformanceScore,   // max 10
            int warrantyScore,              // max 10
            int experienceScore,            // max 10
            List<String> rankingReasons,
            List<String> strengths,
            List<String> warnings,
            boolean isDemo
    ) {}

    /**
     * Deterministic trust scoring report.
     */
    public record TrustScoreResponse(
            String shopId,
            int trustScore,                 // 0–100
            String trustLevel,
            List<String> trustFactors,
            List<String> positiveSignals,
            List<String> riskSignals,
            boolean isDemo
    ) {}

    /**
     * Shop reputation and rating breakdown.
     */
    public record ShopReputationResponse(
            String shopId,
            Double averageRating,
            Integer totalReviews,
            Integer totalVerifiedRepairs,
            Double qualityRating,
            Double communicationRating,
            Double valueRating,
            Double timelinessRating,
            Map<Integer, Long> ratingDistribution, // 5 -> count, 4 -> count, etc.
            List<RepairReviewResponse> recentReviews,
            boolean isDemo
    ) {}

    /**
     * Request a repair quote.
     */
    public record RequestQuoteRequest(
            String deviceId,
            String repairShopId,
            String diagnosisId,
            String recommendationId,
            String repairTitle,
            String problemSummary,
            Double userBudget
    ) {}

    /**
     * Repair quotation response.
     */
    public record RepairQuoteResponse(
            String id,
            String userId,
            String deviceId,
            String deviceName,
            String repairShopId,
            String shopName,
            String diagnosisId,
            String recommendationId,
            String repairTitle,
            String problemSummary,
            Double estimatedCost,
            Double minimumCost,
            Double maximumCost,
            Double estimatedDurationHours,
            Double partsCost,
            Double laborCost,
            Integer warrantyDays,
            String status,                  // REQUESTED | DRAFT | SUBMITTED | ACCEPTED | REJECTED | EXPIRED | CANCELLED
            int valueScore,                 // 0–100 deterministic value index
            String valueRating,             // EXCELLENT | GOOD | FAIR | POOR
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            boolean isDemo
    ) {}

    /**
     * Comparative multi-quote analysis response.
     */
    public record QuoteComparisonResponse(
            List<RepairQuoteResponse> quotes,
            String bestValueQuoteId,
            String lowestPriceQuoteId,
            String longestWarrantyQuoteId,
            String highestTrustQuoteId,
            List<String> comparisonInsights,
            boolean isDemo
    ) {}

    /**
     * Request to submit a verified repair review.
     */
    public record CreateReviewRequest(
            String bookingId,
            Integer rating,                 // 1–5
            String title,
            String comment,
            Integer repairQualityRating,    // 1–5
            Integer communicationRating,    // 1–5
            Integer valueRating,            // 1–5
            Integer timelinessRating        // 1–5
    ) {}

    /**
     * Customer review response.
     */
    public record RepairReviewResponse(
            String id,
            String userId,
            String userFullName,
            String repairShopId,
            String bookingId,
            Integer rating,
            String title,
            String comment,
            Integer repairQualityRating,
            Integer communicationRating,
            Integer valueRating,
            Integer timelinessRating,
            Boolean verifiedRepair,
            LocalDateTime createdAt,
            boolean isDemo
    ) {}
}
