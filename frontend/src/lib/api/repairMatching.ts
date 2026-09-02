import { http } from "./client";
import {
  RepairShopMatchResponse,
  SmartRecommendationResponse,
  RepairMarketplaceComparison,
  QuoteIntelligenceResponse,
  UserMarketplaceInsights,
  PlatformMarketplaceAnalytics,
  CompareShopsRequest,
  TrackInteractionRequest,
} from "../types/repairMatching";

/**
 * Phase 27 — Smart Repair Matching & Marketplace API Client
 */
export const repairMatchingApi = {
  /**
   * GET /api/v1/marketplace/matches/device/{deviceId}
   */
  async getDeviceMatches(
    deviceId: string,
    params?: {
      lat?: number;
      lng?: number;
      diagnosisId?: string;
      repairType?: string;
    }
  ): Promise<RepairShopMatchResponse[]> {
    try {
      const searchParams = new URLSearchParams();
      if (params?.lat !== undefined) searchParams.append("lat", params.lat.toString());
      if (params?.lng !== undefined) searchParams.append("lng", params.lng.toString());
      if (params?.diagnosisId) searchParams.append("diagnosisId", params.diagnosisId);
      if (params?.repairType) searchParams.append("repairType", params.repairType);

      const qs = searchParams.toString();
      const url = `/marketplace/matches/device/${deviceId}${qs ? `?${qs}` : ""}`;
      const res = await http.get<{ success: boolean; data: RepairShopMatchResponse[] }>(url);
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoMatches(deviceId);
    } catch {
      return getDemoMatches(deviceId);
    }
  },

  /**
   * GET /api/v1/marketplace/recommendations/device/{deviceId}
   */
  async getDeviceRecommendations(
    deviceId: string,
    params?: {
      lat?: number;
      lng?: number;
      diagnosisId?: string;
      repairType?: string;
    }
  ): Promise<SmartRecommendationResponse> {
    try {
      const searchParams = new URLSearchParams();
      if (params?.lat !== undefined) searchParams.append("lat", params.lat.toString());
      if (params?.lng !== undefined) searchParams.append("lng", params.lng.toString());
      if (params?.diagnosisId) searchParams.append("diagnosisId", params.diagnosisId);
      if (params?.repairType) searchParams.append("repairType", params.repairType);

      const qs = searchParams.toString();
      const url = `/marketplace/recommendations/device/${deviceId}${qs ? `?${qs}` : ""}`;
      const res = await http.get<{ success: boolean; data: SmartRecommendationResponse }>(url);
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoRecommendations(deviceId);
    } catch {
      return getDemoRecommendations(deviceId);
    }
  },

  /**
   * POST /api/v1/marketplace/compare
   */
  async compareShops(
    request: CompareShopsRequest,
    coords?: { lat?: number; lng?: number }
  ): Promise<RepairMarketplaceComparison> {
    try {
      const searchParams = new URLSearchParams();
      if (coords?.lat !== undefined) searchParams.append("lat", coords.lat.toString());
      if (coords?.lng !== undefined) searchParams.append("lng", coords.lng.toString());

      const qs = searchParams.toString();
      const url = `/marketplace/compare${qs ? `?${qs}` : ""}`;
      const res = await http.post<{ success: boolean; data: RepairMarketplaceComparison }>(url, request);
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoComparison(request.shopIds);
    } catch {
      return getDemoComparison(request.shopIds);
    }
  },

  /**
   * GET /api/v1/marketplace/quotes/{quoteId}/intelligence
   */
  async getQuoteIntelligence(quoteId: string): Promise<QuoteIntelligenceResponse> {
    try {
      const res = await http.get<{ success: boolean; data: QuoteIntelligenceResponse }>(
        `/marketplace/quotes/${quoteId}/intelligence`
      );
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoQuoteIntelligence(quoteId);
    } catch {
      return getDemoQuoteIntelligence(quoteId);
    }
  },

  /**
   * GET /api/v1/marketplace/analytics
   */
  async getUserMarketplaceAnalytics(): Promise<UserMarketplaceInsights> {
    try {
      const res = await http.get<{ success: boolean; data: UserMarketplaceInsights }>(
        "/marketplace/analytics"
      );
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoUserInsights();
    } catch {
      return getDemoUserInsights();
    }
  },

  /**
   * GET /api/v1/admin/marketplace/analytics
   */
  async getAdminMarketplaceAnalytics(): Promise<PlatformMarketplaceAnalytics> {
    try {
      const res = await http.get<{ success: boolean; data: PlatformMarketplaceAnalytics }>(
        "/admin/marketplace/analytics"
      );
      if (res.data) {
        return (res.data as any).data || res.data;
      }
      return getDemoPlatformAnalytics();
    } catch {
      return getDemoPlatformAnalytics();
    }
  },

  /**
   * POST /api/v1/marketplace/interactions
   */
  async trackInteraction(request: TrackInteractionRequest): Promise<void> {
    try {
      await http.post("/marketplace/interactions", request);
    } catch {
      // Non-blocking telemetry
    }
  },
};

