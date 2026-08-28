package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists autonomous repair and lifecycle action plans for a device.
 * Strategies: MONITOR, PREVENTIVE_MAINTENANCE, REPAIR, REFURBISH, REPLACE, RECYCLE.
 */
@Entity
@Table(
    name = "repair_action_plans",
    indexes = {
        @Index(name = "idx_rap_user_id", columnList = "user_id"),
        @Index(name = "idx_rap_device_id", columnList = "device_id"),
        @Index(name = "idx_rap_strategy", columnList = "overall_strategy"),
        @Index(name = "idx_rap_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairActionPlan {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "overall_strategy", nullable = false, length = 50)
    private String overallStrategy;

    @Column(name = "priority_level", nullable = false, length = 20)
    @Builder.Default
    private String priorityLevel = "MEDIUM";

    @Column(name = "estimated_total_cost")
    @Builder.Default
    private Double estimatedTotalCost = 0.0;

    @Column(name = "estimated_lifecycle_extension_months")
    @Builder.Default
    private Integer estimatedLifecycleExtensionMonths = 0;

    @Column(name = "estimated_carbon_saved")
    @Builder.Default
    private Double estimatedCarbonSaved = 0.0;

    @Column(name = "estimated_ewaste_prevented")
    @Builder.Default
    private Double estimatedEwastePrevented = 0.0;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "actionPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<RepairActionStep> steps = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "plan-" + java.util.UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addStep(RepairActionStep step) {
        steps.add(step);
        step.setActionPlan(this);
    }
}
