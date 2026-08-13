import {
  RepairHistoryDetailApiResponse,
  RepairHistoryItem,
  RepairHistoryListApiResponse,
  RepairHistorySummary,
} from "@/lib/types/repairHistory";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

// ─── Sample Reference Data for Demo/Offline Mode ─────────────────────────────
export const SAMPLE_REPAIR_HISTORY: RepairHistoryItem[] = [
  {
    id: "rep_sample_101",
    deviceId: "dev_sample_1",
    device: {
      id: "dev_sample_1",
      name: "Personal iPhone 14 Pro",
      brand: "Apple",
      model: "iPhone 14 Pro (128GB)",
      category: "Smartphone",
      serialNumber: "F2LX9001K992",
    },
    repairType: "Display OLED Glass & Battery Servicing",
    repairDate: "2024-02-10",
    status: "Completed",
    description:
      "Replaced cracked front glass panel with OEM Super Retina XDR OLED assembly and recalibrated battery management unit.",
    diagnosisIssue: "Cracked Front Glass & Battery Health Degradation (88%)",
    diagnosisConfidence: 94,
    technician: {
      id: "tech_01",
      name: "Alex Vance",
      role: "Certified Master Technician",
      phone: "+91-98765-00001",
      shopName: "Sample Electronics Repair Centre",
      isVerified: true,
    },
    shop: {
      id: "sample_1",
      name: "Sample Electronics Repair Centre",
      address: "42 MG Road, Bengaluru, Karnataka 560001",
      phone: "+91-0000-000001",
      rating: 4.8,
      latitude: 12.9751,
      longitude: 77.6099,
    },
    parts: [
      {
        id: "prt_1",
        name: "iPhone 14 Pro OEM OLED Display Assembly",
        quantity: 1,
        cost: 145,
        partNumber: "APL-14P-DISP",
      },
      {
        id: "prt_2",
        name: "Original Li-ion Battery (3200 mAh)",
        quantity: 1,
        cost: 45,
        partNumber: "APL-14P-BATT",
      },
      {
        id: "prt_3",
        name: "Water Resistance Adhesive Seal",
        quantity: 1,
        cost: 10,
        partNumber: "APL-SEAL-01",
      },
    ],
    partsCost: 200,
    laborCost: 55,
    totalCost: 255,
    repairDuration: "2 hours 30 mins",
    warrantyPeriod: "1 Year Limited Warranty",
    warrantyUntil: "2025-02-10",
    isWarrantyActive: true,
    co2SavedKg: 42.5,
    ewasteReducedKg: 0.21,
    moneySaved: 744,
    notes:
      "Post-repair diagnostic audit confirmed True Tone display functionality, multi-touch calibration, and battery charging cycles 100% restored.",
    timeline: [
      {
        id: "tl_1",
        date: "2024-02-09 14:00",
        title: "AI Diagnosis Logged",
        status: "completed",
        description: "Visual diagnosis identified cracked outer glass and 88% battery capacity.",
      },
      {
        id: "tl_2",
        date: "2024-02-10 10:15",
        title: "Device Dropped Off",
        status: "completed",
        description: "Received at Sample Electronics Repair Centre by Master Tech Alex Vance.",
      },
      {
        id: "tl_3",
        date: "2024-02-10 11:30",
        title: "Parts Replaced & Tested",
        status: "completed",
        description: "OLED panel and battery replaced; thermal adhesive cured.",
      },
      {
        id: "tl_4",
        date: "2024-02-10 12:45",
        title: "Repair Completed & Verified",
        status: "completed",
        description: "Quality assurance test passed. Digital Health Passport updated.",
      },
    ],
  },
  {
    id: "rep_sample_102",
    deviceId: "dev_sample_2",
    device: {
      id: "dev_sample_2",
      name: "Work Macbook Pro 16",
      brand: "Apple",
      model: "MacBook Pro 16\" (M2 Max)",
      category: "Laptop",
      serialNumber: "C02G3002M123",
    },
    repairType: "Internal Cooling Fan Servicing & Thermal Repasting",
    repairDate: "2024-05-01",
    status: "Completed",
    description:
      "Deep internal dust extraction, cooling heatsink decontamination, and application of high-conductivity liquid metal thermal compound.",
    diagnosisIssue: "Thermal Throttling & Heavy Fan Dust Obstruction",
    diagnosisConfidence: 96,
    technician: {
      id: "tech_02",
      name: "Priya Sharma",
      role: "Hardware Specialist",
      phone: "+91-98765-00002",
      shopName: "Sample Tech Fix Studio",
      isVerified: true,
    },
    shop: {
      id: "sample_2",
      name: "Sample Tech Fix Studio",
      address: "18 Brigade Road, Bengaluru, Karnataka 560025",
      phone: "+91-0000-000002",
      rating: 4.5,
    },
    parts: [
      {
        id: "prt_10",
        name: "Thermal Grizzly Kryonaut Liquid Thermal Compound",
        quantity: 1,
        cost: 25,
        partNumber: "TG-KRYO-1G",
      },
      {
        id: "prt_11",
        name: "Silicone Thermal Pads (1.5mm)",
        quantity: 2,
        cost: 15,
        partNumber: "PAD-SIL-1.5",
      },
    ],
    partsCost: 40,
    laborCost: 45,
    totalCost: 85,
    repairDuration: "1 hour 15 mins",
    warrantyPeriod: "6 Months Warranty",
    warrantyUntil: "2024-11-01",
    isWarrantyActive: true,
    co2SavedKg: 120,
    ewasteReducedKg: 2.15,
    moneySaved: 2414,
    notes:
      "CPU/GPU stress benchmark confirmed operating temperatures dropped by 18°C under maximum multi-threaded processing load.",
    timeline: [
      {
        id: "tl_201",
        date: "2024-05-01 09:30",
        title: "Thermal Scan Diagnostics",
        status: "completed",
        description: "AI diagnosis flagged thermal throttling above 94°C.",
      },
      {
        id: "tl_202",
        date: "2024-05-01 10:15",
        title: "Heatsink Serviced & Repasted",
        status: "completed",
        description: "Fans cleaned and thermal paste reapplied.",
      },
      {
        id: "tl_203",
        date: "2024-05-01 11:00",
        title: "Stress Testing Passed",
        status: "completed",
        description: "Temperatures stabilized at optimal 68°C.",
      },
    ],
  },
  {
    id: "rep_sample_103",
    deviceId: "dev_sample_3",
    device: {
      id: "dev_sample_3",
      name: "Living Room Gaming Console",
      brand: "Sony",
      model: "PlayStation 5 Disc Edition",
      category: "Gaming Console",
      serialNumber: "P5-882091-EU",
    },
    repairType: "Optical Drive Laser Lens Replacement",
    repairDate: "2024-07-28",
    status: "In Progress",
    description:
      "Replacing worn optical Blu-ray pickup laser lens and aligning disc feeder mechanism.",
    diagnosisIssue: "Disc Read Error (CE-100005-6)",
    diagnosisConfidence: 91,
    technician: {
      id: "tech_03",
      name: "Rahul Verma",
      role: "Console Repair Specialist",
      phone: "+91-98765-00003",
      shopName: "Sample Circuit Menders",
      isVerified: true,
    },
    shop: {
      id: "sample_4",
      name: "Sample Circuit Menders",
      address: "33 Residency Road, Bengaluru, Karnataka 560025",
      phone: "+91-0000-000004",
      rating: 4.6,
    },
    parts: [
      {
        id: "prt_20",
        name: "PS5 Optical Blu-Ray Laser Pickup Assembly (KES-497A)",
        quantity: 1,
        cost: 55,
        partNumber: "SNY-KES-497A",
      },
    ],
    partsCost: 55,
    laborCost: 40,
    totalCost: 95,
    repairDuration: "Est. 3 hours",
    warrantyPeriod: "90 Days Warranty",
    warrantyUntil: "2024-10-28",
    isWarrantyActive: false,
    co2SavedKg: 65.4,
    ewasteReducedKg: 4.5,
    moneySaved: 404,
    notes: "Replacement optical drive mechanism in assembly stage.",
    timeline: [
      {
        id: "tl_301",
        date: "2024-07-28 11:00",
        title: "Console Diagnosed",
        status: "completed",
        description: "Blu-ray laser read failure confirmed via error logs.",
      },
      {
        id: "tl_302",
        date: "2024-07-28 14:30",
        title: "Disassembly & Laser Alignment",
        status: "current",
        description: "Installing KES-497A optical pickup lens assembly.",
      },
      {
        id: "tl_303",
        date: "2024-07-28 16:00",
        title: "Final Disc Calibration Test",
        status: "pending",
        description: "Verifying Ultra HD Blu-ray playback speeds.",
      },
    ],
  },
  {
    id: "rep_sample_104",
    deviceId: "dev_sample_4",
    device: {
      id: "dev_sample_4",
      name: "Study iPad Air",
      brand: "Apple",
      model: "iPad Air 5th Gen (64GB)",
      category: "Tablet",
      serialNumber: "DMPZ7003A456",
    },
    repairType: "Surface Micro-Glass Re-glassing",
    repairDate: "2024-08-15",
    status: "Scheduled",
    description:
      "Scheduled precision glass re-lamination to eliminate surface micro-abrasions without replacing underlying LCD digitizer.",
    diagnosisIssue: "Surface Micro-fissures (Non-structural)",
    diagnosisConfidence: 85,
    technician: {
      id: "tech_04",
      name: "Demo Technician C",
      role: "Display Glass Technician",
      shopName: "Sample Device Hospital",
    },
    shop: {
      id: "sample_3",
      name: "Sample Device Hospital",
      address: "7 Commercial Street, Bengaluru, Karnataka 560001",
      rating: 4.2,
    },
    parts: [
      {
        id: "prt_30",
        name: "iPad Air 5 OCA Lamination Glass Sheet",
        quantity: 1,
        cost: 35,
        partNumber: "OCA-IPAD-A5",
      },
    ],
    partsCost: 35,
    laborCost: 45,
    totalCost: 80,
    repairDuration: "Est. 2 hours",
    warrantyPeriod: "90 Days Warranty",
    isWarrantyActive: false,
    co2SavedKg: 38,
    ewasteReducedKg: 0.46,
    moneySaved: 519,
    notes: "Appointment scheduled for upcoming maintenance window.",
    timeline: [
      {
        id: "tl_401",
        date: "2024-08-10 10:00",
        title: "AI Visual Inspection",
        status: "completed",
        description: "Assessed surface scratches; structural glass intact.",
      },
      {
        id: "tl_402",
        date: "2024-08-15 11:00",
        title: "Scheduled Service Appointment",
        status: "pending",
        description: "Booked at Sample Device Hospital.",
      },
    ],
  },
];