// --- Fallback Demo Generators ---

export function getDemoMatches(deviceId: string): RepairShopMatchResponse[] {
  return [
    {
      shopId: "shop-1",
      shopName: "Apex Micro-Electronics Care",
      address: "452 Tech Plaza, Innovation District",
      latitude: 37.7749,
      longitude: -122.4194,
      phone: "+1 (555) 432-1098",
      email: "contact@apexmicro.io",
      hours: "Mon-Sat 8:30AM - 6:30PM",
      rating: 4.9,
      reviewCount: 142,
      verificationStatus: "TRUSTED",
      verificationLevel: "PREMIUM",
      distanceKm: 1.8,
      overallScore: 96,
      matchLevel: "EXCELLENT_MATCH",
      rank: 1,
      factors: [
        {
          factorName: "Specialization & Hardware Fit",
          score: 25,
          maxScore: 25,
          weightPercent: 25,
          explanation: "Certified specialist in Apple & Samsung hardware architecture.",
          positiveImpact: true,
        },
        {
          factorName: "Trust & Verified Reputation",
          score: 20,
          maxScore: 20,
          weightPercent: 20,
          explanation: "Exceptional track record with 140+ verified 5-star customer reviews.",
          positiveImpact: true,
        },
        {
          factorName: "Quote & Pricing Value",
          score: 14,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Estimated baseline diagnostic and labor rate ~$65.",
          positiveImpact: true,
        },
        {
          factorName: "Proximity & Distance",
          score: 15,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Extremely close (1.8 km) — walk-in eligible.",
          positiveImpact: true,
        },
        {
          factorName: "Speed & Turnaround",
          score: 10,
          maxScore: 10,
          weightPercent: 10,
          explanation: "Same-day express turnaround (2-4 hours).",
          positiveImpact: true,
        },
        {
          factorName: "Mastery & Experience",
          score: 9,
          maxScore: 10,
          weightPercent: 10,
          explanation: "8+ years in business with 650+ repairs logged.",
          positiveImpact: true,
        },
        {
          factorName: "Sustainability & Circularity",
          score: 5,
          maxScore: 5,
          weightPercent: 5,
          explanation: "Certified Eco-Partner prioritizing component restoration & e-waste reduction.",
          positiveImpact: true,
        },
      ],
      explanation: {
        summary: "Excellent match for your device. Certified specialist in modern consumer hardware.",
        keyReasons: [
          "Certified hardware specialist with authentic parts guarantee.",
          "Industry-leading 96/100 trust score with 180-day warranty.",
          "Express same-day repair turnaround.",
          "Eco-certified facility with circular component renewal.",
        ],
        compatibilityLevel: "EXCELLENT_MATCH",
        recommendations: [
          "Request formal quote for exact hardware parts",
          "Backup device data prior to drop-off or courier pickup",
          "Ensure 180-day warranty coverage is confirmed upon check-in",
        ],
      },
      estimatedCost: 65,
      turnaroundHours: 4,
      warrantyDays: 180,
      trustScore: 96,
      isEcoCertified: true,
      isDemo: true,
    },
    {
      shopId: "shop-2",
      shopName: "GreenCircuit Refurb & Repair",
      address: "88 Eco Boulevard, Westside",
      latitude: 37.7833,
      longitude: -122.4167,
      phone: "+1 (555) 876-5432",
      email: "support@greencircuit.org",
      hours: "Mon-Fri 9:00AM - 7:00PM",
      rating: 4.8,
      reviewCount: 98,
      verificationStatus: "TRUSTED",
      verificationLevel: "VERIFIED",
      distanceKm: 3.4,
      overallScore: 91,
      matchLevel: "EXCELLENT_MATCH",
      rank: 2,
      factors: [
        {
          factorName: "Specialization & Hardware Fit",
          score: 23,
          maxScore: 25,
          weightPercent: 25,
          explanation: "Extensive experience in board soldering and screen replacement.",
          positiveImpact: true,
        },
        {
          factorName: "Trust & Verified Reputation",
          score: 19,
          maxScore: 20,
          weightPercent: 20,
          explanation: "Verified partner with 4.8 star average.",
          positiveImpact: true,
        },
        {
          factorName: "Quote & Pricing Value",
          score: 15,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Best value pricing ~$50 baseline fee.",
          positiveImpact: true,
        },
        {
          factorName: "Proximity & Distance",
          score: 13,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Nearby location (3.4 km).",
          positiveImpact: true,
        },
        {
          factorName: "Speed & Turnaround",
          score: 8,
          maxScore: 10,
          weightPercent: 10,
          explanation: "Standard 24-48h turnaround.",
          positiveImpact: true,
        },
        {
          factorName: "Mastery & Experience",
          score: 8,
          maxScore: 10,
          weightPercent: 10,
          explanation: "6+ years servicing with 400+ repairs.",
          positiveImpact: true,
        },
        {
          factorName: "Sustainability & Circularity",
          score: 5,
          maxScore: 5,
          weightPercent: 5,
          explanation: "Top-tier zero-waste circular repair program.",
          positiveImpact: true,
        },
      ],
      explanation: {
        summary: "Top eco-choice providing maximum price value and sustainable parts reuse.",
        keyReasons: [
          "Lowest estimated baseline service cost ($50).",
          "Premier certified green repair partner.",
          "High customer satisfaction rating (4.8★).",
        ],
        compatibilityLevel: "EXCELLENT_MATCH",
        recommendations: [
          "Ask about eco-rebate for trading in depleted battery cells",
          "Confirm drop-off appointment window",
        ],
      },
      estimatedCost: 50,
      turnaroundHours: 24,
      warrantyDays: 120,
      trustScore: 93,
      isEcoCertified: true,
      isDemo: true,
    },
    {
      shopId: "shop-3",
      shopName: "RapidFix Silicon Valley",
      address: "101 Fast Track Way, Downtown",
      latitude: 37.765,
      longitude: -122.43,
      phone: "+1 (555) 321-7654",
      email: "help@rapidfix.com",
      hours: "Mon-Sun 8:00AM - 8:00PM",
      rating: 4.7,
      reviewCount: 210,
      verificationStatus: "VERIFIED",
      verificationLevel: "BASIC",
      distanceKm: 2.1,
      overallScore: 86,
      matchLevel: "GREAT_MATCH",
      rank: 3,
      factors: [
        {
          factorName: "Specialization & Hardware Fit",
          score: 22,
          maxScore: 25,
          weightPercent: 25,
          explanation: "Specialized in rapid screen and battery modular replacement.",
          positiveImpact: true,
        },
        {
          factorName: "Trust & Verified Reputation",
          score: 17,
          maxScore: 20,
          weightPercent: 20,
          explanation: "High review volume (210 reviews).",
          positiveImpact: true,
        },
        {
          factorName: "Quote & Pricing Value",
          score: 12,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Competitive express pricing ~$75.",
          positiveImpact: true,
        },
        {
          factorName: "Proximity & Distance",
          score: 14,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Convenient downtown location (2.1 km).",
          positiveImpact: true,
        },
        {
          factorName: "Speed & Turnaround",
          score: 10,
          maxScore: 10,
          weightPercent: 10,
          explanation: "Fastest 1-2 hour express turnaround.",
          positiveImpact: true,
        },
        {
          factorName: "Mastery & Experience",
          score: 8,
          maxScore: 10,
          weightPercent: 10,
          explanation: "High volume express repair center.",
          positiveImpact: true,
        },
        {
          factorName: "Sustainability & Circularity",
          score: 3,
          maxScore: 5,
          weightPercent: 5,
          explanation: "Standard parts recycling compliance.",
          positiveImpact: false,
        },
      ],
      explanation: {
        summary: "Fastest turnaround option for urgent screen and battery replacements.",
        keyReasons: [
          "1-2 hour express repairs available 7 days a week.",
          "Over 200 customer reviews.",
          "Central downtown location.",
        ],
        compatibilityLevel: "GREAT_MATCH",
        recommendations: [
          "Book express slot ahead of arrival",
          "Inquire about same-day parts availability",
        ],
      },
      estimatedCost: 75,
      turnaroundHours: 2,
      warrantyDays: 90,
      trustScore: 88,
      isEcoCertified: false,
      isDemo: true,
    },
    {
      shopId: "shop-4",
      shopName: "Precision Motherboard Labs",
      address: "72 Foundry Center, Industrial Park",
      latitude: 37.79,
      longitude: -122.4,
      phone: "+1 (555) 999-1122",
      email: "lab@precisionmotherboard.net",
      hours: "Mon-Fri 10:00AM - 6:00PM",
      rating: 4.9,
      reviewCount: 76,
      verificationStatus: "TRUSTED",
      verificationLevel: "PREMIUM",
      distanceKm: 5.2,
      overallScore: 89,
      matchLevel: "EXCELLENT_MATCH",
      rank: 4,
      factors: [
        {
          factorName: "Specialization & Hardware Fit",
          score: 25,
          maxScore: 25,
          weightPercent: 25,
          explanation: "Elite micro-soldering, BGA rework, and complex logic board repair lab.",
          positiveImpact: true,
        },
        {
          factorName: "Trust & Verified Reputation",
          score: 20,
          maxScore: 20,
          weightPercent: 20,
          explanation: "ISO-compliant cleanroom lab with 98% repair success rate.",
          positiveImpact: true,
        },
        {
          factorName: "Quote & Pricing Value",
          score: 9,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Premium complex board repair rate ~$120.",
          positiveImpact: false,
        },
        {
          factorName: "Proximity & Distance",
          score: 11,
          maxScore: 15,
          weightPercent: 15,
          explanation: "Industrial district facility (5.2 km).",
          positiveImpact: true,
        },
        {
          factorName: "Speed & Turnaround",
          score: 6,
          maxScore: 10,
          weightPercent: 10,
          explanation: "Thorough multi-day diagnostic & stress test (72h).",
          positiveImpact: false,
        },
        {
          factorName: "Mastery & Experience",
          score: 10,
          maxScore: 10,
          weightPercent: 10,
          explanation: "12+ years master technicians in micro-electronics.",
          positiveImpact: true,
        },
        {
          factorName: "Sustainability & Circularity",
          score: 5,
          maxScore: 5,
          weightPercent: 5,
          explanation: "Component-level rescue saves entire logic boards from landfill.",
          positiveImpact: true,
        },
      ],
      explanation: {
        summary: "Unmatched expertise for severe board-level failures and liquid damage recovery.",
        keyReasons: [
          "Component-level micro-soldering saves up to 70% vs complete board replacement.",
          "Highest technical precision rating.",
          "Full diagnostic trace provided.",
        ],
        compatibilityLevel: "EXCELLENT_MATCH",
        recommendations: [
          "Ideal if other shops diagnosed an unfixable dead board",
          "Ensure power supply accessories are provided for full loop testing",
        ],
      },
      estimatedCost: 120,
      turnaroundHours: 72,
      warrantyDays: 180,
      trustScore: 97,
      isEcoCertified: true,
      isDemo: true,
    },
  ];
}

