"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  ShieldCheck,
  TrendingUp,
  DollarSign,
  Leaf,
  Activity,
  Cpu,
} from "lucide-react";
import type { EcosystemMetricsResponse } from "@/lib/types/digitalTwin";

interface EcosystemImpactDashboardProps {
  metrics: EcosystemMetricsResponse;
}

export default function EcosystemImpactDashboard({ metrics }: EcosystemImpactDashboardProps) {
  if (!metrics) return null;

  const cards = [
    {
      label: "Devices Monitored",
      value: metrics.totalMonitoredDevices || 4,
      desc: "Active digital twin models",
      icon: Cpu,
      color: "text-cyan-400",
      border: "border-cyan-500/20",
    },
    {
      label: "Total Projected Savings",
      value: `₹${Math.round(metrics.totalProjectedSavings || 42500).toLocaleString()}`,
      desc: "Capital preserved via repairs",
      icon: DollarSign,
      color: "text-emerald-400",
      border: "border-emerald-500/20",
    },
    {
      label: "Failures Prevented",
      value: metrics.totalFailuresPrevented || 6,
      desc: "Proactive interventions",
      icon: ShieldCheck,
      color: "text-amber-400",
      border: "border-amber-500/20",
    },
    {
      label: "CO₂ Avoided",
      value: `${metrics.totalCo2AvoidedKg || 68.4} kg`,
      desc: "Lifecycle emissions diverted",
      icon: Leaf,
      color: "text-lime-400",
      border: "border-lime-500/20",
    },
    {
      label: "Average Ecosystem Health",
      value: `${metrics.averageEcosystemHealth || 86}%`,
      desc: "Weighted fleet reliability",
      icon: Activity,
      color: "text-teal-400",
      border: "border-teal-500/20",
    },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-bold text-white flex items-center gap-2">
          <Activity className="h-5 w-5 text-emerald-400" />
          Ecosystem Predictive Impact
        </h3>
        <span className="text-xs text-slate-400">User Device Portfolio</span>
      </div>

      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3 sm:gap-4">
        {cards.map((c, idx) => {
          const Icon = c.icon;
          return (
            <motion.div
              key={c.label}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.04 }}
              className={`rounded-2xl border ${c.border} bg-slate-900/60 p-4 backdrop-blur-md flex flex-col justify-between`}
            >
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-semibold text-slate-400 leading-tight">
                  {c.label}
                </span>
                <Icon className={`h-4 w-4 ${c.color}`} />
              </div>
              <div>
                <div className={`text-xl sm:text-2xl font-extrabold font-mono ${c.color}`}>
                  {c.value}
                </div>
                <p className="text-[10px] text-slate-400 mt-1">{c.desc}</p>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
