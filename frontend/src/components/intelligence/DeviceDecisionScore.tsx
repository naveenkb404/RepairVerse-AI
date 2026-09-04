"use client";

import React from "react";
import { motion } from "framer-motion";
import { Shield, Sparkles, Activity, Award } from "lucide-react";
import type { IntelligenceTier, IntelligenceScoreBreakdown } from "@/lib/types/deviceIntelligence";

interface DeviceDecisionScoreProps {
  score: number;
  tier: IntelligenceTier;
  confidence: number;
  breakdown: IntelligenceScoreBreakdown;
}

export const DeviceDecisionScore: React.FC<DeviceDecisionScoreProps> = ({
  score,
  tier,
  confidence,
  breakdown,
}) => {
  const getTierDetails = (t: IntelligenceTier) => {
    switch (t) {
      case "EXCEPTIONAL":
        return {
          label: "Exceptional Stability",
          badge: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30",
          strokeColor: "#10b981",
          description: "Prime hardware health across all modules with minimal failure hazard.",
        };
      case "HEALTHY":
        return {
          label: "Healthy Operational",
          badge: "bg-teal-500/20 text-teal-300 border-teal-500/30",
          strokeColor: "#14b8a6",
          description: "Strong functional performance with routine maintenance recommended.",
        };
      case "STABLE":
        return {
          label: "Stable / Servicing Candidate",
          badge: "bg-cyan-500/20 text-cyan-300 border-cyan-500/30",
          strokeColor: "#06b6d4",
          description: "Moderate wear detected; component servicing yields high return on equity.",
        };
      case "AT_RISK":
        return {
          label: "Elevated Risk",
          badge: "bg-amber-500/20 text-amber-300 border-amber-500/30",
          strokeColor: "#f59e0b",
          description: "Active degradation requires intervention to avoid cascading component failure.",
        };
      default: // CRITICAL
        return {
          label: "Critical Condition",
          badge: "bg-rose-500/20 text-rose-300 border-rose-500/30",
          strokeColor: "#f43f5e",
          description: "Severe module failure or thermal risk requires immediate certified action.",
        };
    }
  };

  const tierDetails = getTierDetails(tier);

  // SVG Gauge calculations
  const radius = 80;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (score / 100) * circumference;

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl flex flex-col items-center justify-between space-y-6 shadow-xl relative overflow-hidden"
    >
      <div className="flex items-center justify-between w-full">
        <div className="flex items-center gap-2">
          <Activity className="w-5 h-5 text-emerald-400" />
          <h3 className="text-base font-bold text-white tracking-wide">
            Unified Intelligence Score
          </h3>
        </div>
        <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold border ${tierDetails.badge}`}>
          {tier}
        </span>
      </div>

      {/* Circular Gauge */}
      <div className="relative flex items-center justify-center">
        <svg className="w-48 h-48 transform -rotate-90">
          {/* Background Track */}
          <circle
            cx="96"
            cy="96"
            r={radius}
            stroke="currentColor"
            strokeWidth="12"
            fill="transparent"
            className="text-white/5"
          />
          {/* Progress Stroke */}
          <motion.circle
            cx="96"
            cy="96"
            r={radius}
            stroke={tierDetails.strokeColor}
            strokeWidth="12"
            strokeDasharray={circumference}
            initial={{ strokeDashoffset: circumference }}
            animate={{ strokeDashoffset }}
            transition={{ duration: 1.2, ease: "easeOut" }}
            strokeLinecap="round"
            fill="transparent"
          />
        </svg>

        {/* Center Text */}
        <div className="absolute flex flex-col items-center justify-center text-center">
          <span className="text-5xl font-black text-white tracking-tight">
            {score}
          </span>
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-widest mt-1">
            out of 100
          </span>
        </div>
      </div>

      {/* Status & Sub-Metrics */}
      <div className="w-full space-y-3">
        <div className="text-center">
          <h4 className="text-sm font-bold text-white">{tierDetails.label}</h4>
          <p className="text-xs text-slate-400 mt-0.5 leading-relaxed">{tierDetails.description}</p>
        </div>

        <div className="grid grid-cols-2 gap-2 pt-2 border-t border-white/5">
          <div className="rounded-xl bg-white/5 p-2.5 text-center">
            <span className="text-[11px] font-medium text-slate-400 block">Health Index</span>
            <span className="text-sm font-bold text-emerald-400">{breakdown.healthReliabilityScore}%</span>
          </div>
          <div className="rounded-xl bg-white/5 p-2.5 text-center">
            <span className="text-[11px] font-medium text-slate-400 block">Confidence</span>
            <span className="text-sm font-bold text-cyan-400">{confidence}%</span>
          </div>
        </div>
      </div>
    </motion.div>
  );
};
