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
public class HospitalResponse {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String district;
    private Integer capacity;
    private Double currentIntensity;
    private OperationalStatus operationalStatus;
    private String closureReason;
    private Boolean hasAccidentAndEmergency;
}
