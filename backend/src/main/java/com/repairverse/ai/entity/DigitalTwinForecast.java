package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "digital_twin_forecasts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigitalTwinForecast {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "snapshot_id", nullable = false, length = 36)
    private String snapshotId;

    @Column(name = "device_id", nullable = false, length = 36)
    private String deviceId;

    @Column(name = "forecast_horizon_months", nullable = false)
    private Integer forecastHorizonMonths;

    @Column(name = "predicted_health_score", nullable = false)
    @Builder.Default
    private Integer predictedHealthScore = 80;

    @Column(name = "predicted_failure_risk", nullable = false)
    @Builder.Default
    private Integer predictedFailureRisk = 20;

    @Column(name = "predicted_repair_cost", nullable = false)
    @Builder.Default
    private Double predictedRepairCost = 0.0;

    @Column(name = "predicted_device_value", nullable = false)
    @Builder.Default
    private Double predictedDeviceValue = 0.0;

    @Column(name = "predicted_remaining_lifespan_months", nullable = false)
    @Builder.Default
    private Integer predictedRemainingLifespanMonths = 24;

    @Column(name = "predicted_co2_impact", nullable = false)
    @Builder.Default
    private Double predictedCo2Impact = 0.0;

    @Column(name = "predicted_e_waste_impact", nullable = false)
    @Builder.Default
    private Double predictedEWasteImpact = 0.0;

    @Column(name = "forecast_confidence", nullable = false)
    @Builder.Default
    private Double forecastConfidence = 0.85;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
