package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DecisionFactor;
import com.repairverse.ai.dto.DeviceIntelligenceDto.SmartDecision;
import com.repairverse.ai.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Deterministic personalized device advisor service.
 * Synthesizes hardware metrics, scores, and risks into clear natural language explanations.
 */
@Service
@Slf4j
public class PersonalizedDeviceAdvisorService {

    public record AdvisorNarrative(
            String summary,
            SmartDecision smartDecision
    ) {}

    public AdvisorNarrative generateNarrative(
            Device device,
            String recommendedAction,
            int intelligenceScore,
            String tier,
            int healthScore,
            int failureRisk,
            double estimatedRepairCost,
            double replacementPrice,
            double co2SavedKg
    ) {
        String deviceName = device != null ? device.getDeviceName() : "Your device";
        String category = device != null && device.getCategory() != null ? device.getCategory().toLowerCase() : "device";

        String summary;
        String priority;
        String decisionTitle;
        String decisionExplanation;
        Double cost = estimatedRepairCost;
        String expectedBenefit;

        switch (recommendedAction) {
            case "PROFESSIONAL_SERVICE" -> {
                priority = "URGENT";
                decisionTitle = "Book Certified Professional Diagnostics & Service";
                decisionExplanation = String.format(
                        "%s exhibits critical component distress or safety risks. A certified technician inspection is essential to prevent permanent hardware damage.",
                        deviceName
                );
                expectedBenefit = "Prevents irreversible motherboard or battery failure and restores full device safety.";
                summary = String.format(
                        "%s requires urgent professional servicing. With a current health score of %d/100 and elevated failure probability (%d%%), prompt technical intervention will protect your hardware equity.",
                        deviceName, healthScore, failureRisk
                );
            }
            case "REPAIR_NOW" -> {
                priority = "HIGH";
                decisionTitle = "Perform Targeted Component Repair";
                decisionExplanation = String.format(
                        "An active hardware failure has been identified on %s. Completing this repair immediately will extend device life by ~18-24 months at a fraction of replacement cost ($%.0f vs $%.0f).",
                        deviceName, estimatedRepairCost, replacementPrice
                );
                expectedBenefit = String.format("Extends lifespan significantly and prevents %.1f kg of lifecycle CO2 emissions.", co2SavedKg);
                summary = String.format(
                        "%s is economically and environmentally prime for repair. While health is currently at %d/100, repairing now avoids compounding damage and saves approximately $%.0f compared to buying a replacement.",
                        deviceName, healthScore, Math.max(0, replacementPrice - estimatedRepairCost)
                );
            }
            case "MAINTENANCE_REQUIRED" -> {
                priority = "MEDIUM";
                decisionTitle = "Complete Scheduled Preventative Maintenance";
                decisionExplanation = String.format(
                        "%s is in good operating condition (%d/100 health), but overdue maintenance (cleaning, thermal paste refresh, or battery calibration) will protect long-term reliability.",
                        deviceName, healthScore
                );
                cost = Math.min(estimatedRepairCost, 35.0);
                expectedBenefit = "Maintains optimal operating temperatures and prevents premature component wear.";
                summary = String.format(
                        "%s is in healthy baseline condition. Completing routine preventative maintenance will sustain its %s tier status and prevent unexpected breakdowns.",
                        deviceName, tier
                );
            }
            case "REFURBISH" -> {
                priority = "MEDIUM";
                decisionTitle = "Refurbish & Upgrade Core Modules";
                decisionExplanation = String.format(
                        "%s is a mature hardware unit that remains highly serviceable. Upgrading high-wear parts (battery/storage) provides like-new performance with massive sustainability upside.",
                        deviceName
                );
                expectedBenefit = "Adds 2+ years of seamless daily utility while diverting e-waste.";
                summary = String.format(
                        "%s has reached a mature lifecycle stage. Rather than replacing it, refurbishing key components gives maximum return on investment and conserves valuable natural resources.",
                        deviceName
                );
            }
            case "REPLACE" -> {
                priority = "MEDIUM";
                decisionTitle = "Plan Phased Device Replacement";
                decisionExplanation = String.format(
                        "Extensive wear across multiple subsystems makes comprehensive repair economically inefficient relative to %s's current market valuation.",
                        deviceName
                );
                cost = replacementPrice;
                expectedBenefit = "Upgrades performance, energy efficiency, and security with modern hardware architecture.";
                summary = String.format(
                        "Based on repair economics and health score (%d/100), repairing %s offers diminishing returns. We recommend planning a replacement or trading in responsibly.",
                        healthScore, deviceName
                );
            }
            case "RECYCLE" -> {
                priority = "LOW";
                decisionTitle = "Dispose Through Certified E-Waste Recycling";
                decisionExplanation = String.format(
                        "%s has reached the end of its viable functional lifecycle and cannot be reliably serviced. Certified recycling recovers precious metals and ensures zero landfill impact.",
                        deviceName
                );
                cost = 0.0;
                expectedBenefit = "Recovers up to 95% of recyclable metals and rare earth elements safely.";
                summary = String.format(
                        "%s is beyond practical restoration. Responsible circular recycling is the recommended next step to divert hazardous materials from landfills.",
                        deviceName
                );
            }
            case "MONITOR" -> {
                priority = "LOW";
                decisionTitle = "Monitor Telemetry & Usage Patterns";
                decisionExplanation = String.format(
                        "%s is operating stably with minor performance variations. Continue normal usage while monitoring weekly health telemetry.",
                        deviceName
                );
                cost = 0.0;
                expectedBenefit = "Zero immediate expenditure while maintaining readiness for proactive care.";
                summary = String.format(
                        "%s is in stable condition (%d/100). No active intervention is required today; continue regular usage and monitor periodic health updates.",
                        deviceName, healthScore
                );
            }
            default -> { // CONTINUE_USING
                priority = "LOW";
                decisionTitle = "Continue Normal Operation";
                decisionExplanation = String.format(
                        "%s is in top operating condition (%d/100 health, %s tier). All hardware diagnostics and maintenance schedules are optimal.",
                        deviceName, healthScore, tier
                );
                cost = 0.0;
                expectedBenefit = "Optimal performance with zero additional costs.";
                summary = String.format(
                        "%s is performing exceptionally well with minimal failure risk. Continue standard usage and follow scheduled quarterly inspections.",
                        deviceName
                );
            }
        }

        SmartDecision smartDecision = new SmartDecision(
                recommendedAction,
                priority,
                decisionTitle,
                decisionExplanation,
                cost,
                expectedBenefit
        );

        return new AdvisorNarrative(summary, smartDecision);
    }
}
