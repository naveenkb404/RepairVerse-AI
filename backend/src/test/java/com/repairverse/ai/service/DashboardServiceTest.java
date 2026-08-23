package com.repairverse.ai.service;

import com.repairverse.ai.dto.DashboardDto.*;
import com.repairverse.ai.entity.ActivityLog;
import com.repairverse.ai.entity.CarbonImpact;
import com.repairverse.ai.repository.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Mock
    private CarbonImpactRepository carbonImpactRepository;

    @Mock
    private ActivityLogRepository activityLogRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should return aggregated dashboard statistics")
    void getDashboardStats_Success() {
        when(deviceRepository.countByUserId("usr-1")).thenReturn(2L);
        when(repairHistoryRepository.countByUserId("usr-1")).thenReturn(4L);
        when(repairHistoryRepository.countByUserIdAndStatus("usr-1", "In Progress")).thenReturn(1L);

        CarbonImpact carbon = CarbonImpact.builder()
                .co2Saved(55.0)
                .moneySaved(800.0)
                .sustainabilityScore(88)
                .build();
        when(carbonImpactRepository.findByUserId("usr-1")).thenReturn(Optional.of(carbon));
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of());

        DashboardStatsResponse stats = dashboardService.getDashboardStats("usr-1");

        assertThat(stats.totalDevices()).isEqualTo(2);
        assertThat(stats.totalRepairs()).isEqualTo(4);
        assertThat(stats.totalCarbonSaved()).isEqualTo(55.0);
        assertThat(stats.totalMoneySaved()).isEqualTo(800.0);
        assertThat(stats.healthScore()).isEqualTo(88);
        assertThat(stats.activeRepairs()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return activity feed from database or sample fallback")
    void getActivityFeed_SampleFallback() {
        when(activityLogRepository.findTop20ByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of());

        List<ActivityItemResponse> feed = dashboardService.getActivityFeed("usr-1");

        assertThat(feed).isNotEmpty();
        assertThat(feed.get(0).id()).isEqualTo("act-001");
    }

    @Test
    @DisplayName("Should log activity successfully")
    void logActivity_Success() {
        dashboardService.logActivity("usr-1", "device_added", "Device Added", "Added iPad", "iPad", "cyan");

        verify(activityLogRepository, times(1)).save(any(ActivityLog.class));
    }
}
