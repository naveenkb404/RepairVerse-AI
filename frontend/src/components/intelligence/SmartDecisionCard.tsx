"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Sparkles,
  Zap,
  DollarSign,
  Calendar,
  CheckCircle,
  AlertCircle,
  ArrowRight,
  Shield,
  Layers,
} from "lucide-react";
import type { SmartDecision } from "@/lib/types/deviceIntelligence";
import Link from "next/link";

interface SmartDecisionCardProps {
  decision: SmartDecision;
  deviceId: string;
}

export const SmartDecisionCard: React.FC<SmartDecisionCardProps> = ({
  decision,
  deviceId,
}) => {
  const getPriorityBadge = (p: string) => {
    switch (p) {
      case "URGENT":
        return "bg-rose-500/20 text-rose-300 border-rose-500/40 animate-pulse";
      case "HIGH":
        return "bg-amber-500/20 text-amber-300 border-amber-500/40";
      case "MEDIUM":
        return "bg-cyan-500/20 text-cyan-300 border-cyan-500/40";
      default:
        return "bg-emerald-500/20 text-emerald-300 border-emerald-500/40";
    }
  };

  const getActionHref = (action: string) => {
    switch (action) {
      case "PROFESSIONAL_SERVICE":
      case "REPAIR_NOW":
        return "/marketplace";
      case "MAINTENANCE_REQUIRED":
        return "/maintenance";
      case "REFURBISH":
        return "/marketplace";
      default:
        return `/devices/${deviceId}`;
    }
  };

  const getCtaLabel = (action: string) => {
    switch (action) {
      case "PROFESSIONAL_SERVICE":
        return "Find Certified Repair Shops";
      case "REPAIR_NOW":
        return "Explore Repair Quotes";
      case "MAINTENANCE_REQUIRED":
        return "Schedule Maintenance";
      case "REFURBISH":
        return "View Refurbish Options";
      default:
        return "View Device Passport";
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl flex flex-col justify-between space-y-6 shadow-xl relative overflow-hidden"
    >
      {/* Top Header */}
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              <Sparkles className="w-4 h-4" />
            </span>
            <span className="text-xs font-bold uppercase tracking-wider text-emerald-400">
              Prescriptive Decision Blueprint
            </span>
          </div>
          <h3 className="text-lg md:text-xl font-extrabold text-white tracking-tight">
            {decision.title}
          </h3>
        </div>

        <span className={`px-3 py-1 rounded-full text-xs font-bold border shrink-0 ${getPriorityBadge(decision.priority)}`}>
          {decision.priority} Priority
        </span>
      </div>

      {/* Rationale Narrative */}
      <p className="text-sm text-slate-300 leading-relaxed bg-white/5 p-4 rounded-2xl border border-white/5">
        {decision.explanation}
      </p>

      {/* Metrics Row: Estimated Cost & Expected Benefit */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-2">
        <div className="rounded-2xl border border-white/10 bg-black/30 p-4 space-y-1">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-400">
            <DollarSign className="w-4 h-4 text-emerald-400" />
            <span>Estimated Out-of-Pocket</span>
          </div>
          <div className="text-xl font-extrabold text-white">
            {decision.estimatedCost > 0 ? `$${decision.estimatedCost.toFixed(2)}` : "$0.00 (Zero Expense)"}
          </div>
          <span className="text-[11px] text-slate-500">Includes labor & genuine parts baseline</span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-black/30 p-4 space-y-1">
          <div className="flex items-center gap-2 text-xs font-medium text-slate-400">
            <Zap className="w-4 h-4 text-cyan-400" />
            <span>Expected Value Realization</span>
          </div>
          <div className="text-xs font-semibold text-cyan-300 leading-snug">
            {decision.expectedBenefit}
          </div>
          <span className="text-[11px] text-slate-500">Determined via circular lifecycle model</span>
        </div>
      </div>

      {/* Primary Action Button */}
      <div className="pt-2">
        <Link
          href={getActionHref(decision.recommendedAction)}
          className="w-full inline-flex items-center justify-center gap-2 py-3 px-4 rounded-xl text-sm font-bold bg-gradient-to-r from-emerald-500 via-teal-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 transition-all shadow-lg hover:shadow-emerald-500/20"
        >
          <span>{getCtaLabel(decision.recommendedAction)}</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    </motion.div>
  );
};
