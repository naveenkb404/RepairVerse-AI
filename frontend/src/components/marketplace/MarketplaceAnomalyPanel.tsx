"use client";

import React, { useState } from "react";
import { motion } from "framer-motion";
import { AlertTriangle, Eye, CheckCircle2, XCircle, Clock, ChevronDown, ChevronUp, Shield } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { MarketplaceAnomalyResponse, AnomalySeverity, AnomalyStatus } from "@/lib/types/networkIntelligence";

interface MarketplaceAnomalyPanelProps {
  anomalies: MarketplaceAnomalyResponse[];
  onUpdateStatus?: (id: string, status: string) => Promise<void>;
  isAdmin?: boolean;
}

const SEVERITY_CONFIG: Record<AnomalySeverity, { color: string; bg: string; text: string }> = {
  CRITICAL: { color: "#ef4444", bg: "bg-red-500/15",    text: "text-red-400" },
  HIGH:     { color: "#f97316", bg: "bg-orange-500/15", text: "text-orange-400" },
  MEDIUM:   { color: "#f59e0b", bg: "bg-amber-500/15",  text: "text-amber-400" },
  LOW:      { color: "#8b5cf6", bg: "bg-violet-500/15", text: "text-violet-400" },
};

const STATUS_CONFIG: Record<AnomalyStatus, { label: string; color: string; icon: React.ElementType }> = {
  OPEN:         { label: "Open",         color: "text-red-400",     icon: AlertTriangle },
  UNDER_REVIEW: { label: "Under Review", color: "text-amber-400",   icon: Eye },
  RESOLVED:     { label: "Resolved",     color: "text-emerald-400", icon: CheckCircle2 },
  DISMISSED:    { label: "Dismissed",    color: "text-slate-400",   icon: XCircle },
};

const ANOMALY_TYPE_LABELS: Record<string, string> = {
  SUSPICIOUS_PRICING:         "Suspicious Pricing",
  REVIEW_SPIKE:               "Review Spike",
  REVIEW_PATTERN:             "Review Pattern",
  HIGH_REPEAT_REPAIRS:        "High Repeat Repairs",
  LOW_SUCCESS_RATE:           "Low Success Rate",
  UNUSUAL_CANCELLATION_RATE:  "Unusual Cancellation Rate",
};

export default function MarketplaceAnomalyPanel({
  anomalies,
  onUpdateStatus,
  isAdmin = false,
}: MarketplaceAnomalyPanelProps) {
  const [expanded, setExpanded] = useState<string | null>(null);
  const [updating, setUpdating] = useState<string | null>(null);

  const open   = anomalies.filter(a => a.status === "OPEN");
  const active = anomalies.filter(a => a.status === "UNDER_REVIEW");
  const closed = anomalies.filter(a => a.status === "RESOLVED" || a.status === "DISMISSED");

  const handleUpdate = async (id: string, status: string) => {
    if (!onUpdateStatus) return;
    setUpdating(id);
    await onUpdateStatus(id, status);
    setUpdating(null);
  };

  return (
    <GlassCard className="p-6">
      <div className="flex items-center justify-between mb-5">
        <div className="flex items-center gap-2">
          <Shield className="w-5 h-5 text-orange-400" />
          <h3 className="text-base font-semibold text-white">Anomaly Detection</h3>
        </div>
        <div className="flex items-center gap-3">
          {open.length > 0 && (
            <span className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-red-500/15 border border-red-500/30 text-xs text-red-400">
              <AlertTriangle className="w-3 h-3" />
              {open.length} Open
            </span>
          )}
          {active.length > 0 && (
            <span className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-amber-500/15 border border-amber-500/30 text-xs text-amber-400">
              <Eye className="w-3 h-3" />
              {active.length} Under Review
            </span>
          )}
        </div>
      </div>

      {anomalies.length === 0 ? (
        <div className="text-center py-8">
          <CheckCircle2 className="w-10 h-10 text-emerald-400/40 mx-auto mb-3" />
          <p className="text-sm text-slate-400">No anomalies detected</p>
          <p className="text-xs text-slate-500 mt-1">This provider has no active risk signals.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {anomalies.map((anomaly, i) => (
            <AnomalyRow
              key={anomaly.id}
              anomaly={anomaly}
              index={i}
              isExpanded={expanded === anomaly.id}
              onToggle={() => setExpanded(expanded === anomaly.id ? null : anomaly.id)}
              onUpdate={isAdmin ? (status) => handleUpdate(anomaly.id, status) : undefined}
              isUpdating={updating === anomaly.id}
            />
          ))}
        </div>
      )}

      {!isAdmin && anomalies.length > 0 && (
        <p className="mt-4 text-xs text-slate-500 italic">
          ℹ Anomaly flags are under admin review. No enforcement actions are applied automatically.
        </p>
      )}
    </GlassCard>
  );
}

