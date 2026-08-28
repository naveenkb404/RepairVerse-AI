package com.repairverse.ai.dto;

import java.util.List;

/**
 * Data Transfer Objects for Phase 23: Generative AI Repair Intelligence & Explainable AI.
 * Uses immutable Java records for type-safe representations of explainability narratives.
 */
public class AiExplanationDto {

    // ─── 1. Device Failure & Risk Prediction Explanation ─────────────────────

    public record DeviceRiskExplanationResponse(
            String deviceId,
            String deviceName,
            int predictionScore,
            String riskLevel,
            String executiveSummary,
            String rootCauseAnalysis,
            List<RiskFactorExplanation> keyContributingFactors,
            List<ComponentWearDetail> componentWearAssessment,
            String economicJustification,
            String urgencyRating,
            List<String> safetyPrecautions,
            List<String> preventiveActionRoadmap,
            String modelUsed,
            boolean isDemo,
            String generatedAt
    ) {}

    public record RiskFactorExplanation(
            String factorName,
            String severity, // CRITICAL, HIGH, MEDIUM, LOW
            String explanation,
            String impactOnLifespan
    ) {}

    public record ComponentWearDetail(
            String component, // Battery, Logic Board, Thermal System, Ports, Display
            String status,
            String wearMechanisms,
            String estimatedRemainingLife
    ) {}

    // ─── 2. Diagnosis Reasoning & Evidence Breakdown ─────────────────────────

    public record DiagnosisExplanationResponse(
            String diagnosisId,
            String deviceName,
            String probableIssue,
            int confidenceScore,
            String visualEvidenceAnalysis,
            String symptomCorrelation,
            List<String> differentialDiagnoses,
            String repairFeasibilityRationale,
            List<String> requiredToolsRationale,
            String safetyWarningContext,
            String modelUsed,
            boolean isDemo,
            String generatedAt
    ) {}

    // ─── 3. Repair vs. Replace Recommendation Rationale ──────────────────────

    public record RecommendationExplanationResponse(
            String recommendationId,
            String deviceName,
            String recommendedAction, // REPAIR, REPLACE, UPGRADE, RECYCLE
            double estimatedRepairCost,
            double estimatedDeviceValue,
            String costBenefitRationale,
            String lifespanExtensionAnalysis,
            String environmentalTradeoffNarrative,
            String salvageValueAssessment,
            List<String> riskAdjustedNextSteps,
            String modelUsed,
            boolean isDemo,
            String generatedAt
    ) {}

    // ─── 4. Sustainability & Environmental Impact Storytelling ───────────────

    public record SustainabilityNarrativeResponse(
            String userId,
            double totalCo2SavedKg,
            double totalEwasteReducedKg,
            double totalMoneySaved,
            int devicesExtended,
            String impactHeadline,
            String storytellingNarrative,
            String tangibleRealWorldEquivalents,
            List<String> circularEconomyAchievements,
            String futureImpactProjection,
            String modelUsed,
            boolean isDemo,
            String generatedAt
    ) {}
}
