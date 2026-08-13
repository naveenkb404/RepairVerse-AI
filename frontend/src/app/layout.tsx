import type { Metadata, Viewport } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AuthProvider } from "@/lib/context/AuthContext";

const geistSans = Geist({
  variable: "--font-sans",
  subsets: ["latin"],
  display: "swap",
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
  display: "swap",
});

export const viewport: Viewport = {
  themeColor: "#0B1120",
  colorScheme: "dark",
  width: "device-width",
  initialScale: 1,
};

export const metadata: Metadata = {
  title: {
    default: "RepairVerse AI - AI-Powered Repair Intelligence Platform",
    template: "%s | RepairVerse AI",
  },
  description:
    "RepairVerse AI helps users diagnose electronic issues using Gemini AI, calculate repair vs replacement scores, connect with local technicians, and track e-waste reduction.",
  keywords: [
    "RepairVerse AI",
    "AI Repair Diagnosis",
    "Electronic Repair",
    "E-waste Reduction",
    "Repair vs Replace",
    "Carbon Footprint Calculator",
    "Local Technicians",
    "Device Health Passport",
  ],
  authors: [{ name: "RepairVerse AI Team" }],
  creator: "RepairVerse AI",
  openGraph: {
    type: "website",
    locale: "en_US",
    url: "https://repairverse.ai",
    title: "RepairVerse AI - AI-Powered Repair Intelligence Platform",
    description:
      "Diagnose electronic issues, compare repair costs, save money, and prevent e-waste with AI-driven repair intelligence.",
    siteName: "RepairVerse AI",
  },
  twitter: {
    card: "summary_large_image",
    title: "RepairVerse AI - AI-Powered Repair Intelligence",
    description:
      "Diagnose devices with AI, connect with local technicians, and measure carbon impact.",
    creator: "@RepairVerseAI",
  },
  robots: {
    index: true,
    follow: true,
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} dark scroll-smooth antialiased`}
    >
      <body className="min-h-screen bg-[#0B1120] text-white flex flex-col selection:bg-[#22C55E]/30 selection:text-white">
        <AuthProvider>{children}</AuthProvider>
      </body>
    </html>
  );
}
