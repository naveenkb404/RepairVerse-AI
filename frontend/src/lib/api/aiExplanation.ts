import { apiClient } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  DeviceRiskExplanationResponse,
  DiagnosisExplanationResponse,
  RecommendationExplanationResponse,
  SustainabilityNarrativeResponse,
} from "@/lib/types/aiExplanation";

// ─── Demo reference datasets ──────────────────────────────────────────────────

export const DEMO_DEVICE_RISK_EXPLANATION: DeviceRiskExplanationResponse = {
  deviceId: "dev_sample_1",
  deviceName: "Personal iPhone 14 Pro",
  predictionScore: 84,
  riskLevel: "LOW",
  executiveSummary:
    "Your iPhone 14 Pro is in strong operational health (84/100) with minimal immediate failure probability. Subtle battery cell degradation has commenced but remains well within manufacturer safety thresholds.",
  rootCauseAnalysis:
    "Anode-cathode lithium electrolyte resistance has increased slightly across ~380 equivalent charge cycles. Thermal dissipation through the chassis remains uniform, and NAND flash wear is at less than 4% of total rated Terabytes Written (TBW).",
  keyContributingFactors: [
    {
      factorName: "Battery Capacity Retention",
      severity: "LOW",
      explanation: "Current maximum capacity is 89%. Nominal voltage remains stable under burst processor loads.",
      impactOnLifespan: "Estimated 18-24 months of reliable autonomy before capacity dips below 80%.",
    },
    {
      factorName: "Thermal Cycling",
      severity: "LOW",
      explanation: "Chassis dissipates thermal loads effectively; maximum recorded junction temp is 41°C.",
      impactOnLifespan: "Zero semiconductor junction fatigue detected.",
    },
    {
      factorName: "Charging Port Mechanical Wear",
      severity: "MEDIUM",
      explanation: "Lightning socket pin contacts show micro-friction oxidation from daily insertion.",
      impactOnLifespan: "May require preventative de-oxidation cleaning in 12 months.",
    },
  ],
  componentWearAssessment: [
    {
      component: "Lithium-Ion Battery",
      status: "Good (89%)",
      wearMechanisms: "Solid Electrolyte Interphase (SEI) layer growth",
      estimatedRemainingLife: "18-24 months",
    },
    {
      component: "OLED Display Subpixels",
      status: "Optimal",
      wearMechanisms: "Uniform luminescence decay, zero ghosting",
      estimatedRemainingLife: "36+ months",
    },
    {
      component: "A16 Bionic Logic Board",
      status: "Excellent",
      wearMechanisms: "No solder micro-cracks or capacitor ripple",
      estimatedRemainingLife: "48+ months",
    },
  ],
  economicJustification:
    "Investing $0 in routine software battery optimization today defers a $89 battery replacement for up to 2 years, saving 100% of avoidable emergency maintenance costs.",
  urgencyRating: "Low Priority — Re-evaluate in 6 months",
  safetyPrecautions: [
    "Avoid charging on soft insulated bedding to prevent ambient heat accumulation above 35°C",
    "Use certified USB-PD compliant power bricks to regulate inrush current",
  ],
  preventiveActionRoadmap: [
    "Enable 'Optimized Battery Charging' in iOS Settings",
    "Inspect and gently de-lint charging port using an anti-static tool",
    "Schedule automated quarterly health checkups in RepairVerse",
  ],
  modelUsed: "Gemini 1.5 Flash (Explainable AI)",
  isDemo: true,
  generatedAt: new Date().toISOString(),
};

export const DEMO_DIAGNOSIS_EXPLANATION: DiagnosisExplanationResponse = {
  diagnosisId: "diag_sample_1",
  deviceName: "iPhone 14 Pro",
  probableIssue: "OLED Subpixel Retention & Display Digitizer Degradation",
  confidenceScore: 91,
  visualEvidenceAnalysis:
    "High-resolution image analysis detected micro-fracturing along the lower-right bezel perimeter accompanied by localized capacitive touch dead-zones near the home indicator flex trace.",
  symptomCorrelation:
    "Reported phantom touches and intermittent flicker directly match physical digitizer substrate separation caused by drop impact micro-shear forces.",
  differentialDiagnoses: [
    "GPU display driver framebuffer timing fault (Excluded — software render is artifact-free)",
    "Display flex ribbon connector seating issue (Possible secondary contributor — 15% likelihood)",
  ],
  repairFeasibilityRationale:
    "High DIY and local shop feasibility: Modular screen assembly replacement requires basic pentalobe and tri-point drivers without microsoldering.",
  requiredToolsRationale: [
    "Precision P2 Pentalobe & Y000 Tri-point screwdrivers to remove chassis security screws",
    "Suction clamp & nylon spudger to separate display adhesive without puncturing battery",
    "Replacement waterproof perimeter sealing gasket to restore IP68 resistance",
  ],
  safetyWarningContext:
    "Lithium pouch battery sits directly below display flex; ensure plastic tools are used exclusively to prevent accidental cell puncture or short circuits.",
  modelUsed: "Gemini 1.5 Flash (Explainable AI)",
  isDemo: true,
  generatedAt: new Date().toISOString(),
};

