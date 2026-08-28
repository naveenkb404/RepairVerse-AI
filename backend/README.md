# RepairVerse AI — Spring Boot Backend API

Production-grade Spring Boot 3 REST API foundation for the RepairVerse AI circular economy & hardware intelligence platform.

---

## 🛠️ Tech Stack

- **Java**: 17+ (LTS)
- **Framework**: Spring Boot 3.2.3
- **Security**: Spring Security 6, JWT (`jjwt` 0.12.5), BCrypt
- **Persistence**: Spring Data JPA, Hibernate, PostgreSQL 15+
- **Migrations**: Flyway DB
- **AI Vision Pipeline**: Google Gemini 1.5 Flash / Pro AI Vision
- **Media Storage**: Cloudinary Java SDK (`cloudinary-http44` 1.38.0)
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)
- **Build Tool**: Apache Maven

---

## 📁 Architecture Overview

```
backend/
├── pom.xml
├── .env.example
├── README.md
└── src/
    ├── main/
    │   ├── java/com/repairverse/ai/
    │   │   ├── config/          # SecurityConfig, CorsConfig, CloudinaryConfig, AppProperties
    │   │   ├── controller/      # AuthController, DiagnosisController, RepairAnalysisController, DeviceController
    │   │   ├── dto/             # AuthRequest, AuthResponse, DiagnosisResponseDto, GeminiVisionResponse, RecommendationRequest, RecommendationResponseDto, DeviceDto, DevicePassportDto, ErrorResponse
    │   │   ├── entity/          # User, Role, Device, DeviceHealth, DiagnosisReport, AIRecommendation, RepairGuide
    │   │   ├── exception/       # GlobalExceptionHandler, Custom Exceptions
    │   │   ├── repository/      # UserRepository, DeviceRepository, DeviceHealthRepository, DiagnosisReportRepository, AIRecommendationRepository, RepairGuideRepository
    │   │   ├── security/        # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService, UserPrincipal
    │   │   └── service/         # AuthService, DiagnosisService, AiVisionService, CloudinaryService, RepairAnalysisService, DeviceService, DevicePassportService
    │   └── resources/
    │       ├── application.yml
    │       ├── application-test.yml
    │       └── db/migration/    # Flyway schema scripts (V1__init_schema.sql, V2__diagnosis_reports_update.sql, V3__ai_recommendations_and_guides_update.sql, V4__devices_update.sql)
    └── test/                    # AuthServiceTest, AuthControllerTest, DiagnosisServiceTest, DiagnosisControllerTest, AiVisionServiceTest, CloudinaryServiceTest, RepairAnalysisServiceTest, RepairAnalysisControllerTest, DeviceServiceTest, DevicePassportServiceTest, DeviceControllerTest
```

---

## 🚀 Getting Started

### 1. Prerequisites
- **JDK**: Java 17, 21, or 25
- **Maven**: 3.8+ (or Maven Wrapper `mvnw`)
- **Database**: PostgreSQL 15+ (or in-memory H2 fallback for testing)

### 2. Environment Configuration
Copy `.env.example` to your environment or export the variables:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/repairversedb"
export DB_USERNAME="postgres"
export DB_PASSWORD="your_db_password"
export JWT_SECRET="your_secure_256_bit_random_jwt_secret_key"
export ALLOWED_ORIGINS="http://localhost:3000"
export CLOUDINARY_CLOUD_NAME="your_cloudinary_cloud_name"
export CLOUDINARY_API_KEY="your_cloudinary_api_key"
export CLOUDINARY_API_SECRET="your_cloudinary_api_secret"
export GEMINI_API_KEY="your_gemini_api_key"
export GEMINI_MODEL="gemini-1.5-flash"
```

### 3. Running Locally

```bash
# Run tests
mvn clean test

# Package JAR
mvn package

# Start application
mvn spring-boot:run
```

The server starts by default at `http://localhost:8080/api/v1`.

---

## 🔐 Authentication Endpoints

Base URL: `http://localhost:8080/api/v1`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register new account (`USER` or `TECHNICIAN`). Public `ADMIN` registration is blocked. |
| `POST` | `/auth/login` | Public | Authenticate credentials and receive signed Bearer JWT. |
| `GET` | `/auth/me` | Authenticated | Retrieve current user profile (requires `Authorization: Bearer <token>`). |
| `POST` | `/auth/logout` | Authenticated | Acknowledge logout (client removes token from local storage). |

