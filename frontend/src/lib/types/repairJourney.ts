export interface RepairJourneyStageData {
  stageKey: string;
  title: string;
  description: string;
  isCompleted: boolean;
  isCurrent: boolean;
  completedAt?: string | null;
  actionUrl: string;
}

export interface RepairJourneyData {
  deviceId: string;
  deviceName: string;
  currentStage: string;
  currentStageIndex: number;
  totalStages: number;
  progressPercentage: number;
  stages: RepairJourneyStageData[];
  nextRecommendedAction: string;
  lastUpdated: string;
  isDemo?: boolean;
}
