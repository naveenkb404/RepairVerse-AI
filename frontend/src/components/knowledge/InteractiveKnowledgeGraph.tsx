"use client";

import React, { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Network,
  Cpu,
  Laptop,
  AlertTriangle,
  Wrench,
  CheckCircle2,
  Layers,
  ArrowRight,
  Sparkles,
  Info,
  Filter,
} from "lucide-react";
import GlassCard from "@/components/glass/GlassCard";
import type {
  KnowledgeNodeResponse,
  KnowledgeRelationshipResponse,
} from "@/lib/types/repairKnowledgeGraph";
import { cn } from "@/lib/utils";

interface InteractiveKnowledgeGraphProps {
  nodes: KnowledgeNodeResponse[];
  relationships: KnowledgeRelationshipResponse[];
}

export default function InteractiveKnowledgeGraph({
  nodes,
  relationships,
}: InteractiveKnowledgeGraphProps) {
  const [selectedNodeId, setSelectedNodeId] = useState<string>(nodes[0]?.id || "");
  const [filterType, setFilterType] = useState<string>("ALL");

  const getNodeColor = (nodeType: string) => {
    switch (nodeType) {
      case "DEVICE_MODEL":
      case "DEVICE_CATEGORY":
        return {
          border: "border-cyan-500/40",
          bg: "bg-cyan-500/10",
          text: "text-cyan-300",
          badge: "bg-cyan-500/20 text-cyan-300 border-cyan-500/30",
        };
      case "COMPONENT":
        return {
          border: "border-emerald-500/40",
          bg: "bg-emerald-500/10",
          text: "text-emerald-300",
          badge: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30",
        };
      case "FAILURE_MODE":
      case "SYMPTOM":
        return {
          border: "border-amber-500/40",
          bg: "bg-amber-500/10",
          text: "text-amber-300",
          badge: "bg-amber-500/20 text-amber-300 border-amber-500/30",
        };
      case "REPAIR_ACTION":
      case "REPAIR_PART":
        return {
          border: "border-teal-500/40",
          bg: "bg-teal-500/10",
          text: "text-teal-300",
          badge: "bg-teal-500/20 text-teal-300 border-teal-500/30",
        };
      case "REPAIR_OUTCOME":
        return {
          border: "border-green-500/40",
          bg: "bg-green-500/10",
          text: "text-green-400",
          badge: "bg-green-500/20 text-green-400 border-green-500/30",
        };
      default:
        return {
          border: "border-slate-700",
          bg: "bg-slate-800/40",
          text: "text-slate-300",
          badge: "bg-slate-800 text-slate-300 border-slate-700",
        };
    }
  };

  const selectedNode = nodes.find((n) => n.id === selectedNodeId) || nodes[0];

  const connectedRelationships = relationships.filter(
    (r) => r.sourceNodeId === selectedNodeId || r.targetNodeId === selectedNodeId
  );

  const filteredNodes = nodes.filter((n) => {
    if (filterType === "ALL") return true;
    return n.nodeType === filterType;
  });

  const nodeTypes = ["ALL", "DEVICE_MODEL", "COMPONENT", "FAILURE_MODE", "SYMPTOM", "REPAIR_ACTION", "REPAIR_OUTCOME"];

  return (
    <GlassCard padding="lg" glowColor="none" className="overflow-hidden">
      <div className="space-y-6">
        {/* Header & Filter Controls */}
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-white/10 pb-4">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
              <Network className="h-5 w-5" />
            </div>
            <div>
              <h3 className="text-base font-bold text-white">Interactive Knowledge Graph Explorer</h3>
              <p className="text-xs text-slate-400">Select any entity to trace learned cause-effect paths</p>
            </div>
          </div>

          {/* Node Type Filter Tabs */}
          <div className="flex flex-wrap items-center gap-1.5">
            {nodeTypes.map((type) => (
              <button
                key={type}
                onClick={() => setFilterType(type)}
                className={cn(
                  "rounded-lg px-2.5 py-1 text-[11px] font-semibold transition-all",
                  filterType === type
                    ? "bg-gradient-to-r from-cyan-500 to-emerald-500 text-slate-950 shadow-sm"
                    : "border border-white/5 bg-white/[0.03] text-slate-400 hover:bg-white/[0.08] hover:text-white"
                )}
              >
                {type === "ALL" ? "All Nodes" : type.replace("_", " ")}
              </button>
            ))}
          </div>
        </div>

        {/* Main Graph Visual Area: Node Grid on Left, Inspector on Right */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          {/* Node Matrix (7 Cols) */}
          <div className="lg:col-span-7 space-y-3">
            <div className="text-xs font-semibold text-slate-400 flex items-center justify-between">
              <span>Entities in Knowledge Graph ({filteredNodes.length})</span>
              <span className="text-[10px] text-slate-500 font-mono">Click to inspect</span>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-[480px] overflow-y-auto pr-1">
              {filteredNodes.map((node) => {
                const color = getNodeColor(node.nodeType);
                const isSelected = node.id === selectedNodeId;

                return (
                  <button
                    key={node.id}
                    onClick={() => setSelectedNodeId(node.id)}
                    className={cn(
                      "group flex flex-col text-left rounded-xl border p-3.5 transition-all",
                      isSelected
                        ? `${color.border} ${color.bg} shadow-lg ring-1 ring-cyan-400/40`
                        : "border-white/5 bg-white/[0.02] hover:border-white/10 hover:bg-white/[0.04]"
                    )}
                  >
                    <div className="flex items-center justify-between gap-2">
                      <span className={cn("text-xs font-bold truncate", isSelected ? color.text : "text-white")}>
                        {node.displayName}
                      </span>
                      <span className={cn("rounded-full border px-2 py-0.2 text-[9px] font-mono uppercase shrink-0", color.badge)}>
                        {node.nodeType.replace("_", " ")}
                      </span>
                    </div>

                    <p className="mt-1 text-[11px] text-slate-400 line-clamp-2 leading-relaxed">
                      {node.description || "No description available."}
                    </p>

                    <div className="mt-2.5 flex items-center justify-between text-[10px] font-mono text-slate-500 border-t border-white/5 pt-1.5">
                      <span>Obs: {node.observationCount}</span>
                      <span>Confidence: {Math.round(node.confidenceScore * 100)}%</span>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Node & Connected Relationships Inspector (5 Cols) */}
          <div className="lg:col-span-5 space-y-4">
            {selectedNode ? (
              <div className="rounded-2xl border border-white/10 bg-slate-900/60 p-5 backdrop-blur-md space-y-4">
                {/* Selected Node Details */}
                <div className="border-b border-white/5 pb-3">
                  <div className="flex items-center gap-2">
                    <span className={cn("rounded-full border px-2.5 py-0.5 text-[10px] font-bold font-mono uppercase", getNodeColor(selectedNode.nodeType).badge)}>
                      {selectedNode.nodeType.replace("_", " ")}
                    </span>
                    <span className="text-[10px] font-mono text-slate-500">{selectedNode.nodeKey}</span>
                  </div>
                  <h4 className="text-base font-bold text-white mt-1.5">{selectedNode.displayName}</h4>
                  <p className="text-xs text-slate-300 mt-1 leading-relaxed">{selectedNode.description}</p>
                </div>

                {/* Connected Relationships Stream */}
                <div>
                  <div className="text-xs font-semibold text-slate-300 mb-2.5 flex items-center gap-1.5">
                    <Sparkles className="h-3.5 w-3.5 text-cyan-400" />
                    <span>Connected Graph Relationships ({connectedRelationships.length})</span>
                  </div>

                  {connectedRelationships.length === 0 ? (
                    <div className="rounded-xl border border-dashed border-white/10 p-4 text-center text-xs text-slate-500">
                      No direct relationships connected yet.
                    </div>
                  ) : (
                    <div className="space-y-2.5 max-h-[300px] overflow-y-auto pr-1">
                      {connectedRelationships.map((rel) => {
                        const isSource = rel.sourceNodeId === selectedNodeId;
                        const otherName = isSource ? rel.targetDisplayName : rel.sourceDisplayName;
                        const otherType = isSource ? rel.targetNodeType : rel.sourceNodeType;

                        return (
                          <div
                            key={rel.id}
                            className="rounded-xl border border-white/5 bg-white/[0.02] p-3 transition-colors hover:border-white/10"
                          >
                            <div className="flex items-center justify-between text-[11px]">
                              <span className="rounded bg-cyan-500/10 border border-cyan-500/20 px-1.5 py-0.2 text-[9px] font-mono text-cyan-300">
                                {rel.relationshipType}
                              </span>
                              <span className="font-mono text-emerald-400 text-[10px] font-bold">
                                Strength: {rel.strength.toFixed(0)}/100
                              </span>
                            </div>

                            <div className="mt-2 flex items-center gap-2 text-xs text-white">
                              <span className="font-medium text-slate-400">{isSource ? "Connects to →" : "Derived from ←"}</span>
                              <span className="font-bold text-slate-200">{otherName}</span>
                              <span className="text-[10px] text-slate-500">({otherType})</span>
                            </div>

                            <div className="mt-1 text-[10px] text-slate-500 font-mono">
                              Based on {rel.observationCount} verified repair observations
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>
            ) : (
              <div className="rounded-2xl border border-dashed border-white/10 p-8 text-center text-xs text-slate-500">
                Select an entity node from the left to inspect its ecosystem relationships.
              </div>
            )}
          </div>
        </div>
      </div>
    </GlassCard>
  );
}
