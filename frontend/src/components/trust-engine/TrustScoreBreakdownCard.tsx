"use client";

import React from "react";
import { motion } from "framer-motion";
import { ShieldCheck, ShieldAlert, Cpu, Sparkles, Scale, Database, Clock } from "lucide-react";
import { cn } from "@/lib/utils";
import type { TrustScoreBreakdown } from "@/lib/types/trustEngine";

interface TrustScoreBreakdownCardProps {
  breakdown: TrustScoreBreakdown;
  className?: string;
}

export default function TrustScoreBreakdownCard({
  breakdown,
  className,
}: TrustScoreBreakdownCardProps) {
  const getTierBadge = (tier: string) => {
    switch (tier) {
      case "VERIFIED":
        return {
          label: "Verified Trust",
          color: "border-emerald-500/40 bg-emerald-500/10 text-emerald-400",
          icon: ShieldCheck,
        };
      case "RELIABLE":
        return {
          label: "Reliable Trust",
          color: "border-cyan-500/40 bg-cyan-500/10 text-cyan-400",
          icon: ShieldCheck,
        };
      case "CAUTION":
        return {
          label: "Caution Advised",
          color: "border-amber-500/40 bg-amber-500/10 text-amber-400",
          icon: ShieldAlert,
        };
      case "REVIEW_REQUIRED":
      default:
        return {
          label: "Human Review Required",
          color: "border-rose-500/40 bg-rose-500/10 text-rose-400",
          icon: ShieldAlert,
        };
    }
  };

  const badge = getTierBadge(breakdown.trustTier);
  const TierIcon = badge.icon;

  const components = [
    {
      label: "AI Confidence",
      value: breakdown.confidenceComponent,
      weight: breakdown.confidenceWeight,
      icon: Sparkles,
      color: "from-cyan-500 to-blue-500",
      textColor: "text-cyan-400",
    },
    {
      label: "Evidence Density",
      value: breakdown.evidenceDensityComponent,
      weight: breakdown.evidenceDensityWeight,
      icon: Database,
      color: "from-indigo-500 to-purple-500",
      textColor: "text-indigo-400",
    },
    {
      label: "System Reliability",
      value: breakdown.systemReliabilityComponent,
      weight: breakdown.systemReliabilityWeight,
      icon: Cpu,
      color: "from-emerald-500 to-teal-500",
      textColor: "text-emerald-400",
    },
    {
      label: "Governance Compliance",
      value: breakdown.governanceComplianceComponent,
      weight: breakdown.governanceComplianceWeight,
      icon: Scale,
      color: "from-amber-500 to-orange-500",
      textColor: "text-amber-400",
    },
    {
      label: "Data Freshness",
      value: breakdown.dataFreshnessComponent,
      weight: breakdown.dataFreshnessWeight,
      icon: Clock,
      color: "from-pink-500 to-rose-500",
      textColor: "text-pink-400",
    },
  ];

  return (
    <div
      className={cn(
        "relative overflow-hidden rounded-2xl border border-white/10 bg-gradient-to-b from-slate-900/90 to-slate-950/90 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      {/* Header with final trust score */}
      <div className="flex items-center justify-between border-b border-white/10 pb-5">
        <div>
          <span className="text-xs font-semibold uppercase tracking-wider text-slate-400">
            Trust Intelligence Score
          </span>
          <div className="mt-1 flex items-baseline gap-2">
            <span className="text-4xl font-extrabold tracking-tight text-white">
              {breakdown.finalTrustScore}
            </span>
            <span className="text-sm font-medium text-slate-500">/ 100</span>
          </div>
        </div>

        <div className={cn("flex items-center gap-2 rounded-full border px-3 py-1.5 text-xs font-semibold", badge.color)}>
          <TierIcon className="h-4 w-4" />
          <span>{badge.label}</span>
        </div>
      </div>

      {/* Component progress bars */}
      <div className="mt-6 space-y-4">
        {components.map((c, i) => {
          const Icon = c.icon;
          return (
            <div key={i} className="space-y-1.5">
              <div className="flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-slate-300 font-medium">
                  <Icon className={cn("h-3.5 w-3.5", c.textColor)} />
                  {c.label}
                  <span className="text-[10px] text-slate-500 font-mono">
                    ({Math.round(c.weight * 100)}% weight)
                  </span>
                </span>
                <span className={cn("font-bold font-mono", c.textColor)}>
                  {c.value}%
                </span>
              </div>
              <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800/80 p-0.5">
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: `${Math.min(Math.max(c.value, 0), 100)}%` }}
                  transition={{ duration: 0.8, delay: i * 0.1 }}
                  className={cn("h-full rounded-full bg-gradient-to-r", c.color)}
                />
              </div>
            </div>
          );
        })}
      </div>

      {/* Mathematical transparency formula notice */}
      <div className="mt-5 rounded-xl border border-white/5 bg-white/[0.02] p-3 text-[11px] text-slate-400">
        <span className="font-semibold text-slate-300">Deterministic Computation: </span>
        Weighted composite of AI confidence, evidence density, historical system resolution fidelity, rule compliance, and telemetry age.
      </div>
    </div>
  );
}
