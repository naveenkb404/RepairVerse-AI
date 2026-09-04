package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.SustainabilityGoal;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.SustainabilityGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SustainabilityGoalServiceTest {

    @Mock
    private SustainabilityGoalRepository goalRepository;

    @Mock
    private CircularImpactEventRepository eventRepository;

    @InjectMocks
    private SustainabilityGoalService goalService;

    private SustainabilityGoal activeGoal;

    @BeforeEach
    void setUp() {
        activeGoal = SustainabilityGoal.builder()
            .id("sg-1")
            .userId("usr-1")
            .goalType("CARBON_REDUCTION")
            .targetValue(100.0)
            .currentValue(45.0)
            .startDate(LocalDateTime.now().minusMonths(1))
            .targetDate(LocalDateTime.now().plusMonths(5))
            .status("ACTIVE")
            .build();
    }

    @Test
    @DisplayName("Creates goal and correctly calculates initial progress")
    void testCreateGoal() {
        when(goalRepository.save(any(SustainabilityGoal.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.findByUserId("usr-1")).thenReturn(Collections.emptyList());

        CreateGoalRequest req = new CreateGoalRequest("EWASTE_PREVENTION", 10.0, LocalDateTime.now().plusMonths(6));

        SustainabilityGoalDto created = goalService.createGoal("usr-1", req);

        assertThat(created).isNotNull();
        assertThat(created.goalType()).isEqualTo("EWASTE_PREVENTION");
        assertThat(created.targetValue()).isEqualTo(10.0);
        assertThat(created.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("Updates goal and marks COMPLETED when current value exceeds target")
    void testUpdateGoalCompletion() {
        when(goalRepository.findByIdAndUserId("sg-1", "usr-1")).thenReturn(Optional.of(activeGoal));
        when(goalRepository.save(any(SustainabilityGoal.class))).thenAnswer(inv -> inv.getArgument(0));

        // Set target lower than current 45.0
        UpdateGoalRequest updateReq = new UpdateGoalRequest(40.0, null, null);

        SustainabilityGoalDto updated = goalService.updateGoal("usr-1", "sg-1", updateReq);

        assertThat(updated.status()).isEqualTo("COMPLETED");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Syncs goal progress from circular impact events")
    void testSyncGoalProgress() {
        CircularImpactEvent event1 = CircularImpactEvent.builder()
            .userId("usr-1")
            .eventType("REPAIR_COMPLETED")
            .carbonSavedKg(60.0)
            .build();
        CircularImpactEvent event2 = CircularImpactEvent.builder()
            .userId("usr-1")
            .eventType("MAINTENANCE_COMPLETED")
            .carbonSavedKg(45.0)
            .build();

        when(goalRepository.findByUserIdAndStatus("usr-1", "ACTIVE")).thenReturn(List.of(activeGoal));
        when(eventRepository.findByUserId("usr-1")).thenReturn(List.of(event1, event2));
        when(goalRepository.save(any(SustainabilityGoal.class))).thenAnswer(inv -> inv.getArgument(0));

        goalService.syncGoalProgress("usr-1");

        assertThat(activeGoal.getCurrentValue()).isEqualTo(105.0);
        assertThat(activeGoal.getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException when deleting non-existent or unowned goal")
    void testDeleteGoalNotFound() {
        when(goalRepository.findByIdAndUserId("sg-bad", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.deleteGoal("usr-1", "sg-bad"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Sustainability goal not found");
    }
}
