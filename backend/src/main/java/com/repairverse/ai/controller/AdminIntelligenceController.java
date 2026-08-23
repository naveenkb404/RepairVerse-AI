package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.service.AdminIntelligenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin Predictive Intelligence REST Controller
 * Base path: /api/v1/admin/intelligence
 * Secured at the route level — Spring Security enforces ADMIN role via SecurityConfig.
 */
@RestController
@RequestMapping("/admin/intelligence")
@RequiredArgsConstructor
@Slf4j
public class AdminIntelligenceController {

    private final AdminIntelligenceService adminIntelligenceService;

    /**
     * GET /api/v1/admin/intelligence/summary
     * Platform-wide AI maintenance intelligence summary for admins.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getIntelligenceSummary() {
        log.info("Admin intelligence summary requested");
        AdminIntelligenceSummary summary = adminIntelligenceService.getSummary();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", summary,
                "message", "Intelligence summary retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/admin/intelligence/fleet
     * Platform-wide fleet health overview with risk distribution.
     */
    @GetMapping("/fleet")
    public ResponseEntity<Map<String, Object>> getPlatformFleet() {
        log.info("Admin platform fleet overview requested");
        PredictiveFleetOverview overview = adminIntelligenceService.getPlatformFleetOverview();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", overview,
                "message", "Platform fleet overview retrieved successfully"
        ));
    }
}
