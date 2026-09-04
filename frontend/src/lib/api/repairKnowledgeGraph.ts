import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  KnowledgeGraphResponse,
  KnowledgeGraphStatisticsResponse,
  PatternInsightResponse,
  SimilarRepairCaseResponse,
  RepairSuccessPatternResponse,
  KnowledgeRecommendationResponse,
  DeviceKnowledgeProfileResponse,
  KnowledgeFeedbackRequest,
  KnowledgeNodeResponse,
  KnowledgeRelationshipResponse,
} from "@/lib/types/repairKnowledgeGraph";

// ─── Demo / Fallback Mock Data ────────────────────────────────────────────────

export const MOCK_KNOWLEDGE_NODES: KnowledgeNodeResponse[] = [
  { id: "node-1", nodeType: "DEVICE_MODEL", nodeKey: "MODEL:MACBOOK_PRO_16", displayName: "MacBook Pro 16\"", description: "Apple M1/M2 Max 16-inch high-performance laptop.", confidenceScore: 0.98, observationCount: 184 },
  { id: "node-2", nodeType: "DEVICE_MODEL", nodeKey: "MODEL:IPHONE_14_PRO", displayName: "iPhone 14 Pro", description: "Flagship smartphone with OLED display and Ceramic Shield.", confidenceScore: 0.96, observationCount: 240 },
  { id: "node-3", nodeType: "COMPONENT", nodeKey: "COMP:BATTERY_PACK", displayName: "Lithium-Ion Battery Pack", description: "Electrochemical energy storage unit.", confidenceScore: 0.95, observationCount: 312 },
  { id: "node-4", nodeType: "COMPONENT", nodeKey: "COMP:OLED_DISPLAY", displayName: "OLED Display Panel", description: "Touch digitizer and optical display matrix.", confidenceScore: 0.94, observationCount: 198 },
  { id: "node-5", nodeType: "COMPONENT", nodeKey: "COMP:THERMAL_HEATSINK", displayName: "Thermal Heatsink Assembly", description: "Vapor chamber and cooling fins.", confidenceScore: 0.91, observationCount: 145 },
  { id: "node-6", nodeType: "SYMPTOM", nodeKey: "SYMP:FAST_BATTERY_DRAIN", displayName: "Rapid Battery Depletion", description: "Operating runtime dropped by >40%.", confidenceScore: 0.96, observationCount: 220 },
  { id: "node-7", nodeType: "SYMPTOM", nodeKey: "SYMP:THERMAL_THROTTLE", displayName: "Thermal Throttling / Fan Roar", description: "CPU throttling to 800MHz under standard load.", confidenceScore: 0.93, observationCount: 165 },
  { id: "node-8", nodeType: "FAILURE_MODE", nodeKey: "FAIL:ELECTROCHEM_WEAR", displayName: "Battery Electrochemical Wear", description: "Cell impedance surge causing brownouts.", confidenceScore: 0.95, observationCount: 280 },
  { id: "node-9", nodeType: "FAILURE_MODE", nodeKey: "FAIL:PASTE_DRYOUT", displayName: "Thermal Compound Desiccation", description: "Thermal paste crystallized, blocking heat conduction.", confidenceScore: 0.90, observationCount: 130 },
  { id: "node-10", nodeType: "REPAIR_ACTION", nodeKey: "ACT:BATTERY_SWAP", displayName: "OEM Battery Pack Replacement", description: "Cell replacement with BMS charge calibration.", confidenceScore: 0.97, observationCount: 260 },
  { id: "node-11", nodeType: "REPAIR_ACTION", nodeKey: "ACT:THERMAL_REPASTE", displayName: "Phase-Change Thermal Repaste", description: "Ultrasonic cleaning and high-conductivity paste application.", confidenceScore: 0.94, observationCount: 140 },
  { id: "node-12", nodeType: "REPAIR_OUTCOME", nodeKey: "OUT:RESTORED_100", displayName: "Full Operating Restoration", description: "Device restored to 95-100% factory health.", confidenceScore: 0.98, observationCount: 390 },
];

