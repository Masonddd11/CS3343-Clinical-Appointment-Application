package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.AuthResponse;
import com.hospital.management.demo.dto.LoginRequest;
import com.hospital.management.demo.dto.RegisterRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("patient@example.com");
        registerRequest.setPassword("password");
        registerRequest.setRole(UserRole.PATIENT);
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("Patient");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("patient@example.com");
        loginRequest.setPassword("password");

        authResponse = AuthResponse.builder()
                .email("patient@example.com")
                .role(UserRole.PATIENT)
                .userId(1L)
                .token("test-token")
                .message("Success")
                .build();

        testUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .role(UserRole.PATIENT)
                .build();
    }

    @Test
    void testRegister_Success() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("patient@example.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void testLogin_Success() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("patient@example.com"))
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void testGetCurrentUser_Success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(testUser);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("patient@example.com"))
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }
}

