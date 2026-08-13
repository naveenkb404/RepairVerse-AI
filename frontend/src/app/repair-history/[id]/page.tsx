"use client";

import { useState, useEffect, use } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  ArrowLeft,
  Loader2,
  Sparkles,
  MapPin,
  Leaf,
  Smartphone,
  ShieldCheck,
  AlertCircle,
  Wrench,
  UserCheck,
  Calendar,
  Clock,
  WifiOff,
  Wifi,
  Tag,
  Hash,
  Phone,
  Star,
  ChevronRight,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";

import RepairStatusBadge from "@/components/history/RepairStatusBadge";
import PartsUsedList from "@/components/history/PartsUsedList";
import RepairCostBreakdown from "@/components/history/RepairCostBreakdown";
import RepairImpactSummary from "@/components/history/RepairImpactSummary";
import RepairTimeline from "@/components/history/RepairTimeline";

import { fetchRepairHistoryById } from "@/lib/api/repairHistory";
import { RepairHistoryItem } from "@/lib/types/repairHistory";
import { getCategoryIcon } from "@/components/devices/DeviceCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type PageParams = {
  params: Promise<{ id: string }>;
};

export default function RepairHistoryDetailPage({ params }: PageParams) {
  const resolvedParams = use(params);
  const repairId = resolvedParams.id;

  const [item, setItem] = useState<RepairHistoryItem | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isDemo, setIsDemo] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    async function loadItem() {
      setIsLoading(true);
      setErrorMsg("");
      const res = await fetchRepairHistoryById(repairId);

      if (res.success && res.data) {
        setItem(res.data);
        setIsDemo(res.isDemo !== false);
      } else {
        setIsDemo(true);
        setErrorMsg(res.message || "Failed to load repair history record.");
      }
      setIsLoading(false);
    }

    if (repairId) {
      loadItem();
    }
  }, [repairId]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white flex flex-col items-center justify-center py-24 text-center">
        <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
        <p className="text-sm font-semibold text-white">
          Retrieving Repair History Log…
        </p>
        <p className="text-xs text-white/40 mt-1">
          Checking backend API at {process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1"}
        </p>
      </div>
    );
  }

  if (errorMsg || !item) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white flex flex-col items-center justify-center py-24 text-center px-4">
        <div className="rounded-3xl border border-red-500/30 bg-red-500/10 p-8 max-w-md backdrop-blur-xl">
          <AlertCircle className="size-12 text-red-400 mx-auto mb-4" />
          <h1 className="text-xl font-bold text-white mb-2">
            Record Not Found
          </h1>
          <p className="text-xs text-white/70 mb-6">
            {errorMsg || "The requested repair history log could not be located."}
          </p>
          <div className="flex flex-col gap-2">
            <GlassButton href="/repair-history" icon={<ArrowLeft className="size-4" />}>
              Back to Repair History
            </GlassButton>
            <p className="text-[10px] text-white/30 mt-2">
              Only sample repair IDs (rep_sample_101–104) are available in Demo Mode.
            </p>
          </div>
        </div>
      </div>
    );
  }

  const IconComponent = getCategoryIcon(item.device.category);

  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.10),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* Header */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <Logo size="sm" href="/" />
          <div className="flex items-center gap-2">
            {/* Demo/Live indicator */}
            <span
              className={`hidden sm:inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-[11px] font-semibold ${
                isDemo
                  ? "border-amber-500/30 bg-amber-500/10 text-amber-400"
                  : "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
              }`}
            >
              {isDemo ? (
                <><WifiOff className="size-3" /> Demo</>
              ) : (
                <><Wifi className="size-3" /> Live</>
              )}
            </span>
            <GlassButton
              href="/repair-history"
              variant="secondary"
              size="sm"
              icon={<ArrowLeft className="size-3.5" />}
            >
              Back to History
            </GlassButton>
            <GlassButton
              href={`/devices/${item.deviceId}`}
              size="sm"
              icon={<Smartphone className="size-3.5" />}
            >
              Device Passport
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* Main Content */}
      <main className="relative py-8 sm:py-12">
        <Container className="space-y-8">
          {/* ── Hero Banner Header ──────────────────────────────────────── */}
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.45, ease: EASE }}
            className="relative overflow-hidden rounded-3xl border border-white/15 bg-gradient-to-b from-white/[0.08] to-white/[0.03] p-6 sm:p-8 backdrop-blur-2xl shadow-[0_8px_32px_rgba(0,0,0,0.3)]"
          >
            <div className="flex flex-col gap-6 lg:flex-row lg:items-start lg:justify-between">
              {/* Title & Info */}
              <div className="space-y-3 flex-1 min-w-0">
                <div className="flex flex-wrap items-center gap-2">
                  <RepairStatusBadge status={item.status} />
                  {isDemo && (
                    <span className="inline-flex items-center gap-1 rounded-full border border-amber-500/30 bg-amber-500/10 px-2.5 py-1 text-[11px] font-semibold text-amber-400">
                      <WifiOff className="size-3" /> Sample Demo Record
                    </span>
                  )}
                  <span className="text-xs text-white/50 font-mono hidden sm:inline">
                    ID: {item.id}
                  </span>
                </div>

                <div className="flex items-center gap-3">
                  <div className="flex size-14 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-white shadow-[0_0_24px_rgba(34,197,94,0.3)]">
                    <IconComponent className="size-7" />
                  </div>
                  <div className="min-w-0">
                    <h1 className="text-2xl font-bold text-white sm:text-3xl truncate">
                      {item.repairType}
                    </h1>
                    <p className="text-xs text-[#CBD5E1] sm:text-sm">
                      {item.device.brand} {item.device.model} &bull;{" "}
                      <span className="text-[#22C55E] font-semibold">
                        {item.device.name}
                      </span>
                    </p>
                  </div>
                </div>

                <p className="text-xs text-white/70 max-w-2xl leading-relaxed">
                  {item.description}
                </p>
              </div>

              {/* Serviced Summary Pill */}
              <div className="flex flex-col sm:flex-row lg:flex-col gap-3 shrink-0 border-t border-white/10 pt-4 lg:border-t-0 lg:pt-0">
                <div className="rounded-2xl border border-white/10 bg-black/40 p-4 text-xs space-y-2 min-w-[180px]">
                  <div className="flex items-center justify-between gap-6 text-white/60">
                    <span>Service Date:</span>
                    <strong className="text-white flex items-center gap-1 font-mono">
                      <Calendar className="size-3 text-[#22C55E]" />
                      {item.repairDate}
                    </strong>
                  </div>
                  <div className="flex items-center justify-between gap-6 text-white/60">
                    <span>Duration:</span>
                    <strong className="text-white flex items-center gap-1">
                      <Clock className="size-3 text-[#06B6D4]" />
                      {item.repairDuration}
                    </strong>
                  </div>
                  <div className="flex items-center justify-between gap-6 text-white/60 pt-1.5 border-t border-white/10">
                    <span>Total Cost:</span>
                    <strong className="text-[#22C55E] text-sm font-extrabold">
                      ${item.totalCost}
                    </strong>
                  </div>
                  {item.moneySaved != null && item.moneySaved > 0 && (
                    <div className="flex items-center justify-between gap-6 text-[#22C55E]/80">
                      <span className="text-[10px]">vs. Replacement:</span>
                      <strong className="text-[#22C55E] text-xs">
                        Saved ${item.moneySaved}
                      </strong>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Quick Action Navigation Bar */}
            <div className="mt-8 flex flex-wrap items-center gap-3 pt-6 border-t border-white/[0.08]">
              <GlassButton
                href={`/devices/${item.deviceId}`}
                size="sm"
                icon={<Smartphone className="size-3.5" />}
              >
                View Device Passport
              </GlassButton>
              <GlassButton
                href="/diagnosis"
                variant="secondary"
                size="sm"
                icon={<Sparkles className="size-3.5" />}
              >
                Run AI Diagnosis
              </GlassButton>
              <GlassButton
                href="/repair-shops"
                variant="secondary"
                size="sm"
                icon={<MapPin className="size-3.5" />}
              >
                Find Repair Shops
              </GlassButton>
              <GlassButton
                href="/carbon"
                variant="secondary"
                size="sm"
                icon={<Leaf className="size-3.5" />}
              >
                View Carbon Impact
              </GlassButton>
            </div>
          </motion.div>

          {/* ── Main Grid ─────────────────────────────────────────────── */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
            {/* Left Column: Device, Diagnosis, Parts, & Cost */}
            <div className="lg:col-span-7 space-y-6">

              {/* Device Information Card */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.05, ease: EASE }}
              >
                <GlassCard className="p-6">
                  <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
                    <div className="flex items-center gap-2">
                      <Smartphone className="size-5 text-[#06B6D4]" />
                      <h2 className="text-base font-bold text-white">
                        Serviced Device
                      </h2>
                    </div>
                    <Link
                      href={`/devices/${item.deviceId}`}
                      className="inline-flex items-center gap-1 text-xs font-semibold text-[#22C55E] hover:text-[#4ade80] transition-colors"
                    >
                      View Passport <ChevronRight className="size-3.5" />
                    </Link>
                  </div>

                  <div className="grid grid-cols-2 gap-3 text-xs">
                    <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                      <span className="text-white/50 block mb-1 flex items-center gap-1">
                        <Tag className="size-3 text-[#22C55E]" /> Category
                      </span>
                      <strong className="text-white font-bold">
                        {item.device.category}
                      </strong>
                    </div>
                    <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                      <span className="text-white/50 block mb-1">Brand</span>
                      <strong className="text-white font-bold">
                        {item.device.brand}
                      </strong>
                    </div>
                    <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3 col-span-2">
                      <span className="text-white/50 block mb-1">Model</span>
                      <strong className="text-white font-bold">
                        {item.device.model}
                      </strong>
                    </div>
                    {item.device.serialNumber && (
                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3 col-span-2">
                        <span className="text-white/50 block mb-1 flex items-center gap-1">
                          <Hash className="size-3 text-[#06B6D4]" /> Serial Number
                        </span>
                        <strong className="text-white font-mono text-xs tracking-wider">
                          {item.device.serialNumber}
                        </strong>
                      </div>
                    )}
                  </div>
                </GlassCard>
              </motion.div>

              {/* Diagnosis & Technician Info */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.1, ease: EASE }}
              >
                <GlassCard className="p-6">
                  <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
                    <div className="flex items-center gap-2">
                      <Sparkles className="size-5 text-[#22C55E]" />
                      <h2 className="text-base font-bold text-white">
                        Diagnostic &amp; Technician Record
                      </h2>
                    </div>
                    {item.diagnosisConfidence && (
                      <span className="text-xs font-semibold text-[#22C55E] bg-[#22C55E]/10 px-2.5 py-0.5 rounded-full border border-[#22C55E]/20">
                        {item.diagnosisConfidence}% AI Confidence
                      </span>
                    )}
                  </div>

                  <div className="space-y-3 text-xs">
                    {item.diagnosisIssue && (
                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                        <span className="text-white/50 block mb-0.5">Diagnosed Issue:</span>
                        <strong className="text-white font-bold text-sm">
                          {item.diagnosisIssue}
                        </strong>
                      </div>
                    )}

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                      {item.technician && (
                        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-white/50 block mb-0.5 flex items-center gap-1">
                            <UserCheck className="size-3 text-[#22C55E]" /> Technician
                          </span>
                          <strong className="text-white font-bold">
                            {item.technician.name}
                          </strong>
                          {item.technician.isVerified && (
                            <span className="ml-1.5 text-[9px] font-bold text-[#22C55E] bg-[#22C55E]/10 px-1.5 py-0.5 rounded-full border border-[#22C55E]/20">
                              ✓ Verified
                            </span>
                          )}
                          {item.technician.role && (
                            <span className="text-[10px] text-white/40 block mt-0.5">
                              {item.technician.role}
                            </span>
                          )}
                          {item.technician.phone && (
                            <span className="text-[10px] text-white/40 flex items-center gap-1 mt-0.5">
                              <Phone className="size-2.5" /> {item.technician.phone}
                            </span>
                          )}
                        </div>
                      )}

                      {item.shop && (
                        <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                          <span className="text-white/50 block mb-0.5 flex items-center gap-1">
                            <MapPin className="size-3 text-[#06B6D4]" /> Repair Centre
                          </span>
                          <strong className="text-white font-bold">
                            {item.shop.name}
                          </strong>
                          {item.shop.rating && (
                            <span className="ml-1.5 text-[10px] text-amber-400 flex items-center gap-0.5 mt-0.5">
                              <Star className="size-2.5 fill-amber-400" /> {item.shop.rating}/5
                            </span>
                          )}
                          <span className="text-[10px] text-white/40 block truncate mt-0.5">
                            {item.shop.address}
                          </span>
                          {item.shop.phone && (
                            <span className="text-[10px] text-white/40 flex items-center gap-1 mt-0.5">
                              <Phone className="size-2.5" /> {item.shop.phone}
                            </span>
                          )}
                        </div>
                      )}
                    </div>

                    {item.notes && (
                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3 text-white/70 italic leading-relaxed">
                        &ldquo;{item.notes}&rdquo;
                      </div>
                    )}
                  </div>
                </GlassCard>
              </motion.div>

              {/* Parts Installed List */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.15, ease: EASE }}
              >
                <PartsUsedList parts={item.parts} partsCost={item.partsCost} />
              </motion.div>

              {/* Cost Breakdown */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.2, ease: EASE }}
              >
                <RepairCostBreakdown
                  partsCost={item.partsCost}
                  laborCost={item.laborCost}
                  totalCost={item.totalCost}
                  moneySaved={item.moneySaved}
                />
              </motion.div>
            </div>

            {/* Right Column: Warranty, Sustainability, & Timeline */}
            <div className="lg:col-span-5 space-y-6">

              {/* Repair Detail Summary Card */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.08, ease: EASE }}
              >
                <GlassCard className="p-6">
                  <div className="flex items-center gap-2 border-b border-white/10 pb-3 mb-4">
                    <Wrench className="size-5 text-[#06B6D4]" />
                    <h2 className="text-base font-bold text-white">
                      Repair Details
                    </h2>
                  </div>
                  <div className="space-y-2.5 text-xs">
                    <div className="flex justify-between items-center text-white/60">
                      <span>Repair Type:</span>
                      <strong className="text-white text-right max-w-[60%] truncate">
                        {item.repairType}
                      </strong>
                    </div>
                    <div className="flex justify-between items-center text-white/60">
                      <span className="flex items-center gap-1">
                        <Calendar className="size-3" /> Service Date:
                      </span>
                      <strong className="text-white font-mono">{item.repairDate}</strong>
                    </div>
                    <div className="flex justify-between items-center text-white/60">
                      <span className="flex items-center gap-1">
                        <Clock className="size-3" /> Duration:
                      </span>
                      <strong className="text-white">{item.repairDuration}</strong>
                    </div>
                    {item.technician && (
                      <div className="flex justify-between items-center text-white/60">
                        <span className="flex items-center gap-1">
                          <UserCheck className="size-3" /> Technician:
                        </span>
                        <strong className="text-white">{item.technician.name}</strong>
                      </div>
                    )}
                    {item.shop && (
                      <div className="flex justify-between items-start text-white/60 gap-4">
                        <span className="flex items-center gap-1 shrink-0">
                          <MapPin className="size-3" /> Shop:
                        </span>
                        <strong className="text-white text-right">{item.shop.name}</strong>
                      </div>
                    )}
                  </div>
                </GlassCard>
              </motion.div>

              {/* Warranty Coverage Card */}
              {item.warrantyPeriod && (
                <motion.div
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.4, delay: 0.12, ease: EASE }}
                >
                  <GlassCard className="p-6">
                    <div className="flex items-center gap-2 mb-4 border-b border-white/10 pb-3">
                      <ShieldCheck className="size-5 text-[#22C55E]" />
                      <h2 className="text-base font-bold text-white">
                        Repair Warranty Coverage
                      </h2>
                    </div>

                    <div className="space-y-3 text-xs">
                      <div className="flex justify-between items-center">
                        <span className="text-white/60">Warranty Period:</span>
                        <strong className="text-white font-bold">
                          {item.warrantyPeriod}
                        </strong>
                      </div>

                      {item.warrantyUntil && (
                        <div className="flex justify-between items-center">
                          <span className="text-white/60">Valid Until:</span>
                          <span className="font-semibold text-white font-mono">
                            {item.warrantyUntil}
                          </span>
                        </div>
                      )}

                      <div
                        className={`rounded-2xl border p-3 text-center text-xs font-semibold ${
                          item.isWarrantyActive
                            ? "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
                            : "border-white/10 bg-white/5 text-white/50"
                        }`}
                      >
                        {item.isWarrantyActive
                          ? "✓ Repair Warranty Currently Active"
                          : "Warranty Period Concluded"}
                      </div>
                    </div>
                  </GlassCard>
                </motion.div>
              )}

              {/* Environmental Impact Summary */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.16, ease: EASE }}
              >
                <RepairImpactSummary
                  co2SavedKg={item.co2SavedKg}
                  ewasteReducedKg={item.ewasteReducedKg}
                  moneySaved={item.moneySaved}
                />
              </motion.div>

              {/* Timeline */}
              <motion.div
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.4, delay: 0.2, ease: EASE }}
              >
                <RepairTimeline stages={item.timeline} />
              </motion.div>
            </div>
          </div>
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>
              &copy; {new Date().getFullYear()} RepairVerse AI. Smart Repair History Record.
              {isDemo && (
                <span className="ml-2 text-amber-400/60">[Sample/Demo Data]</span>
              )}
            </p>
            <Link
              href="/repair-history"
              className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors"
            >
              &larr; Back to Repair History
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
