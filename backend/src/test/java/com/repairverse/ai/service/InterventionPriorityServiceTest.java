package com.repairverse.ai.service;

import com.repairverse.ai.service.InterventionPriorityService.PriorityResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InterventionPriorityServiceTest {

    private InterventionPriorityService priorityService;

    @BeforeEach
    void setUp() {
        priorityService = new InterventionPriorityService();
    }

    @Test
    @DisplayName("Calculate priority with maximum input scores yields CRITICAL tier and 100 score")
    void testMaxScores() {
        PriorityResult result = priorityService.calculatePriority(100, 100, 100, 100, 100, 100);

        assertThat(result.priorityScore()).isEqualTo(100);
        assertThat(result.priorityTier()).isEqualTo("CRITICAL");
    }

    @Test
    @DisplayName("Calculate priority with zero scores yields LOW tier and 0 score")
    void testZeroScores() {
        PriorityResult result = priorityService.calculatePriority(0, 0, 0, 0, 0, 0);

        assertThat(result.priorityScore()).isEqualTo(0);
        assertThat(result.priorityTier()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("Calculate priority for typical urgent repair scenario")
    void testUrgentRepairPriority() {
        // FailureRisk: 85, UserImpact: 90, Urgency: 90, FinancialRisk: 60, RepairOpportunity: 80, Sustainability: 70
        PriorityResult result = priorityService.calculatePriority(85, 90, 90, 60, 80, 70);

        assertThat(result.priorityScore()).isGreaterThanOrEqualTo(80);
        assertThat(result.priorityTier()).isIn("HIGH", "CRITICAL");
    }

    @Test
    @DisplayName("Tier threshold mappings")
    void testTierThresholds() {
        assertThat(priorityService.determinePriorityTier(95)).isEqualTo("CRITICAL");
        assertThat(priorityService.determinePriorityTier(90)).isEqualTo("CRITICAL");
        assertThat(priorityService.determinePriorityTier(85)).isEqualTo("HIGH");
        assertThat(priorityService.determinePriorityTier(70)).isEqualTo("HIGH");
        assertThat(priorityService.determinePriorityTier(65)).isEqualTo("MEDIUM");
        assertThat(priorityService.determinePriorityTier(40)).isEqualTo("MEDIUM");
        assertThat(priorityService.determinePriorityTier(30)).isEqualTo("LOW");
        assertThat(priorityService.determinePriorityTier(0)).isEqualTo("LOW");
    }
}
