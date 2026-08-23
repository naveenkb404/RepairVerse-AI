import { apiClient } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  DevicePredictionData,
  FaultPatternItem,
  MaintenanceRecommendationItem,
  PredictiveFleetOverviewData,
} from "@/lib/types/prediction";

// ─── Demo reference datasets ──────────────────────────────────────────────────

export const DEMO_PREDICTIONS: Record<string, DevicePredictionData> = {
  dev_sample_1: {
    deviceId: "dev_sample_1",
    deviceName: "Personal iPhone 14 Pro",
    category: "Smartphone",
    brand: "Apple",
    predictionScore: 84,
    riskLevel: "LOW",
    daysToFailureEstimate: 610,
    primaryFaultType: "Battery Degradation",
    recommendedActions: [
      "Enable optimized battery charging to reduce cycle wear",
      "Avoid using phone during fast charge in hot environments",
      "Schedule routine battery health check in 6 months",
    ],
    scoringBreakdown: [
      { factor: "Device Age", score: 18, maxScore: 20, status: "HEALTHY", description: "1.5 years old — minimal age degradation." },
      { factor: "Health Score", score: 27, maxScore: 30, status: "HEALTHY", description: "Health score: 90/100." },
      { factor: "Battery Health", score: 13, maxScore: 15, status: "HEALTHY", description: "Battery capacity at 89%." },
      { factor: "Repair History", score: 12, maxScore: 15, status: "HEALTHY", description: "1 minor screen repair recorded." },
      { factor: "Warranty Status", score: 5, maxScore: 10, status: "MEDIUM", description: "Standard warranty expired." },
      { factor: "Last Service Recency", score: 9, maxScore: 10, status: "HEALTHY", description: "Serviced 4 months ago." },
    ],
    estimatedRepairCost: 65.0,
    preventiveSavings: 26.0,
    co2SavingsKg: 14.8,
    confidenceScore: 0.88,
    isDemo: true,
    evaluatedAt: new Date().toISOString(),
  },
  dev_sample_2: {
    deviceId: "dev_sample_2",
    deviceName: "Work MacBook Pro 16",
    category: "Laptop",
    brand: "Apple",
    predictionScore: 92,
    riskLevel: "HEALTHY",
    daysToFailureEstimate: 680,
    primaryFaultType: "Thermal Paste Degradation",
    recommendedActions: [
      "Keep fans and vents clear of dust accumulation",
      "Run quarterly Apple Diagnostics hardware scan",
      "Maintain active AppleCare+ coverage",
    ],
    scoringBreakdown: [
      { factor: "Device Age", score: 20, maxScore: 20, status: "HEALTHY", description: "Under 1 year old." },
      { factor: "Health Score", score: 29, maxScore: 30, status: "HEALTHY", description: "Health score: 96/100." },
      { factor: "Battery Health", score: 15, maxScore: 15, status: "HEALTHY", description: "Battery capacity at 98%." },
      { factor: "Repair History", score: 15, maxScore: 15, status: "HEALTHY", description: "No prior repairs recorded." },
      { factor: "Warranty Status", score: 10, maxScore: 10, status: "HEALTHY", description: "Active AppleCare+ through 2026." },
      { factor: "Last Service Recency", score: 3, maxScore: 10, status: "MEDIUM", description: "Never serviced (new unit)." },
    ],
    estimatedRepairCost: 45.0,
    preventiveSavings: 18.0,
    co2SavingsKg: 28.5,
    confidenceScore: 0.94,
    isDemo: true,
    evaluatedAt: new Date().toISOString(),
  },
  dev_sample_3: {
    deviceId: "dev_sample_3",
    deviceName: "Living Room Gaming Console",
    category: "Gaming Console",
    brand: "Sony",
    predictionScore: 48,
    riskLevel: "HIGH",
    daysToFailureEstimate: 45,
    primaryFaultType: "Liquid Metal Thermal Runoff & APU Overheating",
    recommendedActions: [
      "Schedule cooling heatsink repasting and fan cleaning immediately",
      "Ensure vertical orientation has at least 15cm rear clearance",
      "Back up all game saves to cloud storage",
    ],
    scoringBreakdown: [
      { factor: "Device Age", score: 14, maxScore: 20, status: "MEDIUM", description: "2.5 years old — thermal paste aging." },
      { factor: "Health Score", score: 15, maxScore: 30, status: "HIGH", description: "Health score: 50/100 (Thermal alert)." },
      { factor: "Battery Health", score: 10, maxScore: 15, status: "MEDIUM", description: "Not applicable (AC Powered)." },
      { factor: "Repair History", score: 8, maxScore: 15, status: "MEDIUM", description: "2 past maintenance cleanings." },
      { factor: "Warranty Status", score: 0, maxScore: 10, status: "CRITICAL", description: "Manufacturer warranty expired." },
      { factor: "Last Service Recency", score: 1, maxScore: 10, status: "CRITICAL", description: "14 months since last servicing." },
    ],
    estimatedRepairCost: 110.0,
    preventiveSavings: 44.0,
    co2SavingsKg: 35.0,
    confidenceScore: 0.89,
    isDemo: true,
    evaluatedAt: new Date().toISOString(),
  },
};

