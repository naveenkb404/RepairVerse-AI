/**
 * Phase 34: AI Decision Trust & Explainability Engine Type Definitions.
 */

export interface SystemTrustStats {
  sourceSystem: string;
  totalDecisions: number;
  averageTrustScore: number;
  averageConfidence: number;
  dominantTrustTier: string;
  agreeCount: number;
  disagreeCount: number;
}

export interface DecisionSummaryResponse {
  id: string;
  deviceId: string;
  sourceSystem: string;
  decisionType: string;
  confidenceScore: number;
  trustScore: number;
  trustTier: "VERIFIED" | "RELIABLE" | "CAUTION" | "REVIEW_REQUIRED" | string;
  riskLevel: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | string;
  status: string;
  userReviewed: boolean;
  userFeedback: "AGREE" | "DISAGREE" | "UNSURE" | null | string;
  createdAt: string;
}

export interface TrustScoreBreakdown {
  confidenceComponent: number;
  evidenceDensityComponent: number;
  systemReliabilityComponent: number;
  governanceComplianceComponent: number;
  dataFreshnessComponent: number;
  confidenceWeight: number;
  evidenceDensityWeight: number;
  systemReliabilityWeight: number;
  governanceComplianceWeight: number;
  dataFreshnessWeight: number;
  finalTrustScore: number;
  trustTier: "VERIFIED" | "RELIABLE" | "CAUTION" | "REVIEW_REQUIRED" | string;
}

export interface EvidenceTraceResponse {
  id: string;
  evidenceType: string;
  evidenceKey: string;
  evidenceValue: string;
  evidenceWeight: number;
  evidenceSource: string;
}

export interface GovernanceRuleResponse {
  id: string;
  ruleName: string;
  ruleCategory: string;
  description: string;
  appliesToSystems: string;
  severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | string;
  thresholdValue: number;
  isActive: boolean;
}

export interface GovernanceViolationResponse {
  id: string;
  decisionRecordId: string;
  ruleId: string;
  ruleName: string;
  violationMessage: string;
  severity: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | string;
  autoResolved: boolean;
  createdAt: string;
}

export interface UserAutonomyPreferencesResponse {
  id: string;
  userId: string;
  allowAutonomousInterventions: boolean;
  allowAutoScheduling: boolean;
  allowProactiveAlerts: boolean;
  minConfidenceThreshold: number;
  requireApprovalAboveCost: number;
  notificationStyle: string;
}

export interface UpdateAutonomyPreferencesRequest {
  allowAutonomousInterventions: boolean;
  allowAutoScheduling: boolean;
  allowProactiveAlerts: boolean;
  minConfidenceThreshold: number;
  requireApprovalAboveCost: number;
  notificationStyle: string;
}

export interface DecisionAuditResponse {
  id: string;
  userId: string;
  deviceId: string;
  sourceSystem: string;
  decisionType: string;
  sourceRecordId: string;
  decisionOutput: string;
  confidenceScore: number;
  trustScore: number;
  trustTier: "VERIFIED" | "RELIABLE" | "CAUTION" | "REVIEW_REQUIRED" | string;
  riskLevel: "LOW" | "MEDIUM" | "HIGH" | "CRITICAL" | string;
  status: string;
  userReviewed: boolean;
  userFeedback: "AGREE" | "DISAGREE" | "UNSURE" | null | string;
  whyExplanation: string;
  howExplanation: string;
  whatIfExplanation: string;
  impactExplanation: string;
  trustBreakdown: TrustScoreBreakdown;
  evidenceTraces: EvidenceTraceResponse[];
  violations: GovernanceViolationResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface TrustDashboardResponse {
  userId: string;
  totalDecisions: number;
  verifiedCount: number;
  reliableCount: number;
  cautionCount: number;
  reviewRequiredCount: number;
  averageTrustScore: number;
  activeViolations: number;
  decisionsReviewedByUser: number;
  systemStats: SystemTrustStats[];
  recentDecisions: DecisionSummaryResponse[];
  activeViolationsList: GovernanceViolationResponse[];
  autonomyPreferences: UserAutonomyPreferencesResponse;
}

export interface DecisionFeedbackRequest {
  feedback: "AGREE" | "DISAGREE" | "UNSURE";
}
