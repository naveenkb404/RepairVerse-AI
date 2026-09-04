package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceScenario;
import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceScenarioSimulationRequest;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.repository.DeviceDecisionScenarioRepository;
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
class DeviceScenarioSimulationServiceTest {

    @Mock
    private DeviceDecisionScenarioRepository scenarioRepository;

    @InjectMocks
    private DeviceScenarioSimulationService simulationService;

    private Device sampleDevice;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("Dell XPS 15")
                .category("laptop")
                .build();
    }

    @Test
    @DisplayName("Generate scenarios produces 7 standard scenarios")
    void testGenerateScenarios() {
        List<DeviceScenario> scenarios = simulationService.generateScenarios(
                sampleDevice, 80, 20, 150.0, 1200.0, 36.0
        );

        assertThat(scenarios).hasSize(7);
        assertThat(scenarios).extracting(DeviceScenario::scenarioType)
                .containsExactlyInAnyOrder(
                        "CONTINUE_USING", "MAINTENANCE", "REPAIR", "PROFESSIONAL_SERVICE",
                        "REFURBISH", "REPLACE", "RECYCLE"
                );
    }

    @Test
    @DisplayName("Generate and save scenarios calls repository save")
    void testGenerateAndSaveScenarios() {
        List<DeviceScenario> scenarios = simulationService.generateAndSaveScenarios(
                sampleDevice, "usr-1", 75, 25, 120.0, 1000.0, 25.0
        );

        assertThat(scenarios).hasSize(7);
        verify(scenarioRepository, times(1)).deleteByDeviceIdAndUserId("dev-1", "usr-1");
        verify(scenarioRepository, times(7)).save(any());
    }

    @Test
    @DisplayName("Custom scenario simulation applies budget and sustainability bonuses")
    void testCustomScenarioSimulation() {
        DeviceScenarioSimulationRequest request = new DeviceScenarioSimulationRequest(
                "REPAIR", 200.0, 24, true
        );

        List<DeviceScenario> scenarios = simulationService.simulateCustomScenario(
                sampleDevice, 75, 25, 120.0, 1000.0, 25.0, request
        );

        assertThat(scenarios).hasSize(7);
        DeviceScenario repair = scenarios.stream().filter(s -> "REPAIR".equals(s.scenarioType())).findFirst().orElseThrow();
        assertThat(repair.intelligenceScore()).isGreaterThanOrEqualTo(85);
    }
}
