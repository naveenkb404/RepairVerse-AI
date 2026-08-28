package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairDelayImpactServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @InjectMocks
    private RepairDelayImpactService delayImpactService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
            .id("dev-phone-1")
            .userId("usr-1")
            .deviceName("Samsung Galaxy S22")
            .category("Smartphone")
            .purchasePrice(799.0)
            .build();
    }

    @Test
    @DisplayName("Simulates 7-day, 30-day, and 90-day delay consequence projections")
    void testDelaySimulationProjections() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "usr-1")).thenReturn(Optional.of(testDevice));

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-phone-1")
            .predictionScore(50)
            .riskLevel("HIGH")
            .estimatedRepairCost(100.0)
            .primaryFaultType("OLED Sub-pixel Fractures")
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(pred));

        DelayImpactResponse response = delayImpactService.simulateDelayImpact("dev-phone-1", "usr-1");

        assertThat(response).isNotNull();
        assertThat(response.baselineRepairCost()).isEqualTo(100.0);
        assertThat(response.projections()).hasSize(3);

        DelayProjection proj7 = response.projections().get(0);
        DelayProjection proj30 = response.projections().get(1);
        DelayProjection proj90 = response.projections().get(2);

        assertThat(proj7.projectedCost()).isGreaterThan(response.baselineRepairCost());
        assertThat(proj30.projectedCost()).isGreaterThan(proj7.projectedCost());
        assertThat(proj90.projectedCost()).isGreaterThan(proj30.projectedCost());
        assertThat(proj90.secondaryDamageProbability()).isGreaterThan(proj7.secondaryDamageProbability());
    }

    @Test
    @DisplayName("Enforces tenant isolation on delay simulation")
    void testUnauthorizedDelaySimulation() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "intruder-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> delayImpactService.simulateDelayImpact("dev-phone-1", "intruder-user"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
