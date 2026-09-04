"use client";

import React from "react";
import { Brain, Sparkles, CheckCircle2, ShieldCheck, ArrowRight, BookOpen } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { KnowledgeRecommendationResponse } from "@/lib/types/repairKnowledgeGraph";
import { cn } from "@/lib/utils";

interface KnowledgeRecommendationsProps {
  recommendations: KnowledgeRecommendationResponse[];
}

export default function KnowledgeRecommendations({
  recommendations,
}: KnowledgeRecommendationsProps) {
  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-white/10 pb-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            <Brain className="h-5 w-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Ecosystem-Backed Repair Recommendations</h3>
            <p className="text-xs text-slate-400">Traceable prescriptive guidance derived from historical graph evidence</p>
          </div>
        </div>
      </div>

      {/* Recommendations Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {recommendations.map((rec) => (
          <GlassCard key={rec.id} padding="md" glowColor="cyan" className="overflow-hidden flex flex-col justify-between">
            <div className="space-y-4">
              {/* Badge & Seen Before Header */}
              <div className="flex items-center justify-between gap-2 border-b border-white/5 pb-3">
                <span className="inline-flex items-center gap-1.5 text-xs font-extrabold uppercase tracking-wider text-cyan-400">
                  <Brain className="h-4 w-4 text-cyan-300" />
                  RepairVerse Has Seen This Before
                </span>

                <span
                  className={cn(
                    "rounded-full border px-2.5 py-0.5 text-[10px] font-bold font-mono uppercase",
                    rec.priority === "HIGH"
                      ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-400"
                      : "border-cyan-500/30 bg-cyan-500/10 text-cyan-300"
                  )}
                >
                  {rec.priority} Priority
                </span>
              </div>

              {/* Title & Expected Outcome */}
              <div>
                <h4 className="text-base font-bold text-white leading-snug">{rec.recommendation}</h4>
                <div className="mt-2 rounded-xl border border-emerald-500/20 bg-emerald-950/20 p-2.5 text-xs font-medium text-emerald-300">
                  {rec.expectedOutcome}
                </div>
              </div>

              {/* Reasoning */}
              <p className="text-xs text-slate-300 leading-relaxed">{rec.reasoning}</p>
            </div>

            {/* Evidence Summary Footer */}
            <div className="mt-4 pt-3 border-t border-white/5 space-y-2">
              <div className="flex items-center justify-between text-[11px] font-mono text-slate-400">
                <span className="flex items-center gap-1 text-emerald-400 font-bold">
                  <CheckCircle2 className="h-3.5 w-3.5" />
                  {Math.round(rec.confidence * 100)}% Confidence
                </span>
                <span>{rec.supportingCases} Verified Cases</span>
              </div>

              <div className="rounded-lg border border-white/5 bg-white/[0.02] p-2 text-[11px] text-slate-400 leading-normal flex items-start gap-1.5">
                <BookOpen className="h-3.5 w-3.5 text-cyan-400 shrink-0 mt-0.5" />
                <span><strong>Evidence:</strong> {rec.evidenceSummary}</span>
              </div>
            </div>
          </GlassCard>
        ))}
      </div>
    </div>
  );
}
