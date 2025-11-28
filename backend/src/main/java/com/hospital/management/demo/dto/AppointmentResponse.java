package com.hospital.management.demo.dto;

import com.hospital.management.demo.model.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private Long hospitalId;
    private String hospitalName;
    private Long departmentId;
    private String departmentName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reasonForVisit;
    private String symptoms;
    private Double patientLatitude;
    private Double patientLongitude;
    private AppointmentStatus status;
    private Double pathfindingScore;
    private String notes;
}
