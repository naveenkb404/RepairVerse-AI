package com.repairverse.ai.dto;

import java.util.List;

public class CarbonDto {

    public record CarbonImpactData(
            Double co2Saved,
            Double ewasteReduced,
            Double moneySaved,
            Integer repairCount
    ) {}

    public record CarbonTrendPoint(
            String period,
            Double co2Saved,
            Double moneySaved
    ) {}

    public record CarbonRepairActivity(
            String id,
            String deviceName,
            String repairType,
            String repairDate,
            Double co2Avoided,
            Double ewasteAvoided,
            Double moneySaved
    ) {}

    public record CarbonDashboardData(
            CarbonImpactData impact,
            List<CarbonTrendPoint> trend,
            List<CarbonRepairActivity> recentActivity,
            Integer sustainabilityScore,
            Boolean isDemoData
    ) {}

    public record CarbonDashboardResponse(
            boolean success,
            String message,
            CarbonDashboardData data
    ) {}
}
