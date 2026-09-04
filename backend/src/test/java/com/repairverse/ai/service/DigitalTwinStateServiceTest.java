package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.DigitalTwinSnapshotResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.DigitalTwinSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DigitalTwinStateServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository healthRepository;

    @Mock
    private DigitalTwinSnapshotRepository snapshotRepository;

    @InjectMocks
    private DigitalTwinStateService stateService;

    private Device testDevice;
    private DeviceHealth testHealth;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro M1")
                .category("LAPTOP")
                .brand("Apple")
                .model("A2338")
                .purchasePrice(85000.0)
                .build();

        testHealth = DeviceHealth.builder()
                .id("dh-1")
                .deviceId("dev-1")
                .healthScore(82)
                .batteryHealth(88)
                .build();
    }

    @Test
    @DisplayName("Generate state snapshot correctly calculates normalized scores and persists snapshot")
    void testBuildAndSaveSnapshot() {
        when(healthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(testHealth));
        when(snapshotRepository.save(any(DigitalTwinSnapshot.class))).thenAnswer(i -> i.getArgument(0));

        DigitalTwinSnapshot snapshot = stateService.buildAndSaveSnapshot("usr-1", testDevice);

        assertThat(snapshot).isNotNull();
        assertThat(snapshot.getDeviceId()).isEqualTo("dev-1");
        assertThat(snapshot.getHealthScore()).isBetween(0, 100);
        assertThat(snapshot.getFailureRiskScore()).isBetween(0, 100);
        assertThat(snapshot.getMaintenanceScore()).isBetween(0, 100);
        assertThat(snapshot.getRepairEconomicsScore()).isBetween(0, 100);
        assertThat(snapshot.getLongevityScore()).isBetween(0, 100);
        assertThat(snapshot.getSustainabilityScore()).isBetween(0, 100);
        assertThat(snapshot.getOverallEcosystemScore()).isBetween(0, 100);
        assertThat(snapshot.getSimulationConfidence()).isGreaterThanOrEqualTo(0.5);

        verify(snapshotRepository, times(1)).save(any(DigitalTwinSnapshot.class));
    }

    @Test
    @DisplayName("Get snapshot returns existing snapshot mapped to DTO")
    void testGetLatestSnapshot() {
        DigitalTwinSnapshot mockSnapshot = DigitalTwinSnapshot.builder()
                .id("snap-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .healthScore(82)
                .failureRiskScore(28)
                .maintenanceScore(75)
                .repairEconomicsScore(85)
                .longevityScore(80)
                .sustainabilityScore(90)
                .predictedValue(70000.0)
                .predictedRepairCost(3500.0)
                .predictedFailureProbability(0.28)
                .simulationConfidence(0.92)
                .overallEcosystemScore(84)
                .snapshotTime(LocalDateTime.now())
                .build();

        when(snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc("dev-1"))
                .thenReturn(Optional.of(mockSnapshot));

        DigitalTwinSnapshotResponse response = stateService.getLatestSnapshot("usr-1", testDevice);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("snap-1");
        assertThat(response.healthScore()).isEqualTo(82);
        assertThat(response.failureRiskScore()).isEqualTo(28);
        assertThat(response.overallEcosystemScore()).isEqualTo(84);
    }
}
