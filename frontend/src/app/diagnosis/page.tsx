"use client";

import { useState } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertCircle,
  AlertTriangle,
  ArrowLeft,
  Cpu,
  RotateCcw,
  Sparkles,
  Wifi,
  WifiOff,
  Wrench,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import ImageUploadZone from "@/components/diagnosis/ImageUploadZone";
import DeviceFormFields from "@/components/diagnosis/DeviceFormFields";
import AnalysisProgressModal from "@/components/diagnosis/AnalysisProgressModal";
import DiagnosisResultCard from "@/components/diagnosis/DiagnosisResultCard";

import { analyzeDeviceDiagnosis } from "@/lib/api/diagnosis";
import type { DiagnosisReport } from "@/lib/types/diagnosis";

const EASE = [0.22, 1, 0.36, 1] as const;

// ─── Preset Sample Devices for Testing ────────────────────────────────────────
const SAMPLE_PRESETS: Array<{
  id: string;
  name: string;
  category: string;
  brand: string;
  model: string;
  symptoms: string;
  imagePreview: string;
  report: DiagnosisReport;
}> = [
  {
    id: "iphone",
    name: "iPhone 13 Pro (Screen Crack)",
    category: "Smartphone",
    brand: "Apple",
    model: "iPhone 13 Pro",
    symptoms: "Cracked glass display, touch erratic in top left corner, battery drains fast.",
    imagePreview:
      "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80",
    report: {
      id: "diag_demo_1",
      symptoms: "Cracked glass display, touch erratic in top left corner, battery drains fast.",
      probableIssue: "OLED Panel Fracture & Lithium Battery Degradation",
      confidenceScore: 94,
      repairDifficulty: "Moderate",
      repairTime: "1-2 hours",
      repairCost: 85,
      imageUrl:
        "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80",
      observations: [
        "Primary impact point detected at top-left bezel frame.",
        "Digitizer flex cable layer shows signal resistance variance.",
        "Battery health estimated at 74% design capacity.",
      ],
      safetyWarning:
        "Handle cracked glass with care. Disconnect battery flex cable first to prevent board shorting.",
      createdAt: new Date().toISOString(),
    },
  },
  {
    id: "macbook",
    name: "MacBook Air M1 (Overheating)",
    category: "Laptop / PC",
    brand: "Apple",
    model: "MacBook Air M1 2020",
    symptoms: "Loud fan noise under light load, keyboard warm to touch, thermal throttling.",
    imagePreview:
      "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80",
    report: {
      id: "diag_demo_2",
      symptoms: "Loud fan noise under light load, keyboard warm to touch, thermal throttling.",
      probableIssue: "Thermal Interface Paste Breakdown & Dust Accumulation",
      confidenceScore: 89,
      repairDifficulty: "Easy",
      repairTime: "30-45 mins",
      repairCost: 45,
      imageUrl:
        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80",
      observations: [
        "Thermal paste dried out on SoC heatsink interface.",
        "Exhaust thermal fins 60% clogged with micro-particulates.",
      ],
      safetyWarning: "Use ESD grounding wrist strap before servicing internal motherboard components.",
      createdAt: new Date().toISOString(),
    },
  },
  {
    id: "gamepad",
    name: "PS5 DualSense (Stick Drift)",
    category: "Gaming Console",
    brand: "Sony",
    model: "DualSense Wireless Controller",
    symptoms: "Left analog stick drifts upward automatically during gameplay.",
    imagePreview:
      "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600&auto=format&fit=crop&q=80",
    report: {
      id: "diag_demo_3",
      symptoms: "Left analog stick drifts upward automatically during gameplay.",
      probableIssue: "Potentiometer Wear on Left Thumbstick Module",
      confidenceScore: 92,
      repairDifficulty: "Moderate",
      repairTime: "45 mins",
      repairCost: 18,
      imageUrl:
        "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=600&auto=format&fit=crop&q=80",
      observations: [
        "Conductive track wear detected on Y-axis potentiometer sensor.",
        "Thumbstick spring mechanism remains intact.",
      ],
      safetyWarning: "Precision desoldering iron required for sensor replacement.",
      createdAt: new Date().toISOString(),
    },
  },
];

