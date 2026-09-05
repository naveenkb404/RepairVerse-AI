"use client";

import React, { useState, useEffect, use } from "react";
import Link from "next/link";
import { fetchDeviceLearningProfile } from "@/lib/api/federatedLearning";
import type { DeviceLearningProfileResponse } from "@/lib/types/federatedLearning";
import { Brain, ArrowLeft, ShieldCheck, CheckCircle2, Lock, TrendingUp } from "lucide-react";
import LearningSignalExplorer from "@/components/learning/LearningSignalExplorer";

interface DeviceLearningPageProps {
  params: Promise<{ deviceId: string }>;
}

export default function DeviceLearningPage({ params }: DeviceLearningPageProps) {
  const resolvedParams = use(params);
  const deviceId = resolvedParams.deviceId;

  const [profile, setProfile] = useState<DeviceLearningProfileResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const res = await fetchDeviceLearningProfile(deviceId);
        if (res.data) setProfile(res.data);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [deviceId]);

  if (loading || !profile) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-950 text-white">
        <div className="flex flex-col items-center gap-3">
          <Brain className="h-10 w-10 animate-spin text-indigo-400" />
          <p className="text-sm font-medium text-slate-400">Loading device learning insights...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 py-8 px-4 sm:px-6 lg:px-8 text-slate-100 space-y-6">
      {/* Top back navigation */}
      <div className="flex items-center justify-between">
        <Link
          href={`/devices/${deviceId}`}
          className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-slate-900 px-3.5 py-2 text-xs font-semibold text-slate-300 hover:text-white hover:border-indigo-500/40 transition"
        >
          <ArrowLeft className="h-4 w-4" />
          <span>Back to Device Profile</span>
        </Link>

        <Link
          href="/learning"
          className="inline-flex items-center gap-1.5 text-xs text-indigo-400 hover:text-indigo-300 font-semibold"
        >
          <Brain className="h-4 w-4" />
          <span>All Ecosystem Learning</span>
        </Link>
      </div>

      {/* Header */}
      <div className="rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900 via-slate-950 to-black p-6 sm:p-8 shadow-2xl backdrop-blur-2xl">
        <div className="flex flex-wrap items-center gap-2.5 mb-2">
          <span className="rounded-full bg-indigo-500/10 border border-indigo-500/30 px-3 py-0.5 text-xs font-mono font-bold text-indigo-400 uppercase">
            Ecosystem Learning Profile
          </span>
          <span className="rounded-full bg-white/5 border border-white/10 px-3 py-0.5 text-xs font-mono text-slate-400">
            {profile.deviceCategory} • {deviceId}
          </span>
          <span className="rounded-full bg-emerald-500/10 border border-emerald-500/20 px-3 py-0.5 text-xs font-mono font-bold text-emerald-400">
            Model {profile.activeModelVersion}
          </span>
        </div>

        <h1 className="text-2xl sm:text-3xl font-black text-white">
          Aggregated Learning Intelligence for this Device
        </h1>

        <p className="mt-2 text-xs sm:text-sm text-slate-300 max-w-2xl leading-relaxed">
          Based on <strong>{profile.matchingEcosystemObservations.toLocaleString()}</strong> privacy-filtered repair outcomes across similar {profile.deviceCategory.toLowerCase()}s in the ecosystem.
        </p>

        {/* Privacy notice banner */}
        <div className="mt-4 flex items-center gap-2.5 rounded-xl border border-emerald-500/20 bg-emerald-950/20 p-3 text-xs text-emerald-300">
          <Lock className="h-4 w-4 text-emerald-400 shrink-0" />
          <span>{profile.privacyNotice}</span>
        </div>
      </div>

      {/* Aggregate Metrics Bar */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="rounded-2xl border border-white/10 bg-slate-900/80 p-5 shadow-xl">
          <span className="text-xs font-medium text-slate-400">Ecosystem Success Rate</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-3xl font-black text-emerald-400">
              {Math.round(profile.ecosystemSuccessRate * 100)}%
            </span>
          </div>
          <span className="text-[11px] text-slate-500">Across verified historical repairs</span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-slate-900/80 p-5 shadow-xl">
          <span className="text-xs font-medium text-slate-400">Expected Lifespan Extension</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-3xl font-black text-cyan-400">
              +{profile.expectedLifespanGainMonths} months
            </span>
          </div>
          <span className="text-[11px] text-slate-500">With timely preventive intervention</span>
        </div>

        <div className="rounded-2xl border border-white/10 bg-slate-900/80 p-5 shadow-xl">
          <span className="text-xs font-medium text-slate-400">Projected Financial Savings</span>
          <div className="mt-2 flex items-baseline gap-2">
            <span className="text-3xl font-black text-indigo-400">
              ₹{profile.expectedCostSavings.toLocaleString()}
            </span>
          </div>
          <span className="text-[11px] text-slate-500">Compared to premature replacement</span>
        </div>
      </div>

      {/* Relevant signals explorer */}
      <LearningSignalExplorer signals={profile.relevantSignals ?? []} />
    </div>
  );
}
