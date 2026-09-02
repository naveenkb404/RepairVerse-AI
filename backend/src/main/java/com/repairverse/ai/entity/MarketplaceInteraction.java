package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists user interactions in the repair marketplace for analytics and recommendations.
 */
@Entity
@Table(
    name = "marketplace_interactions",
    indexes = {
        @Index(name = "idx_mi_user_id", columnList = "user_id"),
        @Index(name = "idx_mi_interaction_type", columnList = "interaction_type"),
        @Index(name = "idx_mi_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_mi_created_at", columnList = "created_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceInteraction {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** SHOP_VIEWED | SHOP_COMPARED | QUOTE_REQUESTED | QUOTE_VIEWED | QUOTE_ACCEPTED | QUOTE_REJECTED | MATCH_SEARCHED */
    @Column(name = "interaction_type", nullable = false, length = 50)
    private String interactionType;

    @Column(name = "entity_id", nullable = false, length = 36)
    private String entityId;

    /** SHOP | QUOTE | MATCH | DEVICE */
    @Column(name = "entity_type", nullable = false, length = 30)
    private String entityType;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "mint-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
