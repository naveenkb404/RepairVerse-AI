package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.repository.MarketplaceAnomalyRepository;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairNetworkIntelligenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairNetworkIntelligenceControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean RepairQualityScoringService    qualityService;
    @MockBean RepairTrustIntelligenceService trustService;
    @MockBean RepairOutcomeAnalyticsService  analyticsService;
    @MockBean RepairNetworkRankingService    rankingService;
    // Repositories required by Spring context even when mocked at service level
    @MockBean RepairShopQualitySnapshotRepository snapshotRepository;
    @MockBean MarketplaceAnomalyRepository        anomalyRepository;
    @MockBean JwtTokenProvider                    jwtTokenProvider;
    @MockBean CustomUserDetailsService            customUserDetailsService;

    @Test
    void getLeaderboard_returnsTopShops() throws Exception {
        when(rankingService.getLeaderboard(anyString(), anyInt())).thenReturn(List.of(
            new NetworkLeaderboardResponse(1, "shop-1", "Elite Repair", 95, 92, 0.97, 4.9, "STABLE", "🏆 #1 Best Overall")
        ));

        mockMvc.perform(get("/api/v1/network-intelligence/leaderboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].rank").value(1))
            .andExpect(jsonPath("$.data[0].qualityScore").value(95));
    }

    @Test
    void getCategories_returnsAnalyticsList() throws Exception {
        when(analyticsService.getCategoryAnalytics()).thenReturn(List.of(
            new CategoryQualityAnalyticsResponse("Smartphone", 120, 0.91, 79.0, 1.5, List.of("shop-1"))
        ));

        mockMvc.perform(get("/api/v1/network-intelligence/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].category").value("Smartphone"));
    }

    @Test
    void getShopTrends_returnsData() throws Exception {
        when(analyticsService.getShopTrends("shop-1")).thenReturn(List.of(
            new QualityTrendResponse("Last month", 85, 82, 0.91, 4.7)
        ));

        mockMvc.perform(get("/api/v1/network-intelligence/shop/shop-1/trends"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}
