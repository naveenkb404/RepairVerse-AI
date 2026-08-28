package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AiExplanationDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AiExplanationService;
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

@WebMvcTest(controllers = AiExplanationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AiExplanationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiExplanationService aiExplanationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/ai-intelligence/device-prediction/{deviceId} - Returns prediction explanation")
    void explainDevicePrediction_Success() throws Exception {
        DeviceRiskExplanationResponse res = new DeviceRiskExplanationResponse(
                "dev-1", "iPhone 14", 82, "LOW", "Operating well", "Normal wear",
                List.of(), List.of(), "Saves money", "Low priority", List.of(), List.of(),
                "Deterministic Engine", false, "2026-08-23"
        );

        when(aiExplanationService.explainDevicePrediction("dev-1", "usr-1")).thenReturn(res);

        mockMvc.perform(get("/ai-intelligence/device-prediction/dev-1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("dev-1"))
                .andExpect(jsonPath("$.data.predictionScore").value(82));
    }

    @Test
    @DisplayName("GET /api/v1/ai-intelligence/diagnosis/{diagnosisId} - Returns diagnosis explanation")
    void explainDiagnosis_Success() throws Exception {
        DiagnosisExplanationResponse res = new DiagnosisExplanationResponse(
                "diag-1", "iPhone 14", "Display Glitch", 89,
                "Visual evidence matches defect", "Symptoms correlate directly",
                List.of(), "Feasible repair", List.of(), "Disconnect battery",
                "Deterministic Engine", false, "2026-08-23"
        );

        when(aiExplanationService.explainDiagnosis("diag-1", "usr-1")).thenReturn(res);

        mockMvc.perform(get("/ai-intelligence/diagnosis/diag-1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.diagnosisId").value("diag-1"));
    }

    @Test
    @DisplayName("GET /api/v1/ai-intelligence/recommendation/{recommendationId} - Returns recommendation explanation")
    void explainRecommendation_Success() throws Exception {
        RecommendationExplanationResponse res = new RecommendationExplanationResponse(
                "rec-1", "iPhone 14", "REPAIR", 90.0, 800.0,
                "Cost effective", "Adds 2 years", "Saves carbon", "High residual value",
                List.of(), "Deterministic Engine", false, "2026-08-23"
        );

        when(aiExplanationService.explainRecommendation("rec-1", "usr-1")).thenReturn(res);

        mockMvc.perform(get("/ai-intelligence/recommendation/rec-1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recommendationId").value("rec-1"));
    }

    @Test
    @DisplayName("GET /api/v1/ai-intelligence/sustainability - Returns sustainability narrative")
    void explainSustainability_Success() throws Exception {
        SustainabilityNarrativeResponse res = new SustainabilityNarrativeResponse(
                "usr-1", 120.0, 5.0, 1500.0, 3,
                "Impact headline", "Compelling narrative", "3 trees",
                List.of(), "Future milestone", "Deterministic Engine", false, "2026-08-23"
        );

        when(aiExplanationService.explainSustainabilityImpact("usr-1")).thenReturn(res);

        mockMvc.perform(get("/ai-intelligence/sustainability")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCo2SavedKg").value(120.0));
    }
}
