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

---

# Module 9 - Autonomous Repair Planning & Lifecycle Intelligence (Phase 24)

## RepairActionPlans

Stores deterministic autonomous action plans generated per device.

Fields

- id (VARCHAR 36, PK)
- user_id (VARCHAR 36, FK -> users.id)
- device_id (VARCHAR 36, FK -> devices.id)
- device_name (VARCHAR 150)
- device_category (VARCHAR 80)
- overall_strategy (VARCHAR 50) — MONITOR, PREVENTIVE_MAINTENANCE, REPAIR, REFURBISH, REPLACE, RECYCLE
- priority_level (VARCHAR 20) — LOW, MEDIUM, HIGH, CRITICAL
- estimated_total_cost (DOUBLE)
- estimated_lifecycle_extension_months (INTEGER)
- estimated_carbon_saved (DOUBLE)
- estimated_ewaste_prevented (DOUBLE)
- status (VARCHAR 20) — ACTIVE, ARCHIVED, COMPLETED
- strategy_rationale (TEXT)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

Indexes: `idx_action_plan_device_user` ON `repair_action_plans(device_id, user_id)`, `idx_action_plan_user` ON `repair_action_plans(user_id)`

## RepairActionSteps

Stores ordered execution steps for each action plan.

Fields

- id (VARCHAR 36, PK)
- action_plan_id (VARCHAR 36, FK -> repair_action_plans.id ON DELETE CASCADE)
- step_order (INTEGER)
- title (VARCHAR 200)
- description (TEXT)
- action_type (VARCHAR 50) — INSPECT, BACKUP_DATA, CLEAN, MAINTAIN, REPAIR, REPLACE_COMPONENT, BOOK_REPAIR, MONITOR, RECYCLE
- priority (VARCHAR 20) — LOW, MEDIUM, HIGH, CRITICAL
- estimated_cost (DOUBLE)
- estimated_duration (VARCHAR 100)
- carbon_impact (DOUBLE)
- is_required (BOOLEAN)
- status (VARCHAR 20) — PENDING, IN_PROGRESS, COMPLETED, SKIPPED
- created_at (TIMESTAMP)

Indexes: `idx_step_plan` ON `repair_action_steps(action_plan_id)`

---

# Module 10 - Proactive Device Care & Smart Maintenance Automation (Phase 25)

## MaintenanceSchedules

Stores deterministic proactive care and scheduled maintenance events.

Fields

- id (VARCHAR 36, PK)
- user_id (VARCHAR 36, FK -> users.id)
- device_id (VARCHAR 36, FK -> devices.id)
- device_name (VARCHAR 150)
- device_category (VARCHAR 80)
- title (VARCHAR 200)
- description (TEXT)
- maintenance_type (VARCHAR 50) — INSPECTION, CLEANING, BATTERY_CHECK, SOFTWARE_MAINTENANCE, PREVENTIVE_REPAIR, COMPONENT_REPLACEMENT, PROFESSIONAL_SERVICE
- priority (VARCHAR 20) — LOW, MEDIUM, HIGH, CRITICAL
- scheduled_date (DATE)
- due_date (DATE)
- status (VARCHAR 20) — UPCOMING, DUE, OVERDUE, COMPLETED, SKIPPED, CANCELLED
- estimated_cost (DOUBLE)
- estimated_duration_minutes (INTEGER)
- estimated_carbon_savings (DOUBLE)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)
- completed_at (TIMESTAMP)

Indexes: `idx_ms_user_id` ON `maintenance_schedules(user_id)`, `idx_ms_device_id` ON `maintenance_schedules(device_id)`, `idx_ms_due_date` ON `maintenance_schedules(due_date)`, `idx_ms_status` ON `maintenance_schedules(status)`, `idx_ms_device_user` ON `maintenance_schedules(device_id, user_id)`, `idx_ms_user_status` ON `maintenance_schedules(user_id, status)`

---

# Module 11 - Trusted Repair Marketplace, Quotes & Reputation (Phase 26)

