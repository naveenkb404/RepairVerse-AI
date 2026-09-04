"use client";

import React, { useState } from "react";
import {
  Sparkles,
  TrendingUp,
  ShieldCheck,
  AlertTriangle,
  Leaf,
  Store,
  ThumbsUp,
  ThumbsDown,
  CheckCircle2,
  Filter,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { PatternInsightResponse, FeedbackType } from "@/lib/types/repairKnowledgeGraph";
import { cn } from "@/lib/utils";

interface PatternInsightsPanelProps {
  insights: PatternInsightResponse[];
  onFeedback?: (insightId: string, type: FeedbackType) => Promise<void> | void;
}

export default function PatternInsightsPanel({
  insights,
  onFeedback,
}: PatternInsightsPanelProps) {
  const [selectedType, setSelectedType] = useState<string>("ALL");
  const [loadingId, setLoadingId] = useState<string | null>(null);

  const getInsightIcon = (type: string) => {
    switch (type) {
      case "COMMON_FAILURE":
        return AlertTriangle;
      case "HIGH_SUCCESS_REPAIR":
        return CheckCircle2;
      case "PREVENTIVE_OPPORTUNITY":
        return Sparkles;
      case "SHOP_SPECIALIZATION":
        return Store;
      case "SUSTAINABILITY_PATTERN":
        return Leaf;
      default:
        return TrendingUp;
    }
  };

  const getInsightBadge = (type: string) => {
    switch (type) {
      case "HIGH_SUCCESS_REPAIR":
        return "bg-emerald-500/10 text-emerald-400 border-emerald-500/30";
      case "COMMON_FAILURE":
        return "bg-amber-500/10 text-amber-400 border-amber-500/30";
      case "PREVENTIVE_OPPORTUNITY":
        return "bg-cyan-500/10 text-cyan-300 border-cyan-500/30";
      case "SUSTAINABILITY_PATTERN":
        return "bg-teal-500/10 text-teal-300 border-teal-500/30";
      default:
        return "bg-indigo-500/10 text-indigo-300 border-indigo-500/30";
    }
  };

  const handleFeedback = async (insightId: string, type: FeedbackType) => {
    if (!onFeedback) return;
    setLoadingId(insightId);
    try {
      await onFeedback(insightId, type);
    } finally {
      setLoadingId(null);
    }
  };

  const filtered = insights.filter((i) => {
    if (selectedType === "ALL") return true;
    return i.insightType === selectedType;
  });

  const insightTypes = [
    "ALL",
    "COMMON_FAILURE",
    "HIGH_SUCCESS_REPAIR",
    "PREVENTIVE_OPPORTUNITY",
    "SHOP_SPECIALIZATION",
    "SUSTAINABILITY_PATTERN",
  ];

  return (
    <div className="space-y-6">
      {/* Header & Filter Tabs */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-white/10 pb-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-amber-500/10 text-amber-400 border border-amber-500/20">
            <Sparkles className="h-5 w-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Discovered Ecosystem Pattern Insights</h3>
            <p className="text-xs text-slate-400">Autonomous pattern extraction across multi-fleet repair records</p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-1.5">
          {insightTypes.map((type) => (
            <button
              key={type}
              onClick={() => setSelectedType(type)}
              className={cn(
                "rounded-lg px-2.5 py-1 text-[11px] font-semibold transition-all",
                selectedType === type
                  ? "bg-gradient-to-r from-amber-500 to-emerald-500 text-slate-950 shadow-sm"
                  : "border border-white/5 bg-white/[0.03] text-slate-400 hover:bg-white/[0.08] hover:text-white"
              )}
            >
              {type === "ALL" ? "All Insights" : type.replace(/_/g, " ")}
            </button>
          ))}
        </div>
      </div>

      {/* Insights Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {filtered.map((insight) => {
          const Icon = getInsightIcon(insight.insightType);
          const badgeClass = getInsightBadge(insight.insightType);
          const isLoading = loadingId === insight.id;

          return (
            <GlassCard key={insight.id} padding="md" glowColor="none" className="flex flex-col justify-between">
              <div className="space-y-3.5">
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-2">
                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-white/[0.05] border border-white/10 text-white">
                      <Icon className="h-4 w-4 text-cyan-400" />
                    </div>
                    <span className={cn("rounded-full border px-2.5 py-0.5 text-[10px] font-bold font-mono uppercase", badgeClass)}>
                      {insight.insightType.replace(/_/g, " ")}
                    </span>
                  </div>

                  <div className="flex items-center gap-2 text-[10px] font-mono">
                    <span className="text-emerald-400 font-bold">Impact: {insight.impactScore}/100</span>
                  </div>
                </div>

                <h4 className="text-sm font-bold text-white leading-snug">{insight.title}</h4>
                <p className="text-xs text-slate-300 leading-relaxed">{insight.description}</p>
              </div>

              {/* Evidence & Feedback Footer */}
              <div className="mt-4 pt-3 border-t border-white/5 flex items-center justify-between">
                <div className="flex items-center gap-2 text-[11px] text-slate-400 font-mono">
                  <span>Based on {insight.supportingObservations} observations</span>
                  <span>•</span>
                  <span>{Math.round(insight.confidence * 100)}% Conf.</span>
                </div>

                <div className="flex items-center gap-2">
                  <button
                    id={`feedback-helpful-${insight.id}`}
                    onClick={() => handleFeedback(insight.id, "HELPFUL")}
                    disabled={isLoading}
                    className="inline-flex items-center gap-1 rounded-lg border border-white/10 bg-white/[0.04] px-2.5 py-1 text-[11px] font-medium text-slate-300 hover:border-emerald-500/40 hover:bg-emerald-500/10 hover:text-emerald-300 transition-colors disabled:opacity-50"
                  >
                    <ThumbsUp className="h-3 w-3" />
                    <span>{insight.helpfulVotes || 0}</span>
                  </button>

                  <button
                    id={`feedback-inaccurate-${insight.id}`}
                    onClick={() => handleFeedback(insight.id, "INACCURATE")}
                    disabled={isLoading}
                    className="inline-flex items-center gap-1 rounded-lg border border-white/10 bg-white/[0.04] px-2.5 py-1 text-[11px] font-medium text-slate-300 hover:border-red-500/40 hover:bg-red-500/10 hover:text-red-400 transition-colors disabled:opacity-50"
                  >
                    <ThumbsDown className="h-3 w-3" />
                  </button>
                </div>
              </div>
            </GlassCard>
          );
        })}
      </div>
    </div>
  );
}
