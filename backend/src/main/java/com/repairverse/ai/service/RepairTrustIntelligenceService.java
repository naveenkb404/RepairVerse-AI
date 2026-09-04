package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import com.repairverse.ai.repository.MarketplaceAnomalyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Phase 28 — Deterministic Trust Intelligence Service.
 * Base 50 pts. Positive signals add up; risk signals subtract.
 * Result clipped to 0–100. Returns itemized signals for full explainability.
 *
 * Trust Tiers:
 *   90–100 → EXCEPTIONAL
 *   75–89  → HIGH
 *   60–74  → ESTABLISHED
 *   40–59  → MODERATE
 *   0–39   → LOW
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairTrustIntelligenceService {

    private final RepairServiceOutcomeRepository outcomeRepository;
    private final MarketplaceAnomalyRepository anomalyRepository;

    public TrustScoreResponse calculateTrustScore(String shopId, String shopName) {
        long total      = outcomeRepository.countByRepairShopId(shopId);
        long passed     = outcomeRepository.countSuccessfulByShopId(shopId);
        long repeat     = outcomeRepository.countRepeatRepairsByShopId(shopId);
        long warranty   = outcomeRepository.countWarrantyClaimsByShopId(shopId);
        long failed     = outcomeRepository.countFailedByShopId(shopId);
        long activeAnom = anomalyRepository.countActiveByShopId(shopId);
        Double avgSat   = outcomeRepository.avgSatisfactionByShopId(shopId);

        int score = 50; // base
        List<String> positiveSignals = new ArrayList<>();
        List<String> riskSignals     = new ArrayList<>();
        Map<String, Integer> breakdown = new LinkedHashMap<>();

        // ── Positive Signals ─────────────────────────────────────────────────
        if (total > 0) {
            double successRate = (double) passed / total;
            if (successRate >= 0.90) {
                score += 15;
                positiveSignals.add("High repair success rate (" + String.format("%.0f%%", successRate * 100) + ")");
                breakdown.put("High success rate", 15);
            } else if (successRate >= 0.80) {
                score += 10;
                positiveSignals.add("Good repair success rate (" + String.format("%.0f%%", successRate * 100) + ")");
                breakdown.put("Good success rate", 10);
            } else if (successRate >= 0.70) {
                score += 5;
                positiveSignals.add("Acceptable repair success rate (" + String.format("%.0f%%", successRate * 100) + ")");
                breakdown.put("Acceptable success rate", 5);
            }
        }

        if (avgSat != null) {
            if (avgSat >= 4.5) {
                score += 12;
                positiveSignals.add("Excellent customer satisfaction (" + String.format("%.1f", avgSat) + "/5.0)");
                breakdown.put("Excellent satisfaction", 12);
            } else if (avgSat >= 4.0) {
                score += 7;
                positiveSignals.add("Good customer satisfaction (" + String.format("%.1f", avgSat) + "/5.0)");
                breakdown.put("Good satisfaction", 7);
            }
        }

        if (total >= 100) {
            score += 8;
            positiveSignals.add("Strong service history with " + total + " verified repairs");
            breakdown.put("Verified repair history", 8);
        } else if (total >= 50) {
            score += 4;
            positiveSignals.add("Growing service history with " + total + " repairs");
            breakdown.put("Service history", 4);
        }

        // ── Risk Signals ──────────────────────────────────────────────────────
        if (total > 0) {
            double repeatRate = (double) repeat / total;
            if (repeatRate > 0.20) {
                score -= 12;
                riskSignals.add("High repeat repair rate (" + String.format("%.0f%%", repeatRate * 100) + ")");
                breakdown.put("High repeat rate", -12);
            } else if (repeatRate > 0.10) {
                score -= 6;
                riskSignals.add("Elevated repeat repair rate (" + String.format("%.0f%%", repeatRate * 100) + ")");
                breakdown.put("Elevated repeat rate", -6);
            }

            double warrantyRate = (double) warranty / total;
            if (warrantyRate > 0.15) {
                score -= 10;
                riskSignals.add("Frequent warranty claims (" + String.format("%.0f%%", warrantyRate * 100) + ")");
                breakdown.put("Frequent warranty claims", -10);
            } else if (warrantyRate > 0.08) {
                score -= 5;
                riskSignals.add("Above-average warranty claims");
                breakdown.put("Elevated warranty claims", -5);
            }

            double failureRate = (double) failed / total;
            if (failureRate > 0.15) {
                score -= 10;
                riskSignals.add("High repair failure rate (" + String.format("%.0f%%", failureRate * 100) + ")");
                breakdown.put("High failure rate", -10);
            }
        }

        if (activeAnom > 0) {
            int deduction = (int) Math.min(15, activeAnom * 5);
            score -= deduction;
            riskSignals.add(activeAnom + " active marketplace anomaly flag" + (activeAnom > 1 ? "s" : "") + " under review");
            breakdown.put("Active anomaly flags", -deduction);
        }

        score = Math.max(0, Math.min(100, score));
        String tier = classifyTrustTier(score);
        log.info("Trust score for shop '{}': {}/100 [{}]", shopId, score, tier);

        return new TrustScoreResponse(shopId, score, tier, positiveSignals, riskSignals, breakdown);
    }

    public String classifyTrustTier(int score) {
        if (score >= 90) return "EXCEPTIONAL";
        if (score >= 75) return "HIGH";
        if (score >= 60) return "ESTABLISHED";
        if (score >= 40) return "MODERATE";
        return "LOW";
    }
}