function AnomalyRow({
  anomaly, index, isExpanded, onToggle, onUpdate, isUpdating,
}: {
  anomaly: MarketplaceAnomalyResponse;
  index: number;
  isExpanded: boolean;
  onToggle: () => void;
  onUpdate?: (status: string) => Promise<void>;
  isUpdating: boolean;
}) {
  const severity = SEVERITY_CONFIG[anomaly.severity] ?? SEVERITY_CONFIG.MEDIUM;
  const statusConf = STATUS_CONFIG[anomaly.status as AnomalyStatus] ?? STATUS_CONFIG.OPEN;
  const StatusIcon = statusConf.icon;

  const detectedDate = new Date(anomaly.detectedAt).toLocaleDateString("en-US", {
    month: "short", day: "numeric", year: "numeric",
  });

  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: index * 0.06 }}
      className="rounded-xl border bg-white/[0.03] overflow-hidden"
      style={{ borderColor: isExpanded ? severity.color + "40" : "rgba(255,255,255,0.06)" }}
    >
      <button
        onClick={onToggle}
        className="w-full flex items-center gap-3 p-3.5 text-left hover:bg-white/[0.03] transition-colors"
      >
        {/* Severity badge */}
        <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold uppercase ${severity.bg} ${severity.text} flex-shrink-0`}>
          {anomaly.severity}
        </span>

        {/* Type */}
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-white truncate">
            {ANOMALY_TYPE_LABELS[anomaly.anomalyType] ?? anomaly.anomalyType}
          </p>
          <p className="text-[10px] text-slate-500 mt-0.5">Detected {detectedDate} · Risk score {anomaly.riskScore}/100</p>
        </div>

        {/* Status */}
        <div className={`flex items-center gap-1.5 flex-shrink-0 ${statusConf.color}`}>
          <StatusIcon className="w-3.5 h-3.5" />
          <span className="text-xs hidden sm:block">{statusConf.label}</span>
        </div>

        {isExpanded ? <ChevronUp className="w-4 h-4 text-slate-500 flex-shrink-0" /> : <ChevronDown className="w-4 h-4 text-slate-500 flex-shrink-0" />}
      </button>

      {isExpanded && (
        <motion.div
          initial={{ height: 0, opacity: 0 }}
          animate={{ height: "auto", opacity: 1 }}
          exit={{ height: 0, opacity: 0 }}
          className="px-3.5 pb-3.5"
        >
          <div className="h-px bg-white/[0.06] mb-3" />
          <p className="text-sm text-slate-300 leading-relaxed mb-3">{anomaly.description}</p>

          {/* Risk Score Bar */}
          <div className="mb-4">
            <div className="flex justify-between text-xs mb-1">
              <span className="text-slate-400">Risk Score</span>
              <span className={severity.text}>{anomaly.riskScore}/100</span>
            </div>
            <div className="h-1.5 bg-white/[0.06] rounded-full">
              <div
                className="h-full rounded-full transition-all duration-700"
                style={{ width: `${anomaly.riskScore}%`, backgroundColor: severity.color }}
              />
            </div>
          </div>

          {/* Admin Actions */}
          {onUpdate && (anomaly.status === "OPEN" || anomaly.status === "UNDER_REVIEW") && (
            <div className="flex gap-2 flex-wrap">
              {anomaly.status === "OPEN" && (
                <button
                  disabled={isUpdating}
                  onClick={() => onUpdate("UNDER_REVIEW")}
                  className="px-3 py-1.5 rounded-lg text-xs bg-amber-500/15 text-amber-400 border border-amber-500/30 hover:bg-amber-500/25 transition-colors disabled:opacity-50 flex items-center gap-1.5"
                >
                  <Clock className="w-3 h-3" />
                  Start Review
                </button>
              )}
              {anomaly.status === "UNDER_REVIEW" && (
                <button
                  disabled={isUpdating}
                  onClick={() => onUpdate("RESOLVED")}
                  className="px-3 py-1.5 rounded-lg text-xs bg-emerald-500/15 text-emerald-400 border border-emerald-500/30 hover:bg-emerald-500/25 transition-colors disabled:opacity-50 flex items-center gap-1.5"
                >
                  <CheckCircle2 className="w-3 h-3" />
                  Mark Resolved
                </button>
              )}
              <button
                disabled={isUpdating}
                onClick={() => onUpdate("DISMISSED")}
                className="px-3 py-1.5 rounded-lg text-xs bg-white/[0.05] text-slate-400 border border-white/[0.08] hover:bg-white/[0.08] transition-colors disabled:opacity-50 flex items-center gap-1.5"
              >
                <XCircle className="w-3 h-3" />
                Dismiss
              </button>
            </div>
          )}
        </motion.div>
      )}
    </motion.div>
  );
}
