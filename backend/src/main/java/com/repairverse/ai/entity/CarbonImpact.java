package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_impact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonImpact {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "co2_saved", nullable = false)
    @Builder.Default
    private Double co2Saved = 0.0;

    @Column(name = "ewaste_reduced", nullable = false)
    @Builder.Default
    private Double ewasteReduced = 0.0;

    @Column(name = "money_saved", nullable = false)
    @Builder.Default
    private Double moneySaved = 0.0;

    @Column(name = "repair_count", nullable = false)
    @Builder.Default
    private Integer repairCount = 0;

    @Column(name = "sustainability_score", nullable = false)
    @Builder.Default
    private Integer sustainabilityScore = 80;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
