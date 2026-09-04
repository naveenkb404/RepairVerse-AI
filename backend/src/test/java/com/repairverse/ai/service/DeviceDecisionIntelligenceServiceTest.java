package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceDecisionIntelligenceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private CircularImpactEventRepository circularImpactEventRepository;

    @Mock
    private DeviceDecisionSnapshotRepository snapshotRepository;

    @Mock
    private DeviceDecisionScenarioRepository scenarioRepository;

    @Spy
    private DeviceIntelligenceScoringService scoringService = new DeviceIntelligenceScoringService();

    @Spy
    private PersonalizedDeviceAdvisorService advisorService = new PersonalizedDeviceAdvisorService();

    @Mock
    private DeviceScenarioSimulationService scenarioSimulationService;

    @Mock
    private DeviceIntelligenceAlertService alertService;

    @InjectMocks
    private DeviceDecisionIntelligenceService decisionIntelligenceService;

    private Device sampleDevice;
    private DeviceHealth sampleHealth;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("ThinkPad X1 Carbon")
                .category("laptop")
                .brand("Lenovo")
                .model("Gen 10")
                .purchasePrice(1400.0)
                .purchaseDate("2023-01-15")
                .build();

        sampleHealth = DeviceHealth.builder()
                .id("dh-1")
                .deviceId("dev-1")
                .healthScore(85)
                .batteryHealth(88)
                .maintenanceDue("2026-12-01")
                .build();
    }

    @Test
    @DisplayName("Evaluate intelligence for healthy device yields CONTINUE_USING or MONITOR")
    void testEvaluateHealthyDevice() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(sampleHealth));
        when(diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc("dev-1")).thenReturn(List.of());
        when(repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc("dev-1")).thenReturn(List.of());
        when(circularImpactEventRepository.findByDeviceIdOrderByEventDateDesc("dev-1")).thenReturn(List.of());
        when(scenarioSimulationService.generateAndSaveScenarios(any(), any(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        DeviceIntelligenceResponse response = decisionIntelligenceService.evaluateDeviceIntelligence("dev-1", "usr-1", false);

        assertThat(response).isNotNull();
        assertThat(response.deviceId()).isEqualTo("dev-1");
        assertThat(response.intelligenceScore()).isGreaterThanOrEqualTo(70);
        assertThat(response.recommendedAction()).isIn("CONTINUE_USING", "MONITOR");
        assertThat(response.decisionFactors()).isNotEmpty();

        verify(snapshotRepository, times(1)).save(any(DeviceDecisionSnapshot.class));
    }

    @Test
    @DisplayName("Evaluate intelligence for critical failing device yields REPAIR_NOW or PROFESSIONAL_SERVICE")
    void testEvaluateCriticalDevice() {
        sampleHealth.setHealthScore(30);
        sampleHealth.setBatteryHealth(40);

        DiagnosisReport criticalReport = DiagnosisReport.builder()
                .id("diag-1")
                .deviceId("dev-1")
                .repairDifficulty("EXPERT")
                .repairCost(220.0)
                .confidenceScore(92)
                .safetyWarning("Severe overheating detected in battery subsystem")
                .build();

        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(sampleHealth));
        when(diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc("dev-1")).thenReturn(List.of(criticalReport));
        when(repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc("dev-1")).thenReturn(List.of());
        when(circularImpactEventRepository.findByDeviceIdOrderByEventDateDesc("dev-1")).thenReturn(List.of());
        when(scenarioSimulationService.generateAndSaveScenarios(any(), any(), anyInt(), anyInt(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        DeviceIntelligenceResponse response = decisionIntelligenceService.evaluateDeviceIntelligence("dev-1", "usr-1", false);

        assertThat(response).isNotNull();
        assertThat(response.recommendedAction()).isIn("PROFESSIONAL_SERVICE", "REPAIR_NOW", "REPLACE");
    }

    @Test
    @DisplayName("Evaluate device throws DeviceNotFoundException when device does not belong to user")
    void testDeviceNotFound() {
        when(deviceRepository.findByIdAndUserId("dev-999", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> decisionIntelligenceService.evaluateDeviceIntelligence("dev-999", "usr-1", false))
                .isInstanceOf(DeviceNotFoundException.class);
    }
}
