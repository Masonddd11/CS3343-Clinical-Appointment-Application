import { createContext } from "react";
import type { UserInfo, LoginRequest, RegisterRequest } from "../types/auth";

export interface AuthContextType {
  user: UserInfo | null;
  token: string | null;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  loading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  clearError: () => void;
}

export const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);