export function getDemoRecommendations(deviceId: string): SmartRecommendationResponse {
  const matches = getDemoMatches(deviceId);
  return {
    deviceId: deviceId || "dev-demo",
    deviceName: "Apple MacBook Pro M2 (2023)",
    recommendations: [
      {
        category: "BEST_OVERALL",
        categoryLabel: "Best Overall Match",
        shop: matches[0],
        highlightReason: "Top holistic score (96/100) combining certified expertise, verified reputation, and same-day turnaround.",
      },
      {
        category: "BEST_VALUE",
        categoryLabel: "Best Value Choice",
        shop: matches[1],
        highlightReason: "Most competitive baseline repair cost (~$50) with 120-day warranty coverage.",
      },
      {
        category: "FASTEST_REPAIR",
        categoryLabel: "Fastest Turnaround",
        shop: matches[2],
        highlightReason: "Express 1-2 hour repair turnaround for minimal device downtime.",
      },
      {
        category: "MOST_TRUSTED",
        categoryLabel: "Highest Trust Rating",
        shop: matches[3],
        highlightReason: "ISO-grade precision lab with industry-leading 97/100 trust score.",
      },
      {
        category: "MOST_SUSTAINABLE",
        categoryLabel: "Eco & Circularity Leader",
        shop: matches[1],
        highlightReason: "Certified green partner specializing in zero-waste component restoration.",
      },
      {
        category: "NEAREST",
        categoryLabel: "Nearest Location",
        shop: matches[0],
        highlightReason: "Conveniently located within 1.8 km of your registered area.",
      },
    ],
    topMatches: matches,
    totalEvaluated: matches.length,
    generatedAt: new Date().toISOString(),
    isDemo: true,
  };
}

