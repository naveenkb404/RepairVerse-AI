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
class RepairCostAnalyticsServiceTest {

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private RepairCostAnalyticsService repairCostAnalyticsService;

    @Test
    @DisplayName("Should calculate cost analytics for user repairs")
    void getAnalyticsForUser_Success() {
        RepairHistory r1 = RepairHistory.builder()
                .id("rh-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .totalCost(150.0)
                .partsCost(80.0)
                .laborCost(70.0)
                .repairDate("2026-08-01")
                .build();

        Device d1 = Device.builder().id("dev-1").category("Smartphone").build();

        when(repairHistoryRepository.findByUserIdOrderByRepairDateDesc("usr-1")).thenReturn(List.of(r1));
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(d1));

        RepairCostAnalytics analytics = repairCostAnalyticsService.getAnalyticsForUser("usr-1");

        assertThat(analytics).isNotNull();
        assertThat(analytics.totalSpent()).isEqualTo(150.0);
        assertThat(analytics.totalPartsCost()).isEqualTo(80.0);
        assertThat(analytics.totalLaborCost()).isEqualTo(70.0);
        assertThat(analytics.isDemo()).isFalse();
    }

    @Test
    @DisplayName("Should return demo cost analytics")
    void getDemoAnalytics_Success() {
        RepairCostAnalytics demo = repairCostAnalyticsService.getDemoAnalytics();

        assertThat(demo).isNotNull();
        assertThat(demo.isDemo()).isTrue();
        assertThat(demo.totalSpent()).isGreaterThan(0);
        assertThat(demo.monthlyCostTrend()).isNotEmpty();
    }
}
