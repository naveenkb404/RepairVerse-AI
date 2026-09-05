import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  TrustDashboardResponse,
  DecisionSummaryResponse,
  DecisionAuditResponse,
  GovernanceRuleResponse,
  GovernanceViolationResponse,
  UserAutonomyPreferencesResponse,
  UpdateAutonomyPreferencesRequest,
  DecisionFeedbackRequest,
} from "@/lib/types/trustEngine";

// ─── Demo / Fallback Mock Data ────────────────────────────────────────────────

export const MOCK_AUTONOMY_PREFERENCES: UserAutonomyPreferencesResponse = {
  id: "pref-001",
  userId: "usr-mock-01",
  allowAutonomousInterventions: true,
  allowAutoScheduling: false,
  allowProactiveAlerts: true,
  minConfidenceThreshold: 75,
  requireApprovalAboveCost: 3500.0,
  notificationStyle: "VERBOSE",
};

export const MOCK_GOVERNANCE_RULES: GovernanceRuleResponse[] = [
  {
    id: "gov-rule-01",
    ruleName: "Confidence Threshold Check",
    ruleCategory: "SAFETY",
    description: "Flags decisions with confidence score lower than 60% as requiring human confirmation.",
    appliesToSystems: "ALL",
    severity: "HIGH",
    thresholdValue: 60.0,
    isActive: true,
  },
  {
    id: "gov-rule-02",
    ruleName: "High Financial Impact Review",
    ruleCategory: "FINANCIAL",
    description: "Requires explicit user sign-off on interventions exceeding financial risk threshold.",
    appliesToSystems: "AUTONOMOUS_REPAIR_AGENT,DIGITAL_TWIN,PROACTIVE_INTERVENTION",
    severity: "MEDIUM",
    thresholdValue: 5000.0,
    isActive: true,
  },
  {
    id: "gov-rule-03",
    ruleName: "Conflicting Signals Safeguard",
    ruleCategory: "CONSISTENCY",
    description: "Detects contradictory recommendations across predictive, health, and twin models.",
    appliesToSystems: "ALL",
    severity: "HIGH",
    thresholdValue: 0.0,
    isActive: true,
  },
  {
    id: "gov-rule-04",
    ruleName: "Outdated Data Quarantine",
    ruleCategory: "DATA_INTEGRITY",
    description: "Flags recommendations based on telemetry older than 30 days.",
    appliesToSystems: "DEVICE_INTELLIGENCE,DIGITAL_TWIN",
    severity: "MEDIUM",
    thresholdValue: 30.0,
    isActive: true,
  },
  {
    id: "gov-rule-05",
    ruleName: "Autonomous Action Guardrail",
    ruleCategory: "AUTONOMY",
    description: "Ensures autonomous triggers strictly adhere to user autonomy constraints.",
    appliesToSystems: "AUTONOMOUS_REPAIR_AGENT",
    severity: "CRITICAL",
    thresholdValue: 0.0,
    isActive: true,
  },
  {
    id: "gov-rule-06",
    ruleName: "Critical Risk Escalation",
    ruleCategory: "SAFETY",
    description: "Escalates any high or critical risk action directly to safety queue.",
    appliesToSystems: "ALL",
    severity: "CRITICAL",
    thresholdValue: 80.0,
    isActive: true,
  },
];

