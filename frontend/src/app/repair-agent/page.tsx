"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  ArrowLeft,
  Bot,
  Sparkles,
  ShieldCheck,
  RefreshCw,
  AlertTriangle,
  Cpu,
  Wifi,
  WifiOff,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import RepairAgentHero from "@/components/agent/RepairAgentHero";
import AgentImpactDashboard from "@/components/agent/AgentImpactDashboard";
import PendingApprovalsPanel from "@/components/agent/PendingApprovalsPanel";
import InterventionPriorityBoard from "@/components/agent/InterventionPriorityBoard";
import AgentActivityTimeline from "@/components/agent/AgentActivityTimeline";

import {
  fetchAgentDashboard,
  evaluateAllDevices,
  approveActionStep,
  rejectActionStep,
  executeActionStep,
} from "@/lib/api/autonomousRepairAgent";
import type { AgentDashboardResponse } from "@/lib/types/autonomousRepairAgent";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function RepairAgentPage() {
  const [dashboard, setDashboard] = useState<AgentDashboardResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isEvaluatingAll, setIsEvaluatingAll] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 4000);
  };

  const loadData = useCallback(async () => {
    setIsLoading(true);
    try {
      const res = await fetchAgentDashboard();
      if (res.success && res.data) {
        setDashboard(res.data);
      }
    } catch (err) {
      console.error("Failed to load agent dashboard", err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleEvaluateAll = async () => {
    setIsEvaluatingAll(true);
    try {
      const res = await evaluateAllDevices();
      showToast(res.message || "Proactive fleet evaluation completed.");
      await loadData();
    } catch (err) {
      showToast("Evaluation encountered an issue.");
    } finally {
      setIsEvaluatingAll(false);
    }
  };

  const handleApproveStep = async (stepId: string) => {
    try {
      const res = await approveActionStep(stepId);
      showToast(res.message || "Action step approved.");
      await loadData();
    } catch (err) {
      showToast("Failed to approve action step.");
    }
  };

  const handleRejectStep = async (stepId: string) => {
    try {
      const res = await rejectActionStep(stepId);
      showToast(res.message || "Action step rejected.");
      await loadData();
    } catch (err) {
      showToast("Failed to reject action step.");
    }
  };

  const handleExecuteStep = async (stepId: string) => {
    try {
      const res = await executeActionStep(stepId);
      showToast(res.message || "Action executed successfully.");
      await loadData();
    } catch (err) {
      showToast("Failed to execute action step.");
    }
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
            className="fixed top-6 right-6 z-50 flex items-center gap-3 rounded-2xl border border-emerald-500/30 bg-slate-900/90 px-5 py-3.5 shadow-2xl backdrop-blur-xl"
          >
            <Sparkles className="h-4 w-4 text-emerald-400 shrink-0" />
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
              <span className="text-cyan-400 font-medium">Autonomous Repair Agent</span>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={loadData}
              disabled={isLoading}
              className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-white/[0.04] px-3.5 py-2 text-xs font-semibold text-slate-300 transition-colors hover:bg-white/[0.08] hover:text-white disabled:opacity-50"
            >
              <RefreshCw className={`h-3.5 w-3.5 ${isLoading ? "animate-spin" : ""}`} />
              <span>Refresh Agent State</span>
            </button>
          </div>
        </div>

        {/* Hero Section */}
        {dashboard ? (
          <RepairAgentHero
            agentStatus={dashboard.agentStatus}
            monitoredDevicesCount={dashboard.monitoredDevicesCount}
            activeInterventionsCount={dashboard.activeInterventionsCount}
            pendingApprovalsCount={dashboard.pendingApprovalsCount}
            completedExecutionsCount={dashboard.completedExecutionsCount}
            isEvaluatingAll={isEvaluatingAll}
            onEvaluateAll={handleEvaluateAll}
          />
        ) : (
          <div className="h-64 rounded-3xl border border-white/10 bg-white/[0.02] animate-pulse" />
        )}

        {/* Impact Metrics Dashboard */}
        {dashboard && (
          <AgentImpactDashboard
            totalMoneySaved={dashboard.totalMoneySaved}
            totalCo2AvoidedKg={dashboard.totalCo2AvoidedKg}
            completedExecutionsCount={dashboard.completedExecutionsCount}
            monitoredDevicesCount={dashboard.monitoredDevicesCount}
          />
        )}

        {/* Human Approvals Panel (if any pending) */}
        {dashboard && dashboard.pendingApprovals && (
          <PendingApprovalsPanel
            approvals={dashboard.pendingApprovals}
            onApproveStep={handleApproveStep}
            onRejectStep={handleRejectStep}
          />
        )}

        {/* Main Grid: Interventions on Left, Activity Timeline on Right */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Active Proactive Interventions (2 Cols) */}
          <div className="lg:col-span-2 space-y-6">
            <div className="flex items-center justify-between border-b border-white/5 pb-2">
              <div className="flex items-center gap-2">
                <Cpu className="h-4 w-4 text-emerald-400" />
                <h2 className="text-lg font-bold text-white">Proactive Interventions & Action Plans</h2>
              </div>
            </div>

            {dashboard ? (
              <InterventionPriorityBoard
                interventions={dashboard.activeInterventions}
                onApproveStep={handleApproveStep}
                onRejectStep={handleRejectStep}
                onExecuteStep={handleExecuteStep}
              />
            ) : (
              <div className="h-96 rounded-3xl border border-white/10 bg-white/[0.02] animate-pulse" />
            )}
          </div>

          {/* Chronological Autonomous Activity Stream (1 Col) */}
          <div className="lg:col-span-1 space-y-6">
            <div className="flex items-center justify-between border-b border-white/5 pb-2">
              <div className="flex items-center gap-2">
                <Bot className="h-4 w-4 text-cyan-400" />
                <h2 className="text-lg font-bold text-white">Agent Audit Log</h2>
              </div>
            </div>

            {dashboard ? (
              <AgentActivityTimeline executions={dashboard.recentExecutions} />
            ) : (
              <div className="h-96 rounded-3xl border border-white/10 bg-white/[0.02] animate-pulse" />
            )}
          </div>
        </div>
      </Container>
    </main>
  );
}
