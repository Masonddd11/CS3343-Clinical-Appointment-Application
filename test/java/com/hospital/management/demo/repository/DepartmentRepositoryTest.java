package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        departmentRepository.save(testDepartment);
    }

    @Test
    void testFindByName_Exists() {
        // Branch: Department exists with name
        Optional<Department> found = departmentRepository.findByName("Cardiology");

        assertTrue(found.isPresent());
        assertEquals("Cardiology", found.get().getName());
    }

    @Test
    void testFindByName_NotExists() {
        // Branch: Department does not exist with name
        Optional<Department> found = departmentRepository.findByName("Nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindByCode_Exists() {
        // Branch: Department exists with code
        Optional<Department> found = departmentRepository.findByCode("CARD");

        assertTrue(found.isPresent());
        assertEquals("CARD", found.get().getCode());
    }

    @Test
    void testFindByCode_NotExists() {
        // Branch: Department does not exist with code
        Optional<Department> found = departmentRepository.findByCode("NONE");

        assertFalse(found.isPresent());
    }

    @Test
    void testExistsByName_True() {
        // Branch: Name exists
        boolean exists = departmentRepository.existsByName("Cardiology");

        assertTrue(exists);
    }

    @Test
    void testExistsByName_False() {
        // Branch: Name does not exist
        boolean exists = departmentRepository.existsByName("Nonexistent");

        assertFalse(exists);
    }

    @Test
    void testExistsByCode_True() {
        // Branch: Code exists
        boolean exists = departmentRepository.existsByCode("CARD");

        assertTrue(exists);
    }

    @Test
    void testExistsByCode_False() {
        // Branch: Code does not exist
        boolean exists = departmentRepository.existsByCode("NONE");

        assertFalse(exists);
    }
}

