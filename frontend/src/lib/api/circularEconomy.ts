// Phase 29 — AI-Powered Circular Economy Intelligence API Client
import { http } from "./client";
import type {
  CircularImpactDashboard,
  CircularImpactMetrics,
  CircularImpactScore,
  SustainabilityRecommendation,
  SustainabilityGoal,
  CreateGoalPayload,
  UpdateGoalPayload,
  SustainabilityAchievement,
  CircularImpactEvent,
  RecordImpactEventPayload,
  CircularEconomyAnalytics,
  CircularTrend,
  CategoryRanking,
  ShopSustainabilityRanking,
} from "@/lib/types/circularEconomy";

function extract<T>(res: { success: boolean; data?: T }): T | null {
  return res?.data ?? null;
}

export const circularEconomyApi = {
  /**
   * User dashboard aggregation.
   */
  async getDashboard(): Promise<CircularImpactDashboard> {
    try {
      const res = await http.get<{ success: boolean; data: CircularImpactDashboard }>(
        "/circular-economy/dashboard"
      );
      return extract(res.data as any) ?? getDemoCircularDashboard();
    } catch {
      return getDemoCircularDashboard();
    }
  },

  /**
   * User impact metrics.
   */
  async getImpactMetrics(): Promise<CircularImpactMetrics> {
    try {
      const res = await http.get<{ success: boolean; data: CircularImpactMetrics }>(
        "/circular-economy/impact"
      );
      return extract(res.data as any) ?? getDemoImpactMetrics();
    } catch {
      return getDemoImpactMetrics();
    }
  },

  /**
   * Device specific impact.
   */
  async getDeviceImpact(deviceId: string): Promise<CircularImpactMetrics> {
    try {
      const res = await http.get<{ success: boolean; data: CircularImpactMetrics }>(
        `/circular-economy/device/${deviceId}`
      );
      return extract(res.data as any) ?? getDemoImpactMetrics();
    } catch {
      return getDemoImpactMetrics();
    }
  },

  /**
   * Circular impact score (0-100).
   */
  async getScore(): Promise<CircularImpactScore> {
    try {
      const res = await http.get<{ success: boolean; data: CircularImpactScore }>(
        "/circular-economy/score"
      );
      return extract(res.data as any) ?? getDemoCircularScore();
    } catch {
      return getDemoCircularScore();
    }
  },

  /**
   * Personalized recommendations.
   */
  async getRecommendations(deviceId?: string): Promise<SustainabilityRecommendation[]> {
    try {
      const url = `/circular-economy/optimize${deviceId ? `?deviceId=${deviceId}` : ""}`;
      const res = await http.get<{ success: boolean; data: SustainabilityRecommendation[] }>(url);
      return extract(res.data as any) ?? getDemoRecommendations();
    } catch {
      return getDemoRecommendations();
    }
  },

  /**
   * User sustainability goals.
   */
  async getGoals(): Promise<SustainabilityGoal[]> {
    try {
      const res = await http.get<{ success: boolean; data: SustainabilityGoal[] }>(
        "/circular-economy/goals"
      );
      return extract(res.data as any) ?? getDemoGoals();
    } catch {
      return getDemoGoals();
    }
  },

  /**
   * Create target goal.
   */
  async createGoal(payload: CreateGoalPayload): Promise<SustainabilityGoal | null> {
    try {
      const res = await http.post<{ success: boolean; data: SustainabilityGoal }>(
        "/circular-economy/goals",
        payload
      );
      return extract(res.data as any);
    } catch {
      return null;
    }
  },

  /**
   * Update target goal.
   */
  async updateGoal(goalId: string, payload: UpdateGoalPayload): Promise<SustainabilityGoal | null> {
    try {
      const res = await http.put<{ success: boolean; data: SustainabilityGoal }>(
        `/circular-economy/goals/${goalId}`,
        payload
      );
      return extract(res.data as any);
    } catch {
      return null;
    }
  },

  /**
   * Delete goal.
   */
  async deleteGoal(goalId: string): Promise<boolean> {
    try {
      const res = await http.delete<{ success: boolean }>(`/circular-economy/goals/${goalId}`);
      return res.data?.success ?? false;
    } catch {
      return false;
    }
  },

  /**
   * Achievements list.
   */
  async getAchievements(): Promise<SustainabilityAchievement[]> {
    try {
      const res = await http.get<{ success: boolean; data: SustainabilityAchievement[] }>(
        "/circular-economy/achievements"
      );
      return extract(res.data as any) ?? getDemoAchievements();
    } catch {
      return getDemoAchievements();
    }
  },

  /**
   * Chronological impact timeline.
   */
  async getTimeline(): Promise<CircularImpactEvent[]> {
    try {
      const res = await http.get<{ success: boolean; data: CircularImpactEvent[] }>(
        "/circular-economy/timeline"
      );
      return extract(res.data as any) ?? getDemoTimeline();
    } catch {
      return getDemoTimeline();
    }
  },

  /**
   * Record new circular impact event.
   */
  async recordEvent(payload: RecordImpactEventPayload): Promise<CircularImpactEvent | null> {
    try {
      const res = await http.post<{ success: boolean; data: CircularImpactEvent }>(
        "/circular-economy/events",
        payload
      );
      return extract(res.data as any);
    } catch {
      return null;
    }
  },

  // ── Admin Endpoints ─────────────────────────────────────────────────────────

  async getPlatformAnalytics(): Promise<CircularEconomyAnalytics> {
    try {
      const res = await http.get<{ success: boolean; data: CircularEconomyAnalytics }>(
        "/admin/circular-economy/analytics"
      );
      return extract(res.data as any) ?? getDemoPlatformAnalytics();
    } catch {
      return getDemoPlatformAnalytics();
    }
  },

  async getMonthlyTrends(): Promise<CircularTrend[]> {
    try {
      const res = await http.get<{ success: boolean; data: CircularTrend[] }>(
        "/admin/circular-economy/trends"
      );
      return extract(res.data as any) ?? getDemoTrends();
    } catch {
      return getDemoTrends();
    }
  },

  async getRankings(): Promise<{ categoryRankings: CategoryRanking[]; shopRankings: ShopSustainabilityRanking[] }> {
    try {
      const res = await http.get<{
        success: boolean;
        categoryRankings: CategoryRanking[];
        shopRankings: ShopSustainabilityRanking[];
      }>("/admin/circular-economy/rankings");
      return (
        res.data ?? {
          categoryRankings: getDemoCategoryRankings(),
          shopRankings: getDemoShopRankings(),
        }
      );
    } catch {
      return {
        categoryRankings: getDemoCategoryRankings(),
        shopRankings: getDemoShopRankings(),
      };
    }
  },
};

