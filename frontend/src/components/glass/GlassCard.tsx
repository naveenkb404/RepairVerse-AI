"use client";

import React from "react";
import { motion, HTMLMotionProps } from "framer-motion";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type GlassCardProps = {
  children: React.ReactNode;
  className?: string;
  hoverEffect?: boolean;
  glowColor?: "green" | "cyan" | "mixed" | "none";
  padding?: "none" | "sm" | "md" | "lg";
} & Omit<HTMLMotionProps<"div">, "children">;

export default function GlassCard({
  children,
  className,
  hoverEffect = true,
  glowColor = "none",
  padding = "md",
  ...props
}: GlassCardProps) {
  const paddingClasses = {
    none: "",
    sm: "p-4 sm:p-5",
    md: "p-6 sm:p-8",
    lg: "p-8 sm:p-10",
  };

  const glowStyles = {
    none: "",
    green: "radial-gradient(circle at 50% 0%, rgba(34,197,94,0.25), transparent 70%)",
    cyan: "radial-gradient(circle at 50% 0%, rgba(6,182,212,0.25), transparent 70%)",
    mixed: "radial-gradient(circle at 50% 0%, rgba(34,197,94,0.2), rgba(6,182,212,0.15), transparent 70%)",
  };

  return (
    <motion.div
      whileHover={hoverEffect ? { y: -6, scale: 1.01 } : undefined}
      transition={{ duration: 0.3, ease: EASE }}
      className={cn("group relative rounded-3xl h-full", className)}
      {...props}
    >
      {/* Background glow on hover or static */}
      {glowColor !== "none" && (
        <div
          className={cn(
            "pointer-events-none absolute -inset-px rounded-3xl opacity-60 blur-xl transition-opacity duration-500",
            hoverEffect && "group-hover:opacity-100"
          )}
          style={{ background: glowStyles[glowColor] }}
          aria-hidden
        />
      )}

      {/* Main Glass Surface */}
      <div
        className={cn(
          "relative flex flex-col h-full rounded-3xl border border-white/10 bg-white/[0.06] shadow-[0_8px_32px_rgba(0,0,0,0.25),inset_0_1px_0_rgba(255,255,255,0.12)] backdrop-blur-xl transition-[border-color,box-shadow,background-color] duration-300",
          hoverEffect && "group-hover:border-white/20 group-hover:bg-white/[0.08] group-hover:shadow-[0_16px_48px_rgba(0,0,0,0.35),inset_0_1px_0_rgba(255,255,255,0.18)]",
          paddingClasses[padding]
        )}
      >
        {children}
      </div>
    </motion.div>
  );
}
