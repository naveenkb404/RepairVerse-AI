"use client";

import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  AlertCircle,
  Camera,
  CheckCircle2,
  Mail,
  MapPin,
  Phone,
  Save,
  Shield,
  User,
} from "lucide-react";

import type { UserProfile } from "@/lib/types/user";
import { fetchUserProfile, updateUserProfile } from "@/lib/api/user";
import { useAuth } from "@/lib/context/AuthContext";
import GlassButton from "@/components/common/GlassButton";
import GlassCard from "@/components/glass/GlassCard";
import { cn } from "@/lib/utils";



const EASE = [0.22, 1, 0.36, 1] as const;

function InputField({
  id,
  label,
  value,
  onChange,
  type = "text",
  placeholder,
  icon: Icon,
}: {
  id: string;
  label: string;
  value: string;
  onChange: (v: string) => void;
  type?: string;
  placeholder?: string;
  icon: React.ElementType;
}) {
  return (
    <div>
      <label htmlFor={id} className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2">
        {label}
      </label>
      <div className="relative">
        <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-4 text-white/30">
          <Icon className="size-4" aria-hidden />
        </div>
        <input
          id={id}
          type={type}
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="w-full rounded-2xl border border-white/15 bg-white/[0.05] py-3 pl-11 pr-4 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50 hover:border-white/25"
        />
      </div>
    </div>
  );
}

