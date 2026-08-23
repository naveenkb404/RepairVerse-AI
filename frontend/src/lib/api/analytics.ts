import { apiClient } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  AdminIntelligenceSummaryData,
  RepairCostAnalyticsData,
  SustainabilityAnalyticsData,
} from "@/lib/types/analytics";
import type { PredictiveFleetOverviewData } from "@/lib/types/prediction";
import { DEMO_FLEET_OVERVIEW } from "@/lib/api/prediction";

// ─── Demo reference datasets ──────────────────────────────────────────────────

export const DEMO_REPAIR_COST_ANALYTICS: RepairCostAnalyticsData = {
  totalSpent: 1240.5,
  averageCostPerRepair: 155.06,
  totalPartsCost: 620.25,
  totalLaborCost: 620.25,
  projectedNextRepairCost: 167.46,
  potentialSavingsIfPreventive: 58.61,
  monthlyCostTrend: [
    { month: "Feb 2026", repairCost: 180.0, partsCost: 90.0, laborCost: 90.0 },
    { month: "Mar 2026", repairCost: 220.0, partsCost: 110.0, laborCost: 110.0 },
    { month: "Apr 2026", repairCost: 95.0, partsCost: 47.5, laborCost: 47.5 },
    { month: "May 2026", repairCost: 310.0, partsCost: 155.0, laborCost: 155.0 },
    { month: "Jun 2026", repairCost: 140.0, partsCost: 70.0, laborCost: 70.0 },
    { month: "Jul 2026", repairCost: 295.5, partsCost: 147.75, laborCost: 147.75 },
  ],
  costByCategory: [
    { category: "Smartphone", totalCost: 520.0, repairCount: 3, averageCost: 173.33 },
    { category: "Laptop", totalCost: 420.0, repairCount: 2, averageCost: 210.0 },
    { category: "Tablet", totalCost: 180.5, repairCount: 1, averageCost: 180.5 },
    { category: "Gaming Console", totalCost: 120.0, repairCount: 2, averageCost: 60.0 },
  ],
  isDemo: true,
};

export const DEMO_SUSTAINABILITY_ANALYTICS: SustainabilityAnalyticsData = {
  totalCo2SavedKg: 127.4,
  totalEwasteReducedKg: 8.4,
  totalMoneySaved: 2340.0,
  devicesExtendedLifespan: 4,
  co2EquivalentTrees: 5.79,
  co2EquivalentCarKm: 1061.67,
  monthlyImpact: [
    { month: "Feb 2026", co2SavedKg: 18.2, ewasteReducedKg: 1.2, moneySaved: 310.0 },
    { month: "Mar 2026", co2SavedKg: 22.5, ewasteReducedKg: 1.4, moneySaved: 420.0 },
    { month: "Apr 2026", co2SavedKg: 14.1, ewasteReducedKg: 0.8, moneySaved: 185.0 },
    { month: "May 2026", co2SavedKg: 31.0, ewasteReducedKg: 1.8, moneySaved: 620.0 },
    { month: "Jun 2026", co2SavedKg: 19.6, ewasteReducedKg: 1.2, moneySaved: 350.0 },
    { month: "Jul 2026", co2SavedKg: 22.0, ewasteReducedKg: 2.0, moneySaved: 455.0 },
  ],
  topDevicesByImpact: [
    { deviceId: "dev_sample_2", deviceName: "Work MacBook Pro 16", co2SavedKg: 52.1, ewasteReducedKg: 3.5, moneySaved: 1100.0, repairCount: 3 },
    { deviceId: "dev_sample_1", deviceName: "Personal iPhone 14 Pro", co2SavedKg: 38.8, ewasteReducedKg: 2.4, moneySaved: 720.0, repairCount: 4 },
    { deviceId: "dev_sample_3", deviceName: "Living Room Gaming Console", co2SavedKg: 21.0, ewasteReducedKg: 1.6, moneySaved: 350.0, repairCount: 2 },
    { deviceId: "dev_sample_4", deviceName: "Study iPad Air", co2SavedKg: 15.5, ewasteReducedKg: 0.9, moneySaved: 170.0, repairCount: 1 },
  ],
  isDemo: true,
};

