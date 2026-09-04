package com.repairverse.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable DTO record layer for Phase 29: AI-Powered Circular Economy Intelligence & Personalized Sustainability Optimization.
 */
public final class CircularEconomyDto {

    private CircularEconomyDto() {}

    /**
     * Aggregated environmental and economic metrics for user or platform.
     */
    public record CircularImpactMetricsDto(
        Double totalCarbonSavedKg,
        Double totalEwastePreventedKg,
        Double totalMoneySaved,
        Integer totalLifeExtensionDays,
        Long totalRepairs,
        Long totalMaintenanceActions,
        Long totalRefurbishments,
        Long totalResponsibleDisposals,
        Long totalCircularActions
    ) {}

    /**
     * Itemized score components (max 100 points).
     */
    public record CircularFactorBreakdownDto(
        Integer repairLifeExtensionPoints, // Max 30
        Integer ewastePreventionPoints,    // Max 25
        Integer carbonImpactPoints,        // Max 20
        Integer endOfLifePoints,           // Max 15
        Integer consistencyPoints,         // Max 10
        Integer totalScore
    ) {}

    /**
     * Deterministic Circular Impact Score (0–100) with explainable factors and tier.
     */
    public record CircularImpactScoreDto(
        Integer score,
        String tier, // CIRCULAR_CHAMPION | ECO_LEADER | SUSTAINABLE | DEVELOPING | STARTING
        CircularFactorBreakdownDto factorBreakdown,
        List<String> strengths,
        List<String> improvementAreas,
        String nextBestAction,
        LocalDateTime evaluatedAt
    ) {}

    /**
     * Deterministic personalized sustainability recommendation.
     */
    public record SustainabilityRecommendationDto(
        String id,
        String deviceId,
        String deviceName,
        String priority, // CRITICAL | HIGH | MEDIUM | LOW
        String title,
        String description,
        Double estimatedCarbonImpact,
        Double estimatedEwasteImpact,
        Double estimatedMoneySavings,
        String reason,
        String actionType // REPAIR_NOW | SCHEDULE_MAINTENANCE | EXTEND_DEVICE_LIFE | UPGRADE_COMPONENT | REFURBISH_DEVICE | DONATE_DEVICE | RECYCLE_RESPONSIBLY | MONITOR_DEVICE
    ) {}

    /**
     * Sustainability target goal representation.
     */
    public record SustainabilityGoalDto(
        String id,
        String userId,
        String goalType, // CARBON_REDUCTION | EWASTE_PREVENTION | DEVICE_LIFE_EXTENSION | REPAIR_COUNT | MONEY_SAVED
        Double targetValue,
        Double currentValue,
        Integer progressPercentage,
        Double remainingValue,
        LocalDateTime startDate,
        LocalDateTime targetDate,
        String status, // ACTIVE | COMPLETED | EXPIRED | CANCELLED
        Boolean isCompleted
    ) {}

    public record CreateGoalRequest(
        @NotBlank(message = "Goal type is required")
        String goalType,

        @NotNull(message = "Target value is required")
        @Positive(message = "Target value must be positive")
        Double targetValue,

        LocalDateTime targetDate
    ) {}

    public record UpdateGoalRequest(
        Double targetValue,
        LocalDateTime targetDate,
        String status
    ) {}

    /**
     * Achievement details.
     */
    public record SustainabilityAchievementDto(
        String id,
        String achievementCode,
        String achievementName,
        String achievementDescription,
        Boolean unlocked,
        LocalDateTime unlockedAt,
        Double impactValue,
        String requirement
    ) {}

    /**
     * Individual circular timeline event.
     */
    public record CircularImpactEventDto(
        String id,
        String userId,
        String deviceId,
        String deviceName,
        String eventType,
        LocalDateTime eventDate,
        Double carbonSavedKg,
        Double ewastePreventedKg,
        Double moneySaved,
        Integer deviceLifeExtensionDays,
        String impactSource,
        String referenceId
    ) {}

    public record RecordImpactEventRequest(
        String deviceId,
        @NotBlank(message = "Event type is required")
        String eventType,
        Double carbonSavedKg,
        Double ewastePreventedKg,
        Double moneySaved,
        Integer deviceLifeExtensionDays,
        String impactSource,
        String referenceId
    ) {}

    /**
     * Unified Circular Economy Dashboard payload.
     */
    public record CircularDashboardDto(
        CircularImpactMetricsDto impactMetrics,
        CircularImpactScoreDto impactScore,
        List<SustainabilityGoalDto> activeGoals,
        Long completedGoalsCount,
        List<SustainabilityAchievementDto> achievements,
        Long unlockedAchievementsCount,
        List<SustainabilityRecommendationDto> nextActions,
        List<CircularImpactEventDto> recentEvents
    ) {}

    /**
     * Admin platform category ranking.
     */
    public record CategoryRankingDto(
        String categoryName,
        Long totalRepairs,
        Double carbonSavedKg,
        Double ewastePreventedKg,
        Double moneySaved
    ) {}

    /**
     * Admin platform shop sustainability ranking.
     */
    public record ShopSustainabilityRankingDto(
        String shopId,
        String shopName,
        Boolean ecoCertified,
        String qualityTier,
        Integer circularScore,
        Long repairsCompleted,
        Double carbonSavedKg
    ) {}

    /**
     * Monthly trend datapoint.
     */
    public record CircularTrendDto(
        String month,
        Double carbonSavedKg,
        Double ewastePreventedKg,
        Double moneySaved,
        Long actionsCount
    ) {}

    /**
     * Admin platform-wide circular intelligence analytics.
     */
    public record PlatformCircularAnalyticsDto(
        Long totalUsers,
        Long totalRepairs,
        Long totalDevicesExtended,
        Double totalCarbonSavedKg,
        Double totalEwastePreventedKg,
        Double totalMoneySaved,
        Long totalDevicesRecycled,
        Long totalDevicesRefurbished,
        List<CategoryRankingDto> categoryRankings,
        List<ShopSustainabilityRankingDto> topSustainableShops,
        List<CircularTrendDto> monthlyTrends
    ) {}
}
