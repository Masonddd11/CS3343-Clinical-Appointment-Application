package com.hospital.management.demo.controller;

import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PatientController.class,
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
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .role(UserRole.PATIENT)
                .build();
    }

    @Test
    void testGetProfile_PatientRole() throws Exception {
        when(userService.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(get("/api/patients/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("patient@example.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    @Test
    void testGetProfile_AdminRole() throws Exception {
        User adminUser = User.builder()
                .id(2L)
                .email("admin@example.com")
                .role(UserRole.ADMIN)
                .build();
        when(userService.getCurrentUser()).thenReturn(adminUser);

        mockMvc.perform(get("/api/patients/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void testGetProfile_AccessDenied_DoctorRole() throws Exception {
        User doctorUser = User.builder()
                .id(3L)
                .email("doctor@example.com")
                .role(UserRole.DOCTOR)
                .build();
        when(userService.getCurrentUser()).thenReturn(doctorUser);

        mockMvc.perform(get("/api/patients/profile"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }
}

