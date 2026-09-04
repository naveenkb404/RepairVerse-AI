package com.repairverse.ai.controller;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.CircularImpactScoreService;
import com.repairverse.ai.service.CircularImpactService;
import com.repairverse.ai.service.SustainabilityAchievementService;
import com.repairverse.ai.service.SustainabilityGoalService;
import com.repairverse.ai.service.SustainabilityOptimizationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CircularEconomyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class CircularEconomyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CircularImpactService circularImpactService;

    @MockBean
    private CircularImpactScoreService scoreService;

    @MockBean
    private SustainabilityOptimizationService optimizationService;

    @MockBean
    private SustainabilityGoalService goalService;

    @MockBean
    private SustainabilityAchievementService achievementService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/circular-economy/dashboard — returns aggregated dashboard payload")
    void testGetDashboard() throws Exception {
        CircularImpactMetricsDto metrics = new CircularImpactMetricsDto(
            142.8, 4.85, 12500.0, 540, 6L, 4L, 1L, 1L, 12L
        );
        CircularImpactScoreDto score = new CircularImpactScoreDto(
            85, "ECO_LEADER",
            new CircularFactorBreakdownDto(25, 20, 18, 12, 10, 85),
            List.of("Strong lifecycle extension"),
            List.of("Set a new sustainability goal"),
            "Schedule maintenance",
            LocalDateTime.now()
        );

        when(circularImpactService.getUserImpactMetrics("usr-1")).thenReturn(metrics);
        when(scoreService.calculateScore("usr-1")).thenReturn(score);
        when(goalService.getUserGoals("usr-1")).thenReturn(List.of());
        when(achievementService.evaluateAchievements("usr-1")).thenReturn(List.of());
        when(optimizationService.getRecommendations("usr-1", null)).thenReturn(List.of());
        when(circularImpactService.getUserTimeline("usr-1")).thenReturn(List.of());

        mockMvc.perform(get("/circular-economy/dashboard")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.impactScore.score").value(85))
            .andExpect(jsonPath("$.data.impactScore.tier").value("ECO_LEADER"))
            .andExpect(jsonPath("$.data.impactMetrics.totalCarbonSavedKg").value(142.8));
    }

    @Test
    @DisplayName("GET /api/v1/circular-economy/impact — returns user circular metrics")
    void testGetImpactMetrics() throws Exception {
        CircularImpactMetricsDto metrics = new CircularImpactMetricsDto(
            85.0, 3.2, 7500.0, 365, 3L, 2L, 0L, 0L, 5L
        );
        when(circularImpactService.getUserImpactMetrics("usr-1")).thenReturn(metrics);

        mockMvc.perform(get("/circular-economy/impact")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalCarbonSavedKg").value(85.0));
    }

    @Test
    @DisplayName("GET /api/v1/circular-economy/score — returns circular score calculation")
    void testGetScore() throws Exception {
        CircularImpactScoreDto score = new CircularImpactScoreDto(
            92, "CIRCULAR_CHAMPION", null, List.of("Strength"), List.of(), "Action", LocalDateTime.now()
        );
        when(scoreService.calculateScore("usr-1")).thenReturn(score);

        mockMvc.perform(get("/circular-economy/score")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.score").value(92))
            .andExpect(jsonPath("$.data.tier").value("CIRCULAR_CHAMPION"));
    }

    @Test
    @DisplayName("POST /api/v1/circular-economy/goals — creates sustainability goal")
    void testCreateGoal() throws Exception {
        SustainabilityGoalDto created = new SustainabilityGoalDto(
            "sg-1", "usr-1", "CARBON_REDUCTION", 100.0, 0.0, 0, 100.0,
            LocalDateTime.now(), LocalDateTime.now().plusMonths(6), "ACTIVE", false
        );

        when(goalService.createGoal(eq("usr-1"), any(CreateGoalRequest.class))).thenReturn(created);

        mockMvc.perform(post("/circular-economy/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"goalType\": \"CARBON_REDUCTION\", \"targetValue\": 100.0}")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("sg-1"))
            .andExpect(jsonPath("$.data.goalType").value("CARBON_REDUCTION"));
    }

    @Test
    @DisplayName("POST /api/v1/circular-economy/events — records impact event")
    void testRecordEvent() throws Exception {
        CircularImpactEventDto eventDto = new CircularImpactEventDto(
            "cie-1", "usr-1", "dev-1", "MacBook", "REPAIR_COMPLETED",
            LocalDateTime.now(), 64.0, 2.1, 4500.0, 365, "USER_ACTION", null
        );

        when(circularImpactService.recordImpactEvent(eq("usr-1"), any(RecordImpactEventRequest.class))).thenReturn(eventDto);

        mockMvc.perform(post("/circular-economy/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"deviceId\": \"dev-1\", \"eventType\": \"REPAIR_COMPLETED\", \"carbonSavedKg\": 64.0, \"ewastePreventedKg\": 2.1, \"moneySaved\": 4500.0, \"deviceLifeExtensionDays\": 365}")
                .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.eventType").value("REPAIR_COMPLETED"));
    }
}
