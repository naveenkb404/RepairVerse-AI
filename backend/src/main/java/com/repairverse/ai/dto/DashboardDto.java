package com.repairverse.ai.dto;

public class DashboardDto {

    public record DashboardStatsResponse(
            long totalDevices,
            long totalRepairs,
            double totalCarbonSaved,
            double totalMoneySaved,
            int healthScore,
            long activeRepairs
    ) {}

    public record ActivityItemResponse(
            String id,
            String type,
            String title,
            String description,
            String timestamp,
            String deviceName,
            String iconColor
    ) {}
}
