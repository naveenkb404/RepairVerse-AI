// Phase 31: Autonomous Repair Agent & Proactive Device Intervention System Types

export type InterventionType =
  | 'MONITOR'
  | 'MAINTENANCE'
  | 'PREVENTIVE_REPAIR'
  | 'URGENT_REPAIR'
  | 'PROFESSIONAL_SERVICE'
  | 'SHOP_RECOMMENDATION'
  | 'QUOTE_REQUEST'
  | 'DEVICE_OPTIMIZATION'
  | 'REFURBISH'
  | 'REPLACE'
  | 'RECYCLE';

export type InterventionPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type InterventionStatus =
  | 'DETECTED'
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'CANCELLED';

export type ActionStepStatus =
  | 'PENDING'
  | 'WAITING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'RUNNING'
  | 'COMPLETED'
  | 'FAILED'
  | 'CANCELLED';

export type ActionType =
  | 'GENERATE_REPORT'
  | 'SCHEDULE_MAINTENANCE'
  | 'FIND_SHOPS'
  | 'REQUEST_QUOTE'
  | 'COMPARE_OPTIONS'
  | 'BOOK_SERVICE'
  | 'DISPOSE_RECYCLE'
  | 'NOTIFY_USER';

export interface ActionStepResponse {
  id: string;
  planId: string;
  interventionId?: string;
  deviceId?: string;
  deviceName?: string;
  stepOrder: number;
  actionType: ActionType | string;
  title: string;
  description: string;
  status: ActionStepStatus;
  requiresApproval: boolean;
  actionMetadata?: string;
  scheduledFor?: string;
  completedAt?: string;
}

export interface ActionPlanResponse {
  id: string;
  interventionId: string;
  planName: string;
  objective: string;
  totalSteps: number;
  completedSteps: number;
  status: string;
  steps: ActionStepResponse[];
  createdAt: string;
}

export interface InterventionResponse {
  id: string;
  userId: string;
  deviceId?: string;
  deviceName?: string;
  deviceCategory?: string;
  interventionType: InterventionType;
  priority: InterventionPriority;
  status: InterventionStatus;
  title: string;
  description: string;
  reason?: string;
  confidenceScore: number;
  priorityScore: number;
  estimatedCost: number;
  estimatedSavings: number;
  estimatedCo2Impact: number;
  recommendedAction?: string;
  requiresUserApproval: boolean;
  actionPlan?: ActionPlanResponse;
  createdAt: string;
  resolvedAt?: string;
}

export interface ExecutionHistoryResponse {
  id: string;
  userId: string;
  deviceId?: string;
  deviceName?: string;
  interventionId?: string;
  actionStepId?: string;
  actionType: string;
  executionStatus: string;
  resultSummary: string;
  executedAt: string;
}

export interface AgentDashboardResponse {
  agentStatus: 'ACTIVE' | 'IDLE' | 'ATTENTION_REQUIRED';
  monitoredDevicesCount: number;
  activeInterventionsCount: number;
  pendingApprovalsCount: number;
  completedExecutionsCount: number;
  totalMoneySaved: number;
  totalCo2AvoidedKg: number;
  activeInterventions: InterventionResponse[];
  pendingApprovals: ActionStepResponse[];
  recentExecutions: ExecutionHistoryResponse[];
  priorityDistribution: Record<string, number>;
}

export interface DeviceEvaluationRequest {
  force?: boolean;
}

export interface ActionApprovalRequest {
  approved?: boolean;
  notes?: string;
}

export interface ActionExecutionRequest {
  parameters?: Record<string, any>;
}

export interface ExecutionResultResponse {
  actionId: string;
  status: string;
  message: string;
  executionId?: string;
  executedAt: string;
}