export default function ProfilePage() {
  const { user, token, updateUser } = useAuth();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form state
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [location, setLocation] = useState("");
  const [bio, setBio] = useState("");

  useEffect(() => {
    (async () => {
      setLoading(true);
      const res = await fetchUserProfile(token ?? "");
      if (res.data) {
        const p = res.data;
        setProfile(p);
        setFullName(p.fullName);
        setEmail(p.email);
        setPhone(p.phone ?? "");
        setLocation(p.location ?? "");
        setBio(p.bio ?? "");
      }
      setLoading(false);
    })();
  }, [token]);

  const handleSave = async () => {
    setError(null);
    setSuccess(false);
    setSaving(true);
    try {
      const res = await updateUserProfile(token ?? "", { fullName, email, phone, location, bio });
      if (res.success && res.data) {
        setProfile(res.data);
        updateUser({ fullName, email, phone, location, bio });
        setSuccess(true);
        setTimeout(() => setSuccess(false), 4000);
      } else {
        setError(res.message ?? "Failed to save profile. Please try again.");
      }
    } catch {
      setError("An unexpected error occurred.");
    } finally {
      setSaving(false);
    }
  };

  const initials = fullName
    ? fullName.split(" ").map((n) => n[0]).join("").slice(0, 2).toUpperCase()
    : "??";

  return (
    <div className="relative min-h-full bg-[#0B1120] px-6 py-8 md:px-8 lg:px-10">
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_left,rgba(34,197,94,0.07),transparent_50%)]"
        aria-hidden
      />
      <div className="relative mx-auto max-w-3xl space-y-8">

        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -14 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, ease: EASE }}
        >
          <h1 className="text-2xl font-bold text-white sm:text-3xl">My Profile</h1>
          <p className="mt-1 text-sm text-white/50">
            Manage your personal information and account preferences.
          </p>
        </motion.div>

        {loading ? (
          <div className="space-y-4">
            {Array.from({ length: 3 }).map((_, i) => (
              <div key={i} className="h-24 animate-pulse rounded-3xl bg-white/[0.04]" />
            ))}
          </div>
        ) : (
          <>
            {/* Avatar + role card */}
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.05, ease: EASE }}
            >
              <GlassCard padding="md" glowColor="green" hoverEffect={false}>
                <div className="flex flex-col items-center gap-6 sm:flex-row">
                  {/* Avatar */}
                  <div className="relative shrink-0">
                    <div className="flex size-20 items-center justify-center rounded-3xl bg-gradient-to-br from-[#22C55E] to-[#06B6D4] text-2xl font-bold text-white shadow-[0_0_32px_rgba(34,197,94,0.4)]">
                      {initials}
                    </div>
                    <button
                      className="absolute -bottom-1 -right-1 flex size-7 items-center justify-center rounded-full border border-white/20 bg-[#0B1120] text-white/60 hover:text-white transition-colors focus:outline-none"
                      aria-label="Change avatar"
                      onClick={() => alert("Avatar upload will be enabled when Cloudinary storage is configured.")}
                    >
                      <Camera className="size-3.5" />
                    </button>
                  </div>

                  {/* Info */}
                  <div className="flex-1 text-center sm:text-left">
                    <h2 className="text-xl font-bold text-white">{fullName || "—"}</h2>
                    <p className="text-sm text-white/50">{email}</p>
                    <div className="mt-3 flex flex-wrap items-center justify-center gap-2 sm:justify-start">
                      <span className="flex items-center gap-1.5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3 py-1 text-xs font-semibold text-[#22C55E]">
                        <Shield className="size-3" aria-hidden />
                        {profile?.role ?? "USER"}
                      </span>
                      {profile?.verified && (
                        <span className="flex items-center gap-1.5 rounded-full border border-[#06B6D4]/30 bg-[#06B6D4]/10 px-3 py-1 text-xs font-semibold text-[#06B6D4]">
                          <CheckCircle2 className="size-3" aria-hidden />
                          Verified
                        </span>
                      )}
                    </div>
                  </div>
                  <div className="shrink-0">
                    <span className="text-xs text-white/30">
                      Member since {profile?.joinedAt ? new Date(profile.joinedAt).toLocaleDateString("en-US", { year: "numeric", month: "long" }) : "—"}
                    </span>
                  </div>
                </div>
              </GlassCard>
            </motion.div>

            {/* Alerts */}
            {success && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex items-center gap-3 rounded-2xl border border-[#22C55E]/40 bg-[#22C55E]/10 px-4 py-3 text-sm text-[#22C55E]"
              >
                <CheckCircle2 className="size-4 shrink-0" aria-hidden />
                Profile updated successfully!
              </motion.div>
            )}
            {error && (
              <motion.div
                initial={{ opacity: 0, y: -8 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex items-center gap-3 rounded-2xl border border-red-500/40 bg-red-500/10 px-4 py-3 text-sm text-red-300"
              >
                <AlertCircle className="size-4 shrink-0" aria-hidden />
                {error}
              </motion.div>
            )}

            {/* Form */}
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.1, ease: EASE }}
            >
              <GlassCard padding="md" hoverEffect={false}>
                <h3 className="mb-6 text-sm font-bold uppercase tracking-widest text-white/40">
                  Personal Information
                </h3>
                <div className="grid gap-5 sm:grid-cols-2">
                  <InputField
                    id="profile-fullname"
                    label="Full Name"
                    value={fullName}
                    onChange={setFullName}
                    placeholder="Your full name"
                    icon={User}
                  />
                  <InputField
                    id="profile-email"
                    label="Email Address"
                    value={email}
                    onChange={setEmail}
                    type="email"
                    placeholder="your@email.com"
                    icon={Mail}
                  />
                  <InputField
                    id="profile-phone"
                    label="Phone Number"
                    value={phone}
                    onChange={setPhone}
                    type="tel"
                    placeholder="+1 (555) 000-0000"
                    icon={Phone}
                  />
                  <InputField
                    id="profile-location"
                    label="Location"
                    value={location}
                    onChange={setLocation}
                    placeholder="City, Country"
                    icon={MapPin}
                  />
                  <div className="sm:col-span-2">
                    <label htmlFor="profile-bio" className="block text-xs font-semibold uppercase tracking-wider text-[#CBD5E1] mb-2">
                      Bio
                    </label>
                    <textarea
                      id="profile-bio"
                      value={bio}
                      onChange={(e) => setBio(e.target.value)}
                      rows={3}
                      placeholder="Tell us a little about yourself..."
                      className="w-full resize-none rounded-2xl border border-white/15 bg-white/[0.05] px-4 py-3 text-sm text-white placeholder-white/30 backdrop-blur-xl transition-all focus:border-[#22C55E] focus:outline-none focus:ring-2 focus:ring-[#22C55E]/50 hover:border-white/25"
                    />
                  </div>
                </div>
                <div className="mt-6 flex justify-end">
                  <GlassButton
                    onClick={handleSave}
                    disabled={saving}
                    icon={<Save className="size-4" />}
                  >
                    {saving ? "Saving..." : "Save Changes"}
                  </GlassButton>
                </div>
              </GlassCard>
            </motion.div>

            {/* Security Section */}
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.15, ease: EASE }}
            >
              <GlassCard padding="md" hoverEffect={false}>
                <h3 className="mb-6 text-sm font-bold uppercase tracking-widest text-white/40">
                  Security
                </h3>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-semibold text-white">Password</p>
                    <p className="mt-0.5 text-xs text-white/50">
                      Last changed: never (connect backend mailer to enable)
                    </p>
                  </div>
                  <GlassButton
                    variant="secondary"
                    size="sm"
                    onClick={() => alert("Password reset requires backend mailer service at localhost:8080.")}
                  >
                    Change Password
                  </GlassButton>
                </div>
              </GlassCard>
            </motion.div>
          </>
        )}
      </div>
    </div>
  );
}
