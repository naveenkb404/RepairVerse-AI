-- Migration V2: Add category, brand, model, safety_warning, and observations to diagnosis_reports
ALTER TABLE diagnosis_reports ADD COLUMN IF NOT EXISTS device_category VARCHAR(50);
ALTER TABLE diagnosis_reports ADD COLUMN IF NOT EXISTS brand VARCHAR(50);
ALTER TABLE diagnosis_reports ADD COLUMN IF NOT EXISTS model VARCHAR(100);
ALTER TABLE diagnosis_reports ADD COLUMN IF NOT EXISTS safety_warning TEXT;
ALTER TABLE diagnosis_reports ADD COLUMN IF NOT EXISTS observations TEXT;
