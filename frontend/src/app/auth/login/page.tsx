"use client";

import { useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  AlertCircle,
  ArrowLeft,
  Eye,
  EyeOff,
  Lock,
  Mail,
  ShieldCheck,
  Sparkles,
} from "lucide-react";

import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import Logo from "@/components/common/Logo";
import { loginUser } from "@/lib/api/auth";

const EASE = [0.22, 1, 0.36, 1] as const;

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const [fieldErrors, setFieldErrors] = useState<{ email?: string; password?: string }>({});
  const [apiError, setApiError] = useState<string | null>(null);
  const [apiSuccess, setApiSuccess] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const validateForm = () => {
    const errors: { email?: string; password?: string } = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email.trim()) {
      errors.email = "Email address is required";
    } else if (!emailRegex.test(email.trim())) {
      errors.email = "Please enter a valid email address";
    }

    if (!password) {
      errors.password = "Password is required";
    } else if (password.length < 6) {
      errors.password = "Password must be at least 6 characters";
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setApiError(null);
    setApiSuccess(null);

    if (!validateForm()) return;

    setIsSubmitting(true);

    try {
      const result = await loginUser({ email, password, rememberMe });
      if (result.success) {
        setApiSuccess(result.message || "Authentication successful!");
      } else {
        setApiError(result.message || "Login failed. Please check your credentials.");
      }
    } catch (err) {
      setApiError("An unexpected client error occurred. Please try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="relative flex min-h-screen w-full flex-col justify-between overflow-hidden bg-[#0B1120] text-white selection:bg-[#22C55E]/30 selection:text-white">
      {/* Background ambient lighting */}
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.12),transparent_50%),radial-gradient(ellipse_at_bottom_right,rgba(6,182,212,0.12),transparent_50%)]"
        aria-hidden
      />

      {/* Top Header Navigation */}
      <header className="relative z-10 w-full border-b border-white/10 bg-[#0B1120]/60 backdrop-blur-xl">
        <Container className="flex h-18 items-center justify-between">
          <Logo size="sm" />
          <Link
            href="/"
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-[#CBD5E1] transition-colors hover:text-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#22C55E]"
          >
            <ArrowLeft className="size-4" />
            Back to Home
          </Link>
        </Container>
      </header>

      {/* Main Login View */}
      <main className="relative z-10 flex flex-1 items-center justify-center py-12 px-4 sm:px-6">
        <Container size="sm" className="max-w-md">
          <motion.div
            initial={{ opacity: 0, y: 24, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            transition={{ duration: 0.5, ease: EASE }}
          >
            <GlassCard padding="lg" glowColor="green" hoverEffect={false}>
              {/* Form Title & Subtitle */}
              <div className="mb-8 text-center">
                <div className="mx-auto mb-3 flex size-12 items-center justify-center rounded-2xl border border-white/15 bg-gradient-to-br from-[#22C55E]/20 to-[#06B6D4]/20 shadow-[inset_0_1px_0_rgba(255,255,255,0.2)]">
                  <Lock className="size-6 text-[#22C55E]" aria-hidden />
                </div>
                <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
                  Welcome Back
                </h1>
                <p className="mt-2 text-xs text-[#CBD5E1] sm:text-sm">
                  Sign in to access your Device Health Passport & AI Diagnostics
                </p>
              </div>

              {/* API Alert Banners */}
              {apiError && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="mb-6 flex items-start gap-3 rounded-2xl border border-red-500/40 bg-red-500/10 p-4 text-xs text-red-200"
                >
                  <AlertCircle className="size-4 text-red-400 shrink-0 mt-0.5" />
                  <div>
                    <p className="font-semibold text-red-300">Authentication Alert</p>
                    <p className="mt-0.5 leading-relaxed">{apiError}</p>
                  </div>
                </motion.div>
              )}

              {apiSuccess && (
                <motion.div
                  initial={{ opacity: 0, y: -10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="mb-6 flex items-start gap-3 rounded-2xl border border-[#22C55E]/40 bg-[#22C55E]/10 p-4 text-xs text-[#22C55E]"
                >
                  <ShieldCheck className="size-4 shrink-0 mt-0.5" />
                  <div>
                    <p className="font-semibold text-white">Success</p>
                    <p className="mt-0.5 leading-relaxed text-white/90">{apiSuccess}</p>
                  </div>
                </motion.div>
              )}

              {/* Login Form */}
              <form onSubmit={handleSubmit} noValidate className="space-y-5">
                {/* Email Field */}
                <div>
                  <label
                    htmlFor="email"
                    className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2"
                  >
                    Email Address
                  </label>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
                      <Mail className="size-4" aria-hidden />
                    </div>
                    <input
                      id="email"
                      type="email"
                      autoComplete="email"
                      value={email}
                      onChange={(e) => {
                        setEmail(e.target.value);
                        if (fieldErrors.email) setFieldErrors((prev) => ({ ...prev, email: undefined }));
                      }}
                      placeholder="name@example.com"
                      className={`w-full rounded-2xl border bg-white/[0.05] py-3 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.email
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                  </div>
                  {fieldErrors.email && (
                    <p className="mt-1.5 text-xs text-red-400 font-medium">{fieldErrors.email}</p>
                  )}
                </div>

                {/* Password Field */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <label
                      htmlFor="password"
                      className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1]"
                    >
                      Password
                    </label>
                    <Link
                      href="#"
                      onClick={(e) => {
                        e.preventDefault();
                        alert("Password reset functionality will be enabled when backend mailer service is active.");
                      }}
                      className="text-xs font-medium text-[#06B6D4] transition-colors hover:text-[#22C55E]"
                    >
                      Forgot password?
                    </Link>
                  </div>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
                      <Lock className="size-4" aria-hidden />
                    </div>
                    <input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      autoComplete="current-password"
                      value={password}
                      onChange={(e) => {
                        setPassword(e.target.value);
                        if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: undefined }));
                      }}
                      placeholder="••••••••"
                      className={`w-full rounded-2xl border bg-white/[0.05] py-3 pl-10 pr-11 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.password
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute inset-y-0 right-0 flex items-center pr-3.5 text-white/40 hover:text-white focus:outline-none"
                      aria-label={showPassword ? "Hide password" : "Show password"}
                    >
                      {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                    </button>
                  </div>
                  {fieldErrors.password && (
                    <p className="mt-1.5 text-xs text-red-400 font-medium">{fieldErrors.password}</p>
                  )}
                </div>

                {/* Remember Me Checkbox */}
                <div className="flex items-center gap-2 pt-1">
                  <input
                    id="rememberMe"
                    type="checkbox"
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                    className="size-4 rounded border-white/20 bg-white/[0.06] text-[#22C55E] focus:ring-[#22C55E]/50"
                  />
                  <label htmlFor="rememberMe" className="text-xs text-[#CBD5E1] cursor-pointer">
                    Remember me on this device
                  </label>
                </div>

                {/* Submit Button */}
                <div className="pt-2">
                  <GlassButton
                    type="submit"
                    fullWidth
                    size="lg"
                    disabled={isSubmitting}
                    icon={<Sparkles className="size-4" />}
                  >
                    {isSubmitting ? "Authenticating..." : "Sign In to RepairVerse"}
                  </GlassButton>
                </div>
              </form>

              {/* Footer Switcher Link */}
              <div className="mt-8 border-t border-white/10 pt-6 text-center text-xs text-[#CBD5E1]">
                Don&apos;t have an account yet?{" "}
                <Link
                  href="/auth/register"
                  className="font-bold text-[#22C55E] transition-colors hover:text-[#06B6D4] hover:underline"
                >
                  Create an account
                </Link>
              </div>
            </GlassCard>
          </motion.div>
        </Container>
      </main>

      {/* Simple Footer */}
      <footer className="relative z-10 border-t border-white/10 py-4 text-center text-xs text-white/50">
        &copy; {new Date().getFullYear()} RepairVerse AI. Secure Authentication Portal.
      </footer>
    </div>
  );
}
