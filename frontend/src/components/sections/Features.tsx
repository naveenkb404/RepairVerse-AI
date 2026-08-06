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

import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

const FEATURES = [
  {
    title: "AI Device Diagnosis",
    description:
      "Upload photos, video, or text and get instant AI analysis with confidence scores, repair difficulty, and safety warnings.",
    href: "#ai-diagnosis",
    icon: ScanLine,
    gradient: "from-[#22C55E] to-[#06B6D4]",
    glow: "rgba(34,197,94,0.35)",
  },
  {
    title: "Repair vs Replace Advisor",
    description:
      "Compare repair and replacement costs, lifespan, and carbon impact to get a clear AI recommendation.",
    href: "#repair-advisor",
    icon: Scale,
    gradient: "from-[#06B6D4] to-[#22C55E]",
    glow: "rgba(6,182,212,0.35)",
  },
  {
    title: "Device Health Passport",
    description:
      "Track warranty, battery health, maintenance records, and AI health scores in one digital device profile.",
    href: "#health-passport",
    icon: HeartPulse,
    gradient: "from-[#22C55E] to-[#16A34A]",
    glow: "rgba(34,197,94,0.3)",
  },
  {
    title: "Carbon Impact Dashboard",
    description:
      "See CO₂ prevented, e-waste reduced, money saved, and sustainability milestones from every repair.",
    href: "#carbon-dashboard",
    icon: Leaf,
    gradient: "from-[#06B6D4] to-[#0891B2]",
    glow: "rgba(6,182,212,0.35)",
  },
  {
    title: "Nearby Repair Shops",
    description:
      "Find trusted local technicians, compare ratings, book appointments, and navigate with integrated maps.",
    href: "#repair-shops",
    icon: MapPin,
    gradient: "from-[#22C55E] to-[#06B6D4]",
    glow: "rgba(34,197,94,0.35)",
  },
  {
    title: "Smart Repair History",
    description:
      "Access a complete timeline of past repairs, costs, parts replaced, and AI-generated maintenance reminders.",
    href: "#repair-history",
    icon: Clock,
    gradient: "from-[#06B6D4] to-[#22C55E]",
    glow: "rgba(6,182,212,0.35)",
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

function FeatureCard({
  title,
  description,
  href,
  icon: Icon,
  gradient,
  glow,
}: (typeof FEATURES)[number]) {
  return (
    <motion.article
      variants={cardVariants}
      whileHover={{ y: -8, scale: 1.02 }}
      transition={{ duration: 0.3, ease: EASE }}
      className="group relative h-full"
    >
      <div
        className="pointer-events-none absolute -inset-px rounded-3xl opacity-0 blur-xl transition-opacity duration-500 group-hover:opacity-100"
        style={{ background: `radial-gradient(circle at 50% 50%, ${glow}, transparent 70%)` }}
        aria-hidden
      />

      <div className="relative flex h-full flex-col rounded-3xl border border-white/10 bg-white/[0.06] p-6 shadow-[0_8px_32px_rgba(0,0,0,0.2),inset_0_1px_0_rgba(255,255,255,0.1)] backdrop-blur-xl transition-[border-color,box-shadow] duration-300 group-hover:border-white/20 group-hover:shadow-[0_16px_48px_rgba(0,0,0,0.3),inset_0_1px_0_rgba(255,255,255,0.15)] sm:p-8">
        <div
          className="pointer-events-none absolute inset-0 rounded-3xl bg-gradient-to-br from-white/[0.04] via-transparent to-transparent opacity-0 transition-opacity duration-300 group-hover:opacity-100"
          aria-hidden
        />

        <div
          className={cn(
            "relative mb-5 flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br shadow-[0_8px_24px_rgba(34,197,94,0.25)]",
            gradient
          )}
        >
          <Icon className="size-5 text-white" aria-hidden />
        </div>

        <h3 className="relative text-lg font-bold text-white sm:text-xl">{title}</h3>

        <p className="relative mt-3 flex-1 text-sm leading-relaxed text-[#CBD5E1] sm:text-base">
          {description}
        </p>

        <Link
          href={href}
          className="relative mt-6 inline-flex items-center gap-1.5 text-sm font-semibold text-[#22C55E] transition-colors hover:text-[#06B6D4]"
        >
          Learn More
          <ArrowRight
            className="size-4 transition-transform duration-300 group-hover:translate-x-1"
            aria-hidden
          />
        </Link>
      </div>
    </motion.article>
  );
}

export default function Features({ className }: FeaturesProps) {
  return (
    <section
      id="features"
      className={cn(
        "relative overflow-hidden bg-[#0B1120] px-4 py-24 sm:px-6 lg:px-8 lg:py-32",
        className
      )}
    >
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_center,rgba(34,197,94,0.06),transparent_60%)]"
        aria-hidden
      />

      <div className="relative mx-auto max-w-7xl">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-16 max-w-3xl text-center lg:mb-20"
        >
          <h2 className="text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Everything You Need to{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Repair Smarter
            </span>
          </h2>
          <p className="mt-5 text-base leading-relaxed text-[#CBD5E1] sm:text-lg">
            RepairVerse AI combines artificial intelligence, sustainability, and
            repair services into one intelligent platform.
          </p>
        </motion.div>

        <motion.div
          variants={containerVariants}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: true, margin: "-60px" }}
          className="grid grid-cols-1 gap-6 sm:grid-cols-2 sm:gap-8 lg:grid-cols-3"
        >
          {FEATURES.map((feature) => (
            <FeatureCard key={feature.title} {...feature} />
          ))}
        </motion.div>
      </div>
    </section>
  );
}
