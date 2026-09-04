package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.DigitalTwinSnapshotResponse;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DigitalTwinStateService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository healthRepository;
    private final DigitalTwinSnapshotRepository snapshotRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Constructs or refreshes the current digital twin snapshot for a device.
     */
    @Transactional
    public DigitalTwinSnapshot buildAndSaveSnapshot(String userId, Device device) {
        String deviceId = device.getId();
        Optional<DeviceHealth> healthOpt = healthRepository.findByDeviceId(deviceId);

        int healthScore = healthOpt.map(h -> h.getHealthScore() != null ? h.getHealthScore() : 80).orElse(82);
        int failureRiskScore = Math.max(5, 100 - healthScore);
        int maintenanceScore = healthOpt.map(h -> h.getBatteryHealth() != null ? h.getBatteryHealth() : 85).orElse(78);
        int repairEconomicsScore = 85;
        int longevityScore = Math.min(95, Math.max(50, healthScore + 5));
        int sustainabilityScore = 88;

        double baseValue = device.getPurchasePrice() != null ? device.getPurchasePrice() : getBaseDeviceValue(device.getCategory());
        double ageDiscount = (healthScore / 100.0) * 0.85;
        double predictedValue = Math.round(baseValue * ageDiscount * 10.0) / 10.0;
        double predictedRepairCost = Math.round((baseValue * (failureRiskScore / 100.0) * 0.35) * 10.0) / 10.0;
        double failureProbability = Math.min(0.95, Math.max(0.05, Math.round((failureRiskScore / 100.0) * 100.0) / 100.0));

        int overallEcosystemScore = (int) Math.round(
                (healthScore * 0.25) +
                ((100 - failureRiskScore) * 0.20) +
                (maintenanceScore * 0.15) +
                (repairEconomicsScore * 0.15) +
                (longevityScore * 0.15) +
                (sustainabilityScore * 0.10)
        );

        DigitalTwinSnapshot snapshot = DigitalTwinSnapshot.builder()
                .userId(userId)
                .deviceId(deviceId)
                .healthScore(healthScore)
                .failureRiskScore(failureRiskScore)
                .maintenanceScore(maintenanceScore)
                .repairEconomicsScore(repairEconomicsScore)
                .longevityScore(longevityScore)
                .sustainabilityScore(sustainabilityScore)
                .predictedValue(predictedValue)
                .predictedRepairCost(predictedRepairCost)
                .predictedFailureProbability(failureProbability)
                .simulationConfidence(0.92)
                .overallEcosystemScore(overallEcosystemScore)
                .snapshotTime(LocalDateTime.now())
                .build();

        DigitalTwinSnapshot saved = snapshotRepository.save(snapshot);
        log.info("Constructed Digital Twin snapshot '{}' for device '{}' (Ecosystem Score: {})", saved.getId(), deviceId, overallEcosystemScore);
        return saved;
    }

    @Transactional
    public DigitalTwinSnapshot buildCurrentSnapshot(String deviceId, String userId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found: " + deviceId));
        if (!device.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to device: " + deviceId);
        }
        return buildAndSaveSnapshot(userId, device);
    }

    @Transactional
    public DigitalTwinSnapshot getOrBuildLatestSnapshot(String deviceId, String userId) {
        return snapshotRepository.findFirstByDeviceIdAndUserIdOrderBySnapshotTimeDesc(deviceId, userId)
                .orElseGet(() -> buildCurrentSnapshot(deviceId, userId));
    }

    @Transactional(readOnly = true)
    public DigitalTwinSnapshotResponse getLatestSnapshot(String userId, Device device) {
        DigitalTwinSnapshot snapshot = snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(device.getId())
                .orElseGet(() -> buildAndSaveSnapshot(userId, device));
        return mapToSnapshotResponse(snapshot, device.getDeviceName(), device.getCategory());
    }

    public DigitalTwinSnapshotResponse mapToSnapshotResponse(DigitalTwinSnapshot s, String deviceName, String deviceCategory) {
        return new DigitalTwinSnapshotResponse(
                s.getId(),
                s.getDeviceId(),
                deviceName,
                deviceCategory,
                s.getHealthScore(),
                s.getFailureRiskScore(),
                s.getMaintenanceScore(),
                s.getRepairEconomicsScore(),
                s.getLongevityScore(),
                s.getSustainabilityScore(),
                s.getPredictedValue(),
                s.getPredictedRepairCost(),
                s.getPredictedFailureProbability(),
                s.getSimulationConfidence(),
                s.getOverallEcosystemScore(),
                s.getSnapshotTime() != null ? s.getSnapshotTime().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
        );
    }

    private double getBaseDeviceValue(String category) {
        if (category == null) return 80000.0;
        return switch (category.toUpperCase()) {
            case "LAPTOP", "COMPUTER" -> 85000.0;
            case "SMARTPHONE", "PHONE" -> 45000.0;
            case "AUDIO", "HEADPHONES" -> 15000.0;
            case "TABLET" -> 35000.0;
            default -> 25000.0;
        };
    }
}
