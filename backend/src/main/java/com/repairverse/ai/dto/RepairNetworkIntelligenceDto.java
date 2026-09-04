package com.repairverse.ai.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Phase 28 — Repair Network Intelligence DTO layer.
 * All records are immutable value types.
 */
public class RepairNetworkIntelligenceDto {

    // ── Network Overview ─────────────────────────────────────────────────────

    public record RepairNetworkOverviewResponse(
        int totalRepairShops,
        long totalCompletedRepairs,
        double networkSuccessRate,
        double averageCustomerSatisfaction,
        double averageTrustScore,
        int eliteShops,
        int shopsNeedingAttention,
        long openAnomalies
    ) {}

    // ── Shop Quality ──────────────────────────────────────────────────────────

    public record RepairShopQualityResponse(
        String shopId,
        String shopName,
        int overallQualityScore,
        String qualityTier,
        int reliabilityScore,
        int trustScore,
        int customerSatisfactionScore,
        int repairSuccessScore,
        int priceFairnessScore,
        int serviceSpeedScore,
        int totalRepairs,
        double successRate,
        double repeatRepairRate,
        String trend,
        List<QualityFactorResponse> factorBreakdown
    ) {}

    public record QualityFactorResponse(
        String factor,
        int score,
        int weight,
        String description
    ) {}

    // ── Outcome Analytics ─────────────────────────────────────────────────────

    public record RepairOutcomeAnalyticsResponse(
        long totalRepairs,
        long successfulRepairs,
        long failedRepairs,
        long repeatRepairs,
        double successRate,
        double failureRate,
        double repeatRepairRate,
        double averageRepairCost,
        double averageTurnaroundDays
    ) {}

    // ── Trust Score ───────────────────────────────────────────────────────────

    public record TrustScoreResponse(
        String shopId,
        int trustScore,
        String trustTier,
        List<String> positiveSignals,
        List<String> riskSignals,
        Map<String, Integer> scoreBreakdown
    ) {}

    // ── Anomalies ─────────────────────────────────────────────────────────────

    public record MarketplaceAnomalyResponse(
        String id,
        String shopId,
        String shopName,
        String anomalyType,
        String severity,
        int riskScore,
        String description,
        String status,
        LocalDateTime detectedAt
    ) {}

    public record UpdateAnomalyStatusRequest(
        String status  // UNDER_REVIEW | RESOLVED | DISMISSED
    ) {}

    // ── Leaderboard ───────────────────────────────────────────────────────────

    public record NetworkLeaderboardResponse(
        int rank,
        String shopId,
        String shopName,
        int qualityScore,
        int trustScore,
        double successRate,
        double customerRating,
        String trend,
        String badge
    ) {}

    // ── Category Analytics ────────────────────────────────────────────────────

    public record CategoryQualityAnalyticsResponse(
        String category,
        long repairCount,
        double successRate,
        double averageCost,
        double averageTurnaroundDays,
        List<String> bestPerformingShops
    ) {}

    // ── Quality Trends ────────────────────────────────────────────────────────

    public record QualityTrendResponse(
        String period,
        int qualityScore,
        int trustScore,
        double successRate,
        double customerSatisfaction
    ) {}

    // ── Admin ─────────────────────────────────────────────────────────────────

    public record NetworkHealthResponse(
        String overallStatus,
        int totalShops,
        int eliteShops,
        int excellentShops,
        int trustedShops,
        int standardShops,
        int needsImprovementShops,
        long openAnomalies,
        long criticalAnomalies,
        double platformTrustScore,
        double platformQualityScore,
        double platformSuccessRate
    ) {}

    public record ShopRiskProfileResponse(
        String shopId,
        String shopName,
        int riskScore,
        String riskLevel,
        long activeAnomalies,
        List<MarketplaceAnomalyResponse> anomalies,
        List<String> riskSignals,
        String recommendation
    ) {}
}
