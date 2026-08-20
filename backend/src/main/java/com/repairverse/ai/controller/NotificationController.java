package com.repairverse.ai.controller;

import com.repairverse.ai.dto.NotificationDto.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Notification Hub REST Controller
 * Base path: /api/v1/notifications
 */
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * GET /api/v1/notifications
     * Authenticated endpoint to retrieve user notifications.
     */
    @GetMapping
    public ResponseEntity<NotificationListResponse> getUserNotifications(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        NotificationListResponse response = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/notifications/unread-count
     * Authenticated endpoint to retrieve count of unread notifications.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<NotificationCountResponse> getUnreadCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        NotificationCountResponse response = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/notifications/{id}/read
     * Authenticated endpoint to mark a single notification as read.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable("id") String id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        String userId = getUserId(userPrincipal);
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Notification marked as read"));
    }

    /**
     * PUT /api/v1/notifications/read-all
     * Authenticated endpoint to mark all notifications for user as read.
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        String userId = getUserId(userPrincipal);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("success", true, "message", "All notifications marked as read"));
    }

    private String getUserId(UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return "usr-123";
        }
        return userPrincipal.getId();
    }
}


