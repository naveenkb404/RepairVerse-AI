package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Persists specialized device categories and brand masteries for a repair shop.
 *
 * Specialization Level: BASIC, EXPERIENCED, EXPERT, CERTIFIED
 */
@Entity
@Table(
    name = "repair_shop_specializations",
    indexes = {
        @Index(name = "idx_rss_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_rss_category_brand", columnList = "device_category, brand")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairShopSpecialization {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "device_category", nullable = false, length = 80)
    private String deviceCategory;

    @Column(name = "brand", nullable = false, length = 80)
    private String brand;

    /** BASIC | EXPERIENCED | EXPERT | CERTIFIED */
    @Column(name = "specialization_level", nullable = false, length = 20)
    @Builder.Default
    private String specializationLevel = "EXPERIENCED";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "rss-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
