package com.hospital.management.demo.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DashboardSummaryResponse {
    long totalUsers;
    long totalPatients;
    long totalDoctors;
    long totalHospitals;
    long totalDepartments;
    long totalAppointments;
    long pendingAppointments;
    long confirmedAppointments;
    long completedAppointments;
}

