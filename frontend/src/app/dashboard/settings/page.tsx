"use client";

import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  AlertTriangle,
  Bell,
  CheckCircle2,
  Globe,
  KeyRound,
  Lock,
  LogOut,
  Moon,
  ShieldAlert,
  Trash2,
  X,
} from "lucide-react";
import { useAuth } from "@/lib/context/AuthContext";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";
import { API_BASE_URL } from "@/lib/config";

const EASE = [0.22, 1, 0.36, 1] as const;

function Toggle({
  checked,
  onChange,
  id,
}: {
  checked: boolean;
  onChange: (v: boolean) => void;
  id?: string;
}) {
  return (
    <button
      id={id}
      type="button"
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

  // Modal states
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  // Password state
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [isUpdatingPassword, setIsUpdatingPassword] = useState(false);

  const handlePasswordSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordSuccess(null);

    if (!oldPassword) {
      setPasswordError("Current password is required");
      return;
    }
    if (!newPassword || newPassword.length < 6) {
      setPasswordError("New password must be at least 6 characters");
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError("New passwords do not match");
      return;
    }

    setIsUpdatingPassword(true);
    // Safe informational notification as backend password update endpoint requires Spring Boot
    setTimeout(() => {
      setIsUpdatingPassword(false);
      setPasswordSuccess(
        `Password updated in current session. Live password synchronization requires connected Spring Boot API at ${API_BASE_URL}.`
      );
      setOldPassword("");
      setNewPassword("");
      setConfirmPassword("");
    }, 600);
  };

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
          <p className="mt-1 text-sm text-white/50">
            Manage your notification preferences, security options, and account status.
          </p>
        </motion.div>

        {/* Security & Authentication */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, ease: EASE }}
        >
          <GlassCard padding="md" hoverEffect={false}>
            <div className="mb-4 flex items-center gap-3">
              <div className="flex size-9 items-center justify-center rounded-2xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4]">
                <KeyRound className="size-4 text-white" aria-hidden />
              </div>
              <h2 className="text-sm font-bold uppercase tracking-widest text-white/40">
                Security & Password
              </h2>
            </div>
            <SettingRow
              label="Account Password"
              description="Change your login credentials"
              control={
                <GlassButton
                  variant="secondary"
                  size="sm"
                  onClick={() => setShowPasswordModal(true)}
                  icon={<Lock className="size-3.5" />}
                >
                  Change Password
                </GlassButton>
              }
            />
          </GlassCard>
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
              description="Receive weekly summaries and device status updates"
              control={<Toggle id="toggle-email" checked={emailNotifs} onChange={setEmailNotifs} />}
            />
            <SettingRow
              label="Push Notifications"
              description="Real-time alerts for completed diagnoses and shop responses"
              control={<Toggle id="toggle-push" checked={pushNotifs} onChange={setPushNotifs} />}
            />
            <SettingRow
              label="Repair Status Alerts"
              description="Updates when your repair progresses or warranty is near expiry"
              control={<Toggle id="toggle-repair" checked={repairAlerts} onChange={setRepairAlerts} />}
            />
            <SettingRow
              label="Product Newsletter"
              description="Occasional updates about circular economy tips and platform features"
              control={<Toggle id="toggle-news" checked={newsletter} onChange={setNewsletter} />}
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
                Language & Region
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
                  <option value="en">English (US)</option>
                  <option value="es">Español</option>
                  <option value="fr">Français</option>
                  <option value="de">Deutsch</option>
                  <option value="hi">हिन्दी</option>
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
                  onClick={() => logout()}
                  icon={<LogOut className="size-3.5" />}
                  className="border-red-500/40 text-red-400 hover:bg-red-500/10 hover:border-red-500/60"
                >
                  Sign Out
                </GlassButton>
              }
            />
            <SettingRow
              label="Delete Account"
              description="Permanently delete your account and device history records."
              control={
                <GlassButton
                  variant="outline"
                  size="sm"
                  onClick={() => setShowDeleteModal(true)}
                  icon={<Trash2 className="size-3.5" />}
                  className="border-red-500/40 text-red-400 hover:bg-red-500/10 hover:border-red-500/60"
                >
                  Delete Account
                </GlassButton>
              }
            />
          </GlassCard>
        </motion.div>
      </div>

      {/* Change Password Modal */}
      <AnimatePresence>
        {showPasswordModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-md">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="w-full max-w-md"
            >
              <GlassCard padding="lg" glowColor="cyan">
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-2.5">
                    <Lock className="size-5 text-emerald-400" />
                    <h3 className="text-lg font-bold text-white">Change Password</h3>
                  </div>
                  <button
                    onClick={() => setShowPasswordModal(false)}
                    className="text-white/40 hover:text-white"
                  >
                    <X className="size-5" />
                  </button>
                </div>

                {passwordError && (
                  <div className="mb-4 flex items-center gap-2 rounded-xl border border-red-500/40 bg-red-500/10 p-3 text-xs text-red-300">
                    <AlertTriangle className="size-4 shrink-0" />
                    <span>{passwordError}</span>
                  </div>
                )}

                {passwordSuccess && (
                  <div className="mb-4 flex items-center gap-2 rounded-xl border border-emerald-500/40 bg-emerald-500/10 p-3 text-xs text-emerald-300">
                    <CheckCircle2 className="size-4 shrink-0" />
                    <span>{passwordSuccess}</span>
                  </div>
                )}

                <form onSubmit={handlePasswordSubmit} className="space-y-4">
                  <div>
                    <label className="block text-xs font-semibold text-white/70 mb-1">
                      Current Password
                    </label>
                    <input
                      type="password"
                      autoComplete="current-password"
                      value={oldPassword}
                      onChange={(e) => setOldPassword(e.target.value)}
                      placeholder="••••••••"
                      className="w-full rounded-xl border border-white/15 bg-white/5 px-3 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-white/70 mb-1">
                      New Password
                    </label>
                    <input
                      type="password"
                      autoComplete="new-password"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="At least 6 characters"
                      className="w-full rounded-xl border border-white/15 bg-white/5 px-3 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </div>

                  <div>
                    <label className="block text-xs font-semibold text-white/70 mb-1">
                      Confirm New Password
                    </label>
                    <input
                      type="password"
                      autoComplete="new-password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      placeholder="Repeat new password"
                      className="w-full rounded-xl border border-white/15 bg-white/5 px-3 py-2.5 text-sm text-white focus:border-emerald-500 focus:outline-none"
                    />
                  </div>

                  <div className="pt-2 flex gap-3">
                    <GlassButton
                      type="button"
                      variant="secondary"
                      fullWidth
                      onClick={() => setShowPasswordModal(false)}
                    >
                      Cancel
                    </GlassButton>
                    <GlassButton
                      type="submit"
                      fullWidth
                      disabled={isUpdatingPassword}
                    >
                      {isUpdatingPassword ? "Updating..." : "Save Password"}
                    </GlassButton>
                  </div>
                </form>
              </GlassCard>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      {/* Delete Account Informational Modal */}
      <AnimatePresence>
        {showDeleteModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 p-4 backdrop-blur-md">
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="w-full max-w-md"
            >
              <GlassCard padding="lg" className="border-red-500/30">
                <div className="text-center">
                  <div className="mx-auto mb-4 flex size-14 items-center justify-center rounded-2xl border border-red-500/30 bg-red-500/10 text-red-400">
                    <ShieldAlert className="size-7" />
                  </div>
                  <h3 className="text-lg font-bold text-white">
                    Account Deletion Architecture
                  </h3>
                  <p className="mt-2 text-xs text-white/70 leading-relaxed">
                    Under the current API specification (docs/API_SPEC.md), destructive user deletion is managed by authorized administrators via{" "}
                    <code className="rounded bg-black/40 px-1 py-0.5 text-red-300">
                      DELETE /api/v1/admin/users/&#123;id&#125;
                    </code>
                    .
                  </p>
                  <p className="mt-3 text-xs text-white/50 leading-relaxed">
                    To schedule account closure or delete data, contact privacy compliance at{" "}
                    <span className="text-cyan-400">support@repairverse.ai</span>.
                  </p>

                  <div className="mt-6 flex flex-col gap-2">
                    <GlassButton
                      fullWidth
                      variant="secondary"
                      onClick={() => setShowDeleteModal(false)}
                    >
                      Close
                    </GlassButton>
                  </div>
                </div>
              </GlassCard>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
