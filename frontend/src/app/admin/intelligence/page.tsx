"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Activity,
  AlertOctagon,
  AlertTriangle,
  ArrowLeft,
  CheckCircle2,
  ChevronRight,
  Clock,
  Cpu,
  DollarSign,
  Filter,
  Layers,
  Leaf,
  RefreshCw,
  Search,
  Shield,
  ShieldAlert,
  Sparkles,
  TrendingDown,
  TrendingUp,
  User,
  Wrench,
  Zap,
} from "lucide-react";
import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import { fetchAdminIntelligenceSummary, fetchAdminPlatformFleet } from "@/lib/api/analytics";
import { useAuth } from "@/lib/context/AuthContext";
import type { AdminIntelligenceSummaryData } from "@/lib/types/analytics";
import type { PredictiveFleetOverviewData, RiskLevel } from "@/lib/types/prediction";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function AdminIntelligencePage() {
  const { token, user } = useAuth();
  const [summary, setSummary] = useState<AdminIntelligenceSummaryData | null>(null);
  const [fleet, setFleet] = useState<PredictiveFleetOverviewData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedRiskFilter, setSelectedRiskFilter] = useState<string>("ALL");

  const loadData = async () => {
    const [summaryRes, fleetRes] = await Promise.all([
      fetchAdminIntelligenceSummary(token),
      fetchAdminPlatformFleet(token),
    ]);
    if (summaryRes.data) setSummary(summaryRes.data);
    if (fleetRes.data) setFleet(fleetRes.data);
    setIsLoading(false);
  };

  useEffect(() => {
    loadData();
  }, [token]);

  const handleRefresh = async () => {
    setIsRefreshing(true);
    await loadData();
    setIsRefreshing(false);
  };

  const getRiskBadge = (risk: string) => {
    switch (risk) {
      case "CRITICAL":
        return "bg-red-500/15 border-red-500/30 text-red-400";
      case "HIGH":
        return "bg-orange-500/15 border-orange-500/30 text-orange-400";
      case "MEDIUM":
        return "bg-yellow-500/15 border-yellow-500/30 text-yellow-400";
      case "LOW":
        return "bg-emerald-500/15 border-emerald-500/30 text-emerald-400";
      default:
        return "bg-cyan-500/15 border-cyan-500/30 text-cyan-400";
    }
  };

  const filteredDevices = summary?.recentHighRiskDevices.filter((dev) => {
    const matchesSearch =
      dev.deviceName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      dev.userEmail.toLowerCase().includes(searchQuery.toLowerCase()) ||
      dev.primaryFaultType.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesRisk =
      selectedRiskFilter === "ALL" || dev.riskLevel === selectedRiskFilter;
    return matchesSearch && matchesRisk;
  });

  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Ambient background lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.12),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* Top Header */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Logo size="sm" href="/" />
            <div className="hidden h-5 w-px bg-white/15 sm:block" />
            <div className="hidden items-center gap-1.5 rounded-full border border-purple-500/30 bg-purple-500/10 px-3 py-1 text-xs font-bold text-purple-300 sm:flex">
              <Shield className="size-3.5" />
              <span>Admin Center</span>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handleRefresh}
              disabled={isRefreshing}
              className="flex size-9 items-center justify-center rounded-xl border border-white/10 bg-white/[0.04] text-white/70 hover:bg-white/10 hover:text-white transition"
              title="Refresh intelligence data"
            >
              <RefreshCw className={`size-4 ${isRefreshing ? "animate-spin text-[#22C55E]" : ""}`} />
            </button>
            <GlassButton href="/dashboard" variant="secondary" size="sm" icon={<ArrowLeft className="size-3.5" />}>
              Dashboard
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* Main Content */}
      <main className="relative py-8 sm:py-12">
        <Container className="space-y-8">
          {/* Page Hero Title */}
          <motion.div
            initial={{ opacity: 0, y: -12 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, ease: EASE }}
            className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <div className="flex items-center gap-2 mb-1">
                <h1 className="text-2xl font-black tracking-tight text-white sm:text-3xl">
                  Platform Predictive Intelligence
                </h1>
                {summary?.isDemo && (
                  <span className="rounded-full bg-cyan-500/10 px-2.5 py-0.5 text-[10px] font-bold text-cyan-400 border border-cyan-500/30">
                    DEMO SIMULATION
                  </span>
                )}
              </div>
              <p className="text-xs sm:text-sm text-white/60">
                Global fleet health, failure mode risk distributions, and preventable maintenance economics.
              </p>
            </div>
          </motion.div>

          {/* Key KPI Metric Cards */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <GlassCard className="p-5 border-white/10">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs uppercase tracking-wider text-white/50">Total Evaluated Fleet</span>
                <div className="flex size-8 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400">
                  <Cpu className="size-4" />
                </div>
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-black text-white">
                  {summary?.totalPredictionsGenerated.toLocaleString() ?? "—"}
                </span>
                <span className="text-xs text-white/40">devices</span>
              </div>
              <p className="text-[11px] text-white/50 mt-2 flex items-center gap-1">
                <Activity className="size-3 text-[#22C55E]" />
                Avg Score: {summary?.platformAverageHealthScore ?? 76}/100
              </p>
            </GlassCard>

            <GlassCard className="p-5 border-red-500/20">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs uppercase tracking-wider text-red-300">At-Risk Fleet</span>
                <div className="flex size-8 items-center justify-center rounded-xl bg-red-500/10 text-red-400">
                  <AlertOctagon className="size-4" />
                </div>
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-black text-red-400">
                  {((summary?.devicesAtCriticalRisk ?? 0) + (summary?.devicesAtHighRisk ?? 0)).toLocaleString()}
                </span>
                <span className="text-xs text-red-300/60">critical & high</span>
              </div>
              <p className="text-[11px] text-red-400/70 mt-2">
                {summary?.devicesAtCriticalRisk ?? 0} Critical &bull; {summary?.devicesAtHighRisk ?? 0} High Risk
              </p>
            </GlassCard>

            <GlassCard className="p-5 border-emerald-500/20">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs uppercase tracking-wider text-emerald-300">Preventable Savings</span>
                <div className="flex size-8 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-400">
                  <DollarSign className="size-4" />
                </div>
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-black text-emerald-400">
                  ${(summary?.totalPreventableSavings ?? 0).toLocaleString()}
                </span>
                <span className="text-xs text-white/40">USD</span>
              </div>
              <p className="text-[11px] text-white/50 mt-2">
                From ${(summary?.totalProjectedFailureCost ?? 0).toLocaleString()} projected failures
              </p>
            </GlassCard>

            <GlassCard className="p-5 border-[#22C55E]/20">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs uppercase tracking-wider text-emerald-300">Environmental Savings</span>
                <div className="flex size-8 items-center justify-center rounded-xl bg-[#22C55E]/10 text-[#22C55E]">
                  <Leaf className="size-4" />
                </div>
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-black text-[#22C55E]">
                  {(summary?.platformCo2ImpactKg ?? 0).toLocaleString()}
                </span>
                <span className="text-xs text-white/40">kg CO₂</span>
              </div>
              <p className="text-[11px] text-white/50 mt-2 flex items-center gap-1">
                <TrendingUp className="size-3 text-[#22C55E]" />
                Emissions averted via early repairs
              </p>
            </GlassCard>
          </div>

          {/* Platform Fleet Risk Distribution */}
          {fleet?.riskDistribution && (
            <GlassCard className="p-6">
              <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-5">
                <div className="flex items-center gap-2">
                  <Layers className="size-5 text-[#06B6D4]" />
                  <h2 className="text-base font-bold text-white">Platform Fleet Risk Stratification</h2>
                </div>
                <span className="text-xs text-white/50">{fleet.totalDevices} Total Units Tracked</span>
              </div>

              <div className="space-y-4">
                {fleet.riskDistribution.map((item) => {
                  const badge = getRiskBadge(item.riskLevel);
                  return (
                    <div key={item.riskLevel} className="space-y-1.5">
                      <div className="flex items-center justify-between text-xs">
                        <div className="flex items-center gap-2">
                          <span className={`rounded-md border px-2 py-0.5 text-[10px] font-bold ${badge}`}>
                            {item.riskLevel}
                          </span>
                          <span className="text-white/60">{item.count} devices</span>
                        </div>
                        <span className="font-mono text-white/80 font-bold">{item.percentage}%</span>
                      </div>
                      <div className="h-2 w-full overflow-hidden rounded-full bg-white/5">
                        <div
                          className={`h-full transition-all duration-500 ${
                            item.riskLevel === "CRITICAL"
                              ? "bg-red-500"
                              : item.riskLevel === "HIGH"
                              ? "bg-orange-500"
                              : item.riskLevel === "MEDIUM"
                              ? "bg-yellow-400"
                              : item.riskLevel === "LOW"
                              ? "bg-emerald-400"
                              : "bg-cyan-400"
                          }`}
                          style={{ width: `${Math.max(2, item.percentage)}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            </GlassCard>
          )}

          {/* Top Failing Categories Table */}
          {summary?.topFailingCategories && summary.topFailingCategories.length > 0 && (
            <GlassCard className="p-6">
              <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
                <div className="flex items-center gap-2">
                  <Wrench className="size-5 text-orange-400" />
                  <h2 className="text-base font-bold text-white">Top Hardware Failure Modes by Category</h2>
                </div>
                <span className="text-xs text-white/50">Ranked by risk incidence rate</span>
              </div>

              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead>
                    <tr className="border-b border-white/10 text-white/40 uppercase tracking-wider text-[10px]">
                      <th className="pb-3 font-semibold">Device Category</th>
                      <th className="pb-3 font-semibold">Active Fleet</th>
                      <th className="pb-3 font-semibold">At-Risk Count</th>
                      <th className="pb-3 font-semibold">Failure Rate</th>
                      <th className="pb-3 font-semibold">Primary Fault Type</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/5">
                    {summary.topFailingCategories.map((cat, idx) => (
                      <tr key={idx} className="hover:bg-white/[0.02] transition">
                        <td className="py-3.5 font-bold text-white flex items-center gap-2">
                          <Cpu className="size-3.5 text-[#06B6D4]" />
                          {cat.category}
                        </td>
                        <td className="py-3.5 text-white/70">{cat.deviceCount} units</td>
                        <td className="py-3.5 text-red-400 font-semibold">{cat.atRiskCount} at risk</td>
                        <td className="py-3.5">
                          <div className="flex items-center gap-2">
                            <span className="font-mono font-bold text-orange-400">{cat.riskPercentage}%</span>
                            <div className="h-1.5 w-16 overflow-hidden rounded-full bg-white/10">
                              <div className="h-full bg-orange-400" style={{ width: `${cat.riskPercentage}%` }} />
                            </div>
                          </div>
                        </td>
                        <td className="py-3.5 text-white/80">
                          <span className="rounded-lg bg-white/[0.04] border border-white/10 px-2 py-1 text-[11px]">
                            {cat.primaryFaultType}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </GlassCard>
          )}

          {/* High & Critical Risk Device Registry */}
          <GlassCard className="p-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-4 mb-4">
              <div className="flex items-center gap-2">
                <ShieldAlert className="size-5 text-red-400" />
                <div>
                  <h2 className="text-base font-bold text-white">Critical & High Risk Devices</h2>
                  <p className="text-[11px] text-white/50">
                    Live hardware monitoring with high failure probabilities
                  </p>
                </div>
              </div>

              {/* Filters */}
              <div className="flex flex-wrap items-center gap-2">
                <div className="relative">
                  <Search className="size-3.5 absolute left-3 top-2.5 text-white/40" />
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    placeholder="Search device, email, fault..."
                    className="rounded-xl border border-white/10 bg-white/[0.04] pl-8 pr-3 py-1.5 text-xs text-white placeholder-white/40 focus:border-[#22C55E] focus:outline-none"
                  />
                </div>

                <select
                  value={selectedRiskFilter}
                  onChange={(e) => setSelectedRiskFilter(e.target.value)}
                  className="rounded-xl border border-white/10 bg-[#0B1120] px-3 py-1.5 text-xs text-white focus:border-[#22C55E] focus:outline-none"
                >
                  <option value="ALL">All Risk Levels</option>
                  <option value="CRITICAL">Critical Only</option>
                  <option value="HIGH">High Only</option>
                </select>
              </div>
            </div>

            {/* Device list */}
            {filteredDevices && filteredDevices.length > 0 ? (
              <div className="divide-y divide-white/5">
                {filteredDevices.map((dev) => (
                  <div
                    key={dev.deviceId}
                    className="py-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 hover:bg-white/[0.02] px-2 rounded-xl transition"
                  >
                    <div className="flex items-start gap-3">
                      <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-white/[0.06] text-white/80 mt-0.5">
                        <Cpu className="size-4" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <h3 className="text-sm font-bold text-white">{dev.deviceName}</h3>
                          <span
                            className={`rounded-md border px-2 py-0.5 text-[9px] font-bold ${getRiskBadge(
                              dev.riskLevel
                            )}`}
                          >
                            {dev.riskLevel}
                          </span>
                        </div>
                        <div className="flex flex-wrap items-center gap-3 text-xs text-white/50 mt-1">
                          <span className="flex items-center gap-1">
                            <User className="size-3" />
                            {dev.userEmail}
                          </span>
                          <span>&bull;</span>
                          <span className="text-orange-300">{dev.primaryFaultType}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center gap-4 self-end sm:self-center">
                      <div className="text-right">
                        <span className="text-[10px] text-white/40 block">Prediction Index</span>
                        <span className="text-sm font-black text-red-400">{dev.predictionScore}/100</span>
                      </div>
                      <GlassButton
                        href={`/devices/${dev.deviceId}`}
                        variant="secondary"
                        size="sm"
                        icon={<ChevronRight className="size-3" />}
                      >
                        Inspect
                      </GlassButton>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="py-12 text-center text-xs text-white/40">
                No high-risk devices match your filter query.
              </div>
            )}
          </GlassCard>
        </Container>
      </main>
    </div>
  );
}
