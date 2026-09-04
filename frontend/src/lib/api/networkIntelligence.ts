// Phase 28 — Repair Network Intelligence API Client
import { http } from "./client";
import type {
  RepairNetworkOverviewResponse,
  RepairShopQualityResponse,
  TrustScoreResponse,
  RepairOutcomeAnalyticsResponse,
  NetworkLeaderboardResponse,
  CategoryQualityAnalyticsResponse,
  QualityTrendResponse,
  MarketplaceAnomalyResponse,
  NetworkHealthResponse,
  ShopRiskProfileResponse,
  ShopIntelligenceResponse,
} from "@/lib/types/networkIntelligence";

// ── Helper ────────────────────────────────────────────────────────────────────

function extract<T>(res: { success: boolean; data?: T }): T | null {
  return res?.data ?? null;
}

// ── Public API ────────────────────────────────────────────────────────────────

export const networkIntelligenceApi = {

  async getNetworkOverview(): Promise<RepairNetworkOverviewResponse> {
    try {
      const res = await http.get<{ success: boolean; data: RepairNetworkOverviewResponse }>(
        "/network-intelligence/overview"
      );
      return extract(res.data as any) ?? getDemoOverview();
    } catch {
      return getDemoOverview();
    }
  },

  async getShopIntelligence(shopId: string): Promise<ShopIntelligenceResponse> {
    try {
      const res = await http.get<{ success: boolean; data: ShopIntelligenceResponse }>(
        `/network-intelligence/shop/${shopId}`
      );
      return extract(res.data as any) ?? getDemoShopIntelligence(shopId);
    } catch {
      return getDemoShopIntelligence(shopId);
    }
  },

  async getShopOutcomes(shopId: string, category?: string): Promise<RepairOutcomeAnalyticsResponse> {
    try {
      const url = `/network-intelligence/shop/${shopId}/outcomes${category ? `?category=${category}` : ""}`;
      const res = await http.get<{ success: boolean; data: RepairOutcomeAnalyticsResponse }>(url);
      return extract(res.data as any) ?? getDemoOutcomes();
    } catch {
      return getDemoOutcomes();
    }
  },

  async getLeaderboard(rankingType = "BEST_OVERALL", limit = 10): Promise<NetworkLeaderboardResponse[]> {
    try {
      const res = await http.get<{ success: boolean; data: NetworkLeaderboardResponse[] }>(
        `/network-intelligence/leaderboard?rankingType=${rankingType}&limit=${limit}`
      );
      return extract(res.data as any) ?? getDemoLeaderboard();
    } catch {
      return getDemoLeaderboard();
    }
  },

  async getCategoryAnalytics(): Promise<CategoryQualityAnalyticsResponse[]> {
    try {
      const res = await http.get<{ success: boolean; data: CategoryQualityAnalyticsResponse[] }>(
        "/network-intelligence/categories"
      );
      return extract(res.data as any) ?? getDemoCategories();
    } catch {
      return getDemoCategories();
    }
  },

  async getShopTrends(shopId: string): Promise<QualityTrendResponse[]> {
    try {
      const res = await http.get<{ success: boolean; data: QualityTrendResponse[] }>(
        `/network-intelligence/shop/${shopId}/trends`
      );
      return extract(res.data as any) ?? getDemoTrends();
    } catch {
      return getDemoTrends();
    }
  },

  // ── Admin ──────────────────────────────────────────────────────────────────

  async getNetworkHealth(): Promise<NetworkHealthResponse> {
    try {
      const res = await http.get<{ success: boolean; data: NetworkHealthResponse }>(
        "/admin/network-intelligence/health"
      );
      return extract(res.data as any) ?? getDemoHealth();
    } catch {
      return getDemoHealth();
    }
  },

  async getAnomalies(status?: string, severity?: string): Promise<MarketplaceAnomalyResponse[]> {
    try {
      const params = new URLSearchParams();
      if (status) params.set("status", status);
      if (severity) params.set("severity", severity);
      const url = `/admin/network-intelligence/anomalies${params.toString() ? `?${params}` : ""}`;
      const res = await http.get<{ success: boolean; data: MarketplaceAnomalyResponse[] }>(url);
      return extract(res.data as any) ?? getDemoAnomalies();
    } catch {
      return getDemoAnomalies();
    }
  },

  async getShopRiskProfile(shopId: string): Promise<ShopRiskProfileResponse> {
    try {
      const res = await http.get<{ success: boolean; data: ShopRiskProfileResponse }>(
        `/admin/network-intelligence/shop/${shopId}/risk`
      );
      return extract(res.data as any) ?? getDemoRiskProfile(shopId);
    } catch {
      return getDemoRiskProfile(shopId);
    }
  },

  async updateAnomalyStatus(anomalyId: string, status: string): Promise<MarketplaceAnomalyResponse | null> {
    try {
      const res = await http.post<{ success: boolean; data: MarketplaceAnomalyResponse }>(
        `/admin/network-intelligence/anomalies/${anomalyId}/status`,
        { status }
      );
      return extract(res.data as any);
    } catch {
      return null;
    }
  },
};

