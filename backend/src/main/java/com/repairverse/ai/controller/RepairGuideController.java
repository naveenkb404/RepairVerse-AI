package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairGuideDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Smart Repair Guides REST Controller
 * Base path: /api/v1/repair-guide
 */
@RestController
@RequestMapping("/repair-guide")
@RequiredArgsConstructor
@Slf4j
public class RepairGuideController {

    private final RepairGuideService repairGuideService;

    /**
     * GET /api/v1/repair-guide
     * List repair guides with optional category and difficulty filtering
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllGuides(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "difficulty", required = false) String difficulty) {
        List<GuideSummaryResponse> list = repairGuideService.getAllGuides(category, difficulty);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "message", "Repair guides retrieved successfully"
        ));
    }

    /**
     * GET /api/v1/repair-guide/{id}
     * Get detailed step-by-step repair guide by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGuideById(@PathVariable("id") String id) {
        GuideDetailResponse guide = repairGuideService.getGuideById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", guide,
                "message", "Repair guide loaded successfully"
        ));
    }

    /**
     * GET /api/v1/repair-guide/category/{category}
     * Get repair guides filtered by device category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getGuidesByCategory(@PathVariable("category") String category) {
        List<GuideSummaryResponse> list = repairGuideService.getAllGuides(category, null);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "message", "Category repair guides retrieved successfully"
        ));
    }

    /**
     * POST /api/v1/repair-guide
     * Authenticated endpoint to contribute a new repair guide
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createGuide(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CreateGuideRequest request) {
        String userId = userPrincipal != null ? userPrincipal.getId() : "usr-123";
        String authorName = userPrincipal != null ? userPrincipal.getFullName() : "Community Contributor";
        GuideDetailResponse created = repairGuideService.createGuide(userId, authorName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "data", created,
                "message", "Repair guide published successfully"
        ));
    }
}
