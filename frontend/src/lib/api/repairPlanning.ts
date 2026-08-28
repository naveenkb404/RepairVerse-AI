import { http } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type { RepairActionPlanData } from "@/lib/types/repairPlanning";

export const DEMO_ACTION_PLANS: Record<string, RepairActionPlanData> = {
  default: {
    id: "plan-demo-1",
    userId: "usr-demo",
    deviceId: "dev_sample_1",
    deviceName: "Personal iPhone 14 Pro",
    deviceCategory: "Smartphone",
    overallStrategy: "PREVENTIVE_MAINTENANCE",
    priorityLevel: "MEDIUM",
    estimatedTotalCost: 45.0,
    estimatedLifecycleExtensionMonths: 14,
    estimatedCarbonSaved: 5.8,
    estimatedEwastePrevented: 0.15,
    status: "ACTIVE",
    strategyRationale: "Moderate component wear detected. Proactive servicing, thermal cleaning, and battery health management will avert critical failure.",
    steps: [
      {
        id: "step-demo-1",
        actionPlanId: "plan-demo-1",
        stepOrder: 1,
        title: "Critical Cloud & Local Data Backup",
        description: "Secure full user profile, encryption keys, and system images before physical servicing.",
        actionType: "BACKUP_DATA",
        priority: "HIGH",
        estimatedCost: 0.0,
        estimatedDuration: "20-40 mins",
        carbonImpact: 0.0,
        isRequired: true,
        status: "COMPLETED",
      },
      {
        id: "step-demo-2",
        actionPlanId: "plan-demo-1",
        stepOrder: 2,
        title: "Internal Thermal De-dusting & Heat-pipe Repaste",
        description: "Disassemble chassis to purge thermal exhaust channels and replenish degraded thermal compound.",
        actionType: "MAINTAIN",
        priority: "MEDIUM",
        estimatedCost: 25.0,
        estimatedDuration: "45 mins",
        carbonImpact: 1.8,
        isRequired: true,
        status: "PENDING",
      },
      {
        id: "step-demo-3",
        actionPlanId: "plan-demo-1",
        stepOrder: 3,
        title: "Battery Conditioning Calibration Cycle",
        description: "Perform controlled calibration discharge to recalibrate the internal fuel-gauge controller.",
        actionType: "MAINTAIN",
        priority: "MEDIUM",
        estimatedCost: 10.0,
        estimatedDuration: "2 hours",
        carbonImpact: 0.8,
        isRequired: false,
        status: "PENDING",
      },
      {
        id: "step-demo-4",
        actionPlanId: "plan-demo-1",
        stepOrder: 4,
        title: "Firmware & Power Management Optimization",
        description: "Update hardware controller microcode for optimized voltage regulation.",
        actionType: "INSPECT",
        priority: "LOW",
        estimatedCost: 0.0,
        estimatedDuration: "15 mins",
        carbonImpact: 0.2,
        isRequired: true,
        status: "PENDING",
      },
    ],
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    isDemo: true,
  },
};

export async function fetchDeviceActionPlan(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<RepairActionPlanData>> {
  if (isDemoSession(token)) {
    const demo = DEMO_ACTION_PLANS[deviceId] || {
      ...DEMO_ACTION_PLANS.default,
      deviceId,
    };
    return createDemoResponse(demo);
  }

  const res = await http.get<RepairActionPlanData>(
    `/repair-planning/device/${deviceId}`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    const fallback = DEMO_ACTION_PLANS[deviceId] || {
      ...DEMO_ACTION_PLANS.default,
      deviceId,
    };
    return createDemoResponse(fallback, res.message ?? "Backend offline — sample action plan loaded.");
  }

  return res;
}

export async function refreshDeviceActionPlan(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<RepairActionPlanData>> {
  if (isDemoSession(token)) {
    const demo = DEMO_ACTION_PLANS[deviceId] || {
      ...DEMO_ACTION_PLANS.default,
      deviceId,
    };
    return createDemoResponse(demo);
  }

  const res = await http.post<RepairActionPlanData>(
    `/repair-planning/device/${deviceId}/refresh`,
    {},
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    const fallback = DEMO_ACTION_PLANS[deviceId] || {
      ...DEMO_ACTION_PLANS.default,
      deviceId,
    };
    return createDemoResponse(fallback, res.message ?? "Backend offline — sample action plan refreshed.");
  }

  return res;
}

export async function fetchUserActionPlans(
  token?: string | null
): Promise<ApiResponse<RepairActionPlanData[]>> {
  if (isDemoSession(token)) {
    return createDemoResponse([DEMO_ACTION_PLANS.default]);
  }

  const res = await http.get<RepairActionPlanData[]>(
    "/repair-planning",
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse([DEMO_ACTION_PLANS.default], res.message ?? "Backend offline — sample action plans loaded.");
  }

  return res;
}
