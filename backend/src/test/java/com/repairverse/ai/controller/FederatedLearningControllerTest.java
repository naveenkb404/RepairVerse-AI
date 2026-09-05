package com.repairverse.ai.controller;

import com.repairverse.ai.dto.FederatedLearningDto.*;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FederatedLearningControllerTest {

    @Mock
    private FederatedLearningDashboardService dashboardService;
    @Mock
    private ContinuousRepairLearningService continuousLearningService;
    @Mock
    private LearningModelVersionService modelVersionService;
    @Mock
    private LearningValidationService validationService;
    @Mock
    private LearningDecisionIntelligenceService decisionIntelligenceService;
    @Mock
    private LearningImpactService impactService;
    @Mock
    private LearningFeedbackService feedbackService;

    private FederatedLearningController controller;

    @BeforeEach
    void setUp() {
        controller = new FederatedLearningController(
                dashboardService,
                continuousLearningService,
                modelVersionService,
                validationService,
                decisionIntelligenceService,
                impactService,
                feedbackService
        );
    }

    @Test
    @DisplayName("GET /api/v1/learning/dashboard should return dashboard payload")
    void testGetDashboard() {
        LearningDashboardResponse mockDash = new LearningDashboardResponse(
                "R35.4", "RepairVerse Federated Core", 94.2, 94, 8.7,
                248, 1284, 18, 73, 100.0, "2026-09-05T12:00:00",
                null, List.of(), List.of(), List.of(), null
        );

        when(dashboardService.getDashboard()).thenReturn(mockDash);

        ResponseEntity<Map<String, Object>> response = controller.getDashboard(null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals(mockDash, response.getBody().get("data"));
    }

    @Test
    @DisplayName("POST /api/v1/learning/run should trigger learning cycle")
    void testTriggerLearningCycle() {
        LearningRunResponse runResp = new LearningRunResponse(
                true, "Learning cycle complete", "BATCH-001", "R35.5", 25, 95.0, true, "READY"
        );

        when(continuousLearningService.runLearningCycle(any())).thenReturn(runResp);

        ResponseEntity<Map<String, Object>> response = controller.triggerLearningCycle(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue((Boolean) response.getBody().get("success"));
        assertEquals(runResp, response.getBody().get("data"));
    }
}
