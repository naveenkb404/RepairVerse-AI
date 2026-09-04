package com.repairverse.ai.service;

import com.repairverse.ai.dto.AutonomousRepairAgentDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutonomousRepairAgentService {

    private final AutonomousInterventionRepository interventionRepository;
    private final AutonomousActionPlanRepository planRepository;
    private final AutonomousActionStepRepository stepRepository;
    private final AgentExecutionHistoryRepository executionHistoryRepository;
    private final DeviceRepository deviceRepository;
    private final ProactiveInterventionService proactiveInterventionService;
    private final AgentApprovalService approvalService;
    private final AgentExecutionService executionService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transactional
    public AgentDashboardResponse getAgentDashboard(String userId) {
        log.info("Generating autonomous repair agent dashboard for user '{}'", userId);

        // Ensure user's devices are evaluated
        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (interventionRepository.findByUserIdOrderByCreatedAtDesc(userId).isEmpty() && !devices.isEmpty()) {
            proactiveInterventionService.evaluateUserDevices(userId);
        }

        List<AutonomousIntervention> interventions = interventionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<AutonomousIntervention> activeInterventions = interventions.stream()
                .filter(i -> List.of("DETECTED", "PENDING_APPROVAL", "APPROVED", "IN_PROGRESS").contains(i.getStatus()))
                .toList();

        Map<String, String> deviceNames = devices.stream()
                .collect(Collectors.toMap(Device::getId, Device::getDeviceName, (a, b) -> a));
        Map<String, String> deviceCategories = devices.stream()
                .collect(Collectors.toMap(Device::getId, d -> d.getCategory() != null ? d.getCategory() : "Other", (a, b) -> a));

        List<InterventionResponse> activeInterventionResponses = activeInterventions.stream()
                .map(i -> toInterventionResponse(i, deviceNames.getOrDefault(i.getDeviceId(), "Device"), deviceCategories.getOrDefault(i.getDeviceId(), "Other")))
                .toList();

        // Pending approvals: find all steps waiting for approval
        List<ActionStepResponse> pendingApprovals = new ArrayList<>();
        for (AutonomousIntervention inter : activeInterventions) {
            planRepository.findByInterventionId(inter.getId()).ifPresent(plan -> {
                List<AutonomousActionStep> steps = stepRepository.findByPlanIdOrderByStepOrderAsc(plan.getId());
                for (AutonomousActionStep s : steps) {
                    if (s.getRequiresApproval() && "WAITING_APPROVAL".equals(s.getStatus())) {
                        pendingApprovals.add(toStepResponse(
                                s,
                                inter.getId(),
                                inter.getDeviceId(),
                                deviceNames.getOrDefault(inter.getDeviceId(), "Device")
                        ));
                    }
                }
            });
        }

        // Recent executions
        List<AgentExecutionHistory> histories = executionHistoryRepository.findByUserIdOrderByExecutedAtDesc(userId);
        List<ExecutionHistoryResponse> recentExecutions = histories.stream()
                .limit(15)
                .map(h -> new ExecutionHistoryResponse(
                        h.getId(),
                        h.getUserId(),
                        h.getDeviceId(),
                        deviceNames.getOrDefault(h.getDeviceId(), "Device"),
                        h.getInterventionId(),
                        h.getActionStepId(),
                        h.getActionType(),
                        h.getExecutionStatus(),
                        h.getResultSummary(),
                        h.getExecutedAt() != null ? h.getExecutedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
                ))
                .toList();

        // Priority Distribution
        Map<String, Integer> priorityDistribution = new HashMap<>();
        priorityDistribution.put("CRITICAL", (int) activeInterventions.stream().filter(i -> "CRITICAL".equals(i.getPriority())).count());
        priorityDistribution.put("HIGH", (int) activeInterventions.stream().filter(i -> "HIGH".equals(i.getPriority())).count());
        priorityDistribution.put("MEDIUM", (int) activeInterventions.stream().filter(i -> "MEDIUM".equals(i.getPriority())).count());
        priorityDistribution.put("LOW", (int) activeInterventions.stream().filter(i -> "LOW".equals(i.getPriority())).count());

        double totalMoneySaved = activeInterventions.stream().mapToDouble(AutonomousIntervention::getEstimatedSavings).sum();
        double totalCo2Avoided = activeInterventions.stream().mapToDouble(AutonomousIntervention::getEstimatedCo2Impact).sum();

        String status = !pendingApprovals.isEmpty() ? "ATTENTION_REQUIRED" : (activeInterventions.isEmpty() ? "IDLE" : "ACTIVE");

        return new AgentDashboardResponse(
                status,
                devices.size(),
                activeInterventions.size(),
                pendingApprovals.size(),
                histories.size(),
                totalMoneySaved,
                totalCo2Avoided,
                activeInterventionResponses,
                pendingApprovals,
                recentExecutions,
                priorityDistribution
        );
    }

    @Transactional(readOnly = true)
    public List<InterventionResponse> getDeviceInterventions(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with ID: " + deviceId));

        List<AutonomousIntervention> interventions = interventionRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        return interventions.stream()
                .map(i -> toInterventionResponse(i, device.getDeviceName(), device.getCategory()))
                .toList();
    }

    @Transactional(readOnly = true)
    public InterventionResponse getInterventionDetails(String interventionId, String userId) {
        AutonomousIntervention intervention = interventionRepository.findByIdAndUserId(interventionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found with ID: " + interventionId));

        String deviceName = "Device";
        String category = "Other";
        if (intervention.getDeviceId() != null) {
            Optional<Device> device = deviceRepository.findById(intervention.getDeviceId());
            if (device.isPresent()) {
                deviceName = device.get().getDeviceName();
                category = device.get().getCategory();
            }
        }

        return toInterventionResponse(intervention, deviceName, category);
    }

    @Transactional(readOnly = true)
    public ActionPlanResponse getInterventionPlan(String interventionId, String userId) {
        interventionRepository.findByIdAndUserId(interventionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found with ID: " + interventionId));

        AutonomousActionPlan plan = planRepository.findByInterventionId(interventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action plan not found for intervention: " + interventionId));

        List<AutonomousActionStep> steps = stepRepository.findByPlanIdOrderByStepOrderAsc(plan.getId());

        List<ActionStepResponse> stepResponses = steps.stream()
                .map(s -> toStepResponse(s, interventionId, null, null))
                .toList();

        return new ActionPlanResponse(
                plan.getId(),
                plan.getInterventionId(),
                plan.getPlanName(),
                plan.getObjective(),
                plan.getTotalSteps(),
                plan.getCompletedSteps(),
                plan.getStatus(),
                stepResponses,
                plan.getCreatedAt() != null ? plan.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
        );
    }

    @Transactional(readOnly = true)
    public List<ExecutionHistoryResponse> getAgentActivity(String userId) {
        List<AgentExecutionHistory> histories = executionHistoryRepository.findByUserIdOrderByExecutedAtDesc(userId);
        Map<String, String> deviceNames = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .collect(Collectors.toMap(Device::getId, Device::getDeviceName, (a, b) -> a));

        return histories.stream()
                .map(h -> new ExecutionHistoryResponse(
                        h.getId(),
                        h.getUserId(),
                        h.getDeviceId(),
                        deviceNames.getOrDefault(h.getDeviceId(), "Device"),
                        h.getInterventionId(),
                        h.getActionStepId(),
                        h.getActionType(),
                        h.getExecutionStatus(),
                        h.getResultSummary(),
                        h.getExecutedAt() != null ? h.getExecutedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
                ))
                .toList();
    }

    private InterventionResponse toInterventionResponse(AutonomousIntervention i, String deviceName, String deviceCategory) {
        ActionPlanResponse planResponse = planRepository.findByInterventionId(i.getId())
                .map(plan -> {
                    List<AutonomousActionStep> steps = stepRepository.findByPlanIdOrderByStepOrderAsc(plan.getId());
                    List<ActionStepResponse> stepResponses = steps.stream()
                            .map(s -> toStepResponse(s, i.getId(), i.getDeviceId(), deviceName))
                            .toList();
                    return new ActionPlanResponse(
                            plan.getId(),
                            plan.getInterventionId(),
                            plan.getPlanName(),
                            plan.getObjective(),
                            plan.getTotalSteps(),
                            plan.getCompletedSteps(),
                            plan.getStatus(),
                            stepResponses,
                            plan.getCreatedAt() != null ? plan.getCreatedAt().format(ISO_FORMATTER) : null
                    );
                })
                .orElse(null);

        return new InterventionResponse(
                i.getId(),
                i.getUserId(),
                i.getDeviceId(),
                deviceName,
                deviceCategory,
                i.getInterventionType(),
                i.getPriority(),
                i.getStatus(),
                i.getTitle(),
                i.getDescription(),
                i.getReason(),
                i.getConfidenceScore(),
                i.getPriorityScore(),
                i.getEstimatedCost(),
                i.getEstimatedSavings(),
                i.getEstimatedCo2Impact(),
                i.getRecommendedAction(),
                i.getRequiresUserApproval(),
                planResponse,
                i.getCreatedAt() != null ? i.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER),
                i.getResolvedAt() != null ? i.getResolvedAt().format(ISO_FORMATTER) : null
        );
    }

    private ActionStepResponse toStepResponse(AutonomousActionStep s, String interventionId, String deviceId, String deviceName) {
        return new ActionStepResponse(
                s.getId(),
                s.getPlanId(),
                interventionId,
                deviceId,
                deviceName,
                s.getStepOrder(),
                s.getActionType(),
                s.getTitle(),
                s.getDescription(),
                s.getStatus(),
                s.getRequiresApproval(),
                s.getActionMetadata(),
                s.getScheduledFor() != null ? s.getScheduledFor().format(ISO_FORMATTER) : null,
                s.getCompletedAt() != null ? s.getCompletedAt().format(ISO_FORMATTER) : null
        );
    }
}
