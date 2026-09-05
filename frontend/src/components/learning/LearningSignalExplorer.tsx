"use client";

import React, { useState, useMemo } from "react";
import { Search, Filter, Cpu, CheckCircle, Database, Shield, Zap } from "lucide-react";
import { cn } from "@/lib/utils";
import type { LearningSignalResponse } from "@/lib/types/federatedLearning";

interface LearningSignalExplorerProps {
  signals: LearningSignalResponse[];
  className?: string;
}

export default function LearningSignalExplorer({
  signals,
  className,
}: LearningSignalExplorerProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("ALL");

  const filteredSignals = useMemo(() => {
    return signals.filter((s) => {
      const matchesSearch =
        s.componentType.toLowerCase().includes(searchTerm.toLowerCase()) ||
        s.repairAction.toLowerCase().includes(searchTerm.toLowerCase()) ||
        s.failureMode.toLowerCase().includes(searchTerm.toLowerCase()) ||
        s.deviceCategory.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesCat = categoryFilter === "ALL" || s.deviceCategory === categoryFilter;
      return matchesSearch && matchesCat;
    });
  }, [signals, searchTerm, categoryFilter]);

  const categories = useMemo(() => {
    const set = new Set(signals.map((s) => s.deviceCategory));
    return ["ALL", ...Array.from(set)];
  }, [signals]);

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-5 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
            <Database className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Aggregated Learning Signals</h3>
            <p className="text-xs text-slate-400">
              Deterministic bounded weights learned across repair categories (N &ge; 5)
            </p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-400" />
            <input
              type="text"
              placeholder="Search components or actions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="h-9 rounded-xl border border-white/10 bg-slate-950/80 pl-8 pr-3 text-xs text-slate-200 placeholder-slate-500 focus:border-indigo-500 focus:outline-none"
            />
          </div>

          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="h-9 rounded-xl border border-white/10 bg-slate-950/80 px-3 text-xs text-slate-200 focus:border-indigo-500 focus:outline-none"
          >
            {categories.map((c) => (
              <option key={c} value={c}>
                Category: {c}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
        {filteredSignals.length === 0 ? (
          <div className="col-span-2 py-8 text-center text-xs text-slate-400">
            No learning signals match your filter.
          </div>
        ) : (
          filteredSignals.map((signal) => (
            <div
              key={signal.id}
              className="flex flex-col justify-between gap-3 rounded-xl border border-white/5 bg-slate-950/60 p-4 transition hover:border-indigo-500/30 hover:bg-slate-900/60"
            >
              <div>
                <div className="flex items-center justify-between gap-2">
                  <span className="rounded bg-indigo-500/10 px-2 py-0.5 text-[10px] font-mono font-bold text-indigo-300 border border-indigo-500/20">
                    {signal.deviceCategory} • {signal.componentType.replace(/_/g, " ")}
                  </span>

                  <span className="rounded bg-emerald-500/10 px-2 py-0.5 text-[10px] font-bold text-emerald-400 border border-emerald-500/20">
                    {Math.round(signal.successRate * 100)}% Success
                  </span>
                </div>

                <h4 className="mt-2 text-sm font-bold text-white">
                  {signal.repairAction.replace(/_/g, " ")}
                </h4>

                <p className="mt-0.5 text-xs text-slate-400 font-medium">
                  Resolves {signal.failureMode.replace(/_/g, " ").toLowerCase()}
                </p>
              </div>

              <div className="grid grid-cols-3 gap-2 pt-3 border-t border-white/5 text-[11px] font-mono">
                <div className="space-y-0.5">
                  <span className="text-[10px] text-slate-500 uppercase">Observations</span>
                  <div className="font-bold text-white">{signal.observationCount} cases</div>
                </div>
                <div className="space-y-0.5">
                  <span className="text-[10px] text-slate-500 uppercase">Avg Cost</span>
                  <div className="font-bold text-cyan-300">₹{signal.averageCost.toLocaleString()}</div>
                </div>
                <div className="space-y-0.5">
                  <span className="text-[10px] text-slate-500 uppercase">Lifespan Gain</span>
                  <div className="font-bold text-emerald-300">+{signal.averageLifespanGain} mo</div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
