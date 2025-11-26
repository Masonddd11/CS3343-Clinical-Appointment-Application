import apiClient from "./apiClient";
import type {
  DepartmentResponse,
  DepartmentRequest,
} from "../types/department";

export const departmentApi = {
  getAllDepartments: async (): Promise<DepartmentResponse[]> => {
    const response = await apiClient.get<DepartmentResponse[]>("/departments");
    return response.data;
  },

  getDepartmentById: async (id: number): Promise<DepartmentResponse> => {
    const response = await apiClient.get<DepartmentResponse>(
      `/departments/${id}`
    );
    return response.data;
  },

  createDepartment: async (
    data: DepartmentRequest
  ): Promise<DepartmentResponse> => {
    const response = await apiClient.post<DepartmentResponse>(
      "/admin/departments",
      data
    );
    return response.data;
  },
};
