package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.CircularEconomyAnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CircularEconomyAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CircularEconomyAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CircularEconomyAnalyticsService analyticsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/admin/circular-economy/analytics — returns platform circular analytics")
    void testGetPlatformAnalytics() throws Exception {
        PlatformCircularAnalyticsDto analytics = new PlatformCircularAnalyticsDto(
            150L, 450L, 320L, 18500.0, 1250.0, 1450000.0, 45L, 62L,
            List.of(new CategoryRankingDto("Laptops", 180L, 9500.0, 380.0, 750000.0)),
            List.of(new ShopSustainabilityRankingDto("shop-1", "GreenTech", true, "ELITE", 95, 80L, 4200.0)),
            List.of(new CircularTrendDto("Feb", 3500.0, 240.0, 280000.0, 70L))
        );

        when(analyticsService.getPlatformAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/admin/circular-economy/analytics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalUsers").value(150))
            .andExpect(jsonPath("$.data.totalCarbonSavedKg").value(18500.0))
            .andExpect(jsonPath("$.data.totalEwastePreventedKg").value(1250.0));
    }

    @Test
    @DisplayName("GET /api/v1/admin/circular-economy/trends — returns monthly trends")
    void testGetTrends() throws Exception {
        when(analyticsService.getMonthlyTrends()).thenReturn(List.of(
            new CircularTrendDto("Feb", 3420.0, 235.0, 275000.0, 68L)
        ));

        mockMvc.perform(get("/admin/circular-economy/trends"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.count").value(1))
            .andExpect(jsonPath("$.data[0].month").value("Feb"));
    }

    @Test
    @DisplayName("GET /api/v1/admin/circular-economy/rankings — returns rankings")
    void testGetRankings() throws Exception {
        when(analyticsService.getCategoryRankings()).thenReturn(List.of(
            new CategoryRankingDto("Laptops", 180L, 9500.0, 380.0, 750000.0)
        ));
        when(analyticsService.getTopSustainableShops()).thenReturn(List.of(
            new ShopSustainabilityRankingDto("shop-1", "GreenTech", true, "ELITE", 95, 80L, 4200.0)
        ));

        mockMvc.perform(get("/admin/circular-economy/rankings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.categoryRankings[0].categoryName").value("Laptops"))
            .andExpect(jsonPath("$.shopRankings[0].shopName").value("GreenTech"));
    }
}
