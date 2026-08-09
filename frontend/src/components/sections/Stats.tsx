"use client";

import { motion } from "framer-motion";
import { DollarSign, Leaf, ShieldCheck, Wrench } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

const STATS = [
  {
    icon: Wrench,
    value: "14,250+",
    label: "Devices Diagnosed",
    subtext: "Laptops, phones & home electronics saved",
    gradient: "from-[#22C55E] to-[#06B6D4]",
  },
  {
    icon: Leaf,
    value: "38.5 Tons",
    label: "CO₂ Prevented",
    subtext: "E-waste diverted from municipal landfills",
    gradient: "from-[#06B6D4] to-[#22C55E]",
  },
  {
    icon: DollarSign,
    value: "$1.8M+",
    label: "Repair Savings",
    subtext: "Saved by choosing repair over replacement",
    gradient: "from-[#22C55E] to-[#16A34A]",
  },
  {
    icon: ShieldCheck,
    value: "94.2%",
    label: "Diagnosis Accuracy",
    subtext: "Validated by certified technician network",
    gradient: "from-[#06B6D4] to-[#0891B2]",
  },
] as const;

export default function Stats() {
  return (
    <section id="stats" className="relative overflow-hidden bg-[#09101d] py-20 sm:py-28">
      {/* Background radial glow */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_center,rgba(34,197,94,0.08),transparent_60%)]"
        aria-hidden
      />

      <Container className="relative">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-14 max-w-3xl text-center sm:mb-18"
        >
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#22C55E]">
            Measurable Impact
          </p>
          <h2 className="mt-3 text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Empowering a{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Sustainable Future
            </span>
          </h2>
          <p className="mt-4 text-base text-[#CBD5E1] sm:text-lg">
            Every device repaired is a step towards reducing global e-waste and keeping technology alive.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {STATS.map((stat, i) => {
            const Icon = stat.icon;
            return (
              <motion.div
                key={stat.label}
                initial={{ opacity: 0, y: 32 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-40px" }}
                transition={{ duration: 0.5, delay: i * 0.1, ease: EASE }}
              >
                <GlassCard hoverEffect glowColor="mixed" padding="md">
                  <div className="flex items-center justify-between">
                    <div
                      className={`flex size-12 items-center justify-center rounded-2xl bg-gradient-to-br ${stat.gradient} shadow-[0_8px_24px_rgba(34,197,94,0.25)]`}
                    >
                      <Icon className="size-6 text-white" aria-hidden />
                    </div>
                    <span className="rounded-full border border-white/10 bg-white/[0.05] px-3 py-1 text-[11px] font-semibold text-[#22C55E]">
                      Live Impact
                    </span>
                  </div>

                  <p className="mt-6 text-3xl font-bold tracking-tight text-white sm:text-4xl">
                    {stat.value}
                  </p>

                  <h3 className="mt-2 text-base font-semibold text-white/90 sm:text-lg">
                    {stat.label}
                  </h3>

                  <p className="mt-1 text-xs leading-relaxed text-[#CBD5E1] sm:text-sm">
                    {stat.subtext}
                  </p>
                </GlassCard>
              </motion.div>
            );
          })}
        </div>
      </Container>
    </section>
  );
}