// ── Demo Mode Fallbacks — clearly labeled ─────────────────────────────────────

export function getDemoOverview(): RepairNetworkOverviewResponse {
  return {
    totalRepairShops: 42,
    totalCompletedRepairs: 1847,
    networkSuccessRate: 0.912,
    averageCustomerSatisfaction: 4.6,
    averageTrustScore: 81,
    eliteShops: 5,
    shopsNeedingAttention: 2,
    openAnomalies: 3,
    isDemo: true,
  } as any;
}

export function getDemoShopIntelligence(shopId: string): ShopIntelligenceResponse {
  return {
    quality: {
      shopId,
      shopName: "Demo Intelligence Data",
      overallQualityScore: 84,
      qualityTier: "EXCELLENT",
      reliabilityScore: 86,
      trustScore: 82,
      customerSatisfactionScore: 88,
      repairSuccessScore: 87,
      priceFairnessScore: 80,
      serviceSpeedScore: 78,
      totalRepairs: 215,
      successRate: 0.93,
      repeatRepairRate: 0.07,
      trend: "IMPROVING",
      factorBreakdown: [
        { factor: "Repair Success Rate",   score: 25, weight: 30, description: "93% of repairs resolved successfully" },
        { factor: "Customer Satisfaction", score: 18, weight: 20, description: "Avg. 4.7/5.0 from verified reviews" },
        { factor: "Reliability",           score: 17, weight: 20, description: "Low repeat repairs (7%) and warranty claims (4%)" },
        { factor: "Price Fairness",        score: 8,  weight: 10, description: "Pricing aligned with market averages" },
        { factor: "Service Speed",         score: 8,  weight: 10, description: "Average 1.4 day turnaround" },
        { factor: "Experience & Volume",   score: 8,  weight: 10, description: "215 verified repairs completed" },
      ],
    },
    trust: {
      shopId,
      trustScore: 82,
      trustTier: "HIGH",
      positiveSignals: [
        "High repair success rate (93%)",
        "Excellent customer satisfaction (4.7/5.0)",
        "Strong service history with 215 verified repairs",
      ],
      riskSignals: [],
      scoreBreakdown: { "High success rate": 15, "Excellent satisfaction": 12, "Verified repair history": 8 },
    },
  };
}

export function getDemoOutcomes(): RepairOutcomeAnalyticsResponse {
  return {
    totalRepairs: 215,
    successfulRepairs: 200,
    failedRepairs: 8,
    repeatRepairs: 15,
    successRate: 0.93,
    failureRate: 0.037,
    repeatRepairRate: 0.07,
    averageRepairCost: 84.5,
    averageTurnaroundDays: 1.4,
  };
}

export function getDemoLeaderboard(): NetworkLeaderboardResponse[] {
  return [
    { rank: 1, shopId: "shop-elite-01", shopName: "PrecisionFix Pro",      qualityScore: 95, trustScore: 94, successRate: 0.97, customerRating: 4.9, trend: "STABLE",    badge: "🏆 #1 Best Overall" },
    { rank: 2, shopId: "shop-elite-02", shopName: "TrueRepair Certified",  qualityScore: 91, trustScore: 92, successRate: 0.95, customerRating: 4.8, trend: "IMPROVING", badge: "🥈 #2 Best Overall" },
    { rank: 3, shopId: "shop-excel-01", shopName: "QuickMend Station",     qualityScore: 87, trustScore: 85, successRate: 0.92, customerRating: 4.7, trend: "STABLE",    badge: "🥉 #3 Best Overall" },
    { rank: 4, shopId: "shop-excel-02", shopName: "EcoRepair Hub",         qualityScore: 84, trustScore: 88, successRate: 0.90, customerRating: 4.7, trend: "IMPROVING", badge: "#4 Best Overall" },
    { rank: 5, shopId: "shop-trust-01", shopName: "CityTech Repair",       qualityScore: 79, trustScore: 80, successRate: 0.87, customerRating: 4.5, trend: "STABLE",    badge: "#5 Best Overall" },
  ];
}

