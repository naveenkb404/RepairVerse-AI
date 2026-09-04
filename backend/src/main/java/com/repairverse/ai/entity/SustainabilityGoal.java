package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores user-defined and AI-assisted sustainability targets and tracks deterministic progress.
 */
@Entity
@Table(
    name = "sustainability_goals",
    indexes = {
        @Index(name = "idx_sg_user_id", columnList = "user_id"),
        @Index(name = "idx_sg_status", columnList = "status"),
        @Index(name = "idx_sg_user_status", columnList = "user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SustainabilityGoal {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * CARBON_REDUCTION | EWASTE_PREVENTION | DEVICE_LIFE_EXTENSION | REPAIR_COUNT | MONEY_SAVED
     */
    @Column(name = "goal_type", nullable = false, length = 50)
    private String goalType;

    @Column(name = "target_value", nullable = false)
    private Double targetValue;

    @Column(name = "current_value", nullable = false)
    @Builder.Default
    private Double currentValue = 0.0;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    /**
     * ACTIVE | COMPLETED | EXPIRED | CANCELLED
     */
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "sg-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (startDate == null) {
            startDate = LocalDateTime.now();
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
