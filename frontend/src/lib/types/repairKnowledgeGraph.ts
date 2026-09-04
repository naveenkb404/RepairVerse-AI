// Phase 32: AI Repair Knowledge Graph & Ecosystem Learning Intelligence Types

export type KnowledgeNodeType =
  | 'DEVICE_MODEL'
  | 'DEVICE_CATEGORY'
  | 'COMPONENT'
  | 'SYMPTOM'
  | 'FAILURE_MODE'
  | 'REPAIR_ACTION'
  | 'REPAIR_PART'
  | 'REPAIR_SHOP'
  | 'REPAIR_OUTCOME';

export type KnowledgeRelationshipType =
  | 'HAS_COMPONENT'
  | 'EXHIBITS_SYMPTOM'
  | 'INDICATES_FAILURE'
  | 'CAUSED_BY'
  | 'RESOLVED_BY'
  | 'REQUIRES_PART'
  | 'PERFORMED_BY'
  | 'RESULTED_IN'
  | 'SIMILAR_TO'
  | 'PREVENTS';

export type PatternInsightType =
  | 'COMMON_FAILURE'
  | 'HIGH_SUCCESS_REPAIR'
  | 'RECURRING_COMPONENT_ISSUE'
  | 'SHOP_SPECIALIZATION'
  | 'COST_EFFECTIVE_REPAIR'
  | 'PREVENTIVE_OPPORTUNITY'
  | 'SUSTAINABILITY_PATTERN';

export type FeedbackType = 'HELPFUL' | 'NOT_HELPFUL' | 'ACCURATE' | 'INACCURATE';

export interface KnowledgeNodeResponse {
  id: string;
  nodeType: KnowledgeNodeType | string;
  nodeKey: string;
  displayName: string;
  description?: string;
  metadata?: string;
  confidenceScore: number;
  observationCount: number;
}

export interface KnowledgeRelationshipResponse {
  id: string;
  sourceNodeId: string;
  sourceDisplayName: string;
  sourceNodeType: string;
  targetNodeId: string;
  targetDisplayName: string;
  targetNodeType: string;
  relationshipType: KnowledgeRelationshipType | string;
  strength: number;
  confidence: number;
  observationCount: number;
}

export interface PatternInsightResponse {
  id: string;
  insightType: PatternInsightType | string;
  title: string;
  description: string;
  confidence: number;
  impactScore: number;
  supportingObservations: number;
  deviceCategory?: string;
  status: string;
  generatedAt: string;
  helpfulVotes?: number;
  inaccurateVotes?: number;
}

export interface SimilarRepairCaseResponse {
  caseId: string;
  similarityScore: number;
  deviceCategory: string;
  deviceModel: string;
  issueSummary: string;
  componentRepaired: string;
  repairAction: string;
  outcomeStatus: string;
  costRange: string;
  co2AvoidedKg: number;
  durationDays: number;
  lessonLearned: string;
}

export interface RepairSuccessPatternResponse {
  failureMode: string;
  repairAction: string;
  successRate: number;
  averageCost: number;
  observedCases: number;
  bestPractice: string;
}

export interface KnowledgeRecommendationResponse {
  id: string;
  recommendation: string;
  confidence: number;
  supportingCases: number;
  expectedOutcome: string;
  reasoning: string;
  evidenceSummary: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | string;
}

export interface KnowledgeGraphStatisticsResponse {
  totalNodes: number;
  totalRelationships: number;
  totalInsights: number;
  observedRepairsCount: number;
  averageConfidence: number;
  nodeTypeDistribution: Record<string, number>;
  relationshipTypeDistribution: Record<string, number>;
}

export interface KnowledgeGraphResponse {
  nodes: KnowledgeNodeResponse[];
  relationships: KnowledgeRelationshipResponse[];
  statistics: KnowledgeGraphStatisticsResponse;
  generatedAt: string;
}

export interface DeviceKnowledgeProfileResponse {
  deviceId: string;
  deviceName: string;
  deviceCategory: string;
  matchedNodes: KnowledgeNodeResponse[];
  directInsights: PatternInsightResponse[];
  similarCases: SimilarRepairCaseResponse[];
  recommendations: KnowledgeRecommendationResponse[];
  totalObservedPatterns: number;
}

export interface KnowledgeFeedbackRequest {
  feedbackType: FeedbackType;
  rating?: number;
  comment?: string;
}
