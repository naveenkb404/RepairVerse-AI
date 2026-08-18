package com.repairverse.ai.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_health")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceHealth {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "device_id", nullable = false, unique = true, length = 36)
    private String deviceId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    @JsonIgnore
    private Device device;

    @Column(name = "battery_health")
    private Integer batteryHealth;

    @Column(name = "health_score", nullable = false)
    @Builder.Default
    private Integer healthScore = 80;

    @Column(name = "last_service", length = 20)
    private String lastService;

    @Column(name = "maintenance_due", length = 20)
    private String maintenanceDue;

    @Column(name = "ai_prediction", columnDefinition = "TEXT")
    private String aiPrediction;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (healthScore == null) {
            healthScore = 80;
        }
    }
}
