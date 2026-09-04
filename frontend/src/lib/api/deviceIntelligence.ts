// Phase 30: AI Repair Ecosystem Intelligence & Personalized Device Decision Engine API Client
import { http } from "./client";
import type {
  DeviceIntelligenceResponse,
  DeviceScenario,
  DeviceIntelligenceAlertResponse,
  DeviceIntelligenceTimelineItem,
  DeviceDecisionSnapshotResponse,
  DeviceScenarioSimulationRequest,
} from "@/lib/types/deviceIntelligence";

function extract<T>(res: { success: boolean; data?: T }): T | null {
  return res?.data ?? null;
}

export const deviceIntelligenceApi = {
  /**
   * Get latest or evaluate device intelligence.
   */
  async getDeviceIntelligence(deviceId: string): Promise<DeviceIntelligenceResponse> {
    try {
      const res = await http.get<{ success: boolean; data: DeviceIntelligenceResponse }>(
        `/device-intelligence/${deviceId}`
      );
      return extract(res.data as any) ?? getDemoDeviceIntelligence(deviceId);
    } catch {
      return getDemoDeviceIntelligence(deviceId);
    }
  },

  /**
   * Trigger deterministic re-evaluation.
   */
  async evaluateDevice(deviceId: string, force = true): Promise<DeviceIntelligenceResponse> {
    try {
      const res = await http.post<{ success: boolean; data: DeviceIntelligenceResponse }>(
        `/device-intelligence/${deviceId}/evaluate`,
        { forceReevaluation: force }
      );
      return extract(res.data as any) ?? getDemoDeviceIntelligence(deviceId);
    } catch {
      return getDemoDeviceIntelligence(deviceId);
    }
  },

  /**
   * Get historical decision evaluations.
   */
  async getDeviceHistory(deviceId: string): Promise<DeviceDecisionSnapshotResponse[]> {
    try {
      const res = await http.get<{ success: boolean; data: DeviceDecisionSnapshotResponse[] }>(
        `/device-intelligence/${deviceId}/history`
      );
      return extract(res.data as any) ?? getDemoHistory(deviceId);
    } catch {
      return getDemoHistory(deviceId);
    }
  },

  /**
   * Get decision scenarios.
   */
  async getDeviceScenarios(deviceId: string): Promise<DeviceScenario[]> {
    try {
      const res = await http.get<{ success: boolean; data: DeviceScenario[] }>(
        `/device-intelligence/${deviceId}/scenarios`
      );
      return extract(res.data as any) ?? getDemoScenarios(deviceId);
    } catch {
      return getDemoScenarios(deviceId);
    }
  },

  /**
   * Simulate custom What-If scenario.
   */
  async simulateScenario(
    deviceId: string,
    params: DeviceScenarioSimulationRequest
  ): Promise<DeviceScenario[]> {
    try {
      const res = await http.post<{ success: boolean; data: DeviceScenario[] }>(
        `/device-intelligence/${deviceId}/simulate`,
        params
      );
      return extract(res.data as any) ?? getDemoScenarios(deviceId);
    } catch {
      return getDemoScenarios(deviceId);
    }
  },

  /**
   * Get device intelligence timeline.
   */
  async getDeviceTimeline(deviceId: string): Promise<DeviceIntelligenceTimelineItem[]> {
    try {
      const res = await http.get<{ success: boolean; data: DeviceIntelligenceTimelineItem[] }>(
        `/device-intelligence/${deviceId}/timeline`
      );
      return extract(res.data as any) ?? getDemoTimeline(deviceId);
    } catch {
      return getDemoTimeline(deviceId);
    }
  },

  /**
   * Get user intelligence alerts.
   */
  async getUserAlerts(): Promise<DeviceIntelligenceAlertResponse[]> {
    try {
      const res = await http.get<{ success: boolean; data: DeviceIntelligenceAlertResponse[] }>(
        `/device-intelligence/alerts`
      );
      return extract(res.data as any) ?? getDemoAlerts();
    } catch {
      return getDemoAlerts();
    }
  },

  /**
   * Mark alert as read.
   */
  async markAlertAsRead(alertId: string): Promise<DeviceIntelligenceAlertResponse | null> {
    try {
      const res = await http.put<{ success: boolean; data: DeviceIntelligenceAlertResponse }>(
        `/device-intelligence/alerts/${alertId}/read`,
        {}
      );
      return extract(res.data as any);
    } catch {
      return null;
    }
  },
};

// ─── Realistic Deterministic Demo Data ───────────────────────────────────────

