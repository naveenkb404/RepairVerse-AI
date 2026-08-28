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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceLifecycleServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceHealthRepository deviceHealthRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private CarbonImpactRepository carbonImpactRepository;

    @InjectMocks
    private DeviceLifecycleService deviceLifecycleService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
            .id("dev-laptop-1")
            .userId("usr-1")
            .deviceName("MacBook Pro M1")
            .category("Laptop")
            .brand("Apple")
            .model("MacBook Pro 14")
            .purchaseDate("2022-01-15")
            .purchasePrice(1999.0)
            .createdAt(LocalDateTime.now().minusMonths(30))
            .build();
    }

    @Test
    @DisplayName("Generates deterministic lifecycle assessment with multi-scenario comparison")
    void testLifecycleAssessmentCalculation() {
        when(deviceRepository.findByIdAndUserId("dev-laptop-1", "usr-1")).thenReturn(Optional.of(testDevice));

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-laptop-1")
            .predictionScore(72)
            .riskLevel("MEDIUM")
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-laptop-1")).thenReturn(Optional.of(pred));
        when(carbonImpactRepository.findByUserId("usr-1")).thenReturn(Optional.of(
            CarbonImpact.builder().co2Saved(45.0).ewasteReduced(2.5).build()
        ));

        DeviceLifecycleAssessmentResponse res = deviceLifecycleService.getLifecycleAssessment("dev-laptop-1", "usr-1");

        assertThat(res).isNotNull();
        assertThat(res.deviceId()).isEqualTo("dev-laptop-1");
        assertThat(res.deviceCategory()).isEqualTo("Laptop");
        assertThat(res.scenarios()).hasSize(5);
        assertThat(res.expectedLifespanAfterRepairMonths()).isGreaterThan(res.predictedRemainingLifespanMonths());
        assertThat(res.repairabilityScore()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for non-owner user")
    void testUnauthorizedUser() {
        when(deviceRepository.findByIdAndUserId("dev-laptop-1", "other-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deviceLifecycleService.getLifecycleAssessment("dev-laptop-1", "other-user"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
