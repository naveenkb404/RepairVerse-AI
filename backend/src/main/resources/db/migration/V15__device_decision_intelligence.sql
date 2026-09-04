-- Phase 30: AI Repair Ecosystem Intelligence & Personalized Device Decision Engine

-- 1. Device Decision Snapshots (Stores historical device intelligence evaluations)
CREATE TABLE device_decision_snapshots (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    device_id                   VARCHAR(36)     NOT NULL,
    user_id                     VARCHAR(36)     NOT NULL,
    intelligence_score          INT             NOT NULL DEFAULT 0,
    recommended_action          VARCHAR(50)     NOT NULL, -- CONTINUE_USING, MONITOR, MAINTENANCE_REQUIRED, REPAIR_NOW, PROFESSIONAL_SERVICE, REFURBISH, REPLACE, RECYCLE
    decision_confidence         INT             NOT NULL DEFAULT 85,
    health_score                INT             NOT NULL DEFAULT 0,
    failure_risk_score          INT             NOT NULL DEFAULT 0,
    economic_score              INT             NOT NULL DEFAULT 0,
    maintenance_score           INT             NOT NULL DEFAULT 0,
    longevity_score             INT             NOT NULL DEFAULT 0,
    sustainability_score        INT             NOT NULL DEFAULT 0,
    repair_history_score        INT             NOT NULL DEFAULT 0,
    explanation_summary         TEXT,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dds_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    CONSTRAINT fk_dds_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 2. Device Decision Scenarios (Stores generated what-if decision scenarios)
CREATE TABLE device_decision_scenarios (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    device_id                   VARCHAR(36)     NOT NULL,
    user_id                     VARCHAR(36)     NOT NULL,
    scenario_type               VARCHAR(50)     NOT NULL, -- CONTINUE_USING, MONITOR, MAINTENANCE, REPAIR, PROFESSIONAL_SERVICE, REFURBISH, REPLACE, RECYCLE
    estimated_cost              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_lifespan_months   INT             NOT NULL DEFAULT 0,
    estimated_co2_impact        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_savings           DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    intelligence_score          INT             NOT NULL DEFAULT 0,
    recommendation              TEXT,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ddsc_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    CONSTRAINT fk_ddsc_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 3. Device Intelligence Alerts (Stores actionable intelligence alerts)
CREATE TABLE device_intelligence_alerts (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    device_id                   VARCHAR(36)     NOT NULL,
    user_id                     VARCHAR(36)     NOT NULL,
    alert_type                  VARCHAR(50)     NOT NULL, -- FAILURE_RISK, MAINTENANCE_REQUIRED, REPAIR_RECOMMENDED, COST_ESCALATION, END_OF_LIFE, SUSTAINABILITY_OPPORTUNITY
    severity                    VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM', -- INFO, LOW, MEDIUM, HIGH, CRITICAL
    title                       VARCHAR(255)    NOT NULL,
    message                     TEXT            NOT NULL,
    recommended_action          VARCHAR(50),
    is_read                     BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dia_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    CONSTRAINT fk_dia_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Performance Indexes
CREATE INDEX idx_dds_device_id          ON device_decision_snapshots(device_id);
CREATE INDEX idx_dds_user_id            ON device_decision_snapshots(user_id);
CREATE INDEX idx_dds_created_at         ON device_decision_snapshots(created_at);
CREATE INDEX idx_dds_device_created     ON device_decision_snapshots(device_id, created_at DESC);

CREATE INDEX idx_ddsc_device_id         ON device_decision_scenarios(device_id);
CREATE INDEX idx_ddsc_user_id           ON device_decision_scenarios(user_id);
CREATE INDEX idx_ddsc_type              ON device_decision_scenarios(scenario_type);
CREATE INDEX idx_ddsc_device_type       ON device_decision_scenarios(device_id, scenario_type);

CREATE INDEX idx_dia_user_id            ON device_intelligence_alerts(user_id);
CREATE INDEX idx_dia_device_id          ON device_intelligence_alerts(device_id);
CREATE INDEX idx_dia_is_read            ON device_intelligence_alerts(is_read);
CREATE INDEX idx_dia_user_unread        ON device_intelligence_alerts(user_id, is_read);
CREATE INDEX idx_dia_type               ON device_intelligence_alerts(alert_type);
