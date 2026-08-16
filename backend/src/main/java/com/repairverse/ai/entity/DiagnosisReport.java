package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisReport {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "device_id", length = 36)
    private String deviceId;

    @Column(name = "device_category", length = 50)
    private String deviceCategory;

    @Column(length = 50)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(name = "probable_issue", nullable = false)
    private String probableIssue;

    @Column(name = "confidence_score", nullable = false)
    private Integer confidenceScore;

    @Column(name = "repair_difficulty", nullable = false, length = 50)
    private String repairDifficulty;

    @Column(name = "repair_time", length = 50)
    private String repairTime;

    @Column(name = "repair_cost")
    private Double repairCost;

    @Column(name = "safety_warning", columnDefinition = "TEXT")
    private String safetyWarning;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

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
