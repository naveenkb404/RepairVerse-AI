"use client";

import React from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Activity,
  ArrowRight,
  Calendar,
  CheckCircle2,
  ChevronRight,
  Circle,
  Clock,
  Compass,
  Cpu,
  Layers,
  LucideIcon,
  ShieldCheck,
  Sparkles,
  Wrench,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import type { RepairJourneyData, RepairJourneyStageData } from "@/lib/types/repairJourney";

const EASE = [0.22, 1, 0.36, 1] as const;

interface RepairJourneyTimelineProps {
  journey: RepairJourneyData;
}

export default function RepairJourneyTimeline({ journey }: RepairJourneyTimelineProps) {
  const getStageIcon = (key: string): LucideIcon => {
    switch (key) {
      case "DEVICE_REGISTERED":
        return Layers;
      case "DIAGNOSIS_COMPLETE":
        return Sparkles;
      case "RISK_ANALYZED":
        return Activity;
      case "REPAIR_RECOMMENDED":
        return Compass;
      case "ACTION_PLAN_READY":
        return ShieldCheck;
      case "SHOP_BOOKED":
        return Calendar;
      case "REPAIR_IN_PROGRESS":
        return Wrench;
      case "REPAIR_COMPLETED":
        return CheckCircle2;
      default:
        return Cpu;
    }
  };

  return (
    <GlassCard className="p-6 border-white/10 relative overflow-hidden">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-5">
        <div>
          <div className="flex items-center gap-2 mb-1.5 flex-wrap">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-3 py-0.5 text-xs font-bold uppercase tracking-wider text-emerald-400">
              <Compass className="size-3.5" /> End-to-End Repair Journey
            </span>
            <span className="text-[11px] font-semibold text-white/50 bg-white/[0.04] px-2.5 py-0.5 rounded-full border border-white/10">
              Stage {journey.currentStageIndex + 1} of {journey.totalStages}
            </span>
          </div>
          <h3 className="text-lg font-black text-white sm:text-xl">
            Device Lifecycle & Servicing Progression
          </h3>
          <p className="text-xs text-white/60 mt-1 max-w-xl">
            {journey.nextRecommendedAction}
          </p>
        </div>

        {/* Progress Circular / Bar Indicator */}
        <div className="flex items-center gap-3 self-start sm:self-auto">
          <div className="text-right">
            <span className="text-xs font-bold text-white block">
              {journey.progressPercentage}% Journey Progress
            </span>
            <span className="text-[10px] text-white/50">
              Stage: {journey.currentStage.replace(/_/g, " ")}
            </span>
          </div>
          <div className="flex size-10 items-center justify-center rounded-2xl border border-emerald-500/30 bg-emerald-500/10 text-xs font-black text-emerald-400">
            {journey.progressPercentage}%
          </div>
        </div>
      </div>

      {/* Progress Strip */}
      <div className="my-5">
        <div className="h-1.5 w-full overflow-hidden rounded-full bg-white/10">
          <motion.div
            className="h-full bg-gradient-to-r from-emerald-500 via-cyan-500 to-indigo-500 rounded-full"
            initial={{ width: 0 }}
            animate={{ width: `${journey.progressPercentage}%` }}
            transition={{ duration: 0.6, ease: EASE }}
          />
        </div>
      </div>

      {/* 9-Stage Stepper Grid */}
      <div className="space-y-2.5">
        {journey.stages.map((stage, idx) => {
          const StageIcon = getStageIcon(stage.stageKey);
          const isDone = stage.isCompleted;
          const isCurr = stage.isCurrent;

          return (
            <div
              key={stage.stageKey}
              className={`rounded-2xl border p-3.5 transition-all duration-200 flex flex-col sm:flex-row sm:items-center justify-between gap-3 ${
                isCurr
                  ? "border-cyan-500/40 bg-cyan-500/[0.08] shadow-lg shadow-cyan-500/5"
                  : isDone
                  ? "border-emerald-500/20 bg-emerald-500/[0.02]"
                  : "border-white/5 bg-white/[0.01] opacity-50"
              }`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`mt-0.5 flex size-7 shrink-0 items-center justify-center rounded-xl border font-bold text-xs ${
                    isDone
                      ? "border-emerald-500 bg-emerald-500 text-black"
                      : isCurr
                      ? "border-cyan-400 bg-cyan-400/20 text-cyan-300 animate-pulse"
                      : "border-white/20 bg-white/5 text-white/40"
                  }`}
                >
                  {isDone ? <CheckCircle2 className="size-4" /> : idx + 1}
                </div>

                <div>
                  <div className="flex items-center gap-2 flex-wrap">
                    <h4
                      className={`text-xs font-bold ${
                        isCurr ? "text-cyan-300 font-black" : isDone ? "text-white" : "text-white/60"
                      }`}
                    >
                      {stage.title}
                    </h4>
                    {isCurr && (
                      <span className="text-[10px] font-black text-cyan-300 bg-cyan-500/20 border border-cyan-500/30 px-2 py-0.2 rounded-full">
                        CURRENT STEP
                      </span>
                    )}
                    {isDone && (
                      <span className="text-[10px] font-semibold text-emerald-400 flex items-center gap-1">
                        <CheckCircle2 className="size-3" /> Completed
                      </span>
                    )}
                  </div>
                  <p className="text-[11px] text-white/50 mt-0.5 max-w-lg">
                    {stage.description}
                  </p>
                </div>
              </div>

              {/* Action Link Button */}
              <div className="self-end sm:self-auto shrink-0">
                <Link
                  href={stage.actionUrl}
                  className={`inline-flex items-center gap-1 rounded-xl px-3 py-1.5 text-xs font-bold transition-colors ${
                    isCurr
                      ? "bg-cyan-500 text-black hover:bg-cyan-400"
                      : "bg-white/5 text-white/70 hover:bg-white/10 hover:text-white border border-white/10"
                  }`}
                >
                  <span>{isDone ? "Review" : "Proceed"}</span>
                  <ArrowRight className="size-3" />
                </Link>
              </div>
            </div>
          );
        })}
      </div>
    </GlassCard>
  );
}
