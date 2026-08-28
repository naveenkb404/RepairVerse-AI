# RepairVerse AI - API Specification

# API Base URL

/api/v1

---

# Authentication

## Register

POST /auth/register

Request

```json
{
  "fullName": "Jane Doe",
  "email": "jane@example.com",
  "password": "SecurePassword123!",
  "role": "USER"
}
```

Response (201 Created)

```json
{
  "success": true,
  "message": "Registration successful. You can now sign in."
}
```

---

## Login

POST /auth/login

Request

```json
{
  "email": "jane@example.com",
  "password": "SecurePassword123!"
}
```

Response (200 OK)

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": "c7a8b9e0-1234-5678-9abc-def012345678",
      "fullName": "Jane Doe",
      "email": "jane@example.com",
      "role": "USER"
    }
  }
}
```

---

## Get Current Authenticated User

GET /auth/me
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": "c7a8b9e0-1234-5678-9abc-def012345678",
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER"
  }
}
```

---

## Logout

POST /auth/logout
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "Logout acknowledged. Remove the token from your client to complete sign-out."
}
```


---

# User (Phase 20)

## Get Profile

GET /users/profile
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "id": "c7a8b9e0-1234-5678-9abc-def012345678",
    "fullName": "Jane Doe",
    "email": "jane@example.com",
    "role": "USER",
    "avatarUrl": "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
    "phone": "+1 (555) 234-5678",
    "location": "San Francisco, CA",
    "bio": "Hardware enthusiast, DIY repair advocate.",
    "joinedAt": "2024-01-15",
    "lastLogin": "Just now",
    "verified": true,
    "preferences": {
      "notifications": true,
      "newsletter": true,
      "theme": "dark",
      "language": "en"
    },
    "totalDevices": 4,
    "totalRepairs": 9,
    "totalCarbonSaved": 47.3,
    "totalMoneySaved": 1240.0
  }
}
```

---

## Update Profile

PUT /users/profile
Header: `Authorization: Bearer <JWT_TOKEN>`

Request:
```json
{
  "fullName": "Jane Doe",
  "phone": "+1 (555) 234-5678",
  "location": "San Francisco, CA",
  "bio": "Hardware enthusiast & DIY repair specialist.",
  "avatarUrl": "https://images.unsplash.com/photo-1534528741775-53994a69daeb"
}
```

---

# Smart Repair History (Phase 20)

## Get All Repairs

GET /repair-history
Header: `Authorization: Bearer <JWT_TOKEN>`

## Get Repair By ID

GET /repair-history/{id}
Header: `Authorization: Bearer <JWT_TOKEN>`

## Log Completed Repair

POST /repair-history
Header: `Authorization: Bearer <JWT_TOKEN>`


# Devices

## Get All Devices

GET /devices
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "data": [
    {
      "id": "dev_01HXYZ...",
      "userId": "c7a8b9e0-1234-5678-9abc-def012345678",
      "deviceName": "Personal iPhone 14 Pro",
      "category": "Smartphone",
      "brand": "Apple",
      "model": "iPhone 14 Pro (128GB)",
      "serialNumber": "F2LX9001K992",
      "purchaseDate": "2023-01-15",
      "warrantyExpiry": "2024-01-15",
      "purchasePrice": 999.0,
      "currentCondition": "Good",
      "createdAt": "2023-01-15T10:00:00"
    }
  ]
}
```

---

## Get Device

GET /devices/{id}
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "data": {
    "id": "dev_01HXYZ...",
    "userId": "c7a8b9e0-1234-5678-9abc-def012345678",
    "deviceName": "Personal iPhone 14 Pro",
    "category": "Smartphone",
    "brand": "Apple",
    "model": "iPhone 14 Pro (128GB)",
    "serialNumber": "F2LX9001K992",
    "purchaseDate": "2023-01-15",
    "warrantyExpiry": "2024-01-15",
    "purchasePrice": 999.0,
    "currentCondition": "Good",
    "createdAt": "2023-01-15T10:00:00"
  }
}
```

