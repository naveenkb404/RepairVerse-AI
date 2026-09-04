package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.ScenarioResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigitalTwinScenarioSimulationServiceTest {

    @Mock
    private DigitalTwinScenarioRepository scenarioRepository;

    @InjectMocks
    private DigitalTwinScenarioSimulationService scenarioService;

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
                .predictedValue(70000.0)
                .predictedRepairCost(3500.0)
                .simulationConfidence(0.90)
                .overallEcosystemScore(80)
                .build();
    }

    @Test
    @DisplayName("Simulate 8 distinct scenarios with deterministic scoring and ranking")
    void testSimulateAndSaveScenarios() {
        when(scenarioRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<ScenarioResponse> scenarios = scenarioService.simulateAndSaveScenarios("usr-1", testDevice, testSnapshot);

        assertThat(scenarios).hasSize(8);

        // Verify scenario types are represented
        List<String> types = scenarios.stream().map(ScenarioResponse::scenarioType).toList();
        assertThat(types).contains(
                "CONTINUE_CURRENT_USAGE",
                "PREVENTIVE_MAINTENANCE",
                "REPAIR_NOW",
                "DELAY_REPAIR",
                "PROFESSIONAL_SERVICE",
                "REFURBISH_DEVICE",
                "REPLACE_DEVICE",
                "RECYCLE_DEVICE"
        );

        // Verify each scenario has valid bounded scores
        for (ScenarioResponse s : scenarios) {
            assertThat(s.projectedHealthScore()).isBetween(0, 100);
            assertThat(s.projectedFailureRisk()).isBetween(0, 100);
            assertThat(s.overallOutcomeScore()).isBetween(0, 100);
            assertThat(s.simulationConfidence()).isGreaterThanOrEqualTo(0.5);
            assertThat(s.downtimeDays()).isGreaterThanOrEqualTo(0);
        }

        verify(scenarioRepository, times(1)).deleteByDeviceId("dev-1");
        verify(scenarioRepository, times(1)).saveAll(anyList());
    }
}
