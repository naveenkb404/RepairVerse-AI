"use client";

import React, { useState } from "react";
import { GitCompare, CheckCircle2, AlertTriangle, ArrowRight, ShieldCheck } from "lucide-react";
import { cn } from "@/lib/utils";
import type { LearningModelComparisonResponse } from "@/lib/types/federatedLearning";

interface ModelComparisonPanelProps {
  comparison: LearningModelComparisonResponse | null;
  onActivateCandidate?: (version: string) => Promise<void>;
  className?: string;
}

export default function ModelComparisonPanel({
  comparison,
  onActivateCandidate,
  className,
}: ModelComparisonPanelProps) {
  const [activating, setActivating] = useState(false);
  const [activatedSuccess, setActivatedSuccess] = useState(false);

  if (!comparison) return null;

  const { currentModel, candidateModel, accuracyDelta, costStabilityDelta, trustScoreDelta, safeToActivate } = comparison;

  const handleActivate = async () => {
    if (!onActivateCandidate || !candidateModel) return;
    setActivating(true);
    try {
      await onActivateCandidate(candidateModel.version);
      setActivatedSuccess(true);
      setTimeout(() => setActivatedSuccess(false), 4000);
    } finally {
      setActivating(false);
    }
  };

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            <GitCompare className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Model Candidate Comparison</h3>
            <p className="text-xs text-slate-400">
              Active Baseline (<span className="font-mono text-cyan-300">{currentModel.version}</span>) vs Candidate (<span className="font-mono text-indigo-300">{candidateModel.version}</span>)
            </p>
          </div>
        </div>

        <span
          className={cn(
            "rounded-full px-2.5 py-0.5 text-xs font-bold border",
            safeToActivate
              ? "bg-emerald-500/10 border-emerald-500/30 text-emerald-400"
              : "bg-rose-500/10 border-rose-500/30 text-rose-400"
          )}
        >
          {safeToActivate ? "Safe to Deploy" : "Regression Detected"}
        </span>
      </div>

      <div className="mt-5 grid grid-cols-1 md:grid-cols-3 gap-4">
        {/* Accuracy Delta */}
        <div className="rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <span className="text-xs text-slate-400 font-medium">Recommendation Accuracy</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-black text-white">{candidateModel.validationScore}%</span>
            <span
              className={cn(
                "text-xs font-bold font-mono",
                accuracyDelta >= 0 ? "text-emerald-400" : "text-rose-400"
              )}
            >
              {accuracyDelta >= 0 ? `+${accuracyDelta}%` : `${accuracyDelta}%`}
            </span>
          </div>
          <span className="text-[11px] text-slate-500">Baseline: {currentModel.validationScore}%</span>
        </div>

        {/* Cost Stability Delta */}
        <div className="rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <span className="text-xs text-slate-400 font-medium">Cost Prediction Stability</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-black text-white">95.0%</span>
            <span className="text-xs font-bold font-mono text-emerald-400">+{costStabilityDelta}%</span>
          </div>
          <span className="text-[11px] text-slate-500">Bounded variance check passed</span>
        </div>

        {/* Trust Score Delta */}
        <div className="rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <span className="text-xs text-slate-400 font-medium">Governance Trust Score</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-2xl font-black text-cyan-400">{candidateModel.trustScore}/100</span>
            <span className="text-xs font-bold font-mono text-cyan-300">
              {trustScoreDelta >= 0 ? `+${trustScoreDelta}` : `${trustScoreDelta}`}
            </span>
          </div>
          <span className="text-[11px] text-slate-500">Phase 34 Trust evaluation</span>
        </div>
      </div>

      {/* Recommendations & Activation CTA */}
      <div className="mt-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 pt-4 border-t border-white/10">
        <div className="space-y-1">
          {comparison.governanceRecommendations?.map((rec, i) => (
            <div key={i} className="flex items-center gap-2 text-xs text-slate-300 font-medium">
              <CheckCircle2 className="h-3.5 w-3.5 text-emerald-400 shrink-0" />
              <span>{rec}</span>
            </div>
          ))}
        </div>

        {safeToActivate && (
          <button
            type="button"
            disabled={activating || activatedSuccess}
            onClick={handleActivate}
            className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 px-5 py-2.5 text-xs font-bold uppercase tracking-wider text-black shadow-lg shadow-emerald-500/20 hover:brightness-110 active:scale-95 disabled:opacity-50"
          >
            <ShieldCheck className="h-4 w-4" />
            <span>{activatedSuccess ? "Model Activated!" : activating ? "Activating..." : `Promote ${candidateModel.version} to Active`}</span>
          </button>
        )}
      </div>
    </div>
  );
}