---

## 🔍 AI Visual Diagnosis Endpoints (Phase 15)

| Method | Endpoint | Content-Type | Access | Description |
|---|---|---|---|---|
| `POST` | `/diagnosis` | `multipart/form-data` | Public / Auth | Uploads device photo to Cloudinary, executes Gemini AI Vision diagnosis, persists report. |
| `GET` | `/diagnosis/{id}` | `application/json` | Public / Auth | Retrieves previously generated diagnosis report by ID. |

---

## ⚖️ Repair vs. Replace Recommendation Engine (Phase 16)

| Method | Endpoint | Content-Type | Access | Description |
|---|---|---|---|---|
| `POST` | `/repair-analysis` | `application/json` | Public / Auth | Evaluates diagnosis report and computes deterministic Repair vs Replace score, savings, and repair plan. |
| `GET` | `/repair-analysis/{diagnosisId}` | `application/json` | Public / Auth | Retrieves existing recommendation for a diagnosis report. |

### Sample Recommendation Request
```json
POST /api/v1/repair-analysis
Content-Type: application/json

{
  "diagnosisId": "diag_01HXYZ..."
}
```

### Sample Recommendation Response
```json
{
  "success": true,
  "message": "Repair recommendation generated successfully",
  "data": {
    "id": "rec_01HXYZ...",
    "diagnosisId": "diag_01HXYZ...",
    "action": "REPAIR",
    "repairScore": 92,
    "replaceScore": 8,
    "decision": {
      "repairScore": 92,
      "replaceScore": 8,
      "recommendation": "REPAIR",
      "moneySaved": 615.0,
      "carbonSaved": 6.5,
      "rationale": "Self-repair is strongly recommended. Resolving the Digitizer & Display Panel Fracture costs approximately $85, saving $615 compared to a new purchase while preventing 6.5 kg of CO₂ emissions."
    },
    "plan": {
      "summary": "Standard Digitizer & Display Panel Fracture repair procedure.",
      "steps": [
        {
          "stepNumber": 1,
          "title": "Power Off & Apply Perimeter Heat",
          "description": "Completely shut down device. Use heat gun around perimeter for 2 minutes to soften screen adhesive.",
          "safetyNote": "Do not exceed 80°C to prevent thermal stress on battery.",
          "estimatedMinutes": 10
        }
      ],
      "parts": [
        {
          "name": "OEM Replacement Display Assembly",
          "quantity": 1,
          "estimatedCost": 65.0,
          "partNumber": "DISP-OEM-SMARTPHONE"
        }
      ],
      "tools": [
        {
          "name": "Precision Screwdriver Set (P2/Y000/PH000)",
          "category": "Precision Drivers",
          "essential": true
        }
      ]
    },
    "createdAt": "2026-08-16T12:00:00"
  }
}
```

---

## 📱 Device Registry & Digital Health Passport Engine (Phase 17)

Base URL: `http://localhost:8080/api/v1`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/devices` | Authenticated | List all devices registered to the authenticated user. |
| `GET` | `/devices/{id}` | Authenticated | Retrieve device details by ID with ownership validation. |
| `POST` | `/devices` | Authenticated | Register new device with automatic Health Passport initialization. |
| `PUT` | `/devices/{id}` | Authenticated | Update device information (condition, warranty, price). |
| `DELETE` | `/devices/{id}` | Authenticated | Remove device and cascade associated health passport data. |
| `GET` | `/devices/{id}/passport` | Authenticated | Retrieve aggregated Digital Health Passport (telemetry, AI diagnoses, lifecycle). |

### Sample Device Registration
```json
POST /api/v1/devices
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "deviceName": "Personal iPhone 14 Pro",
  "category": "Smartphone",
  "brand": "Apple",
  "model": "iPhone 14 Pro (128GB)",
  "serialNumber": "F2LX9001K992",
  "purchaseDate": "2023-01-15",
  "warrantyExpiry": "2024-01-15",
  "purchasePrice": 999.0,
  "currentCondition": "Good"
}
```

---

## 🍃 Carbon Impact, Repair Shops & Notifications Endpoints (Phase 18)

### Carbon Impact Engine (`/carbon`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/carbon` | Authenticated | Retrieves user's carbon dashboard (CO₂ saved, e-waste avoided, money saved, sustainability score 0-100, 6-month trend). |

