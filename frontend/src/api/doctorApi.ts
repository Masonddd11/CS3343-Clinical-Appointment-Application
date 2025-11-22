import apiClient from "./apiClient";
import type { DoctorResponse, DoctorRequest } from "../types/doctor";

export const doctorApi = {
  getAllDoctors: async (
    name?: string,
    specialization?: string
  ): Promise<DoctorResponse[]> => {
    const params = new URLSearchParams();
    if (name) params.append("name", name);
    if (specialization) params.append("specialization", specialization);
    const queryString = params.toString();
    const url = queryString ? `/doctors?${queryString}` : "/doctors";
    const response = await apiClient.get<DoctorResponse[]>(url);
    return response.data;
  },

  getDoctorById: async (id: number): Promise<DoctorResponse> => {
    const response = await apiClient.get<DoctorResponse>(`/doctors/${id}`);
    return response.data;
  },

  getDoctorsByHospital: async (
    hospitalId: number
  ): Promise<DoctorResponse[]> => {
    const response = await apiClient.get<DoctorResponse[]>(
      `/doctors/hospital/${hospitalId}`
    );
    return response.data;
  },

  getDoctorsByDepartment: async (
    departmentId: number
  ): Promise<DoctorResponse[]> => {
    const response = await apiClient.get<DoctorResponse[]>(
      `/doctors/department/${departmentId}`
    );
    return response.data;
  },

  createDoctor: async (data: DoctorRequest): Promise<DoctorResponse> => {
    const response = await apiClient.post<DoctorResponse>("/doctors", data);
    return response.data;
  },
};
