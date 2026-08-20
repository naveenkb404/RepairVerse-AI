package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "repair_shops")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairShop {

    @Id
    private String id;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "rating")
    @Builder.Default
    private Double rating = 4.5;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "hours")
    private String hours;

    @Column(name = "services_json", columnDefinition = "TEXT")
    private String servicesJson;

    @Column(name = "service_categories_json", columnDefinition = "TEXT")
    private String serviceCategoriesJson;

    @Column(name = "certified_brands_json", columnDefinition = "TEXT")
    private String certifiedBrandsJson;

    @Column(name = "estimated_turnaround")
    private String estimatedTurnaround;

    @Column(name = "avg_price")
    private String avgPrice;

    @Column(name = "verified")
    @Builder.Default
    private Boolean verified = true;

    @Column(name = "is_open")
    @Builder.Default
    private Boolean isOpen = true;

    @Column(name = "eco_certified")
    @Builder.Default
    private Boolean ecoCertified = false;

    @Column(name = "is_demo")
    @Builder.Default
    private Boolean isDemo = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
