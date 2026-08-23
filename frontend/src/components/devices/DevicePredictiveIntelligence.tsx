"use client";

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Activity,
  AlertTriangle,
  CheckCircle2,
  Clock,
  DollarSign,
  Info,
  Leaf,
  RefreshCw,
  ShieldAlert,
  Sparkles,
  Wrench,
  Zap,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import { evaluateDevicePrediction } from "@/lib/api/prediction";
import type { DevicePredictionData, RiskLevel } from "@/lib/types/prediction";

interface DevicePredictiveIntelligenceProps {
  deviceId: string;
  deviceName: string;
}

const EASE = [0.22, 1, 0.36, 1] as const;

export default function DevicePredictiveIntelligence({
  deviceId,
  deviceName,
}: DevicePredictiveIntelligenceProps) {
  const [prediction, setPrediction] = useState<DevicePredictionData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isReevaluating, setIsReevaluating] = useState(false);

  useEffect(() => {
    async function loadPrediction() {
      setIsLoading(true);
      const res = await evaluateDevicePrediction(deviceId);
      if (res.data) {
        setPrediction(res.data);
      }
      setIsLoading(false);
    }
    if (deviceId) {
      loadPrediction();
    }
  }, [deviceId]);

  const handleReevaluate = async () => {
    setIsReevaluating(true);
    const res = await evaluateDevicePrediction(deviceId);
    if (res.data) {
      setPrediction(res.data);
    }
    setIsReevaluating(false);
  };

  const getRiskBadge = (risk: RiskLevel) => {
    switch (risk) {
      case "CRITICAL":
        return {
          bg: "bg-red-500/10 border-red-500/30 text-red-400",
          dot: "bg-red-500 animate-ping",
          label: "CRITICAL RISK",
        };
      case "HIGH":
        return {
          bg: "bg-orange-500/10 border-orange-500/30 text-orange-400",
          dot: "bg-orange-500 animate-pulse",
          label: "HIGH RISK",
        };
      case "MEDIUM":
        return {
          bg: "bg-yellow-500/10 border-yellow-500/30 text-yellow-400",
          dot: "bg-yellow-400",
          label: "MODERATE RISK",
        };
      case "LOW":
        return {
          bg: "bg-emerald-500/10 border-emerald-500/30 text-emerald-400",
          dot: "bg-emerald-400",
          label: "LOW RISK",
        };
      case "HEALTHY":
      default:
        return {
          bg: "bg-cyan-500/10 border-cyan-500/30 text-cyan-400",
          dot: "bg-cyan-400",
          label: "OPTIMAL HEALTH",
        };
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 85) return "text-cyan-400";
    if (score >= 70) return "text-emerald-400";
    if (score >= 50) return "text-yellow-400";
    if (score >= 35) return "text-orange-400";
    return "text-red-400";
  };

  if (isLoading) {
    return (
      <GlassCard className="p-6">
        <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
          <div className="flex items-center gap-2">
            <Activity className="size-5 text-[#22C55E] animate-pulse" />
            <h2 className="text-base font-bold text-white">AI Maintenance Intelligence</h2>
          </div>
          <span className="text-xs text-white/50">Analyzing telemetry…</span>
        </div>
        <div className="py-8 text-center text-white/60 text-xs flex flex-col items-center gap-2">
          <RefreshCw className="size-6 animate-spin text-[#22C55E]" />
          Evaluating failure probability models & hardware telemetry…
        </div>
      </GlassCard>
    );
  }

  if (!prediction) return null;

  const riskMeta = getRiskBadge(prediction.riskLevel);

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: 0.15, ease: EASE }}
    >
      <GlassCard className="p-6 border-[#22C55E]/20">
        {/* Header */}
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/10 pb-4 mb-5">
          <div className="flex items-center gap-2.5">
            <div className="flex size-9 items-center justify-center rounded-xl bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/20 border border-white/10">
              <Sparkles className="size-5 text-[#22C55E]" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="text-base font-bold text-white">AI Predictive Maintenance</h2>
                {prediction.isDemo && (
                  <span className="rounded-full bg-cyan-500/10 px-2 py-0.5 text-[9px] font-semibold text-cyan-400 border border-cyan-500/30">
                    DEMO SIMULATION
                  </span>
                )}
              </div>
              <p className="text-[11px] text-white/50">
                Deterministic failure forecasting & preventive health modeling
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <span
              className={`inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs font-bold ${riskMeta.bg}`}
            >
              <span className={`size-1.5 rounded-full ${riskMeta.dot}`} />
              {riskMeta.label}
            </span>
            <button
              onClick={handleReevaluate}
              disabled={isReevaluating}
              title="Re-run predictive scoring"
              className="flex size-8 items-center justify-center rounded-lg border border-white/10 bg-white/[0.04] text-white/70 hover:bg-white/10 hover:text-white transition"
            >
              <RefreshCw className={`size-3.5 ${isReevaluating ? "animate-spin text-[#22C55E]" : ""}`} />
            </button>
          </div>
        </div>

        {/* Top metrics hero */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-3 mb-6">
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3.5">
            <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-1">
              Predictive Health Index
            </span>
            <div className="flex items-baseline gap-2">
              <span className={`text-2xl font-black ${getScoreColor(prediction.predictionScore)}`}>
                {prediction.predictionScore}
              </span>
              <span className="text-xs text-white/40">/ 100</span>
            </div>
            <div className="mt-2 h-1.5 w-full overflow-hidden rounded-full bg-white/10">
              <div
                className="h-full bg-gradient-to-r from-red-500 via-yellow-400 to-[#22C55E] transition-all duration-500"
                style={{ width: `${Math.max(5, prediction.predictionScore)}%` }}
              />
            </div>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3.5">
            <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-1">
              Est. Days to Maintenance
            </span>
            <div className="flex items-center gap-2">
              <Clock className="size-4 text-[#06B6D4]" />
              <span className="text-2xl font-black text-white">
                {prediction.daysToFailureEstimate ?? "N/A"}
              </span>
              <span className="text-xs text-white/50">days</span>
            </div>
            <span className="text-[10px] text-white/40 mt-1 block truncate">
              Primary: {prediction.primaryFaultType}
            </span>
          </div>

          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3.5">
            <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-1">
              Preventive Value
            </span>
            <div className="flex items-center gap-2">
              <DollarSign className="size-4 text-emerald-400" />
              <span className="text-xl font-black text-emerald-400">
                ${prediction.preventiveSavings.toFixed(0)}
              </span>
              <span className="text-[10px] text-white/50">saved vs fail</span>
            </div>
            <div className="flex items-center gap-1 text-[10px] text-[#22C55E] mt-1">
              <Leaf className="size-3" />
              <span>{prediction.co2SavingsKg.toFixed(1)} kg CO₂ avoided</span>
            </div>
          </div>
        </div>

        {/* 6-Factor Deterministic Breakdown */}
        {prediction.scoringBreakdown && prediction.scoringBreakdown.length > 0 && (
          <div className="mb-6">
            <h3 className="text-xs font-bold uppercase tracking-wider text-white/70 mb-3 flex items-center gap-1.5">
              <Activity className="size-3.5 text-[#22C55E]" />
              Multi-Factor Diagnostics Matrix
            </h3>
            <div className="grid grid-cols-1 gap-2 sm:grid-cols-2 lg:grid-cols-3">
              {prediction.scoringBreakdown.map((factor, i) => (
                <div
                  key={i}
                  className="rounded-xl border border-white/5 bg-white/[0.02] p-2.5 flex flex-col justify-between"
                >
                  <div className="flex items-center justify-between text-xs mb-1">
                    <span className="font-semibold text-white/90">{factor.factor}</span>
                    <span className="text-[10px] font-mono text-white/60">
                      {factor.score}/{factor.maxScore} pts
                    </span>
                  </div>
                  <p className="text-[10px] text-white/50 leading-relaxed line-clamp-2">
                    {factor.description}
                  </p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recommended Preventive Actions */}
        {prediction.recommendedActions && prediction.recommendedActions.length > 0 && (
          <div className="mb-5">
            <h3 className="text-xs font-bold uppercase tracking-wider text-white/70 mb-3 flex items-center gap-1.5">
              <Wrench className="size-3.5 text-[#06B6D4]" />
              Recommended Proactive Actions
            </h3>
            <div className="space-y-2">
              {prediction.recommendedActions.map((action, idx) => (
                <div
                  key={idx}
                  className="flex items-start gap-2.5 rounded-xl border border-white/5 bg-white/[0.02] p-2.5 text-xs text-white/80 hover:bg-white/[0.04] transition"
                >
                  <CheckCircle2 className="size-4 shrink-0 text-[#22C55E] mt-0.5" />
                  <span className="leading-snug">{action}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Action bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 pt-3 border-t border-white/10 text-[11px] text-white/40">
          <span>
            Evaluated: {new Date(prediction.evaluatedAt).toLocaleDateString()} &bull; Model Confidence:{" "}
            {(prediction.confidenceScore * 100).toFixed(0)}%
          </span>
          <div className="flex items-center gap-2">
            <GlassButton href="/repair-shops" variant="secondary" size="sm" icon={<Wrench className="size-3" />}>
              Schedule Preventive Service
            </GlassButton>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
