"use client";

import { motion } from "framer-motion";
import { Award, Info } from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

/**
 * Sustainability Score component.
 *
 * Score basis (documented in DATABASE_SCHEMA.md CarbonImpact fields):
 *   - repair_count: number of repair actions
 *   - co2_saved: cumulative CO₂ avoided
 *   - ewaste_reduced: cumulative e-waste diverted
 *   - money_saved: cumulative savings
 *
 * Scoring formula (frontend estimate — final formula must be confirmed by backend):
 *   score = clamp(
 *     (repair_count * 10) + (co2_saved * 0.5) + (ewaste_reduced * 5),
 *     0, 100
 *   )
 *
 * NOTE: This formula is a frontend placeholder. The authoritative score will
 * come from the backend CarbonService once implemented.
 */

const SCORE_LEVELS = [
  { min: 0, max: 24, label: "Eco Starter", color: "from-white/30 to-white/20", textColor: "text-white/60" },
  { min: 25, max: 49, label: "Green Learner", color: "from-[#06B6D4] to-[#0891B2]", textColor: "text-[#06B6D4]" },
  { min: 50, max: 74, label: "Repair Champion", color: "from-[#22C55E] to-[#16A34A]", textColor: "text-[#22C55E]" },
  { min: 75, max: 100, label: "Sustainability Hero", color: "from-[#FACC15] to-[#22C55E]", textColor: "text-[#FACC15]" },
];

function getLevel(score: number) {
  return SCORE_LEVELS.find((l) => score >= l.min && score <= l.max) ?? SCORE_LEVELS[0];
}

export default function SustainabilityScore({ score }: { score: number }) {
  const clamped = Math.min(100, Math.max(0, score));
  const level = getLevel(clamped);
  const circumference = 2 * Math.PI * 54; // r=54
  const dashOffset = circumference * (1 - clamped / 100);

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.96 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.5, ease: EASE }}
      className="flex flex-col items-center text-center"
    >
      {/* Circular progress ring */}
      <div className="relative size-40">
        <svg className="size-full -rotate-90" viewBox="0 0 120 120" aria-hidden>
          {/* Track */}
          <circle cx="60" cy="60" r="54" fill="none" stroke="rgba(255,255,255,0.08)" strokeWidth="10" />
          {/* Progress */}
          <circle
            cx="60"
            cy="60"
            r="54"
            fill="none"
            stroke="url(#scoreGrad)"
            strokeWidth="10"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={dashOffset}
            style={{ transition: "stroke-dashoffset 1.2s ease" }}
          />
          <defs>
            <linearGradient id="scoreGrad" x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%" stopColor="#22C55E" />
              <stop offset="100%" stopColor="#06B6D4" />
            </linearGradient>
          </defs>
        </svg>
        {/* Center text */}
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-3xl font-black text-white">{clamped}</span>
          <span className="text-xs font-semibold text-white/60">/100</span>
        </div>
      </div>

      {/* Level badge */}
      <div className={cn("mt-4 flex items-center gap-2", level.textColor)}>
        <Award className="size-4" />
        <span className="text-sm font-bold">{level.label}</span>
      </div>

      {/* Progress breakdown */}
      <div className="mt-4 w-full rounded-2xl border border-white/10 bg-white/[0.04] p-4 text-left space-y-2.5">
        <div className="flex items-center gap-1.5 text-xs text-white/50">
          <Info className="size-3.5" />
          <span className="font-semibold">How this is calculated</span>
        </div>
        <ul className="space-y-1 text-xs text-[#CBD5E1]">
          <li className="flex justify-between">
            <span>Repair count contribution</span>
            <span className="font-semibold text-white">×10 pts each</span>
          </li>
          <li className="flex justify-between">
            <span>CO₂ avoided</span>
            <span className="font-semibold text-white">×0.5 pts/kg</span>
          </li>
          <li className="flex justify-between">
            <span>E-waste reduced</span>
            <span className="font-semibold text-white">×5 pts/kg</span>
          </li>
        </ul>
        <p className="text-[10px] text-white/30 leading-relaxed">
          ⚠ Score formula is a frontend estimate pending backend confirmation.
        </p>
      </div>
    </motion.div>
  );
}
