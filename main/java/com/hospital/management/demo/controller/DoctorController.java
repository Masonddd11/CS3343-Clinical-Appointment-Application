package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.DoctorRequest;
import com.hospital.management.demo.dto.DoctorResponse;
import com.hospital.management.demo.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        return ResponseEntity.ok(doctorService.createDoctor(request));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "specialization", required = false) String specialization) {
        if (name != null || specialization != null) {
            return ResponseEntity.ok(doctorService.searchDoctors(name, specialization));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByHospital(@PathVariable("hospitalId") Long hospitalId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospital(hospitalId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByDepartment(
            @PathVariable("departmentId") Long departmentId) {
        return ResponseEntity.ok(doctorService.getDoctorsByDepartment(departmentId));
    }

    @GetMapping("/hospital/{hospitalId}/department/{departmentId}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsByHospitalAndDepartment(
            @PathVariable("hospitalId") Long hospitalId,
            @PathVariable("departmentId") Long departmentId) {
        return ResponseEntity.ok(doctorService.getDoctorsByHospitalAndDepartment(hospitalId, departmentId));
    }
}
