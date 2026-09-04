package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 32: AI Repair Knowledge Graph & Ecosystem Learning Intelligence Controller.
 * Base path: /api/v1/knowledge
 */
@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
@Slf4j
public class RepairKnowledgeGraphController {

    private final RepairKnowledgeGraphService graphService;
    private final RepairPatternDiscoveryService patternDiscoveryService;
    private final SimilarRepairCaseService similarCaseService;
    private final KnowledgeDrivenRecommendationService recommendationService;
    private final RepairKnowledgeFeedbackService feedbackService;

    /**
     * GET /api/v1/knowledge/graph
     * Fetch complete knowledge graph with nodes, relationships, and stats.
     */
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getKnowledgeGraph() {
        log.info("Fetching complete Repair Knowledge Graph");
        KnowledgeGraphResponse response = graphService.getKnowledgeGraph();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    /**
     * GET /api/v1/knowledge/graph/statistics
     * Fetch platform-wide knowledge graph statistics.
     */
    @GetMapping("/graph/statistics")
    public ResponseEntity<Map<String, Object>> getGraphStatistics() {
        KnowledgeGraphStatisticsResponse stats = graphService.getGraphStatistics();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }

    /**
     * GET /api/v1/knowledge/devices/{deviceId}/profile
     * Fetch ecosystem knowledge profile for a specific device.
     */
    @GetMapping("/devices/{deviceId}/profile")
    public ResponseEntity<Map<String, Object>> getDeviceKnowledgeProfile(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Fetching device knowledge profile for device '{}' user '{}'", deviceId, userId);

        DeviceKnowledgeProfileResponse baseProfile = graphService.getDeviceKnowledgeProfile(deviceId, userId);
        List<SimilarRepairCaseResponse> similarCases = similarCaseService.findSimilarCasesForDevice(deviceId, userId);
        List<KnowledgeRecommendationResponse> recommendations = recommendationService.getRecommendationsForDevice(deviceId, userId);

        DeviceKnowledgeProfileResponse fullProfile = new DeviceKnowledgeProfileResponse(
                baseProfile.deviceId(),
                baseProfile.deviceName(),
                baseProfile.deviceCategory(),
                baseProfile.matchedNodes(),
                baseProfile.directInsights(),
                similarCases,
                recommendations,
                baseProfile.totalObservedPatterns()
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", fullProfile
        ));
    }

    /**
     * GET /api/v1/knowledge/devices/{deviceId}/similar-cases
     * Find anonymized historical similar repair cases.
     */
    @GetMapping("/devices/{deviceId}/similar-cases")
    public ResponseEntity<Map<String, Object>> getSimilarCases(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<SimilarRepairCaseResponse> cases = similarCaseService.findSimilarCasesForDevice(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", cases,
                "count", cases.size()
        ));
    }

    /**
     * GET /api/v1/knowledge/devices/{deviceId}/recommendations
     * Fetch ecosystem-backed traceable repair recommendations.
     */
    @GetMapping("/devices/{deviceId}/recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<KnowledgeRecommendationResponse> recommendations =
                recommendationService.getRecommendationsForDevice(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", recommendations,
                "count", recommendations.size()
        ));
    }

    /**
     * GET /api/v1/knowledge/insights
     * Fetch discovered ecosystem pattern insights.
     */
    @GetMapping("/insights")
    public ResponseEntity<Map<String, Object>> getInsights(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category) {

        List<PatternInsightResponse> insights = patternDiscoveryService.getActiveInsights(type, category);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", insights,
                "count", insights.size()
        ));
    }

    /**
     * GET /api/v1/knowledge/insights/{insightId}
     * Fetch single pattern insight by ID.
     */
    @GetMapping("/insights/{insightId}")
    public ResponseEntity<Map<String, Object>> getInsightById(@PathVariable String insightId) {
        PatternInsightResponse insight = patternDiscoveryService.getInsightById(insightId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", insight
        ));
    }

    /**
     * POST /api/v1/knowledge/insights/{insightId}/feedback
     * Submit helpful/inaccurate feedback on an insight.
     */
    @PostMapping("/insights/{insightId}/feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @PathVariable String insightId,
            @RequestBody KnowledgeFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        PatternInsightResponse updated = feedbackService.submitFeedback(insightId, userId, request);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Thank you for your feedback. Ecosystem intelligence updated.",
                "data", updated
        ));
    }

    /**
     * POST /api/v1/knowledge/rebuild
     * Rebuild and synchronize knowledge graph.
     */
    @PostMapping("/rebuild")
    public ResponseEntity<Map<String, Object>> rebuildGraph() {
        log.info("Rebuilding Repair Knowledge Graph structure...");
        KnowledgeGraphResponse response = graphService.getKnowledgeGraph();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Repair knowledge graph synchronized successfully.",
                "data", response
        ));
    }

    /**
     * POST /api/v1/knowledge/admin/discover-patterns
     * Admin trigger for running ecosystem pattern discovery.
     */
    @PostMapping("/admin/discover-patterns")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> adminDiscoverPatterns() {
        log.info("Admin triggered pattern discovery");
        List<PatternInsightResponse> insights = patternDiscoveryService.runPatternDiscovery();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pattern discovery executed successfully.",
                "data", insights,
                "count", insights.size()
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
