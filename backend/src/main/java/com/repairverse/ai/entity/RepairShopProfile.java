package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists extended marketplace trust, verification, experience, and capability attributes
 * for a repair shop.
 *
 * Verification Status: PENDING, VERIFIED, TRUSTED, SUSPENDED
 * Verification Level:  BASIC, VERIFIED, PREMIUM
 */
@Entity
@Table(
    name = "repair_shop_profiles",
    indexes = {
        @Index(name = "idx_rsp_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rsp_verification", columnList = "verification_status, verification_level"),
        @Index(name = "idx_rsp_rating", columnList = "average_rating")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairShopProfile {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_shop_id", nullable = false, unique = true, length = 36)
    private String repairShopId;

    /** PENDING | VERIFIED | TRUSTED | SUSPENDED */
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private String verificationStatus = "PENDING";

    /** BASIC | VERIFIED | PREMIUM */
    @Column(name = "verification_level", nullable = false, length = 20)
    @Builder.Default
    private String verificationLevel = "BASIC";

    @Column(name = "years_of_experience", nullable = false)
    @Builder.Default
    private Integer yearsOfExperience = 1;

    @Column(name = "total_repairs_completed", nullable = false)
    @Builder.Default
    private Integer totalRepairsCompleted = 0;

    @Column(name = "specializations_json", columnDefinition = "TEXT")
    private String specializationsJson;

    @Column(name = "certifications_json", columnDefinition = "TEXT")
    private String certificationsJson;

    @Column(name = "average_rating", nullable = false)
    @Builder.Default
    private Double averageRating = 4.5;

    @Column(name = "total_reviews", nullable = false)
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "response_rate", nullable = false)
    @Builder.Default
    private Double responseRate = 95.0;

    @Column(name = "average_response_time_minutes", nullable = false)
    @Builder.Default
    private Integer averageResponseTimeMinutes = 30;

    @Column(name = "warranty_offered", nullable = false)
    @Builder.Default
    private Boolean warrantyOffered = true;

    @Column(name = "warranty_days", nullable = false)
    @Builder.Default
    private Integer warrantyDays = 90;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rsp-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
