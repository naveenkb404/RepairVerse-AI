import { http } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type {
  MaintenanceSchedule,
  MaintenanceCalendarEvent,
  MaintenanceSummary,
  MaintenancePriority,
  MaintenanceStatus,
} from "@/lib/types/maintenance";

// ── Demo Mode Mock Data ──────────────────────────────────────────────────────

const DEMO_MAINTENANCE_SCHEDULES: MaintenanceSchedule[] = [
  {
    id: "ms-demo-1",
    userId: "demo-user-001",
    deviceId: "demo-device-001",
    deviceName: "Apple iPhone 14 Pro",
    deviceCategory: "Smartphone",
    title: "Battery Health Restoration & Calibration",
    description: "Battery capacity at 78%. Full charge-cycle calibration and cell health assessment recommended to prevent sudden power collapse.",
    maintenanceType: "BATTERY_CHECK",
    priority: "HIGH",
    scheduledDate: new Date().toISOString().split("T")[0],
    dueDate: new Date(Date.now() + 6 * 86400000).toISOString().split("T")[0],
    status: "DUE",
    estimatedCost: 29.0,
    estimatedDurationMinutes: 30,
    estimatedCarbonSavings: 1.8,
    createdAt: new Date(Date.now() - 5 * 86400000).toISOString(),
    isDemo: true,
  },
  {
    id: "ms-demo-2",
    userId: "demo-user-001",
    deviceId: "demo-device-001",
    deviceName: "Apple iPhone 14 Pro",
    deviceCategory: "Smartphone",
    title: "Deep Acoustic Port & Connector Cleaning",
    description: "Remove particulate buildup from speaker grilles and Lightning/USB-C connection pins to prevent charging degradation.",
    maintenanceType: "CLEANING",
    priority: "MEDIUM",
    scheduledDate: new Date().toISOString().split("T")[0],
    dueDate: new Date(Date.now() + 18 * 86400000).toISOString().split("T")[0],
    status: "UPCOMING",
    estimatedCost: 15.0,
    estimatedDurationMinutes: 20,
    estimatedCarbonSavings: 0.5,
    createdAt: new Date(Date.now() - 5 * 86400000).toISOString(),
    isDemo: true,
  },
  {
    id: "ms-demo-3",
    userId: "demo-user-001",
    deviceId: "demo-device-002",
    deviceName: "MacBook Pro 16\" M1",
    deviceCategory: "Laptop",
    title: "Thermal Compound & Heatsink De-dusting",
    description: "High thermal delta under load indicates airflow restriction. Disassemble bottom plate and clean twin blower fans.",
    maintenanceType: "PREVENTIVE_REPAIR",
    priority: "HIGH",
    scheduledDate: new Date().toISOString().split("T")[0],
    dueDate: new Date(Date.now() - 2 * 86400000).toISOString().split("T")[0],
    status: "OVERDUE",
    estimatedCost: 45.0,
    estimatedDurationMinutes: 60,
    estimatedCarbonSavings: 3.2,
    createdAt: new Date(Date.now() - 20 * 86400000).toISOString(),
    isDemo: true,
  },
  {
    id: "ms-demo-4",
    userId: "demo-user-001",
    deviceId: "demo-device-002",
    deviceName: "MacBook Pro 16\" M1",
    deviceCategory: "Laptop",
    title: "Quarterly Hardware Integrity Diagnostics",
    description: "Routine scheduled hardware self-test verifying battery cycle health, SSD wear leveling, and display panel uniformity.",
    maintenanceType: "INSPECTION",
    priority: "LOW",
    scheduledDate: new Date().toISOString().split("T")[0],
    dueDate: new Date(Date.now() + 60 * 86400000).toISOString().split("T")[0],
    status: "UPCOMING",
    estimatedCost: 0.0,
    estimatedDurationMinutes: 15,
    estimatedCarbonSavings: 0.8,
    createdAt: new Date(Date.now() - 2 * 86400000).toISOString(),
    isDemo: true,
  },
];

const DEMO_CALENDAR_EVENTS: MaintenanceCalendarEvent[] = [
  {
    eventId: "evt-demo-1",
    eventType: "MAINTENANCE",
    title: "MacBook Pro Thermal De-dusting",
    description: "Clean blower fans to avoid thermal throttling.",
    eventDate: new Date(Date.now() - 2 * 86400000).toISOString().split("T")[0],
    priority: "HIGH",
    deviceId: "demo-device-002",
    deviceName: "MacBook Pro 16\" M1",
    actionUrl: "/maintenance",
    colorTag: "amber",
  },
  {
    eventId: "evt-demo-2",
    eventType: "BOOKING",
    title: "iFix QuickCare Appointment",
    description: "Certified technician inspection at 14:00.",
    eventDate: new Date(Date.now() + 3 * 86400000).toISOString().split("T")[0],
    priority: "MEDIUM",
    deviceId: "demo-device-001",
    deviceName: "Apple iPhone 14 Pro",
    actionUrl: "/repair-shops",
    colorTag: "cyan",
  },
  {
    eventId: "evt-demo-3",
    eventType: "MAINTENANCE",
    title: "iPhone Battery Calibration",
    description: "Run diagnostic cycle and capacity baseline.",
    eventDate: new Date(Date.now() + 6 * 86400000).toISOString().split("T")[0],
    priority: "HIGH",
    deviceId: "demo-device-001",
    deviceName: "Apple iPhone 14 Pro",
    actionUrl: "/maintenance",
    colorTag: "amber",
  },
  {
    eventId: "evt-demo-4",
    eventType: "REPAIR_ACTION",
    title: "Repair Action Plan: Display Protection",
    description: "Install oleophobic tempered shield before deadline.",
    eventDate: new Date(Date.now() + 14 * 86400000).toISOString().split("T")[0],
    priority: "MEDIUM",
    deviceId: "demo-device-001",
    deviceName: "Apple iPhone 14 Pro",
    actionUrl: "/devices/demo-device-001",
    colorTag: "emerald",
  },
];

