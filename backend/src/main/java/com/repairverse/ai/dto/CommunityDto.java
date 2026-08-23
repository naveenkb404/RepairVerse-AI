package com.repairverse.ai.dto;

import java.util.List;

public class CommunityDto {

    public record ReplyResponse(
            String id,
            String authorName,
            String authorAvatar,
            String content,
            boolean isSolution,
            int likesCount,
            String createdAt
    ) {}

    public record PostSummaryResponse(
            String id,
            String authorName,
            String authorAvatar,
            String title,
            String contentSnippet,
            String category,
            String deviceModel,
            int likesCount,
            int repliesCount,
            boolean isSolved,
            String createdAt
    ) {}

    public record PostDetailResponse(
            String id,
            String authorName,
            String authorAvatar,
            String title,
            String content,
            String category,
            String deviceModel,
            int likesCount,
            int repliesCount,
            boolean isSolved,
            String createdAt,
            List<ReplyResponse> replies
    ) {}

    public record CreatePostRequest(
            String title,
            String content,
            String category,
            String deviceModel
    ) {}

    public record CreateReplyRequest(
            String content
    ) {}
}
