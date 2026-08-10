"use client";

import { motion } from "framer-motion";
import { GitCommit, ShoppingBag, Sparkles, Wrench, ShieldCheck } from "lucide-react";
import { DeviceLifecycleEvent } from "@/lib/types/device";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type DeviceLifecycleTimelineProps = {
  events: DeviceLifecycleEvent[];
};

function getEventIcon(type: DeviceLifecycleEvent["type"]) {
  switch (type) {
    case "purchase":
      return ShoppingBag;
    case "diagnosis":
      return Sparkles;
    case "service":
      return Wrench;
    default:
      return ShieldCheck;
  }
}

export default function DeviceLifecycleTimeline({
  events,
}: DeviceLifecycleTimelineProps) {
  if (!events || events.length === 0) {
    return (
      <GlassCard className="p-6">
        <h2 className="text-lg font-bold text-white mb-2">Device Lifecycle</h2>
        <p className="text-xs text-white/40">
          No lifecycle events recorded for this device yet.
        </p>
      </GlassCard>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: 0.2, ease: EASE }}
    >
      <GlassCard className="p-6">
        <div className="flex items-center gap-2 mb-6 border-b border-white/10 pb-4">
          <GitCommit className="size-5 text-[#22C55E]" />
          <h2 className="text-lg font-bold text-white">Device Lifecycle Summary</h2>
        </div>

        <div className="relative pl-6 space-y-6 before:absolute before:left-2.5 before:top-2 before:bottom-2 before:w-0.5 before:bg-gradient-to-b before:from-[#22C55E] before:via-[#06B6D4] before:to-white/10">
          {events.map((evt, idx) => {
            const Icon = getEventIcon(evt.type);
            return (
              <motion.div
                key={evt.id || idx}
                initial={{ opacity: 0, x: -12 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.3, delay: idx * 0.08, ease: EASE }}
                className="relative group"
              >
                {/* Timeline Dot */}
                <div className="absolute -left-[31px] top-0.5 flex size-6 items-center justify-center rounded-full border border-[#22C55E]/40 bg-[#0B1120] text-[#22C55E] group-hover:scale-110 transition-transform">
                  <Icon className="size-3" />
                </div>

                <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 group-hover:border-white/20 group-hover:bg-white/[0.06] transition-all">
                  <div className="flex flex-wrap items-center justify-between gap-2 mb-1">
                    <h3 className="text-sm font-bold text-white">{evt.title}</h3>
                    <span className="text-[11px] font-semibold text-[#06B6D4] bg-[#06B6D4]/10 px-2 py-0.5 rounded-full border border-[#06B6D4]/20">
                      {evt.date}
                    </span>
                  </div>
                  <p className="text-xs text-white/60 leading-relaxed">
                    {evt.description}
                  </p>
                </div>
              </motion.div>
            );
          })}
        </div>
      </GlassCard>
    </motion.div>
  );
}
