"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertTriangle,
  ArrowLeft,
  Leaf,
  DollarSign,
  Recycle,
  Wrench,
  TrendingUp,
  Scale,
  Activity,
  RefreshCw,
  Wifi,
  WifiOff,
  Loader2,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import MetricCard from "@/components/dashboard/MetricCard";
import CarbonTrendChart from "@/components/dashboard/CarbonTrendChart";
import RepairVsReplacePanel from "@/components/dashboard/RepairVsReplacePanel";
import SustainabilityScore from "@/components/dashboard/SustainabilityScore";
import RecentActivityList from "@/components/dashboard/RecentActivityList";

import { fetchCarbonDashboard } from "@/lib/api/carbon";
import type { CarbonDashboardData } from "@/lib/api/carbon";

const EASE = [0.22, 1, 0.36, 1] as const;

// ─── Demo Data ────────────────────────────────────────────────────────────────
// Shown ONLY when backend is offline, with explicit "Demo Mode" labelling.
// This is reference data only — not presented as belonging to the current user.
const DEMO_DATA: CarbonDashboardData = {
  impact: {
    co2Saved: 38.4,
    ewasteReduced: 2.1,
    moneySaved: 1240,
    repairCount: 6,
  },
  sustainabilityScore: 72,
  trend: [
    { period: "Mar", co2Saved: 4.2, moneySaved: 85 },
    { period: "Apr", co2Saved: 6.8, moneySaved: 120 },
    { period: "May", co2Saved: 5.1, moneySaved: 200 },
    { period: "Jun", co2Saved: 9.3, moneySaved: 320 },
    { period: "Jul", co2Saved: 7.0, moneySaved: 295 },
    { period: "Aug", co2Saved: 6.0, moneySaved: 220 },
  ],
  recentActivity: [
    {
      id: "1",
      deviceName: "iPhone 13 Pro",
      repairType: "Screen replacement",
      repairDate: "Aug 7, 2026",
      co2Avoided: 6.5,
      ewasteAvoided: 0.08,
      moneySaved: 640,
    },
    {
      id: "2",
      deviceName: "MacBook Air M1",
      repairType: "Thermal paste & fan service",
      repairDate: "Jul 22, 2026",
      co2Avoided: 4.2,
      ewasteAvoided: 0.05,
      moneySaved: 320,
    },
    {
      id: "3",
      deviceName: "Samsung TV 55\"",
      repairType: "Backlight board replacement",
      repairDate: "Jun 14, 2026",
      co2Avoided: 12.1,
      ewasteAvoided: 0.45,
      moneySaved: 280,
    },
  ],
};

// ─── States ───────────────────────────────────────────────────────────────────
type DashboardState =
  | { status: "loading" }
  | { status: "offline"; message: string }
  | { status: "error"; message: string }
  | { status: "empty" }
  | { status: "live"; data: CarbonDashboardData }
  | { status: "demo"; data: CarbonDashboardData };

// ─── Section Wrapper ──────────────────────────────────────────────────────────
function Section({
  id,
  title,
  subtitle,
  children,
}: {
  id?: string;
  title: string;
  subtitle?: string;
  children: React.ReactNode;
}) {
  return (
    <section id={id} className="mb-8">
      <div className="mb-5">
        <h2 className="text-lg font-bold text-white sm:text-xl">{title}</h2>
        {subtitle && (
          <p className="mt-1 text-xs text-[#CBD5E1]">{subtitle}</p>
        )}
      </div>
      {children}
    </section>
  );
}

// ─── Loading Skeleton ─────────────────────────────────────────────────────────
function LoadingSkeleton() {
  return (
    <div className="space-y-6 animate-pulse" aria-label="Loading carbon dashboard">
      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="h-36 rounded-3xl bg-white/[0.04]" />
        ))}
      </div>
      <div className="h-64 rounded-3xl bg-white/[0.04]" />
      <div className="h-80 rounded-3xl bg-white/[0.04]" />
    </div>
  );
}

