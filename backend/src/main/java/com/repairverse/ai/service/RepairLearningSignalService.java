package com.repairverse.ai.service;

import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.RepairLearningSignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Phase 35: Repair Learning Signal Engine.
 * Extracts bounded, deterministic learning signals using weighted moving averages
 * across component types, failure modes, and repair actions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairLearningSignalService {

    private final RepairLearningSignalRepository signalRepository;
    private final PrivacyPreservationService privacyService;

    // Weight coefficients for bounded learning
    public static final double HISTORICAL_WEIGHT = 0.80;
    public static final double NEW_EVIDENCE_WEIGHT = 0.20;
    public static final double MAX_RATE_DELTA = 0.15; // Maximum 15% rate shift per batch

    /**
     * Compute bounded success rate combining prior rate and newly observed rate.
     */
    public double computeBoundedRate(double priorRate, double newObservationRate) {
        double weighted = (priorRate * HISTORICAL_WEIGHT) + (newObservationRate * NEW_EVIDENCE_WEIGHT);
        // Constrain delta within [-MAX_RATE_DELTA, +MAX_RATE_DELTA]
        double delta = weighted - priorRate;
        if (delta > MAX_RATE_DELTA) {
            return priorRate + MAX_RATE_DELTA;
        } else if (delta < -MAX_RATE_DELTA) {
            return priorRate - MAX_RATE_DELTA;
        }
        return Math.min(Math.max(weighted, 0.0), 1.0);
    }

    /**
     * Synthesize and persist learning signals from sanitized batch records.
     */
    @Transactional
    public List<RepairLearningSignal> extractSignals(
            FederatedLearningBatch batch,
            List<Map<String, Object>> sanitizedOutcomes) {

        if (sanitizedOutcomes == null || sanitizedOutcomes.isEmpty()) {
            return Collections.emptyList();
        }

        // Group by Composite Key: Category + Component + FailureMode + Action
        Map<String, List<Map<String, Object>>> groups = new HashMap<>();

        for (Map<String, Object> record : sanitizedOutcomes) {
            String category = String.valueOf(record.getOrDefault("category", "SMARTPHONE"));
            String component = String.valueOf(record.getOrDefault("component", "BATTERY"));
            String failureMode = String.valueOf(record.getOrDefault("failureMode", "ELECTROCHEMICAL_DEGRADATION"));
            String action = String.valueOf(record.getOrDefault("action", "REPLACE_BATTERY"));

            String key = String.format("%s|%s|%s|%s", category, component, failureMode, action);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        List<RepairLearningSignal> signals = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, Object>>> entry : groups.entrySet()) {
            String[] parts = entry.getKey().split("\\|");
            String category = parts[0];
            String component = parts[1];
            String failureMode = parts[2];
            String action = parts[3];

            List<Map<String, Object>> records = entry.getValue();
            int observationCount = records.size();

            // Enforce minimum aggregation threshold
            if (!privacyService.isSafeToExposeSignal(observationCount)) {
                log.debug("Quarantining signal key '{}' due to low observation count ({})", entry.getKey(), observationCount);
                continue;
            }

            // Calculate aggregate statistics
            long successCount = records.stream()
                    .filter(r -> Boolean.TRUE.equals(r.get("successful")) || "SUCCESS".equalsIgnoreCase(String.valueOf(r.get("status"))))
                    .count();
            double observedSuccessRate = observationCount > 0 ? (double) successCount / observationCount : 0.85;

            double avgCost = records.stream()
                    .mapToDouble(r -> ((Number) r.getOrDefault("cost", 3500.0)).doubleValue())
                    .average().orElse(3500.0);

            int avgLifespan = (int) records.stream()
                    .mapToInt(r -> ((Number) r.getOrDefault("lifespanMonths", 24)).intValue())
                    .average().orElse(24);

            double avgSustainability = records.stream()
                    .mapToDouble(r -> ((Number) r.getOrDefault("sustainabilityScore", 85.0)).doubleValue())
                    .average().orElse(85.0);

            double boundedSuccessRate = computeBoundedRate(0.85, observedSuccessRate);
            double confidence = Math.min(0.70 + (observationCount * 0.02), 0.98);

            RepairLearningSignal signal = RepairLearningSignal.builder()
                    .batch(batch)
                    .signalType("REPAIR_OUTCOME_LEARNING")
                    .deviceCategory(category)
                    .componentType(component)
                    .failureMode(failureMode)
                    .repairAction(action)
                    .outcomeClass(boundedSuccessRate >= 0.80 ? "HIGH_SUCCESS_REPAIR" : "MODERATE_SUCCESS_REPAIR")
                    .aggregatedFrequency(observationCount)
                    .successRate(Math.round(boundedSuccessRate * 100.0) / 100.0)
                    .averageCost(Math.round(avgCost * 10.0) / 10.0)
                    .averageLifespanGain(avgLifespan)
                    .sustainabilityScore(Math.round(avgSustainability * 10.0) / 10.0)
                    .confidence(Math.round(confidence * 100.0) / 100.0)
                    .observationCount(observationCount)
                    .build();

            signals.add(signal);
        }

        return signalRepository.saveAll(signals);
    }
}
