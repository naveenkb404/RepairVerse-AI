"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Activity,
  AlertTriangle,
  Award,
  CheckCircle2,
  ChevronDown,
  ChevronUp,
  Cpu,
  DollarSign,
  FileText,
  HelpCircle,
  Info,
  Layers,
  Leaf,
  RefreshCw,
  Shield,
  ShieldAlert,
  Sparkles,
  Wrench,
  Zap,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import type {
  DeviceRiskExplanationResponse,
  DiagnosisExplanationResponse,
  RecommendationExplanationResponse,
  SustainabilityNarrativeResponse,
} from "@/lib/types/aiExplanation";

type ExplanationData =
  | { type: "device-risk"; data: DeviceRiskExplanationResponse }
  | { type: "diagnosis"; data: DiagnosisExplanationResponse }
  | { type: "recommendation"; data: RecommendationExplanationResponse }
  | { type: "sustainability"; data: SustainabilityNarrativeResponse };

interface ExplainableAiCardProps {
  title?: string;
  subtitle?: string;
  explanation: ExplanationData | null;
  isLoading?: boolean;
  onRefresh?: () => void;
  defaultExpanded?: boolean;
  className?: string;
}

const EASE = [0.22, 1, 0.36, 1] as const;

export default function ExplainableAiCard({
  title = "AI Explainability & Hardware Intelligence",
  subtitle = "Transparent reasoning, root-cause analysis, and physics-based insights powered by Gemini",
  explanation,
  isLoading = false,
  onRefresh,
  defaultExpanded = true,
  className = "",
}: ExplainableAiCardProps) {
  const [isExpanded, setIsExpanded] = useState(defaultExpanded);
  const [activeTab, setActiveTab] = useState<"overview" | "technical" | "roadmap" | "impact">("overview");

  if (isLoading) {
    return (
      <GlassCard className={`p-6 border-[#8B5CF6]/30 ${className}`}>
        <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
          <div className="flex items-center gap-2.5">
            <div className="flex size-8 items-center justify-center rounded-xl bg-purple-500/20 text-purple-400">
              <Sparkles className="size-4 animate-pulse" />
            </div>
            <div>
              <h3 className="text-sm font-bold text-white">Synthesizing Explainable Intelligence…</h3>
              <p className="text-[11px] text-white/50">Consulting Gemini hardware reasoning models</p>
            </div>
          </div>
          <RefreshCw className="size-4 animate-spin text-purple-400" />
        </div>
        <div className="space-y-3 py-4">
          <div className="h-4 w-3/4 animate-pulse rounded bg-white/10" />
          <div className="h-4 w-5/6 animate-pulse rounded bg-white/5" />
          <div className="h-4 w-2/3 animate-pulse rounded bg-white/5" />
        </div>
      </GlassCard>
    );
  }

  if (!explanation) return null;

  const modelBadge = "modelUsed" in explanation.data ? explanation.data.modelUsed : "Gemini 1.5 Flash";
  const isDemo = "isDemo" in explanation.data ? explanation.data.isDemo : false;

  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, ease: EASE }}
      className={className}
    >
      <GlassCard className="p-6 border-purple-500/25 bg-gradient-to-br from-purple-950/20 via-[#0B1120] to-[#0B1120] relative overflow-hidden">
        {/* Ambient Top Glow */}
        <div
          className="pointer-events-none absolute -top-24 -right-24 size-64 rounded-full bg-purple-500/10 blur-3xl"
          aria-hidden
        />

        {/* Card Header */}
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/10 pb-4 mb-4">
          <div className="flex items-center gap-3">
            <div className="flex size-9 items-center justify-center rounded-xl bg-gradient-to-br from-purple-500/30 to-cyan-500/20 border border-purple-500/30 text-purple-300">
              <Sparkles className="size-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base font-bold text-white">{title}</h3>
                <span className="rounded-full bg-purple-500/15 border border-purple-500/30 px-2 py-0.5 text-[9px] font-bold text-purple-300">
                  {modelBadge}
                </span>
                {isDemo && (
                  <span className="rounded-full bg-cyan-500/10 border border-cyan-500/30 px-2 py-0.5 text-[9px] font-semibold text-cyan-400">
                    DEMO MODE
                  </span>
                )}
              </div>
              <p className="text-[11px] text-white/50">{subtitle}</p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {onRefresh && (
              <button
                onClick={onRefresh}
                title="Regenerate Explainable AI summary"
                className="flex size-8 items-center justify-center rounded-lg border border-white/10 bg-white/[0.04] text-white/70 hover:bg-white/10 hover:text-white transition"
              >
                <RefreshCw className="size-3.5" />
              </button>
            )}
            <button
              onClick={() => setIsExpanded(!isExpanded)}
              className="flex items-center gap-1 rounded-lg border border-white/10 bg-white/[0.04] px-2.5 py-1 text-xs text-white/70 hover:bg-white/10 hover:text-white transition"
            >
              <span>{isExpanded ? "Collapse" : "Expand"}</span>
              {isExpanded ? <ChevronUp className="size-3.5" /> : <ChevronDown className="size-3.5" />}
            </button>
          </div>
        </div>

        {/* Expandable Body */}
        <AnimatePresence>
          {isExpanded && (
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: "auto" }}
              exit={{ opacity: 0, height: 0 }}
              transition={{ duration: 0.3, ease: EASE }}
              className="space-y-5"
            >
              {/* Tab Navigation */}
              <div className="flex flex-wrap items-center gap-1 rounded-xl border border-white/10 bg-white/[0.03] p-1 text-xs">
                <button
                  onClick={() => setActiveTab("overview")}
                  className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 font-semibold transition ${
                    activeTab === "overview"
                      ? "bg-purple-500/20 text-purple-200 border border-purple-500/30 shadow-sm"
                      : "text-white/60 hover:text-white"
                  }`}
                >
                  <FileText className="size-3.5" />
                  Overview
                </button>
                <button
                  onClick={() => setActiveTab("technical")}
                  className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 font-semibold transition ${
                    activeTab === "technical"
                      ? "bg-purple-500/20 text-purple-200 border border-purple-500/30 shadow-sm"
                      : "text-white/60 hover:text-white"
                  }`}
                >
                  <Cpu className="size-3.5" />
                  Technical Analysis
                </button>
                <button
                  onClick={() => setActiveTab("roadmap")}
                  className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 font-semibold transition ${
                    activeTab === "roadmap"
                      ? "bg-purple-500/20 text-purple-200 border border-purple-500/30 shadow-sm"
                      : "text-white/60 hover:text-white"
                  }`}
                >
                  <Wrench className="size-3.5" />
                  Roadmap & Safety
                </button>
                <button
                  onClick={() => setActiveTab("impact")}
                  className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 font-semibold transition ${
                    activeTab === "impact"
                      ? "bg-purple-500/20 text-purple-200 border border-purple-500/30 shadow-sm"
                      : "text-white/60 hover:text-white"
                  }`}
                >
                  <Leaf className="size-3.5" />
                  Economics & Impact
                </button>
              </div>

              {/* ─── TAB 1: OVERVIEW ─── */}
              {activeTab === "overview" && (
                <div className="space-y-4">
                  {explanation.type === "device-risk" && (
                    <>
                      <div className="rounded-2xl border border-purple-500/20 bg-purple-500/10 p-4 text-xs leading-relaxed text-purple-100">
                        <strong className="block text-sm font-bold text-white mb-1 flex items-center gap-1.5">
                          <Activity className="size-4 text-purple-400" />
                          Executive Assessment
                        </strong>
                        {explanation.data.executiveSummary}
                      </div>

                      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                        <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-0.5">
                            Urgency & Timing
                          </span>
                          <p className="text-xs font-bold text-amber-300">{explanation.data.urgencyRating}</p>
                        </div>
                        <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-0.5">
                            Economic Defense
                          </span>
                          <p className="text-xs text-white/80">{explanation.data.economicJustification}</p>
                        </div>
                      </div>
                    </>
                  )}

                  {explanation.type === "diagnosis" && (
                    <>
                      <div className="rounded-2xl border border-cyan-500/20 bg-cyan-500/10 p-4 text-xs leading-relaxed text-cyan-100">
                        <strong className="block text-sm font-bold text-white mb-1 flex items-center gap-1.5">
                          <Info className="size-4 text-cyan-400" />
                          Diagnosis Feasibility Rationale
                        </strong>
                        {explanation.data.repairFeasibilityRationale}
                      </div>

                      <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3 text-xs text-white/80">
                        <strong className="text-white block mb-1">Symptom Correlation:</strong>
                        {explanation.data.symptomCorrelation}
                      </div>
                    </>
                  )}

                  {explanation.type === "recommendation" && (
                    <>
                      <div className="rounded-2xl border border-emerald-500/20 bg-emerald-500/10 p-4 text-xs leading-relaxed text-emerald-100">
                        <strong className="block text-sm font-bold text-white mb-1 flex items-center gap-1.5">
                          <DollarSign className="size-4 text-emerald-400" />
                          Cost-Benefit Recommendation Rationale
                        </strong>
                        {explanation.data.costBenefitRationale}
                      </div>

                      <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
                        <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-0.5">
                            Lifespan Extension
                          </span>
                          <p className="text-xs font-semibold text-emerald-300">
                            {explanation.data.lifespanExtensionAnalysis}
                          </p>
                        </div>
                        <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-[10px] uppercase tracking-wider text-white/50 block mb-0.5">
                            Salvage Valuation
                          </span>
                          <p className="text-xs text-white/80">{explanation.data.salvageValueAssessment}</p>
                        </div>
                      </div>
                    </>
                  )}

                  {explanation.type === "sustainability" && (
                    <>
                      <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-4 text-xs leading-relaxed text-[#22C55E]">
                        <strong className="block text-sm font-black text-white mb-1 flex items-center gap-1.5">
                          <Leaf className="size-4 text-[#22C55E]" />
                          {explanation.data.impactHeadline}
                        </strong>
                        {explanation.data.storytellingNarrative}
                      </div>

                      <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/90">
                        <strong className="text-[#06B6D4] block mb-1">Real-World Equivalence:</strong>
                        {explanation.data.tangibleRealWorldEquivalents}
                      </div>
                    </>
                  )}
                </div>
              )}

              {/* ─── TAB 2: TECHNICAL ANALYSIS ─── */}
              {activeTab === "technical" && (
                <div className="space-y-4">
                  {explanation.type === "device-risk" && (
                    <>
                      <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/80">
                        <h4 className="font-bold text-white mb-1">Physical Root Cause Analysis</h4>
                        <p className="leading-relaxed">{explanation.data.rootCauseAnalysis}</p>
                      </div>

                      <div>
                        <h4 className="text-xs font-bold uppercase tracking-wider text-white/60 mb-2">
                          Key Contributing Risk Factors
                        </h4>
                        <div className="space-y-2">
                          {explanation.data.keyContributingFactors.map((factor, i) => (
                            <div
                              key={i}
                              className="rounded-xl border border-white/5 bg-white/[0.02] p-3 text-xs flex flex-col sm:flex-row sm:items-center justify-between gap-2"
                            >
                              <div>
                                <span className="font-bold text-white">{factor.factorName}</span>
                                <p className="text-[11px] text-white/60 mt-0.5">{factor.explanation}</p>
                              </div>
                              <span
                                className={`rounded-full px-2 py-0.5 text-[9px] font-bold self-start sm:self-center ${
                                  factor.severity === "CRITICAL" || factor.severity === "HIGH"
                                    ? "bg-orange-500/20 text-orange-400 border border-orange-500/30"
                                    : "bg-emerald-500/20 text-emerald-400 border border-emerald-500/30"
                                }`}
                              >
                                {factor.severity}
                              </span>
                            </div>
                          ))}
                        </div>
                      </div>

                      {explanation.data.componentWearAssessment && (
                        <div>
                          <h4 className="text-xs font-bold uppercase tracking-wider text-white/60 mb-2">
                            Component Wear Matrix
                          </h4>
                          <div className="grid grid-cols-1 gap-2 sm:grid-cols-3">
                            {explanation.data.componentWearAssessment.map((comp, i) => (
                              <div key={i} className="rounded-xl border border-white/5 bg-white/[0.02] p-3 text-xs">
                                <span className="font-bold text-white block">{comp.component}</span>
                                <span className="text-[10px] text-[#06B6D4] block">{comp.status}</span>
                                <p className="text-[10px] text-white/50 mt-1">{comp.wearMechanisms}</p>
                                <span className="text-[9px] text-white/40 block mt-1">
                                  Remaining: {comp.estimatedRemainingLife}
                                </span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </>
                  )}

                  {explanation.type === "diagnosis" && (
                    <>
                      <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/80">
                        <h4 className="font-bold text-white mb-1">Visual Evidence Analysis</h4>
                        <p className="leading-relaxed">{explanation.data.visualEvidenceAnalysis}</p>
                      </div>

                      {explanation.data.differentialDiagnoses && (
                        <div>
                          <h4 className="text-xs font-bold uppercase tracking-wider text-white/60 mb-2">
                            Differential Diagnoses Considered
                          </h4>
                          <div className="space-y-1.5">
                            {explanation.data.differentialDiagnoses.map((diff, i) => (
                              <div
                                key={i}
                                className="flex items-center gap-2 rounded-lg border border-white/5 bg-white/[0.02] p-2 text-xs text-white/70"
                              >
                                <HelpCircle className="size-3.5 text-purple-400 shrink-0" />
                                <span>{diff}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </>
                  )}

                  {explanation.type === "recommendation" && (
                    <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/80">
                      <h4 className="font-bold text-white mb-1">Lifecycle Extension Mechanics</h4>
                      <p className="leading-relaxed">{explanation.data.lifespanExtensionAnalysis}</p>
                    </div>
                  )}

                  {explanation.type === "sustainability" && (
                    <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/80">
                      <h4 className="font-bold text-white mb-1">Future Environmental Projection</h4>
                      <p className="leading-relaxed">{explanation.data.futureImpactProjection}</p>
                    </div>
                  )}
                </div>
              )}

              {/* ─── TAB 3: ROADMAP & SAFETY ─── */}
              {activeTab === "roadmap" && (
                <div className="space-y-4">
                  {explanation.type === "device-risk" && (
                    <>
                      {explanation.data.safetyPrecautions && (
                        <div>
                          <h4 className="text-xs font-bold uppercase tracking-wider text-orange-400 mb-2 flex items-center gap-1.5">
                            <ShieldAlert className="size-3.5" />
                            Safety & Precaution Guidelines
                          </h4>
                          <div className="space-y-1.5">
                            {explanation.data.safetyPrecautions.map((precaution, i) => (
                              <div
                                key={i}
                                className="flex items-start gap-2 rounded-xl border border-orange-500/20 bg-orange-500/5 p-2.5 text-xs text-orange-200"
                              >
                                <AlertTriangle className="size-3.5 text-orange-400 shrink-0 mt-0.5" />
                                <span>{precaution}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {explanation.data.preventiveActionRoadmap && (
                        <div>
                          <h4 className="text-xs font-bold uppercase tracking-wider text-white/70 mb-2 flex items-center gap-1.5">
                            <CheckCircle2 className="size-3.5 text-[#22C55E]" />
                            Preventive Maintenance Action Roadmap
                          </h4>
                          <div className="space-y-2">
                            {explanation.data.preventiveActionRoadmap.map((step, i) => (
                              <div
                                key={i}
                                className="flex items-start gap-2.5 rounded-xl border border-white/10 bg-white/[0.02] p-2.5 text-xs text-white/90"
                              >
                                <span className="flex size-5 shrink-0 items-center justify-center rounded-full bg-purple-500/20 text-[10px] font-bold text-purple-300">
                                  {i + 1}
                                </span>
                                <span>{step}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </>
                  )}

                  {explanation.type === "diagnosis" && (
                    <>
                      <div className="rounded-xl border border-amber-500/30 bg-amber-500/10 p-3.5 text-xs text-amber-200">
                        <strong className="font-bold text-white block mb-1 flex items-center gap-1.5">
                          <ShieldAlert className="size-3.5 text-amber-400" />
                          Safety Warning Context
                        </strong>
                        {explanation.data.safetyWarningContext}
                      </div>

                      {explanation.data.requiredToolsRationale && (
                        <div>
                          <h4 className="text-xs font-bold uppercase tracking-wider text-white/60 mb-2">
                            Required Tools Rationale
                          </h4>
                          <div className="space-y-1.5">
                            {explanation.data.requiredToolsRationale.map((tool, i) => (
                              <div
                                key={i}
                                className="flex items-start gap-2 rounded-xl border border-white/5 bg-white/[0.02] p-2.5 text-xs text-white/80"
                              >
                                <Wrench className="size-3.5 text-[#06B6D4] shrink-0 mt-0.5" />
                                <span>{tool}</span>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}
                    </>
                  )}

                  {explanation.type === "recommendation" && (
                    <div>
                      <h4 className="text-xs font-bold uppercase tracking-wider text-white/70 mb-2">
                        Risk-Adjusted Execution Steps
                      </h4>
                      <div className="space-y-2">
                        {explanation.data.riskAdjustedNextSteps.map((step, i) => (
                          <div
                            key={i}
                            className="flex items-start gap-2.5 rounded-xl border border-white/10 bg-white/[0.02] p-2.5 text-xs text-white/90"
                          >
                            <CheckCircle2 className="size-4 text-emerald-400 shrink-0 mt-0.5" />
                            <span>{step}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {explanation.type === "sustainability" && (
                    <div>
                      <h4 className="text-xs font-bold uppercase tracking-wider text-emerald-400 mb-2 flex items-center gap-1.5">
                        <Award className="size-3.5 text-emerald-400" />
                        Circular Economy Milestone Achievements
                      </h4>
                      <div className="grid grid-cols-1 gap-2 sm:grid-cols-3">
                        {explanation.data.circularEconomyAchievements.map((badge, i) => (
                          <div
                            key={i}
                            className="flex items-center gap-2 rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-3 text-xs font-bold text-emerald-200"
                          >
                            <Award className="size-4 text-emerald-400 shrink-0" />
                            <span>{badge}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>
              )}

              {/* ─── TAB 4: ECONOMICS & IMPACT ─── */}
              {activeTab === "impact" && (
                <div className="space-y-4">
                  {explanation.type === "device-risk" && (
                    <div className="rounded-xl border border-white/10 bg-white/[0.03] p-3.5 text-xs text-white/80">
                      <h4 className="font-bold text-white mb-1">Preventative Economic Valuation</h4>
                      <p className="leading-relaxed">{explanation.data.economicJustification}</p>
                    </div>
                  )}

                  {explanation.type === "recommendation" && (
                    <div className="rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-3.5 text-xs text-emerald-100">
                      <h4 className="font-bold text-white mb-1 flex items-center gap-1.5">
                        <Leaf className="size-3.5 text-[#22C55E]" />
                        Environmental Tradeoff Analysis
                      </h4>
                      <p className="leading-relaxed">{explanation.data.environmentalTradeoffNarrative}</p>
                    </div>
                  )}

                  {explanation.type === "sustainability" && (
                    <div className="rounded-xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-4 text-xs text-[#22C55E]">
                      <strong className="block text-sm font-bold text-white mb-1">
                        Tangible Real-World Equivalence
                      </strong>
                      <p className="leading-relaxed text-white/80">
                        {explanation.data.tangibleRealWorldEquivalents}
                      </p>
                    </div>
                  )}
                </div>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </GlassCard>
    </motion.div>
  );
}
