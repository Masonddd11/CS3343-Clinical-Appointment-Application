package com.hospital.management.demo.service;

import com.hospital.management.demo.algorithm.WeightCalculator;
import com.hospital.management.demo.dto.HospitalRecommendationRequest;
import com.hospital.management.demo.model.entity.Department;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.entity.HospitalDepartment;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.repository.HospitalDepartmentRepository;
import com.hospital.management.demo.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalPathfinderServiceTest {

    private HospitalPathfinderService hospitalPathfinderService;
    private HospitalRepository hospitalRepository;
    private HospitalDepartmentRepository hospitalDepartmentRepository;
    private WeightCalculator weightCalculator;

    @BeforeEach
    void setUp() {
        hospitalRepository = mock(HospitalRepository.class);
        hospitalDepartmentRepository = mock(HospitalDepartmentRepository.class);
        weightCalculator = mock(WeightCalculator.class);
        
        hospitalPathfinderService = new HospitalPathfinderService(
                hospitalRepository,
                hospitalDepartmentRepository,
                weightCalculator
        );
    }

    @Test
    void testRecommendHospitals_NoHospitalsWithDepartment() {
        // Branch: hospitalIdsWithDepartment.isEmpty()
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(java.util.Collections.emptyList());

        assertThrows(RuntimeException.class, () -> {
            hospitalPathfinderService.recommendHospitals(request);
        });
    }

    @Test
    void testRecommendHospitals_WithMaxResults() {
        // Branch: request.getMaxResults() != null
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setMaxResults(3);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.3)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
    }

    @Test
    void testRecommendHospitals_WithoutMaxResults() {
        // Branch: request.getMaxResults() == null
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setMaxResults(null);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.3)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
    }

    @Test
    void testRecommendHospitals_HospitalExcluded() {
        // Branch: weightCalculator.isHospitalExcluded returns true
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(true);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(0, recommendations.size());
    }

    @Test
    void testRecommendHospitals_WithLowIntensity() {
        // Branch: hospital.getCurrentIntensity() != null && < 0.5 - 行89
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.3)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.get(0).getRecommendationReason().contains("Low intensity"));
        assertTrue(recommendations.get(0).getRecommendationReason().contains("Fully operational"));
    }

    @Test
    void testRecommendHospitals_IntensityNull() {
        // Branch: hospital.getCurrentIntensity() == null - 行89
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(null)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertFalse(recommendations.get(0).getRecommendationReason().contains("Low intensity"));
        assertTrue(recommendations.get(0).getRecommendationReason().contains("Fully operational"));
    }

    @Test
    void testRecommendHospitals_IntensityHigh() {
        // Branch: hospital.getCurrentIntensity() != null && >= 0.5 - 行89
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .currentIntensity(0.8)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertFalse(recommendations.get(0).getRecommendationReason().contains("Low intensity"));
        assertTrue(recommendations.get(0).getRecommendationReason().contains("Fully operational"));
    }

    @Test
    void testRecommendHospitals_NotOperational() {
        // Branch: hospital.getOperationalStatus() != OPERATIONAL - 行93
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .currentIntensity(0.3)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.get(0).getRecommendationReason().contains("Low intensity"));
        assertFalse(recommendations.get(0).getRecommendationReason().contains("Fully operational"));
    }

    @Test
    void testRecommendHospitals_ClosedOther() {
        // Branch: hospital.getOperationalStatus() == CLOSED_OTHER - 行93
        HospitalRecommendationRequest request = new HospitalRecommendationRequest();
        request.setDepartmentId(1L);
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);

        Department department = Department.builder()
                .id(1L)
                .name("Cardiology")
                .build();
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .operationalStatus(OperationalStatus.CLOSED_OTHER)
                .currentIntensity(null)
                .build();
        HospitalDepartment hospitalDepartment = HospitalDepartment.builder()
                .id(1L)
                .hospital(hospital)
                .department(department)
                .build();

        when(hospitalDepartmentRepository.findByDepartmentId(1L))
                .thenReturn(List.of(hospitalDepartment));
        when(hospitalRepository.findAllById(anyList())).thenReturn(List.of(hospital));
        when(weightCalculator.isHospitalExcluded(hospital)).thenReturn(false);
        when(weightCalculator.calculateScore(any(Hospital.class), anyDouble(), anyDouble()))
                .thenReturn(0.5);

        var recommendations = hospitalPathfinderService.recommendHospitals(request);

        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertFalse(recommendations.get(0).getRecommendationReason().contains("Low intensity"));
        assertFalse(recommendations.get(0).getRecommendationReason().contains("Fully operational"));
    }
}

