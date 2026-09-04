package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ecosystem_simulation_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcosystemSimulationEvent {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "INFO";

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "projected_month_offset", nullable = false)
    @Builder.Default
    private Integer projectedMonthOffset = 0;

    @Column(name = "estimated_financial_impact", nullable = false)
    @Builder.Default
    private Double estimatedFinancialImpact = 0.0;

    @Column(name = "mitigation_strategy", length = 255)
    private String mitigationStrategy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.severity == null) {
            this.severity = "INFO";
        }
        if (this.projectedMonthOffset == null) {
            this.projectedMonthOffset = 0;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
