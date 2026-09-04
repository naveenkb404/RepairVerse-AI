package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceIntelligenceAlertResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceIntelligenceAlert;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.DeviceIntelligenceAlertRepository;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceIntelligenceAlertService {

    private final DeviceIntelligenceAlertRepository alertRepository;
    private final DeviceRepository deviceRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transactional
    public void evaluateAndGenerateAlerts(
            Device device,
            String userId,
            int healthScore,
            int failureRisk,
            String recommendedAction,
            int maintenanceScore,
            int economicScore
    ) {
        if (device == null) return;
        String deviceId = device.getId();
        String name = device.getDeviceName();

        // 1. Critical Failure Risk Alert
        if (failureRisk >= 65 || healthScore < 40) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "FAILURE_RISK",
                    failureRisk >= 80 || healthScore < 30 ? "CRITICAL" : "HIGH",
                    "Elevated Component Failure Hazard",
                    String.format("%s exhibits a %d%% failure probability. Prompt diagnostic review is advised.", name, failureRisk),
                    "PROFESSIONAL_SERVICE"
            );
        }

        // 2. Maintenance Overdue Alert
        if (maintenanceScore < 50 && healthScore >= 50) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "MAINTENANCE_REQUIRED",
                    "MEDIUM",
                    "Preventative Maintenance Window Open",
                    String.format("%s is due for routine thermal cleaning, port inspection, or calibration.", name),
                    "MAINTENANCE_REQUIRED"
            );
        }

        // 3. Repair Recommended Alert
        if ("REPAIR_NOW".equals(recommendedAction)) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "REPAIR_RECOMMENDED",
                    "HIGH",
                    "High-Return Component Repair Available",
                    String.format("Repairing %s now prevents compounding hardware deterioration and saves replacement cost.", name),
                    "REPAIR_NOW"
            );
        }

        // 4. Cost Escalation Warning
        if (economicScore < 45 && failureRisk > 50) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "COST_ESCALATION",
                    "HIGH",
                    "Repair Delay Cost Risk",
                    String.format("Delaying servicing on %s may increase secondary component repair expenses by up to 40%%.", name),
                    "REPAIR_NOW"
            );
        }

        // 5. End of Life Notice
        if ("RECYCLE".equals(recommendedAction) || (healthScore < 25 && failureRisk > 85)) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "END_OF_LIFE",
                    "MEDIUM",
                    "End of Serviceable Lifecycle",
                    String.format("%s has completed its useful functional lifecycle. Circular recycling recommended.", name),
                    "RECYCLE"
            );
        }

        // 6. Sustainability Opportunity
        if (healthScore >= 60 && ("REFURBISH".equals(recommendedAction) || "CONTINUE_USING".equals(recommendedAction))) {
            createAlertIfNotExists(
                    deviceId,
                    userId,
                    "SUSTAINABILITY_OPPORTUNITY",
                    "INFO",
                    "Carbon Avoidance Milestone Achievable",
                    String.format("Continuing or refurbishing %s keeps an estimated 15+ kg of CO2 from being emitted.", name),
                    recommendedAction
            );
        }
    }

    private void createAlertIfNotExists(
            String deviceId,
            String userId,
            String alertType,
            String severity,
            String title,
            String message,
            String recommendedAction
    ) {
        Optional<DeviceIntelligenceAlert> existing = alertRepository
                .findFirstByDeviceIdAndUserIdAndAlertTypeAndIsReadFalse(deviceId, userId, alertType);

        if (existing.isEmpty()) {
            DeviceIntelligenceAlert alert = DeviceIntelligenceAlert.builder()
                    .deviceId(deviceId)
                    .userId(userId)
                    .alertType(alertType)
                    .severity(severity)
                    .title(title)
                    .message(message)
                    .recommendedAction(recommendedAction)
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            alertRepository.save(alert);
            log.info("Generated intelligence alert '{}' for device '{}' (user: '{}')", alertType, deviceId, userId);
        }
    }

    @Transactional(readOnly = true)
    public List<DeviceIntelligenceAlertResponse> getUserAlerts(String userId) {
        List<DeviceIntelligenceAlert> alerts = alertRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return mapAlertsWithDeviceNames(alerts, userId);
    }

    @Transactional(readOnly = true)
    public List<DeviceIntelligenceAlertResponse> getDeviceAlerts(String deviceId, String userId) {
        List<DeviceIntelligenceAlert> alerts = alertRepository.findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        return mapAlertsWithDeviceNames(alerts, userId);
    }

    @Transactional
    public DeviceIntelligenceAlertResponse markAlertAsRead(String alertId, String userId) {
        DeviceIntelligenceAlert alert = alertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + alertId));

        alert.setIsRead(true);
        DeviceIntelligenceAlert updated = alertRepository.save(alert);

        String deviceName = deviceRepository.findById(updated.getDeviceId())
                .map(Device::getDeviceName)
                .orElse("Device");

        return toDto(updated, deviceName);
    }

    private List<DeviceIntelligenceAlertResponse> mapAlertsWithDeviceNames(List<DeviceIntelligenceAlert> alerts, String userId) {
        if (alerts.isEmpty()) return List.of();

        Map<String, String> deviceNameMap = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .collect(Collectors.toMap(Device::getId, Device::getDeviceName, (a, b) -> a));

        return alerts.stream()
                .map(a -> toDto(a, deviceNameMap.getOrDefault(a.getDeviceId(), "Device")))
                .toList();
    }

    private DeviceIntelligenceAlertResponse toDto(DeviceIntelligenceAlert alert, String deviceName) {
        return new DeviceIntelligenceAlertResponse(
                alert.getId(),
                alert.getDeviceId(),
                deviceName,
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getTitle(),
                alert.getMessage(),
                alert.getRecommendedAction(),
                alert.getIsRead(),
                alert.getCreatedAt() != null ? alert.getCreatedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER)
        );
    }
}
