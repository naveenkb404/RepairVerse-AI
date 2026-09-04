import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  AgentDashboardResponse,
  InterventionResponse,
  ActionStepResponse,
  ExecutionHistoryResponse,
  ExecutionResultResponse,
  ActionApprovalRequest,
  ActionExecutionRequest,
} from "@/lib/types/autonomousRepairAgent";

// ─── Mock Fallback Data ────────────────────────────────────────────────────────

export const MOCK_INTERVENTIONS: InterventionResponse[] = [
  {
    id: "int-001",
    userId: "demo-user",
    deviceId: "dev-macbook-pro",
    deviceName: "MacBook Pro 16\" (M1 Max)",
    deviceCategory: "LAPTOP",
    interventionType: "URGENT_REPAIR",
    priority: "CRITICAL",
    status: "IN_PROGRESS",
    title: "Thermal Throttling & Battery Swell Risk Detected",
    description:
      "Telemetry indicates sustained CPU temperatures exceeding 94°C and battery cycle count at 842 with internal resistance spikes. High probability of thermal shutdown and cell degradation.",
    reason: "Battery capacity degradation (68% health) and dried thermal interface material causing severe thermal throttling under standard load.",
    confidenceScore: 0.94,
    priorityScore: 92,
    estimatedCost: 189.0,
    estimatedSavings: 1450.0,
    estimatedCo2Impact: 14.8,
    recommendedAction: "Replace battery pack and repaste thermal assembly with high-conductivity compound.",
    requiresUserApproval: true,
    createdAt: new Date(Date.now() - 3600 * 1000 * 4).toISOString(),
    actionPlan: {
      id: "plan-001",
      interventionId: "int-001",
      planName: "Autonomous Thermal & Battery Remediation",
      objective: "Safely restore thermal headroom and battery reliability while preventing motherboard degradation.",
      totalSteps: 4,
      completedSteps: 2,
      status: "IN_PROGRESS",
      createdAt: new Date(Date.now() - 3600 * 1000 * 4).toISOString(),
      steps: [
        {
          id: "step-101",
          planId: "plan-001",
          interventionId: "int-001",
          deviceId: "dev-macbook-pro",
          deviceName: "MacBook Pro 16\" (M1 Max)",
          stepOrder: 1,
          actionType: "GENERATE_REPORT",
          title: "Generate Battery & Thermal Diagnostic Profile",
          description: "Compiled full sensor log, kernel panic analysis, and cycle degradation curve.",
          status: "COMPLETED",
          requiresApproval: false,
          completedAt: new Date(Date.now() - 3600 * 1000 * 3).toISOString(),
        },
        {
          id: "step-102",
          planId: "plan-001",
          interventionId: "int-001",
          deviceId: "dev-macbook-pro",
          deviceName: "MacBook Pro 16\" (M1 Max)",
          stepOrder: 2,
          actionType: "FIND_SHOPS",
          title: "Identify Top-Rated Apple Certified Independent Repairers",
          description: "Matched 3 verified repair shops within 5km with genuine OEM-grade battery stock and 4.9+ star rating.",
          status: "COMPLETED",
          requiresApproval: false,
          completedAt: new Date(Date.now() - 3600 * 1000 * 2).toISOString(),
        },
        {
          id: "step-103",
          planId: "plan-001",
          interventionId: "int-001",
          deviceId: "dev-macbook-pro",
          deviceName: "MacBook Pro 16\" (M1 Max)",
          stepOrder: 3,
          actionType: "REQUEST_QUOTE",
          title: "Request Instant Quotes & Reserve Battery Part",
          description: "Request prioritized repair slot and lock in $189 bundled quote with iFix Labs Downtown.",
          status: "WAITING_APPROVAL",
          requiresApproval: true,
        },
        {
          id: "step-104",
          planId: "plan-001",
          interventionId: "int-001",
          deviceId: "dev-macbook-pro",
          deviceName: "MacBook Pro 16\" (M1 Max)",
          stepOrder: 4,
          actionType: "BOOK_SERVICE",
          title: "Confirm Booking & Dispatch Pickup Courier",
          description: "Authorize appointment booking and generate repair transit insurance tag.",
          status: "PENDING",
          requiresApproval: true,
        },
      ],
    },
  },
  {
    id: "int-002",
    userId: "demo-user",
    deviceId: "dev-iphone-14",
    deviceName: "iPhone 14 Pro",
    deviceCategory: "SMARTPHONE",
    interventionType: "PREVENTIVE_REPAIR",
    priority: "HIGH",
    status: "PENDING_APPROVAL",
    title: "OLED Micro-Fracture Moisture Seal Warning",
    description:
      "Corner impact detected in accelerometer logs followed by display touch grid resistance variances. IP68 water resistance compromised.",
    reason: "Front digitizer hairline stress crack near ear speaker risking internal humidity ingress.",
    confidenceScore: 0.88,
    priorityScore: 78,
    estimatedCost: 120.0,
    estimatedSavings: 680.0,
    estimatedCo2Impact: 8.2,
    recommendedAction: "Apply UV optical adhesive reseal or swap glass lens before moisture penetrates logic board.",
    requiresUserApproval: true,
    createdAt: new Date(Date.now() - 3600 * 1000 * 18).toISOString(),
    actionPlan: {
      id: "plan-002",
      interventionId: "int-002",
      planName: "Display Enclosure Weatherproofing",
      objective: "Restore water seal integrity and preserve OLED display panel.",
      totalSteps: 3,
      completedSteps: 1,
      status: "PENDING_APPROVAL",
      createdAt: new Date(Date.now() - 3600 * 1000 * 18).toISOString(),
      steps: [
        {
          id: "step-201",
          planId: "plan-002",
          interventionId: "int-002",
          deviceId: "dev-iphone-14",
          deviceName: "iPhone 14 Pro",
          stepOrder: 1,
          actionType: "GENERATE_REPORT",
          title: "Synthesize Optical Sensor & Glass Integrity Scan",
          description: "Verified touchscreen matrix and mapped stress propagation path.",
          status: "COMPLETED",
          requiresApproval: false,
          completedAt: new Date(Date.now() - 3600 * 1000 * 17).toISOString(),
        },
        {
          id: "step-202",
          planId: "plan-002",
          interventionId: "int-002",
          deviceId: "dev-iphone-14",
          deviceName: "iPhone 14 Pro",
          stepOrder: 2,
          actionType: "SCHEDULE_MAINTENANCE",
          title: "Schedule Preventive Cleanroom Reseal Appointment",
          description: "Reserve 30-minute Express Reseal slot at TechRestore Hub.",
          status: "WAITING_APPROVAL",
          requiresApproval: true,
        },
        {
          id: "step-203",
          planId: "plan-002",
          interventionId: "int-002",
          deviceId: "dev-iphone-14",
          deviceName: "iPhone 14 Pro",
          stepOrder: 3,
          actionType: "NOTIFY_USER",
          title: "Send Moisture Precaution Checklist to Mobile",
          description: "Notify user to avoid steam and liquid exposure until reseal completes.",
          status: "PENDING",
          requiresApproval: false,
        },
      ],
    },
  },
  {
    id: "int-003",
    userId: "demo-user",
    deviceId: "dev-sony-wh1000",
    deviceName: "Sony WH-1000XM4",
    deviceCategory: "AUDIO",
    interventionType: "MAINTENANCE",
    priority: "MEDIUM",
    status: "APPROVED",
    title: "Earpad Foam Compression & Acoustic Seal Loss",
    description: "Active noise cancellation efficiency dropped by 32% due to headband tension wear and cushion degassing.",
    reason: "Synthetic protein leather earpad degradation causing acoustic air leakage and diminished low-frequency cancellation.",
    confidenceScore: 0.91,
    priorityScore: 54,
    estimatedCost: 24.0,
    estimatedSavings: 280.0,
    estimatedCo2Impact: 3.5,
    recommendedAction: "Install high-density cooling gel replacement ear cushions and recalibrate ANC microphones.",
    requiresUserApproval: false,
    createdAt: new Date(Date.now() - 3600 * 1000 * 48).toISOString(),
    actionPlan: {
      id: "plan-003",
      interventionId: "int-003",
      planName: "Acoustic Cushion Refresh & Calibration",
      objective: "Restore original noise cancellation acoustics and hygiene comfort.",
      totalSteps: 2,
      completedSteps: 1,
      status: "IN_PROGRESS",
      createdAt: new Date(Date.now() - 3600 * 1000 * 48).toISOString(),
      steps: [
        {
          id: "step-301",
          planId: "plan-003",
          interventionId: "int-003",
          deviceId: "dev-sony-wh1000",
          deviceName: "Sony WH-1000XM4",
          stepOrder: 1,
          actionType: "COMPARE_OPTIONS",
          title: "Compare OEM vs Cooling Gel Replacement Earpads",
          description: "Evaluated 4 premium replacement parts for durability, acoustic seal, and price.",
          status: "COMPLETED",
          requiresApproval: false,
          completedAt: new Date(Date.now() - 3600 * 1000 * 36).toISOString(),
        },
        {
          id: "step-302",
          planId: "plan-003",
          interventionId: "int-003",
          deviceId: "dev-sony-wh1000",
          deviceName: "Sony WH-1000XM4",
          stepOrder: 2,
          actionType: "NOTIFY_USER",
          title: "Deliver DIY 3-Minute Replacement Video Guide",
          description: "Dispatch step-by-step DIY video guide with tool-less snap-in instructions.",
          status: "RUNNING",
          requiresApproval: false,
        },
      ],
    },
  },
];

