"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  AlertTriangle,
  CheckCircle2,
  DollarSign,
  HelpCircle,
  Info,
  Scale,
  ShieldAlert,
  Sparkles,
  TrendingDown,
  TrendingUp,
} from "lucide-react";
import { QuoteIntelligenceResponse } from "@/lib/types/repairMatching";
import { cn } from "@/lib/utils";

interface QuoteIntelligenceCardProps {
  intelligence: QuoteIntelligenceResponse | null;
  onAccept?: () => void;
  onReject?: () => void;
}

export default function QuoteIntelligenceCard({
  intelligence,
  onAccept,
  onReject,
}: QuoteIntelligenceCardProps) {
  if (!intelligence) {
    return (
      <div className="rounded-3xl border border-white/10 bg-white/[0.03] p-12 text-center backdrop-blur-xl">
        <Sparkles className="mx-auto size-12 text-white/30" />
        <h3 className="mt-4 text-lg font-bold text-white">No Quote Intelligence Loaded</h3>
        <p className="mt-1 text-sm text-white/60">
          Select or request a quote to view comprehensive deterministic price intelligence and value analysis.
        </p>
      </div>
    );
  }

  const {
    shopName,
    estimatedCost,
    partsCost,
    laborCost,
    marketAverageCost,
    costDifference,
    costDifferencePercent,
    classification,
    classificationLabel,
    priceFairnessScore,
    insights,
    warnings,
  } = intelligence;

  // Class colors
  const classStyles: Record<string, { bg: string; text: string; border: string; icon: React.ReactNode }> = {
    EXCELLENT_VALUE: {
      bg: "bg-emerald-500/15",
      text: "text-emerald-400",
      border: "border-emerald-500/40",
      icon: <Sparkles className="size-4 text-emerald-400" />,
    },
    GOOD_VALUE: {
      bg: "bg-[#22C55E]/15",
      text: "text-[#22C55E]",
      border: "border-[#22C55E]/40",
      icon: <CheckCircle2 className="size-4 text-[#22C55E]" />,
    },
    FAIR_PRICE: {
      bg: "bg-cyan-500/15",
      text: "text-cyan-400",
      border: "border-cyan-500/40",
      icon: <Scale className="size-4 text-cyan-400" />,
    },
    ABOVE_MARKET: {
      bg: "bg-amber-500/15",
      text: "text-amber-400",
      border: "border-amber-500/40",
      icon: <TrendingUp className="size-4 text-amber-400" />,
    },
    OVERPRICED: {
      bg: "bg-rose-500/15",
      text: "text-rose-400",
      border: "border-rose-500/40",
      icon: <AlertTriangle className="size-4 text-rose-400" />,
    },
    SUSPICIOUSLY_LOW: {
      bg: "bg-red-500/20",
      text: "text-red-400",
      border: "border-red-500/50",
      icon: <ShieldAlert className="size-4 text-red-400" />,
    },
  };

  const style = classStyles[classification] || classStyles.FAIR_PRICE;

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      className="rounded-3xl border border-white/10 bg-gradient-to-br from-[#0B1120]/95 via-[#111C33]/90 to-[#0B1120]/95 p-6 shadow-2xl backdrop-blur-2xl md:p-8 space-y-6"
    >
      {/* Header with Classification Badge */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-white/10 pb-6">
        <div>
          <div className="flex items-center gap-2">
            <span
              className={cn(
                "flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs font-bold uppercase tracking-wider",
                style.bg,
                style.text,
                style.border
              )}
            >
              {style.icon}
              {classificationLabel}
            </span>
          </div>
          <h3 className="mt-2 text-xl font-extrabold text-white">
            Quotation Intelligence Analysis
          </h3>
          <p className="text-xs text-white/60">
            Provider: <strong className="text-white">{shopName}</strong>
          </p>
        </div>

        {/* Price Fairness Index Gauge */}
        <div className="flex items-center gap-3 rounded-2xl border border-white/10 bg-white/[0.03] px-4 py-3">
          <div className="text-right">
            <span className="text-[10px] uppercase font-bold text-white/40">
              Price Fairness Index
            </span>
            <p className="text-lg font-black text-[#22C55E]">{priceFairnessScore}/100</p>
          </div>
          <div className="h-10 w-10 flex items-center justify-center rounded-full border-2 border-[#22C55E] bg-[#22C55E]/10 text-xs font-bold text-white">
            {priceFairnessScore}%
          </div>
        </div>
      </div>

      {/* Pricing Comparison Stats Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
          <span className="text-[11px] font-bold uppercase text-white/40">Quoted Price</span>
          <p className="mt-1 text-2xl font-black text-white">${estimatedCost.toFixed(2)}</p>
          {(partsCost > 0 || laborCost > 0) && (
            <p className="mt-1 text-xs text-white/50">
              ${partsCost.toFixed(0)} Parts + ${laborCost.toFixed(0)} Labor
            </p>
          )}
        </div>

        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
          <span className="text-[11px] font-bold uppercase text-white/40">
            Market Regional Average
          </span>
          <p className="mt-1 text-2xl font-black text-white/80">
            ${marketAverageCost.toFixed(2)}
          </p>
          <p className="mt-1 text-xs text-white/50">Prevailing benchmark for fault type</p>
        </div>

        <div
          className={cn(
            "rounded-2xl border p-4",
            costDifference <= 0
              ? "border-[#22C55E]/30 bg-[#22C55E]/[0.06]"
              : "border-amber-500/30 bg-amber-500/[0.06]"
          )}
        >
          <span className="text-[11px] font-bold uppercase text-white/40">Market Variance</span>
          <p
            className={cn(
              "mt-1 text-2xl font-black",
              costDifference <= 0 ? "text-[#22C55E]" : "text-amber-400"
            )}
          >
            {costDifference <= 0 ? "-" : "+"}${Math.abs(costDifference).toFixed(2)} (
            {costDifferencePercent > 0 ? `+${costDifferencePercent}%` : `${costDifferencePercent}%`})
          </p>
          <p className="mt-1 text-xs text-white/60">
            {costDifference <= 0
              ? `Estimated savings: $${Math.abs(costDifference).toFixed(2)}`
              : "Priced above average"}
          </p>
        </div>
      </div>

      {/* Visual Price Scale Bar */}
      <div className="space-y-2 rounded-2xl border border-white/10 bg-white/[0.02] p-4">
        <div className="flex justify-between text-xs text-white/60">
          <span>Rock-Bottom (-50%)</span>
          <span className="font-semibold text-white">Market Avg (${marketAverageCost.toFixed(0)})</span>
          <span>Premium (+50%)</span>
        </div>
        <div className="relative h-3 w-full rounded-full bg-gradient-to-r from-red-500/60 via-[#22C55E]/80 to-amber-500/60 overflow-hidden">
          <div
            className="absolute top-0 bottom-0 w-1.5 bg-white shadow-[0_0_8px_white] rounded-full"
            style={{
              left: `${Math.max(5, Math.min(95, 50 + (costDifferencePercent / 100) * 50))}%`,
            }}
          />
        </div>
        <p className="text-[11px] text-white/40 text-center">
          The indicator shows where this quote falls relative to standard market pricing.
        </p>
      </div>

      {/* Warnings & Risk Signals if present */}
      {warnings.length > 0 && (
        <div className="rounded-2xl border border-red-500/40 bg-red-500/10 p-4 space-y-2">
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-red-400">
            <ShieldAlert className="size-4" />
            Pricing Risk & Validation Warning
          </div>
          <ul className="space-y-1">
            {warnings.map((w, idx) => (
              <li key={idx} className="flex items-start gap-2 text-xs text-red-200">
                <span className="text-red-400">•</span>
                <span>{w}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Insights Breakdown */}
      {insights.length > 0 && (
        <div className="space-y-2">
          <p className="text-xs font-bold uppercase tracking-wider text-white/50">
            Diagnostic & Pricing Insights:
          </p>
          <ul className="space-y-1.5">
            {insights.map((insight, idx) => (
              <li key={idx} className="flex items-start gap-2 text-xs text-white/80">
                <CheckCircle2 className="mt-0.5 size-3.5 shrink-0 text-[#06B6D4]" />
                <span>{insight}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Optional CTAs */}
      {(onAccept || onReject) && (
        <div className="flex items-center justify-end gap-3 pt-4 border-t border-white/10">
          {onReject && (
            <button
              onClick={onReject}
              className="rounded-xl border border-white/15 bg-white/5 px-4 py-2 text-xs font-semibold text-white/70 hover:bg-white/10 hover:text-white"
            >
              Decline Quote
            </button>
          )}
          {onAccept && (
            <button
              onClick={onAccept}
              className="rounded-xl bg-gradient-to-r from-[#22C55E] to-[#06B6D4] px-5 py-2 text-xs font-bold text-white shadow-lg shadow-[#22C55E]/20 hover:opacity-95"
            >
              Accept & Book Repair
            </button>
          )}
        </div>
      )}
    </motion.div>
  );
}
