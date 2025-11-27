package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalRecommendationRequest {
    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Long departmentId;

    private Integer maxResults;
}

