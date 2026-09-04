package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "digital_twin_scenarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwinScenario {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "scenario_type", nullable = false, length = 50)
    private String scenarioType;

    @Column(name = "scenario_name", nullable = false, length = 255)
    private String scenarioName;

    @Column(name = "projected_health_score", nullable = false)
    @Builder.Default
    private Integer projectedHealthScore = 85;

    @Column(name = "projected_failure_risk", nullable = false)
    @Builder.Default
    private Integer projectedFailureRisk = 15;

    @Column(name = "projected_cost", nullable = false)
    @Builder.Default
    private Double projectedCost = 0.0;

    @Column(name = "projected_savings", nullable = false)
    @Builder.Default
    private Double projectedSavings = 0.0;

    @Column(name = "projected_lifespan_months", nullable = false)
    @Builder.Default
    private Integer projectedLifespanMonths = 24;

    @Column(name = "projected_co2_impact", nullable = false)
    @Builder.Default
    private Double projectedCo2Impact = 0.0;

    @Column(name = "projected_e_waste_impact", nullable = false)
    @Builder.Default
    private Double projectedEWasteImpact = 0.0;

    @Column(name = "downtime_days", nullable = false)
    @Builder.Default
    private Integer downtimeDays = 0;

    @Column(name = "overall_outcome_score", nullable = false)
    @Builder.Default
    private Integer overallOutcomeScore = 80;

    @Column(name = "simulation_confidence", nullable = false)
    @Builder.Default
    private Double simulationConfidence = 0.88;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
