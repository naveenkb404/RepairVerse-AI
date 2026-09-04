package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AutonomousRepairAgentDto.*;
import com.repairverse.ai.entity.AutonomousActionStep;
import com.repairverse.ai.entity.AutonomousIntervention;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AgentApprovalService;
import com.repairverse.ai.service.AgentExecutionService;
import com.repairverse.ai.service.AutonomousRepairAgentService;
import com.repairverse.ai.service.ProactiveInterventionService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AutonomousRepairAgentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AutonomousRepairAgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutonomousRepairAgentService agentService;

    @MockBean
    private ProactiveInterventionService proactiveInterventionService;

    @MockBean
    private AgentApprovalService approvalService;

    @MockBean
    private AgentExecutionService executionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private UserPrincipal createMockPrincipal(String userId) {
        return new UserPrincipal(userId, "Test User", "test@repairverse.ai", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/repair-agent/dashboard returns 200 and dashboard response")
    void testGetDashboard() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        AgentDashboardResponse mockDashboard = new AgentDashboardResponse(
                "ACTIVE", 3, 2, 1, 5, 450.0, 32.0,
                List.of(), List.of(), List.of(), Map.of("HIGH", 2)
        );

        when(agentService.getAgentDashboard("usr-1")).thenReturn(mockDashboard);

        mockMvc.perform(get("/repair-agent/dashboard")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.agentStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.data.monitoredDevicesCount").value(3));
    }

    @Test
    @DisplayName("POST /api/v1/repair-agent/devices/{deviceId}/evaluate triggers proactive evaluation")
    void testEvaluateDevice() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        AutonomousIntervention intervention = AutonomousIntervention.builder()
                .id("int-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .interventionType("URGENT_REPAIR")
                .status("DETECTED")
                .title("Urgent Repair")
                .createdAt(LocalDateTime.now())
                .build();

        InterventionResponse response = new InterventionResponse(
                "int-1", "usr-1", "dev-1", "MacBook Pro", "laptop",
                "URGENT_REPAIR", "HIGH", "DETECTED", "Urgent Repair",
                "Description", "Reason", 90, 85, 120.0, 500.0, 36.0,
                "REPAIR_NOW", true, null, "2026-09-04T12:00:00", null
        );

        when(proactiveInterventionService.evaluateDevice("dev-1", "usr-1")).thenReturn(intervention);
        when(agentService.getInterventionDetails("int-1", "usr-1")).thenReturn(response);

        mockMvc.perform(post("/repair-agent/devices/dev-1/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("int-1"))
                .andExpect(jsonPath("$.data.interventionType").value("URGENT_REPAIR"));
    }

    @Test
    @DisplayName("POST /api/v1/repair-agent/actions/{actionId}/approve approves action step")
    void testApproveAction() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        AutonomousActionStep step = AutonomousActionStep.builder()
                .id("step-1")
                .planId("plan-1")
                .stepOrder(1)
                .actionType("REQUEST_QUOTE")
                .title("Request Quote")
                .status("APPROVED")
                .build();

        when(approvalService.approveAction(eq("step-1"), eq("usr-1"), any())).thenReturn(step);

        mockMvc.perform(post("/repair-agent/actions/step-1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notes\": \"Looks good\"}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }

    @Test
    @DisplayName("POST /api/v1/repair-agent/actions/{actionId}/execute executes action step")
    void testExecuteAction() throws Exception {
        UserPrincipal principal = createMockPrincipal("usr-1");

        ExecutionResultResponse result = new ExecutionResultResponse(
                "step-1", "COMPLETED", "Action executed", "hist-1", "2026-09-04T12:00:00"
        );

        when(executionService.executeAction(eq("step-1"), eq("usr-1"), any())).thenReturn(result);

        mockMvc.perform(post("/repair-agent/actions/step-1/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"parameters\": {}}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}
