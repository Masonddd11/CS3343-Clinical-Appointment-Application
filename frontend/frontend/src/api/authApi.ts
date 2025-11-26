import apiClient from "./apiClient";
import type {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
  UserInfo,
} from "../types/auth";

export const authApi = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>("/auth/login", data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>("/auth/register", data);
    return response.data;
  },

  getCurrentUser: async (): Promise<UserInfo> => {
    const response = await apiClient.get<UserInfo>("/auth/me");
    return response.data;
  },
};
