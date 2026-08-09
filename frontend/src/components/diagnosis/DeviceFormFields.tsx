"use client";

import { Cpu, FileText, Smartphone, Tag } from "lucide-react";
import { cn } from "@/lib/utils";

export type DeviceFormFieldsProps = {
  category: string;
  setCategory: (val: string) => void;
  brand: string;
  setBrand: (val: string) => void;
  model: string;
  setModel: (val: string) => void;
  symptoms: string;
  setSymptoms: (val: string) => void;
  symptomsError?: string;
};

const CATEGORIES = [
  "Smartphone",
  "Laptop / PC",
  "Tablet",
  "Gaming Console",
  "Audio / Headphones",
  "Smartwatch",
  "Other Appliance",
];

export default function DeviceFormFields({
  category,
  setCategory,
  brand,
  setBrand,
  model,
  setModel,
  symptoms,
  setSymptoms,
  symptomsError,
}: DeviceFormFieldsProps) {
  return (
    <div className="space-y-4">
      {/* Category Dropdown/Select */}
      <div>
        <label
          htmlFor="deviceCategory"
          className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2"
        >
          Device Category
        </label>
        <div className="relative">
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
            <Smartphone className="size-4" aria-hidden />
          </div>
          <select
            id="deviceCategory"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full appearance-none rounded-2xl border border-white/15 bg-white/[0.05] py-3 pl-10 pr-10 text-sm text-white backdrop-blur-xl transition-all hover:border-white/25 focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
          >
            {CATEGORIES.map((cat) => (
              <option key={cat} value={cat} className="bg-[#0B1120] text-white">
                {cat}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Brand & Model Row */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        {/* Brand */}
        <div>
          <label
            htmlFor="brand"
            className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2"
          >
            Brand / Manufacturer
          </label>
          <div className="relative">
            <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
              <Tag className="size-4" aria-hidden />
            </div>
            <input
              id="brand"
              type="text"
              value={brand}
              onChange={(e) => setBrand(e.target.value)}
              placeholder="e.g. Apple, Samsung, Dell"
              className="w-full rounded-2xl border border-white/15 bg-white/[0.05] py-3 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all hover:border-white/25 focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            />
          </div>
        </div>

        {/* Model */}
        <div>
          <label
            htmlFor="model"
            className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2"
          >
            Device Model
          </label>
          <div className="relative">
            <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
              <Cpu className="size-4" aria-hidden />
            </div>
            <input
              id="model"
              type="text"
              value={model}
              onChange={(e) => setModel(e.target.value)}
              placeholder="e.g. iPhone 13 Pro, M1 Air"
              className="w-full rounded-2xl border border-white/15 bg-white/[0.05] py-3 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all hover:border-white/25 focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            />
          </div>
        </div>
      </div>

      {/* Symptoms Description */}
      <div>
        <label
          htmlFor="symptoms"
          className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2"
        >
          Observed Symptoms / Problem Description <span className="text-[#22C55E]">*</span>
        </label>
        <div className="relative">
          <div className="pointer-events-none absolute top-3.5 left-3.5 text-white/40">
            <FileText className="size-4" aria-hidden />
          </div>
          <textarea
            id="symptoms"
            rows={3}
            value={symptoms}
            onChange={(e) => setSymptoms(e.target.value)}
            placeholder="Describe what's wrong (e.g. screen flickering, won't charge past 15%, grinding fan sound)..."
            className={`w-full rounded-2xl border bg-white/[0.05] py-3 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
              symptomsError
                ? "border-red-500/60 focus:ring-red-500/50"
                : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
            }`}
          />
        </div>
        {symptomsError && (
          <p className="mt-1.5 text-xs font-medium text-red-400">{symptomsError}</p>
        )}
      </div>
    </div>
  );
}
