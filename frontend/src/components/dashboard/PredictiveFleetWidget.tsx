"use client";

import React, { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Activity,
  AlertTriangle,
  ArrowRight,
  CheckCircle2,
  ChevronRight,
  Clock,
  Cpu,
  DollarSign,
  Leaf,
  ShieldAlert,
  Sparkles,
  Wrench,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import { fetchUserFleetPredictions, fetchMaintenanceRecommendations } from "@/lib/api/prediction";
import type { DevicePredictionData, MaintenanceRecommendationItem } from "@/lib/types/prediction";

interface PredictiveFleetWidgetProps {
  token?: string | null;
}

const EASE = [0.22, 1, 0.36, 1] as const;

export default function PredictiveFleetWidget({ token }: PredictiveFleetWidgetProps) {
  const [predictions, setPredictions] = useState<DevicePredictionData[]>([]);
  const [recommendations, setRecommendations] = useState<MaintenanceRecommendationItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      setIsLoading(true);
      const [fleetRes, recRes] = await Promise.all([
        fetchUserFleetPredictions(token),
        fetchMaintenanceRecommendations(token),
      ]);
      if (fleetRes.data) setPredictions(fleetRes.data);
      if (recRes.data) setRecommendations(recRes.data);
      setIsLoading(false);
    }
    loadData();
  }, [token]);

  if (isLoading) {
    return (
      <div className="h-48 animate-pulse rounded-3xl bg-white/[0.04] p-6 border border-white/10" />
    );
  }

  if (predictions.length === 0) return null;

  const criticalCount = predictions.filter((p) => p.riskLevel === "CRITICAL").length;
  const highCount = predictions.filter((p) => p.riskLevel === "HIGH").length;
  const totalPreventiveSavings = predictions.reduce((acc, p) => acc + (p.preventiveSavings || 0), 0);
  const totalCo2Savings = predictions.reduce((acc, p) => acc + (p.co2SavingsKg || 0), 0);

  const topAtRisk = predictions.find((p) => p.riskLevel === "CRITICAL" || p.riskLevel === "HIGH");

  return (
    <motion.section
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: 0.25, ease: EASE }}
      className="space-y-4"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="size-5 text-[#22C55E]" />
          <h2 className="text-lg font-bold text-white">Predictive Fleet Intelligence</h2>
        </div>
        <Link
          href="/devices"
          className="text-xs font-semibold text-[#22C55E] hover:text-[#06B6D4] transition-colors flex items-center gap-1"
        >
          View all passports <ArrowRight className="size-3.5" />
        </Link>
      </div>

      {/* Top High-Risk Alert Banner (if applicable) */}
      {topAtRisk && (
        <div className="rounded-2xl border border-orange-500/30 bg-gradient-to-r from-orange-500/15 via-red-500/10 to-transparent p-4 backdrop-blur-xl">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex items-start gap-3">
              <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-orange-500/20 text-orange-400">
                <AlertTriangle className="size-5" />
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <span className="text-xs font-bold text-orange-400">
                    MAINTENANCE ATTENTION NEEDED
                  </span>
                  <span className="rounded-full bg-orange-500/20 px-2 py-0.5 text-[10px] font-bold text-orange-300">
                    {topAtRisk.riskLevel} RISK
                  </span>
                </div>
                <p className="text-sm font-bold text-white mt-0.5">
                  {topAtRisk.deviceName} &bull; Est. failure in ~{topAtRisk.daysToFailureEstimate ?? 30} days
                </p>
                <p className="text-xs text-white/60 mt-0.5">
                  Primary Mode: {topAtRisk.primaryFaultType} &bull; Save up to ${topAtRisk.preventiveSavings.toFixed(0)} with preventive service
                </p>
              </div>
            </div>

            <GlassButton
              href={`/devices/${topAtRisk.deviceId}`}
              variant="secondary"
              size="sm"
              icon={<Wrench className="size-3.5" />}
            >
              Inspect Device
            </GlassButton>
          </div>
        </div>
      )}

      {/* Fleet Overview Cards */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {/* Device Health Status Cards */}
        {predictions.slice(0, 3).map((pred) => {
          const isHigh = pred.riskLevel === "CRITICAL" || pred.riskLevel === "HIGH";
          const isHealthy = pred.riskLevel === "HEALTHY" || pred.riskLevel === "LOW";
          return (
            <Link
              key={pred.deviceId}
              href={`/devices/${pred.deviceId}`}
              className="group rounded-3xl border border-white/10 bg-white/[0.04] p-5 backdrop-blur-xl transition-all duration-200 hover:border-white/20 hover:bg-white/[0.08]"
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2">
                  <div className="flex size-8 items-center justify-center rounded-xl bg-white/[0.06] text-white/80 group-hover:text-white">
                    <Cpu className="size-4" />
                  </div>
                  <span className="text-xs font-bold text-white truncate max-w-[140px]">
                    {pred.deviceName}
                  </span>
                </div>
                <span
                  className={`rounded-full px-2 py-0.5 text-[10px] font-bold ${
                    isHigh
                      ? "bg-orange-500/20 text-orange-400 border border-orange-500/30"
                      : isHealthy
                      ? "bg-emerald-500/20 text-emerald-400 border border-emerald-500/30"
                      : "bg-yellow-500/20 text-yellow-400 border border-yellow-500/30"
                  }`}
                >
                  {pred.riskLevel}
                </span>
              </div>

              <div className="flex items-baseline justify-between mb-2">
                <span className="text-[11px] text-white/50">Predictive Score</span>
                <span
                  className={`text-lg font-black ${
                    pred.predictionScore >= 80
                      ? "text-emerald-400"
                      : pred.predictionScore >= 60
                      ? "text-yellow-400"
                      : "text-orange-400"
                  }`}
                >
                  {pred.predictionScore}/100
                </span>
              </div>

              <div className="h-1.5 w-full overflow-hidden rounded-full bg-white/10 mb-3">
                <div
                  className="h-full bg-gradient-to-r from-red-500 via-yellow-400 to-[#22C55E]"
                  style={{ width: `${pred.predictionScore}%` }}
                />
              </div>

              <div className="flex items-center justify-between text-[11px] text-white/50 pt-2 border-t border-white/5">
                <span className="flex items-center gap-1">
                  <Clock className="size-3" />
                  {pred.daysToFailureEstimate ? `~${pred.daysToFailureEstimate}d left` : "Optimal"}
                </span>
                <span className="text-[#22C55E] flex items-center gap-0.5">
                  <DollarSign className="size-3" />
                  ${pred.preventiveSavings.toFixed(0)} save
                </span>
              </div>
            </Link>
          );
        })}
      </div>
    </motion.section>
  );
}
