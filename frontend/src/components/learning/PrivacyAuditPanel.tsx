"use client";

import React from "react";
import { Lock, ShieldCheck, FileText, CheckCircle2 } from "lucide-react";
import { cn } from "@/lib/utils";
import type { PrivacyAuditResponse } from "@/lib/types/federatedLearning";

interface PrivacyAuditPanelProps {
  audits: PrivacyAuditResponse[];
  className?: string;
}

export default function PrivacyAuditPanel({
  audits,
  className,
}: PrivacyAuditPanelProps) {
  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      <div className="flex items-center justify-between pb-4 border-b border-white/10">
        <div className="flex items-center gap-2.5">
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
            <Lock className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Privacy Preservation Audit</h3>
            <p className="text-xs text-slate-400">
              Deterministic verification that no user identities, emails, or serials enter the learning loop
            </p>
          </div>
        </div>

        <span className="rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-400 border border-emerald-500/20">
          100% Privacy Compliant
        </span>
      </div>

      <div className="mt-4 space-y-3">
        {audits.map((audit) => (
          <div
            key={audit.id}
            className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 rounded-xl border border-white/5 bg-slate-950/60 p-4"
          >
            <div className="space-y-1">
              <div className="flex items-center gap-2">
                <span className="text-xs font-mono font-bold text-emerald-300">
                  {audit.eventType}
                </span>
                <span className="rounded bg-black/40 px-1.5 py-0.5 text-[10px] font-mono text-slate-400">
                  Rule: {audit.privacyRule}
                </span>
              </div>
              <p className="text-xs text-slate-300 font-medium">
                {audit.recordsProcessed} raw records evaluated &bull; {audit.recordsAggregated} aggregated securely &bull; {audit.sensitiveFieldsRemoved} PII fields permanently removed
              </p>
            </div>

            <div className="flex items-center gap-1.5 text-xs text-emerald-400 font-semibold shrink-0">
              <CheckCircle2 className="h-4 w-4" />
              <span>Verified & Scrubbed</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
