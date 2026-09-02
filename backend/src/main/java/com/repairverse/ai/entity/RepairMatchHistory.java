package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists deterministic matching outcomes between a device and repair shop.
 */
@Entity
@Table(
    name = "repair_match_history",
    indexes = {
        @Index(name = "idx_rmh_user_device", columnList = "user_id, device_id"),
        @Index(name = "idx_rmh_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rmh_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairMatchHistory {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "match_score", nullable = false)
    @Builder.Default
    private Integer matchScore = 0;

    /** EXCELLENT_MATCH | GREAT_MATCH | GOOD_MATCH | FAIR_MATCH | LOW_MATCH */
    @Column(name = "match_level", nullable = false, length = 30)
    @Builder.Default
    private String matchLevel = "GOOD_MATCH";

    @Column(name = "rank_position", nullable = false)
    @Builder.Default
    private Integer rankPosition = 1;

    @Column(name = "factors_json", columnDefinition = "TEXT")
    private String factorsJson;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rmh-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
