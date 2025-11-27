package com.hospital.management.demo.service;

import com.hospital.management.demo.dto.HospitalRequest;
import com.hospital.management.demo.model.entity.Hospital;
import com.hospital.management.demo.model.enums.OperationalStatus;
import com.hospital.management.demo.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalServiceTest {

    private HospitalService hospitalService;
    private HospitalRepository hospitalRepository;

    @BeforeEach
    void setUp() {
        hospitalRepository = mock(HospitalRepository.class);
        hospitalService = new HospitalService(hospitalRepository);
    }

    @Test
    void testCreateHospital_WithNullIntensity() {
        // Branch: request.getCurrentIntensity() == null
        HospitalRequest request = new HospitalRequest();
        request.setName("Test Hospital");
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setCurrentIntensity(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .currentIntensity(0.0)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.createHospital(request);

        assertNotNull(response);
        assertEquals(0.0, response.getCurrentIntensity());
    }

    @Test
    void testCreateHospital_WithIntensity() {
        // Branch: request.getCurrentIntensity() != null
        HospitalRequest request = new HospitalRequest();
        request.setName("Test Hospital");
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setCurrentIntensity(0.5);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .currentIntensity(0.5)
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.createHospital(request);

        assertNotNull(response);
        assertEquals(0.5, response.getCurrentIntensity());
    }

    @Test
    void testCreateHospital_WithNullStatus() {
        // Branch: request.getOperationalStatus() == null - 行31
        HospitalRequest request = new HospitalRequest();
        request.setName("Test Hospital");
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setOperationalStatus(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.createHospital(request);

        assertNotNull(response);
        assertEquals(OperationalStatus.OPERATIONAL, response.getOperationalStatus());
    }

    @Test
    void testCreateHospital_WithStatus() {
        // Branch: request.getOperationalStatus() != null - 行31
        HospitalRequest request = new HospitalRequest();
        request.setName("Test Hospital");
        request.setLatitude(39.9042);
        request.setLongitude(116.4074);
        request.setOperationalStatus(OperationalStatus.CLOSED_EPIDEMIC);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.createHospital(request);

        assertNotNull(response);
        assertEquals(OperationalStatus.CLOSED_EPIDEMIC, response.getOperationalStatus());
    }

    @Test
    void testGetHospitalById_Exists() {
        // Branch: Hospital exists
        Hospital hospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .latitude(39.9042)
                .longitude(116.4074)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        var response = hospitalService.getHospitalById(1L);

        assertNotNull(response);
        assertEquals("Test Hospital", response.getName());
    }

    @Test
    void testGetHospitalById_NotExists() {
        // Branch: Hospital does not exist
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hospitalService.getHospitalById(1L);
        });
    }

    @Test
    void testUpdateHospital_NotExists() {
        // Branch: Hospital does not exist
        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            hospitalService.updateHospital(1L, request);
        });
    }

    @Test
    void testUpdateHospital_WithNullCapacity() {
        // Branch: request.getCapacity() == null
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .capacity(100)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setCapacity(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .capacity(100)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals("Updated Hospital", response.getName());
    }

    @Test
    void testUpdateHospital_WithCapacity() {
        // Branch: request.getCapacity() != null
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .capacity(100)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setCapacity(200);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .capacity(200)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals(200, response.getCapacity());
    }

    @Test
    void testGetAllHospitals() {
        // Cover getAllHospitals method
        when(hospitalRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        var hospitals = hospitalService.getAllHospitals();

        assertNotNull(hospitals);
    }

    @Test
    void testUpdateHospital_CurrentIntensityNull() {
        // Branch: request.getCurrentIntensity() == null - 行62
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .currentIntensity(0.5)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setCurrentIntensity(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .currentIntensity(0.5) // 应该保持原值
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals("Updated Hospital", response.getName());
        // currentIntensity 应该保持原值，因为 request 中为 null
    }

    @Test
    void testUpdateHospital_WithCurrentIntensity() {
        // Branch: request.getCurrentIntensity() != null - 行62
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .currentIntensity(0.5)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setCurrentIntensity(0.8);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .currentIntensity(0.8)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals(0.8, response.getCurrentIntensity());
    }

    @Test
    void testUpdateHospital_OperationalStatusNull() {
        // Branch: request.getOperationalStatus() == null - 行63
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setOperationalStatus(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .operationalStatus(OperationalStatus.OPERATIONAL) // 应该保持原值
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals("Updated Hospital", response.getName());
        // operationalStatus 应该保持原值，因为 request 中为 null
    }

    @Test
    void testUpdateHospital_WithOperationalStatus() {
        // Branch: request.getOperationalStatus() != null - 行63
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .operationalStatus(OperationalStatus.OPERATIONAL)
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setOperationalStatus(OperationalStatus.CLOSED_EPIDEMIC);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .operationalStatus(OperationalStatus.CLOSED_EPIDEMIC)
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals(OperationalStatus.CLOSED_EPIDEMIC, response.getOperationalStatus());
    }

    @Test
    void testUpdateHospital_ClosureReasonNull() {
        // Branch: request.getClosureReason() == null - 行64
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .closureReason("Old reason")
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setClosureReason(null);

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .closureReason("Old reason") // 应该保持原值
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals("Updated Hospital", response.getName());
        // closureReason 应该保持原值，因为 request 中为 null
    }

    @Test
    void testUpdateHospital_WithClosureReason() {
        // Branch: request.getClosureReason() != null - 行64
        Hospital existingHospital = Hospital.builder()
                .id(1L)
                .name("Test Hospital")
                .closureReason("Old reason")
                .build();
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(existingHospital));

        HospitalRequest request = new HospitalRequest();
        request.setName("Updated Hospital");
        request.setClosureReason("New reason");

        Hospital savedHospital = Hospital.builder()
                .id(1L)
                .name("Updated Hospital")
                .closureReason("New reason")
                .build();
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(savedHospital);

        var response = hospitalService.updateHospital(1L, request);

        assertNotNull(response);
        assertEquals("New reason", response.getClosureReason());
    }
}

