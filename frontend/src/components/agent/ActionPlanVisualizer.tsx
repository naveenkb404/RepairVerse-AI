"use client";

import React, { useState } from "react";
import {
  CheckCircle2,
  Clock,
  AlertCircle,
  Play,
  ShieldCheck,
  ShieldAlert,
  FileText,
  Search,
  MessageSquareQuote,
  Calendar,
  Layers,
  Wrench,
  Trash2,
  Bell,
  ArrowRight,
} from "lucide-react";
import type { ActionPlanResponse, ActionStepResponse } from "@/lib/types/autonomousRepairAgent";
import { cn } from "@/lib/utils";

interface ActionPlanVisualizerProps {
  actionPlan: ActionPlanResponse;
  onApproveStep?: (stepId: string) => Promise<void> | void;
  onRejectStep?: (stepId: string) => Promise<void> | void;
  onExecuteStep?: (stepId: string) => Promise<void> | void;
}

export default function ActionPlanVisualizer({
  actionPlan,
  onApproveStep,
  onRejectStep,
  onExecuteStep,
}: ActionPlanVisualizerProps) {
  const [loadingStepId, setLoadingStepId] = useState<string | null>(null);

  const getActionIcon = (actionType: string) => {
    switch (actionType) {
      case "GENERATE_REPORT":
        return FileText;
      case "FIND_SHOPS":
        return Search;
      case "REQUEST_QUOTE":
        return MessageSquareQuote;
      case "SCHEDULE_MAINTENANCE":
        return Calendar;
      case "COMPARE_OPTIONS":
        return Layers;
      case "BOOK_SERVICE":
        return Wrench;
      case "DISPOSE_RECYCLE":
        return Trash2;
      case "NOTIFY_USER":
        return Bell;
      default:
        return Wrench;
    }
  };

  const getStatusBadge = (status: string, requiresApproval: boolean) => {
    switch (status) {
      case "COMPLETED":
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-400">
            <CheckCircle2 className="h-3.5 w-3.5" />
            Completed
          </span>
        );
      case "RUNNING":
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-2.5 py-0.5 text-xs font-semibold text-cyan-300">
            <span className="h-2 w-2 rounded-full bg-cyan-400 animate-ping" />
            Executing
          </span>
        );
      case "WAITING_APPROVAL":
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-amber-500/30 bg-amber-500/10 px-2.5 py-0.5 text-xs font-semibold text-amber-300">
            <ShieldAlert className="h-3.5 w-3.5" />
            Requires Approval
          </span>
        );
      case "APPROVED":
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-teal-500/30 bg-teal-500/10 px-2.5 py-0.5 text-xs font-semibold text-teal-300">
            <ShieldCheck className="h-3.5 w-3.5" />
            Approved
          </span>
        );
      case "REJECTED":
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-red-500/30 bg-red-500/10 px-2.5 py-0.5 text-xs font-semibold text-red-400">
            <AlertCircle className="h-3.5 w-3.5" />
            Rejected
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-1.5 rounded-full border border-slate-700 bg-slate-800/60 px-2.5 py-0.5 text-xs font-medium text-slate-400">
            <Clock className="h-3.5 w-3.5" />
            {requiresApproval ? "Awaiting Trigger" : "Queued"}
          </span>
        );
    }
  };

  const handleAction = async (actionFn?: (id: string) => Promise<void> | void, stepId?: string) => {
    if (!actionFn || !stepId) return;
    setLoadingStepId(stepId);
    try {
      await actionFn(stepId);
    } finally {
      setLoadingStepId(null);
    }
  };

  const progressPercent = Math.round((actionPlan.completedSteps / Math.max(1, actionPlan.totalSteps)) * 100);

  return (
    <div className="space-y-4 rounded-2xl border border-white/10 bg-slate-900/60 p-5 backdrop-blur-md">
      {/* Plan Header & Progress Bar */}
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between border-b border-white/5 pb-3">
        <div>
          <div className="flex items-center gap-2">
            <h4 className="text-sm font-semibold text-white">{actionPlan.planName}</h4>
            <span className="text-xs text-slate-400 font-mono">
              ({actionPlan.completedSteps}/{actionPlan.totalSteps} steps completed)
            </span>
          </div>
          <p className="text-xs text-slate-400 mt-0.5">{actionPlan.objective}</p>
        </div>

        <div className="flex items-center gap-3">
          <div className="w-28 bg-slate-800 rounded-full h-2 overflow-hidden border border-white/5">
            <div
              className="bg-gradient-to-r from-emerald-400 to-cyan-400 h-full rounded-full transition-all duration-500"
              style={{ width: `${progressPercent}%` }}
            />
          </div>
          <span className="text-xs font-mono font-bold text-emerald-400">{progressPercent}%</span>
        </div>
      </div>

      {/* Step Sequence List */}
      <div className="space-y-3">
        {actionPlan.steps.map((step: ActionStepResponse, index: number) => {
          const Icon = getActionIcon(step.actionType);
          const isCurrentLoading = loadingStepId === step.id;

          return (
            <div
              key={step.id}
              className={cn(
                "group relative flex flex-col gap-3 rounded-xl border p-4 transition-all sm:flex-row sm:items-center sm:justify-between",
                step.status === "COMPLETED"
                  ? "border-emerald-500/20 bg-emerald-950/20"
                  : step.status === "WAITING_APPROVAL"
                  ? "border-amber-500/30 bg-amber-950/20 shadow-[0_0_15px_rgba(245,158,11,0.1)]"
                  : step.status === "RUNNING"
                  ? "border-cyan-500/30 bg-cyan-950/20 shadow-[0_0_15px_rgba(6,182,212,0.1)]"
                  : "border-white/5 bg-white/[0.02] hover:border-white/10"
              )}
            >
              {/* Step info */}
              <div className="flex items-start gap-3.5">
                <div
                  className={cn(
                    "flex h-8 w-8 shrink-0 items-center justify-center rounded-lg text-xs font-bold font-mono transition-colors",
                    step.status === "COMPLETED"
                      ? "bg-emerald-500/20 text-emerald-300 border border-emerald-500/30"
                      : step.status === "WAITING_APPROVAL"
                      ? "bg-amber-500/20 text-amber-300 border border-amber-500/30"
                      : step.status === "RUNNING"
                      ? "bg-cyan-500/20 text-cyan-300 border border-cyan-500/30"
                      : "bg-slate-800 text-slate-400 border border-slate-700"
                  )}
                >
                  {index + 1}
                </div>

                <div>
                  <div className="flex flex-wrap items-center gap-2">
                    <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-200">
                      <Icon className="h-3.5 w-3.5 text-cyan-400" />
                      <span>{step.title}</span>
                    </div>
                    {getStatusBadge(step.status, step.requiresApproval)}
                  </div>
                  <p className="mt-1 text-xs text-slate-400 leading-relaxed">{step.description}</p>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex shrink-0 items-center gap-2 self-end sm:self-center">
                {step.status === "WAITING_APPROVAL" && (
                  <>
                    <button
                      id={`approve-step-${step.id}`}
                      onClick={() => handleAction(onApproveStep, step.id)}
                      disabled={isCurrentLoading}
                      className="inline-flex items-center gap-1.5 rounded-lg bg-emerald-500/20 hover:bg-emerald-500/30 border border-emerald-500/40 px-3 py-1.5 text-xs font-medium text-emerald-300 transition-all hover:scale-105 active:scale-95 disabled:opacity-50"
                    >
                      <ShieldCheck className="h-3.5 w-3.5" />
                      Approve
                    </button>
                    <button
                      id={`reject-step-${step.id}`}
                      onClick={() => handleAction(onRejectStep, step.id)}
                      disabled={isCurrentLoading}
                      className="inline-flex items-center gap-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 border border-red-500/30 px-3 py-1.5 text-xs font-medium text-red-400 transition-all hover:scale-105 active:scale-95 disabled:opacity-50"
                    >
                      <AlertCircle className="h-3.5 w-3.5" />
                      Reject
                    </button>
                  </>
                )}

                {step.status === "APPROVED" && (
                  <button
                    id={`execute-step-${step.id}`}
                    onClick={() => handleAction(onExecuteStep, step.id)}
                    disabled={isCurrentLoading}
                    className="inline-flex items-center gap-1.5 rounded-lg bg-gradient-to-r from-emerald-500 to-cyan-500 px-3.5 py-1.5 text-xs font-semibold text-slate-950 transition-all hover:scale-105 active:scale-95 disabled:opacity-50 shadow-md shadow-emerald-500/20"
                  >
                    <Play className="h-3.5 w-3.5 fill-current" />
                    Execute Now
                  </button>
                )}

                {step.status === "PENDING" && !step.requiresApproval && (
                  <button
                    id={`execute-auto-step-${step.id}`}
                    onClick={() => handleAction(onExecuteStep, step.id)}
                    disabled={isCurrentLoading}
                    className="inline-flex items-center gap-1.5 rounded-lg bg-cyan-500/20 hover:bg-cyan-500/30 border border-cyan-500/40 px-3 py-1.5 text-xs font-medium text-cyan-300 transition-all hover:scale-105 active:scale-95 disabled:opacity-50"
                  >
                    <Play className="h-3.5 w-3.5" />
                    Trigger
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