---

## Add Device

POST /devices
Header: `Authorization: Bearer <JWT_TOKEN>`
Content-Type: `application/json`

Request

```json
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

Response (201 Created)

```json
{
  "success": true,
  "message": "Device registered successfully",
  "data": {
    "id": "dev_01HXYZ...",
    "userId": "c7a8b9e0-1234-5678-9abc-def012345678",
    "deviceName": "Personal iPhone 14 Pro",
    "category": "Smartphone",
    "brand": "Apple",
    "model": "iPhone 14 Pro (128GB)",
    "serialNumber": "F2LX9001K992",
    "purchaseDate": "2023-01-15",
    "warrantyExpiry": "2024-01-15",
    "purchasePrice": 999.0,
    "currentCondition": "Good",
    "createdAt": "2026-08-16T12:00:00"
  }
}
```

---

## Update Device

PUT /devices/{id}
Header: `Authorization: Bearer <JWT_TOKEN>`
Content-Type: `application/json`

Request

```json
{
  "deviceName": "Work iPhone 14 Pro",
  "currentCondition": "Excellent"
}
```

Response (200 OK)

```json
{
  "success": true,
  "message": "Device updated successfully",
  "data": {
    "id": "dev_01HXYZ...",
    "deviceName": "Work iPhone 14 Pro",
    "currentCondition": "Excellent"
  }
}
```

---

## Delete Device

DELETE /devices/{id}
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "Device deleted successfully"
}
```

---

# Device Passport

## Get Passport

