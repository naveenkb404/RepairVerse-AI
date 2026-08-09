"use client";

import { useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { ChevronDown, HelpCircle } from "lucide-react";
import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";

const EASE = [0.22, 1, 0.36, 1] as const;

const FAQS = [
  {
    question: "How accurate is the RepairVerse AI diagnosis?",
    answer:
      "RepairVerse AI uses advanced computer vision and trained electronic fault models to detect hardware and software anomalies. In real-world technician verification, our visual diagnosis achieves over 94% initial fault detection accuracy.",
  },
  {
    question: "What types of device inputs can I upload?",
    answer:
      "You can upload device photos (JPG, PNG, WEBP), short video clips of operational glitches, audio recordings of strange mechanical sounds (e.g., fan grinding or coil whine), or text descriptions of symptoms.",
  },
  {
    question: "How is the 'Repair vs Replace' score calculated?",
    answer:
      "Our decision engine analyzes the device's age, estimated market value, expected remaining lifespan, replacement cost, spare parts availability, labor rates, and total carbon footprint prevented to provide an objective score from 0 to 100.",
  },
  {
    question: "Can I find nearby certified repair technicians?",
    answer:
      "Yes! RepairVerse AI features a local repair marketplace integration allowing you to compare verified technician ratings, view estimated repair quotes, and navigate to local shops using Google Maps.",
  },
  {
    question: "Is RepairVerse AI free for device owners?",
    answer:
      "Yes, initial device diagnosis, repair vs replace scoring, basic repair instructions, and carbon tracking are 100% free for individual device owners and consumers.",
  },
] as const;

function FAQItem({
  question,
  answer,
  isOpen,
  onToggle,
}: {
  question: string;
  answer: string;
  isOpen: boolean;
  onToggle: () => void;
}) {
  return (
    <GlassCard
      hoverEffect={false}
      padding="none"
      className="overflow-hidden transition-all duration-300"
    >
      <button
        type="button"
        onClick={onToggle}
        aria-expanded={isOpen}
        className="flex w-full items-center justify-between gap-4 p-6 text-left focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]/50 sm:p-8"
      >
        <span className="text-base font-semibold text-white sm:text-lg">
          {question}
        </span>
        <motion.div
          animate={{ rotate: isOpen ? 180 : 0 }}
          transition={{ duration: 0.3, ease: EASE }}
          className="flex size-8 shrink-0 items-center justify-center rounded-full border border-white/10 bg-white/[0.06] text-white/80"
        >
          <ChevronDown className="size-4 text-[#22C55E]" aria-hidden />
        </motion.div>
      </button>

      <AnimatePresence initial={false}>
        {isOpen && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3, ease: EASE }}
            className="overflow-hidden border-t border-white/10 bg-white/[0.02]"
          >
            <p className="p-6 text-sm leading-relaxed text-[#CBD5E1] sm:p-8 sm:pt-4 sm:text-base">
              {answer}
            </p>
          </motion.div>
        )}
      </AnimatePresence>
    </GlassCard>
  );
}

export default function FAQ() {
  const [openIndex, setOpenIndex] = useState<number | null>(0);

  const toggleIndex = (index: number) => {
    setOpenIndex((prev) => (prev === index ? null : index));
  };

  return (
    <section id="faq" className="relative overflow-hidden bg-[#0B1120] py-20 sm:py-28">
      {/* Background ambient radial light */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top,rgba(34,197,94,0.06),transparent_60%)]"
        aria-hidden
      />

      <Container size="md" className="relative">
        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-60px" }}
          transition={{ duration: 0.6, ease: EASE }}
          className="mx-auto mb-16 text-center"
        >
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/[0.06] px-4 py-1.5 text-xs font-semibold uppercase tracking-[0.18em] text-[#22C55E] backdrop-blur-xl">
            <HelpCircle className="size-3.5" aria-hidden />
            Got Questions?
          </div>
          <h2 className="text-3xl font-bold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Frequently Asked{" "}
            <span className="bg-gradient-to-r from-[#22C55E] to-[#06B6D4] bg-clip-text text-transparent">
              Questions
            </span>
          </h2>
          <p className="mt-4 text-base text-[#CBD5E1] sm:text-lg">
            Everything you need to know about RepairVerse AI diagnosis and repair services.
          </p>
        </motion.div>

        <div className="flex flex-col gap-4">
          {FAQS.map((faq, index) => (
            <motion.div
              key={faq.question}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-30px" }}
              transition={{ duration: 0.4, delay: index * 0.08, ease: EASE }}
            >
              <FAQItem
                question={faq.question}
                answer={faq.answer}
                isOpen={openIndex === index}
                onToggle={() => toggleIndex(index)}
              />
            </motion.div>
          ))}
        </div>
      </Container>
    </section>
  );
}
