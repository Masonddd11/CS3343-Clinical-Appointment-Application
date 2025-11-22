package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.AppointmentRequest;
import com.hospital.management.demo.dto.AppointmentResponse;
import com.hospital.management.demo.dto.RescheduleAppointmentRequest;
import com.hospital.management.demo.model.entity.Appointment;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.enums.AppointmentStatus;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.repository.AppointmentRepository;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import com.hospital.management.demo.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (hospital.getOperationalStatus() == OperationalStatus.CLOSED_EPIDEMIC ||
            hospital.getOperationalStatus() == OperationalStatus.CLOSED_OTHER) {
            throw new RuntimeException("Hospital is currently closed");
        }

        if (!doctor.getHospital().getId().equals(hospital.getId())) {
            throw new RuntimeException("Doctor does not work at the specified hospital");
        }

        if (!doctor.getDepartment().getId().equals(department.getId())) {
            throw new RuntimeException("Doctor does not belong to the specified department");
        }

        if (!doctor.getIsAvailable()) {
            throw new RuntimeException("Doctor is not available");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(request.getAppointmentDate(), request.getAppointmentTime());
        LocalDateTime now = LocalDateTime.now();

        if (appointmentDateTime.isBefore(now.plusHours(24))) {
            throw new RuntimeException("Appointments must be booked at least 24 hours in advance");
        }

        if (appointmentDateTime.isAfter(now.plusMonths(3))) {
            throw new RuntimeException("Appointments can only be booked up to 3 months in advance");
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                request.getDoctorId(), request.getAppointmentDate(), request.getAppointmentTime(), AppointmentStatus.CANCELLED)) {
            throw new RuntimeException("Time slot is already booked");
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .hospital(hospital)
                .department(department)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reasonForVisit(request.getReasonForVisit())
                .symptoms(request.getSymptoms())
                .status(AppointmentStatus.CONFIRMED)
                .build();

        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse bookAppointmentWithPathfindingScore(Long patientId, AppointmentRequest request, Double pathfindingScore) {
        AppointmentResponse response = bookAppointment(patientId, request);
        
        if (pathfindingScore != null) {
            Appointment appointment = appointmentRepository.findById(response.getId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
            appointment.setPathfindingScore(pathfindingScore);
            appointmentRepository.save(appointment);
            return mapToResponse(appointment);
        }
        
        return response;
    }

    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return mapToResponse(appointment);
    }

    @Transactional
    public AppointmentResponse rescheduleAppointment(Long appointmentId, Long patientId, RescheduleAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot reschedule cancelled or completed appointment");
        }

        LocalDateTime newAppointmentDateTime = LocalDateTime.of(request.getAppointmentDate(), request.getAppointmentTime());
        LocalDateTime oldAppointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        LocalDateTime now = LocalDateTime.now();

        if (oldAppointmentDateTime.isBefore(now.plusHours(24))) {
            throw new RuntimeException("Appointments can only be rescheduled at least 24 hours before the appointment time");
        }

        if (newAppointmentDateTime.isBefore(now.plusHours(24))) {
            throw new RuntimeException("New appointment time must be at least 24 hours in advance");
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
                appointment.getDoctor().getId(), request.getAppointmentDate(), request.getAppointmentTime(), AppointmentStatus.CANCELLED)) {
            throw new RuntimeException("New time slot is already booked");
        }

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment = appointmentRepository.save(appointment);

        return mapToResponse(appointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("Access denied");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed appointment");
        }

        LocalDateTime appointmentDateTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getAppointmentTime());
        LocalDateTime now = LocalDateTime.now();

        if (appointmentDateTime.isBefore(now.plusHours(24))) {
            throw new RuntimeException("Appointments can only be cancelled at least 24 hours before the appointment time");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public AppointmentResponse markAppointmentCompleted(Long appointmentId, Long doctorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Access denied");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName())
                .hospitalId(appointment.getHospital().getId())
                .hospitalName(appointment.getHospital().getName())
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reasonForVisit(appointment.getReasonForVisit())
                .symptoms(appointment.getSymptoms())
                .status(appointment.getStatus())
                .pathfindingScore(appointment.getPathfindingScore())
                .notes(appointment.getNotes())
                .build();
    }
}

