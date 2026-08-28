package com.repairverse.ai.service;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * Phase 25 — Deterministic Maintenance Priority Engine.
 *
 * Calculates a deterministic priority score (0–100) from observable device data.
 * Gemini must NEVER be consulted for priority calculations.
 * Gemini/XAI may only provide narrative explanation of the deterministic result.
 *
 * Priority scoring formula:
 *   base                      = 50
 *   + risk bonus              : CRITICAL +40, HIGH +25, MEDIUM +10, LOW +0
 *   + health penalty          : score < 40 → +15, < 60 → +8, < 80 → +3, else +0
 *   + overdue bonus           : > 30 days overdue → +10, > 7 days → +5
 *   + lifecycle urgency bonus : IMMEDIATE → +10, HIGH → +6, MEDIUM → +3
 *   + delay impact bonus      : overdue maintenance exists → +3 per overdue task (max +10)
 *   capped at 100
 *
 * Priority Level thresholds:
 *   80–100 → CRITICAL
 *   60–79  → HIGH
 *   40–59  → MEDIUM
 *   0–39   → LOW
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenancePriorityService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository predictionRepository;
    private final MaintenanceScheduleRepository maintenanceRepository;

    /**
     * Calculates a deterministic priority assessment for a specific device.
     */
    @Transactional(readOnly = true)
    public MaintenancePriorityResponse getPriorityForDevice(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Device not found or not owned by user: " + deviceId));

        return calculatePriority(device, userId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core deterministic scoring engine
    // ─────────────────────────────────────────────────────────────────────────

    private MaintenancePriorityResponse calculatePriority(Device device, String userId) {
        Optional<DeviceHealth> healthOpt = deviceHealthRepository.findByDeviceId(device.getId());
        Optional<DevicePrediction> predOpt = predictionRepository.findByDeviceId(device.getId());

        int healthScore = predOpt.map(DevicePrediction::getPredictionScore)
                .orElseGet(() -> healthOpt.map(DeviceHealth::getHealthScore).orElse(75));
        String riskLevel = predOpt.map(DevicePrediction::getRiskLevel).orElse("LOW");
        Integer daysToFailure = predOpt.map(DevicePrediction::getDaysToFailureEstimate).orElse(90);
        String replacementUrgency = daysToFailure != null && daysToFailure < 15 ? "IMMEDIATE" :
                daysToFailure != null && daysToFailure < 30 ? "HIGH" :
                daysToFailure != null && daysToFailure < 60 ? "MEDIUM" : "MONITOR";

        // Fetch overdue schedules
        List<MaintenanceSchedule> overdueSchedules = maintenanceRepository
                .findByUserIdAndDeviceIdOrderByDueDateAsc(userId, device.getId())
                .stream()
                .filter(s -> "OVERDUE".equalsIgnoreCase(s.getStatus()) ||
                        (!List.of("COMPLETED","SKIPPED","CANCELLED").contains(s.getStatus()) &&
                                s.getDueDate().isBefore(LocalDate.now())))
                .toList();

        int maxOverdueDays = overdueSchedules.stream()
                .mapToInt(s -> (int) ChronoUnit.DAYS.between(s.getDueDate(), LocalDate.now()))
                .max().orElse(0);

        // ── Score calculation ─────────────────────────────────────────────
        int score = 50;

        // Risk bonus
        score += switch (riskLevel.toUpperCase()) {
            case "CRITICAL" -> 40;
            case "HIGH"     -> 25;
            case "MEDIUM"   -> 10;
            default         -> 0;
        };

        // Health penalty
        score += healthScore < 40 ? 15 : healthScore < 60 ? 8 : healthScore < 80 ? 3 : 0;

        // Overdue bonus
        score += maxOverdueDays > 30 ? 10 : maxOverdueDays > 7 ? 5 : 0;

        // Lifecycle urgency bonus
        score += switch (replacementUrgency != null ? replacementUrgency.toUpperCase() : "MONITOR") {
            case "REPLACE_IMMEDIATELY", "IMMEDIATE" -> 10;
            case "REPLACE_SOON", "HIGH"             -> 6;
            case "CONSIDER_REPLACING", "MEDIUM"     -> 3;
            default                                  -> 0;
        };

        // Delay impact bonus (each overdue task adds 3 points, capped at 10)
        score += Math.min(overdueSchedules.size() * 3, 10);

        score = Math.min(score, 100);

        // ── Derive level, reason, action ──────────────────────────────────
        String priorityLevel = score >= 80 ? "CRITICAL" : score >= 60 ? "HIGH" :
                score >= 40 ? "MEDIUM" : "LOW";

        String reason = buildReason(riskLevel, healthScore, maxOverdueDays, overdueSchedules.size(), replacementUrgency);
        String recommendedAction = buildRecommendedAction(priorityLevel, riskLevel, overdueSchedules.size());
        String riskContributor = buildRiskContributor(riskLevel, healthScore, maxOverdueDays);

        log.debug("Priority score for device='{}': {}/100 ({})", device.getId(), score, priorityLevel);

        return new MaintenancePriorityResponse(
                device.getId(),
                device.getDeviceName(),
                score,
                priorityLevel,
                reason,
                recommendedAction,
                riskContributor,
                LocalDateTime.now(),
                false
        );
    }

    // ── Rationale builders ───────────────────────────────────────────────────

    private String buildReason(String riskLevel, int healthScore, int maxOverdueDays,
                                int overdueCount, String urgency) {
        StringBuilder sb = new StringBuilder();

        if ("CRITICAL".equalsIgnoreCase(riskLevel)) {
            sb.append("Device has a CRITICAL failure risk score. ");
        } else if ("HIGH".equalsIgnoreCase(riskLevel)) {
            sb.append("High probability of component failure detected. ");
        } else if ("MEDIUM".equalsIgnoreCase(riskLevel)) {
            sb.append("Moderate degradation signals identified. ");
        }

        if (healthScore < 40) {
            sb.append("Health score is severely degraded (").append(healthScore).append("/100). ");
        } else if (healthScore < 60) {
            sb.append("Health score is below acceptable threshold (").append(healthScore).append("/100). ");
        }

        if (overdueCount > 0) {
            sb.append(overdueCount).append(" maintenance task(s) are overdue");
            if (maxOverdueDays > 0) sb.append(" by up to ").append(maxOverdueDays).append(" days");
            sb.append(". ");
        }

        if (urgency != null && (urgency.contains("REPLACE") || urgency.contains("IMMEDIATE"))) {
            sb.append("Lifecycle assessment recommends urgent replacement evaluation. ");
        }

        return sb.isEmpty() ? "Device is currently in acceptable condition." : sb.toString().trim();
    }

    private String buildRecommendedAction(String level, String riskLevel, int overdueCount) {
        return switch (level) {
            case "CRITICAL" -> "Book a professional repair appointment immediately. Do not delay — further usage risks permanent data loss or hardware damage.";
            case "HIGH"     -> "Schedule a certified technician inspection within 7 days. Complete all overdue maintenance tasks.";
            case "MEDIUM"   -> overdueCount > 0
                    ? "Complete " + overdueCount + " overdue maintenance task(s) within the next 2 weeks."
                    : "Perform scheduled preventive maintenance within the next 30 days.";
            default         -> "Continue routine quarterly monitoring. No urgent action required.";
        };
    }

    private String buildRiskContributor(String riskLevel, int healthScore, int maxOverdueDays) {
        if ("CRITICAL".equalsIgnoreCase(riskLevel) || "HIGH".equalsIgnoreCase(riskLevel)) {
            return "Predictive risk model (" + riskLevel + " risk level)";
        }
        if (healthScore < 60) {
            return "Low health score (" + healthScore + "/100)";
        }
        if (maxOverdueDays > 7) {
            return "Overdue maintenance (" + maxOverdueDays + " days past due)";
        }
        return "Routine monitoring";
    }
}
