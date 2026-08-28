package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AdminDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.AdminIntelligenceService;
import com.repairverse.ai.service.AdminService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private AdminIntelligenceService adminIntelligenceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/admin/users - Returns user list")
    void getAllUsers_Success() throws Exception {
        AdminUserSummary u = new AdminUserSummary("usr-1", "Jane", "jane@example.com", "USER", true, "2024-01-01", 2);
        when(adminService.getAllUsers()).thenReturn(List.of(u));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].email").value("jane@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/admin/analytics - Returns analytics")
    void getAnalytics_Success() throws Exception {
        AdminAnalyticsResponse analytics = new AdminAnalyticsResponse(100, 200, 300, 400, 50, 1000.0, 50000.0);
        when(adminService.getAnalytics()).thenReturn(analytics);

        mockMvc.perform(get("/admin/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(100));
    }

    @Test
    @DisplayName("GET /api/v1/admin/reports - Returns report list")
    void getReports_Success() throws Exception {
        AdminReportSummary r = new AdminReportSummary("rep-1", "Report A", "Sustainability", "2026-08-01", "READY", "/url");
        when(adminService.getReports()).thenReturn(List.of(r));

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value("rep-1"));
    }

    @Test
    @DisplayName("DELETE /api/v1/admin/users/{id} - Deletes user")
    void deleteUser_Success() throws Exception {
        doNothing().when(adminService).deleteUser("usr-1");

        mockMvc.perform(delete("/admin/users/usr-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
