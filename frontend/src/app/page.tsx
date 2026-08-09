import Navbar from "@/components/layout/Navbar";
import Hero from "@/components/sections/Hero";
import Stats from "@/components/sections/Stats";
import Features from "@/components/sections/Features";
import HowItWorks from "@/components/sections/HowItWorks";
import AIDemo from "@/components/sections/AIDemo";
import Testimonials from "@/components/sections/Testimonials";
import FAQ from "@/components/sections/FAQ";
import Footer from "@/components/layout/Footer";

export default function Home() {
  return (
    <div className="flex min-h-screen flex-col bg-[#0B1120] font-sans antialiased text-white selection:bg-[#22C55E]/30 selection:text-white">
      <Navbar />
      <main className="flex-1">
        <Hero />
        <Stats />
        <Features />
        <HowItWorks />
        <AIDemo />
        <Testimonials />
        <FAQ />
      </main>
      <Footer />
    </div>
  );
}
