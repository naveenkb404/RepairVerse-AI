package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairJourneyService;
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

@WebMvcTest(controllers = RepairJourneyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairJourneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairJourneyService repairJourneyService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/repair-journey/device/{deviceId} returns 200 with 9-stage journey timeline")
    void testGetRepairJourney() throws Exception {
        RepairJourneyResponse mockRes = new RepairJourneyResponse(
            "dev-123",
            "Apple iPhone 14 Pro",
            "ACTION_PLAN_READY",
            4,
            9,
            55,
            List.of(),
            "Book a certified local repair shop.",
            LocalDateTime.now()
        );

        when(repairJourneyService.getRepairJourney("dev-123", "usr-1")).thenReturn(mockRes);

        mockMvc.perform(get("/repair-journey/device/dev-123")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.currentStage").value("ACTION_PLAN_READY"))
            .andExpect(jsonPath("$.data.progressPercentage").value(55));
    }
}
