package com.repairverse.ai.dto;

public class UserProfileDto {

    public record UserPreferencesDto(
            boolean notifications,
            boolean newsletter,
            String theme,
            String language
    ) {}

    public record ProfileResponse(
            String id,
            String fullName,
            String email,
            String role,
            String avatarUrl,
            String phone,
            String location,
            String bio,
            String joinedAt,
            String lastLogin,
            boolean verified,
            UserPreferencesDto preferences,
            long totalDevices,
            long totalRepairs,
            double totalCarbonSaved,
            double totalMoneySaved
    ) {}

    public record UpdateProfileRequest(
            String fullName,
            String phone,
            String location,
            String bio,
            String avatarUrl
    ) {}
}
