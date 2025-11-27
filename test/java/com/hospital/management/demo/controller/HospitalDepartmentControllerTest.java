package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.HospitalDepartmentRequest;
import com.hospital.management.demo.dto.HospitalDepartmentResponse;
import com.hospital.management.demo.service.HospitalDepartmentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HospitalDepartmentController.class,
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
class HospitalDepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HospitalDepartmentService hospitalDepartmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private HospitalDepartmentRequest request;
    private HospitalDepartmentResponse response;

    @BeforeEach
    void setUp() {
        request = new HospitalDepartmentRequest();
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        response = HospitalDepartmentResponse.builder()
                .id(1L)
                .hospitalId(1L)
                .hospitalName("Test Hospital")
                .departmentId(1L)
                .departmentName("Cardiology")
                .build();
    }

    @Test
    void testAssignDepartmentToHospital_Success() throws Exception {
        when(hospitalDepartmentService.assignDepartmentToHospital(any(HospitalDepartmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/hospital-departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.hospitalId").value(1L))
                .andExpect(jsonPath("$.departmentId").value(1L));
    }

    @Test
    void testGetDepartmentsByHospital_Success() throws Exception {
        when(hospitalDepartmentService.getDepartmentsByHospital(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/hospital-departments/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hospitalId").value(1L));
    }

    @Test
    void testGetHospitalsByDepartment_Success() throws Exception {
        when(hospitalDepartmentService.getHospitalsByDepartment(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/hospital-departments/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].departmentId").value(1L));
    }
}