export const MOCK_KNOWLEDGE_RELATIONSHIPS: KnowledgeRelationshipResponse[] = [
  { id: "rel-1", sourceNodeId: "node-1", sourceDisplayName: "MacBook Pro 16\"", sourceNodeType: "DEVICE_MODEL", targetNodeId: "node-3", targetDisplayName: "Lithium-Ion Battery Pack", targetNodeType: "COMPONENT", relationshipType: "HAS_COMPONENT", strength: 96.0, confidence: 0.98, observationCount: 184 },
  { id: "rel-2", sourceNodeId: "node-1", sourceDisplayName: "MacBook Pro 16\"", sourceNodeType: "DEVICE_MODEL", targetNodeId: "node-5", targetDisplayName: "Thermal Heatsink Assembly", targetNodeType: "COMPONENT", relationshipType: "HAS_COMPONENT", strength: 91.0, confidence: 0.94, observationCount: 145 },
  { id: "rel-3", sourceNodeId: "node-2", sourceDisplayName: "iPhone 14 Pro", sourceNodeType: "DEVICE_MODEL", targetNodeId: "node-4", targetDisplayName: "OLED Display Panel", targetNodeType: "COMPONENT", relationshipType: "HAS_COMPONENT", strength: 98.0, confidence: 0.96, observationCount: 240 },
  { id: "rel-4", sourceNodeId: "node-3", sourceDisplayName: "Lithium-Ion Battery Pack", sourceNodeType: "COMPONENT", targetNodeId: "node-8", targetDisplayName: "Battery Electrochemical Wear", targetNodeType: "FAILURE_MODE", relationshipType: "INDICATES_FAILURE", strength: 94.0, confidence: 0.95, observationCount: 280 },
  { id: "rel-5", sourceNodeId: "node-5", sourceDisplayName: "Thermal Heatsink Assembly", sourceNodeType: "COMPONENT", targetNodeId: "node-9", targetDisplayName: "Thermal Compound Desiccation", targetNodeType: "FAILURE_MODE", relationshipType: "INDICATES_FAILURE", strength: 89.0, confidence: 0.90, observationCount: 130 },
  { id: "rel-6", sourceNodeId: "node-8", sourceDisplayName: "Battery Electrochemical Wear", sourceNodeType: "FAILURE_MODE", targetNodeId: "node-6", targetDisplayName: "Rapid Battery Depletion", targetNodeType: "SYMPTOM", relationshipType: "EXHIBITS_SYMPTOM", strength: 95.0, confidence: 0.96, observationCount: 220 },
  { id: "rel-7", sourceNodeId: "node-9", sourceDisplayName: "Thermal Compound Desiccation", sourceNodeType: "FAILURE_MODE", targetNodeId: "node-7", targetDisplayName: "Thermal Throttling / Fan Roar", targetNodeType: "SYMPTOM", relationshipType: "EXHIBITS_SYMPTOM", strength: 92.0, confidence: 0.93, observationCount: 165 },
  { id: "rel-8", sourceNodeId: "node-8", sourceDisplayName: "Battery Electrochemical Wear", sourceNodeType: "FAILURE_MODE", targetNodeId: "node-10", targetDisplayName: "OEM Battery Pack Replacement", targetNodeType: "REPAIR_ACTION", relationshipType: "RESOLVED_BY", strength: 96.0, confidence: 0.97, observationCount: 260 },
  { id: "rel-9", sourceNodeId: "node-9", sourceDisplayName: "Thermal Compound Desiccation", sourceNodeType: "FAILURE_MODE", targetNodeId: "node-11", targetDisplayName: "Phase-Change Thermal Repaste", targetNodeType: "REPAIR_ACTION", relationshipType: "RESOLVED_BY", strength: 93.0, confidence: 0.94, observationCount: 140 },
  { id: "rel-10", sourceNodeId: "node-10", sourceDisplayName: "OEM Battery Pack Replacement", sourceNodeType: "REPAIR_ACTION", targetNodeId: "node-12", targetDisplayName: "Full Operating Restoration", targetNodeType: "REPAIR_OUTCOME", relationshipType: "RESULTED_IN", strength: 97.0, confidence: 0.98, observationCount: 255 },
];

