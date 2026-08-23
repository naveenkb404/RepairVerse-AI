package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DashboardDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.DashboardService;
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

@WebMvcTest(controllers = DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/dashboard - Returns aggregated dashboard stats")
    void getDashboardStats_Success() throws Exception {
        DashboardStatsResponse stats = new DashboardStatsResponse(4, 9, 47.3, 1240.0, 84, 1);

        when(dashboardService.getDashboardStats("usr-123")).thenReturn(stats);

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalDevices").value(4))
                .andExpect(jsonPath("$.data.totalRepairs").value(9))
                .andExpect(jsonPath("$.data.healthScore").value(84));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/activity - Returns activity stream")
    void getActivityFeed_Success() throws Exception {
        ActivityItemResponse act = new ActivityItemResponse(
                "act-1", "repair_complete", "Screen Replaced", "Done", "2026-08-20T10:00:00", "iPhone", "green"
        );

        when(dashboardService.getActivityFeed("usr-123")).thenReturn(List.of(act));

        mockMvc.perform(get("/dashboard/activity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("act-1"))
                .andExpect(jsonPath("$.data[0].type").value("repair_complete"));
    }
}
