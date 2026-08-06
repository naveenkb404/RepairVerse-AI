"use client";

import { motion } from "framer-motion";
import {
  Camera,
  Brain,
  Wrench,
  Leaf,
  ArrowDown,
} from "lucide-react";

const steps = [
  {
    icon: Camera,
    title: "Upload Device",
    desc: "Take a photo or describe your device problem.",
  },
  {
    icon: Brain,
    title: "AI Diagnosis",
    desc: "Our AI detects the issue and estimates repair complexity.",
  },
  {
    icon: Wrench,
    title: "Repair Guidance",
    desc: "Receive repair steps, cost estimate and nearby repair shops.",
  },
  {
    icon: Leaf,
    title: "Save Carbon",
    desc: "Track money saved, CO₂ reduced and e-waste prevented.",
  },
];

export default function HowItWorks() {
  return (
    <section className="relative py-28 px-6 bg-[#07101d] overflow-hidden">
      <div className="max-w-7xl mx-auto">

        <div className="text-center mb-20">
          <p className="text-green-400 uppercase tracking-widest mb-4">
            How It Works
          </p>

          <h2 className="text-5xl font-bold text-white">
            Repair in{" "}
            <span className="bg-gradient-to-r from-green-400 to-cyan-400 bg-clip-text text-transparent">
              4 Simple Steps
            </span>
          </h2>

          <p className="text-gray-400 mt-6 max-w-3xl mx-auto">
            From diagnosis to repair guidance and carbon savings—
            everything happens in just a few clicks.
          </p>
        </div>

        <div className="grid md:grid-cols-4 gap-8">

          {steps.map((step, index) => {
            const Icon = step.icon;

            return (
              <motion.div
                key={index}
                initial={{opacity:0,y:40}}
                whileInView={{opacity:1,y:0}}
                transition={{duration:0.5,delay:index*0.2}}
                className="relative"
              >

                <div className="rounded-3xl border border-white/10 bg-white/5 backdrop-blur-xl p-8 text-center hover:border-green-400/40 transition-all">

                  <div className="w-20 h-20 rounded-full bg-gradient-to-r from-green-400 to-cyan-400 flex items-center justify-center mx-auto mb-6">

                    <Icon className="text-black" size={34} />

                  </div>

                  <h3 className="text-2xl font-semibold text-white mb-4">
                    {step.title}
                  </h3>

                  <p className="text-gray-400">
                    {step.desc}
                  </p>

                </div>

                {index < 3 && (
                  <ArrowDown
                    className="hidden md:block absolute -right-6 top-1/2 text-green-400"
                    size={28}
                  />
                )}

              </motion.div>
            );
          })}

        </div>

      </div>
    </section>
  );
}