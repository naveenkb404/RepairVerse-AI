package com.repairverse.ai.repository;

import com.repairverse.ai.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Notification> findByIdAndUserId(String id, String userId);
    long countByUserIdAndIsReadFalse(String userId);
    boolean existsByUserIdAndTitleContainingAndCreatedAtAfter(String userId, String title, java.time.LocalDateTime createdAt);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") String userId);
}
