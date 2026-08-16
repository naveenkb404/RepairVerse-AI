"use client";

import React, { useState } from "react";
import { WifiOff, X, ExternalLink } from "lucide-react";
import { API_BASE_URL } from "@/lib/config";

interface DemoBannerProps {
  featureName?: string;
  className?: string;
  compact?: boolean;
}

export default function DemoBanner({
  featureName = "Live API",
  className = "",
  compact = false,
}: DemoBannerProps) {
  const [dismissed, setDismissed] = useState(false);

  if (dismissed) return null;

  if (compact) {
    return (
      <div
        className={`inline-flex items-center gap-1.5 rounded-full border border-cyan-500/30 bg-cyan-500/10 px-2.5 py-0.5 text-[11px] font-medium text-cyan-400 backdrop-blur-md ${className}`}
      >
        <WifiOff className="size-3 shrink-0" aria-hidden />
        <span>Demo / Reference Data</span>
      </div>
    );
  }

  return (
    <div
      role="status"
      aria-live="polite"
      className={`relative overflow-hidden rounded-2xl border border-cyan-500/30 bg-gradient-to-r from-cyan-500/10 via-[#0B1120]/80 to-cyan-500/5 p-3.5 sm:p-4 text-xs text-cyan-200 backdrop-blur-xl ${className}`}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-2.5">
          <div className="mt-0.5 flex size-6 shrink-0 items-center justify-center rounded-lg bg-cyan-500/20 text-cyan-400">
            <WifiOff className="size-3.5" aria-hidden />
          </div>
          <div>
            <div className="flex flex-wrap items-center gap-2">
              <span className="font-semibold text-white">Demo Mode Active</span>
              <span className="rounded-md border border-cyan-500/40 bg-cyan-500/20 px-1.5 py-0.5 text-[10px] font-bold uppercase tracking-wider text-cyan-300">
                Sample Data
              </span>
            </div>
            <p className="mt-1 leading-relaxed text-white/70">
              The {featureName} backend service is running in client demo mode. Connect your Spring Boot API server at{" "}
              <code className="rounded bg-black/40 px-1 py-0.5 font-mono text-[11px] text-cyan-300">
                {API_BASE_URL}
              </code>{" "}
              to enable live synchronization.
            </p>
          </div>
        </div>

        <button
          type="button"
          onClick={() => setDismissed(true)}
          className="shrink-0 rounded-lg p-1 text-white/40 transition-colors hover:bg-white/10 hover:text-white focus:outline-none focus-visible:ring-2 focus-visible:ring-cyan-400"
          aria-label="Dismiss demo mode banner"
        >
          <X className="size-4" />
        </button>
      </div>
    </div>
  );
}
