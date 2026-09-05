package com.repairverse.ai.service;

import com.repairverse.ai.entity.RepairKnowledgeRelationship;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.entity.RepairPatternInsight;
import com.repairverse.ai.repository.RepairKnowledgeRelationshipRepository;
import com.repairverse.ai.repository.RepairPatternInsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 35: Learning Knowledge Graph Integration Service.
 * Propagates validated federated learning signals into the Repair Knowledge Graph,
 * updating edge strengths and pattern discovery baselines deterministically.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningKnowledgeGraphIntegrationService {

    private final RepairKnowledgeRelationshipRepository relationshipRepository;
    private final RepairPatternInsightRepository patternInsightRepository;

    /**
     * Apply validated learning signals to update knowledge graph relationship strengths.
     */
    @Transactional
    public int applyLearningToKnowledgeGraph(List<RepairLearningSignal> signals) {
        if (signals == null || signals.isEmpty()) {
            return 0;
        }

        int updatedCount = 0;

        for (RepairLearningSignal signal : signals) {
            // Find relationships matching the action
            List<RepairKnowledgeRelationship> relationships = relationshipRepository
                    .findAllByRelationshipType("RESOLVED_BY");

            for (RepairKnowledgeRelationship rel : relationships) {
                // Update relationship strength bounded formula: (current * 0.75) + (newSignal * 0.25)
                double currentStrength = rel.getStrength() != null ? rel.getStrength() : 50.0;
                double signalStrength = signal.getSuccessRate() * 100.0;
                double newStrength = (currentStrength * 0.75) + (signalStrength * 0.25);

                rel.setStrength(Math.round(newStrength * 10.0) / 10.0);
                rel.setObservationCount(rel.getObservationCount() + signal.getObservationCount());
                rel.setLastObservedAt(LocalDateTime.now());
                relationshipRepository.save(rel);
                updatedCount++;
            }

            // Sync high-confidence patterns into pattern insights
            if (signal.getSuccessRate() >= 0.85 && signal.getObservationCount() >= 10) {
                createOrUpdatePatternInsight(signal);
            }
        }

        log.info("Knowledge Graph enriched with {} updated relationships from federated learning.", updatedCount);
        return updatedCount;
    }

    private void createOrUpdatePatternInsight(RepairLearningSignal signal) {
        String title = String.format("Federated Pattern: %s on %s",
                signal.getRepairAction().replace("_", " "),
                signal.getDeviceCategory());

        RepairPatternInsight insight = RepairPatternInsight.builder()
                .insightType("HIGH_SUCCESS_REPAIR")
                .title(title)
                .description(String.format("Validated across %d ecosystem observations with %.1f%% repair success fidelity.",
                        signal.getObservationCount(), signal.getSuccessRate() * 100.0))
                .confidence(signal.getConfidence())
                .impactScore((int) (signal.getSuccessRate() * 100))
                .supportingObservations(signal.getObservationCount())
                .deviceCategory(signal.getDeviceCategory())
                .status("ACTIVE")
                .build();

        patternInsightRepository.save(insight);
    }
}
