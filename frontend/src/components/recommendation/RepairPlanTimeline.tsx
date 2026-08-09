"use client";

import { motion } from "framer-motion";
import { Clock, ShieldAlert, CheckCircle2, ListOrdered } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { RepairStep } from "@/lib/types/recommendation";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function RepairPlanTimeline({
  summary,
  steps,
}: {
  summary: string;
  steps: RepairStep[];
}) {
  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2 mb-2">
        <ListOrdered className="size-5 text-[#22C55E]" />
        <h2 className="text-lg font-bold text-white">Structured Repair Guide & Plan</h2>
      </div>

      <p className="text-xs text-[#CBD5E1] leading-relaxed mb-4">{summary}</p>

      <div className="space-y-4">
        {steps.map((step, idx) => (
          <motion.div
            key={step.stepNumber}
            initial={{ opacity: 0, x: -16 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.4, delay: idx * 0.1, ease: EASE }}
          >
            <GlassCard padding="md" hoverEffect={false} className="border-l-4 border-l-[#22C55E]">
              <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                <div className="flex items-start gap-3">
                  <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-xs font-bold text-white shadow-md">
                    {step.stepNumber}
                  </div>
                  <div>
                    <h3 className="text-base font-bold text-white">{step.title}</h3>
                    <p className="mt-1 text-xs leading-relaxed text-[#CBD5E1]">
                      {step.description}
                    </p>
                  </div>
                </div>

                {step.estimatedMinutes && (
                  <span className="inline-flex shrink-0 items-center gap-1 rounded-full border border-white/10 bg-white/[0.05] px-2.5 py-1 text-[11px] font-medium text-white/70">
                    <Clock className="size-3 text-[#06B6D4]" />
                    ~{step.estimatedMinutes} mins
                  </span>
                )}
              </div>

              {step.safetyNote && (
                <div className="mt-3 flex items-start gap-2 rounded-xl border border-amber-500/30 bg-amber-500/10 p-2.5 text-[11px] text-amber-200">
                  <ShieldAlert className="size-4 text-amber-400 shrink-0 mt-0.5" />
                  <span>
                    <strong>Safety Note: </strong>
                    {step.safetyNote}
                  </span>
                </div>
              )}
            </GlassCard>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
