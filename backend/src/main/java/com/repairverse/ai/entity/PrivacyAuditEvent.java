package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "privacy_audit_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrivacyAuditEvent {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private FederatedLearningBatch batch;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType; // BATCH_PRIVACY_AUDIT, PII_SCRUBBING, THRESHOLD_ENFORCEMENT, OUTCOME_ANONYMIZATION

    @Column(name = "privacy_rule", nullable = false, length = 100)
    private String privacyRule;

    @Column(name = "records_processed", nullable = false)
    private Integer recordsProcessed;

    @Column(name = "records_filtered", nullable = false)
    private Integer recordsFiltered;

    @Column(name = "records_aggregated", nullable = false)
    private Integer recordsAggregated;

    @Column(name = "sensitive_fields_removed", nullable = false)
    private Integer sensitiveFieldsRemoved;

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
        if (recordsProcessed == null) {
            recordsProcessed = 0;
        }
        if (recordsFiltered == null) {
            recordsFiltered = 0;
        }
        if (recordsAggregated == null) {
            recordsAggregated = 0;
        }
        if (sensitiveFieldsRemoved == null) {
            sensitiveFieldsRemoved = 0;
        }
    }
}
