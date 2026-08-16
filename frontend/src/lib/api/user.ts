import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession, DEMO_USER_PROFILE } from "@/lib/demo";
import type { UserProfile, DashboardStats, ActivityItem, Notification } from "@/lib/types/user";

// ---------------------------------------------------------------------------
// Sample reference data (displayed when Spring Boot backend is offline)
// ---------------------------------------------------------------------------

export const SAMPLE_PROFILE: UserProfile = DEMO_USER_PROFILE;

export const SAMPLE_STATS: DashboardStats = {
  totalDevices: 4,
  totalRepairs: 9,
  totalCarbonSaved: 47.3,
  totalMoneySaved: 1240,
  healthScore: 84,
  activeRepairs: 1,
};

export const SAMPLE_ACTIVITY: ActivityItem[] = [
  {
    id: "act-001",
    type: "repair_complete",
    title: "iPhone 13 Screen Repair Completed",
    description: "Screen replaced successfully at TechCare Express. 6-month warranty active.",
    timestamp: new Date(Date.now() - 2 * 3600 * 1000).toISOString(),
    deviceName: "iPhone 13",
    iconColor: "green",
  },
  {
    id: "act-002",
    type: "diagnosis_run",
    title: "AI Diagnosis — MacBook Pro Battery",
    description: "Battery degradation detected. Repair confidence: 91%.",
    timestamp: new Date(Date.now() - 1 * 24 * 3600 * 1000).toISOString(),
    deviceName: "MacBook Pro 14\"",
    iconColor: "cyan",
  },
  {
    id: "act-003",
    type: "device_added",
    title: "PlayStation 5 Added to Passport",
    description: "Device health passport created. Initial health score: 78/100.",
    timestamp: new Date(Date.now() - 3 * 24 * 3600 * 1000).toISOString(),
    deviceName: "PlayStation 5",
    iconColor: "cyan",
  },
  {
    id: "act-004",
    type: "passport_updated",
    title: "iPad Air Passport Updated",
    description: "Battery health updated to 82%. Repair history synced.",
    timestamp: new Date(Date.now() - 5 * 24 * 3600 * 1000).toISOString(),
    deviceName: "iPad Air",
    iconColor: "yellow",
  },
  {
    id: "act-005",
    type: "shop_booked",
    title: "Appointment Booked — GreenCircuit Lab",
    description: "Screen repair appointment confirmed for MacBook Pro.",
    timestamp: new Date(Date.now() - 7 * 24 * 3600 * 1000).toISOString(),
    iconColor: "green",
  },
];

export const SAMPLE_NOTIFICATIONS: Notification[] = [
  {
    id: "notif-001",
    type: "repair",
    title: "Repair Completed",
    message: "Your iPhone 13 screen repair is complete and ready for pickup at TechCare Express.",
    isRead: false,
    createdAt: new Date(Date.now() - 2 * 3600 * 1000).toISOString(),
    actionUrl: "/repair-history",
    actionLabel: "View Repair",
    iconColor: "green",
  },
  {
    id: "notif-002",
    type: "diagnosis",
    title: "AI Diagnosis Ready",
    message: "Your MacBook Pro battery diagnosis is complete. View the full report.",
    isRead: false,
    createdAt: new Date(Date.now() - 6 * 3600 * 1000).toISOString(),
    actionUrl: "/diagnosis",
    actionLabel: "View Report",
    iconColor: "cyan",
  },
  {
    id: "notif-003",
    type: "achievement",
    title: "Sustainability Milestone!",
    message: "You've prevented over 45 kg of CO2 emissions by repairing your devices. Amazing!",
    isRead: false,
    createdAt: new Date(Date.now() - 1 * 24 * 3600 * 1000).toISOString(),
    actionUrl: "/carbon",
    actionLabel: "View Impact",
    iconColor: "green",
  },
  {
    id: "notif-004",
    type: "device",
    title: "Warranty Expiring Soon",
    message: "The warranty on your PlayStation 5 repair expires in 30 days.",
    isRead: true,
    createdAt: new Date(Date.now() - 3 * 24 * 3600 * 1000).toISOString(),
    actionUrl: "/devices",
    actionLabel: "View Device",
    iconColor: "yellow",
  },
  {
    id: "notif-005",
    type: "system",
    title: "Profile Verified",
    message: "Your RepairVerse AI account has been verified. Enjoy all premium features.",
    isRead: true,
    createdAt: new Date(Date.now() - 5 * 24 * 3600 * 1000).toISOString(),
    iconColor: "cyan",
  },
];

