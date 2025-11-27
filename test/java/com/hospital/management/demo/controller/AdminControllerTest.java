package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.DepartmentRequest;
import com.hospital.management.demo.dto.DepartmentResponse;
import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.dto.HospitalResponse;
import com.hospital.management.demo.dto.RegisterRequest;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.service.DepartmentService;
import com.hospital.management.demo.service.HospitalService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.hospital\\.management\\.demo\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private HospitalService hospitalService;

        @MockBean
        private DepartmentService departmentService;

        @Autowired
        private ObjectMapper objectMapper;

        private RegisterRequest registerRequest;
        private User user;
        private HospitalRequest hospitalRequest;
        private HospitalResponse hospitalResponse;
        private DepartmentRequest departmentRequest;
        private DepartmentResponse departmentResponse;

        @BeforeEach
        void setUp() {
                registerRequest = new RegisterRequest();
                registerRequest.setEmail("doctor@example.com");
                registerRequest.setPassword("password");
                registerRequest.setRole(UserRole.DOCTOR);
                registerRequest.setFirstName("John");
                registerRequest.setLastName("Doe");

                user = User.builder()
                                .id(1L)
                                .email("doctor@example.com")
                                .role(UserRole.DOCTOR)
                                .build();

                hospitalRequest = new HospitalRequest();
                hospitalRequest.setName("Test Hospital");
                hospitalRequest.setAddress("123 Test St");
                hospitalRequest.setLatitude(22.3193);
                hospitalRequest.setLongitude(114.1694);

                hospitalResponse = HospitalResponse.builder()
                                .id(1L)
                                .name("Test Hospital")
                                .address("123 Test St")
                                .latitude(22.3193)
                                .longitude(114.1694)
                                .build();

                departmentRequest = new DepartmentRequest();
                departmentRequest.setName("Cardiology");
                departmentRequest.setCode("CARD");

                departmentResponse = DepartmentResponse.builder()
                                .id(1L)
                                .name("Cardiology")
                                .code("CARD")
                                .build();
        }

        @Test
        void testCreateUser_Success() throws Exception {
                when(userService.registerUser(any(RegisterRequest.class))).thenReturn(user);

                mockMvc.perform(post("/api/admin/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(1L))
                                .andExpect(jsonPath("$.email").value("doctor@example.com"))
                                .andExpect(jsonPath("$.role").value("DOCTOR"));
        }

        @Test
        void testGetAllUsers_Success() throws Exception {
                when(userService.getAllUsers()).thenReturn(List.of(user));

                mockMvc.perform(get("/api/admin/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].userId").value(1L));
        }

        @Test
        void testCreateHospital_Success() throws Exception {
                when(hospitalService.createHospital(any(HospitalRequest.class))).thenReturn(hospitalResponse);

                mockMvc.perform(post("/api/admin/hospitals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(hospitalRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.name").value("Test Hospital"));
        }

        @Test
        void testUpdateHospital_Success() throws Exception {
                when(hospitalService.updateHospital(eq(1L), any(HospitalRequest.class))).thenReturn(hospitalResponse);

                mockMvc.perform(put("/api/admin/hospitals/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(hospitalRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L));
        }

        @Test
        void testCreateDepartment_Success() throws Exception {
                when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(departmentResponse);

                mockMvc.perform(post("/api/admin/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(departmentRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.name").value("Cardiology"));
        }
}
