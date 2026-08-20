package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CarbonDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.CarbonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarbonController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CarbonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarbonService carbonService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/carbon - Successful retrieval of carbon dashboard")
    void getCarbonDashboard_Success() throws Exception {
        UserPrincipal principal = new UserPrincipal("usr-123", "User", "user@example.com", "pass", Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        CarbonDashboardData data = CarbonService.getSampleDashboardData(false);
        CarbonDashboardResponse response = new CarbonDashboardResponse(true, "Carbon data retrieved", data);

        when(carbonService.getCarbonDashboard("usr-123")).thenReturn(response);

        mockMvc.perform(get("/carbon").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.impact.co2Saved").value(142.8))
                .andExpect(jsonPath("$.data.sustainabilityScore").value(88));
    }
}
