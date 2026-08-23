package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AdminDto.*;
import com.repairverse.ai.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Platform Administration REST Controller
 * Base path: /api/v1/admin
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    /**
     * GET /api/v1/admin/users
     * List all platform users
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<AdminUserSummary> users = adminService.getAllUsers();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users,
                "message", "User registry retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/admin/analytics
     * Platform-wide analytics summary
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        AdminAnalyticsResponse analytics = adminService.getAnalytics();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics,
                "message", "Platform analytics retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/admin/reports
     * System audits & compliance reports
     */
    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        List<AdminReportSummary> reports = adminService.getReports();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", reports,
                "message", "System reports retrieved successfully"
        ));
    }

    /**
     * DELETE /api/v1/admin/users/{id}
     * Remove user account
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") String id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User account removed successfully"
        ));
    }
}
