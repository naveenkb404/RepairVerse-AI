package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceIntelligenceResponse;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProactiveInterventionService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final AutonomousInterventionRepository interventionRepository;
    private final DeviceDecisionIntelligenceService decisionIntelligenceService;
    private final InterventionPriorityService priorityService;
    private final AutonomousActionPlanningService actionPlanningService;

    private static final List<String> ACTIVE_STATUSES = List.of(
            "DETECTED", "PENDING_APPROVAL", "APPROVED", "IN_PROGRESS"
    );

    @Transactional
    public List<AutonomousIntervention> evaluateUserDevices(String userId) {
        log.info("Running proactive intervention scan for all devices of user '{}'", userId);

        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<AutonomousIntervention> created = new ArrayList<>();

        for (Device device : devices) {
            try {
                AutonomousIntervention intervention = evaluateDevice(device.getId(), userId);
                if (intervention != null) {
                    created.add(intervention);
                }
            } catch (Exception e) {
                log.warn("Failed to evaluate device '{}' during scan: {}", device.getId(), e.getMessage());
            }
        }

        return created;
    }

    @Transactional
    public AutonomousIntervention evaluateDevice(String deviceId, String userId) {
        log.info("Proactively evaluating device '{}' for interventions (user: '{}')", deviceId, userId);

        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        // Get unified decision intelligence
        DeviceIntelligenceResponse intel = decisionIntelligenceService.evaluateDeviceIntelligence(deviceId, userId, false);

        String interventionType = mapRecommendedActionToInterventionType(intel.recommendedAction(), intel.scoreBreakdown().failureRiskScore());

        // Prevent duplicate active intervention of same type
        Optional<AutonomousIntervention> existing = interventionRepository
                .findFirstByDeviceIdAndUserIdAndInterventionTypeAndStatusIn(deviceId, userId, interventionType, ACTIVE_STATUSES);

        if (existing.isPresent()) {
            log.info("Active intervention '{}' already exists for device '{}'", interventionType, deviceId);
            return existing.get();
        }

        // Calculate 6-factor priority
        int failureRisk = intel.scoreBreakdown().failureRiskScore();
        int userImpact = calculateUserImpact(device, failureRisk);
        int urgency = calculateUrgency(intel.recommendedAction(), failureRisk);
        int financialRisk = 100 - intel.scoreBreakdown().repairEconomicsScore();
        int repairOpportunity = intel.scoreBreakdown().repairEconomicsScore();
        int sustainability = intel.scoreBreakdown().sustainabilityScore();

        InterventionPriorityService.PriorityResult priorityResult = priorityService.calculatePriority(
                failureRisk, userImpact, urgency, financialRisk, repairOpportunity, sustainability
        );

        double estCost = intel.smartDecision() != null && intel.smartDecision().estimatedCost() != null
                ? intel.smartDecision().estimatedCost() : 0.0;
        double estSavings = calculateEstimatedSavings(device, estCost);
        double estCo2 = calculateEstimatedCo2(device);

        AutonomousIntervention intervention = AutonomousIntervention.builder()
                .userId(userId)
                .deviceId(deviceId)
                .interventionType(interventionType)
                .priority(priorityResult.priorityTier())
                .priorityScore(priorityResult.priorityScore())
                .status("DETECTED")
                .confidenceScore(intel.decisionConfidence())
                .title(generateTitle(interventionType, device.getDeviceName()))
                .description(generateDescription(interventionType, device.getDeviceName(), intel.summary()))
                .reason(intel.summary())
                .estimatedCost(estCost)
                .estimatedSavings(estSavings)
                .estimatedCo2Impact(estCo2)
                .recommendedAction(intel.recommendedAction())
                .requiresUserApproval(!"MONITOR".equals(interventionType))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AutonomousIntervention saved = interventionRepository.save(intervention);

        // Generate multi-step action plan
        actionPlanningService.generateAndSavePlan(saved);

        log.info("Created autonomous intervention '{}' [Priority: {}] for device '{}'", saved.getTitle(), saved.getPriority(), deviceId);

        return saved;
    }

    private String mapRecommendedActionToInterventionType(String action, int failureRisk) {
        return switch (action) {
            case "PROFESSIONAL_SERVICE" -> "PROFESSIONAL_SERVICE";
            case "REPAIR_NOW" -> failureRisk >= 70 ? "URGENT_REPAIR" : "PREVENTIVE_REPAIR";
            case "MAINTENANCE_REQUIRED" -> "MAINTENANCE";
            case "REFURBISH" -> "REFURBISH";
            case "REPLACE" -> "REPLACE";
            case "RECYCLE" -> "RECYCLE";
            default -> "MONITOR";
        };
    }

    private int calculateUserImpact(Device device, int failureRisk) {
        int impact = 50;
        String cat = device.getCategory() != null ? device.getCategory().toLowerCase() : "other";
        if (cat.contains("phone") || cat.contains("laptop")) impact += 30;
        if (failureRisk > 60) impact += 20;
        return Math.min(100, impact);
    }

    private int calculateUrgency(String action, int failureRisk) {
        return switch (action) {
            case "PROFESSIONAL_SERVICE" -> 95;
            case "REPAIR_NOW" -> failureRisk >= 75 ? 90 : 75;
            case "MAINTENANCE_REQUIRED" -> 60;
            case "REFURBISH" -> 50;
            case "REPLACE" -> 65;
            case "RECYCLE" -> 40;
            default -> 25;
        };
    }

    private double calculateEstimatedSavings(Device device, double repairCost) {
        double repPrice = device.getPurchasePrice() != null && device.getPurchasePrice() > 0 ? device.getPurchasePrice() : 800.0;
        return Math.max(0.0, repPrice - repairCost);
    }

    private double calculateEstimatedCo2(Device device) {
        String cat = device.getCategory() != null ? device.getCategory().toLowerCase() : "other";
        return switch (cat) {
            case "laptop" -> 36.0;
            case "tablet" -> 20.0;
            case "gaming console" -> 28.0;
            case "smartwatch" -> 8.0;
            default -> 14.5;
        };
    }

    private String generateTitle(String type, String deviceName) {
        return switch (type) {
            case "URGENT_REPAIR" -> "Urgent Component Repair for " + deviceName;
            case "PREVENTIVE_REPAIR" -> "Proactive Preventative Repair for " + deviceName;
            case "MAINTENANCE" -> "Preventative Care & Calibration for " + deviceName;
            case "PROFESSIONAL_SERVICE" -> "Certified Diagnostics Required for " + deviceName;
            case "REFURBISH" -> "Hardware Refurbishment & Module Upgrade for " + deviceName;
            case "REPLACE" -> "Phased Upgrade Planning for " + deviceName;
            case "RECYCLE" -> "Responsible Circular Recycling for " + deviceName;
            default -> "Continuous Telemetry Watch for " + deviceName;
        };
    }

    private String generateDescription(String type, String deviceName, String summary) {
        return switch (type) {
            case "URGENT_REPAIR" -> "RepairVerse AI detected active hardware failure patterns. Coordinating certified component repair will prevent critical failure.";
            case "PREVENTIVE_REPAIR" -> "Early stage component degradation observed on " + deviceName + ". Servicing now preserves hardware longevity.";
            case "MAINTENANCE" -> "Routine thermal cleaning and port maintenance is overdue on " + deviceName + ".";
            case "PROFESSIONAL_SERVICE" -> "Safety or power circuit anomaly detected. Certified technician triage is strongly recommended.";
            case "REFURBISH" -> "Extending " + deviceName + " utility through battery and storage module refresh.";
            case "RECYCLE" -> "Zero-landfill certified recycling will recover precious metals from " + deviceName + ".";
            default -> summary != null ? summary : "Autonomous agent is monitoring device operational metrics.";
        };
    }
}
