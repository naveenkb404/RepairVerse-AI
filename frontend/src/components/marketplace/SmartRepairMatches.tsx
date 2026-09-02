"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Award,
  CheckCircle2,
  ChevronDown,
  Clock,
  DollarSign,
  Info,
  Leaf,
  MapPin,
  ShieldCheck,
  Sparkles,
  Star,
  Zap,
} from "lucide-react";
import {
  RepairShopMatchResponse,
  CategoryRecommendation,
} from "@/lib/types/repairMatching";
import { cn } from "@/lib/utils";

interface SmartRepairMatchesProps {
  matches: RepairShopMatchResponse[];
  recommendations?: CategoryRecommendation[];
  selectedShopIds: string[];
  onToggleSelectShop: (shopId: string) => void;
  onRequestQuote: (shop: RepairShopMatchResponse) => void;
}

export default function SmartRepairMatches({
  matches,
  recommendations = [],
  selectedShopIds,
  onToggleSelectShop,
  onRequestQuote,
}: SmartRepairMatchesProps) {
  const [expandedFactorsShopId, setExpandedFactorsShopId] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<"ALL" | "TOP_CHOICES">("TOP_CHOICES");
  const [sortBy, setSortBy] = useState<"SCORE" | "PRICE" | "SPEED" | "TRUST">("SCORE");

  if (!matches || matches.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-white/[0.03] p-12 text-center backdrop-blur-xl">
        <Sparkles className="mx-auto size-12 text-white/30" />
        <h3 className="mt-4 text-lg font-bold text-white">No Matching Repair Providers Found</h3>
        <p className="mt-1 text-sm text-white/60">
          Try expanding search radius or updating diagnosis parameters.
        </p>
      </div>
    );
  }

  // Sorted matches
  const sortedMatches = [...matches].sort((a, b) => {
    if (sortBy === "PRICE") return a.estimatedCost - b.estimatedCost;
    if (sortBy === "SPEED") return a.turnaroundHours - b.turnaroundHours;
    if (sortBy === "TRUST") return b.trustScore - a.trustScore;
    return b.overallScore - a.overallScore;
  });

  const topMatch = sortedMatches[0];
  const otherMatches = sortedMatches.slice(1);

  return (
    <div className="space-y-8">
      {/* Category Recommendation Badges Showcase */}
      {recommendations.length > 0 && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Award className="size-5 text-[#22C55E]" />
              <h3 className="text-base font-bold text-white">Intelligent Decision Categories</h3>
            </div>
            <span className="text-xs text-white/40">Deterministic multi-factor analysis</span>
          </div>

          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {recommendations.map((rec) => {
              const badgeColors: Record<string, string> = {
                BEST_OVERALL: "from-[#22C55E]/20 to-[#06B6D4]/10 border-[#22C55E]/40 text-[#22C55E]",
                BEST_VALUE: "from-emerald-500/20 to-teal-500/10 border-emerald-500/40 text-emerald-400",
                FASTEST_REPAIR: "from-amber-500/20 to-yellow-500/10 border-amber-500/40 text-amber-400",
                MOST_TRUSTED: "from-blue-500/20 to-indigo-500/10 border-blue-500/40 text-blue-400",
                MOST_SUSTAINABLE: "from-[#22C55E]/20 to-emerald-500/10 border-[#22C55E]/40 text-[#22C55E]",
                NEAREST: "from-cyan-500/20 to-sky-500/10 border-cyan-500/40 text-cyan-400",
              };

              const badgeIcons: Record<string, React.ReactNode> = {
                BEST_OVERALL: <Award className="size-4 shrink-0 text-[#22C55E]" />,
                BEST_VALUE: <DollarSign className="size-4 shrink-0 text-emerald-400" />,
                FASTEST_REPAIR: <Zap className="size-4 shrink-0 text-amber-400" />,
                MOST_TRUSTED: <ShieldCheck className="size-4 shrink-0 text-blue-400" />,
                MOST_SUSTAINABLE: <Leaf className="size-4 shrink-0 text-[#22C55E]" />,
                NEAREST: <MapPin className="size-4 shrink-0 text-cyan-400" />,
              };

              return (
                <motion.div
                  key={rec.category}
                  whileHover={{ y: -2 }}
                  className={cn(
                    "flex flex-col justify-between rounded-2xl border bg-gradient-to-br p-4 backdrop-blur-xl transition-all",
                    badgeColors[rec.category] || "from-white/10 to-white/5 border-white/20 text-white"
                  )}
                >
                  <div>
                    <div className="flex items-center gap-2">
                      {badgeIcons[rec.category]}
                      <span className="text-xs font-bold uppercase tracking-wider">
                        {rec.categoryLabel}
                      </span>
                    </div>
                    <p className="mt-2 text-sm font-semibold text-white">{rec.shop.shopName}</p>
                    <p className="mt-1 text-xs text-white/70 line-clamp-2">{rec.highlightReason}</p>
                  </div>

                  <div className="mt-4 flex items-center justify-between border-t border-white/10 pt-3">
                    <span className="text-xs font-medium text-white/60">
                      Score: <strong className="text-white">{rec.shop.overallScore}/100</strong>
                    </span>
                    <button
                      onClick={() => onRequestQuote(rec.shop)}
                      className="rounded-xl bg-white/10 px-3 py-1 text-xs font-semibold text-white transition-all hover:bg-white/20"
                    >
                      Select Shop
                    </button>
                  </div>
                </motion.div>
              );
            })}
          </div>
        </div>
      )}

      {/* Sorting & Filter Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-white/10 pb-4">
        <div>
          <h3 className="text-lg font-bold text-white">Ranked Repair Providers</h3>
          <p className="text-xs text-white/50">
            Ordered by deterministic compatibility with your device & fault profile
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-xs text-white/50">Sort by:</span>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value as any)}
            className="rounded-xl border border-white/15 bg-[#0B1120] px-3 py-1.5 text-xs font-semibold text-white focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
          >
            <option value="SCORE">Best Match (Score)</option>
            <option value="PRICE">Lowest Estimated Cost</option>
            <option value="SPEED">Fastest Turnaround</option>
            <option value="TRUST">Highest Trust Score</option>
          </select>
        </div>
      </div>

      {/* #1 Top Match Showcase Hero Card */}
      {topMatch && (
        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          className="relative overflow-hidden rounded-3xl border border-[#22C55E]/40 bg-gradient-to-br from-[#0B1120]/95 via-[#111C33]/90 to-[#0B1120]/95 p-6 shadow-[0_0_35px_rgba(34,197,94,0.15)] backdrop-blur-2xl md:p-8"
        >
          {/* Subtle glow background */}
          <div className="pointer-events-none absolute -right-20 -top-20 size-80 rounded-full bg-[#22C55E]/15 blur-3xl" />

          <div className="relative flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
            {/* Left Info Column */}
            <div className="flex-1 space-y-4">
              <div className="flex flex-wrap items-center gap-2">
                <span className="flex items-center gap-1.5 rounded-full border border-[#22C55E]/40 bg-[#22C55E]/15 px-3 py-1 text-xs font-bold text-[#22C55E]">
                  <Award className="size-3.5" />
                  #1 TOP COMPATIBILITY MATCH
                </span>
                {topMatch.isEcoCertified && (
                  <span className="flex items-center gap-1 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2.5 py-1 text-xs font-medium text-emerald-400">
                    <Leaf className="size-3" />
                    Eco-Certified
                  </span>
                )}
                <span className="rounded-full border border-white/10 bg-white/5 px-2.5 py-1 text-xs font-medium text-white/70">
                  {topMatch.verificationLevel}
                </span>
              </div>

              <div>
                <h4 className="text-2xl font-extrabold text-white">{topMatch.shopName}</h4>
                <p className="mt-1 flex items-center gap-1.5 text-xs text-white/60">
                  <MapPin className="size-3.5 text-[#06B6D4]" />
                  {topMatch.address}
                  {topMatch.distanceKm !== null && (
                    <span className="font-semibold text-[#06B6D4]">
                      • {topMatch.distanceKm.toFixed(1)} km away
                    </span>
                  )}
                </p>
              </div>

              {/* Explainable Rationale */}
              {topMatch.explanation && (
                <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4">
                  <p className="text-xs font-bold uppercase tracking-wider text-[#22C55E]">
                    Why this shop is recommended:
                  </p>
                  <p className="mt-1 text-sm font-medium text-white/90">
                    {topMatch.explanation.summary}
                  </p>
                  <ul className="mt-2.5 space-y-1">
                    {topMatch.explanation.keyReasons.map((reason, idx) => (
                      <li key={idx} className="flex items-start gap-2 text-xs text-white/70">
                        <CheckCircle2 className="mt-0.5 size-3.5 shrink-0 text-[#22C55E]" />
                        <span>{reason}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Key Highlights row */}
              <div className="grid grid-cols-2 gap-3 sm:grid-cols-4 pt-1">
                <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3 text-center">
                  <span className="text-[10px] font-bold uppercase text-white/40">Est. Cost</span>
                  <p className="mt-0.5 text-base font-bold text-white">
                    ${topMatch.estimatedCost.toFixed(0)}
                  </p>
                </div>
                <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3 text-center">
                  <span className="text-[10px] font-bold uppercase text-white/40">Turnaround</span>
                  <p className="mt-0.5 text-base font-bold text-[#06B6D4]">
                    {topMatch.turnaroundHours <= 6
                      ? "Express (Same Day)"
                      : `${topMatch.turnaroundHours.toFixed(0)} Hours`}
                  </p>
                </div>
                <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3 text-center">
                  <span className="text-[10px] font-bold uppercase text-white/40">Trust Score</span>
                  <p className="mt-0.5 text-base font-bold text-[#22C55E]">
                    {topMatch.trustScore}/100
                  </p>
                </div>
                <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3 text-center">
                  <span className="text-[10px] font-bold uppercase text-white/40">Warranty</span>
                  <p className="mt-0.5 text-base font-bold text-white">
                    {topMatch.warrantyDays} Days
                  </p>
                </div>
              </div>
            </div>

            {/* Right Score & Actions Column */}
            <div className="flex flex-col items-center justify-between gap-6 rounded-2xl border border-white/10 bg-white/[0.04] p-6 lg:w-72 lg:shrink-0">
              <div className="text-center">
                <div className="relative mx-auto flex size-24 items-center justify-center rounded-full border-4 border-[#22C55E] bg-[#22C55E]/10 shadow-[0_0_20px_rgba(34,197,94,0.3)]">
                  <div>
                    <span className="text-3xl font-black text-white">{topMatch.overallScore}</span>
                    <span className="block text-[10px] font-bold uppercase tracking-wider text-[#22C55E]">
                      /100 Match
                    </span>
                  </div>
                </div>

                <div className="mt-3 flex items-center justify-center gap-1 text-sm font-semibold text-amber-400">
                  <Star className="size-4 fill-amber-400" />
                  <span>{topMatch.rating}</span>
                  <span className="text-white/40">({topMatch.reviewCount} reviews)</span>
                </div>
              </div>

              <div className="w-full space-y-2">
                <button
                  onClick={() => onRequestQuote(topMatch)}
                  className="w-full rounded-xl bg-gradient-to-r from-[#22C55E] to-[#06B6D4] px-4 py-3 text-sm font-bold text-white shadow-lg shadow-[#22C55E]/20 transition-all hover:opacity-95 active:scale-[0.98]"
                >
                  Request Formal Quote
                </button>

                <button
                  onClick={() => onToggleSelectShop(topMatch.shopId)}
                  className={cn(
                    "w-full rounded-xl border px-4 py-2.5 text-xs font-semibold transition-all",
                    selectedShopIds.includes(topMatch.shopId)
                      ? "border-[#22C55E] bg-[#22C55E]/20 text-[#22C55E]"
                      : "border-white/15 bg-white/5 text-white/80 hover:bg-white/10"
                  )}
                >
                  {selectedShopIds.includes(topMatch.shopId)
                    ? "✓ Selected for Comparison"
                    : "+ Select for Comparison"}
                </button>
              </div>
            </div>
          </div>
        </motion.div>
      )}

      {/* Subsequent Ranked Match Cards */}
      <div className="space-y-4">
        {otherMatches.map((shop, idx) => {
          const rankNum = idx + 2;
          const isSelected = selectedShopIds.includes(shop.shopId);
          const isFactorsExpanded = expandedFactorsShopId === shop.shopId;

          return (
            <motion.div
              key={shop.shopId}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className={cn(
                "rounded-2xl border bg-[#0B1120]/70 p-5 backdrop-blur-xl transition-all duration-200 hover:border-white/20",
                isSelected
                  ? "border-[#22C55E]/50 bg-gradient-to-r from-[#22C55E]/10 to-transparent shadow-[0_0_20px_rgba(34,197,94,0.08)]"
                  : "border-white/10"
              )}
            >
              <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
                {/* Shop Basic Details */}
                <div className="flex items-start gap-4">
                  <div className="flex size-10 shrink-0 items-center justify-center rounded-xl border border-white/15 bg-white/[0.06] text-sm font-black text-white/80">
                    #{rankNum}
                  </div>

                  <div className="space-y-1">
                    <div className="flex flex-wrap items-center gap-2">
                      <h4 className="text-base font-bold text-white">{shop.shopName}</h4>
                      <span className="rounded-full border border-white/10 bg-white/5 px-2 py-0.5 text-[10px] font-semibold text-white/70">
                        {shop.matchLevel.replace("_", " ")}
                      </span>
                      {shop.isEcoCertified && (
                        <span className="flex items-center gap-1 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2 py-0.5 text-[10px] font-semibold text-emerald-400">
                          <Leaf className="size-2.5" />
                          Eco
                        </span>
                      )}
                    </div>

                    <p className="flex items-center gap-2 text-xs text-white/50">
                      <span>{shop.address}</span>
                      {shop.distanceKm !== null && (
                        <span className="text-[#06B6D4]">
                          • {shop.distanceKm.toFixed(1)} km
                        </span>
                      )}
                      <span>• {shop.rating} ★ ({shop.reviewCount})</span>
                    </p>

                    <p className="text-xs text-white/70">
                      {shop.explanation?.summary}
                    </p>
                  </div>
                </div>

                {/* Metrics Pill & Action */}
                <div className="flex flex-wrap items-center gap-4">
                  <div className="flex items-center gap-3 border-y border-white/10 py-2 sm:border-y-0 sm:py-0">
                    <div className="text-right">
                      <span className="text-[10px] uppercase text-white/40">Compatibility</span>
                      <p className="text-base font-extrabold text-[#22C55E]">
                        {shop.overallScore}/100
                      </p>
                    </div>

                    <div className="h-8 w-px bg-white/10" />

                    <div className="text-right">
                      <span className="text-[10px] uppercase text-white/40">Est. Cost</span>
                      <p className="text-sm font-bold text-white">
                        ${shop.estimatedCost.toFixed(0)}
                      </p>
                    </div>

                    <div className="h-8 w-px bg-white/10" />

                    <div className="text-right">
                      <span className="text-[10px] uppercase text-white/40">Turnaround</span>
                      <p className="text-sm font-bold text-[#06B6D4]">
                        {shop.turnaroundHours <= 6 ? "Express" : `${shop.turnaroundHours.toFixed(0)}h`}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => onToggleSelectShop(shop.shopId)}
                      className={cn(
                        "rounded-xl border px-3 py-2 text-xs font-semibold transition-all",
                        isSelected
                          ? "border-[#22C55E] bg-[#22C55E]/20 text-[#22C55E]"
                          : "border-white/15 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white"
                      )}
                    >
                      {isSelected ? "Selected" : "Compare"}
                    </button>

                    <button
                      onClick={() => onRequestQuote(shop)}
                      className="rounded-xl bg-gradient-to-r from-[#22C55E]/80 to-[#06B6D4]/80 px-4 py-2 text-xs font-bold text-white hover:opacity-90 active:scale-95"
                    >
                      Quote
                    </button>

                    <button
                      onClick={() =>
                        setExpandedFactorsShopId(isFactorsExpanded ? null : shop.shopId)
                      }
                      className="rounded-xl border border-white/10 p-2 text-white/50 hover:bg-white/5 hover:text-white"
                      aria-label="View score breakdown"
                    >
                      <ChevronDown
                        className={cn(
                          "size-4 transition-transform",
                          isFactorsExpanded && "rotate-180 text-[#22C55E]"
                        )}
                      />
                    </button>
                  </div>
                </div>
              </div>

              {/* Expandable Deterministic Factors Breakdown */}
              <AnimatePresence>
                {isFactorsExpanded && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: "auto" }}
                    exit={{ opacity: 0, height: 0 }}
                    className="overflow-hidden border-t border-white/10 pt-4 mt-4"
                  >
                    <div className="space-y-2">
                      <p className="text-xs font-bold uppercase tracking-wider text-white/40">
                        Deterministic 0–100 Factor Breakdown:
                      </p>
                      <div className="grid grid-cols-1 gap-2 sm:grid-cols-2 lg:grid-cols-3">
                        {shop.factors.map((f, i) => (
                          <div
                            key={i}
                            className="rounded-xl border border-white/10 bg-white/[0.02] p-3"
                          >
                            <div className="flex items-center justify-between text-xs">
                              <span className="font-semibold text-white">{f.factorName}</span>
                              <span className="font-bold text-[#22C55E]">
                                {f.score}/{f.maxScore} pts
                              </span>
                            </div>
                            <div className="mt-1.5 h-1.5 w-full overflow-hidden rounded-full bg-white/10">
                              <div
                                className="h-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
                                style={{ width: `${(f.score / f.maxScore) * 100}%` }}
                              />
                            </div>
                            <p className="mt-1.5 text-[11px] text-white/60">{f.explanation}</p>
                          </div>
                        ))}
                      </div>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
