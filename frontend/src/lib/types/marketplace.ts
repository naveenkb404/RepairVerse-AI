export type VerificationStatus = "PENDING" | "VERIFIED" | "TRUSTED" | "SUSPENDED";
export type VerificationLevel = "BASIC" | "VERIFIED" | "PREMIUM";
export type TrustLevel = "EXCEPTIONAL" | "TRUSTED" | "GOOD" | "LIMITED" | "UNVERIFIED";

export interface MarketplaceShop {
  id: string;
  shopName: string;
  address: string;
  latitude?: number;
  longitude?: number;
  phone?: string;
  email?: string;
  hours?: string;
  rating: number;
  reviewCount: number;
  verificationStatus: VerificationStatus;
  verificationLevel: VerificationLevel;
  yearsOfExperience: number;
  totalRepairsCompleted: number;
  responseRate: number;
  averageResponseTimeMinutes: number;
  warrantyOffered: boolean;
  warrantyDays: number;
  specializations: string[];
  certifications: string[];
  marketplaceScore: number;
  trustLevel: TrustLevel;
  keyStrengths: string[];
  isDemo?: boolean;
}

export interface ShopRanking {
  shopId: string;
  shopName: string;
  totalScore: number;
  trustLevel: TrustLevel;
  verificationScore: number;
  customerRatingScore: number;
  specializationScore: number;
  responsePerformanceScore: number;
  warrantyScore: number;
  experienceScore: number;
  rankingReasons: string[];
  strengths: string[];
  warnings: string[];
  isDemo?: boolean;
}

export interface TrustScore {
  shopId: string;
  trustScore: number;
  trustLevel: TrustLevel;
  trustFactors: string[];
  positiveSignals: string[];
  riskSignals: string[];
  isDemo?: boolean;
}

export interface RepairReview {
  id: string;
  userId: string;
  userFullName: string;
  repairShopId: string;
  bookingId?: string;
  rating: number;
  title: string;
  comment: string;
  repairQualityRating: number;
  communicationRating: number;
  valueRating: number;
  timelinessRating: number;
  verifiedRepair: boolean;
  createdAt: string;
  isDemo?: boolean;
}

export interface ShopReputation {
  shopId: string;
  averageRating: number;
  totalReviews: number;
  totalVerifiedRepairs: number;
  qualityRating: number;
  communicationRating: number;
  valueRating: number;
  timelinessRating: number;
  ratingDistribution: Record<number, number>;
  recentReviews: RepairReview[];
  isDemo?: boolean;
}

export interface CreateReviewInput {
  bookingId?: string;
  rating: number;
  title: string;
  comment: string;
  repairQualityRating: number;
  communicationRating: number;
  valueRating: number;
  timelinessRating: number;
}
