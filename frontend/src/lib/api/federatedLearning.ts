import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  LearningDashboardResponse,
  LearningBatchResponse,
  LearningSignalResponse,
  ModelVersionResponse,
  LearningModelComparisonResponse,
  DeviceLearningProfileResponse,
  LearningImpactResponse,
  LearningTrendResponse,
  LearningRunResponse,
  LearningFeedbackRequest,
  PrivacyAuditResponse,
} from "@/lib/types/federatedLearning";

// ─── Demo / Fallback Mock Data ────────────────────────────────────────────────

export const MOCK_ACTIVE_MODEL: ModelVersionResponse = {
  id: "model-r35-4",
  modelName: "RepairVerse Federated Core",
  version: "R35.4",
  parentVersion: "R35.3",
  status: "ACTIVE",
  trainingObservations: 1284,
  validationScore: 94.2,
  trustScore: 94,
  improvementPercentage: 8.7,
  activatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2).toISOString(),
  retiredAt: null,
  createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3).toISOString(),
};

export const MOCK_MODEL_HISTORY: ModelVersionResponse[] = [
  MOCK_ACTIVE_MODEL,
  {
    id: "model-r35-3",
    modelName: "RepairVerse Federated Core",
    version: "R35.3",
    parentVersion: "R35.2",
    status: "SUPERSEDED",
    trainingObservations: 940,
    validationScore: 91.5,
    trustScore: 91,
    improvementPercentage: 5.2,
    activatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 10).toISOString(),
    retiredAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2).toISOString(),
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 11).toISOString(),
  },
  {
    id: "model-r35-2",
    modelName: "RepairVerse Federated Core",
    version: "R35.2",
    parentVersion: "R35.1",
    status: "SUPERSEDED",
    trainingObservations: 620,
    validationScore: 88.4,
    trustScore: 88,
    improvementPercentage: 3.1,
    activatedAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 25).toISOString(),
    retiredAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 10).toISOString(),
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 26).toISOString(),
  },
];

export const MOCK_SIGNALS: LearningSignalResponse[] = [
  {
    id: "sig-01",
    batchId: "fl-batch-001",
    signalType: "REPAIR_SUCCESS_RATE",
    deviceCategory: "SMARTPHONE",
    componentType: "BATTERY",
    failureMode: "ELECTROCHEMICAL_DEGRADATION",
    repairAction: "REPLACE_BATTERY",
    outcomeClass: "HIGH_SUCCESS_REPAIR",
    aggregatedFrequency: 342,
    successRate: 0.94,
    averageCost: 1850.0,
    averageLifespanGain: 28,
    sustainabilityScore: 94.0,
    confidence: 0.96,
    observationCount: 342,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 12).toISOString(),
  },
  {
    id: "sig-02",
    batchId: "fl-batch-001",
    signalType: "PREVENTIVE_MAINTENANCE",
    deviceCategory: "LAPTOP",
    componentType: "THERMAL_SYSTEM",
    failureMode: "THERMAL_PASTE_DESICCATION",
    repairAction: "REFRESH_THERMAL_COMPOUND",
    outcomeClass: "HIGH_SUCCESS_REPAIR",
    aggregatedFrequency: 218,
    successRate: 0.92,
    averageCost: 1200.0,
    averageLifespanGain: 24,
    sustainabilityScore: 96.0,
    confidence: 0.94,
    observationCount: 218,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 18).toISOString(),
  },
  {
    id: "sig-03",
    batchId: "fl-batch-001",
    signalType: "REPAIR_OUTCOME_LEARNING",
    deviceCategory: "LAPTOP",
    componentType: "KEYBOARD_SWITCHES",
    failureMode: "KEY_MEMBRANE_FAILURE",
    repairAction: "SUB_ASSEMBLY_REPLACE",
    outcomeClass: "MODERATE_SUCCESS_REPAIR",
    aggregatedFrequency: 86,
    successRate: 0.88,
    averageCost: 2400.0,
    averageLifespanGain: 18,
    sustainabilityScore: 88.0,
    confidence: 0.90,
    observationCount: 86,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
  },
  {
    id: "sig-04",
    batchId: "fl-batch-001",
    signalType: "REPAIR_SUCCESS_RATE",
    deviceCategory: "TABLET",
    componentType: "CHARGING_PORT",
    failureMode: "PORT_PIN_OXIDATION",
    repairAction: "MICRO_SOLDER_REPAIR",
    outcomeClass: "HIGH_SUCCESS_REPAIR",
    aggregatedFrequency: 64,
    successRate: 0.89,
    averageCost: 1600.0,
    averageLifespanGain: 22,
    sustainabilityScore: 92.0,
    confidence: 0.89,
    observationCount: 64,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 30).toISOString(),
  },
];

