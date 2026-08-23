package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairHistory {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "technician_role", length = 100)
    private String technicianRole;

    @Column(name = "shop_name", length = 100)
    private String shopName;

    @Column(name = "shop_address", length = 255)
    private String shopAddress;

    @Column(name = "repair_type", nullable = false, length = 255)
    private String repairType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "diagnosis_issue", length = 255)
    private String diagnosisIssue;

    @Column(name = "diagnosis_confidence")
    private Integer diagnosisConfidence;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String status = "Completed";

    @Column(name = "repair_date", nullable = false, length = 20)
    private String repairDate;

    @Column(name = "repair_duration", length = 50)
    private String repairDuration;

    @Column(name = "parts_cost", nullable = false)
    @Builder.Default
    private Double partsCost = 0.0;

    @Column(name = "labor_cost", nullable = false)
    @Builder.Default
    private Double laborCost = 0.0;

    @Column(name = "total_cost", nullable = false)
    @Builder.Default
    private Double totalCost = 0.0;

    @Column(name = "warranty_period", length = 100)
    private String warrantyPeriod;

    @Column(name = "warranty_until", length = 20)
    private String warrantyUntil;

    @Column(name = "is_warranty_active")
    @Builder.Default
    private Boolean isWarrantyActive = false;

    @Column(name = "co2_saved_kg")
    @Builder.Default
    private Double co2SavedKg = 0.0;

    @Column(name = "ewaste_reduced_kg")
    @Builder.Default
    private Double ewasteReducedKg = 0.0;

    @Column(name = "money_saved")
    @Builder.Default
    private Double moneySaved = 0.0;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    @Builder.Default
    private List<RepairPart> parts = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "repair_id")
    @Builder.Default
    private List<RepairTimelineStage> timeline = new ArrayList<>();

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
