"use client";

import Link from "next/link";
import { motion } from "framer-motion";
import { Wrench } from "lucide-react";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type LogoProps = {
  href?: string;
  className?: string;
  size?: "sm" | "md" | "lg";
};

export default function Logo({ href = "#home", className, size = "md" }: LogoProps) {
  const iconSizeClasses = {
    sm: "size-8 rounded-xl",
    md: "size-11 rounded-2xl",
    lg: "size-14 rounded-3xl",
  };

  const wrenchSizes = {
    sm: "size-4",
    md: "size-[18px]",
    lg: "size-6",
  };

  const textSizes = {
    sm: "text-base sm:text-lg",
    md: "text-lg sm:text-xl",
    lg: "text-2xl sm:text-3xl",
  };

  return (
    <Link
      href={href}
      className={cn("group flex shrink-0 items-center gap-3", className)}
      aria-label="RepairVerse AI home"
    >
      <motion.div
        animate={{ y: [0, -3, 0] }}
        transition={{ duration: 3, repeat: Infinity, ease: "easeInOut" }}
        whileHover={{ scale: 1.08, rotate: -6 }}
        className={cn(
          "relative flex items-center justify-center border border-white/15 bg-white/[0.08] shadow-[0_8px_32px_rgba(34,197,94,0.2),inset_0_1px_0_rgba(255,255,255,0.2)] backdrop-blur-xl",
          iconSizeClasses[size]
        )}
      >
        <div
          className="absolute inset-0 rounded-[inherit] bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/10 opacity-0 transition-opacity duration-300 group-hover:opacity-100"
          aria-hidden
        />
        <Wrench className={cn("relative text-[#22C55E]", wrenchSizes[size])} aria-hidden />
      </motion.div>

      <motion.span
        className={cn("font-bold tracking-tight text-white", textSizes[size])}
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
