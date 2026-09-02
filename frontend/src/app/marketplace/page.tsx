"use client";

import React, { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  ArrowLeft,
  ArrowRight,
  Award,
  BarChart3,
  CheckCircle2,
  ChevronRight,
  Cpu,
  FileText,
  Filter,
  Layers,
  MapPin,
  RefreshCw,
  Scale,
  Search,
  ShieldCheck,
  Sparkles,
  Store,
  Wrench,
  Zap,
} from "lucide-react";
import { useAuth } from "@/lib/context/AuthContext";
import { fetchUserDevices, SAMPLE_DEVICES } from "@/lib/api/devices";
import { Device } from "@/lib/types/device";
import {
  RepairShopMatchResponse,
  SmartRecommendationResponse,
  RepairMarketplaceComparison,
  QuoteIntelligenceResponse,
  UserMarketplaceInsights,
  PlatformMarketplaceAnalytics,
} from "@/lib/types/repairMatching";
import { repairMatchingApi } from "@/lib/api/repairMatching";
import SmartRepairMatches from "@/components/marketplace/SmartRepairMatches";
import RepairShopComparison from "@/components/marketplace/RepairShopComparison";
import QuoteIntelligenceCard from "@/components/marketplace/QuoteIntelligenceCard";
import MarketplaceAnalyticsOverview from "@/components/marketplace/MarketplaceAnalyticsOverview";
import { cn } from "@/lib/utils";

type ActiveTab = "MATCHING" | "COMPARISON" | "QUOTES" | "ANALYTICS";

