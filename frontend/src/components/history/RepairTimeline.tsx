"use client";

import { motion } from "framer-motion";
import { CheckCircle2, Circle, Clock, GitCommit } from "lucide-react";
import { RepairTimelineStage } from "@/lib/types/repairHistory";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type RepairTimelineProps = {
  stages?: RepairTimelineStage[];
};

export default function RepairTimeline({ stages }: RepairTimelineProps) {
  if (!stages || stages.length === 0) {
    return (
      <GlassCard className="p-6">
        <h3 className="text-base font-bold text-white mb-2">Service Lifecycle Timeline</h3>
        <p className="text-xs text-white/40">
          No detailed lifecycle milestones logged for this repair record.
        </p>
      </GlassCard>
    );
  }

  return (
    <GlassCard className="p-6">
      <div className="flex items-center gap-2 border-b border-white/10 pb-4 mb-6">
        <GitCommit className="size-5 text-[#22C55E]" />
        <h3 className="text-base font-bold text-white">Repair Milestone Timeline</h3>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-gradient-to-b before:from-[#22C55E] before:via-[#06B6D4] before:to-white/10">
        {stages.map((stg, idx) => (
          <motion.div
            key={stg.id || idx}
            initial={{ opacity: 0, x: -12 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.3, delay: idx * 0.08, ease: EASE }}
            className="relative group"
          >
            <div className="absolute -left-[31px] top-0.5 flex size-6 items-center justify-center rounded-full bg-[#0B1120] text-xs">
              {stg.status === "completed" ? (
                <CheckCircle2 className="size-4 text-[#22C55E]" />
              ) : stg.status === "current" ? (
                <Clock className="size-4 text-[#06B6D4] animate-pulse" />
              ) : (
                <Circle className="size-3 text-white/30" />
              )}
            </div>

            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 group-hover:border-white/20 group-hover:bg-white/[0.06] transition-all">
              <div className="flex flex-wrap items-center justify-between gap-2 mb-1">
                <h4 className="text-xs font-bold text-white">{stg.title}</h4>
                <span className="text-[10px] font-mono text-white/50">{stg.date}</span>
              </div>
              <p className="text-xs text-white/60 leading-relaxed">
                {stg.description}
              </p>
            </div>
          </motion.div>
        ))}
      </div>
    </GlassCard>
  );
}
