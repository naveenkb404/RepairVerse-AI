"use client";

import React from "react";
import { motion } from "framer-motion";
import { Bot, Sparkles, ShieldCheck, Cpu, RefreshCw, AlertTriangle, CheckCircle2 } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";

interface RepairAgentHeroProps {
  agentStatus: "ACTIVE" | "IDLE" | "ATTENTION_REQUIRED";
  monitoredDevicesCount: number;
  activeInterventionsCount: number;
  pendingApprovalsCount: number;
  completedExecutionsCount: number;
  isEvaluatingAll?: boolean;
  onEvaluateAll: () => void;
}

export default function RepairAgentHero({
  agentStatus,
  monitoredDevicesCount,
  activeInterventionsCount,
  pendingApprovalsCount,
  completedExecutionsCount,
  isEvaluatingAll = false,
  onEvaluateAll,
}: RepairAgentHeroProps) {
  const statusColor =
    agentStatus === "ATTENTION_REQUIRED"
      ? "text-amber-400 border-amber-500/30 bg-amber-500/10"
      : agentStatus === "ACTIVE"
      ? "text-emerald-400 border-emerald-500/30 bg-emerald-500/10"
      : "text-cyan-400 border-cyan-500/30 bg-cyan-500/10";

  return (
    <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-950/90 to-black/90 p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
      {/* Background Ambient Glows */}
      <div className="pointer-events-none absolute -top-24 -left-20 h-72 w-72 rounded-full bg-emerald-500/20 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-24 -right-20 h-80 w-80 rounded-full bg-cyan-500/20 blur-3xl" />

      <div className="relative z-10 flex flex-col gap-8 lg:flex-row lg:items-center lg:justify-between">
        {/* Left: Brand / Title / Purpose */}
        <div className="max-w-2xl">
          <div className="flex flex-wrap items-center gap-3 mb-4">
            <span
              className={cn(
                "inline-flex items-center gap-2 rounded-full border px-3 py-1 text-xs font-semibold tracking-wide uppercase shadow-inner",
                statusColor
              )}
            >
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-current opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-current"></span>
              </span>
              Repair Agent {agentStatus === "ACTIVE" ? "Online & Monitoring" : agentStatus}
            </span>

            <span className="inline-flex items-center gap-1.5 rounded-full border border-cyan-500/20 bg-cyan-500/10 px-3 py-1 text-xs font-medium text-cyan-300">
              <Cpu className="h-3.5 w-3.5" />
              Phase 31 Autonomous Intelligence
            </span>
          </div>

          <h1 className="text-3xl sm:text-4xl lg:text-5xl font-extrabold tracking-tight text-white">
            Autonomous <span className="bg-gradient-to-r from-emerald-400 via-teal-300 to-cyan-400 bg-clip-text text-transparent">Repair Agent</span>
          </h1>

          <p className="mt-3 text-sm sm:text-base text-slate-300 leading-relaxed">
            RepairVerse AI actively tracks your registered fleet, detects early component failure signatures,
            prioritizes proactive interventions, and executes safe remediations with human-in-the-loop approvals.
          </p>

          <div className="mt-6 flex flex-wrap items-center gap-4">
            <button
              id="evaluate-all-devices-btn"
              onClick={onEvaluateAll}
              disabled={isEvaluatingAll}
              className="group relative inline-flex items-center gap-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-cyan-500 px-5 py-3 text-sm font-semibold text-slate-950 shadow-lg shadow-emerald-500/20 transition-all hover:scale-[1.02] hover:shadow-cyan-500/30 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <RefreshCw className={cn("h-4 w-4", isEvaluatingAll && "animate-spin")} />
              <span>{isEvaluatingAll ? "Evaluating Fleet Telemetry..." : "Scan & Evaluate Fleet"}</span>
              <Sparkles className="h-4 w-4 opacity-70 group-hover:rotate-12 transition-transform" />
            </button>
          </div>
        </div>

        {/* Right: Quick Stats Grid */}
        <div className="grid grid-cols-2 gap-3 sm:gap-4 sm:w-full lg:w-auto lg:min-w-[340px]">
          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Monitored</span>
              <Bot className="h-4 w-4 text-cyan-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-white">{monitoredDevicesCount}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Fleet devices connected</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Active Issues</span>
              <AlertTriangle className="h-4 w-4 text-amber-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-amber-300">{activeInterventionsCount}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Proactive alerts active</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Approvals</span>
              <ShieldCheck className="h-4 w-4 text-emerald-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-emerald-300">{pendingApprovalsCount}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Pending user confirmation</p>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 backdrop-blur-md">
            <div className="flex items-center justify-between text-slate-400 text-xs font-medium">
              <span>Executed</span>
              <CheckCircle2 className="h-4 w-4 text-teal-400" />
            </div>
            <div className="mt-2 text-2xl font-bold text-teal-300">{completedExecutionsCount}</div>
            <p className="text-[11px] text-slate-400 mt-0.5">Autonomous actions run</p>
          </div>
        </div>
      </div>
    </div>
  );
}
