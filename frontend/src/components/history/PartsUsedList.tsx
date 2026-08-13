"use client";

import { Wrench, Package } from "lucide-react";
import { RepairHistoryPart } from "@/lib/types/repairHistory";
import GlassCard from "@/components/glass/GlassCard";

type PartsUsedListProps = {
  parts: RepairHistoryPart[];
  partsCost: number;
};

export default function PartsUsedList({
  parts,
  partsCost,
}: PartsUsedListProps) {
  if (!parts || parts.length === 0) {
    return (
      <GlassCard className="p-6">
        <div className="flex items-center gap-2 mb-3 border-b border-white/10 pb-3">
          <Package className="size-5 text-[#22C55E]" />
          <h3 className="text-base font-bold text-white">Replaced Components</h3>
        </div>
        <p className="text-xs text-white/40">
          No hardware replacement parts recorded for this repair service.
        </p>
      </GlassCard>
    );
  }

  return (
    <GlassCard className="p-6">
      <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
        <div className="flex items-center gap-2">
          <Package className="size-5 text-[#22C55E]" />
          <h3 className="text-base font-bold text-white">
            Installed Parts & Components ({parts.length})
          </h3>
        </div>
        <span className="text-xs font-bold text-[#22C55E]">
          Total Parts: ${partsCost}
        </span>
      </div>

      <div className="space-y-3">
        {parts.map((part) => (
          <div
            key={part.id}
            className="flex items-center justify-between rounded-2xl border border-white/10 bg-white/[0.03] p-3.5 text-xs transition-colors hover:border-white/20 hover:bg-white/[0.06]"
          >
            <div className="min-w-0 pr-2">
              <div className="flex items-center gap-2">
                <Wrench className="size-3.5 shrink-0 text-[#06B6D4]" />
                <h4 className="font-bold text-white truncate">{part.name}</h4>
              </div>
              {part.partNumber && (
                <span className="text-[10px] font-mono text-white/40 block mt-0.5 ml-5">
                  Part #: {part.partNumber}
                </span>
              )}
            </div>

            <div className="flex items-center gap-4 text-right shrink-0">
              <span className="text-white/60">
                Qty: <strong className="text-white">{part.quantity}</strong>
              </span>
              <span className="font-bold text-white">
                ${part.cost * part.quantity}
              </span>
            </div>
          </div>
        ))}
      </div>
    </GlassCard>
  );
}
