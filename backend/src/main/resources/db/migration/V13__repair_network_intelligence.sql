-- Phase 28: Repair Network Intelligence, Trust & Service Quality Platform

-- 1. Repair Service Outcomes (Tracks actual repair results, warranty usage, and satisfaction)
CREATE TABLE repair_service_outcomes (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    repair_shop_id              VARCHAR(36)     NOT NULL,
    device_id                   VARCHAR(36)     NOT NULL,
    booking_id                  VARCHAR(36),
    repair_category             VARCHAR(80)     NOT NULL,
    repair_status               VARCHAR(30)     NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, FAILED, CANCELLED, WARRANTY_REPAIR, REPEAT_REPAIR
    repair_successful           BOOLEAN         NOT NULL DEFAULT TRUE,
    warranty_claimed            BOOLEAN         NOT NULL DEFAULT FALSE,
    repeat_repair_required      BOOLEAN         NOT NULL DEFAULT FALSE,
    customer_satisfaction       INT             NOT NULL DEFAULT 5,           -- 1 to 5
    repair_cost                 DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_cost              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    completed_at                TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP
);

-- 2. Repair Shop Quality Snapshots (Periodic aggregated performance snapshots)
CREATE TABLE repair_shop_quality_snapshots (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    repair_shop_id              VARCHAR(36)     NOT NULL,
    overall_quality_score       INT             NOT NULL DEFAULT 85,          -- 0 to 100
    reliability_score           INT             NOT NULL DEFAULT 85,          -- 0 to 100
    customer_satisfaction_score INT             NOT NULL DEFAULT 90,          -- 0 to 100
    repair_success_score        INT             NOT NULL DEFAULT 90,          -- 0 to 100
    price_fairness_score        INT             NOT NULL DEFAULT 85,          -- 0 to 100
    service_speed_score         INT             NOT NULL DEFAULT 80,          -- 0 to 100
    trust_score                 INT             NOT NULL DEFAULT 85,          -- 0 to 100
    total_repairs               INT             NOT NULL DEFAULT 0,
    successful_repairs          INT             NOT NULL DEFAULT 0,
    failed_repairs              INT             NOT NULL DEFAULT 0,
    repeat_repairs              INT             NOT NULL DEFAULT 0,
    average_rating              DOUBLE PRECISION NOT NULL DEFAULT 4.5,
    average_turnaround_days     DOUBLE PRECISION NOT NULL DEFAULT 1.5,
    quality_tier                VARCHAR(30)     NOT NULL DEFAULT 'TRUSTED',   -- ELITE, EXCELLENT, TRUSTED, STANDARD, NEEDS_IMPROVEMENT
    calculated_at               TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Marketplace Anomalies (Suspicious pricing, review spikes, fraud risk flags for admin review)
CREATE TABLE marketplace_anomalies (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    repair_shop_id              VARCHAR(36)     NOT NULL,
    related_quote_id            VARCHAR(36),
    related_review_id           VARCHAR(36),
    anomaly_type                VARCHAR(50)     NOT NULL, -- SUSPICIOUS_PRICING, REVIEW_SPIKE, REVIEW_PATTERN, HIGH_REPEAT_REPAIRS, LOW_SUCCESS_RATE, UNUSUAL_CANCELLATION_RATE
    severity                    VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',   -- LOW, MEDIUM, HIGH, CRITICAL
    risk_score                  INT             NOT NULL DEFAULT 50,          -- 0 to 100
    description                 TEXT            NOT NULL,
    status                      VARCHAR(20)     NOT NULL DEFAULT 'OPEN',     -- OPEN, UNDER_REVIEW, RESOLVED, DISMISSED
    detected_at                 TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at                 TIMESTAMP
);

-- Performance Indexes
CREATE INDEX idx_rso_shop_id            ON repair_service_outcomes(repair_shop_id);
CREATE INDEX idx_rso_user_id            ON repair_service_outcomes(user_id);
CREATE INDEX idx_rso_device_id          ON repair_service_outcomes(device_id);
CREATE INDEX idx_rso_completed_at       ON repair_service_outcomes(completed_at);

CREATE INDEX idx_rsqs_shop_id           ON repair_shop_quality_snapshots(repair_shop_id);
CREATE INDEX idx_rsqs_calculated_at     ON repair_shop_quality_snapshots(calculated_at);

CREATE INDEX idx_ma_shop_id             ON marketplace_anomalies(repair_shop_id);
CREATE INDEX idx_ma_status              ON marketplace_anomalies(status);
CREATE INDEX idx_ma_severity            ON marketplace_anomalies(severity);
