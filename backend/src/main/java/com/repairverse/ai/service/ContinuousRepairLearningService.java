package com.repairverse.ai.service;

import com.repairverse.ai.dto.FederatedLearningDto.LearningRunResponse;
import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.FederatedLearningBatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Phase 35: Continuous Repair Learning Orchestration Engine.
 * Manages the end-to-end federated learning cycle:
 * Collection → Privacy Filtering → Signal Extraction → Model Candidate → Validation → Governance → Activation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContinuousRepairLearningService {

    private final PrivacyPreservationService privacyService;
    private final RepairLearningSignalService signalService;
    private final LearningModelVersionService modelVersionService;
    private final LearningValidationService validationService;
    private final LearningKnowledgeGraphIntegrationService kgIntegrationService;
    private final FederatedLearningBatchRepository batchRepository;

    /**
     * Trigger a continuous federated learning cycle across ecosystem outcomes.
     */
    @Transactional
    public LearningRunResponse runLearningCycle(List<Map<String, Object>> incomingRawOutcomes) {
        log.info("Starting Federated Learning Cycle...");

        // 1. Resolve raw outcomes or provide simulated batch
        List<Map<String, Object>> outcomes = incomingRawOutcomes != null && !incomingRawOutcomes.isEmpty()
                ? incomingRawOutcomes
                : generateMockOutcomes();

        String batchRef = "BATCH-" + System.currentTimeMillis();
        IntelligenceModelVersion activeModel = modelVersionService.getActiveModel();

        // 2. Create Learning Batch record
        FederatedLearningBatch batch = FederatedLearningBatch.builder()
                .batchReference(batchRef)
                .sourceScope("ECOSYSTEM_GLOBAL")
                .anonymizedDeviceCount(outcomes.size() / 2 + 5)
                .anonymizedRepairCount(outcomes.size())
                .status("COLLECTING")
                .privacyLevel("STRICT")
                .validationScore(0.0)
                .modelVersion(activeModel != null ? activeModel.getVersion() : "R35.4")
                .build();
        batch = batchRepository.save(batch);

        // 3. Privacy Preservation & PII Scrubbing
        var privacyResult = privacyService.processBatchPrivacy(batch, outcomes);
        if (privacyResult.decision() == PrivacyPreservationService.PrivacyDecision.QUARANTINED) {
            batch.setStatus("QUARANTINED");
            batchRepository.save(batch);
            return new LearningRunResponse(
                    false,
                    "Batch quarantined: insufficient observation count for strict privacy guarantee.",
                    batchRef,
                    batch.getModelVersion(),
                    privacyResult.recordsProcessed(),
                    0.0,
                    false,
                    "AWAIT_MORE_OBSERVATIONS"
            );
        }

        // 4. Extract Bounded Learning Signals
        List<RepairLearningSignal> signals = signalService.extractSignals(batch, privacyResult.sanitizedRecords());
        batch.setStatus("AGGREGATED");
        batchRepository.save(batch);

        // 5. Create Candidate Model Version
        IntelligenceModelVersion candidate = modelVersionService.createCandidateVersion(outcomes.size());

        // 6. Multi-dimensional Validation & Regression Detection
        var validationSummary = validationService.validateCandidate(candidate, activeModel, signals);

        batch.setValidationScore(validationSummary.validationScore());
        batch.setModelVersion(candidate.getVersion());
        batch.setStatus(validationSummary.approved() ? "APPROVED" : "REJECTED");
        batchRepository.save(batch);

        // 7. Knowledge Graph Integration if approved
        if (validationSummary.approved()) {
            kgIntegrationService.applyLearningToKnowledgeGraph(signals);
        }

        log.info("Federated Learning Cycle finished for batch '{}'. Candidate '{}' approved: {}",
                batchRef, candidate.getVersion(), validationSummary.approved());

        return new LearningRunResponse(
                true,
                validationSummary.approved()
                        ? "Continuous learning cycle completed successfully. Candidate model approved."
                        : "Learning cycle evaluated. Candidate model rejected due to regression or insufficient confidence.",
                batchRef,
                candidate.getVersion(),
                privacyResult.recordsProcessed(),
                validationSummary.validationScore(),
                validationSummary.approved(),
                validationSummary.approved() ? "READY_FOR_ACTIVATION" : "REJECTED_GOVERNANCE"
        );
    }

    private List<Map<String, Object>> generateMockOutcomes() {
        List<Map<String, Object>> mock = new ArrayList<>();
        String[] components = {"BATTERY", "SCREEN_OLED", "THERMAL_SYSTEM", "CHARGING_PORT", "LOGIC_BOARD"};
        String[] actions = {"REPLACE_BATTERY", "REPLACE_DISPLAY", "THERMAL_PASTE_CLEAN", "PORT_MICRO_SOLDER", "SUB_SYSTEM_OVERHAUL"};
        String[] categories = {"SMARTPHONE", "LAPTOP", "TABLET"};

        for (int i = 0; i < 25; i++) {
            Map<String, Object> r = new HashMap<>();
            r.put("deviceId", "dev-uuid-" + (i % 6));
            r.put("userId", "usr-uuid-" + (i % 4));
            r.put("category", categories[i % categories.length]);
            r.put("component", components[i % components.length]);
            r.put("failureMode", "ELECTROCHEMICAL_DEGRADATION");
            r.put("action", actions[i % actions.length]);
            r.put("successful", i % 7 != 0); // 85%+ success
            r.put("cost", 1800.0 + (i * 120.0));
            r.put("lifespanMonths", 24 + (i % 12));
            r.put("sustainabilityScore", 88.0 + (i % 8));
            r.put("email", "leak_test_" + i + "@user.com"); // Will be stripped by privacy filter
            r.put("serialNumber", "SN-9988-X" + i); // Will be stripped by privacy filter
            mock.add(r);
        }
        return mock;
    }
}
