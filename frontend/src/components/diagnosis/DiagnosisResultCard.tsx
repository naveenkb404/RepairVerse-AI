"use client";

import { motion } from "framer-motion";
import {
  AlertTriangle,
  Battery,
  CheckCircle2,
  Clock,
  DollarSign,
  Info,
  RotateCcw,
  ScanLine,
  ShieldAlert,
  Sparkles,
  Wrench,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import { DiagnosisReport } from "@/lib/types/diagnosis";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type DiagnosisResultCardProps = {
  report: DiagnosisReport;
  isDemo?: boolean;
  onReset: () => void;
};

const DIFFICULTY_STYLES = {
  Easy: "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]",
  Moderate: "border-[#06B6D4]/30 bg-[#06B6D4]/10 text-[#06B6D4]",
  Hard: "border-[#FACC15]/30 bg-[#FACC15]/10 text-[#FACC15]",
  Complex: "border-red-500/30 bg-red-500/10 text-red-400",
};

export default function DiagnosisResultCard({
  report,
  isDemo = false,
  onReset,
}: DiagnosisResultCardProps) {
  const difficultyBadge = DIFFICULTY_STYLES[report.repairDifficulty] || DIFFICULTY_STYLES.Moderate;

  return (
    <motion.div
      initial={{ opacity: 0, y: 24, scale: 0.98 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ duration: 0.5, ease: EASE }}
      className="w-full space-y-6"
    >
      <GlassCard padding="lg" glowColor={isDemo ? "cyan" : "green"} hoverEffect={false}>
        {/* Top Header */}
        <div className="flex flex-wrap items-center justify-between gap-3 mb-6">
          <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-2xl border border-[#22C55E]/40 bg-[#22C55E]/15 text-[#22C55E]">
              <Sparkles className="size-5" />
            </div>
            <div>
              <span className="text-[10px] font-semibold uppercase tracking-widest text-[#06B6D4]">
                Diagnosis Report
              </span>
              <h2 className="text-xl font-bold text-white">AI Hardware Analysis</h2>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span
              className={cn(
                "rounded-full border px-3 py-1 text-xs font-semibold",
                isDemo
                  ? "border-amber-500/30 bg-amber-500/10 text-amber-400"
                  : "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
              )}
            >
              {isDemo ? "Sample Demo Diagnosis" : "Live AI Result"}
            </span>

            <GlassButton
              variant="secondary"
              size="sm"
              icon={<RotateCcw className="size-3.5" />}
              onClick={onReset}
            >
              New Diagnosis
            </GlassButton>
          </div>
        </div>

        {/* Grid Body */}
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
          {/* Left Column: Image with Bounding Box Overlay & Confidence */}
          <div className="lg:col-span-5 flex flex-col justify-between space-y-4">
            {report.imageUrl && (
              <div className="relative overflow-hidden rounded-2xl border border-white/15 bg-black/40 p-2">
                <img
                  src={report.imageUrl}
                  alt="Diagnosed device preview"
                  className="max-h-64 w-full rounded-xl object-contain"
                />
                {/* Bounding overlay effect */}
                <div className="absolute inset-4 rounded-xl border border-dashed border-[#22C55E]/60 pointer-events-none flex items-start justify-end p-2">
                  <span className="rounded-md border border-[#22C55E]/40 bg-[#22C55E]/20 px-2 py-0.5 text-[9px] font-bold text-[#22C55E] backdrop-blur-sm">
                    Fault Region Identified
                  </span>
                </div>
              </div>
            )}

            {/* Confidence Score Bar */}
            <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4">
              <div className="flex items-center justify-between mb-2 text-xs">
                <span className="font-semibold text-[#CBD5E1] flex items-center gap-1.5">
                  <ScanLine className="size-3.5 text-[#22C55E]" /> AI Confidence Score
                </span>
                <span className="font-bold text-[#22C55E]">{report.confidenceScore}%</span>
              </div>
              <div className="h-2 w-full overflow-hidden rounded-full bg-white/[0.08]">
                <div
                  className="h-full rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
                  style={{ width: `${report.confidenceScore}%` }}
                />
              </div>
            </div>
          </div>

          {/* Right Column: Probable Issue & Metrics */}
          <div className="lg:col-span-7 space-y-5">
            {/* Probable Issue Title */}
            <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-5">
              <span className="text-[10px] font-bold uppercase tracking-wider text-[#22C55E]">
                Probable Issue Detected
              </span>
              <h3 className="text-lg font-bold text-white mt-1">{report.probableIssue}</h3>
              <p className="mt-1 text-xs leading-relaxed text-[#CBD5E1]">
                Symptoms: {report.symptoms}
              </p>
            </div>

            {/* Key Metrics Cards */}
            <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
              <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3.5">
                <span className="text-[10px] font-semibold text-[#CBD5E1] flex items-center gap-1">
                  <DollarSign className="size-3 text-[#06B6D4]" /> Est. Repair Cost
                </span>
                <p className="text-lg font-bold text-white mt-1.5">
                  ${report.repairCost}
                </p>
              </div>

              <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3.5">
                <span className="text-[10px] font-semibold text-[#CBD5E1] flex items-center gap-1">
                  <Clock className="size-3 text-[#22C55E]" /> Est. Repair Time
                </span>
                <p className="text-lg font-bold text-white mt-1.5">
                  {report.repairTime}
                </p>
              </div>

              <div className="col-span-2 sm:col-span-1 rounded-2xl border border-white/10 bg-white/[0.04] p-3.5">
                <span className="text-[10px] font-semibold text-[#CBD5E1] flex items-center gap-1">
                  <Wrench className="size-3 text-[#FACC15]" /> Repair Difficulty
                </span>
                <div className="mt-1.5">
                  <span className={cn("rounded-full border px-2.5 py-0.5 text-xs font-bold", difficultyBadge)}>
                    {report.repairDifficulty}
                  </span>
                </div>
              </div>
            </div>

            {/* Observations List */}
            {report.observations && report.observations.length > 0 && (
              <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 space-y-2">
                <span className="text-xs font-semibold text-white flex items-center gap-1.5">
                  <Info className="size-3.5 text-[#06B6D4]" /> Diagnostic Findings & Evidence
                </span>
                <ul className="space-y-1 text-xs text-[#CBD5E1] pl-5 list-disc">
                  {report.observations.map((obs, i) => (
                    <li key={i}>{obs}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* Safety Warning Alert */}
            {report.safetyWarning && (
              <div className="flex items-start gap-3 rounded-2xl border border-amber-500/40 bg-amber-500/10 p-4 text-xs text-amber-200">
                <ShieldAlert className="size-5 text-amber-400 shrink-0 mt-0.5" />
                <div>
                  <p className="font-bold text-amber-300">Safety Precautions Required</p>
                  <p className="mt-0.5 leading-relaxed">{report.safetyWarning}</p>
                </div>
              </div>
            )}

            {/* Transition CTA to Phase 8 Recommendation Engine */}
            <div className="pt-2">
              <GlassButton
                href={`/recommendation?diagnosisId=${encodeURIComponent(report.id)}`}
                fullWidth
                size="lg"
                icon={<Sparkles className="size-4" />}
              >
                View Repair Plan & Recommendation →
              </GlassButton>
            </div>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
