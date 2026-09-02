package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairMatchingDto.PlatformMarketplaceAnalytics;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.MarketplaceAnalyticsService;
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
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MarketplaceAdminAnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class MarketplaceAdminAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarketplaceAnalyticsService analyticsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/admin/marketplace/analytics — returns platform analytics")
    void testGetPlatformAnalytics() throws Exception {
        PlatformMarketplaceAnalytics analytics = new PlatformMarketplaceAnalytics(
                15, 12, 120, 78.5, 76.0,
                Map.of("Smartphone", 80L),
                Map.of("Screen", 60L),
                List.of(),
                Map.of("MATCH_SEARCHED", 150L),
                false
        );

        when(analyticsService.getPlatformAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/admin/marketplace/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalShops").value(15))
                .andExpect(jsonPath("$.data.quoteAcceptanceRate").value(78.5));
    }
}
