"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import { CarbonTrendPoint } from "@/lib/api/carbon";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

type ViewMode = "co2" | "money";
type TimeRange = "weekly" | "monthly" | "yearly";

type CarbonTrendChartProps = {
  data: CarbonTrendPoint[];
};

/**
 * Pure CSS/SVG trend chart — no charting library required.
 * Uses SVG polyline so no dependencies need to be installed.
 */
export default function CarbonTrendChart({ data }: CarbonTrendChartProps) {
  const [viewMode, setViewMode] = useState<ViewMode>("co2");

  if (!data || data.length === 0) {
    return (
      <div className="flex h-48 items-center justify-center text-sm text-white/50">
        No trend data available yet.
      </div>
    );
  }

  const values = data.map((d) =>
    viewMode === "co2" ? d.co2Saved : d.moneySaved
  );
  const maxVal = Math.max(...values, 1);
  const minVal = Math.min(...values, 0);
  const range = maxVal - minVal || 1;

  const W = 600;
  const H = 180;
  const PADDING = { top: 16, right: 24, bottom: 32, left: 48 };
  const chartW = W - PADDING.left - PADDING.right;
  const chartH = H - PADDING.top - PADDING.bottom;

  const toX = (i: number) =>
    PADDING.left + (i / (data.length - 1)) * chartW;
  const toY = (v: number) =>
    PADDING.top + chartH - ((v - minVal) / range) * chartH;

  const pointsStr = values
    .map((v, i) => `${toX(i)},${toY(v)}`)
    .join(" ");

  // Area fill path
  const areaPath =
    `M ${toX(0)},${toY(values[0])} ` +
    values.map((v, i) => `L ${toX(i)},${toY(v)}`).join(" ") +
    ` L ${toX(data.length - 1)},${H - PADDING.bottom} L ${toX(0)},${H - PADDING.bottom} Z`;

  const unit = viewMode === "co2" ? " kg" : " $";
  const label = viewMode === "co2" ? "CO₂ Saved (kg)" : "Money Saved ($)";

  return (
    <div className="space-y-4">
      {/* Controls */}
      <div className="flex flex-wrap items-center justify-between gap-3">
        <p className="text-xs font-semibold uppercase tracking-wider text-[#CBD5E1]">
          {label}
        </p>
        <div className="flex rounded-full border border-white/10 bg-white/[0.04] p-1">
          {(["co2", "money"] as ViewMode[]).map((mode) => (
            <button
              key={mode}
              type="button"
              onClick={() => setViewMode(mode)}
              className={cn(
                "rounded-full px-4 py-1 text-xs font-semibold transition-all",
                viewMode === mode
                  ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-sm"
                  : "text-white/60 hover:text-white"
              )}
            >
              {mode === "co2" ? "CO₂" : "Savings"}
            </button>
          ))}
        </div>
      </div>

      {/* SVG Chart */}
      <motion.div
        initial={{ opacity: 0, scaleY: 0.9 }}
        animate={{ opacity: 1, scaleY: 1 }}
        transition={{ duration: 0.5, ease: EASE }}
        className="overflow-hidden rounded-2xl"
      >
        <svg
          viewBox={`0 0 ${W} ${H}`}
          className="w-full"
          role="img"
          aria-label={`${label} trend chart`}
        >
          <defs>
            <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#22C55E" stopOpacity="0.35" />
              <stop offset="100%" stopColor="#22C55E" stopOpacity="0" />
            </linearGradient>
            <linearGradient id="lineGradient" x1="0" y1="0" x2="1" y2="0">
              <stop offset="0%" stopColor="#22C55E" />
              <stop offset="100%" stopColor="#06B6D4" />
            </linearGradient>
          </defs>

          {/* Grid lines */}
          {[0, 0.25, 0.5, 0.75, 1].map((frac, i) => {
            const y = PADDING.top + chartH * (1 - frac);
            const val = minVal + range * frac;
            return (
              <g key={i}>
                <line
                  x1={PADDING.left}
                  x2={W - PADDING.right}
                  y1={y}
                  y2={y}
                  stroke="rgba(255,255,255,0.06)"
                  strokeWidth="1"
                />
                <text
                  x={PADDING.left - 6}
                  y={y + 4}
                  textAnchor="end"
                  fill="rgba(255,255,255,0.35)"
                  fontSize="10"
                >
                  {val.toFixed(1)}{unit}
                </text>
              </g>
            );
          })}

          {/* Area fill */}
          <path d={areaPath} fill="url(#areaGradient)" />

          {/* Line */}
          <polyline
            points={pointsStr}
            fill="none"
            stroke="url(#lineGradient)"
            strokeWidth="2.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />

          {/* Data points + labels */}
          {data.map((d, i) => {
            const x = toX(i);
            const y = toY(values[i]);
            return (
              <g key={i}>
                <circle
                  cx={x}
                  cy={y}
                  r="4"
                  fill="#22C55E"
                  stroke="#0B1120"
                  strokeWidth="2"
                />
                <text
                  x={x}
                  y={H - PADDING.bottom + 16}
                  textAnchor="middle"
                  fill="rgba(255,255,255,0.45)"
                  fontSize="9"
                >
                  {d.period}
                </text>
              </g>
            );
          })}
        </svg>
      </motion.div>
    </div>
  );
}
