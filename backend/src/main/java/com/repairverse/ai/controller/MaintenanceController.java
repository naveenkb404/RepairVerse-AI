package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.MaintenanceCalendarService;
import com.repairverse.ai.service.MaintenancePriorityService;
import com.repairverse.ai.service.MaintenanceSchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 25 — Proactive Device Care & Smart Maintenance Automation REST Controller.
 * Base path: /api/v1/maintenance
 */
@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
@Slf4j
public class MaintenanceController {

    private final MaintenanceSchedulingService schedulingService;
    private final MaintenanceCalendarService calendarService;
    private final MaintenancePriorityService priorityService;

    /**
     * GET /api/v1/maintenance
     * Retrieve all maintenance schedules for the authenticated user (optional deviceId filter).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserMaintenanceSchedules(
            @RequestParam(name = "deviceId", required = false) String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<MaintenanceScheduleResponse> list = (deviceId != null && !deviceId.isBlank())
                ? schedulingService.getDeviceSchedules(deviceId, userId)
                : schedulingService.getUserSchedules(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "count", list.size()
        ));
    }

    /**
     * GET /api/v1/maintenance/device/{deviceId}
     * Retrieve all maintenance schedules for a specific device.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceMaintenance(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<MaintenanceScheduleResponse> list = schedulingService.getDeviceSchedules(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "count", list.size()
        ));
    }

    /**
     * POST /api/v1/maintenance/device/{deviceId}/generate
     * Generate or refresh deterministic proactive maintenance tasks for a device.
     */
    @PostMapping("/device/{deviceId}/generate")
    public ResponseEntity<Map<String, Object>> generateDeviceMaintenance(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Generating maintenance schedule for device='{}' by user='{}'", deviceId, userId);

        List<MaintenanceScheduleResponse> generated = schedulingService.generateSchedules(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", generated,
                "count", generated.size(),
                "message", "Deterministic maintenance schedule generated successfully"
        ));
    }

    /**
     * PUT /api/v1/maintenance/{id}/status
     * Update the status of a maintenance schedule (COMPLETED, SKIPPED, CANCELLED).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable("id") String id,
            @RequestBody UpdateMaintenanceStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Updating status for maintenance id='{}' to '{}' by user='{}'", id, request.status(), userId);

        MaintenanceScheduleResponse updated = schedulingService.updateStatus(id, userId, request.status());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updated,
                "message", "Maintenance status updated successfully"
        ));
    }

    /**
     * GET /api/v1/maintenance/calendar
     * Retrieve unified chronological calendar events across maintenance, bookings, and actions.
     */
    @GetMapping("/calendar")
    public ResponseEntity<Map<String, Object>> getCalendar(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<MaintenanceCalendarResponse> events = calendarService.getCalendarEvents(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", events,
                "count", events.size()
        ));
    }

    /**
     * GET /api/v1/maintenance/summary
     * Retrieve aggregated proactive maintenance statistics for the user.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        MaintenanceSummaryResponse summary = schedulingService.getSummary(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", summary
        ));
    }

    /**
     * GET /api/v1/maintenance/device/{deviceId}/priority
     * Retrieve deterministic maintenance priority assessment for a device.
     */
    @GetMapping("/device/{deviceId}/priority")
    public ResponseEntity<Map<String, Object>> getDevicePriority(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        MaintenancePriorityResponse priority = priorityService.getPriorityForDevice(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", priority
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
