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

---

# Module 13 - Repair Network Intelligence, Quality & Trust Platform (Phase 28)

## RepairServiceOutcomes
Records repair completion outcomes, success/failure flags, repeat repairs, warranty claims, and actual repair metrics.
- `id` (VARCHAR 36, PK)
- `repair_shop_id` (VARCHAR 36)
- `user_id` (VARCHAR 36)
- `device_id` (VARCHAR 36)
- `quote_id` (VARCHAR 36)
- `repair_category` (VARCHAR 80)
- `repair_successful` (BOOLEAN)
- `repeat_repair_required` (BOOLEAN)
- `warranty_claimed` (BOOLEAN)
- `repair_cost` (DOUBLE PRECISION)
- `estimated_cost` (DOUBLE PRECISION)
- `turnaround_hours` (INT)
- `customer_satisfaction` (INT 1–5)
- `completed_at` (TIMESTAMP)
- `created_at` (TIMESTAMP)

Indexes: `idx_rso_shop_id`, `idx_rso_user_id`, `idx_rso_device_id`, `idx_rso_completed_at`, `idx_rso_category`

## RepairShopQualitySnapshots
Stores periodically computed deterministic quality scores and dimension breakdowns per shop.
- `id` (VARCHAR 36, PK)
- `repair_shop_id` (VARCHAR 36)
- `overall_quality_score` (INT) — 0 to 100
- `reliability_score` (INT) — 0 to 100
- `trust_score` (INT) — 0 to 100
- `customer_satisfaction_score` (INT) — 0 to 100
- `repair_success_score` (INT) — 0 to 100
- `price_fairness_score` (INT) — 0 to 100
- `service_speed_score` (INT) — 0 to 100
- `total_repairs` (INT)
- `successful_repairs` (INT)
- `failed_repairs` (INT)
- `repeat_repairs` (INT)
- `average_rating` (DOUBLE PRECISION)
- `quality_tier` (VARCHAR 30) — ELITE, EXCELLENT, TRUSTED, STANDARD, NEEDS_IMPROVEMENT
- `calculated_at` (TIMESTAMP)
- `created_at` (TIMESTAMP)

Indexes: `idx_rsqs_shop_id`, `idx_rsqs_quality_score`, `idx_rsqs_trust_score`, `idx_rsqs_tier`, `idx_rsqs_calculated_at`

## MarketplaceAnomalies
Stores deterministically detected risk signals and marketplace anomalies for admin-only review.
- `id` (VARCHAR 36, PK)
- `repair_shop_id` (VARCHAR 36)
- `related_quote_id` (VARCHAR 36)
- `related_review_id` (VARCHAR 36)
- `anomaly_type` (VARCHAR 50) — SUSPICIOUS_PRICING, REVIEW_SPIKE, REVIEW_PATTERN, HIGH_REPEAT_REPAIRS, LOW_SUCCESS_RATE, UNUSUAL_CANCELLATION_RATE
- `severity` (VARCHAR 20) — LOW, MEDIUM, HIGH, CRITICAL
- `risk_score` (INT) — 0 to 100
- `description` (TEXT)
- `status` (VARCHAR 20) — OPEN, UNDER_REVIEW, RESOLVED, DISMISSED
- `detected_at` (TIMESTAMP)
- `resolved_at` (TIMESTAMP)

Indexes: `idx_ma_shop_id`, `idx_ma_status`, `idx_ma_severity`

---

# Phase 29: Circular Economy Intelligence & Personalized Sustainability Schema

## CircularImpactEvents (`circular_impact_events`)
Stores verified lifecycle events that contribute to user and platform environmental impact metrics.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id, NULLABLE)
- `event_type` (VARCHAR 50, NOT NULL) — REPAIR_COMPLETED, MAINTENANCE_PERFORMED, DEVICE_EXTENDED, DEVICE_RECYCLED, GOAL_COMPLETED, ACHIEVEMENT_UNLOCKED
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT)
- `co2_avoided_kg` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `ewaste_diverted_kg` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `financial_savings_usd` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `lifespan_extension_days` (INT, NOT NULL DEFAULT 0)
- `source_entity_type` (VARCHAR 50) — REPAIR_JOB, MAINTENANCE_LOG, DEVICE_HEALTH, MANUAL
- `source_entity_id` (VARCHAR 36)
- `created_at` (TIMESTAMP, NOT NULL)

