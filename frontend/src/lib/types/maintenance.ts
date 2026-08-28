export type MaintenanceType =
  | "INSPECTION"
  | "CLEANING"
  | "BATTERY_CHECK"
  | "SOFTWARE_MAINTENANCE"
  | "PREVENTIVE_REPAIR"
  | "COMPONENT_REPLACEMENT"
  | "PROFESSIONAL_SERVICE";

export type MaintenancePriorityLevel = "CRITICAL" | "HIGH" | "MEDIUM" | "LOW";

export type MaintenanceStatus =
  | "UPCOMING"
  | "DUE"
  | "OVERDUE"
  | "COMPLETED"
  | "SKIPPED"
  | "CANCELLED";

export interface MaintenanceSchedule {
  id: string;
  userId: string;
  deviceId: string;
  deviceName: string;
  deviceCategory?: string;
  title: string;
  description: string;
  maintenanceType: MaintenanceType;
  priority: MaintenancePriorityLevel;
  scheduledDate?: string;
  dueDate: string;
  status: MaintenanceStatus;
  estimatedCost: number;
  estimatedDurationMinutes: number;
  estimatedCarbonSavings: number;
  createdAt: string;
  updatedAt?: string;
  completedAt?: string;
  isDemo?: boolean;
}

export interface MaintenanceTask {
  id: string;
  deviceId: string;
  deviceName: string;
  title: string;
  maintenanceType: MaintenanceType;
  priority: MaintenancePriorityLevel;
  dueDate: string;
  status: MaintenanceStatus;
  estimatedCost: number;
  isOverdue: boolean;
  daysUntilDue: number;
}

export type CalendarEventType =
  | "MAINTENANCE"
  | "BOOKING"
  | "REPAIR_ACTION"
  | "LIFECYCLE_ALERT";

export interface MaintenanceCalendarEvent {
  eventId: string;
  eventType: CalendarEventType;
  title: string;
  description: string;
  eventDate: string;
  priority: MaintenancePriorityLevel;
  deviceId?: string;
  deviceName?: string;
  actionUrl: string;
  colorTag: "amber" | "cyan" | "emerald" | "red" | string;
}

export interface MaintenanceSummary {
  totalUpcoming: number;
  totalDue: number;
  totalOverdue: number;
  totalCritical: number;
  completedThisMonth: number;
  totalEstimatedSavingsIfCompleted: number;
  totalCarbonSavingsIfCompleted: number;
  isDemo?: boolean;
}

export interface MaintenancePriority {
  deviceId: string;
  deviceName: string;
  priorityScore: number;
  priorityLevel: MaintenancePriorityLevel;
  reason: string;
  recommendedAction: string;
  riskContributor: string;
  evaluatedAt: string;
  isDemo?: boolean;
}
