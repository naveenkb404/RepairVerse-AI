"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
  Activity,
  BarChart3,
  Bell,
  Bot,
  Brain,
  ChevronRight,
  Cpu,
  History,
  Home,
  Leaf,
  LogOut,
  MapPin,
  Menu,
  Network,
  Settings,
  ShieldCheck,
  Sparkles,
  Store,
  User,
  Wrench,
  X,
  Zap,
} from "lucide-react";
import { useAuth } from "@/lib/context/AuthContext";
import Logo from "@/components/common/Logo";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

type NavItem = {
  label: string;
  href: string;
  icon: React.ElementType;
  badge?: string;
  badgeColor?: "green" | "cyan" | "yellow" | "red";
};

const NAV_ITEMS: NavItem[] = [
  { label: "Overview", href: "/dashboard", icon: Home },
  { label: "Trust & Governance", href: "/trust-engine", icon: ShieldCheck, badge: "TRUST", badgeColor: "cyan" },
  { label: "Digital Twin", href: "/digital-twin/dev-mock-01", icon: Activity, badge: "TWIN", badgeColor: "cyan" },
  { label: "Knowledge Graph", href: "/knowledge", icon: Brain, badge: "GRAPH", badgeColor: "cyan" },
  { label: "Repair Agent", href: "/repair-agent", icon: Bot, badge: "AUTO", badgeColor: "cyan" },
  { label: "Devices", href: "/devices", icon: Cpu, badge: "4" },
  { label: "AI Diagnosis", href: "/diagnosis", icon: Sparkles },
  { label: "Recommendations", href: "/recommendation", icon: Zap },
  { label: "Marketplace", href: "/marketplace", icon: Store, badge: "AI", badgeColor: "cyan" },
  { label: "Network Intel", href: "/marketplace/intelligence", icon: Network, badge: "NEW", badgeColor: "cyan" },
  { label: "Circular Economy", href: "/circular-economy", icon: Leaf, badge: "AI", badgeColor: "green" },
  { label: "Repair History", href: "/repair-history", icon: History, badge: "1", badgeColor: "green" },
  { label: "Repair Shops", href: "/repair-shops", icon: MapPin },
  { label: "Carbon Impact", href: "/carbon", icon: Leaf },
];

const BOTTOM_NAV: NavItem[] = [
  { label: "Profile", href: "/dashboard/profile", icon: User },
  { label: "Notifications", href: "/dashboard/notifications", icon: Bell, badge: "3", badgeColor: "cyan" },
  { label: "Settings", href: "/dashboard/settings", icon: Settings },
];

const BADGE_COLORS = {
  green: "border-[#22C55E]/40 bg-[#22C55E]/15 text-[#22C55E]",
  cyan: "border-[#06B6D4]/40 bg-[#06B6D4]/15 text-[#06B6D4]",
  yellow: "border-[#FACC15]/40 bg-[#FACC15]/15 text-[#FACC15]",
  red: "border-red-500/40 bg-red-500/15 text-red-400",
};

function NavLinkItem({ item, onClick }: { item: NavItem; onClick?: () => void }) {
  const pathname = usePathname();
  const isActive = pathname === item.href;
  const Icon = item.icon;

  return (
    <Link
      href={item.href}
      onClick={onClick}
      className={cn(
        "group flex items-center gap-3 rounded-2xl px-4 py-3 text-sm font-semibold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50",
        isActive
          ? "bg-gradient-to-r from-[#22C55E]/20 to-[#06B6D4]/10 border border-[#22C55E]/30 text-white shadow-[0_0_20px_rgba(34,197,94,0.12)]"
          : "text-white/60 hover:text-white hover:bg-white/[0.06] border border-transparent"
      )}
    >
      <Icon
        className={cn(
          "size-4.5 shrink-0 transition-colors",
          isActive ? "text-[#22C55E]" : "text-white/40 group-hover:text-white/70"
        )}
        aria-hidden
      />
      <span className="flex-1">{item.label}</span>
      {item.badge && (
        <span
          className={cn(
            "rounded-full border px-2 py-0.5 text-[10px] font-bold",
            BADGE_COLORS[item.badgeColor ?? "green"]
          )}
        >
          {item.badge}
        </span>
      )}
      {isActive && (
        <ChevronRight className="size-3.5 text-[#22C55E] opacity-60" aria-hidden />
      )}
    </Link>
  );
}