Indexes: `idx_cie_user_id`, `idx_cie_device_id`, `idx_cie_event_type`, `idx_cie_created_at`, `idx_cie_source`

## SustainabilityGoals (`sustainability_goals`)
Stores user-defined and AI-recommended sustainability targets with deterministic tracking.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT)
- `target_metric` (VARCHAR 50, NOT NULL) — CO2_AVOIDED_KG, EWASTE_DIVERTED_KG, REPAIRS_COMPLETED, MAINTENANCE_LOGS, LIFESPAN_EXTENSION_DAYS
- `target_value` (DOUBLE PRECISION, NOT NULL)
- `current_value` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `start_date` (TIMESTAMP, NOT NULL)
- `target_date` (TIMESTAMP, NOT NULL)
- `completed_at` (TIMESTAMP, NULLABLE)
- `status` (VARCHAR 30, NOT NULL) — ACTIVE, COMPLETED, EXPIRED, CANCELLED
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP, NOT NULL)

Indexes: `idx_sg_user_id`, `idx_sg_status`, `idx_sg_target_metric`, `idx_sg_target_date`

## SustainabilityAchievements (`sustainability_achievements`)
Stores unlocked circular milestone badges for gamified sustainability engagement.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `achievement_code` (VARCHAR 50, NOT NULL) — FIRST_REPAIR, CARBON_WARRIOR, EWASTE_CHAMPION, MAINTENANCE_MASTER, CENTURY_CLUB, ZERO_WASTE_CHAMPION, LIFESPAN_LEGEND, CIRCULAR_GUARDIAN
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT, NOT NULL)
- `badge_icon` (VARCHAR 100, NOT NULL)
- `tier` (VARCHAR 30, NOT NULL) — BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
- `unlocked_at` (TIMESTAMP, NOT NULL)
- `created_at` (TIMESTAMP, NOT NULL)

Constraints: Unique `(user_id, achievement_code)`
Indexes: `idx_sa_user_id`, `idx_sa_achievement_code`, `idx_sa_tier`

---

# Phase 30: AI Repair Ecosystem Intelligence & Personalized Device Decision Engine Schema

## DeviceDecisionSnapshots (`device_decision_snapshots`)
Stores historical multi-signal decision evaluations generated for devices.
- `id` (VARCHAR 36, PK)
- `device_id` (VARCHAR 36, FK → devices.id, NOT NULL)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `intelligence_score` (INT, NOT NULL DEFAULT 0) — 0 to 100
- `recommended_action` (VARCHAR 50, NOT NULL) — CONTINUE_USING, MONITOR, MAINTENANCE_REQUIRED, REPAIR_NOW, PROFESSIONAL_SERVICE, REFURBISH, REPLACE, RECYCLE
- `decision_confidence` (INT, NOT NULL DEFAULT 85) — 0 to 100
- `health_score` (INT, NOT NULL DEFAULT 0)
- `failure_risk_score` (INT, NOT NULL DEFAULT 0)
- `economic_score` (INT, NOT NULL DEFAULT 0)
- `maintenance_score` (INT, NOT NULL DEFAULT 0)
- `longevity_score` (INT, NOT NULL DEFAULT 0)
- `sustainability_score` (INT, NOT NULL DEFAULT 0)
- `repair_history_score` (INT, NOT NULL DEFAULT 0)
- `explanation_summary` (TEXT)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dds_device_id`, `idx_dds_user_id`, `idx_dds_created_at`, `idx_dds_device_created`

## DeviceDecisionScenarios (`device_decision_scenarios`)
Stores generated alternative what-if decision scenarios.
- `id` (VARCHAR 36, PK)
- `device_id` (VARCHAR 36, FK → devices.id, NOT NULL)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `scenario_type` (VARCHAR 50, NOT NULL) — CONTINUE_USING, MONITOR, MAINTENANCE, REPAIR, PROFESSIONAL_SERVICE, REFURBISH, REPLACE, RECYCLE
- `estimated_cost` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `estimated_lifespan_months` (INT, NOT NULL DEFAULT 0)
- `estimated_co2_impact` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `estimated_savings` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `intelligence_score` (INT, NOT NULL DEFAULT 0)
- `recommendation` (TEXT)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_ddsc_device_id`, `idx_ddsc_user_id`, `idx_ddsc_type`, `idx_ddsc_device_type`

