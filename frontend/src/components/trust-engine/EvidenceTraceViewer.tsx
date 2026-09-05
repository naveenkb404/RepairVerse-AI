"use client";

import React from "react";
import { Database, Activity, GitFork, CheckCircle, ShieldCheck, Layers } from "lucide-react";
import { cn } from "@/lib/utils";
import type { EvidenceTraceResponse } from "@/lib/types/trustEngine";

interface EvidenceTraceViewerProps {
  evidenceTraces: EvidenceTraceResponse[];
  className?: string;
}

export default function EvidenceTraceViewer({
  evidenceTraces,
  className,
}: EvidenceTraceViewerProps) {
  if (!evidenceTraces || evidenceTraces.length === 0) {
    return (
      <div className={cn("rounded-2xl border border-white/10 bg-slate-900/60 p-6 text-center text-sm text-slate-400", className)}>
        No discrete evidence traces logged for this decision record.
      </div>
    );
  }

  const getSourceIcon = (source: string) => {
    if (source.includes("SENSOR") || source.includes("TELEMETRY")) return Activity;
    if (source.includes("GRAPH") || source.includes("KNOWLEDGE")) return GitFork;
    if (source.includes("TWIN") || source.includes("SIMULATOR")) return Layers;
    return Database;
  };

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2">
          <ShieldCheck className="h-5 w-5 text-cyan-400" />
          <h3 className="text-base font-bold text-white">Decision Evidence Traces</h3>
        </div>
        <span className="rounded-full bg-cyan-500/10 px-2.5 py-0.5 text-xs font-semibold text-cyan-300 border border-cyan-500/20">
          {evidenceTraces.length} Signals Captured
        </span>
      </div>

      <div className="mt-4 space-y-3">
        {evidenceTraces.map((trace, idx) => {
          const Icon = getSourceIcon(trace.evidenceSource);
          const weightPct = Math.round((trace.evidenceWeight ?? 1) * 100);

          return (
            <div
              key={trace.id || idx}
              className="group relative flex flex-col gap-2 rounded-xl border border-white/5 bg-slate-950/60 p-4 transition hover:border-cyan-500/30 hover:bg-slate-900/80"
            >
              <div className="flex flex-wrap items-center justify-between gap-2">
                <div className="flex items-center gap-2">
                  <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                    <Icon className="h-4 w-4" />
                  </div>
                  <div>
                    <span className="text-xs font-mono font-bold text-cyan-300 uppercase">
                      {trace.evidenceKey.replace(/_/g, " ")}
                    </span>
                    <span className="ml-2 text-[11px] text-slate-500 font-mono">
                      [{trace.evidenceType}]
                    </span>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <span className="rounded-md bg-white/[0.04] px-2 py-0.5 text-[11px] font-mono text-slate-400 border border-white/5">
                    Signal Weight: {weightPct}%
                  </span>
                </div>
              </div>

              <p className="text-xs text-slate-200 leading-relaxed font-medium pl-9">
                {trace.evidenceValue}
              </p>

              <div className="pl-9 flex items-center gap-1.5 text-[10px] text-slate-500">
                <span>Source Provenance:</span>
                <span className="font-mono text-slate-400">{trace.evidenceSource}</span>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
