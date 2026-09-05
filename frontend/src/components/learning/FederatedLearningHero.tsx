"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Brain,
  ShieldCheck,
  Sparkles,
  RefreshCw,
  TrendingUp,
  Cpu,
  Lock,
  CheckCircle2,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { LearningDashboardResponse } from "@/lib/types/federatedLearning";

interface FederatedLearningHeroProps {
  dashboard: LearningDashboardResponse;
  onRunLearning: () => void;
  isRunning?: boolean;
}

export default function FederatedLearningHero({
  dashboard,
  onRunLearning,
  isRunning = false,
}: FederatedLearningHeroProps) {
  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-950/90 to-black/90 p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
      {/* Background Ambient Glows */}
      <div className="pointer-events-none absolute -top-24 -left-20 h-72 w-72 rounded-full bg-indigo-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-24 -right-20 h-80 w-80 rounded-full bg-cyan-500/20 blur-3xl" />

      <div className="relative z-10 flex flex-col gap-8 lg:flex-row lg:items-center lg:justify-between">
        {/* Left Content */}
        <div className="max-w-2xl">
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span className="inline-flex items-center gap-2 rounded-full border border-indigo-500/30 bg-indigo-500/10 px-3 py-1 text-xs font-semibold text-indigo-300 uppercase shadow-inner">
              <Brain className="h-3.5 w-3.5 animate-pulse text-indigo-400" />
              Federated Learning Core Active
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/20 bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300">
              <Lock className="h-3.5 w-3.5 text-emerald-400" />
              Zero-PII Privacy Guaranteed (N &ge; 5)
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-cyan-500/20 bg-cyan-500/10 px-3 py-1 text-xs font-medium text-cyan-300">
              <Sparkles className="h-3.5 w-3.5 text-cyan-400" />
              Phase 35 Engine
            </span>
          </div>

          <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
            RepairVerse AI Gets Smarter — Without Exposing Your Data.
          </h1>

          <p className="mt-3 text-sm sm:text-base leading-relaxed text-slate-300">
            Transforms {dashboard.totalAnonymizedRepairs.toLocaleString()} anonymized repair outcomes across {dashboard.totalAnonymizedDevices.toLocaleString()} devices into bounded, versioned, governance-verified intelligence.
          </p>

          {/* Quick status chips */}
          <div className="mt-5 flex flex-wrap items-center gap-2">
            <div className="flex items-center gap-1.5 rounded-lg border border-white/5 bg-slate-950/60 px-2.5 py-1 text-xs text-slate-300 font-mono">
              <Cpu className="h-3 w-3 text-indigo-400" />
              <span>Active Model:</span>
              <strong className="text-indigo-300">{dashboard.activeModelVersion}</strong>
            </div>

            <div className="flex items-center gap-1.5 rounded-lg border border-white/5 bg-slate-950/60 px-2.5 py-1 text-xs text-slate-300 font-mono">
              <ShieldCheck className="h-3 w-3 text-emerald-400" />
              <span>Privacy Score:</span>
              <strong className="text-emerald-300">{dashboard.privacyComplianceScore}%</strong>
            </div>

            <div className="flex items-center gap-1.5 rounded-lg border border-white/5 bg-slate-950/60 px-2.5 py-1 text-xs text-slate-300 font-mono">
              <TrendingUp className="h-3 w-3 text-cyan-400" />
              <span>Improvement:</span>
              <strong className="text-cyan-300">+{dashboard.improvementPercentage}%</strong>
            </div>
          </div>
        </div>

        {/* Right Stats & Trigger */}
        <div className="flex flex-col sm:flex-row items-stretch sm:items-center gap-4">
          {/* Active Model Score Card */}
          <div className="flex flex-col items-center justify-center rounded-2xl border border-white/10 bg-slate-900/80 p-5 text-center backdrop-blur-xl shadow-lg min-w-[160px]">
            <span className="text-[11px] font-bold uppercase tracking-wider text-slate-400">
              Validation Score
            </span>
            <div className="mt-2 flex items-baseline gap-1">
              <span className="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-cyan-400">
                {dashboard.validationScore}%
              </span>
            </div>
            <span className="mt-1 rounded-full bg-indigo-500/10 px-2 py-0.5 text-[10px] font-bold text-indigo-300 border border-indigo-500/20">
              Trust Score {dashboard.trustScore}/100
            </span>
          </div>

          {/* Quick Metrics Column */}
          <div className="flex flex-col gap-2.5 min-w-[170px]">
            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <div className="text-[10px] uppercase font-bold text-slate-400">Signals Extracted</div>
              <div className="text-lg font-bold text-white">{dashboard.activeLearningSignalsCount} Active</div>
            </div>

            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <div className="text-[10px] uppercase font-bold text-slate-400">Validated Patterns</div>
              <div className="text-lg font-bold text-emerald-400">
                {dashboard.validatedPatternsCount} Verified
              </div>
            </div>
          </div>

          {/* Run Learning Cycle Button */}
          <button
            type="button"
            onClick={onRunLearning}
            disabled={isRunning}
            className="flex sm:flex-col items-center justify-center gap-2 rounded-2xl border border-indigo-500/30 bg-gradient-to-br from-indigo-600/30 to-cyan-600/30 p-4 text-xs font-bold text-white hover:brightness-125 transition active:scale-95 disabled:opacity-50 shadow-lg shadow-indigo-500/20"
          >
            <RefreshCw className={cn("h-5 w-5", isRunning && "animate-spin text-cyan-400")} />
            <span>{isRunning ? "Learning..." : "Run Learning Cycle"}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
