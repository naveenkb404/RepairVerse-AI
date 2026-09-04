import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  DigitalTwinDashboardResponse,
  DigitalTwinSnapshotResponse,
  ForecastResponse,
  ScenarioResponse,
  OptimizationResponse,
  SimulationEventResponse,
  DeviceTrajectoryResponse,
  EcosystemMetricsResponse,
  RunSimulationRequest,
  OptimizationRequest,
} from "@/lib/types/digitalTwin";

// ─── Demo / Fallback Mock Data ────────────────────────────────────────────────

export const MOCK_SNAPSHOT: DigitalTwinSnapshotResponse = {
  id: "dt-snap-001",
  deviceId: "dev-mock-01",
  deviceName: "MacBook Pro 16\" (M1 Max)",
  deviceCategory: "LAPTOP",
  healthScore: 84,
  failureRiskScore: 26,
  maintenanceScore: 88,
  repairEconomicsScore: 92,
  longevityScore: 85,
  sustainabilityScore: 94,
  predictedValue: 74500,
  predictedRepairCost: 3200,
  predictedFailureProbability: 0.26,
  simulationConfidence: 0.94,
  overallEcosystemScore: 88,
  snapshotTime: new Date().toISOString(),
};

export const MOCK_FORECASTS: ForecastResponse[] = [
  {
    id: "fc-3m",
    snapshotId: "dt-snap-001",
    deviceId: "dev-mock-01",
    forecastHorizonMonths: 3,
    predictedHealthScore: 81,
    predictedFailureRisk: 31,
    predictedRepairCost: 3450,
    predictedDeviceValue: 71200,
    predictedRemainingLifespanMonths: 33,
    predictedCo2Impact: 4.8,
    predictedEWasteImpact: 0.2,
    forecastConfidence: 0.93,
  },
  {
    id: "fc-6m",
    snapshotId: "dt-snap-001",
    deviceId: "dev-mock-01",
    forecastHorizonMonths: 6,
    predictedHealthScore: 76,
    predictedFailureRisk: 38,
    predictedRepairCost: 3800,
    predictedDeviceValue: 67500,
    predictedRemainingLifespanMonths: 30,
    predictedCo2Impact: 7.2,
    predictedEWasteImpact: 0.3,
    forecastConfidence: 0.90,
  },
  {
    id: "fc-12m",
    snapshotId: "dt-snap-001",
    deviceId: "dev-mock-01",
    forecastHorizonMonths: 12,
    predictedHealthScore: 68,
    predictedFailureRisk: 49,
    predictedRepairCost: 4700,
    predictedDeviceValue: 59000,
    predictedRemainingLifespanMonths: 24,
    predictedCo2Impact: 11.5,
    predictedEWasteImpact: 0.5,
    forecastConfidence: 0.86,
  },
  {
    id: "fc-18m",
    snapshotId: "dt-snap-001",
    deviceId: "dev-mock-01",
    forecastHorizonMonths: 18,
    predictedHealthScore: 59,
    predictedFailureRisk: 62,
    predictedRepairCost: 6100,
    predictedDeviceValue: 51200,
    predictedRemainingLifespanMonths: 18,
    predictedCo2Impact: 16.2,
    predictedEWasteImpact: 0.9,
    forecastConfidence: 0.81,
  },
  {
    id: "fc-24m",
    snapshotId: "dt-snap-001",
    deviceId: "dev-mock-01",
    forecastHorizonMonths: 24,
    predictedHealthScore: 48,
    predictedFailureRisk: 75,
    predictedRepairCost: 7800,
    predictedDeviceValue: 43000,
    predictedRemainingLifespanMonths: 12,
    predictedCo2Impact: 21.0,
    predictedEWasteImpact: 1.8,
    forecastConfidence: 0.76,
  },
];

