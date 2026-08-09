"use client";

import { useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  AlertCircle,
  ArrowLeft,
  CheckCircle2,
  Eye,
  EyeOff,
  Lock,
  Mail,
  ShieldCheck,
  Sparkles,
  User as UserIcon,
  Wrench,
} from "lucide-react";

import Container from "@/components/layout/Container";
import GlassCard from "@/components/glass/GlassCard";
import GlassButton from "@/components/common/GlassButton";
import Logo from "@/components/common/Logo";
import { registerUser } from "@/lib/api/auth";
import { UserRole } from "@/lib/types/auth";

const EASE = [0.22, 1, 0.36, 1] as const;

function calculatePasswordStrength(pass: string): {
  score: number;
  label: string;
  color: string;
} {
  if (!pass) return { score: 0, label: "None", color: "bg-white/10" };
  let score = 0;
  if (pass.length >= 6) score += 1;
  if (pass.length >= 10) score += 1;
  if (/[A-Z]/.test(pass) && /[0-9]/.test(pass)) score += 1;
  if (/[^A-Za-z0-9]/.test(pass)) score += 1;

  switch (score) {
    case 1:
      return { score: 25, label: "Weak", color: "bg-red-500" };
    case 2:
      return { score: 50, label: "Fair", color: "bg-yellow-500" };
    case 3:
      return { score: 75, label: "Strong", color: "bg-[#06B6D4]" };
    case 4:
      return { score: 100, label: "Very Strong", color: "bg-[#22C55E]" };
    default:
      return { score: 25, label: "Weak", color: "bg-red-500" };
  }
}

