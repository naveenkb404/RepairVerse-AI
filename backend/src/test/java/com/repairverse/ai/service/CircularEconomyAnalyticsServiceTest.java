package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import com.repairverse.ai.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CircularEconomyAnalyticsServiceTest {

    @Mock
    private CircularImpactEventRepository eventRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RepairShopRepository repairShopRepository;

    @InjectMocks
    private CircularEconomyAnalyticsService analyticsService;

    @Test
    @DisplayName("Aggregates platform analytics with rankings and trend series")
    void testGetPlatformAnalytics() {
        when(userRepository.count()).thenReturn(150L);
        when(deviceRepository.count()).thenReturn(300L);
        when(eventRepository.sumPlatformCarbonSaved()).thenReturn(15400.0);
        when(eventRepository.sumPlatformEwastePrevented()).thenReturn(1100.0);
        when(eventRepository.sumPlatformMoneySaved()).thenReturn(1200000.0);
        when(repairShopRepository.findAll()).thenReturn(Collections.emptyList());

        PlatformCircularAnalyticsDto analytics = analyticsService.getPlatformAnalytics();

        assertThat(analytics).isNotNull();
        assertThat(analytics.totalUsers()).isEqualTo(150L);
        assertThat(analytics.totalCarbonSavedKg()).isEqualTo(15400.0);
        assertThat(analytics.totalEwastePreventedKg()).isEqualTo(1100.0);
        assertThat(analytics.categoryRankings()).isNotEmpty();
        assertThat(analytics.topSustainableShops()).isNotEmpty();
        assertThat(analytics.monthlyTrends()).isNotEmpty();
    }
}
