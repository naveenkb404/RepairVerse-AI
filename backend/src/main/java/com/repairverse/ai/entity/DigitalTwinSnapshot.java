package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "digital_twin_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwinSnapshot {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "health_score", nullable = false)
    @Builder.Default
    private Integer healthScore = 85;

    @Column(name = "failure_risk_score", nullable = false)
    @Builder.Default
    private Integer failureRiskScore = 15;

    @Column(name = "maintenance_score", nullable = false)
    @Builder.Default
    private Integer maintenanceScore = 80;

    @Column(name = "repair_economics_score", nullable = false)
    @Builder.Default
    private Integer repairEconomicsScore = 85;

    @Column(name = "longevity_score", nullable = false)
    @Builder.Default
    private Integer longevityScore = 80;

    @Column(name = "sustainability_score", nullable = false)
    @Builder.Default
    private Integer sustainabilityScore = 85;

    @Column(name = "predicted_value", nullable = false)
    @Builder.Default
    private Double predictedValue = 0.0;

    @Column(name = "predicted_repair_cost", nullable = false)
    @Builder.Default
    private Double predictedRepairCost = 0.0;

    @Column(name = "predicted_failure_probability", nullable = false)
    @Builder.Default
    private Double predictedFailureProbability = 0.15;

    @Column(name = "simulation_confidence", nullable = false)
    @Builder.Default
    private Double simulationConfidence = 0.88;

    @Column(name = "overall_ecosystem_score", nullable = false)
    @Builder.Default
    private Integer overallEcosystemScore = 82;

    @Column(name = "snapshot_time", nullable = false)
    private LocalDateTime snapshotTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.snapshotTime == null) {
            this.snapshotTime = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
