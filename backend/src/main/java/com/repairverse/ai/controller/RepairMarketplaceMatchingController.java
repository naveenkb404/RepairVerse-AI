package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.MarketplaceAnalyticsService;
import com.repairverse.ai.service.QuoteIntelligenceService;
import com.repairverse.ai.service.RepairMatchingService;
import com.repairverse.ai.service.SmartRepairRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 27 — Intelligent Repair Matching & Marketplace REST Controller.
 * Base path: /api/v1/marketplace
 */
@RestController
@RequestMapping("/marketplace")
@RequiredArgsConstructor
@Slf4j
public class RepairMarketplaceMatchingController {

    private final RepairMatchingService matchingService;
    private final SmartRepairRecommendationService recommendationService;
    private final QuoteIntelligenceService quoteIntelligenceService;
    private final MarketplaceAnalyticsService analyticsService;

    /**
     * GET /api/v1/marketplace/matches/device/{deviceId}
     * Returns ranked repair shop matches with deterministic 0-100 compatibility scores.
     */
    @GetMapping("/matches/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceMatches(
            @PathVariable("deviceId") String deviceId,
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lng", required = false) Double lng,
            @RequestParam(name = "diagnosisId", required = false) String diagnosisId,
            @RequestParam(name = "repairType", required = false) String repairType,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Smart repair matching requested for device '{}' by user '{}'", deviceId, userId);

        List<RepairShopMatchResponse> matches = matchingService.findMatchesForDevice(
                deviceId, userId, lat, lng, diagnosisId, repairType);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", matches,
                "count", matches.size()
        ));
    }

    /**
     * GET /api/v1/marketplace/recommendations/device/{deviceId}
     * Returns smart recommendation categories (Best Overall, Best Value, Fastest, Most Trusted, Most Sustainable, Nearest).
     */
    @GetMapping("/recommendations/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceRecommendations(
            @PathVariable("deviceId") String deviceId,
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lng", required = false) Double lng,
            @RequestParam(name = "diagnosisId", required = false) String diagnosisId,
            @RequestParam(name = "repairType", required = false) String repairType,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Smart recommendations requested for device '{}' by user '{}'", deviceId, userId);

        SmartRecommendationResponse recommendations = recommendationService.getRecommendationsForDevice(
                deviceId, userId, lat, lng, diagnosisId, repairType);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", recommendations
        ));
    }

    /**
     * POST /api/v1/marketplace/compare
     * Compare selected repair shops side-by-side with transparent metrics.
     */
    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareShops(
            @RequestBody CompareShopsRequest request,
            @RequestParam(name = "lat", required = false) Double lat,
            @RequestParam(name = "lng", required = false) Double lng,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Shop comparison requested by user '{}' for {} shops", userId, request.shopIds() != null ? request.shopIds().size() : 0);

        RepairMarketplaceComparison comparison = recommendationService.compareShops(
                request.shopIds(), request.deviceId(), userId, lat, lng);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comparison
        ));
    }

    /**
     * GET /api/v1/marketplace/quotes/{quoteId}/intelligence
     * Returns deterministic price intelligence and value analysis for a quote.
     */
    @GetMapping("/quotes/{quoteId}/intelligence")
    public ResponseEntity<Map<String, Object>> getQuoteIntelligence(
            @PathVariable("quoteId") String quoteId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Quote intelligence requested for quote '{}' by user '{}'", quoteId, userId);

        QuoteIntelligenceResponse intelligence = quoteIntelligenceService.evaluateQuoteIntelligence(quoteId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", intelligence
        ));
    }

    /**
     * GET /api/v1/marketplace/analytics
     * Returns authenticated user marketplace insights and potential savings.
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getUserMarketplaceAnalytics(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        UserMarketplaceInsights insights = analyticsService.getUserInsights(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", insights
        ));
    }

    /**
     * POST /api/v1/marketplace/interactions
     * Track user interactions for telemetry and intelligence.
     */
    @PostMapping("/interactions")
    public ResponseEntity<Map<String, Object>> trackInteraction(
            @RequestBody TrackInteractionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        analyticsService.trackInteraction(userId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Interaction tracked successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
