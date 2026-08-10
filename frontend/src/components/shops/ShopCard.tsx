"use client";

import { RepairShop } from "@/lib/types/repairShops";
import { motion } from "framer-motion";
import {
  Phone,
  MapPin,
  Star,
  ExternalLink,
  ShieldCheck,
  Clock,
  Wrench,
  ChevronRight,
} from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

type ShopCardProps = {
  shop: RepairShop;
  isSelected: boolean;
  isDemo: boolean;
  onSelect: (shop: RepairShop) => void;
  delay?: number;
};

export default function ShopCard({
  shop,
  isSelected,
  isDemo,
  onSelect,
  delay = 0,
}: ShopCardProps) {
  const mapsUrl = shop.latitude && shop.longitude
    ? `https://www.google.com/maps/dir/?api=1&destination=${shop.latitude},${shop.longitude}`
    : shop.address
    ? `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(shop.address)}`
    : null;

  return (
    <motion.div
      initial={{ opacity: 0, y: 16 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay, ease: EASE }}
      role="button"
      tabIndex={0}
      aria-pressed={isSelected}
      onClick={() => onSelect(shop)}
      onKeyDown={(e: React.KeyboardEvent<HTMLDivElement>) => e.key === "Enter" && onSelect(shop)}
      className={cn(
        "group relative cursor-pointer rounded-3xl border p-5 transition-all",
        "focus:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]",
        isSelected
          ? "border-[#22C55E] bg-[#22C55E]/[0.08] shadow-[0_4px_24px_rgba(34,197,94,0.2)]"
          : "border-white/10 bg-white/[0.04] hover:border-white/20 hover:bg-white/[0.07]"
      )}
    >
      {/* Demo label */}
      {isDemo && (
        <span className="absolute top-3 right-3 rounded-full border border-amber-500/30 bg-amber-500/10 px-2 py-0.5 text-[9px] font-bold text-amber-400">
          SAMPLE
        </span>
      )}

      {/* Header row */}
      <div className="flex items-start gap-3 mb-3 pr-14">
        {/* Avatar placeholder */}
        <div className="flex size-10 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-white font-bold text-sm">
          {shop.shopName.charAt(0)}
        </div>
        <div className="min-w-0">
          <div className="flex items-center gap-1.5 flex-wrap">
            <h3 className="truncate text-sm font-bold text-white">
              {shop.shopName}
            </h3>
            {shop.isVerified && (
              <ShieldCheck className="size-3.5 shrink-0 text-[#06B6D4]" aria-label="Verified technician" />
            )}
          </div>
          {shop.ownerName && (
            <p className="text-[11px] text-white/50">{shop.ownerName}</p>
          )}
        </div>
      </div>

      {/* Meta row */}
      <div className="flex flex-wrap gap-2 mb-3">
        {/* Rating */}
        <span className="inline-flex items-center gap-1 rounded-full border border-[#FACC15]/30 bg-[#FACC15]/10 px-2.5 py-0.5 text-[11px] font-semibold text-[#FACC15]">
          <Star className="size-3 fill-[#FACC15]" />
          {shop.rating.toFixed(1)}
          {shop.reviewCount != null && (
            <span className="font-normal text-white/50">({shop.reviewCount})</span>
          )}
        </span>

        {/* Distance */}
        {shop.distanceKm != null && (
          <span className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.05] px-2.5 py-0.5 text-[11px] text-white/70">
            <MapPin className="size-3" />
            {shop.distanceKm < 1
              ? `${Math.round(shop.distanceKm * 1000)} m`
              : `${shop.distanceKm.toFixed(1)} km`}
          </span>
        )}

        {/* Open status */}
        {shop.isOpen != null && (
          <span
            className={cn(
              "inline-flex items-center gap-1 rounded-full border px-2.5 py-0.5 text-[11px] font-semibold",
              shop.isOpen
                ? "border-[#22C55E]/30 bg-[#22C55E]/10 text-[#22C55E]"
                : "border-red-500/30 bg-red-500/10 text-red-400"
            )}
          >
            <Clock className="size-3" />
            {shop.isOpen ? "Open" : "Closed"}
          </span>
        )}
      </div>

      {/* Address */}
      <p className="mb-3 flex items-start gap-1.5 text-[11px] text-white/60 leading-relaxed">
        <MapPin className="size-3.5 mt-0.5 shrink-0 text-[#22C55E]" />
        {shop.address}
      </p>

      {/* Service Tags */}
      {shop.serviceCategories && shop.serviceCategories.length > 0 && (
        <div className="mb-4 flex flex-wrap gap-1.5">
          {shop.serviceCategories.slice(0, 3).map((cat) => (
            <span
              key={cat}
              className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/[0.04] px-2 py-0.5 text-[10px] text-white/60"
            >
              <Wrench className="size-2.5" />
              {cat}
            </span>
          ))}
          {shop.serviceCategories.length > 3 && (
            <span className="text-[10px] text-white/40">
              +{shop.serviceCategories.length - 3} more
            </span>
          )}
        </div>
      )}

      {/* Action row */}
      <div className="flex flex-wrap items-center gap-2 pt-3 border-t border-white/[0.06]">
        {shop.phone && (
          <a
            href={`tel:${shop.phone}`}
            onClick={(e) => e.stopPropagation()}
            className="inline-flex items-center gap-1.5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3 py-1 text-[11px] font-semibold text-[#22C55E] transition-colors hover:bg-[#22C55E]/20"
            aria-label={`Call ${shop.shopName}`}
          >
            <Phone className="size-3" /> Call
          </a>
        )}
        {mapsUrl && (
          <a
            href={mapsUrl}
            target="_blank"
            rel="noopener noreferrer"
            onClick={(e) => e.stopPropagation()}
            className="inline-flex items-center gap-1.5 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-3 py-1 text-[11px] font-semibold text-[#06B6D4] transition-colors hover:bg-[#06B6D4]/20"
            aria-label={`Get directions to ${shop.shopName}`}
          >
            <MapPin className="size-3" /> Directions
          </a>
        )}
        {shop.website && (
          <a
            href={shop.website}
            target="_blank"
            rel="noopener noreferrer"
            onClick={(e) => e.stopPropagation()}
            className="inline-flex items-center gap-1.5 rounded-full border border-white/10 bg-white/[0.05] px-3 py-1 text-[11px] text-white/60 transition-colors hover:text-white"
            aria-label={`Visit ${shop.shopName} website`}
          >
            <ExternalLink className="size-3" /> Website
          </a>
        )}

        {/* Select arrow */}
        <span className="ml-auto text-white/30 group-hover:text-[#22C55E] transition-colors">
          <ChevronRight className="size-4" />
        </span>
      </div>
    </motion.div>
  );
}
