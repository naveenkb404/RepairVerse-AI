package com.repairverse.ai.service;

import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ExplainabilityServiceTest {

    @InjectMocks
    private ExplainabilityService explainabilityService;

    private AiDecisionRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = AiDecisionRecord.builder()
                .id("dec-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .sourceSystem("DIAGNOSIS")
                .decisionType("FAULT_DIAGNOSIS")
                .decisionOutput("{\"action\":\"replace_battery\"}")
                .confidenceScore(82)
                .trustScore(78)
                .trustTier("RELIABLE")
                .riskLevel("MEDIUM")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("generateExplanations populates all four explanation fields")
    void testGenerateExplanations_AllFields() {
        List<AiDecisionEvidence> evidence = List.of(
                AiDecisionEvidence.builder()
                        .evidenceType("HEALTH_SCORE")
                        .evidenceKey("Device Health Score")
                        .evidenceValue("78")
                        .evidenceWeight(0.85)
                        .evidenceSource("device_health")
                        .build()
        );

        explainabilityService.generateExplanations(testRecord, evidence);

        assertThat(testRecord.getWhyExplanation()).isNotBlank();
        assertThat(testRecord.getHowExplanation()).isNotBlank();
        assertThat(testRecord.getWhatIfExplanation()).isNotBlank();
        assertThat(testRecord.getImpactExplanation()).isNotBlank();
    }

    @Test
    @DisplayName("Why explanation includes source system, confidence, and evidence summary")
    void testWhyExplanation_Content() {
        List<AiDecisionEvidence> evidence = List.of(
                AiDecisionEvidence.builder()
                        .evidenceType("HEALTH_SCORE")
                        .evidenceKey("Device Health Score")
                        .evidenceValue("78")
                        .evidenceWeight(0.85)
                        .evidenceSource("device_health")
                        .build()
        );

        explainabilityService.generateExplanations(testRecord, evidence);

        assertThat(testRecord.getWhyExplanation()).contains("DIAGNOSIS");
        assertThat(testRecord.getWhyExplanation()).contains("82%");
        assertThat(testRecord.getWhyExplanation()).contains("Device Health Score");
    }

    @Test
    @DisplayName("How explanation mentions evidence count and deterministic scoring")
    void testHowExplanation_Content() {
        List<AiDecisionEvidence> evidence = List.of(
                AiDecisionEvidence.builder()
                        .evidenceType("HEALTH_SCORE").evidenceKey("Health").evidenceValue("78")
                        .evidenceWeight(0.85).evidenceSource("device_health").build(),
                AiDecisionEvidence.builder()
                        .evidenceType("BATTERY_HEALTH").evidenceKey("Battery").evidenceValue("65")
                        .evidenceWeight(0.5).evidenceSource("device_health").build()
        );

        explainabilityService.generateExplanations(testRecord, evidence);

        assertThat(testRecord.getHowExplanation()).contains("2 evidence signal");
        assertThat(testRecord.getHowExplanation()).contains("deterministic");
    }

    @Test
    @DisplayName("What-if explanation varies based on confidence level")
    void testWhatIfExplanation_VariesByConfidence() {
        explainabilityService.generateExplanations(testRecord, List.of());
        String highConfExplanation = testRecord.getWhatIfExplanation();

        testRecord.setConfidenceScore(45);
        explainabilityService.generateExplanations(testRecord, List.of());
        String lowConfExplanation = testRecord.getWhatIfExplanation();

        assertThat(highConfExplanation).isNotEqualTo(lowConfExplanation);
    }

    @Test
    @DisplayName("Impact explanation varies based on risk level")
    void testImpactExplanation_VariesByRiskLevel() {
        testRecord.setRiskLevel("CRITICAL");
        explainabilityService.generateExplanations(testRecord, List.of());
        String criticalImpact = testRecord.getImpactExplanation();

        testRecord.setRiskLevel("LOW");
        explainabilityService.generateExplanations(testRecord, List.of());
        String lowImpact = testRecord.getImpactExplanation();

        assertThat(criticalImpact).contains("Immediate action");
        assertThat(lowImpact).contains("Low impact");
    }

    @Test
    @DisplayName("Empty evidence list produces 'no additional evidence' in why explanation")
    void testEmptyEvidence() {
        explainabilityService.generateExplanations(testRecord, List.of());

        assertThat(testRecord.getWhyExplanation()).contains("no additional evidence");
    }
}
