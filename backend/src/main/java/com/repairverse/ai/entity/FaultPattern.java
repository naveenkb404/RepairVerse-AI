package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Curated fault-pattern library entry.
 * Rows are seeded via Flyway V8 and can be managed by admins.
 * The scoring engine uses these patterns as weighted rules during evaluation.
 */
@Entity
@Table(
    name = "fault_patterns",
    indexes = {
        @Index(name = "idx_fp_category",   columnList = "device_category"),
        @Index(name = "idx_fp_fault_type", columnList = "fault_type"),
        @Index(name = "idx_fp_active",     columnList = "is_active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaultPattern {

    @Id
    @Column(length = 36)
    private String id;

    /** Device category this pattern applies to (e.g., "Smartphone", "Laptop") — null means all */
    @Column(name = "device_category", length = 50)
    private String deviceCategory;

    /** Brand this pattern applies to — null means all brands */
    @Column(name = "device_brand", length = 100)
    private String deviceBrand;

    /** Human-readable fault name (e.g., "Battery Degradation") */
    @Column(name = "fault_type", nullable = false, length = 100)
    private String faultType;

    /** Detailed description of the failure mode */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Minimum device age (years) for this pattern to activate.
     * E.g., battery degradation commonly starts after 2 years.
     */
    @Column(name = "min_device_age_years")
    @Builder.Default
    private Integer minDeviceAgeYears = 0;

    /**
     * Health-score threshold below which this pattern contributes to risk.
     * E.g., if healthScore < 60 → activate this pattern.
     */
    @Column(name = "health_score_threshold")
    @Builder.Default
    private Integer healthScoreThreshold = 60;

    /**
     * Weight applied to the risk calculation (1-10).
     * Higher weight → more severe penalty to prediction score.
     */
    @Column(name = "risk_weight", nullable = false)
    @Builder.Default
    private Integer riskWeight = 5;

    /** Typical repair cost range minimum (USD) */
    @Column(name = "typical_cost_min")
    private Double typicalCostMin;

    /** Typical repair cost range maximum (USD) */
    @Column(name = "typical_cost_max")
    private Double typicalCostMax;

    /** JSON-serialised list of recommended preventive actions */
    @Column(name = "preventive_actions", columnDefinition = "TEXT")
    private String preventiveActions;

    /** Whether this pattern is currently used by the scoring engine */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
