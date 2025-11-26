import apiClient from "./apiClient";

export interface HospitalRecommendationRequest {
  latitude: number;
  longitude: number;
  departmentId: number;
  maxResults?: number;
}

export interface HospitalRecommendationResponse {
  hospitalId: number;
  hospitalName: string;
  address: string;
  district: string;
  distance: number;
  intensity: number;
  operationalStatus: string;
  score: number;
  recommendationReason: string;
}

export const pathfindingApi = {
  recommendHospitals: async (
    data: HospitalRecommendationRequest
  ): Promise<HospitalRecommendationResponse[]> => {
    const response = await apiClient.post<HospitalRecommendationResponse[]>(
      "/pathfinding/recommend",
      data
    );
    return response.data;
  },
};
