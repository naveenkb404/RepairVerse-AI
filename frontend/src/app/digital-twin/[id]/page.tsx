"use client";

import React, { useState, useEffect, useCallback, use } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  ArrowLeft,
  Cpu,
  RefreshCw,
  Sparkles,
  Sliders,
  Layers,
  Bot,
  Brain,
  ShieldCheck,
  TrendingUp,
} from "lucide-react";

import Container from "@/components/layout/Container";
import GlassButton from "@/components/common/GlassButton";
import Logo from "@/components/common/Logo";

import DigitalTwinHero from "@/components/digital-twin/DigitalTwinHero";
import DigitalTwinHealthMatrix from "@/components/digital-twin/DigitalTwinHealthMatrix";
import DeviceTrajectoryChart from "@/components/digital-twin/DeviceTrajectoryChart";
import FutureScenarioSimulator from "@/components/digital-twin/FutureScenarioSimulator";
import StrategyOptimizationCard from "@/components/digital-twin/StrategyOptimizationCard";
import SimulationEventTimeline from "@/components/digital-twin/SimulationEventTimeline";
import EcosystemImpactDashboard from "@/components/digital-twin/EcosystemImpactDashboard";
import SimulationInsightsPanel from "@/components/digital-twin/SimulationInsightsPanel";

import {
  fetchDigitalTwin,
  refreshDigitalTwin,
  runCustomSimulation,
  fetchEcosystemDashboard,
  MOCK_DASHBOARD,
  MOCK_ECOSYSTEM_METRICS,
} from "@/lib/api/digitalTwin";

import type {
  DigitalTwinDashboardResponse,
  EcosystemMetricsResponse,
  RunSimulationRequest,
} from "@/lib/types/digitalTwin";

type PageParams = {
  params: Promise<{ id: string }>;
};

