"use client";

import React from "react";
import { ThumbsUp, ThumbsDown, HelpCircle, Users, MessageSquare } from "lucide-react";
import { cn } from "@/lib/utils";

interface LearningFeedbackPanelProps {
  agreeCount?: number;
  disagreeCount?: number;
  unsureCount?: number;
  className?: string;
}

export default function LearningFeedbackPanel({
  agreeCount = 142,
  disagreeCount = 6,
  unsureCount = 18,
  className,
}: LearningFeedbackPanelProps) {
  const total = agreeCount + disagreeCount + unsureCount || 1;
  const agreePct = Math.round((agreeCount / total) * 100);
  const disagreePct = Math.round((disagreeCount / total) * 100);
  const unsurePct = Math.round((unsureCount / total) * 100);

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
            <Users className="h-4 w-4" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">Aggregated Community Feedback</h3>
            <p className="text-xs text-slate-400">
              Outcome validation consensus aggregated across all user reviews (no individual bias)
            </p>
          </div>
        </div>

        <span className="rounded-full bg-emerald-500/10 px-2.5 py-0.5 text-xs font-semibold text-emerald-400 border border-emerald-500/20">
          {agreePct}% Agreement Consensus
        </span>
      </div>

      <div className="mt-5 grid grid-cols-1 sm:grid-cols-3 gap-4">
        {/* Agree Card */}
        <div className="rounded-xl border border-emerald-500/20 bg-emerald-950/20 p-4 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase text-emerald-300">Agree / Verified</span>
            <ThumbsUp className="h-4 w-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-black text-white">{agreeCount} reviews</div>
          <div className="h-1.5 w-full bg-slate-800 rounded-full overflow-hidden">
            <div className="h-full bg-emerald-500 rounded-full" style={{ width: `${agreePct}%` }} />
          </div>
          <span className="text-[10px] text-slate-400 font-mono">{agreePct}% of aggregate feedback</span>
        </div>

        {/* Disagree Card */}
        <div className="rounded-xl border border-rose-500/20 bg-rose-950/20 p-4 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase text-rose-300">Disagree / Flagged</span>
            <ThumbsDown className="h-4 w-4 text-rose-400" />
          </div>
          <div className="text-2xl font-black text-white">{disagreeCount} reviews</div>
          <div className="h-1.5 w-full bg-slate-800 rounded-full overflow-hidden">
            <div className="h-full bg-rose-500 rounded-full" style={{ width: `${disagreePct}%` }} />
          </div>
          <span className="text-[10px] text-slate-400 font-mono">{disagreePct}% of aggregate feedback</span>
        </div>

        {/* Unsure Card */}
        <div className="rounded-xl border border-amber-500/20 bg-amber-950/20 p-4 space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold uppercase text-amber-300">Unsure / Pending</span>
            <HelpCircle className="h-4 w-4 text-amber-400" />
          </div>
          <div className="text-2xl font-black text-white">{unsureCount} reviews</div>
          <div className="h-1.5 w-full bg-slate-800 rounded-full overflow-hidden">
            <div className="h-full bg-amber-500 rounded-full" style={{ width: `${unsurePct}%` }} />
          </div>
          <span className="text-[10px] text-slate-400 font-mono">{unsurePct}% of aggregate feedback</span>
        </div>
      </div>
    </div>
  );
}