export const MOCK_DECISIONS: DecisionSummaryResponse[] = [
  {
    id: "dec-001",
    deviceId: "dev-mock-01",
    sourceSystem: "AUTONOMOUS_REPAIR_AGENT",
    decisionType: "SCHEDULE_INTERVENTION",
    confidenceScore: 94,
    trustScore: 92,
    trustTier: "VERIFIED",
    riskLevel: "LOW",
    status: "ACTIVE",
    userReviewed: true,
    userFeedback: "AGREE",
    createdAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
  },
  {
    id: "dec-002",
    deviceId: "dev-mock-01",
    sourceSystem: "DIGITAL_TWIN",
    decisionType: "STRATEGY_OPTIMIZATION",
    confidenceScore: 91,
    trustScore: 88,
    trustTier: "VERIFIED",
    riskLevel: "LOW",
    status: "ACTIVE",
    userReviewed: false,
    userFeedback: null,
    createdAt: new Date(Date.now() - 1000 * 60 * 120).toISOString(),
  },
  {
    id: "dec-003",
    deviceId: "dev-mock-01",
    sourceSystem: "DEVICE_INTELLIGENCE",
    decisionType: "RISK_ASSESSMENT",
    confidenceScore: 78,
    trustScore: 76,
    trustTier: "RELIABLE",
    riskLevel: "MEDIUM",
    status: "ACTIVE",
    userReviewed: false,
    userFeedback: null,
    createdAt: new Date(Date.now() - 1000 * 60 * 360).toISOString(),
  },
  {
    id: "dec-004",
    deviceId: "dev-mock-02",
    sourceSystem: "PROACTIVE_INTERVENTION",
    decisionType: "PREVENTIVE_MAINTENANCE",
    confidenceScore: 68,
    trustScore: 64,
    trustTier: "CAUTION",
    riskLevel: "MEDIUM",
    status: "ACTIVE",
    userReviewed: true,
    userFeedback: "AGREE",
    createdAt: new Date(Date.now() - 1000 * 60 * 720).toISOString(),
  },
  {
    id: "dec-005",
    deviceId: "dev-mock-02",
    sourceSystem: "REPAIR_KNOWLEDGE_GRAPH",
    decisionType: "SIMILAR_CASE_MATCH",
    confidenceScore: 54,
    trustScore: 48,
    trustTier: "REVIEW_REQUIRED",
    riskLevel: "HIGH",
    status: "FLAGGED",
    userReviewed: false,
    userFeedback: null,
    createdAt: new Date(Date.now() - 1000 * 60 * 1440).toISOString(),
  },
];

export const MOCK_DECISION_DETAIL: DecisionAuditResponse = {
  id: "dec-001",
  userId: "usr-mock-01",
  deviceId: "dev-mock-01",
  sourceSystem: "AUTONOMOUS_REPAIR_AGENT",
  decisionType: "SCHEDULE_INTERVENTION",
  sourceRecordId: "act-plan-001",
  decisionOutput: "Auto-scheduled thermal paste replacement and fan cleaning intervention at certified Master Lab.",
  confidenceScore: 94,
  trustScore: 92,
  trustTier: "VERIFIED",
  riskLevel: "LOW",
  status: "ACTIVE",
  userReviewed: true,
  userFeedback: "AGREE",
  whyExplanation: "Thermal sensor telemetry detected junction temperature delta exceeding 22°C above baseline under moderate workloads, indicating compound degradation before irreversible thermal throttling.",
  howExplanation: "Synthesized multi-sensor telemetry (CPU junction + battery impedance) cross-referenced against 1,240 verified repair knowledge graph outcomes with 98.2% historical resolution fidelity.",
  whatIfExplanation: "If unaddressed, projected thermal runaway breaches safety throttling in 45 days, causing an estimated 38% battery capacity degradation and ₹4,200 secondary component stress.",
  impactExplanation: "Intervention executes at ₹1,200, saving ₹5,800 in avoided board replacements and eliminating 8.5 kg CO₂ manufacturing emissions.",
  trustBreakdown: {
    confidenceComponent: 94,
    evidenceDensityComponent: 90,
    systemReliabilityComponent: 95,
    governanceComplianceComponent: 100,
    dataFreshnessComponent: 95,
    confidenceWeight: 0.35,
    evidenceDensityWeight: 0.20,
    systemReliabilityWeight: 0.20,
    governanceComplianceWeight: 0.15,
    dataFreshnessWeight: 0.10,
    finalTrustScore: 92,
    trustTier: "VERIFIED",
  },
  evidenceTraces: [
    {
      id: "ev-001",
      evidenceType: "SENSOR_TELEMETRY",
      evidenceKey: "junction_temp_delta",
      evidenceValue: "+22.4°C over baseline (88.4°C peak)",
      evidenceWeight: 0.95,
      evidenceSource: "DEVICE_INTELLIGENCE_SENSORS",
    },
    {
      id: "ev-002",
      evidenceType: "HISTORICAL_BENCHMARK",
      evidenceKey: "kg_similar_cases",
      evidenceValue: "1,240 cases matched with 98.2% positive resolution",
      evidenceWeight: 0.90,
      evidenceSource: "REPAIR_KNOWLEDGE_GRAPH",
    },
    {
      id: "ev-003",
      evidenceType: "PREDICTIVE_HORIZON",
      evidenceKey: "twin_trajectory_month_3",
      evidenceValue: "Health degradation to 68% without preventive compound refresh",
      evidenceWeight: 0.88,
      evidenceSource: "DIGITAL_TWIN_SIMULATOR",
    },
    {
      id: "ev-004",
      evidenceType: "CONSENT_AUTHORIZATION",
      evidenceKey: "autonomy_preference_match",
      evidenceValue: "Cost ₹1,200 <= Max threshold ₹3,500; confidence 94% >= Min 75%",
      evidenceWeight: 1.0,
      evidenceSource: "USER_AUTONOMY_ENGINE",
    },
  ],
  violations: [],
  createdAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
  updatedAt: new Date(Date.now() - 1000 * 60 * 5).toISOString(),
};

