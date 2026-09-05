-- ============================================================
-- V20: Privacy-Preserving Federated Repair Intelligence & Continuous Learning Engine
-- Phase 35 — RepairVerse AI
-- ============================================================

-- 1. Federated learning aggregation batches
CREATE TABLE federated_learning_batches (
    id                      VARCHAR(36) PRIMARY KEY,
    batch_reference         VARCHAR(80) NOT NULL UNIQUE,
    source_scope            VARCHAR(50) NOT NULL DEFAULT 'ECOSYSTEM_GLOBAL',
    anonymized_device_count INT         NOT NULL DEFAULT 0,
    anonymized_repair_count INT         NOT NULL DEFAULT 0,
    generated_at            TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status                  VARCHAR(30) NOT NULL DEFAULT 'AGGREGATED',
    privacy_level           VARCHAR(30) NOT NULL DEFAULT 'STRICT',
    validation_score        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    model_version           VARCHAR(50) NOT NULL,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_flb_status        ON federated_learning_batches(status);
CREATE INDEX idx_flb_model_version ON federated_learning_batches(model_version);
CREATE INDEX idx_flb_created_at    ON federated_learning_batches(created_at);

-- 2. Aggregated bounded repair learning signals
CREATE TABLE repair_learning_signals (
    id                      VARCHAR(36) PRIMARY KEY,
    batch_id                VARCHAR(36) NOT NULL,
    signal_type             VARCHAR(60) NOT NULL,
    device_category         VARCHAR(50) NOT NULL,
    component_type          VARCHAR(60) NOT NULL,
    failure_mode            VARCHAR(80) NOT NULL,
    repair_action           VARCHAR(80) NOT NULL,
    outcome_class           VARCHAR(40) NOT NULL DEFAULT 'SUCCESSFUL_REPAIR',
    aggregated_frequency    INT         NOT NULL DEFAULT 1,
    success_rate            DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    average_cost            DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    average_lifespan_gain   INT         NOT NULL DEFAULT 0,
    sustainability_score    DOUBLE PRECISION NOT NULL DEFAULT 85.0,
    confidence              DOUBLE PRECISION NOT NULL DEFAULT 0.85,
    observation_count       INT         NOT NULL DEFAULT 5,
    created_at              TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rls_batch FOREIGN KEY (batch_id) REFERENCES federated_learning_batches(id) ON DELETE CASCADE
);

CREATE INDEX idx_rls_batch_id    ON repair_learning_signals(batch_id);
CREATE INDEX idx_rls_category    ON repair_learning_signals(device_category);
CREATE INDEX idx_rls_component   ON repair_learning_signals(component_type);
CREATE INDEX idx_rls_action      ON repair_learning_signals(repair_action);
CREATE INDEX idx_rls_confidence  ON repair_learning_signals(confidence);

-- 3. Versioned intelligence models
CREATE TABLE intelligence_model_versions (
    id                      VARCHAR(36) PRIMARY KEY,
    model_name              VARCHAR(100) NOT NULL,
    version                 VARCHAR(50)  NOT NULL UNIQUE,
    parent_version          VARCHAR(50),
    status                  VARCHAR(30)  NOT NULL DEFAULT 'COLLECTING',
    training_observations   INT          NOT NULL DEFAULT 0,
    validation_score        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    trust_score             INT          NOT NULL DEFAULT 85,
    improvement_percentage  DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    activated_at            TIMESTAMP,
    retired_at              TIMESTAMP,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_imv_status      ON intelligence_model_versions(status);
CREATE INDEX idx_imv_version     ON intelligence_model_versions(version);
CREATE INDEX idx_imv_activated   ON intelligence_model_versions(activated_at);

-- 4. Learning validation results
CREATE TABLE learning_validation_results (
    id                      VARCHAR(36) PRIMARY KEY,
    model_version_id        VARCHAR(36) NOT NULL,
    validation_type         VARCHAR(60) NOT NULL,
    baseline_score          DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    candidate_score         DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    improvement_score       DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    regression_detected     BOOLEAN      NOT NULL DEFAULT FALSE,
    confidence              DOUBLE PRECISION NOT NULL DEFAULT 0.90,
    decision                VARCHAR(30)  NOT NULL DEFAULT 'ACCEPTED',
    validated_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lvr_model FOREIGN KEY (model_version_id) REFERENCES intelligence_model_versions(id) ON DELETE CASCADE
);

CREATE INDEX idx_lvr_model_id    ON learning_validation_results(model_version_id);
CREATE INDEX idx_lvr_decision    ON learning_validation_results(decision);
CREATE INDEX idx_lvr_regression  ON learning_validation_results(regression_detected);

-- 5. Privacy audit events
CREATE TABLE privacy_audit_events (
    id                      VARCHAR(36) PRIMARY KEY,
    batch_id                VARCHAR(36),
    event_type              VARCHAR(60)  NOT NULL,
    privacy_rule            VARCHAR(100) NOT NULL,
    records_processed       INT          NOT NULL DEFAULT 0,
    records_filtered        INT          NOT NULL DEFAULT 0,
    records_aggregated      INT          NOT NULL DEFAULT 0,
    sensitive_fields_removed INT         NOT NULL DEFAULT 0,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pae_batch FOREIGN KEY (batch_id) REFERENCES federated_learning_batches(id) ON DELETE SET NULL
);

CREATE INDEX idx_pae_batch_id    ON privacy_audit_events(batch_id);
CREATE INDEX idx_pae_event_type  ON privacy_audit_events(event_type);
CREATE INDEX idx_pae_created_at  ON privacy_audit_events(created_at);

-- 6. Learning feedback
CREATE TABLE learning_feedback (
    id                      VARCHAR(36) PRIMARY KEY,
    model_version_id        VARCHAR(36) NOT NULL,
    decision_reference      VARCHAR(80) NOT NULL,
    feedback_type           VARCHAR(30) NOT NULL DEFAULT 'AGREE',
    outcome_quality         DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    feedback_weight         DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lfb_model FOREIGN KEY (model_version_id) REFERENCES intelligence_model_versions(id) ON DELETE CASCADE
);

CREATE INDEX idx_lfb_model_id    ON learning_feedback(model_version_id);
CREATE INDEX idx_lfb_type        ON learning_feedback(feedback_type);

-- 7. Seed active baseline model version (R35.4) and initial privacy audit
INSERT INTO intelligence_model_versions (
    id, model_name, version, parent_version, status, training_observations, validation_score, trust_score, improvement_percentage, activated_at, created_at
) VALUES (
    'model-r35-4', 'RepairVerse Federated Core', 'R35.4', 'R35.3', 'ACTIVE', 1284, 94.2, 94, 8.7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO intelligence_model_versions (
    id, model_name, version, parent_version, status, training_observations, validation_score, trust_score, improvement_percentage, activated_at, retired_at, created_at
) VALUES (
    'model-r35-3', 'RepairVerse Federated Core', 'R35.3', 'R35.2', 'SUPERSEDED', 940, 91.5, 91, 5.2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

INSERT INTO federated_learning_batches (
    id, batch_reference, source_scope, anonymized_device_count, anonymized_repair_count, status, privacy_level, validation_score, model_version, created_at
) VALUES (
    'fl-batch-001', 'BATCH-2026-09-001', 'ECOSYSTEM_GLOBAL', 248, 1284, 'ACTIVE', 'STRICT', 94.2, 'R35.4', CURRENT_TIMESTAMP
);

INSERT INTO privacy_audit_events (
    id, batch_id, event_type, privacy_rule, records_processed, records_filtered, records_aggregated, sensitive_fields_removed, created_at
) VALUES (
    'pae-001', 'fl-batch-001', 'BATCH_PRIVACY_AUDIT', 'MIN_OBSERVATIONS_THRESHOLD_AND_PII_STRIP', 1340, 56, 1284, 1340, CURRENT_TIMESTAMP
);
