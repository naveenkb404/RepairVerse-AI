"use client";

import { Search, Filter, ArrowUpDown } from "lucide-react";
import { RepairStatus } from "@/lib/types/repairHistory";
import { cn } from "@/lib/utils";

type SortOption = "newest" | "oldest" | "highestCost" | "lowestCost";

type RepairHistoryFiltersProps = {
  searchQuery: string;
  onSearchChange: (q: string) => void;
  selectedStatus: string;
  onStatusChange: (status: string) => void;
  selectedCategory: string;
  onCategoryChange: (cat: string) => void;
  selectedSort: SortOption;
  onSortChange: (sort: SortOption) => void;
  categories: string[];
};

const STATUS_TABS: Array<{ label: string; value: string }> = [
  { label: "All Records", value: "All" },
  { label: "Completed", value: "Completed" },
  { label: "In Progress", value: "In Progress" },
  { label: "Scheduled", value: "Scheduled" },
  { label: "Cancelled", value: "Cancelled" },
];

export default function RepairHistoryFilters({
  searchQuery,
  onSearchChange,
  selectedStatus,
  onStatusChange,
  selectedCategory,
  onCategoryChange,
  selectedSort,
  onSortChange,
  categories,
}: RepairHistoryFiltersProps) {
  return (
    <div className="space-y-4">
      {/* Search & Select Controls Row */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search Bar */}
        <div className="relative flex-1 min-w-[240px]">
          <Search className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 size-4 text-white/40" />
          <input
            type="text"
            placeholder="Search by device, issue, technician, or shop name..."
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            className="w-full rounded-2xl border border-white/15 bg-white/[0.05] py-2.5 pl-10 pr-4 text-xs font-semibold text-white placeholder-white/40 backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Search repair history"
          />
        </div>

        {/* Category Filter Dropdown */}
        <div className="relative">
          <Filter className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-white/40" />
          <select
            value={selectedCategory}
            onChange={(e) => onCategoryChange(e.target.value)}
            className="appearance-none rounded-2xl border border-white/15 bg-[#0B1120] py-2.5 pl-9 pr-8 text-xs font-semibold text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Filter repair history by category"
          >
            {categories.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </div>

        {/* Sort Dropdown */}
        <div className="relative">
          <ArrowUpDown className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 size-3.5 text-white/40" />
          <select
            value={selectedSort}
            onChange={(e) => onSortChange(e.target.value as SortOption)}
            className="appearance-none rounded-2xl border border-white/15 bg-[#0B1120] py-2.5 pl-9 pr-8 text-xs font-semibold text-white backdrop-blur-xl focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Sort repair history"
          >
            <option value="newest">Newest First</option>
            <option value="oldest">Oldest First</option>
            <option value="highestCost">Highest Cost</option>
            <option value="lowestCost">Lowest Cost</option>
          </select>
        </div>
      </div>

      {/* Status Filter Tab Pills */}
      <div className="flex flex-wrap items-center gap-1.5 rounded-2xl border border-white/10 bg-white/[0.03] p-1.5">
        {STATUS_TABS.map((tab) => (
          <button
            key={tab.value}
            type="button"
            onClick={() => onStatusChange(tab.value)}
            className={cn(
              "rounded-xl px-3.5 py-1.5 text-xs font-semibold transition-all",
              selectedStatus === tab.value
                ? "bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white shadow-[0_2px_12px_rgba(34,197,94,0.25)]"
                : "text-white/60 hover:bg-white/5 hover:text-white"
            )}
          >
            {tab.label}
          </button>
        ))}
      </div>
    </div>
  );
}
