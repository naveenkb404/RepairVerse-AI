package com.repairverse.ai.dto;

import java.util.List;

/**
 * Phase 34: AI Decision Trust & Explainability Engine DTOs.
 * Immutable record types following existing project conventions.
 */
public class TrustEngineDto {

    // ─── Dashboard / Summary ────────────────────────────────────────────

    public record TrustDashboardResponse(
            String userId,
            Integer totalDecisions,
            Integer verifiedCount,
            Integer reliableCount,
            Integer cautionCount,
            Integer reviewRequiredCount,
            Double averageTrustScore,
            Integer activeViolations,
            Integer decisionsReviewedByUser,
            List<SystemTrustStats> systemStats,
            List<DecisionSummaryResponse> recentDecisions,
            List<GovernanceViolationResponse> activeViolationsList,
            UserAutonomyPreferencesResponse autonomyPreferences
    ) {}

    public record SystemTrustStats(
            String sourceSystem,
            Integer totalDecisions,
            Double averageTrustScore,
            Integer averageConfidence,
            String dominantTrustTier,
            Integer agreeCount,
            Integer disagreeCount
    ) {}

    public record DecisionSummaryResponse(
            String id,
            String deviceId,
            String sourceSystem,
            String decisionType,
            Integer confidenceScore,
            Integer trustScore,
            String trustTier,
            String riskLevel,
            String status,
            Boolean userReviewed,
            String userFeedback,
            String createdAt
    ) {}

    // ─── Decision Audit Detail ──────────────────────────────────────────

    public record DecisionAuditResponse(
            String id,
            String userId,
            String deviceId,
            String sourceSystem,
            String decisionType,
            String sourceRecordId,
            String decisionOutput,
            Integer confidenceScore,
            Integer trustScore,
            String trustTier,
            String riskLevel,
            String status,
            Boolean userReviewed,
            String userFeedback,
            String whyExplanation,
            String howExplanation,
            String whatIfExplanation,
            String impactExplanation,
            TrustScoreBreakdown trustBreakdown,
            List<EvidenceTraceResponse> evidenceTraces,
            List<GovernanceViolationResponse> violations,
            String createdAt,
            String updatedAt
    ) {}

    public record TrustScoreBreakdown(
            Integer confidenceComponent,
            Integer evidenceDensityComponent,
            Integer systemReliabilityComponent,
            Integer governanceComplianceComponent,
            Integer dataFreshnessComponent,
            Double confidenceWeight,
            Double evidenceDensityWeight,
            Double systemReliabilityWeight,
            Double governanceComplianceWeight,
            Double dataFreshnessWeight,
            Integer finalTrustScore,
            String trustTier
    ) {}

    // ─── Evidence ───────────────────────────────────────────────────────

    public record EvidenceTraceResponse(
            String id,
            String evidenceType,
            String evidenceKey,
            String evidenceValue,
            Double evidenceWeight,
            String evidenceSource
    ) {}

    // ─── Governance ─────────────────────────────────────────────────────

    public record GovernanceRuleResponse(
            String id,
            String ruleName,
            String ruleCategory,
            String description,
            String appliesToSystems,
            String severity,
            Double thresholdValue,
            Boolean isActive
    ) {}

    public record GovernanceViolationResponse(
            String id,
            String decisionRecordId,
            String ruleId,
            String ruleName,
            String violationMessage,
            String severity,
            Boolean autoResolved,
            String createdAt
    ) {}

    // ─── Feedback ───────────────────────────────────────────────────────

    public record DecisionFeedbackRequest(
            String feedback  // AGREE, DISAGREE, UNSURE
    ) {}

    // ─── User Autonomy Preferences ──────────────────────────────────────

    public record UserAutonomyPreferencesResponse(
            String id,
            String userId,
            Boolean allowAutonomousInterventions,
            Boolean allowAutoScheduling,
            Boolean allowProactiveAlerts,
            Integer minConfidenceThreshold,
            Double requireApprovalAboveCost,
            String notificationStyle
    ) {}

    public record UpdateAutonomyPreferencesRequest(
            Boolean allowAutonomousInterventions,
            Boolean allowAutoScheduling,
            Boolean allowProactiveAlerts,
            Integer minConfidenceThreshold,
            Double requireApprovalAboveCost,
            String notificationStyle
    ) {}
}
