export type RiskLevel = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW" | "HEALTHY";

export interface ScoringFactor {
  factor: string;
  score: number;
  maxScore: number;
  status: "CRITICAL" | "HIGH" | "MEDIUM" | "HEALTHY";
  description: string;
}

export interface DevicePredictionData {
  deviceId: string;
  deviceName: string;
  category: string;
  brand: string;
  predictionScore: number;
  riskLevel: RiskLevel;
  daysToFailureEstimate: number | null;
  primaryFaultType: string;
  recommendedActions: string[];
  scoringBreakdown: ScoringFactor[];
  estimatedRepairCost: number;
  preventiveSavings: number;
  co2SavingsKg: number;
  confidenceScore: number;
  isDemo: boolean;
  evaluatedAt: string;
}

export interface FaultPatternItem {
  id: string;
  deviceCategory: string | null;
  deviceBrand: string | null;
  faultType: string;
  description: string;
  minDeviceAgeYears: number;
  healthScoreThreshold: number;
  riskWeight: number;
  typicalCostMin: number | null;
  typicalCostMax: number | null;
  preventiveActions: string[];
  isActive: boolean;
}

export interface MaintenanceRecommendationItem {
  id: string;
  title: string;
  description: string;
  priority: "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";
  category: string;
  estimatedCost: string;
  estimatedTime: string;
  impact: string;
  steps: string[];
}

export interface RiskDistributionEntry {
  riskLevel: RiskLevel;
  count: number;
  percentage: number;
}

export interface PredictiveFleetOverviewData {
  totalDevices: number;
  criticalDevices: number;
  highRiskDevices: number;
  mediumRiskDevices: number;
  lowRiskDevices: number;
  healthyDevices: number;
  averagePredictionScore: number;
  totalEstimatedRepairCost: number;
  totalPreventiveSavings: number;
  totalCo2SavingsKg: number;
  riskDistribution: RiskDistributionEntry[];
  isDemo: boolean;
}
