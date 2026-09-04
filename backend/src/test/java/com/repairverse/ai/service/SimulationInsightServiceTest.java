package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.ForecastResponse;
import com.repairverse.ai.dto.DigitalTwinDto.ScenarioResponse;
import com.repairverse.ai.dto.DigitalTwinDto.SimulationInsight;
import com.repairverse.ai.entity.DigitalTwinOptimizationResult;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationInsightServiceTest {

    private SimulationInsightService insightService;

    @BeforeEach
    void setUp() {
        insightService = new SimulationInsightService();
    }

    @Test
    @DisplayName("Generate deterministic insights for high risk state")
    void testGenerateInsightsHighRisk() {
        DigitalTwinSnapshot snapshot = DigitalTwinSnapshot.builder()
                .id("snap-1")
                .healthScore(60)
                .failureRiskScore(65)
                .predictedRepairCost(4500.0)
                .predictedValue(50000.0)
                .build();

        DigitalTwinOptimizationResult opt = DigitalTwinOptimizationResult.builder()
                .recommendedStrategy("REPAIR_NOW")
                .optimizationScore(88)
                .estimatedSavings(3200.0)
                .estimatedLifespanGain(18)
                .estimatedCo2Savings(24.5)
                .build();

        ForecastResponse f24 = new ForecastResponse(
                "f-24", "snap-1", "dev-1", 24, 45, 75, 7200.0, 32000.0, 14, 25.0, 1.2, 0.85
        );

        List<SimulationInsight> insights = insightService.generateInsights(snapshot, List.of(), opt, List.of(f24));

        assertThat(insights).isNotEmpty();
        assertThat(insights.stream().anyMatch(i -> i.category().equals("RELIABILITY"))).isTrue();
        assertThat(insights.stream().anyMatch(i -> i.category().equals("FINANCIAL"))).isTrue();
        assertThat(insights.stream().anyMatch(i -> i.category().equals("LONGEVITY"))).isTrue();
        assertThat(insights.stream().anyMatch(i -> i.category().equals("SUSTAINABILITY"))).isTrue();
        assertThat(insights.stream().anyMatch(i -> i.category().equals("RISK"))).isTrue();
    }

    @Test
    @DisplayName("Handle null snapshot gracefully without throwing")
    void testGenerateInsightsNullSnapshot() {
        List<SimulationInsight> insights = insightService.generateInsights(null, List.of(), null, List.of());
        assertThat(insights).isEmpty();
    }
}
