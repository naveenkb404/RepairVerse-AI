import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import type { ApiResponse } from "@/lib/types/auth";

export type SystemHealthData = {
  status: "UP" | "DEGRADED" | "DOWN" | string;
  timestamp: string;
  system: string;
  version: string;
  services: Record<string, string>;
  activeProfiles: string;
};

export type SystemHealthResponse = ApiResponse<SystemHealthData>;

export const SAMPLE_HEALTH_DATA: SystemHealthData = {
  status: "UP",
  timestamp: new Date().toISOString(),
  system: "RepairVerse AI Platform Service (Demo Mode)",
  version: "1.0.0",
  services: {
    database: "DEMO_MODE",
    flyway: "DEMO_MODE",
    geminiAi: "CONFIGURED",
    cloudinary: "CONFIGURED",
  },
  activeProfiles: "demo",
};

/**
 * Fetch backend system health status.
 * GET /api/v1/health
 */
export async function fetchSystemHealth(
  signal?: AbortSignal
): Promise<SystemHealthResponse> {
  const result = await apiClient<SystemHealthData>("/health", {
    method: "GET",
    signal,
  });

  if (result.success && result.data) {
    return {
      success: true,
      message: result.message || "Live system health loaded",
      data: result.data,
    };
  }

  return {
    success: true,
    message: `Backend API at ${API_BASE_URL}/health is offline. System operating in Demo Mode.`,
    data: SAMPLE_HEALTH_DATA,
  };
}