## DeviceIntelligenceAlerts (`device_intelligence_alerts`)
Stores actionable, user-scoped intelligence and risk alerts.
- `id` (VARCHAR 36, PK)
- `device_id` (VARCHAR 36, FK → devices.id, NOT NULL)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `alert_type` (VARCHAR 50, NOT NULL) — FAILURE_RISK, MAINTENANCE_REQUIRED, REPAIR_RECOMMENDED, COST_ESCALATION, END_OF_LIFE, SUSTAINABILITY_OPPORTUNITY
- `severity` (VARCHAR 20, NOT NULL DEFAULT 'MEDIUM') — INFO, LOW, MEDIUM, HIGH, CRITICAL
- `title` (VARCHAR 255, NOT NULL)
- `message` (TEXT, NOT NULL)
- `recommended_action` (VARCHAR 50)
- `is_read` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dia_user_id`, `idx_dia_device_id`, `idx_dia_is_read`, `idx_dia_user_unread`, `idx_dia_type`

---

# Phase 31: Autonomous Repair Agent & Proactive Device Intervention System Schema

## AutonomousInterventions (`autonomous_interventions`)
Stores proactively detected device anomalies, automated evaluations, priority rankings, and financial/carbon estimates.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id, NULLABLE)
- `intervention_type` (VARCHAR 50, NOT NULL) — MONITOR, MAINTENANCE, PREVENTIVE_REPAIR, URGENT_REPAIR, PROFESSIONAL_SERVICE, SHOP_RECOMMENDATION, QUOTE_REQUEST, DEVICE_OPTIMIZATION, REFURBISH, REPLACE, RECYCLE
- `priority` (VARCHAR 20, NOT NULL DEFAULT 'MEDIUM') — LOW, MEDIUM, HIGH, CRITICAL
- `status` (VARCHAR 30, NOT NULL DEFAULT 'DETECTED') — DETECTED, PENDING_APPROVAL, APPROVED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT, NOT NULL)
- `reason` (TEXT)
- `confidence_score` (DOUBLE PRECISION, NOT NULL DEFAULT 0.85)
- `priority_score` (INT, NOT NULL DEFAULT 50) — 0 to 100
- `estimated_cost` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `estimated_savings` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `estimated_co2_impact` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `recommended_action` (VARCHAR 50)
- `requires_user_approval` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `resolved_at` (TIMESTAMP, NULLABLE)

Indexes: `idx_ai_user_id`, `idx_ai_device_id`, `idx_ai_priority`, `idx_ai_status`, `idx_ai_type`, `idx_ai_created_at`

## AutonomousActionPlans (`autonomous_action_plans`)
Stores the multi-step prescriptive remediation roadmap associated with an intervention.
- `id` (VARCHAR 36, PK)
- `intervention_id` (VARCHAR 36, FK → autonomous_interventions.id ON DELETE CASCADE, NOT NULL)
- `plan_name` (VARCHAR 255, NOT NULL)
- `objective` (TEXT, NOT NULL)
- `total_steps` (INT, NOT NULL DEFAULT 0)
- `completed_steps` (INT, NOT NULL DEFAULT 0)
- `status` (VARCHAR 30, NOT NULL DEFAULT 'PENDING') — PENDING, PENDING_APPROVAL, APPROVED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aap_intervention_id`, `idx_aap_status`

