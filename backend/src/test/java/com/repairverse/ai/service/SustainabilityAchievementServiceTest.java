package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.SustainabilityAchievement;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.SustainabilityAchievementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SustainabilityAchievementServiceTest {

    @Mock
    private SustainabilityAchievementRepository achievementRepository;

    @Mock
    private CircularImpactEventRepository eventRepository;

    @Mock
    private CircularImpactScoreService circularImpactScoreService;

    @InjectMocks
    private SustainabilityAchievementService achievementService;

    @Test
    @DisplayName("Unlocks achievements when threshold criteria are met")
    void testEvaluateAchievementsUnlocksWhenCriteriaMet() {
        CircularImpactEvent event = CircularImpactEvent.builder()
            .userId("usr-1")
            .eventType("REPAIR_COMPLETED")
            .carbonSavedKg(120.0)
            .ewastePreventedKg(6.5)
            .deviceLifeExtensionDays(200)
            .build();

        CircularImpactScoreDto scoreDto = new CircularImpactScoreDto(
            92, "CIRCULAR_CHAMPION", null, List.of(), List.of(), "Action", LocalDateTime.now()
        );

        when(eventRepository.findByUserId("usr-1")).thenReturn(List.of(event));
        when(circularImpactScoreService.calculateScore("usr-1")).thenReturn(scoreDto);
        when(achievementRepository.existsByUserIdAndAchievementCode(eq("usr-1"), any())).thenReturn(false);
        when(achievementRepository.findByUserIdOrderByUnlockedAtDesc("usr-1")).thenReturn(Collections.emptyList());

        List<SustainabilityAchievementDto> result = achievementService.evaluateAchievements("usr-1");

        assertThat(result).isNotEmpty();
        // FIRST_REPAIR, EWASTE_SAVER, CARBON_CONSCIOUS, LIFE_EXTENDER, PLANET_PROTECTOR, CIRCULAR_CHAMPION should all be saved
        verify(achievementRepository, atLeast(6)).save(any(SustainabilityAchievement.class));
    }

    @Test
    @DisplayName("Prevents duplicate unlock if achievement already exists")
    void testPreventsDuplicateAchievementUnlock() {
        CircularImpactEvent event = CircularImpactEvent.builder()
            .userId("usr-1")
            .eventType("REPAIR_COMPLETED")
            .carbonSavedKg(30.0)
            .ewastePreventedKg(1.0)
            .deviceLifeExtensionDays(50)
            .build();

        CircularImpactScoreDto scoreDto = new CircularImpactScoreDto(
            50, "DEVELOPING", null, List.of(), List.of(), "Action", LocalDateTime.now()
        );

        when(eventRepository.findByUserId("usr-1")).thenReturn(List.of(event));
        when(circularImpactScoreService.calculateScore("usr-1")).thenReturn(scoreDto);
        // Already exists
        when(achievementRepository.existsByUserIdAndAchievementCode("usr-1", "FIRST_REPAIR")).thenReturn(true);
        when(achievementRepository.existsByUserIdAndAchievementCode("usr-1", "CARBON_CONSCIOUS")).thenReturn(true);
        when(achievementRepository.findByUserIdOrderByUnlockedAtDesc("usr-1")).thenReturn(Collections.emptyList());

        achievementService.evaluateAchievements("usr-1");

        // No new save should happen
        verify(achievementRepository, never()).save(any(SustainabilityAchievement.class));
    }
}
