package com.repairverse.ai.dto;

import java.util.List;

/**
 * DTOs for Phase 22: Predictive Intelligence & Maintenance Analytics.
 * Uses Java records for immutability and compact declaration.
 */
public class PredictiveMaintenanceDto {

    // ─── 1. Device Prediction Response ───────────────────────────────────────

    public record DevicePredictionResponse(
            String deviceId,
            String deviceName,
            String category,
            String brand,
            int predictionScore,
            String riskLevel,
            Integer daysToFailureEstimate,
            String primaryFaultType,
            List<String> recommendedActions,
            List<ScoringFactor> scoringBreakdown,
            double estimatedRepairCost,
            double preventiveSavings,
            double co2SavingsKg,
            double confidenceScore,
            boolean isDemo,
            String evaluatedAt
    ) {}

    // ─── 2. Scoring Factor (within DevicePredictionResponse) ─────────────────

    public record ScoringFactor(
            String factor,
            int score,
            int maxScore,
            String status,
            String description
    ) {}

    // ─── 3. Fault Pattern DTO ─────────────────────────────────────────────────

    public record FaultPatternDto(
            String id,
            String deviceCategory,
            String deviceBrand,
            String faultType,
            String description,
            int minDeviceAgeYears,
            int healthScoreThreshold,
            int riskWeight,
            Double typicalCostMin,
            Double typicalCostMax,
            List<String> preventiveActions,
            boolean isActive
    ) {}

    // ─── 4. Maintenance Recommendation ───────────────────────────────────────

    public record MaintenanceRecommendation(
            String id,
            String title,
            String description,
            String priority,        // CRITICAL, HIGH, MEDIUM, LOW
            String category,        // Battery, Cleaning, Software, Hardware
            String estimatedCost,
            String estimatedTime,
            String impact,
            List<String> steps
    ) {}

    // ─── 5. Predictive Fleet Overview (Dashboard & Admin) ────────────────────

    public record PredictiveFleetOverview(
            long totalDevices,
            long criticalDevices,
            long highRiskDevices,
            long mediumRiskDevices,
            long lowRiskDevices,
            long healthyDevices,
            double averagePredictionScore,
            double totalEstimatedRepairCost,
            double totalPreventiveSavings,
            double totalCo2SavingsKg,
            List<RiskDistributionEntry> riskDistribution,
            boolean isDemo
    ) {}

    public record RiskDistributionEntry(
            String riskLevel,
            long count,
            double percentage
    ) {}

    // ─── 6. Repair Cost Analytics ─────────────────────────────────────────────

    public record RepairCostAnalytics(
            double totalSpent,
            double averageCostPerRepair,
            double totalPartsCost,
            double totalLaborCost,
            double projectedNextRepairCost,
            double potentialSavingsIfPreventive,
            List<MonthlyCostEntry> monthlyCostTrend,
            List<CategoryCostEntry> costByCategory,
            boolean isDemo
    ) {}

    public record MonthlyCostEntry(
            String month,
            double repairCost,
            double partsCost,
            double laborCost
    ) {}

    public record CategoryCostEntry(
            String category,
            double totalCost,
            long repairCount,
            double averageCost
    ) {}

    // ─── 7. Sustainability Analytics ─────────────────────────────────────────

    public record SustainabilityAnalytics(
            double totalCo2SavedKg,
            double totalEwasteReducedKg,
            double totalMoneySaved,
            int devicesExtendedLifespan,
            double co2EquivalentTrees,
            double co2EquivalentCarKm,
            List<MonthlyImpactEntry> monthlyImpact,
            List<DeviceImpactEntry> topDevicesByImpact,
            boolean isDemo
    ) {}

    public record MonthlyImpactEntry(
            String month,
            double co2SavedKg,
            double ewasteReducedKg,
            double moneySaved
    ) {}

    public record DeviceImpactEntry(
            String deviceId,
            String deviceName,
            double co2SavedKg,
            double ewasteReducedKg,
            double moneySaved,
            int repairCount
    ) {}

    // ─── 8. Admin Intelligence Summary ───────────────────────────────────────

    public record AdminIntelligenceSummary(
            long totalPredictionsGenerated,
            long devicesAtCriticalRisk,
            long devicesAtHighRisk,
            double platformAverageHealthScore,
            double totalProjectedFailureCost,
            double totalPreventableSavings,
            double platformCo2ImpactKg,
            List<TopFailingCategory> topFailingCategories,
            List<RecentHighRiskDevice> recentHighRiskDevices,
            boolean isDemo
    ) {}

    public record TopFailingCategory(
            String category,
            long deviceCount,
            long atRiskCount,
            double riskPercentage,
            String primaryFaultType
    ) {}

    public record RecentHighRiskDevice(
            String deviceId,
            String deviceName,
            String userId,
            String userEmail,
            String riskLevel,
            int predictionScore,
            String primaryFaultType,
            String evaluatedAt
    ) {}

    // ─── 9. Notification Dedup Check Result ──────────────────────────────────

    public record NotificationDedupResult(
            boolean shouldSend,
            String reason
    ) {}
}
