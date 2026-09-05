/**
 * Phase 35: Privacy-Preserving Federated Repair Intelligence & Continuous Learning Types.
 */

export interface ModelVersionResponse {
  id: string;
  modelName: string;
  version: string;
  parentVersion: string | null;
  status: "COLLECTING" | "AGGREGATED" | "VALIDATING" | "APPROVED" | "ACTIVE" | "SUPERSEDED" | "REJECTED" | "QUARANTINED" | string;
  trainingObservations: number;
  validationScore: number;
  trustScore: number;
  improvementPercentage: number;
  activatedAt: string | null;
  retiredAt: string | null;
  createdAt: string;
}

export interface LearningSignalResponse {
  id: string;
  batchId: string | null;
  signalType: string;
  deviceCategory: string;
  componentType: string;
  failureMode: string;
  repairAction: string;
  outcomeClass: string;
  aggregatedFrequency: number;
  successRate: number;
  averageCost: number;
  averageLifespanGain: number;
  sustainabilityScore: number;
  confidence: number;
  observationCount: number;
  createdAt: string;
}

export interface LearningBatchResponse {
  id: string;
  batchReference: string;
  sourceScope: string;
  anonymizedDeviceCount: number;
  anonymizedRepairCount: number;
  status: string;
  privacyLevel: string;
  validationScore: number;
  modelVersion: string;
  generatedAt: string;
  createdAt: string;
}

export interface ValidationResultResponse {
  id: string;
  modelVersionId: string;
  validationType: string;
  baselineScore: number;
  candidateScore: number;
  improvementScore: number;
  regressionDetected: boolean;
  confidence: number;
  decision: "ACCEPTED" | "REJECTED" | "QUARANTINED" | string;
  validatedAt: string;
}

export interface PrivacyAuditResponse {
  id: string;
  batchId: string | null;
  eventType: string;
  privacyRule: string;
  recordsProcessed: number;
  recordsFiltered: number;
  recordsAggregated: number;
  sensitiveFieldsRemoved: number;
  createdAt: string;
}

export interface LearningImpactResponse {
  recommendationAccuracyGain: number;
  repairSuccessImprovement: number;
  costPredictionStability: number;
  co2OptimizationImprovement: number;
  failurePredictionGain: number;
  totalDecisionsEnriched: number;
}

export interface ModelTrendPoint {
  version: string;
  validationScore: number;
  trustScore: number;
  improvementPercentage: number;
  observationCount: number;
  timestamp: string;
}

export interface SignalCategoryDistribution {
  category: string;
  signalCount: number;
  averageSuccessRate: number;
  averageConfidence: number;
}

export interface LearningTrendResponse {
  modelTrajectory: ModelTrendPoint[];
  categoryDistribution: SignalCategoryDistribution[];
  overallEcosystemGrowth: number;
}

export interface LearningModelComparisonResponse {
  currentModel: ModelVersionResponse;
  candidateModel: ModelVersionResponse;
  accuracyDelta: number;
  costStabilityDelta: number;
  trustScoreDelta: number;
  newObservationsCount: number;
  safeToActivate: boolean;
  validationBreakdown: ValidationResultResponse[];
  governanceRecommendations: string[];
}

export interface DeviceLearningProfileResponse {
  deviceId: string;
  deviceCategory: string;
  activeModelVersion: string;
  matchingEcosystemObservations: number;
  ecosystemSuccessRate: number;
  expectedLifespanGainMonths: number;
  expectedCostSavings: number;
  confidence: number;
  relevantSignals: LearningSignalResponse[];
  privacyNotice: string;
}

export interface LearningDashboardResponse {
  activeModelVersion: string;
  activeModelName: string;
  validationScore: number;
  trustScore: number;
  improvementPercentage: number;
  totalAnonymizedDevices: number;
  totalAnonymizedRepairs: number;
  activeLearningSignalsCount: number;
  validatedPatternsCount: number;
  privacyComplianceScore: number;
  lastLearningCycle: string;
  currentModel: ModelVersionResponse;
  modelHistory: ModelVersionResponse[];
  topSignals: LearningSignalResponse[];
  recentPrivacyAudits: PrivacyAuditResponse[];
  impactMetrics: LearningImpactResponse;
}

export interface LearningRunResponse {
  success: boolean;
  message: string;
  batchReference: string;
  candidateVersion: string;
  anonymizedOutcomesProcessed: number;
  validationScore: number;
  validationPassed: boolean;
  nextAction: string;
}

export interface LearningFeedbackRequest {
  modelVersion: string;
  decisionReference: string;
  feedbackType: "AGREE" | "DISAGREE" | "UNSURE";
  outcomeQuality?: number;
}
