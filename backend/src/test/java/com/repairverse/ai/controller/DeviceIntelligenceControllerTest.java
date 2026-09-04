package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DeviceIntelligenceDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.DeviceDecisionIntelligenceService;
import com.repairverse.ai.service.DeviceIntelligenceAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DeviceIntelligenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class DeviceIntelligenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceDecisionIntelligenceService intelligenceService;

    @MockBean
    private DeviceIntelligenceAlertService alertService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private UserPrincipal createMockPrincipal(String userId) {
        return new UserPrincipal(userId, "Test User", "test@repairverse.ai", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/device-intelligence/{deviceId} returns 200 and evaluation response")
    void testGetDeviceIntelligence() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        DeviceIntelligenceResponse mockResponse = new DeviceIntelligenceResponse(
                "dev-1", "MacBook Pro", "laptop", "Apple", "M2",
                85, "HEALTHY", "CONTINUE_USING", 90,
                "Device operating optimally", "2026-09-04T10:00:00",
                new IntelligenceScoreBreakdown(85, 80, 80, 90, 80, 75, 90),
                List.of(),
                new SmartDecision("CONTINUE_USING", "LOW", "Continue Normal Use", "No action needed", 0.0, "Optimal health"),
                List.of(),
                List.of()
        );

        when(intelligenceService.evaluateDeviceIntelligence(eq("dev-1"), eq("usr-1"), eq(false)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/device-intelligence/dev-1")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("dev-1"))
                .andExpect(jsonPath("$.data.intelligenceScore").value(85));
    }

    @Test
    @DisplayName("POST /api/v1/device-intelligence/{deviceId}/evaluate triggers re-evaluation")
    void testTriggerEvaluation() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        DeviceIntelligenceResponse mockResponse = new DeviceIntelligenceResponse(
                "dev-1", "MacBook Pro", "laptop", "Apple", "M2",
                85, "HEALTHY", "CONTINUE_USING", 90,
                "Device evaluated", "2026-09-04T10:00:00",
                null, List.of(), null, List.of(), List.of()
        );

        when(intelligenceService.evaluateDeviceIntelligence(eq("dev-1"), eq("usr-1"), anyBoolean()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/device-intelligence/dev-1/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"forceReevaluation\": true}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deviceId").value("dev-1"));
    }

    @Test
    @DisplayName("GET /api/v1/device-intelligence/alerts returns user alerts")
    void testGetUserAlerts() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        DeviceIntelligenceAlertResponse alert = new DeviceIntelligenceAlertResponse(
                "alt-1", "dev-1", "MacBook Pro", "FAILURE_RISK", "HIGH",
                "Component Risk", "Check thermal paste", "MAINTENANCE_REQUIRED", false, "2026-09-04T10:00:00"
        );

        when(alertService.getUserAlerts("usr-1")).thenReturn(List.of(alert));

        mockMvc.perform(get("/device-intelligence/alerts")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].alertType").value("FAILURE_RISK"));
    }

    @Test
    @DisplayName("PUT /api/v1/device-intelligence/alerts/{alertId}/read marks alert read")
    void testMarkAlertAsRead() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        DeviceIntelligenceAlertResponse alert = new DeviceIntelligenceAlertResponse(
                "alt-1", "dev-1", "MacBook Pro", "FAILURE_RISK", "HIGH",
                "Component Risk", "Check thermal paste", "MAINTENANCE_REQUIRED", true, "2026-09-04T10:00:00"
        );

        when(alertService.markAlertAsRead("alt-1", "usr-1")).thenReturn(alert);

        mockMvc.perform(put("/device-intelligence/alerts/alt-1/read")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isRead").value(true));
    }
}