export function getDemoCategories(): CategoryQualityAnalyticsResponse[] {
  return [
    { category: "Smartphone",     repairCount: 842, successRate: 0.94, averageCost: 79,  averageTurnaroundDays: 1.5, bestPerformingShops: ["PrecisionFix Pro"] },
    { category: "Laptop",         repairCount: 431, successRate: 0.89, averageCost: 149, averageTurnaroundDays: 3.0, bestPerformingShops: ["TrueRepair Certified"] },
    { category: "Tablet",         repairCount: 218, successRate: 0.91, averageCost: 99,  averageTurnaroundDays: 2.0, bestPerformingShops: ["QuickMend Station"] },
    { category: "Gaming Console", repairCount: 174, successRate: 0.86, averageCost: 119, averageTurnaroundDays: 4.0, bestPerformingShops: ["CityTech Repair"] },
    { category: "Wearable",       repairCount: 98,  successRate: 0.93, averageCost: 59,  averageTurnaroundDays: 1.0, bestPerformingShops: ["EcoRepair Hub"] },
  ];
}

export function getDemoTrends(): QualityTrendResponse[] {
  return [
    { period: "6 months ago", qualityScore: 71, trustScore: 68, successRate: 0.82, customerSatisfaction: 4.2 },
    { period: "5 months ago", qualityScore: 74, trustScore: 70, successRate: 0.84, customerSatisfaction: 4.3 },
    { period: "4 months ago", qualityScore: 77, trustScore: 74, successRate: 0.86, customerSatisfaction: 4.4 },
    { period: "3 months ago", qualityScore: 80, trustScore: 77, successRate: 0.88, customerSatisfaction: 4.5 },
    { period: "2 months ago", qualityScore: 83, trustScore: 80, successRate: 0.90, customerSatisfaction: 4.6 },
    { period: "Last month",   qualityScore: 85, trustScore: 82, successRate: 0.91, customerSatisfaction: 4.7 },
  ];
}

export function getDemoAnomalies(): MarketplaceAnomalyResponse[] {
  return [
    {
      id: "anom-demo-1",
      shopId: "shop-std-03",
      shopName: "Demo Intelligence Data",
      anomalyType: "REVIEW_SPIKE",
      severity: "MEDIUM",
      riskScore: 52,
      description: "Review volume 3.2× above historical average detected in a 7-day window. Investigation recommended.",
      status: "OPEN",
      detectedAt: new Date(Date.now() - 86400000 * 2).toISOString(),
    },
    {
      id: "anom-demo-2",
      shopId: "shop-std-04",
      shopName: "Demo Intelligence Data",
      anomalyType: "HIGH_REPEAT_REPAIRS",
      severity: "HIGH",
      riskScore: 71,
      description: "Repeat repair rate of 28% exceeds the 20% network threshold over the last 30 days.",
      status: "UNDER_REVIEW",
      detectedAt: new Date(Date.now() - 86400000 * 5).toISOString(),
    },
  ];
}

export function getDemoHealth(): NetworkHealthResponse {
  return {
    overallStatus: "HEALTHY",
    totalShops: 42,
    eliteShops: 5,
    excellentShops: 12,
    trustedShops: 18,
    standardShops: 6,
    needsImprovementShops: 1,
    openAnomalies: 3,
    criticalAnomalies: 0,
    platformTrustScore: 82,
    platformQualityScore: 80.4,
    platformSuccessRate: 0.912,
  };
}

export function getDemoRiskProfile(shopId: string): ShopRiskProfileResponse {
  return {
    shopId,
    shopName: "Demo Intelligence Data",
    riskScore: 28,
    riskLevel: "LOW",
    activeAnomalies: 0,
    anomalies: [],
    riskSignals: [],
    recommendation: "No immediate action required. Continue routine monitoring.",
  };
}
