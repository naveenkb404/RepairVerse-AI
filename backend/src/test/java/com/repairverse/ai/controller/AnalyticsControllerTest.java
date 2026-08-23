package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairCostAnalyticsService;
import com.repairverse.ai.service.SustainabilityAnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AnalyticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairCostAnalyticsService repairCostAnalyticsService;

    @MockBean
    private SustainabilityAnalyticsService sustainabilityAnalyticsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/repair-costs - Returns repair cost analytics")
    void getRepairCostAnalytics_Success() throws Exception {
        RepairCostAnalytics analytics = new RepairCostAnalytics(
                450.0, 150.0, 200.0, 250.0, 160.0, 56.0, List.of(), List.of(), false
        );

        when(repairCostAnalyticsService.getAnalyticsForUser("usr-1")).thenReturn(analytics);

        mockMvc.perform(get("/analytics/repair-costs")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSpent").value(450.0));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/sustainability - Returns sustainability analytics")
    void getSustainabilityAnalytics_Success() throws Exception {
        SustainabilityAnalytics analytics = new SustainabilityAnalytics(
                65.4, 2.5, 320.0, 2, 2.97, 545.0, List.of(), List.of(), false
        );

        when(sustainabilityAnalyticsService.getAnalyticsForUser("usr-1")).thenReturn(analytics);

        mockMvc.perform(get("/analytics/sustainability")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCo2SavedKg").value(65.4));
    }
}