// ─── Main Page ────────────────────────────────────────────────────────────────
export default function CarbonDashboardPage() {
  const [state, setState] = useState<DashboardState>({ status: "loading" });

  const loadData = async () => {
    setState({ status: "loading" });
    const result = await fetchCarbonDashboard();

    if (!result.success) {
      // Backend offline — show demo data with clear disclosure
      setState({ status: "offline", message: result.message ?? "Backend offline." });
    } else if (!result.data || result.data.impact.repairCount === 0) {
      setState({ status: "empty" });
    } else {
      setState({ status: "live", data: result.data });
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const isDemo = state.status === "offline";
  const dashData =
    state.status === "live"
      ? state.data
      : state.status === "offline"
      ? DEMO_DATA
      : null;

  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.10),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* ── Header ─────────────────────────────────────────────────────────── */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <Logo size="sm" href="/" />

          <div className="flex items-center gap-3">
            {/* Backend status indicator */}
            <span
              className={`hidden items-center gap-1.5 rounded-full border px-3 py-1 text-[11px] font-semibold sm:inline-flex ${
                state.status === "live"
                  ? "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
                  : "border-amber-500/30 bg-amber-500/10 text-amber-400"
              }`}
            >
              {state.status === "live" ? (
                <>
                  <Wifi className="size-3" />
                  Live Data
                </>
              ) : (
                <>
                  <WifiOff className="size-3" />
                  Demo Mode
                </>
              )}
            </span>

            <GlassButton href="/" variant="secondary" size="sm" icon={<ArrowLeft className="size-3.5" />}>
              Home
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* ── Page Title ─────────────────────────────────────────────────────── */}
      <div className="relative border-b border-white/[0.06] bg-white/[0.015] py-8 sm:py-12">
        <Container>
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, ease: EASE }}
            className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <div className="mb-2 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-3.5 py-1 text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
                <Leaf className="size-3.5" /> Carbon Impact Dashboard
              </div>
              <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
                Your Environmental{" "}
                <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
                  Footprint Saved
                </span>
              </h1>
              <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
                Every device you repair instead of replacing prevents CO₂ emissions,
                e-waste, and wasted resources. Track your personal sustainability impact here.
              </p>
            </div>

            <GlassButton
              variant="outline"
              size="sm"
              icon={<RefreshCw className="size-3.5" />}
              onClick={loadData}
              disabled={state.status === "loading"}
            >
              Refresh
            </GlassButton>
          </motion.div>
        </Container>
      </div>

      {/* ── Demo Banner ─────────────────────────────────────────────────────── */}
      <AnimatePresence>
        {isDemo && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="overflow-hidden border-b border-amber-500/20 bg-amber-500/[0.07]"
          >
            <Container className="flex items-center gap-3 py-3 text-xs text-amber-300">
              <AlertTriangle className="size-4 shrink-0 text-amber-400" />
              <span>
                <strong>Demo Mode — </strong>
                The data shown below is example reference data, not your personal impact.{" "}
                {(state as { status: "offline"; message: string }).message}
              </span>
            </Container>
          </motion.div>
        )}
      </AnimatePresence>

      {/* ── Main Content ────────────────────────────────────────────────────── */}
      <main className="relative py-8 sm:py-12">
        <Container>
          {/* LOADING */}
          {state.status === "loading" && (
            <div className="flex flex-col items-center justify-center py-24 text-center">
              <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
              <p className="text-sm font-semibold text-white">Loading carbon data…</p>
              <p className="mt-1 text-xs text-white/50">
                Connecting to RepairVerse AI backend
              </p>
            </div>
          )}

          {/* ERROR */}
          {state.status === "error" && (
            <div className="flex flex-col items-center justify-center rounded-3xl border border-red-500/20 bg-red-500/[0.05] py-16 text-center">
              <AlertTriangle className="size-10 text-red-400 mb-3" />
              <h2 className="text-base font-bold text-white">Failed to Load Data</h2>
              <p className="mt-1 max-w-sm text-xs text-white/60">
                {(state as { status: "error"; message: string }).message}
              </p>
              <GlassButton
                className="mt-6"
                variant="outline"
                size="sm"
                onClick={loadData}
              >
                Try Again
              </GlassButton>
            </div>
          )}

          {/* EMPTY (authenticated, no repairs yet) */}
          {state.status === "empty" && (
            <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-white/10 py-20 text-center">
              <Leaf className="size-14 text-[#22C55E]/30 mb-4" />
              <h2 className="text-xl font-bold text-white">No Impact Recorded Yet</h2>
              <p className="mt-3 max-w-md text-sm text-[#CBD5E1] leading-relaxed">
                Your carbon impact data will appear here once you complete your first
                device repair using RepairVerse AI. Every repair counts!
              </p>
              <div className="mt-8 flex flex-wrap justify-center gap-3">
                <GlassButton href="/#ai-demo" icon={<Wrench className="size-4" />}>
                  Start AI Diagnosis
                </GlassButton>
                <GlassButton href="/" variant="secondary" icon={<ArrowLeft className="size-4" />}>
                  Back to Home
                </GlassButton>
              </div>
            </div>
          )}

          {/* LIVE / DEMO */}
          {dashData && (state.status === "live" || state.status === "offline") && (
            <div className="space-y-10">

              {/* ── Metric Overview ─────────────────────────────────────────── */}
              <Section
                id="overview"
                title="Impact Overview"
                subtitle={`Cumulative environmental impact from ${dashData.impact.repairCount} device repair${dashData.impact.repairCount !== 1 ? "s" : ""}`}
              >
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
                  <MetricCard
                    icon={Leaf}
                    label="CO₂ Emissions Avoided"
                    value={`${dashData.impact.co2Saved.toFixed(1)} kg`}
                    subtext="CO₂ not released by repairing instead of manufacturing a new device"
                    accentColor="green"
                    badgeText={isDemo ? "Demo" : "Live"}
                    delay={0}
                  />
                  <MetricCard
                    icon={Recycle}
                    label="E-Waste Prevented"
                    value={`${dashData.impact.ewasteReduced.toFixed(1)} kg`}
                    subtext="Hazardous electronic waste diverted from landfill"
                    accentColor="cyan"
                    badgeText={isDemo ? "Demo" : "Live"}
                    delay={0.08}
                  />
                  <MetricCard
                    icon={DollarSign}
                    label="Money Saved"
                    value={`$${dashData.impact.moneySaved.toLocaleString()}`}
                    subtext="Estimated savings vs cost of full device replacement"
                    accentColor="yellow"
                    badgeText={isDemo ? "Demo" : "Live"}
                    delay={0.16}
                  />
                  <MetricCard
                    icon={Wrench}
                    label="Devices Repaired"
                    value={`${dashData.impact.repairCount}`}
                    subtext="Total repair actions that contributed to sustainability"
                    accentColor="green"
                    badgeText={isDemo ? "Demo" : "Live"}
                    delay={0.24}
                  />
                </div>
              </Section>

              {/* ── Trend Chart + Score ─────────────────────────────────────── */}
              <Section id="trends" title="Carbon Impact Trends" subtitle="CO₂ and savings over time">
                <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                  {/* Chart (2/3) */}
                  <GlassCard padding="lg" hoverEffect={false} className="lg:col-span-2">
                    <div className="flex items-center gap-2 mb-5">
                      <TrendingUp className="size-5 text-[#22C55E]" aria-hidden />
                      <h3 className="text-base font-bold text-white">Impact Over Time</h3>
                    </div>
                    <CarbonTrendChart data={dashData.trend} />
                  </GlassCard>

                  {/* Sustainability Score (1/3) */}
                  <GlassCard padding="lg" hoverEffect={false}>
                    <div className="flex items-center gap-2 mb-5">
                      <Activity className="size-5 text-[#06B6D4]" aria-hidden />
                      <h3 className="text-base font-bold text-white">Sustainability Score</h3>
                    </div>
                    <SustainabilityScore score={dashData.sustainabilityScore} />
                  </GlassCard>
                </div>
              </Section>

              {/* ── Repair vs Replace ───────────────────────────────────────── */}
              <Section
                id="comparison"
                title="Repair vs Replace"
                subtitle="Environmental impact comparison for common device types"
              >
                <GlassCard padding="lg" hoverEffect={false}>
                  <div className="flex items-center gap-2 mb-5">
                    <Scale className="size-5 text-[#22C55E]" aria-hidden />
                    <h3 className="text-base font-bold text-white">
                      Why Repair Wins Every Time
                    </h3>
                  </div>
                  <RepairVsReplacePanel />
                </GlassCard>
              </Section>

              {/* ── Environmental Breakdown ─────────────────────────────────── */}
              <Section
                id="breakdown"
                title="Environmental Impact Breakdown"
                subtitle="How your repairs contribute to sustainability goals"
              >
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
                  <BreakdownBar
                    label="CO₂ Footprint Avoided"
                    value={dashData.impact.co2Saved}
                    max={100}
                    unit="kg"
                    color="from-[#22C55E] to-[#06B6D4]"
                    description="Equivalent to planting approx. {n} trees for a year"
                    equiv={Math.round(dashData.impact.co2Saved / 21)}
                    delay={0}
                  />
                  <BreakdownBar
                    label="E-Waste Diverted"
                    value={dashData.impact.ewasteReduced}
                    max={10}
                    unit="kg"
                    color="from-[#06B6D4] to-[#0891B2]"
                    description="Hazardous materials prevented from entering landfill"
                    delay={0.1}
                  />
                  <BreakdownBar
                    label="Device Lifespan Extended"
                    value={dashData.impact.repairCount}
                    max={20}
                    unit=" repairs"
                    color="from-[#22C55E] to-[#16A34A]"
                    description="Each repair adds ~2 years to average device lifespan"
                    delay={0.2}
                  />
                </div>
              </Section>

              {/* ── Recent Activity ─────────────────────────────────────────── */}
              <Section
                id="activity"
                title="Recent Repair Impact"
                subtitle="Environmental contribution of your most recent repairs"
              >
                <GlassCard padding="lg" hoverEffect={false}>
                  <RecentActivityList activities={dashData.recentActivity} />
                </GlassCard>
              </Section>
            </div>
          )}
        </Container>
      </main>

      {/* ── Footer ──────────────────────────────────────────────────────────── */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>&copy; {new Date().getFullYear()} RepairVerse AI. Carbon Impact Dashboard.</p>
            <Link href="/" className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors">
              ← Back to RepairVerse AI
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}

