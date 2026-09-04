-- Phase 29: AI-Powered Circular Economy Intelligence & Personalized Sustainability Optimization

-- 1. Circular Impact Events (Tracks measurable circular economy actions)
CREATE TABLE circular_impact_events (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    device_id                   VARCHAR(36),
    event_type                  VARCHAR(50)     NOT NULL, -- REPAIR_COMPLETED, MAINTENANCE_COMPLETED, DEVICE_LIFE_EXTENDED, COMPONENT_UPGRADE, DEVICE_REFURBISHED, DEVICE_DONATED, DEVICE_RECYCLED, RESPONSIBLE_DISPOSAL
    event_date                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    carbon_saved_kg             DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    ewaste_prevented_kg         DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    money_saved                 DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    device_life_extension_days  INT             NOT NULL DEFAULT 0,
    impact_source               VARCHAR(50)     NOT NULL DEFAULT 'MANUAL', -- AUTOMATED_REPAIR, MAINTENANCE_SCHEDULE, USER_ACTION, MARKETPLACE_BOOKING, PASSPORT_EVENT, MANUAL
    reference_id                VARCHAR(36),
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP
);

-- 2. Sustainability Goals (Stores user sustainability targets and progress)
CREATE TABLE sustainability_goals (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    goal_type                   VARCHAR(50)     NOT NULL, -- CARBON_REDUCTION, EWASTE_PREVENTION, DEVICE_LIFE_EXTENSION, REPAIR_COUNT, MONEY_SAVED
    target_value                DOUBLE PRECISION NOT NULL,
    current_value               DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    start_date                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    target_date                 TIMESTAMP,
    status                      VARCHAR(30)     NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, COMPLETED, EXPIRED, CANCELLED
    created_at                  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP
);

-- 3. Sustainability Achievements (Tracks unlocked user achievements with duplicate prevention)
CREATE TABLE sustainability_achievements (
    id                          VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                     VARCHAR(36)     NOT NULL,
    achievement_code            VARCHAR(50)     NOT NULL, -- FIRST_REPAIR, EWASTE_SAVER, CARBON_CONSCIOUS, LIFE_EXTENDER, CIRCULAR_CHAMPION, PLANET_PROTECTOR
    achievement_name            VARCHAR(100)    NOT NULL,
    achievement_description     TEXT            NOT NULL,
    unlocked_at                 TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    impact_value                DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    CONSTRAINT uq_user_achievement UNIQUE(user_id, achievement_code)
);

-- Performance Indexes
CREATE INDEX idx_cie_user_id            ON circular_impact_events(user_id);
CREATE INDEX idx_cie_device_id          ON circular_impact_events(device_id);
CREATE INDEX idx_cie_event_type         ON circular_impact_events(event_type);
CREATE INDEX idx_cie_event_date         ON circular_impact_events(event_date);
CREATE INDEX idx_cie_user_date          ON circular_impact_events(user_id, event_date);
CREATE INDEX idx_cie_user_type          ON circular_impact_events(user_id, event_type);
CREATE INDEX idx_cie_device_date        ON circular_impact_events(device_id, event_date);

CREATE INDEX idx_sg_user_id             ON sustainability_goals(user_id);
CREATE INDEX idx_sg_status              ON sustainability_goals(status);
CREATE INDEX idx_sg_user_status         ON sustainability_goals(user_id, status);

CREATE INDEX idx_sa_user_id             ON sustainability_achievements(user_id);
CREATE INDEX idx_sa_code                ON sustainability_achievements(achievement_code);
