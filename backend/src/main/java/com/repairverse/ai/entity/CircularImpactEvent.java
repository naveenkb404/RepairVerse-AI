package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks individual measurable circular economy and sustainability impact events.
 */
@Entity
@Table(
    name = "circular_impact_events",
    indexes = {
        @Index(name = "idx_cie_user_id", columnList = "user_id"),
        @Index(name = "idx_cie_device_id", columnList = "device_id"),
        @Index(name = "idx_cie_event_type", columnList = "event_type"),
        @Index(name = "idx_cie_event_date", columnList = "event_date"),
        @Index(name = "idx_cie_user_date", columnList = "user_id, event_date"),
        @Index(name = "idx_cie_user_type", columnList = "user_id, event_type"),
        @Index(name = "idx_cie_device_date", columnList = "device_id, event_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircularImpactEvent {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", length = 36)
    private String deviceId;

    /**
     * REPAIR_COMPLETED | MAINTENANCE_COMPLETED | DEVICE_LIFE_EXTENDED |
     * COMPONENT_UPGRADE | DEVICE_REFURBISHED | DEVICE_DONATED |
     * DEVICE_RECYCLED | RESPONSIBLE_DISPOSAL
     */
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "carbon_saved_kg", nullable = false)
    @Builder.Default
    private Double carbonSavedKg = 0.0;

    @Column(name = "ewaste_prevented_kg", nullable = false)
    @Builder.Default
    private Double ewastePreventedKg = 0.0;

    @Column(name = "money_saved", nullable = false)
    @Builder.Default
    private Double moneySaved = 0.0;

    @Column(name = "device_life_extension_days", nullable = false)
    @Builder.Default
    private Integer deviceLifeExtensionDays = 0;

    /**
     * AUTOMATED_REPAIR | MAINTENANCE_SCHEDULE | USER_ACTION |
     * MARKETPLACE_BOOKING | PASSPORT_EVENT | MANUAL
     */
    @Column(name = "impact_source", nullable = false, length = 50)
    @Builder.Default
    private String impactSource = "MANUAL";

    @Column(name = "reference_id", length = 36)
    private String referenceId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "cie-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (eventDate == null) {
            eventDate = LocalDateTime.now();
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
