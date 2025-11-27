package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.HospitalDepartmentRequest;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.HospitalDepartment;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalDepartmentServiceTest {

    private HospitalDepartmentService hospitalDepartmentService;
    private HospitalDepartmentRepository hospitalDepartmentRepository;
    private HospitalRepository hospitalRepository;
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        hospitalDepartmentRepository = mock(HospitalDepartmentRepository.class);
        hospitalRepository = mock(HospitalRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        
        hospitalDepartmentService = new HospitalDepartmentService(
                hospitalDepartmentRepository,
                hospitalRepository,
                departmentRepository
        );
    }

    @Test
    void testAssignDepartmentToHospital_HospitalNotFound() {
        // Branch: hospitalRepository.findById returns empty
        HospitalDepartmentRequest request = new HospitalDepartmentRequest();
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hospitalDepartmentService.assignDepartmentToHospital(request);
        });
    }

    @Test
    void testAssignDepartmentToHospital_DepartmentNotFound() {
        // Branch: departmentRepository.findById returns empty
        HospitalDepartmentRequest request = new HospitalDepartmentRequest();
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hospitalDepartmentService.assignDepartmentToHospital(request);
        });
    }

    @Test
    void testAssignDepartmentToHospital_AlreadyAssigned() {
        // Branch: hospitalDepartmentRepository.findByHospitalIdAndDepartmentId returns present
        HospitalDepartmentRequest request = new HospitalDepartmentRequest();
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        HospitalDepartment existing = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> {
            hospitalDepartmentService.assignDepartmentToHospital(request);
        });
    }

    @Test
    void testAssignDepartmentToHospital_Success() {
        // Branch: All validations pass
        HospitalDepartmentRequest request = new HospitalDepartmentRequest();
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .build();
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();

        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(1L, 1L))
                .thenReturn(Optional.empty());

        HospitalDepartment saved = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .isActive(true)
                .build();
        when(hospitalDepartmentRepository.save(any(HospitalDepartment.class))).thenReturn(saved);

        var response = hospitalDepartmentService.assignDepartmentToHospital(request);

        assertNotNull(response);
        assertEquals(1L, response.getHospitalId());
    }

    @Test
    void testGetDepartmentsByHospital() {
        // Cover getDepartmentsByHospital method
        when(hospitalDepartmentRepository.findByHospitalIdAndIsActiveTrue(1L))
                .thenReturn(java.util.Collections.emptyList());

        var departments = hospitalDepartmentService.getDepartmentsByHospital(1L);

        assertNotNull(departments);
    }

    @Test
    void testGetHospitalsByDepartment() {
        // Cover getHospitalsByDepartment method
        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(java.util.Collections.emptyList());

        var hospitals = hospitalDepartmentService.getHospitalsByDepartment(1L);

        assertNotNull(hospitals);
    }
}