## AutonomousActionSteps (`autonomous_action_steps`)
Stores individual sequenced remediation actions with safety classification and approval state.
- `id` (VARCHAR 36, PK)
- `plan_id` (VARCHAR 36, FK → autonomous_action_plans.id ON DELETE CASCADE, NOT NULL)
- `step_order` (INT, NOT NULL)
- `action_type` (VARCHAR 50, NOT NULL) — GENERATE_REPORT, SCHEDULE_MAINTENANCE, FIND_SHOPS, REQUEST_QUOTE, COMPARE_OPTIONS, BOOK_SERVICE, DISPOSE_RECYCLE, NOTIFY_USER
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT, NOT NULL)
- `status` (VARCHAR 30, NOT NULL DEFAULT 'PENDING') — PENDING, WAITING_APPROVAL, APPROVED, REJECTED, RUNNING, COMPLETED, FAILED, CANCELLED
- `requires_approval` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `action_metadata` (TEXT, NULLABLE)
- `scheduled_for` (TIMESTAMP, NULLABLE)
- `completed_at` (TIMESTAMP, NULLABLE)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aas_plan_id`, `idx_aas_status`, `idx_aas_requires_approval`, `idx_aas_step_order`

## AgentExecutionHistory (`agent_execution_history`)
Immutable chronological audit log of all autonomous agent execution results.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id, NULLABLE)
- `intervention_id` (VARCHAR 36, FK → autonomous_interventions.id ON DELETE SET NULL, NULLABLE)
- `action_step_id` (VARCHAR 36, FK → autonomous_action_steps.id ON DELETE SET NULL, NULLABLE)
- `action_type` (VARCHAR 50, NOT NULL)
- `execution_status` (VARCHAR 30, NOT NULL) — SUCCESS, FAILED, SKIPPED, TIMEOUT
- `result_summary` (TEXT, NOT NULL)
- `executed_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aeh_user_id`, `idx_aeh_device_id`, `idx_aeh_intervention_id`, `idx_aeh_status`, `idx_aeh_executed_at`

---

# Phase 32: AI Repair Knowledge Graph & Ecosystem Learning Intelligence Schema

## RepairKnowledgeNodes (`repair_knowledge_nodes`)
Represents categorized domain entities (device models, categories, components, symptoms, failure modes, actions, parts, shops, outcomes).
- `id` (VARCHAR 36, PK)
- `node_type` (VARCHAR 50, NOT NULL) — DEVICE_MODEL, DEVICE_CATEGORY, COMPONENT, SYMPTOM, FAILURE_MODE, REPAIR_ACTION, REPAIR_PART, REPAIR_SHOP, REPAIR_OUTCOME
- `node_key` (VARCHAR 100, NOT NULL)
- `display_name` (VARCHAR 255, NOT NULL)
- `description` (TEXT)
- `metadata` (TEXT)
- `confidence_score` (DOUBLE PRECISION, NOT NULL DEFAULT 0.85)
- `observation_count` (INT, NOT NULL DEFAULT 1)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Constraints: Unique `(node_type, node_key)`
Indexes: `idx_rkn_type`, `idx_rkn_key`, `idx_rkn_obs_count`, `idx_rkn_confidence`

## RepairKnowledgeRelationships (`repair_knowledge_relationships`)
Represents weighted directed graph edges linking entities.
- `id` (VARCHAR 36, PK)
- `source_node_id` (VARCHAR 36, FK → repair_knowledge_nodes.id ON DELETE CASCADE, NOT NULL)
- `target_node_id` (VARCHAR 36, FK → repair_knowledge_nodes.id ON DELETE CASCADE, NOT NULL)
- `relationship_type` (VARCHAR 50, NOT NULL) — HAS_COMPONENT, EXHIBITS_SYMPTOM, INDICATES_FAILURE, CAUSED_BY, RESOLVED_BY, REQUIRES_PART, PERFORMED_BY, RESULTED_IN, SIMILAR_TO, PREVENTS
- `strength` (DOUBLE PRECISION, NOT NULL DEFAULT 50.0) — 0 to 100
- `confidence` (DOUBLE PRECISION, NOT NULL DEFAULT 0.80) — 0.0 to 1.0
- `observation_count` (INT, NOT NULL DEFAULT 1)
- `metadata` (TEXT)
- `first_observed_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `last_observed_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Constraints: Unique `(source_node_id, target_node_id, relationship_type)`
Indexes: `idx_rkr_source`, `idx_rkr_target`, `idx_rkr_type`, `idx_rkr_strength`, `idx_rkr_source_type`

## RepairPatternInsights (`repair_pattern_insights`)
Stores discovered ecosystem-wide failure, repair, and sustainability insights.
- `id` (VARCHAR 36, PK)
- `insight_type` (VARCHAR 50, NOT NULL) — COMMON_FAILURE, HIGH_SUCCESS_REPAIR, RECURRING_COMPONENT_ISSUE, SHOP_SPECIALIZATION, COST_EFFECTIVE_REPAIR, PREVENTIVE_OPPORTUNITY, SUSTAINABILITY_PATTERN
- `title` (VARCHAR 255, NOT NULL)
- `description` (TEXT, NOT NULL)
- `confidence` (DOUBLE PRECISION, NOT NULL DEFAULT 0.85)
- `impact_score` (INT, NOT NULL DEFAULT 50) — 0 to 100
- `supporting_observations` (INT, NOT NULL DEFAULT 1)
- `device_category` (VARCHAR 50)
- `status` (VARCHAR 30, NOT NULL DEFAULT 'ACTIVE')
- `generated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_rpi_type`, `idx_rpi_category`, `idx_rpi_status`, `idx_rpi_confidence`, `idx_rpi_impact`

## RepairKnowledgeFeedback (`repair_knowledge_feedback`)
Captures user rating and validation feedback on pattern insights.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id ON DELETE CASCADE, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE SET NULL)
- `insight_id` (VARCHAR 36, FK → repair_pattern_insights.id ON DELETE CASCADE)
- `feedback_type` (VARCHAR 50, NOT NULL) — HELPFUL, NOT_HELPFUL, ACCURATE, INACCURATE
- `rating` (INT)
- `comment` (TEXT)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_rkf_user_id`, `idx_rkf_device_id`, `idx_rkf_insight_id`, `idx_rkf_type`

