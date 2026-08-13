export type UserRole = "USER" | "TECHNICIAN" | "ADMIN";

export type UserProfile = {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
  avatarUrl?: string;
  phone?: string;
  location?: string;
  bio?: string;
  joinedAt: string;
  lastLogin?: string;
  verified: boolean;
  preferences?: UserPreferences;
};

export type UserPreferences = {
  notifications: boolean;
  newsletter: boolean;
  theme: "dark" | "light" | "system";
  language: string;
};

export type Notification = {
  id: string;
  type: "repair" | "diagnosis" | "device" | "shop" | "system" | "achievement";
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
  actionUrl?: string;
  actionLabel?: string;
  iconColor?: "green" | "cyan" | "yellow" | "red";
};

export type DashboardStats = {
  totalDevices: number;
  totalRepairs: number;
  totalCarbonSaved: number; // kg CO2
  totalMoneySaved: number; // USD
  healthScore: number; // 0-100
  activeRepairs: number;
};

export type ActivityItem = {
  id: string;
  type: "repair_complete" | "device_added" | "diagnosis_run" | "shop_booked" | "passport_updated";
  title: string;
  description: string;
  timestamp: string;
  deviceName?: string;
  iconColor?: "green" | "cyan" | "yellow" | "red";
};
