-- Phase 27: Intelligent Repair Marketplace Experience & Smart Matching

-- 1. Repair Match History (Stores deterministic matching records, compatibility scores, and explanations)
CREATE TABLE repair_match_history (
    id                      VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                 VARCHAR(36)     NOT NULL,
    device_id               VARCHAR(36)     NOT NULL,
    repair_shop_id          VARCHAR(36)     NOT NULL,
    match_score             INT             NOT NULL DEFAULT 0,
    match_level             VARCHAR(30)     NOT NULL DEFAULT 'GOOD_MATCH', -- EXCELLENT_MATCH, GREAT_MATCH, GOOD_MATCH, FAIR_MATCH, LOW_MATCH
    rank_position           INT             NOT NULL DEFAULT 1,
    factors_json            TEXT,
    explanation             TEXT,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Marketplace Interactions (Anonymized and structured user marketplace events for intelligence analytics)
CREATE TABLE marketplace_interactions (
    id                      VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                 VARCHAR(36)     NOT NULL,
    interaction_type        VARCHAR(50)     NOT NULL, -- SHOP_VIEWED, SHOP_COMPARED, QUOTE_REQUESTED, QUOTE_VIEWED, QUOTE_ACCEPTED, QUOTE_REJECTED, MATCH_SEARCHED
    entity_id               VARCHAR(36)     NOT NULL,
    entity_type             VARCHAR(30)     NOT NULL, -- SHOP, QUOTE, MATCH, DEVICE
    metadata_json           TEXT,
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_rmh_user_device        ON repair_match_history(user_id, device_id);
CREATE INDEX idx_rmh_shop_id            ON repair_match_history(repair_shop_id);
CREATE INDEX idx_rmh_created_at         ON repair_match_history(created_at);

CREATE INDEX idx_mi_user_id             ON marketplace_interactions(user_id);
CREATE INDEX idx_mi_interaction_type    ON marketplace_interactions(interaction_type);
CREATE INDEX idx_mi_entity              ON marketplace_interactions(entity_type, entity_id);
CREATE INDEX idx_mi_created_at          ON marketplace_interactions(created_at);
