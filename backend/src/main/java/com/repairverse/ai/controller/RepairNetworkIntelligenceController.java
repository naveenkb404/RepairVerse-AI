package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 28 — Repair Network Intelligence REST Controller.
 * Authenticated user endpoints for quality, trust, and leaderboard intelligence.
 */
@RestController
@RequestMapping("/api/v1/network-intelligence")
@RequiredArgsConstructor
public class RepairNetworkIntelligenceController {

    private final RepairQualityScoringService    qualityService;
    private final RepairTrustIntelligenceService trustService;
    private final RepairOutcomeAnalyticsService  analyticsService;
    private final RepairNetworkRankingService    rankingService;

    // ── Network Overview ──────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/overview
     * Platform-wide network health statistics.
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getNetworkOverview() {
        RepairNetworkOverviewResponse overview = analyticsService.getNetworkOverview();
        return ResponseEntity.ok(Map.of("success", true, "data", overview));
    }

    // ── Shop Intelligence ─────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/shop/{shopId}
     * Quality score, trust score, and detailed factor breakdown for a shop.
     */
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Map<String, Object>> getShopIntelligence(
        @PathVariable String shopId
    ) {
        RepairShopQualityResponse quality = qualityService.calculateShopQuality(shopId, "Shop " + shopId);
        TrustScoreResponse trust          = trustService.calculateTrustScore(shopId, "Shop " + shopId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of("quality", quality, "trust", trust)
        ));
    }

    // ── Shop Outcomes ─────────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/shop/{shopId}/outcomes
     */
    @GetMapping("/shop/{shopId}/outcomes")
    public ResponseEntity<Map<String, Object>> getShopOutcomes(
        @PathVariable String shopId,
        @RequestParam(required = false) String category
    ) {
        RepairOutcomeAnalyticsResponse outcomes = category != null
            ? analyticsService.getShopOutcomesByCategory(shopId, category)
            : analyticsService.getShopOutcomes(shopId);
        return ResponseEntity.ok(Map.of("success", true, "data", outcomes));
    }

    // ── Leaderboard ───────────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/leaderboard
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<Map<String, Object>> getLeaderboard(
        @RequestParam(defaultValue = "BEST_OVERALL") String rankingType,
        @RequestParam(defaultValue = "10") int limit
    ) {
        int cappedLimit = Math.max(1, Math.min(50, limit));
        List<NetworkLeaderboardResponse> leaderboard = rankingService.getLeaderboard(rankingType, cappedLimit);
        return ResponseEntity.ok(Map.of("success", true, "data", leaderboard));
    }

    // ── Category Analytics ────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategoryAnalytics() {
        List<CategoryQualityAnalyticsResponse> categories = analyticsService.getCategoryAnalytics();
        return ResponseEntity.ok(Map.of("success", true, "data", categories));
    }

    // ── Quality Trends ────────────────────────────────────────────────────────

    /**
     * GET /api/v1/network-intelligence/shop/{shopId}/trends
     */
    @GetMapping("/shop/{shopId}/trends")
    public ResponseEntity<Map<String, Object>> getShopTrends(
        @PathVariable String shopId
    ) {
        List<QualityTrendResponse> trends = analyticsService.getShopTrends(shopId);
        return ResponseEntity.ok(Map.of("success", true, "data", trends));
    }
}
