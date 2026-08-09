import { ApiResponse } from "@/lib/types/auth";

export type RepairDifficulty = "Easy" | "Moderate" | "Hard" | "Complex";

export type DiagnosisRequest = {
  deviceId?: string;
  deviceCategory?: string;
  brand?: string;
  model?: string;
  symptoms: string;
  image?: File | string;
};

/**
 * Matches DiagnosisReports table in DATABASE_SCHEMA.md Module 3
 */
export type DiagnosisReport = {
  id: string;
  deviceId?: string;
  imageUrl?: string;
  symptoms: string;
  probableIssue: string;
  confidenceScore: number; // Percentage e.g. 92
  repairDifficulty: RepairDifficulty;
  repairTime: string; // e.g. "1-2 hours"
  repairCost: number; // e.g. 85 (USD)
  safetyWarning?: string;
  observations?: string[];
  createdAt?: string;
};

export type DiagnosisResponse = ApiResponse<DiagnosisReport>;
