export type ScenarioKey =
  | "DO_NOTHING"
  | "PREVENTIVE_MAINTENANCE"
  | "REPAIR_NOW"
  | "DELAY_REPAIR"
  | "REPLACE";

export type ReplacementUrgency = "LOW" | "MEDIUM" | "HIGH" | "IMMEDIATE";

export interface LifecycleScenarioData {
  scenarioKey: ScenarioKey;
  title: string;
  description: string;
  estimatedCost: number;
  estimatedLifespanMonths: number;
  carbonImpactKg: number;
  ewasteImpactKg: number;
  riskLevel: string;
  recommendationTag: "RECOMMENDED" | "HIGHLY_RECOMMENDED" | "VIABLE" | "HIGH_RISK" | "NOT_RECOMMENDED" | "CONSIDER" | "DISCOURAGED";
}

export interface DeviceLifecycleAssessmentData {
  deviceId: string;
  deviceName: string;
  deviceCategory: string;
  deviceAgeMonths: number;
  predictedRemainingLifespanMonths: number;
  expectedLifespanAfterMaintenanceMonths: number;
  expectedLifespanAfterRepairMonths: number;
  lifecycleExtensionPotentialMonths: number;
  repairabilityScore: number;
  replacementUrgency: ReplacementUrgency;
  cumulativeCarbonSavedKg: number;
  cumulativeEwasteDivertedKg: number;
  scenarios: LifecycleScenarioData[];
  evaluatedAt: string;
  isDemo?: boolean;
}

export interface DelayProjectionData {
  delayDays: number;
  timeHorizonLabel: string;
  projectedCost: number;
  costEscalationPercentage: number;
  projectedRiskLevel: string;
  secondaryDamageProbability: number;
  lifecycleReductionMonths: number;
  additionalCarbonPenaltyKg: number;
  consequenceSummary: string;
}

export interface DelayImpactData {
  deviceId: string;
  deviceName: string;
  baselineRepairCost: number;
  currentRiskLevel: string;
  primaryFaultRisk: string;
  projections: DelayProjectionData[];
  urgencyRecommendation: string;
  simulatedAt: string;
  isDemo?: boolean;
}
