package com.repairverse.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "autonomous_action_steps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutonomousActionStep {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "plan_id", nullable = false, length = 36)
    private String planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", insertable = false, updatable = false)
    @JsonIgnore
    private AutonomousActionPlan plan;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "requires_approval", nullable = false)
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "action_metadata", columnDefinition = "TEXT")
    private String actionMetadata;

    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
        if (status == null) {
            status = "PENDING";
        }
        if (requiresApproval == null) {
            requiresApproval = false;
        }
        if (stepOrder == null) {
            stepOrder = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
