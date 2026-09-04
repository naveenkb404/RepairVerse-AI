package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.service.EcosystemSimulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigitalTwinControllerTest {

    @Mock
    private EcosystemSimulationService simulationService;

    @InjectMocks
    private DigitalTwinController controller;

    @Test
    @DisplayName("GET /api/v1/digital-twin/dashboard returns ecosystem aggregate metrics")
    void testGetEcosystemDashboard() {
        EcosystemMetricsResponse metrics = new EcosystemMetricsResponse(3, 8500.0, 4, 32.5, 84, 3);
        when(simulationService.getUserEcosystemDashboard("usr-1")).thenReturn(metrics);

        ResponseEntity<Map<String, Object>> response = controller.getEcosystemDashboard(null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(true);
        assertThat(response.getBody().get("data")).isEqualTo(metrics);
        verify(simulationService, times(1)).getUserEcosystemDashboard("usr-1");
    }

    @Test
    @DisplayName("GET /api/v1/digital-twin/{deviceId} returns full digital twin response")
    void testGetDigitalTwin() {
        DigitalTwinSnapshotResponse snapshot = new DigitalTwinSnapshotResponse(
                "snap-1", "dev-1", "MacBook Pro", "LAPTOP",
                82, 28, 80, 85, 80, 90,
                70000.0, 3500.0, 0.28, 0.92, 84, LocalDateTime.now().toString()
        );
        OptimizationResponse opt = new OptimizationResponse(
                "opt-1", "dev-1", "PREVENTIVE_MAINTENANCE",
                85, 82, 85, 90, 86, 2800.0, 14, 19.2, "Deterministic multi-factor balance.", LocalDateTime.now().toString()
        );
        DigitalTwinDashboardResponse dashboard = new DigitalTwinDashboardResponse(
                "dev-1", "MacBook Pro", "LAPTOP", snapshot, List.of(), List.of(), opt, List.of(), List.of(), false
        );
        when(simulationService.getDigitalTwin("usr-1", "dev-1")).thenReturn(dashboard);

        ResponseEntity<Map<String, Object>> response = controller.getDigitalTwin("dev-1", null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(true);
        verify(simulationService, times(1)).getDigitalTwin("usr-1", "dev-1");
    }

    @Test
    @DisplayName("POST /api/v1/digital-twin/{deviceId}/refresh rebuilds digital twin")
    void testRefreshDigitalTwin() {
        DigitalTwinSnapshotResponse snapshot = new DigitalTwinSnapshotResponse(
                "snap-1", "dev-1", "MacBook Pro", "LAPTOP",
                85, 25, 80, 85, 80, 90,
                72000.0, 3000.0, 0.25, 0.92, 86, LocalDateTime.now().toString()
        );
        DigitalTwinDashboardResponse dashboard = new DigitalTwinDashboardResponse(
                "dev-1", "MacBook Pro", "LAPTOP", snapshot, List.of(), List.of(), null, List.of(), List.of(), false
        );
        when(simulationService.refreshDigitalTwin("usr-1", "dev-1")).thenReturn(dashboard);

        ResponseEntity<Map<String, Object>> response = controller.refreshDigitalTwin("dev-1", null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().get("message")).isEqualTo("Digital Twin recalibrated and refreshed successfully.");
        verify(simulationService, times(1)).refreshDigitalTwin("usr-1", "dev-1");
    }

    @Test
    @DisplayName("GET /api/v1/digital-twin/{deviceId}/forecasts returns multi-horizon list")
    void testGetForecasts() {
        ForecastResponse f = new ForecastResponse("f-1", "snap-1", "dev-1", 6, 78, 32, 4200.0, 68000.0, 28, 12.0, 0.5, 0.90);
        when(simulationService.getForecasts("usr-1", "dev-1")).thenReturn(List.of(f));

        ResponseEntity<Map<String, Object>> response = controller.getForecasts("dev-1", null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/digital-twin/{deviceId}/simulate executes parameterized simulation")
    void testRunCustomSimulation() {
        RunSimulationRequest req = new RunSimulationRequest(5000.0, 24, true, true, 3, "PREVENTIVE_MAINTENANCE");
        DigitalTwinDashboardResponse dashboard = new DigitalTwinDashboardResponse(
                "dev-1", "MacBook Pro", "LAPTOP", null, List.of(), List.of(), null, List.of(), List.of(), true
        );
        when(simulationService.simulateCustomScenario("usr-1", "dev-1", req)).thenReturn(dashboard);

        ResponseEntity<Map<String, Object>> response = controller.runCustomSimulation("dev-1", req, null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().get("message")).isEqualTo("Custom simulation executed successfully.");
    }
}
