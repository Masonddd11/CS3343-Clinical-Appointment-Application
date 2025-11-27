package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.SymptomAnalysisRequest;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Symptom;
import com.hospital.management.demo.service.SymptomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SymptomController.class, excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.hospital\\.management\\.demo\\.security\\..*"))
@AutoConfigureMockMvc(addFilters = false)
class SymptomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SymptomService symptomService;

    @Autowired
    private ObjectMapper objectMapper;

    private SymptomAnalysisRequest analysisRequest;
    private Symptom symptom;
    private Department department;

    @BeforeEach
    void setUp() {
        analysisRequest = new SymptomAnalysisRequest();
        analysisRequest.setSymptom("chest pain");

        department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .code("CARD")
                .build();

        symptom = Symptom.builder()
                .id(1L)
                .symptom("chest pain")
                .recommendedDepartment(department)
                .build();
    }

    @Test
    void testAnalyzeSymptom_WithMatch() throws Exception {
        SymptomService.SymptomMatchResult matchResult = SymptomService.SymptomMatchResult.builder()
                .department(department)
                .confidenceScore(0.8)
                .matchedKeywords(List.of("chest", "pain"))
                .build();

        when(symptomService.analyzeSymptom("chest pain")).thenReturn(Optional.of(matchResult));

        mockMvc.perform(post("/api/symptoms/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(analysisRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId").value(1L))
                .andExpect(jsonPath("$.departmentName").value("Cardiology"))
                .andExpect(jsonPath("$.confidenceScore").value(0.8));
    }

    @Test
    void testAnalyzeSymptom_NoMatch() throws Exception {
        when(symptomService.analyzeSymptom("unknown symptom")).thenReturn(Optional.empty());

        SymptomAnalysisRequest request = new SymptomAnalysisRequest();
        request.setSymptom("unknown symptom");

        mockMvc.perform(post("/api/symptoms/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confidenceScore").value(0.0))
                .andExpect(jsonPath("$.message").value("No matching department found for symptom: unknown symptom"));
    }

    @Test
    void testGetAllSymptoms_Success() throws Exception {
        when(symptomService.getAllSymptoms()).thenReturn(List.of(symptom));

        mockMvc.perform(get("/api/symptoms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetSymptomsByDepartment_Success() throws Exception {
        when(symptomService.getSymptomsByDepartment(1L)).thenReturn(List.of(symptom));

        mockMvc.perform(get("/api/symptoms/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testCreateSymptom_Success() throws Exception {
        when(symptomService.createSymptom(eq("chest pain"), eq(1L), eq(1), any()))
                .thenReturn(symptom);

        mockMvc.perform(post("/api/symptoms")
                .param("symptom", "chest pain")
                .param("departmentId", "1")
                .param("priority", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.symptom").value("chest pain"));
    }

    @Test
    void testCreateSymptom_WithKeywords() throws Exception {
        when(symptomService.createSymptom(eq("chest pain"), eq(1L), eq(2), anyList()))
                .thenReturn(symptom);

        mockMvc.perform(post("/api/symptoms")
                .param("symptom", "chest pain")
                .param("departmentId", "1")
                .param("priority", "2")
                .param("keywords", "chest", "pain"))
                .andExpect(status().isOk());
    }
}
