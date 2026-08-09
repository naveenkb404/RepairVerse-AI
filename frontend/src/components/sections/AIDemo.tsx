"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import {
  AlertTriangle,
  Battery,
  CheckCircle2,
  Cpu,
  DollarSign,
  Image as ImageIcon,
  Leaf,
  Loader2,
  RotateCcw,
  Sparkles,
  Upload,
  Wrench,
} from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";

const EASE = [0.22, 1, 0.36, 1] as const;

// Sample demo devices for quick testing
const SAMPLE_DEVICES = [
  {
    id: "iphone",
    name: "iPhone 13 Pro (Cracked Screen & Battery)",
    imagePreview: "📱",
    healthScore: "68 / 100",
    cost: "$85",
    difficulty: "Moderate",
    carbon: "3.2 kg CO₂",
    issue: "OLED Display damage & 74% battery capacity degradation",
    recommendation: "Repair recommended (saves $640 over replacement)",
  },
  {
    id: "macbook",
    name: "MacBook Air M1 (Overheating & Fan Noise)",
    imagePreview: "💻",
    healthScore: "82 / 100",
    cost: "$45",
    difficulty: "Easy",
    carbon: "4.8 kg CO₂",
    issue: "Thermal paste degradation & dust buildup in heatsink",
    recommendation: "Simple thermal service recommended",
  },
  {
    id: "gamepad",
    name: "PS5 DualSense Controller (Stick Drift)",
    imagePreview: "🎮",
    healthScore: "75 / 100",
    cost: "$18",
    difficulty: "Easy",
    carbon: "0.9 kg CO₂",
    issue: "Analog potentiometer wear on Left Stick",
    recommendation: "Module replacement recommended",
  },
];

