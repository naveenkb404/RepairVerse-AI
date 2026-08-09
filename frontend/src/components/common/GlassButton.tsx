"use client";

import React from "react";
import Link from "next/link";
import { motion, HTMLMotionProps } from "framer-motion";
import { cn } from "@/lib/utils";

const EASE = [0.22, 1, 0.36, 1] as const;

export type GlassButtonProps = {
  variant?: "primary" | "secondary" | "outline" | "ghost";
  size?: "sm" | "md" | "lg";
  href?: string;
  fullWidth?: boolean;
  children: React.ReactNode;
  icon?: React.ReactNode;
  iconPosition?: "left" | "right";
  className?: string;
  disabled?: boolean;
  onClick?: (e: React.MouseEvent<HTMLElement>) => void;
} & Omit<HTMLMotionProps<"button">, "children" | "onClick">;

export default function GlassButton({
  variant = "primary",
  size = "md",
  href,
  fullWidth = false,
  children,
  icon,
  iconPosition = "left",
  className,
  disabled = false,
  onClick,
  ...props
}: GlassButtonProps) {
  const sizeClasses = {
    sm: "px-4 py-2 text-xs sm:text-sm gap-1.5 rounded-full",
    md: "px-6 py-3 text-sm sm:text-base gap-2.5 rounded-full",
    lg: "px-8 py-4 text-base sm:text-lg gap-3 rounded-full",
  };

  const variantClasses = {
    primary:
      "relative overflow-hidden bg-gradient-to-r from-[#22C55E] to-[#06B6D4] text-white font-bold shadow-[0_4px_20px_rgba(34,197,94,0.35),inset_0_1px_0_rgba(255,255,255,0.25)] hover:shadow-[0_0_40px_rgba(34,197,94,0.55),0_8px_32px_rgba(6,182,212,0.25)] focus-visible:ring-[#22C55E]/60",
    secondary:
      "relative border border-white/15 bg-white/[0.08] text-white font-semibold shadow-[inset_0_1px_0_rgba(255,255,255,0.15)] backdrop-blur-xl hover:bg-white/[0.12] hover:border-white/25 focus-visible:ring-white/50",
    outline:
      "relative border border-[#22C55E]/40 bg-transparent text-[#22C55E] font-semibold hover:bg-[#22C55E]/10 hover:border-[#22C55E] focus-visible:ring-[#22C55E]/50",
    ghost:
      "relative text-white/80 font-medium hover:text-white hover:bg-white/[0.06] focus-visible:ring-white/40",
  };

  const baseClasses = cn(
    "group inline-flex items-center justify-center transition-all duration-300 focus-visible:outline-none focus-visible:ring-2 disabled:pointer-events-none disabled:opacity-50",
    sizeClasses[size],
    variantClasses[variant],
    fullWidth && "w-full",
    className
  );

  const content = (
    <>
      {variant === "primary" && (
        <span
          className="pointer-events-none absolute -inset-1 rounded-full bg-gradient-to-r from-[#22C55E] to-[#06B6D4] opacity-30 blur-lg transition-opacity duration-300 group-hover:opacity-70"
          aria-hidden
        />
      )}
      {variant === "primary" && (
        <motion.span
          className="pointer-events-none absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent"
          initial={{ x: "-100%" }}
          whileHover={{ x: "100%" }}
          transition={{ duration: 0.6, ease: EASE }}
          aria-hidden
        />
      )}
      {icon && iconPosition === "left" && <span className="shrink-0">{icon}</span>}
      <span className="relative z-10">{children}</span>
      {icon && iconPosition === "right" && <span className="shrink-0">{icon}</span>}
    </>
  );

  if (href) {
    return (
      <motion.div
        whileHover={disabled ? undefined : { scale: 1.04, y: -1 }}
        whileTap={disabled ? undefined : { scale: 0.97 }}
        transition={{ duration: 0.25, ease: EASE }}
        className={cn(fullWidth && "w-full")}
      >
        <Link href={href} className={baseClasses} onClick={onClick}>
          {content}
        </Link>
      </motion.div>
    );
  }

  return (
    <motion.button
      whileHover={disabled ? undefined : { scale: 1.04, y: -1 }}
      whileTap={disabled ? undefined : { scale: 0.97 }}
      transition={{ duration: 0.25, ease: EASE }}
      disabled={disabled}
      onClick={onClick}
      className={baseClasses}
      {...props}
    >
      {content}
    </motion.button>
  );
}
