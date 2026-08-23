package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RepairCostDto.*;
import com.repairverse.ai.service.RepairCostEstimatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Repair Cost Estimator REST Controller
 * Base path: /api/v1/repair-cost-estimate
 */
@RestController
@RequestMapping("/repair-cost-estimate")
@RequiredArgsConstructor
@Slf4j
public class RepairCostEstimatorController {

    private final RepairCostEstimatorService repairCostEstimatorService;

    /**
     * POST /api/v1/repair-cost-estimate
     * Calculate multi-channel repair cost breakdown (DIY vs Local vs Authorized)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> calculateCostEstimate(@RequestBody CostEstimateRequest request) {
        CostEstimateResponse response = repairCostEstimatorService.calculateEstimate(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Repair cost estimation computed successfully"
        ));
    }

    /**
     * GET /api/v1/repair-cost-estimate/categories
     * Return supported hardware categories and common repair issues
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getSupportedCategories() {
        List<CategoryIssueBaseline> categories = repairCostEstimatorService.getSupportedCategories();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", categories,
                "message", "Supported categories loaded successfully"
        ));
    }
}
