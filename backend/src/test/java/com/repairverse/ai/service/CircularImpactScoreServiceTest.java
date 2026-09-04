package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.repository.SustainabilityGoalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircularImpactScoreServiceTest {

    @Mock
    private CircularImpactService circularImpactService;

    @Mock
    private SustainabilityGoalRepository goalRepository;

    @InjectMocks
    private CircularImpactScoreService scoreService;

    @Test
    @DisplayName("Calculates score accurately and classifies as CIRCULAR_CHAMPION for top metrics")
    void testCircularChampionScore() {
        CircularImpactMetricsDto metrics = new CircularImpactMetricsDto(
            150.0, 5.0, 15000.0, 600, 6L, 4L, 2L, 1L, 13L
        );

        when(circularImpactService.getUserImpactMetrics("usr-champ")).thenReturn(metrics);
        when(goalRepository.countByUserIdAndStatus("usr-champ", "ACTIVE")).thenReturn(2L);
        when(goalRepository.countByUserIdAndStatus("usr-champ", "COMPLETED")).thenReturn(2L);

        CircularImpactScoreDto result = scoreService.calculateScore("usr-champ");

        assertThat(result.score()).isGreaterThanOrEqualTo(90);
        assertThat(result.tier()).isEqualTo("CIRCULAR_CHAMPION");
        assertThat(result.factorBreakdown()).isNotNull();
        assertThat(result.strengths()).isNotEmpty();
        assertThat(result.nextBestAction()).isNotBlank();
    }

    @Test
    @DisplayName("Calculates score correctly for beginner/starting tier")
    void testStartingTierScore() {
        CircularImpactMetricsDto metrics = new CircularImpactMetricsDto(
            5.0, 0.2, 500.0, 30, 0L, 1L, 0L, 0L, 1L
        );

        when(circularImpactService.getUserImpactMetrics("usr-start")).thenReturn(metrics);
        when(goalRepository.countByUserIdAndStatus("usr-start", "ACTIVE")).thenReturn(0L);
        when(goalRepository.countByUserIdAndStatus("usr-start", "COMPLETED")).thenReturn(0L);

        CircularImpactScoreDto result = scoreService.calculateScore("usr-start");

        assertThat(result.score()).isLessThan(40);
        assertThat(result.tier()).isEqualTo("STARTING");
        assertThat(result.improvementAreas()).isNotEmpty();
    }

    @Test
    @DisplayName("Scores are strictly bounded between 0 and 100")
    void testScoreBoundaries() {
        // Enormous numbers should cap at 100
        CircularImpactMetricsDto maxMetrics = new CircularImpactMetricsDto(
            99999.0, 9999.0, 999999.0, 99999, 500L, 500L, 500L, 500L, 2000L
        );

        CircularImpactScoreDto maxResult = scoreService.computeScoreFromMetrics(maxMetrics, 10, 10);
        assertThat(maxResult.score()).isEqualTo(100);
        assertThat(maxResult.tier()).isEqualTo("CIRCULAR_CHAMPION");

        // Zero numbers should produce 0
        CircularImpactMetricsDto zeroMetrics = new CircularImpactMetricsDto(
            0.0, 0.0, 0.0, 0, 0L, 0L, 0L, 0L, 0L
        );

        CircularImpactScoreDto zeroResult = scoreService.computeScoreFromMetrics(zeroMetrics, 0, 0);
        assertThat(zeroResult.score()).isZero();
        assertThat(zeroResult.tier()).isEqualTo("STARTING");
    }
}