## RepairShopProfiles
- `id` (VARCHAR 36, PK)
- `repair_shop_id` (VARCHAR 36, UNIQUE)
- `verification_status` (VARCHAR 20) — PENDING, VERIFIED, TRUSTED, SUSPENDED
- `verification_level` (VARCHAR 20) — BASIC, VERIFIED, PREMIUM
- `years_of_experience` (INT)
- `total_repairs_completed` (INT)
- `specializations_json` (TEXT)
- `certifications_json` (TEXT)
- `average_rating` (DOUBLE PRECISION)
- `total_reviews` (INT)
- `response_rate` (DOUBLE PRECISION)
- `average_response_time_minutes` (INT)
- `warranty_offered` (BOOLEAN)
- `warranty_days` (INT)
- `created_at`, `updated_at` (TIMESTAMP)

## RepairShopSpecializations
- `id` (VARCHAR 36, PK)
- `repair_shop_id` (VARCHAR 36)
- `device_category` (VARCHAR 80)
- `brand` (VARCHAR 80)
- `specialization_level` (VARCHAR 20) — BASIC, EXPERIENCED, EXPERT, CERTIFIED
- `created_at` (TIMESTAMP)

## RepairQuotes
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36)
- `device_id` (VARCHAR 36)
- `repair_shop_id` (VARCHAR 36)
- `diagnosis_id`, `recommendation_id` (VARCHAR 36)
- `repair_title` (VARCHAR 200)
- `problem_summary` (TEXT)
- `estimated_cost`, `minimum_cost`, `maximum_cost` (DOUBLE PRECISION)
- `estimated_duration_hours`, `parts_cost`, `labor_cost` (DOUBLE PRECISION)
- `warranty_days` (INT)
- `status` (VARCHAR 20) — REQUESTED, DRAFT, SUBMITTED, ACCEPTED, REJECTED, EXPIRED, CANCELLED
- `created_at`, `updated_at`, `expires_at` (TIMESTAMP)

## RepairReviews
- `id` (VARCHAR 36, PK)
- `user_id`, `repair_shop_id`, `booking_id` (VARCHAR 36)
- `rating` (INT 1-5)
- `title` (VARCHAR 200), `comment` (TEXT)
- `repair_quality_rating`, `communication_rating`, `value_rating`, `timeliness_rating` (INT 1-5)
- `verified_repair` (BOOLEAN)
- `created_at`, `updated_at` (TIMESTAMP)

---

# Module 12 - Smart Repair Matching & Marketplace Telemetry (Phase 27)

## RepairMatchHistory
Stores deterministic matching records, scores, rankings, and explanation factors between user devices and repair shops.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36)
- `device_id` (VARCHAR 36)
- `repair_shop_id` (VARCHAR 36)
- `match_score` (INT) — 0 to 100 compatibility score
- `match_level` (VARCHAR 30) — EXCELLENT_MATCH, GREAT_MATCH, GOOD_MATCH, FAIR_MATCH, LOW_MATCH
- `rank_position` (INT)
- `factors_json` (TEXT) — Itemized 7-dimension score factors
- `explanation` (TEXT) — Explainable recommendation summary
- `created_at` (TIMESTAMP)

Indexes: `idx_rmh_user_device`, `idx_rmh_shop_id`, `idx_rmh_created_at`

## MarketplaceInteractions
Stores anonymized and structured marketplace events for user savings tracking and platform conversion analytics.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36)
- `interaction_type` (VARCHAR 50) — SHOP_VIEWED, SHOP_COMPARED, QUOTE_REQUESTED, QUOTE_VIEWED, QUOTE_ACCEPTED, QUOTE_REJECTED, MATCH_SEARCHED
- `entity_id` (VARCHAR 36)
- `entity_type` (VARCHAR 30) — SHOP, QUOTE, MATCH, DEVICE
- `metadata_json` (TEXT)
- `created_at` (TIMESTAMP)

Indexes: `idx_mi_user_id`, `idx_mi_interaction_type`, `idx_mi_entity`, `idx_mi_created_at`