export default function RegisterPage() {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [role, setRole] = useState<UserRole>("USER");
  const [acceptTerms, setAcceptTerms] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const [fieldErrors, setFieldErrors] = useState<{
    fullName?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
    acceptTerms?: string;
  }>({});

  const [apiError, setApiError] = useState<string | null>(null);
  const [apiSuccess, setApiSuccess] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const strength = calculatePasswordStrength(password);

  const validateForm = () => {
    const errors: typeof fieldErrors = {};
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!fullName.trim()) {
      errors.fullName = "Full name is required";
    }

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

    if (confirmPassword !== password) {
      errors.confirmPassword = "Passwords do not match";
    }

    if (!acceptTerms) {
      errors.acceptTerms = "You must accept the Terms and Privacy Policy";
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
      const result = await registerUser({
        fullName,
        email,
        password,
        role,
        acceptTerms,
      });

      if (result.success) {
        setApiSuccess(result.message || "Registration successful! You may now sign in.");
      } else {
        setApiError(result.message || "Registration failed. Please check your details.");
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
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,rgba(34,197,94,0.12),transparent_50%),radial-gradient(ellipse_at_bottom_left,rgba(6,182,212,0.12),transparent_50%)]"
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

      {/* Main Registration View */}
      <main className="relative z-10 flex flex-1 items-center justify-center py-12 px-4 sm:px-6">
        <Container size="sm" className="max-w-lg">
          <motion.div
            initial={{ opacity: 0, y: 24, scale: 0.98 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            transition={{ duration: 0.5, ease: EASE }}
          >
            <GlassCard padding="lg" glowColor="cyan" hoverEffect={false}>
              {/* Form Title & Subtitle */}
              <div className="mb-6 text-center">
                <div className="mx-auto mb-3 flex size-12 items-center justify-center rounded-2xl border border-white/15 bg-gradient-to-br from-[#06B6D4]/20 to-[#22C55E]/20 shadow-[inset_0_1px_0_rgba(255,255,255,0.2)]">
                  <UserIcon className="size-6 text-[#06B6D4]" aria-hidden />
                </div>
                <h1 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">
                  Create Your Account
                </h1>
                <p className="mt-2 text-xs text-[#CBD5E1] sm:text-sm">
                  Join RepairVerse AI to diagnose devices and track carbon reduction
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
                    <p className="font-semibold text-red-300">Registration Alert</p>
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
                    <p className="font-semibold text-white">Registration Submitted</p>
                    <p className="mt-0.5 leading-relaxed text-white/90">{apiSuccess}</p>
                  </div>
                </motion.div>
              )}

              {/* Registration Form */}
              <form onSubmit={handleSubmit} noValidate className="space-y-4">
                {/* Account Type Role Selector */}
                <div>
                  <label className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2">
                    I am registering as:
                  </label>
                  <div className="grid grid-cols-2 gap-3">
                    <button
                      type="button"
                      onClick={() => setRole("USER")}
                      className={`flex items-center justify-center gap-2 rounded-2xl border p-3 text-xs font-bold transition-all ${
                        role === "USER"
                          ? "border-[#22C55E] bg-[#22C55E]/15 text-white shadow-[0_4px_16px_rgba(34,197,94,0.2)]"
                          : "border-white/10 bg-white/[0.04] text-white/70 hover:border-white/20 hover:text-white"
                      }`}
                    >
                      <UserIcon className="size-4 text-[#22C55E]" />
                      Device Owner
                    </button>

                    <button
                      type="button"
                      onClick={() => setRole("TECHNICIAN")}
                      className={`flex items-center justify-center gap-2 rounded-2xl border p-3 text-xs font-bold transition-all ${
                        role === "TECHNICIAN"
                          ? "border-[#06B6D4] bg-[#06B6D4]/15 text-white shadow-[0_4px_16px_rgba(6,182,212,0.2)]"
                          : "border-white/10 bg-white/[0.04] text-white/70 hover:border-white/20 hover:text-white"
                      }`}
                    >
                      <Wrench className="size-4 text-[#06B6D4]" />
                      Repair Technician
                    </button>
                  </div>
                </div>

                {/* Full Name Field */}
                <div>
                  <label
                    htmlFor="fullName"
                    className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-1.5"
                  >
                    Full Name
                  </label>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
                      <UserIcon className="size-4" aria-hidden />
                    </div>
                    <input
                      id="fullName"
                      type="text"
                      autoComplete="name"
                      value={fullName}
                      onChange={(e) => {
                        setFullName(e.target.value);
                        if (fieldErrors.fullName) setFieldErrors((prev) => ({ ...prev, fullName: undefined }));
                      }}
                      placeholder="Alex Rivera"
                      className={`w-full rounded-2xl border bg-white/[0.05] py-2.5 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.fullName
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                  </div>
                  {fieldErrors.fullName && (
                    <p className="mt-1 text-xs text-red-400 font-medium">{fieldErrors.fullName}</p>
                  )}
                </div>

                {/* Email Field */}
                <div>
                  <label
                    htmlFor="email"
                    className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-1.5"
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
                      className={`w-full rounded-2xl border bg-white/[0.05] py-2.5 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.email
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                  </div>
                  {fieldErrors.email && (
                    <p className="mt-1 text-xs text-red-400 font-medium">{fieldErrors.email}</p>
                  )}
                </div>

                {/* Password Field & Strength Indicator */}
                <div>
                  <label
                    htmlFor="password"
                    className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-1.5"
                  >
                    Password
                  </label>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
                      <Lock className="size-4" aria-hidden />
                    </div>
                    <input
                      id="password"
                      type={showPassword ? "text" : "password"}
                      autoComplete="new-password"
                      value={password}
                      onChange={(e) => {
                        setPassword(e.target.value);
                        if (fieldErrors.password) setFieldErrors((prev) => ({ ...prev, password: undefined }));
                      }}
                      placeholder="At least 6 characters"
                      className={`w-full rounded-2xl border bg-white/[0.05] py-2.5 pl-10 pr-11 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.password
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute inset-y-0 right-0 flex items-center pr-3.5 text-white/40 hover:text-white focus:outline-none"
                    >
                      {showPassword ? <EyeOff className="size-4" /> : <Eye className="size-4" />}
                    </button>
                  </div>
                  {password && (
                    <div className="mt-2 flex items-center gap-2">
                      <div className="h-1.5 flex-1 rounded-full bg-white/10 overflow-hidden">
                        <div
                          className={`h-full transition-all duration-300 ${strength.color}`}
                          style={{ width: `${strength.score}%` }}
                        />
                      </div>
                      <span className="text-[10px] font-semibold text-white/70">
                        {strength.label}
                      </span>
                    </div>
                  )}
                  {fieldErrors.password && (
                    <p className="mt-1 text-xs text-red-400 font-medium">{fieldErrors.password}</p>
                  )}
                </div>

                {/* Confirm Password Field */}
                <div>
                  <label
                    htmlFor="confirmPassword"
                    className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-1.5"
                  >
                    Confirm Password
                  </label>
                  <div className="relative">
                    <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3.5 text-white/40">
                      <Lock className="size-4" aria-hidden />
                    </div>
                    <input
                      id="confirmPassword"
                      type={showPassword ? "text" : "password"}
                      autoComplete="new-password"
                      value={confirmPassword}
                      onChange={(e) => {
                        setConfirmPassword(e.target.value);
                        if (fieldErrors.confirmPassword)
                          setFieldErrors((prev) => ({ ...prev, confirmPassword: undefined }));
                      }}
                      placeholder="Repeat password"
                      className={`w-full rounded-2xl border bg-white/[0.05] py-2.5 pl-10 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:outline-none focus:ring-2 ${
                        fieldErrors.confirmPassword
                          ? "border-red-500/60 focus:ring-red-500/50"
                          : "border-white/15 hover:border-white/25 focus:border-[#22C55E] focus:ring-[#22C55E]/50"
                      }`}
                    />
                  </div>
                  {fieldErrors.confirmPassword && (
                    <p className="mt-1 text-xs text-red-400 font-medium">
                      {fieldErrors.confirmPassword}
                    </p>
                  )}
                </div>

                {/* Terms Consent Checkbox */}
                <div className="pt-1">
                  <div className="flex items-start gap-2">
                    <input
                      id="acceptTerms"
                      type="checkbox"
                      checked={acceptTerms}
                      onChange={(e) => {
                        setAcceptTerms(e.target.checked);
                        if (fieldErrors.acceptTerms)
                          setFieldErrors((prev) => ({ ...prev, acceptTerms: undefined }));
                      }}
                      className="mt-0.5 size-4 rounded border-white/20 bg-white/[0.06] text-[#22C55E] focus:ring-[#22C55E]/50"
                    />
                    <label htmlFor="acceptTerms" className="text-xs text-[#CBD5E1] leading-snug cursor-pointer">
                      I agree to the RepairVerse AI Terms of Service and Privacy Policy
                    </label>
                  </div>
                  {fieldErrors.acceptTerms && (
                    <p className="mt-1 text-xs text-red-400 font-medium">{fieldErrors.acceptTerms}</p>
                  )}
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
                    {isSubmitting ? "Creating Account..." : "Create Account"}
                  </GlassButton>
                </div>
              </form>

              {/* Footer Switcher Link */}
              <div className="mt-6 border-t border-white/10 pt-5 text-center text-xs text-[#CBD5E1]">
                Already have an account?{" "}
                <Link
                  href="/auth/login"
                  className="font-bold text-[#22C55E] transition-colors hover:text-[#06B6D4] hover:underline"
                >
                  Sign in instead
                </Link>
              </div>
            </GlassCard>
          </motion.div>
        </Container>
      </main>

      {/* Simple Footer */}
      <footer className="relative z-10 border-t border-white/10 py-4 text-center text-xs text-white/50">
        &copy; {new Date().getFullYear()} RepairVerse AI. Secure Registration Portal.
      </footer>
    </div>
  );
}
