"use client";

import React, { useEffect, useState, useCallback } from "react";
import { motion } from "framer-motion";
import {
  Network, Star, Shield, BarChart3, TrendingUp,
  RefreshCw, Activity, CheckCircle2, AlertTriangle, Zap
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import RepairQualityScore from "@/components/marketplace/RepairQualityScore";
import TrustIntelligenceCard from "@/components/marketplace/TrustIntelligenceCard";
import RepairNetworkLeaderboard from "@/components/marketplace/RepairNetworkLeaderboard";
import MarketplaceAnomalyPanel from "@/components/marketplace/MarketplaceAnomalyPanel";
import {
  networkIntelligenceApi,
  getDemoOverview,
  getDemoLeaderboard,
  getDemoCategories,
  getDemoAnomalies,
  getDemoShopIntelligence,
} from "@/lib/api/networkIntelligence";
import type {
  RepairNetworkOverviewResponse,
  NetworkLeaderboardResponse,
  CategoryQualityAnalyticsResponse,
  MarketplaceAnomalyResponse,
  ShopIntelligenceResponse,
} from "@/lib/types/networkIntelligence";

const DEMO_SHOP_ID = "shop-elite-01";
const RANKING_TYPES = ["BEST_OVERALL", "MOST_TRUSTED", "HIGHEST_QUALITY", "FASTEST", "BEST_VALUE", "MOST_SUSTAINABLE"];

export default function RepairNetworkIntelligencePage() {
  const [overview,      setOverview]      = useState<RepairNetworkOverviewResponse>(getDemoOverview());
  const [leaderboards,  setLeaderboards]  = useState<Record<string, NetworkLeaderboardResponse[]>>({
    BEST_OVERALL: getDemoLeaderboard(),
  });
  const [categories,   setCategories]    = useState<CategoryQualityAnalyticsResponse[]>(getDemoCategories());
  const [anomalies,    setAnomalies]     = useState<MarketplaceAnomalyResponse[]>(getDemoAnomalies());
  const [shopIntel,    setShopIntel]     = useState<ShopIntelligenceResponse>(getDemoShopIntelligence(DEMO_SHOP_ID));
  const [isDemo,       setIsDemo]        = useState(true);
  const [loading,      setLoading]       = useState(false);
  const [activeTab,    setActiveTab]     = useState<"overview" | "leaderboard" | "shop" | "categories">("overview");

  const loadAll = useCallback(async () => {
    setLoading(true);
    try {
      const [ov, cats, anom, intel] = await Promise.all([
        networkIntelligenceApi.getNetworkOverview(),
        networkIntelligenceApi.getCategoryAnalytics(),
        networkIntelligenceApi.getAnomalies(),
        networkIntelligenceApi.getShopIntelligence(DEMO_SHOP_ID),
      ]);
      setOverview(ov);
      setCategories(cats);
      setAnomalies(anom);
      setShopIntel(intel);

      // Load all leaderboard types
      const boards: Record<string, NetworkLeaderboardResponse[]> = {};
      await Promise.all(
        RANKING_TYPES.map(async (type) => {
          boards[type] = await networkIntelligenceApi.getLeaderboard(type, 6);
        })
      );
      setLeaderboards(boards);
      setIsDemo(false);
    } catch {
      // Demo mode — already set
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadAll(); }, [loadAll]);

  return (
    <main className="min-h-screen bg-[#0a0a0f] text-white">
      <div className="max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Page Header */}
        <div className="mb-8">
          <div className="flex items-start justify-between flex-wrap gap-4">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-violet-500 to-indigo-600 flex items-center justify-center">
                  <Network className="w-4.5 h-4.5 text-white" />
                </div>
                <h1 className="text-2xl font-bold bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
                  Repair Network Intelligence
                </h1>
              </div>
              <p className="text-slate-400 text-sm max-w-2xl">
                Deterministic quality scoring, trust intelligence, and anomaly detection for every repair provider on the network.
              </p>
            </div>
            <div className="flex items-center gap-3">
              {isDemo && (
                <span className="px-3 py-1.5 rounded-lg bg-violet-500/15 border border-violet-500/30 text-xs text-violet-400">
                  Demo Mode
                </span>
              )}
              <button
                onClick={loadAll}
                disabled={loading}
                className="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/[0.06] border border-white/10 text-sm text-slate-300 hover:bg-white/[0.08] transition-colors disabled:opacity-50"
              >
                <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
                Refresh
              </button>
            </div>
          </div>
        </div>

        {/* Network Stats Strip */}
        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-3 mb-8">
          {[
            { label: "Shops",         value: overview.totalRepairShops,                          icon: Network,        color: "text-violet-400" },
            { label: "Repairs",       value: overview.totalCompletedRepairs.toLocaleString(),    icon: Activity,       color: "text-cyan-400" },
            { label: "Success Rate",  value: `${(overview.networkSuccessRate * 100).toFixed(0)}%`, icon: CheckCircle2, color: "text-emerald-400" },
            { label: "Avg Rating",    value: overview.averageCustomerSatisfaction.toFixed(1),    icon: Star,           color: "text-amber-400" },
            { label: "Trust Score",   value: overview.averageTrustScore.toFixed(0),              icon: Shield,         color: "text-blue-400" },
            { label: "Elite Shops",   value: overview.eliteShops,                               icon: Zap,            color: "text-amber-400" },
            { label: "Need Attn.",    value: overview.shopsNeedingAttention,                    icon: AlertTriangle,  color: "text-orange-400" },
            { label: "Open Flags",    value: overview.openAnomalies,                            icon: TrendingUp,     color: "text-red-400" },
          ].map((stat, i) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.04 }}
            >
              <GlassCard className="p-3 h-full">
                <stat.icon className={`w-4 h-4 ${stat.color} mb-1.5`} />
                <p className="text-lg font-bold text-white">{stat.value}</p>
                <p className="text-[10px] text-slate-400">{stat.label}</p>
              </GlassCard>
            </motion.div>
          ))}
        </div>

        {/* Navigation Tabs */}
        <div className="flex gap-1.5 mb-6 flex-wrap">
          {(["overview", "leaderboard", "shop", "categories"] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-all capitalize ${
                activeTab === tab
                  ? "bg-violet-600 text-white shadow-lg shadow-violet-500/20"
                  : "bg-white/[0.04] text-slate-400 hover:bg-white/[0.07] hover:text-white border border-white/[0.06]"
              }`}
            >
              {tab === "overview" ? "Overview" : tab === "leaderboard" ? "Leaderboard" : tab === "shop" ? "Shop Intelligence" : "Categories"}
            </button>
          ))}
        </div>

        {/* Content */}
        {activeTab === "overview" && (
          <motion.div key="overview" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <RepairQualityScore quality={shopIntel.quality} />
              <TrustIntelligenceCard trust={shopIntel.trust} />
            </div>
            <MarketplaceAnomalyPanel anomalies={anomalies} isAdmin={false} />
          </motion.div>
        )}

        {activeTab === "leaderboard" && (
          <motion.div key="leaderboard" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            <RepairNetworkLeaderboard data={leaderboards} />
          </motion.div>
        )}

        {activeTab === "shop" && (
          <motion.div key="shop" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <RepairQualityScore quality={shopIntel.quality} compact={false} />
            <TrustIntelligenceCard trust={shopIntel.trust} />
          </motion.div>
        )}

        {activeTab === "categories" && (
          <motion.div key="categories" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
            <CategoryTable categories={categories} />
          </motion.div>
        )}
      </div>
    </main>
  );
}

function CategoryTable({ categories }: { categories: CategoryQualityAnalyticsResponse[] }) {
  return (
    <GlassCard className="p-6">
      <div className="flex items-center gap-2 mb-5">
        <BarChart3 className="w-5 h-5 text-violet-400" />
        <h3 className="text-base font-semibold text-white">Repair Category Intelligence</h3>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="text-xs text-slate-400 border-b border-white/[0.06]">
              <th className="text-left pb-3 pr-4 font-medium">Category</th>
              <th className="text-right pb-3 pr-4 font-medium">Repairs</th>
              <th className="text-right pb-3 pr-4 font-medium">Success Rate</th>
              <th className="text-right pb-3 pr-4 font-medium">Avg Cost</th>
              <th className="text-right pb-3 font-medium">Turnaround</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/[0.04]">
            {categories.map((cat, i) => {
              const successColor = cat.successRate >= 0.92 ? "text-emerald-400"
                : cat.successRate >= 0.85 ? "text-cyan-400" : "text-amber-400";
              return (
                <motion.tr
                  key={cat.category}
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: i * 0.06 }}
                  className="group hover:bg-white/[0.02] transition-colors"
                >
                  <td className="py-3 pr-4">
                    <span className="text-sm font-medium text-white">{cat.category}</span>
                  </td>
                  <td className="py-3 pr-4 text-right">
                    <span className="text-sm text-slate-300">{cat.repairCount.toLocaleString()}</span>
                  </td>
                  <td className="py-3 pr-4 text-right">
                    <span className={`text-sm font-semibold ${successColor}`}>
                      {(cat.successRate * 100).toFixed(0)}%
                    </span>
                  </td>
                  <td className="py-3 pr-4 text-right">
                    <span className="text-sm text-slate-300">${cat.averageCost}</span>
                  </td>
                  <td className="py-3 text-right">
                    <span className="text-sm text-slate-300">{cat.averageTurnaroundDays}d</span>
                  </td>
                </motion.tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </GlassCard>
  );
}
