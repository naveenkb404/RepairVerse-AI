"use client";

import { motion } from "framer-motion";
import { Cloud, Trash2, IndianRupee, Clock, Wrench, RefreshCw, Sparkles, CheckCircle } from "lucide-react";
import type { CircularImpactMetrics } from "@/lib/types/circularEconomy";

interface ImpactMetricsGridProps {
  metrics: CircularImpactMetrics;
}

export default function ImpactMetricsGrid({ metrics }: ImpactMetricsGridProps) {
  const cards = [
    {
      title: "Carbon Footprint Avoided",
      value: `${metrics?.totalCarbonSavedKg ?? 0}`,
      unit: "kg CO₂",
      icon: Cloud,
      color: "text-emerald-400",
      bgGradient: "from-emerald-500/10 via-emerald-500/5 to-transparent",
      borderColor: "border-emerald-500/20",
      description: "Avoided manufacturing & supply-chain greenhouse emissions",
      highlight: `≈ ${Math.round((metrics?.totalCarbonSavedKg ?? 0) * 4.2)} km car travel offset`,
    },
    {
      title: "E-Waste Prevented",
      value: `${metrics?.totalEwastePreventedKg ?? 0}`,
      unit: "kg e-waste",
      icon: Trash2,
      color: "text-cyan-400",
      bgGradient: "from-cyan-500/10 via-cyan-500/5 to-transparent",
      borderColor: "border-cyan-500/20",
      description: "Hazardous electronic components diverted from landfill dump sites",
      highlight: "Heavy metals & battery toxins locked",
    },
    {
      title: "Financial Savings",
      value: `₹${(metrics?.totalMoneySaved ?? 0).toLocaleString("en-IN")}`,
      unit: "saved",
      icon: IndianRupee,
      color: "text-amber-300",
      bgGradient: "from-amber-500/10 via-amber-500/5 to-transparent",
      borderColor: "border-amber-500/20",
      description: "Cost differential between repair/maintenance vs brand new replacements",
      highlight: "Direct household economic retention",
    },
    {
      title: "Lifespan Extended",
      value: `${metrics?.totalLifeExtensionDays ?? 0}`,
      unit: "days",
      icon: Clock,
      color: "text-blue-400",
      bgGradient: "from-blue-500/10 via-blue-500/5 to-transparent",
      borderColor: "border-blue-500/20",
      description: "Cumulative operational days added to active hardware ecosystem",
      highlight: `≈ ${((metrics?.totalLifeExtensionDays ?? 0) / 365).toFixed(1)} additional years`,
    },
  ];

  return (
    <div className="space-y-4">
      {/* 4 Main Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {cards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <motion.div
              key={idx}
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: idx * 0.08 }}
              className={`relative overflow-hidden rounded-3xl border ${card.borderColor} bg-gradient-to-br ${card.bgGradient} bg-[#0B1120]/80 p-5 backdrop-blur-xl shadow-lg flex flex-col justify-between`}
            >
              <div>
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-slate-400">{card.title}</span>
                  <div className={`rounded-xl bg-white/5 p-2 ${card.color}`}>
                    <Icon className="size-4" />
                  </div>
                </div>

                <div className="mt-4 flex items-baseline gap-2">
                  <span className="text-2xl md:text-3xl font-black text-white tracking-tight">
                    {card.value}
                  </span>
                  <span className={`text-xs font-bold ${card.color}`}>{card.unit}</span>
                </div>

                <p className="mt-2 text-xs text-slate-400 line-clamp-2 leading-relaxed">
                  {card.description}
                </p>
              </div>

              <div className="mt-4 pt-3 border-t border-white/5 flex items-center gap-1.5 text-[11px] font-medium text-slate-300">
                <Sparkles className="size-3 text-emerald-400 shrink-0" />
                <span className="truncate">{card.highlight}</span>
              </div>
            </motion.div>
          );
        })}
      </div>

      {/* Action Totals Strip */}
      <div className="rounded-2xl border border-white/10 bg-white/[0.02] p-4 backdrop-blur-md">
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-center">
          <div className="space-y-1">
            <div className="text-xs text-slate-400 flex items-center justify-center gap-1.5">
              <Wrench className="size-3.5 text-emerald-400" />
              Repairs Completed
            </div>
            <div className="text-lg font-bold text-white">{metrics?.totalRepairs ?? 0}</div>
          </div>
          <div className="space-y-1 border-l border-white/5">
            <div className="text-xs text-slate-400 flex items-center justify-center gap-1.5">
              <CheckCircle className="size-3.5 text-cyan-400" />
              Maintenance Actions
            </div>
            <div className="text-lg font-bold text-white">
              {metrics?.totalMaintenanceActions ?? 0}
            </div>
          </div>
          <div className="space-y-1 border-l border-white/5">
            <div className="text-xs text-slate-400 flex items-center justify-center gap-1.5">
              <RefreshCw className="size-3.5 text-amber-300" />
              Refurbished Gear
            </div>
            <div className="text-lg font-bold text-white">{metrics?.totalRefurbishments ?? 0}</div>
          </div>
          <div className="space-y-1 border-l border-white/5">
            <div className="text-xs text-slate-400 flex items-center justify-center gap-1.5">
              <Trash2 className="size-3.5 text-blue-400" />
              Recycled Responsibly
            </div>
            <div className="text-lg font-bold text-white">
              {metrics?.totalResponsibleDisposals ?? 0}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
