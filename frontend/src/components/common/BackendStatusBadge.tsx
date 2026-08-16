"use client";

import React from "react";
import { Wifi, WifiOff } from "lucide-react";

interface BackendStatusBadgeProps {
  isOnline?: boolean;
  className?: string;
}

export default function BackendStatusBadge({
  isOnline = false,
  className = "",
}: BackendStatusBadgeProps) {
  if (isOnline) {
    return (
      <div
        className={`inline-flex items-center gap-1.5 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2.5 py-1 text-xs font-semibold text-emerald-400 backdrop-blur-md ${className}`}
      >
        <span className="relative flex size-2">
          <span className="absolute inline-flex size-full animate-ping rounded-full bg-emerald-400 opacity-75" />
          <span className="relative inline-flex size-2 rounded-full bg-emerald-500" />
        </span>
        <Wifi className="size-3.5" aria-hidden />
        <span>Live Backend</span>
      </div>
    );
  }

  return (
    <div
      className={`inline-flex items-center gap-1.5 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-2.5 py-1 text-xs font-semibold text-cyan-300 backdrop-blur-md ${className}`}
    >
      <WifiOff className="size-3.5" aria-hidden />
      <span>Demo Mode</span>
    </div>
  );
}
