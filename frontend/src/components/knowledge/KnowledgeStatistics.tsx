"use client";

import React from "react";
import { Database, Network, Sparkles, ShieldCheck, Activity, BarChart3 } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { KnowledgeGraphStatisticsResponse } from "@/lib/types/repairKnowledgeGraph";

interface KnowledgeStatisticsProps {
  statistics: KnowledgeGraphStatisticsResponse;
}

export default function KnowledgeStatistics({ statistics }: KnowledgeStatisticsProps) {
  const nodeEntries = Object.entries(statistics.nodeTypeDistribution || {});
  const relEntries = Object.entries(statistics.relationshipTypeDistribution || {});

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Node Type Breakdown */}
      <GlassCard padding="md" glowColor="none">
        <div className="space-y-4">
          <div className="flex items-center gap-2 border-b border-white/5 pb-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
              <Database className="h-4 w-4" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">Entity (Node) Distribution</h4>
              <p className="text-[11px] text-slate-400">Classified knowledge domain categories</p>
            </div>
          </div>

          <div className="space-y-3">
            {nodeEntries.map(([type, count]) => {
              const pct = Math.round((Number(count) / Math.max(1, statistics.totalNodes)) * 100);

              return (
                <div key={type} className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-slate-300 font-medium">{type.replace(/_/g, " ")}</span>
                    <span className="font-mono text-cyan-400 font-bold">{count} ({pct}%)</span>
                  </div>
                  <div className="w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
                    <div className="bg-gradient-to-r from-cyan-500 to-teal-400 h-full rounded-full" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </GlassCard>

      {/* Relationship Type Breakdown */}
      <GlassCard padding="md" glowColor="none">
        <div className="space-y-4">
          <div className="flex items-center gap-2 border-b border-white/5 pb-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              <Network className="h-4 w-4" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">Relationship Edge Distribution</h4>
              <p className="text-[11px] text-slate-400">Weighted connections linking domain entities</p>
            </div>
          </div>

          <div className="space-y-3">
            {relEntries.map(([type, count]) => {
              const pct = Math.round((Number(count) / Math.max(1, statistics.totalRelationships)) * 100);

              return (
                <div key={type} className="space-y-1">
                  <div className="flex items-center justify-between text-xs">
                    <span className="text-slate-300 font-medium">{type.replace(/_/g, " ")}</span>
                    <span className="font-mono text-emerald-400 font-bold">{count} ({pct}%)</span>
                  </div>
                  <div className="w-full bg-slate-800 rounded-full h-1.5 overflow-hidden">
                    <div className="bg-gradient-to-r from-emerald-500 to-cyan-400 h-full rounded-full" style={{ width: `${pct}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </GlassCard>
    </div>
  );
}
