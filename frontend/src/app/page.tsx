import Navbar from "@/components/layout/Navbar";
import Hero from "@/components/sections/Hero";
import Features from "@/components/sections/Features";
import HowItWorks from "@/components/sections/HowItWorks";
import AIDemo from "@/components/sections/AIDemo";

export default function Home() {
  return (
    <main>
      <Navbar />
      <Hero/>
      <Features/>
      <HowItWorks/>
      <AIDemo/>
    </main>
  );
}
