package com.repairverse.ai.controller;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.FaultPatternService;
import com.repairverse.ai.service.MaintenanceRecommendationService;
import com.repairverse.ai.service.PredictiveScoringService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PredictiveController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class PredictiveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PredictiveScoringService scoringService;

    @MockBean
    private MaintenanceRecommendationService recommendationService;

    @MockBean
    private FaultPatternService faultPatternService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/predictive/devices/{id} - Evaluates device prediction")
    void evaluateDevice_Success() throws Exception {
        DevicePredictionResponse res = new DevicePredictionResponse(
                "dev-1", "iPhone 14", "Smartphone", "Apple",
                82, "LOW", 365, "General Wear",
                List.of("Clean ports"), List.of(), 60.0, 24.0, 10.0, 0.85, false, "2026-08-23T10:00:00"
        );

        when(scoringService.evaluateDevice("dev-1", "usr-1")).thenReturn(res);

        mockMvc.perform(get("/predictive/devices/dev-1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.predictionScore").value(82))
                .andExpect(jsonPath("$.data.riskLevel").value("LOW"));
    }

    @Test
    @DisplayName("GET /api/v1/predictive/fleet - Returns user fleet predictions")
    void getUserFleet_Success() throws Exception {
        when(scoringService.getUserFleet("usr-1")).thenReturn(List.of());

        mockMvc.perform(get("/predictive/fleet")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/predictive/recommendations - Returns fleet recommendations")
    void getRecommendations_Success() throws Exception {
        when(recommendationService.getRecommendationsForUser("usr-1")).thenReturn(List.of());

        mockMvc.perform(get("/predictive/recommendations")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/predictive/fault-patterns - Returns fault patterns")
    void getFaultPatterns_Success() throws Exception {
        when(faultPatternService.getActivePatterns()).thenReturn(List.of());

        mockMvc.perform(get("/predictive/fault-patterns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
