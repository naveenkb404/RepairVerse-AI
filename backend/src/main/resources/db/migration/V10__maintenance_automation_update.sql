-- Phase 25: Proactive Device Care & Smart Maintenance Automation
-- Creates maintenance_schedules table for deterministic scheduled care tasks

CREATE TABLE maintenance_schedules (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    device_id                   VARCHAR(36)     NOT NULL,
    device_name                 VARCHAR(150)    NOT NULL,
    device_category             VARCHAR(80),
    title                       VARCHAR(200)    NOT NULL,
    description                 TEXT,
    maintenance_type            VARCHAR(50)     NOT NULL,
    priority                    VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM',
    scheduled_date              DATE,
    due_date                    DATE            NOT NULL,
    status                      VARCHAR(20)     NOT NULL DEFAULT 'UPCOMING',
    estimated_cost              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_duration_minutes  INT             NOT NULL DEFAULT 30,
    estimated_carbon_savings    DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    completed_at                TIMESTAMP
);

-- Performance indexes for common query patterns
CREATE INDEX idx_ms_user_id        ON maintenance_schedules(user_id);
CREATE INDEX idx_ms_device_id      ON maintenance_schedules(device_id);
CREATE INDEX idx_ms_due_date       ON maintenance_schedules(due_date);
CREATE INDEX idx_ms_status         ON maintenance_schedules(status);
CREATE INDEX idx_ms_device_user    ON maintenance_schedules(device_id, user_id);
CREATE INDEX idx_ms_user_status    ON maintenance_schedules(user_id, status);