export const MOCK_SCENARIOS: ScenarioResponse[] = [
  {
    id: "sc-repair-now",
    deviceId: "dev-mock-01",
    scenarioType: "REPAIR_NOW",
    scenarioName: "Immediate Component Repair & BMS Recalibration",
    projectedHealthScore: 96,
    projectedFailureRisk: 5,
    projectedCost: 2800,
    projectedSavings: 18500,
    projectedLifespanMonths: 36,
    projectedCo2Impact: 14.8,
    projectedEWasteImpact: 0.2,
    downtimeDays: 1,
    overallOutcomeScore: 96,
    simulationConfidence: 0.95,
  },
  {
    id: "sc-prev-maint",
    deviceId: "dev-mock-01",
    scenarioType: "PREVENTIVE_MAINTENANCE",
    scenarioName: "Scheduled Ultrasonic Clean & Thermal Compound Refresh",
    projectedHealthScore: 92,
    projectedFailureRisk: 10,
    projectedCost: 1200,
    projectedSavings: 6800,
    projectedLifespanMonths: 28,
    projectedCo2Impact: 8.5,
    projectedEWasteImpact: 0.1,
    downtimeDays: 0,
    overallOutcomeScore: 92,
    simulationConfidence: 0.92,
  },
  {
    id: "sc-pro-serv",
    deviceId: "dev-mock-01",
    scenarioType: "PROFESSIONAL_SERVICE",
    scenarioName: "Certified Master Service Center Overhaul",
    projectedHealthScore: 98,
    projectedFailureRisk: 4,
    projectedCost: 4200,
    projectedSavings: 15500,
    projectedLifespanMonths: 36,
    projectedCo2Impact: 13.5,
    projectedEWasteImpact: 0.2,
    downtimeDays: 2,
    overallOutcomeScore: 93,
    simulationConfidence: 0.95,
  },
  {
    id: "sc-refurb",
    deviceId: "dev-mock-01",
    scenarioType: "REFURBISH_DEVICE",
    scenarioName: "Complete Sub-System Refurbishment & Tier-1 Upgrades",
    projectedHealthScore: 94,
    projectedFailureRisk: 8,
    projectedCost: 5500,
    projectedSavings: 12000,
    projectedLifespanMonths: 30,
    projectedCo2Impact: 18.2,
    projectedEWasteImpact: 0.3,
    downtimeDays: 3,
    overallOutcomeScore: 88,
    simulationConfidence: 0.90,
  },
  {
    id: "sc-continue",
    deviceId: "dev-mock-01",
    scenarioType: "CONTINUE_CURRENT_USAGE",
    scenarioName: "Continue Standard Operation (No Intervention)",
    projectedHealthScore: 62,
    projectedFailureRisk: 48,
    projectedCost: 0,
    projectedSavings: 0,
    projectedLifespanMonths: 14,
    projectedCo2Impact: 2.0,
    projectedEWasteImpact: 0.0,
    downtimeDays: 0,
    overallOutcomeScore: 56,
    simulationConfidence: 0.88,
  },
  {
    id: "sc-delay",
    deviceId: "dev-mock-01",
    scenarioType: "DELAY_REPAIR",
    scenarioName: "Defer Component Repair by 6-9 Months",
    projectedHealthScore: 44,
    projectedFailureRisk: 72,
    projectedCost: 5800,
    projectedSavings: -3500,
    projectedLifespanMonths: 8,
    projectedCo2Impact: 5.0,
    projectedEWasteImpact: 0.8,
    downtimeDays: 4,
    overallOutcomeScore: 38,
    simulationConfidence: 0.85,
  },
  {
    id: "sc-replace",
    deviceId: "dev-mock-01",
    scenarioType: "REPLACE_DEVICE",
    scenarioName: "Procure Brand New Replacement Unit",
    projectedHealthScore: 100,
    projectedFailureRisk: 2,
    projectedCost: 75000,
    projectedSavings: -25000,
    projectedLifespanMonths: 48,
    projectedCo2Impact: -54.0,
    projectedEWasteImpact: 2.4,
    downtimeDays: 1,
    overallOutcomeScore: 70,
    simulationConfidence: 0.96,
  },
  {
    id: "sc-recycle",
    deviceId: "dev-mock-01",
    scenarioType: "RECYCLE_DEVICE",
    scenarioName: "Certified Zero-Landfill E-Waste Material Recovery",
    projectedHealthScore: 0,
    projectedFailureRisk: 0,
    projectedCost: 0,
    projectedSavings: 1500,
    projectedLifespanMonths: 0,
    projectedCo2Impact: 24.5,
    projectedEWasteImpact: 2.4,
    downtimeDays: 0,
    overallOutcomeScore: 64,
    simulationConfidence: 0.98,
  },
];