function SidebarContent({ onClose }: { onClose?: () => void }) {
  const { user, logout } = useAuth();

  const initials = user?.fullName
    ? user.fullName.split(" ").map((n) => n[0]).join("").slice(0, 2).toUpperCase()
    : "??";

  return (
    <div className="flex h-full flex-col">
      {/* Logo area */}
      <div className="flex items-center justify-between px-4 py-5 border-b border-white/10">
        <Logo size="sm" />
        {onClose && (
          <button
            onClick={onClose}
            className="rounded-xl p-2 text-white/50 hover:text-white hover:bg-white/[0.08] transition-colors focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
            aria-label="Close sidebar"
          >
            <X className="size-4" />
          </button>
        )}
      </div>

      {/* User profile chip */}
      <div className="px-4 py-4">
        <Link href="/dashboard/profile" onClick={onClose} className="group flex items-center gap-3 rounded-2xl border border-white/10 bg-white/[0.06] px-3 py-3 transition-all hover:border-white/20 hover:bg-white/[0.09]">
          <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-sm font-bold text-white shadow-[0_0_16px_rgba(34,197,94,0.3)]">
            {initials}
          </div>
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-semibold text-white">{user?.fullName || "Demo User"}</p>
            <p className="truncate text-xs text-white/50">{user?.email || ""}</p>
          </div>
          {user?.verified && (
            <span className="shrink-0 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-2 py-0.5 text-[10px] font-semibold text-[#22C55E]">
              Verified
            </span>
          )}
        </Link>
      </div>

      {/* Main Navigation */}
      <nav aria-label="Main navigation" className="flex-1 overflow-y-auto px-4">
        <p className="mb-2 text-[10px] font-bold uppercase tracking-widest text-white/30">
          Main Menu
        </p>
        <ul className="space-y-1">
          {NAV_ITEMS.map((item) => (
            <li key={item.href}>
              <NavLinkItem item={item} onClick={onClose} />
            </li>
          ))}
        </ul>

        <div className="mt-6 mb-2 border-t border-white/10 pt-5">
          <p className="mb-2 text-[10px] font-bold uppercase tracking-widest text-white/30">
            Account
          </p>
          <ul className="space-y-1">
            {BOTTOM_NAV.map((item) => (
              <li key={item.href}>
                <NavLinkItem item={item} onClick={onClose} />
              </li>
            ))}
          </ul>
        </div>
      </nav>

      {/* Sign Out */}
      <div className="border-t border-white/10 p-4">
        <button
          onClick={() => { onClose?.(); logout(); }}
          className="group flex w-full items-center gap-3 rounded-2xl border border-transparent px-4 py-3 text-sm font-semibold text-red-400/80 transition-all hover:border-red-500/20 hover:bg-red-500/10 hover:text-red-400 focus:outline-none focus:ring-2 focus:ring-red-500/40"
        >
          <LogOut className="size-4.5 shrink-0" aria-hidden />
          Sign Out
        </button>
      </div>
    </div>
  );
}

export default function DashboardSidebar() {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <>
      {/* Desktop sidebar */}
      <aside
        className="hidden lg:flex lg:w-64 xl:w-72 shrink-0 flex-col border-r border-white/10 bg-[#0B1120]/80 backdrop-blur-xl"
        aria-label="Dashboard sidebar"
      >
        <SidebarContent />
      </aside>

      {/* Mobile: hamburger trigger */}
      <button
        className="fixed left-4 top-4 z-50 flex size-10 items-center justify-center rounded-2xl border border-white/15 bg-[#0B1120]/80 backdrop-blur-xl text-white shadow-lg lg:hidden focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50"
        onClick={() => setMobileOpen(true)}
        aria-label="Open sidebar"
      >
        <Menu className="size-5" />
      </button>

      {/* Mobile drawer */}
      <AnimatePresence>
        {mobileOpen && (
          <>
            <motion.div
              key="overlay"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="fixed inset-0 z-40 bg-black/60 backdrop-blur-sm lg:hidden"
              onClick={() => setMobileOpen(false)}
            />
            <motion.aside
              key="drawer"
              initial={{ x: "-100%" }}
              animate={{ x: 0 }}
              exit={{ x: "-100%" }}
              transition={{ duration: 0.3, ease: EASE }}
              className="fixed inset-y-0 left-0 z-50 w-72 border-r border-white/10 bg-[#0B1120] lg:hidden"
              aria-label="Mobile dashboard sidebar"
            >
              <SidebarContent onClose={() => setMobileOpen(false)} />
            </motion.aside>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
