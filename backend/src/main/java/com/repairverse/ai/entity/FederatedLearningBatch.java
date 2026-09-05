package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "federated_learning_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FederatedLearningBatch {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "batch_reference", nullable = false, unique = true, length = 80)
    private String batchReference;

    @Column(name = "source_scope", nullable = false, length = 50)
    private String sourceScope;

    @Column(name = "anonymized_device_count", nullable = false)
    private Integer anonymizedDeviceCount;

    @Column(name = "anonymized_repair_count", nullable = false)
    private Integer anonymizedRepairCount;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(nullable = false, length = 30)
    private String status; // COLLECTING, AGGREGATED, VALIDATING, APPROVED, ACTIVE, SUPERSEDED, REJECTED, QUARANTINED

    @Column(name = "privacy_level", nullable = false, length = 30)
    private String privacyLevel; // STRICT, STANDARD, AGGREGATED

    @Column(name = "validation_score", nullable = false)
    private Double validationScore;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (sourceScope == null) {
            sourceScope = "ECOSYSTEM_GLOBAL";
        }
        if (status == null) {
            status = "AGGREGATED";
        }
        if (privacyLevel == null) {
            privacyLevel = "STRICT";
        }
        if (validationScore == null) {
            validationScore = 0.0;
        }
        if (anonymizedDeviceCount == null) {
            anonymizedDeviceCount = 0;
        }
        if (anonymizedRepairCount == null) {
            anonymizedRepairCount = 0;
        }
    }
}
