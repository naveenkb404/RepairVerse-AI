package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Phase 26 — Deterministic Repair Trust Scoring Engine.
 *
 * Computes a transparent, reproducible 0–100 trust score based on:
 *   1. Verification Status & Level (up to 30 pts)
 *   2. Customer Rating & Review Volume (up to 30 pts)
 *   3. Total Completed Repairs Experience (up to 20 pts)
 *   4. Warranty Coverage Duration (up to 10 pts)
 *   5. Responsiveness & Turnaround Speed (up to 10 pts)
 *
 * Trust Levels:
 *   85–100: EXCEPTIONAL
 *   70–84:  TRUSTED
 *   50–69:  GOOD
 *   30–49:  LIMITED
 *   0–29:   UNVERIFIED
 *
 * Gemini is NEVER used to calculate trust scores.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairTrustService {

    private final RepairShopRepository repairShopRepository;
    private final RepairShopProfileRepository profileRepository;
    private final RepairReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public TrustScoreResponse evaluateTrust(String shopId) {
        RepairShop shop = repairShopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Repair shop not found: " + shopId));

        Optional<RepairShopProfile> profileOpt = profileRepository.findByRepairShopId(shopId);

        String vStatus = profileOpt.map(RepairShopProfile::getVerificationStatus).orElse("PENDING");
        String vLevel = profileOpt.map(RepairShopProfile::getVerificationLevel).orElse("BASIC");
        double rating = profileOpt.map(RepairShopProfile::getAverageRating).orElse(shop.getRating() != null ? shop.getRating() : 4.0);
        int reviews = profileOpt.map(RepairShopProfile::getTotalReviews).orElse(shop.getReviewCount() != null ? shop.getReviewCount() : 0);
        int repairs = profileOpt.map(RepairShopProfile::getTotalRepairsCompleted).orElse(25);
        int warrantyDays = profileOpt.map(RepairShopProfile::getWarrantyDays).orElse(90);
        double responseRate = profileOpt.map(RepairShopProfile::getResponseRate).orElse(90.0);

        int score = 0;
        List<String> trustFactors = new ArrayList<>();
        List<String> positiveSignals = new ArrayList<>();
        List<String> riskSignals = new ArrayList<>();

        // 1. Verification (Max 30)
        if ("TRUSTED".equalsIgnoreCase(vStatus) || "PREMIUM".equalsIgnoreCase(vLevel)) {
            score += 30;
            positiveSignals.add("Premium certified shop verification with verified business background check.");
            trustFactors.add("Identity & Credentials Verified (+30 pts)");
        } else if ("VERIFIED".equalsIgnoreCase(vStatus) || "VERIFIED".equalsIgnoreCase(vLevel)) {
            score += 24;
            positiveSignals.add("Standard verified facility with validated workshop location.");
            trustFactors.add("Standard Verification (+24 pts)");
        } else if ("SUSPENDED".equalsIgnoreCase(vStatus)) {
            riskSignals.add("Account suspended pending compliance review.");
            trustFactors.add("Suspended Account (-20 pts)");
            score = Math.max(0, score - 20);
        } else {
            score += 10;
            riskSignals.add("Shop has pending verification status. Verification in progress.");
            trustFactors.add("Basic/Pending Verification (+10 pts)");
        }

        // 2. Customer Rating & Reviews (Max 30)
        int ratingScore = (int) Math.round((rating / 5.0) * 20); // up to 20 pts
        int reviewVolScore = Math.min(reviews / 5, 10);          // up to 10 pts
        int ratingTotal = Math.min(ratingScore + reviewVolScore, 30);
        score += ratingTotal;
        trustFactors.add(String.format("Customer Rating %.1f★ & %d Reviews (+%d pts)", rating, reviews, ratingTotal));

        if (rating >= 4.8) positiveSignals.add("Outstanding customer satisfaction score (≥4.8★).");
        else if (rating < 3.8) riskSignals.add("Average rating below industry standard (<3.8★).");

        // 3. Completed Repairs (Max 20)
        int repairScore = Math.min(repairs / 10, 20);
        score += repairScore;
        trustFactors.add(String.format("Service Volume: %d Verified Repairs (+%d pts)", repairs, repairScore));
        if (repairs >= 100) positiveSignals.add("High volume veteran provider (>100 completed repairs).");

        // 4. Warranty Coverage (Max 10)
        int warrantyScore = warrantyDays >= 180 ? 10 : warrantyDays >= 90 ? 8 : warrantyDays >= 30 ? 5 : 2;
        score += warrantyScore;
        trustFactors.add(String.format("Guaranteed Warranty: %d Days (+%d pts)", warrantyDays, warrantyScore));
        if (warrantyDays >= 180) positiveSignals.add("Extended 6-month+ hardware guarantee provided.");

        // 5. Response Performance (Max 10)
        int respScore = responseRate >= 95.0 ? 10 : responseRate >= 80.0 ? 7 : 4;
        score += respScore;
        trustFactors.add(String.format("Responsiveness: %.0f%% Response Rate (+%d pts)", responseRate, respScore));

        score = Math.min(Math.max(score, 0), 100);

        String trustLevel = score >= 85 ? "EXCEPTIONAL" :
                score >= 70 ? "TRUSTED" :
                score >= 50 ? "GOOD" :
                score >= 30 ? "LIMITED" : "UNVERIFIED";

        return new TrustScoreResponse(
                shopId,
                score,
                trustLevel,
                trustFactors,
                positiveSignals,
                riskSignals,
                false
        );
    }
}
