"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AnimatePresence, motion } from "framer-motion";
import { Menu, Sparkles, X, User as UserIcon, LogOut, LayoutDashboard } from "lucide-react";

import Logo from "@/components/common/Logo";
import GlassButton from "@/components/common/GlassButton";
import Container from "@/components/layout/Container";
import { useAuth } from "@/lib/context/AuthContext";
import { cn } from "@/lib/utils";

const NAV_LINKS = [
  { label: "Home", href: "#home" },
  { label: "Stats", href: "#stats" },
  { label: "Features", href: "#features" },
  { label: "How It Works", href: "#how-it-works" },
  { label: "AI Demo", href: "#ai-demo" },
  { label: "FAQ", href: "#faq" },
] as const;

const CTA_HREF = "#ai-demo";
const EASE = [0.22, 1, 0.36, 1] as const;

type NavbarProps = {
  className?: string;
};

function NavLink({
  href,
  label,
  onClick,
}: {
  href: string;
  label: string;
  onClick?: () => void;
}) {
  return (
    <motion.li whileHover={{ y: -2 }} transition={{ duration: 0.25, ease: EASE }}>
      <Link
        href={href}
        onClick={onClick}
        className="group relative rounded-xl px-4 py-2 text-sm font-semibold text-white/90 transition-colors hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
      >
        {label}
        <motion.span
          className="absolute bottom-1 left-4 right-4 h-px origin-left bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
          initial={{ scaleX: 0, opacity: 0 }}
          whileHover={{ scaleX: 1, opacity: 1 }}
          transition={{ duration: 0.3, ease: EASE }}
          aria-hidden
        />
      </Link>
    </motion.li>
  );
}

