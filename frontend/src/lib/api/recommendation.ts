import { RecommendationResponse } from "@/lib/types/recommendation";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * Repair Recommendation API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoints:
 * - POST /api/v1/repair-analysis
 * - GET /api/v1/repair-guide/{issue}
 */
export async function fetchRepairRecommendation(
  diagnosisId: string,
  token?: string
): Promise<RecommendationResponse> {
  try {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}/repair-analysis`, {
      method: "POST",
      headers,
      body: JSON.stringify({ diagnosisId }),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      return {
        success: false,
        message:
          errorData?.message ||
          `Recommendation request failed with status ${response.status}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      message: data.message || "Repair recommendation generated successfully",
      data: data.data || data,
    };
  } catch {
    return {
      success: false,
      message:
        "Backend Repair Recommendation engine is currently offline. " +
        "Please verify Spring Boot API server at " +
        API_BASE_URL,
    };
  }
}
