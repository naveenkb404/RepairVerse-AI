"use client";

import { DollarSign, ShieldCheck, TrendingDown } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";

type RepairCostBreakdownProps = {
  partsCost: number;
  laborCost: number;
  totalCost: number;
  moneySaved?: number;
};

export default function RepairCostBreakdown({
  partsCost,
  laborCost,
  totalCost,
  moneySaved,
}: RepairCostBreakdownProps) {
  return (
    <GlassCard className="p-6">
      <div className="flex items-center gap-2 border-b border-white/10 pb-4 mb-4">
        <DollarSign className="size-5 text-[#22C55E]" />
        <h3 className="text-base font-bold text-white">Repair Cost Financial Summary</h3>
      </div>

      <div className="space-y-3 text-xs">
        {/* Parts Cost */}
        <div className="flex justify-between items-center text-white/70 py-1.5 border-b border-white/[0.06]">
          <span>Replacement Parts Cost</span>
          <span className="font-semibold text-white">${partsCost}</span>
        </div>

        {/* Labor Cost */}
        <div className="flex justify-between items-center text-white/70 py-1.5 border-b border-white/[0.06]">
          <span>Technician Service & Labor</span>
          <span className="font-semibold text-white">${laborCost}</span>
        </div>

        {/* Total Cost */}
        <div className="flex justify-between items-center text-sm py-2 font-bold text-white">
          <span className="flex items-center gap-1.5">
            <ShieldCheck className="size-4 text-[#22C55E]" /> Total Service Cost
          </span>
          <span className="text-[#22C55E] text-base">${totalCost}</span>
        </div>

        {/* Money Saved Callout */}
        {moneySaved != null && moneySaved > 0 && (
          <div className="mt-4 flex items-center justify-between rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-3.5 text-xs text-[#22C55E]">
            <span className="flex items-center gap-1.5 font-semibold">
              <TrendingDown className="size-4" /> Net Money Saved vs Replacement:
            </span>
            <strong className="text-sm font-extrabold">${moneySaved}</strong>
          </div>
        )}
      </div>
    </GlassCard>
  );
}
