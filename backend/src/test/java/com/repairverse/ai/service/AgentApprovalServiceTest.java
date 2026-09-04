package com.repairverse.ai.service;

import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentApprovalServiceTest {

    @Mock
    private AutonomousActionStepRepository stepRepository;

    @Mock
    private AutonomousActionPlanRepository planRepository;

    @Mock
    private AutonomousInterventionRepository interventionRepository;

    @Mock
    private AgentExecutionHistoryRepository executionHistoryRepository;

    @InjectMocks
    private AgentApprovalService approvalService;

    private AutonomousActionStep sampleStep;
    private AutonomousActionPlan samplePlan;
    private AutonomousIntervention sampleIntervention;

    @BeforeEach
    void setUp() {
        sampleIntervention = AutonomousIntervention.builder()
                .id("int-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .interventionType("URGENT_REPAIR")
                .status("PENDING_APPROVAL")
                .createdAt(LocalDateTime.now())
                .build();

        samplePlan = AutonomousActionPlan.builder()
                .id("plan-1")
                .interventionId("int-1")
                .status("PLANNED")
                .build();

        sampleStep = AutonomousActionStep.builder()
                .id("step-1")
                .planId("plan-1")
                .stepOrder(4)
                .actionType("REQUEST_QUOTE")
                .title("Dispatch Formal Quotation Request")
                .requiresApproval(true)
                .status("WAITING_APPROVAL")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Automation tier classification correctness")
    void testAutomationTiers() {
        assertThat(approvalService.getAutomationTier("GENERATE_REPORT"))
                .isEqualTo(AgentApprovalService.AutomationTier.AUTOMATIC);
        assertThat(approvalService.getAutomationTier("FIND_SHOPS"))
                .isEqualTo(AgentApprovalService.AutomationTier.AUTOMATIC);
        assertThat(approvalService.getAutomationTier("REQUEST_QUOTE"))
                .isEqualTo(AgentApprovalService.AutomationTier.REQUIRES_APPROVAL);
        assertThat(approvalService.getAutomationTier("BOOK_SERVICE"))
                .isEqualTo(AgentApprovalService.AutomationTier.ALWAYS_EXPLICIT_APPROVAL);
        assertThat(approvalService.getAutomationTier("DISPOSE_RECYCLE"))
                .isEqualTo(AgentApprovalService.AutomationTier.ALWAYS_EXPLICIT_APPROVAL);

        assertThat(approvalService.requiresApproval("GENERATE_REPORT")).isFalse();
        assertThat(approvalService.requiresApproval("BOOK_SERVICE")).isTrue();
    }

    @Test
    @DisplayName("Approve action updates step and intervention to APPROVED and records history")
    void testApproveAction() {
        when(stepRepository.findById("step-1")).thenReturn(Optional.of(sampleStep));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(samplePlan));
        when(interventionRepository.findByIdAndUserId("int-1", "usr-1")).thenReturn(Optional.of(sampleIntervention));
        when(stepRepository.save(any(AutonomousActionStep.class))).thenAnswer(i -> i.getArgument(0));

        AutonomousActionStep approved = approvalService.approveAction("step-1", "usr-1", "Approved via dashboard");

        assertThat(approved.getStatus()).isEqualTo("APPROVED");
        assertThat(sampleIntervention.getStatus()).isEqualTo("APPROVED");

        verify(stepRepository, times(1)).save(sampleStep);
        verify(interventionRepository, times(1)).save(sampleIntervention);
        verify(executionHistoryRepository, times(1)).save(any(AgentExecutionHistory.class));
    }

    @Test
    @DisplayName("Reject action updates step and intervention to REJECTED")
    void testRejectAction() {
        when(stepRepository.findById("step-1")).thenReturn(Optional.of(sampleStep));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(samplePlan));
        when(interventionRepository.findByIdAndUserId("int-1", "usr-1")).thenReturn(Optional.of(sampleIntervention));
        when(stepRepository.save(any(AutonomousActionStep.class))).thenAnswer(i -> i.getArgument(0));

        AutonomousActionStep rejected = approvalService.rejectAction("step-1", "usr-1", "User prefers DIY");

        assertThat(rejected.getStatus()).isEqualTo("REJECTED");
        assertThat(sampleIntervention.getStatus()).isEqualTo("REJECTED");
        assertThat(sampleIntervention.getResolvedAt()).isNotNull();

        verify(stepRepository, times(1)).save(sampleStep);
        verify(interventionRepository, times(1)).save(sampleIntervention);
        verify(executionHistoryRepository, times(1)).save(any(AgentExecutionHistory.class));
    }
}
