/**
 * RepairVerse AI - Centralized Environment and Application Configuration
 *
 * All API endpoint URLs, storage keys, and environment flags are consolidated here.
 * Never expose private backend secrets (e.g. Gemini keys, database credentials,
 * Cloudinary secrets, JWT signing keys) in NEXT_PUBLIC_* variables.
 */

// Normalize API base URL by stripping trailing slashes
const rawApiUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1";
export const API_BASE_URL: string = rawApiUrl.replace(/\/+$/, "");

/** Environment mode flags */
export const IS_PRODUCTION: boolean = process.env.NODE_ENV === "production";
export const IS_DEVELOPMENT: boolean = process.env.NODE_ENV === "development";

/** Request timeout in milliseconds (default 15s) */
export const DEFAULT_REQUEST_TIMEOUT_MS = 15000;

/** Local storage key constants to prevent key collisions */
export const STORAGE_KEYS = {
  TOKEN: "rv_token",
  USER: "rv_user",
  THEME: "rv_theme",
  DEMO_NOTICE_DISMISSED: "rv_demo_dismissed",
} as const;

/** Application metadata */
export const APP_CONFIG = {
  name: "RepairVerse AI",
  tagline: "AI-Powered Repair Intelligence Platform",
  version: "1.0.0",
  backendTarget: API_BASE_URL,
  supportEmail: "support@repairverse.ai",
} as const;

/** Helper to construct a normalized endpoint URL */
export function buildApiUrl(endpoint: string): string {
  if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
    return endpoint;
  }
  const cleanEndpoint = endpoint.startsWith("/") ? endpoint : `/${endpoint}`;
  return `${API_BASE_URL}${cleanEndpoint}`;
}
