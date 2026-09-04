package com.repairverse.ai.service;

import com.repairverse.ai.service.DeviceIntelligenceScoringService.ScoringResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceIntelligenceScoringServiceTest {

    private DeviceIntelligenceScoringService scoringService;

    @BeforeEach
    void setUp() {
        scoringService = new DeviceIntelligenceScoringService();
    }

    @Test
    @DisplayName("Calculate score with perfect inputs yields EXCEPTIONAL tier and 100 score")
    void testPerfectScores() {
        ScoringResult result = scoringService.calculateScore(100, 100, 100, 100, 100, 100, 100);

        assertThat(result.overallScore()).isEqualTo(100);
        assertThat(result.tier()).isEqualTo("EXCEPTIONAL");
        assertThat(result.breakdown().healthReliabilityScore()).isEqualTo(100);
        assertThat(result.decisionFactors()).hasSize(7);
    }

    @Test
    @DisplayName("Calculate score with zero inputs yields CRITICAL tier and 0 score")
    void testZeroScores() {
        ScoringResult result = scoringService.calculateScore(0, 0, 0, 0, 0, 0, 0);

        assertThat(result.overallScore()).isEqualTo(0);
        assertThat(result.tier()).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("Calculate score with typical healthy device metrics")
    void testHealthyDevice() {
        // Health: 85, FailureRiskResilience: 80, Economics: 70, Maintenance: 90, Longevity: 75, Sustainability: 70, RepairHistory: 90
        ScoringResult result = scoringService.calculateScore(85, 80, 70, 90, 75, 70, 90);

        assertThat(result.overallScore()).isBetween(75, 89);
        assertThat(result.tier()).isEqualTo("HEALTHY");
    }

    @Test
    @DisplayName("Score clamping handles negative or out-of-bound inputs safely")
    void testClamping() {
        ScoringResult result = scoringService.calculateScore(-50, 150, -20, 200, -10, 120, -5);

        assertThat(result.overallScore()).isBetween(0, 100);
        assertThat(result.breakdown().healthReliabilityScore()).isEqualTo(0);
        assertThat(result.breakdown().failureRiskScore()).isEqualTo(100);
    }

    @Test
    @DisplayName("Tier thresholds mapping correctness")
    void testTierThresholds() {
        assertThat(scoringService.determineTier(95)).isEqualTo("EXCEPTIONAL");
        assertThat(scoringService.determineTier(90)).isEqualTo("EXCEPTIONAL");
        assertThat(scoringService.determineTier(85)).isEqualTo("HEALTHY");
        assertThat(scoringService.determineTier(75)).isEqualTo("HEALTHY");
        assertThat(scoringService.determineTier(65)).isEqualTo("STABLE");
        assertThat(scoringService.determineTier(60)).isEqualTo("STABLE");
        assertThat(scoringService.determineTier(45)).isEqualTo("AT_RISK");
        assertThat(scoringService.determineTier(40)).isEqualTo("AT_RISK");
        assertThat(scoringService.determineTier(30)).isEqualTo("CRITICAL");
        assertThat(scoringService.determineTier(0)).isEqualTo("CRITICAL");
    }
}
