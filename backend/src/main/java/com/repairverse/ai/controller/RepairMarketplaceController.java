package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.service.RepairMarketplaceService;
import com.repairverse.ai.service.RepairReputationService;
import com.repairverse.ai.service.RepairTrustService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 26 — Repair Marketplace REST Controller.
 * Base path: /api/v1/marketplace
 */
@RestController
@RequestMapping("/marketplace")
@RequiredArgsConstructor
@Slf4j
public class RepairMarketplaceController {

    private final RepairMarketplaceService marketplaceService;
    private final RepairTrustService trustService;
    private final RepairReputationService reputationService;

    /**
     * GET /api/v1/marketplace/shops
     * Discover and rank marketplace shops with optional filters.
     */
    @GetMapping("/shops")
    public ResponseEntity<Map<String, Object>> discoverShops(
            @RequestParam(name = "deviceCategory", required = false) String deviceCategory,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "minRating", required = false) Double minRating,
            @RequestParam(name = "verificationStatus", required = false) String verificationStatus,
            @RequestParam(name = "lat", required = false) Double latitude,
            @RequestParam(name = "lng", required = false) Double longitude,
            @RequestParam(name = "radius", required = false) Double radiusKm) {

        List<MarketplaceShopResponse> shops = marketplaceService.discoverShops(
                deviceCategory, brand, minRating, verificationStatus, latitude, longitude, radiusKm);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", shops,
                "count", shops.size()
        ));
    }

    /**
     * GET /api/v1/marketplace/shops/{id}
     * Retrieve single shop marketplace details, trust score, and profile.
     */
    @GetMapping("/shops/{id}")
    public ResponseEntity<Map<String, Object>> getShopDetails(@PathVariable("id") String id) {
        MarketplaceShopResponse shop = marketplaceService.getShopDetails(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", shop
        ));
    }

    /**
     * GET /api/v1/marketplace/shops/{id}/ranking
     * Retrieve transparent ranking score breakdown.
     */
    @GetMapping("/shops/{id}/ranking")
    public ResponseEntity<Map<String, Object>> getShopRanking(
            @PathVariable("id") String id,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "brand", required = false) String brand) {

        ShopRankingResponse ranking = marketplaceService.getShopRanking(id, category, brand);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", ranking
        ));
    }

    /**
     * GET /api/v1/marketplace/shops/{id}/trust
     * Retrieve deterministic trust score evaluation.
     */
    @GetMapping("/shops/{id}/trust")
    public ResponseEntity<Map<String, Object>> getShopTrust(@PathVariable("id") String id) {
        TrustScoreResponse trust = trustService.evaluateTrust(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", trust
        ));
    }
}
