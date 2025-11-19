package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.HospitalDepartmentRequest;
import com.hospital.management.demo.dto.HospitalDepartmentResponse;
import com.hospital.management.demo.service.HospitalDepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospital-departments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class HospitalDepartmentController {

    private final HospitalDepartmentService hospitalDepartmentService;

    @PostMapping
    public ResponseEntity<HospitalDepartmentResponse> assignDepartmentToHospital(
            @Valid @RequestBody HospitalDepartmentRequest request) {
        return ResponseEntity.ok(hospitalDepartmentService.assignDepartmentToHospital(request));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<HospitalDepartmentResponse>> getDepartmentsByHospital(
            @PathVariable Long hospitalId) {
        return ResponseEntity.ok(hospitalDepartmentService.getDepartmentsByHospital(hospitalId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<HospitalDepartmentResponse>> getHospitalsByDepartment(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(hospitalDepartmentService.getHospitalsByDepartment(departmentId));
    }
}

