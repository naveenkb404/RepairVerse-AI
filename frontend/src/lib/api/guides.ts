import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import type {
  RepairGuideSummary,
  RepairGuideDetail,
  CostEstimateResponse,
  CategoryIssueBaseline,
} from "@/lib/types/guides";

export const SAMPLE_GUIDES: RepairGuideSummary[] = [
  {
    id: "guide-001",
    title: "iPhone 13 / 14 Pro OLED Display & Digitizer Replacement",
    category: "Smartphone",
    difficulty: "Intermediate",
    estimatedTime: "45 mins",
    authorName: "Alex Vance, Master Tech",
    viewsCount: 1420,
    likesCount: 320,
    isVerified: true,
    createdAt: "2024-02-01",
  },
  {
    id: "guide-002",
    title: "MacBook Pro 14 / 16 Battery Pack Replacement & Thermal Clean",
    category: "Laptop",
    difficulty: "Advanced",
    estimatedTime: "1 hour 30 mins",
    authorName: "Elena Rostova, Mac Specialist",
    viewsCount: 980,
    likesCount: 210,
    isVerified: true,
    createdAt: "2024-03-15",
  },
  {
    id: "guide-003",
    title: "PlayStation 5 Liquid Metal Thermal Paste & Fan Replacement",
    category: "Console",
    difficulty: "Advanced",
    estimatedTime: "1 hour 15 mins",
    authorName: "Marcus Cole, Hardware Engineer",
    viewsCount: 830,
    likesCount: 195,
    isVerified: true,
    createdAt: "2024-04-10",
  },
];

/** Fetch all repair guides */
export async function fetchRepairGuides(
  category?: string,
  difficulty?: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: RepairGuideSummary[]; isDemo?: boolean }> {
  let url = "/repair-guide";
  const params = new URLSearchParams();
  if (category) params.append("category", category);
  if (difficulty) params.append("difficulty", difficulty);
  if (params.toString()) url += `?${params.toString()}`;

  const result = await apiClient<RepairGuideSummary[]>(url, {
    method: "GET",
    signal,
  });

  if (result.success && result.data && Array.isArray(result.data)) {
    return { success: true, data: result.data, isDemo: false };
  }

  return {
    success: true,
    data: SAMPLE_GUIDES,
    isDemo: true,
  };
}

/** Fetch repair guide by ID */
export async function fetchRepairGuideById(
  id: string,
  signal?: AbortSignal
): Promise<{ success: boolean; data?: RepairGuideDetail; isDemo?: boolean }> {
  const result = await apiClient<RepairGuideDetail>(`/repair-guide/${id}`, {
    method: "GET",
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return {
    success: true,
    data: {
      id,
      title: "iPhone 13 / 14 Pro OLED Display & Digitizer Replacement",
      category: "Smartphone",
      difficulty: "Intermediate",
      estimatedTime: "45 mins",
      guideContent: "Procedure for disassembling and replacing front display assembly with True Tone transfer.",
      authorName: "Alex Vance, Master Tech",
      viewsCount: 1420,
      likesCount: 320,
      isVerified: true,
      createdAt: "2024-02-01",
      tools: [
        { name: "P2 Pentalobe Screwdriver", description: "Bottom edge screw removal", isRequired: true },
        { name: "Plastic Spudger", description: "Pry tools for ribbon cables", isRequired: true },
      ],
      steps: [
        { stepNumber: 1, title: "Power Down Device", instructions: "Power down device and remove bottom screws.", safetyWarning: "Keep screws organized." },
        { stepNumber: 2, title: "Disconnect Battery", instructions: "Use plastic pick to lift battery flex cable.", safetyWarning: "Never use metal tweezers on battery." },
      ],
    },
    isDemo: true,
  };
}

/** Calculate multi-channel repair cost estimate */
export async function calculateRepairCost(
  data: { category: string; deviceModel: string; issueType: string; deviceAgeYears?: string },
  signal?: AbortSignal
): Promise<{ success: boolean; data?: CostEstimateResponse; isDemo?: boolean }> {
  const result = await apiClient<CostEstimateResponse>("/repair-cost-estimate", {
    method: "POST",
    body: data,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data, isDemo: false };
  }

  return {
    success: true,
    data: {
      category: data.category || "Smartphone",
      deviceModel: data.deviceModel || "iPhone 13 Pro",
      issueType: data.issueType || "OLED Screen Crack",
      marketReplacementValue: 900,
      diyOption: {
        channel: "DIY Repair",
        channelDescription: "Self-repair using precision tool kit and step-by-step guide",
        partsCost: 150,
        laborCost: 0,
        totalCost: 150,
        estimatedDuration: "1 - 2 hours",
        warrantyPeriod: "90 Days Warranty",
        recommendedTier: "Best Value",
      },
      localTechOption: {
        channel: "Local Certified Shop",
        channelDescription: "Professional repair by vetted local repair shops",
        partsCost: 135,
        laborCost: 45,
        totalCost: 180,
        estimatedDuration: "2 - 4 hours (Same Day)",
        warrantyPeriod: "6 Months Warranty",
        recommendedTier: "Most Popular",
      },
      authorizedServiceOption: {
        channel: "Authorized Service Center",
        channelDescription: "Official manufacturer service with OEM parts guarantee",
        partsCost: 202,
        laborCost: 95,
        totalCost: 297,
        estimatedDuration: "3 - 7 Business Days",
        warrantyPeriod: "1 Year Manufacturer Warranty",
        recommendedTier: "Official Guarantee",
      },
      maxSavingsDollars: 750,
      maxSavingsPercent: 83,
      recommendation: "Repairing is highly economical. You save up to $750 (83%) compared to purchasing a new device.",
      suggestedParts: ["Super Retina XDR OLED Assembly", "Pre-cut Waterproof Adhesive Seal"],
    },
    isDemo: true,
  };
}
