package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record of a governance rule violation triggered when evaluating an AI decision.
 * Phase 34: AI Decision Trust & Explainability Engine.
 */
@Entity
@Table(name = "ai_governance_violations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGovernanceViolation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "decision_record_id", nullable = false, length = 36)
    private String decisionRecordId;

    @Column(name = "rule_id", nullable = false, length = 36)
    private String ruleId;

    @Column(name = "violation_message", columnDefinition = "TEXT")
    private String violationMessage;

    /** WARNING, BLOCKER, INFO — mirrors the triggering rule's severity */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "WARNING";

    @Column(name = "auto_resolved", nullable = false)
    @Builder.Default
    private Boolean autoResolved = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
