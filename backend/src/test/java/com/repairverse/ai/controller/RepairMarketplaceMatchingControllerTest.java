package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RepairMatchingDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.MarketplaceAnalyticsService;
import com.repairverse.ai.service.QuoteIntelligenceService;
import com.repairverse.ai.service.RepairMatchingService;
import com.repairverse.ai.service.SmartRepairRecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairMarketplaceMatchingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairMarketplaceMatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairMatchingService matchingService;

    @MockBean
    private SmartRepairRecommendationService recommendationService;

    @MockBean
    private QuoteIntelligenceService quoteIntelligenceService;

    @MockBean
    private MarketplaceAnalyticsService analyticsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/marketplace/matches/device/{deviceId} — returns ranked matches")
    void testGetDeviceMatches() throws Exception {
        RepairShopMatchResponse match = new RepairShopMatchResponse(
                "shop-1", "Tech Care", "123 Main", 37.77, -122.41,
                "+1 555", "tc@care.com", "9-5", 4.9, 100, "TRUSTED", "PREMIUM",
                2.0, 95, "EXCELLENT_MATCH", 1, List.of(),
                new RepairMatchExplanation("Summary", List.of(), "EXCELLENT_MATCH", List.of()),
                75.0, 4.0, 90, 96, true, false
        );

        when(matchingService.findMatchesForDevice(eq("dev-1"), any(), any(), any(), any(), any()))
                .thenReturn(List.of(match));

        mockMvc.perform(get("/marketplace/matches/device/dev-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].shopId").value("shop-1"))
                .andExpect(jsonPath("$.data[0].overallScore").value(95));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/recommendations/device/{deviceId} — returns recommendations")
    void testGetDeviceRecommendations() throws Exception {
        SmartRecommendationResponse response = new SmartRecommendationResponse(
                "dev-1", "MacBook Pro", List.of(), List.of(), 1, LocalDateTime.now(), false
        );

        when(recommendationService.getRecommendationsForDevice(eq("dev-1"), any(), any(), any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(get("/marketplace/recommendations/device/dev-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("dev-1"));
    }

    @Test
    @DisplayName("POST /api/v1/marketplace/compare — returns comparison matrix")
    void testCompareShops() throws Exception {
        RepairMarketplaceComparison comparison = new RepairMarketplaceComparison(
                List.of(), List.of(), "shop-1", "shop-2", "shop-1", "shop-1", "shop-1", "shop-1", "Comparison summary", false
        );

        when(recommendationService.compareShops(any(), any(), any(), any(), any()))
                .thenReturn(comparison);

        CompareShopsRequest request = new CompareShopsRequest(List.of("shop-1", "shop-2"), "dev-1");

        mockMvc.perform(post("/marketplace/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bestOverallShopId").value("shop-1"));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/quotes/{quoteId}/intelligence — returns price intelligence")
    void testGetQuoteIntelligence() throws Exception {
        QuoteIntelligenceResponse intelligence = new QuoteIntelligenceResponse(
                "q-1", "shop-1", "Tech Care", 80.0, 40.0, 40.0, 85.0,
                -5.0, -5.8, "FAIR_PRICE", "⚖️ Fair Market Price", 88,
                List.of("Insight 1"), List.of(), false
        );

        when(quoteIntelligenceService.evaluateQuoteIntelligence(eq("q-1"), any()))
                .thenReturn(intelligence);

        mockMvc.perform(get("/marketplace/quotes/q-1/intelligence"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.classification").value("FAIR_PRICE"));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/analytics — returns user marketplace insights")
    void testGetUserMarketplaceAnalytics() throws Exception {
        UserMarketplaceInsights insights = new UserMarketplaceInsights(
                4, 2, 1, 65.0, 30.0, List.of("Opp 1"), List.of(), false
        );

        when(analyticsService.getUserInsights(any())).thenReturn(insights);

        mockMvc.perform(get("/marketplace/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalQuotesRequested").value(2));
    }
}