GET /devices/{id}/passport
Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "Device health passport retrieved successfully",
  "data": {
    "device": {
      "id": "dev_01HXYZ...",
      "deviceName": "Personal iPhone 14 Pro",
      "category": "Smartphone",
      "brand": "Apple",
      "model": "iPhone 14 Pro (128GB)",
      "currentCondition": "Good"
    },
    "health": {
      "deviceId": "dev_01HXYZ...",
      "batteryHealth": 88,
      "healthScore": 86,
      "lastService": "2024-02-10",
      "aiPrediction": "Battery capacity degradation detected (88%). Display operating at optimal parameters."
    },
    "diagnosisSummary": {
      "probableIssue": "Digitizer & Display Panel Fracture",
      "confidenceScore": 92,
      "repairDifficulty": "Moderate",
      "repairCost": 85.0,
      "lastDiagnosisDate": "2024-02-10"
    },
    "repairSummary": {
      "repairsCompleted": 1,
      "lastRepairDate": "2024-02-10",
      "lastRecommendedAction": "Component replacement recommended within 30 days"
    },
    "carbonSummary": {
      "co2SavedKg": 42.5,
      "ewasteReducedKg": 0.21,
      "moneySaved": 700.0
    },
    "lifecycleTimeline": [
      {
        "id": "evt_reg_dev_01HXYZ...",
        "date": "2023-01-15",
        "title": "Device Registered",
        "type": "purchase",
        "description": "Enrolled Apple iPhone 14 Pro (128GB) in RepairVerse Digital Health Passport."
      },
      {
        "id": "evt_diag_diag_01...",
        "date": "2024-02-10",
        "title": "AI Visual Diagnosis",
        "type": "diagnosis",
        "description": "Identified: Digitizer & Display Panel Fracture (92% confidence)."
      }
    ]
  }
}
```

---

# AI Diagnosis

## Analyze Device

POST /diagnosis
Content-Type: `multipart/form-data`

Form Fields:
- `image`: Binary file (JPEG, PNG, WEBP, max 10MB) [Required]
- `symptoms`: String [Required]
- `deviceId`: String (UUID) [Optional]
- `deviceCategory`: String (e.g. "Smartphone", "Laptop") [Optional]
- `brand`: String [Optional]
- `model`: String [Optional]

Response (201 Created)

```json
{
  "success": true,
  "message": "AI hardware diagnosis completed successfully",
  "data": {
    "id": "diag_01HXYZ...",
    "deviceId": "dev-1",
    "deviceCategory": "Smartphone",
    "brand": "Apple",
    "model": "iPhone 13",
    "imageUrl": "https://res.cloudinary.com/demo/image/upload/...",
    "symptoms": "Cracked screen and touch unresponsive on top left",
    "probableIssue": "Digitizer & Display Panel Fracture",
    "confidenceScore": 92,
    "repairDifficulty": "Moderate",
    "repairTime": "45-90 mins",
    "repairCost": 85.0,
    "safetyWarning": "Handle cracked glass with protective eye-wear. Disconnect battery connector first.",
    "observations": [
      "Visual fracture detected across display digitizer layer.",
      "Sub-pixel array integrity compromised near impact epicenter.",
      "Internal logic board appears intact based on visual housing inspection."
    ],
    "createdAt": "2026-08-16T12:00:00"
  }
}
```

---

## Get Diagnosis Report

GET /diagnosis/{id}

Response (200 OK)

```json
{
  "id": "diag_01HXYZ...",
  "deviceId": "dev-1",
  "deviceCategory": "Smartphone",
  "brand": "Apple",
  "model": "iPhone 13",
  "imageUrl": "https://res.cloudinary.com/demo/image/upload/...",
  "symptoms": "Cracked screen and touch unresponsive on top left",
  "probableIssue": "Digitizer & Display Panel Fracture",
  "confidenceScore": 92,
  "repairDifficulty": "Moderate",
  "repairTime": "45-90 mins",
  "repairCost": 85.0,
  "safetyWarning": "Handle cracked glass with protective eye-wear. Disconnect battery connector first.",
  "observations": [
    "Visual fracture detected across display digitizer layer.",
    "Sub-pixel array integrity compromised near impact epicenter."
  ],
  "createdAt": "2026-08-16T12:00:00"
}
```

---


# Repair vs Replace

## Analyze Repair vs Replace

POST /repair-analysis
Content-Type: `application/json`

Request

```json
{
  "diagnosisId": "diag_01HXYZ..."
}
```

Response (201 Created)

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
      "summary": "Standard Digitizer & Display Panel Fracture repair procedure. Requires standard precision electronic tools and basic ESD protection.",
      "steps": [
        {
          "stepNumber": 1,
          "title": "Power Off & Apply Perimeter Heat",
          "description": "Completely shut down device. Use heat gun/heating pad around perimeter for 2 minutes to soften screen adhesive.",
          "safetyNote": "Do not exceed 80°C to prevent thermal stress on internal battery.",
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

## Get Recommendation by Diagnosis ID

GET /repair-analysis/{diagnosisId}

Response (200 OK) — Same payload as POST /repair-analysis

---


# Repair Guide

GET /repair-guide/{issue}

---

# Carbon Dashboard

GET /carbon

Header: `Authorization: Bearer <JWT_TOKEN>`

Response (200 OK)

```json
{
  "success": true,
  "message": "Live carbon impact data loaded",
  "data": {
    "impact": {
      "co2Saved": 142.8,
      "ewasteReduced": 4.85,
      "moneySaved": 1250.0,
      "repairCount": 8
    },
    "trend": [
      { "period": "Sep", "co2Saved": 12.4, "moneySaved": 120.0 },
      { "period": "Oct", "co2Saved": 28.1, "moneySaved": 250.0 },
      { "period": "Nov", "co2Saved": 49.3, "moneySaved": 480.0 },
      { "period": "Dec", "co2Saved": 78.6, "moneySaved": 710.0 },
      { "period": "Jan", "co2Saved": 110.2, "moneySaved": 990.0 },
      { "period": "Feb", "co2Saved": 142.8, "moneySaved": 1250.0 }
    ],
    "recentActivity": [
      {
        "id": "act-1",
        "deviceName": "iPhone 13 Pro",
        "repairType": "OLED Screen & Battery Replacement",
        "repairDate": "2026-02-10",
        "co2Avoided": 58.2,
        "ewasteAvoided": 0.24,
        "moneySaved": 680.0
      }
    ],
    "sustainabilityScore": 88,
    "isDemoData": false
  }
}
```

# System Health & Diagnostics (Phase 19)

GET /health

Public Endpoint for infrastructure monitoring and container orchestration healthchecks.

Response (200 OK)

```json
{
  "success": true,
  "message": "System health report generated",
  "data": {
    "status": "UP",
    "timestamp": "2026-08-20T23:50:00",
    "system": "RepairVerse AI Platform Service",
    "version": "1.0.0",
    "services": {
      "database": "UP",
      "flyway": "UP",
      "geminiAi": "CONFIGURED",
      "cloudinary": "CONFIGURED"
    },
    "activeProfiles": "prod"
  }
}
```

---

# Repair Shops


GET /shops?latitude=37.7749&longitude=-122.4194&radiusKm=10&serviceCategory=Smartphone+Repair&sortBy=nearest

Public Endpoint. Supports server-side Haversine distance calculation in km and coordinate validation (-90 to 90 / -180 to 180).

GET /shops/{id}

Public Endpoint.

---

# Bookings Pipeline

POST /bookings

Header: `Authorization: Bearer <JWT_TOKEN>`

Request

```json
{
  "shopId": "shop-001",
  "bookingDate": "2026-09-01 10:00 AM",
  "notes": "Screen replacement"
}
```

Response (201 Created)

```json
{
  "success": true,
  "message": "Booking confirmed successfully",
  "data": {
    "id": "book-12345",
    "userId": "c7a8b9e0-1234-5678-9abc-def012345678",
    "shopId": "shop-001",
    "shopName": "TechCare Express Repair",
    "bookingDate": "2026-09-01 10:00 AM",
    "bookingStatus": "SCHEDULED",
    "status": "SCHEDULED",
    "notes": "Screen replacement",
    "createdAt": "2026-08-20T23:25:00"
  }
}
```

GET /bookings

Header: `Authorization: Bearer <JWT_TOKEN>`

DELETE /bookings/{id}

Header: `Authorization: Bearer <JWT_TOKEN>` (403 Forbidden if not booking owner)

---

# Notifications

GET /notifications

Header: `Authorization: Bearer <JWT_TOKEN>`

PUT /notifications/{id}/read

Header: `Authorization: Bearer <JWT_TOKEN>`

PUT /notifications/read-all

Header: `Authorization: Bearer <JWT_TOKEN>`


---

# Dashboard

GET /dashboard

Returns

- Devices
- Recent Repairs
- Carbon Statistics
- AI Insights

---

# Admin

GET /admin/users

GET /admin/analytics

GET /admin/reports

DELETE /admin/users/{id}

---

# Smart Repair Guides (Phase 21)

GET /repair-guide?category={category}&difficulty={difficulty}
Public Endpoint. Returns curated repair guides.

GET /repair-guide/{id}
Public Endpoint. Returns detailed step-by-step repair procedure, safety warnings, and tool list.

GET /repair-guide/category/{category}
Public Endpoint.

POST /repair-guide
Header: `Authorization: Bearer <JWT_TOKEN>`

---

# Repair Cost Estimator (Phase 21)

POST /repair-cost-estimate
Public Endpoint. Calculates multi-channel cost breakdown (DIY vs Local vs Authorized Service Center).

Request:
```json
{
  "category": "Smartphone",
  "deviceModel": "iPhone 13 Pro",
  "issueType": "OLED Screen Crack",
  "deviceAgeYears": "2"
}
```

GET /repair-cost-estimate/categories
Public Endpoint. Returns supported hardware categories and common failure mode baselines.

---

# Community Hub & Q&A (Phase 21)

GET /community/posts?category={category}
Public Endpoint.

GET /community/posts/{id}
Public Endpoint. Returns topic discussion and reply thread.

POST /community/posts
Header: `Authorization: Bearer <JWT_TOKEN>`

POST /community/posts/{id}/reply
Header: `Authorization: Bearer <JWT_TOKEN>`

POST /community/posts/{id}/like
Public/Authenticated Endpoint. Upvotes a community discussion.

---

# Predictive Intelligence & Platform Analytics (Phase 22)

## Device Predictive Assessment
`GET /predictions/device/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`

`POST /predictions/device/{deviceId}/evaluate`
Header: `Authorization: Bearer <JWT_TOKEN>`

`GET /predictions/patterns`
Public/Authenticated Endpoint. Returns historical failure distributions and hardware risk archetypes.

## Repair & Sustainability Analytics
`GET /analytics/repair-costs`
Header: `Authorization: Bearer <JWT_TOKEN>`

`GET /analytics/sustainability`
Header: `Authorization: Bearer <JWT_TOKEN>`

`GET /analytics/fleet-overview`
Header: `Authorization: Bearer <JWT_TOKEN>`

## Admin Fleet Intelligence
`GET /admin/intelligence/summary`
Header: `Authorization: Bearer <JWT_TOKEN>` (ROLE_ADMIN)

`GET /admin/intelligence/fleet`
Header: `Authorization: Bearer <JWT_TOKEN>` (ROLE_ADMIN)

---

# Explainable AI & Generative Repair Intelligence (Phase 23)

## Device Risk Explanation
`GET /ai-intelligence/device-prediction/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns structured Gemini generative narrative explaining the device's predictive score, degradation root-causes, component wear breakdown, and safety maintenance roadmap.

