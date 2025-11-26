import React, { useState, useEffect, useCallback } from "react";
import type { ReactNode } from "react";
import { authApi } from "../api/authApi";
import type { AuthResponse, UserInfo, LoginRequest, RegisterRequest } from "../types/auth";
import { AuthContext, type AuthContextType } from "./AuthContextTypes";
import { storage } from "../utils/storage";

export const AuthProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const updateAuthState = useCallback((newToken: string, newUser: UserInfo) => {
    setToken(newToken);
    setUser(newUser);
    storage.setToken(newToken);
    storage.setUser(newUser);
  }, []);

  const clearAuthState = useCallback(() => {
    setToken(null);
    setUser(null);
    storage.clear();
  }, []);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const storedToken = storage.getToken();
        const storedUser = storage.getUser<UserInfo>();

        if (storedToken && storedUser) {
          try {
            const userInfo = await authApi.getCurrentUser();
            setToken(storedToken);
            setUser(userInfo);
            storage.setUser(userInfo);
          } catch {
            clearAuthState();
          }
        }
      } catch {
        clearAuthState();
      } finally {
        setLoading(false);
      }
    };

    initializeAuth();
  }, [clearAuthState]);

  const login = useCallback(
    async (data: LoginRequest) => {
      try {
        setError(null);
        setLoading(true);
        const response: AuthResponse = await authApi.login(data);
        const userInfo: UserInfo = {
          userId: response.userId,
          email: response.email,
          role: response.role,
        };
        updateAuthState(response.token, userInfo);
      } catch (err: unknown) {
        const errorMessage =
          err && typeof err === "object" && "response" in err
            ? (err as { response?: { data?: { message?: string } } }).response
              ?.data?.message
            : undefined;
        setError(errorMessage || "Login failed. Please check your credentials.");
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [updateAuthState]
  );

  const register = useCallback(
    async (data: RegisterRequest) => {
      try {
        setError(null);
        setLoading(true);
        const response: AuthResponse = await authApi.register(data);
        const userInfo: UserInfo = {
          userId: response.userId,
          email: response.email,
          role: response.role,
        };
        updateAuthState(response.token, userInfo);
      } catch (err: unknown) {
        const errorMessage =
          err && typeof err === "object" && "response" in err
            ? (err as { response?: { data?: { message?: string } } }).response
              ?.data?.message
            : undefined;
        setError(
          errorMessage || "Registration failed. Please try again."
        );
        throw err;
      } finally {
        setLoading(false);
      }
    },
    [updateAuthState]
  );

  const logout = useCallback(() => {
    clearAuthState();
    setError(null);
  }, [clearAuthState]);

  const value: AuthContextType = {
    user,
    token,
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!token && !!user,
    error,
    clearError,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
