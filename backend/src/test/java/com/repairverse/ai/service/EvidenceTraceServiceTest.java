package com.repairverse.ai.service;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvidenceTraceServiceTest {

    @Mock
    private DeviceHealthRepository deviceHealthRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private DigitalTwinSnapshotRepository digitalTwinSnapshotRepository;
    @Mock
    private AutonomousInterventionRepository autonomousInterventionRepository;
    @Mock
    private RepairKnowledgeNodeRepository repairKnowledgeNodeRepository;
    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @InjectMocks
    private EvidenceTraceService evidenceTraceService;

    private DeviceHealth testHealth;

    @BeforeEach
    void setUp() {
        testHealth = DeviceHealth.builder()
                .id("health-1")
                .deviceId("dev-1")
                .healthScore(78)
                .batteryHealth(82)
                .build();
    }

    @Test
    @DisplayName("DIAGNOSIS source collects diagnosis report and health evidence")
    void testCollectDiagnosisEvidence() {
        DiagnosisReport report = DiagnosisReport.builder()
                .id("diag-1")
                .confidenceScore(85)
                .severity("HIGH")
                .build();
        when(diagnosisReportRepository.findById("diag-1")).thenReturn(Optional.of(report));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(testHealth));

        List<AiDecisionEvidence> evidence = evidenceTraceService
                .collectEvidence("DIAGNOSIS", "diag-1", "dev-1");

        assertThat(evidence).hasSizeGreaterThanOrEqualTo(3);
        assertThat(evidence.stream().anyMatch(e -> "DIAGNOSIS_SCORE".equals(e.getEvidenceType()))).isTrue();
        assertThat(evidence.stream().anyMatch(e -> "HEALTH_SCORE".equals(e.getEvidenceType()))).isTrue();
    }

    @Test
    @DisplayName("DIGITAL_TWIN source collects snapshot metrics")
    void testCollectDigitalTwinEvidence() {
        DigitalTwinSnapshot snapshot = DigitalTwinSnapshot.builder()
                .id("snap-1")
                .deviceId("dev-1")
                .healthScore(82)
                .failureRiskScore(28)
                .simulationConfidence(0.92)
                .overallEcosystemScore(84)
                .snapshotTime(LocalDateTime.now())
                .build();
        when(digitalTwinSnapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc("dev-1"))
                .thenReturn(Optional.of(snapshot));

        List<AiDecisionEvidence> evidence = evidenceTraceService
                .collectEvidence("DIGITAL_TWIN", null, "dev-1");

        assertThat(evidence).hasSize(4);
        assertThat(evidence.stream().anyMatch(e -> "HEALTH_SCORE".equals(e.getEvidenceType()))).isTrue();
        assertThat(evidence.stream().anyMatch(e -> "SIMULATION_CONFIDENCE".equals(e.getEvidenceType()))).isTrue();
    }

    @Test
    @DisplayName("PREDICTIVE source collects predictions and health")
    void testCollectPredictiveEvidence() {
        DevicePrediction prediction = DevicePrediction.builder()
                .id("pred-1")
                .deviceId("dev-1")
                .failureProbability(0.35)
                .riskLevel("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build();
        when(devicePredictionRepository.findByDeviceIdOrderByCreatedAtDesc("dev-1"))
                .thenReturn(List.of(prediction));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(testHealth));

        List<AiDecisionEvidence> evidence = evidenceTraceService
                .collectEvidence("PREDICTIVE", null, "dev-1");

        assertThat(evidence).hasSizeGreaterThanOrEqualTo(2);
        assertThat(evidence.stream().anyMatch(e -> "FAILURE_PROBABILITY".equals(e.getEvidenceType()))).isTrue();
    }

    @Test
    @DisplayName("Unknown source system returns empty list")
    void testUnknownSourceSystem() {
        List<AiDecisionEvidence> evidence = evidenceTraceService
                .collectEvidence("UNKNOWN_SYSTEM", null, "dev-1");

        assertThat(evidence).isEmpty();
    }

    @Test
    @DisplayName("AUTONOMOUS_AGENT source collects intervention details")
    void testCollectAutonomousAgentEvidence() {
        AutonomousIntervention intervention = AutonomousIntervention.builder()
                .id("int-1")
                .interventionType("PREVENTIVE_MAINTENANCE")
                .urgencyScore(85)
                .status("PENDING")
                .build();
        when(autonomousInterventionRepository.findById("int-1")).thenReturn(Optional.of(intervention));

        List<AiDecisionEvidence> evidence = evidenceTraceService
                .collectEvidence("AUTONOMOUS_AGENT", "int-1", "dev-1");

        assertThat(evidence).hasSize(3);
        assertThat(evidence.stream().anyMatch(e -> "URGENCY_SCORE".equals(e.getEvidenceType()))).isTrue();
    }
}
