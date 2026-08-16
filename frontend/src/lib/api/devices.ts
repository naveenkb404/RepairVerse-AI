import {
  CreateDeviceRequest,
  Device,
  DeviceDetailResponse,
  DeviceListResponse,
  DevicePassportData,
  DevicePassportResponse,
} from "@/lib/types/device";
import { apiClient } from "@/lib/api/client";
import { API_BASE_URL } from "@/lib/config";
import { isDemoSession } from "@/lib/demo";

// ─── Sample Reference Data for Demo/Offline Mode ─────────────────────────────
export const SAMPLE_DEVICES: Device[] = [
  {
    id: "dev_sample_1",
    userId: "usr_demo",
    deviceName: "Personal iPhone 14 Pro",
    category: "Smartphone",
    brand: "Apple",
    model: "iPhone 14 Pro (128GB)",
    serialNumber: "F2LX9001K992",
    purchaseDate: "2023-01-15",
    warrantyExpiry: "2024-01-15",
    purchasePrice: 999,
    currentCondition: "Good",
    createdAt: "2023-01-15T10:00:00Z",
  },
  {
    id: "dev_sample_2",
    userId: "usr_demo",
    deviceName: "Work Macbook Pro 16",
    category: "Laptop",
    brand: "Apple",
    model: "MacBook Pro 16\" (M2 Max)",
    serialNumber: "C02G3002M123",
    purchaseDate: "2023-04-10",
    warrantyExpiry: "2026-04-10",
    purchasePrice: 2499,
    currentCondition: "Excellent",
    createdAt: "2023-04-10T11:30:00Z",
  },
  {
    id: "dev_sample_3",
    userId: "usr_demo",
    deviceName: "Living Room Gaming Console",
    category: "Gaming Console",
    brand: "Sony",
    model: "PlayStation 5 Disc Edition",
    serialNumber: "P5-882091-EU",
    purchaseDate: "2022-11-20",
    warrantyExpiry: "2023-11-20",
    purchasePrice: 499,
    currentCondition: "Needs Attention",
    createdAt: "2022-11-20T14:15:00Z",
  },
  {
    id: "dev_sample_4",
    userId: "usr_demo",
    deviceName: "Study iPad Air",
    category: "Tablet",
    brand: "Apple",
    model: "iPad Air 5th Gen (64GB)",
    serialNumber: "DMPZ7003A456",
    purchaseDate: "2023-08-05",
    warrantyExpiry: "2024-08-05",
    purchasePrice: 599,
    currentCondition: "Fair",
    createdAt: "2023-08-05T09:20:00Z",
  },
];

