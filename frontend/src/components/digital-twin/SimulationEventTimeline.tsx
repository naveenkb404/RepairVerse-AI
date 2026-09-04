"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Calendar,
  AlertTriangle,
  Wrench,
  CheckCircle2,
  DollarSign,
  ShieldAlert,
  ArrowRight,
} from "lucide-react";
import type { SimulationEventResponse } from "@/lib/types/digitalTwin";
import { cn } from "@/lib/utils";

interface SimulationEventTimelineProps {
  events: SimulationEventResponse[];
}

export default function SimulationEventTimeline({ events }: SimulationEventTimelineProps) {
  if (!events || events.length === 0) return null;

  const getSeverityBadge = (sev: string) => {
    switch (sev?.toUpperCase()) {
      case "HIGH":
      case "CRITICAL":
        return { label: "High Impact", color: "bg-rose-500/20 text-rose-300 border-rose-500/40", icon: AlertTriangle };
      case "MEDIUM":
        return { label: "Moderate", color: "bg-amber-500/20 text-amber-300 border-amber-500/40", icon: Wrench };
      default:
        return { label: "Informational", color: "bg-cyan-500/20 text-cyan-300 border-cyan-500/40", icon: CheckCircle2 };
    }
  };

  return (
    <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-md space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="text-lg font-bold text-white flex items-center gap-2">
            <Calendar className="h-5 w-5 text-cyan-400" />
            Predicted Lifecycle Event Timeline
          </h3>
          <p className="text-xs text-slate-400 mt-1">
            Deterministic milestones projected across the upcoming operating cycle.
          </p>
        </div>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-gradient-to-b before:from-cyan-500 before:via-emerald-500 before:to-slate-700">
        {events.map((ev, idx) => {
          const badge = getSeverityBadge(ev.severity);
          const Icon = badge.icon;
          const isImmediate = ev.projectedMonthOffset === 0;

          return (
            <motion.div
              key={ev.id || idx}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: idx * 0.05 }}
              className="relative group"
            >
              {/* Dot */}
              <div
                className={cn(
                  "absolute -left-6 top-1.5 h-5 w-5 rounded-full border-2 border-slate-900 flex items-center justify-center transition-all",
                  isImmediate ? "bg-emerald-400 shadow-md shadow-emerald-500/50" : "bg-cyan-400"
                )}
              >
                <div className="h-1.5 w-1.5 rounded-full bg-slate-950" />
              </div>

              {/* Event Content Box */}
              <div className="rounded-2xl border border-white/10 bg-slate-950/60 p-4 transition-all hover:border-white/20">
                <div className="flex flex-wrap items-center justify-between gap-2 mb-2">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-mono font-bold text-cyan-300">
                      {isImmediate ? "Month 0 (Now)" : `Month +${ev.projectedMonthOffset}`}
                    </span>
                    <span className={cn("text-[10px] font-semibold px-2 py-0.5 rounded-full border", badge.color)}>
                      {badge.label}
                    </span>
                  </div>

                  {ev.estimatedFinancialImpact > 0 && (
                    <span className="text-xs font-mono text-amber-400 font-semibold flex items-center gap-1">
                      <DollarSign className="h-3 w-3" />
                      Est. Risk Impact: ₹{ev.estimatedFinancialImpact.toLocaleString()}
                    </span>
                  )}
                </div>

                <h4 className="text-sm font-bold text-white mb-1">{ev.title}</h4>
                <p className="text-xs text-slate-300 leading-relaxed mb-3">{ev.description}</p>

                {ev.mitigationStrategy && (
                  <div className="flex items-center gap-2 text-[11px] text-emerald-300 bg-emerald-950/30 border border-emerald-500/20 px-3 py-1.5 rounded-xl">
                    <ArrowRight className="h-3 w-3" />
                    <span>Recommended Mitigation: <strong>{ev.mitigationStrategy.replace(/_/g, " ")}</strong></span>
                  </div>
                )}
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
