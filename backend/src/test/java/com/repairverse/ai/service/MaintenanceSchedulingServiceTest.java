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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceSchedulingServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DeviceHealthRepository deviceHealthRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private RepairHistoryRepository repairHistoryRepository;
    @Mock
    private MaintenanceScheduleRepository maintenanceRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private MaintenanceSchedulingService schedulingService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-101")
                .userId("usr-1")
                .deviceName("Dell XPS 15")
                .category("Laptop")
                .brand("Dell")
                .model("XPS 15 9520")
                .purchasePrice(1899.0)
                .createdAt(LocalDateTime.now().minusMonths(8))
                .build();
    }

    @Test
    @DisplayName("Generates quarterly inspection for healthy device (risk LOW, score 90)")
    void testHealthyDeviceGeneratesQuarterlyInspection() {
        when(deviceRepository.findByIdAndUserId("dev-101", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-101").healthScore(90).batteryHealth(95).lastService(LocalDate.now().minusMonths(1).toString()).build()));
        when(devicePredictionRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-101").riskLevel("LOW").predictionScore(90).build()));
        when(maintenanceRepository.existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(any(), any(), any(), any(), any()))
                .thenReturn(false);
        when(maintenanceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<MaintenanceScheduleResponse> schedules = schedulingService.generateSchedules("dev-101", "usr-1");

        assertThat(schedules).isNotEmpty();
        assertThat(schedules.get(0).maintenanceType()).isEqualTo("INSPECTION");
        assertThat(schedules.get(0).priority()).isEqualTo("LOW");
        assertThat(schedules.get(0).dueDate()).isAfterOrEqualTo(LocalDate.now().plusDays(80));
    }

    @Test
    @DisplayName("Generates immediate critical care for CRITICAL risk device")
    void testCriticalRiskDeviceGeneratesImmediateAction() {
        when(deviceRepository.findByIdAndUserId("dev-101", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-101").healthScore(25).batteryHealth(40).build()));
        when(devicePredictionRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-101").riskLevel("CRITICAL").predictionScore(25).build()));
        when(maintenanceRepository.existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(any(), any(), any(), any(), any()))
                .thenReturn(false);
        when(maintenanceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<MaintenanceScheduleResponse> schedules = schedulingService.generateSchedules("dev-101", "usr-1");

        assertThat(schedules).isNotEmpty();
        MaintenanceScheduleResponse critTask = schedules.stream()
                .filter(s -> "CRITICAL".equalsIgnoreCase(s.priority()))
                .findFirst().orElse(null);

        assertThat(critTask).isNotNull();
        assertThat(critTask.maintenanceType()).isEqualTo("PROFESSIONAL_SERVICE");
        assertThat(critTask.dueDate()).isBeforeOrEqualTo(LocalDate.now().plusDays(5));
        verify(notificationRepository, atLeastOnce()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Generates preventive maintenance for MEDIUM risk device")
    void testMediumRiskDeviceGeneratesPreventiveMaintenance() {
        when(deviceRepository.findByIdAndUserId("dev-101", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-101").healthScore(70).batteryHealth(80).build()));
        when(devicePredictionRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-101").riskLevel("MEDIUM").predictionScore(70).build()));
        when(maintenanceRepository.existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(any(), any(), any(), any(), any()))
                .thenReturn(false);
        when(maintenanceRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<MaintenanceScheduleResponse> schedules = schedulingService.generateSchedules("dev-101", "usr-1");

        assertThat(schedules).hasSizeGreaterThanOrEqualTo(2);
        boolean hasPreventive = schedules.stream().anyMatch(s -> "PREVENTIVE_REPAIR".equals(s.maintenanceType()));
        assertThat(hasPreventive).isTrue();
    }

    @Test
    @DisplayName("Deduplication prevents creating duplicate tasks when one exists in ±7 day window")
    void testDeduplicationPreventsDuplicateTasks() {
        when(deviceRepository.findByIdAndUserId("dev-101", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DeviceHealth.builder().deviceId("dev-101").healthScore(90).batteryHealth(95).build()));
        when(devicePredictionRepository.findByDeviceId("dev-101")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-101").riskLevel("LOW").predictionScore(90).build()));
        // Mock that existing duplicate is detected
        when(maintenanceRepository.existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(any(), any(), any(), any(), any()))
                .thenReturn(true);
        when(maintenanceRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        List<MaintenanceScheduleResponse> schedules = schedulingService.generateSchedules("dev-101", "usr-1");

        assertThat(schedules).isEmpty();
        verify(maintenanceRepository).saveAll(Collections.emptyList());
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when device does not belong to user")
    void testDeviceOwnershipValidation() {
        when(deviceRepository.findByIdAndUserId("dev-unowned", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> schedulingService.generateSchedules("dev-unowned", "usr-1"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Device not found or not owned by user");
    }

    @Test
    @DisplayName("Successfully transitions valid status: UPCOMING -> COMPLETED")
    void testValidStatusTransition() {
        MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                .id("ms-1")
                .userId("usr-1")
                .deviceId("dev-101")
                .deviceName("Dell XPS 15")
                .title("Inspection")
                .maintenanceType("INSPECTION")
                .dueDate(LocalDate.now().plusDays(10))
                .status("UPCOMING")
                .build();

        when(maintenanceRepository.findByIdAndUserId("ms-1", "usr-1")).thenReturn(Optional.of(schedule));
        when(maintenanceRepository.save(any(MaintenanceSchedule.class))).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceScheduleResponse response = schedulingService.updateStatus("ms-1", "usr-1", "COMPLETED");

        assertThat(response.status()).isEqualTo("COMPLETED");
        assertThat(schedule.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Rejects invalid status transition: COMPLETED -> UPCOMING")
    void testInvalidStatusTransitionThrowsException() {
        MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                .id("ms-1")
                .userId("usr-1")
                .deviceId("dev-101")
                .deviceName("Dell XPS 15")
                .title("Inspection")
                .maintenanceType("INSPECTION")
                .dueDate(LocalDate.now().minusDays(2))
                .status("COMPLETED")
                .build();

        when(maintenanceRepository.findByIdAndUserId("ms-1", "usr-1")).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> schedulingService.updateStatus("ms-1", "usr-1", "UPCOMING"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot transition from status 'COMPLETED'");
    }
}
