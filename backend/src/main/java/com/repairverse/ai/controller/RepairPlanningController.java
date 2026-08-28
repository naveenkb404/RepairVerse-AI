package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairPlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Autonomous Repair Planning REST Controller
 * Base path: /api/v1/repair-planning
 */
@RestController
@RequestMapping("/repair-planning")
@RequiredArgsConstructor
@Slf4j
public class RepairPlanningController {

    private final RepairPlanningService repairPlanningService;

    /**
     * GET /api/v1/repair-planning/device/{deviceId}
     * Retrieve or generate the deterministic action plan for a device.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceActionPlan(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Action plan requested for device='{}' by user='{}'", deviceId, userId);

        RepairActionPlanResponse plan = repairPlanningService.getOrCreateActionPlan(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", plan,
                "message", "Repair action plan retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/repair-planning
     * List all action plans for the current authenticated user.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserActionPlans(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<RepairActionPlanResponse> plans = repairPlanningService.getUserActionPlans(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", plans,
                "count", plans.size()
        ));
    }

    /**
     * POST /api/v1/repair-planning/device/{deviceId}/refresh
     * Explicitly recalculate the deterministic action plan.
     */
    @PostMapping("/device/{deviceId}/refresh")
    public ResponseEntity<Map<String, Object>> refreshActionPlan(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Refreshing action plan for device='{}' by user='{}'", deviceId, userId);

        RepairActionPlanResponse plan = repairPlanningService.refreshActionPlan(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", plan,
                "message", "Repair action plan refreshed successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
