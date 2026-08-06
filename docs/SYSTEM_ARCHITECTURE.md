# RepairVerse AI - System Architecture

# Overview

RepairVerse AI follows a modern client-server architecture.

The frontend communicates with the backend using REST APIs.

The backend communicates with:

- PostgreSQL Database
- Gemini AI
- Cloudinary
- Google Maps

---

# High Level Architecture

User

↓

Frontend (Next.js)

↓

Spring Boot REST API

↓

Business Services

↓

Database (PostgreSQL)

↓

External Services

• Gemini AI
• Google Maps
• Cloudinary

---

# Frontend Responsibilities

The frontend handles:

- Authentication
- User Interface
- Dashboard
- Device Management
- AI Diagnosis Interface
- Image Upload
- Charts
- Theme Switching

---

# Backend Responsibilities

The backend handles:

- Authentication
- Authorization
- Database Operations
- AI Requests
- Business Logic
- Booking System
- Notifications

---

# AI Service

Gemini is responsible for:

- Device diagnosis
- Fault prediction
- Repair suggestions
- Repair vs Replace analysis
- Cost estimation
- Maintenance recommendations

---

# Image Upload Flow

User uploads image

↓

Frontend

↓

Spring Boot

↓

Cloudinary

↓

Image URL

↓

Gemini Analysis

↓

Diagnosis Report

↓

Database

↓

Frontend Result

---

# Authentication Flow

User Login

↓

JWT Generated

↓

JWT Stored

↓

Authenticated Requests

↓

Protected APIs

---

# Dashboard Flow

Login

↓

Load User

↓

Load Devices

↓

Load Dashboard Data

↓

Display Statistics

---

# External Integrations

Gemini API

Purpose:
AI Diagnosis

---

Cloudinary

Purpose:
Image Storage

---

Google Maps API

Purpose:
Nearby Repair Shops

---

# Security

JWT Authentication

HTTPS

Password Hashing

Input Validation

Role-Based Authorization

Rate Limiting

Secure File Upload

---

# Future Integrations

Stripe

Push Notifications

Email Service

AI Voice Assistant

IoT Devices