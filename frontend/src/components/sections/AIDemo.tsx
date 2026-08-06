"use client";

import { Cpu, Upload, Battery, DollarSign, Leaf, Wrench } from "lucide-react";

export default function AIDemo() {
  return (
    <section className="py-28 bg-[#08111F] px-6">
      <div className="max-w-7xl mx-auto">

        <div className="text-center mb-16">
          <p className="text-green-400 uppercase tracking-widest">
            AI Diagnosis Demo
          </p>

          <h2 className="text-5xl font-bold text-white mt-4">
            Experience AI Device Diagnosis
          </h2>

          <p className="text-gray-400 mt-6 max-w-2xl mx-auto">
            Upload a device image and let RepairVerse AI analyze its health,
            estimate repair cost, and recommend the best solution.
          </p>
        </div>

        <div className="grid lg:grid-cols-2 gap-10">

          {/* Upload Card */}
          <div className="rounded-3xl bg-white/5 backdrop-blur-xl border border-white/10 p-10">

            <div className="border-2 border-dashed border-green-400 rounded-3xl h-72 flex flex-col items-center justify-center">

              <Upload size={60} className="text-green-400 mb-6"/>

              <h3 className="text-white text-2xl font-semibold">
                Upload Device Image
              </h3>

              <p className="text-gray-400 mt-4">
                JPG • PNG • WEBP
              </p>

              <button className="mt-8 px-8 py-3 rounded-full bg-gradient-to-r from-green-400 to-cyan-400 text-black font-bold">
                Choose Image
              </button>

            </div>

          </div>

          {/* Result Card */}
          <div className="rounded-3xl bg-white/5 backdrop-blur-xl border border-white/10 p-10">

            <div className="flex items-center gap-3 mb-8">

              <Cpu className="text-green-400"/>

              <h3 className="text-white text-2xl font-bold">
                AI Analysis
              </h3>

            </div>

            <div className="space-y-5">

              <Result title="Device Health" value="87 / 100" icon={<Battery />} />
              <Result title="Estimated Cost" value="$45" icon={<DollarSign />} />
              <Result title="Repair Difficulty" value="Medium" icon={<Wrench />} />
              <Result title="Carbon Saved" value="2.4 kg CO₂" icon={<Leaf />} />

            </div>

            <button className="mt-10 w-full py-4 rounded-xl bg-gradient-to-r from-green-400 to-cyan-400 text-black font-bold text-lg">
              Start AI Diagnosis
            </button>

          </div>

        </div>

      </div>
    </section>
  );
}

function Result({
  title,
  value,
  icon,
}: {
  title: string;
  value: string;
  icon: React.ReactNode;
}) {
  return (
    <div className="flex justify-between items-center bg-white/5 rounded-xl p-4">
      <div className="flex items-center gap-3 text-white">
        {icon}
        <span>{title}</span>
      </div>
      <span className="text-green-400 font-bold">{value}</span>
    </div>
  );
}