import { AuthResponse, LoginRequest, RegisterRequest } from "@/lib/types/auth";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * Authentication API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoints:
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 */
export async function loginUser(credentials: LoginRequest): Promise<AuthResponse> {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: credentials.email,
        password: credentials.password,
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      return {
        success: false,
        message: errorData?.message || `Authentication failed with status ${response.status}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      message: data.message || "Login successful",
      data: data.data || data,
    };
  } catch (error) {
    // Graceful error reporting when Spring Boot backend service is not running
    return {
      success: false,
      message:
        "Backend authentication server is currently offline or unreachable. Please verify Spring Boot API service at " +
        API_BASE_URL,
    };
  }
}

export async function registerUser(userData: RegisterRequest): Promise<AuthResponse> {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        fullName: userData.fullName,
        email: userData.email,
        password: userData.password,
        role: userData.role || "USER",
      }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      return {
        success: false,
        message: errorData?.message || `Registration failed with status ${response.status}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      message: data.message || "Registration successful",
      data: data.data,
    };
  } catch (error) {
    return {
      success: false,
      message:
        "Backend authentication server is currently offline or unreachable. Please verify Spring Boot API service at " +
        API_BASE_URL,
    };
  }
}
