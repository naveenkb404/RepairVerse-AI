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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairPlanningServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceHealthRepository deviceHealthRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;
    @Mock
    private AIRecommendationRepository recommendationRepository;
    @Mock
    private RepairActionPlanRepository actionPlanRepository;
    @Mock
    private RepairActionStepRepository actionStepRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private RepairPlanningService repairPlanningService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
            .id("dev-123")
            .userId("usr-1")
            .deviceName("Apple iPhone 14 Pro")
            .category("Smartphone")
            .brand("Apple")
            .model("iPhone 14 Pro")
            .purchasePrice(999.0)
            .createdAt(LocalDateTime.now().minusMonths(12))
            .build();
    }

    @Test
    @DisplayName("Synthesizes MONITOR plan for healthy device (score 90)")
    void testHealthyDeviceGeneratesMonitorPlan() {
        when(deviceRepository.findByIdAndUserId("dev-123", "usr-1")).thenReturn(Optional.of(testDevice));
        when(actionPlanRepository.findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc("dev-123", "usr-1"))
            .thenReturn(Optional.empty());

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-123")
            .userId("usr-1")
            .predictionScore(92)
            .riskLevel("HEALTHY")
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-123")).thenReturn(Optional.of(pred));
        when(diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc("dev-123")).thenReturn(Optional.empty());
        when(actionPlanRepository.save(any(RepairActionPlan.class))).thenAnswer(i -> {
            RepairActionPlan p = i.getArgument(0);
            p.setId("plan-test-1");
            return p;
        });

        RepairActionPlanResponse response = repairPlanningService.getOrCreateActionPlan("dev-123", "usr-1");

        assertThat(response).isNotNull();
        assertThat(response.overallStrategy()).isEqualTo("MONITOR");
        assertThat(response.priorityLevel()).isEqualTo("LOW");
        assertThat(response.estimatedTotalCost()).isEqualTo(0.0);
        assertThat(response.steps()).isNotEmpty();
    }

    @Test
    @DisplayName("Synthesizes PREVENTIVE_MAINTENANCE plan for moderate risk (score 70)")
    void testModerateRiskGeneratesMaintenancePlan() {
        when(deviceRepository.findByIdAndUserId("dev-123", "usr-1")).thenReturn(Optional.of(testDevice));

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-123")
            .userId("usr-1")
            .predictionScore(70)
            .riskLevel("MEDIUM")
            .estimatedRepairCost(80.0)
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-123")).thenReturn(Optional.of(pred));
        when(diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc("dev-123")).thenReturn(Optional.empty());
        when(actionPlanRepository.save(any(RepairActionPlan.class))).thenAnswer(i -> {
            RepairActionPlan p = i.getArgument(0);
            p.setId("plan-test-2");
            return p;
        });

        RepairActionPlanResponse response = repairPlanningService.refreshActionPlan("dev-123", "usr-1");

        assertThat(response).isNotNull();
        assertThat(response.overallStrategy()).isEqualTo("PREVENTIVE_MAINTENANCE");
        assertThat(response.priorityLevel()).isEqualTo("MEDIUM");
        assertThat(response.estimatedLifecycleExtensionMonths()).isEqualTo(14);
    }

    @Test
    @DisplayName("Synthesizes REPAIR plan for severe component damage with favorable cost ratio")
    void testRepairPlanForHighRiskDevice() {
        when(deviceRepository.findByIdAndUserId("dev-123", "usr-1")).thenReturn(Optional.of(testDevice));

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-123")
            .userId("usr-1")
            .predictionScore(45)
            .riskLevel("HIGH")
            .estimatedRepairCost(120.0)
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-123")).thenReturn(Optional.of(pred));
        when(diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc("dev-123")).thenReturn(Optional.empty());
        when(actionPlanRepository.save(any(RepairActionPlan.class))).thenAnswer(i -> {
            RepairActionPlan p = i.getArgument(0);
            p.setId("plan-test-3");
            return p;
        });

        RepairActionPlanResponse response = repairPlanningService.refreshActionPlan("dev-123", "usr-1");

        assertThat(response).isNotNull();
        assertThat(response.overallStrategy()).isEqualTo("REPAIR");
        assertThat(response.priorityLevel()).isEqualTo("HIGH");
        assertThat(response.estimatedTotalCost()).isEqualTo(120.0);
    }

    @Test
    @DisplayName("Enforces cross-user ownership isolation and throws ResourceNotFoundException")
    void testOwnershipValidation() {
        when(deviceRepository.findByIdAndUserId("dev-123", "other-user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairPlanningService.getOrCreateActionPlan("dev-123", "other-user"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException if device does not exist")
    void testDeviceNotFound() {
        when(deviceRepository.findByIdAndUserId("unknown-dev", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairPlanningService.getOrCreateActionPlan("unknown-dev", "usr-1"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
