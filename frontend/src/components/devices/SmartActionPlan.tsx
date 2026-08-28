"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Activity,
  AlertOctagon,
  AlertTriangle,
  ArrowRight,
  Calendar,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Clock,
  Cpu,
  DollarSign,
  Flame,
  Leaf,
  Layers,
  LucideIcon,
  Recycle,
  RefreshCw,
  ShieldAlert,
  ShieldCheck,
  Sparkles,
  Wrench,
  Zap,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import type {
  ActionStrategy,
  ActionType,
  PriorityLevel,
  RepairActionPlanData,
  RepairActionStepData,
} from "@/lib/types/repairPlanning";

const EASE = [0.22, 1, 0.36, 1] as const;

interface SmartActionPlanProps {
  plan: RepairActionPlanData;
  isLoading?: boolean;
  onRefresh?: () => void;
}

export default function SmartActionPlan({
  plan,
  isLoading = false,
  onRefresh,
}: SmartActionPlanProps) {
  const [completedStepIds, setCompletedStepIds] = useState<Set<string>>(
    new Set(plan.steps.filter((s) => s.status === "COMPLETED").map((s) => s.id))
  );
  const [isExpanded, setIsExpanded] = useState(true);

  const toggleStep = (stepId: string) => {
    setCompletedStepIds((prev) => {
      const next = new Set(prev);
      if (next.has(stepId)) {
        next.delete(stepId);
      } else {
        next.add(stepId);
      }
      return next;
    });
  };

  const getStrategyConfig = (strategy: ActionStrategy) => {
    switch (strategy) {
      case "MONITOR":
        return {
          label: "Continuous Health Monitoring",
          color: "border-cyan-500/30 bg-cyan-500/10 text-cyan-400",
          icon: Activity,
          description: "Hardware operating nominally. Regular diagnostics recommended.",
        };
      case "PREVENTIVE_MAINTENANCE":
        return {
          label: "Proactive Preventive Maintenance",
          color: "border-emerald-500/30 bg-emerald-500/10 text-emerald-400",
          icon: ShieldCheck,
          description: "Early servicing recommended to preempt collateral hardware damage.",
        };
      case "REPAIR":
        return {
          label: "Targeted Component Repair",
          color: "border-amber-500/30 bg-amber-500/10 text-amber-400",
          icon: Wrench,
          description: "Modular part replacement is economically viable and environmentally sound.",
        };
      case "REFURBISH":
        return {
          label: "Modular Hardware Refurbishment",
          color: "border-indigo-500/30 bg-indigo-500/10 text-indigo-400",
          icon: Cpu,
          description: "Full sub-assembly reconditioning restores maximum operational lifespan.",
        };
      case "REPLACE":
        return {
          label: "Economic Model Succession / Replacement",
          color: "border-rose-500/30 bg-rose-500/10 text-rose-400",
          icon: AlertOctagon,
          description: "Repair cost exceeds replacement ceiling. Transitioning to newer unit advised.",
        };
      case "RECYCLE":
        return {
          label: "Hazardous / Direct Circular Recycling",
          color: "border-purple-500/30 bg-purple-500/10 text-purple-400",
          icon: Recycle,
          description: "Severe battery/board compromise. Certified material reclamation required.",
        };
    }
  };

  const getActionTypeIcon = (type: ActionType): LucideIcon => {
    switch (type) {
      case "BACKUP_DATA":
        return Layers;
      case "CLEAN":
        return Sparkles;
      case "MAINTAIN":
        return Cpu;
      case "REPAIR":
      case "REPLACE_COMPONENT":
        return Wrench;
      case "BOOK_REPAIR":
        return Calendar;
      case "RECYCLE":
        return Recycle;
      default:
        return Activity;
    }
  };

  const strategyConfig = getStrategyConfig(plan.overallStrategy);
  const StrategyIcon = strategyConfig.icon;

  const totalSteps = plan.steps.length;
  const completedCount = completedStepIds.size;
  const progressPct = totalSteps > 0 ? Math.round((completedCount / totalSteps) * 100) : 0;

  return (
    <GlassCard className="p-6 border-white/10 relative overflow-hidden">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-5">
        <div>
          <div className="flex items-center gap-2 mb-1.5 flex-wrap">
            <span
              className={`inline-flex items-center gap-1.5 rounded-full border px-3 py-0.5 text-xs font-bold uppercase tracking-wider ${strategyConfig.color}`}
            >
              <StrategyIcon className="size-3.5" />
              {strategyConfig.label}
            </span>
            <span className="text-[11px] font-semibold text-white/50 bg-white/[0.04] px-2.5 py-0.5 rounded-full border border-white/10">
              Priority: {plan.priorityLevel}
            </span>
            {plan.isDemo && (
              <span className="text-[10px] font-bold text-cyan-400 bg-cyan-500/10 border border-cyan-500/20 px-2 py-0.5 rounded-full">
                DEMO SIMULATION
              </span>
            )}
          </div>
          <h3 className="text-lg font-black text-white sm:text-xl">
            Autonomous Action Plan & Execution Roadmap
          </h3>
          <p className="text-xs text-white/60 mt-1 max-w-xl">
            {plan.strategyRationale}
          </p>
        </div>

        <div className="flex items-center gap-2 self-end sm:self-auto">
          {onRefresh && (
            <GlassButton
              variant="outline"
              size="sm"
              icon={<RefreshCw className={`size-3.5 ${isLoading ? "animate-spin" : ""}`} />}
              onClick={onRefresh}
              disabled={isLoading}
            >
              Recalculate
            </GlassButton>
          )}
          <button
            onClick={() => setIsExpanded(!isExpanded)}
            className="flex size-8 items-center justify-center rounded-xl bg-white/5 hover:bg-white/10 text-white/70 hover:text-white transition-colors border border-white/10"
            aria-label="Toggle action plan details"
          >
            {isExpanded ? <ChevronUp className="size-4" /> : <ChevronDown className="size-4" />}
          </button>
        </div>
      </div>

      {/* Metric Highlights Strip */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 my-5">
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5 flex items-center gap-1">
            <DollarSign className="size-3 text-emerald-400" />
            Estimated Cost
          </span>
          <span className="text-base font-black text-white">
            ${plan.estimatedTotalCost.toFixed(2)}
          </span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5 flex items-center gap-1">
            <Clock className="size-3 text-cyan-400" />
            Lifespan Extension
          </span>
          <span className="text-base font-black text-cyan-400">
            +{plan.estimatedLifecycleExtensionMonths} mos
          </span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5 flex items-center gap-1">
            <Leaf className="size-3 text-[#22C55E]" />
            CO₂ Averted
          </span>
          <span className="text-base font-black text-[#22C55E]">
            {plan.estimatedCarbonSaved.toFixed(1)} kg
          </span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5 flex items-center gap-1">
            <Recycle className="size-3 text-purple-400" />
            E-Waste Saved
          </span>
          <span className="text-base font-black text-purple-400">
            {plan.estimatedEwastePrevented.toFixed(2)} kg
          </span>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="mb-5">
        <div className="flex items-center justify-between text-xs text-white/60 mb-1.5 font-medium">
          <span>Roadmap Completion</span>
          <span className="font-bold text-white">
            {completedCount} of {totalSteps} steps ({progressPct}%)
          </span>
        </div>
        <div className="h-2 w-full overflow-hidden rounded-full bg-white/10">
          <motion.div
            className="h-full bg-gradient-to-r from-emerald-500 to-cyan-500 rounded-full"
            initial={{ width: 0 }}
            animate={{ width: `${progressPct}%` }}
            transition={{ duration: 0.5, ease: EASE }}
          />
        </div>
      </div>

      {/* Sequential Action Steps */}
      <AnimatePresence>
        {isExpanded && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="space-y-3 pt-2"
          >
            {plan.steps.map((step) => {
              const isChecked = completedStepIds.has(step.id);
              const StepIcon = getActionTypeIcon(step.actionType);

              return (
                <div
                  key={step.id}
                  onClick={() => toggleStep(step.id)}
                  className={`cursor-pointer rounded-2xl border p-4 transition-all duration-200 ${
                    isChecked
                      ? "border-emerald-500/30 bg-emerald-500/[0.05] opacity-75"
                      : "border-white/10 bg-white/[0.02] hover:border-white/20 hover:bg-white/[0.04]"
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <button
                      type="button"
                      className={`mt-0.5 flex size-5 shrink-0 items-center justify-center rounded-lg border transition-colors ${
                        isChecked
                          ? "border-emerald-500 bg-emerald-500 text-black"
                          : "border-white/30 hover:border-white text-transparent"
                      }`}
                    >
                      <CheckCircle2 className="size-3.5" />
                    </button>

                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap mb-1">
                        <span className="text-[10px] font-black uppercase text-white/40 tracking-wider">
                          Step {step.stepOrder}
                        </span>
                        <h4
                          className={`text-sm font-bold ${
                            isChecked ? "line-through text-white/60" : "text-white"
                          }`}
                        >
                          {step.title}
                        </h4>
                        <span className="text-[10px] font-semibold bg-white/5 border border-white/10 px-2 py-0.2 rounded text-white/60">
                          {step.actionType}
                        </span>
                        {step.priority === "CRITICAL" && (
                          <span className="text-[10px] font-bold text-red-400 bg-red-500/10 border border-red-500/30 px-1.5 py-0.2 rounded">
                            CRITICAL
                          </span>
                        )}
                      </div>

                      <p className="text-xs text-white/60 leading-relaxed">
                        {step.description}
                      </p>

                      <div className="flex items-center gap-4 mt-2.5 text-[11px] text-white/50 flex-wrap">
                        {step.estimatedDuration && (
                          <span className="flex items-center gap-1">
                            <Clock className="size-3 text-cyan-400" />
                            {step.estimatedDuration}
                          </span>
                        )}
                        {step.estimatedCost > 0 && (
                          <span className="flex items-center gap-1">
                            <DollarSign className="size-3 text-emerald-400" />
                            Est. ${step.estimatedCost.toFixed(2)}
                          </span>
                        )}
                        {step.carbonImpact > 0 && (
                          <span className="flex items-center gap-1">
                            <Leaf className="size-3 text-[#22C55E]" />
                            {step.carbonImpact.toFixed(1)} kg CO₂
                          </span>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </motion.div>
        )}
      </AnimatePresence>
    </GlassCard>
  );
}
