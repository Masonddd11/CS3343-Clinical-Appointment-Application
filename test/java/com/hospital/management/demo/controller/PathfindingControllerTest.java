package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.HospitalRecommendationRequest;
import com.hospital.management.demo.dto.HospitalRecommendationResponse;
import com.hospital.management.demo.service.HospitalPathfinderService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PathfindingController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.hospital\\.management\\.demo\\.security\\..*"
    )
)
@AutoConfigureMockMvc(addFilters = false)
class PathfindingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HospitalPathfinderService hospitalPathfinderService;

    @Autowired
    private ObjectMapper objectMapper;

    private HospitalRecommendationRequest request;
    private HospitalRecommendationResponse response;

    @BeforeEach
    void setUp() {
        request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setMaxResults(5);

        response = HospitalRecommendationResponse.builder()
                .hospitalId(1L)
                .hospitalName("Test Hospital")
                .distance(5.0)
                .score(0.8)
                .build();
    }

    @Test
    void testRecommendHospitals_Success() throws Exception {
        when(hospitalPathfinderService.recommendHospitals(any(HospitalRecommendationRequest.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/pathfinding/recommend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hospitalId").value(1L))
                .andExpect(jsonPath("$[0].hospitalName").value("Test Hospital"));
    }
}

