package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DevicePrediction;
import com.repairverse.ai.entity.FaultPattern;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictiveScoringServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private FaultPatternRepository faultPatternRepository;

    @Mock
    private DevicePredictionRepository devicePredictionRepository;

    @Mock
    private NotificationService notificationService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private PredictiveScoringService predictiveScoringService;

    private Device testDevice;
    private DeviceHealth testHealth;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-123")
                .userId("usr-456")
                .deviceName("Test iPhone 14")
                .category("Smartphone")
                .brand("Apple")
                .model("A2882")
                .purchaseDate("2023-01-15")
                .warrantyExpiry("2025-01-15")
                .build();

        testHealth = DeviceHealth.builder()
                .id("dh-1")
                .deviceId("dev-123")
                .healthScore(85)
                .batteryHealth(88)
                .lastService("2024-01-10")
                .build();
    }

    @Test
    @DisplayName("Should successfully evaluate device score and persist prediction")
    void evaluateDevice_Success() {
        when(deviceRepository.findByIdAndUserId("dev-123", "usr-456")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-123")).thenReturn(Optional.of(testHealth));
        when(repairHistoryRepository.countByUserId("usr-456")).thenReturn(1L);
        when(faultPatternRepository.findActiveByCategoryAndBrand("Smartphone", "Apple")).thenReturn(List.of());
        when(devicePredictionRepository.findByDeviceId("dev-123")).thenReturn(Optional.empty());
        when(devicePredictionRepository.save(any(DevicePrediction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DevicePredictionResponse response = predictiveScoringService.evaluateDevice("dev-123", "usr-456");

        assertThat(response).isNotNull();
        assertThat(response.deviceId()).isEqualTo("dev-123");
        assertThat(response.predictionScore()).isGreaterThan(0);
        assertThat(response.scoringBreakdown()).isNotEmpty();
        verify(devicePredictionRepository, atLeastOnce()).save(any(DevicePrediction.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when device does not exist or belong to user")
    void evaluateDevice_NotFound() {
        when(deviceRepository.findByIdAndUserId("dev-none", "usr-456")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> predictiveScoringService.evaluateDevice("dev-none", "usr-456"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should evaluate demo device without persistence")
    void evaluateDemoDevice_Success() {
        DevicePredictionResponse res = predictiveScoringService.evaluateDemoDevice(
                "dev-demo", "Demo Phone", "Smartphone", "Samsung", 75, 80, "2023-05-01"
        );

        assertThat(res).isNotNull();
        assertThat(res.isDemo()).isTrue();
        assertThat(res.deviceId()).isEqualTo("dev-demo");
        verifyNoInteractions(devicePredictionRepository);
    }

    @Test
    @DisplayName("Should retrieve fleet predictions for user")
    void getUserFleet_Success() {
        DevicePrediction dp = DevicePrediction.builder()
                .id("dp-1")
                .deviceId("dev-123")
                .userId("usr-456")
                .predictionScore(82)
                .riskLevel("LOW")
                .evaluatedAt(LocalDateTime.now())
                .build();

        when(devicePredictionRepository.findByUserIdOrderByEvaluatedAtDesc("usr-456")).thenReturn(List.of(dp));
        when(deviceRepository.findById("dev-123")).thenReturn(Optional.of(testDevice));

        List<DevicePredictionResponse> fleet = predictiveScoringService.getUserFleet("usr-456");

        assertThat(fleet).hasSize(1);
        assertThat(fleet.get(0).deviceName()).isEqualTo("Test iPhone 14");
    }
}
