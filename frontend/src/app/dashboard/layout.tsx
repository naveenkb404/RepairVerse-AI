"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/lib/context/AuthContext";
import DashboardSidebar from "@/components/dashboard/DashboardSidebar";

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { isLoggedIn, isLoading } = useAuth();
  const router = useRouter();

  // Redirect unauthenticated users to login
  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push("/auth/login");
    }
  }, [isLoggedIn, isLoading, router]);

  // Show blank while rehydrating from localStorage to avoid flash
  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-[#0B1120]">
        <div className="size-10 animate-spin rounded-full border-2 border-white/10 border-t-[#22C55E]" />
      </div>
    );
  }

  if (!isLoggedIn) return null;

  return (
    <div className="flex min-h-screen bg-[#0B1120] text-white">
      {/* Sidebar */}
      <DashboardSidebar />

      {/* Main content area */}
      <div className="flex flex-1 flex-col min-w-0">
        <main className="flex-1 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
}
