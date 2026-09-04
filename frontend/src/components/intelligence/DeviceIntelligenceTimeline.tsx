"use client";

import React from "react";
import { motion } from "framer-motion";
import {
  Clock,
  Cpu,
  Sparkles,
  ShieldCheck,
  Wrench,
  Leaf,
  Activity,
} from "lucide-react";
import type { DeviceIntelligenceTimelineItem } from "@/lib/types/deviceIntelligence";

interface DeviceIntelligenceTimelineProps {
  timeline: DeviceIntelligenceTimelineItem[];
}

export const DeviceIntelligenceTimeline: React.FC<DeviceIntelligenceTimelineProps> = ({
  timeline,
}) => {
  const getEventIcon = (type: string) => {
    switch (type) {
      case "DECISION_EVALUATION":
        return { icon: Cpu, color: "bg-emerald-500/20 text-emerald-400 border-emerald-500/30" };
      case "DIAGNOSIS":
        return { icon: Activity, color: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30" };
      case "CIRCULAR_IMPACT":
        return { icon: Leaf, color: "bg-teal-500/20 text-teal-400 border-teal-500/30" };
      default:
        return { icon: Wrench, color: "bg-blue-500/20 text-blue-400 border-blue-500/30" };
    }
  };

  if (!timeline || timeline.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl text-center text-slate-400 text-sm">
        No intelligence timeline events recorded yet.
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 15 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4 }}
      className="rounded-3xl border border-white/10 bg-slate-900/60 p-6 backdrop-blur-xl space-y-6 shadow-xl"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Clock className="w-5 h-5 text-emerald-400" />
          <h3 className="text-lg font-bold text-white tracking-wide">
            Unified Intelligence Stream
          </h3>
        </div>
        <span className="text-xs font-semibold text-slate-400">
          {timeline.length} Historical Records
        </span>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-white/10">
        {timeline.map((item, idx) => {
          const config = getEventIcon(item.eventType);
          const Icon = config.icon;

          return (
            <motion.div
              key={item.id || idx}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: idx * 0.05 }}
              className="relative space-y-1.5"
            >
              {/* Timeline Node Icon */}
              <div
                className={`absolute -left-[30px] top-0.5 p-1.5 rounded-full border ${config.color} bg-slate-950`}
              >
                <Icon className="w-3 h-3" />
              </div>

              {/* Event Content */}
              <div className="rounded-2xl border border-white/5 bg-white/[0.02] p-4 space-y-1.5">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h4 className="text-sm font-bold text-white">{item.title}</h4>
                  <span className="text-[11px] font-medium text-slate-500">
                    {new Date(item.timestamp).toLocaleDateString(undefined, {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </span>
                </div>

                <p className="text-xs text-slate-300 leading-relaxed">
                  {item.description}
                </p>

                {item.impactBadge && (
                  <div className="pt-1">
                    <span className="inline-block px-2 py-0.5 rounded-md text-[10px] font-bold bg-white/5 border border-white/10 text-slate-400">
                      {item.impactBadge}
                    </span>
                  </div>
                )}
              </div>
            </motion.div>
          );
        })}
      </div>
    </motion.div>
  );
};
