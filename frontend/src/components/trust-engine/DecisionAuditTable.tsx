"use client";

import React, { useState, useMemo } from "react";
import {
  Search,
  Filter,
  ShieldCheck,
  ShieldAlert,
  ChevronRight,
  Sparkles,
  CheckCircle,
  ThumbsUp,
  ThumbsDown,
} from "lucide-react";
import { cn } from "@/lib/utils";
import type { DecisionSummaryResponse } from "@/lib/types/trustEngine";

interface DecisionAuditTableProps {
  decisions: DecisionSummaryResponse[];
  onSelectDecision: (id: string) => void;
  className?: string;
}

export default function DecisionAuditTable({
  decisions,
  onSelectDecision,
  className,
}: DecisionAuditTableProps) {
  const [searchTerm, setSearchTerm] = useState("");
  const [systemFilter, setSystemFilter] = useState("ALL");
  const [tierFilter, setTierFilter] = useState("ALL");

  const filteredDecisions = useMemo(() => {
    return decisions.filter((d) => {
      const matchesSearch =
        d.decisionType.toLowerCase().includes(searchTerm.toLowerCase()) ||
        d.sourceSystem.toLowerCase().includes(searchTerm.toLowerCase()) ||
        d.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
        d.deviceId.toLowerCase().includes(searchTerm.toLowerCase());

      const matchesSystem =
        systemFilter === "ALL" || d.sourceSystem === systemFilter;
      const matchesTier = tierFilter === "ALL" || d.trustTier === tierFilter;

      return matchesSearch && matchesSystem && matchesTier;
    });
  }, [decisions, searchTerm, systemFilter, tierFilter]);

  const getTierBadge = (tier: string) => {
    switch (tier) {
      case "VERIFIED":
        return "border-emerald-500/30 bg-emerald-500/10 text-emerald-400";
      case "RELIABLE":
        return "border-cyan-500/30 bg-cyan-500/10 text-cyan-400";
      case "CAUTION":
        return "border-amber-500/30 bg-amber-500/10 text-amber-400";
      case "REVIEW_REQUIRED":
      default:
        return "border-rose-500/30 bg-rose-500/10 text-rose-400";
    }
  };

  const getRiskBadge = (risk: string) => {
    switch (risk) {
      case "LOW":
        return "text-emerald-400 bg-emerald-500/10 border-emerald-500/20";
      case "MEDIUM":
        return "text-amber-400 bg-amber-500/10 border-amber-500/20";
      case "HIGH":
      case "CRITICAL":
        return "text-rose-400 bg-rose-500/10 border-rose-500/20";
      default:
        return "text-slate-400 bg-slate-500/10 border-slate-500/20";
    }
  };

  const systems = useMemo(() => {
    const set = new Set(decisions.map((d) => d.sourceSystem));
    return ["ALL", ...Array.from(set)];
  }, [decisions]);

  return (
    <div
      className={cn(
        "rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-xl backdrop-blur-xl",
        className
      )}
    >
      {/* Header with Search & Filters */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between pb-5 border-b border-white/10">
        <div>
          <h3 className="text-base font-bold text-white">AI Decision Audit Log</h3>
          <p className="text-xs text-slate-400">
            Immutable trace of autonomous recommendations, predictions, and automated actions
          </p>
        </div>

        <div className="flex flex-wrap items-center gap-3">
          {/* Search bar */}
          <div className="relative">
            <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-400" />
            <input
              type="text"
              placeholder="Search decisions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="h-9 rounded-xl border border-white/10 bg-slate-950/80 pl-8 pr-3 text-xs text-slate-200 placeholder-slate-500 focus:border-cyan-500 focus:outline-none"
            />
          </div>

          {/* System filter */}
          <select
            value={systemFilter}
            onChange={(e) => setSystemFilter(e.target.value)}
            className="h-9 rounded-xl border border-white/10 bg-slate-950/80 px-3 text-xs text-slate-200 focus:border-cyan-500 focus:outline-none"
          >
            {systems.map((s) => (
              <option key={s} value={s}>
                System: {s}
              </option>
            ))}
          </select>

          {/* Tier filter */}
          <select
            value={tierFilter}
            onChange={(e) => setTierFilter(e.target.value)}
            className="h-9 rounded-xl border border-white/10 bg-slate-950/80 px-3 text-xs text-slate-200 focus:border-cyan-500 focus:outline-none"
          >
            <option value="ALL">All Trust Tiers</option>
            <option value="VERIFIED">VERIFIED</option>
            <option value="RELIABLE">RELIABLE</option>
            <option value="CAUTION">CAUTION</option>
            <option value="REVIEW_REQUIRED">REVIEW_REQUIRED</option>
          </select>
        </div>
      </div>

      {/* Decision Table */}
      <div className="mt-4 overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead>
            <tr className="border-b border-white/10 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
              <th className="pb-3 pr-4">Decision / Type</th>
              <th className="pb-3 pr-4">Source System</th>
              <th className="pb-3 pr-4">Trust Score</th>
              <th className="pb-3 pr-4">AI Confidence</th>
              <th className="pb-3 pr-4">Risk Level</th>
              <th className="pb-3 pr-4">Status & Feedback</th>
              <th className="pb-3 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-white/5">
            {filteredDecisions.length === 0 ? (
              <tr>
                <td colSpan={7} className="py-8 text-center text-slate-400">
                  No decision records match the current filters.
                </td>
              </tr>
            ) : (
              filteredDecisions.map((d) => (
                <tr
                  key={d.id}
                  className="group transition hover:bg-white/[0.02] cursor-pointer"
                  onClick={() => onSelectDecision(d.id)}
                >
                  <td className="py-3.5 pr-4">
                    <div className="font-bold text-white group-hover:text-cyan-300 transition">
                      {d.decisionType.replace(/_/g, " ")}
                    </div>
                    <div className="text-[10px] text-slate-500 font-mono">
                      {d.id} • {new Date(d.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </div>
                  </td>

                  <td className="py-3.5 pr-4">
                    <span className="rounded bg-white/5 px-2 py-0.5 font-mono text-[10px] text-slate-300 border border-white/5">
                      {d.sourceSystem}
                    </span>
                  </td>

                  <td className="py-3.5 pr-4">
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold text-white">{d.trustScore}</span>
                      <span
                        className={cn(
                          "rounded-full px-2 py-0.5 text-[10px] font-bold border uppercase",
                          getTierBadge(d.trustTier)
                        )}
                      >
                        {d.trustTier}
                      </span>
                    </div>
                  </td>

                  <td className="py-3.5 pr-4">
                    <div className="flex items-center gap-1.5">
                      <div className="h-1.5 w-12 rounded-full bg-slate-800 overflow-hidden">
                        <div
                          className="h-full bg-indigo-500 rounded-full"
                          style={{ width: `${d.confidenceScore}%` }}
                        />
                      </div>
                      <span className="font-mono font-medium text-slate-300">
                        {d.confidenceScore}%
                      </span>
                    </div>
                  </td>

                  <td className="py-3.5 pr-4">
                    <span
                      className={cn(
                        "rounded-full px-2 py-0.5 text-[10px] font-bold border uppercase",
                        getRiskBadge(d.riskLevel)
                      )}
                    >
                      {d.riskLevel}
                    </span>
                  </td>

                  <td className="py-3.5 pr-4">
                    <div className="flex items-center gap-2">
                      {d.userReviewed ? (
                        <span className="flex items-center gap-1 text-[10px] text-emerald-400 font-medium">
                          <CheckCircle className="h-3 w-3" />
                          Reviewed
                        </span>
                      ) : (
                        <span className="text-[10px] text-slate-500">Unreviewed</span>
                      )}

                      {d.userFeedback === "AGREE" && (
                        <span className="flex items-center gap-1 text-[10px] text-emerald-400">
                          <ThumbsUp className="h-3 w-3" />
                        </span>
                      )}
                      {d.userFeedback === "DISAGREE" && (
                        <span className="flex items-center gap-1 text-[10px] text-rose-400">
                          <ThumbsDown className="h-3 w-3" />
                        </span>
                      )}
                    </div>
                  </td>

                  <td className="py-3.5 text-right">
                    <button
                      type="button"
                      onClick={(e) => {
                        e.stopPropagation();
                        onSelectDecision(d.id);
                      }}
                      className="inline-flex items-center gap-1 rounded-lg border border-white/10 bg-slate-900 px-2.5 py-1 text-[11px] font-bold text-cyan-400 hover:border-cyan-500/40 hover:bg-cyan-500/10 transition"
                    >
                      <span>Explain</span>
                      <ChevronRight className="h-3 w-3" />
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
