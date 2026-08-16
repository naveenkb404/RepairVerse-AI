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
    │   │   ├── controller/      # AuthController, DiagnosisController, RepairAnalysisController
    │   │   ├── dto/             # AuthRequest, AuthResponse, DiagnosisResponseDto, GeminiVisionResponse, RecommendationRequest, RecommendationResponseDto, ErrorResponse
    │   │   ├── entity/          # User, Role, Device, DiagnosisReport, AIRecommendation, RepairGuide
    │   │   ├── exception/       # GlobalExceptionHandler, Custom Exceptions
    │   │   ├── repository/      # UserRepository, DiagnosisReportRepository, AIRecommendationRepository, RepairGuideRepository
    │   │   ├── security/        # JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService, UserPrincipal
    │   │   └── service/         # AuthService, DiagnosisService, AiVisionService, CloudinaryService, RepairAnalysisService
    │   └── resources/
    │       ├── application.yml
    │       ├── application-test.yml
    │       └── db/migration/    # Flyway schema scripts (V1__init_schema.sql, V2__diagnosis_reports_update.sql, V3__ai_recommendations_and_guides_update.sql)
    └── test/                    # AuthServiceTest, AuthControllerTest, DiagnosisServiceTest, DiagnosisControllerTest, AiVisionServiceTest, CloudinaryServiceTest, RepairAnalysisServiceTest, RepairAnalysisControllerTest
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

## 🛡️ Security Blueprint

1. **Zero Secret Hardcoding**: Secrets (`JWT_SECRET`, Cloudinary secrets, Gemini API keys, DB passwords) are managed exclusively in backend environment variables.
2. **Stateless JWT Authorization**: All authenticated requests require `Authorization: Bearer <token>`.
3. **Safe Error Handling**: `GlobalExceptionHandler` ensures stack traces, SQL, and database credentials are never leaked.
4. **CORS Isolation**: Configured strictly for authorized origins (e.g. `http://localhost:3000`) with credentials support.
5. **Deterministic Recommendation Rules**: Recommendation scores (0–100) are computed mathematically from diagnostic data without arbitrary randomness.
