"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import {
  ArrowRight,
  DollarSign,
  Leaf,
  ScanLine,
  Sparkles,
  Smartphone,
} from "lucide-react";

import { cn } from "@/lib/utils";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

const FLOATING_ORBS = [
  { size: 320, top: "8%", left: "-8%", color: "rgba(34,197,94,0.18)", delay: 0 },
  { size: 240, top: "55%", left: "72%", color: "rgba(6,182,212,0.16)", delay: 1.2 },
  { size: 180, top: "18%", left: "58%", color: "rgba(34,197,94,0.12)", delay: 0.6 },
  { size: 140, top: "72%", left: "12%", color: "rgba(6,182,212,0.14)", delay: 1.8 },
  { size: 100, top: "38%", left: "88%", color: "rgba(34,197,94,0.1)", delay: 2.4 },
] as const;

const STATS = [
  { label: "Device Health Score", value: "87", suffix: "/100", icon: Smartphone, accent: "#22C55E" },
  { label: "Estimated Repair Cost", value: "$49", suffix: "", icon: DollarSign, accent: "#06B6D4" },
  { label: "Carbon Saved", value: "2.4", suffix: " kg", icon: Leaf, accent: "#22C55E" },
] as const;

type HeroProps = {
  className?: string;
};

function FloatingOrbs() {
  return (
    <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden>
      {FLOATING_ORBS.map((orb, i) => (
        <motion.div
          key={i}
          className="absolute rounded-full blur-3xl"
          style={{
            width: orb.size,
            height: orb.size,
            top: orb.top,
            left: orb.left,
            background: `radial-gradient(circle, ${orb.color} 0%, transparent 70%)`,
          }}
          animate={{
            y: [0, -24, 0],
            x: [0, 12, 0],
            scale: [1, 1.08, 1],
            opacity: [0.5, 0.85, 0.5],
          }}
          transition={{
            duration: 8 + i * 1.5,
            repeat: Infinity,
            ease: "easeInOut",
            delay: orb.delay,
          }}
        />
      ))}
    </div>
  );
}

function SmartphoneIllustration() {
  return (
    <div className="relative mx-auto w-[148px] sm:w-[168px]">
      <div className="relative overflow-hidden rounded-[28px] border border-white/20 bg-gradient-to-b from-[#111827] to-[#0B1120] p-2 shadow-[0_20px_60px_rgba(0,0,0,0.45),inset_0_1px_0_rgba(255,255,255,0.15)]">
        <div className="absolute left-1/2 top-2 z-10 h-1.5 w-10 -translate-x-1/2 rounded-full bg-white/20" />
        <div className="relative aspect-[9/19] overflow-hidden rounded-[22px] border border-white/10 bg-[#0B1120]">
          <div className="absolute inset-0 bg-gradient-to-br from-[#22C55E]/10 via-transparent to-[#06B6D4]/10" />

          <div className="absolute inset-x-3 top-8 space-y-2">
            <div className="h-2 w-3/4 rounded-full bg-white/15" />
            <div className="h-2 w-1/2 rounded-full bg-white/10" />
            <div className="mt-4 h-16 rounded-xl border border-white/10 bg-white/[0.04]" />
            <div className="h-10 rounded-xl border border-white/10 bg-white/[0.04]" />
          </div>

          <motion.div
            className="absolute inset-x-0 h-[2px] bg-gradient-to-r from-transparent via-[#22C55E] to-transparent shadow-[0_0_16px_rgba(34,197,94,0.8)]"
            animate={{ top: ["12%", "88%", "12%"] }}
            transition={{ duration: 3.5, repeat: Infinity, ease: "easeInOut" }}
          />

          <motion.div
            className="absolute inset-x-4 top-[30%] flex items-center gap-1.5 rounded-lg border border-[#22C55E]/30 bg-[#22C55E]/10 px-2 py-1.5 backdrop-blur-sm"
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: 1.2, duration: 0.5, ease: EASE }}
          >
            <ScanLine className="size-3 text-[#22C55E]" />
            <span className="text-[9px] font-semibold text-[#22C55E]">Scanning device…</span>
          </motion.div>
        </div>
      </div>

      <motion.div
        className="absolute -right-3 top-1/2 size-8 -translate-y-1/2 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/15 backdrop-blur-sm"
        animate={{ scale: [1, 1.2, 1], opacity: [0.6, 1, 0.6] }}
        transition={{ duration: 2, repeat: Infinity, ease: "easeInOut" }}
      />
      <motion.div
        className="absolute -left-2 top-8 size-5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/15"
        animate={{ scale: [1, 1.15, 1], opacity: [0.5, 0.9, 0.5] }}
        transition={{ duration: 2.5, repeat: Infinity, ease: "easeInOut", delay: 0.5 }}
      />
    </div>
  );
}