export const MOCK_OPTIMIZATION: OptimizationResponse = {
  id: "opt-rec-001",
  deviceId: "dev-mock-01",
  recommendedStrategy: "REPAIR_NOW",
  costScore: 94,
  reliabilityScore: 96,
  longevityScore: 92,
  sustainabilityScore: 96,
  optimizationScore: 95,
  estimatedSavings: 18500,
  estimatedLifespanGain: 36,
  estimatedCo2Savings: 14.8,
  decisionReason: "Executing immediate repair resolves critical failure risk while preserving ₹18,500 in asset value and avoiding 14.8 kg CO₂ manufacturing emissions.",
  generatedAt: new Date().toISOString(),
};

export const MOCK_SIMULATION_EVENTS: SimulationEventResponse[] = [
  {
    id: "ev-01",
    deviceId: "dev-mock-01",
    eventType: "OPTIMAL_INTERVENTION",
    severity: "INFO",
    title: "Digital Twin Calibrated",
    description: "Twin baseline established using multi-sensor diagnostics and battery telemetry.",
    projectedMonthOffset: 0,
    estimatedFinancialImpact: 0,
    mitigationStrategy: "PREVENTIVE_MAINTENANCE",
    createdAt: new Date().toISOString(),
  },
  {
    id: "ev-02",
    deviceId: "dev-mock-01",
    eventType: "MAINTENANCE_DUE",
    severity: "MEDIUM",
    title: "Scheduled Thermal Compound Renewal",
    description: "Thermal interface desiccation projected to breach 85°C junction threshold.",
    projectedMonthOffset: 3,
    estimatedFinancialImpact: 1200,
    mitigationStrategy: "PREVENTIVE_MAINTENANCE",
    createdAt: new Date().toISOString(),
  },
  {
    id: "ev-03",
    deviceId: "dev-mock-01",
    eventType: "FAILURE_RISK_INCREASE",
    severity: "HIGH",
    title: "Battery Electrochemical Degradation Accelerating",
    description: "Unattended cell impedance escalation projected to trigger kernel panics.",
    projectedMonthOffset: 8,
    estimatedFinancialImpact: 4200,
    mitigationStrategy: "REPAIR_NOW",
    createdAt: new Date().toISOString(),
  },
  {
    id: "ev-04",
    deviceId: "dev-mock-01",
    eventType: "SUSTAINABILITY_OPPORTUNITY",
    severity: "LOW",
    title: "Sub-System Refurbishment Window",
    description: "Optimal point for complete refurbishment to extend usable life past Year 4.",
    projectedMonthOffset: 18,
    estimatedFinancialImpact: 5500,
    mitigationStrategy: "REFURBISH_DEVICE",
    createdAt: new Date().toISOString(),
  },
];

