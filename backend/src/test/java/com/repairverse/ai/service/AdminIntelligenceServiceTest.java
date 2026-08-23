package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DevicePrediction;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.repository.DevicePredictionRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminIntelligenceServiceTest {

    @Mock
    private DevicePredictionRepository devicePredictionRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminIntelligenceService adminIntelligenceService;

    @Test
    @DisplayName("Should return admin intelligence summary with live data")
    void getSummary_Success() {
        DevicePrediction dp = DevicePrediction.builder()
                .id("dp-1")
                .deviceId("dev-1")
                .userId("usr-1")
                .predictionScore(30)
                .riskLevel("CRITICAL")
                .primaryFaultType("Battery Degradation")
                .co2SavingsKg(15.0)
                .estimatedRepairCost(120.0)
                .preventiveSavings(48.0)
                .evaluatedAt(LocalDateTime.now())
                .build();

        Device d = Device.builder().id("dev-1").deviceName("iPhone").category("Smartphone").build();
        User u = User.builder().id("usr-1").email("user@test.com").build();

        when(devicePredictionRepository.count()).thenReturn(1L);
        when(devicePredictionRepository.countByRiskLevel("CRITICAL")).thenReturn(1L);
        when(devicePredictionRepository.countByRiskLevel("HIGH")).thenReturn(0L);
        when(devicePredictionRepository.findPlatformAveragePredictionScore()).thenReturn(30.0);
        when(devicePredictionRepository.sumTotalEstimatedRepairCost()).thenReturn(120.0);
        when(devicePredictionRepository.sumTotalPreventiveSavings()).thenReturn(48.0);
        when(devicePredictionRepository.findAll()).thenReturn(List.of(dp));
        when(devicePredictionRepository.findHighAndCriticalRiskDevices()).thenReturn(List.of(dp));
        when(deviceRepository.findAll()).thenReturn(List.of(d));
        when(userRepository.findAll()).thenReturn(List.of(u));

        AdminIntelligenceSummary summary = adminIntelligenceService.getSummary();

        assertThat(summary).isNotNull();
        assertThat(summary.totalPredictionsGenerated()).isEqualTo(1L);
        assertThat(summary.devicesAtCriticalRisk()).isEqualTo(1L);
        assertThat(summary.isDemo()).isFalse();
    }

    @Test
    @DisplayName("Should return platform fleet overview")
    void getPlatformFleetOverview_Success() {
        when(devicePredictionRepository.count()).thenReturn(10L);
        when(devicePredictionRepository.countByRiskLevel("CRITICAL")).thenReturn(1L);
        when(devicePredictionRepository.countByRiskLevel("HIGH")).thenReturn(2L);
        when(devicePredictionRepository.countByRiskLevel("MEDIUM")).thenReturn(3L);
        when(devicePredictionRepository.countByRiskLevel("LOW")).thenReturn(2L);
        when(devicePredictionRepository.countByRiskLevel("HEALTHY")).thenReturn(2L);
        when(devicePredictionRepository.findPlatformAveragePredictionScore()).thenReturn(75.0);
        when(devicePredictionRepository.sumTotalEstimatedRepairCost()).thenReturn(1500.0);
        when(devicePredictionRepository.sumTotalPreventiveSavings()).thenReturn(600.0);
        when(devicePredictionRepository.findAll()).thenReturn(List.of());

        PredictiveFleetOverview overview = adminIntelligenceService.getPlatformFleetOverview();

        assertThat(overview).isNotNull();
        assertThat(overview.totalDevices()).isEqualTo(10L);
        assertThat(overview.criticalDevices()).isEqualTo(1L);
        assertThat(overview.isDemo()).isFalse();
    }
}
