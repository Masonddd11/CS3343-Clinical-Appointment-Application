package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class SmartAppointmentRequest {
    @NotBlank
    private String symptoms;
    
    private Long departmentId;
    
    private Long hospitalId;
    
    private Long doctorId;
    
    @NotNull
    private LocalDate appointmentDate;
    
    @NotNull
    private LocalTime appointmentTime;
    
    @NotBlank
    private String reasonForVisit;
    
    private Integer maxHospitalRecommendations;
}


