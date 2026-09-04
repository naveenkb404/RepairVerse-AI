"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertTriangle,
  ChevronDown,
  ChevronUp,
  DollarSign,
  Leaf,
  ShieldAlert,
  Sparkles,
  Laptop,
  Smartphone,
  Headphones,
  Tv,
  CheckCircle2,
  Zap,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import ActionPlanVisualizer from "./ActionPlanVisualizer";
import type { InterventionResponse } from "@/lib/types/autonomousRepairAgent";
import { cn } from "@/lib/utils";

interface AutonomousInterventionCardProps {
  intervention: InterventionResponse;
  onApproveStep?: (stepId: string) => Promise<void> | void;
  onRejectStep?: (stepId: string) => Promise<void> | void;
  onExecuteStep?: (stepId: string) => Promise<void> | void;
}

export default function AutonomousInterventionCard({
  intervention,
  onApproveStep,
  onRejectStep,
  onExecuteStep,
}: AutonomousInterventionCardProps) {
  const [expanded, setExpanded] = useState(true);

  const getPriorityBadge = (priority: string) => {
    switch (priority) {
      case "CRITICAL":
        return {
          bg: "bg-red-500/10 border-red-500/30 text-red-400",
          glow: "radial-gradient(circle at top right, rgba(239,68,68,0.2), transparent 70%)",
          glowColor: "none" as const,
          label: "Critical Priority",
        };
      case "HIGH":
        return {
          bg: "bg-amber-500/10 border-amber-500/30 text-amber-400",
          glow: "radial-gradient(circle at top right, rgba(245,158,11,0.2), transparent 70%)",
          glowColor: "none" as const,
          label: "High Priority",
        };
      case "MEDIUM":
        return {
          bg: "bg-cyan-500/10 border-cyan-500/30 text-cyan-300",
          glow: "radial-gradient(circle at top right, rgba(6,182,212,0.2), transparent 70%)",
          glowColor: "cyan" as const,
          label: "Medium Priority",
        };
      default:
        return {
          bg: "bg-emerald-500/10 border-emerald-500/30 text-emerald-400",
          glow: "radial-gradient(circle at top right, rgba(34,197,94,0.2), transparent 70%)",
          glowColor: "green" as const,
          label: "Low Priority",
        };
    }
  };

  const getDeviceIcon = (category?: string) => {
    const cat = category?.toUpperCase();
    if (cat === "LAPTOP" || cat === "COMPUTER") return Laptop;
    if (cat === "SMARTPHONE" || cat === "PHONE") return Smartphone;
    if (cat === "AUDIO" || cat === "HEADPHONES") return Headphones;
    return Tv;
  };

  const priorityStyle = getPriorityBadge(intervention.priority);
  const DeviceIcon = getDeviceIcon(intervention.deviceCategory);

  return (
    <GlassCard glowColor={priorityStyle.glowColor} padding="md" className="overflow-hidden">
      <div className="flex flex-col gap-5">
        {/* Card Header */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div className="flex items-start gap-3.5">
            <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl border border-white/10 bg-white/[0.05] text-cyan-400 shadow-inner">
              <DeviceIcon className="h-6 w-6" />
            </div>

            <div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="text-xs font-semibold text-slate-300">
                  {intervention.deviceName || "Unspecified Device"}
                </span>
                <span className={cn("inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-[11px] font-semibold tracking-wide uppercase", priorityStyle.bg)}>
                  <Zap className="h-3 w-3" />
                  {priorityStyle.label} ({intervention.priorityScore}/100)
                </span>
                <span className="text-[11px] text-slate-400 font-mono">
                  Confidence: {Math.round(intervention.confidenceScore * 100)}%
                </span>
              </div>

              <h3 className="mt-1 text-lg font-bold text-white tracking-tight">
                {intervention.title}
              </h3>
            </div>
          </div>

          <div className="flex items-center gap-2 self-end sm:self-auto">
            <button
              onClick={() => setExpanded(!expanded)}
              className="inline-flex items-center gap-1.5 rounded-lg border border-white/10 bg-white/[0.04] px-3 py-1.5 text-xs font-medium text-slate-300 hover:bg-white/[0.08] transition-colors"
            >
              <span>{expanded ? "Hide Plan" : "View Action Plan"}</span>
              {expanded ? <ChevronUp className="h-3.5 w-3.5" /> : <ChevronDown className="h-3.5 w-3.5" />}
            </button>
          </div>
        </div>

        {/* Diagnostic Description & Root Cause */}
        <div className="space-y-2 rounded-xl border border-white/5 bg-black/20 p-4 text-xs leading-relaxed text-slate-300">
          <p>{intervention.description}</p>
          {intervention.reason && (
            <p className="text-slate-400 pt-1 border-t border-white/5">
              <strong className="text-slate-200">Root Cause:</strong> {intervention.reason}
            </p>
          )}
        </div>

        {/* Financial & Environmental Metrics Ribbon */}
        <div className="grid grid-cols-3 gap-3 rounded-xl border border-white/10 bg-white/[0.02] p-3 text-center">
          <div>
            <div className="text-[11px] text-slate-400">Est. Repair Cost</div>
            <div className="mt-0.5 text-sm font-bold text-slate-200 font-mono">
              ${intervention.estimatedCost.toFixed(0)}
            </div>
          </div>
          <div>
            <div className="text-[11px] text-emerald-400">Est. Savings</div>
            <div className="mt-0.5 text-sm font-bold text-emerald-300 font-mono">
              +${intervention.estimatedSavings.toFixed(0)}
            </div>
          </div>
          <div>
            <div className="text-[11px] text-cyan-400">CO₂ Avoided</div>
            <div className="mt-0.5 text-sm font-bold text-cyan-300 font-mono">
              {intervention.estimatedCo2Impact.toFixed(1)} kg
            </div>
          </div>
        </div>

        {/* Expandable Action Plan */}
        <AnimatePresence>
          {expanded && intervention.actionPlan && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.3 }}
            >
              <ActionPlanVisualizer
                actionPlan={intervention.actionPlan}
                onApproveStep={onApproveStep}
                onRejectStep={onRejectStep}
                onExecuteStep={onExecuteStep}
              />
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </GlassCard>
  );
}