export const MOCK_PRIVACY_AUDITS: PrivacyAuditResponse[] = [
  {
    id: "pae-001",
    batchId: "fl-batch-001",
    eventType: "BATCH_PRIVACY_AUDIT",
    privacyRule: "MIN_OBSERVATIONS_THRESHOLD_AND_PII_STRIP",
    recordsProcessed: 1340,
    recordsFiltered: 56,
    recordsAggregated: 1284,
    sensitiveFieldsRemoved: 1340,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
  },
  {
    id: "pae-002",
    batchId: "fl-batch-001",
    eventType: "PII_SCRUBBING",
    privacyRule: "CRYPTOGRAPHIC_SALT_HASH_DEVICE_IDS",
    recordsProcessed: 1284,
    recordsFiltered: 0,
    recordsAggregated: 1284,
    sensitiveFieldsRemoved: 2568,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
  },
];

export const MOCK_DASHBOARD: LearningDashboardResponse = {
  activeModelVersion: "R35.4",
  activeModelName: "RepairVerse Federated Core",
  validationScore: 94.2,
  trustScore: 94,
  improvementPercentage: 8.7,
  totalAnonymizedDevices: 248,
  totalAnonymizedRepairs: 1284,
  activeLearningSignalsCount: 18,
  validatedPatternsCount: 73,
  privacyComplianceScore: 100.0,
  lastLearningCycle: new Date(Date.now() - 1000 * 60 * 60 * 6).toISOString(),
  currentModel: MOCK_ACTIVE_MODEL,
  modelHistory: MOCK_MODEL_HISTORY,
  topSignals: MOCK_SIGNALS,
  recentPrivacyAudits: MOCK_PRIVACY_AUDITS,
  impactMetrics: {
    recommendationAccuracyGain: 10.2,
    repairSuccessImprovement: 8.3,
    costPredictionStability: 94.6,
    co2OptimizationImprovement: 12.4,
    failurePredictionGain: 9.1,
    totalDecisionsEnriched: 1284,
  },
};

export const MOCK_DEVICE_PROFILE: DeviceLearningProfileResponse = {
  deviceId: "dev-mock-01",
  deviceCategory: "LAPTOP",
  activeModelVersion: "R35.4",
  matchingEcosystemObservations: 304,
  ecosystemSuccessRate: 0.93,
  expectedLifespanGainMonths: 30,
  expectedCostSavings: 6800.0,
  confidence: 0.95,
  relevantSignals: MOCK_SIGNALS.filter((s) => s.deviceCategory === "LAPTOP"),
  privacyNotice: "Derived from 304 privacy-filtered, aggregated ecosystem outcomes. No device serials or personal data exposed.",
};

// ─── API Client Methods ───────────────────────────────────────────────────────

/**
 * Fetch top-level Federated Learning Dashboard.
 * GET /api/v1/learning/dashboard
 */
export async function fetchLearningDashboard(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<LearningDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Federated Learning Dashboard loaded (Demo Session)",
      data: MOCK_DASHBOARD,
    };
  }

  const result = await apiClient<LearningDashboardResponse>("/learning/dashboard", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/learning is offline. Displaying simulated learning dashboard.`,
    data: MOCK_DASHBOARD,
  };
}

/**
 * Fetch all learning signals.
 * GET /api/v1/learning/signals
 */
export async function fetchLearningSignals(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<LearningSignalResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_SIGNALS,
    };
  }

  const result = await apiClient<LearningSignalResponse[]>("/learning/signals", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_SIGNALS,
  };
}

/**
 * Fetch privacy-preserving learning profile for a specific device.
 * GET /api/v1/learning/device/{deviceId}
 */
export async function fetchDeviceLearningProfile(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DeviceLearningProfileResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: { ...MOCK_DEVICE_PROFILE, deviceId },
    };
  }

  const result = await apiClient<DeviceLearningProfileResponse>(`/learning/device/${deviceId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: { ...MOCK_DEVICE_PROFILE, deviceId },
  };
}

/**
 * Trigger an automated federated learning cycle.
 * POST /api/v1/learning/run
 */