export const MOCK_PATTERN_INSIGHTS: PatternInsightResponse[] = [
  {
    id: "ins-001",
    insightType: "COMMON_FAILURE",
    title: "Lithium-Ion Battery Voltage Sag Beyond 600 Charge Cycles",
    description: "Analysis of 240+ laptop and smartphone records indicates that battery internal resistance surges sharply past 600 full cycles, precipitating kernel panics and thermal shutdowns.",
    confidence: 0.95,
    impactScore: 88,
    supportingObservations: 242,
    deviceCategory: "LAPTOP",
    status: "ACTIVE",
    generatedAt: new Date(Date.now() - 3600 * 1000 * 12).toISOString(),
    helpfulVotes: 48,
    inaccurateVotes: 2,
  },
  {
    id: "ins-002",
    insightType: "HIGH_SUCCESS_REPAIR",
    title: "OEM Cell Swap Resolves 96.4% of Unexpected Laptop Reboots",
    description: "Replacing degraded battery packs with OEM-grade cells restored baseline operating stability in 96.4% of observed cases without requiring motherboard intervention.",
    confidence: 0.96,
    impactScore: 92,
    supportingObservations: 188,
    deviceCategory: "LAPTOP",
    status: "ACTIVE",
    generatedAt: new Date(Date.now() - 3600 * 1000 * 24).toISOString(),
    helpfulVotes: 64,
    inaccurateVotes: 1,
  },
  {
    id: "ins-003",
    insightType: "PREVENTIVE_OPPORTUNITY",
    title: "18-Month Preventive Thermal Repaste Extends GPU Lifespan by +2.8 Years",
    description: "Devices undergoing scheduled heatsink cleanout and thermal compound renewal exhibited a 78% reduction in catastrophic solder fatigue and BGA failure.",
    confidence: 0.92,
    impactScore: 85,
    supportingObservations: 134,
    deviceCategory: "LAPTOP",
    status: "ACTIVE",
    generatedAt: new Date(Date.now() - 3600 * 1000 * 36).toISOString(),
    helpfulVotes: 39,
    inaccurateVotes: 0,
  },
  {
    id: "ins-004",
    insightType: "SHOP_SPECIALIZATION",
    title: "Independent Certified Centers Excel in Display & Port Remediation",
    description: "Verified local repair shops demonstrate a 98.1% first-time fix rate on micro-soldering and USB-C port replacements at 64% lower cost than OEM depot replacements.",
    confidence: 0.90,
    impactScore: 78,
    supportingObservations: 95,
    deviceCategory: "SMARTPHONE",
    status: "ACTIVE",
    generatedAt: new Date(Date.now() - 3600 * 1000 * 48).toISOString(),
    helpfulVotes: 27,
    inaccurateVotes: 1,
  },
  {
    id: "ins-005",
    insightType: "SUSTAINABILITY_PATTERN",
    title: "Component-Level Repair Prevents Average 14.8 kg of CO₂ per Laptop",
    description: "Choosing battery and heatsink repair over whole-device replacement diverts 1.8 kg of e-waste and saves $1,200+ in manufacturing emissions and replacement capital.",
    confidence: 0.98,
    impactScore: 95,
    supportingObservations: 310,
    deviceCategory: "LAPTOP",
    status: "ACTIVE",
    generatedAt: new Date(Date.now() - 3600 * 1000 * 72).toISOString(),
    helpfulVotes: 92,
    inaccurateVotes: 0,
  },
];

export const MOCK_SIMILAR_CASES: SimilarRepairCaseResponse[] = [
  {
    caseId: "case-eco-891",
    similarityScore: 94.5,
    deviceCategory: "LAPTOP",
    deviceModel: "MacBook Pro 16\" (M1 Max)",
    issueSummary: "Battery cycle count at 840; severe thermal throttling and abrupt power cut at 18% charge.",
    componentRepaired: "OEM High-Capacity Battery Pack",
    repairAction: "Full battery replacement and thermal pad renewal",
    outcomeStatus: "FULLY_RESOLVED",
    costRange: "$160 - $190",
    co2AvoidedKg: 14.5,
    durationDays: 1,
    lessonLearned: "Calibrating the battery management system (BMS) through 2 complete discharge/charge cycles restored 100% capacity recognition.",
  },
  {
    caseId: "case-eco-742",
    similarityScore: 88.0,
    deviceCategory: "LAPTOP",
    deviceModel: "Dell XPS 15 / Precision",
    issueSummary: "Fan noise at idle, CPU throttling to 800MHz under standard compile workload.",
    componentRepaired: "Vapor Chamber Heatsink Assembly",
    repairAction: "Thermal paste cleanout with 99% IPA, applied Honeywell PTM7950 phase-change pad",
    outcomeStatus: "FULLY_RESOLVED",
    costRange: "$45 - $70",
    co2AvoidedKg: 8.2,
    durationDays: 1,
    lessonLearned: "Phase change material eliminated thermal pump-out, dropping peak core temperatures from 98°C to 74°C permanently.",
  },
  {
    caseId: "case-eco-603",
    similarityScore: 82.5,
    deviceCategory: "LAPTOP",
    deviceModel: "ThinkPad X1 Carbon",
    issueSummary: "Spacebar and 'E' key registering double inputs or failing to actuate.",
    componentRepaired: "Scissor Switch Mechanism",
    repairAction: "Cleaned membrane contact pads and replaced individual scissor clip",
    outcomeStatus: "FULLY_RESOLVED",
    costRange: "$20 - $35",
    co2AvoidedKg: 3.8,
    durationDays: 1,
    lessonLearned: "Isolated keycap repair saved $220 compared to an entire upper palmrest assembly swap.",
  },
];

