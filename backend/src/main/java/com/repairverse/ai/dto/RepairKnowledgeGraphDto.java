package com.repairverse.ai.dto;

import java.util.List;
import java.util.Map;

public class RepairKnowledgeGraphDto {

    public record KnowledgeNodeResponse(
            String id,
            String nodeType,
            String nodeKey,
            String displayName,
            String description,
            String metadata,
            Double confidenceScore,
            Integer observationCount
    ) {}

    public record KnowledgeRelationshipResponse(
            String id,
            String sourceNodeId,
            String sourceDisplayName,
            String sourceNodeType,
            String targetNodeId,
            String targetDisplayName,
            String targetNodeType,
            String relationshipType,
            Double strength,
            Double confidence,
            Integer observationCount
    ) {}

    public record KnowledgeGraphResponse(
            List<KnowledgeNodeResponse> nodes,
            List<KnowledgeRelationshipResponse> relationships,
            KnowledgeGraphStatisticsResponse statistics,
            String generatedAt
    ) {}

    public record PatternInsightResponse(
            String id,
            String insightType,
            String title,
            String description,
            Double confidence,
            Integer impactScore,
            Integer supportingObservations,
            String deviceCategory,
            String status,
            String generatedAt,
            Long helpfulVotes,
            Long inaccurateVotes
    ) {}

    public record SimilarRepairCaseResponse(
            String caseId,
            Double similarityScore,
            String deviceCategory,
            String deviceModel,
            String issueSummary,
            String componentRepaired,
            String repairAction,
            String outcomeStatus,
            String costRange,
            Double co2AvoidedKg,
            Integer durationDays,
            String lessonLearned
    ) {}

    public record RepairSuccessPatternResponse(
            String failureMode,
            String repairAction,
            Double successRate,
            Double averageCost,
            Integer observedCases,
            String bestPractice
    ) {}

    public record KnowledgeRecommendationResponse(
            String id,
            String recommendation,
            Double confidence,
            Integer supportingCases,
            String expectedOutcome,
            String reasoning,
            String evidenceSummary,
            String priority
    ) {}

    public record DeviceKnowledgeProfileResponse(
            String deviceId,
            String deviceName,
            String deviceCategory,
            List<KnowledgeNodeResponse> matchedNodes,
            List<PatternInsightResponse> directInsights,
            List<SimilarRepairCaseResponse> similarCases,
            List<KnowledgeRecommendationResponse> recommendations,
            Integer totalObservedPatterns
    ) {}

    public record KnowledgeFeedbackRequest(
            String feedbackType,
            Integer rating,
            String comment
    ) {}

    public record KnowledgeGraphStatisticsResponse(
            Long totalNodes,
            Long totalRelationships,
            Long totalInsights,
            Long observedRepairsCount,
            Double averageConfidence,
            Map<String, Long> nodeTypeDistribution,
            Map<String, Long> relationshipTypeDistribution
    ) {}
}
