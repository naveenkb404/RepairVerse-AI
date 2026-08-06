# RepairVerse AI - API Specification

# API Base URL

/api/v1

---

# Authentication

## Register

POST /auth/register

Request

{
  "fullName": "",
  "email": "",
  "password": ""
}

Response

{
  "success": true,
  "message": "Registration successful"
}

---

## Login

POST /auth/login

Request

{
  "email": "",
  "password": ""
}

Response

{
  "token": "",
  "user": {}
}

---

## Logout

POST /auth/logout

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

Input

- Device ID
- Image
- Symptoms

Response

- Probable Issue
- Confidence Score
- Estimated Cost
- Repair Difficulty
- Repair Time

---

# Repair vs Replace

POST /repair-analysis

Response

- Repair Score
- Replace Score
- Recommendation
- Money Saved
- Carbon Saved

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