export const DEMO_ADMIN_INTELLIGENCE: AdminIntelligenceSummaryData = {
  totalPredictionsGenerated: 1450,
  devicesAtCriticalRisk: 23,
  devicesAtHighRisk: 87,
  platformAverageHealthScore: 76.4,
  totalProjectedFailureCost: 48500.0,
  totalPreventableSavings: 19400.0,
  platformCo2ImpactKg: 18420.0,
  topFailingCategories: [
    { category: "Smartphone", deviceCount: 480, atRiskCount: 87, riskPercentage: 18.1, primaryFaultType: "Battery Degradation" },
    { category: "Laptop", deviceCount: 320, atRiskCount: 52, riskPercentage: 16.3, primaryFaultType: "Thermal Paste Degradation" },
    { category: "Gaming Console", deviceCount: 210, atRiskCount: 29, riskPercentage: 13.8, primaryFaultType: "APU Overheating" },
    { category: "Tablet", deviceCount: 180, atRiskCount: 18, riskPercentage: 10.0, primaryFaultType: "Charging Port Failure" },
    { category: "Smartwatch", deviceCount: 90, atRiskCount: 7, riskPercentage: 7.8, primaryFaultType: "Water Seal Degradation" },
  ],
  recentHighRiskDevices: [
    { deviceId: "dev-001", deviceName: "Samsung Galaxy S21", userId: "usr-001", userEmail: "david.k@example.com", riskLevel: "CRITICAL", predictionScore: 28, primaryFaultType: "Battery Degradation", evaluatedAt: "2026-08-23T14:30:00Z" },
    { deviceId: "dev-002", deviceName: "Dell XPS 15 (2021)", userId: "usr-002", userEmail: "elena.m@example.com", riskLevel: "CRITICAL", predictionScore: 31, primaryFaultType: "Thermal Paste Degradation", evaluatedAt: "2026-08-23T13:15:00Z" },
    { deviceId: "dev-003", deviceName: "iPhone 12 Pro", userId: "usr-003", userEmail: "marcus.v@example.com", riskLevel: "HIGH", predictionScore: 42, primaryFaultType: "Lightning Port Wear", evaluatedAt: "2026-08-23T11:45:00Z" },
    { deviceId: "dev-004", deviceName: "PlayStation 5", userId: "usr-004", userEmail: "sarah.t@example.com", riskLevel: "HIGH", predictionScore: 48, primaryFaultType: "APU Overheating", evaluatedAt: "2026-08-23T09:20:00Z" },
  ],
  isDemo: true,
};

// ─── API Client Functions ─────────────────────────────────────────────────────

/**
 * Fetch repair cost analytics.
 */
export async function fetchRepairCostAnalytics(
  token?: string | null
): Promise<ApiResponse<RepairCostAnalyticsData>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_REPAIR_COST_ANALYTICS, "Repair cost analytics (Demo Mode)");
  }

  const res = await apiClient<RepairCostAnalyticsData>("/analytics/repair-costs", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_REPAIR_COST_ANALYTICS, "Repair cost analytics (Demo Mode)");
}

/**
 * Fetch sustainability & environmental impact analytics.
 */
export async function fetchSustainabilityAnalytics(
  token?: string | null
): Promise<ApiResponse<SustainabilityAnalyticsData>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_SUSTAINABILITY_ANALYTICS, "Sustainability analytics (Demo Mode)");
  }

  const res = await apiClient<SustainabilityAnalyticsData>("/analytics/sustainability", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_SUSTAINABILITY_ANALYTICS, "Sustainability analytics (Demo Mode)");
}

/**
 * Fetch admin platform intelligence summary.
 */
export async function fetchAdminIntelligenceSummary(
  token?: string | null
): Promise<ApiResponse<AdminIntelligenceSummaryData>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_ADMIN_INTELLIGENCE, "Platform intelligence summary (Demo Mode)");
  }

  const res = await apiClient<AdminIntelligenceSummaryData>("/admin/intelligence/summary", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_ADMIN_INTELLIGENCE, "Platform intelligence summary (Demo Mode)");
}

/**
 * Fetch admin platform fleet overview.
 */
export async function fetchAdminPlatformFleet(
  token?: string | null
): Promise<ApiResponse<PredictiveFleetOverviewData>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_FLEET_OVERVIEW, "Platform fleet overview (Demo Mode)");
  }

  const res = await apiClient<PredictiveFleetOverviewData>("/admin/intelligence/fleet", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_FLEET_OVERVIEW, "Platform fleet overview (Demo Mode)");
}