export function getDemoComparison(shopIds?: string[]): RepairMarketplaceComparison {
  const allShops = getDemoMatches("dev-demo");
  const selected = (shopIds && shopIds.length > 0)
    ? allShops.filter((s) => shopIds.includes(s.shopId))
    : allShops.slice(0, 3);
  const shops = selected.length > 0 ? selected : allShops.slice(0, 3);

  return {
    shops,
    metrics: [
      {
        metricKey: "COMPATIBILITY",
        metricName: "Compatibility Match",
        description: "Deterministic 0-100 overall fit score",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `${s.overallScore}/100`])),
        winnerShopId: shops[0].shopId,
      },
      {
        metricKey: "TRUST",
        metricName: "Trust & Verification",
        description: "Platform verified trust score and audit level",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `${s.trustScore}/100 (${s.verificationLevel})`])),
        winnerShopId: shops.reduce((max, s) => (s.trustScore > max.trustScore ? s : max), shops[0]).shopId,
      },
      {
        metricKey: "RATING",
        metricName: "Customer Reviews",
        description: "Verified customer review average and feedback volume",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `${s.rating} ★ (${s.reviewCount} reviews)`])),
        winnerShopId: shops.reduce((max, s) => (s.rating > max.rating ? s : max), shops[0]).shopId,
      },
      {
        metricKey: "PRICE",
        metricName: "Estimated Service Cost",
        description: "Estimated baseline repair & diagnostics pricing",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `$${s.estimatedCost.toFixed(0)} Est.`])),
        winnerShopId: shops.reduce((min, s) => (s.estimatedCost < min.estimatedCost ? s : min), shops[0]).shopId,
      },
      {
        metricKey: "DISTANCE",
        metricName: "Proximity & Distance",
        description: "Distance from user coordinates or regional dispatch",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, s.distanceKm ? `${s.distanceKm.toFixed(1)} km` : "Regional"])),
        winnerShopId: shops.reduce((min, s) => ((s.distanceKm || 999) < (min.distanceKm || 999) ? s : min), shops[0]).shopId,
      },
      {
        metricKey: "TURNAROUND",
        metricName: "Turnaround Speed",
        description: "Estimated completion and testing window",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `${s.turnaroundHours.toFixed(0)} Hours`])),
        winnerShopId: shops.reduce((min, s) => (s.turnaroundHours < min.turnaroundHours ? s : min), shops[0]).shopId,
      },
      {
        metricKey: "WARRANTY",
        metricName: "Warranty Guarantee",
        description: "Post-repair parts and labor warranty protection",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, `${s.warrantyDays} Days Guarantee`])),
        winnerShopId: shops.reduce((max, s) => (s.warrantyDays > max.warrantyDays ? s : max), shops[0]).shopId,
      },
      {
        metricKey: "SUSTAINABILITY",
        metricName: "Circularity & Eco Care",
        description: "Component level reuse and e-waste prevention",
        shopValues: Object.fromEntries(shops.map((s) => [s.shopId, s.isEcoCertified ? "Certified Eco-Partner 🌱" : "Standard Compliance"])),
        winnerShopId: shops.find((s) => s.isEcoCertified)?.shopId || shops[0].shopId,
      },
    ],
    bestOverallShopId: shops[0].shopId,
    bestValueShopId: shops.reduce((min, s) => (s.estimatedCost < min.estimatedCost ? s : min), shops[0]).shopId,
    fastestShopId: shops.reduce((min, s) => (s.turnaroundHours < min.turnaroundHours ? s : min), shops[0]).shopId,
    mostTrustedShopId: shops.reduce((max, s) => (s.trustScore > max.trustScore ? s : max), shops[0]).shopId,
    mostSustainableShopId: shops.find((s) => s.isEcoCertified)?.shopId || shops[0].shopId,
    nearestShopId: shops.reduce((min, s) => ((s.distanceKm || 999) < (min.distanceKm || 999) ? s : min), shops[0]).shopId,
    comparisonSummary: `Comparing ${shops.length} repair providers: ${shops[0].shopName} leads in overall compatibility (${shops[0].overallScore}/100), while ${shops.find((s) => s.shopId === shops.reduce((min, x) => (x.estimatedCost < min.estimatedCost ? x : min), shops[0]).shopId)?.shopName || "Value Shop"} provides the most economical option.`,
    isDemo: true,
  };
}

