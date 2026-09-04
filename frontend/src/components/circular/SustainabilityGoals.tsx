"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Target, Plus, CheckCircle, Trash2, Calendar, X, Cloud, IndianRupee, Clock, Wrench } from "lucide-react";
import type { SustainabilityGoal, CreateGoalPayload, SustainabilityGoalType } from "@/lib/types/circularEconomy";

interface SustainabilityGoalsProps {
  goals: SustainabilityGoal[];
  onCreateGoal: (payload: CreateGoalPayload) => Promise<void>;
  onDeleteGoal: (goalId: string) => Promise<void>;
}

const GOAL_ICONS: Record<string, any> = {
  CARBON_REDUCTION: Cloud,
  EWASTE_PREVENTION: Trash2,
  DEVICE_LIFE_EXTENSION: Clock,
  REPAIR_COUNT: Wrench,
  MONEY_SAVED: IndianRupee,
};

const GOAL_UNITS: Record<string, string> = {
  CARBON_REDUCTION: "kg CO₂",
  EWASTE_PREVENTION: "kg e-waste",
  DEVICE_LIFE_EXTENSION: "days extended",
  REPAIR_COUNT: "repairs",
  MONEY_SAVED: "₹ saved",
};

export default function SustainabilityGoals({
  goals,
  onCreateGoal,
  onDeleteGoal,
}: SustainabilityGoalsProps) {
  const [activeTab, setActiveTab] = useState<"ACTIVE" | "COMPLETED">("ACTIVE");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Form State
  const [goalType, setGoalType] = useState<SustainabilityGoalType>("CARBON_REDUCTION");
  const [targetValue, setTargetValue] = useState<number>(100);
  const [targetMonths, setTargetMonths] = useState<number>(6);

  const filteredGoals = (goals || []).filter((g) =>
    activeTab === "COMPLETED" ? g.isCompleted : !g.isCompleted
  );

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (targetValue <= 0) return;

    setIsSubmitting(true);
    try {
      const targetDate = new Date();
      targetDate.setMonth(targetDate.getMonth() + targetMonths);

      await onCreateGoal({
        goalType,
        targetValue,
        targetDate: targetDate.toISOString(),
      });
      setIsModalOpen(false);
      setTargetValue(100);
    } catch (err) {
      console.error(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-xl md:text-2xl font-bold text-white flex items-center gap-2.5">
            <Target className="size-6 text-emerald-400" />
            Sustainability Target Directives & Goals
          </h2>
          <p className="text-xs md:text-sm text-slate-400 mt-1">
            Track quantifiable milestones updated automatically from verified diagnostic, repair, and care events.
          </p>
        </div>

        <div className="flex items-center gap-3">
          {/* Tabs */}
          <div className="flex rounded-xl bg-white/5 p-1 border border-white/10 text-xs font-semibold">
            <button
              onClick={() => setActiveTab("ACTIVE")}
              className={`rounded-lg px-3 py-1.5 transition-all ${
                activeTab === "ACTIVE"
                  ? "bg-emerald-500 text-white shadow-sm"
                  : "text-slate-400 hover:text-white"
              }`}
            >
              Active Goals
            </button>
            <button
              onClick={() => setActiveTab("COMPLETED")}
              className={`rounded-lg px-3 py-1.5 transition-all ${
                activeTab === "COMPLETED"
                  ? "bg-emerald-500 text-white shadow-sm"
                  : "text-slate-400 hover:text-white"
              }`}
            >
              Completed ({goals.filter((g) => g.isCompleted).length})
            </button>
          </div>

          <button
            onClick={() => setIsModalOpen(true)}
            className="inline-flex items-center gap-1.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 px-3.5 py-1.5 text-xs font-bold text-white shadow-md hover:brightness-110 active:scale-95 transition-all"
          >
            <Plus className="size-3.5" />
            Create Goal
          </button>
        </div>
      </div>

      {/* Goals Grid */}
      {filteredGoals.length === 0 ? (
        <div className="rounded-3xl border border-white/10 bg-[#0B1120]/70 p-8 text-center backdrop-blur-xl">
          <Target className="size-10 text-slate-500 mx-auto" />
          <h3 className="mt-3 text-base font-bold text-white">
            {activeTab === "ACTIVE" ? "No Active Goals" : "No Completed Goals Yet"}
          </h3>
          <p className="text-xs text-slate-400 mt-1 max-w-md mx-auto">
            {activeTab === "ACTIVE"
              ? "Set a personal sustainability directive to focus your repair, maintenance, and hardware lifecycle efforts."
              : "Completed milestones will appear here as you log repair, upgrade, and circular actions."}
          </p>
          {activeTab === "ACTIVE" && (
            <button
              onClick={() => setIsModalOpen(true)}
              className="mt-4 inline-flex items-center gap-1.5 rounded-xl bg-emerald-500/20 border border-emerald-500/30 px-4 py-2 text-xs font-bold text-emerald-400 hover:bg-emerald-500/30 transition-all"
            >
              <Plus className="size-3.5" />
              Set First Target
            </button>
          )}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {filteredGoals.map((goal, idx) => {
            const Icon = GOAL_ICONS[goal.goalType] || Target;
            const unit = GOAL_UNITS[goal.goalType] || "";
            const isCompleted = goal.isCompleted;

            return (
              <motion.div
                key={goal.id || idx}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className={`relative rounded-3xl border ${
                  isCompleted ? "border-emerald-500/30 bg-emerald-950/20" : "border-white/10 bg-[#0B1120]/80"
                } p-5 backdrop-blur-xl shadow-lg space-y-4`}
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-2.5">
                    <div className="rounded-xl bg-white/5 p-2 text-emerald-400">
                      <Icon className="size-4" />
                    </div>
                    <div>
                      <div className="text-xs font-bold text-white uppercase tracking-wider">
                        {goal.goalType.replace("_", " ")}
                      </div>
                      <div className="text-[11px] text-slate-400 flex items-center gap-1 mt-0.5">
                        <Calendar className="size-3" />
                        <span>
                          Target:{" "}
                          {goal.targetDate
                            ? new Date(goal.targetDate).toLocaleDateString()
                            : "Ongoing"}
                        </span>
                      </div>
                    </div>
                  </div>

                  <button
                    onClick={() => onDeleteGoal(goal.id)}
                    className="text-slate-500 hover:text-rose-400 transition-colors p-1"
                    title="Remove Goal"
                  >
                    <Trash2 className="size-3.5" />
                  </button>
                </div>

                {/* Progress Bar & Numerical Metrics */}
                <div className="space-y-1.5">
                  <div className="flex justify-between text-xs">
                    <span className="text-slate-400">Current Progress</span>
                    <span className="font-bold text-white">
                      {goal.currentValue} / {goal.targetValue} {unit}
                    </span>
                  </div>

                  <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
                    <motion.div
                      className="h-full rounded-full bg-gradient-to-r from-emerald-500 to-cyan-400"
                      initial={{ width: 0 }}
                      animate={{ width: `${goal.progressPercentage}%` }}
                      transition={{ duration: 0.8 }}
                    />
                  </div>

                  <div className="flex justify-between text-[11px] font-medium text-slate-400">
                    <span className="text-emerald-400 font-bold">{goal.progressPercentage}% Achieved</span>
                    <span>
                      {isCompleted
                        ? "Completed 🎉"
                        : `${goal.remainingValue} ${unit} remaining`}
                    </span>
                  </div>
                </div>

                {isCompleted && (
                  <div className="flex items-center gap-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 px-3 py-1.5 text-xs font-bold text-emerald-400">
                    <CheckCircle className="size-3.5" />
                    Goal Verified & Completed
                  </div>
                )}
              </motion.div>
            );
          })}
        </div>
      )}

      {/* Create Goal Modal */}
      <AnimatePresence>
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="relative w-full max-w-md rounded-3xl border border-white/15 bg-[#0F172A] p-6 shadow-2xl space-y-5"
            >
              <div className="flex items-center justify-between border-b border-white/10 pb-3">
                <h3 className="text-lg font-bold text-white flex items-center gap-2">
                  <Target className="size-5 text-emerald-400" />
                  Define Sustainability Target
                </h3>
                <button
                  onClick={() => setIsModalOpen(false)}
                  className="text-slate-400 hover:text-white transition-colors"
                >
                  <X className="size-5" />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="text-xs font-semibold text-slate-300">Goal Target Metric</label>
                  <select
                    value={goalType}
                    onChange={(e) => setGoalType(e.target.value as SustainabilityGoalType)}
                    className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                  >
                    <option value="CARBON_REDUCTION">Carbon Footprint Avoidance (kg CO₂)</option>
                    <option value="EWASTE_PREVENTION">E-Waste Landfill Prevention (kg)</option>
                    <option value="DEVICE_LIFE_EXTENSION">Cumulative Hardware Lifespan (Days)</option>
                    <option value="REPAIR_COUNT">Completed Repair Milestones (Repairs)</option>
                    <option value="MONEY_SAVED">Financial Repair Savings (₹)</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">
                    Target Value ({GOAL_UNITS[goalType]})
                  </label>
                  <input
                    type="number"
                    min="1"
                    step="any"
                    value={targetValue}
                    onChange={(e) => setTargetValue(parseFloat(e.target.value) || 0)}
                    className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    required
                  />
                </div>

                <div>
                  <label className="text-xs font-semibold text-slate-300">Time Horizon</label>
                  <select
                    value={targetMonths}
                    onChange={(e) => setTargetMonths(parseInt(e.target.value, 10))}
                    className="mt-1 w-full rounded-xl border border-white/15 bg-slate-900 px-3.5 py-2 text-sm text-white focus:border-emerald-500 focus:outline-none"
                  >
                    <option value={3}>3 Months Horizon</option>
                    <option value={6}>6 Months Horizon</option>
                    <option value={12}>1 Year Long-term Horizon</option>
                  </select>
                </div>

                <div className="flex items-center justify-end gap-3 pt-3 border-t border-white/10">
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="rounded-xl border border-white/10 px-4 py-2 text-xs font-semibold text-slate-300 hover:bg-white/5 transition-colors"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={isSubmitting}
                    className="rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 px-5 py-2 text-xs font-bold text-white shadow-lg shadow-emerald-500/20 hover:brightness-110 active:scale-95 transition-all"
                  >
                    {isSubmitting ? "Creating..." : "Set Target Goal"}
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