export default function MarketplacePage() {
  const { user } = useAuth();

  // Devices & Selection
  const [devices, setDevices] = useState<Device[]>([]);
  const [selectedDevice, setSelectedDevice] = useState<Device | null>(null);
  const [repairType, setRepairType] = useState<string>("Battery & Power Diagnostic");

  // Active Tab & Sub-States
  const [activeTab, setActiveTab] = useState<ActiveTab>("MATCHING");
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Data states
  const [recommendations, setRecommendations] = useState<SmartRecommendationResponse | null>(null);
  const [selectedShopIds, setSelectedShopIds] = useState<string[]>([]);
  const [comparison, setComparison] = useState<RepairMarketplaceComparison | null>(null);
  const [activeQuoteId, setActiveQuoteId] = useState<string>("demo-quote-1");
  const [quoteIntelligence, setQuoteIntelligence] = useState<QuoteIntelligenceResponse | null>(null);
  const [userInsights, setUserInsights] = useState<UserMarketplaceInsights | null>(null);
  const [platformAnalytics, setPlatformAnalytics] = useState<PlatformMarketplaceAnalytics | null>(null);

  // Quote Request Modal / Confirmation
  const [quoteModalShop, setQuoteModalShop] = useState<RepairShopMatchResponse | null>(null);
  const [quoteSubmittedMessage, setQuoteSubmittedMessage] = useState<string | null>(null);

  // Load Initial Devices
  useEffect(() => {
    async function loadDevices() {
      try {
        const res = await fetchUserDevices();
        if (res.data && res.data.length > 0) {
          setDevices(res.data);
          setSelectedDevice(res.data[0]);
        } else {
          setDevices(SAMPLE_DEVICES);
          setSelectedDevice(SAMPLE_DEVICES[0]);
        }
      } catch {
        setDevices(SAMPLE_DEVICES);
        setSelectedDevice(SAMPLE_DEVICES[0]);
      }
    }
    loadDevices();
  }, []);

  // Fetch Smart Recommendations when selected device or repairType changes
  useEffect(() => {
    if (!selectedDevice) return;

    let isMounted = true;
    async function fetchMatches() {
      setLoading(true);
      setError(null);
      try {
        const res = await repairMatchingApi.getDeviceRecommendations(selectedDevice!.id, {
          repairType,
          lat: 37.7749,
          lng: -122.4194,
        });

        if (!isMounted) return;
        setRecommendations(res);

        // Pre-select top 2-3 shops for comparison
        if (res.topMatches && res.topMatches.length > 0) {
          const initialSelect = res.topMatches.slice(0, 3).map((s) => s.shopId);
          setSelectedShopIds(initialSelect);

          // Track interaction
          repairMatchingApi.trackInteraction({
            interactionType: "MATCH_SEARCHED",
            entityId: selectedDevice!.id,
            entityType: "DEVICE",
            metadata: JSON.stringify({ repairType, matchCount: res.topMatches.length }),
          });
        }
      } catch (err: any) {
        if (!isMounted) return;
        setError(err.message || "Failed to calculate smart repair matches");
      } finally {
        if (isMounted) setLoading(false);
      }
    }

    fetchMatches();
    return () => {
      isMounted = false;
    };
  }, [selectedDevice, repairType]);

  // Fetch Comparison when selected shops change or tab switched to COMPARISON
  useEffect(() => {
    if (selectedShopIds.length === 0) return;

    async function fetchComparison() {
      try {
        const comp = await repairMatchingApi.compareShops({
          shopIds: selectedShopIds,
          deviceId: selectedDevice?.id,
        });
        setComparison(comp);

        repairMatchingApi.trackInteraction({
          interactionType: "SHOP_COMPARED",
          entityId: selectedShopIds.join(","),
          entityType: "SHOP",
        });
      } catch (err) {
        console.error("Comparison load error:", err);
      }
    }

    fetchComparison();
  }, [selectedShopIds, selectedDevice]);

  // Fetch Analytics & Quote Intelligence
  useEffect(() => {
    async function fetchExtraData() {
      try {
        const [insights, pAnalytics, qIntel] = await Promise.all([
          repairMatchingApi.getUserMarketplaceAnalytics(),
          user?.role === "ADMIN" ? repairMatchingApi.getAdminMarketplaceAnalytics() : Promise.resolve(null),
          repairMatchingApi.getQuoteIntelligence(activeQuoteId),
        ]);

        setUserInsights(insights);
        if (pAnalytics) setPlatformAnalytics(pAnalytics);
        setQuoteIntelligence(qIntel);
      } catch (err) {
        console.error("Analytics fetch error:", err);
      }
    }
    fetchExtraData();
  }, [user, activeQuoteId]);

  // Handle toggling shop selection for comparison
  const handleToggleSelectShop = (shopId: string) => {
    setSelectedShopIds((prev) => {
      if (prev.includes(shopId)) {
        return prev.filter((id) => id !== shopId);
      } else {
        if (prev.length >= 4) {
          return [...prev.slice(1), shopId];
        }
        return [...prev, shopId];
      }
    });
  };

  const handleOpenQuoteModal = (shop: RepairShopMatchResponse) => {
    setQuoteModalShop(shop);
    setQuoteSubmittedMessage(null);
  };

  const handleConfirmQuoteRequest = () => {
    if (!quoteModalShop || !selectedDevice) return;

    repairMatchingApi.trackInteraction({
      interactionType: "QUOTE_REQUESTED",
      entityId: quoteModalShop.shopId,
      entityType: "SHOP",
      metadata: JSON.stringify({ deviceId: selectedDevice.id, cost: quoteModalShop.estimatedCost }),
    });

    setQuoteSubmittedMessage(`Quotation request sent to ${quoteModalShop.shopName}! Expect response within ${quoteModalShop.turnaroundHours}h.`);
    setTimeout(() => {
      setQuoteModalShop(null);
      setQuoteSubmittedMessage(null);
    }, 2500);
  };

  return (
    <div className="min-h-screen bg-[#070B14] text-white p-4 sm:p-6 lg:p-10 space-y-8">
      {/* Top Banner / Hero */}
      <div className="relative overflow-hidden rounded-3xl border border-white/10 bg-gradient-to-r from-[#0B1120] via-[#111C33] to-[#0B1120] p-6 sm:p-10 shadow-2xl backdrop-blur-2xl">
        <div className="pointer-events-none absolute -right-10 -top-10 size-96 rounded-full bg-[#22C55E]/10 blur-3xl" />
        <div className="pointer-events-none absolute -left-10 -bottom-10 size-96 rounded-full bg-[#06B6D4]/10 blur-3xl" />

        <div className="relative z-10 flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
          <div className="space-y-3 max-w-2xl">
            <div className="flex items-center gap-2">
              <span className="flex items-center gap-1.5 rounded-full border border-[#22C55E]/40 bg-[#22C55E]/15 px-3 py-1 text-xs font-bold uppercase tracking-wider text-[#22C55E]">
                <Sparkles className="size-3.5" />
                Intelligent Marketplace Engine
              </span>
              <span className="rounded-full border border-white/10 bg-white/5 px-3 py-1 text-xs font-semibold text-white/60">
                Phase 27
              </span>
            </div>

            <h1 className="text-3xl sm:text-4xl font-black tracking-tight text-white">
              Smart Repair Matching & Quotes
            </h1>
            <p className="text-sm sm:text-base text-white/70 leading-relaxed">
              Connect your specific device and diagnosed fault with the highest-ranked certified repair providers using deterministic 0–100 compatibility scoring.
            </p>
          </div>

          {/* Quick Stats Pill */}
          <div className="flex flex-wrap items-center gap-3">
            <div className="rounded-2xl border border-white/10 bg-white/[0.04] p-4 text-center min-w-[120px]">
              <span className="text-[10px] font-bold uppercase text-white/40">Evaluated</span>
              <p className="text-2xl font-black text-white">{recommendations?.totalEvaluated || 4} Shops</p>
            </div>
            <div className="rounded-2xl border border-[#22C55E]/30 bg-[#22C55E]/10 p-4 text-center min-w-[120px]">
              <span className="text-[10px] font-bold uppercase text-[#22C55E]">Top Match Score</span>
              <p className="text-2xl font-black text-[#22C55E]">
                {recommendations?.topMatches?.[0]?.overallScore || 96}/100
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Step 1: Device & Fault Context Selector */}
      <div className="rounded-3xl border border-white/10 bg-[#0B1120]/80 p-6 backdrop-blur-xl space-y-4">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex items-center gap-2">
            <div className="flex size-8 items-center justify-center rounded-xl bg-[#22C55E]/20 text-[#22C55E] font-black text-sm">
              1
            </div>
            <div>
              <h2 className="text-base font-bold text-white">Select Device & Repair Focus</h2>
              <p className="text-xs text-white/50">Target matching specifically to your hardware architecture</p>
            </div>
          </div>

          {/* Fault Category Selector */}
          <div className="flex items-center gap-2">
            <span className="text-xs text-white/50">Fault Focus:</span>
            <select
              value={repairType}
              onChange={(e) => setRepairType(e.target.value)}
              className="rounded-xl border border-white/15 bg-[#111C33] px-3 py-1.5 text-xs font-semibold text-white focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            >
              <option value="Battery & Power Diagnostic">Battery & Power Renewal</option>
              <option value="Screen & OLED Assembly">Screen & Display Assembly</option>
              <option value="Logic Board / Micro-soldering">Logic Board / Micro-soldering</option>
              <option value="Liquid Damage Restoration">Liquid Damage Cleaning</option>
              <option value="Camera & Sensor Modular Fix">Camera & Sensor Modular Fix</option>
            </select>
          </div>
        </div>

        {/* Device selection chips */}
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-4 pt-2">
          {devices.map((dev) => {
            const isSelected = selectedDevice?.id === dev.id;
            return (
              <button
                key={dev.id}
                onClick={() => setSelectedDevice(dev)}
                className={cn(
                  "flex items-center gap-3 rounded-2xl border p-3.5 text-left transition-all",
                  isSelected
                    ? "border-[#22C55E] bg-gradient-to-r from-[#22C55E]/20 to-[#06B6D4]/10 shadow-[0_0_20px_rgba(34,197,94,0.15)]"
                    : "border-white/10 bg-white/[0.02] hover:border-white/20 hover:bg-white/[0.05]"
                )}
              >
                <div
                  className={cn(
                    "flex size-10 shrink-0 items-center justify-center rounded-xl",
                    isSelected ? "bg-[#22C55E] text-black" : "bg-white/10 text-white"
                  )}
                >
                  <Cpu className="size-5" />
                </div>
                <div className="min-w-0 flex-1">
                  <p className="truncate text-sm font-bold text-white">{dev.deviceName}</p>
                  <p className="truncate text-xs text-white/50">
                    {dev.brand} • {dev.category}
                  </p>
                </div>
                {isSelected && (
                  <CheckCircle2 className="size-4 shrink-0 text-[#22C55E]" />
                )}
              </button>
            );
          })}
        </div>
      </div>

      {/* Main Tabs Navigation */}
      <div className="flex flex-wrap items-center justify-between gap-4 border-b border-white/10 pb-4">
        <div className="flex items-center gap-2">
          <button
            onClick={() => setActiveTab("MATCHING")}
            className={cn(
              "flex items-center gap-2 rounded-2xl px-5 py-2.5 text-sm font-bold transition-all",
              activeTab === "MATCHING"
                ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-lg shadow-[#22C55E]/20"
                : "border border-white/10 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white"
            )}
          >
            <Sparkles className="size-4" />
            Smart Matches ({recommendations?.topMatches?.length || 0})
          </button>

          <button
            onClick={() => setActiveTab("COMPARISON")}
            className={cn(
              "flex items-center gap-2 rounded-2xl px-5 py-2.5 text-sm font-bold transition-all",
              activeTab === "COMPARISON"
                ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-lg shadow-[#22C55E]/20"
                : "border border-white/10 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white"
            )}
          >
            <Scale className="size-4" />
            Shop Comparison ({selectedShopIds.length})
          </button>

          <button
            onClick={() => setActiveTab("QUOTES")}
            className={cn(
              "flex items-center gap-2 rounded-2xl px-5 py-2.5 text-sm font-bold transition-all",
              activeTab === "QUOTES"
                ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-lg shadow-[#22C55E]/20"
                : "border border-white/10 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white"
            )}
          >
            <FileText className="size-4" />
            Quote Intelligence
          </button>

          <button
            onClick={() => setActiveTab("ANALYTICS")}
            className={cn(
              "flex items-center gap-2 rounded-2xl px-5 py-2.5 text-sm font-bold transition-all",
              activeTab === "ANALYTICS"
                ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-lg shadow-[#22C55E]/20"
                : "border border-white/10 bg-white/5 text-white/70 hover:bg-white/10 hover:text-white"
            )}
          >
            <BarChart3 className="size-4" />
            Insights & Analytics
          </button>
        </div>

        {selectedShopIds.length > 0 && activeTab === "MATCHING" && (
          <button
            onClick={() => setActiveTab("COMPARISON")}
            className="flex items-center gap-1.5 rounded-xl border border-cyan-500/40 bg-cyan-500/15 px-3 py-1.5 text-xs font-bold text-cyan-400 hover:bg-cyan-500/25 transition-all"
          >
            <span>Compare {selectedShopIds.length} Selected</span>
            <ArrowRight className="size-3.5" />
          </button>
        )}
      </div>

      {/* Main Tab Content */}
      <AnimatePresence mode="wait">
        {loading ? (
          <div className="flex flex-col items-center justify-center p-16 space-y-4">
            <RefreshCw className="size-10 text-[#22C55E] animate-spin" />
            <p className="text-sm font-semibold text-white/70">
              Evaluating deterministic compatibility factors across certified repair providers...
            </p>
          </div>
        ) : error ? (
          <div className="rounded-3xl border border-red-500/30 bg-red-500/10 p-8 text-center">
            <p className="text-red-300">{error}</p>
          </div>
        ) : (
          <div>
            {/* TAB 1: SMART MATCHES */}
            {activeTab === "MATCHING" && recommendations && (
              <SmartRepairMatches
                matches={recommendations.topMatches}
                recommendations={recommendations.recommendations}
                selectedShopIds={selectedShopIds}
                onToggleSelectShop={handleToggleSelectShop}
                onRequestQuote={handleOpenQuoteModal}
              />
            )}

            {/* TAB 2: SHOP COMPARISON */}
            {activeTab === "COMPARISON" && (
              <RepairShopComparison
                comparison={comparison}
                onRemoveShop={(shopId) =>
                  setSelectedShopIds((prev) => prev.filter((id) => id !== shopId))
                }
                onRequestQuote={handleOpenQuoteModal}
              />
            )}

            {/* TAB 3: QUOTE INTELLIGENCE */}
            {activeTab === "QUOTES" && (
              <QuoteIntelligenceCard
                intelligence={quoteIntelligence}
                onAccept={() => {
                  alert("Quotation accepted! Proceeding to Repair Journey booking...");
                }}
                onReject={() => {
                  alert("Quotation declined.");
                }}
              />
            )}

            {/* TAB 4: MARKETPLACE ANALYTICS */}
            {activeTab === "ANALYTICS" && (
              <MarketplaceAnalyticsOverview
                userInsights={userInsights}
                platformAnalytics={platformAnalytics}
                isAdmin={user?.role === "ADMIN"}
              />
            )}
          </div>
        )}
      </AnimatePresence>

      {/* Quick Quote Request Modal */}
      <AnimatePresence>
        {quoteModalShop && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-md">
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="w-full max-w-lg rounded-3xl border border-white/15 bg-[#0B1120] p-6 shadow-2xl space-y-6"
            >
              <div className="flex items-start justify-between border-b border-white/10 pb-4">
                <div>
                  <span className="text-xs font-bold uppercase tracking-wider text-[#22C55E]">
                    Request Official Quotation
                  </span>
                  <h3 className="text-xl font-extrabold text-white">{quoteModalShop.shopName}</h3>
                </div>
                <button
                  onClick={() => setQuoteModalShop(null)}
                  className="rounded-xl p-1.5 text-white/50 hover:bg-white/10 hover:text-white"
                >
                  ✕
                </button>
              </div>

              {quoteSubmittedMessage ? (
                <div className="rounded-2xl border border-[#22C55E]/40 bg-[#22C55E]/15 p-6 text-center space-y-2">
                  <CheckCircle2 className="mx-auto size-10 text-[#22C55E]" />
                  <p className="text-base font-bold text-white">{quoteSubmittedMessage}</p>
                </div>
              ) : (
                <div className="space-y-4">
                  <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-4 space-y-2 text-sm">
                    <div className="flex justify-between">
                      <span className="text-white/50">Device:</span>
                      <strong className="text-white">{selectedDevice?.deviceName}</strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-white/50">Fault Scope:</span>
                      <strong className="text-white">{repairType}</strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-white/50">Est. Baseline Price:</span>
                      <strong className="text-[#22C55E]">${quoteModalShop.estimatedCost.toFixed(0)}</strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-white/50">Turnaround Guarantee:</span>
                      <strong className="text-[#06B6D4]">
                        {quoteModalShop.turnaroundHours <= 6 ? "Express (Same Day)" : `${quoteModalShop.turnaroundHours}h`}
                      </strong>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-white/50">Warranty:</span>
                      <strong className="text-white">{quoteModalShop.warrantyDays} Days</strong>
                    </div>
                  </div>

                  <p className="text-xs text-white/60">
                    Submitting this inquiry initiates a formal quotation with locked-in parts pricing and priority bench scheduling.
                  </p>

                  <div className="flex items-center justify-end gap-3 pt-2">
                    <button
                      onClick={() => setQuoteModalShop(null)}
                      className="rounded-xl border border-white/15 bg-white/5 px-4 py-2.5 text-xs font-semibold text-white/70 hover:bg-white/10"
                    >
                      Cancel
                    </button>
                    <button
                      onClick={handleConfirmQuoteRequest}
                      className="rounded-xl bg-gradient-to-r from-[#22C55E] to-[#06B6D4] px-5 py-2.5 text-xs font-bold text-white shadow-lg shadow-[#22C55E]/20 hover:opacity-95"
                    >
                      Confirm Quote Request
                    </button>
                  </div>
                </div>
              )}
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
