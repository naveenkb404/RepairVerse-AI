"use client";

import { motion } from "framer-motion";
import { Award, Lock, CheckCircle2, Sparkles, Shield, Trophy, Cloud, Trash2, Clock, Zap } from "lucide-react";
import type { SustainabilityAchievement } from "@/lib/types/circularEconomy";

interface AchievementGalleryProps {
  achievements: SustainabilityAchievement[];
}

const ACHIEVEMENT_ICONS: Record<string, any> = {
  FIRST_REPAIR: Sparkles,
  EWASTE_SAVER: Trash2,
  CARBON_CONSCIOUS: Cloud,
  LIFE_EXTENDER: Clock,
  PLANET_PROTECTOR: Shield,
  CIRCULAR_CHAMPION: Trophy,
};

export default function AchievementGallery({ achievements }: AchievementGalleryProps) {
  const unlockedCount = (achievements || []).filter((a) => a.unlocked).length;
  const totalCount = (achievements || []).length;

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 className="text-xl md:text-2xl font-bold text-white flex items-center gap-2.5">
            <Trophy className="size-6 text-amber-400" />
            Sustainability Achievement Gallery
          </h2>
          <p className="text-xs md:text-sm text-slate-400 mt-1">
            Authoritative, deterministic milestones unlocked through verified platform repair and circular stewardship.
          </p>
        </div>

        <div className="inline-flex items-center gap-2 rounded-full border border-amber-500/30 bg-amber-500/10 px-3.5 py-1 text-xs font-bold text-amber-300">
          <Award className="size-4" />
          <span>
            {unlockedCount} of {totalCount} Unlocked
          </span>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {(achievements || []).map((achievement, idx) => {
          const Icon = ACHIEVEMENT_ICONS[achievement.achievementCode] || Award;
          const isUnlocked = achievement.unlocked;

          return (
            <motion.div
              key={achievement.id || idx}
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.3, delay: idx * 0.05 }}
              className={`relative overflow-hidden rounded-3xl border p-5 backdrop-blur-xl shadow-lg flex flex-col justify-between transition-all ${
                isUnlocked
                  ? "border-amber-500/30 bg-gradient-to-br from-amber-500/10 via-[#0B1120]/90 to-[#0F172A]/90 hover:border-amber-500/50"
                  : "border-white/5 bg-[#0B1120]/50 opacity-60 grayscale hover:opacity-80"
              }`}
            >
              <div>
                <div className="flex items-start justify-between">
                  <div
                    className={`rounded-2xl p-3 ${
                      isUnlocked
                        ? "bg-gradient-to-br from-amber-400 to-amber-600 text-slate-950 shadow-md shadow-amber-500/20"
                        : "bg-white/5 text-slate-500"
                    }`}
                  >
                    <Icon className="size-6" />
                  </div>

                  {isUnlocked ? (
                    <span className="inline-flex items-center gap-1 rounded-full border border-emerald-500/30 bg-emerald-500/10 px-2.5 py-0.5 text-[10px] font-bold text-emerald-400">
                      <CheckCircle2 className="size-3" />
                      UNLOCKED
                    </span>
                  ) : (
                    <span className="inline-flex items-center gap-1 rounded-full border border-white/10 bg-white/5 px-2.5 py-0.5 text-[10px] font-bold text-slate-400">
                      <Lock className="size-3" />
                      LOCKED
                    </span>
                  )}
                </div>

                <h3 className="mt-4 text-base font-bold text-white flex items-center gap-1.5">
                  {achievement.achievementName}
                </h3>

                <p className="mt-1.5 text-xs text-slate-300 leading-relaxed">
                  {achievement.achievementDescription}
                </p>
              </div>

              <div className="mt-4 pt-3 border-t border-white/5 space-y-1">
                <div className="text-[11px] font-semibold text-slate-400">
                  Requirement: <span className="text-slate-200">{achievement.requirement}</span>
                </div>
                {isUnlocked && achievement.unlockedAt && (
                  <div className="text-[10px] text-amber-300/80">
                    Unlocked on {new Date(achievement.unlockedAt).toLocaleDateString()}
                  </div>
                )}
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