export const MOCK_DASHBOARD: DigitalTwinDashboardResponse = {
  deviceId: "dev-mock-01",
  deviceName: "MacBook Pro 16\" (M1 Max)",
  deviceCategory: "LAPTOP",
  snapshot: MOCK_SNAPSHOT,
  forecasts: MOCK_FORECASTS,
  scenarios: MOCK_SCENARIOS,
  optimalStrategy: MOCK_OPTIMIZATION,
  events: MOCK_SIMULATION_EVENTS,
  insights: [
    {
      type: "RELIABILITY",
      title: "Elevated Failure Risk",
      message: "Repairing within the next 30 days is projected to reduce failure risk by 21% and restore health to 96%.",
      category: "RELIABILITY",
      impactLevel: "HIGH",
    },
    {
      type: "FINANCIAL",
      title: "Delay Escalation Warning",
      message: "Delaying repair past 90 days may increase estimated repair cost by ₹1,440 due to secondary battery swell.",
      category: "FINANCIAL",
      impactLevel: "MEDIUM",
    },
    {
      type: "STRATEGY",
      title: "Optimal Lifecycle Strategy",
      message: "Immediate repair produces the optimal ROI, mitigating cascading faults and saving up to ₹18,500 over 24 months.",
      category: "LONGEVITY",
      impactLevel: "HIGH",
    },
    {
      type: "SUSTAINABILITY",
      title: "Circular Value Optimization",
      message: "Choosing repair or refurbishment over replacement prevents approximately 14.8 kg CO₂ and eliminates e-waste disposal.",
      category: "SUSTAINABILITY",
      impactLevel: "MEDIUM",
    },
    {
      type: "TRAJECTORY",
      title: "24-Month Trajectory Projection",
      message: "Without intervention, failure risk rises to 75% by month 24 with estimated device residual value falling to ₹43,000.",
      category: "RISK",
      impactLevel: "HIGH",
    },
  ],
  isSimulated: false,
};

export const MOCK_ECOSYSTEM_METRICS: EcosystemMetricsResponse = {
  totalMonitoredDevices: 4,
  totalProjectedSavings: 42500,
  totalFailuresPrevented: 6,
  totalCo2AvoidedKg: 68.4,
  averageEcosystemHealth: 86,
  activeSimulationsCount: 4,
};

// ─── API Client Methods ───────────────────────────────────────────────────────

/**
 * Get full Digital Twin for a device.
 * Corresponds to: GET /api/v1/digital-twin/{deviceId}
 */
export async function fetchDigitalTwin(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DigitalTwinDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Digital Twin loaded (Demo Session)",
      data: { ...MOCK_DASHBOARD, deviceId },
    };
  }

  const result = await apiClient<DigitalTwinDashboardResponse>(`/digital-twin/${deviceId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/digital-twin is offline. Displaying simulated digital twin.`,
    data: { ...MOCK_DASHBOARD, deviceId },
  };
}

/**
 * Recalibrate and refresh Digital Twin state and predictions.
 * Corresponds to: POST /api/v1/digital-twin/{deviceId}/refresh
 */
export async function refreshDigitalTwin(
  deviceId: string,
  token?: string
): Promise<ApiResponse<DigitalTwinDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Digital Twin recalibrated and refreshed successfully (Demo Session).",
      data: { ...MOCK_DASHBOARD, deviceId },
    };
  }

  const result = await apiClient<DigitalTwinDashboardResponse>(`/digital-twin/${deviceId}/refresh`, {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Digital Twin recalibrated (Simulated).",
    data: { ...MOCK_DASHBOARD, deviceId },
  };
}

/**
 * Fetch multi-horizon forecasts for a device.
 * Corresponds to: GET /api/v1/digital-twin/{deviceId}/forecasts
 */
export async function fetchForecasts(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<ForecastResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_FORECASTS,
    };
  }

  const result = await apiClient<ForecastResponse[]>(`/digital-twin/${deviceId}/forecasts`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_FORECASTS,
  };
}

/**
 * Fetch trajectory data points for charts.
 * Corresponds to: GET /api/v1/digital-twin/{deviceId}/trajectory
 */
