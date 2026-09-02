package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairReputationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 26 — Repair Reviews & Reputation Controller.
 * Base path: /api/v1/marketplace/shops/{shopId}
 */
@RestController
@RequestMapping("/marketplace/shops/{shopId}")
@RequiredArgsConstructor
@Slf4j
public class RepairReviewController {

    private final RepairReputationService reputationService;

    /**
     * GET /api/v1/marketplace/shops/{shopId}/reviews
     * Public list of verified customer reviews for a repair shop.
     */
    @GetMapping("/reviews")
    public ResponseEntity<Map<String, Object>> getShopReviews(@PathVariable("shopId") String shopId) {
        List<RepairReviewResponse> reviews = reputationService.getShopReviews(shopId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", reviews,
                "count", reviews.size()
        ));
    }

    /**
     * POST /api/v1/marketplace/shops/{shopId}/reviews
     * Authenticated review submission gated by verified completed repair relationship.
     */
    @PostMapping("/reviews")
    public ResponseEntity<Map<String, Object>> submitReview(
            @PathVariable("shopId") String shopId,
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Review submitted by user '{}' for shop '{}'", userId, shopId);

        RepairReviewResponse review = reputationService.submitReview(shopId, request, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", review,
                "message", "Verified review submitted successfully"
        ));
    }

    /**
     * GET /api/v1/marketplace/shops/{shopId}/reputation
     * Public multi-dimensional reputation and rating distribution report.
     */
    @GetMapping("/reputation")
    public ResponseEntity<Map<String, Object>> getShopReputation(@PathVariable("shopId") String shopId) {
        ShopReputationResponse reputation = reputationService.getShopReputation(shopId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", reputation
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
