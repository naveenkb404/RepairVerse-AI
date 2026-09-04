"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Trophy,
  Sparkles,
  TrendingUp,
  DollarSign,
  Clock,
  Leaf,
  ShieldCheck,
  CheckCircle2,
} from "lucide-react";
import type { OptimizationResponse } from "@/lib/types/digitalTwin";
import { cn } from "@/lib/utils";

interface StrategyOptimizationCardProps {
  optimization: OptimizationResponse;
  onExecutePlan?: () => void;
}

export default function StrategyOptimizationCard({
  optimization,
  onExecutePlan,
}: StrategyOptimizationCardProps) {
  if (!optimization) return null;

  const score = optimization.optimizationScore ?? 92;
  const stratName = (optimization.recommendedStrategy || "PREVENTIVE_MAINTENANCE").replace(/_/g, " ");

  const weights = [
    { label: "Financial Efficiency (25%)", score: optimization.costScore ?? 90, color: "bg-emerald-500" },
    { label: "Reliability (20%)", score: optimization.reliabilityScore ?? 95, color: "bg-cyan-500" },
    { label: "Longevity Gain (15%)", score: optimization.longevityScore ?? 90, color: "bg-teal-500" },
    { label: "Sustainability (15%)", score: optimization.sustainabilityScore ?? 95, color: "bg-lime-500" },
  ];

  return (
    <div className="relative overflow-hidden rounded-3xl border border-cyan-500/30 bg-gradient-to-br from-slate-900 via-slate-950 to-slate-900 p-6 sm:p-8 shadow-2xl backdrop-blur-2xl">
      {/* Decorative Glow */}
      <div className="pointer-events-none absolute top-0 right-0 h-64 w-64 rounded-full bg-cyan-500/15 blur-3xl" />
      <div className="pointer-events-none absolute bottom-0 left-0 h-64 w-64 rounded-full bg-emerald-500/15 blur-3xl" />

      <div className="relative z-10 space-y-6">
        {/* Header Badge & Title */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 rounded-2xl bg-gradient-to-tr from-amber-500/20 to-cyan-500/20 border border-amber-500/30 shadow-inner">
              <Trophy className="h-6 w-6 text-amber-400" />
            </div>
            <div>
              <span className="text-xs font-bold uppercase tracking-wider text-cyan-400">
                Authoritative Deterministic Optimization
              </span>
              <h3 className="text-2xl font-extrabold text-white">
                {stratName}
              </h3>
            </div>
          </div>

          <div className="flex items-center gap-2 rounded-2xl border border-white/10 bg-white/[0.04] px-4 py-2 backdrop-blur-md">
            <span className="text-xs text-slate-400">Optimization Score</span>
            <span className="text-2xl font-extrabold font-mono text-emerald-300">
              {score}<span className="text-xs text-emerald-500 font-normal">/100</span>
            </span>
          </div>
        </div>

        {/* Highlight Metrics */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-4">
            <div className="flex items-center gap-2 text-xs text-slate-400 mb-1">
              <DollarSign className="h-4 w-4 text-emerald-400" />
              <span>Projected Capital Saved</span>
            </div>
            <div className="text-2xl font-bold font-mono text-emerald-300">
              ₹{Math.round(optimization.estimatedSavings || 18500).toLocaleString()}
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">vs new replacement</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-4">
            <div className="flex items-center gap-2 text-xs text-slate-400 mb-1">
              <Clock className="h-4 w-4 text-cyan-400" />
              <span>Lifespan Extended</span>
            </div>
            <div className="text-2xl font-bold font-mono text-cyan-300">
              +{optimization.estimatedLifespanGain || 36} Months
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">Added duty cycle</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-4">
            <div className="flex items-center gap-2 text-xs text-slate-400 mb-1">
              <Leaf className="h-4 w-4 text-lime-400" />
              <span>CO₂ Emissions Prevented</span>
            </div>
            <div className="text-2xl font-bold font-mono text-lime-300">
              {optimization.estimatedCo2Savings || 14.8} kg CO₂
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">Circular economy saving</p>
          </div>
        </div>

        {/* Explainable Decision Blueprint */}
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 text-sm text-slate-200 leading-relaxed">
          <div className="flex items-center gap-2 font-semibold text-white mb-1.5 text-xs uppercase tracking-wide">
            <Sparkles className="h-3.5 w-3.5 text-amber-400" />
            Decision Rationale
          </div>
          <p>{optimization.decisionReason}</p>
        </div>

        {/* Factor Breakdown Bars */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 pt-2">
          {weights.map((w, idx) => (
            <div key={idx} className="space-y-1">
              <div className="flex justify-between text-xs text-slate-300">
                <span className="truncate">{w.label}</span>
                <span className="font-mono font-bold text-white">{w.score}%</span>
              </div>
              <div className="w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
                <div className={cn("h-full rounded-full", w.color)} style={{ width: `${w.score}%` }} />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
