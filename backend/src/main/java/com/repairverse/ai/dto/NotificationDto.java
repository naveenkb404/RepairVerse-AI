package com.repairverse.ai.dto;

import java.util.List;

public class NotificationDto {

    public record NotificationResponse(
            String id,
            String userId,
            String type,
            String title,
            String message,
            Boolean isRead,
            String actionUrl,
            String actionLabel,
            String iconColor,
            String createdAt
    ) {}

    public record NotificationListResponse(
            boolean success,
            String message,
            List<NotificationResponse> data
    ) {}

    public record NotificationCountResponse(
            boolean success,
            String message,
            long unreadCount
    ) {}
}