// ---------------------------------------------------------------------------
// API Functions
// ---------------------------------------------------------------------------

/** Fetch current authenticated user profile */
export async function fetchUserProfile(
  token: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: UserProfile; message?: string; isDemo?: boolean }> {
  if (isDemoSession(token)) {
    return { success: true, data: SAMPLE_PROFILE, isDemo: true };
  }

  const result = await apiClient<UserProfile>("/users/profile", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return {
    success: true,
    data: SAMPLE_PROFILE,
    isDemo: true,
    message: `Backend profile service at ${API_BASE_URL}/users/profile is offline. Displaying demo user profile.`,
  };
}

/** Fetch dashboard statistics */
export async function fetchDashboardStats(
  token: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: DashboardStats; isDemo?: boolean }> {
  if (isDemoSession(token)) {
    return { success: true, data: SAMPLE_STATS, isDemo: true };
  }

  const result = await apiClient<DashboardStats>("/dashboard", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return { success: true, data: SAMPLE_STATS, isDemo: true };
}

/** Fetch activity feed */
export async function fetchActivity(
  token: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: ActivityItem[]; isDemo?: boolean }> {
  if (isDemoSession(token)) {
    return { success: true, data: SAMPLE_ACTIVITY, isDemo: true };
  }

  const result = await apiClient<ActivityItem[]>("/dashboard/activity", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return { success: true, data: SAMPLE_ACTIVITY, isDemo: true };
}

/** Fetch user notifications */
export async function fetchNotifications(
  token: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: Notification[]; isDemo?: boolean }> {
  if (isDemoSession(token)) {
    return { success: true, data: SAMPLE_NOTIFICATIONS, isDemo: true };
  }

  const result = await apiClient<Notification[]>("/notifications", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return { success: true, data: SAMPLE_NOTIFICATIONS, isDemo: true };
}

/** Mark a notification as read */
export async function markNotificationRead(
  token: string,
  notifId: string,
  signal?: AbortSignal
): Promise<{ success: boolean; message?: string }> {
  if (isDemoSession(token)) {
    const notif = SAMPLE_NOTIFICATIONS.find((n) => n.id === notifId);
    if (notif) notif.isRead = true;
    return { success: true, message: "Notification marked as read (Demo Mode)" };
  }

  const result = await apiClient(`/notifications/${notifId}/read`, {
    method: "PUT",
    token,
    signal,
  });

  return { success: result.success, message: result.message };
}

/** Mark all notifications as read */
export async function markAllNotificationsRead(
  token: string,
  signal?: AbortSignal
): Promise<{ success: boolean; message?: string }> {
  if (isDemoSession(token)) {
    SAMPLE_NOTIFICATIONS.forEach((n) => (n.isRead = true));
    return { success: true, message: "All notifications marked as read (Demo Mode)" };
  }

  const result = await apiClient("/notifications/read-all", {
    method: "PUT",
    token,
    signal,
  });

  return { success: result.success, message: result.message };
}

/** Update user profile */
export async function updateUserProfile(
  token: string,
  data: Partial<UserProfile>,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: UserProfile; message?: string }> {
  if (isDemoSession(token)) {
    const updated = { ...SAMPLE_PROFILE, ...data };
    return {
      success: true,
      data: updated,
      message: "Profile updated locally (Demo Mode). Connect backend to persist across devices.",
    };
  }

  const result = await apiClient<UserProfile>("/users/profile", {
    method: "PUT",
    token,
    body: data,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, message: result.message || "Profile updated successfully" };
  }

  return {
    success: false,
    message: result.message || `Failed to update profile. Backend API at ${API_BASE_URL}/users/profile is offline.`,
  };
}
