"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  X,
  ShieldCheck,
  ShieldAlert,
  Sparkles,
  HelpCircle,
  GitPullRequest,
  TrendingDown,
  Leaf,
  CheckCircle,
  ThumbsUp,
  ThumbsDown,
  Clock,
  Cpu,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { DecisionAuditResponse } from "@/lib/types/trustEngine";
import TrustScoreBreakdownCard from "./TrustScoreBreakdownCard";
import EvidenceTraceViewer from "./EvidenceTraceViewer";

interface DecisionDetailModalProps {
  decision: DecisionAuditResponse | null;
  isOpen: boolean;
  onClose: () => void;
  onMarkReviewed: (id: string) => Promise<void>;
  onSubmitFeedback: (id: string, feedback: "AGREE" | "DISAGREE" | "UNSURE") => Promise<void>;
}

export default function DecisionDetailModal({
  decision,
  isOpen,
  onClose,
  onMarkReviewed,
  onSubmitFeedback,
}: DecisionDetailModalProps) {
  const [activeTab, setActiveTab] = useState<"explain" | "evidence" | "trust" | "governance">("explain");
  const [feedbackSuccess, setFeedbackSuccess] = useState<string | null>(null);
  const [submittingFeedback, setSubmittingFeedback] = useState(false);
  const [reviewing, setReviewing] = useState(false);

  if (!isOpen || !decision) return null;

  const handleFeedback = async (type: "AGREE" | "DISAGREE" | "UNSURE") => {
    setSubmittingFeedback(true);
    try {
      await onSubmitFeedback(decision.id, type);
      setFeedbackSuccess(`Feedback recorded: ${type}`);
      setTimeout(() => setFeedbackSuccess(null), 3000);
    } finally {
      setSubmittingFeedback(false);
    }
  };

  const handleReview = async () => {
    setReviewing(true);
    try {
      await onMarkReviewed(decision.id);
    } finally {
      setReviewing(false);
    }
  };

  return (
    <AnimatePresence>
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-4 sm:p-6 backdrop-blur-md overflow-y-auto">
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 20 }}
          className="relative max-h-[90vh] w-full max-w-4xl overflow-y-auto rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900 via-slate-950 to-black p-6 sm:p-8 shadow-2xl text-slate-100"
        >
          {/* Close button */}
          <button
            onClick={onClose}
            className="absolute top-5 right-5 flex h-9 w-9 items-center justify-center rounded-full bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white transition"
          >
            <X className="h-5 w-5" />
          </button>

          {/* Header */}
          <div className="space-y-2 pr-10">
            <div className="flex flex-wrap items-center gap-2">
              <span className="rounded-full bg-cyan-500/10 border border-cyan-500/30 px-3 py-0.5 text-xs font-mono font-bold text-cyan-400 uppercase">
                {decision.sourceSystem}
              </span>
              <span className="rounded-full bg-white/5 border border-white/10 px-3 py-0.5 text-xs font-mono text-slate-300">
                {decision.decisionType}
              </span>
              <span className="rounded-full bg-white/5 border border-white/10 px-3 py-0.5 text-xs font-mono text-slate-400">
                ID: {decision.id}
              </span>
            </div>

            <h2 className="text-xl font-black text-white sm:text-2xl">
              AI Decision Explainability & Audit Log
            </h2>

            <p className="text-xs text-slate-300 font-medium">
              Output: {decision.decisionOutput}
            </p>
          </div>

          {/* Quick Metrics Bar */}
          <div className="mt-5 grid grid-cols-2 sm:grid-cols-4 gap-3">
            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <span className="text-[10px] font-semibold uppercase text-slate-500">Trust Score</span>
              <div className="mt-0.5 flex items-baseline gap-1">
                <span className="text-xl font-bold text-cyan-400">{decision.trustScore}</span>
                <span className="text-[10px] text-slate-400">/ 100</span>
              </div>
            </div>

            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <span className="text-[10px] font-semibold uppercase text-slate-500">AI Confidence</span>
              <div className="mt-0.5 flex items-baseline gap-1">
                <span className="text-xl font-bold text-indigo-400">{decision.confidenceScore}%</span>
              </div>
            </div>

            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <span className="text-[10px] font-semibold uppercase text-slate-500">Trust Tier</span>
              <div className="mt-0.5 text-sm font-bold text-emerald-400">{decision.trustTier}</div>
            </div>

            <div className="rounded-xl border border-white/5 bg-slate-950/60 p-3">
              <span className="text-[10px] font-semibold uppercase text-slate-500">Risk Level</span>
              <div className="mt-0.5 text-sm font-bold text-amber-400">{decision.riskLevel}</div>
            </div>
          </div>

          {/* Tabs */}
          <div className="mt-6 flex border-b border-white/10 gap-2">
            {[
              { key: "explain", label: "Explainability Dimensions" },
              { key: "evidence", label: `Evidence Traces (${decision.evidenceTraces?.length ?? 0})` },
              { key: "trust", label: "Trust Score Breakdown" },
              { key: "governance", label: `Violations (${decision.violations?.length ?? 0})` },
            ].map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key as any)}
                className={cn(
                  "px-4 py-2.5 text-xs font-bold uppercase tracking-wider transition border-b-2 -mb-[2px]",
                  activeTab === tab.key
                    ? "border-cyan-400 text-cyan-300"
                    : "border-transparent text-slate-400 hover:text-slate-200"
                )}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Tab Content */}
          <div className="mt-6">
            {activeTab === "explain" && (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* 1. Why Explanation */}
                <div className="rounded-2xl border border-cyan-500/20 bg-cyan-950/20 p-5 space-y-2">
                  <div className="flex items-center gap-2">
                    <Sparkles className="h-4 w-4 text-cyan-400" />
                    <h4 className="text-xs font-bold uppercase tracking-wider text-cyan-300">
                      Why (Causal Rationale)
                    </h4>
                  </div>
                  <p className="text-xs text-slate-200 leading-relaxed font-medium">
                    {decision.whyExplanation || "Causal rationale derived from multi-sensor telemetry threshold breaches."}
                  </p>
                </div>

                {/* 2. How Explanation */}
                <div className="rounded-2xl border border-indigo-500/20 bg-indigo-950/20 p-5 space-y-2">
                  <div className="flex items-center gap-2">
                    <GitPullRequest className="h-4 w-4 text-indigo-400" />
                    <h4 className="text-xs font-bold uppercase tracking-wider text-indigo-300">
                      How (Data Pipeline & Cross-Validation)
                    </h4>
                  </div>
                  <p className="text-xs text-slate-200 leading-relaxed font-medium">
                    {decision.howExplanation || "Corroborated across historical repair graphs and twin simulations."}
                  </p>
                </div>

                {/* 3. What-If Explanation */}
                <div className="rounded-2xl border border-amber-500/20 bg-amber-950/20 p-5 space-y-2">
                  <div className="flex items-center gap-2">
                    <TrendingDown className="h-4 w-4 text-amber-400" />
                    <h4 className="text-xs font-bold uppercase tracking-wider text-amber-300">
                      What-If (Counterfactual Projection)
                    </h4>
                  </div>
                  <p className="text-xs text-slate-200 leading-relaxed font-medium">
                    {decision.whatIfExplanation || "Without timely intervention, failure risk escalates rapidly."}
                  </p>
                </div>

                {/* 4. Impact Explanation */}
                <div className="rounded-2xl border border-emerald-500/20 bg-emerald-950/20 p-5 space-y-2">
                  <div className="flex items-center gap-2">
                    <Leaf className="h-4 w-4 text-emerald-400" />
                    <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-300">
                      Impact (Financial & Environmental ROI)
                    </h4>
                  </div>
                  <p className="text-xs text-slate-200 leading-relaxed font-medium">
                    {decision.impactExplanation || "Intervention minimizes total cost of ownership and CO₂ footprint."}
                  </p>
                </div>
              </div>
            )}

            {activeTab === "evidence" && (
              <EvidenceTraceViewer evidenceTraces={decision.evidenceTraces ?? []} />
            )}

            {activeTab === "trust" && (
              <TrustScoreBreakdownCard
                breakdown={
                  decision.trustBreakdown ?? {
                    confidenceComponent: decision.confidenceScore,
                    evidenceDensityComponent: 85,
                    systemReliabilityComponent: 90,
                    governanceComplianceComponent: 95,
                    dataFreshnessComponent: 90,
                    confidenceWeight: 0.35,
                    evidenceDensityWeight: 0.20,
                    systemReliabilityWeight: 0.20,
                    governanceComplianceWeight: 0.15,
                    dataFreshnessWeight: 0.10,
                    finalTrustScore: decision.trustScore,
                    trustTier: decision.trustTier,
                  }
                }
              />
            )}

            {activeTab === "governance" && (
              <div className="space-y-3">
                {decision.violations?.length ? (
                  decision.violations.map((v) => (
                    <div key={v.id} className="rounded-xl border border-rose-500/30 bg-rose-950/20 p-4 space-y-1">
                      <div className="flex items-center justify-between">
                        <span className="text-xs font-bold text-rose-300 uppercase">{v.ruleName}</span>
                        <span className="rounded bg-rose-500/20 px-2 py-0.5 text-[10px] font-mono text-rose-300">
                          {v.severity}
                        </span>
                      </div>
                      <p className="text-xs text-slate-300">{v.violationMessage}</p>
                    </div>
                  ))
                ) : (
                  <div className="rounded-xl border border-emerald-500/20 bg-emerald-950/20 p-6 text-center text-xs text-emerald-300">
                    No governance violations detected for this decision. Fully compliant.
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Human-in-the-loop actions & Feedback */}
          <div className="mt-8 flex flex-col sm:flex-row items-center justify-between gap-4 border-t border-white/10 pt-5">
            <div className="flex items-center gap-2">
              <span className="text-xs font-semibold text-slate-400">Rate Decision Accuracy:</span>
              <button
                type="button"
                disabled={submittingFeedback}
                onClick={() => handleFeedback("AGREE")}
                className={cn(
                  "flex items-center gap-1.5 rounded-lg border px-3 py-1.5 text-xs font-bold uppercase transition",
                  decision.userFeedback === "AGREE"
                    ? "border-emerald-500 bg-emerald-500/20 text-emerald-300"
                    : "border-white/10 bg-slate-900 text-slate-300 hover:border-emerald-500/40 hover:text-emerald-400"
                )}
              >
                <ThumbsUp className="h-3.5 w-3.5" />
                Agree
              </button>

              <button
                type="button"
                disabled={submittingFeedback}
                onClick={() => handleFeedback("DISAGREE")}
                className={cn(
                  "flex items-center gap-1.5 rounded-lg border px-3 py-1.5 text-xs font-bold uppercase transition",
                  decision.userFeedback === "DISAGREE"
                    ? "border-rose-500 bg-rose-500/20 text-rose-300"
                    : "border-white/10 bg-slate-900 text-slate-300 hover:border-rose-500/40 hover:text-rose-400"
                )}
              >
                <ThumbsDown className="h-3.5 w-3.5" />
                Disagree
              </button>

              <button
                type="button"
                disabled={submittingFeedback}
                onClick={() => handleFeedback("UNSURE")}
                className={cn(
                  "flex items-center gap-1.5 rounded-lg border px-3 py-1.5 text-xs font-bold uppercase transition",
                  decision.userFeedback === "UNSURE"
                    ? "border-amber-500 bg-amber-500/20 text-amber-300"
                    : "border-white/10 bg-slate-900 text-slate-300 hover:border-amber-500/40 hover:text-amber-400"
                )}
              >
                <HelpCircle className="h-3.5 w-3.5" />
                Unsure
              </button>

              {feedbackSuccess && (
                <span className="text-xs text-emerald-400 font-semibold">{feedbackSuccess}</span>
              )}
            </div>

            <div className="flex items-center gap-3">
              {!decision.userReviewed ? (
                <button
                  type="button"
                  disabled={reviewing}
                  onClick={handleReview}
                  className="flex items-center gap-2 rounded-xl bg-cyan-500 px-4 py-2 text-xs font-bold uppercase tracking-wider text-black hover:bg-cyan-400 transition"
                >
                  <CheckCircle className="h-4 w-4" />
                  {reviewing ? "Marking..." : "Mark as Reviewed"}
                </button>
              ) : (
                <span className="flex items-center gap-1.5 text-xs font-semibold text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1 rounded-full">
                  <CheckCircle className="h-3.5 w-3.5" />
                  Reviewed by User
                </span>
              )}
            </div>
          </div>
        </motion.div>
      </div>
    </AnimatePresence>
  );
}
