package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Phase 26 — Repair Quotes REST Controller.
 * Base path: /api/v1/repair-quotes
 */
@RestController
@RequestMapping("/repair-quotes")
@RequiredArgsConstructor
@Slf4j
public class RepairQuoteController {

    private final RepairQuoteService quoteService;

    /**
     * POST /api/v1/repair-quotes/request
     * User requests a formal repair quotation for a registered/diagnosed device.
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestQuote(
            @RequestBody RequestQuoteRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Quote requested by user '{}' for device '{}' and shop '{}'", userId, request.deviceId(), request.repairShopId());

        RepairQuoteResponse quote = quoteService.requestQuote(request, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", quote,
                "message", "Repair quote requested successfully"
        ));
    }

    /**
     * GET /api/v1/repair-quotes
     * Retrieve all quotes belonging to the authenticated user.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserQuotes(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        List<RepairQuoteResponse> quotes = quoteService.getUserQuotes(userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", quotes,
                "count", quotes.size()
        ));
    }

    /**
     * GET /api/v1/repair-quotes/{id}
     * Retrieve single quote details (ownership protected).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getQuoteDetails(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        RepairQuoteResponse quote = quoteService.getQuoteDetails(id, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", quote
        ));
    }

    /**
     * GET /api/v1/repair-quotes/compare
     * Compare multiple quotations side-by-side with deterministic value scores.
     */
    @GetMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareQuotes(
            @RequestParam(name = "quoteIds", required = false) List<String> quoteIds,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        QuoteComparisonResponse comparison = quoteService.compareQuotes(quoteIds, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", comparison
        ));
    }

    /**
     * PUT /api/v1/repair-quotes/{id}/accept
     * Accept a quotation.
     */
    @PutMapping("/{id}/accept")
    public ResponseEntity<Map<String, Object>> acceptQuote(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        RepairQuoteResponse quote = quoteService.acceptQuote(id, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", quote,
                "message", "Repair quote accepted successfully"
        ));
    }

    /**
     * PUT /api/v1/repair-quotes/{id}/reject
     * Reject a quotation.
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectQuote(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        RepairQuoteResponse quote = quoteService.rejectQuote(id, userId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", quote,
                "message", "Repair quote rejected successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) return "usr-1";
        return principal.getId();
    }
}
