package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "intelligence_model_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntelligenceModelVersion {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(nullable = false, unique = true, length = 50)
    private String version; // e.g. R35.4

    @Column(name = "parent_version", length = 50)
    private String parentVersion; // e.g. R35.3

    @Column(nullable = false, length = 30)
    private String status; // COLLECTING, AGGREGATED, VALIDATING, APPROVED, ACTIVE, SUPERSEDED, REJECTED, QUARANTINED

    @Column(name = "training_observations", nullable = false)
    private Integer trainingObservations;

    @Column(name = "validation_score", nullable = false)
    private Double validationScore;

    @Column(name = "trust_score", nullable = false)
    private Integer trustScore;

    @Column(name = "improvement_percentage", nullable = false)
    private Double improvementPercentage;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "retired_at")
    private LocalDateTime retiredAt;

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
        if (modelName == null) {
            modelName = "RepairVerse Federated Core";
        }
        if (status == null) {
            status = "COLLECTING";
        }
        if (trainingObservations == null) {
            trainingObservations = 0;
        }
        if (validationScore == null) {
            validationScore = 0.0;
        }
        if (trustScore == null) {
            trustScore = 85;
        }
        if (improvementPercentage == null) {
            improvementPercentage = 0.0;
        }
    }
}
