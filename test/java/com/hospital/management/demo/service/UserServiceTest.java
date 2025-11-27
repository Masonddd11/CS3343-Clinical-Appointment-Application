package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.LoginRequest;
import com.hospital.management.demo.dto.RegisterRequest;
import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.PatientRepository;
import com.hospital.management.demo.repository.UserRepository;
import com.hospital.management.demo.security.CustomUserDetailsService;
import com.hospital.management.demo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private PatientRepository patientRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        patientRepository = mock(PatientRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        customUserDetailsService = mock(CustomUserDetailsService.class);
        
        userService = new UserService(
                userRepository,
                patientRepository,
                passwordEncoder,
                authenticationManager,
                jwtUtil,
                customUserDetailsService
        );
    }

    @Test
    void testRegister_NonPatientRole() {
        // Branch: request.getRole() != UserRole.PATIENT
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@example.com");
        request.setPassword("password");
        request.setRole(UserRole.ADMIN);

        assertThrows(RuntimeException.class, () -> {
            userService.register(request);
        });
    }

    @Test
    void testRegister_PatientRole() {
        // Branch: request.getRole() == UserRole.PATIENT
        RegisterRequest request = new RegisterRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password");
        request.setRole(UserRole.PATIENT);
        request.setFirstName("Test");
        request.setLastName("Patient");

        when(userRepository.existsByEmail("patient@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(mock(Patient.class));

        var response = userService.register(request);

        assertNotNull(response);
        assertEquals("patient@example.com", response.getEmail());
        assertEquals(UserRole.PATIENT, response.getRole());
    }

    @Test
    void testRegisterUser_EmailExists() {
        // Branch: userRepository.existsByEmail returns true
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password");
        request.setRole(UserRole.PATIENT);

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });
    }

    @Test
    void testRegisterUser_EmailNotExists() {
        // Branch: userRepository.existsByEmail returns false
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(UserRole.PATIENT);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .email("new@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(mock(Patient.class));

        User result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }

    @Test
    void testRegisterUser_PatientRole() {
        // Branch: request.getRole() == UserRole.PATIENT - 行66
        RegisterRequest request = new RegisterRequest();
        request.setEmail("patient@example.com");
        request.setPassword("password");
        request.setRole(UserRole.PATIENT);
        request.setFirstName("Test");
        request.setLastName("Patient");

        when(userRepository.existsByEmail("patient@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .email("patient@example.com")
                .password("encodedPassword")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(patientRepository.save(any(Patient.class))).thenReturn(mock(Patient.class));

        User result = userService.registerUser(request);

        verify(patientRepository).save(any(Patient.class));
        assertNotNull(result);
    }

    @Test
    void testRegisterUser_NonPatientRole() {
        // Branch: request.getRole() != UserRole.PATIENT - 行66
        RegisterRequest request = new RegisterRequest();
        request.setEmail("doctor@example.com");
        request.setPassword("password");
        request.setRole(UserRole.DOCTOR);

        when(userRepository.existsByEmail("doctor@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        
        User savedUser = User.builder()
                .id(1L)
                .email("doctor@example.com")
                .password("encodedPassword")
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(request);

        verify(patientRepository, never()).save(any(Patient.class));
        assertNotNull(result);
        assertEquals(UserRole.DOCTOR, result.getRole());
    }

    @Test
    void testLogin() {
        // Cover login method
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .role(UserRole.PATIENT)
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails, "PATIENT")).thenReturn("token");

        var response = userService.login(request);

        assertNotNull(response);
        assertEquals("user@example.com", response.getEmail());
        assertEquals("token", response.getToken());
    }

    @Test
    void testLogin_UserNotFound() {
        // Branch: userRepository.findByEmail returns empty - 行92
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(request);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetCurrentUser_NotAuthenticated() {
        // Branch: authentication == null - 行108
        SecurityContextHolder.clearContext();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void testGetCurrentUser_NotAuthenticatedButNotNull() {
        // Branch: authentication != null && !authentication.isAuthenticated() - 行108
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void testGetCurrentUser_Authenticated() {
        // Branch: authentication != null && isAuthenticated - 行108
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .role(UserRole.PATIENT)
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void testGetCurrentUser_UserNotFound() {
        // Branch: userRepository.findByEmail returns empty - 行112
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getCurrentUser();
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        // Cover getAllUsers method
        when(userRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var users = userService.getAllUsers();

        assertNotNull(users);
    }
}