---

# Module 18 — AI Repair Ecosystem Digital Twin & Predictive Optimization Engine (`V18__repair_ecosystem_digital_twin.sql`)

## DigitalTwinSnapshot (`digital_twin_snapshots`)
Stores the authoritative point-in-time state of a device's digital twin.
- `id` (VARCHAR 36, PK)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `user_id` (VARCHAR 36, FK → users.id ON DELETE CASCADE, NOT NULL)
- `overall_health_score` (DOUBLE, NOT NULL)
- `performance_index` (DOUBLE, NOT NULL)
- `reliability_index` (DOUBLE, NOT NULL)
- `sustainability_index` (DOUBLE, NOT NULL)
- `financial_risk_score` (DOUBLE, NOT NULL)
- `predicted_lifespan_months` (INT, NOT NULL)
- `repair_probability_next_6_months` (DOUBLE, NOT NULL)
- `estimated_remaining_value` (DOUBLE, NOT NULL)
- `carbon_debt_score` (DOUBLE, NOT NULL)
- `twin_confidence_score` (DOUBLE, NOT NULL)
- `recommended_action` (VARCHAR 50, NOT NULL)
- `simulation_status` (VARCHAR 30, NOT NULL DEFAULT 'STABLE')
- `last_refreshed` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dts_device_id`, `idx_dts_user_id`, `idx_dts_refreshed`, `idx_dts_health_score`

## DigitalTwinForecast (`digital_twin_forecasts`)
Stores multi-horizon health and reliability forecasts anchored to a snapshot.
- `id` (VARCHAR 36, PK)
- `snapshot_id` (VARCHAR 36, FK → digital_twin_snapshots.id ON DELETE CASCADE, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `horizon_months` (INT, NOT NULL) — 3, 6, 12, 24
- `forecasted_health_score` (DOUBLE, NOT NULL)
- `forecasted_reliability` (DOUBLE, NOT NULL)
- `failure_probability` (DOUBLE, NOT NULL)
- `estimated_repair_cost` (DOUBLE, NOT NULL)
- `recommended_action` (VARCHAR 50, NOT NULL)
- `confidence_score` (DOUBLE, NOT NULL)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dtf_snapshot_id`, `idx_dtf_device_id`, `idx_dtf_horizon`

