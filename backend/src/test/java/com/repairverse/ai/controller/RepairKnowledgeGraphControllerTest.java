package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.*;
import com.repairverse.ai.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairKnowledgeGraphControllerTest {

    @Mock
    private RepairKnowledgeGraphService graphService;

    @Mock
    private RepairPatternDiscoveryService patternDiscoveryService;

    @Mock
    private SimilarRepairCaseService similarCaseService;

    @Mock
    private KnowledgeDrivenRecommendationService recommendationService;

    @Mock
    private RepairKnowledgeFeedbackService feedbackService;

    @InjectMocks
    private RepairKnowledgeGraphController controller;

    @Test
    @DisplayName("GET /graph returns knowledge graph response")
    void testGetKnowledgeGraph() {
        KnowledgeGraphStatisticsResponse stats = new KnowledgeGraphStatisticsResponse(
                10L, 15L, 5L, 250L, 0.92, Map.of(), Map.of()
        );
        KnowledgeGraphResponse mockGraph = new KnowledgeGraphResponse(
                List.of(), List.of(), stats, "2026-09-04T12:00:00"
        );
        when(graphService.getKnowledgeGraph()).thenReturn(mockGraph);

        ResponseEntity<Map<String, Object>> response = controller.getKnowledgeGraph();

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("success")).isEqualTo(true);
        verify(graphService, times(1)).getKnowledgeGraph();
    }

    @Test
    @DisplayName("GET /insights returns active ecosystem insights")
    void testGetInsights() {
        PatternInsightResponse insight = new PatternInsightResponse(
                "ins-1", "COMMON_FAILURE", "Battery Failure", "Description",
                0.95, 88, 200, "LAPTOP", "ACTIVE", "2026-09-04T12:00:00", 10L, 0L
        );
        when(patternDiscoveryService.getActiveInsights("COMMON_FAILURE", null))
                .thenReturn(List.of(insight));

        ResponseEntity<Map<String, Object>> response = controller.getInsights("COMMON_FAILURE", null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }

    @Test
    @DisplayName("GET /devices/{deviceId}/similar-cases returns similar repair cases")
    void testGetSimilarCases() {
        SimilarRepairCaseResponse mockCase = new SimilarRepairCaseResponse(
                "case-1", 94.5, "LAPTOP", "MacBook Pro", "Battery drain",
                "Battery Pack", "OEM Replacement", "FULLY_RESOLVED", "$160 - $190",
                14.5, 1, "Lesson learned"
        );
        when(similarCaseService.findSimilarCasesForDevice("dev-1", "usr-1"))
                .thenReturn(List.of(mockCase));

        ResponseEntity<Map<String, Object>> response = controller.getSimilarCases("dev-1", null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("count")).isEqualTo(1);
    }
}
