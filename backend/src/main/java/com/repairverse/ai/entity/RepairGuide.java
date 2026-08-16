package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_guides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairGuide {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String difficulty;

    @Column(name = "estimated_time", length = 50)
    private String estimatedTime;

    @Column(name = "guide_content", columnDefinition = "TEXT")
    private String guideContent;

    @Column(name = "created_at")
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
