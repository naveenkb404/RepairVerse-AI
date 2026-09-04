package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.KnowledgeGraphResponse;
import com.repairverse.ai.dto.RepairKnowledgeGraphDto.KnowledgeGraphStatisticsResponse;
import com.repairverse.ai.entity.RepairKnowledgeNode;
import com.repairverse.ai.entity.RepairKnowledgeRelationship;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairKnowledgeGraphServiceTest {

    @Mock
    private RepairKnowledgeNodeRepository nodeRepository;

    @Mock
    private RepairKnowledgeRelationshipRepository relationshipRepository;

    @Mock
    private RepairPatternInsightRepository insightRepository;

    @Mock
    private RepairKnowledgeFeedbackRepository feedbackRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private RepairKnowledgeGraphService graphService;

    private RepairKnowledgeNode nodeA;
    private RepairKnowledgeNode nodeB;
    private RepairKnowledgeRelationship sampleRel;

    @BeforeEach
    void setUp() {
        nodeA = RepairKnowledgeNode.builder()
                .id("node-1")
                .nodeType("DEVICE_MODEL")
                .nodeKey("MODEL:MACBOOK_PRO")
                .displayName("MacBook Pro")
                .confidenceScore(0.95)
                .observationCount(10)
                .build();

        nodeB = RepairKnowledgeNode.builder()
                .id("node-2")
                .nodeType("COMPONENT")
                .nodeKey("COMP:BATTERY")
                .displayName("Battery")
                .confidenceScore(0.90)
                .observationCount(8)
                .build();

        sampleRel = RepairKnowledgeRelationship.builder()
                .id("rel-1")
                .sourceNodeId("node-1")
                .targetNodeId("node-2")
                .relationshipType("HAS_COMPONENT")
                .strength(85.0)
                .confidence(0.90)
                .observationCount(5)
                .firstObservedAt(LocalDateTime.now().minusDays(10))
                .lastObservedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deterministic relationship strength calculation normalizes to 0-100")
    void testCalculateRelationshipStrength() {
        double strength = graphService.calculateRelationshipStrength(
                10, 95.0, LocalDateTime.now(), 0.90, 85.0
        );

        assertThat(strength).isBetween(0.0, 100.0);
        assertThat(strength).isGreaterThan(70.0);
    }

    @Test
    @DisplayName("Get knowledge graph returns nodes, relationships, and stats")
    void testGetKnowledgeGraph() {
        when(nodeRepository.count()).thenReturn(2L);
        when(relationshipRepository.count()).thenReturn(1L);
        when(nodeRepository.findAll()).thenReturn(List.of(nodeA, nodeB));
        when(relationshipRepository.findAll()).thenReturn(List.of(sampleRel));
        when(insightRepository.count()).thenReturn(5L);

        KnowledgeGraphResponse response = graphService.getKnowledgeGraph();

        assertThat(response).isNotNull();
        assertThat(response.nodes()).hasSize(2);
        assertThat(response.relationships()).hasSize(1);
        assertThat(response.statistics().totalNodes()).isEqualTo(2L);
        assertThat(response.statistics().totalRelationships()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Record or update relationship strengthens existing relationship")
    void testRecordOrUpdateRelationship() {
        when(relationshipRepository.findBySourceNodeIdAndTargetNodeIdAndRelationshipType("node-1", "node-2", "HAS_COMPONENT"))
                .thenReturn(Optional.of(sampleRel));
        when(relationshipRepository.save(any(RepairKnowledgeRelationship.class)))
                .thenAnswer(i -> i.getArgument(0));

        RepairKnowledgeRelationship result = graphService.recordOrUpdateRelationship(
                nodeA, nodeB, "HAS_COMPONENT", 95.0, 90.0
        );

        assertThat(result.getObservationCount()).isEqualTo(6);
        verify(relationshipRepository, times(1)).save(sampleRel);
    }
}
