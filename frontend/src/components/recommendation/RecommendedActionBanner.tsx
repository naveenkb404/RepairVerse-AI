"use client";

import { motion } from "framer-motion";
import { CheckCircle2, ShieldAlert, Sparkles, Wrench, RefreshCw, Eye, UserCheck } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { RecommendedAction } from "@/lib/types/recommendation";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

const ACTION_CONFIG = {
  REPAIR: {
    title: "Recommended Action: Self / DIY Repair",
    icon: Wrench,
    badge: "High Repairability Index",
    gradient: "from-[#22C55E] to-[#06B6D4]",
    accentClass: "text-[#22C55E] border-[#22C55E]/30 bg-[#22C55E]/10",
  },
  MONITOR: {
    title: "Recommended Action: Monitor & Maintenance",
    icon: Eye,
    badge: "Minor Wear Signature",
    gradient: "from-[#06B6D4] to-[#0891B2]",
    accentClass: "text-[#06B6D4] border-[#06B6D4]/30 bg-[#06B6D4]/10",
  },
  REPLACE: {
    title: "Recommended Action: Consider Replacement / Recycling",
    icon: RefreshCw,
    badge: "Severe Structural Damage",
    gradient: "from-red-500 to-amber-500",
    accentClass: "text-red-400 border-red-500/30 bg-red-500/10",
  },
  PROFESSIONAL_SERVICE: {
    title: "Recommended Action: Professional Service Center",
    icon: UserCheck,
    badge: "Precision Component Work Required",
    gradient: "from-[#FACC15] to-[#D97706]",
    accentClass: "text-[#FACC15] border-[#FACC15]/30 bg-[#FACC15]/10",
  },
};

export default function RecommendedActionBanner({
  action,
  rationale,
  safetyWarning,
}: {
  action: RecommendedAction;
  rationale: string;
  safetyWarning?: string;
}) {
  const config = ACTION_CONFIG[action] || ACTION_CONFIG.REPAIR;
  const Icon = config.icon;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, ease: EASE }}
    >
      <GlassCard padding="lg" glowColor="green" hoverEffect={false}>
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between mb-4">
          <div className="flex items-center gap-3">
            <div
              className={cn(
                "flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br text-white shadow-[0_8px_24px_rgba(34,197,94,0.25)]",
                config.gradient
              )}
            >
              <Icon className="size-6" />
            </div>
            <div>
              <span className="text-[10px] font-bold uppercase tracking-wider text-[#06B6D4] flex items-center gap-1">
                <Sparkles className="size-3" /> AI Decision Outcome
              </span>
              <h1 className="text-xl font-bold text-white sm:text-2xl">{config.title}</h1>
            </div>
          </div>

          <span
            className={cn(
              "self-start sm:self-auto rounded-full border px-3.5 py-1 text-xs font-bold",
              config.accentClass
            )}
          >
            {config.badge}
          </span>
        </div>

        {/* Rationale */}
        <p className="text-sm leading-relaxed text-[#CBD5E1] bg-white/[0.03] rounded-2xl p-4 border border-white/10">
          {rationale}
        </p>

        {/* Prominent Safety Warning if present */}
        {safetyWarning && (
          <div className="mt-4 flex items-start gap-3 rounded-2xl border border-amber-500/40 bg-amber-500/10 p-4 text-xs text-amber-200">
            <ShieldAlert className="size-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <p className="font-bold text-amber-300">Crucial Safety Protocol</p>
              <p className="mt-0.5 leading-relaxed">{safetyWarning}</p>
            </div>
          </div>
        )}
      </GlassCard>
    </motion.div>
  );
}
