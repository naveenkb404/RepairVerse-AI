package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CircularImpactServiceTest {

    @Mock
    private CircularImpactEventRepository eventRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ObjectProvider<SustainabilityGoalService> goalServiceProvider;

    @Mock
    private ObjectProvider<SustainabilityAchievementService> achievementServiceProvider;

    @Mock
    private SustainabilityGoalService goalService;

    @Mock
    private SustainabilityAchievementService achievementService;

    private CircularImpactService circularImpactService;

    private Device testDevice;
    private CircularImpactEvent testEvent1;
    private CircularImpactEvent testEvent2;

    @BeforeEach
    void setUp() {
        circularImpactService = new CircularImpactService(
            eventRepository, deviceRepository, goalServiceProvider, achievementServiceProvider
        );

        testDevice = Device.builder()
            .id("dev-1")
            .userId("usr-1")
            .deviceName("MacBook Pro")
            .category("Laptop")
            .brand("Apple")
            .model("M1 Pro")
            .build();

        testEvent1 = CircularImpactEvent.builder()
            .id("cie-1")
            .userId("usr-1")
            .deviceId("dev-1")
            .eventType("REPAIR_COMPLETED")
            .eventDate(LocalDateTime.now().minusDays(3))
            .carbonSavedKg(58.5)
            .ewastePreventedKg(2.1)
            .moneySaved(4500.0)
            .deviceLifeExtensionDays(365)
            .impactSource("AUTOMATED_REPAIR")
            .build();

        testEvent2 = CircularImpactEvent.builder()
            .id("cie-2")
            .userId("usr-1")
            .deviceId("dev-1")
            .eventType("MAINTENANCE_COMPLETED")
            .eventDate(LocalDateTime.now().minusDays(1))
            .carbonSavedKg(15.0)
            .ewastePreventedKg(0.24)
            .moneySaved(800.0)
            .deviceLifeExtensionDays(90)
            .impactSource("MAINTENANCE_SCHEDULE")
            .build();
    }

    @Test
    @DisplayName("Calculates aggregate impact metrics deterministically from user events")
    void testGetUserImpactMetrics() {
        when(eventRepository.findByUserId("usr-1")).thenReturn(List.of(testEvent1, testEvent2));

        CircularImpactMetricsDto metrics = circularImpactService.getUserImpactMetrics("usr-1");

        assertThat(metrics).isNotNull();
        assertThat(metrics.totalCarbonSavedKg()).isEqualTo(73.5);
        assertThat(metrics.totalEwastePreventedKg()).isEqualTo(2.34);
        assertThat(metrics.totalMoneySaved()).isEqualTo(5300.0);
        assertThat(metrics.totalLifeExtensionDays()).isEqualTo(455);
        assertThat(metrics.totalRepairs()).isEqualTo(1);
        assertThat(metrics.totalMaintenanceActions()).isEqualTo(1);
        assertThat(metrics.totalCircularActions()).isEqualTo(2);
    }

    @Test
    @DisplayName("Returns fallback metrics when user has zero recorded events")
    void testGetFallbackMetricsWhenEmpty() {
        when(eventRepository.findByUserId("usr-new")).thenReturn(Collections.emptyList());

        CircularImpactMetricsDto metrics = circularImpactService.getUserImpactMetrics("usr-new");

        assertThat(metrics).isNotNull();
        assertThat(metrics.totalCarbonSavedKg()).isGreaterThan(0);
        assertThat(metrics.totalCircularActions()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Records new impact event and triggers sync on goal and achievement services")
    void testRecordImpactEvent() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(eventRepository.save(any(CircularImpactEvent.class))).thenAnswer(inv -> inv.getArgument(0));
        when(goalServiceProvider.getIfAvailable()).thenReturn(goalService);
        when(achievementServiceProvider.getIfAvailable()).thenReturn(achievementService);

        RecordImpactEventRequest req = new RecordImpactEventRequest(
            "dev-1", "REPAIR_COMPLETED", 64.0, 2.1, 5000.0, 365, "USER_ACTION", "ref-100"
        );

        CircularImpactEventDto result = circularImpactService.recordImpactEvent("usr-1", req);

        assertThat(result).isNotNull();
        assertThat(result.eventType()).isEqualTo("REPAIR_COMPLETED");
        assertThat(result.carbonSavedKg()).isEqualTo(64.0);
        verify(goalService, times(1)).syncGoalProgress("usr-1");
        verify(achievementService, times(1)).evaluateAchievements("usr-1");
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when device does not belong to user")
    void testGetDeviceImpactMetricsUnownedDevice() {
        when(deviceRepository.findByIdAndUserId("dev-other", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> circularImpactService.getDeviceImpactMetrics("dev-other", "usr-1"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Device not found or not owned by user");
    }

    @Test
    @DisplayName("Returns device impact metrics for owned device")
    void testGetDeviceImpactMetricsSuccess() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(eventRepository.findByDeviceId("dev-1")).thenReturn(List.of(testEvent1));

        CircularImpactMetricsDto metrics = circularImpactService.getDeviceImpactMetrics("dev-1", "usr-1");

        assertThat(metrics).isNotNull();
        assertThat(metrics.totalCarbonSavedKg()).isEqualTo(58.5);
        assertThat(metrics.totalEwastePreventedKg()).isEqualTo(2.1);
        assertThat(metrics.totalLifeExtensionDays()).isEqualTo(365);
    }
}