const DEMO_SUMMARY: MaintenanceSummary = {
  totalUpcoming: 2,
  totalDue: 1,
  totalOverdue: 1,
  totalCritical: 0,
  completedThisMonth: 3,
  totalEstimatedSavingsIfCompleted: 145.0,
  totalCarbonSavingsIfCompleted: 6.3,
  isDemo: true,
};

const DEMO_PRIORITY: MaintenancePriority = {
  deviceId: "demo-device-001",
  deviceName: "Apple iPhone 14 Pro",
  priorityScore: 68,
  priorityLevel: "HIGH",
  reason: "High failure probability identified. 1 maintenance task is due in 6 days and battery health is below 80%.",
  recommendedAction: "Complete scheduled battery restoration task within 7 days to preserve battery cycle longevity.",
  riskContributor: "Predictive risk model (HIGH risk level)",
  evaluatedAt: new Date().toISOString(),
  isDemo: true,
};

// ── API Functions ────────────────────────────────────────────────────────────

/**
 * Fetch all maintenance schedules for authenticated user.
 */
export async function fetchMaintenanceSchedules(
  deviceId?: string,
  status?: string,
  token?: string | null
): Promise<ApiResponse<MaintenanceSchedule[]>> {
  if (isDemoSession(token)) {
    let filtered = [...DEMO_MAINTENANCE_SCHEDULES];
    if (deviceId) filtered = filtered.filter((s) => s.deviceId === deviceId);
    if (status) filtered = filtered.filter((s) => s.status === status);
    return createDemoResponse(filtered);
  }

  const queryParams: Record<string, string> = {};
  if (deviceId) queryParams.deviceId = deviceId;
  if (status) queryParams.status = status;

  const res = await http.get<MaintenanceSchedule[]>("/maintenance", {
    token: token || undefined,
    params: queryParams,
  });

  if (!res.success || !res.data) {
    let filtered = [...DEMO_MAINTENANCE_SCHEDULES];
    if (deviceId) filtered = filtered.filter((s) => s.deviceId === deviceId);
    return createDemoResponse(filtered, res.message);
  }

  return res;
}

/**
 * Fetch maintenance schedules for a single device.
 */
export async function fetchDeviceMaintenance(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<MaintenanceSchedule[]>> {
  if (isDemoSession(token)) {
    const list = DEMO_MAINTENANCE_SCHEDULES.filter((s) => s.deviceId === deviceId);
    return createDemoResponse(list.length > 0 ? list : [DEMO_MAINTENANCE_SCHEDULES[0]]);
  }

  const res = await http.get<MaintenanceSchedule[]>(
    `/maintenance/device/${deviceId}`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse([DEMO_MAINTENANCE_SCHEDULES[0]], res.message);
  }

  return res;
}

/**
 * Generate or refresh deterministic maintenance schedules for a device.
 */
export async function generateMaintenance(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<MaintenanceSchedule[]>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_MAINTENANCE_SCHEDULES);
  }

  const res = await http.post<MaintenanceSchedule[]>(
    `/maintenance/device/${deviceId}/generate`,
    {},
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(DEMO_MAINTENANCE_SCHEDULES, res.message);
  }

  return res;
}

/**
 * Update maintenance status (COMPLETED, SKIPPED, CANCELLED).
 */
export async function updateMaintenanceStatus(
  id: string,
  status: MaintenanceStatus,
  token?: string | null
): Promise<ApiResponse<MaintenanceSchedule>> {
  if (isDemoSession(token)) {
    const found = DEMO_MAINTENANCE_SCHEDULES.find((s) => s.id === id) || DEMO_MAINTENANCE_SCHEDULES[0];
    return createDemoResponse({ ...found, status, completedAt: status === "COMPLETED" ? new Date().toISOString() : undefined });
  }

  const res = await http.put<MaintenanceSchedule>(
    `/maintenance/${id}/status`,
    { status },
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    const found = DEMO_MAINTENANCE_SCHEDULES[0];
    return createDemoResponse({ ...found, status }, res.message);
  }

  return res;
}

/**
 * Fetch unified maintenance calendar events.
 */
export async function fetchMaintenanceCalendar(
  token?: string | null
): Promise<ApiResponse<MaintenanceCalendarEvent[]>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_CALENDAR_EVENTS);
  }

  const res = await http.get<MaintenanceCalendarEvent[]>(
    "/maintenance/calendar",
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(DEMO_CALENDAR_EVENTS, res.message);
  }

  return res;
}

/**
 * Fetch maintenance summary statistics.
 */
export async function fetchMaintenanceSummary(
  token?: string | null
): Promise<ApiResponse<MaintenanceSummary>> {
  if (isDemoSession(token)) {
    return createDemoResponse(DEMO_SUMMARY);
  }

  const res = await http.get<MaintenanceSummary>(
    "/maintenance/summary",
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(DEMO_SUMMARY, res.message);
  }

  return res;
}

/**
 * Fetch deterministic priority assessment for a device.
 */
export async function fetchDevicePriority(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<MaintenancePriority>> {
  if (isDemoSession(token)) {
    return createDemoResponse({ ...DEMO_PRIORITY, deviceId });
  }

  const res = await http.get<MaintenancePriority>(
    `/maintenance/device/${deviceId}/priority`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse({ ...DEMO_PRIORITY, deviceId }, res.message);
  }

  return res;
}
