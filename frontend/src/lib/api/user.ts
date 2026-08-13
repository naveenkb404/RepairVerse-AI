import type { UserProfile, DashboardStats, ActivityItem, Notification } from "@/lib/types/user";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

// ---------------------------------------------------------------------------
// Sample reference data (displayed when Spring Boot backend is offline)
// ---------------------------------------------------------------------------

export const SAMPLE_PROFILE: UserProfile = {
  id: "demo-user-001",
  fullName: "Alex Johnson",
  email: "alex.johnson@example.com",
  role: "USER",
  phone: "+1 (555) 012-3456",
  location: "San Francisco, CA",
  bio: "Electronics enthusiast and sustainability advocate. I believe in repairing over replacing.",
  joinedAt: "2024-06-15T10:00:00Z",
  lastLogin: new Date().toISOString(),
  verified: true,
  preferences: {
    notifications: true,
    newsletter: true,
    theme: "dark",
    language: "en",
  },
};

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
    description: "Screen replaced successfully at iRepair Pro. 6-month warranty active.",
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
    title: "Appointment Booked — TechFix Center",
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
    message: "Your iPhone 13 screen repair is complete and ready for pickup at iRepair Pro.",
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
export async function fetchUserProfile(token: string): Promise<{ success: boolean; data?: UserProfile; message?: string }> {
  try {
    const res = await fetch(`${API_BASE_URL}/users/profile`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const json = await res.json();
    return { success: true, data: json.data || json };
  } catch {
    // Offline fallback
    return { success: true, data: SAMPLE_PROFILE };
  }
}

/** Fetch dashboard statistics */
export async function fetchDashboardStats(token: string): Promise<{ success: boolean; data?: DashboardStats }> {
  try {
    const res = await fetch(`${API_BASE_URL}/dashboard`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const json = await res.json();
    return { success: true, data: json.data || json };
  } catch {
    return { success: true, data: SAMPLE_STATS };
  }
}

/** Fetch activity feed */
export async function fetchActivity(token: string): Promise<{ success: boolean; data?: ActivityItem[] }> {
  try {
    const res = await fetch(`${API_BASE_URL}/dashboard/activity`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const json = await res.json();
    return { success: true, data: json.data || json };
  } catch {
    return { success: true, data: SAMPLE_ACTIVITY };
  }
}

/** Fetch user notifications */
export async function fetchNotifications(token: string): Promise<{ success: boolean; data?: Notification[] }> {
  try {
    const res = await fetch(`${API_BASE_URL}/notifications`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const json = await res.json();
    return { success: true, data: json.data || json };
  } catch {
    return { success: true, data: SAMPLE_NOTIFICATIONS };
  }
}

/** Mark a notification as read */
export async function markNotificationRead(token: string, notifId: string): Promise<void> {
  try {
    await fetch(`${API_BASE_URL}/notifications/${notifId}/read`, {
      method: "PUT",
      headers: { Authorization: `Bearer ${token}` },
    });
  } catch {
    // Silently fail for offline scenario
  }
}

/** Update user profile */
export async function updateUserProfile(
  token: string,
  data: Partial<UserProfile>
): Promise<{ success: boolean; data?: UserProfile; message?: string }> {
  try {
    const res = await fetch(`${API_BASE_URL}/users/profile`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const json = await res.json();
    return { success: true, data: json.data || json };
  } catch {
    // Return the submitted data merged with sample as offline response
    return { success: true, data: { ...SAMPLE_PROFILE, ...data } };
  }
}
