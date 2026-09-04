package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.CircularImpactScoreService;
import com.repairverse.ai.service.CircularImpactService;
import com.repairverse.ai.service.SustainabilityAchievementService;
import com.repairverse.ai.service.SustainabilityGoalService;
import com.repairverse.ai.service.SustainabilityOptimizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 29 — AI-Powered Circular Economy Intelligence & Personalized Sustainability Optimization REST Controller.
 * Base path: /api/v1/circular-economy
 */
@RestController
@RequestMapping("/circular-economy")
@RequiredArgsConstructor
@Slf4j
public class CircularEconomyController {

    private final CircularImpactService circularImpactService;
    private final CircularImpactScoreService scoreService;
    private final SustainabilityOptimizationService optimizationService;
    private final SustainabilityGoalService goalService;
    private final SustainabilityAchievementService achievementService;

    /**
     * GET /api/v1/circular-economy/dashboard
     * Aggregated personal circular economy dashboard.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Circular economy dashboard requested by user '{}'", userId);

        CircularImpactMetricsDto impactMetrics = circularImpactService.getUserImpactMetrics(userId);
        CircularImpactScoreDto impactScore = scoreService.calculateScore(userId);
        List<SustainabilityGoalDto> goals = goalService.getUserGoals(userId);
        long completedGoalsCount = goals.stream().filter(SustainabilityGoalDto::isCompleted).count();
        List<SustainabilityAchievementDto> achievements = achievementService.evaluateAchievements(userId);
        long unlockedAchievementsCount = achievements.stream().filter(SustainabilityAchievementDto::unlocked).count();
        List<SustainabilityRecommendationDto> nextActions = optimizationService.getRecommendations(userId, null);
        List<CircularImpactEventDto> recentEvents = circularImpactService.getUserTimeline(userId);

        CircularDashboardDto dashboard = new CircularDashboardDto(
            impactMetrics,
            impactScore,
            goals,
            completedGoalsCount,
            achievements,
            unlockedAchievementsCount,
            nextActions,
            recentEvents
        );

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", dashboard
        ));
    }

    /**
     * GET /api/v1/circular-economy/impact
     * User's aggregate circular impact metrics.
     */
    @GetMapping("/impact")
    public ResponseEntity<Map<String, Object>> getImpactMetrics(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        CircularImpactMetricsDto metrics = circularImpactService.getUserImpactMetrics(userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", metrics
        ));
    }

    /**
     * GET /api/v1/circular-economy/device/{deviceId}
     * Device-specific circular impact metrics with ownership verification.
     */
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceImpact(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        CircularImpactMetricsDto metrics = circularImpactService.getDeviceImpactMetrics(deviceId, userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", metrics
        ));
    }

    /**
     * GET /api/v1/circular-economy/score
     * Deterministic Circular Impact Score (0–100) and tier evaluation.
     */
    @GetMapping("/score")
    public ResponseEntity<Map<String, Object>> getScore(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        CircularImpactScoreDto score = scoreService.calculateScore(userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", score
        ));
    }

    /**
     * GET /api/v1/circular-economy/optimize
     * Personalized sustainability optimization recommendations.
     */
    @GetMapping("/optimize")
    public ResponseEntity<Map<String, Object>> getOptimizationRecommendations(
            @RequestParam(name = "deviceId", required = false) String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<SustainabilityRecommendationDto> recommendations = optimizationService.getRecommendations(userId, deviceId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", recommendations,
            "count", recommendations.size()
        ));
    }

    /**
     * GET /api/v1/circular-economy/goals
     * Retrieve user's sustainability goals.
     */
    @GetMapping("/goals")
    public ResponseEntity<Map<String, Object>> getGoals(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<SustainabilityGoalDto> goals = goalService.getUserGoals(userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", goals,
            "count", goals.size()
        ));
    }

    /**
     * POST /api/v1/circular-economy/goals
     * Create a new sustainability target goal.
     */
    @PostMapping("/goals")
    public ResponseEntity<Map<String, Object>> createGoal(
            @Valid @RequestBody CreateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        SustainabilityGoalDto created = goalService.createGoal(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "data", created,
            "message", "Sustainability goal created successfully"
        ));
    }

    /**
     * PUT /api/v1/circular-economy/goals/{id}
     * Update an existing sustainability goal.
     */
    @PutMapping("/goals/{id}")
    public ResponseEntity<Map<String, Object>> updateGoal(
            @PathVariable("id") String id,
            @RequestBody UpdateGoalRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        SustainabilityGoalDto updated = goalService.updateGoal(userId, id, request);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", updated,
            "message", "Sustainability goal updated successfully"
        ));
    }

    /**
     * DELETE /api/v1/circular-economy/goals/{id}
     * Remove a sustainability goal.
     */
    @DeleteMapping("/goals/{id}")
    public ResponseEntity<Map<String, Object>> deleteGoal(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        goalService.deleteGoal(userId, id);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Sustainability goal removed successfully"
        ));
    }

    /**
     * GET /api/v1/circular-economy/achievements
     * Retrieve unlocked and locked sustainability achievements.
     */
    @GetMapping("/achievements")
    public ResponseEntity<Map<String, Object>> getAchievements(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<SustainabilityAchievementDto> achievements = achievementService.evaluateAchievements(userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", achievements,
            "count", achievements.size()
        ));
    }

    /**
     * GET /api/v1/circular-economy/timeline
     * Chronological stream of circular impact milestones.
     */
    @GetMapping("/timeline")
    public ResponseEntity<Map<String, Object>> getTimeline(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<CircularImpactEventDto> timeline = circularImpactService.getUserTimeline(userId);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", timeline,
            "count", timeline.size()
        ));
    }

    /**
     * POST /api/v1/circular-economy/events
     * Record a new circular economy action event.
     */
    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> recordEvent(
            @Valid @RequestBody RecordImpactEventRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        CircularImpactEventDto recorded = circularImpactService.recordImpactEvent(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "success", true,
            "data", recorded,
            "message", "Circular impact event recorded successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
