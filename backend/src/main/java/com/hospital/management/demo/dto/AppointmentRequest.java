package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long patientId;

    @NotNull
    private Long doctorId;

    @NotNull
    private Long hospitalId;

    @NotNull
    private Long departmentId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;

    @NotBlank
    private String reasonForVisit;

    private String symptoms;

    // New: patient's current location for pathfinding (latitude, longitude)
    private Double patientLatitude;

    private Double patientLongitude;
}