export const MOCK_DASHBOARD: TrustDashboardResponse = {
  userId: "usr-mock-01",
  totalDecisions: 28,
  verifiedCount: 18,
  reliableCount: 7,
  cautionCount: 2,
  reviewRequiredCount: 1,
  averageTrustScore: 84.6,
  activeViolations: 1,
  decisionsReviewedByUser: 14,
  systemStats: [
    {
      sourceSystem: "AUTONOMOUS_REPAIR_AGENT",
      totalDecisions: 10,
      averageTrustScore: 90.2,
      averageConfidence: 92,
      dominantTrustTier: "VERIFIED",
      agreeCount: 8,
      disagreeCount: 0,
    },
    {
      sourceSystem: "DIGITAL_TWIN",
      totalDecisions: 8,
      averageTrustScore: 86.5,
      averageConfidence: 89,
      dominantTrustTier: "VERIFIED",
      agreeCount: 4,
      disagreeCount: 0,
    },
    {
      sourceSystem: "DEVICE_INTELLIGENCE",
      totalDecisions: 6,
      averageTrustScore: 78.4,
      averageConfidence: 80,
      dominantTrustTier: "RELIABLE",
      agreeCount: 2,
      disagreeCount: 0,
    },
    {
      sourceSystem: "REPAIR_KNOWLEDGE_GRAPH",
      totalDecisions: 4,
      averageTrustScore: 72.0,
      averageConfidence: 74,
      dominantTrustTier: "CAUTION",
      agreeCount: 0,
      disagreeCount: 1,
    },
  ],
  recentDecisions: MOCK_DECISIONS,
  activeViolationsList: [
    {
      id: "viol-001",
      decisionRecordId: "dec-005",
      ruleId: "gov-rule-01",
      ruleName: "Confidence Threshold Check",
      violationMessage: "Decision confidence score 54% is below safe automated intervention threshold 60%.",
      severity: "HIGH",
      autoResolved: false,
      createdAt: new Date(Date.now() - 1000 * 60 * 1440).toISOString(),
    },
  ],
  autonomyPreferences: MOCK_AUTONOMY_PREFERENCES,
};

// ─── API Client Methods ───────────────────────────────────────────────────────

/**
 * Get full Trust Engine Dashboard.
 * GET /api/v1/trust/dashboard
 */
