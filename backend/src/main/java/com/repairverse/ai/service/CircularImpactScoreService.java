package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.repository.SustainabilityGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic calculation engine for user Circular Impact Score (0–100) and tier evaluation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CircularImpactScoreService {

    private final CircularImpactService circularImpactService;
    private final SustainabilityGoalRepository goalRepository;

    @Transactional(readOnly = true)
    public CircularImpactScoreDto calculateScore(String userId) {
        CircularImpactMetricsDto metrics = circularImpactService.getUserImpactMetrics(userId);
        long activeGoalsCount = goalRepository.countByUserIdAndStatus(userId, "ACTIVE");
        long completedGoalsCount = goalRepository.countByUserIdAndStatus(userId, "COMPLETED");

        return computeScoreFromMetrics(metrics, activeGoalsCount, completedGoalsCount);
    }

    public CircularImpactScoreDto computeScoreFromMetrics(
        CircularImpactMetricsDto metrics,
        long activeGoals,
        long completedGoals
    ) {
        // 1. Repair & Life Extension (Max 30 points)
        int repairPts = (int) Math.min(15, metrics.totalRepairs() * 3);
        int lifePts = Math.min(10, metrics.totalLifeExtensionDays() / 50);
        int maintPts = (int) Math.min(5, Math.round(metrics.totalMaintenanceActions() * 1.5));
        int repairLifeExtensionPoints = Math.min(30, repairPts + lifePts + maintPts);

        // 2. E-Waste Prevention (Max 25 points)
        int ewastePreventionPoints = (int) Math.min(25, Math.round(metrics.totalEwastePreventedKg() * 5.0));

        // 3. Carbon Impact (Max 20 points)
        int carbonImpactPoints = (int) Math.min(20, Math.round(metrics.totalCarbonSavedKg() * 0.15));

        // 4. Responsible End-of-Life Actions (Max 15 points)
        int endOfLifePoints = (int) Math.min(15, (metrics.totalRefurbishments() * 7) + (metrics.totalResponsibleDisposals() * 5));

        // 5. Sustainability Consistency (Max 10 points)
        int goalPts = (int) Math.min(6, (activeGoals * 2) + (completedGoals * 3));
        int actionConsistencyPts = metrics.totalCircularActions() >= 3 ? 4 : (int) (metrics.totalCircularActions() * 1.3);
        int consistencyPoints = Math.min(10, goalPts + actionConsistencyPts);

        // Total score calculation (bounded between 0 and 100)
        int totalScore = Math.max(0, Math.min(100,
            repairLifeExtensionPoints + ewastePreventionPoints + carbonImpactPoints + endOfLifePoints + consistencyPoints
        ));

        // Classify Tier
        String tier = classifyTier(totalScore);

        CircularFactorBreakdownDto breakdown = new CircularFactorBreakdownDto(
            repairLifeExtensionPoints,
            ewastePreventionPoints,
            carbonImpactPoints,
            endOfLifePoints,
            consistencyPoints,
            totalScore
        );

        // Strengths & Improvement Areas
        List<String> strengths = new ArrayList<>();
        List<String> improvementAreas = new ArrayList<>();

        if (repairLifeExtensionPoints >= 22) {
            strengths.add("Outstanding hardware life extension through proactive repair and servicing.");
        } else {
            improvementAreas.add("Extend active device lifecycles with regular maintenance to boost repair points.");
        }

        if (ewastePreventionPoints >= 18) {
            strengths.add("Exceptional e-waste diversion preventing toxic landfill accumulation.");
        } else {
            improvementAreas.add("Safely refurbish or repurpose aging hardware to increase e-waste prevention score.");
        }

        if (carbonImpactPoints >= 14) {
            strengths.add("High carbon mitigation achieved by avoiding new manufactured hardware replacement.");
        } else {
            improvementAreas.add("Prioritize repairing high-footprint devices (laptops and PCs) to accelerate CO₂ savings.");
        }

        if (endOfLifePoints >= 10) {
            strengths.add("Strong circular stewardship through refurbishment and responsible recycling.");
        } else {
            improvementAreas.add("Participate in certified eco-recycling and trade-in programs for decommissioned gear.");
        }

        if (consistencyPoints >= 8) {
            strengths.add("Remarkable consistency with active sustainability goals and care schedules.");
        } else {
            improvementAreas.add("Set at least one active sustainability goal to improve consistency metrics.");
        }

        String nextBestAction = determineNextBestAction(totalScore, breakdown);

        return new CircularImpactScoreDto(
            totalScore,
            tier,
            breakdown,
            strengths,
            improvementAreas,
            nextBestAction,
            LocalDateTime.now()
        );
    }

    private String classifyTier(int score) {
        if (score >= 90) return "CIRCULAR_CHAMPION";
        if (score >= 75) return "ECO_LEADER";
        if (score >= 60) return "SUSTAINABLE";
        if (score >= 40) return "DEVELOPING";
        return "STARTING";
    }

    private String determineNextBestAction(int totalScore, CircularFactorBreakdownDto breakdown) {
        if (breakdown.consistencyPoints() < 6) {
            return "Set a 90-day Carbon Reduction or E-Waste target in Sustainability Goals.";
        }
        if (breakdown.repairLifeExtensionPoints() < 18) {
            return "Schedule an inspection for your primary device to prevent sudden hardware degradation.";
        }
        if (breakdown.endOfLifePoints() < 8) {
            return "Refurbish or recycle dormant unused electronic components in your device inventory.";
        }
        if (totalScore >= 85) {
            return "Share your Circular Impact Passport or achieve the Circular Champion milestone.";
        }
        return "Complete scheduled quarterly maintenance on active devices to maintain score momentum.";
    }
}
