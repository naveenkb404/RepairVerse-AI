import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";

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
  isDemoData?: boolean;
};

export type CarbonDashboardResponse = ApiResponse<CarbonDashboardData>;

// ─── Reference Sample Data for Demo/Offline Mode ─────────────────────────────

export const SAMPLE_CARBON_DATA: CarbonDashboardData = {
  impact: {
    co2Saved: 142.8,
    ewasteReduced: 4.85,
    moneySaved: 1250,
    repairCount: 8,
  },
  sustainabilityScore: 88,
  trend: [
    { period: "Sep", co2Saved: 12.4, moneySaved: 120 },
    { period: "Oct", co2Saved: 28.1, moneySaved: 250 },
    { period: "Nov", co2Saved: 49.3, moneySaved: 480 },
    { period: "Dec", co2Saved: 78.6, moneySaved: 710 },
    { period: "Jan", co2Saved: 110.2, moneySaved: 990 },
    { period: "Feb", co2Saved: 142.8, moneySaved: 1250 },
  ],
  recentActivity: [
    {
      id: "act-1",
      deviceName: "iPhone 13 Pro",
      repairType: "OLED Screen & Battery Replacement",
      repairDate: "2026-02-10",
      co2Avoided: 58.2,
      ewasteAvoided: 0.24,
      moneySaved: 680,
    },
    {
      id: "act-2",
      deviceName: "MacBook Pro 16\" (M1)",
      repairType: "Logic Board Capacitor Micro-soldering",
      repairDate: "2026-01-18",
      co2Avoided: 64.5,
      ewasteAvoided: 2.1,
      moneySaved: 450,
    },
    {
      id: "act-3",
      deviceName: "Sony WH-1000XM4",
      repairType: "ANC Hinge & Left Driver Repair",
      repairDate: "2025-12-04",
      co2Avoided: 20.1,
      ewasteAvoided: 0.25,
      moneySaved: 120,
    },
  ],
  isDemoData: true,
};

// ─── Carbon API Service ───────────────────────────────────────────────────────

/**
 * Fetch the authenticated user's carbon impact dashboard data.
 * Corresponds to: GET /api/v1/carbon (docs/API_SPEC.md)
 */
export async function fetchCarbonDashboard(
  token?: string,
  signal?: AbortSignal
): Promise<CarbonDashboardResponse> {
  // If demo session, return sample immediately
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Sample carbon data loaded (Demo Session)",
      data: SAMPLE_CARBON_DATA,
    };
  }

  const result = await apiClient<CarbonDashboardData>("/carbon", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return {
      success: true,
      data: { ...result.data, isDemoData: false },
      message: result.message || "Live carbon impact data loaded",
    };
  }

  // Fallback to sample data with explicit demo message
  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/carbon is offline. Displaying reference metrics.`,
    data: SAMPLE_CARBON_DATA,
  };
}
