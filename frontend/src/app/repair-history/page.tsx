"use client";

import { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  Wrench,
  Search,
  Loader2,
  AlertTriangle,
  RefreshCw,
  WifiOff,
  Wifi,
  Sparkles,
  Smartphone,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import RepairHistoryStats from "@/components/history/RepairHistoryStats";
import RepairHistoryFilters from "@/components/history/RepairHistoryFilters";
import RepairHistoryCard from "@/components/history/RepairHistoryCard";

import {
  fetchRepairHistory,
  computeRepairHistorySummary,
} from "@/lib/api/repairHistory";
import { RepairHistoryItem, RepairHistorySummary } from "@/lib/types/repairHistory";

const EASE = [0.22, 1, 0.36, 1] as const;

type SortOption = "newest" | "oldest" | "highestCost" | "lowestCost";

function RepairHistoryContent() {
  const [items, setItems] = useState<RepairHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isBackendOffline, setIsBackendOffline] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedStatus, setSelectedStatus] = useState("All");
  const [selectedCategory, setSelectedCategory] = useState("All Categories");
  const [selectedSort, setSelectedSort] = useState<SortOption>("newest");

  const loadData = async () => {
    setIsLoading(true);
    const res = await fetchRepairHistory();

    if (res.success && res.data) {
      setItems(res.data);
      // isDemo flag tells us if we're in sample/offline mode
      setIsBackendOffline(res.isDemo !== false);
    } else {
      // If fetch failed entirely, assume demo mode but with empty data
      setIsBackendOffline(true);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const categories = [
    "All Categories",
    ...Array.from(new Set(items.map((i) => i.device.category))),
  ];

  const filteredItems = items
    .filter((item) => {
      const q = searchQuery.toLowerCase();
      const matchesSearch =
        item.device.name.toLowerCase().includes(q) ||
        item.device.brand.toLowerCase().includes(q) ||
        item.device.model.toLowerCase().includes(q) ||
        item.repairType.toLowerCase().includes(q) ||
        (item.diagnosisIssue && item.diagnosisIssue.toLowerCase().includes(q)) ||
        (item.technician && item.technician.name.toLowerCase().includes(q)) ||
        (item.shop && item.shop.name.toLowerCase().includes(q));

      const matchesStatus =
        selectedStatus === "All" || item.status === selectedStatus;

      const matchesCategory =
        selectedCategory === "All Categories" ||
        item.device.category.toLowerCase() === selectedCategory.toLowerCase();

      return matchesSearch && matchesStatus && matchesCategory;
    })
    .sort((a, b) => {
      if (selectedSort === "newest") {
        return new Date(b.repairDate).getTime() - new Date(a.repairDate).getTime();
      }
      if (selectedSort === "oldest") {
        return new Date(a.repairDate).getTime() - new Date(b.repairDate).getTime();
      }
      if (selectedSort === "highestCost") {
        return b.totalCost - a.totalCost;
      }
      return a.totalCost - b.totalCost;
    });

  const summary: RepairHistorySummary = computeRepairHistorySummary(items);

  return (
    <div className="space-y-6">
      {/* Header Bar */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, ease: EASE }}
        className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between border-b border-white/[0.06] pb-6"
      >
        <div>
          <div className="mb-2 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-3.5 py-1 text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
            <Wrench className="size-3.5" /> Maintenance & Repair Intelligence
          </div>
          <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
            Smart Repair{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              History & Log
            </span>
          </h1>
          <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
            Track complete device repair records, itemized parts costs, technician logs, warranty coverages, and cumulative carbon savings.
          </p>
        </div>

        <div className="flex items-center gap-3 shrink-0">
          <span
            className={`inline-flex items-center gap-1.5 rounded-full border px-3 py-1 text-[11px] font-semibold ${
              isBackendOffline
                ? "border-amber-500/30 bg-amber-500/10 text-amber-400"
                : "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
            }`}
          >
            {isBackendOffline ? (
              <>
                <WifiOff className="size-3" /> Demo Mode
              </>
            ) : (
              <>
                <Wifi className="size-3" /> Live Data
              </>
            )}
          </span>

          <GlassButton
            size="sm"
            variant="secondary"
            icon={<RefreshCw className="size-3.5" />}
            onClick={loadData}
            disabled={isLoading}
          >
            Refresh
          </GlassButton>
        </div>
      </motion.div>

      {/* Demo Banner */}
      <AnimatePresence>
        {isBackendOffline && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3, ease: EASE }}
            className="overflow-hidden rounded-2xl border border-amber-500/20 bg-amber-500/[0.07] px-4 py-3"
          >
            <div className="flex items-center gap-2.5 text-xs text-amber-300">
              <AlertTriangle className="size-4 shrink-0 text-amber-400" />
              <span>
                <strong>Sample Reference Mode &mdash; </strong>
                Displaying sample repair records across multiple hardware categories. Connect the Spring Boot API at <code>http://localhost:8080/api/v1</code> to query live repair history.
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Metric Counters */}
      <RepairHistoryStats summary={summary} />

      {/* Filter Bar */}
      <RepairHistoryFilters
        searchQuery={searchQuery}
        onSearchChange={setSearchQuery}
        selectedStatus={selectedStatus}
        onStatusChange={setSelectedStatus}
        selectedCategory={selectedCategory}
        onCategoryChange={setSelectedCategory}
        selectedSort={selectedSort}
        onSortChange={setSelectedSort}
        categories={categories}
      />

      {/* Main Repair History Grid */}
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <Loader2 className="size-10 text-[#22C55E] animate-spin mb-3" />
          <p className="text-sm font-semibold text-white">
            Loading Repair History Logs…
          </p>
        </div>
      ) : filteredItems.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-white/10 py-16 text-center px-6">
          <Search className="size-12 text-white/20 mb-3" />
          <h2 className="text-base font-bold text-white">
            No Repair Records Found
          </h2>
          <p className="mt-1 text-xs text-[#CBD5E1] max-w-sm">
            No repair logs matched your search or status filter. Try clearing filters or running a new device diagnosis.
          </p>
          <div className="mt-5 flex gap-3">
            <GlassButton
              size="sm"
              variant="outline"
              onClick={() => {
                setSearchQuery("");
                setSelectedStatus("All");
                setSelectedCategory("All Categories");
              }}
            >
              Reset Filters
            </GlassButton>
            <GlassButton
              href="/diagnosis"
              size="sm"
              icon={<Sparkles className="size-3.5" />}
            >
              Diagnose Device
            </GlassButton>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-2">
          {filteredItems.map((item, idx) => (
            <RepairHistoryCard
              key={item.id}
              item={item}
              isDemo={isBackendOffline}
              delay={idx * 0.05}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function RepairHistoryPage() {
  return (
    <div className="relative min-h-screen bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none fixed inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.10),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.10),transparent_50%)]"
        aria-hidden
      />

      {/* Header Navigation */}
      <header className="sticky top-0 z-40 border-b border-white/10 bg-[#0B1120]/80 backdrop-blur-xl">
        <Container className="flex h-16 items-center justify-between gap-4">
          <Logo size="sm" href="/" />
          <div className="flex items-center gap-3">
            <GlassButton
              href="/devices"
              variant="secondary"
              size="sm"
              icon={<Smartphone className="size-3.5" />}
            >
              My Devices
            </GlassButton>
            <GlassButton
              href="/diagnosis"
              size="sm"
              icon={<Sparkles className="size-3.5" />}
            >
              AI Diagnosis
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
                <p className="text-sm font-semibold text-white">
                  Loading Smart Repair History Dashboard…
                </p>
              </div>
            }
          >
            <RepairHistoryContent />
          </Suspense>
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>
              &copy; {new Date().getFullYear()} RepairVerse AI. Smart Repair History.
            </p>
            <Link
              href="/"
              className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors"
            >
              &larr; Back to RepairVerse AI Home
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
