package com.repairverse.ai.service;

import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Phase 34: Deterministic natural-language explanation generator.
 * Produces why / how / what-if / impact explanations from decision metadata
 * and evidence signals — no external AI APIs involved.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExplainabilityService {

    /**
     * Generate all four explanation strings and set them on the record (mutates in-place).
     */
    public void generateExplanations(AiDecisionRecord record,
                                     List<AiDecisionEvidence> evidenceList) {
        record.setWhyExplanation(generateWhyExplanation(record, evidenceList));
        record.setHowExplanation(generateHowExplanation(record, evidenceList));
        record.setWhatIfExplanation(generateWhatIfExplanation(record));
        record.setImpactExplanation(generateImpactExplanation(record));
        log.debug("Generated explanations for decision '{}'", record.getId());
    }

    // ─── Templates ──────────────────────────────────────────────────────

    private String generateWhyExplanation(AiDecisionRecord record,
                                          List<AiDecisionEvidence> evidenceList) {
        String evidenceSummary = summarizeEvidence(evidenceList);
        return String.format(
                "This %s decision was triggered by the %s system because %s. " +
                "The AI confidence level is %d%% (trust tier: %s). " +
                "Key contributing signals: %s.",
                record.getDecisionType(),
                record.getSourceSystem(),
                describeDecisionReason(record),
                record.getConfidenceScore(),
                record.getTrustTier(),
                evidenceSummary
        );
    }

    private String generateHowExplanation(AiDecisionRecord record,
                                          List<AiDecisionEvidence> evidenceList) {
        int evidenceCount = evidenceList != null ? evidenceList.size() : 0;
        return String.format(
                "The %s system analysed %d evidence signal(s) collected from device telemetry, " +
                "repair history, and predictive models. Each signal was weighted by relevance " +
                "and combined using deterministic scoring rules. The final confidence of %d%% " +
                "was computed without any external AI API calls — all calculations are transparent " +
                "and reproducible.",
                record.getSourceSystem(),
                evidenceCount,
                record.getConfidenceScore()
        );
    }

    private String generateWhatIfExplanation(AiDecisionRecord record) {
        String alternative;
        if (record.getConfidenceScore() >= 85) {
            alternative = "If additional maintenance had been performed earlier, the confidence " +
                    "could have been even higher, but the current level is already strong.";
        } else if (record.getConfidenceScore() >= 70) {
            alternative = "If the device had more recent health data or repair history, " +
                    "the confidence score might reach the VERIFIED tier (85+).";
        } else if (record.getConfidenceScore() >= 55) {
            alternative = "If the underlying data were fresher or additional evidence signals " +
                    "were available, this decision could move from CAUTION to RELIABLE tier.";
        } else {
            alternative = "This decision has low confidence. Providing more device data, " +
                    "running a fresh diagnosis, or updating the device health profile would " +
                    "significantly improve trust in this recommendation.";
        }
        return String.format(
                "Alternative scenario analysis for this %s decision: %s " +
                "Current risk level: %s.",
                record.getDecisionType(), alternative, record.getRiskLevel()
        );
    }

    private String generateImpactExplanation(AiDecisionRecord record) {
        String impact;
        switch (record.getRiskLevel()) {
            case "CRITICAL" -> impact = "Immediate action is strongly recommended. " +
                    "Ignoring this decision could lead to device failure, data loss, or significantly increased repair costs.";
            case "HIGH" -> impact = "Timely attention is recommended. " +
                    "Delaying action may result in accelerated device degradation and higher future costs.";
            case "MEDIUM" -> impact = "Moderate impact expected. " +
                    "Following this recommendation will help maintain device longevity and optimal performance.";
            default -> impact = "Low impact scenario. " +
                    "This is a routine recommendation that contributes to overall device health optimization.";
        }
        return String.format(
                "Impact assessment for %s (source: %s): %s",
                record.getDecisionType(), record.getSourceSystem(), impact
        );
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private String summarizeEvidence(List<AiDecisionEvidence> evidenceList) {
        if (evidenceList == null || evidenceList.isEmpty()) {
            return "no additional evidence signals available";
        }
        return evidenceList.stream()
                .map(e -> e.getEvidenceKey() + " = " + e.getEvidenceValue())
                .collect(Collectors.joining(", "));
    }

    private String describeDecisionReason(AiDecisionRecord record) {
        return switch (record.getDecisionType()) {
            case "FAULT_DIAGNOSIS" -> "a potential fault was identified through diagnostic analysis";
            case "FAILURE_RISK_ASSESSMENT" -> "predictive models detected elevated failure risk";
            case "REPAIR_VS_REPLACE" -> "a cost-benefit comparison was needed for repair vs replacement";
            case "STRATEGY_OPTIMIZATION" -> "the system identified an opportunity to optimize repair strategy";
            case "AUTONOMOUS_INTERVENTION" -> "an autonomous agent detected a condition requiring intervention";
            case "SCENARIO_SIMULATION" -> "a digital twin simulation projected future device outcomes";
            case "SUSTAINABILITY_ASSESSMENT" -> "circular economy metrics were evaluated for environmental impact";
            default -> "intelligence signals required an actionable recommendation";
        };
    }
}
