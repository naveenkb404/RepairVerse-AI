package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairPlanningService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairPlanningController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairPlanningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairPlanningService repairPlanningService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private RepairActionPlanResponse mockPlanResponse() {
        return new RepairActionPlanResponse(
            "plan-1",
            "usr-1",
            "dev-123",
            "iPhone 14 Pro",
            "Smartphone",
            "PREVENTIVE_MAINTENANCE",
            "MEDIUM",
            45.0,
            14,
            5.8,
            0.15,
            "ACTIVE",
            "Proactive servicing required.",
            List.of(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/v1/repair-planning/device/{deviceId} returns 200 with action plan")
    void testGetDeviceActionPlan() throws Exception {
        when(repairPlanningService.getOrCreateActionPlan("dev-123", "usr-1"))
            .thenReturn(mockPlanResponse());

        mockMvc.perform(get("/repair-planning/device/dev-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.overallStrategy").value("PREVENTIVE_MAINTENANCE"))
            .andExpect(jsonPath("$.data.estimatedLifecycleExtensionMonths").value(14));
    }

    @Test
    @DisplayName("POST /api/v1/repair-planning/device/{deviceId}/refresh returns 200 with recalculated plan")
    void testRefreshActionPlan() throws Exception {
        when(repairPlanningService.refreshActionPlan("dev-123", "usr-1"))
            .thenReturn(mockPlanResponse());

        mockMvc.perform(post("/repair-planning/device/dev-123/refresh")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.deviceId").value("dev-123"));
    }

    @Test
    @DisplayName("GET /api/v1/repair-planning returns 200 with list of user action plans")
    void testGetUserActionPlans() throws Exception {
        when(repairPlanningService.getUserActionPlans("usr-1"))
            .thenReturn(List.of(mockPlanResponse()));

        mockMvc.perform(get("/repair-planning")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.count").value(1))
            .andExpect(jsonPath("$.data[0].overallStrategy").value("PREVENTIVE_MAINTENANCE"));
    }
}
