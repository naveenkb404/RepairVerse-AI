-- Migration V7: Enhance repair_guides and add community_posts & community_replies tables
-- Supports Phase 21: Smart Repair Guides, Repair Cost Estimator & Community Knowledge Ecosystem

-- 1. Enhance repair_guides table
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS author_id VARCHAR(36);
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS author_name VARCHAR(100) DEFAULT 'RepairVerse Expert';
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS tools_json TEXT;
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS steps_json TEXT;
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS views_count INT DEFAULT 0;
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS likes_count INT DEFAULT 0;
ALTER TABLE repair_guides ADD COLUMN IF NOT EXISTS is_verified BOOLEAN DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS idx_repair_guides_category ON repair_guides(category);
CREATE INDEX IF NOT EXISTS idx_repair_guides_difficulty ON repair_guides(difficulty);
CREATE INDEX IF NOT EXISTS idx_repair_guides_created_at ON repair_guides(created_at);

-- 2. Create community_posts table
CREATE TABLE IF NOT EXISTS community_posts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    author_avatar VARCHAR(255),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    device_model VARCHAR(100),
    likes_count INT NOT NULL DEFAULT 0,
    replies_count INT NOT NULL DEFAULT 0,
    is_solved BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_community_posts_category ON community_posts(category);
CREATE INDEX IF NOT EXISTS idx_community_posts_created_at ON community_posts(created_at);
CREATE INDEX IF NOT EXISTS idx_community_posts_user_id ON community_posts(user_id);

-- 3. Create community_replies table
CREATE TABLE IF NOT EXISTS community_replies (
    id VARCHAR(36) PRIMARY KEY,
    post_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    author_avatar VARCHAR(255),
    content TEXT NOT NULL,
    is_solution BOOLEAN NOT NULL DEFAULT FALSE,
    likes_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_replies_post FOREIGN KEY (post_id) REFERENCES community_posts(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_community_replies_post_id ON community_replies(post_id);
CREATE INDEX IF NOT EXISTS idx_community_replies_created_at ON community_replies(created_at);