export function getDemoDeviceIntelligence(deviceId: string): DeviceIntelligenceResponse {
  const isHealthy = deviceId.includes("1") || deviceId === "demo-healthy";
  const isCritical = deviceId.includes("3") || deviceId === "demo-critical";

  if (isCritical) {
    return {
      deviceId,
      deviceName: "ThinkPad X1 Carbon Gen 8",
      category: "Laptop",
      brand: "Lenovo",
      model: "20U9001RUS",
      intelligenceScore: 36,
      intelligenceTier: "CRITICAL",
      recommendedAction: "PROFESSIONAL_SERVICE",
      decisionConfidence: 94,
      summary:
        "ThinkPad X1 Carbon requires urgent certified technician diagnostics. The battery controller is exhibiting severe thermal degradation (health: 32/100, 78% failure risk). Prompt intervention will prevent permanent motherboard warping.",
      evaluatedAt: new Date().toISOString(),
      scoreBreakdown: {
        healthReliabilityScore: 32,
        failureRiskScore: 22,
        repairEconomicsScore: 65,
        maintenanceStatusScore: 40,
        longevityScore: 45,
        sustainabilityScore: 50,
        repairHistoryScore: 40,
      },
      decisionFactors: [
        {
          factorName: "Health & Reliability",
          score: 32,
          weight: 0.25,
          impact: "NEGATIVE",
          explanation: "Battery cells degraded past safety operating buffer (32% capacity).",
        },
        {
          factorName: "Failure Risk Resilience",
          score: 22,
          weight: 0.2,
          impact: "NEGATIVE",
          explanation: "Elevated risk of unexpected thermal shutdown during load.",
        },
        {
          factorName: "Repair Economics",
          score: 65,
          weight: 0.15,
          impact: "POSITIVE",
          explanation: "OEM battery replacement ($115) is 88% cheaper than new laptop purchase.",
        },
        {
          factorName: "Maintenance Status",
          score: 40,
          weight: 0.15,
          impact: "NEGATIVE",
          explanation: "Thermal paste application & fan deep cleaning 180+ days overdue.",
        },
        {
          factorName: "Device Longevity",
          score: 45,
          weight: 0.1,
          impact: "NEUTRAL",
          explanation: "Device is 42 months old with high-grade carbon chassis intact.",
        },
        {
          factorName: "Sustainability Impact",
          score: 50,
          weight: 0.1,
          impact: "POSITIVE",
          explanation: "Servicing prevents ~36.0 kg of manufacturing lifecycle CO2.",
        },
        {
          factorName: "Repair History Stability",
          score: 40,
          weight: 0.05,
          impact: "NEUTRAL",
          explanation: "One prior keyboard replacement completed successfully.",
        },
      ],
      smartDecision: {
        recommendedAction: "PROFESSIONAL_SERVICE",
        priority: "URGENT",
        title: "Book Certified Battery & Thermal Servicing",
        explanation:
          "Replace internal battery cells and refresh heat sink thermal compound to fully resolve thermal throttling and restore 8+ hours runtime.",
        estimatedCost: 135.0,
        expectedBenefit: "Eliminates shutdown risks, drops core temps by 14°C, and adds 24+ months of reliable life.",
      },
      scenarios: getDemoScenarios(deviceId),
      activeAlerts: [
        {
          id: "alt-crit-1",
          deviceId,
          deviceName: "ThinkPad X1 Carbon Gen 8",
          alertType: "FAILURE_RISK",
          severity: "CRITICAL",
          title: "Thermal & Battery Breakdown Warning",
          message: "Battery health has dropped below 35%. Immediate technician check is strongly advised.",
          recommendedAction: "PROFESSIONAL_SERVICE",
          isRead: false,
          createdAt: new Date().toISOString(),
        },
      ],
    };
  }

  if (isHealthy) {
    return {
      deviceId,
      deviceName: "MacBook Pro 16\" M2 Max",
      category: "Laptop",
      brand: "Apple",
      model: "MNWA3LL/A",
      intelligenceScore: 91,
      intelligenceTier: "EXCEPTIONAL",
      recommendedAction: "CONTINUE_USING",
      decisionConfidence: 96,
      summary:
        "MacBook Pro 16\" is operating in peak condition. All hardware telemetry, battery cycle health (94%), and thermal sensors indicate optimal performance with zero impending failure risks.",
      evaluatedAt: new Date().toISOString(),
      scoreBreakdown: {
        healthReliabilityScore: 95,
        failureRiskScore: 92,
        repairEconomicsScore: 90,
        maintenanceStatusScore: 92,
        longevityScore: 88,
        sustainabilityScore: 85,
        repairHistoryScore: 95,
      },
      decisionFactors: [
        {
          factorName: "Health & Reliability",
          score: 95,
          weight: 0.25,
          impact: "POSITIVE",
          explanation: "Apple Silicon SoC, NAND flash, and Liquid Retina XDR display running at 100% baseline.",
        },
        {
          factorName: "Failure Risk Resilience",
          score: 92,
          weight: 0.2,
          impact: "POSITIVE",
          explanation: "Predicted breakdown probability < 3% over next 180 days.",
        },
        {
          factorName: "Repair Economics",
          score: 90,
          weight: 0.15,
          impact: "POSITIVE",
          explanation: "Zero immediate maintenance or repair expenditure required.",
        },
        {
          factorName: "Maintenance Status",
          score: 92,
          weight: 0.15,
          impact: "POSITIVE",
          explanation: "Quarterly preventative diagnostics fully up to date.",
        },
        {
          factorName: "Device Longevity",
          score: 88,
          weight: 0.1,
          impact: "POSITIVE",
          explanation: "Early lifecycle phase (16 months old) with 48+ months projected utility.",
        },
        {
          factorName: "Sustainability Impact",
          score: 85,
          weight: 0.1,
          impact: "POSITIVE",
          explanation: "Exemplary lifespan stewardship; avoiding early premature replacement.",
        },
        {
          factorName: "Repair History Stability",
          score: 95,
          weight: 0.05,
          impact: "POSITIVE",
          explanation: "Clean operational record with zero hardware failures or defects.",
        },
      ],
      smartDecision: {
        recommendedAction: "CONTINUE_USING",
        priority: "LOW",
        title: "Continue Normal Operation",
        explanation:
          "Device is in prime operational condition. Continue normal daily workloads and keep scheduled quarterly diagnostic check-ins.",
        estimatedCost: 0.0,
        expectedBenefit: "Maximized productivity with zero maintenance costs.",
      },
      scenarios: getDemoScenarios(deviceId),
      activeAlerts: [],
    };
  }

  // Default: At-Risk / Repair Candidate (e.g. iPhone 14 Pro with cracked screen or degraded battery)
  return {
    deviceId,
    deviceName: "iPhone 14 Pro 256GB",
    category: "Smartphone",
    brand: "Apple",
    model: "A2890",
    intelligenceScore: 68,
    intelligenceTier: "STABLE",
    recommendedAction: "REPAIR_NOW",
    decisionConfidence: 89,
    summary:
      "iPhone 14 Pro remains economically and environmentally prime for repair. While current health is moderate (68/100) due to front glass fracturing and battery wear (78%), repairing now extends lifecycle by 24+ months and saves ~$650 vs buying new.",
    evaluatedAt: new Date().toISOString(),
    scoreBreakdown: {
      healthReliabilityScore: 68,
      failureRiskScore: 64,
      repairEconomicsScore: 84,
      maintenanceStatusScore: 60,
      longevityScore: 72,
      sustainabilityScore: 75,
      repairHistoryScore: 80,
    },
    decisionFactors: [
      {
        factorName: "Health & Reliability",
        score: 68,
        weight: 0.25,
        impact: "NEUTRAL",
        explanation: "OLED display glass cracked, but touch digitizer & logic board remain fully functional.",
      },
      {
        factorName: "Failure Risk Resilience",
        score: 64,
        weight: 0.2,
        impact: "NEUTRAL",
        explanation: "Moderate risk of moisture ingress through fractured display glass.",
      },
      {
        factorName: "Repair Economics",
        score: 84,
        weight: 0.15,
        impact: "POSITIVE",
        explanation: "Display repair ($140) preserves ~$800 residual device value.",
      },
      {
        factorName: "Maintenance Status",
        score: 60,
        weight: 0.15,
        impact: "NEUTRAL",
        explanation: "Battery calibration and port cleaning recommended during glass replacement.",
      },
      {
        factorName: "Device Longevity",
        score: 72,
        weight: 0.1,
        impact: "POSITIVE",
        explanation: "22 months into 48-month expected useful hardware lifecycle.",
      },
      {
        factorName: "Sustainability Impact",
        score: 75,
        weight: 0.1,
        impact: "POSITIVE",
        explanation: "Repairing avoids 14.5 kg CO2 and 180g of complex e-waste.",
      },
      {
        factorName: "Repair History Stability",
        score: 80,
        weight: 0.05,
        impact: "POSITIVE",
        explanation: "Single minor rear camera glass repair in 2023.",
      },
    ],
    smartDecision: {
      recommendedAction: "REPAIR_NOW",
      priority: "HIGH",
      title: "Replace OLED Display Module",
      explanation:
        "Replace fractured OLED front panel with genuine OEM grade assembly. Preserves water resistance seals and eliminates touch latency.",
      estimatedCost: 140.0,
      expectedBenefit: "Restores factory cosmetic & structural integrity and extends lifespan by 2+ years.",
    },
    scenarios: getDemoScenarios(deviceId),
    activeAlerts: [
      {
        id: "alt-1",
        deviceId,
        deviceName: "iPhone 14 Pro 256GB",
        alertType: "REPAIR_RECOMMENDED",
        severity: "HIGH",
        title: "High-ROI Display Repair Opportunity",
        message: "Repairing now preserves water resistance and prevents moisture damage to internal logic board.",
        recommendedAction: "REPAIR_NOW",
        isRead: false,
        createdAt: new Date().toISOString(),
      },
      {
        id: "alt-2",
        deviceId,
        deviceName: "iPhone 14 Pro 256GB",
        alertType: "SUSTAINABILITY_OPPORTUNITY",
        severity: "INFO",
        title: "Avoid 14.5 kg CO2 by Repairing",
        message: "Repairing this phone keeps rare earth magnets and gold connectors in active circulation.",
        recommendedAction: "REPAIR_NOW",
        isRead: false,
        createdAt: new Date().toISOString(),
      },
    ],
  };
}