### Certified Repair Shops (`/shops`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/shops` | Public | Search/list repair shops with server-side Haversine distance calculations (km), category/rating filtering, and eco-certification. |
| `GET` | `/shops/{id}` | Public | Retrieve detailed shop profile by ID. |

### Booking Pipeline (`/bookings`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/bookings` | Authenticated | Create a new repair appointment (triggers automatic notification event hook). |
| `GET` | `/bookings` | Authenticated | Retrieve authenticated user's repair appointments. |
| `DELETE` | `/bookings/{id}` | Authenticated | Cancel an appointment owned by the user (403 Forbidden if not owner). |

### Notification Hub (`/notifications`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/notifications` | Authenticated | List all notifications for authenticated user. |
| `GET` | `/notifications/unread-count` | Authenticated | Retrieve unread notification count. |
| `PUT` | `/notifications/{id}/read` | Authenticated | Mark a single notification as read. |
| `PUT` | `/notifications/read-all` | Authenticated | Mark all notifications for authenticated user as read. |

---

## 🔮 Predictive Maintenance & Platform Analytics (Phase 22)

### Predictive Maintenance (`/predictions`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/predictions/device/{deviceId}` | Authenticated | Retrieve predictive health score and degradation metrics for a device. |
| `POST` | `/predictions/device/{deviceId}/evaluate` | Authenticated | Trigger on-demand deterministic predictive evaluation for a device. |
| `GET` | `/predictions/patterns` | Public/Auth | List common fault patterns and failure mode distributions. |

### Platform Analytics (`/analytics`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/analytics/repair-costs` | Authenticated | Retrieve repair vs replace cost comparison analytics and ROI. |
| `GET` | `/analytics/sustainability` | Authenticated | Retrieve advanced circular sustainability and e-waste metrics. |
| `GET` | `/analytics/fleet-overview` | Authenticated | Retrieve user fleet degradation status summary. |

### Admin Platform Intelligence (`/admin/intelligence`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/admin/intelligence/summary` | Admin | Global fleet health KPIs, critical risk counts, and preventable savings. |
| `GET` | `/admin/intelligence/fleet` | Admin | Platform-wide fleet risk distributions and failure mode clusters. |

---

## 🧠 Explainable AI & Generative Repair Intelligence (Phase 23)

### AI Intelligence Endpoints (`/ai-intelligence`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/ai-intelligence/device-prediction/{deviceId}` | Authenticated | Returns structured Gemini explainability for device failure probability, root cause, and component degradation. |
| `GET` | `/ai-intelligence/diagnosis/{diagnosisId}` | Authenticated | Returns plain-English generative explanation of visual diagnosis findings. |
| `GET` | `/ai-intelligence/recommendation/{recommendationId}` | Authenticated | Returns generative economic and environmental justification for repair recommendations. |
| `GET` | `/ai-intelligence/sustainability` | Authenticated | Returns circular economy storytelling and real-world equivalence narrative. |

---

## 🛡️ Security Blueprint

1. **Zero Secret Hardcoding**: Secrets (`JWT_SECRET`, Cloudinary secrets, Gemini API keys, DB passwords) are managed exclusively in backend environment variables.
2. **Stateless JWT Authorization**: All authenticated requests require `Authorization: Bearer <token>`.
3. **Safe Error Handling**: `GlobalExceptionHandler` ensures stack traces, SQL, and database credentials are never leaked.
4. **CORS Isolation**: Configured strictly for authorized origins (e.g. `http://localhost:3000`) with credentials support.
5. **Deterministic Recommendation & Carbon Metrics**: Scores and trends are computed mathematically from empirical data without arbitrary randomness.
6. **Cross-User Ownership Guard**: Strict identity context enforcement guarantees users cannot view, modify, or delete another user's devices, bookings, or notifications.
7. **Explainable AI Invariance**: Gemini models provide narrative explainability only and can never alter risk scores, override authorization, or modify database state.

