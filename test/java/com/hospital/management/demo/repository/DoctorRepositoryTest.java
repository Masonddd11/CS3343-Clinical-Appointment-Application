package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Doctor;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.User;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private User testUser;
    private Hospital testHospital;
    private Department testDepartment;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("doctor@example.com")
                .password("password")
                .role(UserRole.DOCTOR)
                .isActive(true)
                .build();
        testUser = userRepository.save(testUser);

        testHospital = Hospital.builder()
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        testHospital = hospitalRepository.save(testHospital);

        testDepartment = Department.builder()
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testDoctor = Doctor.builder()
                .user(testUser)
                .hospital(testHospital)
                .department(testDepartment)
                .firstName("Test")
                .lastName("Doctor")
                .isAvailable(true)
                .build();
        doctorRepository.save(testDoctor);
    }

    @Test
    void testFindByUserEmail_Exists() {
        // Branch: Doctor exists with user email
        Optional<Doctor> found = doctorRepository.findByUserEmail("doctor@example.com");

        assertTrue(found.isPresent());
        assertEquals("doctor@example.com", found.get().getUser().getEmail());
    }

    @Test
    void testFindByUserEmail_NotExists() {
        // Branch: Doctor does not exist with user email
        Optional<Doctor> found = doctorRepository.findByUserEmail("nonexistent@example.com");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByUserId_Exists() {
        // Branch: Doctor exists with user id
        Optional<Doctor> found = doctorRepository.findByUserId(testUser.getId());

        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    void testFindByUserId_NotExists() {
        // Branch: Doctor does not exist with user id
        Optional<Doctor> found = doctorRepository.findByUserId(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByHospitalId() {
        // Cover findByHospitalId method
        List<Doctor> doctors = doctorRepository.findByHospitalId(testHospital.getId());

        assertFalse(doctors.isEmpty());
    }

    @Test
    void testFindByDepartmentId() {
        // Cover findByDepartmentId method
        List<Doctor> doctors = doctorRepository.findByDepartmentId(testDepartment.getId());

        assertFalse(doctors.isEmpty());
    }

    @Test
    void testFindByHospitalIdAndDepartmentId() {
        // Cover findByHospitalIdAndDepartmentId method
        List<Doctor> doctors = doctorRepository.findByHospitalIdAndDepartmentId(
                testHospital.getId(), testDepartment.getId());

        assertFalse(doctors.isEmpty());
    }

    @Test
    void testFindByIsAvailableTrue() {
        // Cover findByIsAvailableTrue method
        List<Doctor> doctors = doctorRepository.findByIsAvailableTrue();

        assertFalse(doctors.isEmpty());
    }
}

