package com.repairverse.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists an individual sequential step inside a RepairActionPlan.
 * Action Types: INSPECT, BACKUP_DATA, CLEAN, MAINTAIN, REPAIR, REPLACE_COMPONENT, BOOK_REPAIR, MONITOR, RECYCLE.
 */
@Entity
@Table(
    name = "repair_action_steps",
    indexes = {
        @Index(name = "idx_ras_action_plan_id", columnList = "action_plan_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairActionStep {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_plan_id", nullable = false)
    @JsonIgnore
    private RepairActionPlan actionPlan;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(name = "estimated_cost")
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "estimated_duration", length = 50)
    private String estimatedDuration;

    @Column(name = "carbon_impact")
    @Builder.Default
    private Double carbonImpact = 0.0;

    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = true;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "step-" + java.util.UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
