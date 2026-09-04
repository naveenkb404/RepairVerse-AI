package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_decision_scenarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDecisionScenario {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "scenario_type", nullable = false, length = 50)
    private String scenarioType;

    @Column(name = "estimated_cost", nullable = false)
    @Builder.Default
    private Double estimatedCost = 0.0;

    @Column(name = "estimated_lifespan_months", nullable = false)
    @Builder.Default
    private Integer estimatedLifespanMonths = 0;

    @Column(name = "estimated_co2_impact", nullable = false)
    @Builder.Default
    private Double estimatedCo2Impact = 0.0;

    @Column(name = "estimated_savings", nullable = false)
    @Builder.Default
    private Double estimatedSavings = 0.0;

    @Column(name = "intelligence_score", nullable = false)
    @Builder.Default
    private Integer intelligenceScore = 0;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (estimatedCost == null) {
            estimatedCost = 0.0;
        }
        if (estimatedLifespanMonths == null) {
            estimatedLifespanMonths = 0;
        }
        if (estimatedCo2Impact == null) {
            estimatedCo2Impact = 0.0;
        }
        if (estimatedSavings == null) {
            estimatedSavings = 0.0;
        }
        if (intelligenceScore == null) {
            intelligenceScore = 0;
        }
    }
}