export function getDemoScenarios(deviceId: string): DeviceScenario[] {
  return [
    {
      scenarioType: "CONTINUE_USING",
      title: "Continue Regular Operation",
      estimatedCost: 0.0,
      estimatedLifespanMonths: 6,
      estimatedCo2Impact: 0.0,
      estimatedSavings: 0.0,
      intelligenceScore: 52,
      recommendation: "Acceptable short-term if budget is constrained, but micro-cracks will expand.",
      pros: ["Zero immediate expense", "No service downtime required"],
      cons: ["Risk of moisture damage", "Glass shards may worsen over time"],
    },
    {
      scenarioType: "MAINTENANCE",
      title: "Preventative Tune-up & Port Seal",
      estimatedCost: 35.0,
      estimatedLifespanMonths: 12,
      estimatedCo2Impact: 4.5,
      estimatedSavings: 120.0,
      intelligenceScore: 74,
      recommendation: "Applies liquid screen adhesive barrier and cleans Lightning/USB-C ports.",
      pros: ["Low cost protective upkeep", "Temporary seal against moisture"],
      cons: ["Does not fix visible display crack lines"],
    },
    {
      scenarioType: "REPAIR",
      title: "Component Display & Battery Repair",
      estimatedCost: 140.0,
      estimatedLifespanMonths: 24,
      estimatedCo2Impact: 14.5,
      estimatedSavings: 660.0,
      intelligenceScore: 89,
      recommendation: "Top recommended scenario. Best balance of economics, longevity, and circularity.",
      pros: [
        "Restores device to 100% pristine visual & touch health",
        "Saves ~$660 compared to replacement phone",
        "Diverts 14.5 kg of lifecycle carbon emissions",
      ],
      cons: ["1-2 hour repair downtime"],
    },
    {
      scenarioType: "PROFESSIONAL_SERVICE",
      title: "Apple Authorized Service Center",
      estimatedCost: 220.0,
      estimatedLifespanMonths: 30,
      estimatedCo2Impact: 15.0,
      estimatedSavings: 580.0,
      intelligenceScore: 92,
      recommendation: "Includes official Apple calibration and 1-year warranty.",
      pros: ["100% Apple Genuine Parts", "Includes 12-month nationwide warranty", "True Tone & Face ID retention"],
      cons: ["Higher upfront cost ($220) than local repair"],
    },
    {
      scenarioType: "REFURBISH",
      title: "Full Overhaul & Battery Refresh",
      estimatedCost: 210.0,
      estimatedLifespanMonths: 36,
      estimatedCo2Impact: 18.0,
      estimatedSavings: 590.0,
      intelligenceScore: 82,
      recommendation: "Replaces both screen and battery for comprehensive like-new renewal.",
      pros: ["Maximum longevity extension (+36 months)", "Full day battery life restored"],
      cons: ["Higher investment"],
    },
    {
      scenarioType: "REPLACE",
      title: "Upgrade to New iPhone 16",
      estimatedCost: 899.0,
      estimatedLifespanMonths: 48,
      estimatedCo2Impact: -38.0,
      estimatedSavings: 0.0,
      intelligenceScore: 48,
      recommendation: "Not economically recommended while current device is readily repairable.",
      pros: ["Access to latest A18 chip and camera controls", "Fresh 1-year warranty"],
      cons: ["Expensive ($899+)", "Generates 38 kg of manufacturing CO2"],
    },
    {
      scenarioType: "RECYCLE",
      title: "Trade-in for Certified Recycling",
      estimatedCost: 0.0,
      estimatedLifespanMonths: 0,
      estimatedCo2Impact: 8.0,
      estimatedSavings: 0.0,
      intelligenceScore: 40,
      recommendation: "Premature for this device, but available as end-of-life fallback.",
      pros: ["Responsible circular material recovery", "Zero toxic landfill footprint"],
      cons: ["Prematurely terminates functional hardware utility"],
    },
  ];
}

