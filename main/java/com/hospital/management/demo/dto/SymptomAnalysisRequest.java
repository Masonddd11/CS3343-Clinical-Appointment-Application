package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SymptomAnalysisRequest {
    @NotBlank
    private String symptom;
}

