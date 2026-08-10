import { ApiResponse } from "@/lib/types/auth";

export type DeviceCategory =
  | "Smartphone"
  | "Laptop"
  | "Tablet"
  | "Gaming Console"
  | "Smartwatch"
  | "Audio Device"
  | "Other";

export type DeviceCondition =
  | "Excellent"
  | "Good"
  | "Fair"
  | "Needs Attention"
  | "Needs Repair";

/**
 * Matches Devices table in DATABASE_SCHEMA.md Module 2
 */
export type Device = {
  id: string;
  userId?: string;
  deviceName: string;
  category: DeviceCategory | string;
  brand: string;
  model: string;
  serialNumber?: string;
  purchaseDate?: string;
  warrantyExpiry?: string;
  purchasePrice?: number;
  currentCondition: DeviceCondition | string;
  createdAt?: string;
};

/**
 * Matches DeviceHealth table in DATABASE_SCHEMA.md Module 2
 */
export type DeviceHealth = {
  id?: string;
  deviceId: string;
  batteryHealth?: number; // 0 - 100 percentage
  healthScore: number; // 0 - 100 score
  lastService?: string;
  maintenanceDue?: string;
  aiPrediction?: string;
};

export type DeviceLifecycleEvent = {
  id: string;
  date: string;
  title: string;
  type: "purchase" | "diagnosis" | "service" | "inspection";
  description: string;
};

export type DiagnosisSummary = {
  probableIssue: string;
  confidenceScore: number;
  repairDifficulty: string;
  repairCost: number;
  lastDiagnosisDate: string;
};

export type RepairSummary = {
  repairsCompleted: number;
  lastRepairDate?: string;
  lastRecommendedAction?: string;
};

export type CarbonSummary = {
  co2SavedKg: number;
  ewasteReducedKg: number;
  moneySaved: number;
};

export type DevicePassportData = {
  device: Device;
  health: DeviceHealth;
  diagnosisSummary?: DiagnosisSummary;
  repairSummary?: RepairSummary;
  carbonSummary?: CarbonSummary;
  lifecycleTimeline: DeviceLifecycleEvent[];
};

export type CreateDeviceRequest = {
  deviceName: string;
  category: string;
  brand: string;
  model: string;
  serialNumber?: string;
  purchaseDate?: string;
  warrantyExpiry?: string;
  purchasePrice?: number;
  currentCondition: string;
};

export type DeviceListResponse = ApiResponse<Device[]>;
export type DeviceDetailResponse = ApiResponse<Device>;
export type DevicePassportResponse = ApiResponse<DevicePassportData>;