export function getDemoQuoteIntelligence(quoteId: string): QuoteIntelligenceResponse {
  return {
    quoteId: quoteId || "demo-quote-1",
    repairShopId: "shop-1",
    shopName: "Apex Micro-Electronics Care",
    estimatedCost: 65,
    partsCost: 35,
    laborCost: 30,
    marketAverageCost: 80,
    costDifference: -15,
    costDifferencePercent: -18.75,
    classification: "GOOD_VALUE",
    classificationLabel: "✓ Good Value",
    priceFairnessScore: 92,
    insights: [
      "Quoted price ($65.00) is 18.8% below the regional market benchmark ($80.00).",
      "Cost breakdown: $35.00 Certified Parts + $30.00 Certified Labor.",
      "Includes 180-day comprehensive component guarantee.",
      "Fair labor rate transparently indexed against standard board diagnostics.",
    ],
    warnings: [],
    isDemo: true,
  };
}

export function getDemoUserInsights(): UserMarketplaceInsights {
  const matches = getDemoMatches("dev-demo");
  return {
    totalShopsCompared: 6,
    totalQuotesRequested: 3,
    totalQuotesAccepted: 2,
    averageRepairCost: 64.5,
    totalPotentialSavings: 52.0,
    bestValueOpportunities: [
      "3 certified repair providers offer express same-day battery servicing within 5km",
      "Up to 25% cost reduction by accepting multi-quote bidding for screen repairs",
      "Certified eco-partners provide free diagnostics when recycling old components",
    ],
    recentMatches: matches.slice(0, 3),
    isDemo: true,
  };
}

