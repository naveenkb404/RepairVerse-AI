"use client";

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  fetchTrustDashboard,
  fetchDecisionDetail,
  markDecisionReviewed,
  submitDecisionFeedback,
  fetchGovernanceRules,
  updateAutonomyPreferences,
} from "@/lib/api/trustEngine";
import type {
  TrustDashboardResponse,
  DecisionAuditResponse,
  GovernanceRuleResponse,
  UpdateAutonomyPreferencesRequest,
} from "@/lib/types/trustEngine";
import TrustEngineHero from "@/components/trust-engine/TrustEngineHero";
import ActiveViolationsAlert from "@/components/trust-engine/ActiveViolationsAlert";
import DecisionAuditTable from "@/components/trust-engine/DecisionAuditTable";
import GovernanceRulesPanel from "@/components/trust-engine/GovernanceRulesPanel";
import UserAutonomyControls from "@/components/trust-engine/UserAutonomyControls";
import DecisionDetailModal from "@/components/trust-engine/DecisionDetailModal";
import { Shield, Sparkles, Scale } from "lucide-react";

export default function TrustEnginePage() {
  const [dashboard, setDashboard] = useState<TrustDashboardResponse | null>(null);
  const [governanceRules, setGovernanceRules] = useState<GovernanceRuleResponse[]>([]);
  const [selectedDecision, setSelectedDecision] = useState<DecisionAuditResponse | null>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  const loadData = async () => {
    try {
      const [dashRes, rulesRes] = await Promise.all([
        fetchTrustDashboard(),
        fetchGovernanceRules(),
      ]);

      if (dashRes.data) {
        setDashboard(dashRes.data);
      }
      if (rulesRes.data) {
        setGovernanceRules(rulesRes.data);
      }
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleRefresh = () => {
    setRefreshing(true);
    loadData();
  };

  const handleSelectDecision = async (id: string) => {
    const res = await fetchDecisionDetail(id);
    if (res.data) {
      setSelectedDecision(res.data);
      setIsDetailOpen(true);
    }
  };

  const handleMarkReviewed = async (id: string) => {
    const res = await markDecisionReviewed(id);
    if (res.data) {
      setSelectedDecision(res.data);
      // Update local dashboard
      if (dashboard) {
        const updatedRecent = dashboard.recentDecisions.map((d) =>
          d.id === id ? { ...d, userReviewed: true } : d
        );
        setDashboard({
          ...dashboard,
          recentDecisions: updatedRecent,
          decisionsReviewedByUser: (dashboard.decisionsReviewedByUser ?? 0) + 1,
        });
      }
    }
  };

  const handleSubmitFeedback = async (
    id: string,
    feedback: "AGREE" | "DISAGREE" | "UNSURE"
  ) => {
    await submitDecisionFeedback(id, feedback);
    if (selectedDecision && selectedDecision.id === id) {
      setSelectedDecision({ ...selectedDecision, userFeedback: feedback });
    }
    if (dashboard) {
      const updatedRecent = dashboard.recentDecisions.map((d) =>
        d.id === id ? { ...d, userFeedback: feedback } : d
      );
      setDashboard({ ...dashboard, recentDecisions: updatedRecent });
    }
  };

  const handleSaveAutonomy = async (updated: UpdateAutonomyPreferencesRequest) => {
    const res = await updateAutonomyPreferences(updated);
    if (res.data && dashboard) {
      setDashboard({ ...dashboard, autonomyPreferences: res.data });
    }
  };

  if (loading || !dashboard) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-950 text-white">
        <div className="flex flex-col items-center gap-3">
          <Shield className="h-10 w-10 animate-spin text-cyan-400" />
          <p className="text-sm font-medium text-slate-400">
            Initializing AI Decision Trust & Explainability Engine...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 py-8 px-4 sm:px-6 lg:px-8 text-slate-100 space-y-8">
      {/* 1. Hero */}
      <TrustEngineHero
        dashboard={dashboard}
        onRefresh={handleRefresh}
        isRefreshing={refreshing}
      />

      {/* 2. Active Governance Violations / Compliance Status */}
      <ActiveViolationsAlert
        violations={dashboard.activeViolationsList ?? []}
        onSelectDecision={handleSelectDecision}
      />

      {/* 3. Decision Audit Log */}
      <DecisionAuditTable
        decisions={dashboard.recentDecisions ?? []}
        onSelectDecision={handleSelectDecision}
      />

      {/* 4. Autonomy & Consent Controls */}
      {dashboard.autonomyPreferences && (
        <UserAutonomyControls
          preferences={dashboard.autonomyPreferences}
          onSave={handleSaveAutonomy}
        />
      )}

      {/* 5. Active Governance Rules */}
      <GovernanceRulesPanel rules={governanceRules} />

      {/* Decision Detail & Explainability Modal */}
      <DecisionDetailModal
        decision={selectedDecision}
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        onMarkReviewed={handleMarkReviewed}
        onSubmitFeedback={handleSubmitFeedback}
      />
    </div>
  );
}
