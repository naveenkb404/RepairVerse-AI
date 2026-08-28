package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairDelayImpactService {

    private final DeviceRepository deviceRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;

    /**
     * Calculates deterministic 7-day, 30-day, and 90-day delay consequence projections.
     */
    @Transactional(readOnly = true)
    public DelayImpactResponse simulateDelayImpact(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);

        Optional<DevicePrediction> predictionOpt = devicePredictionRepository.findByDeviceId(deviceId);
        Optional<DiagnosisReport> diagOpt = diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc(deviceId);

        int healthScore = predictionOpt.map(DevicePrediction::getPredictionScore).orElse(75);
        String riskLevel = predictionOpt.map(DevicePrediction::getRiskLevel).orElse("MEDIUM");
        String faultType = predictionOpt.map(DevicePrediction::getPrimaryFaultType)
            .orElseGet(() -> diagOpt.map(DiagnosisReport::getProbableIssue).orElse("General Component Wear"));

        double baselineCost = predictionOpt.flatMap(p -> Optional.ofNullable(p.getEstimatedRepairCost()))
            .orElseGet(() -> diagOpt.map(DiagnosisReport::getRepairCost).orElse(85.0));

        List<DelayProjection> projections = new ArrayList<>();

        // Horizon 1: 7-Day Delay
        double cost7 = Math.round(baselineCost * 1.08 * 100.0) / 100.0;
        projections.add(new DelayProjection(
            7,
            "7 Days (Short-term)",
            cost7,
            8.0,
            "LOW".equalsIgnoreCase(riskLevel) ? "LOW" : "MEDIUM",
            12.0,
            1,
            0.4,
            "Minor thermal compound dry-out or microscopic contact oxidation. Servicing remains straightforward."
        ));

        // Horizon 2: 30-Day Delay
        double cost30 = Math.round(baselineCost * 1.35 * 100.0) / 100.0;
        projections.add(new DelayProjection(
            30,
            "30 Days (Medium-term)",
            cost30,
            35.0,
            "HIGH",
            48.0,
            3,
            2.1,
            "Battery swelling risk increases; power management IC experiences over-voltage strain from erratic current."
        ));

        // Horizon 3: 90-Day Delay
        double cost90 = Math.round(baselineCost * 1.78 * 100.0) / 100.0;
        projections.add(new DelayProjection(
            90,
            "90 Days (Long-term)",
            cost90,
            78.0,
            "CRITICAL",
            82.0,
            8,
            5.6,
            "High probability of multi-layer PCB delamination, trace fracture, or catastrophic display controller failure."
        ));

        String urgency = deriveUrgencyAdvice(healthScore, baselineCost);

        return new DelayImpactResponse(
            device.getId(),
            device.getDeviceName(),
            baselineCost,
            riskLevel,
            faultType,
            projections,
            urgency,
            LocalDateTime.now()
        );
    }

    private String deriveUrgencyAdvice(int score, double baselineCost) {
        if (score < 40) {
            return "Immediate repair required within 48-72 hours. Deferring action poses high risk of non-recoverable motherboard failure.";
        }
        if (score < 65) {
            return "Proactive repair recommended within 14 days. Early intervention prevents a 35% to 78% escalation in repair costs.";
        }
        return "Maintenance is elective but optimal within 30 days to preserve peak battery efficiency and resale value.";
    }

    private Device validateDeviceOwnership(String deviceId, String userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));
    }
}
