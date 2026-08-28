import { http } from "@/lib/api/client";
import { isDemoSession, createDemoResponse } from "@/lib/demo";
import type { ApiResponse } from "@/lib/types/auth";
import type { RepairJourneyData } from "@/lib/types/repairJourney";

export const DEMO_REPAIR_JOURNEY: RepairJourneyData = {
  deviceId: "dev_sample_1",
  deviceName: "Personal iPhone 14 Pro",
  currentStage: "ACTION_PLAN_READY",
  currentStageIndex: 4,
  totalStages: 9,
  progressPercentage: 55,
  stages: [
    {
      stageKey: "DEVICE_REGISTERED",
      title: "Device Registered",
      description: "Hardware enrolled in RepairVerse Digital Health Passport ledger.",
      isCompleted: true,
      isCurrent: false,
      completedAt: new Date(Date.now() - 86400000 * 30).toISOString(),
      actionUrl: "/devices/dev_sample_1",
    },
    {
      stageKey: "DIAGNOSIS_COMPLETE",
      title: "AI Visual Diagnosis",
      description: "Completed: OLED Display Fracture & Battery Degradation",
      isCompleted: true,
      isCurrent: false,
      completedAt: new Date(Date.now() - 86400000 * 7).toISOString(),
      actionUrl: "/diagnosis",
    },
    {
      stageKey: "RISK_ANALYZED",
      title: "Predictive Degradation Analysis",
      description: "Risk evaluated as LOW (Score 84/100)",
      isCompleted: true,
      isCurrent: false,
      completedAt: new Date(Date.now() - 86400000 * 3).toISOString(),
      actionUrl: "/devices/dev_sample_1",
    },
    {
      stageKey: "REPAIR_RECOMMENDED",
      title: "Repair vs Replace Decision",
      description: "Recommended Action: REPAIR (Saved $640 / 6.5 kg CO₂)",
      isCompleted: true,
      isCurrent: false,
      completedAt: new Date(Date.now() - 86400000 * 1).toISOString(),
      actionUrl: "/recommendation",
    },
    {
      stageKey: "ACTION_PLAN_READY",
      title: "Smart Action Plan Generated",
      description: "Strategy: PREVENTIVE_MAINTENANCE (4 ordered execution steps)",
      isCompleted: true,
      isCurrent: true,
      completedAt: new Date().toISOString(),
      actionUrl: "/devices/dev_sample_1",
    },
    {
      stageKey: "SHOP_BOOKED",
      title: "Certified Technician Booking",
      description: "Connect with nearby verified repair shops.",
      isCompleted: false,
      isCurrent: false,
      completedAt: null,
      actionUrl: "/repair-shops",
    },
    {
      stageKey: "REPAIR_IN_PROGRESS",
      title: "Hardware Servicing & Teardown",
      description: "Hardware disassembly and precision replacement in progress.",
      isCompleted: false,
      isCurrent: false,
      completedAt: null,
      actionUrl: "/dashboard",
    },
    {
      stageKey: "REPAIR_COMPLETED",
      title: "Quality Assurance & Verification",
      description: "Post-repair burn-in testing and verification completed.",
      isCompleted: false,
      isCurrent: false,
      completedAt: null,
      actionUrl: "/repair-history",
    },
    {
      stageKey: "DEVICE_MONITORED",
      title: "Extended Lifecycle Monitoring",
      description: "Digital passport updated. Active continuous telemetry and circular warranty active.",
      isCompleted: false,
      isCurrent: false,
      completedAt: null,
      actionUrl: "/devices/dev_sample_1",
    },
  ],
  nextRecommendedAction: "Review your autonomous Smart Action Plan and schedule certified technician servicing.",
  lastUpdated: new Date().toISOString(),
  isDemo: true,
};

export async function fetchRepairJourney(
  deviceId: string,
  token?: string | null
): Promise<ApiResponse<RepairJourneyData>> {
  if (isDemoSession(token)) {
    return createDemoResponse({
      ...DEMO_REPAIR_JOURNEY,
      deviceId,
    });
  }

  const res = await http.get<RepairJourneyData>(
    `/repair-journey/device/${deviceId}`,
    token ? { token } : undefined
  );

  if (!res.success || !res.data) {
    return createDemoResponse(
      { ...DEMO_REPAIR_JOURNEY, deviceId },
      res.message ?? "Backend offline — sample repair journey loaded."
    );
  }

  return res;
}