export async function fetchTrustDashboard(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<TrustDashboardResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Trust Engine loaded (Demo Session)",
      data: MOCK_DASHBOARD,
    };
  }

  const result = await apiClient<TrustDashboardResponse>("/trust/dashboard", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/trust is offline. Displaying simulated trust dashboard.`,
    data: MOCK_DASHBOARD,
  };
}

/**
 * Get all decision summaries.
 * GET /api/v1/trust/decisions
 */
export async function fetchDecisionAuditLog(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DecisionSummaryResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_DECISIONS,
    };
  }

  const result = await apiClient<DecisionSummaryResponse[]>("/trust/decisions", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_DECISIONS,
  };
}

/**
 * Get decisions for a specific device.
 * GET /api/v1/trust/decisions/device/{deviceId}
 */
export async function fetchDeviceDecisions(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DecisionSummaryResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_DECISIONS.filter((d) => d.deviceId === deviceId || deviceId === "all"),
    };
  }

  const result = await apiClient<DecisionSummaryResponse[]>(`/trust/decisions/device/${deviceId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_DECISIONS,
  };
}

/**
 * Get single decision audit detail with evidence and breakdown.
 * GET /api/v1/trust/decisions/{decisionId}
 */
export async function fetchDecisionDetail(
  decisionId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DecisionAuditResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: { ...MOCK_DECISION_DETAIL, id: decisionId },
    };
  }

  const result = await apiClient<DecisionAuditResponse>(`/trust/decisions/${decisionId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: { ...MOCK_DECISION_DETAIL, id: decisionId },
  };
}

/**
 * Mark a decision as reviewed.
 * POST /api/v1/trust/decisions/{decisionId}/review
 */
export async function markDecisionReviewed(
  decisionId: string,
  token?: string
): Promise<ApiResponse<DecisionAuditResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Decision marked as reviewed (Demo Session)",
      data: { ...MOCK_DECISION_DETAIL, id: decisionId, userReviewed: true },
    };
  }

  const result = await apiClient<DecisionAuditResponse>(`/trust/decisions/${decisionId}/review`, {
    method: "POST",
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Decision marked as reviewed (Simulated)",
    data: { ...MOCK_DECISION_DETAIL, id: decisionId, userReviewed: true },
  };
}

/**
 * Submit feedback on a decision (AGREE / DISAGREE / UNSURE).
 * POST /api/v1/trust/decisions/{decisionId}/feedback
 */
export async function submitDecisionFeedback(
  decisionId: string,
  feedback: "AGREE" | "DISAGREE" | "UNSURE",
  token?: string
): Promise<ApiResponse<void>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Feedback submitted successfully (Demo Session)",
    };
  }

  const result = await apiClient<void>(`/trust/decisions/${decisionId}/feedback`, {
    method: "POST",
    body: JSON.stringify({ feedback } as DecisionFeedbackRequest),
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

/**
 * Fetch active governance rules.
 * GET /api/v1/trust/governance/rules
 */
export async function fetchGovernanceRules(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<GovernanceRuleResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_GOVERNANCE_RULES,
    };
  }

  const result = await apiClient<GovernanceRuleResponse[]>("/trust/governance/rules", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_GOVERNANCE_RULES,
  };
}

/**
 * Fetch user autonomy preferences.
 * GET /api/v1/trust/autonomy
 */
export async function fetchAutonomyPreferences(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<UserAutonomyPreferencesResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: MOCK_AUTONOMY_PREFERENCES,
    };
  }

  const result = await apiClient<UserAutonomyPreferencesResponse>("/trust/autonomy", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    data: MOCK_AUTONOMY_PREFERENCES,
  };
}

/**
 * Update user autonomy preferences.
 * PUT /api/v1/trust/autonomy
 */
export async function updateAutonomyPreferences(
  preferences: UpdateAutonomyPreferencesRequest,
  token?: string
): Promise<ApiResponse<UserAutonomyPreferencesResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Preferences saved successfully (Demo Session)",
      data: { ...MOCK_AUTONOMY_PREFERENCES, ...preferences },
    };
  }

  const result = await apiClient<UserAutonomyPreferencesResponse>("/trust/autonomy", {
    method: "PUT",
    body: JSON.stringify(preferences),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Preferences updated (Simulated)",
    data: { ...MOCK_AUTONOMY_PREFERENCES, ...preferences },
  };
}
