package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DeviceLifecycleService;
import com.repairverse.ai.service.RepairDelayImpactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Device Lifecycle & Delay Impact REST Controller
 * Base path: /api/v1/lifecycle
 */
@RestController
@RequestMapping("/lifecycle")
@RequiredArgsConstructor
@Slf4j
public class LifecycleController {

    private final DeviceLifecycleService lifecycleService;
    private final RepairDelayImpactService delayImpactService;

    /**
     * GET /api/v1/lifecycle/device/{deviceId}
     * Retrieve comprehensive lifecycle assessment and multi-scenario forecast.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getLifecycleAssessment(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Lifecycle assessment requested for device='{}' by user='{}'", deviceId, userId);

        DeviceLifecycleAssessmentResponse assessment = lifecycleService.getLifecycleAssessment(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", assessment,
                "message", "Lifecycle assessment retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/lifecycle/device/{deviceId}/delay-impact
     * Simulate 7-day, 30-day, and 90-day delay consequence projections.
     */
    @GetMapping("/device/{deviceId}/delay-impact")
    public ResponseEntity<Map<String, Object>> getDelayImpact(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Delay impact simulation requested for device='{}' by user='{}'", deviceId, userId);

        DelayImpactResponse delayImpact = delayImpactService.simulateDelayImpact(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", delayImpact,
                "message", "Delay impact simulation completed successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
