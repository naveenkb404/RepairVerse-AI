"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Calendar,
  CheckCircle2,
  Clock,
  AlertTriangle,
  AlertOctagon,
  Sparkles,
  Wrench,
  Battery,
  Layers,
  Search,
  Check,
  X,
  ChevronDown,
  ChevronUp,
  Leaf,
  DollarSign,
  LucideIcon,
  RefreshCw,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type {
  MaintenanceSchedule,
  MaintenanceType,
  MaintenancePriorityLevel,
  MaintenanceStatus,
} from "@/lib/types/maintenance";

interface MaintenanceTimelineProps {
  schedules: MaintenanceSchedule[];
  onStatusChange?: (id: string, newStatus: MaintenanceStatus) => Promise<void>;
  onRefresh?: () => void;
  isLoading?: boolean;
  deviceIdFilter?: string;
}

export const MaintenanceTimeline: React.FC<MaintenanceTimelineProps> = ({
  schedules,
  onStatusChange,
  onRefresh,
  isLoading = false,
}) => {
  const [selectedFilter, setSelectedFilter] = useState<"ALL" | "ACTIONABLE" | "COMPLETED">("ACTIONABLE");
  const [updatingId, setUpdatingId] = useState<string | null>(null);
  const [expandedId, setExpandedId] = useState<string | null>(null);

  const getMaintenanceTypeIcon = (type: MaintenanceType): LucideIcon => {
    switch (type) {
      case "BATTERY_CHECK":
        return Battery;
      case "CLEANING":
        return Sparkles;
      case "SOFTWARE_MAINTENANCE":
        return Layers;
      case "PREVENTIVE_REPAIR":
      case "COMPONENT_REPLACEMENT":
        return Wrench;
      case "PROFESSIONAL_SERVICE":
        return CheckCircle2;
      default:
        return Search;
    }
  };

  const getPriorityBadge = (priority: MaintenancePriorityLevel) => {
    switch (priority) {
      case "CRITICAL":
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-500/10 text-red-400 border border-red-500/20">
            <AlertOctagon className="w-3 h-3 text-red-400 animate-pulse" />
            CRITICAL
          </span>
        );
      case "HIGH":
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-amber-500/10 text-amber-400 border border-amber-500/20">
            <AlertTriangle className="w-3 h-3 text-amber-400" />
            HIGH
          </span>
        );
      case "MEDIUM":
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            MEDIUM
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            LOW
          </span>
        );
    }
  };

  const getStatusBadge = (status: MaintenanceStatus) => {
    switch (status) {
      case "OVERDUE":
        return (
          <span className="px-2 py-0.5 rounded text-xs font-bold bg-red-600 text-white tracking-wide">
            OVERDUE
          </span>
        );
      case "DUE":
        return (
          <span className="px-2 py-0.5 rounded text-xs font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30">
            DUE SOON
          </span>
        );
      case "COMPLETED":
        return (
          <span className="px-2 py-0.5 rounded text-xs font-semibold bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 flex items-center gap-1">
            <Check className="w-3 h-3" /> COMPLETED
          </span>
        );
      case "SKIPPED":
        return (
          <span className="px-2 py-0.5 rounded text-xs font-medium bg-white/10 text-slate-400">
            SKIPPED
          </span>
        );
      default:
        return (
          <span className="px-2 py-0.5 rounded text-xs font-medium bg-blue-500/10 text-blue-400 border border-blue-500/20">
            UPCOMING
          </span>
        );
    }
  };

  const handleStatusUpdate = async (id: string, newStatus: MaintenanceStatus) => {
    if (!onStatusChange) return;
    try {
      setUpdatingId(id);
      await onStatusChange(id, newStatus);
    } finally {
      setUpdatingId(null);
    }
  };

  // Filter tasks based on view tab
  const filtered = schedules.filter((s) => {
    if (selectedFilter === "ACTIONABLE") {
      return ["OVERDUE", "DUE", "UPCOMING"].includes(s.status);
    }
    if (selectedFilter === "COMPLETED") {
      return ["COMPLETED", "SKIPPED"].includes(s.status);
    }
    return true;
  });

  return (
    <GlassCard className="p-6">
      {/* Header & Filter Controls */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 border-b border-white/10 pb-4">
        <div>
          <div className="flex items-center gap-2">
            <Calendar className="w-5 h-5 text-emerald-400" />
            <h3 className="text-lg font-bold text-white tracking-wide">
              Smart Maintenance Timeline
            </h3>
            {schedules.some((s) => s.isDemo) && (
              <span className="px-2 py-0.5 text-[10px] font-bold rounded bg-amber-500/10 text-amber-300 border border-amber-500/30">
                Demo
              </span>
            )}
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Deterministic care schedule configured from physical wear indicators & telemetry.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {onRefresh && (
            <button
              onClick={onRefresh}
              disabled={isLoading}
              className="p-2 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 text-slate-300 transition-colors disabled:opacity-50"
              title="Refresh Schedule"
            >
              <RefreshCw className={`w-4 h-4 ${isLoading ? "animate-spin text-emerald-400" : ""}`} />
            </button>
          )}

          <div className="flex rounded-lg bg-white/5 p-1 border border-white/10 text-xs">
            {(["ACTIONABLE", "ALL", "COMPLETED"] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => setSelectedFilter(tab)}
                className={`px-3 py-1 rounded-md font-medium transition-all ${
                  selectedFilter === tab
                    ? "bg-emerald-500/20 text-emerald-300 border border-emerald-500/30"
                    : "text-slate-400 hover:text-slate-200"
                }`}
              >
                {tab === "ACTIONABLE" ? "Actionable" : tab === "ALL" ? "All Tasks" : "History"}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Timeline Task List */}
      {filtered.length === 0 ? (
        <div className="text-center py-10 text-slate-400">
          <CheckCircle2 className="w-10 h-10 text-emerald-400/50 mx-auto mb-2" />
          <p className="text-sm font-medium text-white">No maintenance tasks in this view</p>
          <p className="text-xs text-slate-500 mt-1">
            All systems optimal. Your hardware has no immediate pending care alerts.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          <AnimatePresence initial={false}>
            {filtered.map((task) => {
              const Icon = getMaintenanceTypeIcon(task.maintenanceType);
              const isExpanded = expandedId === task.id;
              const isUpdating = updatingId === task.id;

              return (
                <motion.div
                  key={task.id}
                  layout
                  initial={{ opacity: 0, y: 8 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, scale: 0.95 }}
                  className={`rounded-xl border transition-all ${
                    task.status === "OVERDUE"
                      ? "bg-red-500/5 border-red-500/20 hover:border-red-500/40"
                      : task.status === "DUE"
                      ? "bg-amber-500/5 border-amber-500/20 hover:border-amber-500/40"
                      : task.status === "COMPLETED"
                      ? "bg-emerald-500/5 border-emerald-500/20 opacity-75"
                      : "bg-white/[0.03] border-white/10 hover:border-white/20"
                  }`}
                >
                  <div className="p-4">
                    <div className="flex items-start justify-between gap-3">
                      {/* Left: Icon & Meta */}
                      <div className="flex items-start gap-3">
                        <div
                          className={`p-2.5 rounded-lg border ${
                            task.status === "OVERDUE"
                              ? "bg-red-500/10 border-red-500/30 text-red-400"
                              : task.status === "DUE"
                              ? "bg-amber-500/10 border-amber-500/30 text-amber-400"
                              : "bg-emerald-500/10 border-emerald-500/30 text-emerald-400"
                          }`}
                        >
                          <Icon className="w-5 h-5" />
                        </div>

                        <div>
                          <div className="flex flex-wrap items-center gap-2 mb-1">
                            <span className="text-xs font-semibold text-slate-300">
                              {task.deviceName}
                            </span>
                            {getPriorityBadge(task.priority)}
                            {getStatusBadge(task.status)}
                          </div>

                          <h4 className="text-sm font-semibold text-white">
                            {task.title}
                          </h4>

                          <div className="flex flex-wrap items-center gap-4 mt-2 text-xs text-slate-400">
                            <span className="flex items-center gap-1">
                              <Calendar className="w-3.5 h-3.5 text-slate-500" />
                              Due: <strong className="text-slate-300">{task.dueDate}</strong>
                            </span>
                            <span className="flex items-center gap-1">
                              <Clock className="w-3.5 h-3.5 text-slate-500" />
                              {task.estimatedDurationMinutes} mins
                            </span>
                            {task.estimatedCost > 0 && (
                              <span className="flex items-center gap-1 text-emerald-400">
                                <DollarSign className="w-3.5 h-3.5" />
                                Est. ${task.estimatedCost.toFixed(0)}
                              </span>
                            )}
                            {task.estimatedCarbonSavings > 0 && (
                              <span className="flex items-center gap-1 text-emerald-400">
                                <Leaf className="w-3.5 h-3.5" />
                                {task.estimatedCarbonSavings.toFixed(1)} kg CO₂ avoided
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      {/* Right: Actions */}
                      <div className="flex items-center gap-2 shrink-0">
                        {["UPCOMING", "DUE", "OVERDUE"].includes(task.status) && onStatusChange && (
                          <div className="flex items-center gap-1">
                            <button
                              onClick={() => handleStatusUpdate(task.id, "COMPLETED")}
                              disabled={isUpdating}
                              className="px-3 py-1.5 rounded-lg bg-emerald-500/20 hover:bg-emerald-500/30 border border-emerald-500/40 text-emerald-300 text-xs font-semibold transition-all flex items-center gap-1.5 disabled:opacity-50"
                            >
                              <Check className="w-3.5 h-3.5" />
                              Complete
                            </button>
                            <button
                              onClick={() => handleStatusUpdate(task.id, "SKIPPED")}
                              disabled={isUpdating}
                              className="p-1.5 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 text-slate-400 hover:text-slate-200 transition-colors disabled:opacity-50"
                              title="Skip Task"
                            >
                              <X className="w-3.5 h-3.5" />
                            </button>
                          </div>
                        )}

                        <button
                          onClick={() => setExpandedId(isExpanded ? null : task.id)}
                          className="p-1.5 text-slate-400 hover:text-white transition-colors"
                        >
                          {isExpanded ? (
                            <ChevronUp className="w-4 h-4" />
                          ) : (
                            <ChevronDown className="w-4 h-4" />
                          )}
                        </button>
                      </div>
                    </div>

                    {/* Expandable Details */}
                    {isExpanded && (
                      <motion.div
                        initial={{ opacity: 0, height: 0 }}
                        animate={{ opacity: 1, height: "auto" }}
                        exit={{ opacity: 0, height: 0 }}
                        className="mt-3 pt-3 border-t border-white/10 text-xs text-slate-300"
                      >
                        <p className="leading-relaxed">{task.description}</p>
                      </motion.div>
                    )}
                  </div>
                </motion.div>
              );
            })}
          </AnimatePresence>
        </div>
      )}
    </GlassCard>
  );
};
