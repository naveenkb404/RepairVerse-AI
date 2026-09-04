package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public final class DeviceIntelligenceDto {

    private DeviceIntelligenceDto() {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DeviceIntelligenceResponse(
            String deviceId,
            String deviceName,
            String category,
            String brand,
            String model,
            Integer intelligenceScore,
            String intelligenceTier,
            String recommendedAction,
            Integer decisionConfidence,
            String summary,
            String evaluatedAt,
            IntelligenceScoreBreakdown scoreBreakdown,
            List<DecisionFactor> decisionFactors,
            SmartDecision smartDecision,
            List<DeviceScenario> scenarios,
            List<DeviceIntelligenceAlertResponse> activeAlerts
    ) {}

    public record IntelligenceScoreBreakdown(
            int healthReliabilityScore,
            int failureRiskScore,
            int repairEconomicsScore,
            int maintenanceStatusScore,
            int longevityScore,
            int sustainabilityScore,
            int repairHistoryScore
    ) {}

    public record DecisionFactor(
            String factorName,
            int score,
            double weight,
            String impact,
            String explanation
    ) {}

    public record SmartDecision(
            String recommendedAction,
            String priority,
            String title,
            String explanation,
            Double estimatedCost,
            String expectedBenefit
    ) {}

    public record DeviceScenario(
            String scenarioType,
            String title,
            Double estimatedCost,
            Integer estimatedLifespanMonths,
            Double estimatedCo2Impact,
            Double estimatedSavings,
            Integer intelligenceScore,
            String recommendation,
            List<String> pros,
            List<String> cons
    ) {}

    public record DeviceIntelligenceAlertResponse(
            String id,
            String deviceId,
            String deviceName,
            String alertType,
            String severity,
            String title,
            String message,
            String recommendedAction,
            Boolean isRead,
            String createdAt
    ) {}

    public record DeviceIntelligenceTimelineItem(
            String id,
            String eventType,
            String title,
            String description,
            String impactBadge,
            String timestamp
    ) {}

    public record DeviceIntelligenceEvaluationRequest(
            Boolean forceReevaluation
    ) {}

    public record DeviceScenarioSimulationRequest(
            String preferredScenario,
            Double customBudget,
            Integer targetLifespanMonths,
            Boolean prioritizeSustainability
    ) {}

    public record DeviceDecisionSnapshotResponse(
            String id,
            String deviceId,
            Integer intelligenceScore,
            String recommendedAction,
            Integer decisionConfidence,
            Integer healthScore,
            Integer failureRiskScore,
            Integer economicScore,
            String summary,
            String createdAt
    ) {}
}
