package com.repairverse.ai.controller;

import com.repairverse.ai.dto.RecommendationRequest;
import com.repairverse.ai.dto.RecommendationResponseDto.RecommendationResponse;
import com.repairverse.ai.service.RepairAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Repair Analysis & Recommendation REST Controller
 * Base path: /api/v1/repair-analysis
 *
 * Provides deterministic Repair vs. Replace scoring, economic savings, CO2 reduction calculations,
 * and structured repair guides based on AI Diagnosis Reports.
 */
@RestController
@RequestMapping("/repair-analysis")
@RequiredArgsConstructor
@Slf4j
public class RepairAnalysisController {

    private final RepairAnalysisService repairAnalysisService;

    /**
     * POST /api/v1/repair-analysis
     * Computes or retrieves repair-vs-replace decision and step-by-step repair guide.
     */
    @PostMapping
    public ResponseEntity<RecommendationResponse> analyze(@Valid @RequestBody RecommendationRequest request) {
        RecommendationResponse response = repairAnalysisService.generateRecommendation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/repair-analysis/{diagnosisId}
     * Retrieves existing recommendation for a diagnosis report.
     */
    @GetMapping("/{diagnosisId}")
    public ResponseEntity<RecommendationResponse> getByDiagnosisId(@PathVariable("diagnosisId") String diagnosisId) {
        RecommendationResponse response = repairAnalysisService.getRecommendationByDiagnosisId(diagnosisId);
        return ResponseEntity.ok(response);
    }
}
