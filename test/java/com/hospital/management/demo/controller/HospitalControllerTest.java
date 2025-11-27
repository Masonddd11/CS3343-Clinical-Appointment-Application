package com.hospital.management.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.dto.HospitalResponse;
import com.hospital.management.demo.service.HospitalService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = HospitalController.class,
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
class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HospitalService hospitalService;

    @Autowired
    private ObjectMapper objectMapper;

    private HospitalRequest hospitalRequest;
    private HospitalResponse hospitalResponse;

    @BeforeEach
    void setUp() {
        hospitalRequest = new HospitalRequest();
        hospitalRequest.setName("Test Hospital");
        hospitalRequest.setAddress("123 Test St");
        hospitalRequest.setLatitude(39.9042);
        hospitalRequest.setLongitude(116.4074);

        hospitalResponse = HospitalResponse.builder()
                .id(1L)
                .name("Test Hospital")
                .address("123 Test St")
                .latitude(39.9042)
                .longitude(116.4074)
                .build();
    }

    @Test
    void testCreateHospital_Success() throws Exception {
        when(hospitalService.createHospital(any(HospitalRequest.class))).thenReturn(hospitalResponse);

        mockMvc.perform(post("/api/hospitals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Hospital"));
    }

    @Test
    void testGetAllHospitals_Success() throws Exception {
        when(hospitalService.getAllHospitals()).thenReturn(List.of(hospitalResponse));

        mockMvc.perform(get("/api/hospitals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Hospital"));
    }

    @Test
    void testGetHospitalById_Success() throws Exception {
        when(hospitalService.getHospitalById(1L)).thenReturn(hospitalResponse);

        mockMvc.perform(get("/api/hospitals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Hospital"));
    }

    @Test
    void testUpdateHospital_Success() throws Exception {
        when(hospitalService.updateHospital(eq(1L), any(HospitalRequest.class))).thenReturn(hospitalResponse);

        mockMvc.perform(put("/api/hospitals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hospitalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}

