package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AutonomousRepairAgentDto.*;
import com.repairverse.ai.entity.AutonomousActionStep;
import com.repairverse.ai.entity.AutonomousIntervention;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AgentApprovalService;
import com.repairverse.ai.service.AgentExecutionService;
import com.repairverse.ai.service.AutonomousRepairAgentService;
import com.repairverse.ai.service.ProactiveInterventionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 31: Autonomous Repair Agent & Proactive Device Intervention System Controller.
 * Base path: /api/v1/repair-agent
 */
@RestController
@RequestMapping("/repair-agent")
@RequiredArgsConstructor
@Slf4j
public class AutonomousRepairAgentController {

    private final AutonomousRepairAgentService agentService;
    private final ProactiveInterventionService proactiveInterventionService;
    private final AgentApprovalService approvalService;
    private final AgentExecutionService executionService;

    /**
     * GET /api/v1/repair-agent/dashboard
     * Returns the comprehensive autonomous agent dashboard overview.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Autonomous repair agent dashboard requested by user '{}'", userId);

        AgentDashboardResponse dashboard = agentService.getAgentDashboard(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", dashboard
        ));
    }

    /**
     * POST /api/v1/repair-agent/devices/{deviceId}/evaluate
     * Proactively evaluates a device and generates intervention plans.
     */
    @PostMapping("/devices/{deviceId}/evaluate")
    public ResponseEntity<Map<String, Object>> evaluateDevice(
            @PathVariable String deviceId,
            @RequestBody(required = false) DeviceEvaluationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Agent evaluation triggered for device '{}' (user: '{}')", deviceId, userId);

        AutonomousIntervention intervention = proactiveInterventionService.evaluateDevice(deviceId, userId);
        InterventionResponse response = agentService.getInterventionDetails(intervention.getId(), userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Autonomous intervention evaluated successfully"
        ));
    }

    /**
     * GET /api/v1/repair-agent/devices/{deviceId}/interventions
     * Lists all proactive interventions for a device.
     */
    @GetMapping("/devices/{deviceId}/interventions")
    public ResponseEntity<Map<String, Object>> getDeviceInterventions(
            @PathVariable String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<InterventionResponse> interventions = agentService.getDeviceInterventions(deviceId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", interventions,
                "count", interventions.size()
        ));
    }

    /**
     * GET /api/v1/repair-agent/interventions/{interventionId}
     * Returns detailed intervention record with action plan.
     */
    @GetMapping("/interventions/{interventionId}")
    public ResponseEntity<Map<String, Object>> getInterventionDetails(
            @PathVariable String interventionId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        InterventionResponse intervention = agentService.getInterventionDetails(interventionId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", intervention
        ));
    }

    /**
     * GET /api/v1/repair-agent/interventions/{interventionId}/plan
     * Returns multi-step executable action plan.
     */
    @GetMapping("/interventions/{interventionId}/plan")
    public ResponseEntity<Map<String, Object>> getInterventionPlan(
            @PathVariable String interventionId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        ActionPlanResponse plan = agentService.getInterventionPlan(interventionId, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", plan
        ));
    }

    /**
     * POST /api/v1/repair-agent/actions/{actionId}/approve
     * Approves an action step.
     */
    @PostMapping("/actions/{actionId}/approve")
    public ResponseEntity<Map<String, Object>> approveAction(
            @PathVariable String actionId,
            @RequestBody(required = false) ActionApprovalRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        String notes = request != null ? request.notes() : null;

        AutonomousActionStep approvedStep = approvalService.approveAction(actionId, userId, notes);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", approvedStep,
                "message", "Action step approved successfully"
        ));
    }

    /**
     * POST /api/v1/repair-agent/actions/{actionId}/reject
     * Rejects an action step.
     */
    @PostMapping("/actions/{actionId}/reject")
    public ResponseEntity<Map<String, Object>> rejectAction(
            @PathVariable String actionId,
            @RequestBody(required = false) ActionApprovalRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        String notes = request != null ? request.notes() : null;

        AutonomousActionStep rejectedStep = approvalService.rejectAction(actionId, userId, notes);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", rejectedStep,
                "message", "Action step rejected"
        ));
    }

    /**
     * POST /api/v1/repair-agent/actions/{actionId}/execute
     * Executes an authorized action step.
     */
    @PostMapping("/actions/{actionId}/execute")
    public ResponseEntity<Map<String, Object>> executeAction(
            @PathVariable String actionId,
            @RequestBody(required = false) ActionExecutionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        Map<String, Object> params = request != null && request.parameters() != null ? request.parameters() : Map.of();

        ExecutionResultResponse result = executionService.executeAction(actionId, userId, params);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", result,
                "message", "Action step executed successfully"
        ));
    }

    /**
     * GET /api/v1/repair-agent/activity
     * Returns historical agent execution activity stream.
     */
    @GetMapping("/activity")
    public ResponseEntity<Map<String, Object>> getAgentActivity(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<ExecutionHistoryResponse> activity = agentService.getAgentActivity(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", activity,
                "count", activity.size()
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
