"use client";

import React, { useState } from "react";
import { ShieldAlert, ShieldCheck, AlertCircle, CheckCheck, Clock, ArrowRight, Laptop, Smartphone, Headphones, Tv } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { ActionStepResponse } from "@/lib/types/autonomousRepairAgent";
import { cn } from "@/lib/utils";

interface PendingApprovalsPanelProps {
  approvals: ActionStepResponse[];
  onApproveStep: (stepId: string) => Promise<void> | void;
  onRejectStep: (stepId: string) => Promise<void> | void;
}

export default function PendingApprovalsPanel({
  approvals,
  onApproveStep,
  onRejectStep,
}: PendingApprovalsPanelProps) {
  const [loadingId, setLoadingId] = useState<string | null>(null);

  const handleApprove = async (stepId: string) => {
    setLoadingId(stepId);
    try {
      await onApproveStep(stepId);
    } finally {
      setLoadingId(null);
    }
  };

  const handleReject = async (stepId: string) => {
    setLoadingId(stepId);
    try {
      await onRejectStep(stepId);
    } finally {
      setLoadingId(null);
    }
  };

  if (approvals.length === 0) {
    return (
      <GlassCard padding="md" glowColor="none">
        <div className="flex flex-col items-center justify-center p-8 text-center">
          <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 mb-3">
            <ShieldCheck className="h-6 w-6" />
          </div>
          <h4 className="text-sm font-bold text-white">No Approvals Pending</h4>
          <p className="mt-1 text-xs text-slate-400 max-w-xs">
            All proactive agent remediation steps have been verified or do not require human sign-off.
          </p>
        </div>
      </GlassCard>
    );
  }

  return (
    <GlassCard padding="md" glowColor="none" className="border-amber-500/20">
      <div className="space-y-4">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/5 pb-3">
          <div className="flex items-center gap-2">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-amber-500/10 text-amber-400 border border-amber-500/20">
              <ShieldAlert className="h-4 w-4" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Human-in-the-Loop Approvals Required</h3>
              <p className="text-xs text-slate-400">
                RepairVerse AI pauses before external bookings, quotes, or disposals.
              </p>
            </div>
          </div>
          <span className="rounded-full border border-amber-500/30 bg-amber-500/10 px-2.5 py-0.5 text-xs font-bold text-amber-300 font-mono">
            {approvals.length} Pending
          </span>
        </div>

        {/* Approval Items */}
        <div className="space-y-3">
          {approvals.map((step) => {
            const isLoading = loadingId === step.id;

            return (
              <div
                key={step.id}
                className="flex flex-col gap-3 rounded-xl border border-amber-500/20 bg-amber-950/10 p-4 transition-all hover:border-amber-500/30 sm:flex-row sm:items-center sm:justify-between"
              >
                <div className="flex items-start gap-3">
                  <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-amber-500/20 text-amber-300 border border-amber-500/30 text-xs font-bold font-mono">
                    !
                  </div>

                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <span className="text-xs font-bold text-white">{step.title}</span>
                      {step.deviceName && (
                        <span className="rounded bg-white/5 border border-white/10 px-2 py-0.5 text-[10px] text-slate-300">
                          {step.deviceName}
                        </span>
                      )}
                      <span className="text-[10px] font-mono text-cyan-400 uppercase">
                        [{step.actionType}]
                      </span>
                    </div>
                    <p className="mt-1 text-xs text-slate-300 leading-relaxed">{step.description}</p>
                  </div>
                </div>

                <div className="flex shrink-0 items-center gap-2 self-end sm:self-center">
                  <button
                    id={`approval-panel-approve-${step.id}`}
                    onClick={() => handleApprove(step.id)}
                    disabled={isLoading}
                    className="inline-flex items-center gap-1.5 rounded-lg bg-emerald-500/20 hover:bg-emerald-500/30 border border-emerald-500/40 px-3.5 py-1.5 text-xs font-semibold text-emerald-300 transition-all hover:scale-105 active:scale-95 disabled:opacity-50"
                  >
                    <ShieldCheck className="h-3.5 w-3.5" />
                    Approve
                  </button>
                  <button
                    id={`approval-panel-reject-${step.id}`}
                    onClick={() => handleReject(step.id)}
                    disabled={isLoading}
                    className="inline-flex items-center gap-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 border border-red-500/30 px-3 py-1.5 text-xs font-medium text-red-400 transition-all hover:scale-105 active:scale-95 disabled:opacity-50"
                  >
                    <AlertCircle className="h-3.5 w-3.5" />
                    Reject
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </GlassCard>
  );
}
