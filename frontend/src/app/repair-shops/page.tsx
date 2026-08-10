"use client";

import { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertTriangle,
  ArrowLeft,
  Filter,
  LayoutList,
  Loader2,
  Map as MapIcon,
  MapPin,
  RefreshCw,
  Search,
  Star,
  Wifi,
  WifiOff,
  Wrench,
} from "lucide-react";

import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import ShopCard from "@/components/shops/ShopCard";
import MapPanel from "@/components/shops/MapPanel";

import { fetchNearbyShops, fetchShopDetail } from "@/lib/api/repairShops";
import type { RepairShop } from "@/lib/types/repairShops";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

// ─── Demo shops — clearly labeled as SAMPLE, NOT real businesses ──────────────
const SAMPLE_SHOPS: RepairShop[] = [
  {
    id: "sample_1",
    shopName: "Sample Electronics Repair Centre",
    ownerName: "Demo Technician A",
    address: "42 MG Road, Bengaluru, Karnataka 560001",
    latitude: 12.9751,
    longitude: 77.6099,
    rating: 4.8,
    reviewCount: 142,
    phone: "+91-0000-000001",
    serviceCategories: ["Smartphone Repair", "Laptop Repair", "Tablet Repair"],
    isVerified: true,
    isOpen: true,
    distanceKm: 0.6,
  },
  {
    id: "sample_2",
    shopName: "Sample Tech Fix Studio",
    ownerName: "Demo Technician B",
    address: "18 Brigade Road, Bengaluru, Karnataka 560025",
    latitude: 12.9716,
    longitude: 77.6045,
    rating: 4.5,
    reviewCount: 89,
    phone: "+91-0000-000002",
    serviceCategories: ["Gaming Console Repair", "Audio/Headphone Repair"],
    isVerified: false,
    isOpen: true,
    distanceKm: 1.2,
  },
  {
    id: "sample_3",
    shopName: "Sample Device Hospital",
    ownerName: "Demo Technician C",
    address: "7 Commercial Street, Bengaluru, Karnataka 560001",
    latitude: 12.9798,
    longitude: 77.6076,
    rating: 4.2,
    reviewCount: 61,
    serviceCategories: ["Smartphone Repair", "Smartwatch Repair"],
    isVerified: true,
    isOpen: false,
    distanceKm: 2.1,
  },
  {
    id: "sample_4",
    shopName: "Sample Circuit Menders",
    ownerName: "Demo Technician D",
    address: "33 Residency Road, Bengaluru, Karnataka 560025",
    latitude: 12.9687,
    longitude: 77.602,
    rating: 4.6,
    reviewCount: 113,
    phone: "+91-0000-000004",
    serviceCategories: ["Laptop Repair", "PC Repair", "Tablet Repair"],
    isVerified: true,
    isOpen: true,
    distanceKm: 2.8,
  },
];

const SERVICE_CATEGORIES = [
  "All Services",
  "Smartphone Repair",
  "Laptop Repair",
  "Gaming Console Repair",
  "Tablet Repair",
  "Audio/Headphone Repair",
  "Smartwatch Repair",
];

type LocationState =
  | { status: "idle" }
  | { status: "requesting" }
  | { status: "granted"; lat: number; lng: number }
  | { status: "denied" }
  | { status: "unavailable" }
  | { status: "unsupported" };

type ViewMode = "list" | "map";
type SortMode = "nearest" | "rating";

