package com.repairverse.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record RecommendationRequest(
        @NotBlank(message = "Diagnosis ID is required")
        String diagnosisId
) {}
