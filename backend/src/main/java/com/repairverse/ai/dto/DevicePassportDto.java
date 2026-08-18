package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repairverse.ai.dto.DeviceDto.DeviceDetailDto;
import com.repairverse.ai.dto.DeviceDto.DeviceHealthDto;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DevicePassportDto {

    @Builder
    public record DiagnosisSummaryDto(
            String probableIssue,
            Integer confidenceScore,
            String repairDifficulty,
            Double repairCost,
            String lastDiagnosisDate
    ) {}

    @Builder
    public record RepairSummaryDto(
            Integer repairsCompleted,
            String lastRepairDate,
            String lastRecommendedAction
    ) {}

    @Builder
    public record CarbonSummaryDto(
            Double co2SavedKg,
            Double ewasteReducedKg,
            Double moneySaved
    ) {}

    @Builder
    public record LifecycleEventDto(
            String id,
            String date,
            String title,
            String type, // "purchase" | "diagnosis" | "service" | "inspection"
            String description
    ) {}

    @Builder
    public record DevicePassportData(
            DeviceDetailDto device,
            DeviceHealthDto health,
            DiagnosisSummaryDto diagnosisSummary,
            RepairSummaryDto repairSummary,
            CarbonSummaryDto carbonSummary,
            List<LifecycleEventDto> lifecycleTimeline
    ) {}

    public record DevicePassportResponse(
            boolean success,
            String message,
            DevicePassportData data
    ) {
        public static DevicePassportResponse of(DevicePassportData data) {
            return new DevicePassportResponse(true, "Device health passport retrieved successfully", data);
        }
    }
}
