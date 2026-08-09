"use client";

import { motion } from "framer-motion";
import { Wrench, RefreshCw, Leaf, DollarSign, Clock } from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

/**
 * Repair vs Replace Environmental Comparison
 *
 * Industry-standard estimates used (no project-specific formulas defined in docs):
 *
 * Smartphone:
 *  - Manufacturing CO₂: ~70 kg per new device (source: iFixit / Ellen MacArthur Foundation)
 *  - Repair CO₂ cost:   ~4 kg average per repair event
 *  - E-waste (new device): ~0.8 kg of hazardous materials landfill
 *  - E-waste (repair):     ~0.05 kg of parts replaced
 *
 * Laptop:
 *  - Manufacturing CO₂: ~330 kg per new device
 *  - Repair CO₂ cost:   ~8 kg average
 *
 * NOTE: These estimates will be replaced by backend-calculated values
 * from AIRecommendations.carbon_saved once the API is available.
 */
const COMPARISON_DATA = [
  {
    category: "Smartphone Screen",
    repair: {
      co2: 4.2,
      ewaste: 0.05,
      cost: 85,
      time: "1-2 hours",
    },
    replace: {
      co2: 70,
      ewaste: 0.8,
      cost: 750,
      time: "Same day",
    },
  },
  {
    category: "Laptop Battery",
    repair: {
      co2: 3.5,
      ewaste: 0.12,
      cost: 60,
      time: "30 minutes",
    },
    replace: {
      co2: 330,
      ewaste: 2.4,
      cost: 1200,
      time: "2-3 days",
    },
  },
];

export default function RepairVsReplacePanel() {
  return (
    <div className="space-y-6">
      <p className="text-xs font-semibold uppercase tracking-widest text-[#CBD5E1]">
        Environmental comparison — typical repair vs full replacement
      </p>

      {COMPARISON_DATA.map((item, idx) => (
        <motion.div
          key={item.category}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: idx * 0.15, ease: EASE }}
          className="rounded-3xl border border-white/10 bg-white/[0.04] p-5 sm:p-6"
        >
          <h3 className="mb-4 text-sm font-bold text-white sm:text-base">
            {item.category}
          </h3>

          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            {/* Repair Column */}
            <ComparisonColumn
              title="Repair"
              icon={Wrench}
              iconColor="text-[#22C55E]"
              borderColor="border-[#22C55E]/30"
              bgColor="bg-[#22C55E]/[0.06]"
              badgeColor="border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
              co2={item.repair.co2}
              ewaste={item.repair.ewaste}
              cost={item.repair.cost}
              time={item.repair.time}
            />

            {/* Replace Column */}
            <ComparisonColumn
              title="Replace"
              icon={RefreshCw}
              iconColor="text-red-400"
              borderColor="border-red-500/20"
              bgColor="bg-red-500/[0.04]"
              badgeColor="border-red-500/30 bg-red-500/10 text-red-400"
              co2={item.replace.co2}
              ewaste={item.replace.ewaste}
              cost={item.replace.cost}
              time={item.replace.time}
            />
          </div>

          {/* Savings summary bar */}
          <div className="mt-4 flex flex-wrap items-center gap-3 rounded-2xl border border-[#22C55E]/20 bg-[#22C55E]/[0.06] p-3">
            <Leaf className="size-4 shrink-0 text-[#22C55E]" aria-hidden />
            <span className="text-xs font-semibold text-[#22C55E]">
              Repairing saves{" "}
              <span className="font-black">
                {(item.replace.co2 - item.repair.co2).toFixed(1)} kg CO₂
              </span>{" "}
              and{" "}
              <span className="font-black">
                ${(item.replace.cost - item.repair.cost).toLocaleString()}
              </span>{" "}
              per event
            </span>
          </div>
        </motion.div>
      ))}

      {/* Disclosure */}
      <p className="text-[10px] leading-relaxed text-white/30">
        * CO₂ estimates based on industry lifecycle assessments (Ellen MacArthur Foundation, iFixit).
        Actual values will be calculated by the AI analysis engine once backend is connected.
      </p>
    </div>
  );
}

function ComparisonColumn({
  title,
  icon: Icon,
  iconColor,
  borderColor,
  bgColor,
  badgeColor,
  co2,
  ewaste,
  cost,
  time,
}: {
  title: string;
  icon: typeof Wrench;
  iconColor: string;
  borderColor: string;
  bgColor: string;
  badgeColor: string;
  co2: number;
  ewaste: number;
  cost: number;
  time: string;
}) {
  return (
    <div
      className={cn(
        "rounded-2xl border p-4",
        borderColor,
        bgColor
      )}
    >
      <div className="mb-3 flex items-center gap-2">
        <Icon className={cn("size-4", iconColor)} aria-hidden />
        <span className={cn("rounded-full border px-2.5 py-0.5 text-[11px] font-bold", badgeColor)}>
          {title}
        </span>
      </div>

      <ul className="space-y-2 text-xs text-[#CBD5E1]">
        <li className="flex items-center justify-between">
          <span className="flex items-center gap-1.5">
            <Leaf className="size-3 text-current opacity-60" /> CO₂ emitted
          </span>
          <span className="font-semibold text-white">{co2} kg</span>
        </li>
        <li className="flex items-center justify-between">
          <span>E-waste generated</span>
          <span className="font-semibold text-white">{ewaste} kg</span>
        </li>
        <li className="flex items-center justify-between">
          <span className="flex items-center gap-1.5">
            <DollarSign className="size-3 text-current opacity-60" /> Est. cost
          </span>
          <span className="font-semibold text-white">${cost}</span>
        </li>
        <li className="flex items-center justify-between">
          <span className="flex items-center gap-1.5">
            <Clock className="size-3 text-current opacity-60" /> Time
          </span>
          <span className="font-semibold text-white">{time}</span>
        </li>
      </ul>
    </div>
  );
}
