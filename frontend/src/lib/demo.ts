import { API_BASE_URL } from "@/lib/config";
import type { ApiResponse } from "@/lib/types/auth";
import type { UserProfile } from "@/lib/types/user";

/**
 * Standard identifier for client-side demo/offline mode.
 * Never use a fake JWT string.
 */
export const DEMO_TOKEN = "demo-offline-token";

/** Reference demo user profile */
export const DEMO_USER_PROFILE: UserProfile = {
  id: "demo-user-001",
  fullName: "Alex Johnson",
  email: "demo@repairverse.ai",
  role: "USER",
  phone: "+1 (555) 012-3456",
  location: "San Francisco, CA",
  bio: "Electronics enthusiast & circular economy advocate. Repairing over replacing.",
  joinedAt: "2024-06-15T10:00:00Z",
  lastLogin: new Date().toISOString(),
  verified: true,
  preferences: {
    notifications: true,
    newsletter: true,
    theme: "dark",
    language: "en",
  },
};

/**
 * Check if a token belongs to an offline demo session.
 */
export function isDemoSession(token?: string | null): boolean {
  if (!token) return false;
  return token === DEMO_TOKEN || token.startsWith("demo-");
}

/**
 * Standard message when a backend service is unavailable.
 */
export function getBackendOfflineMessage(featureName?: string): string {
  const prefix = featureName ? `${featureName} backend service` : "Backend API server";
  return `${prefix} is currently offline. Connect the Spring Boot API at ${API_BASE_URL} to synchronize live data.`;
}

/**
 * Wrap reference data in a standardized ApiResponse object explicitly tagged with demo notice.
 */
export function createDemoResponse<T>(data: T, customMessage?: string): ApiResponse<T> {
  return {
    success: true,
    message: customMessage || "Showing sample reference data (Demo Mode)",
    data,
  };
}
