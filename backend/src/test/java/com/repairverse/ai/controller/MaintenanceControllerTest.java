package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.MaintenanceCalendarService;
import com.repairverse.ai.service.MaintenancePriorityService;
import com.repairverse.ai.service.MaintenanceSchedulingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MaintenanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class MaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaintenanceSchedulingService schedulingService;

    @MockBean
    private MaintenanceCalendarService calendarService;

    @MockBean
    private MaintenancePriorityService priorityService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private MaintenanceScheduleResponse mockSchedule() {
        return new MaintenanceScheduleResponse(
                "ms-101",
                "usr-1",
                "dev-123",
                "Apple iPhone 14 Pro",
                "Smartphone",
                "Battery Health Check",
                "Evaluate battery health and degradation curve",
                "BATTERY_CHECK",
                "MEDIUM",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "UPCOMING",
                25.0,
                30,
                1.5,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                false
        );
    }

    @Test
    @DisplayName("GET /api/v1/maintenance — returns user maintenance schedules")
    void testGetUserMaintenanceSchedules() throws Exception {
        when(schedulingService.getUserSchedules("usr-1")).thenReturn(List.of(mockSchedule()));

        mockMvc.perform(get("/maintenance")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].id").value("ms-101"))
                .andExpect(jsonPath("$.data[0].maintenanceType").value("BATTERY_CHECK"));
    }

    @Test
    @DisplayName("GET /api/v1/maintenance/device/{deviceId} — returns schedules for device")
    void testGetDeviceMaintenance() throws Exception {
        when(schedulingService.getDeviceSchedules("dev-123", "usr-1")).thenReturn(List.of(mockSchedule()));

        mockMvc.perform(get("/maintenance/device/dev-123")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].deviceId").value("dev-123"));
    }

    @Test
    @DisplayName("POST /api/v1/maintenance/device/{deviceId}/generate — generates maintenance schedule")
    void testGenerateDeviceMaintenance() throws Exception {
        when(schedulingService.generateSchedules("dev-123", "usr-1")).thenReturn(List.of(mockSchedule()));

        mockMvc.perform(post("/maintenance/device/dev-123/generate")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.message").value("Deterministic maintenance schedule generated successfully"));
    }

    @Test
    @DisplayName("PUT /api/v1/maintenance/{id}/status — successfully updates status")
    void testUpdateStatusSuccess() throws Exception {
        MaintenanceScheduleResponse completed = new MaintenanceScheduleResponse(
                "ms-101", "usr-1", "dev-123", "Apple iPhone 14 Pro", "Smartphone",
                "Battery Health Check", "desc", "BATTERY_CHECK", "MEDIUM",
                LocalDate.now(), LocalDate.now().plusDays(30), "COMPLETED",
                25.0, 30, 1.5, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), false
        );
        when(schedulingService.updateStatus("ms-101", "usr-1", "COMPLETED")).thenReturn(completed);

        mockMvc.perform(put("/maintenance/ms-101/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"COMPLETED\"}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/v1/maintenance/calendar — returns calendar events")
    void testGetCalendarEvents() throws Exception {
        MaintenanceCalendarResponse event = new MaintenanceCalendarResponse(
                "evt-1", "MAINTENANCE", "Quarterly Check", "desc",
                LocalDate.now().plusDays(10), "LOW", "dev-123", "iPhone 14", "/maintenance", "amber"
        );
        when(calendarService.getCalendarEvents("usr-1")).thenReturn(List.of(event));

        mockMvc.perform(get("/maintenance/calendar")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].eventType").value("MAINTENANCE"));
    }

    @Test
    @DisplayName("GET /api/v1/maintenance/summary — returns maintenance summary counts")
    void testGetSummary() throws Exception {
        MaintenanceSummaryResponse summary = new MaintenanceSummaryResponse(3, 1, 0, 0, 2, 120.0, 4.5, false);
        when(schedulingService.getSummary("usr-1")).thenReturn(summary);

        mockMvc.perform(get("/maintenance/summary")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUpcoming").value(3))
                .andExpect(jsonPath("$.data.totalDue").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/maintenance/device/{deviceId}/priority — returns priority assessment")
    void testGetDevicePriority() throws Exception {
        MaintenancePriorityResponse priority = new MaintenancePriorityResponse(
                "dev-123", "iPhone 14 Pro", 72, "HIGH",
                "High failure probability identified.",
                "Schedule a certified technician inspection within 7 days.",
                "Predictive risk model",
                LocalDateTime.now(), false
        );
        when(priorityService.getPriorityForDevice("dev-123", "usr-1")).thenReturn(priority);

        mockMvc.perform(get("/maintenance/device/dev-123/priority")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.priorityScore").value(72))
                .andExpect(jsonPath("$.data.priorityLevel").value("HIGH"));
    }

    @Test
    @DisplayName("Returns 404 when device is not found or not owned by user")
    void testUnownedDeviceReturns404() throws Exception {
        when(schedulingService.getDeviceSchedules("dev-unowned", "usr-1"))
                .thenThrow(new ResourceNotFoundException("Device not found or not owned by user: dev-unowned"));

        mockMvc.perform(get("/maintenance/device/dev-unowned")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isNotFound());
    }
}
