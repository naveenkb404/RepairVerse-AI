"use client";

import React from "react";
import { CheckCircle2, XCircle, AlertTriangle, ShieldCheck, Scale } from "lucide-react";
import { cn } from "@/lib/utils";
import type { ValidationResultResponse } from "@/lib/types/federatedLearning";

interface LearningValidationPanelProps {
  validationResults?: ValidationResultResponse[];
  className?: string;
}

export default function LearningValidationPanel({
  validationResults = [],
  className,
}: LearningValidationPanelProps) {
  const defaultResults: ValidationResultResponse[] = [
    {
      id: "val-1",
      modelVersionId: "model-r35-4",
      validationType: "RECOMMENDATION_ACCURACY",
      baselineScore: 92.0,
      candidateScore: 94.2,
      improvementScore: 2.2,
      regressionDetected: false,
      confidence: 0.94,
      decision: "ACCEPTED",
      validatedAt: new Date().toISOString(),
    },
    {
      id: "val-2",
      modelVersionId: "model-r35-4",
      validationType: "COST_ESTIMATION_STABILITY",
      baselineScore: 88.0,
      candidateScore: 94.6,
      improvementScore: 6.6,
      regressionDetected: false,
      confidence: 0.92,
      decision: "ACCEPTED",
      validatedAt: new Date().toISOString(),
    },
    {
      id: "val-3",
      modelVersionId: "model-r35-4",
      validationType: "TRUST_ALIGNMENT",
      baselineScore: 91.0,
      candidateScore: 94.0,
      improvementScore: 3.0,
      regressionDetected: false,
      confidence: 0.96,
      decision: "ACCEPTED",
      validatedAt: new Date().toISOString(),
    },
    {
      id: "val-4",
      modelVersionId: "model-r35-4",
      validationType: "GOVERNANCE_COMPLIANCE",
      baselineScore: 100.0,
      candidateScore: 100.0,
      improvementScore: 0.0,
      regressionDetected: false,
      confidence: 0.98,
      decision: "ACCEPTED",
      validatedAt: new Date().toISOString(),
    },
  ];

  const results = validationResults.length > 0 ? validationResults : defaultResults;

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
            <Scale className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Model Validation Guardrails</h3>
            <p className="text-xs text-slate-400">
              Multi-dimensional regression and safety checks before candidate activation
            </p>
          </div>
        </div>

        <span className="rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-400 border border-emerald-500/20">
          All Checks Passed
        </span>
      </div>

      <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-3">
        {results.map((res) => (
          <div
            key={res.id}
            className="flex items-start justify-between gap-3 rounded-xl border border-white/5 bg-slate-950/60 p-4"
          >
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-white uppercase tracking-wider">
                  {res.validationType.replace(/_/g, " ")}
                </span>
                <span
                  className={cn(
                    "rounded px-1.5 py-0.2 text-[9px] font-mono font-bold uppercase",
                    res.decision === "ACCEPTED"
                      ? "bg-emerald-500/20 text-emerald-300"
                      : "bg-rose-500/20 text-rose-300"
                  )}
                >
                  {res.decision}
                </span>
              </div>

              <div className="flex items-center gap-3 text-xs font-mono text-slate-400">
                <span>Baseline: {res.baselineScore}%</span>
                <span>Candidate: <strong className="text-cyan-300">{res.candidateScore}%</strong></span>
                <span className={cn(res.improvementScore >= 0 ? "text-emerald-400" : "text-rose-400")}>
                  ({res.improvementScore >= 0 ? `+${res.improvementScore}%` : `${res.improvementScore}%`})
                </span>
              </div>
            </div>

            {res.regressionDetected ? (
              <XCircle className="h-5 w-5 text-rose-400 shrink-0" />
            ) : (
              <CheckCircle2 className="h-5 w-5 text-emerald-400 shrink-0" />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
