export type QuoteStatus =
  | "REQUESTED"
  | "DRAFT"
  | "SUBMITTED"
  | "ACCEPTED"
  | "REJECTED"
  | "EXPIRED"
  | "CANCELLED";

export type ValueRating = "EXCELLENT" | "GOOD" | "FAIR" | "POOR";

export interface RepairQuote {
  id: string;
  userId: string;
  deviceId: string;
  deviceName: string;
  repairShopId: string;
  shopName: string;
  diagnosisId?: string;
  recommendationId?: string;
  repairTitle: string;
  problemSummary: string;
  estimatedCost: number;
  minimumCost: number;
  maximumCost: number;
  estimatedDurationHours: number;
  partsCost: number;
  laborCost: number;
  warrantyDays: number;
  status: QuoteStatus;
  valueScore: number;
  valueRating: ValueRating;
  createdAt: string;
  expiresAt?: string;
  isDemo?: boolean;
}

export interface QuoteComparison {
  quotes: RepairQuote[];
  bestValueQuoteId?: string;
  lowestPriceQuoteId?: string;
  longestWarrantyQuoteId?: string;
  highestTrustQuoteId?: string;
  comparisonInsights: string[];
  isDemo?: boolean;
}

export interface RequestQuoteInput {
  deviceId: string;
  repairShopId: string;
  diagnosisId?: string;
  recommendationId?: string;
  repairTitle?: string;
  problemSummary?: string;
  userBudget?: number;
}
