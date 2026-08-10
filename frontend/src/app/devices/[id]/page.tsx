"use client";

import { useState, useEffect, use } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  ArrowLeft,
  Loader2,
  Sparkles,
  Wrench,
  Leaf,
  MapPin,
  AlertCircle,
  CheckCircle2,
  DollarSign,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";

import DevicePassportHeader from "@/components/devices/DevicePassportHeader";
import HealthScoreBreakdown from "@/components/devices/HealthScoreBreakdown";
import DeviceInformationPanel from "@/components/devices/DeviceInformationPanel";
import DeviceLifecycleTimeline from "@/components/devices/DeviceLifecycleTimeline";
import DevicePassportQRModal from "@/components/devices/DevicePassportQRModal";

import { fetchDevicePassport } from "@/lib/api/devices";
import { DevicePassportData } from "@/lib/types/device";

const EASE = [0.22, 1, 0.36, 1] as const;

type PageParams = {
  params: Promise<{ id: string }>;
};

export default function DevicePassportDetailPage({ params }: PageParams) {
  const resolvedParams = use(params);
  const deviceId = resolvedParams.id;

  const [passportData, setPassportData] = useState<DevicePassportData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");
  const [isQRModalOpen, setIsQRModalOpen] = useState(false);

  useEffect(() => {
    async function loadPassport() {
      setIsLoading(true);
      setErrorMsg("");
      const res = await fetchDevicePassport(deviceId);

      if (res.success && res.data) {
        setPassportData(res.data);
      } else {
        setErrorMsg(res.message || "Failed to load device passport data.");
      }
      setIsLoading(false);
    }

    if (deviceId) {
      loadPassport();
    }
  }, [deviceId]);

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white flex flex-col items-center justify-center py-24 text-center">
        <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
        <p className="text-sm font-semibold text-white">
          Fetching Digital Health Passport…
        </p>
      </div>
    );
  }

  if (errorMsg || !passportData) {
    return (
      <div className="min-h-screen bg-[#0B1120] text-white flex flex-col items-center justify-center py-24 text-center px-4">
        <div className="rounded-3xl border border-red-500/30 bg-red-500/10 p-8 max-w-md backdrop-blur-xl">
          <AlertCircle className="size-12 text-red-400 mx-auto mb-4" />
          <h1 className="text-xl font-bold text-white mb-2">
            Passport Not Found
          </h1>
          <p className="text-xs text-white/70 mb-6">
            {errorMsg || "The requested device Health Passport could not be retrieved."}
          </p>
          <GlassButton href="/devices" icon={<ArrowLeft className="size-4" />}>
            Back to My Devices
          </GlassButton>
        </div>
      </div>
    );
  }

  const { device, health, diagnosisSummary, repairSummary, carbonSummary, lifecycleTimeline } =
    passportData;

  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.10),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* Navigation Header */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <Logo size="sm" href="/" />
          <div className="flex items-center gap-3">
            <GlassButton
              href="/devices"
              variant="secondary"
              size="sm"
              icon={<ArrowLeft className="size-3.5" />}
            >
              All Devices
            </GlassButton>
            <GlassButton
              href="/diagnosis"
              size="sm"
              icon={<Sparkles className="size-3.5" />}
            >
              AI Diagnosis
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* Main Content */}
      <main className="relative py-8 sm:py-12">
        <Container className="space-y-8">
          {/* Hero Passport Header */}
          <DevicePassportHeader
            device={device}
            health={health}
            isDemo={true}
            onOpenQR={() => setIsQRModalOpen(true)}
          />

          {/* Main Grid Section */}
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
            {/* Left Column: Health & Specifications */}
            <div className="lg:col-span-7 space-y-6">
              <HealthScoreBreakdown health={health} />
              <DeviceInformationPanel device={device} />

              {/* Diagnosis Summary Card */}
              {diagnosisSummary && (
                <motion.div
                  initial={{ opacity: 0, y: 16 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.4, delay: 0.2, ease: EASE }}
                >
                  <GlassCard className="p-6">
                    <div className="flex items-center justify-between border-b border-white/10 pb-4 mb-4">
                      <div className="flex items-center gap-2">
                        <Sparkles className="size-5 text-[#22C55E]" />
                        <h2 className="text-base font-bold text-white">
                          Latest AI Diagnosis Summary
                        </h2>
                      </div>
                      <span className="text-[10px] text-white/50">
                        {diagnosisSummary.lastDiagnosisDate}
                      </span>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs mb-4">
                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                        <span className="text-white/50 block mb-0.5">Probable Issue:</span>
                        <strong className="text-white font-bold text-sm">
                          {diagnosisSummary.probableIssue}
                        </strong>
                      </div>

                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                        <span className="text-white/50 block mb-0.5">AI Confidence:</span>
                        <strong className="text-[#22C55E] font-bold text-sm">
                          {diagnosisSummary.confidenceScore}%
                        </strong>
                      </div>

                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                        <span className="text-white/50 block mb-0.5">Repair Difficulty:</span>
                        <strong className="text-amber-400 font-bold text-sm">
                          {diagnosisSummary.repairDifficulty}
                        </strong>
                      </div>

                      <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-3">
                        <span className="text-white/50 block mb-0.5">Est. Repair Cost:</span>
                        <strong className="text-emerald-400 font-bold text-sm">
                          ${diagnosisSummary.repairCost}
                        </strong>
                      </div>
                    </div>

                    <GlassButton
                      href="/diagnosis"
                      variant="secondary"
                      size="sm"
                      icon={<Sparkles className="size-3.5" />}
                    >
                      Run Full AI Diagnosis
                    </GlassButton>
                  </GlassCard>
                </motion.div>
              )}
            </div>

            {/* Right Column: Summaries & Lifecycle Timeline */}
            <div className="lg:col-span-5 space-y-6">
              {/* Repair Summary Card */}
              {repairSummary && (
                <GlassCard className="p-6">
                  <div className="flex items-center gap-2 mb-4 border-b border-white/10 pb-3">
                    <Wrench className="size-5 text-[#06B6D4]" />
                    <h2 className="text-base font-bold text-white">
                      Repair & Maintenance Record
                    </h2>
                  </div>

                  <div className="space-y-3 text-xs mb-4">
                    <div className="flex justify-between items-center text-white/80">
                      <span className="text-white/50">Total Serviced Repairs:</span>
                      <span className="font-bold text-white text-sm bg-white/10 px-2.5 py-0.5 rounded-full">
                        {repairSummary.repairsCompleted}
                      </span>
                    </div>

                    {repairSummary.lastRepairDate && (
                      <div className="flex justify-between items-center text-white/80">
                        <span className="text-white/50">Last Serviced:</span>
                        <span className="font-semibold text-white">
                          {repairSummary.lastRepairDate}
                        </span>
                      </div>
                    )}

                    {repairSummary.lastRecommendedAction && (
                      <div className="rounded-2xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 p-3 text-[11px] text-[#06B6D4]">
                        <strong>Recommended Action:</strong>{" "}
                        {repairSummary.lastRecommendedAction}
                      </div>
                    )}
                  </div>

                  <GlassButton
                    href="/repair-shops"
                    variant="secondary"
                    size="sm"
                    fullWidth
                    icon={<MapPin className="size-3.5" />}
                  >
                    Find Nearby Repair Shops
                  </GlassButton>
                </GlassCard>
              )}

              {/* Sustainability & Carbon Summary */}
              {carbonSummary && (
                <GlassCard className="p-6">
                  <div className="flex items-center gap-2 mb-4 border-b border-white/10 pb-3">
                    <Leaf className="size-5 text-[#22C55E]" />
                    <h2 className="text-base font-bold text-white">
                      Sustainability Impact
                    </h2>
                  </div>

                  <div className="grid grid-cols-2 gap-3 text-xs mb-4">
                    <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-3">
                      <span className="text-white/60 block text-[10px]">CO₂ Avoided</span>
                      <strong className="text-[#22C55E] text-base font-extrabold">
                        {carbonSummary.co2SavedKg} kg
                      </strong>
                    </div>

                    <div className="rounded-2xl border border-[#06B6D4]/30 bg-[#06B6D4]/10 p-3">
                      <span className="text-white/60 block text-[10px]">Money Saved</span>
                      <strong className="text-[#06B6D4] text-base font-extrabold">
                        ${carbonSummary.moneySaved}
                      </strong>
                    </div>
                  </div>

                  <GlassButton
                    href="/carbon"
                    variant="secondary"
                    size="sm"
                    fullWidth
                    icon={<Leaf className="size-3.5" />}
                  >
                    View Full Carbon Dashboard
                  </GlassButton>
                </GlassCard>
              )}

              {/* Lifecycle Timeline */}
              <DeviceLifecycleTimeline events={lifecycleTimeline} />
            </div>
          </div>
        </Container>
      </main>

      {/* QR Code Modal Dialog */}
      <DevicePassportQRModal
        isOpen={isQRModalOpen}
        onClose={() => setIsQRModalOpen(false)}
        deviceId={device.id}
        deviceName={device.deviceName}
      />

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>
              &copy; {new Date().getFullYear()} RepairVerse AI. Digital Device Passport.
            </p>
            <Link
              href="/devices"
              className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors"
            >
              &larr; Back to Devices Registry
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
