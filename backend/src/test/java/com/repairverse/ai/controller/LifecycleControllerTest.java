package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DeviceLifecycleService;
import com.repairverse.ai.service.RepairDelayImpactService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = LifecycleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class LifecycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceLifecycleService lifecycleService;

    @MockBean
    private RepairDelayImpactService delayImpactService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/lifecycle/device/{deviceId} returns 200 with lifecycle assessment")
    void testGetLifecycleAssessment() throws Exception {
        DeviceLifecycleAssessmentResponse mockRes = new DeviceLifecycleAssessmentResponse(
            "dev-123",
            "MacBook Pro",
            "Laptop",
            24,
            12,
            24,
            36,
            24,
            85,
            "MEDIUM",
            42.5,
            2.1,
            List.of(),
            LocalDateTime.now()
        );

        when(lifecycleService.getLifecycleAssessment("dev-123", "usr-1")).thenReturn(mockRes);

        mockMvc.perform(get("/lifecycle/device/dev-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.repairabilityScore").value(85))
            .andExpect(jsonPath("$.data.lifecycleExtensionPotentialMonths").value(24));
    }

    @Test
    @DisplayName("GET /api/v1/lifecycle/device/{deviceId}/delay-impact returns 200 with simulation projections")
    void testGetDelayImpact() throws Exception {
        DelayImpactResponse mockRes = new DelayImpactResponse(
            "dev-123",
            "MacBook Pro",
            120.0,
            "MEDIUM",
            "Battery Degraded",
            List.of(),
            "Servicing advised within 14 days.",
            LocalDateTime.now()
        );

        when(delayImpactService.simulateDelayImpact("dev-123", "usr-1")).thenReturn(mockRes);

        mockMvc.perform(get("/lifecycle/device/dev-123/delay-impact")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.baselineRepairCost").value(120.0));
    }
}
