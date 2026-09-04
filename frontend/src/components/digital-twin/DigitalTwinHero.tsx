"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Activity,
  Cpu,
  RefreshCw,
  Sparkles,
  ShieldAlert,
  Layers,
  Zap,
  CheckCircle2,
  TrendingUp,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { DigitalTwinSnapshotResponse, OptimizationResponse } from "@/lib/types/digitalTwin";

interface DigitalTwinHeroProps {
  deviceName: string;
  deviceCategory: string;
  snapshot: DigitalTwinSnapshotResponse;
  optimalStrategy?: OptimizationResponse;
  isRefreshing?: boolean;
  onRefresh: () => void;
  onOpenSimulate: () => void;
}

export default function DigitalTwinHero({
  deviceName,
  deviceCategory,
  snapshot,
  optimalStrategy,
  isRefreshing = false,
  onRefresh,
  onOpenSimulate,
}: DigitalTwinHeroProps) {
  const score = snapshot?.overallEcosystemScore ?? 85;
  const confidence = Math.round((snapshot?.simulationConfidence ?? 0.92) * 100);

  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-950/90 to-black/90 p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
      {/* Background Ambient Glows */}
      <div className="pointer-events-none absolute -top-24 -left-20 h-72 w-72 rounded-full bg-cyan-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-24 -right-20 h-80 w-80 rounded-full bg-emerald-500/20 blur-3xl" />

      <div className="relative z-10 flex flex-col gap-8 lg:flex-row lg:items-center lg:justify-between">
        {/* Left: Device & Twin Meta */}
        <div className="max-w-2xl">
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span className="inline-flex items-center gap-2 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-3 py-1 text-xs font-semibold text-cyan-300 uppercase shadow-inner">
              <Cpu className="h-3.5 w-3.5 animate-pulse text-cyan-400" />
              Digital Twin Simulation Active
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/20 bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300">
              <Activity className="h-3.5 w-3.5" />
              Phase 33 Twin Engine
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-slate-700 bg-slate-800/60 px-2.5 py-0.5 text-xs text-slate-300">
              {deviceCategory}
            </span>
          </div>

          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight text-white">
            {deviceName}{" "}
            <span className="bg-gradient-to-r from-cyan-400 via-teal-300 to-emerald-400 bg-clip-text text-transparent">
              Digital Twin
            </span>
          </h1>

          <p className="mt-3 text-sm sm:text-base text-slate-300 leading-relaxed">
            Predictive simulation model projecting health decay, multi-horizon failure risks, cost escalations, and deterministic lifecycle strategies before real-world actions are committed.
          </p>

          {/* Action CTAs */}
          <div className="mt-6 flex flex-wrap items-center gap-4">
            <button
              id="refresh-digital-twin-btn"
              onClick={onRefresh}
              disabled={isRefreshing}
              className="group relative inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-emerald-500 px-5 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-cyan-500/20 transition-all hover:scale-[1.02] hover:shadow-emerald-500/30 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <RefreshCw className={cn("h-4 w-4", isRefreshing && "animate-spin")} />
              <span>{isRefreshing ? "Recalibrating..." : "Recalibrate Twin"}</span>
              <Sparkles className="h-4 w-4 opacity-70 group-hover:rotate-12 transition-transform" />
            </button>

            <button
              id="run-custom-simulation-btn"
              onClick={onOpenSimulate}
              className="inline-flex items-center gap-2 rounded-xl border border-white/15 bg-white/[0.05] px-5 py-3 text-sm font-medium text-slate-200 transition-all hover:bg-white/10 hover:text-white"
            >
              <Zap className="h-4 w-4 text-cyan-400" />
              <span>Simulate Custom Scenario</span>
            </button>
          </div>
        </div>

        {/* Right: Quick Stat Matrices */}
        <div className="grid grid-cols-2 gap-3 sm:gap-4 sm:w-full lg:w-auto lg:min-w-[340px]">
          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Ecosystem Score</span>
              <Activity className="h-4 w-4 text-emerald-400" />
            </div>
            <div className="mt-2 text-3xl font-extrabold text-emerald-300 font-mono">
              {score}<span className="text-sm text-emerald-500 font-normal">/100</span>
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">Authoritative health index</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Simulation Confidence</span>
              <CheckCircle2 className="h-4 w-4 text-cyan-400" />
            </div>
            <div className="mt-2 text-3xl font-extrabold text-cyan-300 font-mono">
              {confidence}%
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">Deterministic calibration</p>
          </div>

          <div className="col-span-2 rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium mb-1">
              <span>Optimal Action Blueprint</span>
              <TrendingUp className="h-4 w-4 text-amber-400" />
            </div>
            <div className="text-sm font-semibold text-white">
              {optimalStrategy?.recommendedStrategy?.replace(/_/g, " ") ?? "PREVENTIVE MAINTENANCE"}
            </div>
            <p className="text-[11px] text-emerald-400 mt-0.5 flex items-center gap-1">
              <span>Est. Savings: ₹{Math.round(optimalStrategy?.estimatedSavings ?? 18500).toLocaleString()}</span>
              <span>•</span>
              <span>+{optimalStrategy?.estimatedLifespanGain ?? 36}M Lifespan</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
