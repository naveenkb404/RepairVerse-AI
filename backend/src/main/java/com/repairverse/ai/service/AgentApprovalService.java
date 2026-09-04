package com.repairverse.ai.service;

import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentApprovalService {

    private final AutonomousActionStepRepository stepRepository;
    private final AutonomousActionPlanRepository planRepository;
    private final AutonomousInterventionRepository interventionRepository;
    private final AgentExecutionHistoryRepository executionHistoryRepository;

    public enum AutomationTier {
        AUTOMATIC,
        REQUIRES_APPROVAL,
        ALWAYS_EXPLICIT_APPROVAL
    }

    public AutomationTier getAutomationTier(String actionType) {
        return switch (actionType) {
            case "GENERATE_REPORT", "NOTIFY_USER", "FIND_SHOPS", "COMPARE_OPTIONS" -> AutomationTier.AUTOMATIC;
            case "SCHEDULE_MAINTENANCE", "REQUEST_QUOTE" -> AutomationTier.REQUIRES_APPROVAL;
            case "BOOK_SERVICE", "DISPOSE_RECYCLE", "CONFIRM_PAYMENT" -> AutomationTier.ALWAYS_EXPLICIT_APPROVAL;
            default -> AutomationTier.REQUIRES_APPROVAL;
        };
    }

    public boolean requiresApproval(String actionType) {
        return getAutomationTier(actionType) != AutomationTier.AUTOMATIC;
    }

    @Transactional
    public AutonomousActionStep approveAction(String actionStepId, String userId, String notes) {
        log.info("User '{}' approving autonomous action step '{}' (notes: '{}')", userId, actionStepId, notes);

        AutonomousActionStep step = stepRepository.findById(actionStepId)
                .orElseThrow(() -> new ResourceNotFoundException("Action step not found with ID: " + actionStepId));

        AutonomousActionPlan plan = planRepository.findById(step.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Action plan not found"));

        AutonomousIntervention intervention = interventionRepository.findByIdAndUserId(plan.getInterventionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found or unauthorized"));

        step.setStatus("APPROVED");
        step.setUpdatedAt(LocalDateTime.now());
        AutonomousActionStep savedStep = stepRepository.save(step);

        intervention.setStatus("APPROVED");
        intervention.setUpdatedAt(LocalDateTime.now());
        interventionRepository.save(intervention);

        // Record history
        AgentExecutionHistory history = AgentExecutionHistory.builder()
                .userId(userId)
                .deviceId(intervention.getDeviceId())
                .interventionId(intervention.getId())
                .actionStepId(step.getId())
                .actionType(step.getActionType())
                .executionStatus("APPROVED")
                .resultSummary("User approved action step: " + step.getTitle() + (notes != null ? " (" + notes + ")" : ""))
                .executedAt(LocalDateTime.now())
                .build();
        executionHistoryRepository.save(history);

        return savedStep;
    }

    @Transactional
    public AutonomousActionStep rejectAction(String actionStepId, String userId, String notes) {
        log.info("User '{}' rejecting autonomous action step '{}' (notes: '{}')", userId, actionStepId, notes);

        AutonomousActionStep step = stepRepository.findById(actionStepId)
                .orElseThrow(() -> new ResourceNotFoundException("Action step not found with ID: " + actionStepId));

        AutonomousActionPlan plan = planRepository.findById(step.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Action plan not found"));

        AutonomousIntervention intervention = interventionRepository.findByIdAndUserId(plan.getInterventionId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found or unauthorized"));

        step.setStatus("REJECTED");
        step.setUpdatedAt(LocalDateTime.now());
        AutonomousActionStep savedStep = stepRepository.save(step);

        intervention.setStatus("REJECTED");
        intervention.setUpdatedAt(LocalDateTime.now());
        intervention.setResolvedAt(LocalDateTime.now());
        interventionRepository.save(intervention);

        // Record history
        AgentExecutionHistory history = AgentExecutionHistory.builder()
                .userId(userId)
                .deviceId(intervention.getDeviceId())
                .interventionId(intervention.getId())
                .actionStepId(step.getId())
                .actionType(step.getActionType())
                .executionStatus("REJECTED")
                .resultSummary("User rejected action step: " + step.getTitle() + (notes != null ? " (" + notes + ")" : ""))
                .executedAt(LocalDateTime.now())
                .build();
        executionHistoryRepository.save(history);

        return savedStep;
    }
}