export const DEMO_FAULT_PATTERNS: FaultPatternItem[] = [
  {
    id: "fp-001",
    deviceCategory: "Smartphone",
    deviceBrand: null,
    faultType: "Battery Degradation",
    description: "Lithium-ion chemical capacity reduction below critical retention threshold resulting in rapid discharge and unexpected throttling.",
    minDeviceAgeYears: 2,
    healthScoreThreshold: 70,
    riskWeight: 7,
    typicalCostMin: 45.0,
    typicalCostMax: 95.0,
    preventiveActions: ["Limit charge cycles to 80%", "Avoid ambient heat over 35°C", "Enable OS battery protection"],
    isActive: true,
  },
  {
    id: "fp-004",
    deviceCategory: "Laptop",
    deviceBrand: null,
    faultType: "Thermal Interface Paste Degradation",
    description: "Dried silicone thermal compound leading to high junction temperatures and severe CPU/GPU thermal throttling.",
    minDeviceAgeYears: 2,
    healthScoreThreshold: 65,
    riskWeight: 8,
    typicalCostMin: 50.0,
    typicalCostMax: 110.0,
    preventiveActions: ["Repaste heatsink with high-conductivity compound", "Clean exhaust fans bi-annually"],
    isActive: true,
  },
  {
    id: "fp-007",
    deviceCategory: "Gaming Console",
    deviceBrand: "Sony",
    faultType: "Liquid Metal Thermal Runoff & APU Overheating",
    description: "Oxidation and dry spot development on the APU dye causing thermal shutdown during peak load.",
    minDeviceAgeYears: 2,
    healthScoreThreshold: 60,
    riskWeight: 8,
    typicalCostMin: 75.0,
    typicalCostMax: 150.0,
    preventiveActions: ["Ensure 15cm rear clearance", "Clean rear exhaust quarterly", "Service cooling chamber"],
    isActive: true,
  },
];

export const DEMO_RECOMMENDATIONS: MaintenanceRecommendationItem[] = [
  {
    id: "rec-1",
    title: "Thermal Repaste & Fan De-dusting",
    description: "Living Room Gaming Console shows elevated APU thermal signatures. Proactive cleaning avoids APU failure.",
    priority: "HIGH",
    category: "Cooling & Hardware",
    estimatedCost: "$45 - $80",
    estimatedTime: "1 hour",
    impact: "Reduces junction temp by 18°C, extends console lifespan 3+ years",
    steps: [
      "Power down console and disconnect all cables",
      "Book service with a local certified technician",
      "Request phase-change or premium liquid metal repaste",
    ],
  },
  {
    id: "rec-2",
    title: "Optimized Battery Charge Calibration",
    description: "Personal iPhone 14 Pro battery health at 89%. Calibration preserves remaining cell lifespan.",
    priority: "MEDIUM",
    category: "Battery & Power",
    estimatedCost: "$0",
    estimatedTime: "Overnight",
    impact: "Slows cell decay rate by ~40% over next 12 months",
    steps: [
      "Enable 'Optimized Battery Charging' in iOS Settings",
      "Avoid wireless charging above 80% battery capacity",
      "Perform a full discharge and 100% recalibration cycle monthly",
    ],
  },
];

