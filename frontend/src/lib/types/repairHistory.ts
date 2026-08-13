import { ApiResponse } from "@/lib/types/auth";

// Augmented response types that include demo-mode flag
export type RepairHistoryListApiResponse = ApiResponse<RepairHistoryItem[]> & { isDemo?: boolean };
export type RepairHistoryDetailApiResponse = ApiResponse<RepairHistoryItem> & { isDemo?: boolean };

export type RepairStatus =
  | "Completed"
  | "In Progress"
  | "Scheduled"
  | "Cancelled";

export type RepairHistoryPart = {
  id: string;
  name: string;
  quantity: number;
  cost: number;
  partNumber?: string;
};

export type RepairHistoryTechnician = {
  id?: string;
  name: string;
  role?: string;
  phone?: string;
  shopName?: string;
  isVerified?: boolean;
};

export type RepairHistoryShop = {
  id?: string;
  name: string;
  address: string;
  phone?: string;
  rating?: number;
  latitude?: number;
  longitude?: number;
};

export type RepairHistoryDevice = {
  id: string;
  name: string;
  brand: string;
  model: string;
  category: string;
  serialNumber?: string;
};

export type RepairTimelineStage = {
  id: string;
  date: string;
  title: string;
  status: "completed" | "current" | "pending";
  description: string;
};

/**
 * Matches RepairHistory table in DATABASE_SCHEMA.md Module 4
 */
export type RepairHistoryItem = {
  id: string;
  deviceId: string;
  device: RepairHistoryDevice;
  repairType: string;
  repairDate: string;
  status: RepairStatus;
  description: string;
  diagnosisIssue?: string;
  diagnosisConfidence?: number;
  technician?: RepairHistoryTechnician;
  shop?: RepairHistoryShop;
  parts: RepairHistoryPart[];
  partsCost: number;
  laborCost: number;
  totalCost: number;
  repairDuration: string;
  warrantyPeriod?: string;
  warrantyUntil?: string;
  isWarrantyActive?: boolean;
  co2SavedKg?: number;
  ewasteReducedKg?: number;
  moneySaved?: number;
  notes?: string;
  timeline?: RepairTimelineStage[];
};

export type RepairHistorySummary = {
  totalRepairs: number;
  completedRepairs: number;
  inProgressRepairs: number;
  totalSpent: number;
  totalSavedMoney: number;
  totalCo2SavedKg: number;
  totalEwasteReducedKg: number;
};

/** @deprecated Use RepairHistoryListApiResponse which includes isDemo flag */
export type RepairHistoryListResponse = RepairHistoryListApiResponse;
/** @deprecated Use RepairHistoryDetailApiResponse which includes isDemo flag */
export type RepairHistoryDetailResponse = RepairHistoryDetailApiResponse;
