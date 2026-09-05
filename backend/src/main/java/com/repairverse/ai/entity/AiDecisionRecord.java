package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Central audit record for every AI decision produced by any RepairVerse intelligence system.
 * Phase 34: AI Decision Trust & Explainability Engine.
 */
@Entity
@Table(name = "ai_decision_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiDecisionRecord {

    @Id
    @Column(length = 36)
    private String id;

    /** Owning user */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** Device context (nullable for fleet-level decisions) */
    @Column(name = "device_id", length = 36)
    private String deviceId;

    /** Source intelligence system: DIAGNOSIS, PREDICTIVE, DEVICE_INTELLIGENCE,
     *  AUTONOMOUS_AGENT, KNOWLEDGE_GRAPH, DIGITAL_TWIN, CIRCULAR_ECONOMY */
    @Column(name = "source_system", nullable = false, length = 50)
    private String sourceSystem;

    /** Decision category: FAULT_DIAGNOSIS, FAILURE_RISK_ASSESSMENT, REPAIR_VS_REPLACE,
     *  STRATEGY_OPTIMIZATION, AUTONOMOUS_INTERVENTION, SCENARIO_SIMULATION, etc. */
    @Column(name = "decision_type", nullable = false, length = 80)
    private String decisionType;

    /** FK to the original record (diagnosisId, snapshotId, interventionId, etc.) */
    @Column(name = "source_record_id", length = 36)
    private String sourceRecordId;

    /** JSON-serialised decision output (action, score, recommendation) */
    @Column(name = "decision_output", nullable = false, columnDefinition = "TEXT")
    private String decisionOutput;

    /** Confidence score from the originating AI system (0–100) */
    @Column(name = "confidence_score", nullable = false)
    @Builder.Default
    private Integer confidenceScore = 80;

    /** Computed by TrustScoreService — weighted composite (0–100) */
    @Column(name = "trust_score", nullable = false)
    @Builder.Default
    private Integer trustScore = 75;

    /** VERIFIED (85+), RELIABLE (70–84), CAUTION (55–69), REVIEW_REQUIRED (<55) */
    @Column(name = "trust_tier", nullable = false, length = 20)
    @Builder.Default
    private String trustTier = "RELIABLE";

    /** Risk level: LOW, MEDIUM, HIGH, CRITICAL */
    @Column(name = "risk_level", nullable = false, length = 20)
    @Builder.Default
    private String riskLevel = "LOW";

    /** ACTIVE, SUPERSEDED, OVERRIDDEN_BY_USER */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "user_reviewed", nullable = false)
    @Builder.Default
    private Boolean userReviewed = false;

    /** AGREE, DISAGREE, UNSURE — null until user submits feedback */
    @Column(name = "user_feedback", length = 20)
    private String userFeedback;

    /** Structured natural-language explanations */
    @Column(name = "why_explanation", columnDefinition = "TEXT")
    private String whyExplanation;

    @Column(name = "how_explanation", columnDefinition = "TEXT")
    private String howExplanation;

    @Column(name = "what_if_explanation", columnDefinition = "TEXT")
    private String whatIfExplanation;

    @Column(name = "impact_explanation", columnDefinition = "TEXT")
    private String impactExplanation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
