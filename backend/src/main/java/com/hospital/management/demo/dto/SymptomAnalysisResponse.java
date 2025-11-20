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
public class SymptomAnalysisResponse {
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Double confidenceScore;
    private List<String> matchedKeywords;
    private String message;
}

