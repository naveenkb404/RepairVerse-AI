"use client";

import React from "react";
import { CheckCircle2, DollarSign, Leaf, Clock, BookOpen, Layers, Laptop, Smartphone, Headphones, Tv } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { SimilarRepairCaseResponse } from "@/lib/types/repairKnowledgeGraph";
import { cn } from "@/lib/utils";

interface SimilarRepairCasesProps {
  cases: SimilarRepairCaseResponse[];
}

export default function SimilarRepairCases({ cases }: SimilarRepairCasesProps) {
  const getDeviceIcon = (category: string) => {
    const cat = category.toUpperCase();
    if (cat === "LAPTOP" || cat === "COMPUTER") return Laptop;
    if (cat === "SMARTPHONE" || cat === "PHONE") return Smartphone;
    if (cat === "AUDIO" || cat === "HEADPHONES") return Headphones;
    return Tv;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-white/10 pb-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-teal-500/10 text-teal-400 border border-teal-500/20">
            <Layers className="h-5 w-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Anonymized Similar Repair Cases</h3>
            <p className="text-xs text-slate-400">Traceable historical precedent from verified ecosystem repairs</p>
          </div>
        </div>
        <span className="rounded-full border border-teal-500/30 bg-teal-500/10 px-3 py-1 text-xs font-bold text-teal-300 font-mono">
          {cases.length} Cases Matched
        </span>
      </div>

      {/* Case List */}
      <div className="space-y-4">
        {cases.map((c) => {
          const DeviceIcon = getDeviceIcon(c.deviceCategory);

          return (
            <GlassCard key={c.caseId} padding="md" glowColor="none" className="overflow-hidden">
              <div className="space-y-4">
                {/* Top Info Bar */}
                <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between border-b border-white/5 pb-3">
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-white/[0.05] border border-white/10 text-cyan-400">
                      <DeviceIcon className="h-4.5 w-4.5" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-bold text-white">{c.deviceModel}</span>
                        <span className="rounded bg-white/5 border border-white/10 px-2 py-0.2 text-[10px] text-slate-300 font-mono">
                          {c.caseId}
                        </span>
                      </div>
                      <span className="text-[11px] text-slate-400">{c.deviceCategory}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-3 self-end sm:self-auto">
                    <span className="inline-flex items-center gap-1 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-3 py-1 text-xs font-bold text-emerald-300 font-mono">
                      <CheckCircle2 className="h-3.5 w-3.5" />
                      {c.similarityScore.toFixed(1)}% Match
                    </span>
                  </div>
                </div>

                {/* Problem vs Repair Performed Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
                  <div className="rounded-xl border border-white/5 bg-black/20 p-3">
                    <span className="font-semibold text-slate-400">Historical Problem:</span>
                    <p className="mt-1 text-slate-200 leading-relaxed">{c.issueSummary}</p>
                  </div>

                  <div className="rounded-xl border border-white/5 bg-black/20 p-3">
                    <span className="font-semibold text-cyan-400">Repair Action Performed:</span>
                    <p className="mt-1 text-slate-200 leading-relaxed">{c.repairAction}</p>
                  </div>
                </div>

                {/* Key Metrics Ribbon */}
                <div className="grid grid-cols-3 gap-2 rounded-xl border border-white/5 bg-white/[0.02] p-2.5 text-center text-[11px] font-mono">
                  <div>
                    <span className="text-slate-400">Cost Range</span>
                    <div className="font-bold text-slate-200 mt-0.5">{c.costRange}</div>
                  </div>
                  <div>
                    <span className="text-emerald-400">CO₂ Avoided</span>
                    <div className="font-bold text-emerald-300 mt-0.5">{c.co2AvoidedKg} kg</div>
                  </div>
                  <div>
                    <span className="text-cyan-400">Turnaround</span>
                    <div className="font-bold text-cyan-300 mt-0.5">{c.durationDays} Day</div>
                  </div>
                </div>

                {/* Lesson Learned */}
                <div className="flex items-start gap-2 rounded-xl border border-teal-500/20 bg-teal-950/20 p-3 text-xs text-teal-200">
                  <BookOpen className="h-4 w-4 text-teal-400 shrink-0 mt-0.5" />
                  <div>
                    <strong className="text-teal-300">Ecosystem Lesson Learned:</strong> {c.lessonLearned}
                  </div>
                </div>
              </div>
            </GlassCard>
          );
        })}
      </div>
    </div>
  );
}
