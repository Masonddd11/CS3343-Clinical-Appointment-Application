import apiClient from "./apiClient";

export interface Symptom {
  id: number;
  symptom: string;
  departmentId: number;
  priority: number;
  keywords: string[];
}

export interface SymptomAnalysisRequest {
  symptom: string;
}

export interface SymptomAnalysisResponse {
  departmentId?: number;
  departmentName?: string;
  departmentCode?: string;
  confidenceScore: number;
  matchedKeywords: string[];
  message: string;
}

export const symptomApi = {
  analyzeSymptom: async (data: SymptomAnalysisRequest): Promise<SymptomAnalysisResponse> => {
    const response = await apiClient.post<SymptomAnalysisResponse>(
      "/symptoms/analyze",
      data
    );
    return response.data;
  },

  getAllSymptoms: async (): Promise<Symptom[]> => {
    const response = await apiClient.get<Symptom[]>("/symptoms");
    return response.data;
  },

  getSymptomsByDepartment: async (departmentId: number): Promise<Symptom[]> => {
    const response = await apiClient.get<Symptom[]>(`/symptoms/department/${departmentId}`);
    return response.data;
  },

  createSymptom: async (
    symptom: string,
    departmentId: number,
    priority: number = 1,
    keywords?: string[]
  ): Promise<Symptom> => {
    const params = new URLSearchParams();
    params.append("symptom", symptom);
    params.append("departmentId", departmentId.toString());
    params.append("priority", priority.toString());
    if (keywords && keywords.length > 0) {
      keywords.forEach((kw) => params.append("keywords", kw));
    }

    const response = await apiClient.post<Symptom>(`/symptoms?${params.toString()}`);
    return response.data;
  },
};
