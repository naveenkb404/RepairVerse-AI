export type ActionStrategy =
  | "MONITOR"
  | "PREVENTIVE_MAINTENANCE"
  | "REPAIR"
  | "REFURBISH"
  | "REPLACE"
  | "RECYCLE";

export type PriorityLevel = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export type ActionType =
  | "INSPECT"
  | "BACKUP_DATA"
  | "CLEAN"
  | "MAINTAIN"
  | "REPAIR"
  | "REPLACE_COMPONENT"
  | "BOOK_REPAIR"
  | "MONITOR"
  | "RECYCLE";

export interface RepairActionStepData {
  id: string;
  actionPlanId: string;
  stepOrder: number;
  title: string;
  description: string;
  actionType: ActionType;
  priority: PriorityLevel;
  estimatedCost: number;
  estimatedDuration: string;
  carbonImpact: number;
  isRequired: boolean;
  status: "PENDING" | "IN_PROGRESS" | "COMPLETED" | "SKIPPED";
}

export interface RepairActionPlanData {
  id: string;
  userId: string;
  deviceId: string;
  deviceName: string;
  deviceCategory: string;
  overallStrategy: ActionStrategy;
  priorityLevel: PriorityLevel;
  estimatedTotalCost: number;
  estimatedLifecycleExtensionMonths: number;
  estimatedCarbonSaved: number;
  estimatedEwastePrevented: number;
  status: "ACTIVE" | "ARCHIVED" | "COMPLETED";
  strategyRationale: string;
  steps: RepairActionStepData[];
  createdAt: string;
  updatedAt: string;
  isDemo?: boolean;
}
