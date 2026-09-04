package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_decision_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDecisionSnapshot {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "intelligence_score", nullable = false)
    @Builder.Default
    private Integer intelligenceScore = 0;

    @Column(name = "recommended_action", nullable = false, length = 50)
    private String recommendedAction;

    @Column(name = "decision_confidence", nullable = false)
    @Builder.Default
    private Integer decisionConfidence = 85;

    @Column(name = "health_score", nullable = false)
    @Builder.Default
    private Integer healthScore = 0;

    @Column(name = "failure_risk_score", nullable = false)
    @Builder.Default
    private Integer failureRiskScore = 0;

    @Column(name = "economic_score", nullable = false)
    @Builder.Default
    private Integer economicScore = 0;

    @Column(name = "maintenance_score", nullable = false)
    @Builder.Default
    private Integer maintenanceScore = 0;

    @Column(name = "longevity_score", nullable = false)
    @Builder.Default
    private Integer longevityScore = 0;

    @Column(name = "sustainability_score", nullable = false)
    @Builder.Default
    private Integer sustainabilityScore = 0;

    @Column(name = "repair_history_score", nullable = false)
    @Builder.Default
    private Integer repairHistoryScore = 0;

    @Column(name = "explanation_summary", columnDefinition = "TEXT")
    private String explanationSummary;

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
        if (intelligenceScore == null) {
            intelligenceScore = 0;
        }
        if (decisionConfidence == null) {
            decisionConfidence = 85;
        }
        if (healthScore == null) {
            healthScore = 0;
        }
        if (failureRiskScore == null) {
            failureRiskScore = 0;
        }
        if (economicScore == null) {
            economicScore = 0;
        }
        if (maintenanceScore == null) {
            maintenanceScore = 0;
        }
        if (longevityScore == null) {
            longevityScore = 0;
        }
        if (sustainabilityScore == null) {
            sustainabilityScore = 0;
        }
        if (repairHistoryScore == null) {
            repairHistoryScore = 0;
        }
    }
}
