import { AuthResponse, LoginRequest, RegisterRequest } from "@/lib/types/auth";
import { apiClient } from "./client";

/**
 * Authentication API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoints:
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 */
export async function loginUser(credentials: LoginRequest): Promise<AuthResponse> {
  const result = await apiClient<{ token: string; user: any }>("/auth/login", {
    method: "POST",
    body: {
      email: credentials.email,
      password: credentials.password,
    },
  });

  if (!result.success) {
    return {
      success: false,
      message: result.message || "Authentication failed. Backend API server is offline or credentials invalid.",
    };
  }

  return {
    success: true,
    message: result.message || "Login successful",
    data: result.data as any,
  };
}

export async function registerUser(userData: RegisterRequest): Promise<AuthResponse> {
  const result = await apiClient<{ message?: string }>("/auth/register", {
    method: "POST",
    body: {
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password,
      role: userData.role || "USER",
    },
  });

  if (!result.success) {
    return {
      success: false,
      message: result.message || "Registration failed. Backend API server is offline or data invalid.",
    };
  }

  return {
    success: true,
    message: result.message || "Registration successful",
    data: result.data as any,
  };
}
