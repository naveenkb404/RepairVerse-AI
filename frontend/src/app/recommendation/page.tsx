"use client";

import { useEffect, useState, Suspense } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertTriangle,
  ArrowLeft,
  Loader2,
  MapPin as MapPinIcon,
  RefreshCw,
  Sparkles,
  Wifi,
  WifiOff,
  Wrench,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import DiagnosisSummaryPanel from "@/components/recommendation/DiagnosisSummaryPanel";
import RecommendedActionBanner from "@/components/recommendation/RecommendedActionBanner";
import DecisionComparisonPanel from "@/components/recommendation/DecisionComparisonPanel";
import PartsAndToolsGrid from "@/components/recommendation/PartsAndToolsGrid";
import RepairPlanTimeline from "@/components/recommendation/RepairPlanTimeline";

import { fetchRepairRecommendation } from "@/lib/api/recommendation";
import type { RepairRecommendation } from "@/lib/types/recommendation";
import type { DiagnosisReport } from "@/lib/types/diagnosis";

const EASE = [0.22, 1, 0.36, 1] as const;

// ─── Preset Sample Recommendations for Demo Mode ──────────────────────────────
const SAMPLE_RECOMMENDATIONS: Record<string, RepairRecommendation> = {
  diag_demo_1: {
    id: "rec_demo_1",
    diagnosisId: "diag_demo_1",
    action: "REPAIR",
    repairScore: 92,
    replaceScore: 28,
    diagnosisReport: {
      id: "diag_demo_1",
      symptoms: "Cracked glass display, touch erratic in top left corner, battery drains fast.",
      probableIssue: "OLED Panel Fracture & Lithium Battery Degradation",
      confidenceScore: 94,
      repairDifficulty: "Moderate",
      repairTime: "1-2 hours",
      repairCost: 85,
      imageUrl:
        "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&auto=format&fit=crop&q=80",
    },
    decision: {
      repairScore: 92,
      replaceScore: 28,
      recommendation: "REPAIR",
      moneySaved: 640,
      carbonSaved: 6.5,
      rationale:
        "Self-repair is strongly recommended. Replacing the OLED assembly and battery costs $85, saving $640 over purchasing a new flagship smartphone while preventing 6.5 kg of CO₂ emissions.",
    },
    plan: {
      summary:
        "Standard OLED assembly and battery replacement procedure. Requires prying pick, Pentalobe screwdriver, and heat gun to soften display adhesive.",
      steps: [
        {
          stepNumber: 1,
          title: "Power Off & Heat Bezel Display Adhesive",
          description:
            "Completely shut down device. Use a heat gun or iOpener around perimeter edges for 2 minutes to soften screen adhesive bond.",
          safetyNote: "Do not exceed 80°C heat surface temperature to avoid thermal battery stress.",
          estimatedMinutes: 10,
        },
        {
          stepNumber: 2,
          title: "Apply Suction Cup & Pry Display Assembly",
          description:
            "Attach suction cup near bottom speaker edge. Insert thin opening pick underneath glass lip and slice around left and right edges.",
          safetyNote: "Slice shallowly on right edge to avoid tearing digitizer sensor flex ribbon cables.",
          estimatedMinutes: 15,
        },
        {
          stepNumber: 3,
          title: "Disconnect Battery & Display Connector Bracket",
          description:
            "Unscrew tri-point EMI shield bracket screws. Use plastic spudger to pop off battery flex connector first, followed by display flex connectors.",
          safetyNote: "Always disconnect battery cable before removing display connectors.",
          estimatedMinutes: 10,
        },
        {
          stepNumber: 4,
          title: "Install Replacement OLED Panel & Battery Pack",
          description:
            "Install new battery pull-tabs and secure battery pack. Connect replacement OLED assembly flex cables and replace EMI shield bracket.",
          estimatedMinutes: 20,
        },
        {
          stepNumber: 5,
          title: "Post-Repair Testing & Adhesive Sealing",
          description:
            "Power on device to test touch digitizer accuracy, display brightness, and charge cycle. Apply pre-cut water resistance adhesive seal and press closed.",
          estimatedMinutes: 15,
        },
      ],
      parts: [
        {
          name: "Replacement OLED Display Assembly + Digitizer",
          quantity: 1,
          estimatedCost: 65,
          partNumber: "APL-IP13P-DSP",
        },
        {
          name: "High-Capacity Replacement Battery Pack",
          quantity: 1,
          estimatedCost: 20,
          partNumber: "APL-IP13P-BATT",
        },
      ],
      tools: [
        { name: "Pentalobe & Tri-point Precision Screwdriver Set", category: "Hand Tools", essential: true },
        { name: "Plastic Spudger & Opening Picks", category: "Pry Tools", essential: true },
        { name: "Suction Cup Handle", category: "Hand Tools", essential: true },
        { name: "Heat Gun or iOpener Pad", category: "Thermal", essential: true },
      ],
    },
  },
  diag_demo_2: {
    id: "rec_demo_2",
    diagnosisId: "diag_demo_2",
    action: "REPAIR",
    repairScore: 96,
    replaceScore: 18,
    diagnosisReport: {
      id: "diag_demo_2",
      symptoms: "Loud fan noise under light load, keyboard warm to touch, thermal throttling.",
      probableIssue: "Thermal Interface Paste Breakdown & Dust Accumulation",
      confidenceScore: 89,
      repairDifficulty: "Easy",
      repairTime: "30-45 mins",
      repairCost: 45,
      imageUrl:
        "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=600&auto=format&fit=crop&q=80",
    },
    decision: {
      repairScore: 96,
      replaceScore: 18,
      recommendation: "REPAIR",
      moneySaved: 320,
      carbonSaved: 4.2,
      rationale:
        "Thermal maintenance is fast and highly effective. Cleaning dust fins and reapplying thermal paste restores 100% cooling performance for under $45.",
    },
    plan: {
      summary:
        "Thermal servicing plan involving bottom case removal, heatsink cleaning, and application of premium compound paste.",
      steps: [
        {
          stepNumber: 1,
          title: "Remove Bottom Case Screws",
          description: "Unscrew 10 Pentalobe screws securing bottom aluminum case.",
          estimatedMinutes: 5,
        },
        {
          stepNumber: 2,
          title: "Disconnect Battery Flex & Remove Heatsink",
          description:
            "Disconnect battery connector flex. Unscrew 4 heatsink spring screws and lift heatsink assembly.",
          safetyNote: "Ground yourself using ESD wrist strap before touching internal logic board.",
          estimatedMinutes: 10,
        },
        {
          stepNumber: 3,
          title: "Clean Old Compound & Reapply Thermal Paste",
          description:
            "Use isopropyl alcohol (99%) and lint-free wipe to clean dried paste. Apply pea-sized dot of fresh thermal compound.",
          estimatedMinutes: 15,
        },
        {
          stepNumber: 4,
          title: "Reassemble & Run Thermal Stress Test",
          description: "Reattach heatsink, reconnect battery, fasten bottom case, and run diagnostic benchmark.",
          estimatedMinutes: 10,
        },
      ],
      parts: [
        { name: "High Performance Thermal Paste Compound (4g)", quantity: 1, estimatedCost: 15, partNumber: "MX4-4G" },
        { name: "Isopropyl Alcohol Wipes (99%)", quantity: 1, estimatedCost: 5 },
      ],
      tools: [
        { name: "P5 Pentalobe & Torx Screwdriver", category: "Hand Tools", essential: true },
        { name: "ESD Anti-Static Wrist Strap", category: "Safety", essential: true },
        { name: "Compressed Air Duster", category: "Cleaning", essential: true },
      ],
    },
  },
};