export const DEMO_RECOMMENDATION_EXPLANATION: RecommendationExplanationResponse = {
  recommendationId: "rec_sample_1",
  deviceName: "Work MacBook Pro 16",
  recommendedAction: "REPAIR",
  estimatedRepairCost: 140.0,
  estimatedDeviceValue: 1850.0,
  costBenefitRationale:
    "Repairing the damaged thermal module and internal cooling fan costs $140 (~7.5% of device market value). Purchasing a replacement MacBook Pro of equivalent compute tier costs ~$2,200, resulting in immediate net savings of $2,060.",
  lifespanExtensionAnalysis:
    "Replacing degraded thermal compound and heatsink restores baseline junction cooling, lowering peak thermals by ~18°C and extending total operational lifespan by 3.5+ years.",
  environmentalTradeoffNarrative:
    "Keeping this high-performance M2 Max machine active prevents ~280 kg of upstream supply-chain CO₂ emissions generated during new silicon wafer fabrication and chassis CNC milling.",
  salvageValueAssessment:
    "Post-repair hardware retains over 85% of its secondary market value on certified refurbished marketplaces.",
  riskAdjustedNextSteps: [
    "Order OEM-spec liquid metal or phase-change thermal interface pad",
    "Follow static-safe ESD workspace protocol during heatsink detachment",
    "Execute Cinebench stress benchmark post-repair to verify thermal dissipation delta",
  ],
  modelUsed: "Gemini 1.5 Flash (Explainable AI)",
  isDemo: true,
  generatedAt: new Date().toISOString(),
};

export const DEMO_SUSTAINABILITY_NARRATIVE: SustainabilityNarrativeResponse = {
  userId: "usr_demo",
  totalCo2SavedKg: 127.4,
  totalEwasteReducedKg: 8.4,
  totalMoneySaved: 2340.0,
  devicesExtended: 4,
  impactHeadline: "Pioneering Circular Electronics: 127.4 kg of CO₂ Emissions Prevented!",
  storytellingNarrative:
    "Through active hardware stewardship, you have extended the operational lifetime of 4 core computing devices. By choosing precision repair over premature replacement, you have directly bypassed the carbon-heavy manufacturing cycles that account for over 80% of consumer electronics' lifetime emissions.",
  tangibleRealWorldEquivalents:
    "Your 127.4 kg of prevented CO₂ is equivalent to planting 5.8 mature urban trees that sequester carbon for a full year, or completely offsetting 1,060 kilometers of passenger car travel.",
  circularEconomyAchievements: [
    "Zero-Waste Vanguard Badge",
    "Right-to-Repair Champion",
    "Carbon Mitigation Pioneer",
  ],
  futureImpactProjection:
    "Maintaining regular preventive servicing will divert an additional 15 kg of toxic heavy metal e-waste and avert another 350 kg of greenhouse gases over the next 24 months.",
  modelUsed: "Gemini 1.5 Flash (Explainable AI)",
  isDemo: true,
  generatedAt: new Date().toISOString(),
};

// ─── API Client Functions ─────────────────────────────────────────────────────

/**
 * Fetch Explainable AI deep dive for a device's predictive maintenance assessment.
 */
export async function fetchDeviceRiskExplanation(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<DeviceRiskExplanationResponse>> {
  if (isDemoSession(token)) {
    const demo = { ...DEMO_DEVICE_RISK_EXPLANATION, deviceId };
    return createDemoResponse(demo, "Explainable AI predictive assessment (Demo Mode)");
  }

  const res = await apiClient<DeviceRiskExplanationResponse>(
    `/ai-intelligence/device-prediction/${deviceId}`,
    { token }
  );
  if (res.success && res.data) {
    return res;
  }

  const fallback = { ...DEMO_DEVICE_RISK_EXPLANATION, deviceId };
  return createDemoResponse(fallback, "Explainable AI predictive assessment (Demo Mode)");
}

/**
 * Fetch Explainable AI breakdown for a diagnosis report.
 */
export async function fetchDiagnosisExplanation(
  diagnosisId: string,
  token?: string | null
): Promise<ApiResponse<DiagnosisExplanationResponse>> {
  if (isDemoSession(token)) {
    const demo = { ...DEMO_DIAGNOSIS_EXPLANATION, diagnosisId };
    return createDemoResponse(demo, "Explainable AI diagnosis breakdown (Demo Mode)");
  }

  const res = await apiClient<DiagnosisExplanationResponse>(
    `/ai-intelligence/diagnosis/${diagnosisId}`,
    { token }
  );
  if (res.success && res.data) {
    return res;
  }

  const fallback = { ...DEMO_DIAGNOSIS_EXPLANATION, diagnosisId };
  return createDemoResponse(fallback, "Explainable AI diagnosis breakdown (Demo Mode)");
}

/**
 * Fetch Explainable AI rationale for a repair vs replace recommendation.
 */
export async function fetchRecommendationExplanation(
  recommendationId: string,
  token?: string | null
): Promise<ApiResponse<RecommendationExplanationResponse>> {
  if (isDemoSession(token)) {
    const demo = { ...DEMO_RECOMMENDATION_EXPLANATION, recommendationId };
    return createDemoResponse(demo, "Explainable AI recommendation rationale (Demo Mode)");
  }

  const res = await apiClient<RecommendationExplanationResponse>(
    `/ai-intelligence/recommendation/${recommendationId}`,
    { token }
  );
  if (res.success && res.data) {
    return res;
  }

  const fallback = { ...DEMO_RECOMMENDATION_EXPLANATION, recommendationId };
  return createDemoResponse(fallback, "Explainable AI recommendation rationale (Demo Mode)");
}

/**
 * Fetch Explainable AI sustainability storytelling narrative.
 */
export async function fetchSustainabilityNarrative(
  token?: string | null
): Promise<ApiResponse<SustainabilityNarrativeResponse>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_SUSTAINABILITY_NARRATIVE, "Sustainability narrative (Demo Mode)");
  }

  const res = await apiClient<SustainabilityNarrativeResponse>(
    "/ai-intelligence/sustainability",
    { token }
  );
  if (res.success && res.data) {
    return res;
  }

  return createDemoResponse(DEMO_SUSTAINABILITY_NARRATIVE, "Sustainability narrative (Demo Mode)");
}
