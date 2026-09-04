"use client";

import React, { useState } from "react";
import { ShieldCheck, Search, Filter, AlertTriangle, Zap, CheckCircle2 } from "lucide-react";
import AutonomousInterventionCard from "./AutonomousInterventionCard";
import type { InterventionResponse, InterventionPriority } from "@/lib/types/autonomousRepairAgent";
import { cn } from "@/lib/utils";

interface InterventionPriorityBoardProps {
  interventions: InterventionResponse[];
  onApproveStep?: (stepId: string) => Promise<void> | void;
  onRejectStep?: (stepId: string) => Promise<void> | void;
  onExecuteStep?: (stepId: string) => Promise<void> | void;
}

export default function InterventionPriorityBoard({
  interventions,
  onApproveStep,
  onRejectStep,
  onExecuteStep,
}: InterventionPriorityBoardProps) {
  const [selectedPriority, setSelectedPriority] = useState<string>("ALL");
  const [searchQuery, setSearchQuery] = useState<string>("");

  const filtered = interventions.filter((item) => {
    const matchesPriority = selectedPriority === "ALL" || item.priority === selectedPriority;
    const matchesQuery =
      !searchQuery ||
      item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (item.deviceName && item.deviceName.toLowerCase().includes(searchQuery.toLowerCase())) ||
      item.description.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesPriority && matchesQuery;
  });

  const priorityCounts = {
    ALL: interventions.length,
    CRITICAL: interventions.filter((i) => i.priority === "CRITICAL").length,
    HIGH: interventions.filter((i) => i.priority === "HIGH").length,
    MEDIUM: interventions.filter((i) => i.priority === "MEDIUM").length,
    LOW: interventions.filter((i) => i.priority === "LOW").length,
  };

  return (
    <div className="space-y-6">
      {/* Search & Priority Filter Controls */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex flex-wrap items-center gap-2">
          {(["ALL", "CRITICAL", "HIGH", "MEDIUM", "LOW"] as const).map((p) => {
            const count = priorityCounts[p];
            const isSelected = selectedPriority === p;

            return (
              <button
                key={p}
                onClick={() => setSelectedPriority(p)}
                className={cn(
                  "inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-semibold transition-all",
                  isSelected
                    ? "bg-gradient-to-r from-emerald-500 to-cyan-500 text-slate-950 shadow-md shadow-emerald-500/20"
                    : "border border-white/10 bg-white/[0.03] text-slate-400 hover:bg-white/[0.08] hover:text-white"
                )}
              >
                <span>{p === "ALL" ? "All Interventions" : p}</span>
                <span
                  className={cn(
                    "rounded-full px-1.5 py-0.2 text-[10px] font-bold font-mono",
                    isSelected ? "bg-slate-950/30 text-slate-950" : "bg-white/10 text-slate-300"
                  )}
                >
                  {count}
                </span>
              </button>
            );
          })}
        </div>

        <div className="relative w-full sm:w-64">
          <Search className="pointer-events-none absolute left-3.5 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search active issues..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full rounded-xl border border-white/10 bg-white/[0.04] py-2 pl-9.5 pr-4 text-xs text-white placeholder:text-slate-500 focus:border-cyan-500/50 focus:outline-none focus:ring-1 focus:ring-cyan-500/50"
          />
        </div>
      </div>

      {/* Interventions Card Grid */}
      {filtered.length > 0 ? (
        <div className="grid grid-cols-1 gap-6">
          {filtered.map((item) => (
            <AutonomousInterventionCard
              key={item.id}
              intervention={item}
              onApproveStep={onApproveStep}
              onRejectStep={onRejectStep}
              onExecuteStep={onExecuteStep}
            />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-white/10 bg-white/[0.02] p-12 text-center">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 mb-4">
            <CheckCircle2 className="h-7 w-7" />
          </div>
          <h3 className="text-base font-bold text-white">No Interventions in this Category</h3>
          <p className="mt-1.5 max-w-sm text-xs text-slate-400">
            {searchQuery
              ? "No active proactive interventions match your search query."
              : "All monitored devices in this priority tier are currently within normal operating parameters."}
          </p>
        </div>
      )}
    </div>
  );
}
