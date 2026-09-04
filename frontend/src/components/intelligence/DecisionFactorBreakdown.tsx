"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Layers,
  CheckCircle2,
  AlertTriangle,
  MinusCircle,
  HelpCircle,
} from "lucide-react";
import type { DecisionFactor } from "@/lib/types/deviceIntelligence";

interface DecisionFactorBreakdownProps {
  factors: DecisionFactor[];
}

export const DecisionFactorBreakdown: React.FC<DecisionFactorBreakdownProps> = ({
  factors,
}) => {
  const getImpactBadge = (impact: string) => {
    switch (impact) {
      case "POSITIVE":
        return {
          icon: CheckCircle2,
          color: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30",
          barColor: "from-emerald-500 to-teal-400",
        };
      case "NEGATIVE":
        return {
          icon: AlertTriangle,
          color: "bg-rose-500/20 text-rose-300 border-rose-500/30",
          barColor: "from-rose-500 to-amber-500",
        };
      default:
        return {
          icon: MinusCircle,
          color: "bg-cyan-500/20 text-cyan-300 border-cyan-500/30",
          barColor: "from-cyan-500 to-blue-400",
        };
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl space-y-6 shadow-xl"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Layers className="w-5 h-5 text-emerald-400" />
          <h3 className="text-lg font-bold text-white tracking-wide">
            Decision Factor Decomposition
          </h3>
        </div>
        <span className="text-xs font-semibold text-slate-400">
          7 Weighted Intelligence Dimensions
        </span>
      </div>

      <div className="space-y-4">
        {factors.map((factor, idx) => {
          const impact = getImpactBadge(factor.impact);
          const ImpactIcon = impact.icon;
          const weightPercent = Math.round(factor.weight * 100);

          return (
            <div
              key={factor.factorName || idx}
              className="rounded-2xl border border-white/5 bg-white/[0.02] hover:bg-white/[0.04] p-4 transition-colors space-y-2.5"
            >
              <div className="flex items-center justify-between gap-3">
                <div className="flex items-center gap-2">
                  <span className="text-sm font-bold text-white">{factor.factorName}</span>
                  <span className="text-[11px] font-semibold text-slate-400 px-2 py-0.5 rounded-md bg-white/5 border border-white/10">
                    Weight: {weightPercent}%
                  </span>
                </div>

                <div className="flex items-center gap-2.5">
                  <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-bold border ${impact.color}`}>
                    <ImpactIcon className="w-3 h-3" />
                    {factor.impact}
                  </span>
                  <span className="text-sm font-extrabold text-white">
                    {factor.score}<span className="text-slate-500 text-xs font-normal">/100</span>
                  </span>
                </div>
              </div>

              {/* Animated Progress Bar */}
              <div className="h-2 w-full rounded-full bg-white/10 overflow-hidden">
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: `${factor.score}%` }}
                  transition={{ duration: 0.8, delay: idx * 0.08, ease: "easeOut" }}
                  className={`h-full rounded-full bg-gradient-to-r ${impact.barColor}`}
                />
              </div>

              {/* Rationale explanation */}
              <p className="text-xs text-slate-400 leading-relaxed">
                {factor.explanation}
              </p>
            </div>
          );
        })}
      </div>
    </motion.div>
  );
};
