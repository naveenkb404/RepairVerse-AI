-- Phase 31: Autonomous Repair Agent & Proactive Device Intervention System

-- 1. Autonomous Interventions (Stores proactive interventions detected by RepairVerse AI Agent)
CREATE TABLE autonomous_interventions (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    device_id                   VARCHAR(36),
    intervention_type           VARCHAR(50)     NOT NULL, -- MONITOR, MAINTENANCE, PREVENTIVE_REPAIR, URGENT_REPAIR, PROFESSIONAL_SERVICE, SHOP_RECOMMENDATION, QUOTE_REQUEST, DEVICE_OPTIMIZATION, REFURBISH, REPLACE, RECYCLE
    priority                    VARCHAR(20)     NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    status                      VARCHAR(30)     NOT NULL DEFAULT 'DETECTED', -- DETECTED, PENDING_APPROVAL, APPROVED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED
    title                       VARCHAR(255)    NOT NULL,
    description                 TEXT,
    reason                      TEXT,
    confidence_score            INT             NOT NULL DEFAULT 85,
    priority_score              INT             NOT NULL DEFAULT 0,
    estimated_cost              DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_savings           DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_co2_impact        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    recommended_action          VARCHAR(50),
    action_payload              TEXT,
    requires_user_approval      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    resolved_at                 TIMESTAMP,
    CONSTRAINT fk_ai_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ai_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

-- 2. Autonomous Action Plans (Stores multi-step execution plans for interventions)
CREATE TABLE autonomous_action_plans (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    intervention_id             VARCHAR(36)     NOT NULL,
    plan_name                   VARCHAR(255)    NOT NULL,
    objective                   TEXT,
    total_steps                 INT             NOT NULL DEFAULT 0,
    completed_steps             INT             NOT NULL DEFAULT 0,
    status                      VARCHAR(30)     NOT NULL DEFAULT 'PLANNED', -- PLANNED, IN_PROGRESS, COMPLETED, CANCELLED
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    CONSTRAINT fk_aap_intervention FOREIGN KEY (intervention_id) REFERENCES autonomous_interventions(id) ON DELETE CASCADE
);

-- 3. Autonomous Action Steps (Stores individual executable steps)
CREATE TABLE autonomous_action_steps (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    plan_id                     VARCHAR(36)     NOT NULL,
    step_order                  INT             NOT NULL,
    action_type                 VARCHAR(50)     NOT NULL, -- GENERATE_REPORT, SCHEDULE_MAINTENANCE, FIND_SHOPS, REQUEST_QUOTE, COMPARE_OPTIONS, BOOK_SERVICE, DISPOSE_RECYCLE, NOTIFY_USER
    title                       VARCHAR(255)    NOT NULL,
    description                 TEXT,
    status                      VARCHAR(30)     NOT NULL DEFAULT 'PENDING', -- PENDING, WAITING_APPROVAL, RUNNING, COMPLETED, REJECTED, FAILED, CANCELLED
    requires_approval           BOOLEAN         NOT NULL DEFAULT FALSE,
    action_metadata             TEXT,
    scheduled_for               TIMESTAMP,
    completed_at                TIMESTAMP,
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP,
    CONSTRAINT fk_aas_plan FOREIGN KEY (plan_id) REFERENCES autonomous_action_plans(id) ON DELETE CASCADE
);

-- 4. Agent Execution History (Stores actions executed or attempted by the autonomous agent)
CREATE TABLE agent_execution_history (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    device_id                   VARCHAR(36),
    intervention_id             VARCHAR(36),
    action_step_id              VARCHAR(36),
    action_type                 VARCHAR(50)     NOT NULL,
    execution_status            VARCHAR(30)     NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, FAILED, CANCELLED, REJECTED
    result_summary              TEXT,
    executed_at                 TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_aeh_user         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_aeh_device       FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    CONSTRAINT fk_aeh_intervention FOREIGN KEY (intervention_id) REFERENCES autonomous_interventions(id) ON DELETE SET NULL,
    CONSTRAINT fk_aeh_step         FOREIGN KEY (action_step_id) REFERENCES autonomous_action_steps(id) ON DELETE SET NULL
);

-- Performance Indexes
CREATE INDEX idx_ai_user_id             ON autonomous_interventions(user_id);
CREATE INDEX idx_ai_device_id           ON autonomous_interventions(device_id);
CREATE INDEX idx_ai_status              ON autonomous_interventions(status);
CREATE INDEX idx_ai_priority            ON autonomous_interventions(priority);
CREATE INDEX idx_ai_user_status         ON autonomous_interventions(user_id, status);
CREATE INDEX idx_ai_device_status       ON autonomous_interventions(device_id, status);
CREATE INDEX idx_ai_created_at          ON autonomous_interventions(created_at DESC);

CREATE INDEX idx_aap_intervention_id    ON autonomous_action_plans(intervention_id);
CREATE INDEX idx_aap_status             ON autonomous_action_plans(status);

CREATE INDEX idx_aas_plan_id            ON autonomous_action_steps(plan_id);
CREATE INDEX idx_aas_status             ON autonomous_action_steps(status);
CREATE INDEX idx_aas_scheduled          ON autonomous_action_steps(scheduled_for);
CREATE INDEX idx_aas_plan_order         ON autonomous_action_steps(plan_id, step_order);

CREATE INDEX idx_aeh_user_id            ON agent_execution_history(user_id);
CREATE INDEX idx_aeh_device_id          ON agent_execution_history(device_id);
CREATE INDEX idx_aeh_executed_at        ON agent_execution_history(executed_at DESC);
