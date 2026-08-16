-- RepairVerse AI Database Initial Schema
-- Matches DATABASE_SCHEMA.md

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    profile_image VARCHAR(255),
    phone VARCHAR(30),
    location VARCHAR(100),
    bio TEXT,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(100) NOT NULL,
    serial_number VARCHAR(100),
    purchase_date VARCHAR(20),
    warranty_expiry VARCHAR(20),
    purchase_price DOUBLE PRECISION,
    current_condition VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_devices_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS device_health (
    id VARCHAR(36) PRIMARY KEY,
    device_id VARCHAR(36) NOT NULL UNIQUE,
    battery_health INT,
    health_score INT NOT NULL DEFAULT 80,
    last_service VARCHAR(20),
    maintenance_due VARCHAR(20),
    ai_prediction TEXT,
    CONSTRAINT fk_health_device FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS diagnosis_reports (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    device_id VARCHAR(36),
    image_url VARCHAR(500),
    symptoms TEXT NOT NULL,
    probable_issue VARCHAR(255) NOT NULL,
    confidence_score INT NOT NULL,
    repair_difficulty VARCHAR(50) NOT NULL,
    repair_time VARCHAR(50),
    repair_cost DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS ai_recommendations (
    id VARCHAR(36) PRIMARY KEY,
    diagnosis_id VARCHAR(36) NOT NULL UNIQUE,
    recommendation TEXT NOT NULL,
    repair_score INT NOT NULL,
    replace_score INT NOT NULL,
    carbon_saved DOUBLE PRECISION NOT NULL,
    money_saved DOUBLE PRECISION NOT NULL,
    CONSTRAINT fk_rec_diagnosis FOREIGN KEY (diagnosis_id) REFERENCES diagnosis_reports(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS repair_history (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(36) NOT NULL,
    technician_name VARCHAR(100),
    technician_role VARCHAR(100),
    shop_name VARCHAR(100),
    shop_address VARCHAR(255),
    repair_type VARCHAR(255) NOT NULL,
    description TEXT,
    diagnosis_issue VARCHAR(255),
    diagnosis_confidence INT,
    status VARCHAR(50) NOT NULL DEFAULT 'Completed',
    repair_date VARCHAR(20) NOT NULL,
    repair_duration VARCHAR(50),
    parts_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    labor_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_cost DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    warranty_period VARCHAR(100),
    warranty_until VARCHAR(20),
    is_warranty_active BOOLEAN DEFAULT FALSE,
    co2_saved_kg DOUBLE PRECISION DEFAULT 0.0,
    ewaste_reduced_kg DOUBLE PRECISION DEFAULT 0.0,
    money_saved DOUBLE PRECISION DEFAULT 0.0,
    notes TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS repair_parts (
    id VARCHAR(36) PRIMARY KEY,
    repair_id VARCHAR(36) NOT NULL,
    name VARCHAR(150) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    cost DOUBLE PRECISION NOT NULL,
    part_number VARCHAR(100),
    CONSTRAINT fk_parts_repair FOREIGN KEY (repair_id) REFERENCES repair_history(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS repair_timeline_stages (
    id VARCHAR(36) PRIMARY KEY,
    repair_id VARCHAR(36) NOT NULL,
    stage_date VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description TEXT,
    CONSTRAINT fk_timeline_repair FOREIGN KEY (repair_id) REFERENCES repair_history(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carbon_impact (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL UNIQUE,
    co2_saved DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    ewaste_reduced DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    money_saved DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    repair_count INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_carbon_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS repair_shops (
    id VARCHAR(36) PRIMARY KEY,
    shop_name VARCHAR(150) NOT NULL,
    owner_name VARCHAR(100),
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    rating DOUBLE PRECISION DEFAULT 4.5,
    phone VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    shop_id VARCHAR(36) NOT NULL,
    booking_date VARCHAR(50) NOT NULL,
    booking_status VARCHAR(50) NOT NULL DEFAULT 'Confirmed',
    notes TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    action_url VARCHAR(255),
    action_label VARCHAR(100),
    icon_color VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    device_name VARCHAR(100),
    icon_color VARCHAR(20),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_activity_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
