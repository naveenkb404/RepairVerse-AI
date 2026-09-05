"use client";

import React from "react";
import { TrendingUp, CheckCircle, DollarSign, Leaf, Zap, Award } from "lucide-react";
import { cn } from "@/lib/utils";
import type { LearningImpactResponse } from "@/lib/types/federatedLearning";

interface LearningImpactDashboardProps {
  impact: LearningImpactResponse;
  className?: string;
}

export default function LearningImpactDashboard({
  impact,
  className,
}: LearningImpactDashboardProps) {
  const metrics = [
    {
      label: "Recommendation Accuracy Gain",
      value: `+${impact.recommendationAccuracyGain}%`,
      subtext: "Ecosystem precision baseline",
      icon: TrendingUp,
      color: "from-cyan-500 to-blue-500",
      textColor: "text-cyan-400",
    },
    {
      label: "Repair Success Improvement",
      value: `+${impact.repairSuccessImprovement}%`,
      subtext: "Validated outcomes",
      icon: CheckCircle,
      color: "from-emerald-500 to-teal-500",
      textColor: "text-emerald-400",
    },
    {
      label: "Cost Prediction Stability",
      value: `${impact.costPredictionStability}%`,
      subtext: "Bounded error variance",
      icon: DollarSign,
      color: "from-indigo-500 to-purple-500",
      textColor: "text-indigo-400",
    },
    {
      label: "CO₂ Optimization Improvement",
      value: `+${impact.co2OptimizationImprovement}%`,
      subtext: "Circular economy savings",
      icon: Leaf,
      color: "from-emerald-400 to-green-500",
      textColor: "text-green-400",
    },
  ];

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            <Award className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Continuous Learning Impact</h3>
            <p className="text-xs text-slate-400">
              Measurable improvements across {impact.totalDecisionsEnriched.toLocaleString()} decisions enriched by federated intelligence
            </p>
          </div>
        </div>

        <span className="rounded-full bg-cyan-500/10 px-2.5 py-0.5 text-xs font-semibold text-cyan-400 border border-cyan-500/20">
          Governed Feedback Loop
        </span>
      </div>

      <div className="mt-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {metrics.map((m, idx) => {
          const Icon = m.icon;
          return (
            <div
              key={idx}
              className="relative overflow-hidden rounded-xl border border-white/5 bg-slate-950/60 p-4 transition hover:border-white/15"
            >
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-slate-400">{m.label}</span>
                <Icon className={cn("h-4 w-4", m.textColor)} />
              </div>

              <div className="mt-3 flex items-baseline gap-2">
                <span className={cn("text-2xl font-black tracking-tight", m.textColor)}>
                  {m.value}
                </span>
              </div>

              <div className="mt-1 text-[11px] text-slate-500 font-medium">
                {m.subtext}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
