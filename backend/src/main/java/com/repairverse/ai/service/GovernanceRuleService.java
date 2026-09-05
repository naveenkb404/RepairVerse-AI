package com.repairverse.ai.service;

import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import com.repairverse.ai.entity.AiGovernanceRule;
import com.repairverse.ai.entity.AiGovernanceViolation;
import com.repairverse.ai.repository.AiGovernanceRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 34: Evaluates all active governance rules against a decision record
 * and its evidence, producing a list of governance violations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GovernanceRuleService {

    private final AiGovernanceRuleRepository ruleRepository;

    /**
     * Evaluate all active governance rules against the given decision.
     *
     * @return list of violations (empty if decision is fully compliant)
     */
    public List<AiGovernanceViolation> evaluateRules(AiDecisionRecord record,
                                                      List<AiDecisionEvidence> evidenceList) {
        List<AiGovernanceRule> activeRules = ruleRepository.findAllByIsActiveTrue();
        List<AiGovernanceViolation> violations = new ArrayList<>();

        for (AiGovernanceRule rule : activeRules) {
            if (!appliesToSystem(rule, record.getSourceSystem())) {
                continue;
            }
            AiGovernanceViolation violation = evaluateRule(rule, record, evidenceList);
            if (violation != null) {
                violations.add(violation);
                log.info("Governance violation triggered: rule='{}', decision='{}', severity='{}'",
                        rule.getRuleName(), record.getId(), rule.getSeverity());
            }
        }
        return violations;
    }

    // ─── Individual rule evaluators ─────────────────────────────────────

    private AiGovernanceViolation evaluateRule(AiGovernanceRule rule,
                                               AiDecisionRecord record,
                                               List<AiDecisionEvidence> evidenceList) {
        return switch (rule.getRuleName()) {
            case "LOW_CONFIDENCE_BLOCKER" -> evaluateLowConfidence(rule, record);
            case "HIGH_COST_APPROVAL_GATE" -> evaluateHighCost(rule, record);
            case "CONFLICTING_RECOMMENDATIONS" -> evaluateConflicting(rule, record, evidenceList);
            case "STALE_DATA_WARNING" -> evaluateStaleData(rule, record);
            case "CASCADING_CRITICAL_ALERT" -> evaluateCascadingCritical(rule, record);
            case "AUTONOMOUS_RATE_LIMIT" -> evaluateRateLimit(rule, record);
            default -> null;
        };
    }

    /**
     * LOW_CONFIDENCE_BLOCKER: Blocks decisions with confidence < threshold (default 50).
     */
    private AiGovernanceViolation evaluateLowConfidence(AiGovernanceRule rule,
                                                        AiDecisionRecord record) {
        if (record.getConfidenceScore() < rule.getThresholdValue()) {
            return buildViolation(rule, record.getId(),
                    String.format("Decision confidence (%d%%) is below the minimum threshold (%.0f%%). " +
                                    "This decision requires human review before any action is taken.",
                            record.getConfidenceScore(), rule.getThresholdValue()));
        }
        return null;
    }

    /**
     * HIGH_COST_APPROVAL_GATE: Warns when the decision output references costs
     * above the threshold. Uses a heuristic check on the decision output text.
     */
    private AiGovernanceViolation evaluateHighCost(AiGovernanceRule rule,
                                                    AiDecisionRecord record) {
        // Parse cost from decision output heuristically
        String output = record.getDecisionOutput();
        if (output != null && output.contains("cost")) {
            try {
                // Look for numeric patterns after "cost" keywords
                String[] tokens = output.replaceAll("[^0-9. ]", " ").trim().split("\\s+");
                for (String token : tokens) {
                    if (!token.isEmpty()) {
                        double value = Double.parseDouble(token);
                        if (value > rule.getThresholdValue()) {
                            return buildViolation(rule, record.getId(),
                                    String.format("Estimated cost (₹%.0f) exceeds the approval threshold (₹%.0f). " +
                                                    "User approval is required before proceeding.",
                                            value, rule.getThresholdValue()));
                        }
                    }
                }
            } catch (NumberFormatException ignored) {
                // Not a parseable cost — no violation
            }
        }
        return null;
    }

    /**
     * CONFLICTING_RECOMMENDATIONS: Triggers when evidence signals show inconsistent
     * directional indicators (e.g., high health + high failure risk).
     */
    private AiGovernanceViolation evaluateConflicting(AiGovernanceRule rule,
                                                       AiDecisionRecord record,
                                                       List<AiDecisionEvidence> evidenceList) {
        if (evidenceList == null || evidenceList.size() < 2) return null;

        boolean hasHighHealth = false;
        boolean hasHighRisk = false;
        for (AiDecisionEvidence e : evidenceList) {
            try {
                double val = Double.parseDouble(e.getEvidenceValue());
                if ("HEALTH_SCORE".equals(e.getEvidenceType()) && val >= 80) hasHighHealth = true;
                if ("FAILURE_PROBABILITY".equals(e.getEvidenceType()) && val >= 0.5) hasHighRisk = true;
                if ("FAILURE_RISK".equals(e.getEvidenceType()) && val >= 60) hasHighRisk = true;
            } catch (NumberFormatException ignored) {}
        }

        if (hasHighHealth && hasHighRisk) {
            return buildViolation(rule, record.getId(),
                    "Conflicting signals detected: device health score is high but failure risk is also elevated. " +
                    "This inconsistency may indicate stale or incomplete data.");
        }
        return null;
    }

    /**
     * STALE_DATA_WARNING: Warns when confidence is moderate and the decision references
     * data freshness concerns.
     */
    private AiGovernanceViolation evaluateStaleData(AiGovernanceRule rule,
                                                     AiDecisionRecord record) {
        if (record.getConfidenceScore() < rule.getThresholdValue()
                && record.getConfidenceScore() >= rule.getThresholdValue() - 20) {
            return buildViolation(rule, record.getId(),
                    String.format("Decision confidence (%d%%) suggests underlying data may be stale. " +
                                    "Consider running a fresh device health scan for more accurate results.",
                            record.getConfidenceScore()));
        }
        return null;
    }

    /**
     * CASCADING_CRITICAL_ALERT: Alerts when risk is CRITICAL or HIGH.
     */
    private AiGovernanceViolation evaluateCascadingCritical(AiGovernanceRule rule,
                                                            AiDecisionRecord record) {
        if ("CRITICAL".equals(record.getRiskLevel()) || "HIGH".equals(record.getRiskLevel())) {
            return buildViolation(rule, record.getId(),
                    String.format("This %s-risk decision may trigger cascading effects across the repair ecosystem. " +
                                    "Careful review is recommended before any autonomous action.",
                            record.getRiskLevel()));
        }
        return null;
    }

    /**
     * AUTONOMOUS_RATE_LIMIT: Warns autonomous agent decisions that have low confidence.
     */
    private AiGovernanceViolation evaluateRateLimit(AiGovernanceRule rule,
                                                     AiDecisionRecord record) {
        if ("AUTONOMOUS_AGENT".equals(record.getSourceSystem())
                && record.getConfidenceScore() < rule.getThresholdValue()) {
            return buildViolation(rule, record.getId(),
                    String.format("Autonomous agent decision has confidence %d%% which is below the " +
                                    "rate-limit threshold (%.0f%%). Manual review is recommended.",
                            record.getConfidenceScore(), rule.getThresholdValue()));
        }
        return null;
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private boolean appliesToSystem(AiGovernanceRule rule, String sourceSystem) {
        String applies = rule.getAppliesToSystems();
        if (applies == null || applies.isBlank() || "ALL".equalsIgnoreCase(applies)) return true;
        return applies.contains(sourceSystem);
    }

    private AiGovernanceViolation buildViolation(AiGovernanceRule rule,
                                                  String decisionRecordId,
                                                  String message) {
        return AiGovernanceViolation.builder()
                .decisionRecordId(decisionRecordId)
                .ruleId(rule.getId())
                .violationMessage(message)
                .severity(rule.getSeverity())
                .autoResolved(false)
                .build();
    }
}
