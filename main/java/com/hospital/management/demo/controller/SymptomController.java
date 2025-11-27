package com.hospital.management.demo.controller;

import com.hospital.management.demo.dto.SymptomAnalysisRequest;
import com.hospital.management.demo.dto.SymptomAnalysisResponse;
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
        Optional<SymptomService.SymptomMatchResult> matchResult = symptomService.analyzeSymptom(request.getSymptom());

        if (matchResult.isEmpty()) {
            return ResponseEntity.ok(SymptomAnalysisResponse.builder()
                    .message("No matching department found for symptom: " + request.getSymptom())
                    .confidenceScore(0.0)
                    .matchedKeywords(List.of())
                    .build());
        }

        SymptomService.SymptomMatchResult result = matchResult.get();
        return ResponseEntity.ok(SymptomAnalysisResponse.builder()
                .departmentId(result.getDepartment().getId())
                .departmentName(result.getDepartment().getName())
                .departmentCode(result.getDepartment().getCode())
                .confidenceScore(result.getConfidenceScore())
                .matchedKeywords(result.getMatchedKeywords())
                .message("Recommended department: " + result.getDepartment().getName())
                .build());
    }

    @GetMapping
    public ResponseEntity<List<Symptom>> getAllSymptoms() {
        return ResponseEntity.ok(symptomService.getAllSymptoms());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Symptom>> getSymptomsByDepartment(@PathVariable("departmentId") Long departmentId) {
        return ResponseEntity.ok(symptomService.getSymptomsByDepartment(departmentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Symptom> createSymptom(
            @RequestParam("symptom") String symptom,
            @RequestParam("departmentId") Long departmentId,
            @RequestParam(value = "priority", required = false, defaultValue = "1") Integer priority,
            @RequestParam(value = "keywords", required = false) List<String> keywords) {
        return ResponseEntity.ok(symptomService.createSymptom(symptom, departmentId, priority, keywords));
    }
}

