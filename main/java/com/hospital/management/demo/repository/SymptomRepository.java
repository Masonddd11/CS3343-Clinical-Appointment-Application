package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymptomRepository extends JpaRepository<Symptom, Long> {
    Optional<Symptom> findBySymptomIgnoreCase(String symptom);
    List<Symptom> findBySymptomContainingIgnoreCase(String symptom);
    List<Symptom> findByRecommendedDepartmentId(Long departmentId);
    List<Symptom> findAllByOrderByPriorityAsc();
}

