package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists verified multi-dimensional customer reviews for repair shops.
 *
 * Ratings are strictly 1–5 stars.
 */
@Entity
@Table(
    name = "repair_reviews",
    indexes = {
        @Index(name = "idx_rr_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rr_user_id", columnList = "user_id"),
        @Index(name = "idx_rr_booking_id", columnList = "booking_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairReview {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "booking_id", length = 36)
    private String bookingId;

    @Column(name = "rating", nullable = false)
    @Builder.Default
    private Integer rating = 5;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "repair_quality_rating", nullable = false)
    @Builder.Default
    private Integer repairQualityRating = 5;

    @Column(name = "communication_rating", nullable = false)
    @Builder.Default
    private Integer communicationRating = 5;

    @Column(name = "value_rating", nullable = false)
    @Builder.Default
    private Integer valueRating = 5;

    @Column(name = "timeliness_rating", nullable = false)
    @Builder.Default
    private Integer timelinessRating = 5;

    @Column(name = "verified_repair", nullable = false)
    @Builder.Default
    private Boolean verifiedRepair = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rev-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
