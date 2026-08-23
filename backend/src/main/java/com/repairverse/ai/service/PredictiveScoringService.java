package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DevicePrediction;
import com.repairverse.ai.entity.FaultPattern;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Deterministic predictive scoring engine.
 *
 * <p>Scoring factors (weights total = 100 points):
 * <ul>
 *   <li>Device Age (20 pts)
 *   <li>Health Score (30 pts)
 *   <li>Battery Health (15 pts)
 *   <li>Repair Frequency (15 pts)
 *   <li>Warranty Status (10 pts)
 *   <li>Last Service Recency (10 pts)
 * </ul>
 *
 * <p>Risk levels derived from predictionScore:
 * <ul>
 *   <li>CRITICAL: 0-34
 *   <li>HIGH:     35-54
 *   <li>MEDIUM:   55-74
 *   <li>LOW:      75-89
 *   <li>HEALTHY:  90-100
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PredictiveScoringService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final FaultPatternRepository faultPatternRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    // ─── Risk thresholds ─────────────────────────────────────────────────────
    private static final int CRITICAL_MAX = 34;
    private static final int HIGH_MAX     = 54;
    private static final int MEDIUM_MAX   = 74;
    private static final int LOW_MAX      = 89;

    // ─── Notification dedup window ───────────────────────────────────────────
    private static final int DEDUP_HOURS = 24;

    // ─── Public API ──────────────────────────────────────────────────────────

    /**
     * Evaluate predictive maintenance score for a device owned by the authenticated user.
     * The result is persisted (upsert) and the prediction DTO is returned.
     * A HIGH/CRITICAL notification is created at most once per 24-hour window.
     */
    @Transactional
    public DevicePredictionResponse evaluateDevice(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Device not found or does not belong to the authenticated user: " + deviceId));

        DeviceHealth health = deviceHealthRepository.findByDeviceId(deviceId)
                .orElse(null);

        long repairCount = repairHistoryRepository.countByUserId(userId);
        List<FaultPattern> patterns = faultPatternRepository
                .findActiveByCategoryAndBrand(device.getCategory(), device.getBrand());

        return computeAndPersist(device, health, (int) repairCount, patterns, userId, false);
    }

    /**
     * Return demo prediction without DB persistence.
     */
    public DevicePredictionResponse evaluateDemoDevice(String deviceId, String deviceName,
                                                       String category, String brand,
                                                       int healthScore, Integer batteryHealth,
                                                       String purchaseDate) {
        return buildDemoResponse(deviceId, deviceName, category, brand,
                healthScore, batteryHealth, purchaseDate);
    }

    /**
     * Evaluate all devices for a user (batch for dashboard view).
     */
    @Transactional(readOnly = true)
    public List<DevicePredictionResponse> getUserFleet(String userId) {
        return devicePredictionRepository.findByUserIdOrderByEvaluatedAtDesc(userId)
                .stream()
                .map(dp -> {
                    Device device = deviceRepository.findById(dp.getDeviceId()).orElse(null);
                    String name = device != null ? device.getDeviceName() : "Unknown Device";
                    String cat  = device != null ? device.getCategory()   : "";
                    String br   = device != null ? device.getBrand()       : "";
                    return mapToDto(dp, name, cat, br, false);
                })
                .toList();
    }

    // ─── Core scoring logic ───────────────────────────────────────────────────

    private DevicePredictionResponse computeAndPersist(
            Device device, DeviceHealth health, int totalRepairs,
            List<FaultPattern> patterns, String userId, boolean demo) {

        List<ScoringFactor> factors = new ArrayList<>();
        int totalScore = 0;

        // 1. Device Age Factor (20 pts)
        int ageFactor = computeAgeFactor(device.getPurchaseDate(), factors);
        totalScore += ageFactor;

        // 2. Health Score Factor (30 pts)
        int healthFactor = computeHealthFactor(health, factors);
        totalScore += healthFactor;

        // 3. Battery Health Factor (15 pts)
        int batteryFactor = computeBatteryFactor(health, factors);
        totalScore += batteryFactor;

        // 4. Repair Frequency Factor (15 pts)
        int repairFactor = computeRepairFactor(totalRepairs, factors);
        totalScore += repairFactor;

        // 5. Warranty Status Factor (10 pts)
        int warrantyFactor = computeWarrantyFactor(device.getWarrantyExpiry(), factors);
        totalScore += warrantyFactor;

        // 6. Last Service Recency Factor (10 pts)
        int serviceFactor = computeServiceFactor(health, factors);
        totalScore += serviceFactor;

        // Apply pattern-based penalty
        String primaryFault = "General Wear";
        for (FaultPattern p : patterns) {
            int score = health != null ? health.getHealthScore() : 80;
            int ageYears = getDeviceAgeYears(device.getPurchaseDate());
            if (score <= p.getHealthScoreThreshold() && ageYears >= p.getMinDeviceAgeYears()) {
                int penalty = p.getRiskWeight() * 2;
                totalScore = Math.max(0, totalScore - penalty);
                primaryFault = p.getFaultType();
                break; // apply worst matching pattern only
            }
        }

        totalScore = Math.max(0, Math.min(100, totalScore));
        String riskLevel = classifyRisk(totalScore);

        // Build recommendations
        List<String> recommendations = buildRecommendations(riskLevel, primaryFault, health, device);
        int daysToFailure = estimateDaysToFailure(totalScore);
        double repairCost = estimateRepairCost(patterns, riskLevel);
        double preventiveSavings = repairCost * 0.4;
        double co2Savings = 12.5 + (100 - totalScore) * 0.3;

        // Upsert DevicePrediction
        DevicePrediction saved = upsertPrediction(
                device, userId, totalScore, riskLevel, daysToFailure,
                primaryFault, recommendations, factors,
                repairCost, preventiveSavings, co2Savings, 0.82);

        // Issue notification if HIGH/CRITICAL and not already sent in last 24h
        if (!demo && (riskLevel.equals("CRITICAL") || riskLevel.equals("HIGH"))) {
            boolean alreadyNotified = devicePredictionRepository.existsRecentHighRiskNotification(
                    device.getId(),
                    LocalDateTime.now().minusHours(DEDUP_HOURS));
            if (!alreadyNotified) {
                notificationService.createNotification(
                        userId,
                        "predictive_maintenance",
                        riskLevel.equals("CRITICAL")
                                ? "⚠️ CRITICAL: " + device.getDeviceName() + " Needs Immediate Attention"
                                : "🔶 HIGH Risk: " + device.getDeviceName() + " Maintenance Recommended",
                        "Predictive score: " + totalScore + "/100. Estimated days to failure: "
                                + daysToFailure + ". Issue: " + primaryFault,
                        "/devices/" + device.getId(),
                        "View Device Health",
                        riskLevel.equals("CRITICAL") ? "red" : "orange"
                );
                saved.setNotificationSent(true);
                devicePredictionRepository.save(saved);
            }
        }

        return mapToDto(saved, device.getDeviceName(), device.getCategory(), device.getBrand(), demo);
    }

    // ─── Scoring factor computations ──────────────────────────────────────────

    private int computeAgeFactor(String purchaseDate, List<ScoringFactor> out) {
        int ageYears = getDeviceAgeYears(purchaseDate);
        int pts;
        String status;
        String desc;
        if (ageYears <= 1) {
            pts = 20; status = "HEALTHY"; desc = "Device is less than 1 year old.";
        } else if (ageYears <= 2) {
            pts = 18; status = "HEALTHY"; desc = "Device is 1-2 years old — minimal age risk.";
        } else if (ageYears <= 3) {
            pts = 14; status = "MEDIUM"; desc = "Device is 2-3 years old — moderate age risk.";
        } else if (ageYears <= 5) {
            pts = 9;  status = "HIGH";   desc = "Device is 3-5 years old — increased failure probability.";
        } else {
            pts = 4;  status = "CRITICAL"; desc = "Device is over 5 years old — high failure risk.";
        }
        out.add(new ScoringFactor("Device Age", pts, 20, status, desc));
        return pts;
    }

    private int computeHealthFactor(DeviceHealth health, List<ScoringFactor> out) {
        int hs = health != null ? health.getHealthScore() : 70;
        int pts = Math.round(hs * 30.0f / 100);
        String status = hs >= 80 ? "HEALTHY" : hs >= 60 ? "MEDIUM" : hs >= 40 ? "HIGH" : "CRITICAL";
        out.add(new ScoringFactor("Health Score", pts, 30, status,
                "Current device health score: " + hs + "/100."));
        return pts;
    }

    private int computeBatteryFactor(DeviceHealth health, List<ScoringFactor> out) {
        Integer bh = health != null ? health.getBatteryHealth() : null;
        if (bh == null) {
            out.add(new ScoringFactor("Battery Health", 10, 15, "MEDIUM", "Battery health data not available."));
            return 10;
        }
        int pts = Math.round(bh * 15.0f / 100);
        String status = bh >= 80 ? "HEALTHY" : bh >= 60 ? "MEDIUM" : bh >= 40 ? "HIGH" : "CRITICAL";
        out.add(new ScoringFactor("Battery Health", pts, 15, status,
                "Battery capacity: " + bh + "% of original."));
        return pts;
    }

    private int computeRepairFactor(int repairCount, List<ScoringFactor> out) {
        int pts;
        String status, desc;
        if (repairCount == 0) {
            pts = 15; status = "HEALTHY"; desc = "No previous repairs — excellent reliability record.";
        } else if (repairCount <= 2) {
            pts = 12; status = "HEALTHY"; desc = repairCount + " repair(s) — normal wear.";
        } else if (repairCount <= 5) {
            pts = 8;  status = "MEDIUM"; desc = repairCount + " repairs — device shows recurring issues.";
        } else {
            pts = 3;  status = "HIGH"; desc = repairCount + " repairs — high recurring failure pattern.";
        }
        out.add(new ScoringFactor("Repair History", pts, 15, status, desc));
        return pts;
    }

    private int computeWarrantyFactor(String warrantyExpiry, List<ScoringFactor> out) {
        if (warrantyExpiry == null || warrantyExpiry.isBlank()) {
            out.add(new ScoringFactor("Warranty", 5, 10, "MEDIUM", "No warranty data available."));
            return 5;
        }
        try {
            LocalDate expiry = LocalDate.parse(warrantyExpiry);
            boolean active = expiry.isAfter(LocalDate.now());
            int pts = active ? 10 : 5;
            String status = active ? "HEALTHY" : "MEDIUM";
            String desc = active
                    ? "Warranty active until " + expiry + "."
                    : "Warranty expired on " + expiry + " — repair costs are out-of-pocket.";
            out.add(new ScoringFactor("Warranty Status", pts, 10, status, desc));
            return pts;
        } catch (Exception e) {
            out.add(new ScoringFactor("Warranty", 5, 10, "MEDIUM", "Could not parse warranty date."));
            return 5;
        }
    }

    private int computeServiceFactor(DeviceHealth health, List<ScoringFactor> out) {
        String lastService = health != null ? health.getLastService() : null;
        if (lastService == null || lastService.isBlank()) {
            out.add(new ScoringFactor("Last Service", 5, 10, "MEDIUM", "No service history recorded."));
            return 5;
        }
        try {
            LocalDate svcDate = LocalDate.parse(lastService);
            int monthsSince = Period.between(svcDate, LocalDate.now()).getMonths()
                    + Period.between(svcDate, LocalDate.now()).getYears() * 12;
            int pts;
            String status, desc;
            if (monthsSince <= 6) {
                pts = 10; status = "HEALTHY"; desc = "Serviced " + monthsSince + " month(s) ago — great maintenance cadence.";
            } else if (monthsSince <= 12) {
                pts = 7;  status = "MEDIUM"; desc = "Last service was ~" + monthsSince + " months ago.";
            } else if (monthsSince <= 24) {
                pts = 4;  status = "HIGH"; desc = "Over a year since last service — maintenance overdue.";
            } else {
                pts = 1;  status = "CRITICAL"; desc = "Over 2 years since last service — critical maintenance needed.";
            }
            out.add(new ScoringFactor("Last Service Recency", pts, 10, status, desc));
            return pts;
        } catch (Exception e) {
            out.add(new ScoringFactor("Last Service", 5, 10, "MEDIUM", "Could not parse service date."));
            return 5;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private int getDeviceAgeYears(String purchaseDate) {
        if (purchaseDate == null || purchaseDate.isBlank()) return 2;
        try {
            LocalDate pd = LocalDate.parse(purchaseDate);
            return Period.between(pd, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 2;
        }
    }

    private String classifyRisk(int score) {
        if (score <= CRITICAL_MAX) return "CRITICAL";
        if (score <= HIGH_MAX)     return "HIGH";
        if (score <= MEDIUM_MAX)   return "MEDIUM";
        if (score <= LOW_MAX)      return "LOW";
        return "HEALTHY";
    }

    private int estimateDaysToFailure(int score) {
        // Linear interpolation: score=0 → 7 days, score=100 → 730 days
        return 7 + (score * 723 / 100);
    }

    private double estimateRepairCost(List<FaultPattern> patterns, String riskLevel) {
        if (!patterns.isEmpty() && patterns.get(0).getTypicalCostMax() != null) {
            return patterns.get(0).getTypicalCostMax();
        }
        return switch (riskLevel) {
            case "CRITICAL" -> 280.0;
            case "HIGH"     -> 180.0;
            case "MEDIUM"   -> 110.0;
            case "LOW"      -> 60.0;
            default         -> 30.0;
        };
    }

    private List<String> buildRecommendations(String riskLevel, String faultType,
                                               DeviceHealth health, Device device) {
        List<String> recs = new ArrayList<>();
        switch (riskLevel) {
            case "CRITICAL" -> {
                recs.add("Schedule professional repair immediately for: " + faultType);
                recs.add("Back up all data before the device fails completely");
                recs.add("Consider device replacement if repair cost exceeds 60% of device value");
            }
            case "HIGH" -> {
                recs.add("Book a maintenance appointment within the next 2 weeks");
                recs.add("Monitor device temperature and performance closely");
                recs.add("Address " + faultType + " before it escalates");
            }
            case "MEDIUM" -> {
                recs.add("Schedule a routine checkup within the next month");
                recs.add("Clean device ports and cooling vents");
                recs.add("Update firmware and run diagnostics");
            }
            default -> {
                recs.add("Continue regular maintenance every 6 months");
                recs.add("Keep software and firmware up to date");
                recs.add("Ensure proper storage and handling");
            }
        }
        if (health != null && health.getBatteryHealth() != null && health.getBatteryHealth() < 70) {
            recs.add("Battery replacement recommended — current capacity: " + health.getBatteryHealth() + "%");
        }
        return recs;
    }

    private DevicePrediction upsertPrediction(
            Device device, String userId, int score, String riskLevel,
            int daysToFailure, String faultType, List<String> recommendations,
            List<ScoringFactor> factors, double repairCost,
            double preventiveSavings, double co2Savings, double confidence) {

        try {
            Optional<DevicePrediction> existing = devicePredictionRepository.findByDeviceId(device.getId());
            String actionsJson = objectMapper.writeValueAsString(recommendations);
            String breakdownJson = objectMapper.writeValueAsString(factors);

            DevicePrediction dp = existing.map(e -> {
                e.setPredictionScore(score);
                e.setRiskLevel(riskLevel);
                e.setDaysToFailureEstimate(daysToFailure);
                e.setPrimaryFaultType(faultType);
                e.setRecommendedActions(actionsJson);
                e.setScoringBreakdown(breakdownJson);
                e.setEstimatedRepairCost(repairCost);
                e.setPreventiveSavings(preventiveSavings);
                e.setCo2SavingsKg(co2Savings);
                e.setConfidenceScore(confidence);
                e.setEvaluatedAt(LocalDateTime.now());
                e.setNotificationSent(false);
                return e;
            }).orElse(DevicePrediction.builder()
                    .id(UUID.randomUUID().toString())
                    .deviceId(device.getId())
                    .userId(userId)
                    .predictionScore(score)
                    .riskLevel(riskLevel)
                    .daysToFailureEstimate(daysToFailure)
                    .primaryFaultType(faultType)
                    .recommendedActions(actionsJson)
                    .scoringBreakdown(breakdownJson)
                    .estimatedRepairCost(repairCost)
                    .preventiveSavings(preventiveSavings)
                    .co2SavingsKg(co2Savings)
                    .confidenceScore(confidence)
                    .notificationSent(false)
                    .evaluatedAt(LocalDateTime.now())
                    .build());

            return devicePredictionRepository.save(dp);
        } catch (Exception e) {
            log.error("Failed to persist DevicePrediction for device {}: {}", device.getId(), e.getMessage());
            return DevicePrediction.builder()
                    .id(UUID.randomUUID().toString())
                    .deviceId(device.getId())
                    .userId(userId)
                    .predictionScore(score)
                    .riskLevel(riskLevel)
                    .build();
        }
    }

    private DevicePredictionResponse mapToDto(DevicePrediction dp, String deviceName,
                                               String category, String brand, boolean demo) {
        List<String> actions = new ArrayList<>();
        List<ScoringFactor> breakdown = new ArrayList<>();
        try {
            if (dp.getRecommendedActions() != null) {
                actions = objectMapper.readValue(dp.getRecommendedActions(), new TypeReference<>() {});
            }
            if (dp.getScoringBreakdown() != null) {
                breakdown = objectMapper.readValue(dp.getScoringBreakdown(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.warn("Could not deserialize prediction JSON for device {}", dp.getDeviceId());
        }
        return new DevicePredictionResponse(
                dp.getDeviceId(),
                deviceName,
                category,
                brand,
                dp.getPredictionScore(),
                dp.getRiskLevel(),
                dp.getDaysToFailureEstimate(),
                dp.getPrimaryFaultType() != null ? dp.getPrimaryFaultType() : "General Wear",
                actions,
                breakdown,
                dp.getEstimatedRepairCost() != null ? dp.getEstimatedRepairCost() : 100.0,
                dp.getPreventiveSavings() != null ? dp.getPreventiveSavings() : 40.0,
                dp.getCo2SavingsKg() != null ? dp.getCo2SavingsKg() : 8.5,
                dp.getConfidenceScore() != null ? dp.getConfidenceScore() : 0.80,
                demo,
                dp.getEvaluatedAt() != null ? dp.getEvaluatedAt().toString() : LocalDateTime.now().toString()
        );
    }

    // ─── Demo mode (no DB interaction) ───────────────────────────────────────

    private DevicePredictionResponse buildDemoResponse(
            String deviceId, String deviceName, String category, String brand,
            int healthScore, Integer batteryHealth, String purchaseDate) {

        int score = Math.round(
                (healthScore * 0.30f) +
                ((batteryHealth != null ? batteryHealth : 75) * 0.15f) +
                20 - (getDeviceAgeYears(purchaseDate) * 4) +
                12 + 7 + 5);
        score = Math.max(0, Math.min(100, score));
        String risk = classifyRisk(score);
        int daysToFailure = estimateDaysToFailure(score);

        List<ScoringFactor> factors = List.of(
                new ScoringFactor("Device Age", 14, 20, "MEDIUM", "Demo evaluation"),
                new ScoringFactor("Health Score", Math.round(healthScore * 30f / 100), 30, "MEDIUM", "Sample score"),
                new ScoringFactor("Battery Health", batteryHealth != null ? Math.round(batteryHealth * 15f / 100) : 10, 15, "MEDIUM", "Sample battery"),
                new ScoringFactor("Repair History", 12, 15, "HEALTHY", "No repairs in demo"),
                new ScoringFactor("Warranty Status", 5, 10, "MEDIUM", "Demo warranty"),
                new ScoringFactor("Last Service", 7, 10, "MEDIUM", "Demo service")
        );

        List<String> recs = buildRecommendations(risk, "General Wear", null, null);

        return new DevicePredictionResponse(
                deviceId, deviceName, category, brand,
                score, risk, daysToFailure, "General Wear",
                recs, factors, 120.0, 48.0, 9.2, 0.78,
                true, LocalDateTime.now().toString()
        );
    }
}