// ── Fallback Demo Mode Data (Clearly Labeled) ──────────────────────────────────

export function getDemoImpactMetrics(): CircularImpactMetrics {
  return {
    totalCarbonSavedKg: 142.8,
    totalEwastePreventedKg: 4.85,
    totalMoneySaved: 12500.0,
    totalLifeExtensionDays: 540,
    totalRepairs: 6,
    totalMaintenanceActions: 4,
    totalRefurbishments: 1,
    totalResponsibleDisposals: 1,
    totalCircularActions: 12,
  };
}

export function getDemoCircularScore(): CircularImpactScore {
  return {
    score: 88,
    tier: "ECO_LEADER",
    factorBreakdown: {
      repairLifeExtensionPoints: 26,
      ewastePreventionPoints: 24,
      carbonImpactPoints: 18,
      endOfLifePoints: 12,
      consistencyPoints: 8,
      totalScore: 88,
    },
    strengths: [
      "Outstanding hardware life extension through proactive repair and servicing.",
      "Exceptional e-waste diversion preventing toxic landfill accumulation.",
      "High carbon mitigation achieved by avoiding new manufactured hardware replacement.",
    ],
    improvementAreas: [
      "Set a new 90-day E-Waste Prevention target in Sustainability Goals.",
      "Decommission dormant electronics via certified recycling partners.",
    ],
    nextBestAction: "Complete scheduled battery inspection for MacBook Pro 16\" to reach Circular Champion tier (90+).",
    evaluatedAt: new Date().toISOString(),
  };
}

