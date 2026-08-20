package com.repairverse.ai.service;

import com.repairverse.ai.dto.NotificationDto.*;
import com.repairverse.ai.entity.Notification;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public NotificationListResponse getUserNotifications(String userId) {
        List<NotificationResponse> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .toList();

        return new NotificationListResponse(true, "Notifications fetched successfully", list);
    }

    @Transactional(readOnly = true)
    public NotificationCountResponse getUnreadCount(String userId) {
        long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new NotificationCountResponse(true, "Unread notification count retrieved", count);
    }

    @Transactional
    public NotificationResponse createNotification(
            String userId,
            String type,
            String title,
            String message,
            String actionUrl,
            String actionLabel,
            String iconColor
    ) {
        Notification notif = Notification.builder()
                .id("notif-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .type(type != null ? type : "system")
                .title(title)
                .message(message)
                .isRead(false)
                .actionUrl(actionUrl)
                .actionLabel(actionLabel)
                .iconColor(iconColor != null ? iconColor : "green")
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notif);
        log.info("Created notification '{}' for user '{}'", saved.getId(), userId);
        return mapToDto(saved);
    }

    @Transactional
    public void markAsRead(String notificationId, String userId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        if (!notif.getUserId().equals(userId)) {
            log.warn("Unauthorized attempt by user '{}' to mark notification '{}' of user '{}' as read",
                    userId, notificationId, notif.getUserId());
            throw new AccessDeniedException("You do not have permission to update this notification");
        }

        notif.setIsRead(true);
        notificationRepository.save(notif);
        log.info("Notification '{}' marked as read by user '{}'", notificationId, userId);
    }

    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("All notifications marked as read for user '{}'", userId);
    }

    private NotificationResponse mapToDto(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getUserId(),
                n.getType(),
                n.getTitle(),
                n.getMessage(),
                n.getIsRead(),
                n.getActionUrl(),
                n.getActionLabel(),
                n.getIconColor(),
                n.getCreatedAt() != null ? n.getCreatedAt().toString() : LocalDateTime.now().toString()
        );
    }
}
