"use client";

import React, { useState } from "react";
import { motion } from "framer-motion";
import {
  Activity,
  Award,
  BarChart3,
  CheckCircle,
  Clock,
  DollarSign,
  Flame,
  Layers,
  Lightbulb,
  ShieldCheck,
  TrendingUp,
  Users,
} from "lucide-react";
import {
  UserMarketplaceInsights,
  PlatformMarketplaceAnalytics,
} from "@/lib/types/repairMatching";

interface MarketplaceAnalyticsOverviewProps {
  userInsights: UserMarketplaceInsights | null;
  platformAnalytics?: PlatformMarketplaceAnalytics | null;
  isAdmin?: boolean;
}

export default function MarketplaceAnalyticsOverview({
  userInsights,
  platformAnalytics,
  isAdmin = false,
}: MarketplaceAnalyticsOverviewProps) {
  const [viewMode, setViewMode] = useState<"USER" | "PLATFORM">(isAdmin ? "PLATFORM" : "USER");

  if (!userInsights && !platformAnalytics) {
    return null;
  }

  return (
    <div className="space-y-6">
      {/* View Switcher for Admins or Multi-view */}
      {isAdmin && platformAnalytics && (
        <div className="flex items-center justify-between border-b border-white/10 pb-4">
          <div className="flex items-center gap-2">
            <BarChart3 className="size-5 text-[#22C55E]" />
            <h3 className="text-base font-bold text-white">Marketplace Intelligence & Telemetry</h3>
          </div>
          <div className="flex rounded-xl border border-white/10 bg-white/5 p-1">
            <button
              onClick={() => setViewMode("USER")}
              className={`rounded-lg px-3 py-1 text-xs font-semibold transition-all ${
                viewMode === "USER"
                  ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white"
                  : "text-white/60 hover:text-white"
              }`}
            >
              My Insights
            </button>
            <button
              onClick={() => setViewMode("PLATFORM")}
              className={`rounded-lg px-3 py-1 text-xs font-semibold transition-all ${
                viewMode === "PLATFORM"
                  ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white"
                  : "text-white/60 hover:text-white"
              }`}
            >
              Platform Overview
            </button>
          </div>
        </div>
      )}

      {/* User Insights Mode */}
      {viewMode === "USER" && userInsights && (
        <div className="space-y-6">
          {/* Stats Row */}
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 backdrop-blur-xl">
              <span className="text-[11px] font-bold uppercase text-white/40">Shops Compared</span>
              <p className="mt-1 text-2xl font-black text-white">
                {userInsights.totalShopsCompared}
              </p>
              <span className="text-[10px] text-white/50">Multi-provider evaluations</span>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 backdrop-blur-xl">
              <span className="text-[11px] font-bold uppercase text-white/40">Quotes Requested</span>
              <p className="mt-1 text-2xl font-black text-[#06B6D4]">
                {userInsights.totalQuotesRequested}
              </p>
              <span className="text-[10px] text-white/50">
                {userInsights.totalQuotesAccepted} accepted
              </span>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 backdrop-blur-xl">
              <span className="text-[11px] font-bold uppercase text-white/40">Avg. Repair Cost</span>
              <p className="mt-1 text-2xl font-black text-white">
                ${userInsights.averageRepairCost.toFixed(2)}
              </p>
              <span className="text-[10px] text-white/50">Across your hardware profile</span>
            </div>

            <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/[0.08] p-4 backdrop-blur-xl">
              <span className="text-[11px] font-bold uppercase text-[#22C55E]">Total Potential Savings</span>
              <p className="mt-1 text-2xl font-black text-[#22C55E]">
                ${userInsights.totalPotentialSavings.toFixed(2)}
              </p>
              <span className="text-[10px] text-[#22C55E]/70">Via competitive quote matching</span>
            </div>
          </div>

          {/* Value Opportunities list */}
          {userInsights.bestValueOpportunities.length > 0 && (
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 backdrop-blur-xl space-y-3">
              <div className="flex items-center gap-2">
                <Lightbulb className="size-4 text-amber-400" />
                <h4 className="text-xs font-bold uppercase tracking-wider text-white">
                  Intelligent Value Opportunities
                </h4>
              </div>
              <ul className="space-y-2">
                {userInsights.bestValueOpportunities.map((opp, idx) => (
                  <li key={idx} className="flex items-start gap-2 text-xs text-white/80">
                    <CheckCircle className="mt-0.5 size-3.5 shrink-0 text-[#22C55E]" />
                    <span>{opp}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}

      {/* Platform Analytics Mode */}
      {viewMode === "PLATFORM" && platformAnalytics && (
        <div className="space-y-6">
          {/* Top Platform KPIs */}
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <span className="text-[11px] font-bold uppercase text-white/40">Active Providers</span>
              <p className="mt-1 text-2xl font-black text-white">
                {platformAnalytics.totalShops}
              </p>
              <span className="text-[10px] text-[#22C55E]">
                {platformAnalytics.verifiedShops} Verified
              </span>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <span className="text-[11px] font-bold uppercase text-white/40">Total Quotes</span>
              <p className="mt-1 text-2xl font-black text-[#06B6D4]">
                {platformAnalytics.totalQuotes}
              </p>
              <span className="text-[10px] text-white/50">Marketplace inquiries</span>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <span className="text-[11px] font-bold uppercase text-white/40">Quote Acceptance Rate</span>
              <p className="mt-1 text-2xl font-black text-[#22C55E]">
                {platformAnalytics.quoteAcceptanceRate}%
              </p>
              <span className="text-[10px] text-white/50">High conversion intent</span>
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <span className="text-[11px] font-bold uppercase text-white/40">Platform Avg Cost</span>
              <p className="mt-1 text-2xl font-black text-white">
                ${platformAnalytics.averageMarketplaceRepairCost.toFixed(2)}
              </p>
              <span className="text-[10px] text-white/50">Prevailing market index</span>
            </div>
          </div>

          {/* Popular Categories & Top Repairs Row */}
          <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
            {/* Category demand */}
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 space-y-3">
              <div className="flex items-center gap-2">
                <Layers className="size-4 text-[#06B6D4]" />
                <h4 className="text-xs font-bold uppercase tracking-wider text-white">
                  Popular Device Categories
                </h4>
              </div>
              <div className="space-y-2">
                {Object.entries(platformAnalytics.popularDeviceCategories).map(([cat, count]) => (
                  <div key={cat} className="space-y-1">
                    <div className="flex justify-between text-xs">
                      <span className="text-white/80">{cat}</span>
                      <span className="font-bold text-white">{count} repairs</span>
                    </div>
                    <div className="h-1.5 w-full rounded-full bg-white/10 overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-[#06B6D4] to-[#22C55E]"
                        style={{ width: `${Math.min(100, (count / 150) * 100)}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Top Requested Repairs */}
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 space-y-3">
              <div className="flex items-center gap-2">
                <Flame className="size-4 text-amber-400" />
                <h4 className="text-xs font-bold uppercase tracking-wider text-white">
                  Most Requested Repair Types
                </h4>
              </div>
              <div className="space-y-2">
                {Object.entries(platformAnalytics.topRequestedRepairs).map(([repair, count]) => (
                  <div key={repair} className="space-y-1">
                    <div className="flex justify-between text-xs">
                      <span className="text-white/80">{repair}</span>
                      <span className="font-bold text-white">{count}</span>
                    </div>
                    <div className="h-1.5 w-full rounded-full bg-white/10 overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-amber-500 to-[#22C55E]"
                        style={{ width: `${Math.min(100, (count / 120) * 100)}%` }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* High Performing Repair Shops Leaderboard */}
          {platformAnalytics.highPerformingShops.length > 0 && (
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-5 space-y-4">
              <div className="flex items-center gap-2">
                <Award className="size-4 text-[#22C55E]" />
                <h4 className="text-xs font-bold uppercase tracking-wider text-white">
                  High-Performing Provider Leaderboard
                </h4>
              </div>

              <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4">
                {platformAnalytics.highPerformingShops.map((shop, i) => (
                  <div
                    key={shop.shopId}
                    className="rounded-xl border border-white/10 bg-white/[0.02] p-3 space-y-2"
                  >
                    <div className="flex items-center justify-between">
                      <span className="text-[10px] font-bold uppercase text-white/40">#{i + 1} Rank</span>
                      <span className="text-xs font-bold text-[#22C55E]">{shop.trustScore}/100 Trust</span>
                    </div>
                    <p className="text-sm font-bold text-white truncate">{shop.shopName}</p>
                    <div className="flex justify-between text-xs text-white/60">
                      <span>{shop.averageRating} ★</span>
                      <span>{shop.totalQuotesAccepted} Jobs ({shop.acceptanceRate}%)</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