export function getDemoPlatformAnalytics(): PlatformMarketplaceAnalytics {
  return {
    totalShops: 16,
    verifiedShops: 14,
    totalQuotes: 124,
    quoteAcceptanceRate: 78.4,
    averageMarketplaceRepairCost: 72.8,
    popularDeviceCategories: {
      Smartphone: 145,
      Laptop: 98,
      Tablet: 42,
      Wearable: 28,
      "Audio & Peripherals": 19,
    },
    topRequestedRepairs: {
      "Screen & OLED Assembly": 112,
      "Battery Renewal": 89,
      "Logic Board / Micro-soldering": 47,
      "Charging Port Replacement": 38,
      "Camera Module Fix": 24,
    },
    highPerformingShops: [
      {
        shopId: "shop-1",
        shopName: "Apex Micro-Electronics Care",
        trustScore: 96,
        averageRating: 4.9,
        totalQuotesAccepted: 48,
        acceptanceRate: 88.5,
      },
      {
        shopId: "shop-2",
        shopName: "GreenCircuit Refurb & Repair",
        trustScore: 93,
        averageRating: 4.8,
        totalQuotesAccepted: 36,
        acceptanceRate: 84.0,
      },
      {
        shopId: "shop-3",
        shopName: "RapidFix Silicon Valley",
        trustScore: 88,
        averageRating: 4.7,
        totalQuotesAccepted: 52,
        acceptanceRate: 79.2,
      },
      {
        shopId: "shop-4",
        shopName: "Precision Motherboard Labs",
        trustScore: 97,
        averageRating: 4.9,
        totalQuotesAccepted: 29,
        acceptanceRate: 91.0,
      },
    ],
    interactionTrends: {
      MATCH_SEARCHED: 320,
      SHOP_VIEWED: 280,
      SHOP_COMPARED: 165,
      QUOTE_REQUESTED: 115,
      QUOTE_ACCEPTED: 82,
    },
    isDemo: true,
  };
}
