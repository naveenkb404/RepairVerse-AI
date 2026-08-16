import { API_BASE_URL, DEFAULT_REQUEST_TIMEOUT_MS, buildApiUrl } from "@/lib/config";
import type { ApiResponse } from "@/lib/types/auth";

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export interface RequestOptions {
  method?: HttpMethod;
  body?: unknown;
  token?: string | null;
  headers?: Record<string, string>;
  params?: Record<string, string | number | boolean | undefined | null>;
  signal?: AbortSignal;
  timeoutMs?: number;
  cache?: RequestCache;
}

/**
 * Custom typed error class representing API failures.
 */
export class ApiError extends Error {
  status: number;
  code?: string;
  data?: unknown;
  isNetworkError: boolean;
  isTimeout: boolean;

  constructor(
    message: string,
    options: {
      status?: number;
      code?: string;
      data?: unknown;
      isNetworkError?: boolean;
      isTimeout?: boolean;
    } = {}
  ) {
    super(message);
    this.name = "ApiError";
    this.status = options.status ?? 0;
    this.code = options.code;
    this.data = options.data;
    this.isNetworkError = options.isNetworkError ?? false;
    this.isTimeout = options.isTimeout ?? false;
  }
}

/**
 * Event name dispatched when a 401 Unauthorized is encountered,
 * allowing AuthContext to reactively clear invalid session state.
 */
export const AUTH_UNAUTHORIZED_EVENT = "rv:auth:unauthorized";

function notifyUnauthorized() {
  if (typeof window !== "undefined") {
    window.dispatchEvent(new CustomEvent(AUTH_UNAUTHORIZED_EVENT));
  }
}

/**
 * Helper to build query string from parameters object.
 */
function buildQueryString(params?: Record<string, string | number | boolean | undefined | null>): string {
  if (!params) return "";
  const query = new URLSearchParams();
  for (const [key, value] of Object.entries(params)) {
    if (value !== undefined && value !== null && value !== "") {
      query.append(key, String(value));
    }
  }
  const str = query.toString();
  return str ? `?${str}` : "";
}

/**
 * Robust, production-grade HTTP API Client for RepairVerse AI.
 * Handles FormData and JSON payloads, Bearer authorization tokens,
 * abort signals, timeouts, detailed HTTP error translation, and safe parsing.
 */
export async function apiClient<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<ApiResponse<T>> {
  const {
    method = "GET",
    body,
    token,
    headers = {},
    params,
    signal,
    timeoutMs = DEFAULT_REQUEST_TIMEOUT_MS,
    cache = "no-store",
  } = options;

  // Build target URL
  const baseTargetUrl = buildApiUrl(endpoint);
  const targetUrl = `${baseTargetUrl}${buildQueryString(params)}`;

  // Prepare request headers
  const requestHeaders: Record<string, string> = { ...headers };

  const isFormData = typeof FormData !== "undefined" && body instanceof FormData;

  // Set JSON Content-Type only if not FormData
  if (!isFormData && body !== undefined && !requestHeaders["Content-Type"]) {
    requestHeaders["Content-Type"] = "application/json";
  }

  // Attach Bearer authentication token if provided and non-empty
  if (token && token.trim()) {
    requestHeaders["Authorization"] = `Bearer ${token}`;
  }

  // Prepare request body
  let requestBody: BodyInit | undefined;
  if (isFormData) {
    requestBody = body as FormData;
  } else if (body !== undefined) {
    requestBody = typeof body === "string" ? body : JSON.stringify(body);
  }

  // Setup timeout and abort controller
  const timeoutController = new AbortController();
  let isTimedOut = false;

  const timeoutId = setTimeout(() => {
    isTimedOut = true;
    timeoutController.abort();
  }, timeoutMs);

  // Link external signal if provided
  let effectiveSignal = timeoutController.signal;
  if (signal) {
    if (signal.aborted) {
      clearTimeout(timeoutId);
      return {
        success: false,
        message: "Request was cancelled.",
      };
    }
    // If external signal fires, abort our controller as well
    signal.addEventListener("abort", () => {
      timeoutController.abort();
    });
  }

  try {
    const response = await fetch(targetUrl, {
      method,
      headers: requestHeaders,
      body: requestBody,
      cache,
      signal: effectiveSignal,
    });

    clearTimeout(timeoutId);

    // Parse response safely
    let responseData: any = null;
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
      try {
        responseData = await response.json();
      } catch {
        responseData = null;
      }
    } else {
      const text = await response.text().catch(() => "");
      responseData = text ? { message: text } : null;
    }

    // Handle non-2xx HTTP status codes
    if (!response.ok) {
      const status = response.status;
      let errorMessage = responseData?.message || "";

      if (!errorMessage) {
        switch (status) {
          case 400:
            errorMessage = "Bad Request: The submitted data is invalid.";
            break;
          case 401:
            errorMessage = "Unauthorized: Session expired or invalid authentication.";
            notifyUnauthorized();
            break;
          case 403:
            errorMessage = "Forbidden: You do not have permission to perform this action.";
            break;
          case 404:
            errorMessage = "Not Found: The requested resource does not exist.";
            break;
          case 409:
            errorMessage = "Conflict: The resource already exists or state is in conflict.";
            break;
          case 422:
            errorMessage = "Unprocessable Entity: Validation failed for submitted fields.";
            break;
          case 429:
            errorMessage = "Too Many Requests: Rate limit exceeded. Please wait a moment.";
            break;
          case 500:
            errorMessage = "Internal Server Error: The backend server encountered an error.";
            break;
          case 502:
          case 503:
          case 504:
            errorMessage = "Backend Service Unavailable: Spring Boot server is starting or unreachable.";
            break;
          default:
            errorMessage = `Request failed with HTTP status ${status}.`;
        }
      } else if (status === 401) {
        notifyUnauthorized();
      }

      return {
        success: false,
        message: errorMessage,
        data: responseData?.data !== undefined ? responseData.data : responseData,
      };
    }

    // Return successful response
    const extractedData =
      responseData?.data !== undefined ? responseData.data : responseData;

    return {
      success: true,
      message: responseData?.message || "Success",
      data: extractedData as T,
    };
  } catch (error) {
    clearTimeout(timeoutId);

    if (isTimedOut) {
      return {
        success: false,
        message: `API request timed out after ${timeoutMs / 1000}s. Server may be offline.`,
      };
    }

    if (error instanceof Error && error.name === "AbortError") {
      return {
        success: false,
        message: "Request cancelled.",
      };
    }

    const errorMsg = error instanceof Error ? error.message : "Network error";
    return {
      success: false,
      message: `Failed to connect to backend server at ${API_BASE_URL} (${errorMsg}).`,
    };
  }
}

/** Convenience HTTP Method wrappers */
export const http = {
  get: <T>(endpoint: string, options?: Omit<RequestOptions, "method">) =>
    apiClient<T>(endpoint, { ...options, method: "GET" }),

  post: <T>(endpoint: string, body?: unknown, options?: Omit<RequestOptions, "method" | "body">) =>
    apiClient<T>(endpoint, { ...options, method: "POST", body }),

  put: <T>(endpoint: string, body?: unknown, options?: Omit<RequestOptions, "method" | "body">) =>
    apiClient<T>(endpoint, { ...options, method: "PUT", body }),

  patch: <T>(endpoint: string, body?: unknown, options?: Omit<RequestOptions, "method" | "body">) =>
    apiClient<T>(endpoint, { ...options, method: "PATCH", body }),

  delete: <T>(endpoint: string, options?: Omit<RequestOptions, "method">) =>
    apiClient<T>(endpoint, { ...options, method: "DELETE" }),
};
