package com.repairverse.ai.service;

import com.repairverse.ai.dto.AutonomousRepairAgentDto.*;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutonomousRepairAgentServiceTest {

    @Mock
    private AutonomousInterventionRepository interventionRepository;

    @Mock
    private AutonomousActionPlanRepository planRepository;

    @Mock
    private AutonomousActionStepRepository stepRepository;

    @Mock
    private AgentExecutionHistoryRepository executionHistoryRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ProactiveInterventionService proactiveInterventionService;

    @Mock
    private AgentApprovalService approvalService;

    @Mock
    private AgentExecutionService executionService;

    @InjectMocks
    private AutonomousRepairAgentService agentService;

    private Device sampleDevice;
    private AutonomousIntervention sampleIntervention;
    private AutonomousActionPlan samplePlan;
    private AutonomousActionStep sampleStep;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro 16")
                .category("laptop")
                .build();

        sampleIntervention = AutonomousIntervention.builder()
                .id("int-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .interventionType("URGENT_REPAIR")
                .priority("HIGH")
                .priorityScore(85)
                .status("PENDING_APPROVAL")
                .title("Urgent Component Repair")
                .description("Battery fault detected")
                .estimatedCost(140.0)
                .estimatedSavings(600.0)
                .estimatedCo2Impact(36.0)
                .requiresUserApproval(true)
                .createdAt(LocalDateTime.now())
                .build();

        samplePlan = AutonomousActionPlan.builder()
                .id("plan-1")
                .interventionId("int-1")
                .planName("Action Plan: Urgent Component Repair")
                .totalSteps(1)
                .completedSteps(0)
                .status("PLANNED")
                .createdAt(LocalDateTime.now())
                .build();

        sampleStep = AutonomousActionStep.builder()
                .id("step-1")
                .planId("plan-1")
                .stepOrder(1)
                .actionType("REQUEST_QUOTE")
                .title("Dispatch Formal Quote")
                .requiresApproval(true)
                .status("WAITING_APPROVAL")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Get agent dashboard aggregates interventions, pending approvals, and distributions")
    void testGetAgentDashboard() {
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(sampleDevice));
        when(interventionRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(sampleIntervention));
        when(planRepository.findByInterventionId("int-1")).thenReturn(Optional.of(samplePlan));
        when(stepRepository.findByPlanIdOrderByStepOrderAsc("plan-1")).thenReturn(List.of(sampleStep));
        when(executionHistoryRepository.findByUserIdOrderByExecutedAtDesc("usr-1")).thenReturn(List.of());

        AgentDashboardResponse dashboard = agentService.getAgentDashboard("usr-1");

        assertThat(dashboard).isNotNull();
        assertThat(dashboard.monitoredDevicesCount()).isEqualTo(1);
        assertThat(dashboard.activeInterventionsCount()).isEqualTo(1);
        assertThat(dashboard.pendingApprovalsCount()).isEqualTo(1);
        assertThat(dashboard.agentStatus()).isEqualTo("ATTENTION_REQUIRED");
        assertThat(dashboard.totalMoneySaved()).isEqualTo(600.0);
        assertThat(dashboard.totalCo2AvoidedKg()).isEqualTo(36.0);
        assertThat(dashboard.priorityDistribution().get("HIGH")).isEqualTo(1);
    }

    @Test
    @DisplayName("Get device interventions returns mapped responses")
    void testGetDeviceInterventions() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));
        when(interventionRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc("dev-1", "usr-1"))
                .thenReturn(List.of(sampleIntervention));
        when(planRepository.findByInterventionId("int-1")).thenReturn(Optional.of(samplePlan));
        when(stepRepository.findByPlanIdOrderByStepOrderAsc("plan-1")).thenReturn(List.of(sampleStep));

        List<InterventionResponse> responses = agentService.getDeviceInterventions("dev-1", "usr-1");

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).deviceName()).isEqualTo("MacBook Pro 16");
        assertThat(responses.get(0).interventionType()).isEqualTo("URGENT_REPAIR");
    }
}
