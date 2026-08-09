"use client";

import { motion } from "framer-motion";
import { Wrench, Package, Check, DollarSign, Tag } from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import { RequiredPart, RequiredTool } from "@/lib/types/recommendation";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function PartsAndToolsGrid({
  parts,
  tools,
}: {
  parts: RequiredPart[];
  tools: RequiredTool[];
}) {
  const totalPartsCost = parts.reduce(
    (acc, item) => acc + item.estimatedCost * item.quantity,
    0
  );

  return (
    <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
      {/* Replacement Parts Card */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, ease: EASE }}
      >
        <GlassCard padding="lg" hoverEffect={false}>
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Package className="size-5 text-[#06B6D4]" />
              <h3 className="text-base font-bold text-white">Required Replacement Parts</h3>
            </div>
            <span className="text-xs font-bold text-[#06B6D4]">
              Est. ${totalPartsCost}
            </span>
          </div>

          {parts.length === 0 ? (
            <p className="text-xs text-white/50 italic py-4">
              No component replacement required for this repair procedure.
            </p>
          ) : (
            <ul className="space-y-2.5">
              {parts.map((part, idx) => (
                <li
                  key={idx}
                  className="flex items-center justify-between rounded-xl border border-white/10 bg-white/[0.04] p-3 text-xs"
                >
                  <div className="flex items-center gap-2.5">
                    <span className="flex size-6 items-center justify-center rounded-lg bg-[#06B6D4]/20 text-[10px] font-bold text-[#06B6D4]">
                      x{part.quantity}
                    </span>
                    <div>
                      <p className="font-bold text-white">{part.name}</p>
                      {part.partNumber && (
                        <p className="text-[10px] text-white/50">P/N: {part.partNumber}</p>
                      )}
                    </div>
                  </div>

                  <span className="font-bold text-[#22C55E]">${part.estimatedCost}</span>
                </li>
              ))}
            </ul>
          )}
        </GlassCard>
      </motion.div>

      {/* Required Tools Card */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, delay: 0.1, ease: EASE }}
      >
        <GlassCard padding="lg" hoverEffect={false}>
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Wrench className="size-5 text-[#22C55E]" />
              <h3 className="text-base font-bold text-white">Required Repair Tools</h3>
            </div>
            <span className="text-xs font-medium text-white/60">
              {tools.length} Tools
            </span>
          </div>

          {tools.length === 0 ? (
            <p className="text-xs text-white/50 italic py-4">
              No specialized tools required. Standard servicing applies.
            </p>
          ) : (
            <ul className="space-y-2.5">
              {tools.map((tool, idx) => (
                <li
                  key={idx}
                  className="flex items-center justify-between rounded-xl border border-white/10 bg-white/[0.04] p-3 text-xs"
                >
                  <div className="flex items-center gap-2">
                    <div className="flex size-5 items-center justify-center rounded-full bg-[#22C55E]/20 text-[#22C55E]">
                      <Check className="size-3" />
                    </div>
                    <span className="font-semibold text-white">{tool.name}</span>
                  </div>

                  <div className="flex items-center gap-2">
                    {tool.category && (
                      <span className="text-[10px] text-white/50 bg-white/[0.05] px-2 py-0.5 rounded-md">
                        {tool.category}
                      </span>
                    )}
                    {tool.essential && (
                      <span className="rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-2 py-0.5 text-[9px] font-bold text-[#22C55E]">
                        Essential
                      </span>
                    )}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </GlassCard>
      </motion.div>
    </div>
  );
}
