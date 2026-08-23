package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.RepairHistory;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SustainabilityAnalyticsServiceTest {

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private SustainabilityAnalyticsService sustainabilityAnalyticsService;

    @Test
    @DisplayName("Should calculate sustainability analytics from repairs")
    void getAnalyticsForUser_Success() {
        RepairHistory r1 = RepairHistory.builder()
                .id("rh-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .co2SavedKg(42.5)
                .ewasteReducedKg(1.8)
                .moneySaved(200.0)
                .repairDate("2026-08-01")
                .build();

        Device d1 = Device.builder().id("dev-1").deviceName("Dell XPS 15").build();

        when(repairHistoryRepository.findByUserIdOrderByRepairDateDesc("usr-1")).thenReturn(List.of(r1));
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(d1));

        SustainabilityAnalytics analytics = sustainabilityAnalyticsService.getAnalyticsForUser("usr-1");

        assertThat(analytics).isNotNull();
        assertThat(analytics.totalCo2SavedKg()).isEqualTo(42.5);
        assertThat(analytics.totalEwasteReducedKg()).isEqualTo(1.8);
        assertThat(analytics.totalMoneySaved()).isEqualTo(200.0);
        assertThat(analytics.isDemo()).isFalse();
    }

    @Test
    @DisplayName("Should return demo sustainability analytics")
    void getDemoAnalytics_Success() {
        SustainabilityAnalytics demo = sustainabilityAnalyticsService.getDemoAnalytics();

        assertThat(demo).isNotNull();
        assertThat(demo.isDemo()).isTrue();
        assertThat(demo.totalCo2SavedKg()).isGreaterThan(0);
        assertThat(demo.topDevicesByImpact()).isNotEmpty();
    }
}
