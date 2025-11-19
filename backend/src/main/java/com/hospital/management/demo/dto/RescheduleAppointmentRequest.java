package com.hospital.management.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RescheduleAppointmentRequest {
    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime appointmentTime;
}

