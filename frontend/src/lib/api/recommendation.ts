import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import type { RepairRecommendation, RecommendationResponse } from "@/lib/types/recommendation";

/**
 * Reference sample recommendation data for offline fallback
 */
export const SAMPLE_RECOMMENDATION: RepairRecommendation = {
  id: "rec_demo_1",
  diagnosisId: "diag_demo_1",
  action: "REPAIR",
  repairScore: 92,
  replaceScore: 28,
  diagnosisReport: {
    id: "diag_demo_1",
    symptoms: "Cracked glass display, touch erratic in top left corner, battery drains fast.",
    probableIssue: "OLED Panel Fracture & Lithium Battery Degradation",
    confidenceScore: 94,
    repairDifficulty: "Moderate",
    repairTime: "1-2 hours",
    repairCost: 85,
    imageUrl:
      "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80",
  },
  decision: {
    repairScore: 92,
    replaceScore: 28,
    recommendation: "REPAIR",
    moneySaved: 640,
    carbonSaved: 6.5,
    rationale:
      "Self-repair is strongly recommended. Replacing the OLED assembly and battery costs $85, saving $640 over purchasing a new flagship smartphone while preventing 6.5 kg of CO₂ emissions.",
  },
  plan: {
    summary:
      "Standard OLED assembly and battery replacement procedure. Requires prying pick, Pentalobe screwdriver, and heat gun to soften display adhesive.",
    steps: [
      {
        stepNumber: 1,
        title: "Power Off & Heat Bezel Display Adhesive",
        description:
          "Completely shut down device. Use a heat gun or iOpener around perimeter edges for 2 minutes to soften screen adhesive bond.",
        safetyNote: "Do not exceed 80°C heat surface temperature to avoid thermal battery stress.",
        estimatedMinutes: 10,
      },
      {
        stepNumber: 2,
        title: "Apply Suction Cup & Pry Display Assembly",
        description:
          "Attach suction cup near bottom speaker edge. Insert thin opening pick underneath glass lip and slice around left and right edges.",
        safetyNote: "Slice shallowly on right edge to avoid tearing digitizer sensor flex ribbon cables.",
        estimatedMinutes: 15,
      },
      {
        stepNumber: 3,
        title: "Disconnect Battery & Display Connector Bracket",
        description:
          "Unscrew tri-point EMI shield bracket screws. Use plastic spudger to pop off battery flex connector first, followed by display flex connectors.",
        safetyNote: "Always disconnect battery cable before removing display connectors.",
        estimatedMinutes: 10,
      },
      {
        stepNumber: 4,
        title: "Install Replacement OLED Panel & Battery Pack",
        description:
          "Install new battery pull-tabs and secure battery pack. Connect replacement OLED assembly flex cables and replace EMI shield bracket.",
        estimatedMinutes: 20,
      },
      {
        stepNumber: 5,
        title: "Post-Repair Testing & Adhesive Sealing",
        description:
          "Connect power and test display touch registration and charging cycles before final adhesive clamping.",
        estimatedMinutes: 10,
      },
    ],
    parts: [
      {
        name: "OEM Super Retina XDR OLED Display Assembly",
        quantity: 1,
        estimatedCost: 65,
        partNumber: "APL-13P-DISP",
      },
      {
        name: "Replacement Li-ion Battery Pack (3095 mAh)",
        quantity: 1,
        estimatedCost: 20,
        partNumber: "APL-13P-BATT",
      },
    ],
    tools: [
      {
        name: "P2 Pentalobe Screwdriver",
        category: "Precision Drivers",
        essential: true,
      },
      {
        name: "Tri-point Y000 Precision Screwdriver",
        category: "Precision Drivers",
        essential: true,
      },
      {
        name: "Heat Gun / Thermal Heating Pad",
        category: "Heating Equipment",
        essential: true,
      },
      {
        name: "Suction Cup & Plastic Opening Picks",
        category: "Prying Tools",
        essential: true,
      },
    ],
  },
  createdAt: new Date().toISOString(),
};

/**
 * Repair Recommendation API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoints:
 * - POST /api/v1/repair-analysis
 * - GET /api/v1/repair-guide/{issue}
 */
export async function fetchRepairRecommendation(
  diagnosisId: string,
  token?: string,
  signal?: AbortSignal
): Promise<RecommendationResponse> {
  const result = await apiClient<RepairRecommendation>("/repair-analysis", {
    method: "POST",
    body: { diagnosisId },
    token,
    signal,
  });

  if (result.success && result.data) {
    return {
      success: true,
      message: result.message || "Repair recommendation generated successfully",
      data: result.data,
    };
  }

  // Graceful sample fallback for demo presentation
  return {
    success: true,
    message: `Backend recommendation service at ${API_BASE_URL}/repair-analysis is offline. Displaying calculated sample recommendations.`,
    data: SAMPLE_RECOMMENDATION,
  };
}
