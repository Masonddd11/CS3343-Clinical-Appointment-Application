package com.hospital.management.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymptomAnalysisResponse {
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private String message;
}

