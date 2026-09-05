package com.repairverse.ai.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 35: Privacy-Preserving Federated Repair Intelligence & Continuous Learning DTOs.
 * Immutable Java records following existing project conventions.
 */
public class FederatedLearningDto {

    // ─── 1. Dashboard Response ──────────────────────────────────────────

    public record LearningDashboardResponse(
            String activeModelVersion,
            String activeModelName,
            Double validationScore,
            Integer trustScore,
            Double improvementPercentage,
            Integer totalAnonymizedDevices,
            Integer totalAnonymizedRepairs,
            Integer activeLearningSignalsCount,
            Integer validatedPatternsCount,
            Double privacyComplianceScore,
            String lastLearningCycle,
            ModelVersionResponse currentModel,
            List<ModelVersionResponse> modelHistory,
            List<LearningSignalResponse> topSignals,
            List<PrivacyAuditResponse> recentPrivacyAudits,
            LearningImpactResponse impactMetrics
    ) {}

    // ─── 2. Learning Batch ──────────────────────────────────────────────

    public record LearningBatchResponse(
            String id,
            String batchReference,
            String sourceScope,
            Integer anonymizedDeviceCount,
            Integer anonymizedRepairCount,
            String status,
            String privacyLevel,
            Double validationScore,
            String modelVersion,
            String generatedAt,
            String createdAt
    ) {}

    // ─── 3. Learning Signals ────────────────────────────────────────────

    public record LearningSignalResponse(
            String id,
            String batchId,
            String signalType,
            String deviceCategory,
            String componentType,
            String failureMode,
            String repairAction,
            String outcomeClass,
            Integer aggregatedFrequency,
            Double successRate,
            Double averageCost,
            Integer averageLifespanGain,
            Double sustainabilityScore,
            Double confidence,
            Integer observationCount,
            String createdAt
    ) {}

    // ─── 4. Model Versions ──────────────────────────────────────────────

    public record ModelVersionResponse(
            String id,
            String modelName,
            String version,
            String parentVersion,
            String status,
            Integer trainingObservations,
            Double validationScore,
            Integer trustScore,
            Double improvementPercentage,
            String activatedAt,
            String retiredAt,
            String createdAt
    ) {}

    // ─── 5. Validation Result ───────────────────────────────────────────

    public record ValidationResultResponse(
            String id,
            String modelVersionId,
            String validationType,
            Double baselineScore,
            Double candidateScore,
            Double improvementScore,
            Boolean regressionDetected,
            Double confidence,
            String decision,
            String validatedAt
    ) {}

    // ─── 6. Privacy Audit ───────────────────────────────────────────────

    public record PrivacyAuditResponse(
            String id,
            String batchId,
            String eventType,
            String privacyRule,
            Integer recordsProcessed,
            Integer recordsFiltered,
            Integer recordsAggregated,
            Integer sensitiveFieldsRemoved,
            String createdAt
    ) {}

    // ─── 7. Impact Metrics ──────────────────────────────────────────────

    public record LearningImpactResponse(
            Double recommendationAccuracyGain,
            Double repairSuccessImprovement,
            Double costPredictionStability,
            Double co2OptimizationImprovement,
            Double failurePredictionGain,
            Integer totalDecisionsEnriched
    ) {}

    // ─── 8. Trends & Historical Trajectory ──────────────────────────────

    public record LearningTrendResponse(
            List<ModelTrendPoint> modelTrajectory,
            List<SignalCategoryDistribution> categoryDistribution,
            Double overallEcosystemGrowth
    ) {}

    public record ModelTrendPoint(
            String version,
            Double validationScore,
            Integer trustScore,
            Double improvementPercentage,
            Integer observationCount,
            String timestamp
    ) {}

    public record SignalCategoryDistribution(
            String category,
            Integer signalCount,
            Double averageSuccessRate,
            Double averageConfidence
    ) {}

    // ─── 9. Model Comparison (Current vs Candidate) ─────────────────────

    public record LearningModelComparisonResponse(
            ModelVersionResponse currentModel,
            ModelVersionResponse candidateModel,
            Double accuracyDelta,
            Double costStabilityDelta,
            Double trustScoreDelta,
            Integer newObservationsCount,
            Boolean safeToActivate,
            List<ValidationResultResponse> validationBreakdown,
            List<String> governanceRecommendations
    ) {}

    // ─── 10. Device-level Privacy-Preserving Profile ─────────────────────

    public record DeviceLearningProfileResponse(
            String deviceId,
            String deviceCategory,
            String activeModelVersion,
            Integer matchingEcosystemObservations,
            Double ecosystemSuccessRate,
            Integer expectedLifespanGainMonths,
            Double expectedCostSavings,
            Double confidence,
            List<LearningSignalResponse> relevantSignals,
            String privacyNotice
    ) {}

    // ─── 11. Run & Action Requests/Responses ────────────────────────────

    public record LearningRunResponse(
            Boolean success,
            String message,
            String batchReference,
            String candidateVersion,
            Integer anonymizedOutcomesProcessed,
            Double validationScore,
            Boolean validationPassed,
            String nextAction
    ) {}

    public record LearningFeedbackRequest(
            String modelVersion,
            String decisionReference,
            String feedbackType, // AGREE, DISAGREE, UNSURE
            Double outcomeQuality
    ) {}
}
