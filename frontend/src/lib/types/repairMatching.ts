/**
 * Phase 27 — Intelligent Repair Matching & Marketplace Intelligence Types
 */

export type MatchLevel =
  | "EXCELLENT_MATCH"
  | "GREAT_MATCH"
  | "GOOD_MATCH"
  | "FAIR_MATCH"
  | "LOW_MATCH";

export type RecommendationCategory =
  | "BEST_OVERALL"
  | "BEST_VALUE"
  | "FASTEST_REPAIR"
  | "MOST_TRUSTED"
  | "MOST_SUSTAINABLE"
  | "NEAREST";

export type PriceClassification =
  | "EXCELLENT_VALUE"
  | "GOOD_VALUE"
  | "FAIR_PRICE"
  | "ABOVE_MARKET"
  | "OVERPRICED"
  | "SUSPICIOUSLY_LOW";

export interface MatchingFactor {
  factorName: string;
  score: number;
  maxScore: number;
  weightPercent: number;
  explanation: string;
  positiveImpact: boolean;
}

export interface RepairMatchExplanation {
  summary: string;
  keyReasons: string[];
  compatibilityLevel: MatchLevel;
  recommendations: string[];
}

export interface RepairShopMatchResponse {
  shopId: string;
  shopName: string;
  address: string;
  latitude: number | null;
  longitude: number | null;
  phone: string;
  email: string;
  hours: string;
  rating: number;
  reviewCount: number;
  verificationStatus: string;
  verificationLevel: string;
  distanceKm: number | null;
  overallScore: number;
  matchLevel: MatchLevel;
  rank: number;
  factors: MatchingFactor[];
  explanation: RepairMatchExplanation;
  estimatedCost: number;
  turnaroundHours: number;
  warrantyDays: number;
  trustScore: number;
  isEcoCertified: boolean;
  isDemo: boolean;
}

export interface CategoryRecommendation {
  category: RecommendationCategory;
  categoryLabel: string;
  shop: RepairShopMatchResponse;
  highlightReason: string;
}

export interface SmartRecommendationResponse {
  deviceId: string;
  deviceName: string;
  recommendations: CategoryRecommendation[];
  topMatches: RepairShopMatchResponse[];
  totalEvaluated: number;
  generatedAt: string;
  isDemo: boolean;
}

export interface CompareShopsRequest {
  shopIds: string[];
  deviceId?: string;
}

export interface ShopComparisonMetric {
  metricKey: string;
  metricName: string;
  description: string;
  shopValues: Record<string, string>;
  winnerShopId: string;
}

export interface RepairMarketplaceComparison {
  shops: RepairShopMatchResponse[];
  metrics: ShopComparisonMetric[];
  bestOverallShopId: string;
  bestValueShopId: string;
  fastestShopId: string;
  mostTrustedShopId: string;
  mostSustainableShopId: string;
  nearestShopId: string;
  comparisonSummary: string;
  isDemo: boolean;
}

export interface QuoteIntelligenceResponse {
  quoteId: string;
  repairShopId: string;
  shopName: string;
  estimatedCost: number;
  partsCost: number;
  laborCost: number;
  marketAverageCost: number;
  costDifference: number;
  costDifferencePercent: number;
  classification: PriceClassification;
  classificationLabel: string;
  priceFairnessScore: number;
  insights: string[];
  warnings: string[];
  isDemo: boolean;
}

export interface UserMarketplaceInsights {
  totalShopsCompared: number;
  totalQuotesRequested: number;
  totalQuotesAccepted: number;
  averageRepairCost: number;
  totalPotentialSavings: number;
  bestValueOpportunities: string[];
  recentMatches: RepairShopMatchResponse[];
  isDemo: boolean;
}

export interface HighPerformingShop {
  shopId: string;
  shopName: string;
  trustScore: number;
  averageRating: number;
  totalQuotesAccepted: number;
  acceptanceRate: number;
}

export interface PlatformMarketplaceAnalytics {
  totalShops: number;
  verifiedShops: number;
  totalQuotes: number;
  quoteAcceptanceRate: number;
  averageMarketplaceRepairCost: number;
  popularDeviceCategories: Record<string, number>;
  topRequestedRepairs: Record<string, number>;
  highPerformingShops: HighPerformingShop[];
  interactionTrends: Record<string, number>;
  isDemo: boolean;
}

export interface TrackInteractionRequest {
  interactionType: string;
  entityId: string;
  entityType: string;
  metadata?: string;
}
