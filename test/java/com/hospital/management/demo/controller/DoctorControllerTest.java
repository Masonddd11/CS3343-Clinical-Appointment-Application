package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.DoctorRequest;
import com.hospital.management.demo.dto.DoctorResponse;
import com.hospital.management.demo.service.DoctorService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = DoctorController.class,
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
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    private DoctorRequest doctorRequest;
    private DoctorResponse doctorResponse;

    @BeforeEach
    void setUp() {
        doctorRequest = new DoctorRequest();
        doctorRequest.setEmail("doctor@example.com");
        doctorRequest.setPassword("password");
        doctorRequest.setHospitalId(1L);
        doctorRequest.setDepartmentId(1L);
        doctorRequest.setFirstName("Test");
        doctorRequest.setLastName("Doctor");

        doctorResponse = DoctorResponse.builder()
                .id(1L)
                .email("doctor@example.com")
                .firstName("Test")
                .lastName("Doctor")
                .build();
    }

    @Test
    void testCreateDoctor_Success() throws Exception {
        when(doctorService.createDoctor(any(DoctorRequest.class))).thenReturn(doctorResponse);

        mockMvc.perform(post("/api/doctors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(doctorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("doctor@example.com"));
    }

    @Test
    void testGetAllDoctors_Success() throws Exception {
        when(doctorService.getAllDoctors()).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("doctor@example.com"));
    }

    @Test
    void testGetAllDoctors_WithSearchParams() throws Exception {
        when(doctorService.searchDoctors(eq("Test"), eq("Cardiology"))).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors")
                        .param("name", "Test")
                        .param("specialization", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("doctor@example.com"));
    }

    @Test
    void testGetAllDoctors_WithNameOnly() throws Exception {
        when(doctorService.searchDoctors(eq("Test"), isNull())).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors")
                        .param("name", "Test"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllDoctors_WithSpecializationOnly() throws Exception {
        when(doctorService.searchDoctors(isNull(), eq("Cardiology"))).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors")
                        .param("specialization", "Cardiology"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDoctorById_Success() throws Exception {
        when(doctorService.getDoctorById(1L)).thenReturn(doctorResponse);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testGetDoctorsByHospital_Success() throws Exception {
        when(doctorService.getDoctorsByHospital(1L)).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetDoctorsByDepartment_Success() throws Exception {
        when(doctorService.getDoctorsByDepartment(1L)).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetDoctorsByHospitalAndDepartment_Success() throws Exception {
        when(doctorService.getDoctorsByHospitalAndDepartment(1L, 1L)).thenReturn(List.of(doctorResponse));

        mockMvc.perform(get("/api/doctors/hospital/1/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}

