"use client";

import React, { useState, useEffect, use } from "react";
import Link from "next/link";
import {
  fetchDeviceDecisions,
  fetchDecisionDetail,
  markDecisionReviewed,
  submitDecisionFeedback,
  fetchTrustDashboard,
} from "@/lib/api/trustEngine";
import type {
  DecisionSummaryResponse,
  DecisionAuditResponse,
  TrustDashboardResponse,
} from "@/lib/types/trustEngine";
import DecisionAuditTable from "@/components/trust-engine/DecisionAuditTable";
import DecisionDetailModal from "@/components/trust-engine/DecisionDetailModal";
import { ShieldCheck, ArrowLeft, Cpu, Activity } from "lucide-react";

interface DeviceTrustPageProps {
  params: Promise<{ deviceId: string }>;
}

export default function DeviceTrustPage({ params }: DeviceTrustPageProps) {
  const resolvedParams = use(params);
  const deviceId = resolvedParams.deviceId;

  const [decisions, setDecisions] = useState<DecisionSummaryResponse[]>([]);
  const [dashboard, setDashboard] = useState<TrustDashboardResponse | null>(null);
  const [selectedDecision, setSelectedDecision] = useState<DecisionAuditResponse | null>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [decRes, dashRes] = await Promise.all([
          fetchDeviceDecisions(deviceId),
          fetchTrustDashboard(),
        ]);
        if (decRes.data) setDecisions(decRes.data);
        if (dashRes.data) setDashboard(dashRes.data);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, [deviceId]);

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
      setDecisions((prev) =>
        prev.map((d) => (d.id === id ? { ...d, userReviewed: true } : d))
      );
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
    setDecisions((prev) =>
      prev.map((d) => (d.id === id ? { ...d, userFeedback: feedback } : d))
    );
  };

  return (
    <div className="min-h-screen bg-slate-950 py-8 px-4 sm:px-6 lg:px-8 text-slate-100 space-y-6">
      {/* Top back navigation */}
      <div className="flex items-center justify-between">
        <Link
          href={`/devices/${deviceId}`}
          className="inline-flex items-center gap-2 rounded-xl border border-white/10 bg-slate-900 px-3.5 py-2 text-xs font-semibold text-slate-300 hover:text-white hover:border-cyan-500/40 transition"
        >
          <ArrowLeft className="h-4 w-4" />
          <span>Back to Device Profile</span>
        </Link>

        <Link
          href="/trust-engine"
          className="inline-flex items-center gap-1.5 text-xs text-cyan-400 hover:text-cyan-300 font-semibold"
        >
          <ShieldCheck className="h-4 w-4" />
          <span>All Ecosystem Decisions</span>
        </Link>
      </div>

      {/* Header */}
      <div className="rounded-3xl border border-white/10 bg-gradient-to-b from-slate-900 via-slate-950 to-black p-6 sm:p-8 shadow-2xl backdrop-blur-2xl">
        <div className="flex flex-wrap items-center gap-2.5 mb-2">
          <span className="rounded-full bg-cyan-500/10 border border-cyan-500/30 px-3 py-0.5 text-xs font-mono font-bold text-cyan-400 uppercase">
            Device Trust Audit
          </span>
          <span className="rounded-full bg-white/5 border border-white/10 px-3 py-0.5 text-xs font-mono text-slate-400">
            {deviceId}
          </span>
        </div>

        <h1 className="text-2xl sm:text-3xl font-black text-white">
          Device Decision Explainability & Trust Audit
        </h1>
        <p className="mt-2 text-xs sm:text-sm text-slate-300 max-w-2xl leading-relaxed">
          Detailed breakdown of all autonomous maintenance triggers, digital twin optimizations, and risk projections executed for this device.
        </p>
      </div>

      {/* Decisions table */}
      <DecisionAuditTable
        decisions={decisions}
        onSelectDecision={handleSelectDecision}
      />

      {/* Detail Modal */}
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