export const DEMO_FLEET_OVERVIEW: PredictiveFleetOverviewData = {
  totalDevices: 4,
  criticalDevices: 0,
  highRiskDevices: 1,
  mediumRiskDevices: 1,
  lowRiskDevices: 1,
  healthyDevices: 1,
  averagePredictionScore: 78.5,
  totalEstimatedRepairCost: 220.0,
  totalPreventiveSavings: 88.0,
  totalCo2SavingsKg: 78.3,
  riskDistribution: [
    { riskLevel: "CRITICAL", count: 0, percentage: 0 },
    { riskLevel: "HIGH", count: 1, percentage: 25 },
    { riskLevel: "MEDIUM", count: 1, percentage: 25 },
    { riskLevel: "LOW", count: 1, percentage: 25 },
    { riskLevel: "HEALTHY", count: 1, percentage: 25 },
  ],
  isDemo: true,
};

// ─── API Client Functions ─────────────────────────────────────────────────────

/**
 * Evaluate predictive maintenance assessment for a device.
 */
export async function evaluateDevicePrediction(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<DevicePredictionData>> {
  if (isDemoSession(token)) {
    const demo = DEMO_PREDICTIONS[deviceId] || {
      ...DEMO_PREDICTIONS.dev_sample_1,
      deviceId,
      deviceName: "Monitored Device",
    };
    return createDemoResponse(demo, "Sample predictive maintenance assessment (Demo Mode)");
  }

  const res = await apiClient<DevicePredictionData>(`/predictive/devices/${deviceId}`, { token });
  if (res.success && res.data) {
    return res;
  }

  // Graceful fallback for demo devices
  const fallback = DEMO_PREDICTIONS[deviceId] || {
    ...DEMO_PREDICTIONS.dev_sample_1,
    deviceId,
    deviceName: "Monitored Device",
  };
  return createDemoResponse(fallback, "Backend unavailable. Showing reference predictive score (Demo Mode)");
}

/**
 * Fetch predictive assessments across the authenticated user's device fleet.
 */
export async function fetchUserFleetPredictions(
  token?: string | null
): Promise<ApiResponse<DevicePredictionData[]>> {
  if (isDemoSession(token)) {
    return createDemoResponse(Object.values(DEMO_PREDICTIONS), "Fleet predictive status (Demo Mode)");
  }

  const res = await apiClient<DevicePredictionData[]>("/predictive/fleet", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(Object.values(DEMO_PREDICTIONS), "Fleet predictive status (Demo Mode)");
}

/**
 * Fetch prioritized maintenance recommendations.
 */
export async function fetchMaintenanceRecommendations(
  token?: string | null
): Promise<ApiResponse<MaintenanceRecommendationItem[]>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_RECOMMENDATIONS, "Maintenance recommendations (Demo Mode)");
  }

  const res = await apiClient<MaintenanceRecommendationItem[]>("/predictive/recommendations", { token });
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_RECOMMENDATIONS, "Maintenance recommendations (Demo Mode)");
}

/**
 * Fetch curated fault patterns library.
 */
export async function fetchFaultPatterns(
  category?: string,
  token?: string | null
): Promise<ApiResponse<FaultPatternItem[]>> {
  const params = category ? { category } : undefined;
  const res = await apiClient<FaultPatternItem[]>("/predictive/fault-patterns", { token, params });

  if (res.success && res.data && res.data.length > 0) {
    return res;
  }

  const filtered = category
    ? DEMO_FAULT_PATTERNS.filter((p) => !p.deviceCategory || p.deviceCategory === category)
    : DEMO_FAULT_PATTERNS;

  return createDemoResponse(filtered, "Curated fault patterns (Demo Mode)");
}
