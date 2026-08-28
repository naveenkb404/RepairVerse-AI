export interface RiskFactorExplanation {
  factorName: string;
  severity: "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";
  explanation: string;
  impactOnLifespan: string;
}

export interface ComponentWearDetail {
  component: string;
  status: string;
  wearMechanisms: string;
  estimatedRemainingLife: string;
}

export interface DeviceRiskExplanationResponse {
  deviceId: string;
  deviceName: string;
  predictionScore: number;
  riskLevel: "CRITICAL" | "HIGH" | "MEDIUM" | "LOW" | "HEALTHY";
  executiveSummary: string;
  rootCauseAnalysis: string;
  keyContributingFactors: RiskFactorExplanation[];
  componentWearAssessment: ComponentWearDetail[];
  economicJustification: string;
  urgencyRating: string;
  safetyPrecautions: string[];
  preventiveActionRoadmap: string[];
  modelUsed: string;
  isDemo: boolean;
  generatedAt: string;
}

export interface DiagnosisExplanationResponse {
  diagnosisId: string;
  deviceName: string;
  probableIssue: string;
  confidenceScore: number;
  visualEvidenceAnalysis: string;
  symptomCorrelation: string;
  differentialDiagnoses: string[];
  repairFeasibilityRationale: string;
  requiredToolsRationale: string[];
  safetyWarningContext: string;
  modelUsed: string;
  isDemo: boolean;
  generatedAt: string;
}

export interface RecommendationExplanationResponse {
  recommendationId: string;
  deviceName: string;
  recommendedAction: string;
  estimatedRepairCost: number;
  estimatedDeviceValue: number;
  costBenefitRationale: string;
  lifespanExtensionAnalysis: string;
  environmentalTradeoffNarrative: string;
  salvageValueAssessment: string;
  riskAdjustedNextSteps: string[];
  modelUsed: string;
  isDemo: boolean;
  generatedAt: string;
}

export interface SustainabilityNarrativeResponse {
  userId: string;
  totalCo2SavedKg: number;
  totalEwasteReducedKg: number;
  totalMoneySaved: number;
  devicesExtended: number;
  impactHeadline: string;
  storytellingNarrative: string;
  tangibleRealWorldEquivalents: string;
  circularEconomyAchievements: string[];
  futureImpactProjection: string;
  modelUsed: string;
  isDemo: boolean;
  generatedAt: string;
}
