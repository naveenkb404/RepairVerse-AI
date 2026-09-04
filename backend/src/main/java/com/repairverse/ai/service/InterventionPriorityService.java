package com.repairverse.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Phase 31: Intervention Priority Engine.
 * Deterministic 0-100 priority calculation across 6 weighted dimensions.
 */
@Service
@Slf4j
public class InterventionPriorityService {

    public static final double WEIGHT_FAILURE_RISK = 0.25;
    public static final double WEIGHT_USER_IMPACT = 0.20;
    public static final double WEIGHT_URGENCY = 0.20;
    public static final double WEIGHT_FINANCIAL_RISK = 0.15;
    public static final double WEIGHT_REPAIR_OPPORTUNITY = 0.10;
    public static final double WEIGHT_SUSTAINABILITY = 0.10;

    public record PriorityResult(
            int priorityScore,
            String priorityTier
    ) {}

    public PriorityResult calculatePriority(
            int failureRiskScore,
            int userImpactScore,
            int urgencyScore,
            int financialRiskScore,
            int repairOpportunityScore,
            int sustainabilityScore
    ) {
        // Clamp inputs to [0, 100]
        int fScore = Math.max(0, Math.min(100, failureRiskScore));
        int uScore = Math.max(0, Math.min(100, userImpactScore));
        int urgScore = Math.max(0, Math.min(100, urgencyScore));
        int finScore = Math.max(0, Math.min(100, financialRiskScore));
        int repScore = Math.max(0, Math.min(100, repairOpportunityScore));
        int susScore = Math.max(0, Math.min(100, sustainabilityScore));

        double weighted = (fScore * WEIGHT_FAILURE_RISK)
                + (uScore * WEIGHT_USER_IMPACT)
                + (urgScore * WEIGHT_URGENCY)
                + (finScore * WEIGHT_FINANCIAL_RISK)
                + (repScore * WEIGHT_REPAIR_OPPORTUNITY)
                + (susScore * WEIGHT_SUSTAINABILITY);

        int priorityScore = Math.max(0, Math.min(100, (int) Math.round(weighted)));
        String priorityTier = determinePriorityTier(priorityScore);

        return new PriorityResult(priorityScore, priorityTier);
    }

    public String determinePriorityTier(int score) {
        if (score >= 90) return "CRITICAL";
        if (score >= 70) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }
}
