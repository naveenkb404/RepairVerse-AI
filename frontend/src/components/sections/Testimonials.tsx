"use client";

import { motion } from "framer-motion";
import { Quote, Star } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

const TESTIMONIALS = [
  {
    name: "Alex Rivera",
    role: "MacBook Owner & Student",
    avatar: "AR",
    rating: 5,
    quote:
      "RepairVerse AI diagnosed a faulty motherboard capacitor on my MacBook Pro within seconds. The local shop estimate saved me $850 compared to replacing the laptop!",
    device: "MacBook Pro M1",
    tag: "Saved $850",
  },
  {
    name: "Sarah Chen",
    role: "Certified Electronics Technician",
    avatar: "SC",
    rating: 5,
    quote:
      "As a local repair shop owner, RepairVerse AI streams pre-diagnosed customers right to our door with precise fault reports. It's revolutionizing repair workflow.",
    device: "Shop Owner Partner",
    tag: "Repair Partner",
  },
  {
    name: "David Miller",
    role: "Eco-conscious Consumer",
    avatar: "DM",
    rating: 5,
    quote:
      "The Repair vs Replace score gave me the confidence to fix my 3-year-old OLED TV instead of sending it to a landfill. Tracking the carbon saved is super rewarding!",
    device: "LG 55\" OLED TV",
    tag: "Prevented E-waste",
  },
] as const;

export default function Testimonials() {
  return (
    <section id="testimonials" className="relative overflow-hidden bg-[#07101d] py-20 sm:py-28">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_bottom_left,rgba(6,182,212,0.08),transparent_50%),radial-gradient(ellipse_at_top_right,rgba(34,197,94,0.08),transparent_50%)]"
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
          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-[#06B6D4]">
            Community Stories
          </p>
          <h2 className="mt-3 text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Trusted by Thousands of{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Repair Champions
            </span>
          </h2>
          <p className="mt-4 text-base text-[#CBD5E1] sm:text-lg">
            See how everyday users and certified technicians are extending device lifespans together.
          </p>
        </motion.div>

        <div className="grid grid-cols-1 gap-8 md:grid-cols-3">
          {TESTIMONIALS.map((item, index) => (
            <motion.div
              key={item.name}
              initial={{ opacity: 0, y: 32 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-40px" }}
              transition={{ duration: 0.5, delay: index * 0.15, ease: EASE }}
            >
              <GlassCard hoverEffect glowColor="mixed" padding="lg" className="flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between">
                    <div className="flex gap-1 text-[#FACC15]">
                      {Array.from({ length: item.rating }).map((_, i) => (
                        <Star key={i} className="size-4 fill-[#FACC15]" aria-hidden />
                      ))}
                    </div>
                    <Quote className="size-6 text-white/20" aria-hidden />
                  </div>

                  <p className="mt-6 text-sm leading-relaxed text-[#CBD5E1] sm:text-base italic">
                    &ldquo;{item.quote}&rdquo;
                  </p>
                </div>

                <div className="mt-8 border-t border-white/10 pt-6">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="flex size-10 items-center justify-center rounded-full border border-[#22C55E]/40 bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/20 text-xs font-bold text-white shadow-[inset_0_1px_0_rgba(255,255,255,0.2)]">
                        {item.avatar}
                      </div>
                      <div>
                        <h3 className="text-sm font-bold text-white sm:text-base">{item.name}</h3>
                        <p className="text-xs text-white/60">{item.role}</p>
                      </div>
                    </div>
                    <span className="rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-2.5 py-1 text-[10px] font-semibold text-[#22C55E]">
                      {item.tag}
                    </span>
                  </div>
                </div>
              </GlassCard>
            </motion.div>
          ))}
        </div>
      </Container>
    </section>
  );
}
