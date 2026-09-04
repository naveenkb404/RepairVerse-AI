"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Sparkles,
  CheckCircle,
  AlertCircle,
  Clock,
  DollarSign,
  Leaf,
  Sliders,
  Play,
  RotateCcw,
} from "lucide-react";
import type { ScenarioResponse, RunSimulationRequest } from "@/lib/types/digitalTwin";
import { cn } from "@/lib/utils";

interface FutureScenarioSimulatorProps {
  scenarios: ScenarioResponse[];
  onRunCustomSimulation: (req: RunSimulationRequest) => void;
  isSimulating?: boolean;
}

export default function FutureScenarioSimulator({
  scenarios,
  onRunCustomSimulation,
  isSimulating = false,
}: FutureScenarioSimulatorProps) {
  const [selectedScenario, setSelectedScenario] = useState<ScenarioResponse | null>(scenarios[0] || null);
  const [budget, setBudget] = useState<number>(5000);
  const [targetLifespan, setTargetLifespan] = useState<number>(24);
  const [prioritizeEco, setPrioritizeEco] = useState<boolean>(true);
  const [prioritizeReliability, setPrioritizeReliability] = useState<boolean>(true);

  const handleSimulate = (e: React.FormEvent) => {
    e.preventDefault();
    onRunCustomSimulation({
      budget,
      targetLifespanMonths: targetLifespan,
      prioritizeSustainability: prioritizeEco,
      prioritizeReliability,
    });
  };

  const getStrategyBadge = (type: string) => {
    switch (type) {
      case "REPAIR_NOW":
        return { label: "Optimal ROI", color: "bg-emerald-500/20 text-emerald-300 border-emerald-500/40" };
      case "PREVENTIVE_MAINTENANCE":
        return { label: "Lowest Cost", color: "bg-cyan-500/20 text-cyan-300 border-cyan-500/40" };
      case "PROFESSIONAL_SERVICE":
        return { label: "Max Reliability", color: "bg-teal-500/20 text-teal-300 border-teal-500/40" };
      case "REFURBISH_DEVICE":
        return { label: "Lifespan Boost", color: "bg-indigo-500/20 text-indigo-300 border-indigo-500/40" };
      case "DELAY_REPAIR":
        return { label: "High Risk", color: "bg-rose-500/20 text-rose-300 border-rose-500/40" };
      case "REPLACE_DEVICE":
        return { label: "High Capex", color: "bg-amber-500/20 text-amber-300 border-amber-500/40" };
      case "RECYCLE_DEVICE":
        return { label: "Circular Recovery", color: "bg-lime-500/20 text-lime-300 border-lime-500/40" };
      default:
        return { label: "Status Quo", color: "bg-slate-500/20 text-slate-300 border-slate-500/40" };
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-white flex items-center gap-2">
            <Sliders className="h-5 w-5 text-cyan-400" />
            Future Scenario Simulation Engine
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            Compare 8 simulated future strategies before making real-world repair commitments.
          </p>
        </div>
      </div>

      {/* Interactive Controls Bar */}
      <form
        onSubmit={handleSimulate}
        className="rounded-2xl border border-white/10 bg-slate-900/80 p-4 sm:p-5 backdrop-blur-md grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 items-end"
      >
        <div>
          <label className="text-xs font-semibold text-slate-300 block mb-1">
            Budget Ceiling (₹): {budget.toLocaleString()}
          </label>
          <input
            type="range"
            min={500}
            max={50000}
            step={500}
            value={budget}
            onChange={(e) => setBudget(Number(e.target.value))}
            className="w-full accent-cyan-400 bg-slate-800 rounded-lg cursor-pointer"
          />
        </div>

        <div>
          <label className="text-xs font-semibold text-slate-300 block mb-1">
            Target Lifespan: {targetLifespan} Months
          </label>
          <input
            type="range"
            min={6}
            max={48}
            step={6}
            value={targetLifespan}
            onChange={(e) => setTargetLifespan(Number(e.target.value))}
            className="w-full accent-emerald-400 bg-slate-800 rounded-lg cursor-pointer"
          />
        </div>

        <div className="flex items-center gap-2 pt-2">
          <input
            type="checkbox"
            id="eco-priority"
            checked={prioritizeEco}
            onChange={(e) => setPrioritizeEco(e.target.checked)}
            className="rounded border-slate-700 bg-slate-800 text-emerald-500 focus:ring-emerald-500 h-4 w-4"
          />
          <label htmlFor="eco-priority" className="text-xs text-slate-300 cursor-pointer">
            Prioritize Sustainability
          </label>
        </div>

        <div className="flex items-center gap-2 pt-2">
          <input
            type="checkbox"
            id="rel-priority"
            checked={prioritizeReliability}
            onChange={(e) => setPrioritizeReliability(e.target.checked)}
            className="rounded border-slate-700 bg-slate-800 text-cyan-500 focus:ring-cyan-500 h-4 w-4"
          />
          <label htmlFor="rel-priority" className="text-xs text-slate-300 cursor-pointer">
            Prioritize Reliability
          </label>
        </div>

        <div>
          <button
            type="submit"
            disabled={isSimulating}
            className="w-full inline-flex items-center justify-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-emerald-500 px-4 py-2.5 text-xs font-bold text-slate-950 shadow-md transition-all hover:opacity-90 disabled:opacity-50"
          >
            <Play className="h-3.5 w-3.5 fill-current" />
            <span>{isSimulating ? "Simulating..." : "Run Scenario Simulation"}</span>
          </button>
        </div>
      </form>

      {/* Scenario Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {scenarios.map((s, idx) => {
          const badge = getStrategyBadge(s.scenarioType);
          const isSelected = selectedScenario?.id === s.id;

          return (
            <motion.div
              key={s.id || s.scenarioType}
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.04 }}
              onClick={() => setSelectedScenario(s)}
              className={cn(
                "rounded-2xl border p-5 transition-all cursor-pointer relative overflow-hidden backdrop-blur-md flex flex-col justify-between",
                isSelected
                  ? "border-cyan-400 bg-cyan-950/20 shadow-xl shadow-cyan-500/10 scale-[1.02]"
                  : "border-white/10 bg-slate-900/60 hover:border-white/20 hover:bg-slate-900/80"
              )}
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <span className={cn("text-[10px] font-bold uppercase tracking-wider px-2.5 py-0.5 rounded-full border", badge.color)}>
                    {badge.label}
                  </span>
                  <span className="text-xs font-mono font-bold text-emerald-400">
                    {s.overallOutcomeScore}/100
                  </span>
                </div>

                <h4 className="text-sm font-bold text-white mb-2 leading-tight">
                  {s.scenarioName}
                </h4>

                <div className="space-y-1.5 text-xs text-slate-300 mt-3">
                  <div className="flex justify-between">
                    <span className="text-slate-400">Projected Cost:</span>
                    <span className="font-mono font-semibold text-white">
                      {s.projectedCost === 0 ? "Free" : `₹${s.projectedCost.toLocaleString()}`}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Net Savings:</span>
                    <span className={cn("font-mono font-semibold", s.projectedSavings >= 0 ? "text-emerald-400" : "text-rose-400")}>
                      ₹{s.projectedSavings.toLocaleString()}
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Lifespan Ext.:</span>
                    <span className="font-mono font-semibold text-cyan-300">
                      +{s.projectedLifespanMonths} Mos
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">Health / Risk:</span>
                    <span className="font-mono font-semibold text-slate-200">
                      {s.projectedHealthScore}% / {s.projectedFailureRisk}%
                    </span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-400">CO₂ Avoided:</span>
                    <span className="font-mono font-semibold text-teal-300">
                      {s.projectedCo2Impact > 0 ? `+${s.projectedCo2Impact} kg` : `${s.projectedCo2Impact} kg`}
                    </span>
                  </div>
                </div>
              </div>

              <div className="mt-4 pt-3 border-t border-white/5 flex items-center justify-between text-[11px] text-slate-400">
                <span>Downtime: {s.downtimeDays} {s.downtimeDays === 1 ? "day" : "days"}</span>
                <span className="font-mono">{(s.simulationConfidence * 100).toFixed(0)}% conf.</span>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
