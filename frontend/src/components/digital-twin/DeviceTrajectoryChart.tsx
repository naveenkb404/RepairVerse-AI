"use client";

import React, { useState } from "react";
import { motion } from "framer-motion";
import { TrendingUp, Activity, AlertTriangle, DollarSign, Calendar } from "lucide-react";
import type { ForecastResponse, DeviceTrajectoryPoint } from "@/lib/types/digitalTwin";
import { cn } from "@/lib/utils";

interface DeviceTrajectoryChartProps {
  forecasts: ForecastResponse[];
  trajectoryPoints?: DeviceTrajectoryPoint[];
  deviceName: string;
}

export default function DeviceTrajectoryChart({
  forecasts,
  trajectoryPoints = [],
  deviceName,
}: DeviceTrajectoryChartProps) {
  const [activeMetric, setActiveMetric] = useState<"health" | "risk" | "cost" | "value">("health");
  const [selectedHorizon, setSelectedHorizon] = useState<number | null>(null);

  // Combine baseline (0M) with forecasts if trajectory points are available
  const points = trajectoryPoints.length > 0 ? trajectoryPoints : [
    { monthOffset: 0, healthScore: 84, failureRisk: 26, repairCost: 3200, deviceValue: 74500 },
    ...forecasts.map(f => ({
      monthOffset: f.forecastHorizonMonths,
      healthScore: f.predictedHealthScore,
      failureRisk: f.predictedFailureRisk,
      repairCost: f.predictedRepairCost,
      deviceValue: f.predictedDeviceValue,
    }))
  ];

  const getMetricData = (p: DeviceTrajectoryPoint) => {
    switch (activeMetric) {
      case "health":
        return { value: p.healthScore, label: `${p.healthScore}%`, color: "text-emerald-400", bg: "bg-emerald-500", max: 100 };
      case "risk":
        return { value: p.failureRisk, label: `${p.failureRisk}%`, color: "text-rose-400", bg: "bg-rose-500", max: 100 };
      case "cost":
        return { value: p.repairCost, label: `₹${p.repairCost.toLocaleString()}`, color: "text-amber-400", bg: "bg-amber-500", max: 10000 };
      case "value":
        return { value: p.deviceValue, label: `₹${p.deviceValue.toLocaleString()}`, color: "text-cyan-400", bg: "bg-cyan-500", max: 85000 };
    }
  };

  const metricTabs = [
    { key: "health", label: "Health Score Trajectory", icon: Activity, color: "text-emerald-400" },
    { key: "risk", label: "Failure Risk Escalation", icon: AlertTriangle, color: "text-rose-400" },
    { key: "cost", label: "Repair Cost Growth", icon: DollarSign, color: "text-amber-400" },
    { key: "value", label: "Residual Asset Value", icon: TrendingUp, color: "text-cyan-400" },
  ] as const;

  return (
    <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-md space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h3 className="text-lg font-bold text-white flex items-center gap-2">
            <TrendingUp className="h-5 w-5 text-cyan-400" />
            24-Month Predictive Lifecycle Trajectory
          </h3>
          <p className="text-xs text-slate-400 mt-1">
            Simulated trajectory model for {deviceName} without proactive intervention.
          </p>
        </div>

        {/* Metric Switcher */}
        <div className="flex flex-wrap gap-1.5 p-1 rounded-xl bg-slate-950/80 border border-white/10">
          {metricTabs.map((t) => (
            <button
              key={t.key}
              onClick={() => setActiveMetric(t.key)}
              className={cn(
                "px-3 py-1.5 rounded-lg text-xs font-semibold transition-all flex items-center gap-1.5",
                activeMetric === t.key
                  ? "bg-white/15 text-white shadow-sm"
                  : "text-slate-400 hover:text-slate-200"
              )}
            >
              <t.icon className={cn("h-3.5 w-3.5", t.color)} />
              <span className="hidden sm:inline">{t.label.split(" ")[0]}</span>
            </button>
          ))}
        </div>
      </div>

      {/* Chart Visualization */}
      <div className="grid grid-cols-2 sm:grid-cols-6 gap-3 pt-4">
        {points.map((p, idx) => {
          const data = getMetricData(p);
          const heightPercent = Math.min(100, Math.max(15, (data.value / data.max) * 100));
          const isCurrent = p.monthOffset === 0;

          return (
            <motion.div
              key={p.monthOffset}
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.04 }}
              onClick={() => setSelectedHorizon(p.monthOffset)}
              className={cn(
                "flex flex-col items-center justify-end rounded-2xl border p-4 transition-all cursor-pointer h-56",
                selectedHorizon === p.monthOffset
                  ? "border-cyan-500/50 bg-cyan-950/20 shadow-lg shadow-cyan-500/10"
                  : isCurrent
                  ? "border-emerald-500/30 bg-emerald-950/10"
                  : "border-white/10 bg-white/[0.02] hover:bg-white/[0.05]"
              )}
            >
              {/* Value Label */}
              <div className={cn("text-xs font-bold font-mono mb-2", data.color)}>
                {data.label}
              </div>

              {/* Bar */}
              <div className="w-12 bg-slate-800/80 rounded-t-xl overflow-hidden flex flex-col justify-end h-32 p-1">
                <motion.div
                  initial={{ height: 0 }}
                  animate={{ height: `${heightPercent}%` }}
                  transition={{ duration: 0.6, delay: idx * 0.05 }}
                  className={cn("w-full rounded-t-lg", data.bg, "opacity-90")}
                />
              </div>

              {/* Month Horizon Label */}
              <div className="mt-3 text-center">
                <span className="text-xs font-semibold text-slate-200">
                  {isCurrent ? "Current" : `+${p.monthOffset}M`}
                </span>
                <span className="block text-[10px] text-slate-400">
                  {isCurrent ? "Baseline" : `${p.monthOffset} Months`}
                </span>
              </div>
            </motion.div>
          );
        })}
      </div>

      {/* Horizon summary notes */}
      <div className="rounded-xl border border-white/5 bg-slate-950/40 p-4 flex items-center justify-between text-xs text-slate-300">
        <span className="flex items-center gap-2">
          <Calendar className="h-4 w-4 text-cyan-400" />
          Projections updated dynamically via multi-sensor Bayesian extrapolation.
        </span>
        <span className="text-slate-400 font-mono">Horizon: 0 to 24 Months</span>
      </div>
    </div>
  );
}
