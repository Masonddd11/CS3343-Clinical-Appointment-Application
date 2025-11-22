import apiClient from "./apiClient";
import type { HospitalResponse, HospitalRequest } from "../types/hospital";

export const hospitalApi = {
  getAllHospitals: async (): Promise<HospitalResponse[]> => {
    const response = await apiClient.get<HospitalResponse[]>("/hospitals");
    return response.data;
  },

  getHospitalById: async (id: number): Promise<HospitalResponse> => {
    const response = await apiClient.get<HospitalResponse>(`/hospitals/${id}`);
    return response.data;
  },

  createHospital: async (data: HospitalRequest): Promise<HospitalResponse> => {
    const response = await apiClient.post<HospitalResponse>(
      "/admin/hospitals",
      data
    );
    return response.data;
  },

  updateHospital: async (
    id: number,
    data: HospitalRequest
  ): Promise<HospitalResponse> => {
    const response = await apiClient.put<HospitalResponse>(
      `/admin/hospitals/${id}`,
      data
    );
    return response.data;
  },
};
