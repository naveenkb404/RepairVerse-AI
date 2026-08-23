-- Migration V8: Add device_predictions and fault_patterns tables
-- Supports Phase 22: Predictive Intelligence & Maintenance Analytics

-- 1. Create device_predictions table
CREATE TABLE IF NOT EXISTS device_predictions (
    id VARCHAR(36) PRIMARY KEY,
    device_id VARCHAR(36) NOT NULL UNIQUE,
    user_id VARCHAR(36) NOT NULL,
    prediction_score INT NOT NULL DEFAULT 80,
    risk_level VARCHAR(20) NOT NULL DEFAULT 'LOW',
    days_to_failure_estimate INT,
    primary_fault_type VARCHAR(100),
    recommended_actions TEXT,
    scoring_breakdown TEXT,
    estimated_repair_cost DOUBLE PRECISION,
    preventive_savings DOUBLE PRECISION,
    co2_savings_kg DOUBLE PRECISION,
    confidence_score DOUBLE PRECISION DEFAULT 0.80,
    notification_sent BOOLEAN DEFAULT FALSE,
    evaluated_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_dp_device_id ON device_predictions(device_id);
CREATE INDEX IF NOT EXISTS idx_dp_user_id ON device_predictions(user_id);
CREATE INDEX IF NOT EXISTS idx_dp_risk_level ON device_predictions(risk_level);
CREATE INDEX IF NOT EXISTS idx_dp_evaluated_at ON device_predictions(evaluated_at);

-- 2. Create fault_patterns table
CREATE TABLE IF NOT EXISTS fault_patterns (
    id VARCHAR(36) PRIMARY KEY,
    device_category VARCHAR(50),
    device_brand VARCHAR(100),
    fault_type VARCHAR(100) NOT NULL,
    description TEXT,
    min_device_age_years INT DEFAULT 0,
    health_score_threshold INT DEFAULT 60,
    risk_weight INT NOT NULL DEFAULT 5,
    typical_cost_min DOUBLE PRECISION,
    typical_cost_max DOUBLE PRECISION,
    preventive_actions TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_fp_category ON fault_patterns(device_category);
CREATE INDEX IF NOT EXISTS idx_fp_fault_type ON fault_patterns(fault_type);
CREATE INDEX IF NOT EXISTS idx_fp_active ON fault_patterns(is_active);

-- 3. Seed initial curated fault pattern library
INSERT INTO fault_patterns (id, device_category, device_brand, fault_type, description, min_device_age_years, health_score_threshold, risk_weight, typical_cost_min, typical_cost_max, preventive_actions, is_active, created_at)
VALUES
('fp-001', 'Smartphone', NULL, 'Battery Degradation', 'Lithium-ion chemical capacity reduction below critical retention threshold resulting in rapid discharge and unexpected throttling.', 2, 70, 7, 45.0, 95.0, 'Limit charge cycles to 80%|Avoid exposure to extreme ambient heat|Enable OS battery optimization', TRUE, NOW()),
('fp-002', 'Smartphone', 'Apple', 'OLED Display Burn-in & Ghosting', 'Static UI retention and subpixel luminescence decay on Super Retina XDR panels.', 3, 65, 6, 120.0, 280.0, 'Reduce auto-lock timeout to 30s|Avoid prolonged static high-brightness displays|Enable auto-brightness', TRUE, NOW()),
('fp-003', 'Smartphone', NULL, 'USB-C / Lightning Port Wear', 'Oxidation and physical micro-fractures in charging socket contact pins causing intermittent power delivery.', 2, 60, 5, 35.0, 75.0, 'Clean port with anti-static compressed air|Use certified OEM braided cables|Avoid angled strain while charging', TRUE, NOW()),
('fp-004', 'Laptop', NULL, 'Thermal Interface Paste Degradation', 'Dried silicone thermal compound leading to high junction temperatures and severe CPU/GPU thermal throttling.', 2, 65, 8, 50.0, 110.0, 'Repaste heatsink with high-thermal-conductivity compound|Clean cooling fan exhaust fins every 6 months|Elevate laptop base on flat surface', TRUE, NOW()),
('fp-005', 'Laptop', 'Apple', 'Butterfly / Low-Travel Key Switch Failure', 'Debris intrusion under scissor-switch mechanisms leading to double keystrokes or unresponsive keys.', 3, 55, 6, 90.0, 240.0, 'Clean with 75-degree angled air blast|Use silicone keyboard protector in dusty environments', TRUE, NOW()),
('fp-006', 'Laptop', NULL, 'NVMe SSD Controller Endurance Exhaustion', 'NAND flash write endurance (TBW) reaching manufacturer limit resulting in read-only lock or sector corruption.', 4, 50, 9, 80.0, 190.0, 'Perform immediate full cloud/external backup|Reduce unnecessary disk paging/swap|Monitor S.M.A.R.T. metrics', TRUE, NOW()),
('fp-007', 'Gaming Console', 'Sony', 'Liquid Metal Thermal Runoff & APU Overheating', 'Oxidation and dry spot development on the Accelerated Processing Unit dye causing thermal shutdown during peak load.', 2, 60, 8, 75.0, 150.0, 'Keep console in well-ventilated vertical/horizontal orientation|Clean rear air vents every 3 months|Service cooling chamber', TRUE, NOW()),
('fp-008', 'Gaming Console', 'Microsoft', 'Optical Drive Laser Diode Decay', 'Bluray drive optical lens degradation preventing disc read and installation operations.', 3, 50, 5, 45.0, 95.0, 'Use digital installation when possible|Clean lens with non-abrasive optical cleaning kit', TRUE, NOW()),
('fp-009', 'Tablet', NULL, 'Digitizer Micro-Delamination', 'Adhesive failure between tempered glass and touch capacitive layer causing phantom touches or dead zones.', 3, 60, 6, 70.0, 160.0, 'Avoid extreme temperature fluctuations|Use rigid protective folio case', TRUE, NOW()),
('fp-010', 'Smartwatch', NULL, 'Water Ingress Seal Degradation', 'Elastomer gasket degradation due to soap/chlorine exposure compromising IP68 water resistance.', 2, 65, 7, 40.0, 90.0, 'Avoid hot showers or sauna with wearable|Rinse with freshwater after swimming|Service seals bi-annually', TRUE, NOW());
