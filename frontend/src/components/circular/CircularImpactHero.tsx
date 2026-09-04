"use client";

import { motion } from "framer-motion";
import { Sparkles, ArrowRight, ShieldCheck, Plus, Target, Zap, RotateCcw } from "lucide-react";
import type { CircularImpactScore, CircularImpactMetrics } from "@/lib/types/circularEconomy";

interface CircularImpactHeroProps {
  score: CircularImpactScore;
  metrics: CircularImpactMetrics;
  onOpenCreateGoal: () => void;
  onOpenRecordEvent: () => void;
}

const TIER_CONFIG: Record<
  string,
  { label: string; color: string; badgeBg: string; border: string; glow: string }
> = {
  CIRCULAR_CHAMPION: {
    label: "Circular Champion",
    color: "text-[#22C55E]",
    badgeBg: "bg-[#22C55E]/15",
    border: "border-[#22C55E]/30",
    glow: "rgba(34, 197, 94, 0.2)",
  },
  ECO_LEADER: {
    label: "Eco Leader",
    color: "text-[#06B6D4]",
    badgeBg: "bg-[#06B6D4]/15",
    border: "border-[#06B6D4]/30",
    glow: "rgba(6, 182, 212, 0.2)",
  },
  SUSTAINABLE: {
    label: "Sustainable",
    color: "text-[#3B82F6]",
    badgeBg: "bg-[#3B82F6]/15",
    border: "border-[#3B82F6]/30",
    glow: "rgba(59, 130, 246, 0.2)",
  },
  DEVELOPING: {
    label: "Developing",
    color: "text-[#F59E0B]",
    badgeBg: "bg-[#F59E0B]/15",
    border: "border-[#F59E0B]/30",
    glow: "rgba(245, 158, 11, 0.2)",
  },
  STARTING: {
    label: "Starting",
    color: "text-white/60",
    badgeBg: "bg-white/10",
    border: "border-white/20",
    glow: "rgba(255, 255, 255, 0.1)",
  },
};

export default function CircularImpactHero({
  score,
  metrics,
  onOpenCreateGoal,
  onOpenRecordEvent,
}: CircularImpactHeroProps) {
  const tierInfo = TIER_CONFIG[score?.tier] || TIER_CONFIG.ECO_LEADER;

  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-br from-[#0F172A]/90 via-[#0B1120]/95 to-[#050914]/90 p-6 md:p-8 backdrop-blur-xl shadow-2xl">
      {/* Background Decorative Glow */}
      <div
        className="pointer-events-none absolute -right-20 -top-20 size-96 rounded-full blur-3xl opacity-30"
        style={{ background: tierInfo.glow }}
      />
      <div className="pointer-events-none absolute -left-20 -bottom-20 size-80 rounded-full blur-3xl opacity-20 bg-[#06B6D4]/20" />

      <div className="relative z-10 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-8">
        {/* Left: Main Impact Narrative */}
        <div className="space-y-4 max-w-2xl">
          <div className="flex flex-wrap items-center gap-3">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-3 py-1 text-xs font-semibold text-emerald-400">
              <Sparkles className="size-3.5" />
              Circular Economy Intelligence
            </span>
            <span
              className={`inline-flex items-center gap-1.5 rounded-full border ${tierInfo.border} ${tierInfo.badgeBg} px-3.5 py-1 text-xs font-bold ${tierInfo.color}`}
            >
              <ShieldCheck className="size-3.5" />
              {tierInfo.label} Tier
            </span>
          </div>

          <h1 className="text-3xl md:text-4xl lg:text-5xl font-extrabold tracking-tight text-white leading-tight">
            Every Repair Powers a{" "}
            <span className="bg-gradient-to-r from-[#22C55E] via-[#06B6D4] to-[#38BDF8] bg-clip-text text-transparent">
              Measurable Planet
            </span>
          </h1>

          <p className="text-sm md:text-base text-slate-300 leading-relaxed">
            Your personal electronics ecosystem has avoided{" "}
            <span className="font-semibold text-emerald-400">
              {metrics?.totalCarbonSavedKg ?? 0} kg CO₂
            </span>
            , diverted{" "}
            <span className="font-semibold text-cyan-400">
              {metrics?.totalEwastePreventedKg ?? 0} kg
            </span>{" "}
            of hazardous e-waste, and extended hardware operational life by{" "}
            <span className="font-semibold text-amber-300">
              {metrics?.totalLifeExtensionDays ?? 0} days
            </span>
            .
          </p>

          {/* Next Best Action Callout */}
          {score?.nextBestAction && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex items-start gap-3 rounded-2xl border border-cyan-500/20 bg-cyan-950/30 p-4"
            >
              <div className="rounded-xl bg-cyan-500/20 p-2 text-cyan-400 shrink-0">
                <Zap className="size-4" />
              </div>
              <div className="text-xs md:text-sm">
                <div className="font-semibold text-cyan-300">Recommended Next Step:</div>
                <div className="text-slate-300 mt-0.5">{score.nextBestAction}</div>
              </div>
            </motion.div>
          )}

          {/* Action Button Row */}
          <div className="flex flex-wrap items-center gap-3 pt-2">
            <button
              onClick={onOpenRecordEvent}
              className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 px-5 py-2.5 text-sm font-semibold text-white shadow-lg shadow-emerald-500/20 hover:brightness-110 active:scale-95 transition-all"
            >
              <Plus className="size-4" />
              Record Circular Action
            </button>
            <button
              onClick={onOpenCreateGoal}
              className="inline-flex items-center gap-2 rounded-xl border border-white/15 bg-white/5 px-4 py-2.5 text-sm font-semibold text-white hover:bg-white/10 active:scale-95 transition-all backdrop-blur-md"
            >
              <Target className="size-4 text-cyan-400" />
              Set Sustainability Target
            </button>
          </div>
        </div>

        {/* Right: Score Counter Badge */}
        <div className="flex flex-col items-center justify-center rounded-2xl border border-white/10 bg-white/[0.03] p-6 lg:min-w-[260px] text-center backdrop-blur-md">
          <div className="text-xs font-semibold uppercase tracking-wider text-slate-400">
            Circular Impact Index
          </div>
          <div className="mt-2 text-5xl md:text-6xl font-black text-white tracking-tight flex items-baseline justify-center gap-1">
            <span className="bg-gradient-to-b from-white via-slate-100 to-slate-400 bg-clip-text text-transparent">
              {score?.score ?? 0}
            </span>
            <span className="text-lg font-bold text-slate-500">/100</span>
          </div>
          <div className="mt-2 inline-flex items-center gap-1.5 text-xs font-semibold text-emerald-400">
            <RotateCcw className="size-3.5" />
            Deterministic AI Engine
          </div>
          <p className="mt-3 text-[11px] text-slate-400 leading-normal max-w-[200px]">
            Based on verifiable repairs, preventative care, and component stewardship.
          </p>
        </div>
      </div>
    </div>
  );
}
