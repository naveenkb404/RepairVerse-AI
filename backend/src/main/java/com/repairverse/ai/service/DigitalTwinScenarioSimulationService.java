package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.RunSimulationRequest;
import com.repairverse.ai.dto.DigitalTwinDto.ScenarioResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinScenario;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DigitalTwinScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalTwinScenarioSimulationService {

    private final DigitalTwinScenarioRepository scenarioRepository;

    @Transactional
    public List<ScenarioResponse> simulateAndSaveScenarios(String userId, Device device, DigitalTwinSnapshot snapshot) {
        List<DigitalTwinScenario> scenarios = simulateAllScenarios(snapshot);
        return scenarios.stream().map(this::mapToScenarioResponse).collect(Collectors.toList());
    }

    /**
     * Simulates 8 standard alternative future strategies for a snapshot.
     */
    @Transactional
    public List<DigitalTwinScenario> simulateAllScenarios(DigitalTwinSnapshot snapshot) {
        String deviceId = snapshot.getDeviceId();
        String userId = snapshot.getUserId();

        List<DigitalTwinScenario> scenarios = List.of(
                createScenario(userId, deviceId, "REPAIR_NOW", "Execute Immediate Component Repair",
                        96, 5, 2400.0, 18500.0, 36, 14.8, 0.2, 1, 96, 0.94),

                createScenario(userId, deviceId, "PREVENTIVE_MAINTENANCE", "Perform Scheduled Preventive Service",
                        92, 10, 1200.0, 6800.0, 28, 8.5, 0.1, 0, 92, 0.92),

                createScenario(userId, deviceId, "PROFESSIONAL_SERVICE", "Certified Service Center Overhaul",
                        98, 4, 3800.0, 15500.0, 36, 13.5, 0.2, 2, 93, 0.95),

                createScenario(userId, deviceId, "REFURBISH_DEVICE", "Full Sub-System Refurbishment",
                        94, 8, 4500.0, 12000.0, 30, 18.2, 0.3, 3, 88, 0.90),

                createScenario(userId, deviceId, "CONTINUE_CURRENT_USAGE", "Continue Current Usage (No Action)",
                        62, 48, 0.0, 0.0, 12, 2.0, 0.0, 0, 56, 0.88),

                createScenario(userId, deviceId, "DELAY_REPAIR", "Defer Repair by 6 Months",
                        44, 72, 4800.0, -2500.0, 8, 5.0, 0.8, 4, 38, 0.85),

                createScenario(userId, deviceId, "REPLACE_DEVICE", "Procure Brand New Replacement Unit",
                        100, 2, 65000.0, -15000.0, 48, -48.0, 2.2, 1, 70, 0.96),

                createScenario(userId, deviceId, "RECYCLE_DEVICE", "Responsible Certified E-Waste Recycling",
                        0, 0, 0.0, 1200.0, 0, 22.5, 2.4, 0, 64, 0.98)
        );

        // Delete prior simulated scenarios for this device
        scenarioRepository.deleteByDeviceId(deviceId);

        List<DigitalTwinScenario> saved = scenarioRepository.saveAll(scenarios);
        log.info("Simulated {} future scenarios for device '{}'", saved.size(), deviceId);
        return saved;
    }

    /**
     * Run custom simulation with user-defined constraints (budget, target lifespan, eco priority).
     */
    @Transactional
    public List<DigitalTwinScenario> runCustomSimulation(DigitalTwinSnapshot snapshot, RunSimulationRequest request) {
        List<DigitalTwinScenario> baseScenarios = simulateAllScenarios(snapshot);
        List<DigitalTwinScenario> tailored = new ArrayList<>();

        double budget = request.budget() != null ? request.budget() : 10000.0;
        int targetLifespan = request.targetLifespanMonths() != null ? request.targetLifespanMonths() : 24;
        boolean ecoPriority = Boolean.TRUE.equals(request.prioritizeSustainability());
        boolean reliabilityPriority = Boolean.TRUE.equals(request.prioritizeReliability());

        for (DigitalTwinScenario s : baseScenarios) {
            int score = s.getOverallOutcomeScore();

            // Constraint adjustments
            if (s.getProjectedCost() > budget) {
                score -= 30; // penalize over-budget
            }
            if (s.getProjectedLifespanMonths() < targetLifespan) {
                score -= 15; // penalize insufficient lifespan
            }
            if (ecoPriority && s.getProjectedCo2Impact() > 10.0) {
                score += 8; // bonus for sustainability
            }
            if (reliabilityPriority && s.getProjectedHealthScore() >= 95) {
                score += 8; // bonus for high reliability
            }

            s.setOverallOutcomeScore(Math.min(100, Math.max(10, score)));
            tailored.add(s);
        }

        return scenarioRepository.saveAll(tailored);
    }

    private DigitalTwinScenario createScenario(
            String userId, String deviceId, String type, String name,
            int health, int risk, double cost, double savings, int lifespan,
            double co2, double eWaste, int downtime, int outcomeScore, double confidence) {

        return DigitalTwinScenario.builder()
                .userId(userId)
                .deviceId(deviceId)
                .scenarioType(type)
                .scenarioName(name)
                .projectedHealthScore(health)
                .projectedFailureRisk(risk)
                .projectedCost(cost)
                .projectedSavings(savings)
                .projectedLifespanMonths(lifespan)
                .projectedCo2Impact(co2)
                .projectedEWasteImpact(eWaste)
                .downtimeDays(downtime)
                .overallOutcomeScore(outcomeScore)
                .simulationConfidence(confidence)
                .build();
    }

    public ScenarioResponse mapToScenarioResponse(DigitalTwinScenario s) {
        return new ScenarioResponse(
                s.getId(),
                s.getDeviceId(),
                s.getScenarioType(),
                s.getScenarioName(),
                s.getProjectedHealthScore(),
                s.getProjectedFailureRisk(),
                s.getProjectedCost(),
                s.getProjectedSavings(),
                s.getProjectedLifespanMonths(),
                s.getProjectedCo2Impact(),
                s.getProjectedEWasteImpact(),
                s.getDowntimeDays(),
                s.getOverallOutcomeScore(),
                s.getSimulationConfidence()
        );
    }
}
