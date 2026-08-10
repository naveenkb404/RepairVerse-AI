"use client";

import { useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Smartphone,
  Laptop,
  Gamepad2,
  Tablet,
  Watch,
  Headphones,
  HardDrive,
  Eye,
  EyeOff,
  ChevronRight,
  ShieldCheck,
  Activity,
  Calendar,
  Tag,
} from "lucide-react";
import { Device } from "@/lib/types/device";
import { cn } from "@/lib/utils";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

type DeviceCardProps = {
  device: Device;
  healthScore?: number;
  isDemo?: boolean;
  delay?: number;
};

export function getCategoryIcon(category: string) {
  const cat = category.toLowerCase();
  if (cat.includes("phone")) return Smartphone;
  if (cat.includes("laptop") || cat.includes("macbook") || cat.includes("pc"))
    return Laptop;
  if (cat.includes("console") || cat.includes("playstation") || cat.includes("xbox"))
    return Gamepad2;
  if (cat.includes("tablet") || cat.includes("ipad")) return Tablet;
  if (cat.includes("watch")) return Watch;
  if (cat.includes("audio") || cat.includes("headphone")) return Headphones;
  return HardDrive;
}

export function getConditionBadgeColor(condition: string) {
  const cond = condition.toLowerCase();
  if (cond.includes("excellent"))
    return "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]";
  if (cond.includes("good"))
    return "border-[#06B6D4]/30 bg-[#06B6D4]/10 text-[#06B6D4]";
  if (cond.includes("fair"))
    return "border-amber-500/30 bg-amber-500/10 text-amber-400";
  return "border-red-500/30 bg-red-500/10 text-red-400";
}

export function maskIdentifier(identifier?: string): string {
  if (!identifier) return "••••••••";
  if (identifier.length <= 4) return "••••" + identifier;
  return "••••••••" + identifier.slice(-4);
}

export default function DeviceCard({
  device,
  healthScore = 85,
  isDemo = false,
  delay = 0,
}: DeviceCardProps) {
  const [showSerial, setShowSerial] = useState(false);
  const IconComponent = getCategoryIcon(device.category);

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay, ease: EASE }}
      className={cn(
        "group relative flex flex-col justify-between rounded-3xl border border-white/10 bg-white/[0.04] p-6 backdrop-blur-xl transition-all duration-300",
        "hover:border-white/20 hover:bg-white/[0.07] hover:shadow-[0_8px_32px_rgba(0,0,0,0.25)]"
      )}
    >
      {/* Top row: Category & Demo Tag */}
      <div>
        <div className="flex items-center justify-between gap-2 mb-4">
          <div className="flex items-center gap-2">
            <div className="flex size-10 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/20 border border-[#22C55E]/30 text-[#22C55E]">
              <IconComponent className="size-5" />
            </div>
            <div>
              <span className="text-[11px] font-semibold uppercase tracking-wider text-white/50">
                {device.category}
              </span>
              <h3 className="truncate text-base font-bold text-white group-hover:text-[#22C55E] transition-colors">
                {device.deviceName}
              </h3>
            </div>
          </div>

          {isDemo && (
            <span className="rounded-full border border-amber-500/30 bg-amber-500/10 px-2 py-0.5 text-[9px] font-bold text-amber-400">
              SAMPLE
            </span>
          )}
        </div>

        {/* Device Brand & Model Specs */}
        <div className="mb-5 space-y-1.5 rounded-2xl border border-white/[0.06] bg-black/20 p-3 text-xs">
          <div className="flex justify-between text-white/70">
            <span className="flex items-center gap-1 text-white/40">
              <Tag className="size-3" /> Brand/Model:
            </span>
            <span className="font-semibold text-white">
              {device.brand} {device.model}
            </span>
          </div>

          {device.serialNumber && (
            <div className="flex items-center justify-between text-white/70">
              <span className="flex items-center gap-1 text-white/40">
                <ShieldCheck className="size-3" /> Serial No:
              </span>
              <div className="flex items-center gap-1.5 font-mono text-[11px]">
                <span>
                  {showSerial ? device.serialNumber : maskIdentifier(device.serialNumber)}
                </span>
                <button
                  type="button"
                  onClick={(e) => {
                    e.preventDefault();
                    setShowSerial(!showSerial);
                  }}
                  className="text-white/40 hover:text-white transition-colors"
                  aria-label={showSerial ? "Hide serial number" : "Reveal serial number"}
                >
                  {showSerial ? <EyeOff className="size-3" /> : <Eye className="size-3" />}
                </button>
              </div>
            </div>
          )}

          {device.purchaseDate && (
            <div className="flex justify-between text-white/70">
              <span className="flex items-center gap-1 text-white/40">
                <Calendar className="size-3" /> Purchased:
              </span>
              <span className="text-white/80">{device.purchaseDate}</span>
            </div>
          )}
        </div>
      </div>

      {/* Middle metric & condition row */}
      <div className="mb-6 flex items-center justify-between gap-3 border-t border-white/[0.06] pt-4">
        {/* Health Score Pill */}
        <div className="flex items-center gap-2">
          <Activity className="size-4 text-[#22C55E]" />
          <div>
            <div className="text-[10px] text-white/50 uppercase tracking-wider">
              Health Score
            </div>
            <div className="text-sm font-bold text-white">
              <span className="text-[#22C55E]">{healthScore}</span>
              <span className="text-white/40 text-xs">/100</span>
            </div>
          </div>
        </div>

        {/* Condition Badge */}
        <span
          className={cn(
            "rounded-full border px-3 py-1 text-xs font-semibold backdrop-blur-md",
            getConditionBadgeColor(device.currentCondition)
          )}
        >
          {device.currentCondition}
        </span>
      </div>

      {/* CTA Button */}
      <GlassButton
        href={`/devices/${device.id}`}
        variant="secondary"
        size="sm"
        fullWidth
        icon={<ChevronRight className="size-4" />}
        className="justify-center font-semibold"
      >
        View Health Passport
      </GlassButton>
    </motion.div>
  );
}
