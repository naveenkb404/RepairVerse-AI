package com.repairverse.ai.service;

import com.repairverse.ai.entity.AutonomousActionPlan;
import com.repairverse.ai.entity.AutonomousActionStep;
import com.repairverse.ai.entity.AutonomousIntervention;
import com.repairverse.ai.repository.AutonomousActionPlanRepository;
import com.repairverse.ai.repository.AutonomousActionStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutonomousActionPlanningServiceTest {

    @Mock
    private AutonomousActionPlanRepository planRepository;

    @Mock
    private AutonomousActionStepRepository stepRepository;

    @InjectMocks
    private AutonomousActionPlanningService planningService;

    private AutonomousIntervention sampleIntervention;

    @BeforeEach
    void setUp() {
        sampleIntervention = AutonomousIntervention.builder()
                .id("int-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .interventionType("URGENT_REPAIR")
                .title("Urgent Component Repair for MacBook Pro")
                .description("Battery and display fault detected")
                .status("DETECTED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Generate and save plan produces 5 steps for URGENT_REPAIR")
    void testGenerateUrgentRepairPlan() {
        AutonomousActionPlan mockSavedPlan = AutonomousActionPlan.builder()
                .id("plan-1")
                .interventionId("int-1")
                .planName("Action Plan: Urgent Component Repair for MacBook Pro")
                .status("PLANNED")
                .build();

        when(planRepository.save(any(AutonomousActionPlan.class))).thenReturn(mockSavedPlan);
        when(stepRepository.save(any(AutonomousActionStep.class))).thenAnswer(inv -> inv.getArgument(0));

        AutonomousActionPlan plan = planningService.generateAndSavePlan(sampleIntervention);

        assertThat(plan).isNotNull();
        verify(planRepository, times(1)).deleteByInterventionId("int-1");
        verify(stepRepository, times(5)).save(any(AutonomousActionStep.class));
    }

    @Test
    @DisplayName("Generate plan for MAINTENANCE produces 3 steps")
    void testGenerateMaintenancePlan() {
        sampleIntervention.setInterventionType("MAINTENANCE");

        AutonomousActionPlan mockSavedPlan = AutonomousActionPlan.builder()
                .id("plan-2")
                .interventionId("int-1")
                .status("PLANNED")
                .build();

        when(planRepository.save(any(AutonomousActionPlan.class))).thenReturn(mockSavedPlan);
        when(stepRepository.save(any(AutonomousActionStep.class))).thenAnswer(inv -> inv.getArgument(0));

        AutonomousActionPlan plan = planningService.generateAndSavePlan(sampleIntervention);

        assertThat(plan).isNotNull();
        verify(stepRepository, times(3)).save(any(AutonomousActionStep.class));
    }
}
