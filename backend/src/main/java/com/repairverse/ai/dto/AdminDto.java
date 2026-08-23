package com.repairverse.ai.dto;

import java.util.List;

public class AdminDto {

    public record AdminUserSummary(
            String id,
            String fullName,
            String email,
            String role,
            boolean verified,
            String createdAt,
            long deviceCount
    ) {}

    public record AdminAnalyticsResponse(
            long totalUsers,
            long totalDevices,
            long totalDiagnoses,
            long totalRepairs,
            long totalBookings,
            double totalCo2AvoidedKg,
            double totalMoneySaved
    ) {}

    public record AdminReportSummary(
            String id,
            String title,
            String category,
            String generatedAt,
            String status,
            String downloadUrl
    ) {}
}