## DigitalTwinScenario (`digital_twin_scenarios`)
Stores pre-built and custom future scenario simulation results.
- `id` (VARCHAR 36, PK)
- `snapshot_id` (VARCHAR 36, FK → digital_twin_snapshots.id ON DELETE CASCADE, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `scenario_type` (VARCHAR 50, NOT NULL) — MAINTAIN, REPAIR_OPTIMIZE, UPGRADE, REPURPOSE, RECYCLE, CUSTOM
- `scenario_name` (VARCHAR 200, NOT NULL)
- `projected_health_score` (DOUBLE, NOT NULL)
- `estimated_cost` (DOUBLE, NOT NULL)
- `estimated_lifespan_gain_months` (INT, NOT NULL)
- `sustainability_impact` (DOUBLE, NOT NULL)
- `financial_roi` (DOUBLE, NOT NULL)
- `risk_level` (VARCHAR 30, NOT NULL)
- `recommendation` (TEXT)
- `simulation_parameters` (TEXT) — JSON
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dtsc_snapshot_id`, `idx_dtsc_device_id`, `idx_dtsc_type`

## DigitalTwinOptimizationResult (`digital_twin_optimization_results`)
Stores the output of the 6-factor deterministic strategy optimizer.
- `id` (VARCHAR 36, PK)
- `snapshot_id` (VARCHAR 36, FK → digital_twin_snapshots.id ON DELETE CASCADE, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `recommended_strategy` (VARCHAR 50, NOT NULL)
- `strategy_name` (VARCHAR 200, NOT NULL)
- `composite_score` (DOUBLE, NOT NULL)
- `financial_score` (DOUBLE, NOT NULL) — weight 25%
- `reliability_score` (DOUBLE, NOT NULL) — weight 20%
- `longevity_score` (DOUBLE, NOT NULL) — weight 15%
- `risk_reduction_score` (DOUBLE, NOT NULL) — weight 15%
- `sustainability_score` (DOUBLE, NOT NULL) — weight 15%
- `downtime_score` (DOUBLE, NOT NULL) — weight 10%
- `estimated_cost` (DOUBLE, NOT NULL)
- `estimated_roi` (DOUBLE, NOT NULL)
- `payback_months` (INT, NOT NULL)
- `key_recommendations` (TEXT) — newline-delimited list
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_dtop_snapshot_id`, `idx_dtop_device_id`, `idx_dtop_strategy`

## EcosystemSimulationEvent (`ecosystem_simulation_events`)
Chronological audit log of all digital twin simulation events.
- `id` (VARCHAR 36, PK)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `user_id` (VARCHAR 36, FK → users.id ON DELETE CASCADE, NOT NULL)
- `event_type` (VARCHAR 80, NOT NULL) — TWIN_SNAPSHOT_CREATED, FORECAST_UPDATED, SCENARIO_SIMULATED, OPTIMIZATION_COMPLETED, RISK_ALERT, MAINTENANCE_TRIGGERED
- `title` (VARCHAR 200, NOT NULL)
- `description` (TEXT)
- `severity` (VARCHAR 20, NOT NULL DEFAULT 'INFO') — INFO, WARNING, CRITICAL
- `metadata` (TEXT) — JSON
- `event_timestamp` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_ese_device_id`, `idx_ese_user_id`, `idx_ese_type`, `idx_ese_severity`, `idx_ese_timestamp`

---

# Module 19 — AI Decision Trust, Explainability & Governance Engine (`V19__ai_decision_trust_engine.sql`)

## AiDecisionRecord (`ai_decision_records`)
Authoritative, immutable ledger of all AI recommendation and autonomous decision records.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id ON DELETE CASCADE, NOT NULL)
- `device_id` (VARCHAR 36, FK → devices.id ON DELETE CASCADE, NOT NULL)
- `source_system` (VARCHAR 60, NOT NULL) — AUTONOMOUS_REPAIR_AGENT, DIGITAL_TWIN, DEVICE_INTELLIGENCE, REPAIR_KNOWLEDGE_GRAPH, PROACTIVE_INTERVENTION, DIAGNOSIS
- `decision_type` (VARCHAR 80, NOT NULL)
- `source_record_id` (VARCHAR 36, NULLABLE)
- `decision_output` (TEXT, NOT NULL)
- `confidence_score` (INT, NOT NULL) — 0 to 100
- `trust_score` (INT, NOT NULL) — 0 to 100
- `trust_tier` (VARCHAR 30, NOT NULL) — VERIFIED, RELIABLE, CAUTION, REVIEW_REQUIRED
- `risk_level` (VARCHAR 30, NOT NULL) — LOW, MEDIUM, HIGH, CRITICAL
- `status` (VARCHAR 30, NOT NULL DEFAULT 'ACTIVE') — ACTIVE, FLAGGED, OVERRIDDEN, EXECUTED, REJECTED
- `user_reviewed` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `user_feedback` (VARCHAR 30, NULLABLE) — AGREE, DISAGREE, UNSURE
- `why_explanation` (TEXT, NOT NULL)
- `how_explanation` (TEXT, NOT NULL)
- `what_if_explanation` (TEXT, NOT NULL)
- `impact_explanation` (TEXT, NOT NULL)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aidr_user_id`, `idx_aidr_device_id`, `idx_aidr_source_system`, `idx_aidr_trust_tier`, `idx_aidr_status`, `idx_aidr_created_at`

## AiDecisionEvidence (`ai_decision_evidence`)
Discrete multi-signal evidence traces corroborating an AI decision record.
- `id` (VARCHAR 36, PK)
- `decision_record_id` (VARCHAR 36, FK → ai_decision_records.id ON DELETE CASCADE, NOT NULL)
- `evidence_type` (VARCHAR 60, NOT NULL) — SENSOR_TELEMETRY, HISTORICAL_BENCHMARK, PREDICTIVE_HORIZON, CONSENT_AUTHORIZATION, KNOWLEDGE_GRAPH_LINK
- `evidence_key` (VARCHAR 100, NOT NULL)
- `evidence_value` (TEXT, NOT NULL)
- `evidence_weight` (DOUBLE PRECISION, NOT NULL DEFAULT 1.0)
- `evidence_source` (VARCHAR 80, NOT NULL)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aide_decision_id`, `idx_aide_type`, `idx_aide_source`

## AiGovernanceRule (`ai_governance_rules`)
Deterministic compliance, safety, and operational guardrail rules.
- `id` (VARCHAR 36, PK)
- `rule_name` (VARCHAR 120, NOT NULL)
- `rule_category` (VARCHAR 50, NOT NULL) — SAFETY, FINANCIAL, CONSISTENCY, DATA_INTEGRITY, AUTONOMY
- `description` (TEXT, NOT NULL)
- `applies_to_systems` (VARCHAR 255, NOT NULL) — Comma-separated or ALL
- `severity` (VARCHAR 20, NOT NULL) — LOW, MEDIUM, HIGH, CRITICAL
- `threshold_value` (DOUBLE PRECISION, NOT NULL DEFAULT 0.0)
- `is_active` (BOOLEAN, NOT NULL DEFAULT TRUE)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aigr_category`, `idx_aigr_severity`, `idx_aigr_is_active`

## AiGovernanceViolation (`ai_governance_violations`)
Audit log of rule triggers and constraint breaches.
- `id` (VARCHAR 36, PK)
- `decision_record_id` (VARCHAR 36, FK → ai_decision_records.id ON DELETE CASCADE, NOT NULL)
- `rule_id` (VARCHAR 36, FK → ai_governance_rules.id ON DELETE CASCADE, NOT NULL)
- `rule_name` (VARCHAR 120, NOT NULL)
- `violation_message` (TEXT, NOT NULL)
- `severity` (VARCHAR 20, NOT NULL)
- `auto_resolved` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_aigv_decision_id`, `idx_aigv_rule_id`, `idx_aigv_severity`, `idx_aigv_resolved`

## UserAutonomyPreference (`user_autonomy_preferences`)
User-configured governance thresholds and autonomous execution limits.
- `id` (VARCHAR 36, PK)
- `user_id` (VARCHAR 36, FK → users.id ON DELETE CASCADE, NOT NULL, UNIQUE)
- `allow_autonomous_interventions` (BOOLEAN, NOT NULL DEFAULT TRUE)
- `allow_auto_scheduling` (BOOLEAN, NOT NULL DEFAULT FALSE)
- `allow_proactive_alerts` (BOOLEAN, NOT NULL DEFAULT TRUE)
- `min_confidence_threshold` (INT, NOT NULL DEFAULT 75)
- `require_approval_above_cost` (DOUBLE PRECISION, NOT NULL DEFAULT 3500.0)
- `notification_style` (VARCHAR 30, NOT NULL DEFAULT 'VERBOSE')
- `created_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (TIMESTAMP, NOT NULL DEFAULT CURRENT_TIMESTAMP)

Indexes: `idx_uap_user_id`