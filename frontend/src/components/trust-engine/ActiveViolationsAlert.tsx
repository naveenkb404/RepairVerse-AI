"use client";

import React from "react";
import { AlertTriangle, ShieldAlert, CheckCircle2, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";
import type { GovernanceViolationResponse } from "@/lib/types/trustEngine";

interface ActiveViolationsAlertProps {
  violations: GovernanceViolationResponse[];
  onSelectDecision?: (decisionId: string) => void;
  className?: string;
}

export default function ActiveViolationsAlert({
  violations,
  onSelectDecision,
  className,
}: ActiveViolationsAlertProps) {
  if (!violations || violations.length === 0) {
    return (
      <div
        className={cn(
          "flex items-center gap-3 rounded-2xl border border-emerald-500/20 bg-emerald-950/20 p-4 text-emerald-300",
          className
        )}
      >
        <CheckCircle2 className="h-5 w-5 text-emerald-400 shrink-0" />
        <div className="text-xs">
          <span className="font-semibold text-emerald-200">Zero Governance Violations Active: </span>
          All autonomous recommendations, digital twin forecasts, and repair agent interventions are within compliance guardrails.
        </div>
      </div>
    );
  }

  const getSeverityStyle = (severity: string) => {
    switch (severity) {
      case "CRITICAL":
        return "border-rose-500/30 bg-rose-950/30 text-rose-300";
      case "HIGH":
        return "border-rose-500/20 bg-rose-950/20 text-rose-200";
      case "MEDIUM":
        return "border-amber-500/20 bg-amber-950/20 text-amber-200";
      default:
        return "border-blue-500/20 bg-blue-950/20 text-blue-200";
    }
  };

  return (
    <div
      className={cn(
        "rounded-2xl border border-rose-500/30 bg-gradient-to-r from-rose-950/40 via-slate-950/80 to-slate-900/80 p-5 shadow-2xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-3 border-b border-rose-500/20">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-rose-500/20 text-rose-400 border border-rose-500/30 animate-pulse">
            <ShieldAlert className="h-4 w-4" />
          </div>
          <div>
            <h4 className="text-sm font-bold text-white">Active AI Governance Alerts</h4>
            <p className="text-[11px] text-slate-400">
              {violations.length} recommendation(s) triggered safety / compliance guardrails
            </p>
          </div>
        </div>

        <span className="rounded-full bg-rose-500/20 px-2.5 py-0.5 text-xs font-bold text-rose-300 border border-rose-500/30">
          Action Required
        </span>
      </div>

      <div className="mt-3 space-y-2.5">
        {violations.map((violation) => (
          <div
            key={violation.id}
            onClick={() => onSelectDecision && onSelectDecision(violation.decisionRecordId)}
            className={cn(
              "flex flex-col sm:flex-row sm:items-center justify-between gap-3 rounded-xl border p-3.5 transition hover:brightness-110 cursor-pointer",
              getSeverityStyle(violation.severity)
            )}
          >
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold uppercase tracking-wider">
                  {violation.ruleName}
                </span>
                <span className="rounded bg-black/40 px-1.5 py-0.5 text-[10px] font-mono font-semibold uppercase">
                  {violation.severity}
                </span>
              </div>
              <p className="text-xs text-slate-300 font-medium">
                {violation.violationMessage}
              </p>
            </div>

            <div className="flex items-center gap-1 text-xs font-semibold text-rose-300 hover:text-white shrink-0">
              <span>Inspect Audit</span>
              <ChevronRight className="h-3.5 w-3.5" />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
