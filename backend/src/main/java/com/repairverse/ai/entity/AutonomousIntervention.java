package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "autonomous_interventions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutonomousIntervention {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", length = 36)
    private String deviceId;

    @Column(name = "intervention_type", nullable = false, length = 50)
    private String interventionType;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "DETECTED";

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "confidence_score", nullable = false)
    @Builder.Default
    private Integer confidenceScore = 85;

    @Column(name = "priority_score", nullable = false)
    @Builder.Default
    private Integer priorityScore = 0;

    @Column(name = "estimated_cost", nullable = false)
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "estimated_savings", nullable = false)
    @Builder.Default
    private Double estimatedSavings = 0.0;

    @Column(name = "estimated_co2_impact", nullable = false)
    @Builder.Default
    private Double estimatedCo2Impact = 0.0;

    @Column(name = "recommended_action", length = 50)
    private String recommendedAction;

    @Column(name = "action_payload", columnDefinition = "TEXT")
    private String actionPayload;

    @Column(name = "requires_user_approval", nullable = false)
    @Builder.Default
    private Boolean requiresUserApproval = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (priority == null) {
            priority = "MEDIUM";
        }
        if (status == null) {
            status = "DETECTED";
        }
        if (confidenceScore == null) {
            confidenceScore = 85;
        }
        if (priorityScore == null) {
            priorityScore = 0;
        }
        if (estimatedCost == null) {
            estimatedCost = 0.0;
        }
        if (estimatedSavings == null) {
            estimatedSavings = 0.0;
        }
        if (estimatedCo2Impact == null) {
            estimatedCo2Impact = 0.0;
        }
        if (requiresUserApproval == null) {
            requiresUserApproval = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
