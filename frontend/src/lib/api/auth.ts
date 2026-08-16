import { API_BASE_URL } from "@/lib/config";
import { apiClient } from "@/lib/api/client";
import type { AuthResponse, LoginRequest, RegisterRequest } from "@/lib/types/auth";

/**
 * Authentication API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoints:
 * - POST /api/v1/auth/login
 * - POST /api/v1/auth/register
 * - POST /api/v1/auth/logout
 */

export async function loginUser(
  credentials: LoginRequest,
  signal?: AbortSignal
): Promise<AuthResponse> {
  const result = await apiClient<{ token: string; user: any }>("/auth/login", {
    method: "POST",
    body: {
      email: credentials.email,
      password: credentials.password,
    },
    signal,
  });

  if (!result.success) {
    return {
      success: false,
      message:
        result.message ||
        `Authentication failed. Backend API at ${API_BASE_URL} is offline or credentials invalid.`,
    };
  }

  return {
    success: true,
    message: result.message || "Login successful",
    data: result.data as any,
  };
}

export async function registerUser(
  userData: RegisterRequest,
  signal?: AbortSignal
): Promise<AuthResponse> {
  const result = await apiClient<{ message?: string }>("/auth/register", {
    method: "POST",
    body: {
      fullName: userData.fullName,
      email: userData.email,
      password: userData.password,
      role: userData.role || "USER",
    },
    signal,
  });

  if (!result.success) {
    return {
      success: false,
      message:
        result.message ||
        `Registration failed. Backend API at ${API_BASE_URL} is offline or data invalid.`,
    };
  }

  return {
    success: true,
    message: result.message || "Registration successful",
    data: result.data as any,
  };
}

export async function logoutUser(token?: string, signal?: AbortSignal): Promise<{ success: boolean; message?: string }> {
  if (!token) return { success: true, message: "Logged out locally" };

  try {
    const result = await apiClient("/auth/logout", {
      method: "POST",
      token,
      signal,
    });
    return { success: result.success, message: result.message };
  } catch {
    return { success: true, message: "Logged out locally" };
  }
}