export default function AIDemo() {
  const [selectedSample, setSelectedSample] = useState(SAMPLE_DEVICES[0]);
  const [customImage, setCustomImage] = useState<string | null>(null);
  const [customFileName, setCustomFileName] = useState<string | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisComplete, setAnalysisComplete] = useState(true);

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setCustomFileName(file.name);
      const reader = new FileReader();
      reader.onload = (event) => {
        setCustomImage(event.target?.result as string);
        setAnalysisComplete(false);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRunDiagnosis = () => {
    setIsAnalyzing(true);
    setAnalysisComplete(false);
    setTimeout(() => {
      setIsAnalyzing(false);
      setAnalysisComplete(true);
    }, 2000);
  };

  return (
    <section id="ai-demo" className="relative overflow-hidden bg-[#08111F] py-20 sm:py-28">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,rgba(34,197,94,0.08),transparent_50%),radial-gradient(ellipse_at_bottom_left,rgba(6,182,212,0.08),transparent_50%)]"
        aria-hidden
      />

      <Container className="relative">
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-16 max-w-3xl text-center"
        >
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.18em] text-[#22C55E]">
            <Sparkles className="size-3.5" aria-hidden />
            Interactive Demo
          </div>
          <h2 className="text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Experience{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              AI Device Diagnosis
            </span>
          </h2>
          <p className="mt-4 text-base text-[#CBD5E1] sm:text-lg">
            Simulate an instant AI diagnostic scan. Select a sample device or upload a photo to preview real-time issue analysis, repair estimates, and carbon footprint reduction.
          </p>
        </motion.div>

        {/* Demo Interface Grid */}
        <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
          {/* Upload / Selection Card */}
          <div className="lg:col-span-6">
            <GlassCard padding="lg" glowColor="green">
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-bold text-white flex items-center gap-2">
                  <Upload className="size-5 text-[#22C55E]" aria-hidden />
                  Input Device Data
                </h3>
                <span className="rounded-full border border-white/10 bg-white/[0.05] px-3 py-1 text-[11px] font-medium text-white/70">
                  Demo Preview Mode
                </span>
              </div>

              {/* Sample Device Quick Selectors */}
              <div className="mb-6">
                <label className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-3">
                  Or select a sample device:
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
                  {SAMPLE_DEVICES.map((sample) => (
                    <button
                      key={sample.id}
                      type="button"
                      onClick={() => {
                        setSelectedSample(sample);
                        setCustomImage(null);
                        setCustomFileName(null);
                        setAnalysisComplete(true);
                      }}
                      className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-medium transition-all ${
                        selectedSample.id === sample.id && !customImage
                          ? "border-[#22C55E] bg-[#22C55E]/15 text-white shadow-[0_4px_16px_rgba(34,197,94,0.2)]"
                          : "border-white/10 bg-white/[0.04] text-white/70 hover:border-white/20 hover:text-white"
                      }`}
                    >
                      <span className="text-xl mb-1">{sample.imagePreview}</span>
                      <span className="truncate w-full text-center">{sample.name.split("(")[0]}</span>
                    </button>
                  ))}
                </div>
              </div>

              {/* Upload Dropzone */}
              <div className="relative border-2 border-dashed border-[#22C55E]/40 rounded-3xl p-8 text-center bg-white/[0.02] hover:bg-white/[0.04] transition-colors">
                <input
                  type="file"
                  accept="image/jpeg,image/png,image/webp"
                  onChange={handleFileUpload}
                  className="absolute inset-0 size-full opacity-0 cursor-pointer z-10"
                  aria-label="Upload device photo"
                />

                {customImage ? (
                  <div className="flex flex-col items-center">
                    <img
                      src={customImage}
                      alt="Uploaded device preview"
                      className="max-h-40 rounded-2xl object-cover border border-white/20 mb-3"
                    />
                    <p className="text-sm font-semibold text-white truncate max-w-xs">
                      {customFileName}
                    </p>
                    <p className="text-xs text-[#22C55E] mt-1 flex items-center gap-1">
                      <CheckCircle2 className="size-3.5" /> Image ready for AI scan
                    </p>
                  </div>
                ) : (
                  <div className="flex flex-col items-center">
                    <div className="size-14 rounded-2xl border border-white/15 bg-white/[0.06] flex items-center justify-center mb-4 text-[#22C55E]">
                      <ImageIcon className="size-7" />
                    </div>
                    <h4 className="text-base font-semibold text-white">
                      Drop device photo here or click to browse
                    </h4>
                    <p className="text-xs text-[#CBD5E1] mt-1">
                      Supports JPG, PNG, WEBP up to 10MB
                    </p>
                  </div>
                )}
              </div>

              {/* Action Trigger */}
              <div className="mt-8">
                <GlassButton
                  fullWidth
                  size="lg"
                  onClick={handleRunDiagnosis}
                  disabled={isAnalyzing}
                  icon={
                    isAnalyzing ? (
                      <Loader2 className="size-5 animate-spin" />
                    ) : (
                      <Sparkles className="size-5" />
                    )
                  }
                >
                  {isAnalyzing ? "Analyzing Device Hardware..." : "Simulate AI Diagnosis"}
                </GlassButton>
              </div>
            </GlassCard>
          </div>

          {/* Diagnosis Results Card */}
          <div className="lg:col-span-6">
            <GlassCard padding="lg" glowColor="cyan">
              <div className="flex items-center justify-between mb-6">
                <div className="flex items-center gap-2.5">
                  <Cpu className="size-6 text-[#06B6D4]" aria-hidden />
                  <h3 className="text-xl font-bold text-white">AI Diagnostic Result</h3>
                </div>
                <div className="flex items-center gap-2">
                  <span className="relative flex size-2.5">
                    <span className="absolute inline-flex size-full animate-ping rounded-full bg-[#22C55E] opacity-75" />
                    <span className="relative inline-flex size-2.5 rounded-full bg-[#22C55E]" />
                  </span>
                  <span className="text-xs font-semibold text-[#22C55E]">Analysis Active</span>
                </div>
              </div>

              <AnimatePresence mode="wait">
                {isAnalyzing ? (
                  <motion.div
                    key="analyzing"
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    className="flex flex-col items-center justify-center py-16 text-center"
                  >
                    <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
                    <h4 className="text-lg font-bold text-white">Running Vision Neural Net</h4>
                    <p className="text-xs text-[#CBD5E1] mt-1 max-w-xs">
                      Checking component integrity, thermal stress signatures, and cost matrix...
                    </p>
                  </motion.div>
                ) : (
                  <motion.div
                    key="results"
                    initial={{ opacity: 0, y: 16 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.4, ease: EASE }}
                    className="space-y-4"
                  >
                    {/* Device Label */}
                    <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 flex items-center justify-between">
                      <div>
                        <p className="text-xs text-[#CBD5E1]">Detected Device</p>
                        <p className="text-base font-bold text-white mt-0.5">
                          {customImage ? customFileName : selectedSample.name}
                        </p>
                      </div>
                      <span className="text-2xl">
                        {customImage ? "📷" : selectedSample.imagePreview}
                      </span>
                    </div>

                    {/* Detected Issue */}
                    <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-4">
                      <p className="text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
                        Probable Issue Detected
                      </p>
                      <p className="text-sm font-medium text-white mt-1">
                        {customImage
                          ? "Component surface wear & battery lifecycle warning"
                          : selectedSample.issue}
                      </p>
                    </div>

                    {/* Metric Cards Grid */}
                    <div className="grid grid-cols-2 gap-3">
                      <ResultMetric
                        icon={<Battery className="size-4 text-[#22C55E]" />}
                        title="Health Score"
                        value={customImage ? "78 / 100" : selectedSample.healthScore}
                      />
                      <ResultMetric
                        icon={<DollarSign className="size-4 text-[#06B6D4]" />}
                        title="Estimated Cost"
                        value={customImage ? "$55 - $70" : selectedSample.cost}
                      />
                      <ResultMetric
                        icon={<Wrench className="size-4 text-[#22C55E]" />}
                        title="Repair Difficulty"
                        value={customImage ? "Moderate" : selectedSample.difficulty}
                      />
                      <ResultMetric
                        icon={<Leaf className="size-4 text-[#06B6D4]" />}
                        title="Carbon Saved"
                        value={customImage ? "2.6 kg CO₂" : selectedSample.carbon}
                      />
                    </div>

                    {/* Recommendation Alert */}
                    <div className="flex items-start gap-3 rounded-2xl border border-white/10 bg-white/[0.05] p-4">
                      <AlertTriangle className="size-5 text-[#FACC15] shrink-0 mt-0.5" />
                      <div>
                        <p className="text-xs font-bold text-white">AI Recommendation</p>
                        <p className="text-xs text-[#CBD5E1] mt-0.5">
                          {customImage
                            ? "Repair is highly economical compared to buying a replacement."
                            : selectedSample.recommendation}
                        </p>
                      </div>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </GlassCard>
          </div>
        </div>
      </Container>
    </section>
  );
}

function ResultMetric({
  icon,
  title,
  value,
}: {
  icon: React.ReactNode;
  title: string;
  value: string;
}) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-3.5 flex flex-col justify-between">
      <div className="flex items-center gap-2">
        {icon}
        <span className="text-xs text-[#CBD5E1]">{title}</span>
      </div>
      <p className="text-base font-bold text-white mt-2">{value}</p>
    </div>
  );
}