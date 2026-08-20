package com.repairverse.ai.service;

import com.repairverse.ai.dto.NotificationDto.*;
import com.repairverse.ai.entity.Notification;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = "usr-123";
    }

    @Test
    @DisplayName("Should list user notifications and unread count")
    void getUserNotifications_And_UnreadCount() {
        Notification notif = Notification.builder()
                .id("notif-1")
                .userId(userId)
                .type("repair")
                .title("Repair Complete")
                .message("Your repair is finished.")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notif));
        when(notificationRepository.countByUserIdAndIsReadFalse(userId)).thenReturn(1L);

        NotificationListResponse listResp = notificationService.getUserNotifications(userId);
        NotificationCountResponse countResp = notificationService.getUnreadCount(userId);

        assertThat(listResp.success()).isTrue();
        assertThat(listResp.data()).hasSize(1);
        assertThat(countResp.unreadCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should mark notification as read for owner, and throw 403 AccessDeniedException for non-owner")
    void markAsRead_OwnershipChecks() {
        Notification notif = Notification.builder()
                .id("notif-1")
                .userId(userId)
                .isRead(false)
                .build();

        when(notificationRepository.findById("notif-1")).thenReturn(Optional.of(notif));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        // Successful read by owner
        notificationService.markAsRead("notif-1", userId);
        assertThat(notif.getIsRead()).isTrue();

        // Attempt read by non-owner
        assertThatThrownBy(() -> notificationService.markAsRead("notif-1", "other-user"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("You do not have permission to update this notification");
    }
}