Response (200 OK):
```json
{
  "success": true,
  "message": "Device predictive maintenance explanation generated successfully",
  "data": {
    "deviceId": "dev-123",
    "deviceName": "MacBook Pro M1",
    "overallHealthScore": 72,
    "riskLevel": "MEDIUM",
    "executiveSummary": "Thermal cycling and battery cycles indicate accelerated gate-oxide wear.",
    "rootCauseAnalysis": "High charging cycles combined with thermal load over 45C have accelerated electrolyte degradation.",
    "riskFactors": [
      {
        "factor": "Battery Cycle Count",
        "severity": "HIGH",
        "impactDescription": "620 charge cycles have reduced nominal battery capacity to 78%."
      }
    ],
    "componentWear": [
      {
        "componentName": "Li-Ion Battery Cell",
        "wearPercentage": 78,
        "estimatedRemainingLifespanMonths": 8,
        "recommendedPreventativeAction": "Schedule replacement before cycle count exceeds 800."
      }
    ],
    "roadmap": [
      "1. Avoid fast charging above 80% state-of-charge.",
      "2. Perform fan cleaning and thermal paste repaste."
    ],
    "isAiGenerated": true,
    "modelProvider": "Google Gemini 1.5 Flash",
    "evaluatedAt": "2026-08-23T18:00:00"
  }
}
```

