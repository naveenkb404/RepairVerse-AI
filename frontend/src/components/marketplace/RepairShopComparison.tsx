"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Award,
  Check,
  CheckCircle2,
  DollarSign,
  HelpCircle,
  Leaf,
  MapPin,
  ShieldCheck,
  Star,
  Trash2,
  Zap,
} from "lucide-react";
import {
  RepairMarketplaceComparison,
  RepairShopMatchResponse,
} from "@/lib/types/repairMatching";
import { cn } from "@/lib/utils";

interface RepairShopComparisonProps {
  comparison: RepairMarketplaceComparison | null;
  onRemoveShop?: (shopId: string) => void;
  onRequestQuote: (shop: RepairShopMatchResponse) => void;
}

export default function RepairShopComparison({
  comparison,
  onRemoveShop,
  onRequestQuote,
}: RepairShopComparisonProps) {
  if (!comparison || comparison.shops.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-white/[0.03] p-12 text-center backdrop-blur-xl">
        <HelpCircle className="mx-auto size-12 text-white/30" />
        <h3 className="mt-4 text-lg font-bold text-white">No Shops Selected for Comparison</h3>
        <p className="mt-1 text-sm text-white/60">
          Select 2 or more repair providers from the smart match list above to compare side-by-side.
        </p>
      </div>
    );
  }

  const {
    shops,
    metrics,
    bestOverallShopId,
    bestValueShopId,
    fastestShopId,
    mostTrustedShopId,
    mostSustainableShopId,
    comparisonSummary,
  } = comparison;

  return (
    <div className="space-y-6">
      {/* Comparison Summary Banner */}
      <div className="rounded-2xl border border-cyan-500/30 bg-gradient-to-r from-cyan-500/15 via-[#0B1120] to-[#22C55E]/15 p-5 backdrop-blur-xl">
        <div className="flex items-start gap-3">
          <Award className="mt-0.5 size-5 shrink-0 text-[#06B6D4]" />
          <div>
            <h4 className="text-sm font-bold uppercase tracking-wider text-[#06B6D4]">
              Multi-Shop Intelligent Analysis
            </h4>
            <p className="mt-1 text-sm text-white/90">{comparisonSummary}</p>
          </div>
        </div>
      </div>

      {/* Comparison Matrix Table */}
      <div className="overflow-x-auto rounded-3xl border border-white/10 bg-[#0B1120]/80 shadow-2xl backdrop-blur-2xl">
        <table className="w-full min-w-[650px] border-collapse text-left">
          <thead>
            <tr className="border-b border-white/10 bg-white/[0.03]">
              <th className="p-5 text-xs font-bold uppercase tracking-wider text-white/50 w-1/4">
                Comparison Metric
              </th>
              {shops.map((shop) => {
                const isBestOverall = shop.shopId === bestOverallShopId;
                const isBestValue = shop.shopId === bestValueShopId;
                const isFastest = shop.shopId === fastestShopId;
                const isMostTrusted = shop.shopId === mostTrustedShopId;
                const isMostSustainable = shop.shopId === mostSustainableShopId;

                return (
                  <th key={shop.shopId} className="p-5 text-left align-top">
                    <div className="space-y-2">
                      <div className="flex items-start justify-between gap-2">
                        <div>
                          <h4 className="text-base font-extrabold text-white">{shop.shopName}</h4>
                          <p className="text-xs text-white/50">{shop.address}</p>
                        </div>
                        {onRemoveShop && shops.length > 2 && (
                          <button
                            onClick={() => onRemoveShop(shop.shopId)}
                            className="rounded-lg p-1 text-white/30 hover:bg-white/10 hover:text-red-400 transition-colors"
                            title="Remove from comparison"
                          >
                            <Trash2 className="size-3.5" />
                          </button>
                        )}
                      </div>

                      {/* Highlight Badges */}
                      <div className="flex flex-wrap gap-1">
                        {isBestOverall && (
                          <span className="flex items-center gap-1 rounded-full border border-[#22C55E]/40 bg-[#22C55E]/15 px-2 py-0.5 text-[10px] font-bold text-[#22C55E]">
                            <Award className="size-3" /> Best Overall
                          </span>
                        )}
                        {isBestValue && (
                          <span className="flex items-center gap-1 rounded-full border border-emerald-500/40 bg-emerald-500/15 px-2 py-0.5 text-[10px] font-bold text-emerald-400">
                            <DollarSign className="size-3" /> Best Value
                          </span>
                        )}
                        {isFastest && (
                          <span className="flex items-center gap-1 rounded-full border border-amber-500/40 bg-amber-500/15 px-2 py-0.5 text-[10px] font-bold text-amber-400">
                            <Zap className="size-3" /> Fastest
                          </span>
                        )}
                        {isMostTrusted && (
                          <span className="flex items-center gap-1 rounded-full border border-blue-500/40 bg-blue-500/15 px-2 py-0.5 text-[10px] font-bold text-blue-400">
                            <ShieldCheck className="size-3" /> Most Trusted
                          </span>
                        )}
                        {isMostSustainable && (
                          <span className="flex items-center gap-1 rounded-full border border-[#22C55E]/40 bg-[#22C55E]/15 px-2 py-0.5 text-[10px] font-bold text-[#22C55E]">
                            <Leaf className="size-3" /> Eco Partner
                          </span>
                        )}
                      </div>

                      <button
                        onClick={() => onRequestQuote(shop)}
                        className="mt-2 w-full rounded-xl bg-gradient-to-r from-[#22C55E] to-[#06B6D4] px-3 py-2 text-xs font-bold text-white transition-all hover:opacity-95 active:scale-[0.98]"
                      >
                        Request Quote
                      </button>
                    </div>
                  </th>
                );
              })}
            </tr>
          </thead>

          <tbody className="divide-y divide-white/5 text-sm">
            {metrics.map((metric) => (
              <tr key={metric.metricKey} className="hover:bg-white/[0.02] transition-colors">
                <td className="p-5 align-middle">
                  <span className="font-bold text-white block">{metric.metricName}</span>
                  <span className="text-xs text-white/40 block mt-0.5">{metric.description}</span>
                </td>

                {shops.map((shop) => {
                  const isWinner = shop.shopId === metric.winnerShopId;
                  const val = metric.shopValues[shop.shopId] || "—";

                  return (
                    <td
                      key={shop.shopId}
                      className={cn(
                        "p-5 align-middle font-medium transition-colors",
                        isWinner ? "bg-[#22C55E]/[0.06] text-white" : "text-white/80"
                      )}
                    >
                      <div className="flex items-center gap-2">
                        {isWinner && (
                          <span className="flex size-5 items-center justify-center rounded-full bg-[#22C55E]/20 text-[#22C55E]">
                            <Check className="size-3" />
                          </span>
                        )}
                        <span className={cn(isWinner ? "font-bold text-[#22C55E]" : "")}>
                          {val}
                        </span>
                      </div>
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