export const SAMPLE_PASSPORTS: Record<string, DevicePassportData> = {
  dev_sample_1: {
    device: SAMPLE_DEVICES[0],
    health: {
      id: "hlth_1",
      deviceId: "dev_sample_1",
      batteryHealth: 88,
      healthScore: 86,
      lastService: "2024-02-10",
      maintenanceDue: "2024-11-15",
      aiPrediction:
        "Battery capacity degradation detected (88%). Display and logic board operating at optimal parameters.",
    },
    diagnosisSummary: {
      probableIssue: "Slight battery capacity degradation",
      confidenceScore: 92,
      repairDifficulty: "Moderate",
      repairCost: 79,
      lastDiagnosisDate: "2024-02-10",
    },
    repairSummary: {
      repairsCompleted: 1,
      lastRepairDate: "2024-02-10",
      lastRecommendedAction: "Schedule Battery Replacement within 3 months",
    },
    carbonSummary: {
      co2SavedKg: 42.5,
      ewasteReducedKg: 0.21,
      moneySaved: 850,
    },
    lifecycleTimeline: [
      {
        id: "evt_1",
        date: "2023-01-15",
        title: "Device Registered",
        type: "purchase",
        description: "Purchased new and initialized digital Health Passport.",
      },
      {
        id: "evt_2",
        date: "2023-08-12",
        title: "Routine AI Diagnostic Check",
        type: "diagnosis",
        description: "All core sensors, battery health, and display verified 100% operational.",
      },
      {
        id: "evt_3",
        date: "2024-02-10",
        title: "Battery Health Inspection",
        type: "service",
        description: "AI Diagnosis flagged minor battery wear (88%). Recommended servicing.",
      },
    ],
  },
  dev_sample_2: {
    device: SAMPLE_DEVICES[1],
    health: {
      id: "hlth_2",
      deviceId: "dev_sample_2",
      batteryHealth: 96,
      healthScore: 94,
      lastService: "2024-05-01",
      maintenanceDue: "2025-04-10",
      aiPrediction: "Thermal efficiency optimal. Battery condition prime. No hardware defects detected.",
    },
    diagnosisSummary: {
      probableIssue: "Fan dust buildup (Minor)",
      confidenceScore: 95,
      repairDifficulty: "Easy",
      repairCost: 35,
      lastDiagnosisDate: "2024-05-01",
    },
    repairSummary: {
      repairsCompleted: 0,
      lastRecommendedAction: "Perform periodic fan cleaning",
    },
    carbonSummary: {
      co2SavedKg: 120,
      ewasteReducedKg: 2.15,
      moneySaved: 2100,
    },
    lifecycleTimeline: [
      {
        id: "evt_201",
        date: "2023-04-10",
        title: "Device Purchased & Registered",
        type: "purchase",
        description: "Work MacBook Pro 16 enrolled in RepairVerse Passport.",
      },
      {
        id: "evt_202",
        date: "2024-05-01",
        title: "AI Visual & System Scan",
        type: "inspection",
        description: "Confirmed pristine state with 94/100 AI Health Rating.",
      },
    ],
  },
  dev_sample_3: {
    device: SAMPLE_DEVICES[2],
    health: {
      id: "hlth_3",
      deviceId: "dev_sample_3",
      healthScore: 68,
      lastService: "2023-10-15",
      maintenanceDue: "Immediate",
      aiPrediction: "Overheating detected during high TDP load. Dust buildup or thermal paste degradation likely.",
    },
    diagnosisSummary: {
      probableIssue: "Optical drive / Fan cooling unit obstruction",
      confidenceScore: 89,
      repairDifficulty: "Moderate",
      repairCost: 65,
      lastDiagnosisDate: "2023-10-15",
    },
    repairSummary: {
      repairsCompleted: 2,
      lastRepairDate: "2023-10-15",
      lastRecommendedAction: "Clean cooling heatsink & reapply thermal paste",
    },
    carbonSummary: {
      co2SavedKg: 65.4,
      ewasteReducedKg: 4.5,
      moneySaved: 420,
    },
    lifecycleTimeline: [
      {
        id: "evt_301",
        date: "2022-11-20",
        title: "Purchase & Setup",
        type: "purchase",
        description: "PS5 registered to user profile.",
      },
      {
        id: "evt_302",
        date: "2023-10-15",
        title: "Thermal Maintenance",
        type: "service",
        description: "Serviced at authorized repair center for internal dust removal.",
      },
    ],
  },
  dev_sample_4: {
    device: SAMPLE_DEVICES[3],
    health: {
      id: "hlth_4",
      deviceId: "dev_sample_4",
      batteryHealth: 82,
      healthScore: 78,
      lastService: "2024-01-20",
      maintenanceDue: "2024-09-30",
      aiPrediction: "Touch digitizer responsive. Micro-scratches present on glass panel.",
    },
    diagnosisSummary: {
      probableIssue: "Minor screen micro-fissures",
      confidenceScore: 84,
      repairDifficulty: "Hard",
      repairCost: 140,
      lastDiagnosisDate: "2024-01-20",
    },
    repairSummary: {
      repairsCompleted: 1,
      lastRepairDate: "2024-01-20",
      lastRecommendedAction: "Apply screen protector or consider display glass re-glassing",
    },
    carbonSummary: {
      co2SavedKg: 38,
      ewasteReducedKg: 0.46,
      moneySaved: 460,
    },
    lifecycleTimeline: [
      {
        id: "evt_401",
        date: "2023-08-05",
        title: "Device Registered",
        type: "purchase",
        description: "iPad Air enrolled in passport tracking.",
      },
      {
        id: "evt_402",
        date: "2024-01-20",
        title: "AI Visual Glass Diagnosis",
        type: "diagnosis",
        description: "Assessed surface scratches; structural glass integrity intact.",
      },
    ],
  },
};