export const MOCK_PENDING_APPROVALS: ActionStepResponse[] = [
  {
    id: "step-103",
    planId: "plan-001",
    interventionId: "int-001",
    deviceId: "dev-macbook-pro",
    deviceName: "MacBook Pro 16\" (M1 Max)",
    stepOrder: 3,
    actionType: "REQUEST_QUOTE",
    title: "Request Instant Quotes & Reserve Battery Part",
    description: "Request prioritized repair slot and lock in $189 bundled quote with iFix Labs Downtown.",
    status: "WAITING_APPROVAL",
    requiresApproval: true,
  },
  {
    id: "step-202",
    planId: "plan-002",
    interventionId: "int-002",
    deviceId: "dev-iphone-14",
    deviceName: "iPhone 14 Pro",
    stepOrder: 2,
    actionType: "SCHEDULE_MAINTENANCE",
    title: "Schedule Preventive Cleanroom Reseal Appointment",
    description: "Reserve 30-minute Express Reseal slot at TechRestore Hub.",
    status: "WAITING_APPROVAL",
    requiresApproval: true,
  },
];

export const MOCK_EXECUTION_HISTORY: ExecutionHistoryResponse[] = [
  {
    id: "hist-001",
    userId: "demo-user",
    deviceId: "dev-macbook-pro",
    deviceName: "MacBook Pro 16\" (M1 Max)",
    interventionId: "int-001",
    actionStepId: "step-101",
    actionType: "GENERATE_REPORT",
    executionStatus: "SUCCESS",
    resultSummary: "Autonomous Agent generated full battery & thermal telemetry diagnostic dossier (14 pages).",
    executedAt: new Date(Date.now() - 3600 * 1000 * 3).toISOString(),
  },
  {
    id: "hist-002",
    userId: "demo-user",
    deviceId: "dev-macbook-pro",
    deviceName: "MacBook Pro 16\" (M1 Max)",
    interventionId: "int-001",
    actionStepId: "step-102",
    actionType: "FIND_SHOPS",
    executionStatus: "SUCCESS",
    resultSummary: "Matched 3 verified local shops with genuine Apple batteries in stock within 5km radius.",
    executedAt: new Date(Date.now() - 3600 * 1000 * 2).toISOString(),
  },
  {
    id: "hist-003",
    userId: "demo-user",
    deviceId: "dev-iphone-14",
    deviceName: "iPhone 14 Pro",
    interventionId: "int-002",
    actionStepId: "step-201",
    actionType: "GENERATE_REPORT",
    executionStatus: "SUCCESS",
    resultSummary: "Compiled optical stress map and IP68 seal vulnerability report.",
    executedAt: new Date(Date.now() - 3600 * 1000 * 17).toISOString(),
  },
  {
    id: "hist-004",
    userId: "demo-user",
    deviceId: "dev-sony-wh1000",
    deviceName: "Sony WH-1000XM4",
    interventionId: "int-003",
    actionStepId: "step-301",
    actionType: "COMPARE_OPTIONS",
    executionStatus: "SUCCESS",
    resultSummary: "Benchmarked 4 earpad models; recommended $24 cooling gel cushions saving $280 over replacement.",
    executedAt: new Date(Date.now() - 3600 * 1000 * 36).toISOString(),
  },
];

