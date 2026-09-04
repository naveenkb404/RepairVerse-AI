# RepairVerse AI - System Architecture

# Overview

RepairVerse AI follows a modern, highly resilient client-server architecture.

The Next.js 16 frontend communicates with the Spring Boot 3.4 backend using REST APIs under `/api/v1`.

When the backend API server is offline or in development, the frontend automatically and transparently operates in an explicit **Demo Mode** using reference baseline metrics without compromising security, generating fake JWTs, or failing builds.

---

# Architecture Diagram

```
+-------------------------------------------------------------------------------+
|                                  USER BROWSER                                 |
|                                                                               |
|  +-------------------------------------------------------------------------+  |
|  |                     Next.js 16 App Router (React 19)                    |  |
|  |                                                                         |  |
|  |  [Landing] [Diagnosis] [Recommendation] [Shops] [Devices] [Dashboard]   |  |
|  |                                                                         |  |
|  |  +---------------------+   +---------------------+   +---------------+  |  |
|  |  |     AuthContext     |   |   Hardened Client   |   |   AuthGuard   |  |  |
|  |  | (Multi-tab Storage) |<->| (Timeout/Abort/401) |<->|  (Protection) |  |  |
|  |  +---------------------+   +---------------------+   +---------------+  |  |
|  +-------------------------------------------------------------------------+  |
+-------------------------------------------------------------------------------+
                                       |
                                       | HTTPS JSON / Multipart FormData
                                       v
+-------------------------------------------------------------------------------+
|                       SPRING BOOT REST API (Port 8080)                        |
|                                                                               |
|  +-------------------------------------------------------------------------+  |
|  |                           API Gateway / Controllers                     |  |
|  |                                                                         |  |
|  |   /auth/*    /users/*    /devices/*    /diagnosis    /repair-analysis   |  |
|  |   /carbon    /shops/*    /bookings     /notifications /admin/*          |  |
|  +-------------------------------------------------------------------------+  |
|                                      |                                        |
|  +-----------------------------------+-------------------------------------+  |
|  |                                SERVICES                                 |  |
|  |                                                                         |  |
|  |  [AuthService] [DeviceService] [AiDiagnosisService] [CarbonScoreEngine] |  |
|  +-------------------------------------------------------------------------+  |
+-------------------------------------------------------------------------------+
           |                                  |                        |
           v                                  v                        v
+---------------------+             +-------------------+    +-------------------+
|  PostgreSQL Database|             |  Google Gemini AI |    |    Cloudinary     |
|   (Relational Data) |             | (Vision & Reasoning|   |  (Image Storage)  |
+---------------------+             +-------------------+    +-------------------+
```

---

# Frontend Resilience & API Architecture (Phase 13)

### 1. Centralized Environment Configuration (`lib/config.ts`)
- Target: `NEXT_PUBLIC_API_URL` (default: `http://localhost:8080/api/v1`)
- Automatic trailing slash stripping
- Strict isolation: zero private backend credentials exposed on client

### 2. Hardened HTTP Client (`lib/api/client.ts`)
- **Supported Methods:** `GET`, `POST`, `PUT`, `PATCH`, `DELETE`
- **Payload Handling:** Automatic detection between `FormData` (multipart) and JSON payloads
- **Authorization:** Automated Bearer token injection
- **Cancellation:** Native `AbortSignal` pass-through for unmounted components
- **Timeout Management:** Default 15-second request timeout via custom `AbortController`
- **Error Mapping:** Complete status translations for `400`, `401`, `403`, `404`, `409`, `422`, `429`, `500+`, and network drops
- **Security:** Zero console leaks of JWTs, passwords, or personal data

### 3. Session Hardening & Multi-Tab Sync (`lib/context/AuthContext.tsx`)
- Multi-tab synchronization via `window.storage` event listener
- Reactive 401 Unauthorized handler automatically clears expired sessions
- Safe SSR hydration without content flashes

### 4. Global UX Boundaries
- Root Error Boundary (`app/error.tsx`)
- Custom 404 Recovery (`app/not-found.tsx`)
- Root Loading Animation (`app/loading.tsx`)
- Route-level AuthGuard (`components/common/AuthGuard.tsx`)

---

# AI Diagnosis & Pipeline Flow

