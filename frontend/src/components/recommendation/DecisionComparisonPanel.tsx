"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Scale, Leaf, DollarSign, ArrowRight, ShieldCheck } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { RepairVsReplaceDecision } from "@/lib/types/recommendation";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function DecisionComparisonPanel({
  decision,
}: {
  decision: RepairVsReplaceDecision;
}) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45, ease: EASE }}
    >
      <GlassCard padding="lg" hoverEffect={false}>
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center gap-2">
            <Scale className="size-5 text-[#22C55E]" />
            <h2 className="text-lg font-bold text-white">Repair vs Replace Decision Matrix</h2>
          </div>
          <Link
            href="/carbon"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-[#06B6D4] transition-colors hover:text-[#22C55E]"
          >
            View Carbon Impact <ArrowRight className="size-3.5" />
          </Link>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-12">
          {/* Repair Score Bar */}
          <div className="md:col-span-6 rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/[0.06] p-5">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold uppercase tracking-wider text-[#22C55E]">
                Repair Feasibility Score
              </span>
              <span className="text-xl font-black text-[#22C55E]">
                {decision.repairScore}/100
              </span>
            </div>
            <div className="h-3 w-full overflow-hidden rounded-full bg-white/[0.08]">
              <div
                className="h-full rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
                style={{ width: `${decision.repairScore}%` }}
              />
            </div>
            <p className="mt-2 text-[11px] text-[#CBD5E1]">
              Higher score indicates economical repair, high component availability, and low safety risk.
            </p>
          </div>

          {/* Replace Score Bar */}
          <div className="md:col-span-6 rounded-2xl border border-red-500/20 bg-red-500/[0.04] p-5">
            <div className="flex items-center justify-between mb-2">
              <span className="text-xs font-bold uppercase tracking-wider text-red-400">
                Replacement Urgency Score
              </span>
              <span className="text-xl font-black text-red-400">
                {decision.replaceScore}/100
              </span>
            </div>
            <div className="h-3 w-full overflow-hidden rounded-full bg-white/[0.08]">
              <div
                className="h-full rounded-full bg-gradient-to-r from-red-500 to-amber-500"
                style={{ width: `${decision.replaceScore}%` }}
              />
            </div>
            <p className="mt-2 text-[11px] text-[#CBD5E1]">
              Higher score indicates device nearing end-of-life or unrepairable board damage.
            </p>
          </div>
        </div>

        {/* Benefits Chips Row */}
        <div className="mt-6 grid grid-cols-1 gap-3 sm:grid-cols-2">
          <div className="flex items-center gap-3 rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-3.5">
            <div className="flex size-9 items-center justify-center rounded-xl bg-[#22C55E]/20 text-[#22C55E]">
              <DollarSign className="size-5" />
            </div>
            <div>
              <span className="text-[10px] uppercase font-bold text-[#CBD5E1]">
                Estimated Cost Savings
              </span>
              <p className="text-base font-extrabold text-white">
                Save ${decision.moneySaved.toLocaleString()} vs buying new
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 rounded-2xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 p-3.5">
            <div className="flex size-9 items-center justify-center rounded-xl bg-[#06B6D4]/20 text-[#06B6D4]">
              <Leaf className="size-5" />
            </div>
            <div>
              <span className="text-[10px] uppercase font-bold text-[#CBD5E1]">
                Carbon Footprint Prevented
              </span>
              <p className="text-base font-extrabold text-white">
                {decision.carbonSaved} kg CO₂ emissions avoided
              </p>
            </div>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