export async function fetchTrajectory(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DeviceTrajectoryResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: {
        deviceId,
        deviceName: "MacBook Pro 16\" (M1 Max)",
        trajectoryPoints: [
          { monthOffset: 0, healthScore: 84, failureRisk: 26, repairCost: 3200, deviceValue: 74500 },
          { monthOffset: 3, healthScore: 81, failureRisk: 31, repairCost: 3450, deviceValue: 71200 },
          { monthOffset: 6, healthScore: 76, failureRisk: 38, repairCost: 3800, deviceValue: 67500 },
          { monthOffset: 12, healthScore: 68, failureRisk: 49, repairCost: 4700, deviceValue: 59000 },
          { monthOffset: 18, healthScore: 59, failureRisk: 62, repairCost: 6100, deviceValue: 51200 },
          { monthOffset: 24, healthScore: 48, failureRisk: 75, repairCost: 7800, deviceValue: 43000 },
        ],
      },
    };
  }

  const result = await apiClient<DeviceTrajectoryResponse>(`/digital-twin/${deviceId}/trajectory`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: {
      deviceId,
      deviceName: "MacBook Pro 16\" (M1 Max)",
      trajectoryPoints: [
        { monthOffset: 0, healthScore: 84, failureRisk: 26, repairCost: 3200, deviceValue: 74500 },
        { monthOffset: 3, healthScore: 81, failureRisk: 31, repairCost: 3450, deviceValue: 71200 },
        { monthOffset: 6, healthScore: 76, failureRisk: 38, repairCost: 3800, deviceValue: 67500 },
        { monthOffset: 12, healthScore: 68, failureRisk: 49, repairCost: 4700, deviceValue: 59000 },
        { monthOffset: 18, healthScore: 59, failureRisk: 62, repairCost: 6100, deviceValue: 51200 },
        { monthOffset: 24, healthScore: 48, failureRisk: 75, repairCost: 7800, deviceValue: 43000 },
      ],
    },
  };
}

/**
 * Fetch simulated alternative scenarios.
 * Corresponds to: GET /api/v1/digital-twin/{deviceId}/scenarios
 */
export async function fetchScenarios(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<ScenarioResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_SCENARIOS,
    };
  }

  const result = await apiClient<ScenarioResponse[]>(`/digital-twin/${deviceId}/scenarios`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_SCENARIOS,
  };
}

/**
 * Run custom simulation with user constraints.
 * Corresponds to: POST /api/v1/digital-twin/{deviceId}/simulate
 */
export async function runCustomSimulation(
  deviceId: string,
  request: RunSimulationRequest,
  token?: string
): Promise<ApiResponse<DigitalTwinDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Custom simulation evaluated (Demo Session).",
      data: { ...MOCK_DASHBOARD, deviceId, isSimulated: true },
    };
  }

  const result = await apiClient<DigitalTwinDashboardResponse>(`/digital-twin/${deviceId}/simulate`, {
    method: "POST",
    body: JSON.stringify(request),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Custom simulation executed (Simulated).",
    data: { ...MOCK_DASHBOARD, deviceId, isSimulated: true },
  };
}

/**
 * Optimize strategy for a device.
 * Corresponds to: POST /api/v1/digital-twin/{deviceId}/optimize
 */
export async function optimizeStrategy(
  deviceId: string,
  request?: OptimizationRequest,
  token?: string
): Promise<ApiResponse<OptimizationResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Strategy optimized (Demo Session).",
      data: MOCK_OPTIMIZATION,
    };
  }

  const result = await apiClient<OptimizationResponse>(`/digital-twin/${deviceId}/optimize`, {
    method: "POST",
    body: request ? JSON.stringify(request) : undefined,
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Strategy optimized (Simulated).",
    data: MOCK_OPTIMIZATION,
  };
}

/**
 * Fetch future simulation events.
 * Corresponds to: GET /api/v1/digital-twin/{deviceId}/events
 */
export async function fetchSimulationEvents(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<SimulationEventResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_SIMULATION_EVENTS,
    };
  }

  const result = await apiClient<SimulationEventResponse[]>(`/digital-twin/${deviceId}/events`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_SIMULATION_EVENTS,
  };
}

/**
 * Fetch ecosystem level aggregate metrics.
 * Corresponds to: GET /api/v1/digital-twin/dashboard
 */
export async function fetchEcosystemDashboard(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<EcosystemMetricsResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_ECOSYSTEM_METRICS,
    };
  }

  const result = await apiClient<EcosystemMetricsResponse>("/digital-twin/dashboard", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_ECOSYSTEM_METRICS,
  };
}
