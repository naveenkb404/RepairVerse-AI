package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Governance safety / bias / constraint rule evaluated against every AI decision.
 * Rules are seeded via Flyway migration (V19) and can be managed via admin API.
 * Phase 34: AI Decision Trust & Explainability Engine.
 */
@Entity
@Table(name = "ai_governance_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiGovernanceRule {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "rule_name", nullable = false, unique = true, length = 100)
    private String ruleName;

    /** SAFETY, FINANCIAL, CONSISTENCY, DATA_QUALITY */
    @Column(name = "rule_category", nullable = false, length = 50)
    private String ruleCategory;

    @Column(columnDefinition = "TEXT")
    private String description;

    /** Comma-separated system names this rule applies to, or "ALL" */
    @Column(name = "applies_to_systems", columnDefinition = "TEXT")
    private String appliesToSystems;

    /** WARNING, BLOCKER, INFO */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String severity = "WARNING";

    /** Numeric threshold for comparison (e.g., confidence < 70, cost > 10000) */
    @Column(name = "threshold_value", nullable = false)
    @Builder.Default
    private Double thresholdValue = 0.0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

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
