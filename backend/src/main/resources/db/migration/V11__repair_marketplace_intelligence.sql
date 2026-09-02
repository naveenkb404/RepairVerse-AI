-- Phase 26: Trusted Repair Marketplace, Smart Quotations & Repair Reputation Intelligence

-- 1. Repair Shop Profiles (Extended marketplace trust & capability attributes)
CREATE TABLE repair_shop_profiles (
    id                                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    repair_shop_id                      VARCHAR(36)     NOT NULL UNIQUE,
    verification_status                 VARCHAR(20)     NOT NULL DEFAULT 'PENDING',  -- PENDING, VERIFIED, TRUSTED, SUSPENDED
    verification_level                  VARCHAR(20)     NOT NULL DEFAULT 'BASIC',    -- BASIC, VERIFIED, PREMIUM
    years_of_experience                 INT             NOT NULL DEFAULT 1,
    total_repairs_completed             INT             NOT NULL DEFAULT 0,
    specializations_json                TEXT,
    certifications_json                 TEXT,
    average_rating                      DOUBLE PRECISION NOT NULL DEFAULT 4.5,
    total_reviews                       INT             NOT NULL DEFAULT 0,
    response_rate                       DOUBLE PRECISION NOT NULL DEFAULT 95.0,
    average_response_time_minutes       INT             NOT NULL DEFAULT 30,
    warranty_offered                    BOOLEAN         NOT NULL DEFAULT TRUE,
    warranty_days                       INT             NOT NULL DEFAULT 90,
    created_at                          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                          TIMESTAMP
);

-- 2. Repair Shop Specializations
CREATE TABLE repair_shop_specializations (
    id                                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    repair_shop_id                      VARCHAR(36)     NOT NULL,
    device_category                     VARCHAR(80)     NOT NULL,
    brand                               VARCHAR(80)     NOT NULL,
    specialization_level                VARCHAR(20)     NOT NULL DEFAULT 'EXPERIENCED', -- BASIC, EXPERIENCED, EXPERT, CERTIFIED
    created_at                          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Repair Quotes
CREATE TABLE repair_quotes (
    id                                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                             VARCHAR(36)     NOT NULL,
    device_id                           VARCHAR(36)     NOT NULL,
    repair_shop_id                      VARCHAR(36)     NOT NULL,
    diagnosis_id                        VARCHAR(36),
    recommendation_id                   VARCHAR(36),
    repair_title                        VARCHAR(200)    NOT NULL,
    problem_summary                     TEXT,
    estimated_cost                      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    minimum_cost                        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    maximum_cost                        DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    estimated_duration_hours            DOUBLE PRECISION NOT NULL DEFAULT 2.0,
    parts_cost                          DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    labor_cost                          DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    warranty_days                       INT             NOT NULL DEFAULT 90,
    status                              VARCHAR(20)     NOT NULL DEFAULT 'REQUESTED', -- REQUESTED, DRAFT, SUBMITTED, ACCEPTED, REJECTED, EXPIRED, CANCELLED
    created_at                          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                          TIMESTAMP,
    expires_at                          TIMESTAMP
);

-- 4. Repair Reviews
CREATE TABLE repair_reviews (
    id                                  VARCHAR(36)     NOT NULL PRIMARY KEY,
    user_id                             VARCHAR(36)     NOT NULL,
    repair_shop_id                      VARCHAR(36)     NOT NULL,
    booking_id                          VARCHAR(36),
    rating                              INT             NOT NULL DEFAULT 5,
    title                               VARCHAR(200),
    comment                             TEXT,
    repair_quality_rating               INT             NOT NULL DEFAULT 5,
    communication_rating                INT             NOT NULL DEFAULT 5,
    value_rating                        INT             NOT NULL DEFAULT 5,
    timeliness_rating                   INT             NOT NULL DEFAULT 5,
    verified_repair                     BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at                          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                          TIMESTAMP
);

-- Performance Indexes
CREATE INDEX idx_rsp_shop_id            ON repair_shop_profiles(repair_shop_id);
CREATE INDEX idx_rsp_verification       ON repair_shop_profiles(verification_status, verification_level);
CREATE INDEX idx_rsp_rating             ON repair_shop_profiles(average_rating);

CREATE INDEX idx_rss_shop_id            ON repair_shop_specializations(repair_shop_id);
CREATE INDEX idx_rss_category_brand     ON repair_shop_specializations(device_category, brand);

CREATE INDEX idx_rq_user_id             ON repair_quotes(user_id);
CREATE INDEX idx_rq_device_id           ON repair_quotes(device_id);
CREATE INDEX idx_rq_shop_id             ON repair_quotes(repair_shop_id);
CREATE INDEX idx_rq_status              ON repair_quotes(status);
CREATE INDEX idx_rq_user_status         ON repair_quotes(user_id, status);

CREATE INDEX idx_rr_shop_id             ON repair_reviews(repair_shop_id);
CREATE INDEX idx_rr_user_id             ON repair_reviews(user_id);
CREATE INDEX idx_rr_booking_id          ON repair_reviews(booking_id);
