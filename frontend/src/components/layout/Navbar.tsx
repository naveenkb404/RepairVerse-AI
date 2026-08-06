"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { AnimatePresence, motion } from "framer-motion";
import { Menu, Sparkles, Wrench, X } from "lucide-react";

import { cn } from "@/lib/utils";

const NAV_LINKS = [
  { label: "Home", href: "#home" },
  { label: "Features", href: "#features" },
  { label: "FAQ", href: "#faq" },
] as const;

const CTA_HREF = "#diagnosis";

const EASE = [0.22, 1, 0.36, 1] as const;

type NavbarProps = {
  className?: string;
};

function Logo() {
  return (
    <Link
      href="#home"
      className="group flex shrink-0 items-center gap-3"
      aria-label="RepairVerse AI home"
    >
      <motion.div
        animate={{ y: [0, -3, 0] }}
        transition={{ duration: 3, repeat: Infinity, ease: "easeInOut" }}
        whileHover={{ scale: 1.08, rotate: -6 }}
        className="relative flex size-11 items-center justify-center rounded-2xl border border-white/15 bg-white/[0.08] shadow-[0_8px_32px_rgba(34,197,94,0.2),inset_0_1px_0_rgba(255,255,255,0.2)] backdrop-blur-xl"
      >
        <motion.div
          className="absolute inset-0 rounded-2xl bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/10 opacity-0 transition-opacity duration-300 group-hover:opacity-100"
          aria-hidden
        />
        <Wrench className="relative size-[18px] text-[#22C55E]" aria-hidden />
      </motion.div>

      <motion.span
        className="text-lg font-bold tracking-tight text-white sm:text-xl"
        whileHover={{ x: 2 }}
        transition={{ duration: 0.2, ease: EASE }}
      >
        Repair
        <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
          Verse
        </span>{" "}
        AI
      </motion.span>
    </Link>
  );
}

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
        className="group relative rounded-xl px-5 py-2.5 text-base font-semibold text-white transition-colors hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
      >
        {label}
        <motion.span
          className="absolute bottom-1.5 left-5 right-5 h-px origin-left bg-gradient-to-r from-[#22C55E] to-[#06B6D4]"
          initial={{ scaleX: 0, opacity: 0 }}
          whileHover={{ scaleX: 1, opacity: 1 }}
          transition={{ duration: 0.3, ease: EASE }}
          aria-hidden
        />
      </Link>
    </motion.li>
  );
}

function TryDiagnosisButton({
  className,
  onClick,
  fullWidth = false,
}: {
  className?: string;
  onClick?: () => void;
  fullWidth?: boolean;
}) {
  return (
    <motion.div
      whileHover={{ scale: 1.04, y: -1 }}
      whileTap={{ scale: 0.97 }}
      transition={{ duration: 0.25, ease: EASE }}
      className={cn("group/cta relative", fullWidth && "w-full")}
    >
      <span
        className="pointer-events-none absolute -inset-1 rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4] opacity-30 blur-lg transition-opacity duration-300 group-hover/cta:opacity-70"
        aria-hidden
      />
      <Link
        href={CTA_HREF}
        onClick={onClick}
        className={cn(
          "relative inline-flex items-center justify-center gap-2.5 overflow-hidden rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4] px-6 py-3 text-base font-bold text-white shadow-[0_4px_20px_rgba(34,197,94,0.35),inset_0_1px_0_rgba(255,255,255,0.25)] transition-shadow duration-300 hover:shadow-[0_0_40px_rgba(34,197,94,0.55),0_8px_32px_rgba(6,182,212,0.25),inset_0_1px_0_rgba(255,255,255,0.3)] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/60",
          fullWidth && "w-full",
          className
        )}
      >
        <motion.span
          className="pointer-events-none absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent"
          initial={{ x: "-100%" }}
          whileHover={{ x: "100%" }}
          transition={{ duration: 0.6, ease: EASE }}
          aria-hidden
        />
        <Sparkles className="relative size-[18px] shrink-0" aria-hidden />
        <span className="relative">Try AI Diagnosis</span>
      </Link>
    </motion.div>
  );
}

export default function Navbar({ className }: NavbarProps) {
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

  return (
    <motion.header
      initial={{ y: -24, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.4, ease: EASE }}
      className={cn(
        "sticky top-0 z-50 w-full bg-[#0B1120]/75 backdrop-blur-xl backdrop-saturate-150",
        className
      )}
    >
      <motion.div
        animate={{
          borderColor: scrolled ? "rgba(255,255,255,0.18)" : "rgba(255,255,255,0.1)",
          boxShadow: scrolled
            ? "0 8px 32px rgba(0,0,0,0.35), inset 0 1px 0 rgba(255,255,255,0.08)"
            : "0 4px 24px rgba(0,0,0,0.15), inset 0 1px 0 rgba(255,255,255,0.06)",
        }}
        transition={{ duration: 0.3, ease: EASE }}
        className="border-b border-white/15 bg-[#0B1120]/50 backdrop-blur-xl"
      >
        <nav
          aria-label="Main navigation"
          className="mx-auto flex h-[72px] max-w-7xl items-center justify-between gap-4 px-4 sm:px-6 lg:px-8"
        >
          <Logo />

          <ul className="absolute left-1/2 hidden -translate-x-1/2 items-center gap-2 rounded-full border border-white/15 bg-white/[0.08] px-3 py-1.5 shadow-[inset_0_1px_0_rgba(255,255,255,0.12),0_8px_32px_rgba(0,0,0,0.12)] backdrop-blur-xl md:flex">
            {NAV_LINKS.map((link) => (
              <NavLink key={link.href} href={link.href} label={link.label} />
            ))}
          </ul>

          <div className="hidden items-center md:flex">
            <TryDiagnosisButton />
          </div>

          <motion.button
            type="button"
            aria-expanded={isOpen}
            aria-controls="mobile-nav"
            aria-label={isOpen ? "Close menu" : "Open menu"}
            whileHover={{ scale: 1.05, backgroundColor: "rgba(255,255,255,0.12)" }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setIsOpen((open) => !open)}
            className="inline-flex size-11 items-center justify-center rounded-full border border-white/15 bg-white/[0.08] text-white shadow-[inset_0_1px_0_rgba(255,255,255,0.15)] backdrop-blur-xl focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50 md:hidden"
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
        </nav>
      </motion.div>

      <AnimatePresence>
        {isOpen && (
          <motion.div
            id="mobile-nav"
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.35, ease: EASE }}
            className="overflow-hidden border-b border-white/15 bg-[#0B1120]/85 backdrop-blur-xl md:hidden"
          >
            <motion.ul
              initial="closed"
              animate="open"
              exit="closed"
              variants={{
                open: { transition: { staggerChildren: 0.07, delayChildren: 0.06 } },
                closed: { transition: { staggerChildren: 0.04, staggerDirection: -1 } },
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
                    className="block rounded-2xl border border-transparent px-4 py-3.5 text-lg font-semibold text-white transition-colors hover:border-white/10 hover:bg-white/[0.08] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50"
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
                className="pt-3"
              >
                <TryDiagnosisButton fullWidth onClick={closeMenu} />
              </motion.li>
            </motion.ul>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  );
}
