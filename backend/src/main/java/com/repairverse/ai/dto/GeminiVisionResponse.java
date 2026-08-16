package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Structured model response returned from Google Gemini AI Vision.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiVisionResponse {
    private String probableIssue;
    private Integer confidenceScore; // 0 - 100
    private String repairDifficulty; // Easy, Moderate, Hard, Complex
    private String repairTime;       // e.g. "1-2 hours"
    private Double repairCost;       // e.g. 85.0
    private String safetyWarning;    // safety considerations e.g. battery hazards, high voltage
    private List<String> observations; // itemized visual observations
}