## Diagnosis Explanation
`GET /ai-intelligence/diagnosis/{diagnosisId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns plain-English visual diagnosis explanation, technical failure mechanisms, and confidence factor breakdown.

## Recommendation Rationale
`GET /ai-intelligence/recommendation/{recommendationId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns economic and environmental justification for the Repair vs Replace decision.

## Sustainability Narrative
`GET /ai-intelligence/sustainability`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns contextual circular economy storytelling, real-world carbon equivalencies, and cumulative milestones.

---

# Autonomous Repair Planning & Lifecycle Intelligence (Phase 24)

## Repair Planning Endpoints (`/repair-planning`)
`GET /repair-planning/device/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Retrieves or synthesizes the deterministic action plan for a device.

`POST /repair-planning/device/{deviceId}/refresh`
Header: `Authorization: Bearer <JWT_TOKEN>`
Recalculates and persists a refreshed action plan.

`GET /repair-planning`
Header: `Authorization: Bearer <JWT_TOKEN>`
Lists all action plans belonging to the authenticated user.

Response (200 OK):
```json
{
  "success": true,
  "data": {
    "id": "plan-123",
    "userId": "usr-1",
    "deviceId": "dev-1",
    "deviceName": "iPhone 14 Pro",
    "deviceCategory": "Smartphone",
    "overallStrategy": "PREVENTIVE_MAINTENANCE",
    "priorityLevel": "MEDIUM",
    "estimatedTotalCost": 45.0,
    "estimatedLifecycleExtensionMonths": 14,
    "estimatedCarbonSaved": 5.8,
    "estimatedEwastePrevented": 0.15,
    "status": "ACTIVE",
    "strategyRationale": "Moderate component wear detected. Proactive servicing recommended.",
    "steps": [
      {
        "id": "step-1",
        "actionPlanId": "plan-123",
        "stepOrder": 1,
        "title": "Critical Data Backup",
        "description": "Secure full backup before physical maintenance.",
        "actionType": "BACKUP_DATA",
        "priority": "HIGH",
        "estimatedCost": 0.0,
        "estimatedDuration": "20 mins",
        "carbonImpact": 0.0,
        "isRequired": true,
        "status": "PENDING"
      }
    ]
  }
}
```

## Lifecycle Intelligence & Delay Simulation (`/lifecycle`)
`GET /lifecycle/device/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns multi-scenario lifespan comparison matrix (DO_NOTHING, PREVENTIVE_MAINTENANCE, REPAIR_NOW, DELAY_REPAIR, REPLACE).

`GET /lifecycle/device/{deviceId}/delay-impact`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns 7-day, 30-day, and 90-day delay consequence projections.

## Repair Journey Tracking (`/repair-journey`)
`GET /repair-journey/device/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns 9-stage unified repair journey status and progress percentage.

---

# Proactive Device Care & Smart Maintenance Automation (Phase 25)

Base path: `/maintenance`

## 1. Get User Maintenance Schedules
`GET /maintenance`
Header: `Authorization: Bearer <JWT_TOKEN>`
Query Params: `deviceId` (optional), `status` (optional)
Returns all active/past maintenance schedules for the authenticated user.

## 2. Get Device Maintenance Schedules
`GET /maintenance/device/{deviceId}`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns maintenance schedules for a single device with strict ownership enforcement.

## 3. Generate Device Maintenance Schedule
`POST /maintenance/device/{deviceId}/generate`
Header: `Authorization: Bearer <JWT_TOKEN>`
Triggers deterministic rule synthesis to generate or refresh proactive care tasks. Deduplication applies across a ±7-day window.

## 4. Update Maintenance Status
`PUT /maintenance/{id}/status`
Header: `Authorization: Bearer <JWT_TOKEN>`
Body:
```json
{
  "status": "COMPLETED"
}
```
Valid targets: `COMPLETED`, `SKIPPED`, `CANCELLED`.

## 5. Maintenance Calendar
`GET /maintenance/calendar`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns unified chronological events across maintenance tasks, shop bookings, action plan deadlines, and lifecycle alerts.

## 6. Maintenance Summary
`GET /maintenance/summary`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns aggregated KPIs (upcoming, due, overdue, critical, completed count, estimated cost & carbon savings).

## 7. Device Maintenance Priority
`GET /maintenance/device/{deviceId}/priority`
Header: `Authorization: Bearer <JWT_TOKEN>`
Returns deterministic 0-100 priority score, priority level (CRITICAL, HIGH, MEDIUM, LOW), rationale, and recommended action.

---

# Response Format

Success

{
  "success": true,
  "data": {}
}

Failure

{
  "success": false,
  "message": ""
}