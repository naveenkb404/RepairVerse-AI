package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DevicePrediction;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DevicePredictionRepository;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaintenanceRecommendationServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private DevicePredictionRepository devicePredictionRepository;

    @InjectMocks
    private MaintenanceRecommendationService recommendationService;

    @Test
    @DisplayName("Should generate recommendations for user devices based on prediction risk")
    void getRecommendationsForUser_Success() {
        Device device = Device.builder().id("dev-1").deviceName("MacBook Pro").build();
        DeviceHealth health = DeviceHealth.builder().id("dh-1").deviceId("dev-1").batteryHealth(65).build();
        DevicePrediction prediction = DevicePrediction.builder().id("dp-1").deviceId("dev-1").riskLevel("HIGH").primaryFaultType("Thermal Overheating").build();

        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(device));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(health));
        when(devicePredictionRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(prediction));

        List<MaintenanceRecommendation> recs = recommendationService.getRecommendationsForUser("usr-1");

        assertThat(recs).isNotEmpty();
        assertThat(recs.stream().anyMatch(r -> r.priority().equals("HIGH"))).isTrue();
    }

    @Test
    @DisplayName("Should return demo recommendations")
    void getDemoRecommendations_Success() {
        List<MaintenanceRecommendation> recs = recommendationService.getDemoRecommendations();

        assertThat(recs).isNotEmpty();
        assertThat(recs.get(0).title()).contains("Battery");
    }
}
