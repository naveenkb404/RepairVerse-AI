"use client";

import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  Shield, AlertTriangle, CheckCircle2, Activity, TrendingUp,
  RefreshCw, Network, BarChart3, Zap, Clock
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import MarketplaceAnomalyPanel from "@/components/marketplace/MarketplaceAnomalyPanel";
import { networkIntelligenceApi, getDemoHealth, getDemoAnomalies } from "@/lib/api/networkIntelligence";
import type { NetworkHealthResponse, MarketplaceAnomalyResponse } from "@/lib/types/networkIntelligence";

export default function AdminNetworkIntelligencePage() {
  const [health,    setHealth]    = useState<NetworkHealthResponse>(getDemoHealth());
  const [anomalies, setAnomalies] = useState<MarketplaceAnomalyResponse[]>(getDemoAnomalies());
  const [isDemo,    setIsDemo]    = useState(true);
  const [loading,   setLoading]   = useState(false);

  const loadData = async () => {
    setLoading(true);
    try {
      const [h, anom] = await Promise.all([
        networkIntelligenceApi.getNetworkHealth(),
        networkIntelligenceApi.getAnomalies(),
      ]);
      setHealth(h);
      setAnomalies(anom);
      setIsDemo(false);
    } catch {
      // stay in demo mode
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleUpdateAnomalyStatus = async (id: string, status: string) => {
    const updated = await networkIntelligenceApi.updateAnomalyStatus(id, status);
    if (updated) {
      setAnomalies(prev => prev.map(a => a.id === id ? updated : a));
    }
  };

  const statusColor = health.overallStatus === "HEALTHY" ? "text-emerald-400"
    : health.overallStatus === "MONITORING" ? "text-amber-400" : "text-red-400";
  const statusBg = health.overallStatus === "HEALTHY" ? "bg-emerald-500/15 border-emerald-500/30"
    : health.overallStatus === "MONITORING" ? "bg-amber-500/15 border-amber-500/30" : "bg-red-500/15 border-red-500/30";

  return (
    <main className="min-h-screen bg-[#0a0a0f] text-white">
      <div className="max-w-screen-xl mx-auto px-4 sm:px-6 lg:px-8 py-10">

        {/* Header */}
        <div className="flex items-start justify-between flex-wrap gap-4 mb-8">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-red-500 to-orange-600 flex items-center justify-center">
                <Shield className="w-4.5 h-4.5 text-white" />
              </div>
              <h1 className="text-2xl font-bold bg-gradient-to-r from-white to-slate-400 bg-clip-text text-transparent">
                Network Trust Admin
              </h1>
            </div>
            <p className="text-slate-400 text-sm">
              Platform health, anomaly review, and shop risk profiles. Detect → Flag → Admin Review only.
            </p>
          </div>
          <div className="flex items-center gap-3">
            {isDemo && (
              <span className="px-3 py-1.5 rounded-lg bg-violet-500/15 border border-violet-500/30 text-xs text-violet-400">Demo Mode</span>
            )}
            <span className={`px-3 py-1.5 rounded-lg border text-xs font-medium ${statusBg} ${statusColor}`}>
              {health.overallStatus}
            </span>
            <button
              onClick={loadData}
              disabled={loading}
              className="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/[0.06] border border-white/10 text-sm text-slate-300 hover:bg-white/[0.08] transition-colors disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
              Refresh
            </button>
          </div>
        </div>

        {/* Platform Health Metrics */}
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 mb-8">
          {[
            { label: "Total Shops",    value: health.totalShops,              icon: Network,        color: "text-violet-400" },
            { label: "Elite Tier",     value: health.eliteShops,              icon: Zap,            color: "text-amber-400" },
            { label: "Excellent",      value: health.excellentShops,          icon: BarChart3,      color: "text-emerald-400" },
            { label: "Open Anomalies", value: health.openAnomalies,           icon: AlertTriangle,  color: health.openAnomalies > 0 ? "text-red-400" : "text-slate-400" },
            { label: "Critical Flags", value: health.criticalAnomalies,      icon: TrendingUp,     color: health.criticalAnomalies > 0 ? "text-red-400" : "text-emerald-400" },
            { label: "Platform Q",     value: `${health.platformQualityScore?.toFixed(0) ?? 80}/100`, icon: Activity, color: "text-cyan-400" },
          ].map((stat, i) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.05 }}
            >
              <GlassCard className="p-3.5 h-full">
                <stat.icon className={`w-4 h-4 ${stat.color} mb-2`} />
                <p className="text-xl font-bold text-white">{stat.value}</p>
                <p className="text-[10px] text-slate-400 mt-0.5">{stat.label}</p>
              </GlassCard>
            </motion.div>
          ))}
        </div>

        {/* Shop Tier Distribution */}
        <GlassCard className="p-6 mb-6">
          <div className="flex items-center gap-2 mb-5">
            <BarChart3 className="w-5 h-5 text-violet-400" />
            <h2 className="text-base font-semibold text-white">Shop Tier Distribution</h2>
          </div>
          <div className="grid grid-cols-5 gap-3">
            {[
              { tier: "Elite",            count: health.eliteShops,            pct: (health.eliteShops / health.totalShops) * 100,            color: "#f59e0b" },
              { tier: "Excellent",        count: health.excellentShops,        pct: (health.excellentShops / health.totalShops) * 100,        color: "#10b981" },
              { tier: "Trusted",          count: health.trustedShops,          pct: (health.trustedShops / health.totalShops) * 100,          color: "#06b6d4" },
              { tier: "Standard",         count: health.standardShops,         pct: (health.standardShops / health.totalShops) * 100,         color: "#8b5cf6" },
              { tier: "Needs Attention",  count: health.needsImprovementShops, pct: (health.needsImprovementShops / health.totalShops) * 100, color: "#ef4444" },
            ].map((item) => (
              <div key={item.tier} className="text-center">
                <div className="h-20 bg-white/[0.04] rounded-lg overflow-hidden flex items-end mb-2">
                  <motion.div
                    className="w-full rounded-lg"
                    style={{ backgroundColor: item.color + "60" }}
                    initial={{ height: 0 }}
                    animate={{ height: `${Math.max(8, item.pct)}%` }}
                    transition={{ duration: 0.8, ease: [0.22, 1, 0.36, 1] }}
                  />
                </div>
                <p className="text-base font-bold text-white">{item.count}</p>
                <p className="text-[10px] text-slate-400">{item.tier}</p>
              </div>
            ))}
          </div>
        </GlassCard>

        {/* Anomaly Management */}
        <div className="mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-semibold text-white flex items-center gap-2">
              <AlertTriangle className="w-5 h-5 text-orange-400" />
              Anomaly Management
            </h2>
            <p className="text-xs text-slate-500">
              Detection → Flag → Admin Review · No automatic enforcement
            </p>
          </div>
          <MarketplaceAnomalyPanel
            anomalies={anomalies}
            onUpdateStatus={handleUpdateAnomalyStatus}
            isAdmin={true}
          />
        </div>

        {/* Platform Trust Statement */}
        <GlassCard className="p-5 bg-gradient-to-r from-emerald-500/5 to-cyan-500/5 border-emerald-500/20">
          <div className="flex items-start gap-3">
            <CheckCircle2 className="w-5 h-5 text-emerald-400 flex-shrink-0 mt-0.5" />
            <div>
              <p className="text-sm font-medium text-white mb-1">Platform Trust Principle</p>
              <p className="text-xs text-slate-400 leading-relaxed">
                All trust scores, quality scores, reliability scores, and anomaly detection are strictly deterministic —
                no AI inference is used. Anomaly flags follow a <span className="text-slate-300">Detect → Flag → Admin Review</span> pattern.
                Shops are never automatically penalised, blocked, or delisted.
              </p>
            </div>
          </div>
        </GlassCard>
      </div>
    </main>
  );
}
