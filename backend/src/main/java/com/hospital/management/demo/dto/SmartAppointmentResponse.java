package com.hospital.management.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmartAppointmentResponse {
    private AppointmentResponse appointment;
    private SymptomAnalysisResponse departmentRecommendation;
    private List<HospitalRecommendationResponse> hospitalRecommendations;
    private String message;
}


