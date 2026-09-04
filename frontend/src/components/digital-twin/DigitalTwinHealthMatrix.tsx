"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Heart,
  AlertTriangle,
  Wrench,
  DollarSign,
  Clock,
  Leaf,
  ShieldCheck,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { DigitalTwinSnapshotResponse } from "@/lib/types/digitalTwin";

interface DigitalTwinHealthMatrixProps {
  snapshot: DigitalTwinSnapshotResponse;
}

export default function DigitalTwinHealthMatrix({ snapshot }: DigitalTwinHealthMatrixProps) {
  if (!snapshot) return null;

  const metrics = [
    {
      key: "health",
      label: "Device Health",
      score: snapshot.healthScore ?? 80,
      icon: Heart,
      color: "text-emerald-400",
      bgBar: "bg-emerald-500",
      desc: "Comprehensive multi-sensor operational score",
    },
    {
      key: "risk",
      label: "Failure Risk",
      score: snapshot.failureRiskScore ?? 20,
      icon: AlertTriangle,
      color: (snapshot.failureRiskScore ?? 20) > 50 ? "text-rose-400" : "text-amber-400",
      bgBar: (snapshot.failureRiskScore ?? 20) > 50 ? "bg-rose-500" : "bg-amber-500",
      desc: "Predicted probability of imminent sub-system fault",
    },
    {
      key: "maintenance",
      label: "Maintenance Status",
      score: snapshot.maintenanceScore ?? 85,
      icon: Wrench,
      color: "text-cyan-400",
      bgBar: "bg-cyan-500",
      desc: "Component wear & service diligence adherence",
    },
    {
      key: "economics",
      label: "Repair Economics",
      score: snapshot.repairEconomicsScore ?? 85,
      icon: DollarSign,
      color: "text-teal-400",
      bgBar: "bg-teal-500",
      desc: "Cost-to-benefit ratio of proactive intervention",
    },
    {
      key: "longevity",
      label: "Longevity Index",
      score: snapshot.longevityScore ?? 80,
      icon: Clock,
      color: "text-indigo-400",
      bgBar: "bg-indigo-500",
      desc: "Projected remaining operating duty cycle",
    },
    {
      key: "sustainability",
      label: "Circular Sustainability",
      score: snapshot.sustainabilityScore ?? 90,
      icon: Leaf,
      color: "text-lime-400",
      bgBar: "bg-lime-500",
      desc: "Emissions prevented & material recovery rating",
    },
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-bold text-white flex items-center gap-2">
          <ShieldCheck className="h-5 w-5 text-cyan-400" />
          Digital Twin Health Matrix
        </h2>
        <span className="text-xs text-slate-400 font-mono">
          Updated: {new Date(snapshot.snapshotTime || Date.now()).toLocaleTimeString()}
        </span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {metrics.map((m, idx) => {
          const Icon = m.icon;
          return (
            <motion.div
              key={m.key}
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.05 }}
              className="rounded-2xl border border-white/10 bg-slate-900/60 p-5 backdrop-blur-md relative overflow-hidden"
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2.5">
                  <div className="p-2 rounded-xl bg-white/[0.05] border border-white/10">
                    <Icon className={`h-4 w-4 ${m.color}`} />
                  </div>
                  <span className="text-sm font-semibold text-slate-200">{m.label}</span>
                </div>
                <span className={`text-xl font-bold font-mono ${m.color}`}>
                  {m.score}%
                </span>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-slate-800 rounded-full h-2 overflow-hidden my-2">
                <div
                  className={`h-full rounded-full ${m.bgBar} transition-all duration-700`}
                  style={{ width: `${Math.min(100, Math.max(0, m.score))}%` }}
                />
              </div>

              <p className="text-xs text-slate-400 mt-2">{m.desc}</p>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
