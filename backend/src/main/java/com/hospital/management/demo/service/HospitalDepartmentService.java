package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.HospitalDepartmentRequest;
import com.hospital.management.demo.dto.HospitalDepartmentResponse;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.HospitalDepartment;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalDepartmentService {

    private final HospitalDepartmentRepository hospitalDepartmentRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public HospitalDepartmentResponse assignDepartmentToHospital(HospitalDepartmentRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (hospitalDepartmentRepository.findByHospitalIdAndDepartmentId(
                request.getHospitalId(), request.getDepartmentId()).isPresent()) {
            throw new RuntimeException("Department already assigned to this hospital");
        }

        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .hospital(hospital)
                .department(department)
                .isActive(true)
                .build();

        hospitalDepartment = hospitalDepartmentRepository.save(hospitalDepartment);
        return mapToResponse(hospitalDepartment);
    }

    public List<HospitalDepartmentResponse> getDepartmentsByHospital(Long hospitalId) {
        return hospitalDepartmentRepository.findByHospitalIdAndIsActiveTrue(hospitalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<HospitalDepartmentResponse> getHospitalsByDepartment(Long departmentId) {
        return hospitalDepartmentRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private HospitalDepartmentResponse mapToResponse(HospitalDepartment hd) {
        return HospitalDepartmentResponse.builder()
                .id(hd.getId())
                .hospitalId(hd.getHospital().getId())
                .hospitalName(hd.getHospital().getName())
                .departmentId(hd.getDepartment().getId())
                .departmentName(hd.getDepartment().getName())
                .isActive(hd.getIsActive())
                .build();
    }
}

