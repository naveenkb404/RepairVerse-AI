"use client";

import React from "react";
import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";

export default function RootLoading() {
  return (
    <div className="relative flex min-h-screen w-full flex-col items-center justify-center overflow-hidden bg-[#0B1120] text-white">
      {/* Ambient background lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(34,197,94,0.12),transparent_50%),radial-gradient(ellipse_at_bottom,rgba(6,182,212,0.12),transparent_50%)]"
        aria-hidden
      />

      <Container size="sm" className="relative z-10 flex flex-col items-center text-center">
        <div className="mb-8">
          <Logo size="md" />
        </div>

        {/* Pulsating multi-ring loader */}
        <div className="relative mb-6 flex size-16 items-center justify-center" aria-hidden>
          <div className="absolute inset-0 animate-ping rounded-full bg-emerald-500/20 duration-1000" />
          <div className="absolute inset-2 animate-pulse rounded-full bg-cyan-500/30 duration-700" />
          <div className="size-10 animate-spin rounded-full border-2 border-white/10 border-t-emerald-400 border-r-cyan-400" />
        </div>

        <h2 className="text-lg font-bold tracking-tight text-white sm:text-xl">
          Loading Repair Intelligence
        </h2>
        <p className="mt-2 text-xs text-white/60 max-w-xs leading-relaxed">
          Preparing device passports, AI diagnostics, and sustainability metrics...
        </p>
      </Container>
    </div>
  );
}
