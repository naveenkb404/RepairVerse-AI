"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import { Bell, Globe, LogOut, Moon, Trash2 } from "lucide-react";
import { useAuth } from "@/lib/context/AuthContext";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";



const EASE = [0.22, 1, 0.36, 1] as const;

function Toggle({ checked, onChange }: { checked: boolean; onChange: (v: boolean) => void }) {
  return (
    <button
      role="switch"
      aria-checked={checked}
      onClick={() => onChange(!checked)}
      className={cn(
        "relative inline-flex h-6 w-11 shrink-0 cursor-pointer items-center rounded-full border-2 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50",
        checked
          ? "border-[#22C55E] bg-[#22C55E]"
          : "border-white/20 bg-white/[0.08]"
      )}
    >
      <span
        className={cn(
          "inline-block size-4 rounded-full bg-white shadow transition-transform duration-200",
          checked ? "translate-x-5" : "translate-x-0.5"
        )}
      />
    </button>
  );
}

function SettingRow({
  label,
  description,
  control,
}: {
  label: string;
  description?: string;
  control: React.ReactNode;
}) {
  return (
    <div className="flex items-center justify-between gap-6 py-4 border-b border-white/[0.06] last:border-0">
      <div>
        <p className="text-sm font-semibold text-white">{label}</p>
        {description && <p className="mt-0.5 text-xs text-white/40">{description}</p>}
      </div>
      <div className="shrink-0">{control}</div>
    </div>
  );
}

export default function SettingsPage() {
  const { logout } = useAuth();

  const [emailNotifs, setEmailNotifs] = useState(true);
  const [pushNotifs, setPushNotifs] = useState(true);
  const [newsletter, setNewsletter] = useState(true);
  const [repairAlerts, setRepairAlerts] = useState(true);
  const [language, setLanguage] = useState("en");

  return (
    <div className="relative min-h-full bg-[#0B1120] px-6 py-8 md:px-8 lg:px-10">
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_bottom_right,rgba(139,92,246,0.07),transparent_50%)]"
        aria-hidden
      />
      <div className="relative mx-auto max-w-2xl space-y-8">

        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -14 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, ease: EASE }}
        >
          <h1 className="text-2xl font-bold text-white sm:text-3xl">Settings</h1>
          <p className="mt-1 text-sm text-white/50">Manage your notification, language, and account preferences.</p>
        </motion.div>

        {/* Notifications */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.05, ease: EASE }}
        >
          <GlassCard padding="md" hoverEffect={false}>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex size-9 items-center justify-center rounded-2xl bg-gradient-to-br from-[#06B6D4] to-[#0891B2]">
                <Bell className="size-4 text-white" aria-hidden />
              </div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-white/40">
                Notifications
              </h2>
            </div>
            <SettingRow
              label="Email Notifications"
              description="Receive repair updates and alerts by email"
              control={<Toggle checked={emailNotifs} onChange={setEmailNotifs} />}
            />
            <SettingRow
              label="Push Notifications"
              description="Browser push alerts for repair status changes"
              control={<Toggle checked={pushNotifs} onChange={setPushNotifs} />}
            />
            <SettingRow
              label="Repair Completion Alerts"
              description="Be notified immediately when a repair is finished"
              control={<Toggle checked={repairAlerts} onChange={setRepairAlerts} />}
            />
            <SettingRow
              label="Newsletter & Tips"
              description="Monthly sustainability tips and platform updates"
              control={<Toggle checked={newsletter} onChange={setNewsletter} />}
            />
          </GlassCard>
        </motion.div>

        {/* Appearance */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1, ease: EASE }}
        >
          <GlassCard padding="md" hoverEffect={false}>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex size-9 items-center justify-center rounded-2xl bg-gradient-to-br from-[#8B5CF6] to-[#7C3AED]">
                <Moon className="size-4 text-white" aria-hidden />
              </div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-white/40">
                Appearance
              </h2>
            </div>
            <SettingRow
              label="Theme"
              description="RepairVerse AI uses a premium dark theme by default"
              control={
                <span className="rounded-full border border-white/10 bg-white/[0.06] px-3 py-1 text-xs font-semibold text-white/60">
                  Dark (Fixed)
                </span>
              }
            />
          </GlassCard>
        </motion.div>

        {/* Language */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.15, ease: EASE }}
        >
          <GlassCard padding="md" hoverEffect={false}>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex size-9 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E] to-[#16A34A]">
                <Globe className="size-4 text-white" aria-hidden />
              </div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-white/40">
                Language
              </h2>
            </div>
            <SettingRow
              label="Display Language"
              description="Controls the UI language across RepairVerse AI"
              control={
                <select
                  id="settings-language"
                  value={language}
                  onChange={(e) => setLanguage(e.target.value)}
                  className="rounded-2xl border border-white/15 bg-[#0B1120] px-4 py-2 text-xs font-semibold text-white focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/40"
                >
                  <option value="en">English</option>
                  <option value="es">Spanish</option>
                  <option value="fr">French</option>
                  <option value="de">German</option>
                  <option value="hi">Hindi</option>
                  <option value="zh">Chinese</option>
                </select>
              }
            />
          </GlassCard>
        </motion.div>

        {/* Danger Zone */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2, ease: EASE }}
        >
          <GlassCard padding="md" hoverEffect={false} className="border-red-500/15">
            <div className="mb-4 flex items-center gap-3">
              <div className="flex size-9 items-center justify-center rounded-2xl bg-gradient-to-br from-[#EF4444] to-[#DC2626]">
                <Trash2 className="size-4 text-white" aria-hidden />
              </div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-red-400/70">
                Danger Zone
              </h2>
            </div>
            <SettingRow
              label="Sign Out"
              description="Sign out from this device immediately"
              control={
                <GlassButton
                  variant="outline"
                  size="sm"
                  onClick={logout}
                  icon={<LogOut className="size-3.5" />}
                  className="border-red-500/40 text-red-400 hover:bg-red-500/10 hover:border-red-500/60"
                >
                  Sign Out
                </GlassButton>
              }
            />
            <SettingRow
              label="Delete Account"
              description="Permanently delete your account and all data. This action is irreversible."
              control={
                <GlassButton
                  variant="outline"
                  size="sm"
                  onClick={() => alert("Account deletion requires backend confirmation. Please contact support.")}
                  icon={<Trash2 className="size-3.5" />}
                  className="border-red-500/40 text-red-400 hover:bg-red-500/10 hover:border-red-500/60"
                >
                  Delete
                </GlassButton>
              }
            />
          </GlassCard>
        </motion.div>

      </div>
    </div>
  );
}
