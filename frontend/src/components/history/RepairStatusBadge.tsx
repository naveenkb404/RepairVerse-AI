"use client";

import { CheckCircle2, Clock, Loader2, XCircle } from "lucide-react";
import { RepairStatus } from "@/lib/types/repairHistory";
import { cn } from "@/lib/utils";

type RepairStatusBadgeProps = {
  status: RepairStatus;
  className?: string;
};

export default function RepairStatusBadge({
  status,
  className,
}: RepairStatusBadgeProps) {
  switch (status) {
    case "Completed":
      return (
        <span
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3 py-1 text-xs font-semibold text-[#22C55E] backdrop-blur-md",
            className
          )}
        >
          <CheckCircle2 className="size-3.5 text-[#22C55E]" /> Completed
        </span>
      );
    case "In Progress":
      return (
        <span
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-3 py-1 text-xs font-semibold text-[#06B6D4] backdrop-blur-md",
            className
          )}
        >
          <Loader2 className="size-3.5 animate-spin text-[#06B6D4]" /> In Progress
        </span>
      );
    case "Scheduled":
      return (
        <span
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border border-amber-500/30 bg-amber-500/10 px-3 py-1 text-xs font-semibold text-amber-400 backdrop-blur-md",
            className
          )}
        >
          <Clock className="size-3.5 text-amber-400" /> Scheduled
        </span>
      );
    case "Cancelled":
      return (
        <span
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border border-red-500/30 bg-red-500/10 px-3 py-1 text-xs font-semibold text-red-400 backdrop-blur-md",
            className
          )}
        >
          <XCircle className="size-3.5 text-red-400" /> Cancelled
        </span>
      );
    default:
      return (
        <span
          className={cn(
            "inline-flex items-center gap-1.5 rounded-full border border-white/20 bg-white/10 px-3 py-1 text-xs font-semibold text-white/70",
            className
          )}
        >
          {status}
        </span>
      );
  }
}
