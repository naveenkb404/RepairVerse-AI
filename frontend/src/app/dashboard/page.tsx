"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Activity,
  AlertOctagon,
  AlertTriangle,
  Bell,
  Calendar,
  CalendarCheck,
  Cpu,
  History,
  Leaf,
  MapPin,
  RefreshCcw,
  Sparkles,
  TrendingUp,
  Wrench,
  Zap,
} from "lucide-react";

import type { DashboardStats, ActivityItem } from "@/lib/types/user";
import type { RepairActionPlanData } from "@/lib/types/repairPlanning";
import type { MaintenanceSchedule, MaintenanceSummary } from "@/lib/types/maintenance";
import { fetchDashboardStats, fetchActivity } from "@/lib/api/user";
import { fetchUserActionPlans } from "@/lib/api/repairPlanning";
import { fetchMaintenanceSummary, fetchMaintenanceSchedules } from "@/lib/api/maintenance";
import { useAuth } from "@/lib/context/AuthContext";
import GlassButton from "@/components/common/GlassButton";
import PredictiveFleetWidget from "@/components/dashboard/PredictiveFleetWidget";
import { cn } from "@/lib/utils";



const EASE = [0.22, 1, 0.36, 1] as const;

// ---------------------------------------------------------------------------
// Quick Action Cards
// ---------------------------------------------------------------------------
const QUICK_ACTIONS = [
  {
    label: "AI Diagnosis",
    description: "Diagnose any device issue",
    href: "/diagnosis",
    icon: Sparkles,
    from: "from-[#22C55E]/20",
    to: "to-[#06B6D4]/10",
    iconColor: "text-[#22C55E]",
    border: "border-[#22C55E]/20 hover:border-[#22C55E]/40",
  },
  {
    label: "Device Passports",
    description: "View all registered devices",
    href: "/devices",
    icon: Cpu,
    from: "from-[#06B6D4]/20",
    to: "to-[#8B5CF6]/10",
    iconColor: "text-[#06B6D4]",
    border: "border-[#06B6D4]/20 hover:border-[#06B6D4]/40",
  },
  {
    label: "Repair History",
    description: "Browse all past repairs",
    href: "/repair-history",
    icon: History,
    from: "from-[#FACC15]/20",
    to: "to-[#F97316]/10",
    iconColor: "text-[#FACC15]",
    border: "border-[#FACC15]/20 hover:border-[#FACC15]/40",
  },
  {
    label: "Find Shops",
    description: "Nearby certified technicians",
    href: "/repair-shops",
    icon: MapPin,
    from: "from-[#F97316]/20",
    to: "to-[#EF4444]/10",
    iconColor: "text-[#F97316]",
    border: "border-[#F97316]/20 hover:border-[#F97316]/40",
  },
  {
    label: "Recommendations",
    description: "Repair vs replace analysis",
    href: "/recommendation",
    icon: Zap,
    from: "from-[#8B5CF6]/20",
    to: "to-[#06B6D4]/10",
    iconColor: "text-[#8B5CF6]",
    border: "border-[#8B5CF6]/20 hover:border-[#8B5CF6]/40",
  },
  {
    label: "Smart Maintenance",
    description: "Proactive care & schedules",
    href: "/maintenance",
    icon: CalendarCheck,
    from: "from-[#22C55E]/20",
    to: "to-[#06B6D4]/10",
    iconColor: "text-emerald-400",
    border: "border-emerald-500/20 hover:border-emerald-500/40",
  },
  {
    label: "Carbon Impact",
    description: "Track environmental savings",
    href: "/carbon",
    icon: Leaf,
    from: "from-[#22C55E]/20",
    to: "to-[#16A34A]/10",
    iconColor: "text-[#22C55E]",
    border: "border-[#22C55E]/20 hover:border-[#22C55E]/40",
  },
];

