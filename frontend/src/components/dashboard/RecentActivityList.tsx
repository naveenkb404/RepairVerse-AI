"use client";

import { motion } from "framer-motion";
import { CarbonRepairActivity } from "@/lib/api/carbon";
import { Leaf, DollarSign, Calendar, Wrench } from "lucide-react";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function RecentActivityList({
  activities,
}: {
  activities: CarbonRepairActivity[];
}) {
  if (!activities || activities.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center rounded-2xl border border-dashed border-white/10 py-10 text-center">
        <Wrench className="size-10 text-white/20 mb-3" />
        <p className="text-sm font-semibold text-white/60">No repair activity yet</p>
        <p className="mt-1 max-w-xs text-xs text-white/40 leading-relaxed">
          Complete a device repair using RepairVerse AI to start tracking your
          personal carbon impact contributions here.
        </p>
      </div>
    );
  }

  return (
    <ul className="space-y-3" role="list" aria-label="Recent repair activity">
      {activities.map((item, i) => (
        <motion.li
          key={item.id}
          initial={{ opacity: 0, x: -16 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.35, delay: i * 0.08, ease: EASE }}
          className="group relative rounded-2xl border border-white/10 bg-white/[0.04] p-4 transition-colors hover:border-white/20 hover:bg-white/[0.07]"
        >
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            {/* Left: Device info */}
            <div className="flex items-center gap-3 min-w-0">
              <div className="flex size-10 shrink-0 items-center justify-center rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10">
                <Wrench className="size-4 text-[#22C55E]" aria-hidden />
              </div>
              <div className="min-w-0">
                <p className="truncate text-sm font-bold text-white">
                  {item.deviceName}
                </p>
                <p className="text-xs text-white/60">{item.repairType}</p>
              </div>
            </div>

            {/* Right: Impact chips */}
            <div className="flex flex-wrap items-center gap-2">
              <span className="inline-flex items-center gap-1 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-2.5 py-0.5 text-[11px] font-semibold text-[#22C55E]">
                <Leaf className="size-3" aria-hidden />
                {item.co2Avoided.toFixed(1)} kg CO₂
              </span>
              <span className="inline-flex items-center gap-1 rounded-full border border-[#06B6D4]/25 bg-[#06B6D4]/10 px-2.5 py-0.5 text-[11px] font-semibold text-[#06B6D4]">
                <DollarSign className="size-3" aria-hidden />${item.moneySaved}
              </span>
              <span className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.05] px-2.5 py-0.5 text-[11px] text-white/60">
                <Calendar className="size-3" aria-hidden />
                {item.repairDate}
              </span>
            </div>
          </div>
        </motion.li>
      ))}
    </ul>
  );
}
