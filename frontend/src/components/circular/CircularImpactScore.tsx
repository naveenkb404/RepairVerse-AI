"use client";

import { motion } from "framer-motion";
import { Award, CheckCircle2, AlertCircle, BarChart3, Wrench, Trash2, Cloud, RefreshCw, Zap } from "lucide-react";
import type { CircularImpactScore } from "@/lib/types/circularEconomy";

interface CircularImpactScoreProps {
  scoreData: CircularImpactScore;
}

export default function CircularImpactScoreGauge({ scoreData }: CircularImpactScoreProps) {
  const score = scoreData?.score ?? 0;
  const clamped = Math.min(100, Math.max(0, score));
  const breakdown = scoreData?.factorBreakdown ?? {
    repairLifeExtensionPoints: 20,
    ewastePreventionPoints: 18,
    carbonImpactPoints: 15,
    endOfLifePoints: 10,
    consistencyPoints: 7,
    totalScore: score,
  };

  const circumference = 2 * Math.PI * 68; // r=68
  const dashOffset = circumference * (1 - clamped / 100);

  const factors = [
    {
      label: "Repair & Life Extension",
      points: breakdown.repairLifeExtensionPoints,
      max: 30,
      icon: Wrench,
      color: "from-emerald-500 to-teal-500",
      desc: "Repairs performed, maintenance milestones, operational longevity",
    },
    {
      label: "E-Waste Prevention",
      points: breakdown.ewastePreventionPoints,
      max: 25,
      icon: Trash2,
      color: "from-cyan-500 to-blue-500",
      desc: "Direct toxic landfill avoidance through hardware retention",
    },
    {
      label: "Carbon Mitigation",
      points: breakdown.carbonImpactPoints,
      max: 20,
      icon: Cloud,
      color: "from-blue-500 to-indigo-500",
      desc: "Avoided manufacturing & extraction embodied CO₂",
    },
    {
      label: "Responsible End-of-Life",
      points: breakdown.endOfLifePoints,
      max: 15,
      icon: RefreshCw,
      color: "from-amber-500 to-emerald-500",
      desc: "Refurbishments, certified recycling, and community donation",
    },
    {
      label: "Care Consistency",
      points: breakdown.consistencyPoints,
      max: 10,
      icon: Zap,
      color: "from-purple-500 to-pink-500",
      desc: "Active sustainability target goals & routine inspections",
    },
  ];

  return (
    <div className="rounded-3xl border border-white/10 bg-gradient-to-b from-[#0F172A]/80 to-[#0B1120]/90 p-6 md:p-8 backdrop-blur-xl shadow-xl space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4 border-b border-white/10 pb-4">
        <div>
          <h2 className="text-xl md:text-2xl font-bold text-white flex items-center gap-2.5">
            <BarChart3 className="size-6 text-emerald-400" />
            Deterministic Score Architecture
          </h2>
          <p className="text-xs md:text-sm text-slate-400 mt-1">
            Authoritative, explainable scoring breakdown computed across 5 weighted lifecycle dimensions.
          </p>
        </div>

        <div className="inline-flex items-center gap-2 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-4 py-1.5 text-xs font-bold text-emerald-300">
          <Award className="size-4" />
          {scoreData?.tier?.replace("_", " ") ?? "ECO LEADER"}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
        {/* Animated Gauge Ring */}
        <div className="lg:col-span-4 flex flex-col items-center justify-center text-center p-4">
          <div className="relative size-48 md:size-52">
            <svg className="size-full -rotate-90" viewBox="0 0 160 160" aria-hidden>
              <circle
                cx="80"
                cy="80"
                r="68"
                fill="none"
                stroke="rgba(255,255,255,0.06)"
                strokeWidth="12"
              />
              <motion.circle
                cx="80"
                cy="80"
                r="68"
                fill="none"
                stroke="url(#circGrad)"
                strokeWidth="12"
                strokeLinecap="round"
                strokeDasharray={circumference}
                initial={{ strokeDashoffset: circumference }}
                animate={{ strokeDashoffset: dashOffset }}
                transition={{ duration: 1.4, ease: [0.22, 1, 0.36, 1] }}
              />
              <defs>
                <linearGradient id="circGrad" x1="0" y1="0" x2="1" y2="1">
                  <stop offset="0%" stopColor="#22C55E" />
                  <stop offset="50%" stopColor="#06B6D4" />
                  <stop offset="100%" stopColor="#3B82F6" />
                </linearGradient>
              </defs>
            </svg>

            {/* Gauge Center Content */}
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-4xl md:text-5xl font-black text-white tracking-tight">
                {clamped}
              </span>
              <span className="text-xs font-semibold text-slate-400 mt-0.5">out of 100</span>
            </div>
          </div>

          <div className="mt-4 text-xs text-slate-400">
            Evaluated on:{" "}
            <span className="text-slate-200">
              {scoreData?.evaluatedAt
                ? new Date(scoreData.evaluatedAt).toLocaleDateString()
                : "Live Session"}
            </span>
          </div>
        </div>

        {/* 5 Factor Breakdown Bars */}
        <div className="lg:col-span-8 space-y-3.5">
          {factors.map((factor, idx) => {
            const percentage = Math.min(100, Math.round((factor.points / factor.max) * 100));
            const Icon = factor.icon;
            return (
              <div key={idx} className="space-y-1.5 rounded-2xl border border-white/5 bg-white/[0.02] p-3">
                <div className="flex items-center justify-between text-xs md:text-sm">
                  <div className="flex items-center gap-2 font-semibold text-white">
                    <Icon className="size-4 text-slate-400" />
                    <span>{factor.label}</span>
                  </div>
                  <div className="flex items-baseline gap-1 text-xs">
                    <span className="font-bold text-white">{factor.points}</span>
                    <span className="text-slate-500">/ {factor.max} pts</span>
                  </div>
                </div>

                {/* Progress bar */}
                <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
                  <motion.div
                    className={`h-full rounded-full bg-gradient-to-r ${factor.color}`}
                    initial={{ width: 0 }}
                    animate={{ width: `${percentage}%` }}
                    transition={{ duration: 0.8, delay: idx * 0.1 }}
                  />
                </div>

                <div className="text-[11px] text-slate-400 truncate">{factor.desc}</div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Strengths & Improvement Opportunities */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2 border-t border-white/10">
        <div className="rounded-2xl border border-emerald-500/20 bg-emerald-950/20 p-4 space-y-2">
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-emerald-400">
            <CheckCircle2 className="size-4" />
            Ecosystem Strengths
          </div>
          <ul className="space-y-1.5 text-xs text-slate-300">
            {scoreData?.strengths && scoreData.strengths.length > 0 ? (
              scoreData.strengths.map((str, i) => (
                <li key={i} className="flex items-start gap-2">
                  <span className="text-emerald-400 font-bold">•</span>
                  <span>{str}</span>
                </li>
              ))
            ) : (
              <li className="text-slate-400">Strong repair history and proactive device retention.</li>
            )}
          </ul>
        </div>

        <div className="rounded-2xl border border-amber-500/20 bg-amber-950/20 p-4 space-y-2">
          <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-amber-400">
            <AlertCircle className="size-4" />
            Improvement Opportunities
          </div>
          <ul className="space-y-1.5 text-xs text-slate-300">
            {scoreData?.improvementAreas && scoreData.improvementAreas.length > 0 ? (
              scoreData.improvementAreas.map((imp, i) => (
                <li key={i} className="flex items-start gap-2">
                  <span className="text-amber-400 font-bold">•</span>
                  <span>{imp}</span>
                </li>
              ))
            ) : (
              <li className="text-slate-400">Set active sustainability goals to boost consistency points.</li>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
}
