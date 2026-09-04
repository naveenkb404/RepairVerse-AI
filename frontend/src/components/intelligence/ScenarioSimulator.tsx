"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Sliders,
  DollarSign,
  Calendar,
  Leaf,
  TrendingUp,
  Check,
  X,
  Sparkles,
  ArrowRight,
  Filter,
} from "lucide-react";
import type { DeviceScenario, DeviceScenarioSimulationRequest } from "@/lib/types/deviceIntelligence";

interface ScenarioSimulatorProps {
  scenarios: DeviceScenario[];
  recommendedScenarioType?: string;
  onSimulate?: (params: DeviceScenarioSimulationRequest) => void;
  isSimulating?: boolean;
}

export const ScenarioSimulator: React.FC<ScenarioSimulatorProps> = ({
  scenarios,
  recommendedScenarioType = "REPAIR",
  onSimulate,
  isSimulating = false,
}) => {
  const [selectedScenario, setSelectedScenario] = useState<string>(
    scenarios[0]?.scenarioType || "REPAIR"
  );
  const [customBudget, setCustomBudget] = useState<number>(200);
  const [targetLifespan, setTargetLifespan] = useState<number>(24);
  const [prioritizeSustainability, setPrioritizeSustainability] = useState<boolean>(true);
  const [showSimulatorControls, setShowSimulatorControls] = useState<boolean>(false);

  const activeScenario =
    scenarios.find((s) => s.scenarioType === selectedScenario) || scenarios[0];

  const handleSimulateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (onSimulate) {
      onSimulate({
        customBudget,
        targetLifespanMonths: targetLifespan,
        prioritizeSustainability,
      });
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl space-y-6 shadow-xl"
    >
      {/* Header with Simulator Toggle */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <Sliders className="w-5 h-5 text-emerald-400" />
            <h3 className="text-lg font-bold text-white tracking-wide">
              What-If Decision Simulator
            </h3>
          </div>
          <p className="text-xs text-slate-400">
            Compare estimated financial, longevity, and environmental outcomes across alternative paths.
          </p>
        </div>

        <button
          onClick={() => setShowSimulatorControls(!showSimulatorControls)}
          className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl text-xs font-semibold bg-white/5 hover:bg-white/10 border border-white/10 text-slate-300 transition-colors self-start sm:self-auto"
        >
          <Filter className="w-3.5 h-3.5 text-cyan-400" />
          {showSimulatorControls ? "Hide Controls" : "Custom Constraints"}
        </button>
      </div>

      {/* Simulator Constraint Controls (Collapsible) */}
      <AnimatePresence>
        {showSimulatorControls && (
          <motion.form
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            onSubmit={handleSimulateSubmit}
            className="rounded-2xl border border-white/10 bg-black/40 p-4 md:p-5 space-y-4 overflow-hidden"
          >
            <div className="text-xs font-bold uppercase tracking-wider text-emerald-400">
              Interactive Constraint Tuning
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Custom Budget */}
              <div className="space-y-2">
                <div className="flex justify-between text-xs text-slate-300 font-medium">
                  <span>Max Budget</span>
                  <span className="font-bold text-emerald-400">${customBudget}</span>
                </div>
                <input
                  type="range"
                  min="0"
                  max="1000"
                  step="25"
                  value={customBudget}
                  onChange={(e) => setCustomBudget(Number(e.target.value))}
                  className="w-full h-1.5 bg-white/10 rounded-lg appearance-none cursor-pointer accent-emerald-500"
                />
              </div>

              {/* Target Lifespan */}
              <div className="space-y-2">
                <div className="flex justify-between text-xs text-slate-300 font-medium">
                  <span>Target Lifespan Extension</span>
                  <span className="font-bold text-cyan-400">{targetLifespan} Mo</span>
                </div>
                <input
                  type="range"
                  min="6"
                  max="60"
                  step="6"
                  value={targetLifespan}
                  onChange={(e) => setTargetLifespan(Number(e.target.value))}
                  className="w-full h-1.5 bg-white/10 rounded-lg appearance-none cursor-pointer accent-cyan-500"
                />
              </div>

              {/* Eco Priority Switch */}
              <div className="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/5">
                <span className="text-xs font-medium text-slate-300">Prioritize Zero-Waste</span>
                <input
                  type="checkbox"
                  checked={prioritizeSustainability}
                  onChange={(e) => setPrioritizeSustainability(e.target.checked)}
                  className="h-4 w-4 rounded bg-slate-900 border-white/20 text-emerald-500 focus:ring-emerald-500 focus:ring-offset-0 cursor-pointer"
                />
              </div>
            </div>

            <div className="flex justify-end pt-1">
              <button
                type="submit"
                disabled={isSimulating}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 transition-all shadow-md"
              >
                {isSimulating ? "Recalculating..." : "Apply Simulation Constraints"}
              </button>
            </div>
          </motion.form>
        )}
      </AnimatePresence>

      {/* Scenario Pills Selector */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-thin">
        {scenarios.map((s) => {
          const isSelected = s.scenarioType === selectedScenario;
          const isRec = s.scenarioType === recommendedScenarioType || s.scenarioType.includes("REPAIR");

          return (
            <button
              key={s.scenarioType}
              onClick={() => setSelectedScenario(s.scenarioType)}
              className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all flex items-center gap-1.5 border ${
                isSelected
                  ? "bg-gradient-to-r from-emerald-500 to-cyan-500 text-slate-950 border-emerald-400 shadow-md font-extrabold"
                  : "bg-white/5 hover:bg-white/10 text-slate-300 border-white/10"
              }`}
            >
              {isRec && <Sparkles className={`w-3 h-3 ${isSelected ? "text-slate-950" : "text-emerald-400"}`} />}
              <span>{s.scenarioType.replace(/_/g, " ")}</span>
            </button>
          );
        })}
      </div>

      {/* Active Scenario Detailed Comparison Card */}
      {activeScenario && (
        <div className="rounded-2xl border border-white/10 bg-slate-950/60 p-5 space-y-5">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-white/5 pb-4">
            <div>
              <div className="flex items-center gap-2">
                <h4 className="text-base font-extrabold text-white">{activeScenario.title}</h4>
                {activeScenario.scenarioType === recommendedScenarioType && (
                  <span className="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 uppercase">
                    Recommended Path
                  </span>
                )}
              </div>
              <p className="text-xs text-slate-400 mt-1">{activeScenario.recommendation}</p>
            </div>

            <div className="text-right shrink-0">
              <span className="text-[11px] font-semibold text-slate-400 block">Scenario Viability Score</span>
              <span className="text-xl font-black text-emerald-400">{activeScenario.intelligenceScore}/100</span>
            </div>
          </div>

          {/* 4 Quantitative Impact Indicators */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
            <div className="rounded-xl bg-white/5 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-medium text-slate-400">
                <DollarSign className="w-3.5 h-3.5 text-emerald-400" />
                <span>Estimated Cost</span>
              </div>
              <div className="text-base font-bold text-white">
                {activeScenario.estimatedCost > 0 ? `$${activeScenario.estimatedCost.toFixed(0)}` : "$0.00"}
              </div>
            </div>

            <div className="rounded-xl bg-white/5 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-medium text-slate-400">
                <Calendar className="w-3.5 h-3.5 text-cyan-400" />
                <span>Lifespan Gain</span>
              </div>
              <div className="text-base font-bold text-cyan-300">
                +{activeScenario.estimatedLifespanMonths} Months
              </div>
            </div>

            <div className="rounded-xl bg-white/5 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-medium text-slate-400">
                <Leaf className="w-3.5 h-3.5 text-emerald-400" />
                <span>CO₂ Avoidance</span>
              </div>
              <div className="text-base font-bold text-emerald-400">
                {activeScenario.estimatedCo2Impact >= 0 ? `+${activeScenario.estimatedCo2Impact.toFixed(1)} kg` : `${activeScenario.estimatedCo2Impact.toFixed(1)} kg`}
              </div>
            </div>

            <div className="rounded-xl bg-white/5 p-3 space-y-1">
              <div className="flex items-center gap-1.5 text-[11px] font-medium text-slate-400">
                <TrendingUp className="w-3.5 h-3.5 text-teal-400" />
                <span>Financial Savings</span>
              </div>
              <div className="text-base font-bold text-teal-300">
                ${Math.round(activeScenario.estimatedSavings)}
              </div>
            </div>
          </div>

          {/* Pros & Cons Columns */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
            <div className="space-y-2">
              <span className="text-xs font-bold text-emerald-400 flex items-center gap-1.5">
                <Check className="w-3.5 h-3.5" /> Advantages
              </span>
              <ul className="space-y-1.5">
                {activeScenario.pros.map((pro, idx) => (
                  <li key={idx} className="text-xs text-slate-300 flex items-start gap-2">
                    <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 mt-1.5 shrink-0" />
                    <span>{pro}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="space-y-2">
              <span className="text-xs font-bold text-rose-400 flex items-center gap-1.5">
                <X className="w-3.5 h-3.5" /> Disadvantages / Trade-offs
              </span>
              <ul className="space-y-1.5">
                {activeScenario.cons.map((con, idx) => (
                  <li key={idx} className="text-xs text-slate-400 flex items-start gap-2">
                    <span className="h-1.5 w-1.5 rounded-full bg-rose-400 mt-1.5 shrink-0" />
                    <span>{con}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      )}
    </motion.div>
  );
};
