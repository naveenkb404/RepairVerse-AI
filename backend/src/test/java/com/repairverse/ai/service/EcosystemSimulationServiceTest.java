package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EcosystemSimulationServiceTest {

    @Mock
    private DigitalTwinSnapshotRepository snapshotRepository;

    @Mock
    private DigitalTwinForecastRepository forecastRepository;

    @Mock
    private DigitalTwinScenarioRepository scenarioRepository;

    @Mock
    private DigitalTwinOptimizationResultRepository optimizationRepository;

    @Mock
    private EcosystemSimulationEventRepository eventRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DigitalTwinStateService stateService;

    @Mock
    private DeviceTrajectoryForecastService forecastService;

    @Mock
    private DigitalTwinScenarioSimulationService scenarioService;

    @Mock
    private RepairStrategyOptimizationService optimizationService;

    @Mock
    private SimulationInsightService insightService;

    @InjectMocks
    private EcosystemSimulationService simulationService;

    private Device testDevice;
    private DigitalTwinSnapshot testSnapshot;
    private DigitalTwinOptimizationResult testOptimization;

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
                .healthScore(82)
                .failureRiskScore(28)
                .maintenanceScore(80)
                .repairEconomicsScore(85)
                .longevityScore(80)
                .sustainabilityScore(90)
                .predictedValue(72000.0)
                .predictedRepairCost(3500.0)
                .predictedFailureProbability(0.28)
                .simulationConfidence(0.92)
                .overallEcosystemScore(84)
                .snapshotTime(LocalDateTime.now())
                .build();

        testOptimization = DigitalTwinOptimizationResult.builder()
                .id("opt-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .recommendedStrategy("PREVENTIVE_MAINTENANCE")
                .costScore(85)
                .reliabilityScore(82)
                .longevityScore(85)
                .sustainabilityScore(90)
                .optimizationScore(86)
                .estimatedSavings(2800.0)
                .estimatedLifespanGain(14)
                .estimatedCo2Savings(19.2)
                .decisionReason("Deterministic multi-objective balance.")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Get digital twin returns full dashboard response for valid user and device")
    void testGetDigitalTwinSuccess() {
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(testDevice));
        when(snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc("dev-1")).thenReturn(Optional.of(testSnapshot));
        when(forecastRepository.findBySnapshotIdOrderByForecastHorizonMonthsAsc("snap-1")).thenReturn(List.of());
        when(forecastService.generateAndSaveForecasts(any(), any())).thenReturn(List.of());
        when(scenarioRepository.findByDeviceIdOrderByOverallOutcomeScoreDesc("dev-1")).thenReturn(List.of());
        when(scenarioService.simulateAndSaveScenarios(any(), any(), any())).thenReturn(List.of());
        when(optimizationRepository.findTopByDeviceIdOrderByCreatedAtDesc("dev-1")).thenReturn(Optional.of(testOptimization));
        when(eventRepository.findByDeviceIdOrderByProjectedMonthOffsetAsc("dev-1")).thenReturn(List.of());
        when(insightService.generateInsights(any(), any(), any(), any())).thenReturn(List.of());

        DigitalTwinDashboardResponse response = simulationService.getDigitalTwin("usr-1", "dev-1");

        assertThat(response).isNotNull();
        assertThat(response.deviceId()).isEqualTo("dev-1");
        assertThat(response.deviceName()).isEqualTo("MacBook Pro M1");
        assertThat(response.snapshot().healthScore()).isEqualTo(82);
        assertThat(response.optimalStrategy().recommendedStrategy()).isEqualTo("PREVENTIVE_MAINTENANCE");
    }

    @Test
    @DisplayName("Access to device belonging to another user throws SecurityException")
    void testGetDigitalTwinUnauthorized() {
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(testDevice));

        assertThatThrownBy(() -> simulationService.getDigitalTwin("usr-other", "dev-1"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("Get user ecosystem dashboard calculates aggregated metrics")
    void testGetUserEcosystemDashboard() {
        when(deviceRepository.findByUserId("usr-1")).thenReturn(List.of(testDevice));
        when(snapshotRepository.findByUserId("usr-1")).thenReturn(List.of(testSnapshot));
        when(optimizationRepository.findByUserId("usr-1")).thenReturn(List.of(testOptimization));

        EcosystemMetricsResponse metrics = simulationService.getUserEcosystemDashboard("usr-1");

        assertThat(metrics).isNotNull();
        assertThat(metrics.totalMonitoredDevices()).isEqualTo(1);
        assertThat(metrics.totalProjectedSavings()).isEqualTo(2800.0);
        assertThat(metrics.totalCo2AvoidedKg()).isEqualTo(19.2);
        assertThat(metrics.averageEcosystemHealth()).isEqualTo(82);
    }
}
