export interface MonthlyCostEntry {
  month: string;
  repairCost: number;
  partsCost: number;
  laborCost: number;
}

export interface CategoryCostEntry {
  category: string;
  totalCost: number;
  repairCount: number;
  averageCost: number;
}

export interface RepairCostAnalyticsData {
  totalSpent: number;
  averageCostPerRepair: number;
  totalPartsCost: number;
  totalLaborCost: number;
  projectedNextRepairCost: number;
  potentialSavingsIfPreventive: number;
  monthlyCostTrend: MonthlyCostEntry[];
  costByCategory: CategoryCostEntry[];
  isDemo: boolean;
}

export interface MonthlyImpactEntry {
  month: string;
  co2SavedKg: number;
  ewasteReducedKg: number;
  moneySaved: number;
}

export interface DeviceImpactEntry {
  deviceId: string;
  deviceName: string;
  co2SavedKg: number;
  ewasteReducedKg: number;
  moneySaved: number;
  repairCount: number;
}

export interface SustainabilityAnalyticsData {
  totalCo2SavedKg: number;
  totalEwasteReducedKg: number;
  totalMoneySaved: number;
  devicesExtendedLifespan: number;
  co2EquivalentTrees: number;
  co2EquivalentCarKm: number;
  monthlyImpact: MonthlyImpactEntry[];
  topDevicesByImpact: DeviceImpactEntry[];
  isDemo: boolean;
}

export interface TopFailingCategory {
  category: string;
  deviceCount: number;
  atRiskCount: number;
  riskPercentage: number;
  primaryFaultType: string;
}

export interface RecentHighRiskDevice {
  deviceId: string;
  deviceName: string;
  userId: string;
  userEmail: string;
  riskLevel: "CRITICAL" | "HIGH" | "MEDIUM" | "LOW" | "HEALTHY";
  predictionScore: number;
  primaryFaultType: string;
  evaluatedAt: string;
}

export interface AdminIntelligenceSummaryData {
  totalPredictionsGenerated: number;
  devicesAtCriticalRisk: number;
  devicesAtHighRisk: number;
  platformAverageHealthScore: number;
  totalProjectedFailureCost: number;
  totalPreventableSavings: number;
  platformCo2ImpactKg: number;
  topFailingCategories: TopFailingCategory[];
  recentHighRiskDevices: RecentHighRiskDevice[];
  isDemo: boolean;
}