// ─── API Functions ────────────────────────────────────────────────────────────

export async function fetchUserDevices(
  token?: string,
  signal?: AbortSignal
): Promise<DeviceListResponse> {
  if (isDemoSession(token)) {
    return {
      success: true,
      data: SAMPLE_DEVICES,
    };
  }

  const result = await apiClient<Device[]>("/devices", {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data && Array.isArray(result.data)) {
    return { success: true, data: result.data };
  }

  // Offline / Demo fallback
  return {
    success: true,
    data: SAMPLE_DEVICES,
    message: `Backend devices service at ${API_BASE_URL}/devices is offline. Displaying sample devices.`,
  };
}

export async function fetchDeviceById(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<DeviceDetailResponse> {
  if (isDemoSession(token)) {
    const found = SAMPLE_DEVICES.find((d) => d.id === deviceId);
    if (found) return { success: true, data: found };
  }

  const result = await apiClient<Device>(`/devices/${deviceId}`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  // Offline / Demo fallback
  const found = SAMPLE_DEVICES.find((d) => d.id === deviceId);
  if (found) {
    return { success: true, data: found };
  }
  return {
    success: false,
    message: "Device not found or backend API offline.",
  };
}

export async function fetchDevicePassport(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<DevicePassportResponse> {
  if (isDemoSession(token)) {
    const samplePassport = SAMPLE_PASSPORTS[deviceId];
    if (samplePassport) return { success: true, data: samplePassport };
  }

  const result = await apiClient<DevicePassportData>(`/devices/${deviceId}/passport`, {
    method: "GET",
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  // Offline / Demo fallback
  const samplePassport = SAMPLE_PASSPORTS[deviceId];
  if (samplePassport) {
    return { success: true, data: samplePassport };
  }

  // Default synthesized passport if device exists in sample list
  const foundDevice = SAMPLE_DEVICES.find((d) => d.id === deviceId);
  if (foundDevice) {
    return {
      success: true,
      data: {
        device: foundDevice,
        health: {
          deviceId: foundDevice.id,
          healthScore: 85,
          batteryHealth: 90,
          lastService: foundDevice.createdAt?.split("T")[0],
          aiPrediction: "Device operating normally.",
        },
        lifecycleTimeline: [
          {
            id: `evt_${foundDevice.id}`,
            date: foundDevice.createdAt?.split("T")[0] || "2023-01-01",
            title: "Device Registered",
            type: "purchase",
            description: "Initialized digital passport.",
          },
        ],
      },
    };
  }

  return {
    success: false,
    message: "Device passport service is offline or device was not found.",
  };
}

export async function createDevice(
  deviceData: CreateDeviceRequest,
  token?: string,
  signal?: AbortSignal
): Promise<DeviceDetailResponse> {
  if (isDemoSession(token)) {
    const newDevice: Device = {
      id: `dev_demo_${Date.now()}`,
      deviceName: deviceData.deviceName,
      category: deviceData.category,
      brand: deviceData.brand,
      model: deviceData.model,
      serialNumber: deviceData.serialNumber || undefined,
      purchaseDate: deviceData.purchaseDate || undefined,
      warrantyExpiry: deviceData.warrantyExpiry || undefined,
      purchasePrice: deviceData.purchasePrice,
      currentCondition: deviceData.currentCondition,
      createdAt: new Date().toISOString(),
    };

    SAMPLE_DEVICES.unshift(newDevice);
    SAMPLE_PASSPORTS[newDevice.id] = {
      device: newDevice,
      health: {
        deviceId: newDevice.id,
        healthScore:
          deviceData.currentCondition === "Excellent"
            ? 95
            : deviceData.currentCondition === "Good"
            ? 85
            : deviceData.currentCondition === "Fair"
            ? 70
            : 55,
        batteryHealth: 92,
        aiPrediction: "Initial passport generated. Run AI diagnosis for deeper metrics.",
      },
      lifecycleTimeline: [
        {
          id: `evt_new_${Date.now()}`,
          date: new Date().toISOString().split("T")[0],
          title: "Device Registered",
          type: "purchase",
          description: `Registered ${newDevice.brand} ${newDevice.model} in Health Passport.`,
        },
      ],
    };

    return {
      success: true,
      data: newDevice,
      message: "Device registered locally in Demo Mode. Connect Spring Boot backend for cloud persistence.",
    };
  }

  const result = await apiClient<Device>("/devices", {
    method: "POST",
    body: deviceData,
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  // Demo fallback on offline
  const newDevice: Device = {
    id: `dev_demo_${Date.now()}`,
    deviceName: deviceData.deviceName,
    category: deviceData.category,
    brand: deviceData.brand,
    model: deviceData.model,
    serialNumber: deviceData.serialNumber || undefined,
    purchaseDate: deviceData.purchaseDate || undefined,
    warrantyExpiry: deviceData.warrantyExpiry || undefined,
    purchasePrice: deviceData.purchasePrice,
    currentCondition: deviceData.currentCondition,
    createdAt: new Date().toISOString(),
  };

  SAMPLE_DEVICES.unshift(newDevice);
  return {
    success: true,
    data: newDevice,
    message: `Backend API at ${API_BASE_URL}/devices is offline. Device registered locally in Demo Mode.`,
  };
}

export async function updateDevice(
  deviceId: string,
  deviceData: Partial<CreateDeviceRequest>,
  token?: string,
  signal?: AbortSignal
): Promise<DeviceDetailResponse> {
  if (isDemoSession(token)) {
    const idx = SAMPLE_DEVICES.findIndex((d) => d.id === deviceId);
    if (idx !== -1) {
      SAMPLE_DEVICES[idx] = { ...SAMPLE_DEVICES[idx], ...deviceData };
      return { success: true, data: SAMPLE_DEVICES[idx] };
    }
  }

  const result = await apiClient<Device>(`/devices/${deviceId}`, {
    method: "PUT",
    body: deviceData,
    token,
    signal,
  });

  if (result.success && result.data) {
    return { success: true, data: result.data };
  }

  const idx = SAMPLE_DEVICES.findIndex((d) => d.id === deviceId);
  if (idx !== -1) {
    SAMPLE_DEVICES[idx] = { ...SAMPLE_DEVICES[idx], ...deviceData };
    return {
      success: true,
      data: SAMPLE_DEVICES[idx],
      message: "Device updated locally (Demo Mode)",
    };
  }

  return { success: false, message: result.message || "Failed to update device" };
}

export async function deleteDevice(
  deviceId: string,
  token?: string,
  signal?: AbortSignal
): Promise<{ success: boolean; message?: string }> {
  if (isDemoSession(token)) {
    const idx = SAMPLE_DEVICES.findIndex((d) => d.id === deviceId);
    if (idx !== -1) {
      SAMPLE_DEVICES.splice(idx, 1);
    }
    return { success: true, message: "Device removed (Demo Mode)" };
  }

  const result = await apiClient(`/devices/${deviceId}`, {
    method: "DELETE",
    token,
    signal,
  });

  if (result.success) {
    return { success: true, message: result.message || "Device deleted successfully" };
  }

  const idx = SAMPLE_DEVICES.findIndex((d) => d.id === deviceId);
  if (idx !== -1) {
    SAMPLE_DEVICES.splice(idx, 1);
    return {
      success: true,
      message: `Backend API offline. Device removed locally from view.`,
    };
  }

  return { success: false, message: result.message || "Failed to delete device" };
}

