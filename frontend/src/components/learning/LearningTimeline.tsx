"use client";

import React from "react";
import { Clock, ShieldCheck, Database, CheckCircle, Brain, GitCommit } from "lucide-react";
import { cn } from "@/lib/utils";

export default function LearningTimeline({ className }: { className?: string }) {
  const events = [
    {
      title: "Model Version R35.4 Promoted to Active",
      description: "Passed all 4 validation dimensions with +8.7% composite improvement. Superseded R35.3.",
      time: "2 days ago",
      icon: CheckCircle,
      color: "text-emerald-400 bg-emerald-500/10 border-emerald-500/30",
    },
    {
      title: "Knowledge Graph Relationship Weights Updated",
      description: "1,284 verified repair signals propagated into component resolution edges.",
      time: "2 days ago",
      icon: Brain,
      color: "text-purple-400 bg-purple-500/10 border-purple-500/30",
    },
    {
      title: "Privacy Audit Complete for Batch BATCH-2026-09-001",
      description: "1,340 raw outcome records processed, 56 sub-threshold records filtered, zero PII transmitted.",
      time: "3 days ago",
      icon: ShieldCheck,
      color: "text-cyan-400 bg-cyan-500/10 border-cyan-500/30",
    },
    {
      title: "Continuous Learning Cycle Initiated",
      description: "18 bounded component signals extracted from multi-sensor diagnostics and digital twins.",
      time: "3 days ago",
      icon: Database,
      color: "text-indigo-400 bg-indigo-500/10 border-indigo-500/30",
    },
  ];

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
            <Clock className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Federated Learning Event Stream</h3>
            <p className="text-xs text-slate-400">
              Chronological log of learning batches, privacy audits, and version activations
            </p>
          </div>
        </div>

        <span className="rounded-full bg-indigo-500/10 px-2.5 py-0.5 text-xs font-semibold text-indigo-300 border border-indigo-500/20">
          Continuous Engine
        </span>
      </div>

      <div className="mt-5 space-y-4">
        {events.map((ev, i) => {
          const Icon = ev.icon;
          return (
            <div key={i} className="flex items-start gap-3.5">
              <div
                className={cn(
                  "flex size-8 shrink-0 items-center justify-center rounded-xl border",
                  ev.color
                )}
              >
                <Icon className="size-4" />
              </div>

              <div className="flex-1 rounded-xl border border-white/5 bg-slate-950/60 p-3.5">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h4 className="text-xs font-bold text-white">{ev.title}</h4>
                  <span className="text-[10px] text-slate-500 font-mono">{ev.time}</span>
                </div>
                <p className="text-xs text-slate-300 mt-1 leading-relaxed">{ev.description}</p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
