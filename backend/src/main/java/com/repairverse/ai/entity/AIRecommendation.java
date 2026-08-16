package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRecommendation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "diagnosis_id", nullable = false, unique = true, length = 36)
    private String diagnosisId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "repair_score", nullable = false)
    private Integer repairScore;

    @Column(name = "replace_score", nullable = false)
    private Integer replaceScore;

    @Column(name = "carbon_saved", nullable = false)
    private Double carbonSaved;

    @Column(name = "money_saved", nullable = false)
    private Double moneySaved;

    @Column(length = 50)
    private String action; // REPAIR, MONITOR, REPLACE, PROFESSIONAL_SERVICE

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "plan_summary", columnDefinition = "TEXT")
    private String planSummary;

    @Column(name = "steps_json", columnDefinition = "TEXT")
    private String stepsJson;

    @Column(name = "parts_json", columnDefinition = "TEXT")
    private String partsJson;

    @Column(name = "tools_json", columnDefinition = "TEXT")
    private String toolsJson;

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
    }
}
