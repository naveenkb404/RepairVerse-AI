"use client";

import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  CalendarCheck,
  AlertTriangle,
  Clock,
  CheckCircle2,
  Leaf,
  DollarSign,
  Plus,
  RefreshCw,
  Layers,
  Sparkles,
  ShieldCheck,
  Wrench,
} from "lucide-react";
import Navbar from "@/components/layout/Navbar";
import Footer from "@/components/layout/Footer";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import { MaintenanceTimeline } from "@/components/devices/MaintenanceTimeline";
import { MaintenanceCalendar } from "@/components/devices/MaintenanceCalendar";
import { useAuth } from "@/lib/context/AuthContext";
import {
  fetchMaintenanceSchedules,
  fetchMaintenanceCalendar,
  fetchMaintenanceSummary,
  updateMaintenanceStatus,
} from "@/lib/api/maintenance";
import type {
  MaintenanceSchedule,
  MaintenanceCalendarEvent,
  MaintenanceSummary,
  MaintenanceStatus,
} from "@/lib/types/maintenance";

export default function MaintenancePage() {
  const { token, isLoggedIn } = useAuth();

  const [schedules, setSchedules] = useState<MaintenanceSchedule[]>([]);
  const [calendarEvents, setCalendarEvents] = useState<MaintenanceCalendarEvent[]>([]);
  const [summary, setSummary] = useState<MaintenanceSummary | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<"TIMELINE" | "CALENDAR">("TIMELINE");

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [schedRes, calRes, sumRes] = await Promise.all([
        fetchMaintenanceSchedules(undefined, undefined, token),
        fetchMaintenanceCalendar(token),
        fetchMaintenanceSummary(token),
      ]);

      if (schedRes.data) setSchedules(schedRes.data);
      if (calRes.data) setCalendarEvents(calRes.data);
      if (sumRes.data) setSummary(sumRes.data);
    } catch (err) {
      console.error("Failed to load maintenance center data:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [token]);

  const handleStatusChange = async (id: string, newStatus: MaintenanceStatus) => {
    try {
      const res = await updateMaintenanceStatus(id, newStatus, token);
      if (res.success && res.data) {
        setSchedules((prev) =>
          prev.map((s) => (s.id === id ? res.data! : s))
        );
        // Refresh summary
        const sumRes = await fetchMaintenanceSummary(token);
        if (sumRes.data) setSummary(sumRes.data);
      }
    } catch (err) {
      console.error("Failed to update status:", err);
    }
  };

  return (
    <div className="min-h-screen bg-[#070b14] text-slate-100 flex flex-col font-sans selection:bg-emerald-500/30 selection:text-emerald-300">
      <Navbar />

      <main className="flex-1 py-10">
        <Container>
          {/* Header Banner */}
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
            <div>
              <div className="flex items-center gap-3">
                <div className="p-3 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400">
                  <CalendarCheck className="w-7 h-7" />
                </div>
                <div>
                  <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
                    Smart Maintenance Center
                  </h1>
                  <p className="text-xs sm:text-sm text-slate-400 mt-1">
                    Proactive device care automation, preventative service schedules, and unified lifecycle calendar.
                  </p>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <button
                onClick={loadData}
                disabled={isLoading}
                className="px-4 py-2.5 rounded-xl bg-white/5 hover:bg-white/10 border border-white/10 text-xs font-semibold text-slate-200 transition-all flex items-center gap-2"
              >
                <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? "animate-spin text-emerald-400" : ""}`} />
                <span>Refresh Center</span>
              </button>
            </div>
          </div>

          {/* Metric KPI Summary Grid */}
          <div className="grid grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
            <GlassCard className="p-4 relative overflow-hidden">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-slate-400">Upcoming</span>
                <Clock className="w-4 h-4 text-cyan-400" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-white">
                  {summary ? summary.totalUpcoming : "--"}
                </span>
                <span className="text-[10px] text-slate-500 font-medium">Tasks</span>
              </div>
            </GlassCard>

            <GlassCard className="p-4 relative overflow-hidden">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-amber-400">Due Soon</span>
                <AlertTriangle className="w-4 h-4 text-amber-400" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-amber-300">
                  {summary ? summary.totalDue : "--"}
                </span>
                <span className="text-[10px] text-slate-500 font-medium">≤ 3 days</span>
              </div>
            </GlassCard>

            <GlassCard className="p-4 relative overflow-hidden">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-red-400">Overdue</span>
                <AlertTriangle className="w-4 h-4 text-red-400" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-red-400">
                  {summary ? summary.totalOverdue : "--"}
                </span>
                <span className="text-[10px] text-slate-500 font-medium">Action required</span>
              </div>
            </GlassCard>

            <GlassCard className="p-4 relative overflow-hidden">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-emerald-400">Completed (Mo)</span>
                <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-emerald-300">
                  {summary ? summary.completedThisMonth : "--"}
                </span>
                <span className="text-[10px] text-slate-500 font-medium">Tasks</span>
              </div>
            </GlassCard>

            <GlassCard className="p-4 relative overflow-hidden col-span-2 lg:col-span-1">
              <div className="flex items-center justify-between">
                <span className="text-xs font-medium text-emerald-400">CO₂ Avoided</span>
                <Leaf className="w-4 h-4 text-emerald-400" />
              </div>
              <div className="mt-2 flex items-baseline gap-2">
                <span className="text-2xl font-black text-white">
                  {summary ? `${summary.totalCarbonSavingsIfCompleted} kg` : "--"}
                </span>
                <span className="text-[10px] text-emerald-400 font-medium">Lifecycle</span>
              </div>
            </GlassCard>
          </div>

          {/* View Mode Toggle */}
          <div className="flex items-center justify-between mb-6">
            <div className="flex rounded-xl bg-white/5 p-1 border border-white/10 text-xs">
              <button
                onClick={() => setActiveTab("TIMELINE")}
                className={`px-4 py-2 rounded-lg font-semibold transition-all flex items-center gap-2 ${
                  activeTab === "TIMELINE"
                    ? "bg-emerald-500/20 text-emerald-300 border border-emerald-500/30"
                    : "text-slate-400 hover:text-slate-200"
                }`}
              >
                <Layers className="w-4 h-4" />
                <span>Task Stream</span>
              </button>
              <button
                onClick={() => setActiveTab("CALENDAR")}
                className={`px-4 py-2 rounded-lg font-semibold transition-all flex items-center gap-2 ${
                  activeTab === "CALENDAR"
                    ? "bg-cyan-500/20 text-cyan-300 border border-cyan-500/30"
                    : "text-slate-400 hover:text-slate-200"
                }`}
              >
                <CalendarCheck className="w-4 h-4" />
                <span>Unified Calendar</span>
              </button>
            </div>
          </div>

          {/* Main Content Area */}
          <div className="space-y-6">
            {activeTab === "TIMELINE" ? (
              <MaintenanceTimeline
                schedules={schedules}
                onStatusChange={handleStatusChange}
                onRefresh={loadData}
                isLoading={isLoading}
              />
            ) : (
              <MaintenanceCalendar
                events={calendarEvents}
                isLoading={isLoading}
              />
            )}
          </div>
        </Container>
      </main>

      <Footer />
    </div>
  );
}
