package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Records deterministically detected marketplace anomalies for admin investigation.
 * Detection is always → Flag → Admin Review, never automatic enforcement.
 */
@Entity
@Table(
    name = "marketplace_anomalies",
    indexes = {
        @Index(name = "idx_ma_shop_id", columnList = "repair_shop_id"),
        @Index(name = "idx_ma_status", columnList = "status"),
        @Index(name = "idx_ma_severity", columnList = "severity")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceAnomaly {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "repair_shop_id", nullable = false, length = 36)
    private String repairShopId;

    @Column(name = "related_quote_id", length = 36)
    private String relatedQuoteId;

    @Column(name = "related_review_id", length = 36)
    private String relatedReviewId;

    /**
     * SUSPICIOUS_PRICING | REVIEW_SPIKE | REVIEW_PATTERN |
     * HIGH_REPEAT_REPAIRS | LOW_SUCCESS_RATE | UNUSUAL_CANCELLATION_RATE
     */
    @Column(name = "anomaly_type", nullable = false, length = 50)
    private String anomalyType;

    /** LOW | MEDIUM | HIGH | CRITICAL */
    @Column(name = "severity", nullable = false, length = 20)
    @Builder.Default
    private String severity = "MEDIUM";

    @Column(name = "risk_score", nullable = false)
    @Builder.Default
    private Integer riskScore = 50;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /** OPEN | UNDER_REVIEW | RESOLVED | DISMISSED */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "OPEN";

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = "anom-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
        if (detectedAt == null) {
            detectedAt = LocalDateTime.now();
        }
    }
}