export default function DigitalTwinPage({ params }: PageParams) {
  const resolvedParams = use(params);
  const deviceId = resolvedParams.id;

  const [twinData, setTwinData] = useState<DigitalTwinDashboardResponse | null>(null);
  const [ecosystemMetrics, setEcosystemMetrics] = useState<EcosystemMetricsResponse | null>(null);
  const [activeTab, setActiveTab] = useState<"overview" | "trajectory" | "scenarios" | "timeline">("overview");
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [isSimulating, setIsSimulating] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  const loadData = useCallback(async () => {
    setIsLoading(true);
    try {
      const [twinRes, ecoRes] = await Promise.all([
        fetchDigitalTwin(deviceId),
        fetchEcosystemDashboard(),
      ]);

      if (twinRes.success && twinRes.data) {
        setTwinData(twinRes.data);
      } else {
        setTwinData({ ...MOCK_DASHBOARD, deviceId });
      }

      if (ecoRes.success && ecoRes.data) {
        setEcosystemMetrics(ecoRes.data);
      } else {
        setEcosystemMetrics(MOCK_ECOSYSTEM_METRICS);
      }
    } catch (err) {
      console.error("Failed to load digital twin data", err);
      setTwinData({ ...MOCK_DASHBOARD, deviceId });
      setEcosystemMetrics(MOCK_ECOSYSTEM_METRICS);
    } finally {
      setIsLoading(false);
    }
  }, [deviceId]);

  useEffect(() => {
    if (deviceId) {
      loadData();
    }
  }, [deviceId, loadData]);

  const handleRefreshTwin = async () => {
    setIsRefreshing(true);
    try {
      const res = await refreshDigitalTwin(deviceId);
      if (res.success && res.data) {
        setTwinData(res.data);
        showToast("Digital Twin recalibrated with fresh telemetry.");
      } else {
        showToast("Digital Twin refreshed (simulated mode).");
      }
    } catch (err) {
      showToast("Twin recalibration encountered an issue.");
    } finally {
      setIsRefreshing(false);
    }
  };

  const handleRunCustomSimulation = async (request: RunSimulationRequest) => {
    setIsSimulating(true);
    try {
      const res = await runCustomSimulation(deviceId, request);
      if (res.success && res.data) {
        setTwinData(res.data);
        showToast("Custom scenario simulation evaluated successfully.");
      } else {
        showToast("Scenario evaluated with provided parameters.");
      }
    } catch (err) {
      showToast("Simulation execution failed.");
    } finally {
      setIsSimulating(false);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white flex flex-col items-center justify-center py-24 text-center">
        <RefreshCw className="size-12 text-cyan-400 animate-spin mb-4" />
        <p className="text-sm font-semibold text-white">
          Initializing AI Digital Twin & Multi-Horizon Simulation Engine…
        </p>
      </div>
    );
  }

  const data = twinData || { ...MOCK_DASHBOARD, deviceId };
  const metrics = ecosystemMetrics || MOCK_ECOSYSTEM_METRICS;

  return (
    <main className="min-h-screen bg-[#0B1120] text-slate-100 selection:bg-cyan-500/30 selection:text-cyan-200">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(6,182,212,0.12),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(34,197,94,0.12),transparent_50%)]"
        aria-hidden
      />

      {/* Toast Notification */}
      <AnimatePresence>
        {toastMessage && (
          <motion.div
            initial={{ opacity: 0, y: -20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -20, scale: 0.95 }}
            className="fixed top-6 right-6 z-50 flex items-center gap-3 rounded-2xl border border-cyan-500/30 bg-slate-900/90 px-5 py-3.5 shadow-2xl backdrop-blur-xl"
          >
            <Sparkles className="h-4 w-4 text-cyan-400 shrink-0" />
            <span className="text-xs font-medium text-slate-200">{toastMessage}</span>
          </motion.div>
        )}
      </AnimatePresence>

      <Container className="py-8 sm:py-12 space-y-10 relative z-10">
        {/* Top Navigation & Breadcrumb */}
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Link
              href={`/devices/${deviceId}`}
              className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-3.5 py-2 text-xs font-semibold text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Health Passport</span>
            </Link>

            <div className="hidden sm:flex items-center gap-2 text-xs text-slate-400">
              <Link href="/dashboard" className="hover:text-slate-200 transition-colors">
                Dashboard
              </Link>
              <span>/</span>
              <Link href={`/devices/${deviceId}`} className="hover:text-slate-200 transition-colors">
                {data.deviceName}
              </Link>
              <span>/</span>
              <span className="text-cyan-400 font-medium">Digital Twin</span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <Link
              href="/knowledge"
              className="inline-flex items-center gap-1.5 rounded-xl border border-white/10 bg-white/[0.04] px-3 py-2 text-xs font-medium text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white"
            >
              <Brain className="h-3.5 w-3.5 text-cyan-400" />
              <span>Knowledge Graph</span>
            </Link>

            <Link
              href="/repair-agent"
              className="inline-flex items-center gap-1.5 rounded-xl border border-white/10 bg-white/[0.04] px-3 py-2 text-xs font-medium text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white"
            >
              <Bot className="h-3.5 w-3.5 text-emerald-400" />
              <span>Repair Agent</span>
            </Link>
          </div>
        </div>

        {/* Hero Banner Component */}
        <DigitalTwinHero
          deviceName={data.deviceName}
          deviceCategory={data.deviceCategory}
          snapshot={data.snapshot}
          optimalStrategy={data.optimalStrategy}
          isRefreshing={isRefreshing}
          onRefresh={handleRefreshTwin}
          onOpenSimulate={() => setActiveTab("scenarios")}
        />

        {/* Ecosystem Impact Dashboard */}
        <EcosystemImpactDashboard metrics={metrics} />

        {/* Strategy Optimization Card (Highlighted Recommendation) */}
        {data.optimalStrategy && (
          <StrategyOptimizationCard optimization={data.optimalStrategy} />
        )}

        {/* Section Navigation Tabs */}
        <div className="flex flex-wrap items-center gap-2 border-b border-white/10 pb-3">
          {[
            { id: "overview", label: "Health Matrix & Insights", icon: ShieldCheck },
            { id: "trajectory", label: "24-Month Trajectory", icon: TrendingUp },
            { id: "scenarios", label: "Future Scenario Simulator", icon: Sliders },
            { id: "timeline", label: "Simulation Event Timeline", icon: Layers },
          ].map((tab) => {
            const Icon = tab.icon;
            const isSelected = activeTab === tab.id;

            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`inline-flex items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-semibold transition-all ${
                  isSelected
                    ? "bg-gradient-to-r from-cyan-500 to-emerald-500 text-slate-950 shadow-md shadow-cyan-500/20"
                    : "border border-white/10 bg-white/[0.03] text-slate-300 hover:bg-white/[0.08] hover:text-white"
                }`}
              >
                <Icon className="h-4 w-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* Tab Content Panels */}
        <div className="space-y-8">
          {activeTab === "overview" && (
            <div className="space-y-8">
              <DigitalTwinHealthMatrix snapshot={data.snapshot} />
              <SimulationInsightsPanel insights={data.insights} />
            </div>
          )}

          {activeTab === "trajectory" && (
            <DeviceTrajectoryChart
              forecasts={data.forecasts}
              deviceName={data.deviceName}
            />
          )}

          {activeTab === "scenarios" && (
            <FutureScenarioSimulator
              scenarios={data.scenarios}
              onRunCustomSimulation={handleRunCustomSimulation}
              isSimulating={isSimulating}
            />
          )}

          {activeTab === "timeline" && (
            <SimulationEventTimeline events={data.events} />
          )}
        </div>
      </Container>
    </main>
  );
}
