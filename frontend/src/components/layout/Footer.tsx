"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Globe, Share2, Code2, Shield, Sparkles } from "lucide-react";
import Container from "@/components/layout/Container";
import Logo from "@/components/common/Logo";

const EASE = [0.22, 1, 0.36, 1] as const;

const QUICK_LINKS = [
  { label: "Home", href: "#home" },
  { label: "Stats", href: "#stats" },
  { label: "Features", href: "#features" },
  { label: "How It Works", href: "#how-it-works" },
  { label: "AI Demo", href: "#ai-demo" },
  { label: "Testimonials", href: "#testimonials" },
  { label: "FAQ", href: "#faq" },
];

const PLATFORM_LINKS = [
  { label: "AI Visual Diagnosis", href: "#ai-demo" },
  { label: "Repair vs Replace", href: "#features" },
  { label: "Device Passport", href: "#features" },
  { label: "Carbon Dashboard", href: "#stats" },
  { label: "Nearby Repair Shops", href: "#features" },
];

const SOCIAL_LINKS = [
  { label: "GitHub Repository", href: "https://github.com", icon: Code2 },
  { label: "Community Hub", href: "https://twitter.com", icon: Share2 },
  { label: "Official Website", href: "https://repairverse.ai", icon: Globe },
];

export default function Footer() {
  return (
    <footer className="relative border-t border-white/10 bg-[#070d18] pt-16 pb-12 text-[#CBD5E1]">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_bottom,rgba(34,197,94,0.05),transparent_70%)]"
        aria-hidden
      />

      <Container className="relative">
        <div className="grid grid-cols-1 gap-12 lg:grid-cols-12 lg:gap-8">
          {/* Brand Info */}
          <div className="lg:col-span-5 flex flex-col justify-between space-y-6">
            <div>
              <Logo size="lg" />
              <p className="mt-5 max-w-md text-sm leading-relaxed text-[#CBD5E1]">
                RepairVerse AI is an AI-powered Repair Intelligence Platform dedicated to reducing electronic waste by making device repair accessible, affordable, transparent, and sustainable.
              </p>
            </div>

            <div className="flex flex-wrap items-center gap-3">
              <span className="inline-flex items-center gap-2 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3.5 py-1.5 text-xs font-semibold text-[#22C55E]">
                <Sparkles className="size-3.5" aria-hidden />
                Powered by Gemini AI
              </span>
              <span className="inline-flex items-center gap-2 rounded-full border border-white/10 bg-white/[0.05] px-3.5 py-1.5 text-xs font-medium text-white/80">
                <Shield className="size-3.5 text-[#06B6D4]" aria-hidden />
                E-waste Reduction
              </span>
            </div>
          </div>

          {/* Quick Navigation Links */}
          <div className="lg:col-span-3 space-y-4">
            <h3 className="text-sm font-bold uppercase tracking-wider text-white">
              Navigation
            </h3>
            <ul className="space-y-2.5 text-sm">
              {QUICK_LINKS.map((link) => (
                <li key={link.href}>
                  <Link
                    href={link.href}
                    className="inline-block transition-colors hover:text-[#22C55E] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Platform Features Links */}
          <div className="lg:col-span-4 space-y-4">
            <h3 className="text-sm font-bold uppercase tracking-wider text-white">
              Core Capabilities
            </h3>
            <ul className="space-y-2.5 text-sm">
              {PLATFORM_LINKS.map((link) => (
                <li key={link.label}>
                  <Link
                    href={link.href}
                    className="inline-block transition-colors hover:text-[#06B6D4] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#06B6D4]"
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="mt-16 border-t border-white/10 pt-8 flex flex-col items-center justify-between gap-4 sm:flex-row">
          <p className="text-xs text-white/60 text-center sm:text-left">
            &copy; {new Date().getFullYear()} RepairVerse AI. All rights reserved. Building a sustainable tech ecosystem.
          </p>

          <div className="flex items-center gap-4">
            {SOCIAL_LINKS.map((social) => {
              const Icon = social.icon;
              return (
                <motion.a
                  key={social.label}
                  href={social.href}
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label={social.label}
                  whileHover={{ scale: 1.1, y: -2 }}
                  whileTap={{ scale: 0.95 }}
                  transition={{ duration: 0.2, ease: EASE }}
                  className="flex size-9 items-center justify-center rounded-full border border-white/10 bg-white/[0.05] text-white/80 transition-colors hover:border-white/20 hover:bg-white/[0.1] hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]"
                >
                  <Icon className="size-4" aria-hidden />
                </motion.a>
              );
            })}
          </div>
        </div>
      </Container>
    </footer>
  );
}
