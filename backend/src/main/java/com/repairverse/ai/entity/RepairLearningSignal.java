package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_learning_signals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairLearningSignal {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private FederatedLearningBatch batch;

    @Column(name = "signal_type", nullable = false, length = 60)
    private String signalType; // FAILURE_PROBABILITY, REPAIR_SUCCESS_RATE, COST_ESTIMATION, LIFESPAN_EXTENSION, SUSTAINABILITY_GAIN

    @Column(name = "device_category", nullable = false, length = 50)
    private String deviceCategory;

    @Column(name = "component_type", nullable = false, length = 60)
    private String componentType;

    @Column(name = "failure_mode", nullable = false, length = 80)
    private String failureMode;

    @Column(name = "repair_action", nullable = false, length = 80)
    private String repairAction;

    @Column(name = "outcome_class", nullable = false, length = 40)
    private String outcomeClass;

    @Column(name = "aggregated_frequency", nullable = false)
    private Integer aggregatedFrequency;

    @Column(name = "success_rate", nullable = false)
    private Double successRate;

    @Column(name = "average_cost", nullable = false)
    private Double averageCost;

    @Column(name = "average_lifespan_gain", nullable = false)
    private Integer averageLifespanGain;

    @Column(name = "sustainability_score", nullable = false)
    private Double sustainabilityScore;

    @Column(nullable = false)
    private Double confidence;

    @Column(name = "observation_count", nullable = false)
    private Integer observationCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (outcomeClass == null) {
            outcomeClass = "SUCCESSFUL_REPAIR";
        }
        if (aggregatedFrequency == null) {
            aggregatedFrequency = 1;
        }
        if (successRate == null) {
            successRate = 0.85;
        }
        if (averageCost == null) {
            averageCost = 0.0;
        }
        if (averageLifespanGain == null) {
            averageLifespanGain = 0;
        }
        if (sustainabilityScore == null) {
            sustainabilityScore = 85.0;
        }
        if (confidence == null) {
            confidence = 0.85;
        }
        if (observationCount == null) {
            observationCount = 5;
        }
    }
}
