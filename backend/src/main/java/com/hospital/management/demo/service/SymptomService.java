package com.hospital.management.demo.service;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.SymptomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SymptomService {

    private final SymptomRepository symptomRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public Symptom createSymptom(String symptom, Long departmentId, Integer priority) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        Symptom symptomEntity = Symptom.builder()
                .symptom(symptom)
                .recommendedDepartment(department)
                .priority(priority != null ? priority : 1)
                .build();

        return symptomRepository.save(symptomEntity);
    }

    public Optional<Department> analyzeSymptom(String symptomText) {
        List<Symptom> matchingSymptoms = symptomRepository.findBySymptomContainingIgnoreCase(symptomText);
        
        if (matchingSymptoms.isEmpty()) {
            return Optional.empty();
        }

        Symptom bestMatch = matchingSymptoms.stream()
                .min((s1, s2) -> Integer.compare(s1.getPriority(), s2.getPriority()))
                .orElse(matchingSymptoms.get(0));

        return Optional.of(bestMatch.getRecommendedDepartment());
    }

    public List<Symptom> getAllSymptoms() {
        return symptomRepository.findAllByOrderByPriorityAsc();
    }

    public List<Symptom> getSymptomsByDepartment(Long departmentId) {
        return symptomRepository.findByRecommendedDepartmentId(departmentId);
    }
}

