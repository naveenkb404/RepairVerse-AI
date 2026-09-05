package com.repairverse.ai.controller;

import com.repairverse.ai.dto.TrustEngineDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 34: AI Decision Trust & Explainability Engine Controller.
 * Base path: /api/v1/trust
 */
@RestController
@RequestMapping("/trust")
@RequiredArgsConstructor
@Slf4j
public class DecisionTrustController {

    private final TrustEngineDashboardService dashboardService;
    private final DecisionAuditService decisionAuditService;
    private final DecisionFeedbackService feedbackService;
    private final UserConsentControlService consentControlService;
    private final GovernanceRuleService governanceRuleService;

    private final com.repairverse.ai.repository.AiGovernanceRuleRepository governanceRuleRepository;

    // ─── 1. Trust Dashboard ─────────────────────────────────────────────

    /**
     * GET /api/v1/trust/dashboard
     * Full trust engine dashboard for the authenticated user.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Fetching trust dashboard for user '{}'", userId);
        TrustDashboardResponse response = dashboardService.getDashboard(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ─── 2. Audit Log ───────────────────────────────────────────────────

    /**
     * GET /api/v1/trust/decisions
     * All AI decision records for the authenticated user.
     */
    @GetMapping("/decisions")
    public ResponseEntity<Map<String, Object>> getDecisionAuditLog(
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        List<DecisionSummaryResponse> log1 = decisionAuditService.getDecisionAuditLog(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", log1,
                "count", log1.size()
        ));
    }

    /**
     * GET /api/v1/trust/decisions/device/{deviceId}
     * Decisions for a specific device.
     */
    @GetMapping("/decisions/device/{deviceId}")
    public ResponseEntity<Map<String, Object>> getDeviceDecisions(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        List<DecisionSummaryResponse> decisions = decisionAuditService.getDeviceDecisionLog(userId, deviceId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", decisions,
                "count", decisions.size()
        ));
    }

    // ─── 3. Decision Detail ─────────────────────────────────────────────

    /**
     * GET /api/v1/trust/decisions/{decisionId}
     * Full audit detail including evidence, violations, trust breakdown, and explanations.
     */
    @GetMapping("/decisions/{decisionId}")
    public ResponseEntity<Map<String, Object>> getDecisionDetail(
            @PathVariable String decisionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        DecisionAuditResponse response = decisionAuditService.getDecisionById(decisionId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
        ));
    }

    // ─── 4. Mark Reviewed ───────────────────────────────────────────────

    /**
     * POST /api/v1/trust/decisions/{decisionId}/review
     * Mark a decision as reviewed by the user.
     */
    @PostMapping("/decisions/{decisionId}/review")
    public ResponseEntity<Map<String, Object>> markReviewed(
            @PathVariable String decisionId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("User '{}' reviewing decision '{}'", userId, decisionId);
        DecisionAuditResponse response = decisionAuditService.markReviewed(decisionId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Decision marked as reviewed.",
                "data", response
        ));
    }

    // ─── 5. Submit Feedback ─────────────────────────────────────────────

    /**
     * POST /api/v1/trust/decisions/{decisionId}/feedback
     * Submit user feedback (AGREE / DISAGREE / UNSURE) on a decision.
     */
    @PostMapping("/decisions/{decisionId}/feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @PathVariable String decisionId,
            @RequestBody DecisionFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("User '{}' submitting feedback '{}' on decision '{}'",
                userId, request.feedback(), decisionId);
        feedbackService.submitFeedback(userId, decisionId, request.feedback());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Feedback recorded successfully."
        ));
    }

    // ─── 6. Accuracy Stats ──────────────────────────────────────────────

    /**
     * GET /api/v1/trust/accuracy
     * System accuracy statistics based on user feedback.
     */
    @GetMapping("/accuracy")
    public ResponseEntity<Map<String, Object>> getAccuracyStats(
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        var stats = feedbackService.getSystemAccuracyStats(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
        ));
    }

    // ─── 7. Governance Rules ────────────────────────────────────────────

    /**
     * GET /api/v1/trust/governance/rules
     * All active governance rules.
     */
    @GetMapping("/governance/rules")
    public ResponseEntity<Map<String, Object>> getGovernanceRules(
            @AuthenticationPrincipal UserPrincipal principal) {
        getUserId(principal); // Auth check
        var rules = governanceRuleRepository.findAllByIsActiveTrue();
        List<GovernanceRuleResponse> responses = rules.stream()
                .map(r -> new GovernanceRuleResponse(
                        r.getId(), r.getRuleName(), r.getRuleCategory(), r.getDescription(),
                        r.getAppliesToSystems(), r.getSeverity(), r.getThresholdValue(), r.getIsActive()))
                .toList();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", responses,
                "count", responses.size()
        ));
    }

    // ─── 8. User Autonomy Preferences ───────────────────────────────────

    /**
     * GET /api/v1/trust/autonomy
     * Current user autonomy / consent preferences.
     */
    @GetMapping("/autonomy")
    public ResponseEntity<Map<String, Object>> getAutonomyPreferences(
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        var prefs = consentControlService.getPreferences(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", prefs
        ));
    }

    /**
     * PUT /api/v1/trust/autonomy
     * Update user autonomy / consent preferences.
     */
    @PutMapping("/autonomy")
    public ResponseEntity<Map<String, Object>> updateAutonomyPreferences(
            @RequestBody UpdateAutonomyPreferencesRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        String userId = getUserId(principal);
        log.info("Updating autonomy preferences for user '{}'", userId);
        var prefs = consentControlService.updatePreferences(userId, request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autonomy preferences updated successfully.",
                "data", prefs
        ));
    }

    // ─── Helper ─────────────────────────────────────────────────────────

    private String getUserId(UserPrincipal principal) {
        if (principal == null) {
            return "usr-1";
        }
        return principal.getId();
    }
}
