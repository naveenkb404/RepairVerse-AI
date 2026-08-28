package com.repairverse.ai.service;

import com.repairverse.ai.dto.MaintenanceDto.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenancePriorityServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceHealthRepository deviceHealthRepository;
    @Mock
    private DevicePredictionRepository predictionRepository;
    @Mock
    private MaintenanceScheduleRepository maintenanceRepository;

    @InjectMocks
    private MaintenancePriorityService priorityService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-phone-1")
                .userId("usr-1")
                .deviceName("Samsung Galaxy S23")
                .category("Smartphone")
                .createdAt(LocalDateTime.now().minusMonths(10))
                .build();
    }

    @Test
    @DisplayName("Calculates CRITICAL priority score (>=80) for high risk and low health")
    void testCriticalPriorityScoreCalculation() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-phone-1").healthScore(35).build()));
        when(predictionRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-phone-1").riskLevel("CRITICAL").predictionScore(35).daysToFailureEstimate(10).build()));
        when(maintenanceRepository.findByUserIdAndDeviceIdOrderByDueDateAsc("usr-1", "dev-phone-1"))
                .thenReturn(Collections.emptyList());

        MaintenancePriorityResponse res = priorityService.getPriorityForDevice("dev-phone-1", "usr-1");

        assertThat(res.priorityScore()).isGreaterThanOrEqualTo(80);
        assertThat(res.priorityLevel()).isEqualTo("CRITICAL");
        assertThat(res.recommendedAction()).contains("Book a professional repair");
    }

    @Test
    @DisplayName("Calculates LOW/MEDIUM priority for healthy device with no overdue tasks")
    void testHealthyDevicePriorityScore() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-phone-1").healthScore(92).build()));
        when(predictionRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-phone-1").riskLevel("LOW").predictionScore(92).daysToFailureEstimate(120).build()));
        when(maintenanceRepository.findByUserIdAndDeviceIdOrderByDueDateAsc("usr-1", "dev-phone-1"))
                .thenReturn(Collections.emptyList());

        MaintenancePriorityResponse res = priorityService.getPriorityForDevice("dev-phone-1", "usr-1");

        assertThat(res.priorityScore()).isLessThan(60);
        assertThat(res.priorityLevel()).isIn("LOW", "MEDIUM");
        assertThat(res.reason()).contains("acceptable condition");
    }

    @Test
    @DisplayName("Applies overdue penalty when overdue maintenance tasks exist")
    void testOverduePenaltyBoostsPriorityScore() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-phone-1").healthScore(65).build()));
        when(predictionRepository.findByDeviceId("dev-phone-1")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-phone-1").riskLevel("MEDIUM").predictionScore(65).daysToFailureEstimate(45).build()));

        MaintenanceSchedule overdue = MaintenanceSchedule.builder()
                .id("ms-overdue")
                .userId("usr-1")
                .deviceId("dev-phone-1")
                .title("Battery Check")
                .dueDate(LocalDate.now().minusDays(15))
                .status("OVERDUE")
                .build();
        when(maintenanceRepository.findByUserIdAndDeviceIdOrderByDueDateAsc("usr-1", "dev-phone-1"))
                .thenReturn(List.of(overdue));

        MaintenancePriorityResponse res = priorityService.getPriorityForDevice("dev-phone-1", "usr-1");

        // Base 50 + MEDIUM 10 + health 3 + overdue 5 + task bonus 3 = 71 (HIGH)
        assertThat(res.priorityScore()).isGreaterThanOrEqualTo(65);
        assertThat(res.reason()).contains("overdue");
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for unowned device")
    void testUnownedDeviceThrowsException() {
        when(deviceRepository.findByIdAndUserId("dev-phone-1", "usr-other")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> priorityService.getPriorityForDevice("dev-phone-1", "usr-other"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
