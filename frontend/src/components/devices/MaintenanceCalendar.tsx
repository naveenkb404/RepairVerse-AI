"use client";

import React, { useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Calendar as CalendarIcon,
  Clock,
  ExternalLink,
  Tag,
  AlertCircle,
  Wrench,
  CheckCircle2,
  CalendarCheck,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type { MaintenanceCalendarEvent } from "@/lib/types/maintenance";

interface MaintenanceCalendarProps {
  events: MaintenanceCalendarEvent[];
  isLoading?: boolean;
}

export const MaintenanceCalendar: React.FC<MaintenanceCalendarProps> = ({
  events,
  isLoading = false,
}) => {
  const [selectedType, setSelectedType] = useState<string>("ALL");

  const getEventBadge = (type: string, colorTag: string) => {
    switch (type) {
      case "MAINTENANCE":
        return (
          <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-amber-500/10 text-amber-300 border border-amber-500/20">
            Maintenance
          </span>
        );
      case "BOOKING":
        return (
          <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-cyan-500/10 text-cyan-300 border border-cyan-500/20">
            Shop Booking
          </span>
        );
      case "REPAIR_ACTION":
        return (
          <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-emerald-500/10 text-emerald-300 border border-emerald-500/20">
            Action Plan
          </span>
        );
      case "LIFECYCLE_ALERT":
        return (
          <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-red-500/10 text-red-300 border border-red-500/20 animate-pulse">
            Lifecycle Alert
          </span>
        );
      default:
        return (
          <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-slate-500/10 text-slate-300">
            {type}
          </span>
        );
    }
  };

  const filteredEvents = events.filter((e) => {
    if (selectedType === "ALL") return true;
    return e.eventType === selectedType;
  });

  return (
    <GlassCard className="p-6">
      {/* Calendar Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 border-b border-white/10 pb-4">
        <div>
          <div className="flex items-center gap-2">
            <CalendarCheck className="w-5 h-5 text-cyan-400" />
            <h3 className="text-lg font-bold text-white tracking-wide">
              Unified Maintenance Calendar
            </h3>
          </div>
          <p className="text-xs text-slate-400 mt-1">
            Aggregated chronological schedule across maintenance tasks, shop bookings, and action deadlines.
          </p>
        </div>

        {/* Filter Badges */}
        <div className="flex flex-wrap gap-1.5 text-xs">
          {[
            { id: "ALL", label: "All Events" },
            { id: "MAINTENANCE", label: "Maintenance" },
            { id: "BOOKING", label: "Bookings" },
            { id: "REPAIR_ACTION", label: "Action Plans" },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setSelectedType(tab.id)}
              className={`px-3 py-1 rounded-md font-medium transition-all ${
                selectedType === tab.id
                  ? "bg-cyan-500/20 text-cyan-300 border border-cyan-500/30"
                  : "bg-white/5 text-slate-400 hover:text-slate-200 border border-white/5"
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>
      </div>

      {/* Events Stream */}
      {filteredEvents.length === 0 ? (
        <div className="text-center py-10 text-slate-400">
          <CalendarIcon className="w-10 h-10 text-cyan-400/40 mx-auto mb-2" />
          <p className="text-sm font-medium text-white">No calendar events in this range</p>
          <p className="text-xs text-slate-500 mt-1">
            Your devices are up-to-date with no upcoming deadlines.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredEvents.map((evt, idx) => {
            const isToday = evt.eventDate === new Date().toISOString().split("T")[0];

            return (
              <motion.div
                key={evt.eventId || idx}
                initial={{ opacity: 0, x: -6 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: idx * 0.05 }}
                className={`p-4 rounded-xl border transition-all ${
                  isToday
                    ? "bg-cyan-500/10 border-cyan-500/30"
                    : "bg-white/[0.02] border-white/10 hover:border-white/20"
                }`}
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <div className="flex items-start gap-3">
                    {/* Date Block */}
                    <div className="px-3 py-2 rounded-lg bg-white/5 border border-white/10 text-center min-w-[70px]">
                      <span className="block text-[10px] uppercase font-bold text-slate-400">
                        {new Date(evt.eventDate).toLocaleDateString("en-US", { month: "short" })}
                      </span>
                      <span className="block text-base font-bold text-white leading-none mt-0.5">
                        {new Date(evt.eventDate).toLocaleDateString("en-US", { day: "numeric" })}
                      </span>
                    </div>

                    {/* Event Meta */}
                    <div>
                      <div className="flex flex-wrap items-center gap-2 mb-1">
                        {getEventBadge(evt.eventType, evt.colorTag)}
                        {evt.deviceName && (
                          <span className="text-xs text-slate-400 font-medium">
                            • {evt.deviceName}
                          </span>
                        )}
                        {isToday && (
                          <span className="px-1.5 py-0.5 text-[10px] font-bold rounded bg-cyan-500/20 text-cyan-300">
                            Today
                          </span>
                        )}
                      </div>

                      <h4 className="text-sm font-semibold text-white">
                        {evt.title}
                      </h4>
                      <p className="text-xs text-slate-400 mt-1 line-clamp-1">
                        {evt.description}
                      </p>
                    </div>
                  </div>

                  {/* Action Link */}
                  {evt.actionUrl && (
                    <Link
                      href={evt.actionUrl}
                      className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white/5 hover:bg-white/10 border border-white/10 text-xs font-semibold text-slate-300 hover:text-white transition-colors shrink-0 self-start sm:self-auto"
                    >
                      <span>View</span>
                      <ExternalLink className="w-3.5 h-3.5" />
                    </Link>
                  )}
                </div>
              </motion.div>
            );
          })}
        </div>
      )}
    </GlassCard>
  );
};
