export type AppointmentStatus =
  | "PENDING"
  | "CONFIRMED"
  | "COMPLETED"
  | "CANCELLED";

export interface AppointmentResponse {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  hospitalId: number;
  hospitalName: string;
  departmentId: number;
  departmentName: string;
  appointmentDate: string;
  appointmentTime: string;
  reasonForVisit?: string;
  symptoms?: string;
  status: AppointmentStatus;
  pathfindingScore?: number;
  notes?: string;
  patientLatitude?: number;
  patientLongitude?: number;
}

export interface AppointmentRequest {
  doctorId: number;
  hospitalId: number;
  departmentId: number;
  appointmentDate: string;
  appointmentTime: string;
  reasonForVisit: string;
  symptoms?: string;
  patientLatitude?: number;
  patientLongitude?: number;
}

export interface RescheduleAppointmentRequest {
  appointmentDate: string;
  appointmentTime: string;
}
