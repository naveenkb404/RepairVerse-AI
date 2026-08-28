"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertOctagon,
  AlertTriangle,
  ArrowUpRight,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Clock,
  DollarSign,
  Flame,
  Leaf,
  Layers,
  Percent,
  RefreshCw,
  Scale,
  ShieldCheck,
  Sparkles,
  TrendingDown,
  TrendingUp,
  Zap,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import type {
  DeviceLifecycleAssessmentData,
  DelayImpactData,
  LifecycleScenarioData,
} from "@/lib/types/lifecycle";

const EASE = [0.22, 1, 0.36, 1] as const;

interface LifecycleSimulatorProps {
  lifecycle: DeviceLifecycleAssessmentData;
  delayImpact?: DelayImpactData | null;
  isLoading?: boolean;
}

export default function LifecycleSimulator({
  lifecycle,
  delayImpact,
  isLoading = false,
}: LifecycleSimulatorProps) {
  const [activeTab, setActiveTab] = useState<"scenarios" | "delay">("scenarios");

  const getTagBadge = (tag: LifecycleScenarioData["recommendationTag"]) => {
    switch (tag) {
      case "HIGHLY_RECOMMENDED":
        return "border-emerald-500/30 bg-emerald-500/15 text-emerald-400 font-black";
      case "RECOMMENDED":
        return "border-cyan-500/30 bg-cyan-500/15 text-cyan-400 font-bold";
      case "VIABLE":
        return "border-blue-500/30 bg-blue-500/15 text-blue-400 font-semibold";
      case "HIGH_RISK":
        return "border-orange-500/30 bg-orange-500/15 text-orange-400 font-bold";
      case "NOT_RECOMMENDED":
        return "border-red-500/30 bg-red-500/15 text-red-400 font-semibold";
      case "DISCOURAGED":
        return "border-purple-500/30 bg-purple-500/15 text-purple-400 font-semibold";
      default:
        return "border-white/20 bg-white/5 text-white/70";
    }
  };

  const getRiskColor = (risk: string) => {
    switch (risk.toUpperCase()) {
      case "CRITICAL":
        return "text-red-400";
      case "HIGH":
        return "text-orange-400";
      case "MEDIUM":
        return "text-yellow-400";
      case "LOW":
        return "text-emerald-400";
      default:
        return "text-cyan-400";
    }
  };

  return (
    <GlassCard className="p-6 border-white/10 relative overflow-hidden">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-5">
        <div>
          <div className="flex items-center gap-2 mb-1.5 flex-wrap">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-3 py-0.5 text-xs font-bold uppercase tracking-wider text-cyan-400">
              <Scale className="size-3.5" /> Lifecycle Intelligence & Delay Simulator
            </span>
            <span className="text-[11px] font-semibold text-white/50 bg-white/[0.04] px-2.5 py-0.5 rounded-full border border-white/10">
              Repairability: {lifecycle.repairabilityScore}/100
            </span>
            {lifecycle.isDemo && (
              <span className="text-[10px] font-bold text-cyan-400 bg-cyan-500/10 border border-cyan-500/20 px-2 py-0.5 rounded-full">
                DEMO PROJECTION
              </span>
            )}
          </div>
          <h3 className="text-lg font-black text-white sm:text-xl">
            Device Lifespan Forecast & Economic Scenarios
          </h3>
          <p className="text-xs text-white/60 mt-1 max-w-xl">
            Current estimated lifespan is{" "}
            <strong className="text-white font-bold">
              {lifecycle.predictedRemainingLifespanMonths} months
            </strong>
            . Timely repair can extend hardware utility up to{" "}
            <strong className="text-cyan-400 font-bold">
              +{lifecycle.lifecycleExtensionPotentialMonths} additional months
            </strong>
            .
          </p>
        </div>

        {/* Tab Toggle Buttons */}
        <div className="flex items-center rounded-xl bg-white/[0.04] border border-white/10 p-1 self-start sm:self-auto">
          <button
            onClick={() => setActiveTab("scenarios")}
            className={`rounded-lg px-3 py-1.5 text-xs font-bold transition-all ${
              activeTab === "scenarios"
                ? "bg-cyan-500 text-black shadow-lg"
                : "text-white/60 hover:text-white"
            }`}
          >
            Scenario Matrix
          </button>
          <button
            onClick={() => setActiveTab("delay")}
            className={`rounded-lg px-3 py-1.5 text-xs font-bold transition-all ${
              activeTab === "delay"
                ? "bg-amber-500 text-black shadow-lg"
                : "text-white/60 hover:text-white"
            }`}
          >
            Delay Impact (7/30/90d)
          </button>
        </div>
      </div>

      {/* Lifespan Progression Header Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 my-5">
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5">Device Age</span>
          <span className="text-base font-black text-white">
            {lifecycle.deviceAgeMonths} months
          </span>
          <span className="text-[10px] text-white/40 block mt-0.5">Operating history</span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
          <span className="text-[11px] text-white/50 block mb-0.5">Unserviced Lifespan</span>
          <span className="text-base font-black text-amber-400">
            {lifecycle.predictedRemainingLifespanMonths} months
          </span>
          <span className="text-[10px] text-white/40 block mt-0.5">Under current wear</span>
        </div>

        <div className="rounded-2xl border border-emerald-500/20 bg-emerald-500/[0.03] p-3">
          <span className="text-[11px] text-emerald-300 block mb-0.5">After Maintenance</span>
          <span className="text-base font-black text-emerald-400">
            {lifecycle.expectedLifespanAfterMaintenanceMonths} months
          </span>
          <span className="text-[10px] text-emerald-400/60 block mt-0.5">
            +{lifecycle.expectedLifespanAfterMaintenanceMonths - lifecycle.predictedRemainingLifespanMonths} mos gained
          </span>
        </div>

        <div className="rounded-2xl border border-cyan-500/20 bg-cyan-500/[0.03] p-3">
          <span className="text-[11px] text-cyan-300 block mb-0.5">After Repair</span>
          <span className="text-base font-black text-cyan-400">
            {lifecycle.expectedLifespanAfterRepairMonths} months
          </span>
          <span className="text-[10px] text-cyan-400/60 block mt-0.5">
            +{lifecycle.lifecycleExtensionPotentialMonths} mos gained
          </span>
        </div>
      </div>

      {/* Tab 1: Scenario Comparison Matrix */}
      {activeTab === "scenarios" && (
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="border-b border-white/10 text-white/40 uppercase tracking-wider text-[10px]">
                <th className="py-2.5 px-3">Decision Path</th>
                <th className="py-2.5 px-3">Estimated Cost</th>
                <th className="py-2.5 px-3">Total Lifespan</th>
                <th className="py-2.5 px-3">Risk Level</th>
                <th className="py-2.5 px-3">CO₂ Impact</th>
                <th className="py-2.5 px-3 text-right">Strategic Rating</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-white/5">
              {lifecycle.scenarios.map((sc) => (
                <tr
                  key={sc.scenarioKey}
                  className="hover:bg-white/[0.02] transition-colors"
                >
                  <td className="py-3 px-3">
                    <div className="font-bold text-white text-xs">{sc.title}</div>
                    <div className="text-[11px] text-white/50 max-w-xs">{sc.description}</div>
                  </td>

                  <td className="py-3 px-3 font-semibold text-white">
                    {sc.estimatedCost > 0 ? `$${sc.estimatedCost.toFixed(2)}` : "Free ($0)"}
                  </td>

                  <td className="py-3 px-3 font-bold text-cyan-400">
                    {sc.estimatedLifespanMonths} mos
                  </td>

                  <td className="py-3 px-3">
                    <span className={`font-bold ${getRiskColor(sc.riskLevel)}`}>
                      {sc.riskLevel}
                    </span>
                  </td>

                  <td className="py-3 px-3 font-semibold">
                    <span
                      className={
                        sc.carbonImpactKg >= 0
                          ? "text-emerald-400 flex items-center gap-1"
                          : "text-rose-400 flex items-center gap-1"
                      }
                    >
                      <Leaf className="size-3 shrink-0" />
                      {sc.carbonImpactKg >= 0
                        ? `+${sc.carbonImpactKg.toFixed(1)} kg`
                        : `${sc.carbonImpactKg.toFixed(1)} kg`}
                    </span>
                  </td>

                  <td className="py-3 px-3 text-right">
                    <span
                      className={`inline-block rounded-full border px-2.5 py-0.5 text-[10px] ${getTagBadge(
                        sc.recommendationTag
                      )}`}
                    >
                      {sc.recommendationTag.replace("_", " ")}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Tab 2: Delay Impact Escalation Curve */}
      {activeTab === "delay" && delayImpact && (
        <div className="space-y-4 pt-1">
          <div className="rounded-2xl border border-amber-500/20 bg-amber-500/[0.05] p-4 flex items-start gap-3">
            <AlertTriangle className="size-5 shrink-0 text-amber-400 mt-0.5" />
            <div>
              <h4 className="text-xs font-bold text-amber-300">
                Action Urgency Directive
              </h4>
              <p className="text-xs text-amber-200/80 mt-0.5 leading-relaxed">
                {delayImpact.urgencyRecommendation}
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
            {delayImpact.projections.map((proj) => (
              <div
                key={proj.delayDays}
                className="rounded-2xl border border-white/10 bg-white/[0.02] p-4 flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-black text-white">
                      {proj.timeHorizonLabel}
                    </span>
                    <span className={`text-[10px] font-bold ${getRiskColor(proj.projectedRiskLevel)}`}>
                      {proj.projectedRiskLevel} RISK
                    </span>
                  </div>

                  <div className="flex items-baseline gap-2 mb-2">
                    <span className="text-xl font-black text-white">
                      ${proj.projectedCost.toFixed(2)}
                    </span>
                    <span className="text-[11px] font-bold text-amber-400 flex items-center gap-0.5">
                      <TrendingUp className="size-3" />
                      +{proj.costEscalationPercentage}% cost
                    </span>
                  </div>

                  <p className="text-[11px] text-white/60 leading-relaxed mb-3">
                    {proj.consequenceSummary}
                  </p>
                </div>

                <div className="border-t border-white/10 pt-2.5 space-y-1 text-[11px] text-white/50">
                  <div className="flex justify-between">
                    <span>Secondary Fault Risk:</span>
                    <strong className="text-white font-bold">{proj.secondaryDamageProbability}%</strong>
                  </div>
                  <div className="flex justify-between">
                    <span>Lifespan Lost:</span>
                    <strong className="text-rose-400 font-bold">-{proj.lifecycleReductionMonths} mos</strong>
                  </div>
                  <div className="flex justify-between">
                    <span>Carbon Penalty:</span>
                    <strong className="text-amber-400 font-bold">+{proj.additionalCarbonPenaltyKg.toFixed(1)} kg CO₂</strong>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </GlassCard>
  );
}
