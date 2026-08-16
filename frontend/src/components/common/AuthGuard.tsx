"use client";

import React, { useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import { useAuth } from "@/lib/context/AuthContext";
import { Shield, Sparkles, Lock, ArrowRight } from "lucide-react";
import GlassButton from "./GlassButton";
import GlassCard from "../glass/GlassCard";

interface AuthGuardProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
  redirectTo?: string;
}

export default function AuthGuard({
  children,
  fallback,
  redirectTo = "/auth/login",
}: AuthGuardProps) {
  const { isLoggedIn, isLoading, login } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (!isLoading && !isLoggedIn && redirectTo) {
      const target = `${redirectTo}?redirect=${encodeURIComponent(pathname)}`;
      router.push(target);
    }
  }, [isLoggedIn, isLoading, redirectTo, pathname, router]);

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] w-full flex-col items-center justify-center p-8">
        <div className="relative mb-4 flex size-14 items-center justify-center">
          <div className="absolute inset-0 animate-ping rounded-full bg-emerald-500/20" />
          <div className="size-10 animate-spin rounded-full border-2 border-white/10 border-t-emerald-500" />
        </div>
        <p className="text-sm font-medium text-white/70">Verifying session...</p>
      </div>
    );
  }

  if (!isLoggedIn) {
    if (fallback) return <>{fallback}</>;

    return (
      <div className="flex min-h-[60vh] w-full items-center justify-center p-4">
        <div className="w-full max-w-md">
          <GlassCard padding="lg" glowColor="cyan" hoverEffect={false}>
            <div className="text-center">
              <div className="mx-auto mb-4 flex size-12 items-center justify-center rounded-2xl border border-white/15 bg-gradient-to-br from-emerald-500/20 to-cyan-500/20">
                <Lock className="size-6 text-emerald-400" />
              </div>
              <h2 className="text-xl font-bold text-white">Authentication Required</h2>
              <p className="mt-2 text-xs text-slate-300 leading-relaxed">
                Please sign in to access your registered devices, repair history, and AI insights.
              </p>
              <div className="mt-6 flex flex-col gap-3">
                <GlassButton
                  fullWidth
                  onClick={() =>
                    router.push(
                      `${redirectTo}?redirect=${encodeURIComponent(pathname)}`
                    )
                  }
                  icon={<ArrowRight className="size-4" />}
                >
                  Sign In to Continue
                </GlassButton>
                <GlassButton
                  variant="secondary"
                  fullWidth
                  onClick={() => {
                    // Start offline demo session
                    login("demo-offline-token", {
                      id: "demo-user-001",
                      fullName: "Demo Guest",
                      email: "demo@repairverse.ai",
                      role: "USER",
                      verified: true,
                      joinedAt: new Date().toISOString(),
                    });
                  }}
                  icon={<Sparkles className="size-4 text-cyan-400" />}
                >
                  Explore in Demo Mode
                </GlassButton>
              </div>
            </div>
          </GlassCard>
        </div>
      </div>
    );
  }

  return <>{children}</>;
}
