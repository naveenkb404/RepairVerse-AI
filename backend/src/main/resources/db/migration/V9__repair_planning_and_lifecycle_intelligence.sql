-- Migration V9: Add repair_action_plans and repair_action_steps tables
-- Supports Phase 24: Autonomous Repair Planning, Device Lifecycle Intelligence & Smart Action Pipelines

-- 1. Create repair_action_plans table
CREATE TABLE IF NOT EXISTS repair_action_plans (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    overall_strategy VARCHAR(50) NOT NULL,
    priority_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    estimated_total_cost DOUBLE PRECISION DEFAULT 0.0,
    estimated_lifecycle_extension_months INT DEFAULT 0,
    estimated_carbon_saved DOUBLE PRECISION DEFAULT 0.0,
    estimated_ewaste_prevented DOUBLE PRECISION DEFAULT 0.0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rap_user_id ON repair_action_plans(user_id);
CREATE INDEX IF NOT EXISTS idx_rap_device_id ON repair_action_plans(device_id);
CREATE INDEX IF NOT EXISTS idx_rap_strategy ON repair_action_plans(overall_strategy);
CREATE INDEX IF NOT EXISTS idx_rap_created_at ON repair_action_plans(created_at);

-- 2. Create repair_action_steps table
CREATE TABLE IF NOT EXISTS repair_action_steps (
    id VARCHAR(36) PRIMARY KEY,
    action_plan_id VARCHAR(36) NOT NULL,
    step_order INT NOT NULL DEFAULT 1,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    action_type VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    estimated_cost DOUBLE PRECISION DEFAULT 0.0,
    estimated_duration VARCHAR(50),
    carbon_impact DOUBLE PRECISION DEFAULT 0.0,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_ras_action_plan FOREIGN KEY (action_plan_id) REFERENCES repair_action_plans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ras_action_plan_id ON repair_action_steps(action_plan_id);
CREATE INDEX IF NOT EXISTS idx_ras_step_order ON repair_action_steps(action_plan_id, step_order);