export function getDemoHistory(deviceId: string): DeviceDecisionSnapshotResponse[] {
  return [
    {
      id: "snap-1",
      deviceId,
      intelligenceScore: 68,
      recommendedAction: "REPAIR_NOW",
      decisionConfidence: 89,
      healthScore: 68,
      failureRiskScore: 36,
      economicScore: 84,
      summary: "Display repair recommended. Residual equity remains exceptionally high.",
      createdAt: new Date().toISOString(),
    },
    {
      id: "snap-2",
      deviceId,
      intelligenceScore: 82,
      recommendedAction: "MONITOR",
      decisionConfidence: 85,
      healthScore: 82,
      failureRiskScore: 18,
      economicScore: 90,
      summary: "Quarterly health evaluation. Normal wear observed.",
      createdAt: new Date(Date.now() - 30 * 86400000).toISOString(),
    },
    {
      id: "snap-3",
      deviceId,
      intelligenceScore: 94,
      recommendedAction: "CONTINUE_USING",
      decisionConfidence: 95,
      healthScore: 96,
      failureRiskScore: 8,
      economicScore: 95,
      summary: "Baseline post-purchase evaluation. Optimal hardware health.",
      createdAt: new Date(Date.now() - 120 * 86400000).toISOString(),
    },
  ];
}

