"use client";

import { motion } from "framer-motion";
import { Wrench, CheckCircle2, DollarSign, Leaf } from "lucide-react";
import { RepairHistorySummary } from "@/lib/types/repairHistory";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type RepairHistoryStatsProps = {
  summary: RepairHistorySummary;
};

export default function RepairHistoryStats({
  summary,
}: RepairHistoryStatsProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, ease: EASE }}
      className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4"
    >
      {/* Total Repairs */}
      <GlassCard padding="md" hoverEffect={false}>
        <div className="flex items-center gap-3">
          <div className="flex size-11 items-center justify-center rounded-2xl bg-[#22C55E]/20 text-[#22C55E]">
            <Wrench className="size-5" />
          </div>
          <div>
            <div className="text-xs text-white/50 font-semibold">Total Repairs Logged</div>
            <div className="text-xl font-bold text-white">{summary.totalRepairs}</div>
          </div>
        </div>
      </GlassCard>

      {/* Completed Repairs */}
      <GlassCard padding="md" hoverEffect={false}>
        <div className="flex items-center gap-3">
          <div className="flex size-11 items-center justify-center rounded-2xl bg-[#06B6D4]/20 text-[#06B6D4]">
            <CheckCircle2 className="size-5" />
          </div>
          <div>
            <div className="text-xs text-white/50 font-semibold">Verified Completed</div>
            <div className="text-xl font-bold text-[#06B6D4]">
              {summary.completedRepairs} <span className="text-xs text-white/40 font-normal">/ {summary.totalRepairs}</span>
            </div>
          </div>
        </div>
      </GlassCard>

      {/* Net Money Saved */}
      <GlassCard padding="md" hoverEffect={false}>
        <div className="flex items-center gap-3">
          <div className="flex size-11 items-center justify-center rounded-2xl bg-emerald-500/20 text-emerald-400">
            <DollarSign className="size-5" />
          </div>
          <div>
            <div className="text-xs text-white/50 font-semibold">Net Replacement Savings</div>
            <div className="text-xl font-bold text-[#22C55E]">
              ${summary.totalSavedMoney.toLocaleString()}
            </div>
          </div>
        </div>
      </GlassCard>

      {/* CO2 Emissions Avoided */}
      <GlassCard padding="md" hoverEffect={false}>
        <div className="flex items-center gap-3">
          <div className="flex size-11 items-center justify-center rounded-2xl bg-amber-500/20 text-amber-400">
            <Leaf className="size-5" />
          </div>
          <div>
            <div className="text-xs text-white/50 font-semibold">CO₂ Emissions Diverted</div>
            <div className="text-xl font-bold text-amber-400">
              {summary.totalCo2SavedKg} kg
            </div>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
