"use client";

import { motion } from "framer-motion";
import { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type MetricCardProps = {
  icon: LucideIcon;
  label: string;
  value: string;
  subtext?: string;
  accentColor?: "green" | "cyan" | "yellow" | "red";
  badgeText?: string;
  delay?: number;
  className?: string;
};

const ACCENT_STYLES = {
  green: {
    icon: "from-[#22C55E] to-[#16A34A]",
    badge: "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]",
    glow: "rgba(34,197,94,0.2)",
  },
  cyan: {
    icon: "from-[#06B6D4] to-[#0891B2]",
    badge: "border-[#06B6D4]/30 bg-[#06B6D4]/10 text-[#06B6D4]",
    glow: "rgba(6,182,212,0.2)",
  },
  yellow: {
    icon: "from-[#FACC15] to-[#D97706]",
    badge: "border-[#FACC15]/30 bg-[#FACC15]/10 text-[#FACC15]",
    glow: "rgba(250,204,21,0.18)",
  },
  red: {
    icon: "from-[#EF4444] to-[#DC2626]",
    badge: "border-red-500/30 bg-red-500/10 text-red-400",
    glow: "rgba(239,68,68,0.18)",
  },
};

export default function MetricCard({
  icon: Icon,
  label,
  value,
  subtext,
  accentColor = "green",
  badgeText = "Live",
  delay = 0,
  className,
}: MetricCardProps) {
  const accent = ACCENT_STYLES[accentColor];

  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay, ease: EASE }}
      className={cn("group relative h-full", className)}
    >
      {/* Hover glow */}
      <div
        className="pointer-events-none absolute -inset-px rounded-3xl opacity-0 blur-xl transition-opacity duration-500 group-hover:opacity-100"
        style={{
          background: `radial-gradient(circle at 50% 0%, ${accent.glow}, transparent 70%)`,
        }}
        aria-hidden
      />

      {/* Card surface */}
      <div className="relative flex h-full flex-col rounded-3xl border border-white/10 bg-white/[0.06] p-6 shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.12)] backdrop-blur-xl transition-[border-color,box-shadow,background-color] duration-300 group-hover:border-white/20 group-hover:bg-white/[0.08]">
        {/* Top row: icon + badge */}
        <div className="flex items-center justify-between mb-5">
          <div
            className={cn(
              "flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br shadow-[0_8px_24px_rgba(34,197,94,0.2)]",
              accent.icon
            )}
          >
            <Icon className="size-6 text-white" aria-hidden />
          </div>
          <span
            className={cn(
              "rounded-full border px-3 py-1 text-[11px] font-semibold",
              accent.badge
            )}
          >
            {badgeText}
          </span>
        </div>

        {/* Value */}
        <p className="text-3xl font-bold tracking-tight text-white sm:text-4xl">
          {value}
        </p>

        {/* Label */}
        <h3 className="mt-1.5 text-sm font-semibold text-white/90 sm:text-base">
          {label}
        </h3>

        {/* Sub-text */}
        {subtext && (
          <p className="mt-1 text-xs leading-relaxed text-[#CBD5E1]">{subtext}</p>
        )}
      </div>
    </motion.div>
  );
}