// ─── API Client Functions ─────────────────────────────────────────────────────

export async function fetchRepairHistory(
  token?: string
): Promise<RepairHistoryListApiResponse> {
  try {
    const headers: HeadersInit = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(`${API_BASE_URL}/repair-history`, {
      method: "GET",
      headers,
      cache: "no-store",
    });

    if (!response.ok) {
      const err = await response.json().catch(() => null);
      // Fall through to demo mode if backend returns error
      if (response.status === 404 || response.status >= 500) {
        return { success: true, data: SAMPLE_REPAIR_HISTORY, isDemo: true };
      }
      return {
        success: false,
        isDemo: true,
        message:
          err?.message ||
          `Failed to fetch repair history (HTTP ${response.status})`,
      };
    }

    const data = await response.json();
    return { success: true, data: data.data || data, isDemo: false };
  } catch {
    // Demo Mode Fallback when Spring Boot backend is offline
    return {
      success: true,
      data: SAMPLE_REPAIR_HISTORY,
      isDemo: true,
    };
  }
}

export async function fetchRepairHistoryById(
  repairId: string,
  token?: string
): Promise<RepairHistoryDetailApiResponse> {
  try {
    const headers: HeadersInit = { "Content-Type": "application/json" };
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(
      `${API_BASE_URL}/repair-history/${repairId}`,
      {
        method: "GET",
        headers,
        cache: "no-store",
      }
    );

    if (!response.ok) {
      // Fall through to demo mode for server errors
      if (response.status >= 500) {
        const found = SAMPLE_REPAIR_HISTORY.find((item) => item.id === repairId);
        if (found) return { success: true, data: found, isDemo: true };
      }
      const err = await response.json().catch(() => null);
      return {
        success: false,
        isDemo: true,
        message:
          err?.message || `Repair record not found (HTTP ${response.status})`,
      };
    }

    const data = await response.json();
    return { success: true, data: data.data || data, isDemo: false };
  } catch {
    // Demo Mode Fallback
    const found = SAMPLE_REPAIR_HISTORY.find((item) => item.id === repairId);
    if (found) {
      return { success: true, data: found, isDemo: true };
    }
    return {
      success: false,
      isDemo: true,
      message: "Repair record not found or backend API offline.",
    };
  }
}

export function computeRepairHistorySummary(
  items: RepairHistoryItem[]
): RepairHistorySummary {
  const completed = items.filter((i) => i.status === "Completed");
  const inProgress = items.filter((i) => i.status === "In Progress");

  const totalSpent = items.reduce((acc, curr) => acc + curr.totalCost, 0);
  const totalSavedMoney = items.reduce(
    (acc, curr) => acc + (curr.moneySaved || 0),
    0
  );
  const totalCo2SavedKg = items.reduce(
    (acc, curr) => acc + (curr.co2SavedKg || 0),
    0
  );
  const totalEwasteReducedKg = items.reduce(
    (acc, curr) => acc + (curr.ewasteReducedKg || 0),
    0
  );

  return {
    totalRepairs: items.length,
    completedRepairs: completed.length,
    inProgressRepairs: inProgress.length,
    totalSpent,
    totalSavedMoney,
    totalCo2SavedKg: Number(totalCo2SavedKg.toFixed(1)),
    totalEwasteReducedKg: Number(totalEwasteReducedKg.toFixed(2)),
  };
}