function RepairShopsContent() {
  const searchParams = useSearchParams();
  const serviceParam = searchParams.get("service") || "";

  const [locationState, setLocationState] = useState<LocationState>({
    status: "idle",
  });
  const [shops, setShops] = useState<RepairShop[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isBackendOffline, setIsBackendOffline] = useState(true);
  const [selectedShop, setSelectedShop] = useState<RepairShop | null>(null);
  const [viewMode, setViewMode] = useState<ViewMode>("list");
  const [serviceFilter, setServiceFilter] = useState(
    serviceParam || "All Services"
  );
  const [sortMode, setSortMode] = useState<SortMode>("nearest");
  const [radiusKm, setRadiusKm] = useState(5);

  const handleSelectShop = async (shop: RepairShop) => {
    setSelectedShop(shop);
    if (!isBackendOffline && shop.id && !shop.id.startsWith("sample_")) {
      const res = await fetchShopDetail(shop.id);
      if (res.success && res.data) {
        setSelectedShop(res.data);
      }
    }
  };

  const requestLocation = () => {
    if (!navigator.geolocation) {
      setLocationState({ status: "unsupported" });
      return;
    }
    setLocationState({ status: "requesting" });
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        setLocationState({
          status: "granted",
          lat: pos.coords.latitude,
          lng: pos.coords.longitude,
        });
      },
      (err) => {
        if (err.code === err.PERMISSION_DENIED) {
          setLocationState({ status: "denied" });
        } else {
          setLocationState({ status: "unavailable" });
        }
      },
      { timeout: 8000, maximumAge: 60000 }
    );
  };

  const loadShops = async () => {
    setIsLoading(true);
    const lat =
      locationState.status === "granted" ? locationState.lat : undefined;
    const lng =
      locationState.status === "granted" ? locationState.lng : undefined;

    const response = await fetchNearbyShops({
      latitude: lat,
      longitude: lng,
      radiusKm,
      serviceCategory:
        serviceFilter !== "All Services" ? serviceFilter : undefined,
    });

    if (response.success && response.data && response.data.length > 0) {
      setIsBackendOffline(false);
      setShops(response.data);
    } else {
      setIsBackendOffline(true);
      setShops(SAMPLE_SHOPS);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    loadShops();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [locationState.status, serviceFilter, radiusKm]);

  // Apply client-side filter & sort on top of fetched list
  const filteredShops = shops
    .filter(
      (s) =>
        serviceFilter === "All Services" ||
        (s.serviceCategories || []).some((cat) =>
          cat.toLowerCase().includes(serviceFilter.toLowerCase())
        )
    )
    .sort((a, b) => {
      if (sortMode === "nearest") {
        return (a.distanceKm ?? 99) - (b.distanceKm ?? 99);
      }
      return b.rating - a.rating;
    });

  const userLat =
    locationState.status === "granted" ? locationState.lat : undefined;
  const userLng =
    locationState.status === "granted" ? locationState.lng : undefined;

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.45, ease: EASE }}
        className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between border-b border-white/[0.06] pb-6"
      >
        <div>
          <div className="mb-2 inline-flex items-center gap-2 rounded-full border border-[#22C55E]/25 bg-[#22C55E]/10 px-3.5 py-1 text-xs font-semibold uppercase tracking-wider text-[#22C55E]">
            <MapPin className="size-3.5" /> Nearby Repair Shops
          </div>
          <h1 className="text-2xl font-bold text-white sm:text-3xl lg:text-4xl">
            Find a Trusted{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Repair Professional
            </span>
          </h1>
          <p className="mt-2 max-w-xl text-sm text-[#CBD5E1]">
            Locate certified repair centres and technicians near you based on your
            device diagnosis and repair requirements.
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
                <strong>Sample Demo Mode — </strong>
                Shops listed below are fictional reference data only and are NOT real
                businesses. Connect the Spring Boot API at{" "}
                <code>http://localhost:8080/api/v1</code> to display live results.
              </span>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Location Status Bar */}
      <GlassCard padding="md" hoverEffect={false}>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2.5">
            <MapPin className="size-5 text-[#22C55E]" />
            <div>
              {locationState.status === "idle" && (
                <p className="text-sm font-semibold text-white">
                  Enable location to find shops near you
                </p>
              )}
              {locationState.status === "requesting" && (
                <p className="text-sm font-semibold text-white flex items-center gap-2">
                  <Loader2 className="size-4 animate-spin text-[#22C55E]" />
                  Requesting location...
                </p>
              )}
              {locationState.status === "granted" && (
                <p className="text-sm font-semibold text-[#22C55E]">
                  ✓ Location active — showing shops within {radiusKm} km
                </p>
              )}
              {locationState.status === "denied" && (
                <p className="text-sm font-semibold text-red-400">
                  Location access denied. Showing demo results.
                </p>
              )}
              {locationState.status === "unavailable" && (
                <p className="text-sm font-semibold text-amber-400">
                  Location unavailable. Showing demo results.
                </p>
              )}
              {locationState.status === "unsupported" && (
                <p className="text-sm font-semibold text-red-400">
                  Geolocation not supported in this browser.
                </p>
              )}
            </div>
          </div>

          <div className="flex flex-wrap gap-2">
            {(locationState.status === "idle" ||
              locationState.status === "denied" ||
              locationState.status === "unavailable") && (
              <GlassButton
                size="sm"
                variant="outline"
                icon={<MapPin className="size-3.5" />}
                onClick={requestLocation}
              >
                Use My Location
              </GlassButton>
            )}
            <GlassButton
              size="sm"
              variant="secondary"
              icon={<RefreshCw className="size-3.5" />}
              onClick={loadShops}
              disabled={isLoading}
            >
              Refresh
            </GlassButton>
          </div>
        </div>
      </GlassCard>

      {/* Filters + Sort Bar */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Service filter */}
        <div className="relative">
          <Filter className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-white/40" />
          <select
            id="serviceFilter"
            value={serviceFilter}
            onChange={(e) => setServiceFilter(e.target.value)}
            className="appearance-none rounded-2xl border border-white/15 bg-white/[0.05] py-2 pl-9 pr-8 text-xs font-semibold text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Filter by service category"
          >
            {SERVICE_CATEGORIES.map((cat) => (
              <option key={cat} value={cat} className="bg-[#0B1120]">
                {cat}
              </option>
            ))}
          </select>
        </div>

        {/* Radius filter */}
        <div className="relative">
          <MapPin className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-white/40" />
          <select
            id="radiusFilter"
            value={radiusKm}
            onChange={(e) => setRadiusKm(Number(e.target.value))}
            className="appearance-none rounded-2xl border border-white/15 bg-white/[0.05] py-2 pl-9 pr-8 text-xs font-semibold text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Search radius"
          >
            {[2, 5, 10, 25, 50].map((r) => (
              <option key={r} value={r} className="bg-[#0B1120]">
                Within {r} km
              </option>
            ))}
          </select>
        </div>

        {/* Sort */}
        <div className="flex rounded-2xl border border-white/10 bg-white/[0.04] p-1">
          {(["nearest", "rating"] as SortMode[]).map((mode) => (
            <button
              key={mode}
              type="button"
              onClick={() => setSortMode(mode)}
              className={cn(
                "rounded-xl px-3 py-1 text-xs font-semibold transition-all",
                sortMode === mode
                  ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white"
                  : "text-white/60 hover:text-white"
              )}
            >
              {mode === "nearest" ? "Nearest" : "Top Rated"}
            </button>
          ))}
        </div>

        {/* Results count */}
        <span className="ml-auto text-xs text-white/50">
          {filteredShops.length} shop{filteredShops.length !== 1 && "s"} found
        </span>

        {/* Mobile view toggle */}
        <div className="flex rounded-2xl border border-white/10 bg-white/[0.04] p-1 lg:hidden">
          {(["list", "map"] as ViewMode[]).map((mode) => (
            <button
              key={mode}
              type="button"
              onClick={() => setViewMode(mode)}
              className={cn(
                "flex items-center gap-1 rounded-xl px-3 py-1 text-xs font-semibold transition-all",
                viewMode === mode
                  ? "bg-white/10 text-white"
                  : "text-white/50 hover:text-white"
              )}
              aria-pressed={viewMode === mode}
            >
              {mode === "list" ? (
                <>
                  <LayoutList className="size-3.5" /> List
                </>
              ) : (
                <>
                  <MapIcon className="size-3.5" /> Map
                </>
              )}
            </button>
          ))}
        </div>
      </div>

      {/* Main split layout */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">

        {/* ── Shop List Column ───────────────────────────────────────────── */}
        <div
          className={cn(
            "lg:col-span-5",
            viewMode === "map" ? "hidden lg:block" : "block"
          )}
        >
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-16 text-center">
              <Loader2 className="size-10 text-[#22C55E] animate-spin mb-3" />
              <p className="text-sm font-semibold text-white">
                Searching nearby repair shops…
              </p>
            </div>
          ) : filteredShops.length === 0 ? (
            <div className="flex flex-col items-center justify-center rounded-3xl border border-dashed border-white/10 py-16 text-center px-6">
              <Search className="size-12 text-white/20 mb-3" />
              <h2 className="text-base font-bold text-white">
                No Repair Shops Found
              </h2>
              <p className="mt-2 text-xs text-[#CBD5E1]">
                Try increasing the search radius or changing the service
                category filter.
              </p>
              <GlassButton
                className="mt-5"
                size="sm"
                variant="outline"
                onClick={() => {
                  setServiceFilter("All Services");
                  setRadiusKm(25);
                }}
              >
                Clear Filters
              </GlassButton>
            </div>
          ) : (
            <div className="space-y-3 overflow-y-auto max-h-[70vh] pr-1">
              {filteredShops.map((shop, idx) => (
                <ShopCard
                  key={shop.id}
                  shop={shop}
                  isSelected={selectedShop?.id === shop.id}
                  isDemo={isBackendOffline}
                  onSelect={handleSelectShop}
                  delay={idx * 0.06}
                />
              ))}
            </div>
          )}
        </div>

        {/* ── Map Column ─────────────────────────────────────────────────── */}
        <div
          className={cn(
            "lg:col-span-7 h-[420px] lg:h-[600px]",
            viewMode === "list" ? "hidden lg:block" : "block"
          )}
        >
          <MapPanel
            shops={filteredShops}
            selectedShop={selectedShop}
            userLat={userLat}
            userLng={userLng}
            isBackendOffline={isBackendOffline}
          />
        </div>
      </div>
    </div>
  );
}

export default function RepairShopsPage() {
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
                  Loading Nearby Repair Shops…
                </p>
              </div>
            }
          >
            <RepairShopsContent />
          </Suspense>
        </Container>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 py-6 text-center text-xs text-white/40">
        <Container>
          <div className="flex flex-col items-center gap-2 sm:flex-row sm:justify-between">
            <p>
              &copy; {new Date().getFullYear()} RepairVerse AI. Nearby Repair
              Shops.
            </p>
            <Link
              href="/"
              className="text-[#22C55E]/70 hover:text-[#22C55E] transition-colors"
            >
              ← Back to RepairVerse AI
            </Link>
          </div>
        </Container>
      </footer>
    </div>
  );
}
