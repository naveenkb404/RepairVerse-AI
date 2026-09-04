package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "digital_twin_optimization_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwinOptimizationResult {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "recommended_strategy", nullable = false, length = 50)
    private String recommendedStrategy;

    @Column(name = "cost_score", nullable = false)
    @Builder.Default
    private Integer costScore = 85;

    @Column(name = "reliability_score", nullable = false)
    @Builder.Default
    private Integer reliabilityScore = 85;

    @Column(name = "longevity_score", nullable = false)
    @Builder.Default
    private Integer longevityScore = 85;

    @Column(name = "sustainability_score", nullable = false)
    @Builder.Default
    private Integer sustainabilityScore = 85;

    @Column(name = "optimization_score", nullable = false)
    @Builder.Default
    private Integer optimizationScore = 88;

    @Column(name = "estimated_savings", nullable = false)
    @Builder.Default
    private Double estimatedSavings = 0.0;

    @Column(name = "estimated_lifespan_gain", nullable = false)
    @Builder.Default
    private Integer estimatedLifespanGain = 0;

    @Column(name = "estimated_co2_savings", nullable = false)
    @Builder.Default
    private Double estimatedCo2Savings = 0.0;

    @Column(name = "decision_reason", nullable = false, columnDefinition = "TEXT")
    private String decisionReason;

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
