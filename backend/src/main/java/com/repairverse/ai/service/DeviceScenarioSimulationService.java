package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceScenario;
import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceScenarioSimulationRequest;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceDecisionScenario;
import com.repairverse.ai.repository.DeviceDecisionScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceScenarioSimulationService {

    private final DeviceDecisionScenarioRepository scenarioRepository;

    @Transactional
    public List<DeviceScenario> generateAndSaveScenarios(
            Device device,
            String userId,
            int healthScore,
            int failureRisk,
            double baselineRepairCost,
            double baselineReplacementPrice,
            double baselineCo2
    ) {
        List<DeviceScenario> scenarios = generateScenarios(
                device, healthScore, failureRisk, baselineRepairCost, baselineReplacementPrice, baselineCo2
        );

        // Persist scenarios
        try {
            scenarioRepository.deleteByDeviceIdAndUserId(device.getId(), userId);
            for (DeviceScenario s : scenarios) {
                DeviceDecisionScenario entity = DeviceDecisionScenario.builder()
                        .deviceId(device.getId())
                        .userId(userId)
                        .scenarioType(s.scenarioType())
                        .estimatedCost(s.estimatedCost())
                        .estimatedLifespanMonths(s.estimatedLifespanMonths())
                        .estimatedCo2Impact(s.estimatedCo2Impact())
                        .estimatedSavings(s.estimatedSavings())
                        .intelligenceScore(s.intelligenceScore())
                        .recommendation(s.recommendation())
                        .createdAt(LocalDateTime.now())
                        .build();
                scenarioRepository.save(entity);
            }
        } catch (Exception e) {
            log.warn("Failed to persist decision scenarios for device {}: {}", device.getId(), e.getMessage());
        }

        return scenarios;
    }

    public List<DeviceScenario> generateScenarios(
            Device device,
            int healthScore,
            int failureRisk,
            double repairCost,
            double replacementPrice,
            double baselineCo2
    ) {
        List<DeviceScenario> scenarios = new ArrayList<>();

        double repPrice = replacementPrice > 0 ? replacementPrice : 800.0;
        double rCost = repairCost > 0 ? repairCost : 120.0;
        double co2 = baselineCo2 > 0 ? baselineCo2 : 15.0;

        // 1. CONTINUE_USING
        scenarios.add(new DeviceScenario(
                "CONTINUE_USING",
                "Continue Regular Operation",
                0.0,
                healthScore >= 70 ? 12 : 3,
                0.0,
                0.0,
                healthScore >= 70 ? 88 : 45,
                healthScore >= 70 ? "Recommended for healthy devices with low immediate risk." : "High risk of sudden degradation without intervention.",
                List.of("Zero immediate out-of-pocket expense", "Maintains current workflow without downtime"),
                List.of("Does not address underlying wear or latent risks", "Unaddressed faults may compound in cost")
        ));

        // 2. MAINTENANCE
        double maintCost = Math.min(35.0, rCost * 0.3);
        scenarios.add(new DeviceScenario(
                "MAINTENANCE",
                "Preventative Deep Maintenance",
                maintCost,
                18,
                co2 * 0.4,
                repPrice * 0.25,
                78,
                "Extends hardware stability and cools operating thermal thresholds.",
                List.of("Inexpensive preventive upkeep ($" + Math.round(maintCost) + ")", "Restores thermal headroom and prevents dust/corrosion"),
                List.of("Cannot resolve existing hardware breakage")
        ));

        // 3. REPAIR
        double repSavings = Math.max(0, repPrice - rCost);
        scenarios.add(new DeviceScenario(
                "REPAIR",
                "Component-Level DIY / Standard Repair",
                rCost,
                24,
                co2,
                repSavings,
                86,
                "Ideal balance of cost savings, performance restoration, and circular impact.",
                List.of("Restores device to peak functional health", "Saves ~$" + Math.round(repSavings) + " compared to new purchase", "Prevents " + String.format("%.1f", co2) + " kg of lifecycle CO2"),
                List.of("Requires sourcing quality parts and standard repair labor")
        ));

        // 4. PROFESSIONAL_SERVICE
        double proCost = rCost * 1.35;
        scenarios.add(new DeviceScenario(
                "PROFESSIONAL_SERVICE",
                "Certified Technician Diagnostic & Repair",
                proCost,
                30,
                co2 * 1.1,
                Math.max(0, repPrice - proCost),
                92,
                "Guaranteed repair with certified warranty and multi-point safety validation.",
                List.of("Includes professional warranty & certified genuine parts", "Comprehensive safety testing & calibration"),
                List.of("Higher labor cost than DIY repair ($" + Math.round(proCost) + ")")
        ));

        // 5. REFURBISH
        double refurbCost = rCost * 1.6;
        scenarios.add(new DeviceScenario(
                "REFURBISH",
                "Complete Refurbish & Component Refresh",
                refurbCost,
                36,
                co2 * 1.3,
                Math.max(0, repPrice - refurbCost),
                80,
                "Breathes multi-year lifespan into mature hardware through battery & storage upgrades.",
                List.of("Maximizes hardware longevity (+36 months)", "High ROI for premium legacy hardware"),
                List.of("Larger upfront investment than single part repair")
        ));

        // 6. REPLACE
        scenarios.add(new DeviceScenario(
                "REPLACE",
                "Replace with New Device",
                repPrice,
                48,
                -co2 * 2.5,
                0.0,
                healthScore < 40 && rCost > repPrice * 0.65 ? 75 : 55,
                healthScore < 40 ? "Reasonable choice when compounding hardware damage exceeds residual value." : "Costly and environmentally carbon-intensive when device is repairable.",
                List.of("Access to newest generation speed, display, and features", "Fresh manufacturer warranty"),
                List.of("Highest out-of-pocket expense ($" + Math.round(repPrice) + ")", "Generates significant manufacturing carbon footprint")
        ));

        // 7. RECYCLE
        scenarios.add(new DeviceScenario(
                "RECYCLE",
                "Responsible E-Waste Recycling",
                0.0,
                0,
                co2 * 0.6,
                0.0,
                healthScore < 30 ? 70 : 40,
                "Ensures circular reclamation of copper, gold, and rare-earth materials.",
                List.of("Zero toxic chemicals in landfills", "Precious metal reclamation", "Responsible circular closure"),
                List.of("Permanently retires the device from utility")
        ));

        return scenarios;
    }

    public List<DeviceScenario> simulateCustomScenario(
            Device device,
            int healthScore,
            int failureRisk,
            double baselineRepairCost,
            double baselineReplacementPrice,
            double baselineCo2,
            DeviceScenarioSimulationRequest request
    ) {
        List<DeviceScenario> baseScenarios = generateScenarios(
                device, healthScore, failureRisk, baselineRepairCost, baselineReplacementPrice, baselineCo2
        );

        if (request == null) {
            return baseScenarios;
        }

        // Apply custom budget / target lifespan filters if requested
        List<DeviceScenario> customList = new ArrayList<>();
        for (DeviceScenario s : baseScenarios) {
            double cost = s.estimatedCost();
            int lifespan = s.estimatedLifespanMonths();
            int score = s.intelligenceScore();

            if (request.customBudget() != null && request.customBudget() > 0) {
                if (cost <= request.customBudget()) {
                    score += 5; // Bonus score for meeting user budget constraint
                } else {
                    score -= 10;
                }
            }

            if (request.targetLifespanMonths() != null && request.targetLifespanMonths() > 0) {
                if (lifespan >= request.targetLifespanMonths()) {
                    score += 5;
                }
            }

            if (Boolean.TRUE.equals(request.prioritizeSustainability()) && s.estimatedCo2Impact() > 0) {
                score += 8;
            }

            int finalScore = Math.max(0, Math.min(100, score));

            customList.add(new DeviceScenario(
                    s.scenarioType(),
                    s.title(),
                    cost,
                    lifespan,
                    s.estimatedCo2Impact(),
                    s.estimatedSavings(),
                    finalScore,
                    s.recommendation(),
                    s.pros(),
                    s.cons()
            ));
        }

        return customList;
    }
}
