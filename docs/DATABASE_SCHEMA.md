# RepairVerse AI - Database Schema

## Database

PostgreSQL (Recommended)

Alternative:
MySQL

---

# Module 1 - Authentication

## Users

Stores registered users.

Fields

- id (UUID)
- full_name
- email
- password_hash
- profile_image
- phone
- role
- created_at
- updated_at

---

## Roles

Stores user roles.

Fields

- id
- role_name

Roles:

- USER
- TECHNICIAN
- ADMIN

---

# Module 2 - Device

## Devices

Stores user devices.

Fields

- id
- user_id
- device_name
- category
- brand
- model
- serial_number
- purchase_date
- warranty_expiry
- purchase_price
- current_condition
- created_at
- updated_at

---

## DeviceHealth

Stores health information.

Fields

- id
- device_id
- battery_health
- health_score
- last_service
- maintenance_due
- ai_prediction

---

# Module 3 - AI

## DiagnosisReports

Stores AI diagnosis results.

Fields

- id
- device_id
- image_url
- symptoms
- probable_issue
- confidence_score
- repair_difficulty
- repair_time
- repair_cost
- created_at

---

## AIRecommendations

Stores AI suggestions.

Fields

- id
- diagnosis_id
- recommendation
- repair_score
- replace_score
- carbon_saved
- money_saved

---

# Module 4 - Repair

## RepairHistory

Fields

- id
- device_id
- technician_id
- repair_type
- replaced_parts
- repair_cost
- repair_date

---

## RepairGuides

Fields

- id
- title
- category
- difficulty
- estimated_time
- guide_content

---

# Module 5 - Carbon

## CarbonImpact

Fields

- id (VARCHAR 36, PK)
- user_id (VARCHAR 36, UNIQUE, FK -> users.id)
- co2_saved (DOUBLE PRECISION)
- ewaste_reduced (DOUBLE PRECISION)
- money_saved (DOUBLE PRECISION)
- repair_count (INT)
- sustainability_score (INT, Default 80)
- updated_at (TIMESTAMP)

Index: `idx_carbon_user_id` ON `carbon_impact(user_id)`

---

# Module 6 - Repair Shops

## RepairShops

Fields

- id (VARCHAR 36, PK)
- shop_name (VARCHAR 150)
- owner_name (VARCHAR 100)
- address (VARCHAR 255)
- latitude (DOUBLE PRECISION)
- longitude (DOUBLE PRECISION)
- rating (DOUBLE PRECISION)
- review_count (INT)
- phone (VARCHAR 30)
- email (VARCHAR 100)
- hours (VARCHAR 100)
- services_json (TEXT)
- service_categories_json (TEXT)
- certified_brands_json (TEXT)
- estimated_turnaround (VARCHAR 100)
- avg_price (VARCHAR 50)
- verified (BOOLEAN)
- is_open (BOOLEAN)
- eco_certified (BOOLEAN)
- is_demo (BOOLEAN)
- created_at (TIMESTAMP)

---

## Bookings

Fields

- id (VARCHAR 36, PK)
- user_id (VARCHAR 36, FK -> users.id)
- shop_id (VARCHAR 36, FK -> repair_shops.id)
- booking_date (VARCHAR 50)
- booking_status (VARCHAR 50, Default 'SCHEDULED')
- notes (TEXT)
- created_at (TIMESTAMP)

Indexes: `idx_bookings_user_id`, `idx_bookings_shop_id`, `idx_bookings_created_at`

---

# Module 7 - Dashboard & Notifications

## Notifications

Fields

- id (VARCHAR 36, PK)
- user_id (VARCHAR 36, FK -> users.id)
- type (VARCHAR 50)
- title (VARCHAR 150)
- message (TEXT)
- is_read (BOOLEAN)
- action_url (VARCHAR 255)
- action_label (VARCHAR 100)
- icon_color (VARCHAR 20)
- created_at (TIMESTAMP)

Indexes: `idx_notif_user_read` ON `notifications(user_id, is_read)`, `idx_notif_created_at` ON `notifications(created_at)`


---

# Module 8 - Analytics

## ActivityLogs

Fields

- id
- user_id
- activity
- ip_address
- created_at