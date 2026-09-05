-- ============================================================
-- V19: AI Decision Trust & Explainability Engine
-- Phase 34 — RepairVerse AI
-- ============================================================

-- 1. Central AI decision audit log
CREATE TABLE ai_decision_records (
    id                  VARCHAR(36) PRIMARY KEY,
    user_id             VARCHAR(36) NOT NULL,
    device_id           VARCHAR(36),
    source_system       VARCHAR(50) NOT NULL,
    decision_type       VARCHAR(80) NOT NULL,
    source_record_id    VARCHAR(36),
    decision_output     TEXT        NOT NULL,
    confidence_score    INT         NOT NULL DEFAULT 80,
    trust_score         INT         NOT NULL DEFAULT 75,
    trust_tier          VARCHAR(20) NOT NULL DEFAULT 'RELIABLE',
    risk_level          VARCHAR(20) NOT NULL DEFAULT 'LOW',
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    user_reviewed       BOOLEAN     NOT NULL DEFAULT FALSE,
    user_feedback       VARCHAR(20),
    why_explanation     TEXT,
    how_explanation     TEXT,
    what_if_explanation TEXT,
    impact_explanation  TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_adr_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_adr_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE SET NULL
);

CREATE INDEX idx_adr_user_id       ON ai_decision_records(user_id);
CREATE INDEX idx_adr_device_id     ON ai_decision_records(device_id);
CREATE INDEX idx_adr_system        ON ai_decision_records(source_system);
CREATE INDEX idx_adr_type          ON ai_decision_records(decision_type);
CREATE INDEX idx_adr_trust_score   ON ai_decision_records(trust_score);
CREATE INDEX idx_adr_trust_tier    ON ai_decision_records(trust_tier);
CREATE INDEX idx_adr_status        ON ai_decision_records(status);
CREATE INDEX idx_adr_created_at    ON ai_decision_records(created_at);

-- 2. Evidence signals for each decision
CREATE TABLE ai_decision_evidence (
    id                  VARCHAR(36) PRIMARY KEY,
    decision_record_id  VARCHAR(36) NOT NULL,
    evidence_type       VARCHAR(50) NOT NULL,
    evidence_key        VARCHAR(100) NOT NULL,
    evidence_value      VARCHAR(200) NOT NULL,
    evidence_weight     DOUBLE       NOT NULL DEFAULT 1.0,
    evidence_source     VARCHAR(100),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ade_decision FOREIGN KEY (decision_record_id) REFERENCES ai_decision_records(id) ON DELETE CASCADE
);

CREATE INDEX idx_ade_decision_id ON ai_decision_evidence(decision_record_id);
CREATE INDEX idx_ade_type        ON ai_decision_evidence(evidence_type);

-- 3. Governance rules catalog
CREATE TABLE ai_governance_rules (
    id                  VARCHAR(36) PRIMARY KEY,
    rule_name           VARCHAR(100) NOT NULL UNIQUE,
    rule_category       VARCHAR(50)  NOT NULL,
    description         TEXT,
    applies_to_systems  TEXT,
    severity            VARCHAR(20)  NOT NULL DEFAULT 'WARNING',
    threshold_value     DOUBLE       NOT NULL DEFAULT 0.0,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_agr_category ON ai_governance_rules(rule_category);
CREATE INDEX idx_agr_active   ON ai_governance_rules(is_active);

-- 4. Governance violations per decision
CREATE TABLE ai_governance_violations (
    id                  VARCHAR(36) PRIMARY KEY,
    decision_record_id  VARCHAR(36) NOT NULL,
    rule_id             VARCHAR(36) NOT NULL,
    violation_message   TEXT,
    severity            VARCHAR(20) NOT NULL DEFAULT 'WARNING',
    auto_resolved       BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_agv_decision FOREIGN KEY (decision_record_id) REFERENCES ai_decision_records(id) ON DELETE CASCADE,
    CONSTRAINT fk_agv_rule     FOREIGN KEY (rule_id)            REFERENCES ai_governance_rules(id) ON DELETE CASCADE
);

CREATE INDEX idx_agv_decision_id ON ai_governance_violations(decision_record_id);
CREATE INDEX idx_agv_rule_id     ON ai_governance_violations(rule_id);
CREATE INDEX idx_agv_severity    ON ai_governance_violations(severity);

-- 5. Per-user autonomy & consent preferences
CREATE TABLE user_autonomy_preferences (
    id                              VARCHAR(36) PRIMARY KEY,
    user_id                         VARCHAR(36) NOT NULL UNIQUE,
    allow_autonomous_interventions  BOOLEAN     NOT NULL DEFAULT TRUE,
    allow_auto_scheduling           BOOLEAN     NOT NULL DEFAULT FALSE,
    allow_proactive_alerts          BOOLEAN     NOT NULL DEFAULT TRUE,
    min_confidence_threshold        INT         NOT NULL DEFAULT 80,
    require_approval_above_cost     DOUBLE      NOT NULL DEFAULT 5000.0,
    notification_style              VARCHAR(20) NOT NULL DEFAULT 'DETAILED',
    created_at                      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_uap_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_uap_user_id ON user_autonomy_preferences(user_id);

-- 6. Seed the 6 built-in governance rules
INSERT INTO ai_governance_rules (id, rule_name, rule_category, description, applies_to_systems, severity, threshold_value, is_active) VALUES
    ('rule-001', 'LOW_CONFIDENCE_BLOCKER',      'SAFETY',     'Block autonomous action when AI confidence is below threshold.',             'AUTONOMOUS_AGENT,PREDICTIVE',              'BLOCKER', 70.0, TRUE),
    ('rule-002', 'HIGH_COST_APPROVAL_GATE',     'FINANCIAL',  'Flag decisions recommending spend above cost threshold for user review.',    'ALL',                                      'WARNING', 10000.0, TRUE),
    ('rule-003', 'CONFLICTING_RECOMMENDATIONS', 'CONSISTENCY','Alert when two or more systems recommend opposing actions for same device.', 'ALL',                                      'WARNING', 0.0, TRUE),
    ('rule-004', 'STALE_DATA_WARNING',          'DATA_QUALITY','Warn if underlying evidence data is older than 30 days.',                  'ALL',                                      'WARNING', 30.0, TRUE),
    ('rule-005', 'CASCADING_CRITICAL_ALERT',    'SAFETY',     'Alert when 3 or more CRITICAL decisions are active for one device.',        'ALL',                                      'BLOCKER', 3.0, TRUE),
    ('rule-006', 'AUTONOMOUS_RATE_LIMIT',       'SAFETY',     'Flag if more than 5 autonomous interventions occur in 24 hours.',           'AUTONOMOUS_AGENT',                         'WARNING', 5.0, TRUE);
