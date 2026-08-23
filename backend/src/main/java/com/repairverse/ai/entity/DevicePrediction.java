package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Stores the latest AI-generated predictive maintenance assessment for a device.
 * One row per device — upserted by the scoring service on each evaluation.
 */
@Entity
@Table(
    name = "device_predictions",
    indexes = {
        @Index(name = "idx_dp_device_id", columnList = "device_id"),
        @Index(name = "idx_dp_user_id",   columnList = "user_id"),
        @Index(name = "idx_dp_risk_level", columnList = "risk_level"),
        @Index(name = "idx_dp_evaluated_at", columnList = "evaluated_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicePrediction {

    @Id
    @Column(length = 36)
    private String id;

    /** FK → devices.id */
    @Column(name = "device_id", nullable = false, length = 36, unique = true)
    private String deviceId;

    /** Denormalized for fast admin queries without JOIN */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * Composite health score 0-100 produced by the deterministic scoring engine.
     * Lower = more likely to fail soon.
     */
    @Column(name = "prediction_score", nullable = false)
    @Builder.Default
    private Integer predictionScore = 80;

    /**
     * Risk classification derived from predictionScore:
     * CRITICAL (0-34), HIGH (35-54), MEDIUM (55-74), LOW (75-89), HEALTHY (90-100)
     */
    @Column(name = "risk_level", nullable = false, length = 20)
    @Builder.Default
    private String riskLevel = "LOW";

    /** Days estimated until the device is likely to require maintenance */
    @Column(name = "days_to_failure_estimate")
    private Integer daysToFailureEstimate;

    /** Primary failure mode most likely to manifest (e.g., "Battery Degradation") */
    @Column(name = "primary_fault_type", length = 100)
    private String primaryFaultType;

    /** JSON-serialised list of recommended maintenance actions */
    @Column(name = "recommended_actions", columnDefinition = "TEXT")
    private String recommendedActions;

    /** JSON-serialised scoring breakdown per factor */
    @Column(name = "scoring_breakdown", columnDefinition = "TEXT")
    private String scoringBreakdown;

    /** Estimated cost of repair if failure occurs (USD) */
    @Column(name = "estimated_repair_cost")
    private Double estimatedRepairCost;

    /** Potential savings if preventive action is taken now (USD) */
    @Column(name = "preventive_savings")
    private Double preventiveSavings;

    /** CO₂ avoided (kg) if the device is repaired rather than replaced */
    @Column(name = "co2_savings_kg")
    private Double co2SavingsKg;

    /** Confidence of the prediction 0.0-1.0 */
    @Column(name = "confidence_score")
    @Builder.Default
    private Double confidenceScore = 0.80;

    /** Whether a HIGH/CRITICAL notification has been issued for this evaluation */
    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = java.util.UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (evaluatedAt == null) evaluatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