// ---------------------------------------------------------------------------
// Stat Card
// ---------------------------------------------------------------------------
function StatCard({
  icon: Icon,
  label,
  value,
  subtext,
  accentColor,
  delay,
}: {
  icon: React.ElementType;
  label: string;
  value: string;
  subtext?: string;
  accentColor: "green" | "cyan" | "yellow" | "purple";
  delay?: number;
}) {
  const accent = {
    green: {
      icon: "from-[#22C55E] to-[#16A34A]",
      badge: "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]",
      glow: "rgba(34,197,94,0.2)",
    },
    cyan: {
      icon: "from-[#06B6D4] to-[#0891B2]",
      badge: "border-[#06B6D4]/30 bg-[#06B6D4]/10 text-[#06B6D4]",
      glow: "rgba(6,182,212,0.2)",
    },
    yellow: {
      icon: "from-[#FACC15] to-[#D97706]",
      badge: "border-[#FACC15]/30 bg-[#FACC15]/10 text-[#FACC15]",
      glow: "rgba(250,204,21,0.18)",
    },
    purple: {
      icon: "from-[#8B5CF6] to-[#7C3AED]",
      badge: "border-[#8B5CF6]/30 bg-[#8B5CF6]/10 text-[#8B5CF6]",
      glow: "rgba(139,92,246,0.18)",
    },
  }[accentColor];

  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay: delay ?? 0, ease: EASE }}
      className="group relative"
    >
      <div
        className="pointer-events-none absolute -inset-px rounded-3xl opacity-0 blur-xl transition-opacity duration-500 group-hover:opacity-100"
        style={{ background: `radial-gradient(circle at 50% 0%, ${accent.glow}, transparent 70%)` }}
        aria-hidden
      />
      <div className="relative flex h-full flex-col rounded-3xl border border-white/10 bg-white/[0.06] p-6 shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.12)] backdrop-blur-xl transition-all duration-300 group-hover:border-white/20 group-hover:bg-white/[0.08]">
        <div className="flex items-center justify-between mb-5">
          <div
            className={cn(
              "flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br",
              accent.icon
            )}
          >
            <Icon className="size-6 text-white" aria-hidden />
          </div>
          <span className={cn("rounded-full border px-3 py-1 text-[11px] font-semibold", accent.badge)}>
            Live
          </span>
        </div>
        <p className="text-3xl font-bold tracking-tight text-white sm:text-4xl">{value}</p>
        <h3 className="mt-1.5 text-sm font-semibold text-white/90 sm:text-base">{label}</h3>
        {subtext && <p className="mt-1 text-xs leading-relaxed text-[#CBD5E1]">{subtext}</p>}
      </div>
    </motion.div>
  );
}

// ---------------------------------------------------------------------------
// Activity Row
// ---------------------------------------------------------------------------
const ACTIVITY_ICON_COLORS = {
  green: "from-[#22C55E] to-[#16A34A]",
  cyan: "from-[#06B6D4] to-[#0891B2]",
  yellow: "from-[#FACC15] to-[#D97706]",
  red: "from-[#EF4444] to-[#DC2626]",
};

function ActivityRow({ item }: { item: ActivityItem }) {
  const iconGrad = ACTIVITY_ICON_COLORS[item.iconColor ?? "green"];
  const date = new Date(item.timestamp);
  const relTime = formatRelative(date);

  return (
    <div className="flex items-start gap-4 py-4 border-b border-white/[0.06] last:border-0">
      <div
        className={cn(
          "mt-0.5 flex size-9 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br shadow-[0_4px_12px_rgba(0,0,0,0.2)]",
          iconGrad
        )}
      >
        <Activity className="size-4 text-white" aria-hidden />
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-sm font-semibold text-white">{item.title}</p>
        <p className="mt-0.5 text-xs leading-relaxed text-white/50">{item.description}</p>
        {item.deviceName && (
          <span className="mt-1.5 inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.06] px-2 py-0.5 text-[10px] font-medium text-white/60">
            <Cpu className="size-3" aria-hidden />
            {item.deviceName}
          </span>
        )}
      </div>
      <span className="shrink-0 text-[11px] text-white/30">{relTime}</span>
    </div>
  );
}

