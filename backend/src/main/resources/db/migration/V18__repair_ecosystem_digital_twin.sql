-- ============================================================================
-- V18: AI Repair Ecosystem Digital Twin & Predictive Optimization Engine
-- Phase 33: Digital Twin Snapshots, Multi-Horizon Forecasts, Future Scenarios,
--           Optimization Results, and Ecosystem Simulation Events
-- ============================================================================

-- 1. Digital Twin Snapshots
CREATE TABLE IF NOT EXISTS digital_twin_snapshots (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    health_score INT NOT NULL DEFAULT 85,
    failure_risk_score INT NOT NULL DEFAULT 15,
    maintenance_score INT NOT NULL DEFAULT 80,
    repair_economics_score INT NOT NULL DEFAULT 85,
    longevity_score INT NOT NULL DEFAULT 80,
    sustainability_score INT NOT NULL DEFAULT 85,
    predicted_value DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    predicted_repair_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    predicted_failure_probability DOUBLE PRECISION NOT NULL DEFAULT 0.15,
    simulation_confidence DOUBLE PRECISION NOT NULL DEFAULT 0.88,
    overall_ecosystem_score INT NOT NULL DEFAULT 82,
    snapshot_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dts_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_dts_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_dts_user_id ON digital_twin_snapshots (user_id);
CREATE INDEX IF NOT EXISTS idx_dts_device_id ON digital_twin_snapshots (device_id);
CREATE INDEX IF NOT EXISTS idx_dts_snapshot_time ON digital_twin_snapshots (snapshot_time DESC);
CREATE INDEX IF NOT EXISTS idx_dts_ecosystem_score ON digital_twin_snapshots (overall_ecosystem_score DESC);

-- 2. Digital Twin Multi-Horizon Forecasts
CREATE TABLE IF NOT EXISTS digital_twin_forecasts (
    id VARCHAR(36) PRIMARY KEY,
    snapshot_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    forecast_horizon_months INT NOT NULL,
    predicted_health_score INT NOT NULL DEFAULT 80,
    predicted_failure_risk INT NOT NULL DEFAULT 20,
    predicted_repair_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    predicted_device_value DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    predicted_remaining_lifespan_months INT NOT NULL DEFAULT 24,
    predicted_co2_impact DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    predicted_e_waste_impact DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    forecast_confidence DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dtf_snapshot FOREIGN KEY (snapshot_id) REFERENCES digital_twin_snapshots (id) ON DELETE CASCADE,
    CONSTRAINT fk_dtf_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_dtf_snapshot_id ON digital_twin_forecasts (snapshot_id);
CREATE INDEX IF NOT EXISTS idx_dtf_device_id ON digital_twin_forecasts (device_id);
CREATE INDEX IF NOT EXISTS idx_dtf_horizon ON digital_twin_forecasts (forecast_horizon_months);

-- 3. Digital Twin Scenarios
CREATE TABLE IF NOT EXISTS digital_twin_scenarios (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    scenario_type VARCHAR(50) NOT NULL,
    scenario_name VARCHAR(255) NOT NULL,
    projected_health_score INT NOT NULL DEFAULT 85,
    projected_failure_risk INT NOT NULL DEFAULT 15,
    projected_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    projected_savings DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    projected_lifespan_months INT NOT NULL DEFAULT 24,
    projected_co2_impact DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    projected_e_waste_impact DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    downtime_days INT NOT NULL DEFAULT 0,
    overall_outcome_score INT NOT NULL DEFAULT 80,
    simulation_confidence DOUBLE PRECISION NOT NULL DEFAULT 0.88,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dtsc_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_dtsc_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_dtsc_user_id ON digital_twin_scenarios (user_id);
CREATE INDEX IF NOT EXISTS idx_dtsc_device_id ON digital_twin_scenarios (device_id);
CREATE INDEX IF NOT EXISTS idx_dtsc_type ON digital_twin_scenarios (scenario_type);
CREATE INDEX IF NOT EXISTS idx_dtsc_outcome ON digital_twin_scenarios (overall_outcome_score DESC);

-- 4. Digital Twin Optimization Results
CREATE TABLE IF NOT EXISTS digital_twin_optimization_results (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    recommended_strategy VARCHAR(50) NOT NULL,
    cost_score INT NOT NULL DEFAULT 85,
    reliability_score INT NOT NULL DEFAULT 85,
    longevity_score INT NOT NULL DEFAULT 85,
    sustainability_score INT NOT NULL DEFAULT 85,
    optimization_score INT NOT NULL DEFAULT 88,
    estimated_savings DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_lifespan_gain INT NOT NULL DEFAULT 0,
    estimated_co2_savings DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    decision_reason TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dtor_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_dtor_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_dtor_user_id ON digital_twin_optimization_results (user_id);
CREATE INDEX IF NOT EXISTS idx_dtor_device_id ON digital_twin_optimization_results (device_id);
CREATE INDEX IF NOT EXISTS idx_dtor_score ON digital_twin_optimization_results (optimization_score DESC);

-- 5. Ecosystem Simulation Events
CREATE TABLE IF NOT EXISTS ecosystem_simulation_events (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    projected_month_offset INT NOT NULL DEFAULT 0,
    estimated_financial_impact DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    mitigation_strategy VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ese_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_ese_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ese_user_id ON ecosystem_simulation_events (user_id);
CREATE INDEX IF NOT EXISTS idx_ese_device_id ON ecosystem_simulation_events (device_id);
CREATE INDEX IF NOT EXISTS idx_ese_event_type ON ecosystem_simulation_events (event_type);
CREATE INDEX IF NOT EXISTS idx_ese_offset ON ecosystem_simulation_events (projected_month_offset ASC);
