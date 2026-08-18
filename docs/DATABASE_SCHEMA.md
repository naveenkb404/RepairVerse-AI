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

- id
- user_id
- co2_saved
- ewaste_reduced
- money_saved
- repair_count

---

# Module 6 - Repair Shops

## RepairShops

Fields

- id
- shop_name
- owner_name
- address
- latitude
- longitude
- rating
- phone

---

## Bookings

Fields

- id
- user_id
- shop_id
- booking_date
- booking_status
- notes

---

# Module 7 - Dashboard

## Notifications

Fields

- id
- user_id
- title
- message
- is_read
- created_at

---

# Module 8 - Analytics

## ActivityLogs

Fields

- id
- user_id
- activity
- ip_address
- created_at