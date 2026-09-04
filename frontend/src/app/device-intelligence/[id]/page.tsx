"use client";

import React, { useEffect, useState, use } from "react";
import { motion } from "framer-motion";
import {
  Cpu,
  Sparkles,
  ArrowLeft,
  RefreshCw,
  Layers,
  Sliders,
  ShieldCheck,
  AlertTriangle,
} from "lucide-react";
import Link from "next/link";
import { deviceIntelligenceApi } from "@/lib/api/deviceIntelligence";
import type {
  DeviceIntelligenceResponse,
  DeviceIntelligenceTimelineItem,
  DeviceScenarioSimulationRequest,
} from "@/lib/types/deviceIntelligence";

import { DeviceIntelligenceHero } from "@/components/intelligence/DeviceIntelligenceHero";
import { DeviceDecisionScore } from "@/components/intelligence/DeviceDecisionScore";
import { SmartDecisionCard } from "@/components/intelligence/SmartDecisionCard";
import { DecisionFactorBreakdown } from "@/components/intelligence/DecisionFactorBreakdown";
import { ScenarioSimulator } from "@/components/intelligence/ScenarioSimulator";
import { DeviceIntelligenceTimeline } from "@/components/intelligence/DeviceIntelligenceTimeline";
import { IntelligenceAlerts } from "@/components/intelligence/IntelligenceAlerts";

export default function DeviceIntelligencePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const resolvedParams = use(params);
  const deviceId = resolvedParams.id;

  const [data, setData] = useState<DeviceIntelligenceResponse | null>(null);
  const [timeline, setTimeline] = useState<DeviceIntelligenceTimelineItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);
  const [simulating, setSimulating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [intelRes, timelineRes] = await Promise.all([
        deviceIntelligenceApi.getDeviceIntelligence(deviceId),
        deviceIntelligenceApi.getDeviceTimeline(deviceId),
      ]);
      setData(intelRes);
      setTimeline(timelineRes);
    } catch (err: any) {
      setError(err?.message || "Failed to load device intelligence");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [deviceId]);

  const handleReevaluate = async () => {
    try {
      setEvaluating(true);
      const refreshed = await deviceIntelligenceApi.evaluateDevice(deviceId, true);
      const refreshedTimeline = await deviceIntelligenceApi.getDeviceTimeline(deviceId);
      setData(refreshed);
      setTimeline(refreshedTimeline);
    } catch (err: any) {
      console.error("Re-evaluation failed:", err);
    } finally {
      setEvaluating(false);
    }
  };

  const handleCustomSimulate = async (simParams: DeviceScenarioSimulationRequest) => {
    if (!data) return;
    try {
      setSimulating(true);
      const updatedScenarios = await deviceIntelligenceApi.simulateScenario(deviceId, simParams);
      setData({
        ...data,
        scenarios: updatedScenarios,
      });
    } catch (err: any) {
      console.error("Simulation failed:", err);
    } finally {
      setSimulating(false);
    }
  };

  const handleMarkAlertRead = async (alertId: string) => {
    if (!data) return;
    try {
      await deviceIntelligenceApi.markAlertAsRead(alertId);
      setData({
        ...data,
        activeAlerts: data.activeAlerts.map((a) =>
          a.id === alertId ? { ...a, isRead: true } : a
        ),
      });
    } catch (err) {
      console.error("Failed to mark alert as read:", err);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 text-white p-6 md:p-12 flex flex-col items-center justify-center space-y-4">
        <div className="relative">
          <div className="h-16 w-16 rounded-2xl bg-gradient-to-tr from-emerald-500 to-cyan-500 animate-pulse flex items-center justify-center">
            <Cpu className="w-8 h-8 text-slate-950 animate-spin" />
          </div>
          <div className="absolute -inset-2 rounded-2xl bg-cyan-500/20 blur-xl animate-ping" />
        </div>
        <p className="text-slate-400 text-sm font-medium tracking-wide">
          Synthesizing multi-signal device intelligence...
        </p>
      </div>
    );
  }

  if (error || !data) {
    return (
      <div className="min-h-screen bg-slate-950 text-white p-6 md:p-12 flex flex-col items-center justify-center space-y-4">
        <div className="p-4 rounded-2xl bg-rose-500/10 border border-rose-500/20 text-rose-400">
          <AlertTriangle className="w-8 h-8" />
        </div>
        <h2 className="text-xl font-bold">Failed to load Device Intelligence</h2>
        <p className="text-slate-400 text-sm">{error || "Device not found"}</p>
        <button
          onClick={loadData}
          className="px-4 py-2 rounded-xl text-sm font-semibold bg-white/10 hover:bg-white/15 text-white transition-all"
        >
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 selection:bg-emerald-500/30">
      {/* Background Decorative Mesh */}
      <div className="fixed inset-0 pointer-events-none z-0">
        <div className="absolute top-0 left-1/4 w-[600px] h-[600px] bg-emerald-500/5 rounded-full blur-[140px]" />
        <div className="absolute top-1/3 right-1/4 w-[600px] h-[600px] bg-cyan-500/5 rounded-full blur-[140px]" />
      </div>

      <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
        {/* Top Breadcrumbs & Back Navigation */}
        <div className="flex items-center justify-between">
          <Link
            href={`/devices/${deviceId}`}
            className="inline-flex items-center gap-2 text-xs font-semibold text-slate-400 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            <span>Back to Device Passport</span>
          </Link>

          <div className="flex items-center gap-2">
            <span className="h-2 w-2 rounded-full bg-emerald-400 animate-pulse" />
            <span className="text-xs font-semibold text-emerald-400">
              Live Algorithmic Determinism Active
            </span>
          </div>
        </div>

        {/* 1. Master Device Intelligence Hero */}
        <DeviceIntelligenceHero
          data={data}
          onReevaluate={handleReevaluate}
          isEvaluating={evaluating}
        />

        {/* 2. Main Intelligence Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
          {/* Left Column (5 Cols): Score, Decision Blueprint, Active Alerts */}
          <div className="lg:col-span-5 space-y-6">
            <DeviceDecisionScore
              score={data.intelligenceScore}
              tier={data.intelligenceTier}
              confidence={data.decisionConfidence}
              breakdown={data.scoreBreakdown}
            />

            <SmartDecisionCard
              decision={data.smartDecision}
              deviceId={data.deviceId}
            />

            <IntelligenceAlerts
              alerts={data.activeAlerts}
              onMarkRead={handleMarkAlertRead}
            />
          </div>

          {/* Right Column (7 Cols): Factor Breakdown, What-If Simulator, Timeline */}
          <div className="lg:col-span-7 space-y-6">
            <DecisionFactorBreakdown factors={data.decisionFactors} />

            <ScenarioSimulator
              scenarios={data.scenarios}
              recommendedScenarioType={data.recommendedAction}
              onSimulate={handleCustomSimulate}
              isSimulating={simulating}
            />

            <DeviceIntelligenceTimeline timeline={timeline} />
          </div>
        </div>
      </div>
    </div>
  );
}
