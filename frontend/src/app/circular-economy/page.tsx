"use client";

import { useEffect, useState, useCallback } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Sparkles,
  RefreshCw,
  Plus,
  X,
  Wrench,
  Cloud,
  Trash2,
  IndianRupee,
  Clock,
  ShieldCheck,
} from "lucide-react";
import { circularEconomyApi } from "@/lib/api/circularEconomy";
import type {
  CircularImpactDashboard,
  CreateGoalPayload,
  RecordImpactEventPayload,
  CircularEventType,
} from "@/lib/types/circularEconomy";
import Container from "@/components/layout/Container";
import CircularImpactHero from "@/components/circular/CircularImpactHero";
import CircularImpactScoreGauge from "@/components/circular/CircularImpactScore";
import ImpactMetricsGrid from "@/components/circular/ImpactMetricsGrid";
import SustainabilityRecommendations from "@/components/circular/SustainabilityRecommendations";
import SustainabilityGoals from "@/components/circular/SustainabilityGoals";
import AchievementGallery from "@/components/circular/AchievementGallery";
import CircularImpactTimeline from "@/components/circular/CircularImpactTimeline";

export default function CircularEconomyPage() {
  const [dashboard, setDashboard] = useState<CircularImpactDashboard | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isRecordModalOpen, setIsRecordModalOpen] = useState(false);
  const [isGoalModalOpen, setIsGoalModalOpen] = useState(false);

  // Record Event Form State
  const [eventType, setEventType] = useState<CircularEventType>("REPAIR_COMPLETED");
  const [carbonSaved, setCarbonSaved] = useState<number>(45.0);
  const [ewastePrevented, setEwastePrevented] = useState<number>(1.5);
  const [moneySaved, setMoneySaved] = useState<number>(3500);
  const [daysExtended, setDaysExtended] = useState<number>(180);
  const [isSubmittingEvent, setIsSubmittingEvent] = useState(false);

  const fetchDashboard = useCallback(async () => {
    try {
      setIsLoading(true);
      const data = await circularEconomyApi.getDashboard();
      setDashboard(data);
    } catch (err) {
      console.error("Failed to load circular economy dashboard:", err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboard();
  }, [fetchDashboard]);

  const handleCreateGoal = async (payload: CreateGoalPayload) => {
    await circularEconomyApi.createGoal(payload);
    await fetchDashboard();
  };

  const handleDeleteGoal = async (goalId: string) => {
    await circularEconomyApi.deleteGoal(goalId);
    await fetchDashboard();
  };

  const handleRecordEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmittingEvent(true);
    try {
      await circularEconomyApi.recordEvent({
        eventType,
        carbonSavedKg: carbonSaved,
        ewastePreventedKg: ewastePrevented,
        moneySaved: moneySaved,
        deviceLifeExtensionDays: daysExtended,
        impactSource: "USER_ACTION",
      });
      setIsRecordModalOpen(false);
      await fetchDashboard();
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmittingEvent(false);
    }
  };

  if (isLoading || !dashboard) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white py-12">
        <Container>
          <div className="flex flex-col items-center justify-center py-28 space-y-4">
            <RefreshCw className="size-8 animate-spin text-emerald-400" />
            <div className="text-sm font-semibold text-slate-400">
              Synthesizing circular economy impact metrics...
            </div>
          </div>
        </Container>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0B1120] text-slate-100 py-8 md:py-12">
      <Container>
        <div className="space-y-8 md:space-y-12">
          {/* Header Strip */}
          <div className="flex flex-wrap items-center justify-between gap-4 border-b border-white/10 pb-4">
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-400">
              <span className="flex size-2 rounded-full bg-emerald-400 animate-pulse" />
              <span>RepairVerse AI Circular Intelligence System</span>
            </div>

            <button
              onClick={fetchDashboard}
              className="inline-flex items-center gap-1.5 rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-xs font-semibold text-slate-300 hover:bg-white/10 active:scale-95 transition-all"
            >
              <RefreshCw className="size-3.5" />
              Refresh Intelligence
            </button>
          </div>

          {/* 1. Hero */}
          <CircularImpactHero
            score={dashboard.impactScore}
            metrics={dashboard.impactMetrics}
            onOpenCreateGoal={() => setIsGoalModalOpen(true)}
            onOpenRecordEvent={() => setIsRecordModalOpen(true)}
          />

          {/* 2. Impact Metrics Grid */}
          <ImpactMetricsGrid metrics={dashboard.impactMetrics} />

          {/* 3. Circular Impact Score Gauge & Factor Breakdown */}
          <CircularImpactScoreGauge scoreData={dashboard.impactScore} />

          {/* 4. Personalized Recommendations */}
          <SustainabilityRecommendations recommendations={dashboard.nextActions} />

          {/* 5. Sustainability Goals */}
          <SustainabilityGoals
            goals={dashboard.activeGoals}
            onCreateGoal={handleCreateGoal}
            onDeleteGoal={handleDeleteGoal}
          />

          {/* 6. Achievement Gallery */}
          <AchievementGallery achievements={dashboard.achievements} />

          {/* 7. Chronological Timeline */}
          <CircularImpactTimeline events={dashboard.recentEvents} />
        </div>
      </Container>

      {/* Record Impact Action Modal */}
      <AnimatePresence>
        {isRecordModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="relative w-full max-w-lg rounded-3xl border border-white/15 bg-[#0F172A] p-6 shadow-2xl space-y-5"
            >
              <div className="flex items-center justify-between border-b border-white/10 pb-3">
                <h3 className="text-lg font-bold text-white flex items-center gap-2">
                  <Wrench className="size-5 text-emerald-400" />
                  Record Circular Impact Milestone
                </h3>
                <button
                  onClick={() => setIsRecordModalOpen(false)}
                  className="text-slate-400 hover:text-white transition-colors"
                >
                  <X className="size-5" />
                </button>
              </div>

              <form onSubmit={handleRecordEvent} className="space-y-4">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Action Type</label>
                  <select
                    value={eventType}
                    onChange={(e) => setEventType(e.target.value as CircularEventType)}
                    className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                  >
                    <option value="REPAIR_COMPLETED">Hardware Repair Completed</option>
                    <option value="MAINTENANCE_COMPLETED">Preventative Maintenance Executed</option>
                    <option value="COMPONENT_UPGRADE">Component / Module Upgrade</option>
                    <option value="DEVICE_REFURBISHED">Hardware Refurbishment Completed</option>
                    <option value="DEVICE_DONATED">Device Donated for Secondary Use</option>
                    <option value="DEVICE_RECYCLED">Certified Eco-Recycling / Decommission</option>
                    <option value="RESPONSIBLE_DISPOSAL">Responsible Component Disposal</option>
                  </select>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-semibold text-slate-300">CO₂ Avoided (kg)</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      value={carbonSaved}
                      onChange={(e) => setCarbonSaved(parseFloat(e.target.value) || 0)}
                      className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                      required
                    />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-slate-300">E-Waste Prevented (kg)</label>
                    <input
                      type="number"
                      step="any"
                      min="0"
                      value={ewastePrevented}
                      onChange={(e) => setEwastePrevented(parseFloat(e.target.value) || 0)}
                      className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="text-xs font-semibold text-slate-300">Money Saved (₹)</label>
                    <input
                      type="number"
                      min="0"
                      value={moneySaved}
                      onChange={(e) => setMoneySaved(parseFloat(e.target.value) || 0)}
                      className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                      required
                    />
                  </div>
                  <div>
                    <label className="text-xs font-semibold text-slate-300">Life Added (Days)</label>
                    <input
                      type="number"
                      min="0"
                      value={daysExtended}
                      onChange={(e) => setDaysExtended(parseInt(e.target.value, 10) || 0)}
                      className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                      required
                    />
                  </div>
                </div>

                <div className="flex items-center justify-end gap-3 pt-3 border-t border-white/10">
                  <button
                    type="button"
                    onClick={() => setIsRecordModalOpen(false)}
                    className="rounded-xl border border-white/10 px-4 py-2 text-xs font-semibold text-slate-300 hover:bg-white/5 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isSubmittingEvent}
                    className="rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 px-5 py-2 text-xs font-bold text-white shadow-lg shadow-emerald-500/20 hover:brightness-110 active:scale-95 transition-all"
                  >
                    {isSubmittingEvent ? "Recording..." : "Log Circular Action"}
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
