package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

public final class AutonomousRepairAgentDto {

    private AutonomousRepairAgentDto() {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AgentDashboardResponse(
            String agentStatus,
            int monitoredDevicesCount,
            int activeInterventionsCount,
            int pendingApprovalsCount,
            int completedExecutionsCount,
            double totalMoneySaved,
            double totalCo2AvoidedKg,
            List<InterventionResponse> activeInterventions,
            List<ActionStepResponse> pendingApprovals,
            List<ExecutionHistoryResponse> recentExecutions,
            Map<String, Integer> priorityDistribution
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InterventionResponse(
            String id,
            String userId,
            String deviceId,
            String deviceName,
            String deviceCategory,
            String interventionType,
            String priority,
            String status,
            String title,
            String description,
            String reason,
            Integer confidenceScore,
            Integer priorityScore,
            Double estimatedCost,
            Double estimatedSavings,
            Double estimatedCo2Impact,
            String recommendedAction,
            Boolean requiresUserApproval,
            ActionPlanResponse actionPlan,
            String createdAt,
            String resolvedAt
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ActionPlanResponse(
            String id,
            String interventionId,
            String planName,
            String objective,
            Integer totalSteps,
            Integer completedSteps,
            String status,
            List<ActionStepResponse> steps,
            String createdAt
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ActionStepResponse(
            String id,
            String planId,
            String interventionId,
            String deviceId,
            String deviceName,
            Integer stepOrder,
            String actionType,
            String title,
            String description,
            String status,
            Boolean requiresApproval,
            String actionMetadata,
            String scheduledFor,
            String completedAt
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ExecutionHistoryResponse(
            String id,
            String userId,
            String deviceId,
            String deviceName,
            String interventionId,
            String actionStepId,
            String actionType,
            String executionStatus,
            String resultSummary,
            String executedAt
    ) {}

    public record DeviceEvaluationRequest(
            Boolean force
    ) {}

    public record ActionApprovalRequest(
            Boolean approved,
            String notes
    ) {}

    public record ActionExecutionRequest(
            Map<String, Object> parameters
    ) {}

    public record ExecutionResultResponse(
            String actionId,
            String status,
            String message,
            String executionId,
            String executedAt
    ) {}
}