function formatRelative(date: Date): string {
  const diff = Date.now() - date.getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(diff / 3600000);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(diff / 86400000);
  return `${days}d ago`;
}

// ---------------------------------------------------------------------------
// Health Score Ring
// ---------------------------------------------------------------------------
function HealthRing({ score }: { score: number }) {
  const radius = 42;
  const circumference = 2 * Math.PI * radius;
  const filled = (score / 100) * circumference;
  const color = score >= 80 ? "#22C55E" : score >= 60 ? "#FACC15" : "#EF4444";

  return (
    <div className="relative flex items-center justify-center size-28">
      <svg viewBox="0 0 100 100" className="size-28 -rotate-90">
        <circle
          cx="50" cy="50" r={radius}
          fill="none"
          stroke="rgba(255,255,255,0.08)"
          strokeWidth="8"
        />
        <circle
          cx="50" cy="50" r={radius}
          fill="none"
          stroke={color}
          strokeWidth="8"
          strokeLinecap="round"
          strokeDasharray={circumference}
          strokeDashoffset={circumference - filled}
          style={{ filter: `drop-shadow(0 0 6px ${color}80)`, transition: "stroke-dashoffset 1s ease" }}
        />
      </svg>
      <div className="absolute inset-0 flex flex-col items-center justify-center">
        <span className="text-2xl font-bold text-white">{score}</span>
        <span className="text-[10px] text-white/50 font-medium">/ 100</span>
      </div>
    </div>
  );
}

