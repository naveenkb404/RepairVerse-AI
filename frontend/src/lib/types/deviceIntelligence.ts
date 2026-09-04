// Phase 30: AI Repair Ecosystem Intelligence & Personalized Device Decision Engine Types

export type IntelligenceTier = 'EXCEPTIONAL' | 'HEALTHY' | 'STABLE' | 'AT_RISK' | 'CRITICAL';

export type RecommendedAction =
  | 'CONTINUE_USING'
  | 'MONITOR'
  | 'MAINTENANCE_REQUIRED'
  | 'REPAIR_NOW'
  | 'PROFESSIONAL_SERVICE'
  | 'REFURBISH'
  | 'REPLACE'
  | 'RECYCLE';

export type ScenarioType =
  | 'CONTINUE_USING'
  | 'MONITOR'
  | 'MAINTENANCE'
  | 'REPAIR'
  | 'PROFESSIONAL_SERVICE'
  | 'REFURBISH'
  | 'REPLACE'
  | 'RECYCLE';

export type AlertSeverity = 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type AlertType =
  | 'FAILURE_RISK'
  | 'MAINTENANCE_REQUIRED'
  | 'REPAIR_RECOMMENDED'
  | 'COST_ESCALATION'
  | 'END_OF_LIFE'
  | 'SUSTAINABILITY_OPPORTUNITY';

export interface IntelligenceScoreBreakdown {
  healthReliabilityScore: number;
  failureRiskScore: number;
  repairEconomicsScore: number;
  maintenanceStatusScore: number;
  longevityScore: number;
  sustainabilityScore: number;
  repairHistoryScore: number;
}

export interface DecisionFactor {
  factorName: string;
  score: number;
  weight: number;
  impact: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';
  explanation: string;
}

export interface SmartDecision {
  recommendedAction: RecommendedAction;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  title: string;
  explanation: string;
  estimatedCost: number;
  expectedBenefit: string;
}

export interface DeviceScenario {
  scenarioType: ScenarioType;
  title: string;
  estimatedCost: number;
  estimatedLifespanMonths: number;
  estimatedCo2Impact: number;
  estimatedSavings: number;
  intelligenceScore: number;
  recommendation: string;
  pros: string[];
  cons: string[];
}

export interface DeviceIntelligenceAlertResponse {
  id: string;
  deviceId: string;
  deviceName: string;
  alertType: AlertType;
  severity: AlertSeverity;
  title: string;
  message: string;
  recommendedAction?: string;
  isRead: boolean;
  createdAt: string;
}

export interface DeviceIntelligenceTimelineItem {
  id: string;
  eventType: string;
  title: string;
  description: string;
  impactBadge?: string;
  timestamp: string;
}

export interface DeviceIntelligenceEvaluationRequest {
  forceReevaluation?: boolean;
}

export interface DeviceScenarioSimulationRequest {
  preferredScenario?: string;
  customBudget?: number;
  targetLifespanMonths?: number;
  prioritizeSustainability?: boolean;
}

export interface DeviceDecisionSnapshotResponse {
  id: string;
  deviceId: string;
  intelligenceScore: number;
  recommendedAction: RecommendedAction;
  decisionConfidence: number;
  healthScore?: number;
  failureRiskScore?: number;
  economicScore?: number;
  summary?: string;
  createdAt: string;
}

export interface DeviceIntelligenceResponse {
  deviceId: string;
  deviceName: string;
  category: string;
  brand: string;
  model: string;
  intelligenceScore: number;
  intelligenceTier: IntelligenceTier;
  recommendedAction: RecommendedAction;
  decisionConfidence: number;
  summary: string;
  evaluatedAt: string;
  scoreBreakdown: IntelligenceScoreBreakdown;
  decisionFactors: DecisionFactor[];
  smartDecision: SmartDecision;
  scenarios: DeviceScenario[];
  activeAlerts: DeviceIntelligenceAlertResponse[];
}
