package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairMatchingDto.QuoteIntelligenceResponse;
import com.repairverse.ai.entity.RepairQuote;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairQuoteRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteIntelligenceService {

    private final RepairQuoteRepository quoteRepository;
    private final RepairShopRepository shopRepository;

    /**
     * Compute deterministic quote price intelligence and value analysis.
     */
    public QuoteIntelligenceResponse evaluateQuoteIntelligence(String quoteId, String userId) {
        RepairQuote quote = quoteRepository.findById(quoteId).orElse(null);

        if (quote == null) {
            // Check if it's a demo quote ID
            if (quoteId != null && quoteId.startsWith("demo-")) {
                return buildDemoQuoteIntelligence(quoteId);
            }
            throw new ResourceNotFoundException("Repair quote not found with ID: " + quoteId);
        }

        // Validate user ownership
        if (userId != null && !userId.equals(quote.getUserId())) {
            throw new AccessDeniedException("Unauthorized access to repair quotation");
        }

        RepairShop shop = shopRepository.findById(quote.getRepairShopId()).orElse(null);
        String shopName = (shop != null) ? shop.getShopName() : "Certified Repair Partner";

        return computeIntelligence(quote, shopName, false);
    }

    private QuoteIntelligenceResponse computeIntelligence(RepairQuote quote, String shopName, boolean isDemo) {
        double quotedCost = quote.getEstimatedCost() > 0 ? quote.getEstimatedCost() : (quote.getPartsCost() + quote.getLaborCost());
        if (quotedCost <= 0) quotedCost = 75.0;

        // Calculate or determine market benchmark
        double marketAverage = calculateMarketBenchmark(quote);
        double costDiff = quotedCost - marketAverage;
        double diffPct = (costDiff / marketAverage) * 100.0;
        diffPct = Math.round(diffPct * 10.0) / 10.0;

        String classification;
        String classificationLabel;
        int priceFairnessScore;
        List<String> insights = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (quotedCost <= 0.35 * marketAverage) {
            classification = "SUSPICIOUSLY_LOW";
            classificationLabel = "⚠️ Suspiciously Low Price";
            priceFairnessScore = 45;
            insights.add(String.format("Quoted price ($%.2f) is %.1f%% below standard market average ($%.2f).", quotedCost, Math.abs(diffPct), marketAverage));
            warnings.add("Price is unusually low for this device class. Verify whether non-OEM/salvaged parts or uncertified labor are utilized.");
            warnings.add("Request written verification of part serial numbers and warranty coverage terms before approving.");
        } else if (quotedCost <= 0.75 * marketAverage) {
            classification = "EXCELLENT_VALUE";
            classificationLabel = "🌟 Excellent Value";
            priceFairnessScore = 96;
            insights.add(String.format("High-value quote providing substantial savings of $%.2f (%.1f%% below market benchmark).", Math.abs(costDiff), Math.abs(diffPct)));
            insights.add("Competitive labor rates and direct component sourcing benefit.");
        } else if (quotedCost <= 0.92 * marketAverage) {
            classification = "GOOD_VALUE";
            classificationLabel = "✓ Good Value";
            priceFairnessScore = 90;
            insights.add(String.format("Competitively priced at $%.2f (%.1f%% lower than average market rate).", quotedCost, Math.abs(diffPct)));
            insights.add("Solid price-to-quality ratio with standard warranty.");
        } else if (quotedCost <= 1.15 * marketAverage) {
            classification = "FAIR_PRICE";
            classificationLabel = "⚖️ Fair Market Price";
            priceFairnessScore = 85;
            insights.add(String.format("Quoted cost ($%.2f) is consistent with prevailing industry standards ($%.2f avg).", quotedCost, marketAverage));
            insights.add("Standard tier labor and genuine certified replacement components.");
        } else if (quotedCost <= 1.40 * marketAverage) {
            classification = "ABOVE_MARKET";
            classificationLabel = "📈 Above Market Average";
            priceFairnessScore = 65;
            insights.add(String.format("Quoted price ($%.2f) is %.1f%% above typical market average.", quotedCost, diffPct));
            insights.add("Check if premium expedited turnaround or extended warranty (e.g. 180+ days) justifies the premium.");
        } else {
            classification = "OVERPRICED";
            classificationLabel = "⚠️ Overpriced";
            priceFairnessScore = 40;
            insights.add(String.format("Quoted price exceeds prevailing market benchmark by $%.2f (+%.1f%%).", costDiff, diffPct));
            warnings.add("We recommend comparing against other certified repair providers on the marketplace before accepting.");
        }

        // Add parts vs labor breakdown insight
        if (quote.getPartsCost() > 0 || quote.getLaborCost() > 0) {
            insights.add(String.format("Cost breakdown: $%.2f Parts + $%.2f Labor.", quote.getPartsCost(), quote.getLaborCost()));
        }

        // Warranty evaluation
        if (quote.getWarrantyDays() != null && quote.getWarrantyDays() >= 180) {
            insights.add(String.format("Includes exceptional %d-day extended warranty coverage.", quote.getWarrantyDays()));
        } else if (quote.getWarrantyDays() != null && quote.getWarrantyDays() > 0) {
            insights.add(String.format("Protected by standard %d-day parts and labor warranty.", quote.getWarrantyDays()));
        }

        return new QuoteIntelligenceResponse(
                quote.getId(),
                quote.getRepairShopId(),
                shopName,
                quotedCost,
                quote.getPartsCost(),
                quote.getLaborCost(),
                marketAverage,
                costDiff,
                diffPct,
                classification,
                classificationLabel,
                priceFairnessScore,
                insights,
                warnings,
                isDemo
        );
    }

    private double calculateMarketBenchmark(RepairQuote quote) {
        // Average benchmark calculation
        double benchmark = 80.0;
        if (quote.getRepairTitle() != null) {
            String title = quote.getRepairTitle().toLowerCase();
            if (title.contains("screen") || title.contains("display")) benchmark = 95.0;
            else if (title.contains("battery")) benchmark = 60.0;
            else if (title.contains("motherboard") || title.contains("logic board") || title.contains("board")) benchmark = 140.0;
            else if (title.contains("port") || title.contains("charging")) benchmark = 55.0;
            else if (title.contains("camera") || title.contains("sensor")) benchmark = 70.0;
        }
        return benchmark;
    }

    private QuoteIntelligenceResponse buildDemoQuoteIntelligence(String quoteId) {
        return new QuoteIntelligenceResponse(
                quoteId,
                "shop-1",
                "Apex Micro-Electronics Care",
                65.0,
                35.0,
                30.0,
                80.0,
                -15.0,
                -18.75,
                "GOOD_VALUE",
                "✓ Good Value",
                92,
                List.of(
                        "Quoted price ($65.00) is 18.8% below the regional market benchmark ($80.00).",
                        "Cost breakdown: $35.00 Parts + $30.00 Certified Labor.",
                        "Includes 90-day comprehensive component guarantee."
                ),
                List.of(),
                true
        );
    }
}
