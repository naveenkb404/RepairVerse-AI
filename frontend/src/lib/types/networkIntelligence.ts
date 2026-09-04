// Phase 28 — Repair Network Intelligence TypeScript Types

export type QualityTier = "ELITE" | "EXCELLENT" | "TRUSTED" | "STANDARD" | "NEEDS_IMPROVEMENT";
export type TrustTier   = "EXCEPTIONAL" | "HIGH" | "ESTABLISHED" | "MODERATE" | "LOW";
export type AnomalySeverity = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
export type AnomalyStatus   = "OPEN" | "UNDER_REVIEW" | "RESOLVED" | "DISMISSED";
export type AnomalyType =
  | "SUSPICIOUS_PRICING"
  | "REVIEW_SPIKE"
  | "REVIEW_PATTERN"
  | "HIGH_REPEAT_REPAIRS"
  | "LOW_SUCCESS_RATE"
  | "UNUSUAL_CANCELLATION_RATE";

export interface QualityFactor {
  factor: string;
  score: number;
  weight: number;
  description: string;
}

export interface RepairShopQualityResponse {
  shopId: string;
  shopName: string;
  overallQualityScore: number;
  qualityTier: QualityTier;
  reliabilityScore: number;
  trustScore: number;
  customerSatisfactionScore: number;
  repairSuccessScore: number;
  priceFairnessScore: number;
  serviceSpeedScore: number;
  totalRepairs: number;
  successRate: number;
  repeatRepairRate: number;
  trend: string;
  factorBreakdown: QualityFactor[];
}

export interface TrustScoreResponse {
  shopId: string;
  trustScore: number;
  trustTier: TrustTier;
  positiveSignals: string[];
  riskSignals: string[];
  scoreBreakdown: Record<string, number>;
}

export interface RepairNetworkOverviewResponse {
  totalRepairShops: number;
  totalCompletedRepairs: number;
  networkSuccessRate: number;
  averageCustomerSatisfaction: number;
  averageTrustScore: number;
  eliteShops: number;
  shopsNeedingAttention: number;
  openAnomalies: number;
}

export interface RepairOutcomeAnalyticsResponse {
  totalRepairs: number;
  successfulRepairs: number;
  failedRepairs: number;
  repeatRepairs: number;
  successRate: number;
  failureRate: number;
  repeatRepairRate: number;
  averageRepairCost: number;
  averageTurnaroundDays: number;
}

export interface MarketplaceAnomalyResponse {
  id: string;
  shopId: string;
  shopName: string;
  anomalyType: AnomalyType;
  severity: AnomalySeverity;
  riskScore: number;
  description: string;
  status: AnomalyStatus;
  detectedAt: string;
}

export interface NetworkLeaderboardResponse {
  rank: number;
  shopId: string;
  shopName: string;
  qualityScore: number;
  trustScore: number;
  successRate: number;
  customerRating: number;
  trend: string;
  badge: string;
}

export interface CategoryQualityAnalyticsResponse {
  category: string;
  repairCount: number;
  successRate: number;
  averageCost: number;
  averageTurnaroundDays: number;
  bestPerformingShops: string[];
}

export interface QualityTrendResponse {
  period: string;
  qualityScore: number;
  trustScore: number;
  successRate: number;
  customerSatisfaction: number;
}

export interface NetworkHealthResponse {
  overallStatus: string;
  totalShops: number;
  eliteShops: number;
  excellentShops: number;
  trustedShops: number;
  standardShops: number;
  needsImprovementShops: number;
  openAnomalies: number;
  criticalAnomalies: number;
  platformTrustScore: number;
  platformQualityScore: number;
  platformSuccessRate: number;
}

export interface ShopRiskProfileResponse {
  shopId: string;
  shopName: string;
  riskScore: number;
  riskLevel: string;
  activeAnomalies: number;
  anomalies: MarketplaceAnomalyResponse[];
  riskSignals: string[];
  recommendation: string;
}

export interface ShopIntelligenceResponse {
  quality: RepairShopQualityResponse;
  trust: TrustScoreResponse;
}
