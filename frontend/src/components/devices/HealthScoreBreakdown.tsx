"use client";

import { motion } from "framer-motion";
import { Battery, Calendar, Wrench, Cpu, AlertCircle } from "lucide-react";
import { DeviceHealth } from "@/lib/types/device";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type HealthScoreBreakdownProps = {
  health: DeviceHealth;
};

export default function HealthScoreBreakdown({
  health,
}: HealthScoreBreakdownProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: 0.1, ease: EASE }}
    >
      <GlassCard className="p-6">
        <div className="flex items-center gap-2 mb-6 border-b border-white/10 pb-4">
          <Cpu className="size-5 text-[#22C55E]" />
          <h2 className="text-lg font-bold text-white">
            Health & Diagnostics Breakdown
          </h2>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3 mb-6">
          {/* Battery Health */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <div className="flex items-center gap-2 text-xs font-semibold text-white/50 mb-2">
              <Battery className="size-4 text-[#06B6D4]" />
              Battery Capacity
            </div>
            {health.batteryHealth != null ? (
              <div>
                <div className="text-2xl font-bold text-white">
                  {health.batteryHealth}%
                </div>
                <div className="mt-2 h-2 w-full overflow-hidden rounded-full bg-white/10">
                  <div
                    className="h-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
                    style={{ width: `${health.batteryHealth}%` }}
                  />
                </div>
              </div>
            ) : (
              <p className="text-xs text-white/40">
                Battery health metrics will appear after hardware diagnostic check.
              </p>
            )}
          </div>

          {/* Last Service */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <div className="flex items-center gap-2 text-xs font-semibold text-white/50 mb-2">
              <Calendar className="size-4 text-[#22C55E]" />
              Last Service
            </div>
            {health.lastService ? (
              <div>
                <div className="text-lg font-bold text-white">
                  {health.lastService}
                </div>
                <div className="text-[11px] text-[#22C55E] mt-1">
                  ✓ Recorded Service
                </div>
              </div>
            ) : (
              <p className="text-xs text-white/40">No service record logged yet.</p>
            )}
          </div>

          {/* Maintenance Due */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <div className="flex items-center gap-2 text-xs font-semibold text-white/50 mb-2">
              <Wrench className="size-4 text-amber-400" />
              Maintenance Schedule
            </div>
            {health.maintenanceDue ? (
              <div>
                <div className="text-lg font-bold text-white">
                  {health.maintenanceDue}
                </div>
                <div className="text-[11px] text-amber-400 mt-1">
                  Upcoming Checkup
                </div>
              </div>
            ) : (
              <p className="text-xs text-white/40">
                No immediate maintenance scheduled.
              </p>
            )}
          </div>
        </div>

        {/* AI Prediction Insight */}
        {health.aiPrediction && (
          <div className="flex items-start gap-3 rounded-2xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 p-4 text-xs text-[#06B6D4]">
            <AlertCircle className="size-5 shrink-0 text-[#06B6D4]" />
            <div>
              <strong className="font-semibold block mb-0.5 text-white">
                AI Health Insights:
              </strong>
              {health.aiPrediction}
            </div>
          </div>
        )}
      </GlassCard>
    </motion.div>
  );
}
