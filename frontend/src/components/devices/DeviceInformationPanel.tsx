"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import {
  Info,
  Tag,
  Calendar,
  ShieldCheck,
  DollarSign,
  Hash,
  Eye,
  EyeOff,
  Clock,
} from "lucide-react";
import { Device } from "@/lib/types/device";
import { maskIdentifier } from "./DeviceCard";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

type DeviceInformationPanelProps = {
  device: Device;
};

export function calculateDeviceAge(purchaseDateStr?: string): string {
  if (!purchaseDateStr) return "Unknown";
  const purchase = new Date(purchaseDateStr);
  const now = new Date();
  if (isNaN(purchase.getTime())) return "Unknown";

  const diffMonths =
    (now.getFullYear() - purchase.getFullYear()) * 12 +
    (now.getMonth() - purchase.getMonth());

  if (diffMonths < 1) return "Less than 1 month";
  if (diffMonths < 12) return `${diffMonths} month${diffMonths > 1 ? "s" : ""}`;

  const years = Math.floor(diffMonths / 12);
  const remMonths = diffMonths % 12;
  if (remMonths === 0) return `${years} year${years > 1 ? "s" : ""}`;
  return `${years} yr ${remMonths} mo`;
}

export function getWarrantyStatus(warrantyDateStr?: string): {
  label: string;
  isExpired: boolean;
} {
  if (!warrantyDateStr) return { label: "Not Specified", isExpired: true };
  const expiry = new Date(warrantyDateStr);
  if (isNaN(expiry.getTime())) return { label: "Not Specified", isExpired: true };

  const isExpired = new Date() > expiry;
  return {
    label: isExpired ? `Expired (${warrantyDateStr})` : `Active until ${warrantyDateStr}`,
    isExpired,
  };
}

export default function DeviceInformationPanel({
  device,
}: DeviceInformationPanelProps) {
  const [showSerial, setShowSerial] = useState(false);
  const ageStr = calculateDeviceAge(device.purchaseDate);
  const warranty = getWarrantyStatus(device.warrantyExpiry);

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: 0.15, ease: EASE }}
    >
      <GlassCard className="p-6">
        <div className="flex items-center gap-2 mb-6 border-b border-white/10 pb-4">
          <Info className="size-5 text-[#06B6D4]" />
          <h2 className="text-lg font-bold text-white">Device Specifications</h2>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          {/* Brand & Model */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
              <Tag className="size-3.5 text-[#22C55E]" /> Brand & Model
            </span>
            <p className="text-sm font-bold text-white">
              {device.brand} {device.model}
            </p>
          </div>

          {/* Category */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
              <Tag className="size-3.5 text-[#06B6D4]" /> Category
            </span>
            <p className="text-sm font-bold text-white">{device.category}</p>
          </div>

          {/* Device Age */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
              <Clock className="size-3.5 text-amber-400" /> Device Age
            </span>
            <p className="text-sm font-bold text-white">{ageStr}</p>
            {device.purchaseDate && (
              <span className="text-[10px] text-white/40">
                Purchased {device.purchaseDate}
              </span>
            )}
          </div>

          {/* Warranty Status */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
              <ShieldCheck className="size-3.5 text-[#22C55E]" /> Warranty Coverage
            </span>
            <p className="text-sm font-bold text-white">{warranty.label}</p>
          </div>

          {/* Purchase Price */}
          {device.purchasePrice != null && (
            <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
              <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
                <DollarSign className="size-3.5 text-emerald-400" /> Original Value
              </span>
              <p className="text-sm font-bold text-white">
                ${device.purchasePrice.toLocaleString()}
              </p>
            </div>
          )}

          {/* Serial Number & Device ID */}
          <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4">
            <span className="flex items-center gap-1.5 text-xs text-white/50 mb-1">
              <Hash className="size-3.5 text-indigo-400" /> Serial / Identity Code
            </span>
            <div className="flex items-center justify-between font-mono text-xs text-white">
              <span>
                {showSerial
                  ? device.serialNumber || device.id
                  : maskIdentifier(device.serialNumber || device.id)}
              </span>
              <button
                type="button"
                onClick={() => setShowSerial(!showSerial)}
                className="text-white/40 hover:text-white transition-colors"
                aria-label={showSerial ? "Hide serial" : "Reveal serial"}
              >
                {showSerial ? <EyeOff className="size-3.5" /> : <Eye className="size-3.5" />}
              </button>
            </div>
          </div>
        </div>
      </GlassCard>
    </motion.div>
  );
}
