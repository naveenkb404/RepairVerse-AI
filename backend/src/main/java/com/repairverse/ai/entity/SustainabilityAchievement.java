package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Tracks unlocked user sustainability achievements with unique constraint on (userId, achievementCode).
 */
@Entity
@Table(
    name = "sustainability_achievements",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_achievement", columnNames = {"user_id", "achievement_code"})
    },
    indexes = {
        @Index(name = "idx_sa_user_id", columnList = "user_id"),
        @Index(name = "idx_sa_code", columnList = "achievement_code")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SustainabilityAchievement {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /**
     * FIRST_REPAIR | EWASTE_SAVER | CARBON_CONSCIOUS | LIFE_EXTENDER | CIRCULAR_CHAMPION | PLANET_PROTECTOR
     */
    @Column(name = "achievement_code", nullable = false, length = 50)
    private String achievementCode;

    @Column(name = "achievement_name", nullable = false, length = 100)
    private String achievementName;

    @Column(name = "achievement_description", nullable = false, columnDefinition = "TEXT")
    private String achievementDescription;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    @Column(name = "impact_value", nullable = false)
    @Builder.Default
    private Double impactValue = 0.0;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "sa-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }
}
