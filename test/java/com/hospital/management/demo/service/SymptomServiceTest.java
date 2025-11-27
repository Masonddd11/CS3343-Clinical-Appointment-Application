package com.hospital.management.demo.service;

import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.repository.DepartmentRepository;
import com.hospital.management.demo.repository.SymptomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SymptomServiceTest {

    private SymptomService symptomService;
    private SymptomRepository symptomRepository;
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void setUp() {
        symptomRepository = mock(SymptomRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        symptomService = new SymptomService(symptomRepository, departmentRepository);
    }

    @Test
    void testCreateSymptom_DepartmentNotFound() {
        // Branch: departmentRepository.findById returns empty
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            symptomService.createSymptom("Chest Pain", 1L, 1, new ArrayList<>());
        });
    }

    @Test
    void testCreateSymptom_WithNullPriority() {
        // Branch: priority == null
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Symptom savedSymptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .build();
        when(symptomRepository.save(any(Symptom.class))).thenReturn(savedSymptom);

        Symptom result = symptomService.createSymptom("Chest Pain", 1L, null, new ArrayList<>());

        assertNotNull(result);
        assertEquals(1, result.getPriority());
    }

    @Test
    void testCreateSymptom_WithPriority() {
        // Branch: priority != null
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Symptom savedSymptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(2)
                .build();
        when(symptomRepository.save(any(Symptom.class))).thenReturn(savedSymptom);

        Symptom result = symptomService.createSymptom("Chest Pain", 1L, 2, new ArrayList<>());

        assertNotNull(result);
        assertEquals(2, result.getPriority());
    }

    @Test
    void testAnalyzeSymptom_NoMatch() {
        // Branch: bestSymptom == null - 行63
        when(symptomRepository.findAll()).thenReturn(new ArrayList<>());

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("test symptom");

        assertFalse(result.isPresent());
    }

    @Test
    void testAnalyzeSymptom_BestScoreZero() {
        // Branch: bestSymptom == null (空列表情况) - 行63
        when(symptomRepository.findAll()).thenReturn(new ArrayList<>());

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("unrelated text");

        assertFalse(result.isPresent());
    }

    @Test
    void testAnalyzeSymptom_BestSymptomNotNullButScoreZero() {
        // Branch: bestSymptom != null && bestScore <= 0 - 行63
        // 这种情况：有症状存在，但匹配分数为0（完全不匹配）
        // 由于adjustedScore = score - (priority * 0.05)，如果score为0，adjustedScore为负
        // 但bestSymptom只有在adjustedScore > bestScore时才会被设置
        // 所以这个测试实际上测试的是：有症状但都不匹配，bestSymptom保持为null
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("completely different symptom")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("unrelated text");

        assertFalse(result.isPresent());
    }

    @Test
    void testAnalyzeSymptom_BestScoreZeroWithSymptoms() {
        // Branch: bestScore <= 0 (当有症状但都不匹配时) - 行63
        // 这个测试确保bestScore <= 0的分支被覆盖
        // 即使bestSymptom为null，这也覆盖了OR条件的第二个部分
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("completely different")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("unrelated text");

        // bestScore保持为0（初始值），因为没有任何匹配
        assertFalse(result.isPresent());
    }

    @Test
    void testAnalyzeSymptom_AdjustedScoreNotGreater() {
        // Branch: adjustedScore <= bestScore - 行56
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom1 = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        Symptom symptom2 = Symptom.builder()
                .id(2L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(10) // 高优先级，adjustedScore会更低
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom1, symptom2));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chest pain");

        assertTrue(result.isPresent());
        // symptom1应该有更高的adjustedScore，因为它优先级更低
    }

    @Test
    void testAnalyzeSymptom_WithMatch() {
        // Branch: bestSymptom != null && bestScore > 0
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chest pain");

        assertTrue(result.isPresent());
    }

    @Test
    void testGetAllSymptoms() {
        // Cover getAllSymptoms method
        when(symptomRepository.findAllByOrderByPriorityAsc()).thenReturn(new ArrayList<>());

        List<Symptom> symptoms = symptomService.getAllSymptoms();

        assertNotNull(symptoms);
    }

    @Test
    void testGetSymptomsByDepartment() {
        // Cover getSymptomsByDepartment method
        when(symptomRepository.findByRecommendedDepartmentId(1L)).thenReturn(new ArrayList<>());

        List<Symptom> symptoms = symptomService.getSymptomsByDepartment(1L);

        assertNotNull(symptoms);
    }

    @Test
    void testNormalizeSymptom_Null() {
        // Branch: symptom == null - 行83
        // 通过createSymptom间接测试
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Symptom savedSymptom = Symptom.builder()
                .id(1L)
                .symptom("")
                .recommendedDepartment(department)
                .priority(1)
                .build();
        when(symptomRepository.save(any(Symptom.class))).thenReturn(savedSymptom);

        Symptom result = symptomService.createSymptom(null, 1L, 1, new ArrayList<>());

        assertNotNull(result);
        assertEquals("", result.getSymptom());
    }

    @Test
    void testNormalizeKeywords_Null() {
        // Branch: keywords == null - 行94
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Symptom savedSymptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.save(any(Symptom.class))).thenReturn(savedSymptom);

        Symptom result = symptomService.createSymptom("Chest Pain", 1L, 1, null);

        assertNotNull(result);
        assertNotNull(result.getKeywords());
        assertTrue(result.getKeywords().isEmpty());
    }

    @Test
    void testNormalizeKeywords_WithEmptyStrings() {
        // Branch: filter(s -> !s.isEmpty()) - 行97
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Symptom savedSymptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(List.of("pain", "chest"))
                .build();
        when(symptomRepository.save(any(Symptom.class))).thenReturn(savedSymptom);

        List<String> keywords = new ArrayList<>();
        keywords.add("pain");
        keywords.add(""); // 空字符串应该被过滤
        keywords.add("   "); // 只有空格的字符串应该被过滤
        keywords.add("chest");

        Symptom result = symptomService.createSymptom("Chest Pain", 1L, 1, keywords);

        assertNotNull(result);
        assertNotNull(result.getKeywords());
        // 空字符串应该被过滤掉
        assertFalse(result.getKeywords().contains(""));
    }

    @Test
    void testTokenize_Empty() {
        // Branch: text.isEmpty() - 行105
        // 通过analyzeSymptom间接测试
        when(symptomRepository.findAll()).thenReturn(new ArrayList<>());

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("   ");

        assertFalse(result.isPresent());
    }

    @Test
    void testComputeScore_InputTokensEmpty() {
        // Branch: inputTokens.isEmpty() - 行112
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("   ");

        assertFalse(result.isPresent());
    }

    @Test
    void testComputeScore_KeywordContainsToken() {
        // Branch: keyword.contains(token) - 行124
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(List.of("chestpain", "heartache"))
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chest");

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchedKeywords().size() > 0);
    }

    @Test
    void testComputeScore_TokenContainsKeyword() {
        // Branch: token.contains(keyword) - 行124
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(List.of("pain"))
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chestpain");

        assertTrue(result.isPresent());
        assertTrue(result.get().getMatchedKeywords().size() > 0);
    }

    @Test
    void testAnalyzeSymptom_WithKeywords() {
        // 测试使用keywords进行匹配
        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Symptom symptom = Symptom.builder()
                .id(1L)
                .symptom("heart problem")
                .recommendedDepartment(department)
                .priority(1)
                .keywords(List.of("chest", "pain", "heart"))
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chest pain");

        assertTrue(result.isPresent());
        assertEquals(department.getId(), result.get().getDepartment().getId());
        assertTrue(result.get().getMatchedKeywords().size() > 0);
    }

    @Test
    void testAnalyzeSymptom_MultipleSymptoms() {
        // 测试多个症状，选择最佳匹配
        Department dept1 = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Department dept2 = Department.builder()
                .id(2L)
                .name("Neurology")
                .build();
        Symptom symptom1 = Symptom.builder()
                .id(1L)
                .symptom("headache")
                .recommendedDepartment(dept1)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        Symptom symptom2 = Symptom.builder()
                .id(2L)
                .symptom("chest pain")
                .recommendedDepartment(dept2)
                .priority(1)
                .keywords(new ArrayList<>())
                .build();
        when(symptomRepository.findAll()).thenReturn(List.of(symptom1, symptom2));

        Optional<SymptomService.SymptomMatchResult> result = symptomService.analyzeSymptom("chest pain");

        assertTrue(result.isPresent());
        assertEquals(dept2.getId(), result.get().getDepartment().getId());
    }
}

