package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.TrustScoreBreakdown;
import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import com.repairverse.ai.entity.AiGovernanceViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Phase 34: Deterministic 5-factor weighted trust score computation.
 *
 * Formula:
 *   trustScore = (confidenceScore × 0.30)
 *              + (evidenceDensityScore × 0.25)
 *              + (systemReliabilityScore × 0.20)
 *              + (governanceComplianceScore × 0.15)
 *              + (dataFreshnessScore × 0.10)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrustScoreService {

    /** Baseline reliability for each intelligence system */
    private static final Map<String, Integer> SYSTEM_RELIABILITY_BASELINES = Map.of(
            "DIGITAL_TWIN", 92,
            "KNOWLEDGE_GRAPH", 88,
            "PREDICTIVE", 85,
            "DEVICE_INTELLIGENCE", 84,
            "DIAGNOSIS", 82,
            "CIRCULAR_ECONOMY", 80,
            "AUTONOMOUS_AGENT", 79
    );

    /**
     * Compute the composite trust score for a decision record.
     *
     * @param record        the decision record (must have confidenceScore and createdAt set)
     * @param evidenceList  evidence signals collected for this decision
     * @param violations    governance violations triggered for this decision
     * @return final weighted trust score (0–100)
     */
    public int computeTrustScore(AiDecisionRecord record,
                                 List<AiDecisionEvidence> evidenceList,
                                 List<AiGovernanceViolation> violations) {
        int confidence = computeConfidenceComponent(record.getConfidenceScore());
        int evidenceDensity = computeEvidenceDensityComponent(evidenceList);
        int systemReliability = computeSystemReliabilityComponent(record.getSourceSystem());
        int governanceCompliance = computeGovernanceComplianceComponent(violations);
        int dataFreshness = computeDataFreshnessComponent(record.getCreatedAt());

        double weighted = (confidence * 0.30)
                + (evidenceDensity * 0.25)
                + (systemReliability * 0.20)
                + (governanceCompliance * 0.15)
                + (dataFreshness * 0.10);

        int score = (int) Math.round(Math.min(100, Math.max(0, weighted)));
        log.debug("Trust score for decision '{}': conf={}, evid={}, sysRel={}, gov={}, fresh={} → {}",
                record.getId(), confidence, evidenceDensity, systemReliability, governanceCompliance, dataFreshness, score);
        return score;
    }

    /**
     * Build a detailed breakdown DTO for explainability.
     */
    public TrustScoreBreakdown buildBreakdown(AiDecisionRecord record,
                                              List<AiDecisionEvidence> evidenceList,
                                              List<AiGovernanceViolation> violations) {
        int confidence = computeConfidenceComponent(record.getConfidenceScore());
        int evidenceDensity = computeEvidenceDensityComponent(evidenceList);
        int systemReliability = computeSystemReliabilityComponent(record.getSourceSystem());
        int governanceCompliance = computeGovernanceComplianceComponent(violations);
        int dataFreshness = computeDataFreshnessComponent(record.getCreatedAt());
        int finalScore = computeTrustScore(record, evidenceList, violations);
        String tier = determineTrustTier(finalScore);

        return new TrustScoreBreakdown(
                confidence, evidenceDensity, systemReliability, governanceCompliance, dataFreshness,
                0.30, 0.25, 0.20, 0.15, 0.10,
                finalScore, tier
        );
    }

    /**
     * Map a numeric trust score to a tier label.
     */
    public String determineTrustTier(int trustScore) {
        if (trustScore >= 85) return "VERIFIED";
        if (trustScore >= 70) return "RELIABLE";
        if (trustScore >= 55) return "CAUTION";
        return "REVIEW_REQUIRED";
    }

    // ─── Component scorers ──────────────────────────────────────────────

    private int computeConfidenceComponent(int confidenceScore) {
        return Math.min(100, Math.max(0, confidenceScore));
    }

    /**
     * Evidence density: 0 signals → 40, 1 → 55, 2 → 65, 3 → 75, 4 → 85, 5+ → 95.
     */
    private int computeEvidenceDensityComponent(List<AiDecisionEvidence> evidenceList) {
        int count = evidenceList != null ? evidenceList.size() : 0;
        if (count == 0) return 40;
        if (count == 1) return 55;
        if (count == 2) return 65;
        if (count == 3) return 75;
        if (count == 4) return 85;
        return 95;
    }

    private int computeSystemReliabilityComponent(String sourceSystem) {
        return SYSTEM_RELIABILITY_BASELINES.getOrDefault(sourceSystem, 75);
    }

    /**
     * Governance compliance: 100 if no violations, minus 15 per WARNING, minus 30 per BLOCKER.
     */
    private int computeGovernanceComplianceComponent(List<AiGovernanceViolation> violations) {
        if (violations == null || violations.isEmpty()) return 100;
        int penalty = 0;
        for (AiGovernanceViolation v : violations) {
            if ("BLOCKER".equals(v.getSeverity())) {
                penalty += 30;
            } else if ("WARNING".equals(v.getSeverity())) {
                penalty += 15;
            } else {
                penalty += 5;
            }
        }
        return Math.max(0, 100 - penalty);
    }

    /**
     * Data freshness: 100 if created within 1 hour, degrades by 3 per hour, floor 40.
     */
    private int computeDataFreshnessComponent(LocalDateTime createdAt) {
        if (createdAt == null) return 70;
        long hoursAgo = ChronoUnit.HOURS.between(createdAt, LocalDateTime.now());
        if (hoursAgo <= 0) return 100;
        int score = (int) (100 - (hoursAgo * 3));
        return Math.max(40, Math.min(100, score));
    }
}
