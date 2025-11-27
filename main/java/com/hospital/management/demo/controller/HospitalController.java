package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.dto.HospitalResponse;
import com.hospital.management.demo.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<HospitalResponse> createHospital(@Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(hospitalService.createHospital(request));
    }

    @GetMapping
    public ResponseEntity<List<HospitalResponse>> getAllHospitals() {
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HospitalResponse> getHospitalById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(hospitalService.getHospitalById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HospitalResponse> updateHospital(@PathVariable("id") Long id, @Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(hospitalService.updateHospital(id, request));
    }
}

