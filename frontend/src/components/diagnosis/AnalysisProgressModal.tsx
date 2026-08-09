"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { Cpu, CheckCircle2, Loader2, Sparkles, ScanLine } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

const STAGES = [
  { step: 1, label: "Preparing & Pre-processing Image", durationMs: 400 },
  { step: 2, label: "Identifying Device Hardware & Model", durationMs: 500 },
  { step: 3, label: "Analyzing Damage Signatures & Circuits", durationMs: 600 },
  { step: 4, label: "Evaluating Repairability & Safety Metrics", durationMs: 500 },
  { step: 5, label: "Generating Diagnostic & Cost Report", durationMs: 400 },
];

export type AnalysisProgressModalProps = {
  isAnalyzing: boolean;
  onComplete: () => void;
};

export default function AnalysisProgressModal({
  isAnalyzing,
  onComplete,
}: AnalysisProgressModalProps) {
  const [currentStageIndex, setCurrentStageIndex] = useState(0);

  useEffect(() => {
    if (!isAnalyzing) {
      setCurrentStageIndex(0);
      return;
    }

    let isMounted = true;
    let timer: NodeJS.Timeout;

    const runStages = (index: number) => {
      if (index >= STAGES.length) {
        if (isMounted) onComplete();
        return;
      }
      setCurrentStageIndex(index);
      timer = setTimeout(() => {
        runStages(index + 1);
      }, STAGES[index].durationMs);
    };

    runStages(0);

    return () => {
      isMounted = false;
      clearTimeout(timer);
    };
  }, [isAnalyzing, onComplete]);

  if (!isAnalyzing) return null;

  const currentStage = STAGES[Math.min(currentStageIndex, STAGES.length - 1)];
  const progressPercent = Math.round(((currentStageIndex + 1) / STAGES.length) * 100);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 backdrop-blur-md">
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 16 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 16 }}
        transition={{ duration: 0.35, ease: EASE }}
        className="w-full max-w-md"
      >
        <GlassCard padding="lg" glowColor="mixed" hoverEffect={false}>
          {/* Header */}
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <div className="flex size-10 items-center justify-center rounded-2xl border border-[#22C55E]/40 bg-[#22C55E]/15 text-[#22C55E]">
                <Cpu className="size-5 animate-pulse" />
              </div>
              <div>
                <h3 className="text-base font-bold text-white">AI Hardware Diagnosis</h3>
                <p className="text-xs text-[#CBD5E1]">Neural Vision Processing</p>
              </div>
            </div>
            <span className="flex items-center gap-1.5 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-3 py-1 text-xs font-bold text-[#06B6D4]">
              <Sparkles className="size-3" />
              {progressPercent}%
            </span>
          </div>

          {/* Progress Bar */}
          <div className="mb-6 h-2 w-full overflow-hidden rounded-full bg-white/[0.08]">
            <motion.div
              className="h-full rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
              initial={{ width: "0%" }}
              animate={{ width: `${progressPercent}%` }}
              transition={{ duration: 0.4, ease: EASE }}
            />
          </div>

          {/* Stages List */}
          <div className="space-y-3">
            {STAGES.map((s, i) => {
              const isDone = i < currentStageIndex;
              const isCurrent = i === currentStageIndex;

              return (
                <div
                  key={s.step}
                  className={`flex items-center gap-3 rounded-xl p-2.5 text-xs transition-colors ${
                    isCurrent
                      ? "border border-[#22C55E]/30 bg-[#22C55E]/10 font-semibold text-white"
                      : isDone
                      ? "text-white/80"
                      : "text-white/30"
                  }`}
                >
                  {isDone ? (
                    <CheckCircle2 className="size-4 shrink-0 text-[#22C55E]" />
                  ) : isCurrent ? (
                    <Loader2 className="size-4 shrink-0 text-[#22C55E] animate-spin" />
                  ) : (
                    <ScanLine className="size-4 shrink-0 opacity-40" />
                  )}
                  <span>{s.label}</span>
                </div>
              );
            })}
          </div>

          <p className="mt-6 text-center text-[10px] text-white/40">
            Please wait while the diagnostic engine compiles component analysis...
          </p>
        </GlassCard>
      </motion.div>
    </div>
  );
}
