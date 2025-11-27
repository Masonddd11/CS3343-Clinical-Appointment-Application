package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    private String description;
}

