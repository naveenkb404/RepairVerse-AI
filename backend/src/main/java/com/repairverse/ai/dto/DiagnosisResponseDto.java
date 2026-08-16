package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Diagnosis response DTOs — precisely matches frontend DiagnosisReport contract.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiagnosisResponseDto {

    @Builder
    public record DiagnosisReportDto(
            String id,
            String deviceId,
            String deviceCategory,
            String brand,
            String model,
            String imageUrl,
            String symptoms,
            String probableIssue,
            Integer confidenceScore,
            String repairDifficulty,
            String repairTime,
            Double repairCost,
            String safetyWarning,
            List<String> observations,
            String createdAt
    ) {}

    public record DiagnosisResponse(
            boolean success,
            String message,
            DiagnosisReportDto data
    ) {}
}