export function getDemoRecommendations(): SustainabilityRecommendation[] {
  return [
    {
      id: "rec-demo-1",
      deviceId: "dev-1",
      deviceName: "MacBook Pro 16\" (M1 Pro)",
      priority: "HIGH",
      title: "Battery Health Inspection & Thermal Servicing",
      description: "Extending battery cycle lifespan delays laptop replacement by up to 2 years and avoids ~64.5kg CO₂ manufacturing emissions.",
      estimatedCarbonImpact: 64.5,
      estimatedEwasteImpact: 2.1,
      estimatedMoneySavings: 6500.0,
      reason: "Battery cycle count exceeds 680 cycles with mild thermal throttling under load.",
      actionType: "SCHEDULE_MAINTENANCE",
    },
    {
      id: "rec-demo-2",
      deviceId: "dev-2",
      deviceName: "iPhone 13 Pro",
      priority: "MEDIUM",
      title: "Charging Port De-Oxidation & Seal Refresh",
      description: "Preventive port maintenance and ingress seal restoration prevents short-circuit damage.",
      estimatedCarbonImpact: 12.5,
      estimatedEwasteImpact: 0.24,
      estimatedMoneySavings: 1800.0,
      reason: "Debris accumulation detected in Lightning port charging pins.",
      actionType: "EXTEND_DEVICE_LIFE",
    },
    {
      id: "rec-demo-3",
      deviceId: "dev-3",
      deviceName: "Dell XPS 15 (2020)",
      priority: "CRITICAL",
      title: "Thermal Paste Replacement & Fan Bearings Check",
      description: "Active thermal throttling is degrading core logic capacitors. Replacing paste restores full benchmark performance.",
      estimatedCarbonImpact: 48.0,
      estimatedEwasteImpact: 1.8,
      estimatedMoneySavings: 4200.0,
      reason: "CPU sustained temperature exceeds 94°C during standard multitasking.",
      actionType: "REPAIR_NOW",
    },
  ];
}

export function getDemoGoals(): SustainabilityGoal[] {
  return [
    {
      id: "sg-demo-1",
      userId: "usr-demo",
      goalType: "CARBON_REDUCTION",
      targetValue: 150.0,
      currentValue: 142.8,
      progressPercentage: 95,
      remainingValue: 7.2,
      startDate: new Date(Date.now() - 86400000 * 60).toISOString(),
      targetDate: new Date(Date.now() + 86400000 * 30).toISOString(),
      status: "ACTIVE",
      isCompleted: false,
    },
    {
      id: "sg-demo-2",
      userId: "usr-demo",
      goalType: "EWASTE_PREVENTION",
      targetValue: 5.0,
      currentValue: 4.85,
      progressPercentage: 97,
      remainingValue: 0.15,
      startDate: new Date(Date.now() - 86400000 * 90).toISOString(),
      targetDate: new Date(Date.now() + 86400000 * 45).toISOString(),
      status: "ACTIVE",
      isCompleted: false,
    },
    {
      id: "sg-demo-3",
      userId: "usr-demo",
      goalType: "REPAIR_COUNT",
      targetValue: 6.0,
      currentValue: 6.0,
      progressPercentage: 100,
      remainingValue: 0.0,
      startDate: new Date(Date.now() - 86400000 * 120).toISOString(),
      targetDate: new Date(Date.now() - 86400000 * 5).toISOString(),
      status: "COMPLETED",
      isCompleted: true,
    },
  ];
}

