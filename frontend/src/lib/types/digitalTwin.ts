// Phase 33: AI Repair Ecosystem Digital Twin & Predictive Optimization Engine Types

export type ScenarioType =
  | 'CONTINUE_CURRENT_USAGE'
  | 'PREVENTIVE_MAINTENANCE'
  | 'REPAIR_NOW'
  | 'DELAY_REPAIR'
  | 'PROFESSIONAL_SERVICE'
  | 'REFURBISH_DEVICE'
  | 'REPLACE_DEVICE'
  | 'RECYCLE_DEVICE';

export type SimulationEventType =
  | 'FAILURE_RISK_INCREASE'
  | 'MAINTENANCE_DUE'
  | 'REPAIR_RECOMMENDED'
  | 'COST_ESCALATION'
  | 'VALUE_DEPRECIATION'
  | 'END_OF_LIFE'
  | 'SUSTAINABILITY_OPPORTUNITY'
  | 'OPTIMAL_INTERVENTION';

export interface DigitalTwinSnapshotResponse {
  id: string;
  deviceId: string;
  deviceName: string;
  deviceCategory: string;
  healthScore: number;
  failureRiskScore: number;
  maintenanceScore: number;
  repairEconomicsScore: number;
  longevityScore: number;
  sustainabilityScore: number;
  predictedValue: number;
  predictedRepairCost: number;
  predictedFailureProbability: number;
  simulationConfidence: number;
  overallEcosystemScore: number;
  snapshotTime: string;
}

export interface ForecastResponse {
  id: string;
  snapshotId: string;
  deviceId: string;
  forecastHorizonMonths: number;
  predictedHealthScore: number;
  predictedFailureRisk: number;
  predictedRepairCost: number;
  predictedDeviceValue: number;
  predictedRemainingLifespanMonths: number;
  predictedCo2Impact: number;
  predictedEWasteImpact: number;
  forecastConfidence: number;
}

export interface ScenarioResponse {
  id: string;
  deviceId: string;
  scenarioType: ScenarioType | string;
  scenarioName: string;
  projectedHealthScore: number;
  projectedFailureRisk: number;
  projectedCost: number;
  projectedSavings: number;
  projectedLifespanMonths: number;
  projectedCo2Impact: number;
  projectedEWasteImpact: number;
  downtimeDays: number;
  overallOutcomeScore: number;
  simulationConfidence: number;
}

export interface OptimizationResponse {
  id: string;
  deviceId: string;
  recommendedStrategy: ScenarioType | string;
  costScore: number;
  reliabilityScore: number;
  longevityScore: number;
  sustainabilityScore: number;
  optimizationScore: number;
  estimatedSavings: number;
  estimatedLifespanGain: number;
  estimatedCo2Savings: number;
  decisionReason: string;
  generatedAt: string;
}

export interface SimulationEventResponse {
  id: string;
  deviceId: string;
  eventType: SimulationEventType | string;
  severity: 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | string;
  title: string;
  description: string;
  projectedMonthOffset: number;
  estimatedFinancialImpact: number;
  mitigationStrategy?: string;
  createdAt: string;
}

export interface DeviceTrajectoryPoint {
  monthOffset: number;
  healthScore: number;
  failureRisk: number;
  repairCost: number;
  deviceValue: number;
}

export interface DeviceTrajectoryResponse {
  deviceId: string;
  deviceName: string;
  trajectoryPoints: DeviceTrajectoryPoint[];
}

export interface SimulationInsight {
  type: string;
  title: string;
  message: string;
  category: string;
  impactLevel: 'LOW' | 'MEDIUM' | 'HIGH' | string;
}

export interface DigitalTwinDashboardResponse {
  deviceId: string;
  deviceName: string;
  deviceCategory: string;
  snapshot: DigitalTwinSnapshotResponse;
  forecasts: ForecastResponse[];
  scenarios: ScenarioResponse[];
  optimalStrategy: OptimizationResponse;
  events: SimulationEventResponse[];
  insights: SimulationInsight[];
  isSimulated?: boolean;
}

export interface EcosystemMetricsResponse {
  totalMonitoredDevices: number;
  totalProjectedSavings: number;
  totalFailuresPrevented: number;
  totalCo2AvoidedKg: number;
  averageEcosystemHealth: number;
  activeSimulationsCount: number;
}

export interface RunSimulationRequest {
  budget?: number;
  targetLifespanMonths?: number;
  prioritizeSustainability?: boolean;
  prioritizeReliability?: boolean;
  maxDowntimeDays?: number;
  preferredStrategy?: string;
}

export interface OptimizationRequest {
  budget?: number;
  targetLifespanMonths?: number;
  prioritizeSustainability?: boolean;
  prioritizeReliability?: boolean;
  maxDowntimeDays?: number;
}
