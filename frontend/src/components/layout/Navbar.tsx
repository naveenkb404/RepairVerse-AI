"use client";

import React, { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { motion, AnimatePresence } from "framer-motion";
import {
  Sparkles,
  Activity,
  Cpu,
  Bot,
  Brain,
  Layers,
  Wrench,
  MapPin,
  Smartphone,
  Calendar,
  Leaf,
  ShieldCheck,
  ChevronDown,
  ArrowRight,
  Menu,
  X,
  LogOut,
  User as UserIcon,
  LayoutDashboard,
} from "lucide-react";

import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import Container from "@/components/layout/Container";
import { useAuth } from "@/lib/context/AuthContext";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

// ─── Dropdown Data Definition ────────────────────────────────────────────────

interface NavDropdownItem {
  title: string;
  description: string;
  href: string;
  icon: React.ElementType;
  badge?: string;
}

const PLATFORM_ITEMS: NavDropdownItem[] = [
  {
    title: "AI Diagnosis",
    description: "Identify device issues using AI",
    href: "/diagnosis",
    icon: Sparkles,
  },
  {
    title: "Predictive Intelligence",
    description: "Detect failures before they happen",
    href: "/devices",
    icon: Activity,
  },
  {
    title: "Device Intelligence",
    description: "Get personalized device decisions",
    href: "/devices",
    icon: Cpu,
  },
  {
    title: "Repair Agent",
    description: "Autonomous proactive repair assistance",
    href: "/repair-agent",
    icon: Bot,
    badge: "AUTO",
  },
  {
    title: "Knowledge Graph",
    description: "Learn from ecosystem repair intelligence",
    href: "/knowledge",
    icon: Brain,
    badge: "GRAPH",
  },
  {
    title: "Digital Twin",
    description: "Simulate your device's future",
    href: "/digital-twin/dev-mock-01",
    icon: Layers,
    badge: "TWIN",
  },
  {
    title: "Federated Learning",
    description: "Privacy-preserving continuous intelligence",
    href: "/learning",
    icon: Brain,
    badge: "LEARN",
  },
];

const SOLUTIONS_ITEMS: NavDropdownItem[] = [
  {
    title: "Repair Recommendations",
    description: "Prescriptive, cost-optimized repair options",
    href: "/recommendation",
    icon: Wrench,
  },
  {
    title: "Find Repair Shops",
    description: "Locate certified repair centers nearby",
    href: "/repair-shops",
    icon: MapPin,
  },
  {
    title: "Device Passport",
    description: "Digital lifecycle and repair provenance",
    href: "/devices",
    icon: Smartphone,
  },
  {
    title: "Smart Maintenance",
    description: "Proactive scheduling and degradation alerts",
    href: "/maintenance",
    icon: Calendar,
  },
  {
    title: "Circular Impact",
    description: "Measure environmental and e-waste savings",
    href: "/circular-economy",
    icon: Leaf,
    badge: "ECO",
  },
  {
    title: "Ecosystem Intelligence",
    description: "Network trends, reputation and decision trust",
    href: "/trust-engine",
    icon: ShieldCheck,
    badge: "TRUST",
  },
];

const SIMPLE_LINKS = [
  { label: "Impact", href: "/carbon" },
  { label: "About", href: "#how-it-works" },
];

export default function Navbar({ className }: { className?: string }) {
  const { user, isLoggedIn, logout } = useAuth();
  const pathname = usePathname();

  const [activeDropdown, setActiveDropdown] = useState<"platform" | "solutions" | null>(null);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  const navRef = useRef<HTMLDivElement>(null);

  // Scroll detection
  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 16);
    };
    handleScroll();
    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  // Outside click & Escape key listener
  useEffect(() => {
    const handleOutsideClick = (e: MouseEvent) => {
      if (navRef.current && !navRef.current.contains(e.target as Node)) {
        setActiveDropdown(null);
      }
    };

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === "Escape") {
        setActiveDropdown(null);
        setMobileOpen(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("mousedown", handleOutsideClick);
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, []);

  // Lock body scroll when mobile menu is open
  useEffect(() => {
    if (mobileOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "";
    }
    return () => {
      document.body.style.overflow = "";
    };
  }, [mobileOpen]);

  const toggleDropdown = (name: "platform" | "solutions") => {
    setActiveDropdown((current) => (current === name ? null : name));
  };

  const closeAll = () => {
    setActiveDropdown(null);
    setMobileOpen(false);
  };

  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .map((n) => n[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "U";

  return (
    <header
      ref={navRef}
      className={cn(
        "sticky top-0 z-50 w-full pt-3 sm:pt-4 px-4 sm:px-6 lg:px-8 transition-all duration-300",
        className
      )}
    >
      <div className="max-w-7xl mx-auto">
        <motion.div
          animate={{
            borderColor: scrolled
              ? "rgba(255, 255, 255, 0.18)"
              : "rgba(255, 255, 255, 0.10)",
            backgroundColor: scrolled
              ? "rgba(11, 17, 32, 0.88)"
              : "rgba(11, 17, 32, 0.75)",
            boxShadow: scrolled
              ? "0 12px 40px rgba(0, 0, 0, 0.45), inset 0 1px 0 rgba(255, 255, 255, 0.1)"
              : "0 8px 32px rgba(0, 0, 0, 0.25), inset 0 1px 0 rgba(255, 255, 255, 0.08)",
          }}
          transition={{ duration: 0.3, ease: EASE }}
          className="relative rounded-2xl border border-white/10 px-4 sm:px-6 h-[68px] flex items-center justify-between backdrop-blur-2xl"
        >
          {/* ─── LEFT: Logo ──────────────────────────────────────────────── */}
          <div className="flex items-center">
            <Logo href="#home" size="md" />
          </div>

          {/* ─── CENTER: Desktop Navigation ─────────────────────────────── */}
          <nav
            aria-label="Desktop Navigation"
            className="hidden md:flex items-center gap-1.5 lg:gap-2"
          >
            {/* 1. Platform Dropdown Trigger */}
            <div className="relative">
              <button
                type="button"
                onClick={() => toggleDropdown("platform")}
                aria-expanded={activeDropdown === "platform"}
                className={cn(
                  "group inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-semibold transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50",
                  activeDropdown === "platform"
                    ? "text-white bg-white/[0.08]"
                    : "text-white/80 hover:text-white hover:bg-white/[0.05]"
                )}
              >
                <span>Platform</span>
                <ChevronDown
                  className={cn(
                    "size-3.5 transition-transform duration-200 text-white/60 group-hover:text-white",
                    activeDropdown === "platform" && "rotate-180 text-cyan-400"
                  )}
                />
              </button>

              {/* Platform Dropdown Panel */}
              <AnimatePresence>
                {activeDropdown === "platform" && (
                  <motion.div
                    initial={{ opacity: 0, y: 10, scale: 0.97 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: 6, scale: 0.97 }}
                    transition={{ duration: 0.2, ease: EASE }}
                    className="absolute top-full left-1/2 -translate-x-1/2 mt-3 w-[560px] lg:w-[600px] rounded-2xl border border-white/10 bg-[#0B1120]/95 p-3 shadow-2xl backdrop-blur-2xl ring-1 ring-white/5 z-50"
                  >
                    <div className="px-3 py-2 border-b border-white/5 flex items-center justify-between">
                      <span className="text-[11px] font-bold uppercase tracking-wider text-cyan-400">
                        AI Product Intelligence Platform
                      </span>
                      <span className="text-[10px] text-slate-500 font-mono">
                        6 Core AI Engines
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-1.5 mt-2">
                      {PLATFORM_ITEMS.map((item) => {
                        const Icon = item.icon;
                        const isActive = pathname === item.href;
                        return (
                          <Link
                            key={item.title}
                            href={item.href}
                            onClick={closeAll}
                            className={cn(
                              "group flex items-start gap-3 rounded-xl p-2.5 transition-all",
                              isActive
                                ? "bg-white/[0.08] border border-cyan-500/30"
                                : "hover:bg-white/[0.06] border border-transparent hover:border-white/5"
                            )}
                          >
                            <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-500/10 to-cyan-500/10 border border-emerald-500/20 text-cyan-400 group-hover:border-cyan-400/40 group-hover:scale-105 transition-all">
                              <Icon className="size-4.5" />
                            </div>
                            <div className="min-w-0 flex-1">
                              <div className="flex items-center gap-1.5">
                                <span className="text-xs font-bold text-white group-hover:text-cyan-300 transition-colors">
                                  {item.title}
                                </span>
                                {item.badge && (
                                  <span className="rounded bg-cyan-500/10 px-1.5 py-0.2 text-[9px] font-mono font-bold text-cyan-300 border border-cyan-500/20">
                                    {item.badge}
                                  </span>
                                )}
                              </div>
                              <p className="text-[11px] text-slate-400 leading-tight line-clamp-1 mt-0.5">
                                {item.description}
                              </p>
                            </div>
                          </Link>
                        );
                      })}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* 2. Solutions Dropdown Trigger */}
            <div className="relative">
              <button
                type="button"
                onClick={() => toggleDropdown("solutions")}
                aria-expanded={activeDropdown === "solutions"}
                className={cn(
                  "group inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-semibold transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50",
                  activeDropdown === "solutions"
                    ? "text-white bg-white/[0.08]"
                    : "text-white/80 hover:text-white hover:bg-white/[0.05]"
                )}
              >
                <span>Solutions</span>
                <ChevronDown
                  className={cn(
                    "size-3.5 transition-transform duration-200 text-white/60 group-hover:text-white",
                    activeDropdown === "solutions" && "rotate-180 text-cyan-400"
                  )}
                />
              </button>

              {/* Solutions Dropdown Panel */}
              <AnimatePresence>
                {activeDropdown === "solutions" && (
                  <motion.div
                    initial={{ opacity: 0, y: 10, scale: 0.97 }}
                    animate={{ opacity: 1, y: 0, scale: 1 }}
                    exit={{ opacity: 0, y: 6, scale: 0.97 }}
                    transition={{ duration: 0.2, ease: EASE }}
                    className="absolute top-full left-1/2 -translate-x-1/2 mt-3 w-[560px] lg:w-[600px] rounded-2xl border border-white/10 bg-[#0B1120]/95 p-3 shadow-2xl backdrop-blur-2xl ring-1 ring-white/5 z-50"
                  >
                    <div className="px-3 py-2 border-b border-white/5 flex items-center justify-between">
                      <span className="text-[11px] font-bold uppercase tracking-wider text-[#22C55E]">
                        Ecosystem Solutions &amp; Tools
                      </span>
                      <span className="text-[10px] text-slate-500 font-mono">
                        Hardware Lifecycle
                      </span>
                    </div>

                    <div className="grid grid-cols-2 gap-1.5 mt-2">
                      {SOLUTIONS_ITEMS.map((item) => {
                        const Icon = item.icon;
                        const isActive = pathname === item.href;
                        return (
                          <Link
                            key={item.title}
                            href={item.href}
                            onClick={closeAll}
                            className={cn(
                              "group flex items-start gap-3 rounded-xl p-2.5 transition-all",
                              isActive
                                ? "bg-white/[0.08] border border-emerald-500/30"
                                : "hover:bg-white/[0.06] border border-transparent hover:border-white/5"
                            )}
                          >
                            <div className="flex size-9 shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-emerald-500/10 to-teal-500/10 border border-emerald-500/20 text-[#22C55E] group-hover:border-emerald-400/40 group-hover:scale-105 transition-all">
                              <Icon className="size-4.5" />
                            </div>
                            <div className="min-w-0 flex-1">
                              <div className="flex items-center gap-1.5">
                                <span className="text-xs font-bold text-white group-hover:text-[#22C55E] transition-colors">
                                  {item.title}
                                </span>
                                {item.badge && (
                                  <span className="rounded bg-emerald-500/10 px-1.5 py-0.2 text-[9px] font-mono font-bold text-emerald-300 border border-emerald-500/20">
                                    {item.badge}
                                  </span>
                                )}
                              </div>
                              <p className="text-[11px] text-slate-400 leading-tight line-clamp-1 mt-0.5">
                                {item.description}
                              </p>
                            </div>
                          </Link>
                        );
                      })}
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>
            </div>

            {/* 3. Simple Nav Links: Impact & About */}
            {SIMPLE_LINKS.map((link) => (
              <Link
                key={link.href}
                href={link.href}
                onClick={closeAll}
                className="group relative rounded-xl px-3.5 py-2 text-sm font-semibold text-white/80 hover:text-white transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
              >
                <span>{link.label}</span>
                <span className="absolute bottom-1.5 left-3.5 right-3.5 h-[2px] scale-x-0 bg-gradient-to-r from-[#22C55E] to-[#06B6D4] transition-transform duration-200 group-hover:scale-x-100 origin-left" />
              </Link>
            ))}
          </nav>

          {/* ─── RIGHT: Primary CTA & User Profile ───────────────────────── */}
          <div className="hidden md:flex items-center gap-3">
            {isLoggedIn ? (
              <div className="flex items-center gap-2.5">
                <GlassButton
                  href="/dashboard"
                  size="sm"
                  variant="primary"
                  icon={<ArrowRight className="size-3.5" />}
                  iconPosition="right"
                >
                  Open Dashboard
                </GlassButton>

                <div className="flex items-center gap-2 pl-2 border-l border-white/15">
                  <Link
                    href="/dashboard/profile"
                    className="flex size-8.5 items-center justify-center rounded-full bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-xs font-bold text-white shadow-[0_0_12px_rgba(34,197,94,0.3)] hover:scale-105 transition-transform"
                    title={user?.fullName || "User Profile"}
                  >
                    {initials}
                  </Link>
                  <button
                    type="button"
                    onClick={() => logout()}
                    className="flex size-8.5 items-center justify-center rounded-full border border-white/10 bg-white/[0.04] text-white/70 hover:text-red-400 hover:border-red-500/30 hover:bg-red-500/10 transition-colors focus:outline-none"
                    title="Sign Out"
                    aria-label="Sign Out"
                  >
                    <LogOut className="size-3.5" />
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  href="/auth/login"
                  className="rounded-xl px-3 py-1.5 text-xs font-semibold text-white/80 hover:text-white transition-colors"
                >
                  Sign In
                </Link>

                <GlassButton
                  href="/dashboard"
                  size="sm"
                  variant="primary"
                  icon={<ArrowRight className="size-3.5" />}
                  iconPosition="right"
                >
                  Open Dashboard
                </GlassButton>
              </div>
            )}
          </div>

          {/* ─── Mobile Hamburger Toggle ─────────────────────────────────── */}
          <motion.button
            type="button"
            aria-expanded={mobileOpen}
            aria-label={mobileOpen ? "Close menu" : "Open menu"}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setMobileOpen(!mobileOpen)}
            className="inline-flex size-10 items-center justify-center rounded-xl border border-white/15 bg-white/[0.08] text-white shadow-inner backdrop-blur-xl md:hidden focus:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
          >
            <AnimatePresence mode="wait" initial={false}>
              <motion.span
                key={mobileOpen ? "close" : "open"}
                initial={{ rotate: -90, opacity: 0 }}
                animate={{ rotate: 0, opacity: 1 }}
                exit={{ rotate: 90, opacity: 0 }}
                transition={{ duration: 0.15, ease: EASE }}
                className="flex items-center justify-center"
              >
                {mobileOpen ? <X className="size-5" /> : <Menu className="size-5" />}
              </motion.span>
            </AnimatePresence>
          </motion.button>
        </motion.div>
      </div>

      {/* ─── Mobile Drawer & Overlay ────────────────────────────────────── */}
      <AnimatePresence>
        {mobileOpen && (
          <div className="fixed inset-0 z-50 md:hidden">
            {/* Backdrop overlay */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.25 }}
              onClick={closeAll}
              className="fixed inset-0 bg-black/80 backdrop-blur-md"
            />

            {/* Slide-in drawer */}
            <motion.div
              initial={{ x: "100%" }}
              animate={{ x: 0 }}
              exit={{ x: "100%" }}
              transition={{ duration: 0.35, ease: EASE }}
              className="fixed top-0 right-0 bottom-0 w-full max-w-sm sm:max-w-md bg-[#0B1120] border-l border-white/10 p-6 flex flex-col justify-between overflow-y-auto shadow-2xl z-10"
            >
              <div className="space-y-6">
                {/* Header with logo & close */}
                <div className="flex items-center justify-between pb-4 border-b border-white/10">
                  <Logo href="#home" size="sm" />
                  <button
                    type="button"
                    onClick={closeAll}
                    className="flex size-9 items-center justify-center rounded-xl bg-white/5 text-slate-400 hover:bg-white/10 hover:text-white transition"
                    aria-label="Close menu"
                  >
                    <X className="size-5" />
                  </button>
                </div>

                {/* Section 1: Platform */}
                <div className="space-y-2.5">
                  <span className="text-[10px] font-mono font-bold uppercase tracking-wider text-cyan-400">
                    Platform
                  </span>
                  <div className="grid grid-cols-1 gap-1">
                    {PLATFORM_ITEMS.map((item) => {
                      const Icon = item.icon;
                      return (
                        <Link
                          key={item.title}
                          href={item.href}
                          onClick={closeAll}
                          className="flex items-center gap-3 rounded-xl p-2 text-xs font-semibold text-slate-200 hover:bg-white/[0.06] hover:text-white transition"
                        >
                          <div className="flex size-7 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                            <Icon className="size-3.5" />
                          </div>
                          <span>{item.title}</span>
                        </Link>
                      );
                    })}
                  </div>
                </div>

                {/* Section 2: Solutions */}
                <div className="space-y-2.5">
                  <span className="text-[10px] font-mono font-bold uppercase tracking-wider text-[#22C55E]">
                    Solutions
                  </span>
                  <div className="grid grid-cols-1 gap-1">
                    {SOLUTIONS_ITEMS.map((item) => {
                      const Icon = item.icon;
                      return (
                        <Link
                          key={item.title}
                          href={item.href}
                          onClick={closeAll}
                          className="flex items-center gap-3 rounded-xl p-2 text-xs font-semibold text-slate-200 hover:bg-white/[0.06] hover:text-white transition"
                        >
                          <div className="flex size-7 items-center justify-center rounded-lg bg-emerald-500/10 text-[#22C55E] border border-emerald-500/20">
                            <Icon className="size-3.5" />
                          </div>
                          <span>{item.title}</span>
                        </Link>
                      );
                    })}
                  </div>
                </div>

                {/* Section 3: Other Links */}
                <div className="space-y-2.5">
                  <span className="text-[10px] font-mono font-bold uppercase tracking-wider text-slate-400">
                    Other
                  </span>
                  <div className="flex flex-col gap-1 text-xs font-semibold text-slate-300">
                    {SIMPLE_LINKS.map((link) => (
                      <Link
                        key={link.href}
                        href={link.href}
                        onClick={closeAll}
                        className="rounded-xl px-2 py-1.5 hover:bg-white/[0.06] hover:text-white transition"
                      >
                        {link.label}
                      </Link>
                    ))}
                    <Link
                      href="#faq"
                      onClick={closeAll}
                      className="rounded-xl px-2 py-1.5 hover:bg-white/[0.06] hover:text-white transition"
                    >
                      FAQ
                    </Link>
                  </div>
                </div>
              </div>

              {/* Bottom Drawer Actions */}
              <div className="mt-8 pt-4 border-t border-white/10 space-y-3">
                <GlassButton
                  href="/dashboard"
                  fullWidth
                  variant="primary"
                  onClick={closeAll}
                  icon={<ArrowRight className="size-4" />}
                  iconPosition="right"
                >
                  🚀 Open Dashboard
                </GlassButton>

                {isLoggedIn ? (
                  <div className="flex items-center justify-between text-xs text-slate-400 px-1 pt-1">
                    <span>Logged in as <strong className="text-white">{user?.fullName || "User"}</strong></span>
                    <button
                      type="button"
                      onClick={() => {
                        closeAll();
                        logout();
                      }}
                      className="flex items-center gap-1 text-red-400 hover:text-red-300 transition"
                    >
                      <LogOut className="size-3.5" />
                      Sign Out
                    </button>
                  </div>
                ) : (
                  <div className="text-center">
                    <Link
                      href="/auth/login"
                      onClick={closeAll}
                      className="text-xs text-slate-400 hover:text-white transition"
                    >
                      Already have an account? <span className="text-cyan-400 font-semibold">Sign In</span>
                    </Link>
                  </div>
                )}
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </header>
  );
}
