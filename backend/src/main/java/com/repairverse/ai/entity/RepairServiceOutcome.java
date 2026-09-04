package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists verified repair service completion outcomes and post-service satisfaction.
 */
@Entity
@Table(
    name = "repair_service_outcomes",
    indexes = {
        @Index(name = "idx_rso_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rso_user_id", columnList = "user_id"),
        @Index(name = "idx_rso_device_id", columnList = "device_id"),
        @Index(name = "idx_rso_completed_at", columnList = "completed_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairServiceOutcome {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "booking_id", length = 36)
    private String bookingId;

    @Column(name = "repair_category", nullable = false, length = 80)
    private String repairCategory;

    /** COMPLETED | FAILED | CANCELLED | WARRANTY_REPAIR | REPEAT_REPAIR */
    @Column(name = "repair_status", nullable = false, length = 30)
    @Builder.Default
    private String repairStatus = "COMPLETED";

    @Column(name = "repair_successful", nullable = false)
    @Builder.Default
    private Boolean repairSuccessful = true;

    @Column(name = "warranty_claimed", nullable = false)
    @Builder.Default
    private Boolean warrantyClaimed = false;

    @Column(name = "repeat_repair_required", nullable = false)
    @Builder.Default
    private Boolean repeatRepairRequired = false;

    @Column(name = "customer_satisfaction", nullable = false)
    @Builder.Default
    private Integer customerSatisfaction = 5;

    @Column(name = "repair_cost", nullable = false)
    @Builder.Default
    private Double repairCost = 0.0;

    @Column(name = "estimated_cost", nullable = false)
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rso-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (completedAt == null) {
            completedAt = LocalDateTime.now();
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
