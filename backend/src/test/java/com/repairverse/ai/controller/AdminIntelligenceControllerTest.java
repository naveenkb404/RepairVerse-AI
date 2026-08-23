package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.AdminIntelligenceService;
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

@WebMvcTest(controllers = AdminIntelligenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AdminIntelligenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminIntelligenceService adminIntelligenceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/admin/intelligence/summary - Returns platform intelligence summary")
    void getIntelligenceSummary_Success() throws Exception {
        AdminIntelligenceSummary summary = new AdminIntelligenceSummary(
                1200, 15, 45, 78.5, 34000.0, 13600.0, 14200.0, List.of(), List.of(), false
        );

        when(adminIntelligenceService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/admin/intelligence/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPredictionsGenerated").value(1200));
    }

    @Test
    @DisplayName("GET /api/v1/admin/intelligence/fleet - Returns platform fleet overview")
    void getPlatformFleet_Success() throws Exception {
        PredictiveFleetOverview fleet = new PredictiveFleetOverview(
                500, 10, 30, 100, 200, 160, 81.2, 18000.0, 7200.0, 8500.0, List.of(), false
        );

        when(adminIntelligenceService.getPlatformFleetOverview()).thenReturn(fleet);

        mockMvc.perform(get("/admin/intelligence/fleet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalDevices").value(500));
    }
}
