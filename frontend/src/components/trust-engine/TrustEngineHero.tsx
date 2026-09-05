"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  ShieldCheck,
  ShieldAlert,
  Cpu,
  Sparkles,
  Layers,
  Activity,
  CheckCircle2,
  RefreshCw,
  Scale,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { TrustDashboardResponse } from "@/lib/types/trustEngine";

interface TrustEngineHeroProps {
  dashboard: TrustDashboardResponse;
  onRefresh: () => void;
  isRefreshing?: boolean;
}

export default function TrustEngineHero({
  dashboard,
  onRefresh,
  isRefreshing = false,
}: TrustEngineHeroProps) {
  const avgTrust = Math.round(dashboard.averageTrustScore ?? 85);
  const total = dashboard.totalDecisions || 1;
  const verifiedPct = Math.round(((dashboard.verifiedCount ?? 0) / total) * 100);

  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-950/90 to-black/90 p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
      {/* Background Ambient Glows */}
      <div className="pointer-events-none absolute -top-24 -left-20 h-72 w-72 rounded-full bg-cyan-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-24 -right-20 h-80 w-80 rounded-full bg-indigo-500/20 blur-3xl" />

      <div className="relative z-10 flex flex-col gap-8 lg:flex-row lg:items-center lg:justify-between">
        {/* Left Header */}
        <div className="max-w-2xl">
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span className="inline-flex items-center gap-2 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-3 py-1 text-xs font-semibold text-cyan-300 uppercase shadow-inner">
              <ShieldCheck className="h-3.5 w-3.5 animate-pulse text-cyan-400" />
              AI Trust & Explainability Engine Active
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-indigo-500/20 bg-indigo-500/10 px-3 py-1 text-xs font-medium text-indigo-300">
              <Scale className="h-3.5 w-3.5" />
              Phase 34 Governance
            </span>
          </div>

          <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
            Ecosystem Decision Trust & Governance
          </h1>

          <p className="mt-3 text-sm sm:text-base leading-relaxed text-slate-300">
            Real-time causal explainability, deterministic trust verification, multi-signal evidence tracing, and user-governed autonomous guardrails across all RepairVerse AI models.
          </p>

          {/* System status chips */}
          <div className="mt-5 flex flex-wrap items-center gap-2">
            {dashboard.systemStats?.map((s) => (
              <div
                key={s.sourceSystem}
                className="flex items-center gap-1.5 rounded-lg border border-white/5 bg-slate-950/60 px-2.5 py-1 text-xs text-slate-300 font-mono"
              >
                <Cpu className="h-3 w-3 text-cyan-400" />
                <span>{s.sourceSystem.replace(/_/g, " ")}:</span>
                <span className="font-bold text-cyan-300">{Math.round(s.averageTrustScore)}% Trust</span>
              </div>
            ))}
          </div>
        </div>

        {/* Right Stats & Gauge */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
          {/* Trust Score Card */}
          <div className="flex flex-col items-center justify-center rounded-2xl border border-white/10 bg-slate-900/80 p-5 text-center backdrop-blur-xl shadow-lg min-w-[160px]">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
              Avg Trust Score
            </span>
            <div className="mt-2 flex items-baseline gap-1">
              <span className="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-indigo-400">
                {avgTrust}
              </span>
              <span className="text-xs text-slate-500 font-mono">/ 100</span>
            </div>
            <span className="mt-1 rounded-full bg-emerald-500/10 px-2 py-0.5 text-[10px] font-bold text-emerald-400 border border-emerald-500/20">
              {verifiedPct}% Verified
            </span>
          </div>

          {/* Quick Metrics Column */}
          <div className="flex flex-col gap-2.5 min-w-[170px]">
            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <div className="text-[10px] uppercase font-bold text-slate-400">Total Decisions</div>
              <div className="text-lg font-bold text-white">{dashboard.totalDecisions} Logged</div>
            </div>

            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <div className="text-[10px] uppercase font-bold text-slate-400">User Reviews</div>
              <div className="text-lg font-bold text-emerald-400">
                {dashboard.decisionsReviewedByUser} Confirmed
              </div>
            </div>
          </div>

          {/* Refresh Button */}
          <button
            type="button"
            onClick={onRefresh}
            disabled={isRefreshing}
            className="flex sm:flex-col items-center justify-center gap-2 rounded-2xl border border-white/10 bg-white/5 p-4 text-xs font-bold text-slate-300 hover:bg-white/10 hover:text-white transition active:scale-95 disabled:opacity-50"
          >
            <RefreshCw className={cn("h-5 w-5", isRefreshing && "animate-spin text-cyan-400")} />
            <span>{isRefreshing ? "Refreshing..." : "Refresh"}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
