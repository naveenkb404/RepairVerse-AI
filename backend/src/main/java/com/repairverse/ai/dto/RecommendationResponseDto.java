package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import lombok.Builder;

import java.util.List;

/**
 * Recommendation Response DTO — exactly matches frontend RepairRecommendation contract.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendationResponseDto {

    @Builder
    public record RepairStepDto(
            int stepNumber,
            String title,
            String description,
            String safetyNote,
            Integer estimatedMinutes
    ) {}

    @Builder
    public record RequiredPartDto(
            String name,
            int quantity,
            double estimatedCost,
            String partNumber
    ) {}

    @Builder
    public record RequiredToolDto(
            String name,
            String category,
            boolean essential
    ) {}

    @Builder
    public record RepairPlanDto(
            String summary,
            List<RepairStepDto> steps,
            List<RequiredPartDto> parts,
            List<RequiredToolDto> tools
    ) {}

    @Builder
    public record RepairVsReplaceDecisionDto(
            int repairScore,
            int replaceScore,
            String recommendation,
            double moneySaved,
            double carbonSaved,
            String rationale
    ) {}

    @Builder
    public record RepairRecommendationDto(
            String id,
            String diagnosisId,
            DiagnosisReportDto diagnosisReport,
            String action,
            int repairScore,
            int replaceScore,
            RepairPlanDto plan,
            RepairVsReplaceDecisionDto decision,
            String createdAt
    ) {}

    public record RecommendationResponse(
            boolean success,
            String message,
            RepairRecommendationDto data
    ) {}
}