// ---------------------------------------------------------------------------
// Main Dashboard Page
// ---------------------------------------------------------------------------
export default function DashboardPage() {
  const { user, token } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [activity, setActivity] = useState<ActivityItem[]>([]);
  const [actionPlans, setActionPlans] = useState<RepairActionPlanData[]>([]);
  const [maintenanceSummary, setMaintenanceSummary] = useState<MaintenanceSummary | null>(null);
  const [dueTasks, setDueTasks] = useState<MaintenanceSchedule[]>([]);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    const [statsRes, actRes, plansRes, sumRes, tasksRes] = await Promise.all([
      fetchDashboardStats(token ?? ""),
      fetchActivity(token ?? ""),
      fetchUserActionPlans(token ?? ""),
      fetchMaintenanceSummary(token ?? ""),
      fetchMaintenanceSchedules(undefined, undefined, token ?? ""),
    ]);
    if (statsRes.data) setStats(statsRes.data);
    if (actRes.data) setActivity(actRes.data);
    if (plansRes.data) setActionPlans(plansRes.data);
    if (sumRes.data) setMaintenanceSummary(sumRes.data);
    if (tasksRes.data) setDueTasks(tasksRes.data.slice(0, 3));
    setLoading(false);
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const hour = new Date().getHours();
  const greeting =
    hour < 12 ? "Good morning" : hour < 18 ? "Good afternoon" : "Good evening";
  const firstName = user?.fullName?.split(" ")[0] ?? "there";

  return (
    <div className="relative min-h-full overflow-hidden bg-[#0B1120] px-6 py-8 md:px-8 lg:px-10">
      {/* Background radials */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.08),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.07),transparent_50%)]"
        aria-hidden
      />

      <div className="relative mx-auto max-w-6xl space-y-10">

        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, ease: EASE }}
          className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
        >
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
              {greeting}, {firstName} 👋
            </h1>
            <p className="mt-1 text-sm text-white/50">
              Here&apos;s an overview of your devices and repair activity.
            </p>
          </div>
          <div className="flex items-center gap-3">
            <Link
              href="/dashboard/notifications"
              className="relative flex size-10 items-center justify-center rounded-2xl border border-white/15 bg-white/[0.06] text-white/60 transition-all hover:border-white/25 hover:bg-white/[0.10] hover:text-white"
              aria-label="Notifications"
            >
              <Bell className="size-4.5" />
              <span className="absolute -right-0.5 -top-0.5 flex size-4 items-center justify-center rounded-full bg-[#06B6D4] text-[9px] font-bold text-white">
                3
              </span>
            </Link>
            <button
              onClick={load}
              className="flex size-10 items-center justify-center rounded-2xl border border-white/15 bg-white/[0.06] text-white/60 transition-all hover:border-white/25 hover:bg-white/[0.10] hover:text-white focus:outline-none focus:ring-2 focus:ring-[#22C55E]/40"
              aria-label="Refresh dashboard"
            >
              <RefreshCcw className={cn("size-4.5", loading && "animate-spin")} />
            </button>
            <GlassButton href="/diagnosis" size="sm" icon={<Sparkles className="size-4" />}>
              Run AI Diagnosis
            </GlassButton>
          </div>
        </motion.div>

        {/* Offline demo notice */}
        {token === "demo-offline-token" && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            className="rounded-3xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-5 py-4 text-sm text-[#06B6D4]"
          >
            <span className="font-bold">Demo Session Active</span>
            <span className="ml-2 text-white/60">
              — You&apos;re exploring RepairVerse AI in demo mode. Sample data is shown because the Spring Boot backend is currently offline at{" "}
              <code className="rounded bg-white/10 px-1 text-xs">localhost:8080</code>.
            </span>
          </motion.div>
        )}

        {/* Stats Grid */}
        {loading ? (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="h-40 animate-pulse rounded-3xl bg-white/[0.04]" />
            ))}
          </div>
        ) : stats ? (
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <StatCard
              icon={Cpu}
              label="Registered Devices"
              value={String(stats.totalDevices)}
              subtext="Active devices in your passport"
              accentColor="cyan"
              delay={0}
            />
            <StatCard
              icon={Wrench}
              label="Total Repairs"
              value={String(stats.totalRepairs)}
              subtext={`${stats.activeRepairs} repair${stats.activeRepairs !== 1 ? "s" : ""} currently active`}
              accentColor="green"
              delay={0.08}
            />
            <StatCard
              icon={TrendingUp}
              label="Money Saved"
              value={`$${stats.totalMoneySaved.toLocaleString()}`}
              subtext="Compared to full replacement cost"
              accentColor="yellow"
              delay={0.16}
            />
            <StatCard
              icon={Leaf}
              label="CO₂ Prevented"
              value={`${stats.totalCarbonSaved} kg`}
              subtext="Carbon emissions avoided by repairing"
              accentColor="green"
              delay={0.24}
            />
          </div>
        ) : null}

        {/* Proactive Device Care (Phase 25) */}
        {maintenanceSummary && (maintenanceSummary.totalDue > 0 || maintenanceSummary.totalOverdue > 0 || dueTasks.length > 0) && (
          <motion.section
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.22, ease: EASE }}
            className="rounded-2xl border border-emerald-500/20 bg-gradient-to-r from-emerald-500/[0.08] via-cyan-500/[0.04] to-transparent p-5 backdrop-blur-xl"
          >
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4">
              <div className="flex items-center gap-3">
                <div className="p-2.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400">
                  <CalendarCheck className="size-5" />
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h2 className="text-base font-bold text-white">Proactive Device Care</h2>
                    {maintenanceSummary.totalOverdue > 0 && (
                      <span className="rounded-full bg-red-500/20 border border-red-500/40 px-2 py-0.5 text-[10px] font-bold text-red-300 animate-pulse">
                        {maintenanceSummary.totalOverdue} Overdue
                      </span>
                    )}
                    {maintenanceSummary.totalDue > 0 && (
                      <span className="rounded-full bg-amber-500/20 border border-amber-500/40 px-2 py-0.5 text-[10px] font-bold text-amber-300">
                        {maintenanceSummary.totalDue} Due Soon
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-white/50 mt-0.5">
                    Proactive maintenance tasks automatically prioritized to prevent hardware degradation.
                  </p>
                </div>
              </div>

              <Link
                href="/maintenance"
                className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 border border-emerald-500/40 text-emerald-300 text-xs font-semibold transition-all self-start sm:self-auto"
              >
                <span>Maintenance Center</span>
                <ChevronRight className="size-3.5" />
              </Link>
            </div>

            {/* Task Preview Cards */}
            <div className="grid gap-3 sm:grid-cols-3">
              {dueTasks.map((task) => (
                <Link
                  key={task.id}
                  href="/maintenance"
                  className={`p-3.5 rounded-xl border transition-all ${
                    task.status === "OVERDUE"
                      ? "bg-red-500/10 border-red-500/30 hover:border-red-500/50"
                      : task.status === "DUE"
                      ? "bg-amber-500/10 border-amber-500/30 hover:border-amber-500/50"
                      : "bg-white/[0.03] border-white/10 hover:border-white/20"
                  }`}
                >
                  <div className="flex items-center justify-between gap-2 mb-1.5">
                    <span className="text-[11px] font-semibold text-white/70 truncate">
                      {task.deviceName}
                    </span>
                    <span
                      className={`text-[9px] font-bold px-1.5 py-0.2 rounded uppercase ${
                        task.status === "OVERDUE"
                          ? "bg-red-600 text-white"
                          : task.status === "DUE"
                          ? "bg-amber-500/30 text-amber-300"
                          : "bg-white/10 text-white/60"
                      }`}
                    >
                      {task.status}
                    </span>
                  </div>
                  <p className="text-xs font-semibold text-white line-clamp-1">
                    {task.title}
                  </p>
                  <p className="text-[11px] text-white/40 mt-1">
                    Due: <strong className="text-white/70">{task.dueDate}</strong>
                  </p>
                </Link>
              ))}
            </div>
          </motion.section>
        )}

        {/* Actionable Repair Plans & Lifecycle Roadmaps (Phase 24) */}
        {actionPlans.length > 0 && (
          <motion.section
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.25, ease: EASE }}
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <h2 className="text-lg font-bold text-white">Actionable Repair Roadmaps</h2>
                <span className="rounded-full bg-emerald-500/10 border border-emerald-500/30 px-2 py-0.5 text-[10px] font-bold text-emerald-400">
                  {actionPlans.length} Active
                </span>
              </div>
              <Link
                href="/devices"
                className="text-xs font-semibold text-[#22C55E] hover:text-[#06B6D4] transition-colors"
              >
                View all devices →
              </Link>
            </div>

            <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
              {actionPlans.map((plan) => (
                <Link
                  key={plan.id}
                  href={`/devices/${plan.deviceId}`}
                  className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 hover:border-white/20 hover:bg-white/[0.07] transition-all flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <span className="text-xs font-bold text-white truncate">
                        {plan.deviceName}
                      </span>
                      <span
                        className={`rounded-full px-2 py-0.5 text-[10px] font-bold uppercase tracking-wider ${
                          plan.priorityLevel === "CRITICAL"
                            ? "bg-red-500/20 text-red-400 border border-red-500/30"
                            : plan.priorityLevel === "HIGH"
                            ? "bg-orange-500/20 text-orange-400 border border-orange-500/30"
                            : "bg-emerald-500/20 text-emerald-400 border border-emerald-500/30"
                        }`}
                      >
                        {plan.overallStrategy.replace(/_/g, " ")}
                      </span>
                    </div>

                    <p className="text-xs text-white/50 line-clamp-2 mb-3">
                      {plan.strategyRationale}
                    </p>
                  </div>

                  <div className="flex items-center justify-between border-t border-white/10 pt-2.5 text-[11px]">
                    <span className="text-white/60">
                      Est. ${plan.estimatedTotalCost.toFixed(2)}
                    </span>
                    <span className="font-bold text-cyan-400">
                      +{plan.estimatedLifecycleExtensionMonths} mos lifespan
                    </span>
                  </div>
                </Link>
              ))}
            </div>
          </motion.section>
        )}

        {/* Quick Actions */}
        <motion.section
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3, ease: EASE }}
        >
          <h2 className="mb-4 text-lg font-bold text-white">Quick Actions</h2>
          <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {QUICK_ACTIONS.map((action) => {
              const Icon = action.icon;
              return (
                <Link
                  key={action.href}
                  href={action.href}
                  className={cn(
                    "group flex items-center gap-4 rounded-2xl border bg-gradient-to-r px-5 py-4 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[#22C55E]/40",
                    action.from,
                    action.to,
                    action.border
                  )}
                >
                  <div className="flex size-10 shrink-0 items-center justify-center rounded-xl border border-white/10 bg-white/[0.08] transition-colors group-hover:bg-white/[0.12]">
                    <Icon className={cn("size-5", action.iconColor)} aria-hidden />
                  </div>
                  <div className="min-w-0">
                    <p className="text-sm font-bold text-white">{action.label}</p>
                    <p className="text-xs text-white/50">{action.description}</p>
                  </div>
                  <ChevronRight className="ml-auto size-4 shrink-0 text-white/20 transition-transform group-hover:translate-x-0.5 group-hover:text-white/40" />
                </Link>
              );
            })}
          </div>
        </motion.section>

        {/* Activity + Health Score */}
        <div className="grid gap-6 lg:grid-cols-3">
          {/* Activity Feed */}
          <motion.section
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.35, ease: EASE }}
            className="lg:col-span-2"
          >
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-bold text-white">Recent Activity</h2>
              <Link
                href="/repair-history"
                className="text-xs font-semibold text-[#22C55E] hover:text-[#06B6D4] transition-colors"
              >
                View all →
              </Link>
            </div>
            <div className="rounded-3xl border border-white/10 bg-white/[0.06] px-6 py-2 backdrop-blur-xl shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.08)]">
              {activity.length === 0 ? (
                <p className="py-12 text-center text-sm text-white/40">No recent activity yet.</p>
              ) : (
                activity.slice(0, 5).map((item) => (
                  <ActivityRow key={item.id} item={item} />
                ))
              )}
            </div>
          </motion.section>

          {/* Health Score + profile summary */}
          <motion.section
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.4, ease: EASE }}
            className="flex flex-col gap-6"
          >
            {/* Health Ring */}
            <div className="rounded-3xl border border-white/10 bg-white/[0.06] p-6 backdrop-blur-xl text-center shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.08)]">
              <h2 className="mb-4 text-sm font-bold uppercase tracking-widest text-white/40">
                Fleet Health Score
              </h2>
              <div className="flex justify-center">
                <HealthRing score={stats?.healthScore ?? 0} />
              </div>
              <p className="mt-4 text-xs text-white/50">
                Based on battery health, repair history, and device age across all registered devices.
              </p>
              <div className="mt-4">
                <GlassButton href="/devices" variant="secondary" size="sm" fullWidth>
                  View Devices
                </GlassButton>
              </div>
            </div>

            {/* Profile summary */}
            <div className="rounded-3xl border border-white/10 bg-white/[0.06] p-6 backdrop-blur-xl shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.08)]">
              <h2 className="mb-4 text-sm font-bold uppercase tracking-widest text-white/40">
                Your Account
              </h2>
              <div className="space-y-2.5 text-sm">
                <div className="flex justify-between">
                  <span className="text-white/50">Name</span>
                  <span className="font-semibold text-white">{user?.fullName}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-white/50">Role</span>
                  <span className="font-semibold capitalize text-[#22C55E]">{user?.role?.toLowerCase()}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-white/50">Status</span>
                  <span className="font-semibold text-[#22C55E]">
                    {user?.verified ? "✓ Verified" : "Unverified"}
                  </span>
                </div>
              </div>
              <div className="mt-4">
                <GlassButton href="/dashboard/profile" variant="secondary" size="sm" fullWidth>
                  Edit Profile
                </GlassButton>
              </div>
            </div>
          </motion.section>
        </div>

      </div>
    </div>
  );
}

// Need to import ChevronRight for the quick actions grid
function ChevronRight({ className }: { className?: string }) {
  return (
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={2}
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
      aria-hidden
    >
      <path d="m9 18 6-6-6-6" />
    </svg>
  );
}