function DiagnosisCard() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 40, scale: 0.96 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ duration: 0.7, delay: 0.3, ease: EASE }}
      className="relative w-full max-w-md"
    >
      <motion.div
        className="pointer-events-none absolute -inset-4 rounded-[32px] bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/15 blur-2xl"
        animate={{ opacity: [0.4, 0.7, 0.4] }}
        transition={{ duration: 4, repeat: Infinity, ease: "easeInOut" }}
        aria-hidden
      />

      <div className="relative overflow-hidden rounded-3xl border border-white/15 bg-white/[0.06] p-6 shadow-[0_24px_64px_rgba(0,0,0,0.35),inset_0_1px_0_rgba(255,255,255,0.12)] backdrop-blur-xl sm:p-8">
        <div className="absolute inset-0 bg-gradient-to-br from-[#22C55E]/5 via-transparent to-[#06B6D4]/5" aria-hidden />

        <div className="relative mb-6 flex items-center justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#06B6D4]">
              AI Diagnosis
            </p>
            <p className="mt-1 text-sm font-medium text-white/70">Live analysis</p>
          </div>
          <motion.div
            animate={{ rotate: 360 }}
            transition={{ duration: 8, repeat: Infinity, ease: "linear" }}
            className="flex size-9 items-center justify-center rounded-full border border-white/15 bg-white/[0.08]"
          >
            <Sparkles className="size-4 text-[#22C55E]" />
          </motion.div>
        </div>

        <SmartphoneIllustration />

        <div className="relative mt-8 grid gap-3 sm:grid-cols-3 sm:gap-4">
          {STATS.map((stat, i) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.6 + i * 0.12, ease: EASE }}
              className="rounded-2xl border border-white/10 bg-white/[0.05] p-3 backdrop-blur-sm sm:p-4"
            >
              <div className="mb-2 flex items-center gap-1.5">
                <stat.icon className="size-3.5" style={{ color: stat.accent }} aria-hidden />
                <span className="text-[10px] font-medium leading-tight text-[#CBD5E1] sm:text-xs">
                  {stat.label}
                </span>
              </div>
              <p className="text-xl font-bold text-white sm:text-2xl">
                {stat.value}
                <span className="text-sm font-semibold text-[#CBD5E1]">{stat.suffix}</span>
              </p>
            </motion.div>
          ))}
        </div>

        <motion.div
          className="relative mt-5 flex items-center gap-2 rounded-xl border border-[#22C55E]/25 bg-[#22C55E]/10 px-3 py-2"
          animate={{ opacity: [0.7, 1, 0.7] }}
          transition={{ duration: 2.5, repeat: Infinity, ease: "easeInOut" }}
        >
          <span className="relative flex size-2">
            <span className="absolute inline-flex size-full animate-ping rounded-full bg-[#22C55E] opacity-60" />
            <span className="relative inline-flex size-2 rounded-full bg-[#22C55E]" />
          </span>
          <span className="text-xs font-medium text-[#22C55E]">
            Diagnosis complete — repair recommended
          </span>
        </motion.div>
      </div>
    </motion.div>
  );
}

export default function Hero({ className }: HeroProps) {
  return (
    <section
      id="home"
      className={cn(
        "relative flex min-h-screen w-full items-center overflow-hidden bg-[#0B1120]",
        className
      )}
    >
      <FloatingOrbs />

      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.08),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.08),transparent_50%)]"
        aria-hidden
      />

      <div className="pointer-events-none absolute inset-0 bg-white/[0.02] backdrop-blur-[2px]" aria-hidden />

      <div className="relative mx-auto grid w-full max-w-7xl grid-cols-1 items-center gap-12 px-4 py-24 sm:px-6 lg:grid-cols-2 lg:gap-16 lg:px-8 lg:py-0">
        <div className="flex flex-col items-center text-center lg:items-start lg:text-left">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, ease: EASE }}
            className="mb-6 inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/[0.06] px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.18em] text-[#22C55E] backdrop-blur-xl"
          >
            <Sparkles className="size-3.5" aria-hidden />
            AI Repair Intelligence
          </motion.div>

          <motion.h1
            initial={{ opacity: 0, y: 28 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.1, ease: EASE }}
            className="max-w-xl text-4xl font-bold leading-[1.08] tracking-tight text-white sm:text-5xl lg:text-6xl xl:text-7xl"
          >
            Repair Smarter.{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Waste Less.
            </span>
          </motion.h1>

          <motion.p
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.22, ease: EASE }}
            className="mt-6 max-w-lg text-base leading-relaxed text-[#CBD5E1] sm:text-lg lg:max-w-xl"
          >
            AI-powered device diagnosis, repair guidance, and carbon impact
            tracking—all in one platform.
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.34, ease: EASE }}
            className="mt-10 flex w-full flex-col gap-4 sm:w-auto sm:flex-row"
          >
            <GlassButton href="#ai-demo" size="lg" icon={<Sparkles className="size-[18px]" />}>
              Try AI Diagnosis
            </GlassButton>

            <GlassButton href="#features" variant="secondary" size="lg" icon={<ArrowRight className="size-[18px]" />} iconPosition="right">
              Explore Features
            </GlassButton>
          </motion.div>
        </div>

        <div className="flex justify-center lg:justify-end">
          <DiagnosisCard />
        </div>
      </div>
    </section>
  );
}
