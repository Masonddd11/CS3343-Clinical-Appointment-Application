package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HospitalDepartmentRequest {
    @NotNull
    private Long hospitalId;

    @NotNull
    private Long departmentId;
}

