"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { ArrowLeft, Clock, DollarSign, ScanLine, Sparkles, Wrench } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { DiagnosisReport } from "@/lib/types/diagnosis";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

const DIFFICULTY_BADGES = {
  Easy: "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]",
  Moderate: "border-[#06B6D4]/30 bg-[#06B6D4]/10 text-[#06B6D4]",
  Hard: "border-[#FACC15]/30 bg-[#FACC15]/10 text-[#FACC15]",
  Complex: "border-red-500/30 bg-red-500/10 text-red-400",
};

export default function DiagnosisSummaryPanel({
  report,
}: {
  report: DiagnosisReport;
}) {
  const badgeClass =
    DIFFICULTY_BADGES[report.repairDifficulty] || DIFFICULTY_BADGES.Moderate;

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45, ease: EASE }}
    >
      <GlassCard padding="lg" hoverEffect={false}>
        <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
          <div className="flex items-center gap-2">
            <ScanLine className="size-5 text-[#06B6D4]" />
            <h2 className="text-base font-bold text-white">Phase 7 AI Diagnosis Summary</h2>
          </div>
          <Link
            href="/diagnosis"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-[#22C55E] transition-colors hover:text-[#06B6D4]"
          >
            <ArrowLeft className="size-3.5" /> Re-run Diagnosis
          </Link>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-12">
          {report.imageUrl && (
            <div className="sm:col-span-3 overflow-hidden rounded-2xl border border-white/10 bg-black/40 p-1.5 flex items-center justify-center">
              <img
                src={report.imageUrl}
                alt="Diagnosed device"
                className="max-h-28 rounded-xl object-contain"
              />
            </div>
          )}

          <div className={cn("space-y-2", report.imageUrl ? "sm:col-span-9" : "sm:col-span-12")}>
            <div className="rounded-xl border border-white/10 bg-white/[0.04] p-3">
              <span className="text-[10px] font-bold uppercase tracking-wider text-[#22C55E]">
                Probable Issue Detected
              </span>
              <p className="text-sm font-bold text-white mt-0.5">{report.probableIssue}</p>
            </div>

            <div className="grid grid-cols-3 gap-2 text-xs">
              <div className="rounded-xl border border-white/10 bg-white/[0.04] p-2.5">
                <span className="text-[10px] text-[#CBD5E1]">Confidence</span>
                <p className="font-bold text-[#22C55E] mt-0.5">{report.confidenceScore}%</p>
              </div>
              <div className="rounded-xl border border-white/10 bg-white/[0.04] p-2.5">
                <span className="text-[10px] text-[#CBD5E1]">Est. Cost</span>
                <p className="font-bold text-[#06B6D4] mt-0.5">${report.repairCost}</p>
              </div>
              <div className="rounded-xl border border-white/10 bg-white/[0.04] p-2.5">
                <span className="text-[10px] text-[#CBD5E1]">Difficulty</span>
                <div className="mt-0.5">
                  <span className={cn("rounded-full border px-2 py-0.5 text-[10px] font-bold", badgeClass)}>
                    {report.repairDifficulty}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
