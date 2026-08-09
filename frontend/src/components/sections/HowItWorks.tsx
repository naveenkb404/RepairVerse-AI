"use client";

import { motion } from "framer-motion";
import { Camera, Brain, Wrench, Leaf, ArrowRight } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

const STEPS = [
  {
    stepNumber: "01",
    icon: Camera,
    title: "Upload Device Data",
    desc: "Take a photo, record audio/video, or describe symptoms.",
    gradient: "from-[#22C55E] to-[#06B6D4]",
  },
  {
    stepNumber: "02",
    icon: Brain,
    title: "AI Fault Diagnosis",
    desc: "Neural network detects issues, safety warnings & confidence.",
    gradient: "from-[#06B6D4] to-[#22C55E]",
  },
  {
    stepNumber: "03",
    icon: Wrench,
    title: "Smart Repair Guide",
    desc: "Receive step-by-step instructions, cost estimate & nearby shops.",
    gradient: "from-[#22C55E] to-[#16A34A]",
  },
  {
    stepNumber: "04",
    icon: Leaf,
    title: "Track Carbon Savings",
    desc: "Measure money saved, CO₂ prevented, and e-waste reduced.",
    gradient: "from-[#06B6D4] to-[#0891B2]",
  },
];

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="relative overflow-hidden bg-[#07101d] py-20 sm:py-28">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_center,rgba(6,182,212,0.06),transparent_60%)]"
        aria-hidden
      />

      <Container className="relative">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-16 max-w-3xl text-center"
        >
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#22C55E]">
            Simple Workflow
          </p>
          <h2 className="mt-3 text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Repair in{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              4 Simple Steps
            </span>
          </h2>
          <p className="mt-4 text-base text-[#CBD5E1] sm:text-lg">
            From initial fault diagnosis to guided repair and carbon tracking—everything happens seamlessly.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {STEPS.map((step, index) => {
            const Icon = step.icon;
            return (
              <motion.div
                key={step.title}
                initial={{ opacity: 0, y: 32 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-40px" }}
                transition={{ duration: 0.5, delay: index * 0.12, ease: EASE }}
                className="relative"
              >
                <GlassCard hoverEffect glowColor="mixed" padding="lg">
                  <div className="flex items-center justify-between mb-6">
                    <div
                      className={`flex size-14 items-center justify-center rounded-2xl bg-gradient-to-br ${step.gradient} shadow-[0_8px_24px_rgba(34,197,94,0.25)]`}
                    >
                      <Icon className="size-7 text-white" aria-hidden />
                    </div>
                    <span className="text-2xl font-black text-white/20">{step.stepNumber}</span>
                  </div>

                  <h3 className="text-xl font-bold text-white mb-2">{step.title}</h3>

                  <p className="text-sm leading-relaxed text-[#CBD5E1]">{step.desc}</p>
                </GlassCard>

                {index < 3 && (
                  <div className="hidden lg:block absolute -right-3 top-1/2 -translate-y-1/2 z-20 text-[#22C55E]/40">
                    <ArrowRight className="size-5" aria-hidden />
                  </div>
                )}
              </motion.div>
            );
          })}
        </div>
      </Container>
    </section>
  );
}