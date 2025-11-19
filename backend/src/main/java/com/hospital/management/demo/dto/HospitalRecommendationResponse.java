package com.hospital.management.demo.dto;

import com.hospital.management.demo.model.enums.OperationalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalRecommendationResponse {
    private Long hospitalId;
    private String hospitalName;
    private String address;
    private String district;
    private Double distance;
    private Double intensity;
    private OperationalStatus operationalStatus;
    private Double score;
    private String recommendationReason;
}

