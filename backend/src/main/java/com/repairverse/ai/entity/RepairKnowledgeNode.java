package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_knowledge_nodes", uniqueConstraints = {
        @UniqueConstraint(name = "uq_rkn_type_key", columnNames = {"node_type", "node_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairKnowledgeNode {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "node_type", nullable = false, length = 50)
    private String nodeType;

    @Column(name = "node_key", nullable = false, length = 100)
    private String nodeKey;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "confidence_score", nullable = false)
    @Builder.Default
    private Double confidenceScore = 0.85;

    @Column(name = "observation_count", nullable = false)
    @Builder.Default
    private Integer observationCount = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.confidenceScore == null) {
            this.confidenceScore = 0.85;
        }
        if (this.observationCount == null) {
            this.observationCount = 1;
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
