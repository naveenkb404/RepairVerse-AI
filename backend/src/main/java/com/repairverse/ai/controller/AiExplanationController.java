package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AiExplanationDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AiExplanationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Explainable AI & Generative Intelligence REST Controller
 * Base path: /api/v1/ai-intelligence
 */
@RestController
@RequestMapping("/ai-intelligence")
@RequiredArgsConstructor
@Slf4j
public class AiExplanationController {

    private final AiExplanationService aiExplanationService;

    /**
     * GET /api/v1/ai-intelligence/device-prediction/{deviceId}
     * Generate an Explainable AI deep-dive for a device's predictive maintenance score.
     */
    @GetMapping("/device-prediction/{deviceId}")
    public ResponseEntity<Map<String, Object>> explainDevicePrediction(
            @PathVariable("deviceId") String deviceId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("AI Explanation requested for device='{}' by user='{}'", deviceId, userId);

        DeviceRiskExplanationResponse explanation = aiExplanationService.explainDevicePrediction(deviceId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", explanation,
                "message", "Device predictive risk explanation generated successfully"
        ));
    }

    /**
     * GET /api/v1/ai-intelligence/diagnosis/{diagnosisId}
     * Generate an Explainable AI breakdown of diagnosis observations and evidence.
     */
    @GetMapping("/diagnosis/{diagnosisId}")
    public ResponseEntity<Map<String, Object>> explainDiagnosis(
            @PathVariable("diagnosisId") String diagnosisId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("AI Diagnosis explanation requested for diagnosis='{}' by user='{}'", diagnosisId, userId);

        DiagnosisExplanationResponse explanation = aiExplanationService.explainDiagnosis(diagnosisId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", explanation,
                "message", "Diagnosis evidence explanation generated successfully"
        ));
    }

    /**
     * GET /api/v1/ai-intelligence/recommendation/{recommendationId}
     * Generate an Explainable AI economic and environmental rationale for repair vs replace.
     */
    @GetMapping("/recommendation/{recommendationId}")
    public ResponseEntity<Map<String, Object>> explainRecommendation(
            @PathVariable("recommendationId") String recommendationId,
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("AI Recommendation explanation requested for rec='{}' by user='{}'", recommendationId, userId);

        RecommendationExplanationResponse explanation = aiExplanationService.explainRecommendation(recommendationId, userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", explanation,
                "message", "Recommendation rationale explanation generated successfully"
        ));
    }

    /**
     * GET /api/v1/ai-intelligence/sustainability
     * Generate a personalized sustainability storytelling narrative based on circular impact metrics.
     */
    @GetMapping("/sustainability")
    public ResponseEntity<Map<String, Object>> explainSustainabilityImpact(
            @AuthenticationPrincipal UserPrincipal principal) {

        String userId = getUserId(principal);
        log.info("Sustainability storytelling narrative requested by user='{}'", userId);

        SustainabilityNarrativeResponse narrative = aiExplanationService.explainSustainabilityImpact(userId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", narrative,
                "message", "Sustainability narrative generated successfully"
        ));
    }

    private String getUserId(UserPrincipal principal) {
        if (principal == null) {
            return "usr-1";
        }
        return principal.getId();
    }
}
