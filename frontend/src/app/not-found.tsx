import Link from "next/link";
import { ArrowLeft, Compass, Home, LayoutDashboard, Wrench } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import Logo from "@/components/common/Logo";

export default function NotFound() {
  return (
    <div className="relative flex min-h-screen w-full flex-col justify-between overflow-hidden bg-[#0B1120] text-white">
      {/* Ambient background lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(34,197,94,0.1),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.1),transparent_50%)]"
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

      {/* 404 Content */}
      <main className="relative z-10 flex flex-1 items-center justify-center p-4 sm:p-6">
        <Container size="sm" className="max-w-lg">
          <GlassCard padding="lg" glowColor="cyan" hoverEffect={false}>
            <div className="text-center">
              <div className="mx-auto mb-4 flex size-16 items-center justify-center rounded-3xl border border-white/15 bg-gradient-to-br from-emerald-500/20 to-cyan-500/20 text-cyan-400">
                <Compass className="size-8" aria-hidden />
              </div>

              <span className="inline-block rounded-full border border-cyan-500/30 bg-cyan-500/10 px-3 py-1 text-xs font-bold text-cyan-300 mb-2">
                404 Error
              </span>

              <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
                Page Not Found
              </h1>

              <p className="mt-2 text-xs text-white/70 leading-relaxed sm:text-sm">
                The resource or repair intelligence path you are looking for does not exist or has been relocated.
              </p>

              <div className="mt-8 flex flex-col sm:flex-row items-center justify-center gap-3">
                <Link href="/" className="w-full sm:w-auto">
                  <GlassButton
                    icon={<ArrowLeft className="size-4" />}
                    fullWidth
                  >
                    Back to Home
                  </GlassButton>
                </Link>

                <Link href="/dashboard" className="w-full sm:w-auto">
                  <GlassButton
                    variant="secondary"
                    icon={<LayoutDashboard className="size-4" />}
                    fullWidth
                  >
                    User Dashboard
                  </GlassButton>
                </Link>
              </div>

              {/* Quick links */}
              <div className="mt-8 border-t border-white/10 pt-6">
                <p className="text-[11px] font-semibold uppercase tracking-wider text-white/50 mb-3">
                  Popular Destinations
                </p>
                <div className="flex flex-wrap items-center justify-center gap-2 text-xs">
                  <Link
                    href="/diagnosis"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-white/80 transition-colors hover:border-emerald-500/40 hover:text-emerald-400"
                  >
                    AI Visual Diagnosis
                  </Link>
                  <Link
                    href="/devices"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-white/80 transition-colors hover:border-emerald-500/40 hover:text-emerald-400"
                  >
                    Device Registry
                  </Link>
                  <Link
                    href="/carbon"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-white/80 transition-colors hover:border-emerald-500/40 hover:text-emerald-400"
                  >
                    Carbon Impact
                  </Link>
                  <Link
                    href="/repair-shops"
                    className="rounded-xl border border-white/10 bg-white/5 px-3 py-1.5 text-white/80 transition-colors hover:border-emerald-500/40 hover:text-emerald-400"
                  >
                    Nearby Shops
                  </Link>
                </div>
              </div>
            </div>
          </GlassCard>
        </Container>
      </main>

      {/* Footer */}
      <footer className="relative z-10 border-t border-white/10 py-4 text-center text-xs text-white/40">
        &copy; {new Date().getFullYear()} RepairVerse AI. All rights reserved.
      </footer>
    </div>
  );
}
