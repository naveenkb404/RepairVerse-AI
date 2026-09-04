package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.OptimizationRequest;
import com.repairverse.ai.dto.DigitalTwinDto.OptimizationResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinOptimizationResult;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DigitalTwinOptimizationResultRepository;
import com.repairverse.ai.repository.DigitalTwinScenarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairStrategyOptimizationServiceTest {

    @Mock
    private DigitalTwinOptimizationResultRepository optimizationRepository;

    @Mock
    private DigitalTwinScenarioRepository scenarioRepository;

    @InjectMocks
    private RepairStrategyOptimizationService optimizationService;

    private Device testDevice;
    private DigitalTwinSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro M1")
                .category("LAPTOP")
                .purchasePrice(80000.0)
                .build();

        testSnapshot = DigitalTwinSnapshot.builder()
                .id("snap-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .healthScore(80)
                .failureRiskScore(30)
                .maintenanceScore(75)
                .repairEconomicsScore(85)
                .longevityScore(80)
                .sustainabilityScore(90)
                .predictedValue(70000.0)
                .predictedRepairCost(3500.0)
                .simulationConfidence(0.90)
                .overallEcosystemScore(82)
                .build();
    }

    @Test
    @DisplayName("Optimization engine calculates authoritative 6-factor deterministic score")
    void testOptimizeAndSaveStrategyDefault() {
        when(scenarioRepository.findByDeviceId("dev-1")).thenReturn(List.of());
        when(optimizationRepository.save(any(DigitalTwinOptimizationResult.class)))
                .thenAnswer(i -> i.getArgument(0));

        OptimizationResponse response = optimizationService.optimizeAndSaveStrategy("usr-1", testDevice, testSnapshot, null);

        assertThat(response).isNotNull();
        assertThat(response.recommendedStrategy()).isNotEmpty();
        assertThat(response.optimizationScore()).isBetween(0, 100);
        assertThat(response.costScore()).isBetween(0, 100);
        assertThat(response.reliabilityScore()).isBetween(0, 100);
        assertThat(response.longevityScore()).isBetween(0, 100);
        assertThat(response.sustainabilityScore()).isBetween(0, 100);
        assertThat(response.estimatedSavings()).isGreaterThanOrEqualTo(0.0);
        assertThat(response.estimatedLifespanGain()).isGreaterThanOrEqualTo(0);
        assertThat(response.estimatedCo2Savings()).isGreaterThanOrEqualTo(0.0);
        assertThat(response.decisionReason()).isNotEmpty();

        verify(optimizationRepository, times(1)).deleteByDeviceId("dev-1");
        verify(optimizationRepository, times(1)).save(any(DigitalTwinOptimizationResult.class));
    }

    @Test
    @DisplayName("Optimization with sustainability priority boosts circular strategies")
    void testOptimizeWithSustainabilityPriority() {
        when(scenarioRepository.findByDeviceId("dev-1")).thenReturn(List.of());
        when(optimizationRepository.save(any(DigitalTwinOptimizationResult.class)))
                .thenAnswer(i -> i.getArgument(0));

        OptimizationRequest request = new OptimizationRequest(
                10000.0,
                24,
                true,
                false,
                5
        );

        OptimizationResponse response = optimizationService.optimizeAndSaveStrategy("usr-1", testDevice, testSnapshot, request);

        assertThat(response).isNotNull();
        assertThat(response.sustainabilityScore()).isGreaterThanOrEqualTo(80);
    }
}
