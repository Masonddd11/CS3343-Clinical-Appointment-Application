export interface DepartmentResponse {
  id: number;
  name: string;
  code: string;
  description?: string;
}

export interface DepartmentRequest {
  name: string;
  code: string;
  description?: string;
}