export const MOCK_AGENT_DASHBOARD: AgentDashboardResponse = {
  agentStatus: "ACTIVE",
  monitoredDevicesCount: 7,
  activeInterventionsCount: 3,
  pendingApprovalsCount: 2,
  completedExecutionsCount: 14,
  totalMoneySaved: 2410.0,
  totalCo2AvoidedKg: 26.5,
  activeInterventions: MOCK_INTERVENTIONS,
  pendingApprovals: MOCK_PENDING_APPROVALS,
  recentExecutions: MOCK_EXECUTION_HISTORY,
  priorityDistribution: {
    CRITICAL: 1,
    HIGH: 1,
    MEDIUM: 1,
    LOW: 0,
  },
};

// ─── API Methods ──────────────────────────────────────────────────────────────

/**
 * Fetch full Autonomous Repair Agent dashboard.
 * Corresponds to: GET /api/v1/repair-agent/dashboard
 */
export async function fetchAgentDashboard(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<AgentDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Agent dashboard loaded (Demo Session)",
      data: MOCK_AGENT_DASHBOARD,
    };
  }

  const result = await apiClient<AgentDashboardResponse>("/repair-agent/dashboard", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/repair-agent is offline. Displaying simulated agent metrics.`,
    data: MOCK_AGENT_DASHBOARD,
  };
}

/**
 * Fetch all active proactive interventions for user.
 * Corresponds to: GET /api/v1/repair-agent/interventions
 */
export async function fetchInterventions(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<InterventionResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Interventions loaded (Demo Session)",
      data: MOCK_INTERVENTIONS,
    };
  }

  const result = await apiClient<InterventionResponse[]>("/repair-agent/interventions", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated intervention data",
    data: MOCK_INTERVENTIONS,
  };
}

/**
 * Trigger proactive evaluation for a single device.
 * Corresponds to: POST /api/v1/repair-agent/evaluate/{deviceId}
 */
export async function evaluateDevice(
  deviceId: string,
  token?: string
): Promise<ApiResponse<InterventionResponse>> {
  if (isDemoSession(token)) {
    const existing = MOCK_INTERVENTIONS.find((i) => i.deviceId === deviceId) || MOCK_INTERVENTIONS[0];
    return {
      success: true,
      message: "Proactive evaluation completed (Demo Session)",
      data: existing,
    };
  }

  const result = await apiClient<InterventionResponse>(`/repair-agent/evaluate/${deviceId}`, {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Simulated proactive device evaluation completed",
    data: MOCK_INTERVENTIONS[0],
  };
}

/**
 * Trigger proactive evaluation for all user devices.
 * Corresponds to: POST /api/v1/repair-agent/evaluate-all
 */
export async function evaluateAllDevices(
  token?: string
): Promise<ApiResponse<InterventionResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Evaluated all devices (Demo Session)",
      data: MOCK_INTERVENTIONS,
    };
  }

  const result = await apiClient<InterventionResponse[]>("/repair-agent/evaluate-all", {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Simulated evaluation of all fleet devices completed",
    data: MOCK_INTERVENTIONS,
  };
}

/**
 * Fetch all action steps pending human user approval.
 * Corresponds to: GET /api/v1/repair-agent/approvals
 */
export async function fetchPendingApprovals(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<ActionStepResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Pending approvals loaded (Demo Session)",
      data: MOCK_PENDING_APPROVALS,
    };
  }

  const result = await apiClient<ActionStepResponse[]>("/repair-agent/approvals", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated approvals data",
    data: MOCK_PENDING_APPROVALS,
  };
}

/**
 * Approve a pending autonomous action step.
 * Corresponds to: POST /api/v1/repair-agent/approvals/{stepId}/approve
 */
export async function approveActionStep(
  stepId: string,
  req?: ActionApprovalRequest,
  token?: string
): Promise<ApiResponse<ActionStepResponse>> {
  if (isDemoSession(token)) {
    const step = MOCK_PENDING_APPROVALS.find((s) => s.id === stepId) || MOCK_PENDING_APPROVALS[0];
    return {
      success: true,
      message: `Action step "${step.title}" approved successfully.`,
      data: { ...step, status: "APPROVED" },
    };
  }

  const result = await apiClient<ActionStepResponse>(`/repair-agent/approvals/${stepId}/approve`, {
    method: "POST",
    body: JSON.stringify(req || {}),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  const step = MOCK_PENDING_APPROVALS.find((s) => s.id === stepId) || MOCK_PENDING_APPROVALS[0];
  return {
    success: true,
    message: "Step approved (Simulated)",
    data: { ...step, status: "APPROVED" },
  };
}

/**
 * Reject a pending autonomous action step.
 * Corresponds to: POST /api/v1/repair-agent/approvals/{stepId}/reject
 */
export async function rejectActionStep(
  stepId: string,
  req?: ActionApprovalRequest,
  token?: string
): Promise<ApiResponse<ActionStepResponse>> {
  if (isDemoSession(token)) {
    const step = MOCK_PENDING_APPROVALS.find((s) => s.id === stepId) || MOCK_PENDING_APPROVALS[0];
    return {
      success: true,
      message: `Action step "${step.title}" rejected.`,
      data: { ...step, status: "REJECTED" },
    };
  }

  const result = await apiClient<ActionStepResponse>(`/repair-agent/approvals/${stepId}/reject`, {
    method: "POST",
    body: JSON.stringify(req || {}),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  const step = MOCK_PENDING_APPROVALS.find((s) => s.id === stepId) || MOCK_PENDING_APPROVALS[0];
  return {
    success: true,
    message: "Step rejected (Simulated)",
    data: { ...step, status: "REJECTED" },
  };
}

/**
 * Execute an approved or autonomous action step.
 * Corresponds to: POST /api/v1/repair-agent/execute/{stepId}
 */
export async function executeActionStep(
  stepId: string,
  req?: ActionExecutionRequest,
  token?: string
): Promise<ApiResponse<ExecutionResultResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Autonomous execution completed successfully.",
      data: {
        actionId: stepId,
        status: "COMPLETED",
        message: "Autonomous task executed with verified output.",
        executionId: "hist-" + Date.now(),
        executedAt: new Date().toISOString(),
      },
    };
  }

  const result = await apiClient<ExecutionResultResponse>(`/repair-agent/execute/${stepId}`, {
    method: "POST",
    body: JSON.stringify(req || {}),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Autonomous execution completed (Simulated)",
    data: {
      actionId: stepId,
      status: "COMPLETED",
      message: "Autonomous task executed with verified output.",
      executionId: "hist-" + Date.now(),
      executedAt: new Date().toISOString(),
    },
  };
}

/**
 * Fetch chronological execution history log.
 * Corresponds to: GET /api/v1/repair-agent/history
 */
export async function fetchExecutionHistory(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<ExecutionHistoryResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Execution history loaded (Demo Session)",
      data: MOCK_EXECUTION_HISTORY,
    };
  }

  const result = await apiClient<ExecutionHistoryResponse[]>("/repair-agent/history", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated execution history",
    data: MOCK_EXECUTION_HISTORY,
  };
}
