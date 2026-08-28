package com.repairverse.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Immutable DTO records for Phase 24:
 * Autonomous Repair Planning, Lifecycle Intelligence, Delay Simulation & Journey Tracking.
 */
public class RepairPlanningDto {

    /** Single action plan with ordered execution steps */
    public record RepairActionPlanResponse(
        String id,
        String userId,
        String deviceId,
        String deviceName,
        String deviceCategory,
        String overallStrategy,
        String priorityLevel,
        Double estimatedTotalCost,
        Integer estimatedLifecycleExtensionMonths,
        Double estimatedCarbonSaved,
        Double estimatedEwastePrevented,
        String status,
        String strategyRationale,
        List<RepairActionStepResponse> steps,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}

    /** Individual sequential step inside an action plan */
    public record RepairActionStepResponse(
        String id,
        String actionPlanId,
        Integer stepOrder,
        String title,
        String description,
        String actionType,
        String priority,
        Double estimatedCost,
        String estimatedDuration,
        Double carbonImpact,
        Boolean isRequired,
        String status
    ) {}

    /** Comprehensive device lifecycle analysis and scenario comparison */
    public record DeviceLifecycleAssessmentResponse(
        String deviceId,
        String deviceName,
        String deviceCategory,
        Integer deviceAgeMonths,
        Integer predictedRemainingLifespanMonths,
        Integer expectedLifespanAfterMaintenanceMonths,
        Integer expectedLifespanAfterRepairMonths,
        Integer lifecycleExtensionPotentialMonths,
        Integer repairabilityScore,
        String replacementUrgency,
        Double cumulativeCarbonSavedKg,
        Double cumulativeEwasteDivertedKg,
        List<LifecycleScenarioResponse> scenarios,
        LocalDateTime evaluatedAt
    ) {}

    /** Individual lifecycle decision scenario (e.g. DO_NOTHING, PREVENTIVE_MAINTENANCE, REPAIR_NOW, DELAY_REPAIR, REPLACE) */
    public record LifecycleScenarioResponse(
        String scenarioKey,
        String title,
        String description,
        Double estimatedCost,
        Integer estimatedLifespanMonths,
        Double carbonImpactKg,
        Double ewasteImpactKg,
        String riskLevel,
        String recommendationTag
    ) {}

    /** Delay impact simulation across 7-day, 30-day, and 90-day time horizons */
    public record DelayImpactResponse(
        String deviceId,
        String deviceName,
        Double baselineRepairCost,
        String currentRiskLevel,
        String primaryFaultRisk,
        List<DelayProjection> projections,
        String urgencyRecommendation,
        LocalDateTime simulatedAt
    ) {}

    /** Projection for a specific delay horizon */
    public record DelayProjection(
        Integer delayDays,
        String timeHorizonLabel,
        Double projectedCost,
        Double costEscalationPercentage,
        String projectedRiskLevel,
        Double secondaryDamageProbability,
        Integer lifecycleReductionMonths,
        Double additionalCarbonPenaltyKg,
        String consequenceSummary
    ) {}

    /** High-level summary of action plans across user devices */
    public record ActionPlanSummaryResponse(
        long totalPlans,
        long activePlans,
        long highPriorityPlans,
        Double totalEstimatedCost,
        Integer totalLifecycleMonthsGained,
        Double totalCarbonSavingsKg
    ) {}

    /** Unified end-to-end device repair journey timeline */
    public record RepairJourneyResponse(
        String deviceId,
        String deviceName,
        String currentStage,
        Integer currentStageIndex,
        Integer totalStages,
        Integer progressPercentage,
        List<RepairJourneyStageResponse> stages,
        String nextRecommendedAction,
        LocalDateTime lastUpdated
    ) {}

    /** Individual stage in the device repair journey */
    public record RepairJourneyStageResponse(
        String stageKey,
        String title,
        String description,
        Boolean isCompleted,
        Boolean isCurrent,
        LocalDateTime completedAt,
        String actionUrl
    ) {}
}
