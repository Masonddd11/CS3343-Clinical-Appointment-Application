package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.HospitalDepartment;
import com.hospital.management.demo.model.enums.OperationalStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HospitalDepartmentRepositoryTest {

    @Autowired
    private HospitalDepartmentRepository hospitalDepartmentRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Hospital testHospital;
    private Department testDepartment;
    private HospitalDepartment testHospitalDepartment;

    @BeforeEach
    void setUp() {
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

        testHospitalDepartment = HospitalDepartment.builder()
                .hospital(testHospital)
                .department(testDepartment)
                .isActive(true)
                .build();
        hospitalDepartmentRepository.save(testHospitalDepartment);
    }

    @Test
    void testFindByHospitalId() {
        // Cover findByHospitalId method
        List<HospitalDepartment> hospitalDepartments = hospitalDepartmentRepository.findByHospitalId(testHospital.getId());

        assertFalse(hospitalDepartments.isEmpty());
    }

    @Test
    void testFindByDepartmentId() {
        // Cover findByDepartmentId method
        List<HospitalDepartment> hospitalDepartments = hospitalDepartmentRepository.findByDepartmentId(testDepartment.getId());

        assertFalse(hospitalDepartments.isEmpty());
    }

    @Test
    void testFindByHospitalIdAndDepartmentId_Exists() {
        // Branch: HospitalDepartment exists
        Optional<HospitalDepartment> found = hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(
                testHospital.getId(), testDepartment.getId());

        assertTrue(found.isPresent());
    }

    @Test
    void testFindByHospitalIdAndDepartmentId_NotExists() {
        // Branch: HospitalDepartment does not exist
        Optional<HospitalDepartment> found = hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(999L, 999L);

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByHospitalIdAndIsActiveTrue() {
        // Cover findByHospitalIdAndIsActiveTrue method
        List<HospitalDepartment> hospitalDepartments = hospitalDepartmentRepository.findByHospitalIdAndIsActiveTrue(testHospital.getId());

        assertFalse(hospitalDepartments.isEmpty());
    }
}

