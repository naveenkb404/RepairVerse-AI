package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairPlanningDto.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceLifecycleService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final CarbonImpactRepository carbonImpactRepository;

    /**
     * Computes deterministic device lifecycle assessments and multi-path scenario projections.
     */
    @Transactional(readOnly = true)
    public DeviceLifecycleAssessmentResponse getLifecycleAssessment(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);

        int ageMonths = calculateDeviceAgeMonths(device);
        Optional<DevicePrediction> predictionOpt = devicePredictionRepository.findByDeviceId(deviceId);
        Optional<DeviceHealth> healthOpt = deviceHealthRepository.findByDeviceId(deviceId);

        int healthScore = predictionOpt.map(DevicePrediction::getPredictionScore)
            .orElseGet(() -> healthOpt.map(DeviceHealth::getHealthScore).orElse(80));

        // Baseline lifespan models based on category and current health score
        int baseCategoryLifespanMonths = getBaseCategoryLifespanMonths(device.getCategory());
        int rawRemainingMonths = Math.max(1, (int) Math.round((healthScore / 100.0) * (baseCategoryLifespanMonths * 0.4)));

        int afterMaintenanceMonths = rawRemainingMonths + Math.max(6, (int) Math.round((healthScore / 100.0) * 12));
        int afterRepairMonths = rawRemainingMonths + Math.max(18, (int) Math.round((healthScore / 100.0) * 24));
        int extensionPotential = afterRepairMonths - rawRemainingMonths;

        int repairabilityScore = calculateRepairabilityScore(device.getCategory(), healthScore);
        String replacementUrgency = deriveUrgency(healthScore, rawRemainingMonths);

        // Fetch cumulative user carbon metrics
        Optional<CarbonImpact> carbonOpt = carbonImpactRepository.findByUserId(userId);
        double cumCarbon = carbonOpt.map(CarbonImpact::getCo2Saved).orElse(42.5);
        double cumEwaste = carbonOpt.map(CarbonImpact::getEwasteReduced).orElse(2.3);

        List<LifecycleScenarioResponse> scenarios = generateScenarios(device, healthScore, rawRemainingMonths, afterMaintenanceMonths, afterRepairMonths);

        return new DeviceLifecycleAssessmentResponse(
            device.getId(),
            device.getDeviceName(),
            device.getCategory() != null ? device.getCategory() : "Electronics",
            ageMonths,
            rawRemainingMonths,
            afterMaintenanceMonths,
            afterRepairMonths,
            extensionPotential,
            repairabilityScore,
            replacementUrgency,
            cumCarbon,
            cumEwaste,
            scenarios,
            LocalDateTime.now()
        );
    }

    private List<LifecycleScenarioResponse> generateScenarios(Device device, int healthScore, int currentLifespan, int maintLifespan, int repairLifespan) {
        List<LifecycleScenarioResponse> list = new ArrayList<>();
        double basePrice = device.getPurchasePrice() != null ? device.getPurchasePrice() : 750.0;
        double maintCost = Math.round(basePrice * 0.06 * 100.0) / 100.0;
        double repairCost = Math.round(basePrice * 0.18 * 100.0) / 100.0;
        double delayCost = Math.round(repairCost * 1.65 * 100.0) / 100.0;
        double replaceCost = Math.round(basePrice * 0.95 * 100.0) / 100.0;

        // Scenario 1: DO_NOTHING
        list.add(new LifecycleScenarioResponse(
            "DO_NOTHING",
            "Take No Action",
            "Hardware continues running under current thermal and component degradation curves.",
            0.0,
            currentLifespan,
            0.0,
            0.0,
            healthScore < 60 ? "CRITICAL" : "MEDIUM",
            "NOT_RECOMMENDED"
        ));

        // Scenario 2: PREVENTIVE_MAINTENANCE
        list.add(new LifecycleScenarioResponse(
            "PREVENTIVE_MAINTENANCE",
            "Proactive Maintenance",
            "Servicing thermal system, cleaning ports, and conditioning battery controller.",
            maintCost,
            maintLifespan,
            6.5,
            0.10,
            "LOW",
            healthScore >= 60 ? "RECOMMENDED" : "VIABLE"
        ));

        // Scenario 3: REPAIR_NOW
        list.add(new LifecycleScenarioResponse(
            "REPAIR_NOW",
            "Prompt Component Repair",
            "Replace degraded wear assemblies immediately before secondary damage develops.",
            repairCost,
            repairLifespan,
            24.8,
            0.30,
            "LOW",
            healthScore < 60 ? "HIGHLY_RECOMMENDED" : "ALTERNATIVE"
        ));

        // Scenario 4: DELAY_REPAIR
        list.add(new LifecycleScenarioResponse(
            "DELAY_REPAIR",
            "Defer Repair (60-90 Days)",
            "Deferred repair leads to collateral power trace stress and 65% higher component costs.",
            delayCost,
            Math.max(1, repairLifespan - 8),
            12.0,
            0.20,
            "HIGH",
            "HIGH_RISK"
        ));

        // Scenario 5: REPLACE
        list.add(new LifecycleScenarioResponse(
            "REPLACE",
            "Full Device Replacement",
            "Decommission current unit and purchase a modern replacement model.",
            replaceCost,
            48,
            -68.0, // negative carbon savings (incurs full manufacturing carbon cost)
            -0.45,
            "LOW",
            healthScore < 30 ? "CONSIDER" : "DISCOURAGED"
        ));

        return list;
    }

    private int calculateDeviceAgeMonths(Device device) {
        if (device.getPurchaseDate() == null) {
            return 20; // Default reasonable baseline if purchase date omitted
        }
        try {
            LocalDate purchase = LocalDate.parse(device.getPurchaseDate());
            long months = ChronoUnit.MONTHS.between(purchase, LocalDate.now());
            return Math.max(1, (int) months);
        } catch (Exception e) {
            return 18;
        }
    }

    private int getBaseCategoryLifespanMonths(String category) {
        if (category == null) return 48;
        return switch (category.toLowerCase()) {
            case "smartphone" -> 42;
            case "laptop" -> 60;
            case "tablet" -> 48;
            case "desktop", "workstation" -> 84;
            case "audio", "headphones" -> 36;
            case "smartwatch", "wearable" -> 36;
            case "appliance" -> 120;
            default -> 48;
        };
    }

    private int calculateRepairabilityScore(String category, int healthScore) {
        int base = switch (category != null ? category.toLowerCase() : "") {
            case "desktop" -> 92;
            case "laptop" -> 78;
            case "smartphone" -> 74;
            case "tablet" -> 65;
            case "smartwatch" -> 50;
            default -> 70;
        };
        // Modulate with current health condition
        return Math.min(100, Math.max(20, (int) Math.round((base * 0.7) + (healthScore * 0.3))));
    }

    private String deriveUrgency(int healthScore, int remainingMonths) {
        if (healthScore < 35 || remainingMonths <= 2) return "IMMEDIATE";
        if (healthScore < 55 || remainingMonths <= 5) return "HIGH";
        if (healthScore < 75 || remainingMonths <= 10) return "MEDIUM";
        return "LOW";
    }

    private Device validateDeviceOwnership(String deviceId, String userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));
    }
}