```
User uploads device image & describes symptoms
  ↓
Frontend creates FormData and calls POST /api/v1/diagnosis
  ↓
Spring Boot receives image and forwards to Cloudinary for secure asset storage
  ↓
Spring Boot constructs structured prompt & sends image to Google Gemini 1.5 Pro
  ↓
Gemini analyzes structural damage, calculates confidence score, and identifies parts
  ↓
Spring Boot normalizes response into DiagnosisReport DTO & persists to database
  ↓
Frontend displays AI Hardware Analysis & triggers Phase 8 Recommendation Engine
```

---

# Backend Architecture (Phases 14, 15, 16, & 17)

### 1. Layered Architecture (`com.repairverse.ai`)
- **`config`**: `SecurityConfig`, `CorsConfig`, `CloudinaryConfig`, `AppProperties`
- **`controller`**: `AuthController`, `DiagnosisController`, `RepairAnalysisController`, `DeviceController`
- **`dto`**: `AuthRequest`, `AuthResponse`, `DiagnosisResponseDto`, `GeminiVisionResponse`, `RecommendationRequest`, `RecommendationResponseDto`, `DeviceDto`, `DevicePassportDto`, `ErrorResponse`
- **`entity`**: `User`, `Role`, `Device`, `DeviceHealth`, `DiagnosisReport`, `AIRecommendation`, `RepairGuide`
- **`exception`**: `GlobalExceptionHandler`, `EmailAlreadyExistsException`, `UnauthorizedRoleException`, `ImageUploadException`, `AiServiceException`, `InvalidFileException`, `DiagnosisNotFoundException`, `RecommendationNotFoundException`, `DeviceNotFoundException`
- **`repository`**: `UserRepository`, `DeviceRepository`, `DeviceHealthRepository`, `DiagnosisReportRepository`, `AIRecommendationRepository`, `RepairGuideRepository`
- **`security`**: `JwtTokenProvider`, `JwtAuthenticationFilter`, `CustomUserDetailsService`, `UserPrincipal`
- **`service`**: `AuthService`, `DiagnosisService`, `AiVisionService`, `CloudinaryService`, `RepairAnalysisService`, `DeviceService`, `DevicePassportService`

