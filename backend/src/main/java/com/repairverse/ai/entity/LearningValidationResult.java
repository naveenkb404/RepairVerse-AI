package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_validation_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningValidationResult {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id", nullable = false)
    private IntelligenceModelVersion modelVersion;

    @Column(name = "validation_type", nullable = false, length = 60)
    private String validationType; // ACCURACY, STABILITY, REGRESSION, TRUST_ALIGNMENT, GOVERNANCE_COMPLIANCE, PRIVACY_CHECK

    @Column(name = "baseline_score", nullable = false)
    private Double baselineScore;

    @Column(name = "candidate_score", nullable = false)
    private Double candidateScore;

    @Column(name = "improvement_score", nullable = false)
    private Double improvementScore;

    @Column(name = "regression_detected", nullable = false)
    private Boolean regressionDetected;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false, length = 30)
    private String decision; // ACCEPTED, REJECTED, QUARANTINED

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (validatedAt == null) {
            validatedAt = LocalDateTime.now();
        }
        if (regressionDetected == null) {
            regressionDetected = false;
        }
        if (confidence == null) {
            confidence = 0.90;
        }
        if (decision == null) {
            decision = "ACCEPTED";
        }
        if (baselineScore == null) {
            baselineScore = 0.0;
        }
        if (candidateScore == null) {
            candidateScore = 0.0;
        }
        if (improvementScore == null) {
            improvementScore = 0.0;
        }
    }
}
