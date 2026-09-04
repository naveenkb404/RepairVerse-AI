package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceDecisionIntelligenceService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final CircularImpactEventRepository circularImpactEventRepository;
    private final DeviceDecisionSnapshotRepository snapshotRepository;
    private final DeviceDecisionScenarioRepository scenarioRepository;
    private final DeviceIntelligenceScoringService scoringService;
    private final PersonalizedDeviceAdvisorService advisorService;
    private final DeviceScenarioSimulationService scenarioSimulationService;
    private final DeviceIntelligenceAlertService alertService;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Baseline category lifespans in months
    private static final Map<String, Integer> CATEGORY_LIFESPAN_MONTHS = Map.of(
            "smartphone", 48,
            "laptop", 72,
            "tablet", 60,
            "gaming console", 84,
            "smartwatch", 36,
            "audio device", 48,
            "other", 48
    );

    // Baseline replacement prices
    private static final Map<String, Double> CATEGORY_REPLACEMENT_PRICE = Map.of(
            "smartphone", 800.0,
            "laptop", 1200.0,
            "tablet", 550.0,
            "gaming console", 500.0,
            "smartwatch", 350.0,
            "audio device", 200.0,
            "other", 400.0
    );

    @Transactional
    public DeviceIntelligenceResponse evaluateDeviceIntelligence(String deviceId, String userId, boolean forceReevaluation) {
        log.info("Evaluating device decision intelligence for device '{}' (user: '{}', force: {})", deviceId, userId, forceReevaluation);

        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        DeviceHealth health = deviceHealthRepository.findByDeviceId(deviceId)
                .orElseGet(() -> DeviceHealth.builder()
                        .deviceId(device.getId())
                        .healthScore(80)
                        .batteryHealth(90)
                        .aiPrediction("Standard operating parameters.")
                        .build());

        List<DiagnosisReport> diagnoses = diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
        List<RepairHistory> repairHistory = repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc(deviceId);
        List<CircularImpactEvent> circularEvents = circularImpactEventRepository.findByDeviceIdOrderByEventDateDesc(deviceId);

        // 1. Calculate raw component signals
        int healthScore = health.getHealthScore() != null ? health.getHealthScore() : 80;
        int batteryHealth = health.getBatteryHealth() != null ? health.getBatteryHealth() : 90;

        // Health & Reliability Score (0-100)
        int healthReliabilityScore = (int) Math.round((healthScore * 0.7) + (batteryHealth * 0.3));

        // Predictive failure risk calculation (0-100 hazard)
        int failureRisk = calculateFailureRisk(device, health, diagnoses, repairHistory);
        int failureRiskResilience = Math.max(0, 100 - failureRisk);

        // Economics calculation
        double replacementPrice = device.getPurchasePrice() != null && device.getPurchasePrice() > 0
                ? device.getPurchasePrice()
                : CATEGORY_REPLACEMENT_PRICE.getOrDefault(device.getCategory() != null ? device.getCategory().toLowerCase() : "other", 500.0);

        double estimatedRepairCost = estimateRepairCost(diagnoses, replacementPrice);
        int repairEconomicsScore = calculateEconomicScore(estimatedRepairCost, replacementPrice);

        // Maintenance status score
        int maintenanceScore = calculateMaintenanceScore(health);

        // Longevity score
        int longevityScore = calculateLongevityScore(device);

        // Sustainability score
        int sustainabilityScore = calculateSustainabilityScore(circularEvents, healthScore);

        // Repair history score
        int repairHistoryScore = calculateRepairHistoryScore(repairHistory);

        // 2. Compute 7-factor weighted score
        DeviceIntelligenceScoringService.ScoringResult scoringResult = scoringService.calculateScore(
                healthReliabilityScore,
                failureRiskResilience,
                repairEconomicsScore,
                maintenanceScore,
                longevityScore,
                sustainabilityScore,
                repairHistoryScore
        );

        int overallScore = scoringResult.overallScore();
        String tier = scoringResult.tier();

        // 3. Determine single authoritative recommended action
        String recommendedAction = determineRecommendedAction(
                healthScore,
                failureRisk,
                repairEconomicsScore,
                maintenanceScore,
                longevityScore,
                estimatedRepairCost,
                replacementPrice,
                diagnoses
        );

        int confidence = calculateConfidence(diagnoses, repairHistory, health);

        // 4. Personalized narrative
        double co2Saved = estimateCo2Saved(device);
        PersonalizedDeviceAdvisorService.AdvisorNarrative narrative = advisorService.generateNarrative(
                device,
                recommendedAction,
                overallScore,
                tier,
                healthScore,
                failureRisk,
                estimatedRepairCost,
                replacementPrice,
                co2Saved
        );

        // 5. Generate and persist decision scenarios
        List<DeviceScenario> scenarios = scenarioSimulationService.generateAndSaveScenarios(
                device,
                userId,
                healthScore,
                failureRisk,
                estimatedRepairCost,
                replacementPrice,
                co2Saved
        );

        // 6. Generate alerts & fetch active
        alertService.evaluateAndGenerateAlerts(
                device,
                userId,
                healthScore,
                failureRisk,
                recommendedAction,
                maintenanceScore,
                repairEconomicsScore
        );
        List<DeviceIntelligenceAlertResponse> activeAlerts = alertService.getDeviceAlerts(deviceId, userId);

        // 7. Persist snapshot
        DeviceDecisionSnapshot snapshot = DeviceDecisionSnapshot.builder()
                .deviceId(deviceId)
                .userId(userId)
                .intelligenceScore(overallScore)
                .recommendedAction(recommendedAction)
                .decisionConfidence(confidence)
                .healthScore(healthScore)
                .failureRiskScore(failureRisk)
                .economicScore(repairEconomicsScore)
                .maintenanceScore(maintenanceScore)
                .longevityScore(longevityScore)
                .sustainabilityScore(sustainabilityScore)
                .repairHistoryScore(repairHistoryScore)
                .explanationSummary(narrative.summary())
                .createdAt(LocalDateTime.now())
                .build();
        snapshotRepository.save(snapshot);

        return new DeviceIntelligenceResponse(
                device.getId(),
                device.getDeviceName(),
                device.getCategory(),
                device.getBrand(),
                device.getModel(),
                overallScore,
                tier,
                recommendedAction,
                confidence,
                narrative.summary(),
                LocalDateTime.now().format(ISO_FORMATTER),
                scoringResult.breakdown(),
                scoringResult.decisionFactors(),
                narrative.smartDecision(),
                scenarios,
                activeAlerts
        );
    }

    @Transactional(readOnly = true)
    public List<DeviceDecisionSnapshotResponse> getDeviceHistory(String deviceId, String userId) {
        deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        return snapshotRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId)
                .stream()
                .map(s -> new DeviceDecisionSnapshotResponse(
                        s.getId(),
                        s.getDeviceId(),
                        s.getIntelligenceScore(),
                        s.getRecommendedAction(),
                        s.getDecisionConfidence(),
                        s.getHealthScore(),
                        s.getFailureRiskScore(),
                        s.getEconomicScore(),
                        s.getExplanationSummary(),
                        s.getCreatedAt() != null ? s.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceScenario> getDeviceScenarios(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        List<DeviceDecisionScenario> stored = scenarioRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        if (!stored.isEmpty()) {
            return stored.stream()
                    .map(s -> new DeviceScenario(
                            s.getScenarioType(),
                            formatScenarioTitle(s.getScenarioType()),
                            s.getEstimatedCost(),
                            s.getEstimatedLifespanMonths(),
                            s.getEstimatedCo2Impact(),
                            s.getEstimatedSavings(),
                            s.getIntelligenceScore(),
                            s.getRecommendation(),
                            List.of("Calculated for " + device.getDeviceName(), "Deterministic model estimate"),
                            List.of("Subject to part availability and service rates")
                    ))
                    .toList();
        }

        // Generate on demand if empty
        return evaluateDeviceIntelligence(deviceId, userId, false).scenarios();
    }

    @Transactional(readOnly = true)
    public List<DeviceScenario> simulateScenario(String deviceId, String userId, DeviceScenarioSimulationRequest request) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        DeviceHealth health = deviceHealthRepository.findByDeviceId(deviceId)
                .orElseGet(() -> DeviceHealth.builder().healthScore(80).build());

        List<DiagnosisReport> diagnoses = diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
        List<RepairHistory> repairHistory = repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc(deviceId);

        int healthScore = health.getHealthScore() != null ? health.getHealthScore() : 80;
        int failureRisk = calculateFailureRisk(device, health, diagnoses, repairHistory);
        double replacementPrice = device.getPurchasePrice() != null && device.getPurchasePrice() > 0 ? device.getPurchasePrice() : 800.0;
        double repairCost = estimateRepairCost(diagnoses, replacementPrice);
        double co2 = estimateCo2Saved(device);

        return scenarioSimulationService.simulateCustomScenario(
                device, healthScore, failureRisk, repairCost, replacementPrice, co2, request
        );
    }

    @Transactional(readOnly = true)
    public List<DeviceIntelligenceTimelineItem> getDeviceTimeline(String deviceId, String userId) {
        deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        List<DeviceIntelligenceTimelineItem> timeline = new ArrayList<>();

        // Snapshots
        List<DeviceDecisionSnapshot> snapshots = snapshotRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        for (DeviceDecisionSnapshot s : snapshots) {
            timeline.add(new DeviceIntelligenceTimelineItem(
                    s.getId(),
                    "DECISION_EVALUATION",
                    "Intelligence Decision: " + s.getRecommendedAction(),
                    "Unified Score: " + s.getIntelligenceScore() + "/100 (" + s.getDecisionConfidence() + "% confidence)",
                    s.getRecommendedAction(),
                    s.getCreatedAt() != null ? s.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
            ));
        }

        // Diagnoses
        List<DiagnosisReport> diagnoses = diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
        for (DiagnosisReport d : diagnoses) {
            timeline.add(new DeviceIntelligenceTimelineItem(
                    d.getId(),
                    "DIAGNOSIS",
                    "AI Vision Diagnosis: " + (d.getProbableIssue() != null ? d.getProbableIssue() : "Hardware Scan"),
                    "Difficulty: " + d.getRepairDifficulty() + " | Confidence: " + (d.getConfidenceScore() != null ? d.getConfidenceScore() : 80) + "%",
                    d.getRepairDifficulty() != null ? d.getRepairDifficulty() : "MODERATE",
                    d.getCreatedAt() != null ? d.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
            ));
        }

        // Circular events
        List<CircularImpactEvent> events = circularImpactEventRepository.findByDeviceIdOrderByEventDateDesc(deviceId);
        for (CircularImpactEvent e : events) {
            timeline.add(new DeviceIntelligenceTimelineItem(
                    e.getId(),
                    "CIRCULAR_IMPACT",
                    "Circular Impact: " + (e.getEventType() != null ? e.getEventType() : "Lifecycle Event"),
                    String.format("Carbon Saved: %.1f kg | E-Waste Prevented: %.2f kg", e.getCarbonSavedKg(), e.getEwastePreventedKg()),
                    e.getEventType(),
                    e.getEventDate() != null ? e.getEventDate().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
            ));
        }

        // Sort by timestamp desc
        timeline.sort((a, b) -> b.timestamp().compareTo(a.timestamp()));

        return timeline;
    }

    // ─── Private Calculation Helpers ───────────────────────────────────────────

    private int calculateFailureRisk(Device device, DeviceHealth health, List<DiagnosisReport> diagnoses, List<RepairHistory> repairs) {
        int risk = 15; // Baseline low risk

        // Health impact
        int healthScore = health.getHealthScore() != null ? health.getHealthScore() : 80;
        if (healthScore < 40) risk += 40;
        else if (healthScore < 65) risk += 25;
        else if (healthScore < 80) risk += 10;

        // Battery health impact
        int battery = health.getBatteryHealth() != null ? health.getBatteryHealth() : 90;
        if (battery < 50) risk += 20;
        else if (battery < 70) risk += 10;

        // Recent severe diagnosis
        if (!diagnoses.isEmpty()) {
            DiagnosisReport latest = diagnoses.get(0);
            String diff = latest.getRepairDifficulty();
            if ("EXPERT".equalsIgnoreCase(diff) || "HARD".equalsIgnoreCase(diff)) {
                risk += 25;
            } else if ("MODERATE".equalsIgnoreCase(diff)) {
                risk += 15;
            }
            if (latest.getSafetyWarning() != null && !latest.getSafetyWarning().isBlank()) {
                risk += 20;
            }
        }

        // Age factor
        int ageMonths = calculateDeviceAgeMonths(device);
        if (ageMonths > 48) risk += 15;
        else if (ageMonths > 36) risk += 10;

        return Math.min(95, Math.max(5, risk));
    }

    private double estimateRepairCost(List<DiagnosisReport> diagnoses, double replacementPrice) {
        if (!diagnoses.isEmpty() && diagnoses.get(0).getRepairCost() != null) {
            return diagnoses.get(0).getRepairCost();
        }
        return Math.max(45.0, replacementPrice * 0.18);
    }

    private int calculateEconomicScore(double repairCost, double replacementPrice) {
        if (replacementPrice <= 0) return 60;
        double ratio = repairCost / replacementPrice;
        if (ratio < 0.20) return 95;
        if (ratio < 0.35) return 85;
        if (ratio < 0.50) return 70;
        if (ratio < 0.65) return 50;
        if (ratio < 0.80) return 30;
        return 15;
    }

    private int calculateMaintenanceScore(DeviceHealth health) {
        if (health == null) return 70;
        if (health.getMaintenanceDue() == null || health.getMaintenanceDue().isBlank()) {
            return 80;
        }
        try {
            LocalDate due = LocalDate.parse(health.getMaintenanceDue());
            if (due.isBefore(LocalDate.now())) {
                return 40; // Overdue
            } else if (due.isBefore(LocalDate.now().plusWeeks(2))) {
                return 65; // Due soon
            }
            return 90; // Up to date
        } catch (Exception e) {
            return 75;
        }
    }

    private int calculateLongevityScore(Device device) {
        int ageMonths = calculateDeviceAgeMonths(device);
        int expectedLifespan = CATEGORY_LIFESPAN_MONTHS.getOrDefault(
                device.getCategory() != null ? device.getCategory().toLowerCase() : "other", 48
        );

        if (ageMonths <= expectedLifespan * 0.3) return 95;
        if (ageMonths <= expectedLifespan * 0.6) return 80;
        if (ageMonths <= expectedLifespan * 0.9) return 65;
        if (ageMonths <= expectedLifespan * 1.2) return 45;
        return 30;
    }

    private int calculateSustainabilityScore(List<CircularImpactEvent> events, int healthScore) {
        int score = 60;
        if (!events.isEmpty()) {
            score += Math.min(30, events.size() * 10);
        }
        if (healthScore >= 75) {
            score += 10;
        }
        return Math.min(100, score);
    }

    private int calculateRepairHistoryScore(List<RepairHistory> history) {
        if (history.isEmpty()) return 90;
        long completed = history.stream().filter(h -> "COMPLETED".equalsIgnoreCase(h.getStatus())).count();
        if (completed == history.size()) return 85;
        return 65;
    }

    private String determineRecommendedAction(
            int healthScore,
            int failureRisk,
            int economicScore,
            int maintenanceScore,
            int longevityScore,
            double repairCost,
            double replacementPrice,
            List<DiagnosisReport> diagnoses
    ) {
        // 1. Critical safety or extreme hardware damage
        boolean hasSafetyWarning = !diagnoses.isEmpty() &&
                diagnoses.get(0).getSafetyWarning() != null &&
                !diagnoses.get(0).getSafetyWarning().isBlank();

        if (hasSafetyWarning || (healthScore < 30 && failureRisk >= 80)) {
            if (economicScore < 30) {
                return "RECYCLE";
            }
            return "PROFESSIONAL_SERVICE";
        }

        // 2. High failure risk & repairable
        if (failureRisk >= 60 || healthScore < 50) {
            if (economicScore >= 45) {
                return "REPAIR_NOW";
            } else {
                return longevityScore < 40 ? "REPLACE" : "PROFESSIONAL_SERVICE";
            }
        }

        // 3. Maintenance required
        if (maintenanceScore < 55 && healthScore >= 60) {
            return "MAINTENANCE_REQUIRED";
        }

        // 4. Mature hardware + refurbishment opportunity
        if (longevityScore <= 45 && healthScore >= 55 && economicScore >= 50) {
            return "REFURBISH";
        }

        // 5. Stable / Monitor
        if (healthScore < 75 || failureRisk >= 35) {
            return "MONITOR";
        }

        // 6. Optimal baseline
        return "CONTINUE_USING";
    }

    private int calculateConfidence(List<DiagnosisReport> diagnoses, List<RepairHistory> repairs, DeviceHealth health) {
        int confidence = 75;
        if (!diagnoses.isEmpty()) confidence += 10;
        if (!repairs.isEmpty()) confidence += 5;
        if (health != null && health.getBatteryHealth() != null) confidence += 5;
        return Math.min(95, confidence);
    }

    private int calculateDeviceAgeMonths(Device device) {
        if (device == null || device.getPurchaseDate() == null || device.getPurchaseDate().isBlank()) {
            return 12; // Default 1 year
        }
        try {
            LocalDate purchase = LocalDate.parse(device.getPurchaseDate());
            Period period = Period.between(purchase, LocalDate.now());
            return Math.max(1, (period.getYears() * 12) + period.getMonths());
        } catch (Exception e) {
            return 12;
        }
    }

    private double estimateCo2Saved(Device device) {
        String cat = device != null && device.getCategory() != null ? device.getCategory().toLowerCase() : "other";
        return switch (cat) {
            case "smartphone" -> 14.5;
            case "laptop" -> 36.0;
            case "tablet" -> 20.0;
            case "gaming console" -> 28.0;
            case "smartwatch" -> 8.0;
            case "audio device" -> 5.5;
            default -> 16.0;
        };
    }

    private String formatScenarioTitle(String scenarioType) {
        return switch (scenarioType) {
            case "CONTINUE_USING" -> "Continue Regular Operation";
            case "MAINTENANCE" -> "Preventative Deep Maintenance";
            case "REPAIR" -> "Component-Level Repair";
            case "PROFESSIONAL_SERVICE" -> "Certified Technician Service";
            case "REFURBISH" -> "Complete Refurbish & Upgrade";
            case "REPLACE" -> "Replace with New Device";
            case "RECYCLE" -> "Responsible E-Waste Recycling";
            default -> scenarioType;
        };
    }
}
