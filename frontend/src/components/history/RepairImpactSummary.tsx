"use client";

import { Leaf, Award, ArrowRight } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";

type RepairImpactSummaryProps = {
  co2SavedKg?: number;
  ewasteReducedKg?: number;
  moneySaved?: number;
};

export default function RepairImpactSummary({
  co2SavedKg = 0,
  ewasteReducedKg = 0,
  moneySaved = 0,
}: RepairImpactSummaryProps) {
  return (
    <GlassCard className="p-6">
      <div className="flex items-center gap-2 border-b border-white/10 pb-4 mb-4">
        <Leaf className="size-5 text-[#22C55E]" />
        <h3 className="text-base font-bold text-white">
          Environmental Impact Contribution
        </h3>
      </div>

      <div className="grid grid-cols-2 gap-3 text-xs mb-4">
        <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-3.5 text-center">
          <span className="text-white/60 block text-[10px] uppercase tracking-wider mb-1">
            CO₂ Avoided
          </span>
          <strong className="text-[#22C55E] text-lg font-extrabold block">
            {co2SavedKg} kg
          </strong>
        </div>

        <div className="rounded-2xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 p-3.5 text-center">
          <span className="text-white/60 block text-[10px] uppercase tracking-wider mb-1">
            E-Waste Diverted
          </span>
          <strong className="text-[#06B6D4] text-lg font-extrabold block">
            {ewasteReducedKg} kg
          </strong>
        </div>
      </div>

      <p className="text-xs text-[#CBD5E1] mb-4 leading-relaxed">
        By opting to repair this device instead of replacing it, you prevented raw material extraction and reduced industrial electronic e-waste.
      </p>

      <GlassButton
        href="/carbon"
        variant="secondary"
        size="sm"
        fullWidth
        icon={<ArrowRight className="size-3.5" />}
      >
        View Complete Carbon Dashboard
      </GlassButton>
    </GlassCard>
  );
}
