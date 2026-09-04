"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Sparkles,
  TrendingUp,
  AlertTriangle,
  DollarSign,
  Leaf,
  ShieldCheck,
  CheckCircle,
} from "lucide-react";
import type { SimulationInsight } from "@/lib/types/digitalTwin";
import { cn } from "@/lib/utils";

interface SimulationInsightsPanelProps {
  insights: SimulationInsight[];
}

export default function SimulationInsightsPanel({ insights }: SimulationInsightsPanelProps) {
  if (!insights || insights.length === 0) return null;

  const getInsightMeta = (category: string, impact: string) => {
    switch (category?.toUpperCase()) {
      case "FINANCIAL":
        return { icon: DollarSign, color: "text-amber-400", border: "border-amber-500/20", bg: "bg-amber-950/20" };
      case "RELIABILITY":
        return { icon: ShieldCheck, color: "text-emerald-400", border: "border-emerald-500/20", bg: "bg-emerald-950/20" };
      case "LONGEVITY":
        return { icon: TrendingUp, color: "text-cyan-400", border: "border-cyan-500/20", bg: "bg-cyan-950/20" };
      case "SUSTAINABILITY":
        return { icon: Leaf, color: "text-lime-400", border: "border-lime-500/20", bg: "bg-lime-950/20" };
      default:
        return { icon: AlertTriangle, color: "text-rose-400", border: "border-rose-500/20", bg: "bg-rose-950/20" };
    }
  };

  return (
    <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-md space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-bold text-white flex items-center gap-2">
          <Sparkles className="h-5 w-5 text-amber-400" />
          Explainable Predictive Insights
        </h3>
        <span className="text-xs text-slate-400 font-mono">{insights.length} active insights</span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {insights.map((ins, idx) => {
          const meta = getInsightMeta(ins.category, ins.impactLevel);
          const Icon = meta.icon;

          return (
            <motion.div
              key={idx}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.04 }}
              className={cn("rounded-2xl border p-4 backdrop-blur-sm flex gap-3.5", meta.border, meta.bg)}
            >
              <div className="p-2.5 rounded-xl bg-slate-950/60 border border-white/10 h-fit">
                <Icon className={cn("h-4 w-4", meta.color)} />
              </div>
              <div className="space-y-1 flex-1">
                <div className="flex items-center justify-between">
                  <h4 className="text-xs font-bold uppercase tracking-wider text-slate-200">
                    {ins.title}
                  </h4>
                  <span className={cn("text-[10px] font-semibold px-2 py-0.5 rounded-full border", meta.border, meta.color)}>
                    {ins.category}
                  </span>
                </div>
                <p className="text-xs text-slate-300 leading-relaxed">
                  {ins.message}
                </p>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
