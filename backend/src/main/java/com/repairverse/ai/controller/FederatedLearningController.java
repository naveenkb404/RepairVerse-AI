package com.repairverse.ai.controller;

import com.repairverse.ai.dto.FederatedLearningDto.*;
import com.repairverse.ai.entity.IntelligenceModelVersion;
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
 * Phase 35: Federated Repair Intelligence & Continuous Learning Controller.
 * Base path: /api/v1/learning
 */
@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
@Slf4j
public class FederatedLearningController {

    private final FederatedLearningDashboardService dashboardService;
    private final ContinuousRepairLearningService continuousLearningService;
    private final LearningModelVersionService modelVersionService;
    private final LearningValidationService validationService;
    private final LearningDecisionIntelligenceService decisionIntelligenceService;
    private final LearningImpactService impactService;
    private final LearningFeedbackService feedbackService;

    // ─── 1. Dashboard ───────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Fetching federated learning dashboard for user '{}'", getUserId(principal));
        LearningDashboardResponse response = dashboardService.getDashboard();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ─── 2. Batches ─────────────────────────────────────────────────────

    @GetMapping("/batches")
    public ResponseEntity<Map<String, Object>> getBatches(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<LearningBatchResponse> batches = dashboardService.getBatches();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", batches,
                "count", batches.size()
        ));
    }

    // ─── 3. Signals ─────────────────────────────────────────────────────

    @GetMapping("/signals")
    public ResponseEntity<Map<String, Object>> getSignals(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<LearningSignalResponse> signals = dashboardService.getSignals();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", signals,
                "count", signals.size()
        ));
    }

    // ─── 4. Models ──────────────────────────────────────────────────────

    @GetMapping("/models")
    public ResponseEntity<Map<String, Object>> getModels(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<IntelligenceModelVersion> versions = modelVersionService.getAllModelVersions();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", versions,
                "count", versions.size()
        ));
    }

    @GetMapping("/models/{version}")
    public ResponseEntity<Map<String, Object>> getModelByVersion(
            @PathVariable String version,
            @AuthenticationPrincipal UserPrincipal principal) {
        IntelligenceModelVersion model = modelVersionService.findByVersion(version)
                .orElseThrow(() -> new IllegalArgumentException("Model version not found: " + version));
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", model
        ));
    }

    @GetMapping("/models/{version}/comparison")
    public ResponseEntity<Map<String, Object>> compareModel(
            @PathVariable String version,
            @AuthenticationPrincipal UserPrincipal principal) {
        LearningModelComparisonResponse comparison = dashboardService.compareModel(version);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comparison
        ));
    }

    // ─── 5. Privacy Audit ───────────────────────────────────────────────

    @GetMapping("/privacy/audit")
    public ResponseEntity<Map<String, Object>> getPrivacyAudits(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<PrivacyAuditResponse> audits = dashboardService.getPrivacyAudits();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", audits,
                "count", audits.size()
        ));
    }

    // ─── 6. Impact & Trends ─────────────────────────────────────────────

    @GetMapping("/impact")
    public ResponseEntity<Map<String, Object>> getImpactMetrics(
            @AuthenticationPrincipal UserPrincipal principal) {
        LearningImpactResponse impact = impactService.calculateImpactMetrics();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", impact
        ));
    }

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrends(
            @AuthenticationPrincipal UserPrincipal principal) {
        LearningTrendResponse trends = impactService.getLearningTrends();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", trends
        ));
    }

    // ─── 7. Device-level Privacy-Preserving Profile ─────────────────────

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceLearningProfile(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        DeviceLearningProfileResponse profile = decisionIntelligenceService.getDeviceLearningProfile(deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", profile
        ));
    }

    // ─── 8. Run Learning Cycle (Admin / Automated Trigger) ──────────────

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> triggerLearningCycle(
            @RequestBody(required = false) List<Map<String, Object>> rawOutcomes,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Triggering continuous learning run requested by '{}'", getUserId(principal));
        LearningRunResponse runResponse = continuousLearningService.runLearningCycle(rawOutcomes);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", runResponse
        ));
    }

    // ─── 9. Activate Model Version ──────────────────────────────────────

    @PostMapping("/activate/{version}")
    public ResponseEntity<Map<String, Object>> activateModelVersion(
            @PathVariable String version,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("Activating intelligence model version '{}' requested by '{}'", version, getUserId(principal));
        IntelligenceModelVersion activated = modelVersionService.activateVersion(version);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Model version '" + version + "' activated successfully.",
                "data", activated
        ));
    }

    // ─── 10. Learning Feedback ──────────────────────────────────────────

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> submitLearningFeedback(
            @RequestBody LearningFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.info("User '{}' submitting feedback for model '{}'", getUserId(principal), request.modelVersion());
        var feedback = feedbackService.recordFeedback(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Learning feedback recorded.",
                "data", feedback
        ));
    }

    // ─── Helper ─────────────────────────────────────────────────────────

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
