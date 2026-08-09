"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import {
  ArrowRight,
  Clock,
  HeartPulse,
  Leaf,
  MapPin,
  Scale,
  ScanLine,
} from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

const FEATURES = [
  {
    title: "AI Device Diagnosis",
    description:
      "Upload photos, video, or text and get instant AI analysis with confidence scores, repair difficulty, and safety warnings.",
    href: "#ai-demo",
    icon: ScanLine,
    gradient: "from-[#22C55E] to-[#06B6D4]",
    glow: "green",
  },
  {
    title: "Repair vs Replace Advisor",
    description:
      "Compare repair and replacement costs, lifespan, and carbon impact to get a clear AI recommendation.",
    href: "#ai-demo",
    icon: Scale,
    gradient: "from-[#06B6D4] to-[#22C55E]",
    glow: "cyan",
  },
  {
    title: "Device Health Passport",
    description:
      "Track warranty, battery health, maintenance records, and AI health scores in one digital device profile.",
    href: "#how-it-works",
    icon: HeartPulse,
    gradient: "from-[#22C55E] to-[#16A34A]",
    glow: "green",
  },
  {
    title: "Carbon Impact Dashboard",
    description:
      "See CO₂ prevented, e-waste reduced, money saved, and sustainability milestones from every repair.",
    href: "#stats",
    icon: Leaf,
    gradient: "from-[#06B6D4] to-[#0891B2]",
    glow: "cyan",
  },
  {
    title: "Nearby Repair Shops",
    description:
      "Find trusted local technicians, compare ratings, book appointments, and navigate with integrated maps.",
    href: "#how-it-works",
    icon: MapPin,
    gradient: "from-[#22C55E] to-[#06B6D4]",
    glow: "green",
  },
  {
    title: "Smart Repair History",
    description:
      "Access a complete timeline of past repairs, costs, parts replaced, and AI-generated maintenance reminders.",
    href: "#stats",
    icon: Clock,
    gradient: "from-[#06B6D4] to-[#22C55E]",
    glow: "cyan",
  },
] as const;

const containerVariants = {
  hidden: {},
  visible: {
    transition: { staggerChildren: 0.1, delayChildren: 0.15 },
  },
};

const cardVariants = {
  hidden: { opacity: 0, y: 28 },
  visible: {
    opacity: 1,
    y: 0,
    transition: { duration: 0.5, ease: EASE },
  },
};

type FeaturesProps = {
  className?: string;
};

export default function Features({ className }: FeaturesProps) {
  return (
    <section
      id="features"
      className={cn(
        "relative overflow-hidden bg-[#0B1120] py-20 sm:py-28",
        className
      )}
    >
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_center,rgba(34,197,94,0.06),transparent_60%)]"
        aria-hidden
      />

      <Container className="relative">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-16 max-w-3xl text-center lg:mb-20"
        >
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#06B6D4]">
            Comprehensive Ecosystem
          </p>
          <h2 className="mt-3 text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Everything You Need to{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Repair Smarter
            </span>
          </h2>
          <p className="mt-5 text-base leading-relaxed text-[#CBD5E1] sm:text-lg">
            RepairVerse AI combines artificial intelligence, sustainability, and repair services into one intelligent platform.
          </p>
        </motion.div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, margin: "-60px" }}
          className="grid grid-cols-1 gap-6 sm:grid-cols-2 sm:gap-8 lg:grid-cols-3"
        >
          {FEATURES.map((feature) => {
            const Icon = feature.icon;
            return (
              <motion.div key={feature.title} variants={cardVariants}>
                <GlassCard hoverEffect glowColor={feature.glow as "green" | "cyan"} padding="lg" className="flex flex-col justify-between">
                  <div>
                    <div
                      className={cn(
                        "relative mb-5 flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br shadow-[0_8px_24px_rgba(34,197,94,0.25)]",
                        feature.gradient
                      )}
                    >
                      <Icon className="size-5 text-white" aria-hidden />
                    </div>

                    <h3 className="text-lg font-bold text-white sm:text-xl">{feature.title}</h3>

                    <p className="mt-3 text-sm leading-relaxed text-[#CBD5E1] sm:text-base">
                      {feature.description}
                    </p>
                  </div>

                  <Link
                    href={feature.href}
                    className="mt-6 inline-flex items-center gap-1.5 text-sm font-semibold text-[#22C55E] transition-colors hover:text-[#06B6D4]"
                  >
                    Learn More
                    <ArrowRight className="size-4 transition-transform duration-300 group-hover:translate-x-1" aria-hidden />
                  </Link>
                </GlassCard>
              </motion.div>
            );
          })}
        </motion.div>
      </Container>
    </section>
  );
}
