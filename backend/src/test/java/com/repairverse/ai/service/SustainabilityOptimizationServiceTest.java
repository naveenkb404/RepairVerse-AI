package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.SustainabilityRecommendationDto;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SustainabilityOptimizationServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @InjectMocks
    private SustainabilityOptimizationService optimizationService;

    @Test
    @DisplayName("Generates critical repair recommendation for low health device (< 45%)")
    void testCriticalDeviceRecommendation() {
        Device lowHealthDevice = Device.builder()
            .id("dev-low")
            .userId("usr-1")
            .category("Laptop")
            .brand("Lenovo")
            .model("ThinkPad")
            .health(DeviceHealth.builder().healthScore(35).build())
            .build();

        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(lowHealthDevice));

        List<SustainabilityRecommendationDto> recs = optimizationService.getRecommendations("usr-1", null);

        assertThat(recs).isNotEmpty();
        SustainabilityRecommendationDto rec = recs.get(0);
        assertThat(rec.priority()).isEqualTo("CRITICAL");
        assertThat(rec.actionType()).isEqualTo("REPAIR_NOW");
        assertThat(rec.estimatedCarbonImpact()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Generates scheduled maintenance recommendation for medium health device (45-69%)")
    void testMaintenanceDeviceRecommendation() {
        Device medHealthDevice = Device.builder()
            .id("dev-med")
            .userId("usr-1")
            .category("Smartphone")
            .brand("Google")
            .model("Pixel 7")
            .health(DeviceHealth.builder().healthScore(62).build())
            .build();

        when(deviceRepository.findByIdAndUserId("dev-med", "usr-1")).thenReturn(Optional.of(medHealthDevice));

        List<SustainabilityRecommendationDto> recs = optimizationService.getRecommendations("usr-1", "dev-med");

        assertThat(recs).isNotEmpty();
        SustainabilityRecommendationDto rec = recs.get(0);
        assertThat(rec.priority()).isEqualTo("HIGH");
        assertThat(rec.actionType()).isEqualTo("SCHEDULE_MAINTENANCE");
    }

    @Test
    @DisplayName("Returns fallback recommendations when user has no devices")
    void testFallbackRecommendations() {
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-empty")).thenReturn(Collections.emptyList());

        List<SustainabilityRecommendationDto> recs = optimizationService.getRecommendations("usr-empty", null);

        assertThat(recs).isNotEmpty();
        assertThat(recs.get(0).title()).isNotBlank();
    }
}
