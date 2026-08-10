"use client";

import { RepairShop } from "@/lib/types/repairShops";
import { MapPin, Wifi, WifiOff } from "lucide-react";

type MapPanelProps = {
  shops: RepairShop[];
  selectedShop: RepairShop | null;
  userLat?: number;
  userLng?: number;
  isBackendOffline: boolean;
};

/**
 * MapPanel — OpenStreetMap embed via iframe (no npm dependency required).
 *
 * Coordinates from DATABASE_SCHEMA.md RepairShops: latitude, longitude.
 *
 * Security: No API keys required. OpenStreetMap tile access is public.
 * Google Maps API key would be NEXT_PUBLIC_GOOGLE_MAPS_KEY when backend is live.
 * Architecture note: documented in SYSTEM_ARCHITECTURE.md as "Google Maps API".
 * This iframe-based implementation is the frontend fallback until that key is configured.
 */
export default function MapPanel({
  shops,
  selectedShop,
  userLat,
  userLng,
  isBackendOffline,
}: MapPanelProps) {
  // Build OSM iframe URL from the selected shop or user location
  const center = selectedShop?.latitude && selectedShop?.longitude
    ? { lat: selectedShop.latitude, lng: selectedShop.longitude }
    : userLat && userLng
    ? { lat: userLat, lng: userLng }
    : { lat: 12.9716, lng: 77.5946 }; // Bengaluru default for demo

  const zoomLevel = 14;

  // Build OSM embed — markers require paid tier; use basic tile view
  const osmSrc = `https://www.openstreetmap.org/export/embed.html?bbox=${
    center.lng - 0.02
  }%2C${center.lat - 0.02}%2C${center.lng + 0.02}%2C${
    center.lat + 0.02
  }&layer=mapnik&marker=${center.lat}%2C${center.lng}`;

  return (
    <div className="relative h-full min-h-[360px] overflow-hidden rounded-3xl border border-white/10 bg-[#0d1526]">
      {/* Map status bar */}
      <div className="absolute top-3 left-3 z-10 flex items-center gap-2">
        <span
          className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-1 text-[11px] font-semibold backdrop-blur-md ${
            isBackendOffline
              ? "border-amber-500/30 bg-black/60 text-amber-400"
              : "border-[#22C55E]/30 bg-black/60 text-[#22C55E]"
          }`}
        >
          {isBackendOffline ? (
            <>
              <WifiOff className="size-3" /> Demo Map
            </>
          ) : (
            <>
              <Wifi className="size-3" /> Live Map
            </>
          )}
        </span>
      </div>

      {/* OSM Embed iframe */}
      <iframe
        title="Nearby Repair Shops Map"
        src={osmSrc}
        className="h-full w-full border-0"
        sandbox="allow-scripts allow-same-origin"
        loading="lazy"
        aria-label="Map showing nearby repair shop locations"
      />

      {/* Selected shop callout overlay */}
      {selectedShop && (
        <div className="absolute bottom-4 left-4 right-4 z-10">
          <div className="flex items-center gap-2.5 rounded-2xl border border-[#22C55E]/40 bg-black/80 p-3 backdrop-blur-md">
            <div className="flex size-8 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-white text-xs font-bold">
              {selectedShop.shopName.charAt(0)}
            </div>
            <div className="min-w-0">
              <p className="truncate text-xs font-bold text-white">
                {selectedShop.shopName}
              </p>
              <p className="truncate text-[10px] text-white/60">
                <MapPin className="inline size-2.5 mr-0.5" />
                {selectedShop.address}
              </p>
            </div>
            {selectedShop.distanceKm != null && (
              <span className="shrink-0 text-[11px] font-bold text-[#22C55E]">
                {selectedShop.distanceKm < 1
                  ? `${Math.round(selectedShop.distanceKm * 1000)} m`
                  : `${selectedShop.distanceKm.toFixed(1)} km`}
              </span>
            )}
          </div>
        </div>
      )}

      {/* Attribution */}
      <div className="absolute bottom-2 right-2 z-10 text-[8px] text-white/30 bg-black/50 px-1.5 py-0.5 rounded">
        © OpenStreetMap contributors
      </div>
    </div>
  );
}