export default function Navbar({ className }: NavbarProps) {
  const { user, isLoggedIn, logout } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 12);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  useEffect(() => {
    document.body.style.overflow = isOpen ? "hidden" : "";
    return () => {
      document.body.style.overflow = "";
    };
  }, [isOpen]);

  const closeMenu = () => setIsOpen(false);

  const initials = user?.fullName
    ? user.fullName.split(" ").map((n) => n[0]).join("").slice(0, 2).toUpperCase()
    : "U";

  return (
    <motion.header
      initial={{ y: -24, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.4, ease: EASE }}
      className={cn(
        "sticky top-0 z-50 w-full bg-[#0B1120]/80 backdrop-blur-xl backdrop-saturate-150",
        className
      )}
    >
      <motion.div
        animate={{
          borderColor: scrolled ? "rgba(255,255,255,0.18)" : "rgba(255,255,255,0.1)",
          boxShadow: scrolled
            ? "0 8px 32px rgba(0,0,0,0.4), inset 0 1px 0 rgba(255,255,255,0.08)"
            : "0 4px 24px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.06)",
        }}
        transition={{ duration: 0.3, ease: EASE }}
        className="border-b border-white/15 bg-[#0B1120]/50 backdrop-blur-xl"
      >
        <Container as="nav" aria-label="Main navigation" className="flex h-[72px] items-center justify-between gap-4">
          <Logo href="#home" />

          {/* Desktop Nav Links */}
          <ul className="hidden items-center gap-1 rounded-full border border-white/15 bg-white/[0.08] px-3 py-1.5 shadow-[inset_0_1px_0_rgba(255,255,255,0.12),0_8px_32px_rgba(0,0,0,0.12)] backdrop-blur-xl lg:flex">
            {NAV_LINKS.map((link) => (
              <NavLink key={link.href} href={link.href} label={link.label} />
            ))}
          </ul>

          {/* Action CTAs */}
          <div className="hidden items-center gap-3 lg:flex">
            <GlassButton href="/circular-economy" variant="secondary" size="sm">
              Circular Impact
            </GlassButton>
            <GlassButton href="/maintenance" variant="secondary" size="sm">
              Maintenance
            </GlassButton>
            <GlassButton href="/devices" variant="secondary" size="sm">
              Passports
            </GlassButton>

            {isLoggedIn ? (
              <>
                <GlassButton href="/dashboard" size="sm" icon={<LayoutDashboard className="size-4" />}>
                  Dashboard
                </GlassButton>
                <div className="flex items-center gap-2 pl-2 border-l border-white/15">
                  <Link
                    href="/dashboard/profile"
                    className="flex size-9 items-center justify-center rounded-full bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-xs font-bold text-white shadow-[0_0_12px_rgba(34,197,94,0.3)] hover:scale-105 transition-transform"
                    title={user?.fullName || "User Profile"}
                  >
                    {initials}
                  </Link>
                  <button
                    type="button"
                    onClick={() => logout()}
                    className="flex size-9 items-center justify-center rounded-full border border-white/15 bg-white/[0.06] text-white/70 hover:text-red-400 hover:border-red-500/40 hover:bg-red-500/10 transition-colors focus:outline-none"
                    title="Sign Out"
                    aria-label="Sign Out"
                  >
                    <LogOut className="size-4" />
                  </button>
                </div>
              </>
            ) : (
              <>
                <GlassButton href="/auth/login" variant="secondary" size="sm">
                  Sign In
                </GlassButton>
                <GlassButton href={CTA_HREF} size="sm" icon={<Sparkles className="size-4" />}>
                  Try AI Diagnosis
                </GlassButton>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <motion.button
            type="button"
            aria-expanded={isOpen}
            aria-controls="mobile-nav"
            aria-label={isOpen ? "Close menu" : "Open menu"}
            whileHover={{ scale: 1.05, backgroundColor: "rgba(255,255,255,0.12)" }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setIsOpen((open) => !open)}
            className="inline-flex size-11 items-center justify-center rounded-full border border-white/15 bg-white/[0.08] text-white shadow-[inset_0_1px_0_rgba(255,255,255,0.15)] backdrop-blur-xl focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50 lg:hidden"
          >
            <AnimatePresence mode="wait" initial={false}>
              <motion.span
                key={isOpen ? "close" : "open"}
                initial={{ rotate: -90, opacity: 0 }}
                animate={{ rotate: 0, opacity: 1 }}
                exit={{ rotate: 90, opacity: 0 }}
                transition={{ duration: 0.2, ease: EASE }}
                className="flex items-center justify-center"
              >
                {isOpen ? (
                  <X className="size-5" aria-hidden />
                ) : (
                  <Menu className="size-5" aria-hidden />
                )}
              </motion.span>
            </AnimatePresence>
          </motion.button>
        </Container>
      </motion.div>

      {/* Mobile Drawer */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            id="mobile-nav"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="overflow-hidden border-b border-white/15 bg-[#0B1120]/90 backdrop-blur-xl lg:hidden"
          >
            <motion.ul
              initial="closed"
              animate="open"
              exit="closed"
              variants={{
                open: { transition: { staggerChildren: 0.05, delayChildren: 0.05 } },
                closed: { transition: { staggerChildren: 0.03, staggerDirection: -1 } },
              }}
              className="flex flex-col gap-1 px-4 py-5 sm:px-6"
            >
              {NAV_LINKS.map((link) => (
                <motion.li
                  key={link.href}
                  variants={{
                    open: { opacity: 1, x: 0 },
                    closed: { opacity: 0, x: -16 },
                  }}
                  transition={{ duration: 0.3, ease: EASE }}
                >
                  <Link
                    href={link.href}
                    onClick={closeMenu}
                    className="block rounded-2xl border border-transparent px-4 py-3 text-base font-semibold text-white transition-colors hover:border-white/10 hover:bg-white/[0.08] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
                  >
                    {link.label}
                  </Link>
                </motion.li>
              ))}
              <motion.li
                variants={{
                  open: { opacity: 1, x: 0 },
                  closed: { opacity: 0, x: -16 },
                }}
                transition={{ duration: 0.3, ease: EASE }}
                className="pt-3 flex flex-col gap-2"
              >
                <GlassButton href="/maintenance" variant="secondary" fullWidth onClick={closeMenu}>
                  Smart Maintenance Center
                </GlassButton>
                <GlassButton href="/repair-history" variant="secondary" fullWidth onClick={closeMenu}>
                  Repair History
                </GlassButton>
                <GlassButton href="/devices" variant="secondary" fullWidth onClick={closeMenu}>
                  Device Passports
                </GlassButton>

                {isLoggedIn ? (
                  <>
                    <GlassButton href="/dashboard" fullWidth onClick={closeMenu} icon={<LayoutDashboard className="size-4" />}>
                      User Dashboard ({user?.fullName || "Account"})
                    </GlassButton>
                    <GlassButton
                      variant="secondary"
                      fullWidth
                      onClick={() => {
                        closeMenu();
                        logout();
                      }}
                      icon={<LogOut className="size-4 text-red-400" />}
                    >
                      Sign Out
                    </GlassButton>
                  </>
                ) : (
                  <>
                    <GlassButton href="/auth/login" variant="secondary" fullWidth onClick={closeMenu}>
                      Sign In
                    </GlassButton>
                    <GlassButton href={CTA_HREF} fullWidth onClick={closeMenu} icon={<Sparkles className="size-4" />}>
                      Try AI Diagnosis
                    </GlassButton>
                  </>
                )}
              </motion.li>
            </motion.ul>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  );
}
