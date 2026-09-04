package com.repairverse.ai.service;

import com.repairverse.ai.dto.AutonomousRepairAgentDto.ExecutionResultResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentExecutionServiceTest {

    @Mock
    private AutonomousActionStepRepository stepRepository;

    @Mock
    private AutonomousActionPlanRepository planRepository;

    @Mock
    private AutonomousInterventionRepository interventionRepository;

    @Mock
    private AgentExecutionHistoryRepository executionHistoryRepository;

    @Mock
    private AgentApprovalService approvalService;

    @InjectMocks
    private AgentExecutionService executionService;

    private AutonomousActionStep sampleStep;
    private AutonomousActionPlan samplePlan;
    private AutonomousIntervention sampleIntervention;

    @BeforeEach
    void setUp() {
        sampleIntervention = AutonomousIntervention.builder()
                .id("int-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .title("MacBook Pro Repair")
                .interventionType("URGENT_REPAIR")
                .status("IN_PROGRESS")
                .createdAt(LocalDateTime.now())
                .build();

        samplePlan = AutonomousActionPlan.builder()
                .id("plan-1")
                .interventionId("int-1")
                .totalSteps(2)
                .completedSteps(0)
                .status("IN_PROGRESS")
                .build();

        sampleStep = AutonomousActionStep.builder()
                .id("step-1")
                .planId("plan-1")
                .stepOrder(1)
                .actionType("GENERATE_REPORT")
                .title("Compile Diagnostic Telemetry")
                .requiresApproval(false)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Execute safe unapproved action succeeds and marks step COMPLETED")
    void testExecuteSafeAction() {
        when(stepRepository.findById("step-1")).thenReturn(Optional.of(sampleStep));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(samplePlan));
        when(interventionRepository.findByIdAndUserId("int-1", "usr-1")).thenReturn(Optional.of(sampleIntervention));
        when(stepRepository.save(any(AutonomousActionStep.class))).thenAnswer(i -> i.getArgument(0));
        when(stepRepository.findByPlanIdOrderByStepOrderAsc("plan-1")).thenReturn(List.of(sampleStep));
        when(executionHistoryRepository.save(any(AgentExecutionHistory.class))).thenAnswer(i -> {
            AgentExecutionHistory h = i.getArgument(0);
            h.setId("hist-1");
            return h;
        });

        ExecutionResultResponse result = executionService.executeAction("step-1", "usr-1", Map.of());

        assertThat(result.status()).isEqualTo("COMPLETED");
        assertThat(result.message()).contains("diagnostic telemetry");
        verify(stepRepository, atLeastOnce()).save(sampleStep);
        verify(executionHistoryRepository, times(1)).save(any(AgentExecutionHistory.class));
    }

    @Test
    @DisplayName("Execute action that requires approval without approval throws IllegalArgumentException")
    void testExecuteUnapprovedActionFails() {
        sampleStep.setRequiresApproval(true);
        sampleStep.setStatus("WAITING_APPROVAL");

        when(stepRepository.findById("step-1")).thenReturn(Optional.of(sampleStep));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(samplePlan));
        when(interventionRepository.findByIdAndUserId("int-1", "usr-1")).thenReturn(Optional.of(sampleIntervention));

        assertThatThrownBy(() -> executionService.executeAction("step-1", "usr-1", Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requires user approval");
    }

    @Test
    @DisplayName("Idempotent execution returns COMPLETED immediately without re-executing")
    void testIdempotentExecution() {
        sampleStep.setStatus("COMPLETED");
        sampleStep.setCompletedAt(LocalDateTime.now());

        when(stepRepository.findById("step-1")).thenReturn(Optional.of(sampleStep));
        when(planRepository.findById("plan-1")).thenReturn(Optional.of(samplePlan));
        when(interventionRepository.findByIdAndUserId("int-1", "usr-1")).thenReturn(Optional.of(sampleIntervention));

        ExecutionResultResponse result = executionService.executeAction("step-1", "usr-1", Map.of());

        assertThat(result.status()).isEqualTo("COMPLETED");
        assertThat(result.message()).contains("already executed");
        verify(executionHistoryRepository, never()).save(any(AgentExecutionHistory.class));
    }
}
