"use client";

import { motion } from "framer-motion";
import { Sparkles, ArrowRight, ShieldAlert, Zap, Wrench, RefreshCw, HeartHandshake, Trash2, IndianRupee, Cloud } from "lucide-react";
import type { SustainabilityRecommendation, RecommendationPriority, SustainabilityActionType } from "@/lib/types/circularEconomy";
import Link from "next/link";

interface SustainabilityRecommendationsProps {
  recommendations: SustainabilityRecommendation[];
}

const PRIORITY_THEMES: Record<
  RecommendationPriority,
  { badge: string; text: string; border: string; glow: string }
> = {
  CRITICAL: {
    badge: "bg-rose-500/15 text-rose-400 border-rose-500/30",
    text: "text-rose-400",
    border: "border-rose-500/30",
    glow: "rgba(244, 63, 94, 0.15)",
  },
  HIGH: {
    badge: "bg-amber-500/15 text-amber-400 border-amber-500/30",
    text: "text-amber-400",
    border: "border-amber-500/30",
    glow: "rgba(245, 158, 11, 0.15)",
  },
  MEDIUM: {
    badge: "bg-cyan-500/15 text-cyan-400 border-cyan-500/30",
    text: "text-cyan-400",
    border: "border-cyan-500/30",
    glow: "rgba(6, 182, 212, 0.15)",
  },
  LOW: {
    badge: "bg-emerald-500/15 text-emerald-400 border-emerald-500/30",
    text: "text-emerald-400",
    border: "border-emerald-500/30",
    glow: "rgba(34, 197, 94, 0.15)",
  },
};

const ACTION_ICONS: Record<SustainabilityActionType | string, any> = {
  REPAIR_NOW: Wrench,
  SCHEDULE_MAINTENANCE: RefreshCw,
  EXTEND_DEVICE_LIFE: Zap,
  UPGRADE_COMPONENT: Wrench,
  REFURBISH_DEVICE: RefreshCw,
  DONATE_DEVICE: HeartHandshake,
  RECYCLE_RESPONSIBLY: Trash2,
  MONITOR_DEVICE: ShieldAlert,
};

function getActionLink(action: SustainabilityActionType | string, deviceId?: string | null) {
  if (action === "REPAIR_NOW" || action === "SCHEDULE_MAINTENANCE") {
    return deviceId ? `/devices/${deviceId}` : "/diagnosis";
  }
  if (action === "REFURBISH_DEVICE" || action === "DONATE_DEVICE" || action === "RECYCLE_RESPONSIBLY") {
    return "/repair-shops";
  }
  return deviceId ? `/devices/${deviceId}` : "/devices";
}

export default function SustainabilityRecommendations({
  recommendations,
}: SustainabilityRecommendationsProps) {
  if (!recommendations || recommendations.length === 0) {
    return null;
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl md:text-2xl font-bold text-white flex items-center gap-2.5">
            <Sparkles className="size-6 text-cyan-400" />
            Personalized Optimization Directives
          </h2>
          <p className="text-xs md:text-sm text-slate-400 mt-1">
            Deterministic AI actions designed to maximize hardware longevity, mitigate carbon emissions, and prevent e-waste.
          </p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {recommendations.map((rec, idx) => {
          const priorityStyle = PRIORITY_THEMES[rec.priority] || PRIORITY_THEMES.MEDIUM;
          const Icon = ACTION_ICONS[rec.actionType] || Zap;
          const targetUrl = getActionLink(rec.actionType, rec.deviceId);

          return (
            <motion.div
              key={rec.id || idx}
              initial={{ opacity: 0, y: 15 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.08 }}
              className={`relative overflow-hidden rounded-3xl border ${priorityStyle.border} bg-[#0B1120]/85 p-6 backdrop-blur-xl shadow-xl flex flex-col justify-between hover:border-cyan-500/40 transition-all group`}
            >
              <div className="space-y-3">
                {/* Header Badge Row */}
                <div className="flex items-center justify-between gap-2">
                  <span
                    className={`inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-[11px] font-bold ${priorityStyle.badge}`}
                  >
                    {rec.priority} PRIORITY
                  </span>
                  <span className="text-xs font-semibold text-slate-400 truncate max-w-[130px]">
                    {rec.deviceName || "Hardware Fleet"}
                  </span>
                </div>

                {/* Title */}
                <h3 className="text-base font-bold text-white group-hover:text-cyan-300 transition-colors flex items-start gap-2">
                  <div className="rounded-lg bg-white/5 p-1.5 text-cyan-400 shrink-0 mt-0.5">
                    <Icon className="size-4" />
                  </div>
                  <span>{rec.title}</span>
                </h3>

                {/* Description */}
                <p className="text-xs text-slate-300 leading-relaxed">{rec.description}</p>

                {/* Explainable Reason */}
                <div className="rounded-xl border border-white/5 bg-white/[0.02] p-3 text-[11px] text-slate-400">
                  <span className="font-semibold text-slate-300">Deterministic Rationale: </span>
                  {rec.reason}
                </div>
              </div>

              {/* Footer Benefit Badges & CTA */}
              <div className="mt-5 pt-4 border-t border-white/10 space-y-3">
                <div className="flex flex-wrap items-center justify-between text-xs gap-2">
                  <div className="flex items-center gap-1 font-semibold text-emerald-400">
                    <Cloud className="size-3.5" />
                    <span>+{rec.estimatedCarbonImpact}kg CO₂</span>
                  </div>
                  <div className="flex items-center gap-1 font-semibold text-amber-300">
                    <IndianRupee className="size-3.5" />
                    <span>₹{rec.estimatedMoneySavings.toLocaleString("en-IN")}</span>
                  </div>
                </div>

                <Link
                  href={targetUrl}
                  className="flex items-center justify-center gap-2 w-full rounded-xl bg-gradient-to-r from-cyan-600/80 to-blue-600/80 hover:from-cyan-500 hover:to-blue-500 py-2.5 text-xs font-bold text-white shadow-md active:scale-95 transition-all"
                >
                  <span>Execute Directive</span>
                  <ArrowRight className="size-3.5 group-hover:translate-x-0.5 transition-transform" />
                </Link>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
