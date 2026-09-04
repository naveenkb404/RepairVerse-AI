package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DeviceIntelligenceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DeviceDecisionIntelligenceService;
import com.repairverse.ai.service.DeviceIntelligenceAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 30 — AI Repair Ecosystem Intelligence & Personalized Device Decision Engine REST Controller.
 * Base path: /api/v1/device-intelligence
 */
@RestController
@RequestMapping("/device-intelligence")
@RequiredArgsConstructor
@Slf4j
public class DeviceIntelligenceController {

    private final DeviceDecisionIntelligenceService intelligenceService;
    private final DeviceIntelligenceAlertService alertService;

    /**
     * GET /api/v1/device-intelligence/{deviceId}
     * Evaluates and returns the latest intelligence evaluation for a device.
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceIntelligence(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Device intelligence evaluation requested for device '{}' (user: '{}')", deviceId, userId);

        DeviceIntelligenceResponse response = intelligenceService.evaluateDeviceIntelligence(deviceId, userId, false);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * POST /api/v1/device-intelligence/{deviceId}/evaluate
     * Triggers deterministic re-evaluation and persists a snapshot.
     */
    @PostMapping("/{deviceId}/evaluate")
    public ResponseEntity<Map<String, Object>> triggerEvaluation(
            @PathVariable String deviceId,
            @RequestBody(required = false) DeviceIntelligenceEvaluationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        boolean force = request != null && Boolean.TRUE.equals(request.forceReevaluation());

        DeviceIntelligenceResponse response = intelligenceService.evaluateDeviceIntelligence(deviceId, userId, force);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Device intelligence evaluated successfully"
        ));
    }

    /**
     * GET /api/v1/device-intelligence/{deviceId}/history
     * Returns historical decision snapshots for a device.
     */
    @GetMapping("/{deviceId}/history")
    public ResponseEntity<Map<String, Object>> getDeviceHistory(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<DeviceDecisionSnapshotResponse> history = intelligenceService.getDeviceHistory(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", history,
                "count", history.size()
        ));
    }

    /**
     * GET /api/v1/device-intelligence/{deviceId}/scenarios
     * Returns generated decision scenarios for a device.
     */
    @GetMapping("/{deviceId}/scenarios")
    public ResponseEntity<Map<String, Object>> getDeviceScenarios(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<DeviceScenario> scenarios = intelligenceService.getDeviceScenarios(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", scenarios,
                "count", scenarios.size()
        ));
    }

    /**
     * POST /api/v1/device-intelligence/{deviceId}/simulate
     * Runs custom What-If scenario simulation.
     */
    @PostMapping("/{deviceId}/simulate")
    public ResponseEntity<Map<String, Object>> simulateScenario(
            @PathVariable String deviceId,
            @RequestBody(required = false) DeviceScenarioSimulationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<DeviceScenario> scenarios = intelligenceService.simulateScenario(deviceId, userId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", scenarios,
                "count", scenarios.size()
        ));
    }

    /**
     * GET /api/v1/device-intelligence/{deviceId}/timeline
     * Returns unified intelligence timeline for a device.
     */
    @GetMapping("/{deviceId}/timeline")
    public ResponseEntity<Map<String, Object>> getDeviceTimeline(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<DeviceIntelligenceTimelineItem> timeline = intelligenceService.getDeviceTimeline(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", timeline,
                "count", timeline.size()
        ));
    }

    /**
     * GET /api/v1/device-intelligence/alerts
     * Returns current user's intelligence alerts.
     */
    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getUserAlerts(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<DeviceIntelligenceAlertResponse> alerts = alertService.getUserAlerts(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", alerts,
                "count", alerts.size()
        ));
    }

    /**
     * PUT /api/v1/device-intelligence/alerts/{alertId}/read
     * Marks an alert as read.
     */
    @PutMapping("/alerts/{alertId}/read")
    public ResponseEntity<Map<String, Object>> markAlertAsRead(
            @PathVariable String alertId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        DeviceIntelligenceAlertResponse updated = alertService.markAlertAsRead(alertId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", updated,
                "message", "Alert marked as read"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
