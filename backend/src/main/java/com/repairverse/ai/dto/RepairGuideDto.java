package com.repairverse.ai.dto;

import java.util.List;

public class RepairGuideDto {

    public record ToolItem(
            String name,
            String description,
            boolean isRequired
    ) {}

    public record StepItem(
            int stepNumber,
            String title,
            String instructions,
            String safetyWarning,
            String imageUrl
    ) {}

    public record GuideSummaryResponse(
            String id,
            String title,
            String category,
            String difficulty,
            String estimatedTime,
            String authorName,
            int viewsCount,
            int likesCount,
            boolean isVerified,
            String createdAt
    ) {}

    public record GuideDetailResponse(
            String id,
            String title,
            String category,
            String difficulty,
            String estimatedTime,
            String guideContent,
            String authorId,
            String authorName,
            List<ToolItem> tools,
            List<StepItem> steps,
            int viewsCount,
            int likesCount,
            boolean isVerified,
            String createdAt
    ) {}

    public record CreateGuideRequest(
            String title,
            String category,
            String difficulty,
            String estimatedTime,
            String guideContent,
            List<ToolItem> tools,
            List<StepItem> steps
    ) {}
}
