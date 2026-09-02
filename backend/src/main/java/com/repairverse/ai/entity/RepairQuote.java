package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists formal repair quotations requested by users and submitted by repair shops.
 *
 * Status: REQUESTED, DRAFT, SUBMITTED, ACCEPTED, REJECTED, EXPIRED, CANCELLED
 */
@Entity
@Table(
    name = "repair_quotes",
    indexes = {
        @Index(name = "idx_rq_user_id", columnList = "user_id"),
        @Index(name = "idx_rq_device_id", columnList = "device_id"),
        @Index(name = "idx_rq_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rq_status", columnList = "status"),
        @Index(name = "idx_rq_user_status", columnList = "user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairQuote {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "diagnosis_id", length = 36)
    private String diagnosisId;

    @Column(name = "recommendation_id", length = 36)
    private String recommendationId;

    @Column(name = "repair_title", nullable = false, length = 200)
    private String repairTitle;

    @Column(name = "problem_summary", columnDefinition = "TEXT")
    private String problemSummary;

    @Column(name = "estimated_cost", nullable = false)
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "minimum_cost", nullable = false)
    @Builder.Default
    private Double minimumCost = 0.0;

    @Column(name = "maximum_cost", nullable = false)
    @Builder.Default
    private Double maximumCost = 0.0;

    @Column(name = "estimated_duration_hours", nullable = false)
    @Builder.Default
    private Double estimatedDurationHours = 2.0;

    @Column(name = "parts_cost", nullable = false)
    @Builder.Default
    private Double partsCost = 0.0;

    @Column(name = "labor_cost", nullable = false)
    @Builder.Default
    private Double laborCost = 0.0;

    @Column(name = "warranty_days", nullable = false)
    @Builder.Default
    private Integer warrantyDays = 90;

    /** REQUESTED | DRAFT | SUBMITTED | ACCEPTED | REJECTED | EXPIRED | CANCELLED */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "REQUESTED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "quote-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusDays(7);
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
