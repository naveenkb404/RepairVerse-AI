package com.repairverse.ai.dto;

import java.util.List;

public class RepairCostDto {

    public record CostEstimateRequest(
            String category,
            String deviceModel,
            String issueType,
            String deviceAgeYears
    ) {}

    public record CostOption(
            String channel,
            String channelDescription,
            double partsCost,
            double laborCost,
            double totalCost,
            String estimatedDuration,
            String warrantyPeriod,
            String recommendedTier
    ) {}

    public record CostEstimateResponse(
            String category,
            String deviceModel,
            String issueType,
            double marketReplacementValue,
            CostOption diyOption,
            CostOption localTechOption,
            CostOption authorizedServiceOption,
            double maxSavingsDollars,
            double maxSavingsPercent,
            String recommendation,
            List<String> suggestedParts
    ) {}

    public record CategoryIssueBaseline(
            String category,
            List<String> issues
    ) {}
}
