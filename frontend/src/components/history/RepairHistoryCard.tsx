"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import {
  Calendar,
  Clock,
  DollarSign,
  ChevronRight,
  Wrench,
  UserCheck,
  MapPin,
  Leaf,
  ShieldCheck,
} from "lucide-react";
import { RepairHistoryItem } from "@/lib/types/repairHistory";
import { getCategoryIcon } from "@/components/devices/DeviceCard";
import RepairStatusBadge from "./RepairStatusBadge";
import GlassButton from "@/components/common/GlassButton";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

type RepairHistoryCardProps = {
  item: RepairHistoryItem;
  isDemo?: boolean;
  delay?: number;
};

export default function RepairHistoryCard({
  item,
  isDemo = false,
  delay = 0,
}: RepairHistoryCardProps) {
  const IconComponent = getCategoryIcon(item.device.category);

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay, ease: EASE }}
      className={cn(
        "group relative flex flex-col justify-between rounded-3xl border border-white/10 bg-white/[0.04] p-6 backdrop-blur-xl transition-all duration-300",
        "hover:border-white/20 hover:bg-white/[0.07] hover:shadow-[0_8px_32px_rgba(0,0,0,0.25)]"
      )}
    >
      <div>
        {/* Top Header Row */}
        <div className="flex items-start justify-between gap-3 mb-4">
          <div className="flex items-center gap-3">
            <div className="flex size-11 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/20 border border-[#22C55E]/30 text-[#22C55E]">
              <IconComponent className="size-5" />
            </div>
            <div>
              <span className="text-[10px] font-semibold uppercase tracking-wider text-white/50">
                {item.device.brand} &bull; {item.device.category}
              </span>
              <h3 className="truncate text-base font-bold text-white group-hover:text-[#22C55E] transition-colors">
                {item.device.name}
              </h3>
            </div>
          </div>

          <div className="flex flex-col items-end gap-1 shrink-0">
            <RepairStatusBadge status={item.status} />
            {isDemo && (
              <span className="rounded-full border border-amber-500/30 bg-amber-500/10 px-2 py-0.5 text-[9px] font-bold text-amber-400">
                SAMPLE
              </span>
            )}
          </div>
        </div>

        {/* Repair Title & Description */}
        <div className="mb-4 space-y-1">
          <h4 className="text-sm font-bold text-white flex items-center gap-1.5">
            <Wrench className="size-3.5 text-[#06B6D4] shrink-0" />
            {item.repairType}
          </h4>
          <p className="text-xs text-[#CBD5E1] line-clamp-2 leading-relaxed">
            {item.description}
          </p>
        </div>

        {/* Technician & Shop Info */}
        <div className="mb-4 flex flex-wrap gap-2 text-xs">
          {item.technician && (
            <span className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.03] px-2.5 py-1 text-white/70">
              <UserCheck className="size-3 text-[#22C55E]" />
              {item.technician.name}
            </span>
          )}
          {item.shop && (
            <span className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.03] px-2.5 py-1 text-white/70">
              <MapPin className="size-3 text-[#06B6D4]" />
              {item.shop.name}
            </span>
          )}
        </div>
      </div>

      {/* Metric details & CTA footer */}
      <div>
        <div className="mb-5 grid grid-cols-3 gap-2 border-y border-white/[0.06] py-3 text-center text-xs">
          <div>
            <span className="text-[10px] text-white/40 block">Service Date</span>
            <span className="font-semibold text-white flex items-center justify-center gap-1 mt-0.5">
              <Calendar className="size-3 text-white/50" />
              {item.repairDate}
            </span>
          </div>

          <div>
            <span className="text-[10px] text-white/40 block">Total Cost</span>
            <span className="font-bold text-[#22C55E] flex items-center justify-center gap-0.5 mt-0.5">
              <DollarSign className="size-3" />
              {item.totalCost}
            </span>
          </div>

          <div>
            <span className="text-[10px] text-white/40 block">CO₂ Diverted</span>
            <span className="font-semibold text-[#06B6D4] flex items-center justify-center gap-1 mt-0.5">
              <Leaf className="size-3" />
              {item.co2SavedKg || 0} kg
            </span>
          </div>
        </div>

        <GlassButton
          href={`/repair-history/${item.id}`}
          variant="secondary"
          size="sm"
          fullWidth
          icon={<ChevronRight className="size-4" />}
          className="justify-center font-semibold"
        >
          View Repair Details
        </GlassButton>
      </div>
    </motion.div>
  );
}
