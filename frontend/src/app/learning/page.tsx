"use client";

import React, { useState, useEffect } from "react";
import {
  fetchLearningDashboard,
  runLearningCycle,
  activateModelVersion,
  compareModelVersion,
} from "@/lib/api/federatedLearning";
import type {
  LearningDashboardResponse,
  LearningModelComparisonResponse,
} from "@/lib/types/federatedLearning";
import FederatedLearningHero from "@/components/learning/FederatedLearningHero";
import LearningImpactDashboard from "@/components/learning/LearningImpactDashboard";
import LearningSignalExplorer from "@/components/learning/LearningSignalExplorer";
import ModelVersionTimeline from "@/components/learning/ModelVersionTimeline";
import ModelComparisonPanel from "@/components/learning/ModelComparisonPanel";
import PrivacyAuditPanel from "@/components/learning/PrivacyAuditPanel";
import LearningValidationPanel from "@/components/learning/LearningValidationPanel";
import LearningFeedbackPanel from "@/components/learning/LearningFeedbackPanel";
import LearningTimeline from "@/components/learning/LearningTimeline";
import { Brain } from "lucide-react";

export default function LearningDashboardPage() {
  const [dashboard, setDashboard] = useState<LearningDashboardResponse | null>(null);
  const [comparison, setComparison] = useState<LearningModelComparisonResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [running, setRunning] = useState(false);
  const [runMessage, setRunMessage] = useState<string | null>(null);

  const loadData = async () => {
    try {
      const [dashRes, compRes] = await Promise.all([
        fetchLearningDashboard(),
        compareModelVersion("R35.5"),
      ]);

      if (dashRes.data) setDashboard(dashRes.data);
      if (compRes.data) setComparison(compRes.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleRunLearning = async () => {
    setRunning(true);
    setRunMessage(null);
    try {
      const res = await runLearningCycle();
      if (res.data) {
        setRunMessage(res.data.message);
        setTimeout(() => setRunMessage(null), 5000);
        // Refresh data
        await loadData();
      }
    } finally {
      setRunning(false);
    }
  };

  const handleActivateCandidate = async (version: string) => {
    const res = await activateModelVersion(version);
    if (res.data) {
      await loadData();
    }
  };

  if (loading || !dashboard) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-950 text-white">
        <div className="flex flex-col items-center gap-3">
          <Brain className="h-10 w-10 animate-spin text-indigo-400" />
          <p className="text-sm font-medium text-slate-400">
            Initializing Federated Repair Intelligence Engine...
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950 py-8 px-4 sm:px-6 lg:px-8 text-slate-100 space-y-8">
      {/* 1. Hero */}
      <FederatedLearningHero
        dashboard={dashboard}
        onRunLearning={handleRunLearning}
        isRunning={running}
      />

      {runMessage && (
        <div className="rounded-xl border border-indigo-500/30 bg-indigo-950/40 p-4 text-xs font-semibold text-indigo-300">
          ✨ {runMessage}
        </div>
      )}

      {/* 2. Impact Dashboard */}
      {dashboard.impactMetrics && (
        <LearningImpactDashboard impact={dashboard.impactMetrics} />
      )}

      {/* 3. Candidate Comparison Panel */}
      {comparison && (
        <ModelComparisonPanel
          comparison={comparison}
          onActivateCandidate={handleActivateCandidate}
        />
      )}

      {/* 4. Aggregated Learning Signals */}
      <LearningSignalExplorer signals={dashboard.topSignals ?? []} />

      {/* 5. Validation Guardrails & Privacy Audit */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <LearningValidationPanel />
        <PrivacyAuditPanel audits={dashboard.recentPrivacyAudits ?? []} />
      </div>

      {/* 6. Version Lineage & Community Feedback */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <ModelVersionTimeline history={dashboard.modelHistory ?? []} />
        <LearningFeedbackPanel />
      </div>

      {/* 7. Chronological Event Timeline */}
      <LearningTimeline />
    </div>
  );
}
