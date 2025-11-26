import apiClient from "./apiClient";
import type {
  AppointmentResponse,
  AppointmentRequest,
  RescheduleAppointmentRequest,
} from "../types/appointment";

export const appointmentApi = {
  bookAppointment: async (
    data: AppointmentRequest
  ): Promise<AppointmentResponse> => {
    const response = await apiClient.post<AppointmentResponse>(
      "/appointments",
      data
    );
    return response.data;
  },

  getPatientAppointments: async (): Promise<AppointmentResponse[]> => {
    const response = await apiClient.get<AppointmentResponse[]>(
      "/appointments/patient"
    );
    return response.data;
  },

  getDoctorAppointments: async (): Promise<AppointmentResponse[]> => {
    const response = await apiClient.get<AppointmentResponse[]>(
      "/appointments/doctor"
    );
    return response.data;
  },

  getAppointmentById: async (id: number): Promise<AppointmentResponse> => {
    const response = await apiClient.get<AppointmentResponse>(
      `/appointments/${id}`
    );
    return response.data;
  },

  rescheduleAppointment: async (
    id: number,
    data: RescheduleAppointmentRequest
  ): Promise<AppointmentResponse> => {
    const response = await apiClient.put<AppointmentResponse>(
      `/appointments/${id}/reschedule`,
      data
    );
    return response.data;
  },

  cancelAppointment: async (id: number): Promise<void> => {
    await apiClient.delete(`/appointments/${id}`);
  },

  markAppointmentCompleted: async (
    id: number
  ): Promise<AppointmentResponse> => {
    const response = await apiClient.put<AppointmentResponse>(
      `/appointments/${id}/complete`,
      {}
    );
    return response.data;
  },
};
