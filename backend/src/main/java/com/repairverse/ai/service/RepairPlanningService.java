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
public class RepairPlanningService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final AIRecommendationRepository recommendationRepository;
    private final RepairActionPlanRepository actionPlanRepository;
    private final RepairActionStepRepository actionStepRepository;
    private final NotificationRepository notificationRepository;

    /**
     * Retrieves the latest active action plan or synthesizes a fresh deterministic plan if none exists.
     */
    @Transactional
    public RepairActionPlanResponse getOrCreateActionPlan(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);

        Optional<RepairActionPlan> existingPlan = actionPlanRepository
            .findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);

        if (existingPlan.isPresent() && "ACTIVE".equalsIgnoreCase(existingPlan.get().getStatus())) {
            return mapToResponse(existingPlan.get(), device);
        }

        return generateAndPersistActionPlan(device, userId);
    }

    /**
     * Explicitly forces recalculation and persistence of the deterministic repair action plan.
     */
    @Transactional
    public RepairActionPlanResponse refreshActionPlan(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);
        return generateAndPersistActionPlan(device, userId);
    }

    /**
     * Lists all action plans belonging to the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<RepairActionPlanResponse> getUserActionPlans(String userId) {
        List<RepairActionPlan> plans = actionPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return plans.stream().map(p -> {
            Device dev = deviceRepository.findById(p.getDeviceId()).orElse(null);
            return mapToResponse(p, dev);
        }).toList();
    }

    /**
     * Core deterministic action plan synthesis engine.
     */
    @Transactional
    public RepairActionPlanResponse generateAndPersistActionPlan(Device device, String userId) {
        log.info("Synthesizing deterministic repair action plan for deviceId='{}', user='{}'", device.getId(), userId);

        Optional<DevicePrediction> predictionOpt = devicePredictionRepository.findByDeviceId(device.getId());
        Optional<DeviceHealth> healthOpt = deviceHealthRepository.findByDeviceId(device.getId());
        Optional<DiagnosisReport> diagOpt = diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc(device.getId());
        Optional<AIRecommendation> recOpt = Optional.empty();
        if (diagOpt.isPresent()) {
            recOpt = recommendationRepository.findByDiagnosisId(diagOpt.get().getId());
        }

        int healthScore = predictionOpt.map(DevicePrediction::getPredictionScore)
            .orElseGet(() -> healthOpt.map(DeviceHealth::getHealthScore).orElse(80));

        String riskLevel = predictionOpt.map(DevicePrediction::getRiskLevel)
            .orElseGet(() -> deriveRiskFromScore(healthScore));

        String strategy;
        String priorityLevel;
        double estimatedCost;
        int lifecycleExtensionMonths;
        double carbonSaved;
        double ewastePrevented;
        String strategyRationale;

        double purchasePrice = device.getPurchasePrice() != null ? device.getPurchasePrice() : 800.0;
        double estRepairCost = predictionOpt.flatMap(p -> Optional.ofNullable(p.getEstimatedRepairCost()))
            .orElseGet(() -> diagOpt.map(DiagnosisReport::getRepairCost).orElse(85.0));

        // Deterministic Strategy Selection Matrix
        if (healthScore >= 85 && ("HEALTHY".equalsIgnoreCase(riskLevel) || "LOW".equalsIgnoreCase(riskLevel))) {
            strategy = "MONITOR";
            priorityLevel = "LOW";
            estimatedCost = 0.0;
            lifecycleExtensionMonths = 6;
            carbonSaved = 1.2;
            ewastePrevented = 0.05;
            strategyRationale = "Device telemetry indicates optimal operating parameters. Regular diagnostics and baseline health monitoring are advised.";
        } else if (healthScore >= 60 && healthScore < 85) {
            strategy = "PREVENTIVE_MAINTENANCE";
            priorityLevel = "MEDIUM";
            estimatedCost = Math.max(35.0, estRepairCost * 0.4);
            lifecycleExtensionMonths = 14;
            carbonSaved = 5.8;
            ewastePrevented = 0.15;
            strategyRationale = "Moderate component wear detected. Proactive servicing, thermal cleaning, and battery health management will avert critical failure.";
        } else if (healthScore >= 35 && estRepairCost <= purchasePrice * 0.55) {
            strategy = "REPAIR";
            priorityLevel = "HIGH";
            estimatedCost = estRepairCost;
            lifecycleExtensionMonths = 24;
            carbonSaved = 28.5;
            ewastePrevented = 0.28;
            strategyRationale = "Targeted component repair is economically favorable, saving substantial replacement cost while extending hardware lifespan by up to 2 years.";
        } else if (healthScore < 35 && estRepairCost <= purchasePrice * 0.65 && isRefurbishableCategory(device.getCategory())) {
            strategy = "REFURBISH";
            priorityLevel = "HIGH";
            estimatedCost = estRepairCost * 1.2;
            lifecycleExtensionMonths = 30;
            carbonSaved = 34.0;
            ewastePrevented = 0.35;
            strategyRationale = "Complete modular overhaul and component reconditioning will restore full operational status with exceptional return on investment.";
        } else if (healthScore < 35 && estRepairCost > purchasePrice * 0.65) {
            strategy = "REPLACE";
            priorityLevel = "CRITICAL";
            estimatedCost = purchasePrice * 0.9;
            lifecycleExtensionMonths = 36;
            carbonSaved = 12.0;
            ewastePrevented = 0.20;
            strategyRationale = "Repair costs approach or exceed replacement value. Transitioning to a newer, energy-efficient model is economically recommended.";
        } else {
            strategy = "RECYCLE";
            priorityLevel = "CRITICAL";
            estimatedCost = 0.0;
            lifecycleExtensionMonths = 0;
            carbonSaved = 15.0;
            ewastePrevented = 0.40;
            strategyRationale = "Catastrophic degradation or hazardous battery defect detected. Responsible e-waste recovery and material recycling is required.";
        }

        // Build and Persist Action Plan
        RepairActionPlan plan = RepairActionPlan.builder()
            .userId(userId)
            .deviceId(device.getId())
            .overallStrategy(strategy)
            .priorityLevel(priorityLevel)
            .estimatedTotalCost(estimatedCost)
            .estimatedLifecycleExtensionMonths(lifecycleExtensionMonths)
            .estimatedCarbonSaved(carbonSaved)
            .estimatedEwastePrevented(ewastePrevented)
            .status("ACTIVE")
            .build();

        List<RepairActionStep> steps = buildDeterministicSteps(strategy, priorityLevel, estimatedCost, device);
        for (RepairActionStep s : steps) {
            plan.addStep(s);
        }

        RepairActionPlan savedPlan = actionPlanRepository.save(plan);

        // Dispatch High-Risk / Critical Notification Hook if necessary
        if ("CRITICAL".equalsIgnoreCase(priorityLevel) || "HIGH".equalsIgnoreCase(priorityLevel)) {
            dispatchActionPlanNotification(savedPlan, device);
        }

        return mapToResponse(savedPlan, device, strategyRationale);
    }

    private List<RepairActionStep> buildDeterministicSteps(String strategy, String priorityLevel, double totalCost, Device device) {
        List<RepairActionStep> steps = new ArrayList<>();
        int order = 1;

        switch (strategy) {
            case "MONITOR" -> {
                steps.add(createStep(order++, "Diagnostic Health Audit", "Execute full automated telemetry sweep and battery condition check.", "INSPECT", "LOW", 0.0, "5-10 mins", 0.1, true));
                steps.add(createStep(order++, "Exterior & Port Hygiene", "Clear debris from USB/charging ports and speaker grilles to prevent contact oxidation.", "CLEAN", "LOW", 0.0, "15 mins", 0.2, false));
                steps.add(createStep(order++, "Continuous Telemetry Observation", "Monitor charge rate consistency and thermal dissipation over standard usage.", "MONITOR", "LOW", 0.0, "Ongoing", 0.5, true));
            }
            case "PREVENTIVE_MAINTENANCE" -> {
                steps.add(createStep(order++, "Critical Cloud & Local Data Backup", "Secure full user profile, encryption keys, and system images before physical servicing.", "BACKUP_DATA", "HIGH", 0.0, "20-40 mins", 0.0, true));
                steps.add(createStep(order++, "Internal Thermal De-dusting & Heat-pipe Repaste", "Disassemble chassis to purge thermal exhaust channels and replenish degraded thermal compound.", "MAINTAIN", "MEDIUM", 25.0, "45 mins", 1.8, true));
                steps.add(createStep(order++, "Battery Conditioning Cycle", "Perform controlled calibration discharge to recalibrate the internal fuel-gauge controller.", "MAINTAIN", "MEDIUM", 10.0, "2 hours", 0.8, false));
                steps.add(createStep(order++, "Firmware & Power Management Optimization", "Update hardware controller microcode for optimized voltage regulation.", "INSPECT", "LOW", 0.0, "15 mins", 0.2, true));
            }
            case "REPAIR" -> {
                steps.add(createStep(order++, "Full System Image & Security Vault Backup", "Backup all sensitive partitions and disengage hardware lock state before technician hand-off.", "BACKUP_DATA", "CRITICAL", 0.0, "30-60 mins", 0.0, true));
                steps.add(createStep(order++, "Hardware Fault Isolation & Verification", "Verify component trace impedance and inspect physical stress fractures.", "INSPECT", "HIGH", 0.0, "20 mins", 0.5, true));
                steps.add(createStep(order++, "Component Replacement / Precision Solder", "Replace failed hardware assembly (screen, battery, or charge port) using precision tools.", "REPLACE_COMPONENT", "CRITICAL", totalCost * 0.85, "1-2 hours", 6.5, true));
                steps.add(createStep(order++, "Post-Repair Calibration & Stress Test", "Execute 30-minute burn-in cycle, multi-touch verification, and thermal sensor audit.", "MAINTAIN", "HIGH", totalCost * 0.15, "30 mins", 1.2, true));
                steps.add(createStep(order++, "Digital Health Passport Recalibration", "Register repair event on blockchain/health ledger to update warranty and residual value.", "MONITOR", "MEDIUM", 0.0, "5 mins", 0.5, true));
            }
            case "REFURBISH" -> {
                steps.add(createStep(order++, "Complete Cryptographic Data Migration", "Transfer all data to temporary secondary device and execute secure flash wipe.", "BACKUP_DATA", "CRITICAL", 0.0, "1 hour", 0.0, true));
                steps.add(createStep(order++, "Modular Component Disassembly", "Full teardown down to chassis level for ultrasonic cleaning and solder joint reflow.", "MAINTAIN", "HIGH", totalCost * 0.3, "2 hours", 8.0, true));
                steps.add(createStep(order++, "Multi-Assembly Replacement (Battery + Display + I/O)", "Install grade-A OEM certified replacement sub-assemblies.", "REPLACE_COMPONENT", "CRITICAL", totalCost * 0.6, "2-3 hours", 18.0, true));
                steps.add(createStep(order++, "Factory Re-certification Benchmarking", "Perform 24-hour stability burn-in and issue refurbished digital passport warranty.", "INSPECT", "HIGH", totalCost * 0.1, "4 hours", 2.0, true));
            }
            case "REPLACE" -> {
                steps.add(createStep(order++, "Full End-of-Life Cloud Migration", "Migrate all documents, profiles, and credentials to a cloud or successor device.", "BACKUP_DATA", "CRITICAL", 0.0, "1 hour", 0.0, true));
                steps.add(createStep(order++, "Cryptographic Factory Reset (NIST 800-88)", "Execute cryptographically secure flash sanitization to destroy lingering data.", "MAINTAIN", "HIGH", 0.0, "15 mins", 0.0, true));
                steps.add(createStep(order++, "Eco-Conscious Device Trade-In / Succession", "Acquire refurbished or eco-certified replacement hardware.", "BOOK_REPAIR", "HIGH", totalCost, "1-3 days", 8.0, true));
                steps.add(createStep(order++, "Direct Material Recovery Transfer", "Send decommissioned unit to certified circular reclamation facility.", "RECYCLE", "MEDIUM", 0.0, "1-2 days", 4.0, true));
            }
            default -> { // RECYCLE
                steps.add(createStep(order++, "Emergency Data Extraction (If Safe)", "Extract accessible storage volumes avoiding thermal/battery rupture hazards.", "BACKUP_DATA", "CRITICAL", 0.0, "30 mins", 0.0, true));
                steps.add(createStep(order++, "Hazardous Material Isolation", "Store in fireproof Li-Ion safety bag to neutralize swelling or thermal runaway risk.", "MAINTAIN", "CRITICAL", 0.0, "Immediate", 2.0, true));
                steps.add(createStep(order++, "Certified WEEE E-Waste Drop-Off", "Deliver device to licensed e-waste recovery center for precious metal reclamation.", "RECYCLE", "CRITICAL", 0.0, "1-2 days", 15.0, true));
            }
        }
        return steps;
    }

    private RepairActionStep createStep(int order, String title, String desc, String type, String priority, double cost, String duration, double carbon, boolean required) {
        return RepairActionStep.builder()
            .stepOrder(order)
            .title(title)
            .description(desc)
            .actionType(type)
            .priority(priority)
            .estimatedCost(cost)
            .estimatedDuration(duration)
            .carbonImpact(carbon)
            .isRequired(required)
            .status("PENDING")
            .build();
    }

    private void dispatchActionPlanNotification(RepairActionPlan plan, Device device) {
        try {
            boolean alreadyNotified = notificationRepository.existsByUserIdAndTitleContainingAndCreatedAtAfter(
                plan.getUserId(),
                device.getDeviceName(),
                LocalDateTime.now().minusHours(24)
            );
            if (!alreadyNotified) {
                Notification notif = Notification.builder()
                    .id("notif-" + java.util.UUID.randomUUID().toString())
                    .userId(plan.getUserId())
                    .title("Action Plan Required: " + device.getDeviceName())
                    .message("Urgent repair plan (" + plan.getOverallStrategy() + ") generated for " + device.getDeviceName() + ". Priority: " + plan.getPriorityLevel())
                    .type("ACTION_PLAN")
                    .isRead(false)
                    .build();
                notificationRepository.save(notif);
            }
        } catch (Exception e) {
            log.warn("Could not dispatch action plan notification: {}", e.getMessage());
        }
    }

    private Device validateDeviceOwnership(String deviceId, String userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));
    }

    private String deriveRiskFromScore(int score) {
        if (score >= 90) return "HEALTHY";
        if (score >= 75) return "LOW";
        if (score >= 55) return "MEDIUM";
        if (score >= 35) return "HIGH";
        return "CRITICAL";
    }

    private boolean isRefurbishableCategory(String category) {
        if (category == null) return false;
        String c = category.toLowerCase();
        return c.contains("laptop") || c.contains("desktop") || c.contains("smartphone") || c.contains("tablet") || c.contains("audio");
    }

    private RepairActionPlanResponse mapToResponse(RepairActionPlan plan, Device device) {
        return mapToResponse(plan, device, "Autonomous lifecycle plan generated based on predictive telemetry and hardware health.");
    }

    private RepairActionPlanResponse mapToResponse(RepairActionPlan plan, Device device, String rationale) {
        List<RepairActionStepResponse> stepDtos = plan.getSteps().stream()
            .map(s -> new RepairActionStepResponse(
                s.getId(),
                plan.getId(),
                s.getStepOrder(),
                s.getTitle(),
                s.getDescription(),
                s.getActionType(),
                s.getPriority(),
                s.getEstimatedCost(),
                s.getEstimatedDuration(),
                s.getCarbonImpact(),
                s.getIsRequired(),
                s.getStatus()
            )).toList();

        return new RepairActionPlanResponse(
            plan.getId(),
            plan.getUserId(),
            plan.getDeviceId(),
            device != null ? device.getDeviceName() : "Unknown Device",
            device != null ? device.getCategory() : "Consumer Electronics",
            plan.getOverallStrategy(),
            plan.getPriorityLevel(),
            plan.getEstimatedTotalCost(),
            plan.getEstimatedLifecycleExtensionMonths(),
            plan.getEstimatedCarbonSaved(),
            plan.getEstimatedEwastePrevented(),
            plan.getStatus(),
            rationale,
            stepDtos,
            plan.getCreatedAt(),
            plan.getUpdatedAt()
        );
    }
}
