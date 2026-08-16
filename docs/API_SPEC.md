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

---

## Get Device

GET /devices/{id}

---

## Add Device

POST /devices

---

## Update Device

PUT /devices/{id}

---

## Delete Device

DELETE /devices/{id}

---

# Device Passport

## Get Passport

GET /devices/{id}/passport

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

Response

- CO₂ Saved
- Money Saved
- Devices Repaired

---

# Repair Shops

GET /shops

GET /shops/{id}

POST /bookings

GET /bookings

DELETE /bookings/{id}

---

# Notifications

GET /notifications

PUT /notifications/{id}/read

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