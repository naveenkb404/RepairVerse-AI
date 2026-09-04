"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Cpu,
  Sparkles,
  RefreshCw,
  ShieldCheck,
  Zap,
  ArrowRight,
  TrendingUp,
  AlertTriangle,
  CheckCircle2,
  Wrench,
  RotateCcw,
} from "lucide-react";
import type { DeviceIntelligenceResponse, RecommendedAction } from "@/lib/types/deviceIntelligence";
import Link from "next/link";

interface DeviceIntelligenceHeroProps {
  data: DeviceIntelligenceResponse;
  onReevaluate: () => void;
  isEvaluating?: boolean;
}

export const DeviceIntelligenceHero: React.FC<DeviceIntelligenceHeroProps> = ({
  data,
  onReevaluate,
  isEvaluating = false,
}) => {
  const getActionConfig = (action: RecommendedAction) => {
    switch (action) {
      case "PROFESSIONAL_SERVICE":
        return {
          label: "Professional Service Required",
          color: "from-rose-500/20 to-red-500/20 border-rose-500/40 text-rose-400",
          icon: AlertTriangle,
          badgeColor: "bg-rose-500/20 text-rose-300 border-rose-500/30",
        };
      case "REPAIR_NOW":
        return {
          label: "Repair Recommended",
          color: "from-amber-500/20 to-orange-500/20 border-amber-500/40 text-amber-400",
          icon: Wrench,
          badgeColor: "bg-amber-500/20 text-amber-300 border-amber-500/30",
        };
      case "MAINTENANCE_REQUIRED":
        return {
          label: "Maintenance Overdue",
          color: "from-cyan-500/20 to-blue-500/20 border-cyan-500/40 text-cyan-400",
          icon: RefreshCw,
          badgeColor: "bg-cyan-500/20 text-cyan-300 border-cyan-500/30",
        };
      case "REFURBISH":
        return {
          label: "Refurbishment & Upgrade",
          color: "from-purple-500/20 to-indigo-500/20 border-purple-500/40 text-purple-400",
          icon: RotateCcw,
          badgeColor: "bg-purple-500/20 text-purple-300 border-purple-500/30",
        };
      case "REPLACE":
        return {
          label: "Plan Device Replacement",
          color: "from-orange-500/20 to-rose-500/20 border-orange-500/40 text-orange-400",
          icon: AlertTriangle,
          badgeColor: "bg-orange-500/20 text-orange-300 border-orange-500/30",
        };
      case "RECYCLE":
        return {
          label: "Responsible E-Waste Recycling",
          color: "from-emerald-500/20 to-teal-500/20 border-emerald-500/40 text-emerald-400",
          icon: RotateCcw,
          badgeColor: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30",
        };
      case "MONITOR":
        return {
          label: "Monitor Telemetry",
          color: "from-blue-500/20 to-cyan-500/20 border-blue-500/40 text-blue-400",
          icon: TrendingUp,
          badgeColor: "bg-blue-500/20 text-blue-300 border-blue-500/30",
        };
      default: // CONTINUE_USING
        return {
          label: "Continue Normal Operation",
          color: "from-emerald-500/20 to-teal-500/20 border-emerald-500/40 text-emerald-400",
          icon: CheckCircle2,
          badgeColor: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30",
        };
    }
  };

  const actionConfig = getActionConfig(data.recommendedAction);
  const ActionIcon = actionConfig.icon;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900/90 via-slate-900/60 to-slate-950/90 p-6 md:p-8 backdrop-blur-xl shadow-2xl"
    >
      {/* Background Ambient Glows */}
      <div className="absolute -top-24 -right-24 h-96 w-96 rounded-full bg-emerald-500/10 blur-3xl pointer-events-none" />
      <div className="absolute -bottom-24 -left-24 h-96 w-96 rounded-full bg-cyan-500/10 blur-3xl pointer-events-none" />

      <div className="relative z-10 flex flex-col lg:flex-row items-start lg:items-center justify-between gap-6">
        {/* Left: Device Info & Advisor Pitch */}
        <div className="flex-1 space-y-4">
          <div className="flex flex-wrap items-center gap-3">
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              <Cpu className="w-3.5 h-3.5" />
              Unified Ecosystem Intelligence
            </span>
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium bg-white/5 text-slate-300 border border-white/10">
              {data.brand} {data.model}
            </span>
            <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium bg-white/5 text-slate-400 border border-white/10">
              {data.category}
            </span>
          </div>

          <div>
            <h1 className="text-3xl md:text-4xl font-extrabold tracking-tight text-white flex items-center gap-3">
              {data.deviceName}
            </h1>
            <p className="mt-1 text-sm text-slate-400">
              Multi-signal algorithmic evaluation computed with {data.decisionConfidence}% confidence
            </p>
          </div>

          {/* AI Advisor Explanation Box */}
          <div className="relative rounded-2xl border border-white/10 bg-black/40 p-4 md:p-5 backdrop-blur-md">
            <div className="flex items-start gap-3">
              <div className="p-2 rounded-xl bg-gradient-to-br from-emerald-500/20 to-cyan-500/20 border border-emerald-500/30 text-emerald-400 shrink-0">
                <Sparkles className="w-5 h-5" />
              </div>
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold uppercase tracking-wider text-emerald-400">
                    Personalized Advisor Rationale
                  </span>
                  <span className="text-[11px] text-slate-500">• Evaluated {new Date(data.evaluatedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                </div>
                <p className="text-sm text-slate-300 leading-relaxed">
                  {data.summary}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Right: Authoritative Decision Capsule & Quick Actions */}
        <div className="flex flex-col items-center sm:items-end gap-4 w-full lg:w-auto shrink-0">
          <div className={`w-full sm:w-auto rounded-2xl border p-5 backdrop-blur-xl ${actionConfig.color} flex flex-col items-center sm:items-end text-center sm:text-right space-y-2`}>
            <div className="flex items-center gap-2">
              <ActionIcon className="w-5 h-5 animate-pulse" />
              <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                Authoritative Recommendation
              </span>
            </div>
            <div className="text-xl md:text-2xl font-black tracking-tight text-white">
              {actionConfig.label}
            </div>
            <div className={`px-3 py-0.5 rounded-full text-xs font-semibold border ${actionConfig.badgeColor}`}>
              Decision Confidence: {data.decisionConfidence}%
            </div>
          </div>

          {/* Action CTAs */}
          <div className="flex flex-wrap items-center gap-3 w-full sm:w-auto justify-center sm:justify-end">
            <button
              onClick={onReevaluate}
              disabled={isEvaluating}
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold bg-white/10 hover:bg-white/15 text-white border border-white/15 transition-all shadow-lg hover:shadow-cyan-500/10 disabled:opacity-50"
            >
              <RefreshCw className={`w-4 h-4 ${isEvaluating ? "animate-spin text-cyan-400" : ""}`} />
              {isEvaluating ? "Re-Evaluating..." : "Re-Evaluate"}
            </button>

            <Link
              href={`/devices/${data.deviceId}`}
              className="inline-flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold bg-gradient-to-r from-emerald-500 to-cyan-500 hover:from-emerald-400 hover:to-cyan-400 text-slate-950 transition-all shadow-lg hover:shadow-emerald-500/25 font-bold"
            >
              <ShieldCheck className="w-4 h-4" />
              Digital Passport
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </div>
    </motion.div>
  );
};
