import { http } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  DeviceLifecycleAssessmentData,
  DelayImpactData,
} from "@/lib/types/lifecycle";

export const DEMO_LIFECYCLE_ASSESSMENT: DeviceLifecycleAssessmentData = {
  deviceId: "dev_sample_1",
  deviceName: "Personal iPhone 14 Pro",
  deviceCategory: "Smartphone",
  deviceAgeMonths: 20,
  predictedRemainingLifespanMonths: 10,
  expectedLifespanAfterMaintenanceMonths: 18,
  expectedLifespanAfterRepairMonths: 28,
  lifecycleExtensionPotentialMonths: 18,
  repairabilityScore: 82,
  replacementUrgency: "MEDIUM",
  cumulativeCarbonSavedKg: 42.5,
  cumulativeEwasteDivertedKg: 2.1,
  scenarios: [
    {
      scenarioKey: "DO_NOTHING",
      title: "Take No Action",
      description: "Hardware continues running under current thermal and component degradation curves.",
      estimatedCost: 0.0,
      estimatedLifespanMonths: 10,
      carbonImpactKg: 0.0,
      ewasteImpactKg: 0.0,
      riskLevel: "HIGH",
      recommendationTag: "NOT_RECOMMENDED",
    },
    {
      scenarioKey: "PREVENTIVE_MAINTENANCE",
      title: "Proactive Maintenance",
      description: "Servicing thermal system, cleaning ports, and conditioning battery controller.",
      estimatedCost: 45.0,
      estimatedLifespanMonths: 18,
      carbonImpactKg: 6.5,
      ewasteImpactKg: 0.1,
      riskLevel: "LOW",
      recommendationTag: "RECOMMENDED",
    },
    {
      scenarioKey: "REPAIR_NOW",
      title: "Prompt Component Repair",
      description: "Replace degraded wear assemblies immediately before secondary damage develops.",
      estimatedCost: 135.0,
      estimatedLifespanMonths: 28,
      carbonImpactKg: 24.8,
      ewasteImpactKg: 0.3,
      riskLevel: "LOW",
      recommendationTag: "HIGHLY_RECOMMENDED",
    },
    {
      scenarioKey: "DELAY_REPAIR",
      title: "Defer Repair (60-90 Days)",
      description: "Deferred repair leads to collateral power trace stress and 65% higher component costs.",
      estimatedCost: 220.0,
      estimatedLifespanMonths: 20,
      carbonImpactKg: 12.0,
      ewasteImpactKg: 0.2,
      riskLevel: "HIGH",
      recommendationTag: "HIGH_RISK",
    },
    {
      scenarioKey: "REPLACE",
      title: "Full Device Replacement",
      description: "Decommission current unit and purchase a modern replacement model.",
      estimatedCost: 799.0,
      estimatedLifespanMonths: 48,
      carbonImpactKg: -68.0,
      ewasteImpactKg: -0.45,
      riskLevel: "LOW",
      recommendationTag: "DISCOURAGED",
    },
  ],
  evaluatedAt: new Date().toISOString(),
  isDemo: true,
};

export const DEMO_DELAY_IMPACT: DelayImpactData = {
  deviceId: "dev_sample_1",
  deviceName: "Personal iPhone 14 Pro",
  baselineRepairCost: 85.0,
  currentRiskLevel: "MEDIUM",
  primaryFaultRisk: "Battery Capacity Degradation & Thermal Throttling",
  projections: [
    {
      delayDays: 7,
      timeHorizonLabel: "7 Days (Short-term)",
      projectedCost: 91.8,
      costEscalationPercentage: 8.0,
      projectedRiskLevel: "MEDIUM",
      secondaryDamageProbability: 12.0,
      lifecycleReductionMonths: 1,
      additionalCarbonPenaltyKg: 0.4,
      consequenceSummary: "Minor thermal compound dry-out or microscopic contact oxidation. Servicing remains straightforward.",
    },
    {
      delayDays: 30,
      timeHorizonLabel: "30 Days (Medium-term)",
      projectedCost: 114.75,
      costEscalationPercentage: 35.0,
      projectedRiskLevel: "HIGH",
      secondaryDamageProbability: 48.0,
      lifecycleReductionMonths: 3,
      additionalCarbonPenaltyKg: 2.1,
      consequenceSummary: "Battery swelling risk increases; power management IC experiences over-voltage strain from erratic current.",
    },
    {
      delayDays: 90,
      timeHorizonLabel: "90 Days (Long-term)",
      projectedCost: 151.3,
      costEscalationPercentage: 78.0,
      projectedRiskLevel: "CRITICAL",
      secondaryDamageProbability: 82.0,
      lifecycleReductionMonths: 8,
      additionalCarbonPenaltyKg: 5.6,
      consequenceSummary: "High probability of multi-layer PCB delamination, trace fracture, or catastrophic display controller failure.",
    },
  ],
  urgencyRecommendation: "Proactive repair recommended within 14 days. Early intervention prevents a 35% to 78% escalation in repair costs.",
  simulatedAt: new Date().toISOString(),
  isDemo: true,
};

export async function fetchDeviceLifecycle(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<DeviceLifecycleAssessmentData>> {
  if (isDemoSession(token)) {
    return createDemoResponse({
      ...DEMO_LIFECYCLE_ASSESSMENT,
      deviceId,
    });
  }

  const res = await http.get<DeviceLifecycleAssessmentData>(
    `/lifecycle/device/${deviceId}`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(
      { ...DEMO_LIFECYCLE_ASSESSMENT, deviceId },
      res.message ?? "Backend offline — sample lifecycle intelligence loaded."
    );
  }

  return res;
}

export async function fetchDelayImpact(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<DelayImpactData>> {
  if (isDemoSession(token)) {
    return createDemoResponse({
      ...DEMO_DELAY_IMPACT,
      deviceId,
    });
  }

  const res = await http.get<DelayImpactData>(
    `/lifecycle/device/${deviceId}/delay-impact`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(
      { ...DEMO_DELAY_IMPACT, deviceId },
      res.message ?? "Backend offline — sample delay simulation loaded."
    );
  }

  return res;
}
