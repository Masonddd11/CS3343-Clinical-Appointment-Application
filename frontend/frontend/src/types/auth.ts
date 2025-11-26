export const UserRole = {
  PATIENT: "PATIENT",
  DOCTOR: "DOCTOR",
  ADMIN: "ADMIN",
} as const;

export type UserRole = (typeof UserRole)[keyof typeof UserRole];

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  role: UserRole;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  district?: string;
}

export interface AuthResponse {
  email: string;
  role: UserRole;
  message: string;
  userId: number;
  token: string;
}

export interface UserInfo {
  userId: number;
  email: string;
  role: UserRole;
}
