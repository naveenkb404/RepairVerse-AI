package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_pattern_insights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairPatternInsight {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "insight_type", nullable = false, length = 50)
    private String insightType;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Double confidence = 0.85;

    @Column(name = "impact_score", nullable = false)
    @Builder.Default
    private Integer impactScore = 50;

    @Column(name = "supporting_observations", nullable = false)
    @Builder.Default
    private Integer supportingObservations = 1;

    @Column(name = "device_category", length = 50)
    private String deviceCategory;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.confidence == null) {
            this.confidence = 0.85;
        }
        if (this.impactScore == null) {
            this.impactScore = 50;
        }
        if (this.supportingObservations == null) {
            this.supportingObservations = 1;
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
