"use client";

import React from "react";
import { CheckCircle2, XCircle, Clock, Activity, Cpu, Sparkles } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { ExecutionHistoryResponse } from "@/lib/types/autonomousRepairAgent";
import { cn } from "@/lib/utils";

interface AgentActivityTimelineProps {
  executions: ExecutionHistoryResponse[];
}

export default function AgentActivityTimeline({ executions }: AgentActivityTimelineProps) {
  const formatDate = (isoString: string) => {
    try {
      const d = new Date(isoString);
      return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) + " • " + d.toLocaleDateString([], { month: "short", day: "numeric" });
    } catch {
      return isoString;
    }
  };

  return (
    <GlassCard padding="md" glowColor="none">
      <div className="space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/5 pb-3">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
              <Activity className="h-4 w-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Autonomous Agent Activity Stream</h3>
              <p className="text-xs text-slate-400">Verifiable trace of proactive actions executed across your fleet</p>
            </div>
          </div>
          <span className="text-xs text-slate-400 font-mono">{executions.length} Actions Logged</span>
        </div>

        {/* Timeline Items */}
        {executions.length === 0 ? (
          <div className="p-6 text-center text-xs text-slate-400">No autonomous actions recorded yet.</div>
        ) : (
          <div className="relative pl-6 space-y-4 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-gradient-to-b before:from-cyan-500/40 before:via-emerald-500/20 before:to-transparent">
            {executions.map((item) => {
              const isSuccess = item.executionStatus === "SUCCESS";

              return (
                <div key={item.id} className="relative group">
                  {/* Node dot */}
                  <div
                    className={cn(
                      "absolute -left-6 top-1.5 flex h-5 w-5 items-center justify-center rounded-full border bg-slate-950",
                      isSuccess
                        ? "border-emerald-500/40 text-emerald-400"
                        : "border-red-500/40 text-red-400"
                    )}
                  >
                    {isSuccess ? <CheckCircle2 className="h-3 w-3" /> : <XCircle className="h-3 w-3" />}
                  </div>

                  <div className="rounded-xl border border-white/5 bg-white/[0.02] p-3.5 transition-colors group-hover:border-white/10 group-hover:bg-white/[0.04]">
                    <div className="flex flex-wrap items-center justify-between gap-2">
                      <div className="flex items-center gap-2">
                        <span className="text-xs font-semibold text-white">{item.resultSummary}</span>
                      </div>
                      <span className="text-[11px] font-mono text-slate-400">{formatDate(item.executedAt)}</span>
                    </div>

                    <div className="mt-1.5 flex flex-wrap items-center gap-2">
                      {item.deviceName && (
                        <span className="rounded bg-white/5 border border-white/10 px-2 py-0.5 text-[10px] text-slate-300">
                          {item.deviceName}
                        </span>
                      )}
                      <span className="rounded bg-cyan-500/10 border border-cyan-500/20 px-2 py-0.5 text-[10px] font-mono text-cyan-300">
                        {item.actionType}
                      </span>
                      <span
                        className={cn(
                          "rounded px-1.5 py-0.5 text-[10px] font-bold font-mono",
                          isSuccess ? "text-emerald-400" : "text-red-400"
                        )}
                      >
                        {item.executionStatus}
                      </span>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </GlassCard>
  );
}