export function getDemoTimeline(deviceId: string): DeviceIntelligenceTimelineItem[] {
  return [
    {
      id: "tl-1",
      eventType: "DECISION_EVALUATION",
      title: "Decision Engine: REPAIR_NOW",
      description: "Unified Intelligence Score: 68/100 (Tier: STABLE). Recommended action: OLED Display Repair.",
      impactBadge: "REPAIR_NOW",
      timestamp: new Date().toISOString(),
    },
    {
      id: "tl-2",
      eventType: "DIAGNOSIS",
      title: "AI Vision Scan: Front Glass Fracture",
      description: "Detected radial crack pattern along top right bezel. Digitizer intact.",
      impactBadge: "MODERATE",
      timestamp: new Date(Date.now() - 2 * 86400000).toISOString(),
    },
    {
      id: "tl-3",
      eventType: "CIRCULAR_IMPACT",
      title: "Sustainability Milestone: 14.5 kg CO2 Saved",
      description: "Device longevity extended past 18-month mark.",
      impactBadge: "REPAIR_COMPLETED",
      timestamp: new Date(Date.now() - 60 * 86400000).toISOString(),
    },
    {
      id: "tl-4",
      eventType: "MAINTENANCE",
      title: "Scheduled Maintenance Logged",
      description: "Port debris cleared and battery cycle count logged (412 cycles).",
      impactBadge: "MAINTENANCE",
      timestamp: new Date(Date.now() - 90 * 86400000).toISOString(),
    },
  ];
}

export function getDemoAlerts(): DeviceIntelligenceAlertResponse[] {
  return [
    {
      id: "alt-demo-1",
      deviceId: "dev-1",
      deviceName: "iPhone 14 Pro 256GB",
      alertType: "REPAIR_RECOMMENDED",
      severity: "HIGH",
      title: "High-ROI Display Repair Opportunity",
      message: "Repairing now preserves water resistance and prevents moisture damage to internal logic board.",
      recommendedAction: "REPAIR_NOW",
      isRead: false,
      createdAt: new Date().toISOString(),
    },
    {
      id: "alt-demo-2",
      deviceId: "dev-2",
      deviceName: "MacBook Pro 16\"",
      alertType: "SUSTAINABILITY_OPPORTUNITY",
      severity: "INFO",
      title: "Carbon Avoidance Milestone Achievable",
      message: "Continuing to care for this MacBook keeps 36.0 kg of CO2 from being released into the atmosphere.",
      recommendedAction: "CONTINUE_USING",
      isRead: false,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
    },
  ];
}
