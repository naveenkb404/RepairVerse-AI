package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "learning_feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningFeedback {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_version_id", nullable = false)
    private IntelligenceModelVersion modelVersion;

    @Column(name = "decision_reference", nullable = false, length = 80)
    private String decisionReference;

    @Column(name = "feedback_type", nullable = false, length = 30)
    private String feedbackType; // AGREE, DISAGREE, UNSURE

    @Column(name = "outcome_quality", nullable = false)
    private Double outcomeQuality; // 1.0 (positive) to 0.0 (negative)

    @Column(name = "feedback_weight", nullable = false)
    private Double feedbackWeight;

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
        if (feedbackType == null) {
            feedbackType = "AGREE";
        }
        if (outcomeQuality == null) {
            outcomeQuality = 1.0;
        }
        if (feedbackWeight == null) {
            feedbackWeight = 1.0;
        }
    }
}
