package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repair_parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairPart {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_id", nullable = false, length = 36)
    private String repairId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    @Column(nullable = false)
    @Builder.Default
    private Double cost = 0.0;

    @Column(name = "part_number", length = 100)
    private String partNumber;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}