export function getDemoAchievements(): SustainabilityAchievement[] {
  return [
    {
      id: "sa-1",
      achievementCode: "FIRST_REPAIR",
      achievementName: "First Life Saved",
      achievementDescription: "Successfully completed your first hardware diagnosis and repair.",
      unlocked: true,
      unlockedAt: new Date(Date.now() - 86400000 * 90).toISOString(),
      impactValue: 1.0,
      requirement: "Complete 1 repair action",
    },
    {
      id: "sa-2",
      achievementCode: "EWASTE_SAVER",
      achievementName: "E-Waste Guardian",
      achievementDescription: "Prevented over 5kg of hazardous electronic scrap from entering landfills.",
      unlocked: true,
      unlockedAt: new Date(Date.now() - 86400000 * 30).toISOString(),
      impactValue: 4.85,
      requirement: "Prevent 5kg e-waste",
    },
    {
      id: "sa-3",
      achievementCode: "CARBON_CONSCIOUS",
      achievementName: "Carbon Conscious",
      achievementDescription: "Offset over 25kg of carbon emissions through proactive repair and refurbishment.",
      unlocked: true,
      unlockedAt: new Date(Date.now() - 86400000 * 45).toISOString(),
      impactValue: 142.8,
      requirement: "Save 25kg CO₂ emissions",
    },
    {
      id: "sa-4",
      achievementCode: "LIFE_EXTENDER",
      achievementName: "Longevity Master",
      achievementDescription: "Extended electronic hardware service lifespans by more than 180 cumulative days.",
      unlocked: true,
      unlockedAt: new Date(Date.now() - 86400000 * 20).toISOString(),
      impactValue: 540.0,
      requirement: "Extend device lifespan by 180 days",
    },
    {
      id: "sa-5",
      achievementCode: "PLANET_PROTECTOR",
      achievementName: "Planet Protector",
      achievementDescription: "Prevented over 100kg of CO₂ emissions across your entire personal electronics fleet.",
      unlocked: true,
      unlockedAt: new Date(Date.now() - 86400000 * 10).toISOString(),
      impactValue: 142.8,
      requirement: "Save 100kg CO₂ emissions",
    },
    {
      id: "sa-6",
      achievementCode: "CIRCULAR_CHAMPION",
      achievementName: "Circular Champion",
      achievementDescription: "Reached the pinnacle 90+ score tier in the Circular Economy Impact Index.",
      unlocked: false,
      unlockedAt: null,
      impactValue: 88.0,
      requirement: "Attain a Circular Impact Score >= 90",
    },
  ];
}

export function getDemoTimeline(): CircularImpactEvent[] {
  return [
    {
      id: "cie-1",
      userId: "usr-demo",
      deviceId: "dev-1",
      deviceName: "MacBook Pro 16\" (M1 Pro)",
      eventType: "REPAIR_COMPLETED",
      eventDate: new Date(Date.now() - 86400000 * 4).toISOString(),
      carbonSavedKg: 64.5,
      ewastePreventedKg: 2.1,
      moneySaved: 4500.0,
      deviceLifeExtensionDays: 365,
      impactSource: "AUTOMATED_REPAIR",
      referenceId: "rep-101",
    },
    {
      id: "cie-2",
      userId: "usr-demo",
      deviceId: "dev-2",
      deviceName: "iPhone 13 Pro",
      eventType: "MAINTENANCE_COMPLETED",
      eventDate: new Date(Date.now() - 86400000 * 16).toISOString(),
      carbonSavedKg: 12.0,
      ewastePreventedKg: 0.24,
      moneySaved: 800.0,
      deviceLifeExtensionDays: 90,
      impactSource: "MAINTENANCE_SCHEDULE",
      referenceId: "ms-202",
    },
    {
      id: "cie-3",
      userId: "usr-demo",
      deviceId: "dev-3",
      deviceName: "Dell XPS 15",
      eventType: "COMPONENT_UPGRADE",
      eventDate: new Date(Date.now() - 86400000 * 35).toISOString(),
      carbonSavedKg: 48.2,
      ewastePreventedKg: 1.8,
      moneySaved: 5200.0,
      deviceLifeExtensionDays: 300,
      impactSource: "USER_ACTION",
      referenceId: "upg-303",
    },
    {
      id: "cie-4",
      userId: "usr-demo",
      deviceId: "dev-4",
      deviceName: "Sony WH-1000XM4",
      eventType: "DEVICE_REFURBISHED",
      eventDate: new Date(Date.now() - 86400000 * 60).toISOString(),
      carbonSavedKg: 18.1,
      ewastePreventedKg: 0.25,
      moneySaved: 2000.0,
      deviceLifeExtensionDays: 180,
      impactSource: "MARKETPLACE_BOOKING",
      referenceId: "rf-404",
    },
  ];
}