// Default fallback recommendation when custom diagnosis ID is passed in demo mode
const DEFAULT_DEMO_RECOMMENDATION: RepairRecommendation = SAMPLE_RECOMMENDATIONS.diag_demo_1;

function RecommendationContent() {
  const searchParams = useSearchParams();
  const diagnosisId = searchParams.get("diagnosisId") || "diag_demo_1";

  const [recommendation, setRecommendation] = useState<RepairRecommendation | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isBackendOffline, setIsBackendOffline] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadData = async () => {
    setIsLoading(true);
    setErrorMessage(null);

    try {
      const response = await fetchRepairRecommendation(diagnosisId);

      if (response.success && response.data) {
        setIsBackendOffline(false);
        setRecommendation(response.data);
      } else {
        // Backend offline fallback — show sample demo recommendation cleanly
        setIsBackendOffline(true);
        const sample =
          SAMPLE_RECOMMENDATIONS[diagnosisId] || DEFAULT_DEMO_RECOMMENDATION;
        setRecommendation(sample);
      }
    } catch {
      setIsBackendOffline(true);
      const sample =
        SAMPLE_RECOMMENDATIONS[diagnosisId] || DEFAULT_DEMO_RECOMMENDATION;
      setRecommendation(sample);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [diagnosisId]);

  return (
    <div className="space-y-8">
      {/* Backend Status Indicator Header Banner */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 border-b border-white/[0.06] pb-6">
        <div>
          <div className="mb-2 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-3.5 py-1 text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
            <Wrench className="size-3.5" /> Phase 8 Repair Recommendation Engine
          </div>
          <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
            Actionable Repair Plan &{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Decision Matrix
            </span>
          </h1>
          <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
            Based on your AI visual diagnosis, here is the structured step-by-step repair guide, itemized parts, required tools, and Repair vs Replace recommendation.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <span
            className={`inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-xs font-semibold ${
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
                Live Recommendation API
              </>
            )}
          </span>

          <GlassButton
            variant="outline"
            size="sm"
            icon={<RefreshCw className="size-3.5" />}
            onClick={loadData}
            disabled={isLoading}
          >
            Refresh
          </GlassButton>
        </div>
      </div>

      {/* Backend Offline Warning Banner */}
      <AnimatePresence>
        {isBackendOffline && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="overflow-hidden border-b border-amber-500/20 bg-amber-500/[0.07] rounded-2xl p-4"
          >
            <div className="flex items-center gap-3 text-xs text-amber-300">
              <AlertTriangle className="size-4 shrink-0 text-amber-400" />
              <span>
                <strong>Sample Demo Recommendation — </strong>
                Spring Boot Recommendation server at <code>http://localhost:8080/api/v1</code> is currently offline. Results shown below are sample reference data for demonstration purposes.
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* LOADING STATE */}
      {isLoading && (
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
          <p className="text-sm font-semibold text-white">Compiling Repair Plan...</p>
          <p className="mt-1 text-xs text-white/50">
            Matching diagnosis signals against component manual database
          </p>
        </div>
      )}

      {/* ERROR STATE */}
      {errorMessage && !isLoading && (
        <div className="flex flex-col items-center justify-center rounded-3xl border border-red-500/20 bg-red-500/[0.05] py-16 text-center">
          <AlertTriangle className="size-10 text-red-400 mb-3" />
          <h2 className="text-base font-bold text-white">Failed to Load Recommendation</h2>
          <p className="mt-1 max-w-sm text-xs text-white/60">{errorMessage}</p>
          <GlassButton className="mt-6" variant="outline" size="sm" onClick={loadData}>
            Retry Request
          </GlassButton>
        </div>
      )}

      {/* MAIN RECOMMENDATION DASHBOARD VIEW */}
      {recommendation && !isLoading && (
        <div className="space-y-8">
          {/* Recommended Action Banner */}
          <RecommendedActionBanner
            action={recommendation.action}
            rationale={recommendation.decision.rationale}
            safetyWarning={recommendation.diagnosisReport?.safetyWarning}
          />

          {/* Diagnosis Summary Recap Card */}
          {recommendation.diagnosisReport && (
            <DiagnosisSummaryPanel report={recommendation.diagnosisReport} />
          )}

          {/* Repair vs Replace Decision Matrix */}
          <DecisionComparisonPanel decision={recommendation.decision} />

          {/* Itemized Parts & Tools Grid */}
          <PartsAndToolsGrid
            parts={recommendation.plan.parts}
            tools={recommendation.plan.tools}
          />

          {/* Structured Step-by-Step Repair Guide */}
          <RepairPlanTimeline
            summary={recommendation.plan.summary}
            steps={recommendation.plan.steps}
          />

          {/* Phase 9 CTA — Find Nearby Repair Shops */}
          <div className="rounded-3xl border border-[#06B6D4]/30 bg-[#06B6D4]/[0.06] p-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h3 className="text-base font-bold text-white">
                Need Professional Help?
              </h3>
              <p className="mt-1 text-xs text-[#CBD5E1]">
                Find certified technicians and repair shops near your location
                based on this repair specification.
              </p>
            </div>
            <GlassButton
              href={`/repair-shops?service=${encodeURIComponent(
                recommendation.diagnosisReport?.probableIssue
                  ? "Smartphone Repair"
                  : "All Services"
              )}`}
              icon={<MapPinIcon className="size-4" />}
              className="shrink-0"
            >
              Find Nearby Repair Shops
            </GlassButton>
          </div>
        </div>
      )}
    </div>
  );
}

export default function RecommendationPage() {
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
            <GlassButton href="/diagnosis" variant="secondary" size="sm" icon={<ArrowLeft className="size-3.5" />}>
              Back to Diagnosis
            </GlassButton>
          </div>
        </Container>
      </header>

      {/* Main Container */}
      <main className="relative py-8 sm:py-12">
        <Container>
          <Suspense
            fallback={
              <div className="flex flex-col items-center justify-center py-24 text-center">
                <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
                <p className="text-sm font-semibold text-white">Loading Recommendation Engine...</p>
              </div>
            }
          >
            <RecommendationContent />
          </Suspense>
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>&copy; {new Date().getFullYear()} RepairVerse AI. Recommendation Engine Dashboard.</p>
            <Link href="/" className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors">
              ← Back to RepairVerse AI
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
