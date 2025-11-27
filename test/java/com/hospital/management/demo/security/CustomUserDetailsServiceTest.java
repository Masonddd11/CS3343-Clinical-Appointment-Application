package com.hospital.management.demo.security;

import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import com.hospital.management.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Branch: userRepository.findByEmail returns empty Optional
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
    }

    @Test
    void testLoadUserByUsername_UserInactive() {
        // Branch: user.getIsActive() == false
        User inactiveUser = User.builder()
                .id(1L)
                .email("inactive@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .isActive(false)
                .build();

        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(inactiveUser));

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("inactive@example.com");
        });
    }

    @Test
    void testLoadUserByUsername_ActiveUser() {
        // Branch: user.getIsActive() == true
        User activeUser = User.builder()
                .id(1L)
                .email("active@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();

        when(userRepository.findByEmail("active@example.com")).thenReturn(Optional.of(activeUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("active@example.com");

        assertNotNull(userDetails);
        assertEquals("active@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
    }
}

