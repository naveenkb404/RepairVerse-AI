package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DashboardDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Unified User Dashboard REST Controller
 * Base path: /api/v1/dashboard
 */
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/v1/dashboard
     * Aggregated statistics for user dashboard
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        DashboardStatsResponse stats = dashboardService.getDashboardStats(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats,
                "message", "Dashboard statistics loaded successfully"
        ));
    }

    /**
     * GET /api/v1/dashboard/activity
     * Chronological activity feed
     */
    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getActivityFeed(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        List<ActivityItemResponse> activity = dashboardService.getActivityFeed(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", activity,
                "message", "Activity feed loaded successfully"
        ));
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}
