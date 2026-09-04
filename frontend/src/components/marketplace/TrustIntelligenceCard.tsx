"use client";

import React from "react";
import { motion } from "framer-motion";
import { CheckCircle2, AlertTriangle, ShieldCheck } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { TrustScoreResponse, TrustTier } from "@/lib/types/networkIntelligence";

interface TrustIntelligenceCardProps {
  trust: TrustScoreResponse;
}

const TIER_CONFIG: Record<TrustTier, { label: string; color: string; bg: string; text: string; border: string }> = {
  EXCEPTIONAL: { label: "Exceptional",  color: "#10b981", bg: "bg-emerald-500/15", text: "text-emerald-400", border: "border-emerald-500/30" },
  HIGH:        { label: "High",         color: "#06b6d4", bg: "bg-cyan-500/15",    text: "text-cyan-400",    border: "border-cyan-500/30" },
  ESTABLISHED: { label: "Established", color: "#8b5cf6", bg: "bg-violet-500/15",  text: "text-violet-400",  border: "border-violet-500/30" },
  MODERATE:    { label: "Moderate",    color: "#f59e0b", bg: "bg-amber-500/15",   text: "text-amber-400",   border: "border-amber-500/30" },
  LOW:         { label: "Low",         color: "#ef4444", bg: "bg-red-500/15",     text: "text-red-400",     border: "border-red-500/30" },
};

export default function TrustIntelligenceCard({ trust }: TrustIntelligenceCardProps) {
  const tier = TIER_CONFIG[trust.trustTier] ?? TIER_CONFIG.MODERATE;

  const circumference = 2 * Math.PI * 36;
  const offset = circumference - (trust.trustScore / 100) * circumference;

  return (
    <GlassCard className="p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-5">
        <div className="flex items-center gap-2">
          <ShieldCheck className="w-5 h-5" style={{ color: tier.color }} />
          <h3 className="text-base font-semibold text-white">Trust Intelligence</h3>
        </div>
        <span className={`px-2.5 py-1 rounded-full text-xs font-medium border ${tier.bg} ${tier.text} ${tier.border}`}>
          {tier.label}
        </span>
      </div>

      {/* Score Gauge */}
      <div className="flex items-center gap-5 mb-6">
        <div className="relative flex-shrink-0">
          <svg width="80" height="80" className="-rotate-90">
            <circle cx="40" cy="40" r="36" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="8" />
            <motion.circle
              cx="40" cy="40" r="36"
              fill="none"
              stroke={tier.color}
              strokeWidth="8"
              strokeLinecap="round"
              strokeDasharray={circumference}
              initial={{ strokeDashoffset: circumference }}
              animate={{ strokeDashoffset: offset }}
              transition={{ duration: 1.0, ease: [0.22, 1, 0.36, 1] }}
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-xl font-bold text-white">{trust.trustScore}</span>
            <span className="text-[9px] text-slate-400">/ 100</span>
          </div>
        </div>
        <div className="flex-1">
          <p className="text-xs text-slate-400 mb-2">Score composition</p>
          <div className="space-y-1.5">
            {Object.entries(trust.scoreBreakdown).slice(0, 4).map(([label, pts]) => (
              <div key={label} className="flex items-center justify-between">
                <span className="text-xs text-slate-300 truncate max-w-[140px]">{label}</span>
                <span className={`text-xs font-medium ${(pts as number) >= 0 ? "text-emerald-400" : "text-red-400"}`}>
                  {(pts as number) > 0 ? "+" : ""}{pts as number}
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Positive Signals */}
      {trust.positiveSignals.length > 0 && (
        <div className="mb-4">
          <p className="text-xs font-medium text-slate-400 mb-2">✓ Positive Signals</p>
          <div className="space-y-1.5">
            {trust.positiveSignals.map((signal, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, x: -8 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.07 }}
                className="flex items-start gap-2"
              >
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 flex-shrink-0 mt-0.5" />
                <span className="text-xs text-slate-300">{signal}</span>
              </motion.div>
            ))}
          </div>
        </div>
      )}

      {/* Risk Signals */}
      {trust.riskSignals.length > 0 && (
        <div>
          <p className="text-xs font-medium text-slate-400 mb-2">⚠ Risk Signals</p>
          <div className="space-y-1.5">
            {trust.riskSignals.map((signal, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0, x: -8 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: i * 0.07 }}
                className="flex items-start gap-2"
              >
                <AlertTriangle className="w-3.5 h-3.5 text-orange-400 flex-shrink-0 mt-0.5" />
                <span className="text-xs text-slate-300">{signal}</span>
              </motion.div>
            ))}
          </div>
        </div>
      )}

      {trust.riskSignals.length === 0 && trust.positiveSignals.length > 0 && (
        <p className="text-xs text-emerald-400/70 mt-2">No risk signals detected.</p>
      )}
    </GlassCard>
  );
}
