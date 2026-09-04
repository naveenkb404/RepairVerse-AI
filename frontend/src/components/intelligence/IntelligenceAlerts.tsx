"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Bell,
  AlertTriangle,
  AlertOctagon,
  Info,
  CheckCircle2,
  Check,
  ArrowRight,
  ShieldAlert,
} from "lucide-react";
import type { DeviceIntelligenceAlertResponse } from "@/lib/types/deviceIntelligence";
import Link from "next/link";

interface IntelligenceAlertsProps {
  alerts: DeviceIntelligenceAlertResponse[];
  onMarkRead?: (alertId: string) => void;
}

export const IntelligenceAlerts: React.FC<IntelligenceAlertsProps> = ({
  alerts,
  onMarkRead,
}) => {
  const getSeverityBadge = (s: string) => {
    switch (s) {
      case "CRITICAL":
        return {
          icon: AlertOctagon,
          badge: "bg-rose-500/20 text-rose-300 border-rose-500/40",
          cardBorder: "border-rose-500/30 bg-rose-950/10",
        };
      case "HIGH":
        return {
          icon: AlertTriangle,
          badge: "bg-amber-500/20 text-amber-300 border-amber-500/40",
          cardBorder: "border-amber-500/30 bg-amber-950/10",
        };
      case "LOW":
        return {
          icon: CheckCircle2,
          badge: "bg-teal-500/20 text-teal-300 border-teal-500/40",
          cardBorder: "border-teal-500/30 bg-teal-950/10",
        };
      case "INFO":
        return {
          icon: Info,
          badge: "bg-cyan-500/20 text-cyan-300 border-cyan-500/40",
          cardBorder: "border-cyan-500/30 bg-cyan-950/10",
        };
      default:
        return {
          icon: Info,
          badge: "bg-blue-500/20 text-blue-300 border-blue-500/40",
          cardBorder: "border-blue-500/30 bg-blue-950/10",
        };
    }
  };

  const getActionLink = (action?: string) => {
    switch (action) {
      case "PROFESSIONAL_SERVICE":
      case "REPAIR_NOW":
        return "/marketplace";
      case "MAINTENANCE_REQUIRED":
        return "/maintenance";
      default:
        return "/devices";
    }
  };

  if (!alerts || alerts.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl flex items-center justify-between shadow-xl">
        <div className="flex items-center gap-3">
          <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
            <CheckCircle2 className="w-5 h-5" />
          </div>
          <div>
            <h4 className="text-sm font-bold text-white">Zero Active Risk Alerts</h4>
            <p className="text-xs text-slate-400">All telemetry parameters within optimal operating limits.</p>
          </div>
        </div>
        <span className="px-3 py-1 rounded-full text-xs font-semibold bg-white/5 text-slate-400 border border-white/10">
          Clean Signal
        </span>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl space-y-4 shadow-xl"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Bell className="w-5 h-5 text-amber-400" />
          <h3 className="text-lg font-bold text-white tracking-wide">
            Active Intelligence Alerts
          </h3>
        </div>
        <span className="text-xs font-semibold text-slate-400">
          {alerts.length} Actionable Notices
        </span>
      </div>

      <div className="space-y-3">
        {alerts.map((alert) => {
          const config = getSeverityBadge(alert.severity);
          const Icon = config.icon;

          return (
            <div
              key={alert.id}
              className={`rounded-2xl border ${config.cardBorder} p-4.5 space-y-3 transition-colors backdrop-blur-md`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-start gap-3">
                  <div className={`p-2 rounded-xl border ${config.badge} shrink-0 mt-0.5`}>
                    <Icon className="w-4 h-4" />
                  </div>
                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <h4 className="text-sm font-bold text-white">{alert.title}</h4>
                      <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold border ${config.badge}`}>
                        {alert.severity}
                      </span>
                    </div>
                    <p className="text-xs text-slate-300 mt-1 leading-relaxed">
                      {alert.message}
                    </p>
                  </div>
                </div>

                {onMarkRead && !alert.isRead && (
                  <button
                    onClick={() => onMarkRead(alert.id)}
                    title="Mark as read"
                    className="p-1.5 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 text-slate-400 hover:text-white transition-colors shrink-0"
                  >
                    <Check className="w-4 h-4" />
                  </button>
                )}
              </div>

              {alert.recommendedAction && (
                <div className="flex items-center justify-between pt-2 border-t border-white/5">
                  <span className="text-[11px] text-slate-400">
                    Recommended Action: <strong className="text-slate-200">{alert.recommendedAction}</strong>
                  </span>
                  <Link
                    href={getActionLink(alert.recommendedAction)}
                    className="inline-flex items-center gap-1 text-xs font-bold text-cyan-400 hover:text-cyan-300 transition-colors"
                  >
                    <span>Take Action</span>
                    <ArrowRight className="w-3 h-3" />
                  </Link>
                </div>
              )}
            </div>
          );
        })}
      </div>
    </motion.div>
  );
};
