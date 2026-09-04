package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DecisionFactor;
import com.repairverse.ai.dto.DeviceIntelligenceDto.IntelligenceScoreBreakdown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic scoring engine for Phase 30: AI Repair Ecosystem Intelligence.
 * Evaluates 7 weighted dimensions to calculate a 0-100 unified intelligence score.
 */
@Service
@Slf4j
public class DeviceIntelligenceScoringService {

    public static final double WEIGHT_HEALTH_RELIABILITY = 0.25;
    public static final double WEIGHT_FAILURE_RISK = 0.20;
    public static final double WEIGHT_REPAIR_ECONOMICS = 0.15;
    public static final double WEIGHT_MAINTENANCE_STATUS = 0.15;
    public static final double WEIGHT_DEVICE_LONGEVITY = 0.10;
    public static final double WEIGHT_SUSTAINABILITY = 0.10;
    public static final double WEIGHT_REPAIR_HISTORY = 0.05;

    public record ScoringResult(
            int overallScore,
            String tier,
            IntelligenceScoreBreakdown breakdown,
            List<DecisionFactor> decisionFactors
    ) {}

    public ScoringResult calculateScore(
            int healthReliabilityScore,
            int failureRiskScore,
            int repairEconomicsScore,
            int maintenanceStatusScore,
            int longevityScore,
            int sustainabilityScore,
            int repairHistoryScore
    ) {
        // Clamp input sub-scores to [0, 100]
        int hScore = Math.max(0, Math.min(100, healthReliabilityScore));
        int fScore = Math.max(0, Math.min(100, failureRiskScore));
        int eScore = Math.max(0, Math.min(100, repairEconomicsScore));
        int mScore = Math.max(0, Math.min(100, maintenanceStatusScore));
        int lScore = Math.max(0, Math.min(100, longevityScore));
        int sScore = Math.max(0, Math.min(100, sustainabilityScore));
        int rScore = Math.max(0, Math.min(100, repairHistoryScore));

        double weighted = (hScore * WEIGHT_HEALTH_RELIABILITY)
                + (fScore * WEIGHT_FAILURE_RISK)
                + (eScore * WEIGHT_REPAIR_ECONOMICS)
                + (mScore * WEIGHT_MAINTENANCE_STATUS)
                + (lScore * WEIGHT_DEVICE_LONGEVITY)
                + (sScore * WEIGHT_SUSTAINABILITY)
                + (rScore * WEIGHT_REPAIR_HISTORY);

        int overallScore = Math.max(0, Math.min(100, (int) Math.round(weighted)));
        String tier = determineTier(overallScore);

        IntelligenceScoreBreakdown breakdown = new IntelligenceScoreBreakdown(
                hScore, fScore, eScore, mScore, lScore, sScore, rScore
        );

        List<DecisionFactor> factors = new ArrayList<>();
        factors.add(new DecisionFactor(
                "Health & Reliability",
                hScore,
                WEIGHT_HEALTH_RELIABILITY,
                hScore >= 75 ? "POSITIVE" : (hScore >= 50 ? "NEUTRAL" : "NEGATIVE"),
                hScore >= 75 ? "Hardware modules & battery operating within prime tolerances."
                        : (hScore >= 50 ? "Moderate degradation observed; baseline function intact."
                        : "Significant hardware degradation or battery depletion detected.")
        ));

        factors.add(new DecisionFactor(
                "Failure Risk Resilience",
                fScore,
                WEIGHT_FAILURE_RISK,
                fScore >= 75 ? "POSITIVE" : (fScore >= 50 ? "NEUTRAL" : "NEGATIVE"),
                fScore >= 75 ? "Minimal predictive breakdown hazard over next 90 days."
                        : (fScore >= 50 ? "Elevated wear patterns detected; monitor telemetry."
                        : "High probability of imminent component failure.")
        ));

        factors.add(new DecisionFactor(
                "Repair Economics",
                eScore,
                WEIGHT_REPAIR_ECONOMICS,
                eScore >= 70 ? "POSITIVE" : (eScore >= 40 ? "NEUTRAL" : "NEGATIVE"),
                eScore >= 70 ? "Repair cost is exceptionally favorable vs replacement price."
                        : (eScore >= 40 ? "Repair cost is reasonable given residual device equity."
                        : "Repair cost approaches or exceeds device residual value.")
        ));

        factors.add(new DecisionFactor(
                "Maintenance Status",
                mScore,
                WEIGHT_MAINTENANCE_STATUS,
                mScore >= 75 ? "POSITIVE" : (mScore >= 50 ? "NEUTRAL" : "NEGATIVE"),
                mScore >= 75 ? "Preventative care and calibrations are fully up to date."
                        : (mScore >= 50 ? "Routine maintenance window is currently open."
                        : "Critical preventative service or cleaning is overdue.")
        ));

        factors.add(new DecisionFactor(
                "Device Longevity",
                lScore,
                WEIGHT_DEVICE_LONGEVITY,
                lScore >= 70 ? "POSITIVE" : (lScore >= 40 ? "NEUTRAL" : "NEGATIVE"),
                lScore >= 70 ? "Device is in early/mid lifecycle with extensive remaining utility."
                        : (lScore >= 40 ? "Device is mature but still well within serviceable lifespan."
                        : "Device is nearing expected hardware design end-of-life.")
        ));

        factors.add(new DecisionFactor(
                "Sustainability Impact",
                sScore,
                WEIGHT_SUSTAINABILITY,
                sScore >= 70 ? "POSITIVE" : "NEUTRAL",
                sScore >= 70 ? "Strong circular contribution with high avoided CO2 and diverted e-waste."
                        : "Standard circular footprint; potential to extend lifecycle further."
        ));

        factors.add(new DecisionFactor(
                "Repair History Stability",
                rScore,
                WEIGHT_REPAIR_HISTORY,
                rScore >= 80 ? "POSITIVE" : (rScore >= 50 ? "NEUTRAL" : "NEGATIVE"),
                rScore >= 80 ? "Consistent repair stability with zero recurring defects."
                        : (rScore >= 50 ? "Normal servicing record with standard minor repairs."
                        : "Repeated failure history requires targeted technical evaluation.")
        ));

        return new ScoringResult(overallScore, tier, breakdown, factors);
    }

    public String determineTier(int score) {
        if (score >= 90) return "EXCEPTIONAL";
        if (score >= 75) return "HEALTHY";
        if (score >= 60) return "STABLE";
        if (score >= 40) return "AT_RISK";
        return "CRITICAL";
    }
}
