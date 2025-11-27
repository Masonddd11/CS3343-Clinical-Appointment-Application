package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.AppointmentRequest;
import com.hospital.management.demo.dto.AppointmentResponse;
import com.hospital.management.demo.dto.RescheduleAppointmentRequest;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.AppointmentStatus;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.DoctorRepository;
import com.hospital.management.demo.repository.PatientRepository;
import com.hospital.management.demo.service.AppointmentService;
import com.hospital.management.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AppointmentController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.hospital\\.management\\.demo\\.security\\..*"
    )
)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private UserService userService;

    @MockBean
    private PatientRepository patientRepository;

    @MockBean
    private DoctorRepository doctorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User patientUser;
    private User adminUser;
    private User doctorUser;
    private Patient patient;
    private Doctor doctor;
    private AppointmentRequest appointmentRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        patientUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .role(UserRole.PATIENT)
                .build();

        adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();

        doctorUser = User.builder()
                .id(3L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();

        patient = Patient.builder()
                .id(1L)
                .user(patientUser)
                .firstName("Test")
                .lastName("Patient")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .firstName("Test")
                .lastName("Doctor")
                .build();

        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setDoctorId(1L);
        appointmentRequest.setHospitalId(1L);
        appointmentRequest.setDepartmentId(1L);
        appointmentRequest.setAppointmentDate(LocalDate.now().plusDays(2));
        appointmentRequest.setAppointmentTime(LocalTime.of(10, 0));
        appointmentRequest.setReasonForVisit("Checkup");

        appointmentResponse = AppointmentResponse.builder()
                .id(1L)
                .patientId(1L)
                .doctorId(1L)
                .status(AppointmentStatus.CONFIRMED)
                .build();
    }

    @Test
    void testBookAppointment_AsPatient() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(appointmentService.bookAppointment(eq(1L), any(AppointmentRequest.class)))
                .thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testBookAppointment_AsAdmin_WithPatientId() throws Exception {
        appointmentRequest.setPatientId(1L);
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentService.bookAppointment(eq(1L), any(AppointmentRequest.class)))
                .thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testBookAppointment_AsAdmin_WithoutPatientId() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient ID is required for admin booking"));
    }

    @Test
    void testGetPatientAppointments() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(appointmentService.getPatientAppointments(1L)).thenReturn(List.of(appointmentResponse));

        mockMvc.perform(get("/api/appointments/patient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetDoctorAppointments() throws Exception {
        when(userService.getCurrentUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUserId(3L)).thenReturn(Optional.of(doctor));
        when(appointmentService.getDoctorAppointments(1L)).thenReturn(List.of(appointmentResponse));

        mockMvc.perform(get("/api/appointments/doctor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetAppointmentById() throws Exception {
        when(appointmentService.getAppointmentById(1L)).thenReturn(appointmentResponse);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testRescheduleAppointment() throws Exception {
        RescheduleAppointmentRequest rescheduleRequest = new RescheduleAppointmentRequest();
        rescheduleRequest.setAppointmentDate(LocalDate.now().plusDays(5));
        rescheduleRequest.setAppointmentTime(LocalTime.of(14, 0));

        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));
        when(appointmentService.rescheduleAppointment(eq(1L), eq(1L), any(RescheduleAppointmentRequest.class)))
                .thenReturn(appointmentResponse);

        mockMvc.perform(put("/api/appointments/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelAppointment() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.of(patient));

        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testMarkAppointmentCompleted() throws Exception {
        when(userService.getCurrentUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUserId(3L)).thenReturn(Optional.of(doctor));
        when(appointmentService.markAppointmentCompleted(1L, 1L)).thenReturn(appointmentResponse);

        mockMvc.perform(put("/api/appointments/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testBookAppointment_AsPatient_PatientProfileNotFound() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient profile not found"));
    }

    @Test
    void testBookAppointment_AsAdmin_PatientNotFound() throws Exception {
        appointmentRequest.setPatientId(999L);
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient not found"));
    }

    @Test
    void testGetPatientAppointments_PatientProfileNotFound() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/patient"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient profile not found"));
    }

    @Test
    void testGetDoctorAppointments_DoctorProfileNotFound() throws Exception {
        when(userService.getCurrentUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUserId(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/appointments/doctor"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Doctor profile not found"));
    }

    @Test
    void testRescheduleAppointment_PatientProfileNotFound() throws Exception {
        RescheduleAppointmentRequest rescheduleRequest = new RescheduleAppointmentRequest();
        rescheduleRequest.setAppointmentDate(LocalDate.now().plusDays(5));
        rescheduleRequest.setAppointmentTime(LocalTime.of(14, 0));

        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/appointments/1/reschedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rescheduleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient profile not found"));
    }

    @Test
    void testCancelAppointment_PatientProfileNotFound() throws Exception {
        when(userService.getCurrentUser()).thenReturn(patientUser);
        when(patientRepository.findByUserId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Patient profile not found"));
    }

    @Test
    void testMarkAppointmentCompleted_DoctorProfileNotFound() throws Exception {
        when(userService.getCurrentUser()).thenReturn(doctorUser);
        when(doctorRepository.findByUserId(3L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/appointments/1/complete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Doctor profile not found"));
    }
}

