// Phase 29 — AI-Powered Circular Economy Intelligence & Personalized Sustainability Optimization Types

export type CircularImpactTier =
  | "CIRCULAR_CHAMPION"
  | "ECO_LEADER"
  | "SUSTAINABLE"
  | "DEVELOPING"
  | "STARTING";

export type SustainabilityGoalType =
  | "CARBON_REDUCTION"
  | "EWASTE_PREVENTION"
  | "DEVICE_LIFE_EXTENSION"
  | "REPAIR_COUNT"
  | "MONEY_SAVED";

export type SustainabilityGoalStatus =
  | "ACTIVE"
  | "COMPLETED"
  | "EXPIRED"
  | "CANCELLED";

export type RecommendationPriority = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";

export type SustainabilityActionType =
  | "REPAIR_NOW"
  | "SCHEDULE_MAINTENANCE"
  | "EXTEND_DEVICE_LIFE"
  | "UPGRADE_COMPONENT"
  | "REFURBISH_DEVICE"
  | "DONATE_DEVICE"
  | "RECYCLE_RESPONSIBLY"
  | "MONITOR_DEVICE";

export type CircularEventType =
  | "REPAIR_COMPLETED"
  | "MAINTENANCE_COMPLETED"
  | "DEVICE_LIFE_EXTENDED"
  | "COMPONENT_UPGRADE"
  | "DEVICE_REFURBISHED"
  | "DEVICE_DONATED"
  | "DEVICE_RECYCLED"
  | "RESPONSIBLE_DISPOSAL";

export interface CircularImpactMetrics {
  totalCarbonSavedKg: number;
  totalEwastePreventedKg: number;
  totalMoneySaved: number;
  totalLifeExtensionDays: number;
  totalRepairs: number;
  totalMaintenanceActions: number;
  totalRefurbishments: number;
  totalResponsibleDisposals: number;
  totalCircularActions: number;
}

export interface CircularFactorBreakdown {
  repairLifeExtensionPoints: number; // Max 30
  ewastePreventionPoints: number;    // Max 25
  carbonImpactPoints: number;        // Max 20
  endOfLifePoints: number;           // Max 15
  consistencyPoints: number;         // Max 10
  totalScore: number;
}

export interface CircularImpactScore {
  score: number;
  tier: CircularImpactTier;
  factorBreakdown: CircularFactorBreakdown;
  strengths: string[];
  improvementAreas: string[];
  nextBestAction: string;
  evaluatedAt: string;
}

export interface SustainabilityRecommendation {
  id: string;
  deviceId?: string | null;
  deviceName?: string;
  priority: RecommendationPriority;
  title: string;
  description: string;
  estimatedCarbonImpact: number;
  estimatedEwasteImpact: number;
  estimatedMoneySavings: number;
  reason: string;
  actionType: SustainabilityActionType;
}

export interface SustainabilityGoal {
  id: string;
  userId: string;
  goalType: SustainabilityGoalType | string;
  targetValue: number;
  currentValue: number;
  progressPercentage: number;
  remainingValue: number;
  startDate: string;
  targetDate?: string | null;
  status: SustainabilityGoalStatus | string;
  isCompleted: boolean;
}

export interface CreateGoalPayload {
  goalType: SustainabilityGoalType | string;
  targetValue: number;
  targetDate?: string;
}

export interface UpdateGoalPayload {
  targetValue?: number;
  targetDate?: string;
  status?: SustainabilityGoalStatus | string;
}

export interface SustainabilityAchievement {
  id: string;
  achievementCode: string;
  achievementName: string;
  achievementDescription: string;
  unlocked: boolean;
  unlockedAt?: string | null;
  impactValue: number;
  requirement: string;
}

export interface CircularImpactEvent {
  id: string;
  userId: string;
  deviceId?: string | null;
  deviceName: string;
  eventType: CircularEventType | string;
  eventDate: string;
  carbonSavedKg: number;
  ewastePreventedKg: number;
  moneySaved: number;
  deviceLifeExtensionDays: number;
  impactSource: string;
  referenceId?: string | null;
}

export interface RecordImpactEventPayload {
  deviceId?: string;
  eventType: CircularEventType | string;
  carbonSavedKg?: number;
  ewastePreventedKg?: number;
  moneySaved?: number;
  deviceLifeExtensionDays?: number;
  impactSource?: string;
  referenceId?: string;
}

export interface CircularImpactDashboard {
  impactMetrics: CircularImpactMetrics;
  impactScore: CircularImpactScore;
  activeGoals: SustainabilityGoal[];
  completedGoalsCount: number;
  achievements: SustainabilityAchievement[];
  unlockedAchievementsCount: number;
  nextActions: SustainabilityRecommendation[];
  recentEvents: CircularImpactEvent[];
}

export interface CategoryRanking {
  categoryName: string;
  totalRepairs: number;
  carbonSavedKg: number;
  ewastePreventedKg: number;
  moneySaved: number;
}

export interface ShopSustainabilityRanking {
  shopId: string;
  shopName: string;
  ecoCertified: boolean;
  qualityTier: string;
  circularScore: number;
  repairsCompleted: number;
  carbonSavedKg: number;
}

export interface CircularTrend {
  month: string;
  carbonSavedKg: number;
  ewastePreventedKg: number;
  moneySaved: number;
  actionsCount: number;
}

export interface CircularEconomyAnalytics {
  totalUsers: number;
  totalRepairs: number;
  totalDevicesExtended: number;
  totalCarbonSavedKg: number;
  totalEwastePreventedKg: number;
  totalMoneySaved: number;
  totalDevicesRecycled: number;
  totalDevicesRefurbished: number;
  categoryRankings: CategoryRanking[];
  topSustainableShops: ShopSustainabilityRanking[];
  monthlyTrends: CircularTrend[];
}
