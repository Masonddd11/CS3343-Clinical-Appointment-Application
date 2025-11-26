import apiClient from "./apiClient";
import type { RegisterRequest } from "../types/auth";

export interface UserInfo {
  userId: number;
  email: string;
  role: "PATIENT" | "DOCTOR" | "ADMIN";
}

export const adminApi = {
  createUser: async (data: RegisterRequest): Promise<UserInfo> => {
    const response = await apiClient.post<UserInfo>("/admin/users", data);
    return response.data;
  },

  getAllUsers: async (): Promise<UserInfo[]> => {
    const response = await apiClient.get<UserInfo[]>("/admin/users");
    return response.data;
  },

  createHospital: async (data: any): Promise<any> => {
    const response = await apiClient.post("/admin/hospitals", data);
    return response.data;
  },

  updateHospital: async (id: number, data: any): Promise<any> => {
    const response = await apiClient.put(`/admin/hospitals/${id}`, data);
    return response.data;
  },

  createDepartment: async (data: any): Promise<any> => {
    const response = await apiClient.post("/admin/departments", data);
    return response.data;
  },
};
