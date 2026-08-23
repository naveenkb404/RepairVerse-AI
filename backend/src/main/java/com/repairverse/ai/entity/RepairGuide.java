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

    @Column(name = "author_id", length = 36)
    private String authorId;

    @Column(name = "author_name", length = 100)
    @Builder.Default
    private String authorName = "RepairVerse Expert";

    @Column(name = "tools_json", columnDefinition = "TEXT")
    private String toolsJson;

    @Column(name = "steps_json", columnDefinition = "TEXT")
    private String stepsJson;

    @Column(name = "views_count")
    @Builder.Default
    private Integer viewsCount = 0;

    @Column(name = "likes_count")
    @Builder.Default
    private Integer likesCount = 0;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = true;

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