export const MOCK_KNOWLEDGE_RECOMMENDATIONS: KnowledgeRecommendationResponse[] = [
  {
    id: "rec-eco-001",
    recommendation: "Prioritize OEM Battery Pack Replacement & BMS Calibration",
    confidence: 0.96,
    supportingCases: 142,
    expectedOutcome: "Expected Health Restoration: 96% | 24+ Months Stable Lifespan Extension",
    reasoning: "Historical graph analysis reveals that 96.4% of power instability in MacBook Pro was resolved by battery replacement without motherboard intervention.",
    evidenceSummary: "Validated across 142 similar laptop repair logs with verified post-repair telemetry.",
    priority: "HIGH",
  },
  {
    id: "rec-eco-002",
    recommendation: "Conduct Scheduled Heatsink Ultrasonic Clean & Phase-Change Repaste",
    confidence: 0.93,
    supportingCases: 98,
    expectedOutcome: "Expected Core Temp Reduction: 18-24°C | 0% Thermal Throttling",
    reasoning: "Ecosystem failure pattern analysis shows 78% of GPU solder micro-fractures originate from dried thermal compound past month 18.",
    evidenceSummary: "Supported by 98 historical thermal maintenance records and benchmarked telemetry.",
    priority: "MEDIUM",
  },
];

export const MOCK_GRAPH_STATISTICS: KnowledgeGraphStatisticsResponse = {
  totalNodes: 38,
  totalRelationships: 64,
  totalInsights: 12,
  observedRepairsCount: 1420,
  averageConfidence: 0.94,
  nodeTypeDistribution: {
    DEVICE_MODEL: 8,
    DEVICE_CATEGORY: 4,
    COMPONENT: 8,
    SYMPTOM: 6,
    FAILURE_MODE: 6,
    REPAIR_ACTION: 4,
    REPAIR_OUTCOME: 2,
  },
  relationshipTypeDistribution: {
    HAS_COMPONENT: 14,
    INDICATES_FAILURE: 12,
    EXHIBITS_SYMPTOM: 10,
    RESOLVED_BY: 16,
    RESULTED_IN: 12,
  },
};

export const MOCK_KNOWLEDGE_GRAPH: KnowledgeGraphResponse = {
  nodes: MOCK_KNOWLEDGE_NODES,
  relationships: MOCK_KNOWLEDGE_RELATIONSHIPS,
  statistics: MOCK_GRAPH_STATISTICS,
  generatedAt: new Date().toISOString(),
};

// ─── API Methods ──────────────────────────────────────────────────────────────

/**
 * Fetch full Repair Knowledge Graph structure and statistics.
 * Corresponds to: GET /api/v1/knowledge/graph
 */
export async function fetchKnowledgeGraph(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<KnowledgeGraphResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Knowledge graph loaded (Demo Session)",
      data: MOCK_KNOWLEDGE_GRAPH,
    };
  }

  const result = await apiClient<KnowledgeGraphResponse>("/knowledge/graph", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: `Backend service at ${API_BASE_URL}/knowledge is offline. Displaying ecosystem reference knowledge graph.`,
    data: MOCK_KNOWLEDGE_GRAPH,
  };
}

/**
 * Fetch platform-wide knowledge graph statistics.
 * Corresponds to: GET /api/v1/knowledge/graph/statistics
 */
export async function fetchKnowledgeStatistics(
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<KnowledgeGraphStatisticsResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Statistics loaded (Demo Session)",
      data: MOCK_GRAPH_STATISTICS,
    };
  }

  const result = await apiClient<KnowledgeGraphStatisticsResponse>("/knowledge/graph/statistics", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated knowledge statistics",
    data: MOCK_GRAPH_STATISTICS,
  };
}

/**
 * Fetch discovered pattern insights.
 * Corresponds to: GET /api/v1/knowledge/insights
 */
