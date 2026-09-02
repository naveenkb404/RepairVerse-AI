package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Phase 26 — Repair Quote Service.
 *
 * Handles formal repair quotation requests, quotation lifecycle (REQUESTED, SUBMITTED,
 * ACCEPTED, REJECTED), and multi-quote comparative analytics with deterministic value scoring.
 *
 * Deterministic Value Score Formula (0–100):
 *   - Price Competitiveness (Max 35 pts): Lower total cost compared to reference market rate
 *   - Warranty Protection (Max 25 pts): 180+ days (25 pts), 90+ days (20 pts), 30+ days (10 pts)
 *   - Turnaround Speed (Max 20 pts): ≤2 hrs (20 pts), ≤6 hrs (15 pts), ≤24 hrs (10 pts)
 *   - Shop Trust Alignment (Max 20 pts): Proportion of shop's trust score
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairQuoteService {

    private final RepairQuoteRepository quoteRepository;
    private final DeviceRepository deviceRepository;
    private final RepairShopRepository repairShopRepository;
    private final RepairShopProfileRepository profileRepository;

    /**
     * User creates a repair quote request for a specific shop or general marketplace broadcast.
     */
    @Transactional
    public RepairQuoteResponse requestQuote(RequestQuoteRequest req, String userId) {
        Device device = deviceRepository.findByIdAndUserId(req.deviceId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + req.deviceId()));

        RepairShop shop = repairShopRepository.findById(req.repairShopId())
                .orElseThrow(() -> new ResourceNotFoundException("Repair shop not found: " + req.repairShopId()));

        // Synthesize baseline estimated costs based on device type or user budget
        double baseCost = req.userBudget() != null && req.userBudget() > 0 ? req.userBudget() : 110.0;
        double parts = Math.round(baseCost * 0.55 * 100.0) / 100.0;
        double labor = Math.round(baseCost * 0.45 * 100.0) / 100.0;

        RepairQuote quote = RepairQuote.builder()
                .id("quote-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .deviceId(device.getId())
                .repairShopId(shop.getId())
                .diagnosisId(req.diagnosisId())
                .recommendationId(req.recommendationId())
                .repairTitle(req.repairTitle() != null ? req.repairTitle() : "Hardware Diagnostic & Component Servicing")
                .problemSummary(req.problemSummary() != null ? req.problemSummary() : "Comprehensive physical hardware assessment and precision repair.")
                .estimatedCost(baseCost)
                .minimumCost(baseCost * 0.85)
                .maximumCost(baseCost * 1.15)
                .estimatedDurationHours(3.0)
                .partsCost(parts)
                .laborCost(labor)
                .warrantyDays(90)
                .status("REQUESTED")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        RepairQuote saved = quoteRepository.save(quote);
        log.info("Quote '{}' requested by user '{}' for shop '{}'", saved.getId(), userId, shop.getId());

        return mapToResponse(saved, device.getDeviceName(), shop.getShopName());
    }

    /**
     * Retrieve all quotes belonging to the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<RepairQuoteResponse> getUserQuotes(String userId) {
        List<RepairQuote> quotes = quoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return quotes.stream().map(this::enrichQuote).collect(Collectors.toList());
    }

    /**
     * Retrieve single quote (ownership protected).
     */
    @Transactional(readOnly = true)
    public RepairQuoteResponse getQuoteDetails(String quoteId, String userId) {
        RepairQuote quote = quoteRepository.findByIdAndUserId(quoteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found or not owned by user: " + quoteId));
        return enrichQuote(quote);
    }

    /**
     * Accept a quote and mark status as ACCEPTED.
     */
    @Transactional
    public RepairQuoteResponse acceptQuote(String quoteId, String userId) {
        RepairQuote quote = quoteRepository.findByIdAndUserId(quoteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found or not owned by user: " + quoteId));

        if (!List.of("REQUESTED", "SUBMITTED").contains(quote.getStatus())) {
            throw new IllegalStateException("Cannot accept quote in status: " + quote.getStatus());
        }

        quote.setStatus("ACCEPTED");
        RepairQuote updated = quoteRepository.save(quote);
        log.info("Quote '{}' accepted by user '{}'", quoteId, userId);
        return enrichQuote(updated);
    }

    /**
     * Reject a quote and mark status as REJECTED.
     */
    @Transactional
    public RepairQuoteResponse rejectQuote(String quoteId, String userId) {
        RepairQuote quote = quoteRepository.findByIdAndUserId(quoteId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found or not owned by user: " + quoteId));

        if (!List.of("REQUESTED", "SUBMITTED").contains(quote.getStatus())) {
            throw new IllegalStateException("Cannot reject quote in status: " + quote.getStatus());
        }

        quote.setStatus("REJECTED");
        RepairQuote updated = quoteRepository.save(quote);
        log.info("Quote '{}' rejected by user '{}'", quoteId, userId);
        return enrichQuote(updated);
    }

    /**
     * Compare multiple quotations side-by-side with deterministic value scores and highlight tags.
     */
    @Transactional(readOnly = true)
    public QuoteComparisonResponse compareQuotes(List<String> quoteIds, String userId) {
        List<RepairQuote> quotes;
        if (quoteIds == null || quoteIds.isEmpty()) {
            quotes = quoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else {
            quotes = quoteRepository.findByIdInAndUserId(quoteIds, userId);
        }

        if (quotes.isEmpty()) {
            return new QuoteComparisonResponse(Collections.emptyList(), null, null, null, null, List.of("No quotes available for comparison."), false);
        }

        List<RepairQuoteResponse> responses = quotes.stream().map(this::enrichQuote).toList();

        // Identify highlights
        String bestValueId = responses.stream()
                .max(Comparator.comparingInt(RepairQuoteResponse::valueScore))
                .map(RepairQuoteResponse::id).orElse(null);

        String lowestPriceId = responses.stream()
                .min(Comparator.comparingDouble(RepairQuoteResponse::estimatedCost))
                .map(RepairQuoteResponse::id).orElse(null);

        String longestWarrantyId = responses.stream()
                .max(Comparator.comparingInt(RepairQuoteResponse::warrantyDays))
                .map(RepairQuoteResponse::id).orElse(null);

        List<String> insights = new ArrayList<>();
        insights.add(String.format("Compared %d quotation(s) with multi-factor price and quality analysis.", responses.size()));
        if (bestValueId != null) {
            insights.add("Best Value recommendation balances warranty duration, transparent labor fees, and provider reputation.");
        }

        return new QuoteComparisonResponse(
                responses,
                bestValueId,
                lowestPriceId,
                longestWarrantyId,
                bestValueId,
                insights,
                false
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal Helper & Value Scoring Engine
    // ─────────────────────────────────────────────────────────────────────────

    private RepairQuoteResponse enrichQuote(RepairQuote q) {
        String devName = deviceRepository.findById(q.getDeviceId()).map(Device::getDeviceName).orElse("Device");
        String shopName = repairShopRepository.findById(q.getRepairShopId()).map(RepairShop::getShopName).orElse("Repair Provider");
        return mapToResponse(q, devName, shopName);
    }

    private RepairQuoteResponse mapToResponse(RepairQuote q, String devName, String shopName) {
        int valueScore = calculateValueScore(q);
        String valueRating = valueScore >= 80 ? "EXCELLENT" :
                valueScore >= 60 ? "GOOD" :
                valueScore >= 40 ? "FAIR" : "POOR";

        return new RepairQuoteResponse(
                q.getId(),
                q.getUserId(),
                q.getDeviceId(),
                devName,
                q.getRepairShopId(),
                shopName,
                q.getDiagnosisId(),
                q.getRecommendationId(),
                q.getRepairTitle(),
                q.getProblemSummary(),
                q.getEstimatedCost(),
                q.getMinimumCost(),
                q.getMaximumCost(),
                q.getEstimatedDurationHours(),
                q.getPartsCost(),
                q.getLaborCost(),
                q.getWarrantyDays(),
                q.getStatus(),
                valueScore,
                valueRating,
                q.getCreatedAt(),
                q.getExpiresAt(),
                false
        );
    }

    public int calculateValueScore(RepairQuote q) {
        int score = 0;

        // 1. Price Competitiveness (Max 35 pts) - Assume benchmark ~ $150
        double cost = q.getEstimatedCost();
        if (cost <= 60.0) score += 35;
        else if (cost <= 100.0) score += 28;
        else if (cost <= 150.0) score += 22;
        else if (cost <= 220.0) score += 15;
        else score += 8;

        // 2. Warranty (Max 25 pts)
        int warranty = q.getWarrantyDays();
        if (warranty >= 180) score += 25;
        else if (warranty >= 90) score += 20;
        else if (warranty >= 30) score += 12;
        else score += 5;

        // 3. Turnaround Speed (Max 20 pts)
        double duration = q.getEstimatedDurationHours();
        if (duration <= 2.0) score += 20;
        else if (duration <= 5.0) score += 15;
        else if (duration <= 24.0) score += 10;
        else score += 5;

        // 4. Shop Trust Baseline (Max 20 pts)
        Optional<RepairShopProfile> profile = profileRepository.findByRepairShopId(q.getRepairShopId());
        double rating = profile.map(RepairShopProfile::getAverageRating).orElse(4.5);
        int trustBonus = (int) Math.round((rating / 5.0) * 20);
        score += trustBonus;

        return Math.min(Math.max(score, 0), 100);
    }
}
