"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import {
  ShieldCheck,
  Eye,
  EyeOff,
  QrCode,
  Sparkles,
  MapPin,
  Leaf,
  Wifi,
  WifiOff,
  Activity,
} from "lucide-react";
import { Device, DeviceHealth } from "@/lib/types/device";
import {
  getCategoryIcon,
  getConditionBadgeColor,
  maskIdentifier,
} from "./DeviceCard";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

type DevicePassportHeaderProps = {
  device: Device;
  health: DeviceHealth;
  isDemo?: boolean;
  onOpenQR: () => void;
};

export default function DevicePassportHeader({
  device,
  health,
  isDemo = false,
  onOpenQR,
}: DevicePassportHeaderProps) {
  const [showSerial, setShowSerial] = useState(false);
  const IconComponent = getCategoryIcon(device.category);

  // Health level calculation
  const score = health.healthScore ?? 85;
  const levelText =
    score >= 90
      ? "Excellent Condition"
      : score >= 80
      ? "Good Condition"
      : score >= 65
      ? "Fair Condition"
      : "Needs Attention";

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45, ease: EASE }}
      className="relative overflow-hidden rounded-3xl border border-white/15 bg-gradient-to-b from-white/[0.08] to-white/[0.03] p-6 sm:p-8 backdrop-blur-2xl shadow-[0_8px_32px_rgba(0,0,0,0.3)]"
    >
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute -right-24 -top-24 size-96 rounded-full bg-gradient-to-br from-[#22C55E]/15 to-[#06B6D4]/15 blur-3xl"
        aria-hidden
      />

      <div className="relative flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
        {/* Left Side: Device Title & Identity */}
        <div className="space-y-3">
          <div className="flex flex-wrap items-center gap-2">
            <span className="inline-flex items-center gap-1.5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3 py-1 text-xs font-semibold text-[#22C55E]">
              <ShieldCheck className="size-3.5" /> Digital Passport Verified
            </span>

            {isDemo ? (
              <span className="inline-flex items-center gap-1 rounded-full border border-amber-500/30 bg-amber-500/10 px-2.5 py-1 text-[11px] font-semibold text-amber-400">
                <WifiOff className="size-3" /> Demo Mode
              </span>
            ) : (
              <span className="inline-flex items-center gap-1 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-2.5 py-1 text-[11px] font-semibold text-[#06B6D4]">
                <Wifi className="size-3" /> Live Passport
              </span>
            )}
          </div>

          <div className="flex items-center gap-3">
            <div className="flex size-14 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-white shadow-[0_0_24px_rgba(34,197,94,0.3)]">
              <IconComponent className="size-7" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
                {device.deviceName}
              </h1>
              <p className="text-xs text-[#CBD5E1] sm:text-sm">
                {device.brand} &bull; {device.model} &bull;{" "}
                <span className="text-white/60">{device.category}</span>
              </p>
            </div>
          </div>

          {/* Masked Serial / ID Bar */}
          <div className="flex flex-wrap items-center gap-3 text-xs text-white/60 pt-1">
            <span>
              Device Passport ID:{" "}
              <code className="rounded bg-black/40 px-2 py-0.5 font-mono text-white/80">
                {device.id}
              </code>
            </span>

            {device.serialNumber && (
              <span className="flex items-center gap-1.5 border-l border-white/10 pl-3">
                Serial No:{" "}
                <code className="font-mono text-white">
                  {showSerial ? device.serialNumber : maskIdentifier(device.serialNumber)}
                </code>
                <button
                  type="button"
                  onClick={() => setShowSerial(!showSerial)}
                  className="text-white/40 hover:text-white transition-colors"
                  aria-label={showSerial ? "Hide serial number" : "Reveal serial number"}
                >
                  {showSerial ? <EyeOff className="size-3.5" /> : <Eye className="size-3.5" />}
                </button>
              </span>
            )}
          </div>
        </div>

        {/* Right Side: Health Score Circular Gauge & Actions */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-6 shrink-0 border-t border-white/10 pt-6 lg:border-t-0 lg:pt-0">
          {/* Health Score Gauge */}
          <div className="flex items-center gap-4 rounded-2xl border border-white/10 bg-black/30 p-4 backdrop-blur-xl">
            <div className="relative flex size-20 items-center justify-center">
              {/* Circular SVG Progress Meter */}
              <svg className="size-full -rotate-90" viewBox="0 0 36 36">
                <path
                  className="text-white/10"
                  strokeWidth="3.5"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
                <path
                  className="text-[#22C55E] transition-all duration-1000"
                  strokeDasharray={`${score}, 100`}
                  strokeWidth="3.5"
                  strokeLinecap="round"
                  stroke="currentColor"
                  fill="none"
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                />
              </svg>
              <div className="absolute flex flex-col items-center justify-center text-center">
                <span className="text-xl font-extrabold text-white">{score}</span>
                <span className="text-[9px] uppercase tracking-wider text-white/50">/100</span>
              </div>
            </div>

            <div>
              <div className="text-xs font-semibold text-white/50 flex items-center gap-1 mb-1">
                <Activity className="size-3 text-[#22C55E]" /> AI Health Rating
              </div>
              <div className="text-sm font-bold text-white mb-1.5">{levelText}</div>
              <span
                className={`inline-block rounded-full border px-2.5 py-0.5 text-[10px] font-semibold ${getConditionBadgeColor(
                  device.currentCondition
                )}`}
              >
                Condition: {device.currentCondition}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions Navigation Bar */}
      <div className="mt-8 flex flex-wrap items-center gap-3 pt-6 border-t border-white/[0.08]">
        <GlassButton href="/diagnosis" size="sm" icon={<Sparkles className="size-3.5" />}>
          Run AI Diagnosis
        </GlassButton>
        <GlassButton href="/repair-shops" variant="secondary" size="sm" icon={<MapPin className="size-3.5" />}>
          Find Repair Shops
        </GlassButton>
        <GlassButton href="/carbon" variant="secondary" size="sm" icon={<Leaf className="size-3.5" />}>
          View Carbon Impact
        </GlassButton>
        <GlassButton
          variant="outline"
          size="sm"
          icon={<QrCode className="size-3.5" />}
          onClick={onOpenQR}
          className="ml-auto"
        >
          Share QR Passport
        </GlassButton>
      </div>
    </motion.div>
  );
}
