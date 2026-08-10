"use client";

import { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import { motion, AnimatePresence } from "framer-motion";
import {
  Smartphone,
  Plus,
  Search,
  Filter,
  Loader2,
  ShieldCheck,
  Activity,
  AlertTriangle,
  ArrowLeft,
  Wifi,
  WifiOff,
  Sparkles,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import DeviceCard from "@/components/devices/DeviceCard";
import AddDeviceModal from "@/components/devices/AddDeviceModal";

import { fetchUserDevices, createDevice } from "@/lib/api/devices";
import { CreateDeviceRequest, Device } from "@/lib/types/device";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

function DevicesContent() {
  const [devices, setDevices] = useState<Device[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isBackendOffline, setIsBackendOffline] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("All Categories");
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const loadDevices = async () => {
    setIsLoading(true);
    const res = await fetchUserDevices();

    if (res.success && res.data) {
      setDevices(res.data);
      // Check if data equals sample reference data to label offline demo mode
      setIsBackendOffline(true);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    loadDevices();
  }, []);

  const handleAddDevice = async (newDev: CreateDeviceRequest): Promise<boolean> => {
    const res = await createDevice(newDev);
    if (res.success && res.data) {
      setDevices((prev) => [res.data!, ...prev]);
      return true;
    }
    return false;
  };

  const categories = [
    "All Categories",
    "Smartphone",
    "Laptop",
    "Tablet",
    "Gaming Console",
    "Smartwatch",
    "Audio Device",
    "Other",
  ];

  const filteredDevices = devices.filter((d) => {
    const matchesSearch =
      d.deviceName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.brand.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.model.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesCategory =
      categoryFilter === "All Categories" ||
      d.category.toLowerCase() === categoryFilter.toLowerCase();

    return matchesSearch && matchesCategory;
  });

  const avgHealthScore = Math.round(
    devices.length > 0
      ? devices.reduce((acc, curr) => {
          const score =
            curr.currentCondition === "Excellent"
              ? 94
              : curr.currentCondition === "Good"
              ? 86
              : curr.currentCondition === "Fair"
              ? 72
              : 58;
          return acc + score;
        }, 0) / devices.length
      : 0
  );

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
            <ShieldCheck className="size-3.5" /> Digital Passport Registry
          </div>
          <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
            My Electronic{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Device Health Passports
            </span>
          </h1>
          <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
            Track condition ratings, digital specifications, repairability history, and lifespan AI metrics for your registered hardware.
          </p>
        </div>

        <div className="flex items-center gap-3 shrink-0">
          <GlassButton
            onClick={() => setIsAddModalOpen(true)}
            size="sm"
            icon={<Plus className="size-4" />}
          >
            Add New Device
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
                <strong>Sample Demo Mode &mdash; </strong>
                Showing reference device records. You can add new devices or inspect sample health passports. Connect Spring Boot at <code>http://localhost:8080/api/v1</code> for live database persistence.
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Metric Counters Grid */}
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <GlassCard padding="md" hoverEffect={false}>
          <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-2xl bg-[#22C55E]/20 text-[#22C55E]">
              <Smartphone className="size-5" />
            </div>
            <div>
              <div className="text-xs text-white/50 font-semibold">Registered Devices</div>
              <div className="text-xl font-bold text-white">{devices.length}</div>
            </div>
          </div>
        </GlassCard>

        <GlassCard padding="md" hoverEffect={false}>
          <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-2xl bg-[#06B6D4]/20 text-[#06B6D4]">
              <Activity className="size-5" />
            </div>
            <div>
              <div className="text-xs text-white/50 font-semibold">Avg Device Health Score</div>
              <div className="text-xl font-bold text-[#22C55E]">{avgHealthScore}/100</div>
            </div>
          </div>
        </GlassCard>

        <GlassCard padding="md" hoverEffect={false}>
          <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-2xl bg-amber-500/20 text-amber-400">
              <ShieldCheck className="size-5" />
            </div>
            <div>
              <div className="text-xs text-white/50 font-semibold">Passport Status</div>
              <div className="text-xl font-bold text-white">Active Registry</div>
            </div>
          </div>
        </GlassCard>
      </div>

      {/* Search & Filter Controls */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="relative flex-1 min-w-[220px]">
          <Search className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-4 text-white/40" />
          <input
            type="text"
            placeholder="Search by device name, brand, or model..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full rounded-2xl border border-white/15 bg-white/[0.05] py-2 pl-9 pr-4 text-xs font-semibold text-white placeholder-white/40 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
          />
        </div>

        <div className="relative">
          <Filter className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-white/40" />
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="appearance-none rounded-2xl border border-white/15 bg-[#0B1120] py-2 pl-9 pr-8 text-xs font-semibold text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Filter devices by category"
          >
            {categories.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Main Devices Grid */}
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-20 text-center">
          <Loader2 className="size-10 text-[#22C55E] animate-spin mb-3" />
          <p className="text-sm font-semibold text-white">
            Loading Device Health Passports…
          </p>
        </div>
      ) : filteredDevices.length === 0 ? (
        <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-white/10 py-16 text-center px-6">
          <Smartphone className="size-12 text-white/20 mb-3" />
          <h2 className="text-base font-bold text-white">No Devices Found</h2>
          <p className="mt-1 text-xs text-[#CBD5E1] max-w-sm">
            No registered devices matched your search or category filter. Add a new device to create its Health Passport.
          </p>
          <GlassButton
            className="mt-5"
            size="sm"
            onClick={() => setIsAddModalOpen(true)}
            icon={<Plus className="size-4" />}
          >
            Add Your First Device
          </GlassButton>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filteredDevices.map((dev, idx) => {
            const score =
              dev.currentCondition === "Excellent"
                ? 94
                : dev.currentCondition === "Good"
                ? 86
                : dev.currentCondition === "Fair"
                ? 72
                : 58;

            return (
              <DeviceCard
                key={dev.id}
                device={dev}
                healthScore={score}
                isDemo={isBackendOffline}
                delay={idx * 0.05}
              />
            );
          })}
        </div>
      )}

      {/* Add Device Modal Dialog */}
      <AddDeviceModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onAddDevice={handleAddDevice}
      />
    </div>
  );
}

export default function DevicesPage() {
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
            <GlassButton
              href="/recommendation"
              variant="secondary"
              size="sm"
              icon={<ArrowLeft className="size-3.5" />}
            >
              Repair Plan
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

      {/* Main */}
      <main className="relative py-8 sm:py-12">
        <Container>
          <Suspense
            fallback={
              <div className="flex flex-col items-center justify-center py-24 text-center">
                <Loader2 className="size-12 text-[#22C55E] animate-spin mb-4" />
                <p className="text-sm font-semibold text-white">
                  Loading Device Health Registry…
                </p>
              </div>
            }
          >
            <DevicesContent />
          </Suspense>
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>
              &copy; {new Date().getFullYear()} RepairVerse AI. Device Health Passport Registry.
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
