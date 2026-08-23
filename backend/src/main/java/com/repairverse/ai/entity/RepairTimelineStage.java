package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repair_timeline_stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairTimelineStage {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_id", nullable = false, length = 36)
    private String repairId;

    @Column(name = "stage_date", nullable = false, length = 50)
    private String stageDate;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 20)
    private String status; // completed, current, pending

    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
    }
}
