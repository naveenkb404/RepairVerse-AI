"use client";

import React, { useState } from "react";
import { motion } from "framer-motion";
import { Star, TrendingUp, TrendingDown, Minus, ChevronDown, ChevronUp, Award } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { RepairShopQualityResponse, QualityFactor } from "@/lib/types/networkIntelligence";

interface RepairQualityScoreProps {
  quality: RepairShopQualityResponse;
  compact?: boolean;
}

const TIER_CONFIG = {
  ELITE:            { color: "#f59e0b", bg: "bg-amber-500/15 border-amber-500/30",  text: "text-amber-400",   label: "Elite",            emoji: "🏆" },
  EXCELLENT:        { color: "#10b981", bg: "bg-emerald-500/15 border-emerald-500/30", text: "text-emerald-400", label: "Excellent",        emoji: "⭐" },
  TRUSTED:          { color: "#06b6d4", bg: "bg-cyan-500/15 border-cyan-500/30",    text: "text-cyan-400",    label: "Trusted",           emoji: "🛡" },
  STANDARD:         { color: "#8b5cf6", bg: "bg-violet-500/15 border-violet-500/30", text: "text-violet-400", label: "Standard",          emoji: "✓" },
  NEEDS_IMPROVEMENT:{ color: "#ef4444", bg: "bg-red-500/15 border-red-500/30",      text: "text-red-400",     label: "Needs Improvement", emoji: "⚠" },
};

export default function RepairQualityScore({ quality, compact = false }: RepairQualityScoreProps) {
  const [showFactors, setShowFactors] = useState(!compact);
  const tier = TIER_CONFIG[quality.qualityTier] ?? TIER_CONFIG.TRUSTED;

  const circumference = 2 * Math.PI * 52;
  const offset = circumference - (quality.overallQualityScore / 100) * circumference;

  const TrendIcon = quality.trend === "IMPROVING" ? TrendingUp
    : quality.trend === "DECLINING" ? TrendingDown : Minus;
  const trendColor = quality.trend === "IMPROVING" ? "text-emerald-400"
    : quality.trend === "DECLINING" ? "text-red-400" : "text-slate-400";

  return (
    <GlassCard className="p-6">
      {/* Header */}
      <div className="flex items-start justify-between mb-6">
        <div>
          <h3 className="text-lg font-semibold text-white">{quality.shopName}</h3>
          <div className="flex items-center gap-2 mt-1">
            <span className={`inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium border ${tier.bg} ${tier.text}`}>
              {tier.emoji} {tier.label}
            </span>
            <TrendIcon className={`w-4 h-4 ${trendColor}`} />
            <span className={`text-xs ${trendColor}`}>{quality.trend}</span>
          </div>
        </div>
        <Award className="w-6 h-6 text-amber-400 opacity-60" />
      </div>

      {/* Circular Score */}
      <div className="flex items-center gap-8 mb-6">
        <div className="relative flex-shrink-0">
          <svg width="120" height="120" className="-rotate-90">
            <circle cx="60" cy="60" r="52" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="10" />
            <motion.circle
              cx="60" cy="60" r="52"
              fill="none"
              stroke={tier.color}
              strokeWidth="10"
              strokeLinecap="round"
              strokeDasharray={circumference}
              initial={{ strokeDashoffset: circumference }}
              animate={{ strokeDashoffset: offset }}
              transition={{ duration: 1.2, ease: [0.22, 1, 0.36, 1] }}
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <motion.span
              className="text-3xl font-bold text-white"
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: 0.5, duration: 0.4 }}
            >
              {quality.overallQualityScore}
            </motion.span>
            <span className="text-xs text-slate-400">/ 100</span>
          </div>
        </div>

        {/* Quick Stats */}
        <div className="flex-1 grid grid-cols-2 gap-3">
          {[
            { label: "Success Rate",  value: `${(quality.successRate * 100).toFixed(0)}%`,  color: "text-emerald-400" },
            { label: "Total Repairs", value: quality.totalRepairs.toString(),                color: "text-cyan-400" },
            { label: "Repeat Rate",   value: `${(quality.repeatRepairRate * 100).toFixed(0)}%`, color: quality.repeatRepairRate > 0.15 ? "text-orange-400" : "text-slate-300" },
            { label: "Reliability",   value: `${quality.reliabilityScore}/100`,               color: "text-violet-400" },
          ].map((stat) => (
            <div key={stat.label} className="bg-white/[0.03] rounded-lg p-2.5">
              <p className="text-[10px] text-slate-400 mb-0.5">{stat.label}</p>
              <p className={`text-sm font-semibold ${stat.color}`}>{stat.value}</p>
            </div>
          ))}
        </div>
      </div>

      {/* Factor Breakdown */}
      {quality.factorBreakdown.length > 0 && (
        <div>
          <button
            onClick={() => setShowFactors(!showFactors)}
            className="flex items-center gap-1.5 text-sm text-slate-400 hover:text-white transition-colors mb-3"
          >
            Score Breakdown
            {showFactors ? <ChevronUp className="w-3.5 h-3.5" /> : <ChevronDown className="w-3.5 h-3.5" />}
          </button>

          {showFactors && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              className="space-y-2"
            >
              {quality.factorBreakdown.map((factor, i) => (
                <FactorBar key={factor.factor} factor={factor} index={i} />
              ))}
            </motion.div>
          )}
        </div>
      )}
    </GlassCard>
  );
}

function FactorBar({ factor, index }: { factor: QualityFactor; index: number }) {
  const pct = (factor.score / factor.weight) * 100;
  const color = pct >= 85 ? "#10b981" : pct >= 60 ? "#06b6d4" : pct >= 40 ? "#f59e0b" : "#ef4444";

  return (
    <motion.div
      initial={{ opacity: 0, x: -10 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ delay: index * 0.06, duration: 0.3 }}
      className="group"
    >
      <div className="flex items-center justify-between mb-1">
        <span className="text-xs text-slate-300">{factor.factor}</span>
        <span className="text-xs text-slate-400">{factor.score}/{factor.weight}</span>
      </div>
      <div className="h-1.5 bg-white/[0.06] rounded-full overflow-hidden">
        <motion.div
          className="h-full rounded-full"
          style={{ backgroundColor: color }}
          initial={{ width: 0 }}
          animate={{ width: `${pct}%` }}
          transition={{ delay: index * 0.06 + 0.2, duration: 0.6, ease: "easeOut" }}
        />
      </div>
      <p className="text-[10px] text-slate-500 mt-0.5 opacity-0 group-hover:opacity-100 transition-opacity">
        {factor.description}
      </p>
    </motion.div>
  );
}
