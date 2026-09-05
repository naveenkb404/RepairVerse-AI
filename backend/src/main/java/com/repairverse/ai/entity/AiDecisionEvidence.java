package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Raw evidence signal that contributed to an AI decision.
 * Each AiDecisionRecord may have many evidence entries.
 * Phase 34: AI Decision Trust & Explainability Engine.
 */
@Entity
@Table(name = "ai_decision_evidence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiDecisionEvidence {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "decision_record_id", nullable = false, length = 36)
    private String decisionRecordId;

    /** Evidence category: HEALTH_SCORE, FAILURE_PROBABILITY, BATTERY_HEALTH,
     *  PATTERN_MATCH, KNOWLEDGE_NODE, REPAIR_HISTORY, CARBON_SCORE, etc. */
    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType;

    /** Human-readable signal name */
    @Column(name = "evidence_key", nullable = false, length = 100)
    private String evidenceKey;

    /** Signal value as string */
    @Column(name = "evidence_value", nullable = false, length = 200)
    private String evidenceValue;

    /** Contribution weight of this signal to the overall decision (0.0–1.0) */
    @Column(name = "evidence_weight", nullable = false)
    @Builder.Default
    private Double evidenceWeight = 1.0;

    /** Which service/table provided this signal */
    @Column(name = "evidence_source", length = 100)
    private String evidenceSource;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
