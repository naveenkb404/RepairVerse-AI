import { ApiResponse } from "@/lib/types/auth";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";

export async function apiClient<T>(
  endpoint: string,
  options: {
    method?: "GET" | "POST" | "PUT" | "DELETE" | "PATCH";
    body?: unknown;
    token?: string | null;
    headers?: Record<string, string>;
  } = {}
): Promise<ApiResponse<T>> {
  const { method = "GET", body, token, headers = {} } = options;

  const requestHeaders: Record<string, string> = {
    "Content-Type": "application/json",
    ...headers,
  };

  if (token) {
    requestHeaders["Authorization"] = `Bearer ${token}`;
  }

  try {
    const url = endpoint.startsWith("http") ? endpoint : `${API_BASE_URL}${endpoint.startsWith("/") ? "" : "/"}${endpoint}`;

    const res = await fetch(url, {
      method,
      headers: requestHeaders,
      body: body ? JSON.stringify(body) : undefined,
      cache: "no-store",
    });

    if (!res.ok) {
      const errorJson = await res.json().catch(() => null);
      return {
        success: false,
        message: errorJson?.message || `Request failed with HTTP status ${res.status}`,
      };
    }

    const data = await res.json();
    return {
      success: true,
      message: data.message || "Success",
      data: data.data !== undefined ? data.data : data,
    };
  } catch (error) {
    const errorMsg = error instanceof Error ? error.message : "Network error or backend unreachable";
    return {
      success: false,
      message: `Failed to communicate with API server (${errorMsg}).`,
    };
  }
}
