package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Per-user autonomous action consent and notification preferences.
 * One row per user (UNIQUE on user_id).
 * Phase 34: AI Decision Trust & Explainability Engine.
 */
@Entity
@Table(name = "user_autonomy_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAutonomyPreference {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, unique = true, length = 36)
    private String userId;

    /** Allow the Autonomous Repair Agent to create and execute action plans */
    @Column(name = "allow_autonomous_interventions", nullable = false)
    @Builder.Default
    private Boolean allowAutonomousInterventions = true;

    /** Allow automatic scheduling of maintenance tasks */
    @Column(name = "allow_auto_scheduling", nullable = false)
    @Builder.Default
    private Boolean allowAutoScheduling = false;

    /** Allow proactive health alerts and push notifications */
    @Column(name = "allow_proactive_alerts", nullable = false)
    @Builder.Default
    private Boolean allowProactiveAlerts = true;

    /** Only take autonomous action if AI confidence >= this threshold (0–100) */
    @Column(name = "min_confidence_threshold", nullable = false)
    @Builder.Default
    private Integer minConfidenceThreshold = 80;

    /** Always require human approval for autonomous actions above this cost (INR) */
    @Column(name = "require_approval_above_cost", nullable = false)
    @Builder.Default
    private Double requireApprovalAboveCost = 5000.0;

    /** DETAILED, SUMMARY, SILENT */
    @Column(name = "notification_style", nullable = false, length = 20)
    @Builder.Default
    private String notificationStyle = "DETAILED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
