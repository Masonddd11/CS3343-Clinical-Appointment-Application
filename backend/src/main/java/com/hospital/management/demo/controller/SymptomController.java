package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.SymptomAnalysisRequest;
import com.hospital.management.demo.dto.SymptomAnalysisResponse;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.service.SymptomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/symptoms")
@RequiredArgsConstructor
public class SymptomController {

    private final SymptomService symptomService;

    @PostMapping("/analyze")
    public ResponseEntity<SymptomAnalysisResponse> analyzeSymptom(@Valid @RequestBody SymptomAnalysisRequest request) {
        Optional<Department> department = symptomService.analyzeSymptom(request.getSymptom());
        
        if (department.isEmpty()) {
            return ResponseEntity.ok(SymptomAnalysisResponse.builder()
                    .message("No matching department found for symptom: " + request.getSymptom())
                    .build());
        }

        Department dept = department.get();
        return ResponseEntity.ok(SymptomAnalysisResponse.builder()
                .departmentId(dept.getId())
                .departmentName(dept.getName())
                .departmentCode(dept.getCode())
                .message("Recommended department: " + dept.getName())
                .build());
    }

    @GetMapping
    public ResponseEntity<List<Symptom>> getAllSymptoms() {
        return ResponseEntity.ok(symptomService.getAllSymptoms());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Symptom>> getSymptomsByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(symptomService.getSymptomsByDepartment(departmentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Symptom> createSymptom(
            @RequestParam String symptom,
            @RequestParam Long departmentId,
            @RequestParam(required = false, defaultValue = "1") Integer priority) {
        return ResponseEntity.ok(symptomService.createSymptom(symptom, departmentId, priority));
    }
}

