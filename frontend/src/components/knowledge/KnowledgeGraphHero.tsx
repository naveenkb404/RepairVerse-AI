"use client";

import React from "react";
import { motion } from "framer-motion";
import { Brain, Network, Sparkles, Cpu, RefreshCw, Database, Layers, ShieldCheck } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";

interface KnowledgeGraphHeroProps {
  totalNodes: number;
  totalRelationships: number;
  totalInsights: number;
  averageConfidence: number;
  isRebuilding?: boolean;
  onRebuildGraph: () => void;
}

export default function KnowledgeGraphHero({
  totalNodes,
  totalRelationships,
  totalInsights,
  averageConfidence,
  isRebuilding = false,
  onRebuildGraph,
}: KnowledgeGraphHeroProps) {
  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-950/90 to-black/90 p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
      {/* Background Ambient Glows */}
      <div className="pointer-events-none absolute -top-24 -left-20 h-72 w-72 rounded-full bg-cyan-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-24 -right-20 h-80 w-80 rounded-full bg-emerald-500/20 blur-3xl" />

      <div className="relative z-10 flex flex-col gap-8 lg:flex-row lg:items-center lg:justify-between">
        {/* Left: Brand / Title / Purpose */}
        <div className="max-w-2xl">
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span className="inline-flex items-center gap-2 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-3 py-1 text-xs font-semibold text-cyan-300 uppercase shadow-inner">
              <Brain className="h-3.5 w-3.5 animate-pulse text-cyan-400" />
              AI Repair Knowledge Graph
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/20 bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300">
              <Network className="h-3.5 w-3.5" />
              Phase 32 Ecosystem Intelligence
            </span>
          </div>

          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight text-white">
            Ecosystem <span className="bg-gradient-to-r from-cyan-400 via-teal-300 to-emerald-400 bg-clip-text text-transparent">Learning Intelligence</span>
          </h1>

          <p className="mt-3 text-sm sm:text-base text-slate-300 leading-relaxed">
            RepairVerse AI learns relational connections across{" "}
            <span className="text-cyan-300 font-semibold">Devices → Components → Failures → Symptoms → Repairs → Outcomes</span>.
            Every observed repair deepens collective ecosystem accuracy.
          </p>

          <div className="mt-6 flex flex-wrap items-center gap-4">
            <button
              id="rebuild-knowledge-graph-btn"
              onClick={onRebuildGraph}
              disabled={isRebuilding}
              className="group relative inline-flex items-center gap-2.5 rounded-xl bg-gradient-to-r from-cyan-500 to-emerald-500 px-5 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-cyan-500/20 transition-all hover:scale-[1.02] hover:shadow-emerald-500/30 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <RefreshCw className={cn("h-4 w-4", isRebuilding && "animate-spin")} />
              <span>{isRebuilding ? "Synchronizing Graph..." : "Synchronize Knowledge Graph"}</span>
              <Sparkles className="h-4 w-4 opacity-70 group-hover:rotate-12 transition-transform" />
            </button>
          </div>
        </div>

        {/* Right: Quick Stats Grid */}
        <div className="grid grid-cols-2 gap-3 sm:gap-4 sm:w-full lg:w-auto lg:min-w-[340px]">
          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Entities (Nodes)</span>
              <Database className="h-4 w-4 text-cyan-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-white font-mono">{totalNodes}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Models, parts & failure modes</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Relationships</span>
              <Network className="h-4 w-4 text-emerald-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-emerald-300 font-mono">{totalRelationships}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Weighted graph edges</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Discovered Patterns</span>
              <Sparkles className="h-4 w-4 text-amber-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-amber-300 font-mono">{totalInsights}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Ecosystem insights active</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Learning Confidence</span>
              <ShieldCheck className="h-4 w-4 text-teal-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-teal-300 font-mono">
              {Math.round(averageConfidence * 100)}%
            </div>
            <p className="text-[11px] text-slate-400 mt-0.5">Traceable evidence score</p>
          </div>
        </div>
      </div>
    </div>
  );
}
