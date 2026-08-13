"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { motion } from "framer-motion";
import {
  Bell,
  CheckCheck,
  ChevronRight,
  Cpu,
  Sparkles,
  Star,
  Trophy,
  Wrench,
  Zap,
} from "lucide-react";

import type { Notification } from "@/lib/types/user";
import { fetchNotifications, markNotificationRead } from "@/lib/api/user";
import { useAuth } from "@/lib/context/AuthContext";
import GlassButton from "@/components/common/GlassButton";
import { cn } from "@/lib/utils";



const EASE = [0.22, 1, 0.36, 1] as const;

const TYPE_ICONS: Record<string, React.ElementType> = {
  repair: Wrench,
  diagnosis: Sparkles,
  device: Cpu,
  shop: Zap,
  system: Bell,
  achievement: Trophy,
};

const ICON_COLORS = {
  green: "from-[#22C55E] to-[#16A34A]",
  cyan: "from-[#06B6D4] to-[#0891B2]",
  yellow: "from-[#FACC15] to-[#D97706]",
  red: "from-[#EF4444] to-[#DC2626]",
};

function formatRelative(dateStr: string) {
  const diff = Date.now() - new Date(dateStr).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.floor(diff / 3600000);
  if (hours < 24) return `${hours}h ago`;
  const days = Math.floor(diff / 86400000);
  return `${days}d ago`;
}

function NotificationCard({
  notif,
  onMarkRead,
}: {
  notif: Notification;
  onMarkRead: (id: string) => void;
}) {
  const Icon = TYPE_ICONS[notif.type] ?? Bell;
  const iconGrad = ICON_COLORS[notif.iconColor ?? "green"];

  return (
    <motion.div
      layout
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3, ease: EASE }}
      className={cn(
        "group relative rounded-3xl border p-5 transition-all duration-200",
        notif.isRead
          ? "border-white/[0.06] bg-white/[0.03]"
          : "border-[#22C55E]/20 bg-white/[0.06] shadow-[0_0_20px_rgba(34,197,94,0.06)]"
      )}
    >
      {!notif.isRead && (
        <span className="absolute right-4 top-4 size-2 rounded-full bg-[#22C55E] shadow-[0_0_8px_rgba(34,197,94,0.7)]" />
      )}
      <div className="flex items-start gap-4">
        <div
          className={cn(
            "mt-0.5 flex size-10 shrink-0 items-center justify-center rounded-2xl bg-gradient-to-br",
            iconGrad
          )}
        >
          <Icon className="size-4.5 text-white" aria-hidden />
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-2">
            <p className={cn("text-sm font-bold", notif.isRead ? "text-white/70" : "text-white")}>
              {notif.title}
            </p>
            <span className="shrink-0 text-[11px] text-white/30">{formatRelative(notif.createdAt)}</span>
          </div>
          <p className="mt-1 text-xs leading-relaxed text-white/50">{notif.message}</p>
          <div className="mt-3 flex items-center gap-3 flex-wrap">
            {notif.actionUrl && notif.actionLabel && (
              <Link
                href={notif.actionUrl}
                className="inline-flex items-center gap-1.5 rounded-full border border-[#22C55E]/30 bg-[#22C55E]/10 px-3 py-1 text-xs font-semibold text-[#22C55E] transition-all hover:bg-[#22C55E]/20 hover:border-[#22C55E]/50"
              >
                {notif.actionLabel}
                <ChevronRight className="size-3" aria-hidden />
              </Link>
            )}
            {!notif.isRead && (
              <button
                onClick={() => onMarkRead(notif.id)}
                className="inline-flex items-center gap-1.5 text-xs text-white/30 hover:text-white/60 transition-colors focus:outline-none"
              >
                <CheckCheck className="size-3" />
                Mark as read
              </button>
            )}
          </div>
        </div>
      </div>
    </motion.div>
  );
}

export default function NotificationsPage() {
  const { token } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<"all" | "unread">("all");

  useEffect(() => {
    (async () => {
      setLoading(true);
      const res = await fetchNotifications(token ?? "");
      if (res.data) setNotifications(res.data);
      setLoading(false);
    })();
  }, [token]);

  const handleMarkRead = async (id: string) => {
    await markNotificationRead(token ?? "", id);
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
    );
  };

  const handleMarkAllRead = async () => {
    const unread = notifications.filter((n) => !n.isRead);
    await Promise.all(unread.map((n) => markNotificationRead(token ?? "", n.id)));
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  const unreadCount = notifications.filter((n) => !n.isRead).length;
  const filtered = filter === "unread" ? notifications.filter((n) => !n.isRead) : notifications;

  return (
    <div className="relative min-h-full bg-[#0B1120] px-6 py-8 md:px-8 lg:px-10">
      <div
        className="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,rgba(6,182,212,0.07),transparent_50%)]"
        aria-hidden
      />
      <div className="relative mx-auto max-w-2xl space-y-8">

        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -14 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, ease: EASE }}
          className="flex items-start justify-between"
        >
          <div>
            <h1 className="text-2xl font-bold text-white sm:text-3xl">Notifications</h1>
            <p className="mt-1 text-sm text-white/50">
              {unreadCount > 0 ? `${unreadCount} unread notification${unreadCount !== 1 ? "s" : ""}` : "All caught up!"}
            </p>
          </div>
          {unreadCount > 0 && (
            <GlassButton
              variant="secondary"
              size="sm"
              onClick={handleMarkAllRead}
              icon={<CheckCheck className="size-4" />}
            >
              Mark all read
            </GlassButton>
          )}
        </motion.div>

        {/* Filter Tabs */}
        <motion.div
          initial={{ opacity: 0, y: 8 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.05, ease: EASE }}
          className="flex gap-2"
        >
          {(["all", "unread"] as const).map((tab) => (
            <button
              key={tab}
              onClick={() => setFilter(tab)}
              className={cn(
                "rounded-full border px-4 py-2 text-xs font-semibold capitalize transition-all focus:outline-none focus:ring-2 focus:ring-[#22C55E]/40",
                filter === tab
                  ? "border-[#22C55E]/40 bg-[#22C55E]/15 text-[#22C55E]"
                  : "border-white/10 bg-transparent text-white/50 hover:text-white hover:border-white/20"
              )}
            >
              {tab}
              {tab === "unread" && unreadCount > 0 && (
                <span className="ml-1.5 rounded-full bg-[#22C55E] px-1.5 py-0.5 text-[10px] font-bold text-white">
                  {unreadCount}
                </span>
              )}
            </button>
          ))}
        </motion.div>

        {/* Notifications List */}
        {loading ? (
          <div className="space-y-3">
            {Array.from({ length: 4 }).map((_, i) => (
              <div key={i} className="h-28 animate-pulse rounded-3xl bg-white/[0.04]" />
            ))}
          </div>
        ) : filtered.length === 0 ? (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="flex flex-col items-center gap-4 rounded-3xl border border-white/10 bg-white/[0.04] py-16 text-center"
          >
            <div className="flex size-14 items-center justify-center rounded-2xl bg-white/[0.06]">
              <Bell className="size-6 text-white/20" aria-hidden />
            </div>
            <p className="text-sm text-white/40">
              {filter === "unread" ? "No unread notifications" : "No notifications yet"}
            </p>
          </motion.div>
        ) : (
          <div className="space-y-3">
            {filtered.map((n) => (
              <NotificationCard key={n.id} notif={n} onMarkRead={handleMarkRead} />
            ))}
          </div>
        )}

      </div>
    </div>
  );
}
