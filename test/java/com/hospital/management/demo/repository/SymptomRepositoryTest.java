package com.hospital.management.demo.repository;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SymptomRepositoryTest {

    @Autowired
    private SymptomRepository symptomRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Symptom testSymptom;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .name("Cardiology")
                .code("CARD")
                .description("Heart department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testSymptom = Symptom.builder()
                .symptom("Chest Pain")
                .priority(1)
                .recommendedDepartment(testDepartment)
                .build();
        symptomRepository.save(testSymptom);
    }

    @Test
    void testFindBySymptomIgnoreCase_Exists() {
        // Branch: Symptom exists (case insensitive)
        Optional<Symptom> found = symptomRepository.findBySymptomIgnoreCase("chest pain");

        assertTrue(found.isPresent());
        assertEquals("Chest Pain", found.get().getSymptom());
    }

    @Test
    void testFindBySymptomIgnoreCase_NotExists() {
        // Branch: Symptom does not exist
        Optional<Symptom> found = symptomRepository.findBySymptomIgnoreCase("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void testFindBySymptomContainingIgnoreCase() {
        // Cover findBySymptomContainingIgnoreCase method
        List<Symptom> symptoms = symptomRepository.findBySymptomContainingIgnoreCase("chest");

        assertFalse(symptoms.isEmpty());
    }

    @Test
    void testFindByRecommendedDepartmentId() {
        // Cover findByRecommendedDepartmentId method
        List<Symptom> symptoms = symptomRepository.findByRecommendedDepartmentId(testDepartment.getId());

        assertFalse(symptoms.isEmpty());
    }

    @Test
    void testFindAllByOrderByPriorityAsc() {
        // Cover findAllByOrderByPriorityAsc method
        Symptom symptom2 = Symptom.builder()
                .symptom("Headache")
                .priority(2)
                .recommendedDepartment(testDepartment)
                .build();
        symptomRepository.save(symptom2);

        List<Symptom> symptoms = symptomRepository.findAllByOrderByPriorityAsc();

        assertEquals(2, symptoms.size());
        assertEquals(1, symptoms.get(0).getPriority());
        assertEquals(2, symptoms.get(1).getPriority());
    }
}
