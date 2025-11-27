package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Patient;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Patient testPatient;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("patient@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        testPatient = Patient.builder()
                .user(testUser)
                .firstName("Test")
                .lastName("Patient")
                .build();
        patientRepository.save(testPatient);
    }

    @Test
    void testFindByUserEmail_Exists() {
        // Branch: Patient exists with user email
        Optional<Patient> found = patientRepository.findByUserEmail("patient@example.com");

        assertTrue(found.isPresent());
        assertEquals("patient@example.com", found.get().getUser().getEmail());
    }

    @Test
    void testFindByUserEmail_NotExists() {
        // Branch: Patient does not exist with user email
        Optional<Patient> found = patientRepository.findByUserEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserId_Exists() {
        // Branch: Patient exists with user id
        Optional<Patient> found = patientRepository.findByUserId(testUser.getId());

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    void testFindByUserId_NotExists() {
        // Branch: Patient does not exist with user id
        Optional<Patient> found = patientRepository.findByUserId(999L);

        assertFalse(found.isPresent());
    }
}

