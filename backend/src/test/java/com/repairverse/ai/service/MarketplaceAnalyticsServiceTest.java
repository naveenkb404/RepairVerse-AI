package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.entity.MarketplaceInteraction;
import com.repairverse.ai.entity.RepairQuote;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketplaceAnalyticsServiceTest {

    @Mock
    private RepairQuoteRepository quoteRepository;

    @Mock
    private RepairShopRepository shopRepository;

    @Mock
    private RepairShopProfileRepository profileRepository;

    @Mock
    private RepairMatchHistoryRepository matchHistoryRepository;

    @Mock
    private MarketplaceInteractionRepository interactionRepository;

    @Mock
    private RepairMatchingService matchingService;

    @InjectMocks
    private MarketplaceAnalyticsService analyticsService;

    @Test
    @DisplayName("getUserInsights — computes user marketplace statistics and savings")
    void testGetUserInsights() {
        RepairQuote quote1 = RepairQuote.builder().id("q-1").userId("usr-1").estimatedCost(60.0).status("ACCEPTED").build();
        RepairQuote quote2 = RepairQuote.builder().id("q-2").userId("usr-1").estimatedCost(75.0).status("SUBMITTED").build();

        when(quoteRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(quote1, quote2));
        when(interactionRepository.countByUserIdAndInteractionType("usr-1", "SHOP_COMPARED")).thenReturn(5L);
        when(matchingService.findMatchesForDevice(any(), any(), any(), any(), any(), any())).thenReturn(List.of());

        UserMarketplaceInsights insights = analyticsService.getUserInsights("usr-1");

        assertNotNull(insights);
        assertEquals(5, insights.totalShopsCompared());
        assertEquals(2, insights.totalQuotesRequested());
        assertEquals(1, insights.totalQuotesAccepted());
        assertTrue(insights.averageRepairCost() > 0);
        assertTrue(insights.totalPotentialSavings() >= 0);
    }

    @Test
    @DisplayName("getPlatformAnalytics — computes platform-wide marketplace analytics")
    void testGetPlatformAnalytics() {
        when(shopRepository.findAll()).thenReturn(List.of());
        when(quoteRepository.findAll()).thenReturn(List.of());

        PlatformMarketplaceAnalytics analytics = analyticsService.getPlatformAnalytics();

        assertNotNull(analytics);
        assertTrue(analytics.totalShops() > 0);
        assertTrue(analytics.quoteAcceptanceRate() > 0);
        assertFalse(analytics.popularDeviceCategories().isEmpty());
        assertFalse(analytics.highPerformingShops().isEmpty());
    }

    @Test
    @DisplayName("trackInteraction — saves interaction event")
    void testTrackInteraction() {
        TrackInteractionRequest request = new TrackInteractionRequest("SHOP_VIEWED", "shop-1", "SHOP", "{}");

        analyticsService.trackInteraction("usr-1", request);

        verify(interactionRepository, times(1)).save(any(MarketplaceInteraction.class));
    }
}
