package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.OptimizationRequest;
import com.repairverse.ai.dto.DigitalTwinDto.OptimizationResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinOptimizationResult;
import com.repairverse.ai.entity.DigitalTwinScenario;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DigitalTwinOptimizationResultRepository;
import com.repairverse.ai.repository.DigitalTwinScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairStrategyOptimizationService {

    private final DigitalTwinOptimizationResultRepository optimizationResultRepository;
    private final DigitalTwinScenarioRepository scenarioRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transactional
    public OptimizationResponse optimizeAndSaveStrategy(
            String userId, Device device, DigitalTwinSnapshot snapshot, OptimizationRequest request) {

        List<DigitalTwinScenario> scenarios = scenarioRepository.findByDeviceId(snapshot.getDeviceId());
        if (scenarios.isEmpty()) {
            // Fallback default simulated scenario
            scenarios = List.of(DigitalTwinScenario.builder()
                    .userId(userId)
                    .deviceId(snapshot.getDeviceId())
                    .scenarioType("PREVENTIVE_MAINTENANCE")
                    .scenarioName("Scheduled Preventive Maintenance")
                    .projectedHealthScore(92)
                    .projectedFailureRisk(10)
                    .projectedCost(1200.0)
                    .projectedSavings(6800.0)
                    .projectedLifespanMonths(28)
                    .projectedCo2Impact(8.5)
                    .projectedEWasteImpact(0.1)
                    .downtimeDays(0)
                    .overallOutcomeScore(92)
                    .simulationConfidence(0.92)
                    .build());
        }

        optimizationResultRepository.deleteByDeviceId(snapshot.getDeviceId());

        DigitalTwinOptimizationResult result = optimizeBestStrategy(snapshot, scenarios);
        return mapToOptimizationResponse(result);
    }

    /**
     * Evaluates and optimizes the best strategy from simulated scenarios.
     * Uses 6-factor deterministic model:
     * Financial Efficiency (25%) + Reliability (20%) + Longevity (15%) + Risk Reduction (15%) + Sustainability (15%) + Downtime (10%)
     */
    @Transactional
    public DigitalTwinOptimizationResult optimizeBestStrategy(
            DigitalTwinSnapshot snapshot, List<DigitalTwinScenario> scenarios) {

        DigitalTwinScenario winningScenario = scenarios.stream()
                .max(Comparator.comparingDouble(this::calculateOptimizationScore))
                .orElse(scenarios.get(0));

        int costScore = calculateFinancialScore(winningScenario.getProjectedCost(), winningScenario.getProjectedSavings());
        int reliabilityScore = winningScenario.getProjectedHealthScore();
        int longevityScore = Math.min(100, winningScenario.getProjectedLifespanMonths() * 3);
        int riskReductionScore = 100 - winningScenario.getProjectedFailureRisk();
        int sustainabilityScore = calculateSustainabilityScore(winningScenario.getProjectedCo2Impact());
        int downtimeScore = Math.max(10, 100 - (winningScenario.getDowntimeDays() * 20));

        int optScore = (int) Math.round(
                (costScore * 0.25) +
                (reliabilityScore * 0.20) +
                (longevityScore * 0.15) +
                (riskReductionScore * 0.15) +
                (sustainabilityScore * 0.15) +
                (downtimeScore * 0.10)
        );

        String decisionReason = generateDecisionReason(winningScenario, optScore);

        DigitalTwinOptimizationResult result = DigitalTwinOptimizationResult.builder()
                .userId(snapshot.getUserId())
                .deviceId(snapshot.getDeviceId())
                .recommendedStrategy(winningScenario.getScenarioType())
                .costScore(costScore)
                .reliabilityScore(reliabilityScore)
                .longevityScore(longevityScore)
                .sustainabilityScore(sustainabilityScore)
                .optimizationScore(optScore)
                .estimatedSavings(winningScenario.getProjectedSavings())
                .estimatedLifespanGain(winningScenario.getProjectedLifespanMonths())
                .estimatedCo2Savings(winningScenario.getProjectedCo2Impact())
                .decisionReason(decisionReason)
                .build();

        DigitalTwinOptimizationResult saved = optimizationResultRepository.save(result);
        log.info("Optimized best strategy '{}' (Score: {}) for device '{}'", saved.getRecommendedStrategy(), optScore, snapshot.getDeviceId());
        return saved;
    }

    public double calculateOptimizationScore(DigitalTwinScenario s) {
        int costScore = calculateFinancialScore(s.getProjectedCost(), s.getProjectedSavings());
        int reliabilityScore = s.getProjectedHealthScore();
        int longevityScore = Math.min(100, s.getProjectedLifespanMonths() * 3);
        int riskReductionScore = 100 - s.getProjectedFailureRisk();
        int sustainabilityScore = calculateSustainabilityScore(s.getProjectedCo2Impact());
        int downtimeScore = Math.max(10, 100 - (s.getDowntimeDays() * 20));

        return (costScore * 0.25) +
                (reliabilityScore * 0.20) +
                (longevityScore * 0.15) +
                (riskReductionScore * 0.15) +
                (sustainabilityScore * 0.15) +
                (downtimeScore * 0.10);
    }

    private int calculateFinancialScore(double cost, double savings) {
        if (savings > 5000) return 98;
        if (savings > 2000) return 92;
        if (savings > 0) return 85;
        if (cost < 2000) return 75;
        if (cost < 8000) return 60;
        return 40;
    }

    private int calculateSustainabilityScore(double co2SavedKg) {
        if (co2SavedKg >= 14.0) return 98;
        if (co2SavedKg >= 8.0) return 90;
        if (co2SavedKg > 0.0) return 80;
        return 45;
    }

    private String generateDecisionReason(DigitalTwinScenario s, int score) {
        return switch (s.getScenarioType()) {
            case "REPAIR_NOW" -> "Executing immediate repair resolves critical failure risk while preserving maximum asset value and preventing ₹" + Math.round(s.getProjectedSavings()) + " in replacement capital.";
            case "PREVENTIVE_MAINTENANCE" -> "Preventive servicing delivers the optimal balance of minimal upfront cost (₹" + Math.round(s.getProjectedCost()) + ") and extends device lifecycle by " + s.getProjectedLifespanMonths() + " months.";
            case "PROFESSIONAL_SERVICE" -> "Professional overhaul guarantees 98% operating reliability with certified workmanship and OEM warranty coverage.";
            case "REFURBISH_DEVICE" -> "Comprehensive refurbishment revitalizes all major sub-systems for heavy continuous duty cycles.";
            case "REPLACE_DEVICE" -> "Device replacement is optimal when structural damage renders repair costs uneconomical.";
            case "RECYCLE_DEVICE" -> "Certified recycling recovers critical raw materials with zero toxic landfill disposal.";
            default -> "Current operating parameters remain stable for immediate utilization.";
        };
    }

    public OptimizationResponse mapToOptimizationResponse(DigitalTwinOptimizationResult r) {
        return new OptimizationResponse(
                r.getId(),
                r.getDeviceId(),
                r.getRecommendedStrategy(),
                r.getCostScore(),
                r.getReliabilityScore(),
                r.getLongevityScore(),
                r.getSustainabilityScore(),
                r.getOptimizationScore(),
                r.getEstimatedSavings(),
                r.getEstimatedLifespanGain(),
                r.getEstimatedCo2Savings(),
                r.getDecisionReason(),
                r.getCreatedAt() != null ? r.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
        );
    }
}