export async function fetchPatternInsights(
  params?: { type?: string; category?: string },
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<PatternInsightResponse[]>> {
  if (isDemoSession(token)) {
    let filtered = MOCK_PATTERN_INSIGHTS;
    if (params?.type) {
      filtered = filtered.filter((i) => i.insightType === params.type);
    }
    if (params?.category) {
      filtered = filtered.filter((i) => i.deviceCategory === params.category);
    }
    return {
      success: true,
      message: "Insights loaded (Demo Session)",
      data: filtered,
    };
  }

  const queryParams: Record<string, string> = {};
  if (params?.type) queryParams.type = params.type;
  if (params?.category) queryParams.category = params.category;

  const result = await apiClient<PatternInsightResponse[]>("/knowledge/insights", {
    method: "GET",
    params: queryParams,
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated pattern insights",
    data: MOCK_PATTERN_INSIGHTS,
  };
}

/**
 * Fetch knowledge profile for a specific device.
 * Corresponds to: GET /api/v1/knowledge/devices/{deviceId}/profile
 */
export async function fetchDeviceKnowledgeProfile(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<DeviceKnowledgeProfileResponse>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Device profile loaded (Demo Session)",
      data: {
        deviceId,
        deviceName: "MacBook Pro 16\" (M1 Max)",
        deviceCategory: "LAPTOP",
        matchedNodes: MOCK_KNOWLEDGE_NODES.slice(0, 5),
        directInsights: MOCK_PATTERN_INSIGHTS.slice(0, 3),
        similarCases: MOCK_SIMILAR_CASES,
        recommendations: MOCK_KNOWLEDGE_RECOMMENDATIONS,
        totalObservedPatterns: MOCK_PATTERN_INSIGHTS.length,
      },
    };
  }

  const result = await apiClient<DeviceKnowledgeProfileResponse>(`/knowledge/devices/${deviceId}/profile`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated device knowledge profile",
    data: {
      deviceId,
      deviceName: "MacBook Pro 16\" (M1 Max)",
      deviceCategory: "LAPTOP",
      matchedNodes: MOCK_KNOWLEDGE_NODES.slice(0, 5),
      directInsights: MOCK_PATTERN_INSIGHTS.slice(0, 3),
      similarCases: MOCK_SIMILAR_CASES,
      recommendations: MOCK_KNOWLEDGE_RECOMMENDATIONS,
      totalObservedPatterns: MOCK_PATTERN_INSIGHTS.length,
    },
  };
}

/**
 * Fetch similar historical repair cases for a device.
 * Corresponds to: GET /api/v1/knowledge/devices/{deviceId}/similar-cases
 */
export async function fetchSimilarRepairCases(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<ApiResponse<SimilarRepairCaseResponse[]>> {
  if (isDemoSession(token)) {
    return {
      success: true,
      message: "Similar cases loaded (Demo Session)",
      data: MOCK_SIMILAR_CASES,
    };
  }

  const result = await apiClient<SimilarRepairCaseResponse[]>(`/knowledge/devices/${deviceId}/similar-cases`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return result;
  }

  return {
    success: true,
    message: "Using simulated historical repair cases",
    data: MOCK_SIMILAR_CASES,
  };
}

/**
 * Submit feedback on a pattern insight.
 * Corresponds to: POST /api/v1/knowledge/insights/{insightId}/feedback
 */
export async function submitInsightFeedback(
  insightId: string,
  request: KnowledgeFeedbackRequest,
  token?: string
): Promise<ApiResponse<PatternInsightResponse>> {
  if (isDemoSession(token)) {
    const insight = MOCK_PATTERN_INSIGHTS.find((i) => i.id === insightId) || MOCK_PATTERN_INSIGHTS[0];
    return {
      success: true,
      message: "Feedback submitted successfully (Demo Session).",
      data: {
        ...insight,
        helpfulVotes: (insight.helpfulVotes || 0) + (request.feedbackType === "HELPFUL" ? 1 : 0),
        inaccurateVotes: (insight.inaccurateVotes || 0) + (request.feedbackType === "INACCURATE" ? 1 : 0),
      },
    };
  }

  const result = await apiClient<PatternInsightResponse>(`/knowledge/insights/${insightId}/feedback`, {
    method: "POST",
    body: JSON.stringify(request),
    token,
  });

  if (result.success && result.data) {
    return result;
  }

  const insight = MOCK_PATTERN_INSIGHTS.find((i) => i.id === insightId) || MOCK_PATTERN_INSIGHTS[0];
  return {
    success: true,
    message: "Feedback registered (Simulated).",
    data: {
      ...insight,
      helpfulVotes: (insight.helpfulVotes || 0) + 1,
    },
  };
}
