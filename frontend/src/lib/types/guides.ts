import type { ApiResponse } from "@/lib/types/auth";

export type RepairTool = {
  name: string;
  description: string;
  isRequired: boolean;
};

export type RepairStep = {
  stepNumber: number;
  title: string;
  instructions: string;
  safetyWarning?: string;
  imageUrl?: string;
};

export type RepairGuideSummary = {
  id: string;
  title: string;
  category: string;
  difficulty: "Beginner" | "Intermediate" | "Advanced" | string;
  estimatedTime: string;
  authorName: string;
  viewsCount: number;
  likesCount: number;
  isVerified: boolean;
  createdAt: string;
};

export type RepairGuideDetail = RepairGuideSummary & {
  guideContent: string;
  authorId?: string;
  tools: RepairTool[];
  steps: RepairStep[];
};

export type CostOption = {
  channel: string;
  channelDescription: string;
  partsCost: number;
  laborCost: number;
  totalCost: number;
  estimatedDuration: string;
  warrantyPeriod: string;
  recommendedTier: string;
};

export type CostEstimateResponse = {
  category: string;
  deviceModel: string;
  issueType: string;
  marketReplacementValue: number;
  diyOption: CostOption;
  localTechOption: CostOption;
  authorizedServiceOption: CostOption;
  maxSavingsDollars: number;
  maxSavingsPercent: number;
  recommendation: string;
  suggestedParts: string[];
};

export type CategoryIssueBaseline = {
  category: string;
  issues: string[];
};
