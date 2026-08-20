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

# User

## Get Profile

GET /users/profile

---

## Update Profile

PUT /users/profile

---

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