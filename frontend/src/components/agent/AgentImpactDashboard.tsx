"use client";

import React from "react";
import { DollarSign, Leaf, ShieldCheck, Sparkles, TrendingUp, Cpu, Award } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";

interface AgentImpactDashboardProps {
  totalMoneySaved: number;
  totalCo2AvoidedKg: number;
  completedExecutionsCount: number;
  monitoredDevicesCount: number;
}

export default function AgentImpactDashboard({
  totalMoneySaved,
  totalCo2AvoidedKg,
  completedExecutionsCount,
  monitoredDevicesCount,
}: AgentImpactDashboardProps) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {/* Financial Savings */}
      <GlassCard padding="sm" glowColor="green">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-slate-400">Total Capital Saved</span>
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            <DollarSign className="h-4 w-4" />
          </div>
        </div>
        <div className="mt-2 text-2xl font-extrabold text-emerald-300 font-mono">
          ${totalMoneySaved.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 })}
        </div>
        <div className="mt-1 flex items-center gap-1.5 text-[11px] text-emerald-400 font-medium">
          <TrendingUp className="h-3 w-3" />
          <span>Avoided replacement costs</span>
        </div>
      </GlassCard>

      {/* Carbon Avoidance */}
      <GlassCard padding="sm" glowColor="cyan">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-slate-400">CO₂ Emissions Avoided</span>
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            <Leaf className="h-4 w-4" />
          </div>
        </div>
        <div className="mt-2 text-2xl font-extrabold text-cyan-300 font-mono">
          {totalCo2AvoidedKg.toFixed(1)} <span className="text-sm font-normal text-slate-400">kg</span>
        </div>
        <div className="mt-1 flex items-center gap-1.5 text-[11px] text-cyan-400 font-medium">
          <Sparkles className="h-3 w-3" />
          <span>Circular lifecycle extension</span>
        </div>
      </GlassCard>

      {/* Autonomous Actions Executed */}
      <GlassCard padding="sm" glowColor="none">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-slate-400">Autonomous Actions</span>
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-teal-500/10 text-teal-400 border border-teal-500/20">
            <Cpu className="h-4 w-4" />
          </div>
        </div>
        <div className="mt-2 text-2xl font-extrabold text-teal-300 font-mono">
          {completedExecutionsCount}
        </div>
        <div className="mt-1 flex items-center gap-1.5 text-[11px] text-slate-400">
          <ShieldCheck className="h-3 w-3 text-emerald-400" />
          <span>Zero unauthorized actions</span>
        </div>
      </GlassCard>

      {/* Fleet Protection Rate */}
      <GlassCard padding="sm" glowColor="none">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-slate-400">Fleet Active Shield</span>
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
            <Award className="h-4 w-4" />
          </div>
        </div>
        <div className="mt-2 text-2xl font-extrabold text-indigo-300 font-mono">
          100%
        </div>
        <div className="mt-1 flex items-center gap-1.5 text-[11px] text-slate-400">
          <span>{monitoredDevicesCount} devices under 24/7 watch</span>
        </div>
      </GlassCard>
    </div>
  );
}
