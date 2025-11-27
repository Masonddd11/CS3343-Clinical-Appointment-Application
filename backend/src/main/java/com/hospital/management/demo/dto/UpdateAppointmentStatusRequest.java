package com.hospital.management.demo.dto;

import com.hospital.management.demo.model.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusRequest {
    @NotNull
    private AppointmentStatus status;
}

