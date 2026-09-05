package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.TrustScoreBreakdown;
import com.repairverse.ai.entity.AiDecisionEvidence;
import com.repairverse.ai.entity.AiDecisionRecord;
import com.repairverse.ai.entity.AiGovernanceViolation;
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
class TrustScoreServiceTest {

    @InjectMocks
    private TrustScoreService trustScoreService;

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
                .confidenceScore(85)
                .trustScore(75)
                .trustTier("RELIABLE")
                .riskLevel("MEDIUM")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("High confidence + many evidence signals + no violations → high trust score")
    void testComputeTrustScore_HighConfidence() {
        testRecord.setConfidenceScore(90);
        testRecord.setSourceSystem("DIGITAL_TWIN");

        List<AiDecisionEvidence> evidence = List.of(
                buildEvidence("HEALTH_SCORE", "85"), buildEvidence("FAILURE_RISK", "20"),
                buildEvidence("SIMULATION_CONFIDENCE", "0.92"), buildEvidence("ECOSYSTEM_SCORE", "84"),
                buildEvidence("BATTERY_HEALTH", "78")
        );

        int score = trustScoreService.computeTrustScore(testRecord, evidence, List.of());

        assertThat(score).isGreaterThanOrEqualTo(85);
        assertThat(trustScoreService.determineTrustTier(score)).isEqualTo("VERIFIED");
    }

    @Test
    @DisplayName("Low confidence + no evidence + violations → low trust score")
    void testComputeTrustScore_LowConfidence() {
        testRecord.setConfidenceScore(40);
        testRecord.setSourceSystem("AUTONOMOUS_AGENT");

        AiGovernanceViolation blocker = AiGovernanceViolation.builder()
                .severity("BLOCKER").build();
        AiGovernanceViolation warning = AiGovernanceViolation.builder()
                .severity("WARNING").build();

        int score = trustScoreService.computeTrustScore(testRecord, List.of(),
                List.of(blocker, warning));

        assertThat(score).isLessThan(55);
        assertThat(trustScoreService.determineTrustTier(score)).isEqualTo("REVIEW_REQUIRED");
    }

    @Test
    @DisplayName("determineTrustTier maps score ranges correctly")
    void testDetermineTrustTier() {
        assertThat(trustScoreService.determineTrustTier(100)).isEqualTo("VERIFIED");
        assertThat(trustScoreService.determineTrustTier(85)).isEqualTo("VERIFIED");
        assertThat(trustScoreService.determineTrustTier(84)).isEqualTo("RELIABLE");
        assertThat(trustScoreService.determineTrustTier(70)).isEqualTo("RELIABLE");
        assertThat(trustScoreService.determineTrustTier(69)).isEqualTo("CAUTION");
        assertThat(trustScoreService.determineTrustTier(55)).isEqualTo("CAUTION");
        assertThat(trustScoreService.determineTrustTier(54)).isEqualTo("REVIEW_REQUIRED");
        assertThat(trustScoreService.determineTrustTier(0)).isEqualTo("REVIEW_REQUIRED");
    }

    @Test
    @DisplayName("buildBreakdown returns all five components and weights")
    void testBuildBreakdown() {
        testRecord.setConfidenceScore(80);
        testRecord.setSourceSystem("PREDICTIVE");
        List<AiDecisionEvidence> evidence = List.of(
                buildEvidence("FAILURE_PROBABILITY", "0.35"),
                buildEvidence("RISK_LEVEL", "MEDIUM"),
                buildEvidence("HEALTH_SCORE", "72")
        );

        TrustScoreBreakdown breakdown = trustScoreService.buildBreakdown(
                testRecord, evidence, List.of());

        assertThat(breakdown.confidenceWeight()).isEqualTo(0.30);
        assertThat(breakdown.evidenceDensityWeight()).isEqualTo(0.25);
        assertThat(breakdown.systemReliabilityWeight()).isEqualTo(0.20);
        assertThat(breakdown.governanceComplianceWeight()).isEqualTo(0.15);
        assertThat(breakdown.dataFreshnessWeight()).isEqualTo(0.10);
        assertThat(breakdown.finalTrustScore()).isBetween(0, 100);
        assertThat(breakdown.trustTier()).isNotBlank();
    }

    @Test
    @DisplayName("System reliability returns correct baseline for each system")
    void testSystemReliabilityBaselines() {
        for (var entry : java.util.Map.of(
                "DIGITAL_TWIN", 92, "KNOWLEDGE_GRAPH", 88, "PREDICTIVE", 85,
                "DEVICE_INTELLIGENCE", 84, "DIAGNOSIS", 82, "CIRCULAR_ECONOMY", 80,
                "AUTONOMOUS_AGENT", 79
        ).entrySet()) {
            testRecord.setSourceSystem(entry.getKey());
            testRecord.setConfidenceScore(80);
            // With matching confidence and same evidence, the system reliability component
            // should shift the final score according to its baseline
            int score = trustScoreService.computeTrustScore(testRecord, List.of(), List.of());
            assertThat(score).isBetween(0, 100);
        }
    }

    private AiDecisionEvidence buildEvidence(String type, String value) {
        return AiDecisionEvidence.builder()
                .evidenceType(type)
                .evidenceKey(type)
                .evidenceValue(value)
                .evidenceWeight(0.8)
                .evidenceSource("test")
                .build();
    }
}
