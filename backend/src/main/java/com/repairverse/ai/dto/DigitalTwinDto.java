package com.repairverse.ai.dto;

import java.util.List;

public class DigitalTwinDto {

    public record DigitalTwinSnapshotResponse(
            String id,
            String deviceId,
            String deviceName,
            String deviceCategory,
            Integer healthScore,
            Integer failureRiskScore,
            Integer maintenanceScore,
            Integer repairEconomicsScore,
            Integer longevityScore,
            Integer sustainabilityScore,
            Double predictedValue,
            Double predictedRepairCost,
            Double predictedFailureProbability,
            Double simulationConfidence,
            Integer overallEcosystemScore,
            String snapshotTime
    ) {}

    public record ForecastResponse(
            String id,
            String snapshotId,
            String deviceId,
            Integer forecastHorizonMonths,
            Integer predictedHealthScore,
            Integer predictedFailureRisk,
            Double predictedRepairCost,
            Double predictedDeviceValue,
            Integer predictedRemainingLifespanMonths,
            Double predictedCo2Impact,
            Double predictedEWasteImpact,
            Double forecastConfidence
    ) {}

    public record ScenarioResponse(
            String id,
            String deviceId,
            String scenarioType,
            String scenarioName,
            Integer projectedHealthScore,
            Integer projectedFailureRisk,
            Double projectedCost,
            Double projectedSavings,
            Integer projectedLifespanMonths,
            Double projectedCo2Impact,
            Double projectedEWasteImpact,
            Integer downtimeDays,
            Integer overallOutcomeScore,
            Double simulationConfidence
    ) {}

    public record OptimizationResponse(
            String id,
            String deviceId,
            String recommendedStrategy,
            Integer costScore,
            Integer reliabilityScore,
            Integer longevityScore,
            Integer sustainabilityScore,
            Integer optimizationScore,
            Double estimatedSavings,
            Integer estimatedLifespanGain,
            Double estimatedCo2Savings,
            String decisionReason,
            String generatedAt
    ) {}

    public record SimulationEventResponse(
            String id,
            String deviceId,
            String eventType,
            String severity,
            String title,
            String description,
            Integer projectedMonthOffset,
            Double estimatedFinancialImpact,
            String mitigationStrategy,
            String createdAt
    ) {}

    public record DeviceTrajectoryPoint(
            Integer monthOffset,
            Integer healthScore,
            Integer failureRisk,
            Double repairCost,
            Double deviceValue
    ) {}

    public record DeviceTrajectoryResponse(
            String deviceId,
            String deviceName,
            List<DeviceTrajectoryPoint> trajectoryPoints
    ) {}

    public record SimulationInsight(
            String type,
            String title,
            String message,
            String category,
            String impactLevel
    ) {}

    public record DigitalTwinDashboardResponse(
            String deviceId,
            String deviceName,
            String deviceCategory,
            DigitalTwinSnapshotResponse snapshot,
            List<ForecastResponse> forecasts,
            List<ScenarioResponse> scenarios,
            OptimizationResponse optimalStrategy,
            List<SimulationEventResponse> events,
            List<SimulationInsight> insights,
            Boolean isSimulated
    ) {}

    public record EcosystemMetricsResponse(
            Integer totalMonitoredDevices,
            Double totalProjectedSavings,
            Integer totalFailuresPrevented,
            Double totalCo2AvoidedKg,
            Integer averageEcosystemHealth,
            Integer activeSimulationsCount
    ) {}

    public record RunSimulationRequest(
            Double budget,
            Integer targetLifespanMonths,
            Boolean prioritizeSustainability,
            Boolean prioritizeReliability,
            Integer maxDowntimeDays,
            String preferredStrategy
    ) {}

    public record OptimizationRequest(
            Double budget,
            Integer targetLifespanMonths,
            Boolean prioritizeSustainability,
            Boolean prioritizeReliability,
            Integer maxDowntimeDays
    ) {}
}
