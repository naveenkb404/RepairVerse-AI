package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_knowledge_relationships", uniqueConstraints = {
        @UniqueConstraint(name = "uq_rkr_source_target_type", columnNames = {"source_node_id", "target_node_id", "relationship_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairKnowledgeRelationship {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "source_node_id", nullable = false, length = 36)
    private String sourceNodeId;

    @Column(name = "target_node_id", nullable = false, length = 36)
    private String targetNodeId;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType;

    @Column(nullable = false)
    @Builder.Default
    private Double strength = 50.0;

    @Column(nullable = false)
    @Builder.Default
    private Double confidence = 0.80;

    @Column(name = "observation_count", nullable = false)
    @Builder.Default
    private Integer observationCount = 1;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "first_observed_at", nullable = false)
    private LocalDateTime firstObservedAt;

    @Column(name = "last_observed_at", nullable = false)
    private LocalDateTime lastObservedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.strength == null) {
            this.strength = 50.0;
        }
        if (this.confidence == null) {
            this.confidence = 0.80;
        }
        if (this.observationCount == null) {
            this.observationCount = 1;
        }
        if (this.firstObservedAt == null) {
            this.firstObservedAt = LocalDateTime.now();
        }
        if (this.lastObservedAt == null) {
            this.lastObservedAt = LocalDateTime.now();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
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
