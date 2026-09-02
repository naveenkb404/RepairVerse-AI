package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairMatchingDto.PlatformMarketplaceAnalytics;
import com.repairverse.ai.service.MarketplaceAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Phase 27 — Platform Marketplace Admin Analytics REST Controller.
 * Base path: /api/v1/admin/marketplace
 * Secured with ROLE_ADMIN.
 */
@RestController
@RequestMapping("/admin/marketplace")
@RequiredArgsConstructor
@Slf4j
public class MarketplaceAdminAnalyticsController {

    private final MarketplaceAnalyticsService analyticsService;

    /**
     * GET /api/v1/admin/marketplace/analytics
     * Platform-wide repair marketplace performance and trend analytics.
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPlatformAnalytics() {
        log.info("Admin marketplace platform analytics requested");
        PlatformMarketplaceAnalytics analytics = analyticsService.getPlatformAnalytics();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", analytics,
                "message", "Platform marketplace analytics retrieved successfully"
        ));
    }
}