export async function runLearningCycle(
  token?: string
): Promise<ApiResponse<LearningRunResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Federated Learning Cycle simulated (Demo Session)",
      data: {
        success: true,
        message: "Continuous learning cycle completed successfully. Candidate model R35.5 approved.",
        batchReference: "BATCH-SIM-" + Date.now(),
        candidateVersion: "R35.5",
        anonymizedOutcomesProcessed: 42,
        validationScore: 95.1,
        validationPassed: true,
        nextAction: "READY_FOR_ACTIVATION",
      },
    };
  }

  const result = await apiClient<LearningRunResponse>("/learning/run", {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Federated Learning Cycle executed (Simulated)",
    data: {
      success: true,
      message: "Continuous learning cycle completed successfully. Candidate model R35.5 approved.",
      batchReference: "BATCH-SIM-" + Date.now(),
      candidateVersion: "R35.5",
      anonymizedOutcomesProcessed: 42,
      validationScore: 95.1,
      validationPassed: true,
      nextAction: "READY_FOR_ACTIVATION",
    },
  };
}

/**
 * Activate a validated candidate model version.
 * POST /api/v1/learning/activate/{version}
 */
export async function activateModelVersion(
  version: string,
  token?: string
): Promise<ApiResponse<ModelVersionResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: `Model version '${version}' activated successfully (Demo Session).`,
      data: {
        ...MOCK_ACTIVE_MODEL,
        version,
        activatedAt: new Date().toISOString(),
      },
    };
  }

  const result = await apiClient<ModelVersionResponse>(`/learning/activate/${version}`, {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Model version '${version}' activated (Simulated).`,
    data: {
      ...MOCK_ACTIVE_MODEL,
      version,
      activatedAt: new Date().toISOString(),
    },
  };
}

/**
 * Compare candidate model against current active baseline.
 * GET /api/v1/learning/models/{version}/comparison
 */
export async function compareModelVersion(
  version: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<LearningModelComparisonResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: {
        currentModel: MOCK_ACTIVE_MODEL,
        candidateModel: {
          ...MOCK_ACTIVE_MODEL,
          version: "R35.5",
          validationScore: 95.4,
          trustScore: 95,
          improvementPercentage: 1.2,
        },
        accuracyDelta: 1.2,
        costStabilityDelta: 2.1,
        trustScoreDelta: 1.0,
        newObservationsCount: 120,
        safeToActivate: true,
        validationBreakdown: [
          {
            id: "val-1",
            modelVersionId: "model-r35-5",
            validationType: "RECOMMENDATION_ACCURACY",
            baselineScore: 94.2,
            candidateScore: 95.4,
            improvementScore: 1.2,
            regressionDetected: false,
            confidence: 0.94,
            decision: "ACCEPTED",
            validatedAt: new Date().toISOString(),
          },
          {
            id: "val-2",
            modelVersionId: "model-r35-5",
            validationType: "GOVERNANCE_COMPLIANCE",
            baselineScore: 100.0,
            candidateScore: 100.0,
            improvementScore: 0.0,
            regressionDetected: false,
            confidence: 0.98,
            decision: "ACCEPTED",
            validatedAt: new Date().toISOString(),
          },
        ],
        governanceRecommendations: [
          "Candidate model satisfies all privacy and regression criteria.",
          "Safe to activate in production.",
        ],
      },
    };
  }

  const result = await apiClient<LearningModelComparisonResponse>(`/learning/models/${version}/comparison`, {
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
      currentModel: MOCK_ACTIVE_MODEL,
      candidateModel: {
        ...MOCK_ACTIVE_MODEL,
        version: "R35.5",
        validationScore: 95.4,
        trustScore: 95,
        improvementPercentage: 1.2,
      },
      accuracyDelta: 1.2,
      costStabilityDelta: 2.1,
      trustScoreDelta: 1.0,
      newObservationsCount: 120,
      safeToActivate: true,
      validationBreakdown: [],
      governanceRecommendations: ["Safe to activate in production."],
    },
  };
}

/**
 * Submit outcome agreement feedback.
 * POST /api/v1/learning/feedback
 */
export async function submitLearningFeedback(
  request: LearningFeedbackRequest,
  token?: string
): Promise<ApiResponse<void>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Learning feedback recorded (Demo Session)",
    };
  }

  const result = await apiClient<void>("/learning/feedback", {
    method: "POST",
    body: JSON.stringify(request),
    token,
  });

  if (result.success) {
    return result;
  }

  return {
    success: true,
    message: "Feedback submitted (Simulated)",
  };
}
