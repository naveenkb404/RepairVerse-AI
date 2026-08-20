-- Migration V5: Enhance repair_shops, bookings, notifications, and carbon_impact tables
-- Add missing columns, constraints, and indexes

-- 1. Enhance repair_shops
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS review_count INT DEFAULT 0;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS email VARCHAR(100);
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS hours VARCHAR(100);
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS services_json TEXT;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS service_categories_json TEXT;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS certified_brands_json TEXT;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS estimated_turnaround VARCHAR(100);
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS avg_price VARCHAR(50);
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS verified BOOLEAN DEFAULT TRUE;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS is_open BOOLEAN DEFAULT TRUE;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS eco_certified BOOLEAN DEFAULT FALSE;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS is_demo BOOLEAN DEFAULT FALSE;
ALTER TABLE repair_shops ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;

-- 2. Add indexes for high-frequency queries
CREATE INDEX IF NOT EXISTS idx_bookings_user_id ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_shop_id ON bookings(shop_id);
CREATE INDEX IF NOT EXISTS idx_bookings_created_at ON bookings(created_at);

CREATE INDEX IF NOT EXISTS idx_notif_user_read ON notifications(user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_notif_created_at ON notifications(created_at);

CREATE INDEX IF NOT EXISTS idx_carbon_user_id ON carbon_impact(user_id);

-- 3. Enhance carbon_impact
ALTER TABLE carbon_impact ADD COLUMN IF NOT EXISTS sustainability_score INT DEFAULT 80;
ALTER TABLE carbon_impact ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

