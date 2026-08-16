"use client";

import React, { useEffect } from "react";
import Link from "next/link";
import { AlertTriangle, RefreshCw, Home, LayoutDashboard, ShieldAlert } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import Logo from "@/components/common/Logo";

interface ErrorProps {
  error: Error & { digest?: string };
  reset: () => void;
}

export default function RootError({ error, reset }: ErrorProps) {
  useEffect(() => {
    // In production, send to error monitoring (e.g. Sentry) without exposing secrets
    console.error("Application error boundary caught:", error.message);
  }, [error]);

  return (
    <div className="relative flex min-h-screen w-full flex-col justify-between overflow-hidden bg-[#0B1120] text-white">
      {/* Ambient background lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(239,68,68,0.12),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.1),transparent_50%)]"
        aria-hidden
      />

      {/* Top Header */}
      <header className="relative z-10 w-full border-b border-white/10 bg-[#0B1120]/60 backdrop-blur-xl">
        <Container className="flex h-18 items-center justify-between">
          <Logo size="sm" />
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-white/70 transition-colors hover:text-white"
          >
            <Home className="size-4" />
            Home
          </Link>
        </Container>
      </header>

      {/* Error Card */}
      <main className="relative z-10 flex flex-1 items-center justify-center p-4 sm:p-6">
        <Container size="sm" className="max-w-lg">
          <GlassCard padding="lg" glowColor="cyan" hoverEffect={false}>
            <div className="text-center">
              <div className="mx-auto mb-4 flex size-14 items-center justify-center rounded-2xl border border-red-500/30 bg-red-500/10 text-red-400">
                <AlertTriangle className="size-7" aria-hidden />
              </div>

              <span className="inline-block rounded-full border border-red-500/30 bg-red-500/10 px-3 py-1 text-[11px] font-semibold text-red-300 mb-2">
                Application Exception
              </span>

              <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
                Something went wrong
              </h1>

              <p className="mt-2 text-xs text-white/70 leading-relaxed sm:text-sm">
                An unexpected condition occurred while rendering this interface. Your session and device data remain secure.
              </p>

              {error?.message && (
                <div className="mt-4 rounded-xl border border-white/10 bg-black/40 p-3 text-left">
                  <p className="text-[11px] font-mono text-red-300 break-words line-clamp-2">
                    {error.message}
                  </p>
                </div>
              )}

              <div className="mt-6 flex flex-col sm:flex-row items-center justify-center gap-3">
                <GlassButton
                  onClick={() => reset()}
                  icon={<RefreshCw className="size-4" />}
                  fullWidth
                >
                  Try Again
                </GlassButton>

                <Link href="/dashboard" className="w-full sm:w-auto">
                  <GlassButton
                    variant="secondary"
                    icon={<LayoutDashboard className="size-4" />}
                    fullWidth
                  >
                    Dashboard
                  </GlassButton>
                </Link>
              </div>
            </div>
          </GlassCard>
        </Container>
      </main>

      {/* Footer */}
      <footer className="relative z-10 border-t border-white/10 py-4 text-center text-xs text-white/40">
        &copy; {new Date().getFullYear()} RepairVerse AI. Self-healing platform architecture.
      </footer>
    </div>
  );
}
