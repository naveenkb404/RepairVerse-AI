package com.repairverse.ai.service;

import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import com.repairverse.ai.entity.AiGovernanceRule;
import com.repairverse.ai.entity.AiGovernanceViolation;
import com.repairverse.ai.repository.AiGovernanceRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GovernanceRuleServiceTest {

    @Mock
    private AiGovernanceRuleRepository ruleRepository;

    @InjectMocks
    private GovernanceRuleService governanceRuleService;

    private AiDecisionRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = AiDecisionRecord.builder()
                .id("dec-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .sourceSystem("DIAGNOSIS")
                .decisionType("FAULT_DIAGNOSIS")
                .decisionOutput("{\"action\":\"replace_battery\",\"cost\":3500}")
                .confidenceScore(82)
                .trustScore(78)
                .trustTier("RELIABLE")
                .riskLevel("MEDIUM")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("LOW_CONFIDENCE_BLOCKER triggers when confidence below threshold")
    void testLowConfidenceBlocker() {
        testRecord.setConfidenceScore(40);
        AiGovernanceRule rule = buildRule("LOW_CONFIDENCE_BLOCKER", "SAFETY", "BLOCKER", 50.0, "ALL");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, List.of());

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getSeverity()).isEqualTo("BLOCKER");
        assertThat(violations.get(0).getViolationMessage()).contains("below the minimum threshold");
    }

    @Test
    @DisplayName("LOW_CONFIDENCE_BLOCKER does NOT trigger when confidence above threshold")
    void testLowConfidenceBlocker_NotTriggered() {
        testRecord.setConfidenceScore(85);
        AiGovernanceRule rule = buildRule("LOW_CONFIDENCE_BLOCKER", "SAFETY", "BLOCKER", 50.0, "ALL");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, List.of());

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CASCADING_CRITICAL_ALERT triggers for CRITICAL risk decisions")
    void testCascadingCritical() {
        testRecord.setRiskLevel("CRITICAL");
        AiGovernanceRule rule = buildRule("CASCADING_CRITICAL_ALERT", "SAFETY", "WARNING", 0.0, "ALL");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, List.of());

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getViolationMessage()).contains("CRITICAL-risk");
    }

    @Test
    @DisplayName("CONFLICTING_RECOMMENDATIONS triggers when health is high but risk is also high")
    void testConflictingRecommendations() {
        AiGovernanceRule rule = buildRule("CONFLICTING_RECOMMENDATIONS", "CONSISTENCY", "WARNING", 0.0, "ALL");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiDecisionEvidence> evidence = List.of(
                AiDecisionEvidence.builder()
                        .evidenceType("HEALTH_SCORE").evidenceKey("Health")
                        .evidenceValue("85").evidenceWeight(0.9).build(),
                AiDecisionEvidence.builder()
                        .evidenceType("FAILURE_PROBABILITY").evidenceKey("Failure Risk")
                        .evidenceValue("0.6").evidenceWeight(0.9).build()
        );

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, evidence);

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getViolationMessage()).contains("Conflicting signals");
    }

    @Test
    @DisplayName("AUTONOMOUS_RATE_LIMIT only triggers for AUTONOMOUS_AGENT system")
    void testAutonomousRateLimit() {
        testRecord.setSourceSystem("AUTONOMOUS_AGENT");
        testRecord.setConfidenceScore(60);
        AiGovernanceRule rule = buildRule("AUTONOMOUS_RATE_LIMIT", "SAFETY", "WARNING", 70.0, "AUTONOMOUS_AGENT");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, List.of());

        assertThat(violations).hasSize(1);
        assertThat(violations.get(0).getViolationMessage()).contains("Autonomous agent");
    }

    @Test
    @DisplayName("Rule with non-matching appliesToSystems is skipped")
    void testRuleSkippedForNonMatchingSystem() {
        testRecord.setSourceSystem("DIAGNOSIS");
        testRecord.setConfidenceScore(60);
        AiGovernanceRule rule = buildRule("AUTONOMOUS_RATE_LIMIT", "SAFETY", "WARNING", 70.0, "AUTONOMOUS_AGENT");
        when(ruleRepository.findAllByIsActiveTrue()).thenReturn(List.of(rule));

        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(testRecord, List.of());

        assertThat(violations).isEmpty();
    }

    private AiGovernanceRule buildRule(String name, String category, String severity,
                                       double threshold, String appliesTo) {
        return AiGovernanceRule.builder()
                .id("rule-" + name)
                .ruleName(name)
                .ruleCategory(category)
                .severity(severity)
                .thresholdValue(threshold)
                .appliesToSystems(appliesTo)
                .isActive(true)
                .build();
    }
}
