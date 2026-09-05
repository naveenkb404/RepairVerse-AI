"use client";

import React from "react";
import { GitCommit, ShieldCheck, CheckCircle2, Clock, Sparkles } from "lucide-react";
import { cn } from "@/lib/utils";
import type { ModelVersionResponse } from "@/lib/types/federatedLearning";

interface ModelVersionTimelineProps {
  history: ModelVersionResponse[];
  onSelectVersion?: (version: string) => void;
  className?: string;
}

export default function ModelVersionTimeline({
  history,
  onSelectVersion,
  className,
}: ModelVersionTimelineProps) {
  const getStatusBadge = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return "border-emerald-500/30 bg-emerald-500/10 text-emerald-400";
      case "SUPERSEDED":
        return "border-slate-500/30 bg-slate-500/10 text-slate-400";
      case "APPROVED":
        return "border-cyan-500/30 bg-cyan-500/10 text-cyan-400";
      default:
        return "border-amber-500/30 bg-amber-500/10 text-amber-400";
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
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-purple-500/10 text-purple-400 border border-purple-500/20">
            <GitCommit className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Model Version Lineage</h3>
            <p className="text-xs text-slate-400">
              Immutable version history of validated federated intelligence iterations
            </p>
          </div>
        </div>

        <span className="rounded-full bg-purple-500/10 px-2.5 py-0.5 text-xs font-semibold text-purple-300 border border-purple-500/20">
          {history.length} Iterations Logged
        </span>
      </div>

      <div className="mt-6 space-y-4">
        {history.map((model, idx) => {
          const isActive = model.status === "ACTIVE";

          return (
            <div
              key={model.id || idx}
              onClick={() => onSelectVersion && onSelectVersion(model.version)}
              className={cn(
                "relative flex flex-col sm:flex-row sm:items-center justify-between gap-4 rounded-xl border p-4 transition cursor-pointer",
                isActive
                  ? "border-emerald-500/40 bg-emerald-950/20 shadow-lg shadow-emerald-500/5"
                  : "border-white/5 bg-slate-950/60 hover:border-white/15"
              )}
            >
              <div className="flex items-center gap-3.5">
                <div
                  className={cn(
                    "flex h-9 w-9 items-center justify-center rounded-xl border font-mono text-xs font-bold",
                    isActive
                      ? "border-emerald-500/30 bg-emerald-500/20 text-emerald-400"
                      : "border-white/10 bg-slate-900 text-slate-400"
                  )}
                >
                  {model.version}
                </div>

                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="text-sm font-bold text-white">{model.modelName}</h4>
                    <span
                      className={cn(
                        "rounded-full px-2 py-0.5 text-[10px] font-bold uppercase border",
                        getStatusBadge(model.status)
                      )}
                    >
                      {model.status}
                    </span>
                  </div>

                  <p className="text-xs text-slate-400 mt-0.5">
                    Parent: <span className="font-mono text-slate-300">{model.parentVersion || "Genesis"}</span> • Training observations: <strong className="text-white">{model.trainingObservations.toLocaleString()}</strong>
                  </p>
                </div>
              </div>

              <div className="flex items-center gap-4 text-xs font-mono">
                <div className="text-right">
                  <span className="text-[10px] text-slate-500 uppercase">Validation</span>
                  <div className="font-bold text-indigo-400">{model.validationScore}%</div>
                </div>

                <div className="text-right">
                  <span className="text-[10px] text-slate-500 uppercase">Trust Score</span>
                  <div className="font-bold text-cyan-400">{model.trustScore}/100</div>
                </div>

                <div className="text-right">
                  <span className="text-[10px] text-slate-500 uppercase">Improvement</span>
                  <div className="font-bold text-emerald-400">+{model.improvementPercentage}%</div>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
