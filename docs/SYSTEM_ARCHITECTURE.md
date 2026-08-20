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




---

# Security Blueprint

1. **Private Backend Secrets**: Gemini API keys, Cloudinary secrets, JWT signing keys, and Database passwords are strictly managed on the Spring Boot backend.
2. **Stateless JWT Authorization**: Frontend stores Bearer token in local storage and includes `Authorization: Bearer <token>` on all private requests.
3. **Reactive Invalidation**: If a token expires or returns 401, the client automatically clears the session and alerts the user.
4. **Input Sanitization**: All client forms enforce strict regex, length, and format validation before transmission.
5. **No Email Enumeration**: Authentication failure messages are unified (`"Invalid email or password"`) to prevent account reconnaissance.