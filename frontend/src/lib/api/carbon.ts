import { ApiResponse } from "@/lib/types/auth";

// ─── Carbon Types ─────────────────────────────────────────────────────────────

/** Mirrors the CarbonImpact database table defined in DATABASE_SCHEMA.md Module 5 */
export type CarbonImpact = {
  /** Cumulative kg of CO₂ emissions avoided by choosing repair over replacement */
  co2Saved: number;
  /** Cumulative kg of e-waste diverted from landfills */
  ewasteReduced: number;
  /** Total money saved (USD) by repairing instead of replacing */
  moneySaved: number;
  /** Total number of repair actions recorded */
  repairCount: number;
};

/** A single time-series data point for the trend chart */
export type CarbonTrendPoint = {
  period: string; // e.g. "Jan", "Feb 2026", "Week 1"
  co2Saved: number;
  moneySaved: number;
};

/** A recent repair activity record contributing to carbon impact */
export type CarbonRepairActivity = {
  id: string;
  deviceName: string;
  repairType: string;
  repairDate: string;
  co2Avoided: number;
  ewasteAvoided: number;
  moneySaved: number;
};

/** Full carbon dashboard payload from GET /api/v1/carbon */
export type CarbonDashboardData = {
  impact: CarbonImpact;
  trend: CarbonTrendPoint[];
  recentActivity: CarbonRepairActivity[];
  sustainabilityScore: number; // 0–100
};

export type CarbonDashboardResponse = ApiResponse<CarbonDashboardData>;

// ─── Carbon API Service ───────────────────────────────────────────────────────

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * Fetch the authenticated user's carbon impact dashboard data.
 * Corresponds to: GET /api/v1/carbon  (documented in docs/API_SPEC.md)
 *
 * NOTE: The Spring Boot backend is not yet implemented.
 * This service layer is ready for integration once the backend
 * CarbonController / CarbonService is deployed.
 */
export async function fetchCarbonDashboard(
  token?: string
): Promise<CarbonDashboardResponse> {
  try {
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    };
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}/carbon`, {
      method: "GET",
      headers,
      cache: "no-store",
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      return {
        success: false,
        message:
          errorData?.message ||
          `Carbon dashboard request failed with status ${response.status}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      data: data.data || data,
    };
  } catch {
    return {
      success: false,
      message:
        "Carbon Impact backend service is currently offline. " +
        "Start the Spring Boot API at " +
        API_BASE_URL +
        " to load live data.",
    };
  }
}
