"use client";

import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { useRouter } from "next/navigation";
import { STORAGE_KEYS } from "@/lib/config";
import { AUTH_UNAUTHORIZED_EVENT } from "@/lib/api/client";
import { isDemoSession } from "@/lib/demo";
import type { UserProfile } from "@/lib/types/user";

type AuthContextValue = {
  user: UserProfile | null;
  token: string | null;
  isLoggedIn: boolean;
  isLoading: boolean;
  isDemo: boolean;
  sessionExpired: boolean;
  clearSessionExpired: () => void;
  login: (token: string, user: UserProfile) => void;
  logout: (options?: { redirect?: boolean }) => void;
  updateUser: (partial: Partial<UserProfile>) => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [sessionExpired, setSessionExpired] = useState(false);
  const router = useRouter();

  // Rehydrate session from localStorage safely on mount
  useEffect(() => {
    try {
      if (typeof window !== "undefined") {
        const storedToken = localStorage.getItem(STORAGE_KEYS.TOKEN);
        const storedUser = localStorage.getItem(STORAGE_KEYS.USER);
        if (storedToken && storedUser) {
          setToken(storedToken);
          setUser(JSON.parse(storedUser) as UserProfile);
        }
      }
    } catch {
      // Ignore parse or access errors
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Multi-tab synchronization
  useEffect(() => {
    if (typeof window === "undefined") return;

    const handleStorageChange = (e: StorageEvent) => {
      if (e.key === STORAGE_KEYS.TOKEN || e.key === STORAGE_KEYS.USER) {
        const currentToken = localStorage.getItem(STORAGE_KEYS.TOKEN);
        const currentUserStr = localStorage.getItem(STORAGE_KEYS.USER);
        if (currentToken && currentUserStr) {
          try {
            setToken(currentToken);
            setUser(JSON.parse(currentUserStr));
          } catch {
            setToken(null);
            setUser(null);
          }
        } else {
          setToken(null);
          setUser(null);
        }
      }
    };

    window.addEventListener("storage", handleStorageChange);
    return () => window.removeEventListener("storage", handleStorageChange);
  }, []);

  // Reactive 401 Unauthorized handling from apiClient
  useEffect(() => {
    if (typeof window === "undefined") return;

    const handleUnauthorized = () => {
      // Clear token and user on 401 from live backend
      setToken(null);
      setUser(null);
      setSessionExpired(true);
      try {
        localStorage.removeItem(STORAGE_KEYS.TOKEN);
        localStorage.removeItem(STORAGE_KEYS.USER);
      } catch {
        // Storage access error
      }
    };

    window.addEventListener(AUTH_UNAUTHORIZED_EVENT, handleUnauthorized);
    return () => window.removeEventListener(AUTH_UNAUTHORIZED_EVENT, handleUnauthorized);
  }, []);

  const login = useCallback((tok: string, u: UserProfile) => {
    setToken(tok);
    setUser(u);
    setSessionExpired(false);
    try {
      if (typeof window !== "undefined") {
        localStorage.setItem(STORAGE_KEYS.TOKEN, tok);
        localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(u));
      }
    } catch {
      // Storage error ignored
    }
  }, []);

  const logout = useCallback(
    (options: { redirect?: boolean } = { redirect: true }) => {
      setToken(null);
      setUser(null);
      setSessionExpired(false);
      try {
        if (typeof window !== "undefined") {
          localStorage.removeItem(STORAGE_KEYS.TOKEN);
          localStorage.removeItem(STORAGE_KEYS.USER);
        }
      } catch {
        // Storage error ignored
      }
      if (options.redirect !== false) {
        router.push("/auth/login");
      }
    },
    [router]
  );

  const updateUser = useCallback((partial: Partial<UserProfile>) => {
    setUser((prev) => {
      if (!prev) return prev;
      const next = { ...prev, ...partial };
      try {
        if (typeof window !== "undefined") {
          localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(next));
        }
      } catch {
        // Storage error ignored
      }
      return next;
    });
  }, []);

  const clearSessionExpired = useCallback(() => {
    setSessionExpired(false);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isLoggedIn: !!token && !!user,
      isLoading,
      isDemo: isDemoSession(token),
      sessionExpired,
      clearSessionExpired,
      login,
      logout,
      updateUser,
    }),
    [user, token, isLoading, sessionExpired, clearSessionExpired, login, logout, updateUser]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
