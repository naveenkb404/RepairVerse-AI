package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persists deterministic proactive maintenance and care schedules for a device.
 *
 * Maintenance Types: INSPECTION, CLEANING, BATTERY_CHECK, SOFTWARE_MAINTENANCE,
 *                    PREVENTIVE_REPAIR, COMPONENT_REPLACEMENT, PROFESSIONAL_SERVICE
 * Priority Levels:   CRITICAL, HIGH, MEDIUM, LOW
 * Status:            UPCOMING, DUE, OVERDUE, COMPLETED, SKIPPED, CANCELLED
 */
@Entity
@Table(
    name = "maintenance_schedules",
    indexes = {
        @Index(name = "idx_ms_user_id",     columnList = "user_id"),
        @Index(name = "idx_ms_device_id",   columnList = "device_id"),
        @Index(name = "idx_ms_due_date",    columnList = "due_date"),
        @Index(name = "idx_ms_status",      columnList = "status"),
        @Index(name = "idx_ms_device_user", columnList = "device_id, user_id"),
        @Index(name = "idx_ms_user_status", columnList = "user_id, status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceSchedule {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "device_name", nullable = false, length = 150)
    private String deviceName;

    @Column(name = "device_category", length = 80)
    private String deviceCategory;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** INSPECTION | CLEANING | BATTERY_CHECK | SOFTWARE_MAINTENANCE |
     *  PREVENTIVE_REPAIR | COMPONENT_REPLACEMENT | PROFESSIONAL_SERVICE */
    @Column(name = "maintenance_type", nullable = false, length = 50)
    private String maintenanceType;

    /** CRITICAL | HIGH | MEDIUM | LOW */
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /** UPCOMING | DUE | OVERDUE | COMPLETED | SKIPPED | CANCELLED */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "UPCOMING";

    @Column(name = "estimated_cost", nullable = false)
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "estimated_duration_minutes", nullable = false)
    @Builder.Default
    private Integer estimatedDurationMinutes = 30;

    @Column(name = "estimated_carbon_savings", nullable = false)
    @Builder.Default
    private Double estimatedCarbonSavings = 0.0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "ms-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
