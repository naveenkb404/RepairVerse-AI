import { DiagnosisRequest, DiagnosisResponse } from "@/lib/types/diagnosis";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

/**
 * AI Diagnosis API Service Layer
 * Connects to documented RepairVerse-AI Spring Boot endpoint:
 * - POST /api/v1/diagnosis
 *
 * Security Note:
 * Google Gemini API keys are maintained strictly on the Spring Boot backend server.
 * Next.js frontend calls POST /api/v1/diagnosis via HTTPS, which invokes Gemini server-side.
 */
export async function analyzeDeviceDiagnosis(
  requestData: DiagnosisRequest,
  token?: string
): Promise<DiagnosisResponse> {
  try {
    const formData = new FormData();
    if (requestData.deviceId) formData.append("deviceId", requestData.deviceId);
    if (requestData.deviceCategory) formData.append("deviceCategory", requestData.deviceCategory);
    if (requestData.brand) formData.append("brand", requestData.brand);
    if (requestData.model) formData.append("model", requestData.model);
    formData.append("symptoms", requestData.symptoms);

    if (requestData.image instanceof File) {
      formData.append("image", requestData.image);
    } else if (typeof requestData.image === "string") {
      formData.append("imageUrl", requestData.image);
    }

    const headers: HeadersInit = {};
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}/diagnosis`, {
      method: "POST",
      headers,
      body: formData,
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => null);
      return {
        success: false,
        message: errorData?.message || `Diagnosis failed with status ${response.status}`,
      };
    }

    const data = await response.json();
    return {
      success: true,
      message: data.message || "AI diagnosis completed successfully",
      data: data.data || data,
    };
  } catch {
    return {
      success: false,
      message:
        "Backend AI diagnosis service is currently offline. " +
        "Please verify Spring Boot API server at " +
        API_BASE_URL,
    };
  }
}
