export interface DoctorResponse {
  id: number;
  userId: number;
  email: string;
  hospitalId: number;
  hospitalName: string;
  departmentId: number;
  departmentName: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  specialization: string;
  qualifications?: string;
  bio?: string;
  isAvailable: boolean;
}

export interface DoctorRequest {
  email: string;
  password: string;
  hospitalId: number;
  departmentId: number;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  specialization: string;
  qualifications?: string;
  bio?: string;
}
