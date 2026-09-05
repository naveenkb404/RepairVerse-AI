package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.DecisionAuditResponse;
import com.repairverse.ai.dto.TrustEngineDto.DecisionSummaryResponse;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionAuditServiceTest {

    @Mock
    private AiDecisionRecordRepository decisionRecordRepository;
    @Mock
    private AiDecisionEvidenceRepository evidenceRepository;
    @Mock
    private AiGovernanceViolationRepository violationRepository;
    @Mock
    private TrustScoreService trustScoreService;
    @Mock
    private EvidenceTraceService evidenceTraceService;
    @Mock
    private ExplainabilityService explainabilityService;
    @Mock
    private GovernanceRuleService governanceRuleService;

    @InjectMocks
    private DecisionAuditService decisionAuditService;

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
                .userReviewed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("recordDecision creates record, collects evidence, evaluates governance, and computes trust")
    void testRecordDecision() {
        when(decisionRecordRepository.save(any())).thenAnswer(inv -> {
            AiDecisionRecord r = inv.getArgument(0);
            if (r.getId() == null) r.setId("dec-new");
            return r;
        });
        when(evidenceTraceService.collectEvidence(any(), any(), any())).thenReturn(List.of());
        when(governanceRuleService.evaluateRules(any(), any())).thenReturn(List.of());
        when(trustScoreService.computeTrustScore(any(), any(), any())).thenReturn(80);
        when(trustScoreService.determineTrustTier(80)).thenReturn("RELIABLE");

        AiDecisionRecord result = decisionAuditService.recordDecision(
                "usr-1", "dev-1", "DIAGNOSIS", "FAULT_DIAGNOSIS",
                "diag-1", "{\"action\":\"test\"}", 82, "MEDIUM"
        );

        assertThat(result).isNotNull();
        assertThat(result.getTrustScore()).isEqualTo(80);
        assertThat(result.getTrustTier()).isEqualTo("RELIABLE");
        verify(evidenceTraceService).collectEvidence("DIAGNOSIS", "diag-1", "dev-1");
        verify(governanceRuleService).evaluateRules(any(), any());
        verify(explainabilityService).generateExplanations(any(), any());
        verify(decisionRecordRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("getDecisionAuditLog returns summaries ordered by creation date")
    void testGetDecisionAuditLog() {
        when(decisionRecordRepository.findAllByUserIdOrderByCreatedAtDesc("usr-1"))
                .thenReturn(List.of(testRecord));

        List<DecisionSummaryResponse> log = decisionAuditService.getDecisionAuditLog("usr-1");

        assertThat(log).hasSize(1);
        assertThat(log.get(0).id()).isEqualTo("dec-1");
        assertThat(log.get(0).sourceSystem()).isEqualTo("DIAGNOSIS");
    }

    @Test
    @DisplayName("getDecisionById returns full audit response with evidence and breakdown")
    void testGetDecisionById() {
        when(decisionRecordRepository.findById("dec-1")).thenReturn(Optional.of(testRecord));
        when(evidenceRepository.findAllByDecisionRecordId("dec-1")).thenReturn(List.of());
        when(violationRepository.findAllByDecisionRecordId("dec-1")).thenReturn(List.of());
        when(trustScoreService.buildBreakdown(any(), any(), any()))
                .thenReturn(new com.repairverse.ai.dto.TrustEngineDto.TrustScoreBreakdown(
                        82, 40, 82, 100, 100, 0.30, 0.25, 0.20, 0.15, 0.10, 78, "RELIABLE"));

        DecisionAuditResponse response = decisionAuditService.getDecisionById("dec-1", "usr-1");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("dec-1");
        assertThat(response.trustBreakdown().finalTrustScore()).isEqualTo(78);
    }

    @Test
    @DisplayName("getDecisionById throws SecurityException for unauthorized user")
    void testGetDecisionById_Unauthorized() {
        when(decisionRecordRepository.findById("dec-1")).thenReturn(Optional.of(testRecord));

        assertThatThrownBy(() -> decisionAuditService.getDecisionById("dec-1", "usr-other"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("markReviewed sets userReviewed to true")
    void testMarkReviewed() {
        when(decisionRecordRepository.findById("dec-1")).thenReturn(Optional.of(testRecord));
        when(decisionRecordRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(evidenceRepository.findAllByDecisionRecordId("dec-1")).thenReturn(List.of());
        when(violationRepository.findAllByDecisionRecordId("dec-1")).thenReturn(List.of());
        when(trustScoreService.buildBreakdown(any(), any(), any()))
                .thenReturn(new com.repairverse.ai.dto.TrustEngineDto.TrustScoreBreakdown(
                        82, 40, 82, 100, 100, 0.30, 0.25, 0.20, 0.15, 0.10, 78, "RELIABLE"));

        DecisionAuditResponse response = decisionAuditService.markReviewed("dec-1", "usr-1");

        assertThat(response.userReviewed()).isTrue();
    }
}
