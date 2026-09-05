package com.repairverse.ai.service;

import com.repairverse.ai.entity.RepairKnowledgeRelationship;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.entity.RepairPatternInsight;
import com.repairverse.ai.repository.RepairKnowledgeRelationshipRepository;
import com.repairverse.ai.repository.RepairPatternInsightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningKnowledgeGraphIntegrationServiceTest {

    @Mock
    private RepairKnowledgeRelationshipRepository relationshipRepository;

    @Mock
    private RepairPatternInsightRepository patternInsightRepository;

    private LearningKnowledgeGraphIntegrationService kgIntegrationService;

    @BeforeEach
    void setUp() {
        kgIntegrationService = new LearningKnowledgeGraphIntegrationService(relationshipRepository, patternInsightRepository);
    }

    @Test
    @DisplayName("Should apply learning signals and update relationship strengths in Knowledge Graph")
    void testApplyLearningToKnowledgeGraph() {
        RepairKnowledgeRelationship rel = RepairKnowledgeRelationship.builder()
                .relationshipType("RESOLVED_BY")
                .strength(60.0)
                .observationCount(10)
                .build();

        RepairLearningSignal signal = RepairLearningSignal.builder()
                .repairAction("REPLACE_BATTERY")
                .deviceCategory("SMARTPHONE")
                .successRate(0.90) // 90%
                .confidence(0.92)
                .observationCount(15)
                .build();

        when(relationshipRepository.findAllByRelationshipType("RESOLVED_BY")).thenReturn(List.of(rel));

        int updated = kgIntegrationService.applyLearningToKnowledgeGraph(List.of(signal));

        assertEquals(1, updated);
        // Formula: 60.0 * 0.75 + 90.0 * 0.25 = 45.0 + 22.5 = 67.5
        assertEquals(67.5, rel.getStrength(), 0.001);
        assertEquals(25, rel.getObservationCount());
        verify(patternInsightRepository, times(1)).save(any(RepairPatternInsight.class));
    }
}
