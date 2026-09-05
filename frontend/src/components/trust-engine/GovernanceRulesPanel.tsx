"use client";

import React from "react";
import { ShieldCheck, Scale, Cpu, AlertCircle, FileText } from "lucide-react";
import { cn } from "@/lib/utils";
import type { GovernanceRuleResponse } from "@/lib/types/trustEngine";

interface GovernanceRulesPanelProps {
  rules: GovernanceRuleResponse[];
  className?: string;
}

export default function GovernanceRulesPanel({
  rules,
  className,
}: GovernanceRulesPanelProps) {
  const getSeverityBadge = (severity: string) => {
    switch (severity) {
      case "CRITICAL":
        return "border-rose-500/30 bg-rose-500/10 text-rose-400";
      case "HIGH":
        return "border-amber-500/30 bg-amber-500/10 text-amber-400";
      case "MEDIUM":
        return "border-blue-500/30 bg-blue-500/10 text-blue-400";
      default:
        return "border-slate-500/30 bg-slate-500/10 text-slate-400";
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
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
            <Scale className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Active AI Governance Rules</h3>
            <p className="text-xs text-slate-400">
              Deterministic guardrails evaluated before any AI recommendation or action executes
            </p>
          </div>
        </div>

        <span className="rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-400 border border-emerald-500/20">
          {rules.length} Rules Enforced
        </span>
      </div>

      <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
        {rules.map((rule) => (
          <div
            key={rule.id}
            className="flex flex-col justify-between gap-3 rounded-xl border border-white/5 bg-slate-950/60 p-4 transition hover:border-indigo-500/30"
          >
            <div className="space-y-2">
              <div className="flex items-center justify-between gap-2">
                <span className="text-xs font-bold text-white uppercase tracking-wider">
                  {rule.ruleName}
                </span>
                <span
                  className={cn(
                    "rounded-full px-2 py-0.5 text-[10px] font-bold uppercase border",
                    getSeverityBadge(rule.severity)
                  )}
                >
                  {rule.severity}
                </span>
              </div>
              <p className="text-xs text-slate-300 leading-relaxed font-medium">
                {rule.description}
              </p>
            </div>

            <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-white/5 text-[10px] text-slate-400 font-mono">
              <span>Systems: {rule.appliesToSystems}</span>
              <span className="text-indigo-400 font-semibold">
                Category: {rule.ruleCategory}
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