// ─── Breakdown Bar ─────────────────────────────────────────────────────────
function BreakdownBar({
  label,
  value,
  max,
  unit,
  color,
  description,
  equiv,
  delay = 0,
}: {
  label: string;
  value: number;
  max: number;
  unit: string;
  color: string;
  description: string;
  equiv?: number;
  delay?: number;
}) {
  const pct = Math.min(100, (value / max) * 100);
  const desc = equiv !== undefined
    ? description.replace("{n}", String(equiv))
    : description;

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45, delay, ease: EASE }}
      className="rounded-3xl border border-white/10 bg-white/[0.04] p-5"
    >
      <p className="text-xs font-bold uppercase tracking-wider text-[#CBD5E1]">{label}</p>
      <p className="mt-3 text-2xl font-black text-white">
        {typeof value === "number" && value % 1 !== 0 ? value.toFixed(1) : value}
        <span className="text-sm font-semibold text-white/60">{unit}</span>
      </p>
      {/* Bar */}
      <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-white/[0.06]">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${pct}%` }}
          transition={{ duration: 1, delay: delay + 0.3, ease: EASE }}
          className={`h-full rounded-full bg-gradient-to-r ${color}`}
        />
      </div>
      <p className="mt-3 text-[11px] leading-relaxed text-white/50">{desc}</p>
    </motion.div>
  );
}
