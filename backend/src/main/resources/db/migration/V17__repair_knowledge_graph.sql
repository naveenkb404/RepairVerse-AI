-- ============================================================================
-- V17: Repair Knowledge Graph & Ecosystem Learning Intelligence
-- Phase 32: Graph Nodes, Weighted Relationships, Pattern Insights, Feedback Loop
-- ============================================================================

-- 1. Knowledge Graph Nodes
CREATE TABLE IF NOT EXISTS repair_knowledge_nodes (
    id VARCHAR(36) PRIMARY KEY,
    node_type VARCHAR(50) NOT NULL,
    node_key VARCHAR(100) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    metadata TEXT,
    confidence_score DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    observation_count INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_rkn_type_key UNIQUE (node_type, node_key)
);

CREATE INDEX IF NOT EXISTS idx_rkn_type ON repair_knowledge_nodes (node_type);
CREATE INDEX IF NOT EXISTS idx_rkn_key ON repair_knowledge_nodes (node_key);
CREATE INDEX IF NOT EXISTS idx_rkn_obs_count ON repair_knowledge_nodes (observation_count DESC);
CREATE INDEX IF NOT EXISTS idx_rkn_confidence ON repair_knowledge_nodes (confidence_score DESC);

-- 2. Knowledge Graph Relationships
CREATE TABLE IF NOT EXISTS repair_knowledge_relationships (
    id VARCHAR(36) PRIMARY KEY,
    source_node_id VARCHAR(36) NOT NULL,
    target_node_id VARCHAR(36) NOT NULL,
    relationship_type VARCHAR(50) NOT NULL,
    strength DOUBLE PRECISION NOT NULL DEFAULT 50.0,
    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.80,
    observation_count INT NOT NULL DEFAULT 1,
    metadata TEXT,
    first_observed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_observed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rkr_source FOREIGN KEY (source_node_id) REFERENCES repair_knowledge_nodes (id) ON DELETE CASCADE,
    CONSTRAINT fk_rkr_target FOREIGN KEY (target_node_id) REFERENCES repair_knowledge_nodes (id) ON DELETE CASCADE,
    CONSTRAINT uq_rkr_source_target_type UNIQUE (source_node_id, target_node_id, relationship_type)
);

CREATE INDEX IF NOT EXISTS idx_rkr_source ON repair_knowledge_relationships (source_node_id);
CREATE INDEX IF NOT EXISTS idx_rkr_target ON repair_knowledge_relationships (target_node_id);
CREATE INDEX IF NOT EXISTS idx_rkr_type ON repair_knowledge_relationships (relationship_type);
CREATE INDEX IF NOT EXISTS idx_rkr_strength ON repair_knowledge_relationships (strength DESC);
CREATE INDEX IF NOT EXISTS idx_rkr_source_type ON repair_knowledge_relationships (source_node_id, relationship_type);

-- 3. Repair Pattern Insights
CREATE TABLE IF NOT EXISTS repair_pattern_insights (
    id VARCHAR(36) PRIMARY KEY,
    insight_type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    impact_score INT NOT NULL DEFAULT 50,
    supporting_observations INT NOT NULL DEFAULT 1,
    device_category VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_rpi_type ON repair_pattern_insights (insight_type);
CREATE INDEX IF NOT EXISTS idx_rpi_category ON repair_pattern_insights (device_category);
CREATE INDEX IF NOT EXISTS idx_rpi_status ON repair_pattern_insights (status);
CREATE INDEX IF NOT EXISTS idx_rpi_confidence ON repair_pattern_insights (confidence DESC);
CREATE INDEX IF NOT EXISTS idx_rpi_impact ON repair_pattern_insights (impact_score DESC);

-- 4. Repair Knowledge Feedback
CREATE TABLE IF NOT EXISTS repair_knowledge_feedback (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36),
    insight_id VARCHAR(36),
    feedback_type VARCHAR(50) NOT NULL,
    rating INT,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rkf_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_rkf_device FOREIGN KEY (device_id) REFERENCES devices (id) ON DELETE SET NULL,
    CONSTRAINT fk_rkf_insight FOREIGN KEY (insight_id) REFERENCES repair_pattern_insights (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_rkf_user_id ON repair_knowledge_feedback (user_id);
CREATE INDEX IF NOT EXISTS idx_rkf_device_id ON repair_knowledge_feedback (device_id);
CREATE INDEX IF NOT EXISTS idx_rkf_insight_id ON repair_knowledge_feedback (insight_id);
CREATE INDEX IF NOT EXISTS idx_rkf_type ON repair_knowledge_feedback (feedback_type);
