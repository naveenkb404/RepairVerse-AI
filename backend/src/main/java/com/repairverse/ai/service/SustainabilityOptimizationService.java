package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.SustainabilityRecommendationDto;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic service that analyzes device ecosystem telemetry and generates personalized sustainability optimization actions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SustainabilityOptimizationService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;

    @Transactional(readOnly = true)
    public List<SustainabilityRecommendationDto> getRecommendations(String userId, String optionalDeviceId) {
        List<Device> devices;
        if (optionalDeviceId != null && !optionalDeviceId.isBlank()) {
            devices = deviceRepository.findByIdAndUserId(optionalDeviceId, userId)
                .map(List::of)
                .orElse(List.of());
        } else {
            devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        if (devices.isEmpty()) {
            return getFallbackRecommendations();
        }

        List<SustainabilityRecommendationDto> recommendations = new ArrayList<>();

        for (Device device : devices) {
            String deviceName = (device.getBrand() != null ? device.getBrand() + " " : "") +
                                (device.getModel() != null ? device.getModel() : "Device");
            
            int health = 80;
            if (device.getHealth() != null && device.getHealth().getHealthScore() != null) {
                health = device.getHealth().getHealthScore();
            } else if (device.getId() != null) {
                health = deviceHealthRepository.findByDeviceId(device.getId())
                    .map(DeviceHealth::getHealthScore)
                    .orElse(80);
            }

            String category = device.getCategory() != null ? device.getCategory().toLowerCase() : "other";

            double baseCarbon = CircularImpactService.CATEGORY_CARBON_SAVED_KG.getOrDefault(category, 45.0);
            double baseEwaste = CircularImpactService.CATEGORY_EWASTE_WEIGHT_KG.getOrDefault(category, 0.50);

            if (health < 45) {
                recommendations.add(new SustainabilityRecommendationDto(
                    "rec-" + device.getId() + "-1",
                    device.getId(),
                    deviceName,
                    "CRITICAL",
                    "Immediate Diagnostic & Repair Required",
                    "Device health score is at " + health + "%. Prompt repair will avoid complete component failure and prevent new replacement manufacturing.",
                    baseCarbon * 0.9,
                    baseEwaste,
                    8500.0,
                    "Severe hardware degradation detected. Addressing this now preserves motherboard and core assembly.",
                    "REPAIR_NOW"
                ));
            } else if (health < 70) {
                recommendations.add(new SustainabilityRecommendationDto(
                    "rec-" + device.getId() + "-2",
                    device.getId(),
                    deviceName,
                    "HIGH",
                    "Schedule Preventative Thermal & Battery Service",
                    "Proactive maintenance can recover up to 25% battery efficiency and extend lifespan by 18+ months.",
                    baseCarbon * 0.4,
                    baseEwaste * 0.5,
                    3200.0,
                    "Thermal paste degradation and battery cycle accumulation reduce system efficiency over time.",
                    "SCHEDULE_MAINTENANCE"
                ));
            } else if (health < 85) {
                recommendations.add(new SustainabilityRecommendationDto(
                    "rec-" + device.getId() + "-3",
                    device.getId(),
                    deviceName,
                    "MEDIUM",
                    "Targeted Component Upgrade / Optimization",
                    "Upgrading RAM, storage (NVMe SSD), or replacing worn contact ports boosts performance without whole-device replacement.",
                    baseCarbon * 0.5,
                    baseEwaste * 0.3,
                    4500.0,
                    "Modular component refresh provides 2-3 additional years of high-performance utility.",
                    "UPGRADE_COMPONENT"
                ));
            } else {
                recommendations.add(new SustainabilityRecommendationDto(
                    "rec-" + device.getId() + "-4",
                    device.getId(),
                    deviceName,
                    "LOW",
                    "Regular Health Monitoring & Optimal Charging Cycle",
                    "Device is in optimal condition (" + health + "% health). Keep 20-80% charge thresholds to maintain longevity.",
                    baseCarbon * 0.1,
                    0.0,
                    500.0,
                    "Good operational habits maximize the total lifecycle utility of your hardware.",
                    "MONITOR_DEVICE"
                ));
            }
        }

        // Add a circular donation/recycling opportunity if user has 2+ devices
        if (devices.size() >= 2) {
            recommendations.add(new SustainabilityRecommendationDto(
                "rec-gen-circ-1",
                null,
                "Device Fleet Ecosystem",
                "MEDIUM",
                "Certified Trade-In or Responsible Refurbishment",
                "Decommission older spare devices to certified refurbishment channels to unlock secondary lifecycle value.",
                55.0,
                1.2,
                4000.0,
                "Refurbished devices offset virgin electronics demand in secondary markets.",
                "REFURBISH_DEVICE"
            ));
        }

        return recommendations;
    }

    public static List<SustainabilityRecommendationDto> getFallbackRecommendations() {
        return List.of(
            new SustainabilityRecommendationDto(
                "rec-demo-1",
                "dev-1",
                "MacBook Pro 16\" (M1)",
                "HIGH",
                "Battery Calibration & Thermal Pad Refresh",
                "Extending battery cycle lifespan saves ~64.5kg CO₂ and delays laptop replacement by up to 2 years.",
                64.5,
                2.10,
                6500.0,
                "Battery health telemetry indicates 78% capacity with mild thermal throttling.",
                "SCHEDULE_MAINTENANCE"
            ),
            new SustainabilityRecommendationDto(
                "rec-demo-2",
                "dev-2",
                "iPhone 13 Pro",
                "MEDIUM",
                "OLED Screen Armor & Port Cleaning",
                "Preventive port de-oxidation and protective seal prevents moisture ingress.",
                12.5,
                0.24,
                1800.0,
                "Micro-debris accumulation detected in Lightning port charging pins.",
                "EXTEND_DEVICE_LIFE"
            ),
            new SustainabilityRecommendationDto(
                "rec-demo-3",
                "dev-3",
                "Legacy iPad Air 2",
                "LOW",
                "Repurpose or Donate for Educational Use",
                "Functional tablet can be converted into a smart home dashboard or donated to local schools.",
                35.0,
                0.45,
                2500.0,
                "Dormant tablet idle for over 60 days with intact display and logic board.",
                "DONATE_DEVICE"
            )
        );
    }
}
