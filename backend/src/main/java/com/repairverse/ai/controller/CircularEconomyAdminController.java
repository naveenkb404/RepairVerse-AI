package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.service.CircularEconomyAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Phase 29 — Admin Circular Economy Platform Analytics REST Controller.
 * Base path: /api/v1/admin/circular-economy
 */
@RestController
@RequestMapping("/admin/circular-economy")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class CircularEconomyAdminController {

    private final CircularEconomyAnalyticsService analyticsService;

    /**
     * GET /api/v1/admin/circular-economy/analytics
     * Complete platform circular economy intelligence analytics.
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getPlatformAnalytics() {
        log.info("Admin platform circular economy analytics requested");
        PlatformCircularAnalyticsDto analytics = analyticsService.getPlatformAnalytics();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", analytics
        ));
    }

    /**
     * GET /api/v1/admin/circular-economy/platform-impact
     * Summary platform-wide impact numbers.
     */
    @GetMapping("/platform-impact")
    public ResponseEntity<Map<String, Object>> getPlatformImpact() {
        PlatformCircularAnalyticsDto analytics = analyticsService.getPlatformAnalytics();
        Map<String, Object> impact = Map.of(
            "totalUsers", analytics.totalUsers(),
            "totalRepairs", analytics.totalRepairs(),
            "totalDevicesExtended", analytics.totalDevicesExtended(),
            "totalCarbonSavedKg", analytics.totalCarbonSavedKg(),
            "totalEwastePreventedKg", analytics.totalEwastePreventedKg(),
            "totalMoneySaved", analytics.totalMoneySaved(),
            "totalDevicesRecycled", analytics.totalDevicesRecycled(),
            "totalDevicesRefurbished", analytics.totalDevicesRefurbished()
        );

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", impact
        ));
    }

    /**
     * GET /api/v1/admin/circular-economy/trends
     * Monthly circular impact trend time-series.
     */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrends() {
        List<CircularTrendDto> trends = analyticsService.getMonthlyTrends();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", trends,
            "count", trends.size()
        ));
    }

    /**
     * GET /api/v1/admin/circular-economy/rankings
     * Category rankings and shop sustainability leaderboards.
     */
    @GetMapping("/rankings")
    public ResponseEntity<Map<String, Object>> getRankings() {
        List<CategoryRankingDto> categoryRankings = analyticsService.getCategoryRankings();
        List<ShopSustainabilityRankingDto> shopRankings = analyticsService.getTopSustainableShops();

        return ResponseEntity.ok(Map.of(
            "success", true,
            "categoryRankings", categoryRankings,
            "shopRankings", shopRankings
        ));
    }
}
