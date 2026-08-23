package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairCostAnalyticsService;
import com.repairverse.ai.service.SustainabilityAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Analytics REST Controller — repair costs & sustainability impact.
 * Base path: /api/v1/analytics
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {

    private final RepairCostAnalyticsService repairCostAnalyticsService;
    private final SustainabilityAnalyticsService sustainabilityAnalyticsService;

    /**
     * GET /api/v1/analytics/repair-costs
     * Historical and projected repair cost breakdown for the authenticated user.
     */
    @GetMapping("/repair-costs")
    public ResponseEntity<Map<String, Object>> getRepairCostAnalytics(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = principal.getId();
        log.info("Repair cost analytics requested for user='{}'", userId);

        RepairCostAnalytics analytics = repairCostAnalyticsService.getAnalyticsForUser(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics,
                "message", "Repair cost analytics retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/analytics/sustainability
     * Environmental impact analytics for the authenticated user.
     */
    @GetMapping("/sustainability")
    public ResponseEntity<Map<String, Object>> getSustainabilityAnalytics(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = principal.getId();
        log.info("Sustainability analytics requested for user='{}'", userId);

        SustainabilityAnalytics analytics = sustainabilityAnalyticsService.getAnalyticsForUser(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics,
                "message", "Sustainability analytics retrieved successfully"
        ));
    }
}
