import { ApiResponse } from "@/lib/types/auth";
import { DiagnosisReport } from "@/lib/types/diagnosis";

export type RecommendedAction =
  | "REPAIR"
  | "MONITOR"
  | "REPLACE"
  | "PROFESSIONAL_SERVICE";

export type RepairStep = {
  stepNumber: number;
  title: string;
  description: string;
  safetyNote?: string;
  estimatedMinutes?: number;
};

export type RequiredPart = {
  name: string;
  quantity: number;
  estimatedCost: number;
  partNumber?: string;
};

export type RequiredTool = {
  name: string;
  category?: string;
  essential: boolean;
};

export type RepairPlan = {
  summary: string;
  steps: RepairStep[];
  parts: RequiredPart[];
  tools: RequiredTool[];
};

export type RepairVsReplaceDecision = {
  repairScore: number; // 0 - 100
  replaceScore: number; // 0 - 100
  recommendation: RecommendedAction;
  moneySaved: number;
  carbonSaved: number; // in kg CO2
  rationale: string;
};

/**
 * Matches AIRecommendations and RepairGuides tables in DATABASE_SCHEMA.md
 */
export type RepairRecommendation = {
  id: string;
  diagnosisId: string;
  diagnosisReport?: DiagnosisReport;
  action: RecommendedAction;
  repairScore: number;
  replaceScore: number;
  plan: RepairPlan;
  decision: RepairVsReplaceDecision;
  createdAt?: string;
};

export type RecommendationResponse = ApiResponse<RepairRecommendation>;
