package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists calculated multi-dimensional quality snapshots for certified repair providers.
 */
@Entity
@Table(
    name = "repair_shop_quality_snapshots",
    indexes = {
        @Index(name = "idx_rsqs_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rsqs_calculated_at", columnList = "calculated_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairShopQualitySnapshot {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "overall_quality_score", nullable = false)
    @Builder.Default
    private Integer overallQualityScore = 85;

    @Column(name = "reliability_score", nullable = false)
    @Builder.Default
    private Integer reliabilityScore = 85;

    @Column(name = "customer_satisfaction_score", nullable = false)
    @Builder.Default
    private Integer customerSatisfactionScore = 90;

    @Column(name = "repair_success_score", nullable = false)
    @Builder.Default
    private Integer repairSuccessScore = 90;

    @Column(name = "price_fairness_score", nullable = false)
    @Builder.Default
    private Integer priceFairnessScore = 85;

    @Column(name = "service_speed_score", nullable = false)
    @Builder.Default
    private Integer serviceSpeedScore = 80;

    @Column(name = "trust_score", nullable = false)
    @Builder.Default
    private Integer trustScore = 85;

    @Column(name = "total_repairs", nullable = false)
    @Builder.Default
    private Integer totalRepairs = 0;

    @Column(name = "successful_repairs", nullable = false)
    @Builder.Default
    private Integer successfulRepairs = 0;

    @Column(name = "failed_repairs", nullable = false)
    @Builder.Default
    private Integer failedRepairs = 0;

    @Column(name = "repeat_repairs", nullable = false)
    @Builder.Default
    private Integer repeatRepairs = 0;

    @Column(name = "average_rating", nullable = false)
    @Builder.Default
    private Double averageRating = 4.5;

    @Column(name = "average_turnaround_days", nullable = false)
    @Builder.Default
    private Double averageTurnaroundDays = 1.5;

    /** ELITE | EXCELLENT | TRUSTED | STANDARD | NEEDS_IMPROVEMENT */
    @Column(name = "quality_tier", nullable = false, length = 30)
    @Builder.Default
    private String qualityTier = "TRUSTED";

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rsqs-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (calculatedAt == null) {
            calculatedAt = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
