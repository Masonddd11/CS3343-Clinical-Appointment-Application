package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.AppointmentRequest;
import com.hospital.management.demo.dto.AppointmentResponse;
import com.hospital.management.demo.dto.RescheduleAppointmentRequest;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.PatientRepository;
import com.hospital.management.demo.service.AppointmentService;
import com.hospital.management.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserService userService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        User currentUser = userService.getCurrentUser();
        Patient patient = null;
        
        if (currentUser.getRole() == UserRole.PATIENT) {
            patient = patientRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        } else if (currentUser.getRole() == UserRole.ADMIN) {
            if (request.getPatientId() == null) {
                throw new RuntimeException("Patient ID is required for admin booking");
            }
            patient = patientRepository.findById(request.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
        }
        
        return ResponseEntity.ok(appointmentService.bookAppointment(patient.getId(), request));
    }

    @GetMapping("/patient")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments() {
        User currentUser = userService.getCurrentUser();
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patient.getId()));
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments() {
        User currentUser = userService.getCurrentUser();
        com.hospital.management.demo.model.entity.Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctor.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable("id") Long id,
            @Valid @RequestBody RescheduleAppointmentRequest request) {
        User currentUser = userService.getCurrentUser();
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, patient.getId(), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<Void> cancelAppointment(@PathVariable("id") Long id) {
        User currentUser = userService.getCurrentUser();
        Patient patient = patientRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        appointmentService.cancelAppointment(id, patient.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> markAppointmentCompleted(@PathVariable("id") Long id) {
        User currentUser = userService.getCurrentUser();
        com.hospital.management.demo.model.entity.Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return ResponseEntity.ok(appointmentService.markAppointmentCompleted(id, doctor.getId()));
    }
}

