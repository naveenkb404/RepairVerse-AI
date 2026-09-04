package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "autonomous_action_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutonomousActionPlan {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "intervention_id", nullable = false, length = 36)
    private String interventionId;

    @Column(name = "plan_name", nullable = false, length = 255)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(name = "total_steps", nullable = false)
    @Builder.Default
    private Integer totalSteps = 0;

    @Column(name = "completed_steps", nullable = false)
    @Builder.Default
    private Integer completedSteps = 0;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "PLANNED";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("stepOrder ASC")
    @Builder.Default
    private List<AutonomousActionStep> steps = new ArrayList<>();

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
        if (totalSteps == null) {
            totalSteps = 0;
        }
        if (completedSteps == null) {
            completedSteps = 0;
        }
        if (status == null) {
            status = "PLANNED";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
