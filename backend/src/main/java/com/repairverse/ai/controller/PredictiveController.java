package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.FaultPatternService;
import com.repairverse.ai.service.MaintenanceRecommendationService;
import com.repairverse.ai.service.PredictiveScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Predictive Maintenance REST Controller
 * Base path: /api/v1/predictive
 */
@RestController
@RequestMapping("/predictive")
@RequiredArgsConstructor
@Slf4j
public class PredictiveController {

    private final PredictiveScoringService scoringService;
    private final MaintenanceRecommendationService recommendationService;
    private final FaultPatternService faultPatternService;

    /**
     * GET /api/v1/predictive/devices/{deviceId}
     * Evaluate predictive maintenance score for a specific device.
     * Persists result and issues notification if HIGH/CRITICAL (24h dedup).
     */
    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<Map<String, Object>> evaluateDevice(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Predictive evaluation requested for device='{}' by user='{}'", deviceId, userId);

        DevicePredictionResponse prediction = scoringService.evaluateDevice(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", prediction,
                "message", "Predictive analysis completed successfully"
        ));
    }

    /**
     * GET /api/v1/predictive/fleet
     * Get prediction overview for all of the authenticated user's devices.
     */
    @GetMapping("/fleet")
    public ResponseEntity<Map<String, Object>> getUserFleet(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Fleet prediction overview requested for user='{}'", userId);

        List<DevicePredictionResponse> fleet = scoringService.getUserFleet(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", fleet,
                "message", "Fleet prediction overview retrieved"
        ));
    }

    /**
     * GET /api/v1/predictive/recommendations
     * Get maintenance recommendations prioritised by risk for the user's fleet.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Maintenance recommendations requested for user='{}'", userId);

        List<MaintenanceRecommendation> recs = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", recs,
                "message", "Maintenance recommendations retrieved"
        ));
    }

    /**
     * GET /api/v1/predictive/fault-patterns
     * List active fault patterns, optionally filtered by device category.
     */
    @GetMapping("/fault-patterns")
    public ResponseEntity<Map<String, Object>> getFaultPatterns(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "brand", required = false) String brand) {

        List<FaultPatternDto> patterns;
        if (category != null && !category.isBlank() && brand != null && !brand.isBlank()) {
            patterns = faultPatternService.getPatternsForDevice(category, brand);
        } else if (category != null && !category.isBlank()) {
            patterns = faultPatternService.getPatternsByCategory(category);
        } else {
            patterns = faultPatternService.getActivePatterns();
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", patterns,
                "message", "Fault patterns retrieved"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) {
            return "usr-1";
        }
        return principal.getId();
    }
}
