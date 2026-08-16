-- Migration V3: Add action, rationale, plan details, and created_at to ai_recommendations; create repair_guides
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS action VARCHAR(50) DEFAULT 'REPAIR';
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS rationale TEXT;
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS plan_summary TEXT;
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS steps_json TEXT;
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS parts_json TEXT;
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS tools_json TEXT;
ALTER TABLE ai_recommendations ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS repair_guides (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    estimated_time VARCHAR(50),
    guide_content TEXT,
    created_at TIMESTAMP
);