### 2. Spring Security & JWT Filter Chain
- **Public Endpoints**: `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `POST /api/v1/diagnosis`, `GET /api/v1/diagnosis/**`, `POST /api/v1/repair-analysis`, `GET /api/v1/repair-analysis/**`, `GET /api/v1/shops/**`, `GET /api/v1/repair-guide/**`
- **Protected Endpoints**: `GET /api/v1/auth/me`, `POST /api/v1/auth/logout`, `GET /api/v1/devices/**`, `POST /api/v1/devices`, `PUT /api/v1/devices/**`, `DELETE /api/v1/devices/**`
- **Stateless Sessions**: `SessionCreationPolicy.STATELESS`
- **BCrypt Encryption**: Passwords salted and hashed with `BCryptPasswordEncoder`
- **Cryptographic JWT**: HMAC-SHA256 tokens carrying userId, email, and fullName claims

### 3. AI Vision & Cloudinary Pipeline (Phase 15)
- **Image Pipeline**: `CloudinaryService` verifies MIME type (`image/jpeg`, `image/png`, `image/webp`) and enforces a 10MB limit before uploading to `repairverse/diagnosis` folder.
- **Vision Pipeline**: `AiVisionService` encodes image as Base64 and executes structured prompt with Google Gemini 1.5 Flash/Pro with `response_mime_type: application/json`.
- **Safety Critical Detection**: Identifies lithium battery swelling, burnt capacitors, thermal runaway, and liquid ingress.

### 4. Repair Recommendation Engine (Phase 16)
- **Deterministic Feasibility Scoring (0-100)**: Evaluates diagnostic confidence, hardware difficulty, economic cost-to-replacement ratio, and safety hazards.
- **Actions**: `REPAIR` (feasible & safe), `MONITOR` (low diagnostic certainty), `REPLACE` (uneconomic/catastrophic damage), `PROFESSIONAL_SERVICE` (high voltage / thermal hazard).
- **Economic & Carbon Calculation**: Transparent calculation of financial savings against category new-purchase baselines, and avoided embodied carbon emissions aligned with Phase 6 models.
- **Repair Plan Generation**: Itemizes step-by-step procedures, safety notes, parts (with estimated cost & part numbers), and precision toolkit requirements.

### 6. Carbon Impact, Certified Repair Shops & Notification Hub (Phase 18)
- **Carbon Engine**: `CarbonService` & `CarbonController` (`GET /api/v1/carbon`) calculating CO₂ emissions saved, e-waste avoided, financial savings, 0–100 sustainability score, and 6-month historical trends.
- **Repair Shops & Haversine Search**: `RepairShopService` & `ShopController` (`GET /api/v1/shops`) with server-side Haversine distance calculations in kilometers and coordinate bounds validation (-90 to 90 / -180 to 180).
- **Booking Pipeline & Notifications**: `BookingService` & `BookingController` handling scheduling, cancellation, 409 conflict checks, user identity context enforcement, and event hooks invoking `NotificationService`.

### 7. End-to-End Orchestration & Deployment Packaging (Phase 19)
- **System Health Diagnostics**: `SystemHealthService` & `HealthController` (`GET /api/v1/health`) performing live connectivity checks across database, Flyway, Gemini AI, and Cloudinary.
- **Production Containerization**: Multi-stage `Dockerfile` for Spring Boot (JRE 21 non-root container) and Next.js 16 (Node 20 Alpine standalone build).
- **Multi-Service Docker Compose**: Root `docker-compose.yml` linking PostgreSQL 15, Spring Boot 3 backend API, and Next.js frontend web service with automated health dependency checks and persistent volume storage (`postgres_data`).

### 8. User Profile, Smart Repair History & Unified Dashboard (Phase 20)
- **User Profile Management**: `UserService` & `UserController` (`GET/PUT /api/v1/users/profile`) managing profile details, contact information, preferences, and aggregate lifetime stats.
### 10. Predictive Maintenance & Platform Analytics (Phase 22)
- **Predictive Scoring Engine**: `PredictiveScoringService` evaluates failure probability (0-100), health score, projected remaining lifespan, and failure modes across 7 hardware categories using deterministic statistical heuristics.
- **Fault Pattern Recognition**: `FaultPatternService` classifies recurring hardware degradation patterns (e.g., thermal throttling, battery swelling, solder joint fatigue) and generates actionable preventative recommendations.
- **Cross-Service Analytics**: `RepairCostAnalyticsService`, `SustainabilityAnalyticsService`, and `AdminIntelligenceService` compute platform-wide cost distributions, ROI, and global fleet metrics.

### 11. Generative AI Explainability & Explainable AI (XAI) (Phase 23)
- **Gemini Narrative Layer**: `AiExplanationService` orchestrates Google Gemini 1.5 Flash to generate structured, natural language explainability for deterministic predictions, visual diagnoses, repair recommendations, and sustainability impact.
- **Zero-Dependency Heuristic Fallback**: If Gemini API key is not configured, or if requests time out or fail validation, the system falls back seamlessly to deterministic rule-based explainability heuristics, ensuring zero downtime and complete offline resilience.
- **Security & Authorization Invariant**: Gemini narratives are strictly explanatory/read-only and are never permitted to modify risk scores, bypass authentication, or alter database state. User data access is strictly bounded by JWT principal ownership.

### 12. Smart Repair Matching, Quotation & Marketplace Intelligence (Phase 26 & 27)
```
Device
   ↓
Diagnosis / Repair Need
   ↓
Repair Matching Engine (0–100 Compatibility Scoring)
   ├─ Specialization Match (25 pts)
   ├─ Trust & Reputation (20 pts)
   ├─ Quote Competitiveness (15 pts)
   ├─ Distance / Proximity (15 pts)
   ├─ Availability & Speed (10 pts)
   ├─ Experience & Tenure (10 pts)
   └─ Circularity & Eco Score (5 pts)
   ↓
Smart Categorized Recommendations (Best Overall, Best Value, Fastest, Most Trusted, Eco Leader, Nearest)
   ↓
Side-by-Side Multi-Shop Comparison Matrix
   ↓
Quote Intelligence & Price Fairness Analysis
   ↓
Confident Repair Decision & Booking
```
- **Deterministic 0–100 Compatibility Engine**: `RepairMatchingService` synthesizes multi-dimensional shop capabilities, customer reviews, distance, and eco-credentials to rank repair providers for specific hardware architectures.
- **Decision Category Winner Resolution**: `SmartRepairRecommendationService` calculates winners across distinct priorities (value, speed, trust, sustainability).
- **Quote Pricing Fairness & Risk Flagging**: `QuoteIntelligenceService` compares quoted repair costs against regional market averages, generating a 0–100 price fairness index and warning against suspiciously low or overpriced quotes.
- **Telemetry & Platform Analytics**: `MarketplaceAnalyticsService` captures user savings insights and aggregates platform-wide conversion and category trends for administrators.

### 13. Circular Economy Intelligence & Personalized Sustainability Optimization Engine (Phase 29)
```
Lifecycle Activity (Repair, Maintenance, Device Lifespan, Recycling)
   ↓
Circular Impact Event Ingestion (`CircularImpactService`)
   ├─ Real-time CO₂ avoidance calculation (kg)
   ├─ E-waste diverted calculation (kg)
   ├─ Financial savings calculation (USD)
   └─ Lifespan extension tracking (days)
   ↓
Deterministic Circular Impact Scoring Engine (`CircularImpactScoreService`)
   ├─ Repair Actions Dimension (30 pts)
   ├─ Maintenance Diligence Dimension (25 pts)
   ├─ Device Longevity Dimension (20 pts)
   ├─ E-Waste & Carbon Diversion Dimension (15 pts)
   └─ Sustainability Goal Progression Dimension (10 pts)
   ↓
Circular Tier Classification (`PLANET_GUARDIAN`, `CIRCULAR_HERO`, `ECO_STEWARD`, `CONSCIOUS_USER`, `BEGINNER`)
   ↓
Sustainability Optimization & Goal Engine
   ├─ `SustainabilityOptimizationService`: Rule-based actionable recommendations with carbon/cost saving potential
   ├─ `SustainabilityGoalService`: Dynamic target calculation, progress tracking, and automated completion
   ├─ `SustainabilityAchievementService`: Deterministic milestone badge unlocking with rarity tiers
   └─ `CircularEconomyAnalyticsService`: Platform aggregates, community benchmarks & leaderboard
```
- **Authoritative Determinism**: All environmental calculations (CO₂ avoided, e-waste diverted, dollar savings), score components (0–100), and goal achievements are computed deterministically by backend services.
- **Explainable Circular Scoring**: Weighted multi-factor decomposition provides transparent scoring with actionable improvement paths.
- **Gamified Milestone Engine**: Strict idempotent achievement checks prevent duplicate rewards while engaging users across repair milestones.

### 14. AI Repair Ecosystem Intelligence & Personalized Device Decision Engine (Phase 30)
```
AI Vision & Diagnosis (Phase 15) ────────┐
Device Health Passport (Phase 17) ──────┤
Predictive Failure Risk (Phase 22) ─────┤
Repair Planning & Lifecycle (Phase 24) ─┼──► UNIFIED DEVICE DECISION ENGINE (`DeviceDecisionIntelligenceService`)
Maintenance Automation (Phase 25) ─────┤        ├─ 7-Factor Weighted Intelligence Scoring (`DeviceIntelligenceScoringService`)
Repair Economics & Quotes (Phase 26/27) ┤        ├─ Authoritative Prescriptive Action Resolution (CONTINUE, REPAIR, REFURBISH, etc)
Circular Impact & Carbon (Phase 29) ────┘        ├─ Personalized Advisor Explanation (`PersonalizedDeviceAdvisorService`)
                                                 ├─ What-If Scenario Simulator (`DeviceScenarioSimulationService`)
                                                 └─ Scoped Intelligence Alerts (`DeviceIntelligenceAlertService`)
                                                          ↓
                                                 Deterministic Action Blueprint & Simulator Dashboard
```
- **Unified Intelligence Scoring (0–100)**: Evaluates Health & Reliability (25%), Failure Risk (20%), Repair Economics (15%), Maintenance Status (15%), Device Longevity (10%), Sustainability Impact (10%), and Repair History (5%) into tiers (`EXCEPTIONAL`, `HEALTHY`, `STABLE`, `AT_RISK`, `CRITICAL`).
- **One Clear Decision**: Resolves conflicting diagnostic and economic telemetry into one authoritative recommendation (`CONTINUE_USING`, `MONITOR`, `MAINTENANCE_REQUIRED`, `REPAIR_NOW`, `PROFESSIONAL_SERVICE`, `REFURBISH`, `REPLACE`, `RECYCLE`).
### 15. Autonomous Repair Agent & Proactive Device Intervention System (Phase 31)
```
Continuous Fleet Telemetry & Anomaly Detection ────────┐
Device Health Passport & Failure Predictor (Phase 22) ─┤
Unified Decision Blueprint (Phase 30) ─────────────────┼──► PROACTIVE INTERVENTION ENGINE (`ProactiveInterventionService`)
Repair Economics & Local Network (Phase 26/27) ────────┘        ├─ 6-Factor Priority Engine (`InterventionPriorityService`)
                                                               ├─ Prescriptive Action Planner (`AutonomousActionPlanningService`)
                                                               ├─ Safe Automation vs Human Approval (`AgentApprovalService`)
                                                               ├─ Execution Engine & Idempotency (`AgentExecutionService`)
                                                               └─ Master Fleet Dashboard (`AutonomousRepairAgentService`)
                                                                        ↓
                                                               Autonomous Remediation & Audit Log Stream
```
- **Proactive Anomaly Detection**: Proactively detects component failure signatures and telemetry drift across user devices before catastrophic failure occurs, preventing duplicate active interventions.
- **6-Factor Deterministic Prioritization**: Scores interventions 0–100 across Failure Risk (25%), User Impact (20%), Urgency (20%), Financial Risk (15%), Repair Opportunity (10%), and Sustainability Impact (10%) into priority tiers (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).
- **Safe Automation Hierarchy**:
  - 🟢 **Automatic**: Safe read/analysis tasks (`GENERATE_REPORT`, `NOTIFY_USER`, `FIND_SHOPS`, `COMPARE_OPTIONS`).
  - 🟡 **Requires Approval**: Medium-impact actions (`SCHEDULE_MAINTENANCE`, `REQUEST_QUOTE`).
  - 🔴 **Always Explicit Human Approval**: High-impact / financial actions (`BOOK_SERVICE`, `DISPOSE_RECYCLE`, `CONFIRM_PAYMENT`).
- **Idempotent Audit Log Runtime**: Enforces safe execution guarantees and records every agent action permanently in `agent_execution_history`.

### 16. AI Repair Knowledge Graph & Ecosystem Learning Intelligence (Phase 32)
```
DEVICE_MODEL / CATEGORY ──HAS_COMPONENT──► COMPONENT ──INDICATES_FAILURE──► FAILURE_MODE
                                                                                 │
                                                                          EXHIBITS_SYMPTOM
                                                                                 │
                                                                                 ▼
REPAIR_OUTCOME ◄──RESULTED_IN── REPAIR_ACTION ◄──RESOLVED_BY─────────────► SYMPTOM
      ▲                               │
      │                               ▼
KNOWLEDGE INSIGHT ◄───────────── REPAIR_SHOP (Specialization)
```
- **Relational Knowledge Graph Engine**: Synthesizes and maintains domain entities (nodes) and weighted directed connections (edges) capturing collective repair precedents across the ecosystem.
- **Deterministic Relationship Strength (0–100)**: Weights frequency of observations (30%), verified outcome quality (25%), observation recency (15%), confidence (20%), and community validation feedback (10%).
- **Pattern Discovery Engine**: Autonomously extracts recurring failure modes, high-confidence repair strategies, shop specializations, and component-level carbon saving patterns.
- **Privacy-Preserving Precedent Engine**: Provides 5-factor similarity scoring matching current device symptoms against anonymized historical repairs without exposing user identity.
- **Autonomous Agent Knowledge Injection**: Allows Phase 31 Autonomous Repair Agent to query learned repair strategies before generating proactive intervention roadmaps.

---

# Security Blueprint

1. **Private Backend Secrets**: Gemini API keys, Cloudinary secrets, JWT signing keys, and Database passwords are strictly managed on the Spring Boot backend.
2. **Stateless JWT Authorization**: Frontend stores Bearer token in local storage and includes `Authorization: Bearer <token>` on all private requests.
3. **Reactive Invalidation**: If a token expires or returns 401, the client automatically clears the session and alerts the user.
4. **Input Sanitization**: All client forms enforce strict regex, length, and format validation before transmission.
5. **No Email Enumeration**: Authentication failure messages are unified (`"Invalid email or password"`) to prevent account reconnaissance.
6. **Authoritative Determinism**: Generative AI models never dictate authorization or database state; all risk and security determinations are enforced by deterministic Spring Boot services.