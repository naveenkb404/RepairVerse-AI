"use client";

import React, { useState } from "react";
import { motion } from "framer-motion";
import {
  ShieldAlert,
  ShieldCheck,
  AlertTriangle,
  Flame,
  ArrowRight,
  Info,
  ChevronDown,
  ChevronUp,
  Cpu,
  Sparkles,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { MaintenancePriority, MaintenancePriorityLevel } from "@/lib/types/maintenance";

interface MaintenancePriorityCardProps {
  priority: MaintenancePriority;
  onActionClick?: () => void;
  actionButtonText?: string;
}

export const MaintenancePriorityCard: React.FC<MaintenancePriorityCardProps> = ({
  priority,
  onActionClick,
  actionButtonText = "Generate Proactive Schedule",
}) => {
  const [showDetails, setShowDetails] = useState(false);

  const getScoreColor = (score: number) => {
    if (score >= 80) return "text-red-400 border-red-500/30 bg-red-500/10";
    if (score >= 60) return "text-amber-400 border-amber-500/30 bg-amber-500/10";
    if (score >= 40) return "text-cyan-400 border-cyan-500/30 bg-cyan-500/10";
    return "text-emerald-400 border-emerald-500/30 bg-emerald-500/10";
  };

  const getLevelBadge = (level: MaintenancePriorityLevel) => {
    switch (level) {
      case "CRITICAL":
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-red-500/20 text-red-300 border border-red-500/40 animate-pulse">
            CRITICAL CARE REQUIRED
          </span>
        );
      case "HIGH":
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-500/20 text-amber-300 border border-amber-500/40">
            HIGH MAINTENANCE PRIORITY
          </span>
        );
      case "MEDIUM":
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-cyan-500/20 text-cyan-300 border border-cyan-500/40">
            MODERATE ATTENTION NEEDED
          </span>
        );
      default:
        return (
          <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-emerald-500/20 text-emerald-300 border border-emerald-500/40">
            ROUTINE MAINTENANCE ONLY
          </span>
        );
    }
  };

  return (
    <GlassCard className="p-6 relative overflow-hidden">
      {/* Background Accent Glow */}
      <div
        className={`absolute -top-10 -right-10 w-40 h-40 rounded-full blur-3xl opacity-20 pointer-events-none ${
          priority.priorityScore >= 80
            ? "bg-red-500"
            : priority.priorityScore >= 60
            ? "bg-amber-500"
            : "bg-emerald-500"
        }`}
      />

      <div className="relative z-10">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-4 mb-5">
          <div className="flex items-center gap-3">
            <div
              className={`p-2.5 rounded-xl border ${
                priority.priorityScore >= 80
                  ? "bg-red-500/10 border-red-500/30 text-red-400"
                  : priority.priorityScore >= 60
                  ? "bg-amber-500/10 border-amber-500/30 text-amber-400"
                  : "bg-emerald-500/10 border-emerald-500/30 text-emerald-400"
              }`}
            >
              {priority.priorityScore >= 80 ? (
                <Flame className="w-5 h-5" />
              ) : priority.priorityScore >= 60 ? (
                <AlertTriangle className="w-5 h-5" />
              ) : (
                <ShieldCheck className="w-5 h-5" />
              )}
            </div>

            <div>
              <div className="flex items-center gap-2 mb-1">
                <span className="text-xs text-slate-400 uppercase tracking-wider font-semibold">
                  Proactive Care Engine
                </span>
                {priority.isDemo && (
                  <span className="px-1.5 py-0.2 text-[9px] font-bold rounded bg-amber-500/10 text-amber-300 border border-amber-500/20">
                    Demo
                  </span>
                )}
              </div>
              <h3 className="text-base font-bold text-white">
                Maintenance Priority Assessment
              </h3>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {getLevelBadge(priority.priorityLevel)}
            {/* Score Ring Gauge */}
            <div
              className={`px-3 py-1.5 rounded-xl border font-mono font-bold text-lg ${getScoreColor(
                priority.priorityScore
              )}`}
            >
              {priority.priorityScore}
              <span className="text-xs font-normal text-slate-400 ml-0.5">/100</span>
            </div>
          </div>
        </div>

        {/* Primary Rationale & Action */}
        <div className="space-y-4">
          <div className="p-4 rounded-xl bg-white/[0.03] border border-white/10">
            <span className="text-xs font-semibold uppercase tracking-wide text-slate-400 block mb-1">
              Assessment Rationale
            </span>
            <p className="text-sm text-slate-200 leading-relaxed font-medium">
              {priority.reason}
            </p>
          </div>

          <div className="p-4 rounded-xl bg-emerald-500/5 border border-emerald-500/20">
            <span className="text-xs font-semibold uppercase tracking-wide text-emerald-400 block mb-1">
              Recommended Proactive Action
            </span>
            <p className="text-sm text-white font-medium">
              {priority.recommendedAction}
            </p>
          </div>
        </div>

        {/* Details Toggle */}
        <div className="mt-4 pt-4 border-t border-white/10 flex items-center justify-between">
          <button
            onClick={() => setShowDetails(!showDetails)}
            className="inline-flex items-center gap-1.5 text-xs text-slate-400 hover:text-white transition-colors"
          >
            <Info className="w-3.5 h-3.5" />
            <span>{showDetails ? "Hide" : "Show"} Scoring Breakdown</span>
            {showDetails ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
          </button>

          {onActionClick && (
            <button
              onClick={onActionClick}
              className="inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-600 text-black text-xs font-bold transition-all shadow-lg shadow-emerald-500/20"
            >
              <span>{actionButtonText}</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          )}
        </div>

        {/* Expandable Scoring Details */}
        {showDetails && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            className="mt-3 p-3 rounded-lg bg-black/40 border border-white/5 text-xs space-y-2"
          >
            <div className="flex justify-between text-slate-400">
              <span>Primary Risk Contributor:</span>
              <span className="text-slate-200 font-medium">{priority.riskContributor}</span>
            </div>
            <div className="flex justify-between text-slate-400">
              <span>Deterministic Evaluated At:</span>
              <span className="text-slate-200 font-mono">
                {new Date(priority.evaluatedAt).toLocaleString()}
              </span>
            </div>
            <p className="text-[11px] text-slate-500 mt-2">
              Note: Priority scores are computed strictly by deterministic rule algorithms factoring predictive wear models, physical sensors, and overdue service intervals.
            </p>
          </motion.div>
        )}
      </div>
    </GlassCard>
  );
};
