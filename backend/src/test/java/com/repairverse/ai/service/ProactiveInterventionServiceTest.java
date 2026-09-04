package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProactiveInterventionServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private AutonomousInterventionRepository interventionRepository;

    @Mock
    private DeviceDecisionIntelligenceService decisionIntelligenceService;

    @Mock
    private InterventionPriorityService priorityService;

    @Mock
    private AutonomousActionPlanningService actionPlanningService;

    @InjectMocks
    private ProactiveInterventionService proactiveInterventionService;

    private Device sampleDevice;
    private DeviceIntelligenceResponse sampleIntelResponse;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("ThinkPad X1")
                .category("laptop")
                .purchasePrice(1200.0)
                .build();

        sampleIntelResponse = new DeviceIntelligenceResponse(
                "dev-1", "ThinkPad X1", "laptop", "Lenovo", "X1",
                45, "AT_RISK", "REPAIR_NOW", 92,
                "Battery and keyboard failure detected", "2026-09-04T12:00:00",
                new IntelligenceScoreBreakdown(40, 75, 80, 50, 60, 70, 60),
                List.of(),
                new SmartDecision("REPAIR_NOW", "HIGH", "Replace Battery", "Battery degraded", 120.0, "Restores 8h runtime"),
                List.of(),
                List.of()
        );
    }

    @Test
    @DisplayName("Evaluate device creates intervention and generates action plan")
    void testEvaluateDeviceCreatesIntervention() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));
        when(decisionIntelligenceService.evaluateDeviceIntelligence("dev-1", "usr-1", false)).thenReturn(sampleIntelResponse);
        when(interventionRepository.findFirstByDeviceIdAndUserIdAndInterventionTypeAndStatusIn(eq("dev-1"), eq("usr-1"), anyString(), anyList()))
                .thenReturn(Optional.empty());
        when(priorityService.calculatePriority(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(new InterventionPriorityService.PriorityResult(88, "HIGH"));
        when(interventionRepository.save(any(AutonomousIntervention.class))).thenAnswer(i -> {
            AutonomousIntervention in = i.getArgument(0);
            in.setId("int-1");
            return in;
        });

        AutonomousIntervention intervention = proactiveInterventionService.evaluateDevice("dev-1", "usr-1");

        assertThat(intervention).isNotNull();
        assertThat(intervention.getInterventionType()).isEqualTo("URGENT_REPAIR");
        assertThat(intervention.getPriority()).isEqualTo("HIGH");
        assertThat(intervention.getPriorityScore()).isEqualTo(88);

        verify(interventionRepository, times(1)).save(any(AutonomousIntervention.class));
        verify(actionPlanningService, times(1)).generateAndSavePlan(any(AutonomousIntervention.class));
    }

    @Test
    @DisplayName("Prevent duplicate active intervention returns existing active record")
    void testPreventDuplicateIntervention() {
        AutonomousIntervention existing = AutonomousIntervention.builder()
                .id("int-existing")
                .deviceId("dev-1")
                .userId("usr-1")
                .interventionType("URGENT_REPAIR")
                .status("DETECTED")
                .build();

        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));
        when(decisionIntelligenceService.evaluateDeviceIntelligence("dev-1", "usr-1", false)).thenReturn(sampleIntelResponse);
        when(interventionRepository.findFirstByDeviceIdAndUserIdAndInterventionTypeAndStatusIn(eq("dev-1"), eq("usr-1"), eq("URGENT_REPAIR"), anyList()))
                .thenReturn(Optional.of(existing));

        AutonomousIntervention result = proactiveInterventionService.evaluateDevice("dev-1", "usr-1");

        assertThat(result.getId()).isEqualTo("int-existing");
        verify(interventionRepository, never()).save(any(AutonomousIntervention.class));
        verify(actionPlanningService, never()).generateAndSavePlan(any(AutonomousIntervention.class));
    }
}
