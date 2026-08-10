"use client";

import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { X, QrCode, ShieldCheck, Copy, Check, ExternalLink } from "lucide-react";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

type DevicePassportQRModalProps = {
  isOpen: boolean;
  onClose: () => void;
  deviceId: string;
  deviceName: string;
};

// Generates a deterministic visual QR pattern matrix for a device ID string
function generateQRMatrix(seed: string): boolean[][] {
  const size = 21;
  const matrix: boolean[][] = Array.from({ length: size }, () =>
    Array(size).fill(false)
  );

  // Position detection patterns (top-left, top-right, bottom-left)
  const addFinder = (row: number, col: number) => {
    for (let r = 0; r < 7; r++) {
      for (let c = 0; c < 7; c++) {
        if (
          r === 0 ||
          r === 6 ||
          c === 0 ||
          c === 6 ||
          (r >= 2 && r <= 4 && c >= 2 && c <= 4)
        ) {
          matrix[row + r][col + c] = true;
        }
      }
    }
  };

  addFinder(0, 0);
  addFinder(0, 14);
  addFinder(14, 0);

  // Pseudo-random data filling using hash of seed
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash << 5) - hash + seed.charCodeAt(i);
    hash |= 0;
  }

  for (let r = 0; r < size; r++) {
    for (let c = 0; c < size; c++) {
      // Skip finder pattern zones
      if (
        (r < 8 && c < 8) ||
        (r < 8 && c >= 13) ||
        (r >= 13 && c < 8)
      ) {
        continue;
      }

      const bit = ((hash ^ (r * 31 + c * 17)) & 1) === 1;
      matrix[r][c] = bit;
    }
  }

  return matrix;
}

export default function DevicePassportQRModal({
  isOpen,
  onClose,
  deviceId,
  deviceName,
}: DevicePassportQRModalProps) {
  const [copied, setCopied] = useState(false);
  const [origin, setOrigin] = useState("https://repairverse.ai");

  useEffect(() => {
    if (typeof window !== "undefined") {
      setOrigin(window.location.origin);
    }
  }, []);

  const passportUrl = `${origin}/devices/${deviceId}`;
  const matrix = generateQRMatrix(deviceId);

  const copyUrl = () => {
    navigator.clipboard.writeText(passportUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="absolute inset-0 bg-black/80 backdrop-blur-md"
          />

          {/* Modal Card */}
          <motion.div
            initial={{ opacity: 0, scale: 0.95, y: 16 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.95, y: 16 }}
            transition={{ duration: 0.3, ease: EASE }}
            className="relative w-full max-w-md overflow-hidden rounded-3xl border border-white/15 bg-[#0d1526] p-6 shadow-2xl backdrop-blur-2xl"
          >
            {/* Header */}
            <div className="flex items-center justify-between border-b border-white/10 pb-4">
              <div className="flex items-center gap-2">
                <QrCode className="size-5 text-[#22C55E]" />
                <h3 className="text-base font-bold text-white">
                  Passport QR Code
                </h3>
              </div>
              <button
                type="button"
                onClick={onClose}
                className="rounded-full border border-white/10 bg-white/5 p-1.5 text-white/60 hover:text-white transition-colors"
                aria-label="Close dialog"
              >
                <X className="size-4" />
              </button>
            </div>

            {/* QR Content Body */}
            <div className="py-6 flex flex-col items-center text-center">
              <div className="mb-2 inline-flex items-center gap-1.5 text-xs text-[#06B6D4]">
                <ShieldCheck className="size-4" /> Public Device Health Passport
              </div>
              <h4 className="text-sm font-bold text-white mb-4">
                {deviceName}
              </h4>

              {/* QR Code SVG Matrix Frame */}
              <div className="relative flex size-56 items-center justify-center rounded-2xl border border-[#22C55E]/30 bg-white p-4 shadow-[0_0_32px_rgba(34,197,94,0.2)]">
                <svg viewBox="0 0 21 21" className="size-full">
                  {matrix.map((row, r) =>
                    row.map((filled, c) =>
                      filled ? (
                        <rect
                          key={`${r}-${c}`}
                          x={c}
                          y={r}
                          width="1"
                          height="1"
                          fill="#0d1526"
                          rx="0.1"
                        />
                      ) : null
                    )
                  )}
                </svg>
              </div>

              {/* URL Display */}
              <div className="mt-5 w-full rounded-2xl border border-white/10 bg-black/40 p-3 text-left">
                <span className="text-[10px] text-white/40 block mb-1">
                  Public Passport Link (No Tokens / No Private Data):
                </span>
                <div className="flex items-center justify-between text-xs font-mono text-[#22C55E] truncate">
                  <span className="truncate pr-2">{passportUrl}</span>
                  <button
                    type="button"
                    onClick={copyUrl}
                    className="shrink-0 text-white/60 hover:text-white transition-colors"
                    aria-label="Copy link"
                  >
                    {copied ? (
                      <Check className="size-4 text-[#22C55E]" />
                    ) : (
                      <Copy className="size-4" />
                    )}
                  </button>
                </div>
              </div>
            </div>

            {/* Action buttons */}
            <div className="flex items-center gap-3 border-t border-white/10 pt-4">
              <GlassButton
                variant="secondary"
                size="sm"
                fullWidth
                onClick={copyUrl}
                icon={copied ? <Check className="size-3.5" /> : <Copy className="size-3.5" />}
              >
                {copied ? "Copied!" : "Copy Link"}
              </GlassButton>
              <GlassButton
                href={passportUrl}
                target="_blank"
                size="sm"
                fullWidth
                icon={<ExternalLink className="size-3.5" />}
              >
                Open Link
              </GlassButton>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