export function getDemoCircularDashboard(): CircularImpactDashboard {
  return {
    impactMetrics: getDemoImpactMetrics(),
    impactScore: getDemoCircularScore(),
    activeGoals: getDemoGoals(),
    completedGoalsCount: 1,
    achievements: getDemoAchievements(),
    unlockedAchievementsCount: 5,
    nextActions: getDemoRecommendations(),
    recentEvents: getDemoTimeline(),
  };
}

export function getDemoCategoryRankings(): CategoryRanking[] {
  return [
    { categoryName: "Laptops & MacBooks", totalRepairs: 68, carbonSavedKg: 6240.0, ewastePreventedKg: 142.8, moneySaved: 480000.0 },
    { categoryName: "Smartphones", totalRepairs: 112, carbonSavedKg: 4520.0, ewastePreventedKg: 26.8, moneySaved: 290000.0 },
    { categoryName: "Tablets & iPads", totalRepairs: 42, carbonSavedKg: 1850.0, ewastePreventedKg: 18.9, moneySaved: 115000.0 },
    { categoryName: "Gaming Consoles", totalRepairs: 24, carbonSavedKg: 980.0, ewastePreventedKg: 76.8, moneySaved: 68000.0 },
    { categoryName: "Audio & Accessories", totalRepairs: 38, carbonSavedKg: 340.0, ewastePreventedKg: 9.5, moneySaved: 32000.0 },
  ];
}

export function getDemoShopRankings(): ShopSustainabilityRanking[] {
  return [
    { shopId: "shop-1", shopName: "GreenTech Micro-Repair Lab", ecoCertified: true, qualityTier: "ELITE", circularScore: 96, repairsCompleted: 78, carbonSavedKg: 4200.5 },
    { shopId: "shop-2", shopName: "EcoFix Master Electronics", ecoCertified: true, qualityTier: "ELITE", circularScore: 92, repairsCompleted: 64, carbonSavedKg: 3450.0 },
    { shopId: "shop-3", shopName: "CircuitWise Certified Hub", ecoCertified: true, qualityTier: "EXCELLENT", circularScore: 88, repairsCompleted: 52, carbonSavedKg: 2800.0 },
    { shopId: "shop-4", shopName: "Apex Component Revival", ecoCertified: false, qualityTier: "TRUSTED", circularScore: 82, repairsCompleted: 41, carbonSavedKg: 1950.0 },
  ];
}

export function getDemoTrends(): CircularTrend[] {
  return [
    { month: "Oct", carbonSavedKg: 1420.0, ewastePreventedKg: 95.0, moneySaved: 110000.0, actionsCount: 28 },
    { month: "Nov", carbonSavedKg: 1850.0, ewastePreventedKg: 124.0, moneySaved: 145000.0, actionsCount: 36 },
    { month: "Dec", carbonSavedKg: 2310.0, ewastePreventedKg: 158.0, moneySaved: 182000.0, actionsCount: 45 },
    { month: "Jan", carbonSavedKg: 2780.0, ewastePreventedKg: 189.0, moneySaved: 220000.0, actionsCount: 54 },
    { month: "Feb", carbonSavedKg: 3420.0, ewastePreventedKg: 235.0, moneySaved: 275000.0, actionsCount: 68 },
    { month: "Mar", carbonSavedKg: 4120.0, ewastePreventedKg: 285.0, moneySaved: 335000.0, actionsCount: 82 },
  ];
}

export function getDemoPlatformAnalytics(): CircularEconomyAnalytics {
  return {
    totalUsers: 148,
    totalRepairs: 284,
    totalDevicesExtended: 192,
    totalCarbonSavedKg: 15720.5,
    totalEwastePreventedKg: 980.4,
    totalMoneySaved: 1185000.0,
    totalDevicesRecycled: 38,
    totalDevicesRefurbished: 54,
    categoryRankings: getDemoCategoryRankings(),
    topSustainableShops: getDemoShopRankings(),
    monthlyTrends: getDemoTrends(),
  };
}
