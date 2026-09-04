"use client";

import { motion } from "framer-motion";
import { History, Wrench, RefreshCw, Cloud, Trash2, IndianRupee, Clock, Zap, HeartHandshake, CheckCircle2 } from "lucide-react";
import type { CircularImpactEvent, CircularEventType } from "@/lib/types/circularEconomy";

interface CircularImpactTimelineProps {
  events: CircularImpactEvent[];
}

const EVENT_TYPE_CONFIG: Record<
  string,
  { label: string; icon: any; color: string; bg: string; border: string }
> = {
  REPAIR_COMPLETED: {
    label: "Hardware Repair Completed",
    icon: Wrench,
    color: "text-emerald-400",
    bg: "bg-emerald-500/10",
    border: "border-emerald-500/30",
  },
  MAINTENANCE_COMPLETED: {
    label: "Preventative Maintenance",
    icon: RefreshCw,
    color: "text-cyan-400",
    bg: "bg-cyan-500/10",
    border: "border-cyan-500/30",
  },
  DEVICE_LIFE_EXTENDED: {
    label: "Lifespan Extension",
    icon: Clock,
    color: "text-blue-400",
    bg: "bg-blue-500/10",
    border: "border-blue-500/30",
  },
  COMPONENT_UPGRADE: {
    label: "Component Upgrade",
    icon: Zap,
    color: "text-amber-300",
    bg: "bg-amber-500/10",
    border: "border-amber-500/30",
  },
  DEVICE_REFURBISHED: {
    label: "Hardware Refurbishment",
    icon: RefreshCw,
    color: "text-purple-400",
    bg: "bg-purple-500/10",
    border: "border-purple-500/30",
  },
  DEVICE_DONATED: {
    label: "Community Device Donation",
    icon: HeartHandshake,
    color: "text-pink-400",
    bg: "bg-pink-500/10",
    border: "border-pink-500/30",
  },
  DEVICE_RECYCLED: {
    label: "Certified Eco-Recycling",
    icon: Trash2,
    color: "text-teal-400",
    bg: "bg-teal-500/10",
    border: "border-teal-500/30",
  },
  RESPONSIBLE_DISPOSAL: {
    label: "Responsible Component Disposal",
    icon: CheckCircle2,
    color: "text-slate-300",
    bg: "bg-white/10",
    border: "border-white/20",
  },
};

export default function CircularImpactTimeline({ events }: CircularImpactTimelineProps) {
  if (!events || events.length === 0) {
    return (
      <div className="rounded-3xl border border-white/10 bg-[#0B1120]/70 p-8 text-center backdrop-blur-xl">
        <History className="size-8 text-slate-500 mx-auto" />
        <h3 className="mt-3 text-base font-bold text-white">No Circular Events Logged Yet</h3>
        <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
          Completed repairs, preventative care routines, and device lifecycle extensions will appear chronologically here.
        </p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div>
        <h2 className="text-xl md:text-2xl font-bold text-white flex items-center gap-2.5">
          <History className="size-6 text-cyan-400" />
          Circular Impact Timeline Stream
        </h2>
        <p className="text-xs md:text-sm text-slate-400 mt-1">
          Verifiable record of hardware preservation, component renewals, and carbon mitigation milestones.
        </p>
      </div>

      <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-3 before:bottom-3 before:w-0.5 before:bg-gradient-to-b before:from-emerald-500 before:via-cyan-500 before:to-slate-800">
        {events.map((evt, idx) => {
          const config = EVENT_TYPE_CONFIG[evt.eventType] || EVENT_TYPE_CONFIG.REPAIR_COMPLETED;
          const Icon = config.icon;

          return (
            <motion.div
              key={evt.id || idx}
              initial={{ opacity: 0, x: -10 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: idx * 0.05 }}
              className="relative space-y-2"
            >
              {/* Dot Icon */}
              <div
                className={`absolute -left-6 top-1.5 flex size-5 items-center justify-center rounded-full border ${config.border} ${config.bg} ${config.color} shadow-sm backdrop-blur-md`}
              >
                <div className="size-1.5 rounded-full bg-current" />
              </div>

              {/* Event Card */}
              <div className="rounded-2xl border border-white/10 bg-[#0B1120]/80 p-4 backdrop-blur-xl shadow-md hover:border-white/20 transition-all">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <div className="flex items-center gap-2">
                    <span
                      className={`inline-flex items-center gap-1 rounded-lg border ${config.border} ${config.bg} px-2 py-0.5 text-[11px] font-bold ${config.color}`}
                    >
                      <Icon className="size-3" />
                      {config.label}
                    </span>
                    <span className="text-xs font-bold text-white">{evt.deviceName}</span>
                  </div>

                  <span className="text-[11px] text-slate-400">
                    {evt.eventDate ? new Date(evt.eventDate).toLocaleDateString() : "Recent"}
                  </span>
                </div>

                {/* Impact Stat Tags */}
                <div className="mt-3 flex flex-wrap items-center gap-2 text-[11px]">
                  {evt.carbonSavedKg > 0 && (
                    <span className="inline-flex items-center gap-1 rounded-lg bg-emerald-500/10 border border-emerald-500/20 px-2 py-0.5 font-semibold text-emerald-400">
                      <Cloud className="size-3" />
                      {evt.carbonSavedKg}kg CO₂ Saved
                    </span>
                  )}
                  {evt.ewastePreventedKg > 0 && (
                    <span className="inline-flex items-center gap-1 rounded-lg bg-cyan-500/10 border border-cyan-500/20 px-2 py-0.5 font-semibold text-cyan-400">
                      <Trash2 className="size-3" />
                      {evt.ewastePreventedKg}kg E-Waste Avoided
                    </span>
                  )}
                  {evt.moneySaved > 0 && (
                    <span className="inline-flex items-center gap-1 rounded-lg bg-amber-500/10 border border-amber-500/20 px-2 py-0.5 font-semibold text-amber-300">
                      <IndianRupee className="size-3" />
                      ₹{evt.moneySaved.toLocaleString("en-IN")} Saved
                    </span>
                  )}
                  {evt.deviceLifeExtensionDays > 0 && (
                    <span className="inline-flex items-center gap-1 rounded-lg bg-blue-500/10 border border-blue-500/20 px-2 py-0.5 font-semibold text-blue-400">
                      <Clock className="size-3" />
                      +{evt.deviceLifeExtensionDays} Days Life
                    </span>
                  )}
                </div>

                <div className="mt-2 text-[10px] text-slate-500 flex items-center justify-between">
                  <span>Source: {evt.impactSource?.replace("_", " ") || "USER ACTION"}</span>
                  {evt.referenceId && <span>Ref: {evt.referenceId}</span>}
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
