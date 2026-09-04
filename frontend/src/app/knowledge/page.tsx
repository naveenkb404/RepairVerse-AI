"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  ArrowLeft,
  Brain,
  Network,
  Sparkles,
  Layers,
  BarChart3,
  BookOpen,
  RefreshCw,
} from "lucide-react";

import Container from "@/components/layout/Container";
import KnowledgeGraphHero from "@/components/knowledge/KnowledgeGraphHero";
import InteractiveKnowledgeGraph from "@/components/knowledge/InteractiveKnowledgeGraph";
import PatternInsightsPanel from "@/components/knowledge/PatternInsightsPanel";
import SimilarRepairCases from "@/components/knowledge/SimilarRepairCases";
import KnowledgeRecommendations from "@/components/knowledge/KnowledgeRecommendations";
import KnowledgeStatistics from "@/components/knowledge/KnowledgeStatistics";

import {
  fetchKnowledgeGraph,
  fetchPatternInsights,
  submitInsightFeedback,
  MOCK_SIMILAR_CASES,
  MOCK_KNOWLEDGE_RECOMMENDATIONS,
} from "@/lib/api/repairKnowledgeGraph";
import type {
  KnowledgeGraphResponse,
  PatternInsightResponse,
  FeedbackType,
} from "@/lib/types/repairKnowledgeGraph";

export default function KnowledgeGraphPage() {
  const [graphData, setGraphData] = useState<KnowledgeGraphResponse | null>(null);
  const [insights, setInsights] = useState<PatternInsightResponse[]>([]);
  const [activeTab, setActiveTab] = useState<"graph" | "insights" | "similar" | "recommendations" | "stats">("graph");
  const [isLoading, setIsLoading] = useState(true);
  const [isRebuilding, setIsRebuilding] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => setToastMessage(null), 4000);
  };

  const loadData = useCallback(async () => {
    setIsLoading(true);
    try {
      const [graphRes, insightsRes] = await Promise.all([
        fetchKnowledgeGraph(),
        fetchPatternInsights(),
      ]);

      if (graphRes.success && graphRes.data) {
        setGraphData(graphRes.data);
      }
      if (insightsRes.success && insightsRes.data) {
        setInsights(insightsRes.data);
      }
    } catch (err) {
      console.error("Failed to load knowledge graph data", err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleRebuildGraph = async () => {
    setIsRebuilding(true);
    try {
      await loadData();
      showToast("Repair Knowledge Graph synchronized with latest ecosystem telemetry.");
    } catch (err) {
      showToast("Knowledge graph sync encountered an issue.");
    } finally {
      setIsRebuilding(false);
    }
  };

  const handleInsightFeedback = async (insightId: string, type: FeedbackType) => {
    try {
      const res = await submitInsightFeedback(insightId, { feedbackType: type });
      showToast(res.message || "Feedback registered. Ecosystem learning updated.");
      if (res.data) {
        setInsights((prev) => prev.map((i) => (i.id === insightId ? res.data! : i)));
      }
    } catch (err) {
      showToast("Failed to submit feedback.");
    }
  };

  const stats = graphData?.statistics || {
    totalNodes: 38,
    totalRelationships: 64,
    totalInsights: insights.length || 12,
    observedRepairsCount: 1420,
    averageConfidence: 0.94,
    nodeTypeDistribution: {},
    relationshipTypeDistribution: {},
  };

  return (
    <main className="min-h-screen bg-slate-950 text-slate-100 selection:bg-cyan-500/30 selection:text-cyan-200">
      {/* Toast Notification */}
      <AnimatePresence>
        {toastMessage && (
          <motion.div
            initial={{ opacity: 0, y: -20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -20, scale: 0.95 }}
            className="fixed top-6 right-6 z-50 flex items-center gap-3 rounded-2xl border border-cyan-500/30 bg-slate-900/90 px-5 py-3.5 shadow-2xl backdrop-blur-xl"
          >
            <Sparkles className="h-4 w-4 text-cyan-400 shrink-0" />
            <span className="text-xs font-medium text-slate-200">{toastMessage}</span>
          </motion.div>
        )}
      </AnimatePresence>

      <Container className="py-8 sm:py-12 space-y-10">
        {/* Top Breadcrumb & Actions Bar */}
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Link
              href="/dashboard"
              className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-3.5 py-2 text-xs font-semibold text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white"
            >
              <ArrowLeft className="h-4 w-4" />
              <span>Back to Dashboard</span>
            </Link>

            <div className="hidden sm:flex items-center gap-2 text-xs text-slate-400">
              <Link href="/dashboard" className="hover:text-slate-200 transition-colors">
                Dashboard
              </Link>
              <span>/</span>
              <span className="text-cyan-400 font-medium">Repair Knowledge Graph</span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={loadData}
              disabled={isLoading}
              className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-3.5 py-2 text-xs font-semibold text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white disabled:opacity-50"
            >
              <RefreshCw className={`h-3.5 w-3.5 ${isLoading ? "animate-spin" : ""}`} />
              <span>Refresh Graph</span>
            </button>
          </div>
        </div>

        {/* Hero Section */}
        <KnowledgeGraphHero
          totalNodes={stats.totalNodes}
          totalRelationships={stats.totalRelationships}
          totalInsights={stats.totalInsights}
          averageConfidence={stats.averageConfidence}
          isRebuilding={isRebuilding}
          onRebuildGraph={handleRebuildGraph}
        />

        {/* Section Navigation Tabs */}
        <div className="flex flex-wrap items-center gap-2 border-b border-white/10 pb-3">
          {[
            { id: "graph", label: "Interactive Knowledge Graph", icon: Network },
            { id: "insights", label: "Pattern Insights", icon: Sparkles },
            { id: "similar", label: "Similar Historical Cases", icon: Layers },
            { id: "recommendations", label: "Evidence Recommendations", icon: Brain },
            { id: "stats", label: "Distribution Analytics", icon: BarChart3 },
          ].map((tab) => {
            const Icon = tab.icon;
            const isSelected = activeTab === tab.id;

            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`inline-flex items-center gap-2 rounded-xl px-4 py-2.5 text-xs font-semibold transition-all ${
                  isSelected
                    ? "bg-gradient-to-r from-cyan-500 to-emerald-500 text-slate-950 shadow-md shadow-cyan-500/20"
                    : "border border-white/10 bg-white/[0.03] text-slate-300 hover:bg-white/[0.08] hover:text-white"
                }`}
              >
                <Icon className="h-4 w-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* Tab Content Panels */}
        <div>
          {activeTab === "graph" && graphData && (
            <InteractiveKnowledgeGraph
              nodes={graphData.nodes}
              relationships={graphData.relationships}
            />
          )}

          {activeTab === "insights" && (
            <PatternInsightsPanel
              insights={insights}
              onFeedback={handleInsightFeedback}
            />
          )}

          {activeTab === "similar" && (
            <SimilarRepairCases cases={MOCK_SIMILAR_CASES} />
          )}

          {activeTab === "recommendations" && (
            <KnowledgeRecommendations recommendations={MOCK_KNOWLEDGE_RECOMMENDATIONS} />
          )}

          {activeTab === "stats" && graphData && (
            <KnowledgeStatistics statistics={stats} />
          )}
        </div>
      </Container>
    </main>
  );
}
