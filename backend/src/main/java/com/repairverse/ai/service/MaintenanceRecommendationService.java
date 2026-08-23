package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DevicePredictionRepository;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates maintenance recommendations for a user's device fleet,
 * prioritised by risk level derived from stored predictions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceRecommendationService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;

    @Transactional(readOnly = true)
    public List<MaintenanceRecommendation> getRecommendationsForUser(String userId) {
        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<MaintenanceRecommendation> recommendations = new ArrayList<>();
        int idx = 0;

        for (Device device : devices) {
            DeviceHealth health = deviceHealthRepository.findByDeviceId(device.getId()).orElse(null);
            var prediction = devicePredictionRepository.findByDeviceId(device.getId()).orElse(null);
            String riskLevel = prediction != null ? prediction.getRiskLevel() : "LOW";
            String fault = prediction != null ? prediction.getPrimaryFaultType() : "General Wear";

            List<MaintenanceRecommendation> deviceRecs = buildForDevice(device, health, riskLevel, fault, idx);
            recommendations.addAll(deviceRecs);
            idx += deviceRecs.size();
        }

        return recommendations;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecommendation> getDemoRecommendations() {
        return List.of(
                new MaintenanceRecommendation(
                        "rec-demo-1",
                        "Battery Replacement Recommended",
                        "Your device's battery is showing signs of degradation. Replacing it will restore full battery life.",
                        "HIGH",
                        "Battery",
                        "$45 - $85",
                        "1-2 hours",
                        "Restores full battery life, improves performance",
                        List.of("Book appointment at certified repair shop",
                                "Request genuine OEM battery",
                                "Test battery after replacement")
                ),
                new MaintenanceRecommendation(
                        "rec-demo-2",
                        "Clean Cooling System",
                        "Accumulated dust can cause overheating, reducing CPU performance and lifespan.",
                        "MEDIUM",
                        "Cleaning",
                        "$20 - $40",
                        "30 minutes",
                        "Reduces thermal throttling, extends device lifespan",
                        List.of("Power off the device",
                                "Use compressed air to clear vents",
                                "Clean fan blades gently")
                ),
                new MaintenanceRecommendation(
                        "rec-demo-3",
                        "Software & Firmware Update",
                        "Running outdated firmware can expose security vulnerabilities and reduce performance.",
                        "LOW",
                        "Software",
                        "Free",
                        "15 minutes",
                        "Security patches, bug fixes, performance improvements",
                        List.of("Connect to stable Wi-Fi",
                                "Backup data before updating",
                                "Install all pending updates")
                )
        );
    }

    private List<MaintenanceRecommendation> buildForDevice(
            Device device, DeviceHealth health,
            String riskLevel, String faultType, int startIdx) {

        List<MaintenanceRecommendation> recs = new ArrayList<>();

        switch (riskLevel) {
            case "CRITICAL" -> recs.add(new MaintenanceRecommendation(
                    "rec-" + (startIdx + 1),
                    "CRITICAL: Immediate Repair Required — " + device.getDeviceName(),
                    "Predictive analysis indicates imminent failure. Immediate professional repair is essential.",
                    "CRITICAL",
                    "Hardware",
                    "$150 - $350",
                    "Same day",
                    "Prevents total device failure and data loss",
                    List.of(
                            "Back up all data immediately",
                            "Schedule emergency repair appointment",
                            "Address: " + faultType,
                            "Ask technician for extended warranty on repairs"
                    )
            ));
            case "HIGH" -> recs.add(new MaintenanceRecommendation(
                    "rec-" + (startIdx + 1),
                    "Schedule Maintenance: " + device.getDeviceName(),
                    "Device shows HIGH risk signals. Proactive maintenance prevents failure within weeks.",
                    "HIGH",
                    "Hardware",
                    "$80 - $180",
                    "1-3 days",
                    "Avoids emergency repair and data loss",
                    List.of(
                            "Book maintenance within 2 weeks",
                            "Monitor performance daily",
                            "Address: " + faultType
                    )
            ));
            case "MEDIUM" -> recs.add(new MaintenanceRecommendation(
                    "rec-" + (startIdx + 1),
                    "Routine Check-Up: " + device.getDeviceName(),
                    "Device is in moderate health. A preventive check-up is recommended this month.",
                    "MEDIUM",
                    "General",
                    "$30 - $80",
                    "1 hour",
                    "Extends device lifespan by 12-18 months",
                    List.of("Schedule routine inspection", "Clean ports and vents", "Run diagnostics")
            ));
            default -> {
                // LOW/HEALTHY — only add a general tip
                if (recs.isEmpty()) {
                    recs.add(new MaintenanceRecommendation(
                            "rec-" + (startIdx + 1),
                            "Keep It Great: " + device.getDeviceName(),
                            "Your device is in excellent health. Maintain it with these simple habits.",
                            "LOW",
                            "General",
                            "$0",
                            "Ongoing",
                            "Maintains device health score above 90",
                            List.of("Update software regularly", "Use protective case", "Service every 12 months")
                    ));
                }
            }
        }

        if (health != null && health.getBatteryHealth() != null && health.getBatteryHealth() < 70) {
            recs.add(new MaintenanceRecommendation(
                    "rec-battery-" + device.getId().substring(0, 4),
                    "Battery Degraded: " + device.getDeviceName(),
                    "Battery capacity at " + health.getBatteryHealth() + "%. Replacement extends device life by 2+ years.",
                    "HIGH",
                    "Battery",
                    "$40 - $100",
                    "1-2 hours",
                    "Restores full battery life",
                    List.of("Book battery replacement at certified shop", "Request OEM battery",
                            "Calibrate battery after replacement")
            ));
        }

        return recs;
    }
}
