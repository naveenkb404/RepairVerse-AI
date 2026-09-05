"use client";

import React, { useState } from "react";
import { Sliders, Shield, AlertCircle, Check, DollarSign, Bell } from "lucide-react";
import { cn } from "@/lib/utils";
import type {
  UserAutonomyPreferencesResponse,
  UpdateAutonomyPreferencesRequest,
} from "@/lib/types/trustEngine";

interface UserAutonomyControlsProps {
  preferences: UserAutonomyPreferencesResponse;
  onSave: (updated: UpdateAutonomyPreferencesRequest) => Promise<void>;
  className?: string;
}

export default function UserAutonomyControls({
  preferences,
  onSave,
  className,
}: UserAutonomyControlsProps) {
  const [allowInterventions, setAllowInterventions] = useState(
    preferences.allowAutonomousInterventions ?? true
  );
  const [allowAutoScheduling, setAllowAutoScheduling] = useState(
    preferences.allowAutoScheduling ?? false
  );
  const [allowProactiveAlerts, setAllowProactiveAlerts] = useState(
    preferences.allowProactiveAlerts ?? true
  );
  const [minConfidence, setMinConfidence] = useState(
    preferences.minConfidenceThreshold ?? 75
  );
  const [maxCost, setMaxCost] = useState(
    preferences.requireApprovalAboveCost ?? 3500
  );
  const [notificationStyle, setNotificationStyle] = useState(
    preferences.notificationStyle ?? "VERBOSE"
  );
  const [saving, setSaving] = useState(false);
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSave = async () => {
    setSaving(true);
    setSavedSuccess(false);
    try {
      await onSave({
        allowAutonomousInterventions: allowInterventions,
        allowAutoScheduling: allowAutoScheduling,
        allowProactiveAlerts: allowProactiveAlerts,
        minConfidenceThreshold: minConfidence,
        requireApprovalAboveCost: maxCost,
        notificationStyle: notificationStyle,
      });
      setSavedSuccess(true);
      setTimeout(() => setSavedSuccess(false), 3000);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
            <Sliders className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Autonomy & Consent Governance</h3>
            <p className="text-xs text-slate-400">
              Control what decisions RepairVerse AI can execute autonomously vs. require sign-off
            </p>
          </div>
        </div>

        {savedSuccess && (
          <span className="flex items-center gap-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 px-3 py-1 text-xs font-semibold text-emerald-400">
            <Check className="h-3.5 w-3.5" />
            Preferences Saved
          </span>
        )}
      </div>

      <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Toggle 1: Autonomous Interventions */}
        <div className="flex items-start justify-between gap-4 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <Shield className="h-4 w-4 text-cyan-400" />
              <label className="text-xs font-bold text-white uppercase tracking-wider">
                Autonomous Action Planning
              </label>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Permit AI agent to automatically formulate preventive action plans and order parts.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setAllowInterventions(!allowInterventions)}
            className={cn(
              "relative h-6 w-11 shrink-0 rounded-full transition-colors focus:outline-none",
              allowInterventions ? "bg-cyan-500" : "bg-slate-700"
            )}
          >
            <span
              className={cn(
                "inline-block h-4 w-4 transform rounded-full bg-white transition-transform",
                allowInterventions ? "translate-x-6" : "translate-x-1"
              )}
            />
          </button>
        </div>

        {/* Toggle 2: Auto-scheduling */}
        <div className="flex items-start justify-between gap-4 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <Sliders className="h-4 w-4 text-emerald-400" />
              <label className="text-xs font-bold text-white uppercase tracking-wider">
                Direct Shop Auto-Booking
              </label>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Allow high-confidence interventions to book certified repair shop appointments without confirmation.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setAllowAutoScheduling(!allowAutoScheduling)}
            className={cn(
              "relative h-6 w-11 shrink-0 rounded-full transition-colors focus:outline-none",
              allowAutoScheduling ? "bg-emerald-500" : "bg-slate-700"
            )}
          >
            <span
              className={cn(
                "inline-block h-4 w-4 transform rounded-full bg-white transition-transform",
                allowAutoScheduling ? "translate-x-6" : "translate-x-1"
              )}
            />
          </button>
        </div>

        {/* Toggle 3: Proactive Alerts */}
        <div className="flex items-start justify-between gap-4 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <Bell className="h-4 w-4 text-amber-400" />
              <label className="text-xs font-bold text-white uppercase tracking-wider">
                Proactive Degradation Alerts
              </label>
            </div>
            <p className="text-xs text-slate-400 leading-relaxed">
              Notify when multi-sensor telemetry projects impending battery or thermal degradation.
            </p>
          </div>
          <button
            type="button"
            onClick={() => setAllowProactiveAlerts(!allowProactiveAlerts)}
            className={cn(
              "relative h-6 w-11 shrink-0 rounded-full transition-colors focus:outline-none",
              allowProactiveAlerts ? "bg-amber-500" : "bg-slate-700"
            )}
          >
            <span
              className={cn(
                "inline-block h-4 w-4 transform rounded-full bg-white transition-transform",
                allowProactiveAlerts ? "translate-x-6" : "translate-x-1"
              )}
            />
          </button>
        </div>

        {/* Notification Style */}
        <div className="flex flex-col justify-between gap-2 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="space-y-1">
            <label className="text-xs font-bold text-white uppercase tracking-wider">
              Explainability Verbosity
            </label>
            <p className="text-xs text-slate-400">
              Depth of evidence tracing and causal breakdowns.
            </p>
          </div>
          <select
            value={notificationStyle}
            onChange={(e) => setNotificationStyle(e.target.value)}
            className="w-full rounded-lg border border-white/10 bg-slate-900 px-3 py-2 text-xs text-slate-200 focus:border-cyan-500 focus:outline-none"
          >
            <option value="VERBOSE">VERBOSE (Detailed 4-factor explainability & traces)</option>
            <option value="STANDARD">STANDARD (Summary & core evidence only)</option>
            <option value="MINIMAL">MINIMAL (Direct action & score only)</option>
          </select>
        </div>

        {/* Slider 1: Min Confidence */}
        <div className="space-y-2 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="flex items-center justify-between text-xs">
            <span className="font-bold text-white uppercase tracking-wider">
              Minimum AI Confidence Threshold
            </span>
            <span className="font-mono font-bold text-cyan-400">{minConfidence}%</span>
          </div>
          <input
            type="range"
            min={50}
            max={95}
            step={5}
            value={minConfidence}
            onChange={(e) => setMinConfidence(Number(e.target.value))}
            className="w-full accent-cyan-500 cursor-pointer"
          />
          <p className="text-[11px] text-slate-400">
            Decisions below this confidence will strictly require human approval.
          </p>
        </div>

        {/* Slider 2: Max Cost Approval */}
        <div className="space-y-2 rounded-xl border border-white/5 bg-slate-950/60 p-4">
          <div className="flex items-center justify-between text-xs">
            <span className="font-bold text-white uppercase tracking-wider">
              Require Approval Above Financial Cost
            </span>
            <span className="font-mono font-bold text-emerald-400">₹{maxCost.toLocaleString()}</span>
          </div>
          <input
            type="range"
            min={500}
            max={15000}
            step={500}
            value={maxCost}
            onChange={(e) => setMaxCost(Number(e.target.value))}
            className="w-full accent-emerald-500 cursor-pointer"
          />
          <p className="text-[11px] text-slate-400">
            Any action or repair quote exceeding this cost requires user sign-off.
          </p>
        </div>
      </div>

      <div className="mt-6 flex justify-end">
        <button
          type="button"
          onClick={handleSave}
          disabled={saving}
          className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 px-5 py-2.5 text-xs font-bold uppercase tracking-wider text-white shadow-lg shadow-cyan-500/20 hover:brightness-110 active:scale-95 disabled:opacity-50"
        >
          {saving ? "Saving Governance..." : "Save Autonomy Constraints"}
        </button>
      </div>
    </div>
  );
}
