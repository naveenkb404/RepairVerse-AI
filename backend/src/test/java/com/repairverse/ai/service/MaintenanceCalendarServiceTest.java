package com.repairverse.ai.service;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.entity.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceCalendarServiceTest {

    @Mock
    private MaintenanceScheduleRepository maintenanceRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RepairActionPlanRepository actionPlanRepository;
    @Mock
    private DevicePredictionRepository predictionRepository;
    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private MaintenanceCalendarService calendarService;

    @Test
    @DisplayName("Aggregates all 4 event sources in chronological order")
    void testCalendarAggregationAllSources() {
        String userId = "usr-1";

        // Source 1: Maintenance
        MaintenanceSchedule schedule = MaintenanceSchedule.builder()
                .id("ms-1")
                .userId(userId)
                .deviceId("dev-1")
                .deviceName("MacBook Pro")
                .title("Battery Calibration")
                .dueDate(LocalDate.now().plusDays(5))
                .priority("MEDIUM")
                .status("UPCOMING")
                .build();
        when(maintenanceRepository.findByUserIdAndStatusInOrderByDueDateAsc(any(), any()))
                .thenReturn(List.of(schedule));

        // Source 2: Booking
        Booking booking = Booking.builder()
                .id("book-1")
                .userId(userId)
                .bookingDate(LocalDate.now().plusDays(2).toString())
                .bookingStatus("SCHEDULED")
                .build();
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(booking));

        // Source 3: Action plan
        RepairActionPlan plan = RepairActionPlan.builder()
                .id("plan-1")
                .userId(userId)
                .deviceId("dev-1")
                .overallStrategy("REPAIR")
                .priorityLevel("HIGH")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
        when(actionPlanRepository.findByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(List.of(plan));

        // Source 4: Lifecycle alert
        Device dev = Device.builder().id("dev-1").userId(userId).deviceName("MacBook Pro").build();
        when(deviceRepository.findByUserId(userId)).thenReturn(List.of(dev));
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(dev));
        when(predictionRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(
                DevicePrediction.builder().deviceId("dev-1").riskLevel("CRITICAL").build()));

        List<MaintenanceCalendarResponse> events = calendarService.getCalendarEvents(userId);

        assertThat(events).hasSize(4);
        // Lifecycle alert is today, Booking is +2 days, Schedule is +5 days, Action plan is +7 days (HIGH priority)
        assertThat(events.get(0).eventType()).isEqualTo("LIFECYCLE_ALERT");
        assertThat(events.get(1).eventType()).isEqualTo("BOOKING");
        assertThat(events.get(2).eventType()).isEqualTo("MAINTENANCE");
        assertThat(events.get(3).eventType()).isEqualTo("REPAIR_ACTION");
    }

    @Test
    @DisplayName("Returns empty list when user has no active maintenance, bookings, or alerts")
    void testCalendarEmptyWhenNoData() {
        String userId = "usr-empty";
        when(maintenanceRepository.findByUserIdAndStatusInOrderByDueDateAsc(any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());
        when(actionPlanRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());
        when(deviceRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<MaintenanceCalendarResponse> events = calendarService.getCalendarEvents(userId);

        assertThat(events).isEmpty();
    }
}
