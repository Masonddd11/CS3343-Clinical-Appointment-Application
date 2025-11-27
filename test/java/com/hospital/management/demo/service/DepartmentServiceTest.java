package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.DepartmentRequest;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {

    private DepartmentService departmentService;
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        departmentRepository = mock(DepartmentRepository.class);
        departmentService = new DepartmentService(departmentRepository);
    }

    @Test
    void testCreateDepartment_NameExists() {
        // Branch: departmentRepository.existsByName returns true
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Cardiology");
        request.setCode("CARD");
        request.setDescription("Heart department");

        when(departmentRepository.existsByName("Cardiology")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.createDepartment(request);
        });
        assertEquals("Department name already exists", exception.getMessage());
    }

    @Test
    void testCreateDepartment_CodeExists() {
        // Branch: departmentRepository.existsByCode returns true
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Cardiology");
        request.setCode("CARD");
        request.setDescription("Heart department");

        when(departmentRepository.existsByName("Cardiology")).thenReturn(false);
        when(departmentRepository.existsByCode("CARD")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.createDepartment(request);
        });
        assertEquals("Department code already exists", exception.getMessage());
    }

    @Test
    void testCreateDepartment_Success() {
        // Branch: name and code do not exist
        DepartmentRequest request = new DepartmentRequest();
        request.setName("Cardiology");
        request.setCode("CARD");
        request.setDescription("Heart department");

        when(departmentRepository.existsByName("Cardiology")).thenReturn(false);
        when(departmentRepository.existsByCode("CARD")).thenReturn(false);

        Department savedDepartment = Department.builder()
                .id(1L)
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        var response = departmentService.createDepartment(request);

        assertNotNull(response);
        assertEquals("Cardiology", response.getName());
    }

    @Test
    void testGetDepartmentById_Exists() {
        // Branch: Department exists
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        var response = departmentService.getDepartmentById(1L);

        assertNotNull(response);
        assertEquals("Cardiology", response.getName());
    }

    @Test
    void testGetDepartmentById_NotExists() {
        // Branch: Department does not exist
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            departmentService.getDepartmentById(1L);
        });
        assertEquals("Department not found", exception.getMessage());
    }

    @Test
    void testGetAllDepartments() {
        // Cover getAllDepartments method
        when(departmentRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var departments = departmentService.getAllDepartments();

        assertNotNull(departments);
    }
}
