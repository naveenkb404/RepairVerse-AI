-- Migration V4: Add updated_at column to devices table
ALTER TABLE devices ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;