export default function DiagnosisDashboardPage() {
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  const [category, setCategory] = useState("Smartphone");
  const [brand, setBrand] = useState("Apple");
  const [model, setModel] = useState("iPhone 13 Pro");
  const [symptoms, setSymptoms] = useState(
    "Cracked glass display, touch erratic in top left corner, battery drains fast."
  );

  const [selectedPresetId, setSelectedPresetId] = useState<string | null>("iphone");

  const [symptomsError, setSymptomsError] = useState<string | undefined>();
  const [apiError, setApiError] = useState<string | null>(null);

  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [activeReport, setActiveReport] = useState<DiagnosisReport | null>(null);
  const [isBackendOffline, setIsBackendOffline] = useState(true);

  const handleImageSelected = (file: File | null, previewUrl: string | null) => {
    setSelectedFile(file);
    setImagePreview(previewUrl);
    setSelectedPresetId(null);
  };

  const handleSelectPreset = (preset: (typeof SAMPLE_PRESETS)[number]) => {
    setSelectedPresetId(preset.id);
    setCategory(preset.category);
    setBrand(preset.brand);
    setModel(preset.model);
    setSymptoms(preset.symptoms);
    setImagePreview(preset.imagePreview);
    setSelectedFile(null);
    setSymptomsError(undefined);
    setApiError(null);
    setActiveReport(null);
  };

  const handleStartDiagnosis = async () => {
    setSymptomsError(undefined);
    setApiError(null);

    if (!symptoms.trim()) {
      setSymptomsError("Please describe the observed symptoms before running AI diagnosis.");
      return;
    }

    setIsAnalyzing(true);

    try {
      const response = await analyzeDeviceDiagnosis({
        deviceCategory: category,
        brand,
        model,
        symptoms,
        image: selectedFile || imagePreview || undefined,
      });

      if (response.success && response.data) {
        setIsBackendOffline(false);
        setActiveReport(response.data);
        setIsAnalyzing(false);
      } else {
        // Backend offline fallback — run sample demo analysis cleanly
        setIsBackendOffline(true);
        const matchedPreset = SAMPLE_PRESETS.find((p) => p.id === selectedPresetId);
        const fallbackReport: DiagnosisReport = matchedPreset
          ? matchedPreset.report
          : {
              id: "diag_custom_demo",
              symptoms,
              probableIssue: `${brand} ${model} Hardware Fault & Sensor Anomaly`,
              confidenceScore: 88,
              repairDifficulty: "Moderate",
              repairTime: "1-2 hours",
              repairCost: 65,
              imageUrl: imagePreview || undefined,
              observations: [
                "Visual artifact signatures indicate surface thermal stress.",
                "Primary power rail components require secondary voltage test.",
              ],
              safetyWarning: "Always disconnect power source before attempting hardware disassembly.",
              createdAt: new Date().toISOString(),
            };

        setActiveReport(fallbackReport);
      }
    } catch {
      setIsBackendOffline(true);
      setIsAnalyzing(false);
    }
  };

  const handleReset = () => {
    setActiveReport(null);
    setIsAnalyzing(false);
    setApiError(null);
  };

  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.10),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* Header */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <Logo size="sm" href="/" />

          <div className="flex items-center gap-3">
            <span
              className={`hidden items-center gap-1.5 rounded-full border px-3 py-1 text-[11px] font-semibold sm:inline-flex ${
                isBackendOffline
                  ? "border-amber-500/30 bg-amber-500/10 text-amber-400"
                  : "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
              }`}
            >
              {isBackendOffline ? (
                <>
                  <WifiOff className="size-3" />
                  Sample Demo Mode
                </>
              ) : (
                <>
                  <Wifi className="size-3" />
                  Live AI Server
                </>
              )}
            </span>

            <GlassButton href="/" variant="secondary" size="sm" icon={<ArrowLeft className="size-3.5" />}>
              Home
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* Page Title */}
      <div className="relative border-b border-white/[0.06] bg-white/[0.015] py-8 sm:py-10">
        <Container>
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, ease: EASE }}
            className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <div className="mb-2 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-3.5 py-1 text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
                <Cpu className="size-3.5" /> AI Diagnosis Dashboard
              </div>
              <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
                Smart Device{" "}
                <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
                  Visual Diagnosis
                </span>
              </h1>
              <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
                Upload a device image and symptoms to simulate instant AI hardware analysis, confidence score, estimated repair cost, and safety alerts.
              </p>
            </div>

            {activeReport && (
              <GlassButton
                variant="outline"
                size="sm"
                icon={<RotateCcw className="size-3.5" />}
                onClick={handleReset}
              >
                Start New Diagnosis
              </GlassButton>
            )}
          </motion.div>
        </Container>
      </div>

      {/* Backend Offline Disclosure Banner */}
      <AnimatePresence>
        {isBackendOffline && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="overflow-hidden border-b border-amber-500/20 bg-amber-500/[0.07]"
          >
            <Container className="flex items-center gap-3 py-3 text-xs text-amber-300">
              <AlertTriangle className="size-4 shrink-0 text-amber-400" />
              <span>
                <strong>Sample Demo Mode — </strong>
                Spring Boot AI backend server at <code>http://localhost:8080/api/v1</code> is currently offline. Results shown below are sample reference data for demonstration purposes.
              </span>
            </Container>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Main Content Area */}
      <main className="relative py-8 sm:py-12">
        <Container>
          {/* Analysis Progress Modal */}
          <AnalysisProgressModal
            isAnalyzing={isAnalyzing}
            onComplete={() => setIsAnalyzing(false)}
          />

          {/* Active Result View */}
          {activeReport ? (
            <DiagnosisResultCard
              report={activeReport}
              isDemo={isBackendOffline}
              onReset={handleReset}
            />
          ) : (
            /* Upload & Input Form View */
            <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
              {/* Left Column: Preset Selectors & Upload */}
              <div className="lg:col-span-6 space-y-6">
                <GlassCard padding="lg" glowColor="green" hoverEffect={false}>
                  <div className="flex items-center justify-between mb-4">
                    <h2 className="text-base font-bold text-white flex items-center gap-2">
                      <Wrench className="size-4 text-[#22C55E]" /> 1. Select Sample or Upload
                    </h2>
                    <span className="text-[11px] text-white/50">Step 1 of 2</span>
                  </div>

                  {/* Sample Device Quick Selector */}
                  <div className="mb-6">
                    <label className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2.5">
                      Or choose a preset sample device:
                    </label>
                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-2.5">
                      {SAMPLE_PRESETS.map((preset) => (
                        <button
                          key={preset.id}
                          type="button"
                          onClick={() => handleSelectPreset(preset)}
                          className={`flex flex-col items-center justify-center p-3 rounded-2xl border text-xs font-semibold transition-all ${
                            selectedPresetId === preset.id
                              ? "border-[#22C55E] bg-[#22C55E]/15 text-white shadow-[0_4px_16px_rgba(34,197,94,0.2)]"
                              : "border-white/10 bg-white/[0.04] text-white/70 hover:border-white/20 hover:text-white"
                          }`}
                        >
                          <span className="truncate w-full text-center">{preset.brand}</span>
                          <span className="text-[10px] font-normal text-white/50 truncate w-full text-center">
                            {preset.category}
                          </span>
                        </button>
                      ))}
                    </div>
                  </div>

                  {/* Image Upload Dropzone */}
                  <ImageUploadZone
                    onImageSelected={handleImageSelected}
                    selectedImagePreview={imagePreview}
                    selectedFileName={selectedFile?.name || null}
                  />
                </GlassCard>
              </div>

              {/* Right Column: Device Info & Diagnosis Trigger */}
              <div className="lg:col-span-6 space-y-6">
                <GlassCard padding="lg" glowColor="cyan" hoverEffect={false}>
                  <div className="flex items-center justify-between mb-4">
                    <h2 className="text-base font-bold text-white flex items-center gap-2">
                      <Cpu className="size-4 text-[#06B6D4]" /> 2. Device & Symptom Details
                    </h2>
                    <span className="text-[11px] text-white/50">Step 2 of 2</span>
                  </div>

                  {apiError && (
                    <div className="mb-4 flex items-center gap-2.5 rounded-2xl border border-red-500/40 bg-red-500/10 p-3.5 text-xs text-red-200">
                      <AlertCircle className="size-4 text-red-400 shrink-0" />
                      <span>{apiError}</span>
                    </div>
                  )}

                  <DeviceFormFields
                    category={category}
                    setCategory={setCategory}
                    brand={brand}
                    setBrand={setBrand}
                    model={model}
                    setModel={setModel}
                    symptoms={symptoms}
                    setSymptoms={setSymptoms}
                    symptomsError={symptomsError}
                  />

                  <div className="mt-8 pt-4 border-t border-white/10">
                    <GlassButton
                      fullWidth
                      size="lg"
                      onClick={handleStartDiagnosis}
                      disabled={isAnalyzing}
                      icon={<Sparkles className="size-5" />}
                    >
                      Run AI Diagnosis
                    </GlassButton>
                  </div>
                </GlassCard>
              </div>
            </div>
          )}
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>&copy; {new Date().getFullYear()} RepairVerse AI. Visual Diagnosis Dashboard.</p>
            <Link href="/" className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors">
              ← Back to RepairVerse AI
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
