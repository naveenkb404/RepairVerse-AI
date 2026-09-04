"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Trophy, Star, Zap, Shield, DollarSign, Leaf, TrendingUp, TrendingDown, Minus, BarChart3 } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { NetworkLeaderboardResponse } from "@/lib/types/networkIntelligence";

const RANKING_TYPES = [
  { key: "BEST_OVERALL",    label: "Best Overall",    icon: Trophy,    color: "text-amber-400" },
  { key: "MOST_TRUSTED",    label: "Most Trusted",    icon: Shield,    color: "text-cyan-400" },
  { key: "HIGHEST_QUALITY", label: "Highest Quality", icon: Star,      color: "text-violet-400" },
  { key: "FASTEST",         label: "Fastest",         icon: Zap,       color: "text-yellow-400" },
  { key: "BEST_VALUE",      label: "Best Value",      icon: DollarSign,color: "text-emerald-400" },
  { key: "MOST_SUSTAINABLE",label: "Eco Leader",      icon: Leaf,      color: "text-green-400" },
];

interface RepairNetworkLeaderboardProps {
  data: Record<string, NetworkLeaderboardResponse[]>;
  onFilterChange?: (rankingType: string) => void;
}

export default function RepairNetworkLeaderboard({ data, onFilterChange }: RepairNetworkLeaderboardProps) {
  const [activeType, setActiveType] = useState("BEST_OVERALL");
  const entries = data[activeType] ?? [];

  const handleSwitch = (key: string) => {
    setActiveType(key);
    onFilterChange?.(key);
  };

  return (
    <GlassCard className="p-6">
      <div className="flex items-center gap-2 mb-5">
        <BarChart3 className="w-5 h-5 text-amber-400" />
        <h3 className="text-base font-semibold text-white">Repair Network Leaderboard</h3>
      </div>

      {/* Ranking Type Tabs */}
      <div className="flex flex-wrap gap-1.5 mb-5">
        {RANKING_TYPES.map(({ key, label, icon: Icon, color }) => (
          <button
            key={key}
            onClick={() => handleSwitch(key)}
            className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
              activeType === key
                ? "bg-white/10 text-white border border-white/20"
                : "bg-white/[0.03] text-slate-400 border border-white/[0.06] hover:bg-white/[0.06] hover:text-slate-300"
            }`}
          >
            <Icon className={`w-3 h-3 ${activeType === key ? color : ""}`} />
            {label}
          </button>
        ))}
      </div>

      {/* Leaderboard Rows */}
      <div className="space-y-2.5">
        <AnimatePresence mode="wait">
          <motion.div
            key={activeType}
            initial={{ opacity: 0, y: 6 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -6 }}
            transition={{ duration: 0.2 }}
            className="space-y-2.5"
          >
            {entries.length === 0 ? (
              <p className="text-sm text-slate-500 text-center py-6">No data available for this ranking</p>
            ) : (
              entries.map((entry, i) => (
                <LeaderboardRow key={entry.shopId} entry={entry} index={i} />
              ))
            )}
          </motion.div>
        </AnimatePresence>
      </div>
    </GlassCard>
  );
}

function LeaderboardRow({ entry, index }: { entry: NetworkLeaderboardResponse; index: number }) {
  const rankColors = ["text-amber-400", "text-slate-300", "text-amber-600", "text-slate-400", "text-slate-400"];
  const rankColor = rankColors[index] ?? "text-slate-500";
  const TrendIcon = entry.trend === "IMPROVING" ? TrendingUp : entry.trend === "DECLINING" ? TrendingDown : Minus;
  const trendColor = entry.trend === "IMPROVING" ? "text-emerald-400" : entry.trend === "DECLINING" ? "text-red-400" : "text-slate-500";

  return (
    <motion.div
      initial={{ opacity: 0, x: -10 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ delay: index * 0.05 }}
      className="flex items-center gap-3 p-3 rounded-xl bg-white/[0.03] border border-white/[0.06] hover:bg-white/[0.05] transition-colors group"
    >
      {/* Rank */}
      <div className={`w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold ${
        index === 0 ? "bg-amber-500/20 border border-amber-500/30" : "bg-white/[0.04]"
      } ${rankColor}`}>
        {index === 0 ? "🏆" : index === 1 ? "🥈" : index === 2 ? "🥉" : `#${entry.rank}`}
      </div>

      {/* Shop Info */}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-white truncate">{entry.shopName}</p>
        <p className="text-[10px] text-slate-500 truncate">{entry.badge}</p>
      </div>

      {/* Metrics */}
      <div className="flex items-center gap-4 text-right">
        <div className="hidden sm:block">
          <p className="text-[10px] text-slate-500">Success</p>
          <p className="text-xs font-medium text-emerald-400">{(entry.successRate * 100).toFixed(0)}%</p>
        </div>
        <div>
          <p className="text-[10px] text-slate-500">Quality</p>
          <p className="text-xs font-semibold text-white">{entry.qualityScore}</p>
        </div>
        <div className="flex items-center gap-1">
          <Star className="w-3 h-3 text-amber-400" />
          <span className="text-xs text-slate-300">{entry.customerRating.toFixed(1)}</span>
        </div>
        <TrendIcon className={`w-4 h-4 ${trendColor} flex-shrink-0`} />
      </div>
    </motion.div>
  );
}
