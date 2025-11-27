package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .role(UserRole.PATIENT)
                .isActive(true)
                .build();
        userRepository.save(testUser);
    }

    @Test
    void testFindByEmail_Exists() {
        // Branch: User exists with email
        Optional<User> found = userRepository.findByEmail("test@example.com");

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotExists() {
        // Branch: User does not exist with email
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByEmail_True() {
        // Branch: Email exists
        boolean exists = userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail_False() {
        // Branch: Email does not exist
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        assertFalse(exists);
    }

    @Test
    void testCountByRole() {
        // Cover countByRole method
        User adminUser = User.builder()
                .email("admin@example.com")
                .password("password")
                .role(UserRole.ADMIN)
                .isActive(true)
                .build();
        userRepository.save(adminUser);

        long count = userRepository.countByRole(UserRole.ADMIN);

        assertEquals(1, count);
    }
}

