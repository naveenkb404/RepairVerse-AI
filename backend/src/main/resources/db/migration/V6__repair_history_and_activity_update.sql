-- Migration V6: Enhance repair_history and activity_logs indexes and constraints
-- Supports Phase 20: User Profile, Smart Repair History, Dashboard Aggregation & Activity Feed

CREATE INDEX IF NOT EXISTS idx_repair_history_user_id ON repair_history(user_id);
CREATE INDEX IF NOT EXISTS idx_repair_history_device_id ON repair_history(device_id);
CREATE INDEX IF NOT EXISTS idx_repair_history_date ON repair_history(repair_date);

CREATE INDEX IF NOT EXISTS idx_repair_parts_repair_id ON repair_parts(repair_id);
CREATE INDEX IF NOT EXISTS idx_repair_timeline_repair_id ON repair_timeline_stages(repair_id);

CREATE INDEX IF NOT EXISTS idx_activity_user_id ON activity_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_activity_created_at ON activity_logs(created_at);
