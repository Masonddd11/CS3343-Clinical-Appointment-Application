package com.hospital.management.demo.dto;

import com.hospital.management.demo.model.enums.OperationalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalRequest {
    @NotBlank
    private String name;

    private String address;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String district;
    private Integer capacity;
    private Double currentIntensity;
    private OperationalStatus operationalStatus;
    private String closureReason;
}

