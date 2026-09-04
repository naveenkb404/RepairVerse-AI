package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.EcosystemSimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 33: AI Repair Ecosystem Digital Twin & Predictive Optimization Engine Controller.
 * Base path: /api/v1/digital-twin
 */
@RestController
@RequestMapping("/digital-twin")
@RequiredArgsConstructor
@Slf4j
public class DigitalTwinController {

    private final EcosystemSimulationService simulationService;

    /**
     * GET /api/v1/digital-twin/dashboard
     * User-level ecosystem aggregate metrics.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getEcosystemDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Fetching ecosystem dashboard for user '{}'", userId);
        EcosystemMetricsResponse response = simulationService.getUserEcosystemDashboard(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * GET /api/v1/digital-twin/{deviceId}
     * Full Digital Twin dashboard data for a device.
     */
    @GetMapping("/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDigitalTwin(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Fetching Digital Twin for device '{}', user '{}'", deviceId, userId);
        DigitalTwinDashboardResponse response = simulationService.getDigitalTwin(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * POST /api/v1/digital-twin/{deviceId}/refresh
     * Rebuild and refresh Digital Twin state and predictions.
     */
    @PostMapping("/{deviceId}/refresh")
    public ResponseEntity<Map<String, Object>> refreshDigitalTwin(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Refreshing Digital Twin for device '{}', user '{}'", deviceId, userId);
        DigitalTwinDashboardResponse response = simulationService.refreshDigitalTwin(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Digital Twin recalibrated and refreshed successfully.",
                "data", response
        ));
    }

    /**
     * GET /api/v1/digital-twin/{deviceId}/forecasts
     * Multi-horizon forecast projections (3M, 6M, 12M, 18M, 24M).
     */
    @GetMapping("/{deviceId}/forecasts")
    public ResponseEntity<Map<String, Object>> getForecasts(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        List<ForecastResponse> forecasts = simulationService.getForecasts(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", forecasts,
                "count", forecasts.size()
        ));
    }

    /**
     * GET /api/v1/digital-twin/{deviceId}/trajectory
     * Health, risk, cost, and residual value trajectory data points.
     */
    @GetMapping("/{deviceId}/trajectory")
    public ResponseEntity<Map<String, Object>> getTrajectory(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        DeviceTrajectoryResponse trajectory = simulationService.getTrajectory(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", trajectory
        ));
    }

    /**
     * GET /api/v1/digital-twin/{deviceId}/scenarios
     * Alternative future simulated strategies.
     */
    @GetMapping("/{deviceId}/scenarios")
    public ResponseEntity<Map<String, Object>> getScenarios(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        List<ScenarioResponse> scenarios = simulationService.getScenarios(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", scenarios,
                "count", scenarios.size()
        ));
    }

    /**
     * POST /api/v1/digital-twin/{deviceId}/simulate
     * Run custom parameterized simulation.
     */
    @PostMapping("/{deviceId}/simulate")
    public ResponseEntity<Map<String, Object>> runCustomSimulation(
            @PathVariable String deviceId,
            @RequestBody(required = false) RunSimulationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Running custom Digital Twin simulation for device '{}'", deviceId);
        DigitalTwinDashboardResponse response = simulationService.simulateCustomScenario(userId, deviceId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Custom simulation executed successfully.",
                "data", response
        ));
    }

    /**
     * POST /api/v1/digital-twin/{deviceId}/optimize
     * Calculate and return optimal repair strategy with trade-off scoring.
     */
    @PostMapping("/{deviceId}/optimize")
    public ResponseEntity<Map<String, Object>> optimizeStrategy(
            @PathVariable String deviceId,
            @RequestBody(required = false) OptimizationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Optimizing repair strategy for device '{}'", deviceId);
        OptimizationResponse response = simulationService.optimizeStrategy(userId, deviceId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Optimal strategy determined successfully.",
                "data", response
        ));
    }

    /**
     * GET /api/v1/digital-twin/{deviceId}/events
     * Future predicted lifecycle and intervention simulation events.
     */
    @GetMapping("/{deviceId}/events")
    public ResponseEntity<Map<String, Object>> getSimulationEvents(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        List<SimulationEventResponse> events = simulationService.getSimulationEvents(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", events,
                "count", events.size()
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) {
            return "usr-1";
        }
        return principal.getId();
    }
}
