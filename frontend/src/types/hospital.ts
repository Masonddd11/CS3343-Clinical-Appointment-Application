export type OperationalStatus = "OPERATIONAL" | "CLOSED" | "LIMITED";

export interface HospitalResponse {
  id: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  district: string;
  capacity: number;
  currentIntensity: number;
  operationalStatus: OperationalStatus;
  closureReason?: string;
  hasAccidentAndEmergency: boolean;
}

export interface HospitalRequest {
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  district: string;
  capacity: number;
  currentIntensity: number;
  operationalStatus: OperationalStatus;
  closureReason?: string;
  hasAccidentAndEmergency?: boolean;
}
